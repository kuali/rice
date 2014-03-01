/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.uif.freemarker.FreeMarkerInlineRenderBootstrap;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Utilities class for establishing a minimal environment for testing operations involving Uif
 * components.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class UifUnitTestUtils {
    private final static Logger LOG = Logger.getLogger(UifUnitTestUtils.class);

    private final static ThreadLocal<Properties> TL_CONFIG_PROPERTIES = new ThreadLocal<Properties>();

    private static ConfigurableWebApplicationContext webApplicationContext;

    /**
     * Create a web application context suitable for FreeMarker unit testing.
     */
    private static void configureKradWebApplicationContext() {
        MockServletContext sctx = new MockServletContext();
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.setServletContext(sctx);

        MutablePropertyValues mpv = new MutablePropertyValues();
        mpv.add("preferFileSystemAccess", false);
        mpv.add("templateLoaderPath", "/krad-web");
        Properties props = new Properties();
        props.put("number_format", "computer");
        props.put("template_update_delay", "2147483647");
        mpv.add("freemarkerSettings", props);
        ctx.registerSingleton("freemarkerConfig", FreeMarkerConfigurer.class, mpv);

        mpv = new MutablePropertyValues();
        mpv.add("cache", true);
        mpv.add("prefix", "");
        mpv.add("suffix", ".ftl");
        ctx.registerSingleton("viewResolver", FreeMarkerViewResolver.class, mpv);

        ctx.registerSingleton("freeMarkerInputBootstrap", FreeMarkerInlineRenderBootstrap.class);

        ctx.refresh();
        ctx.start();
        sctx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
        webApplicationContext = ctx;
    }

    /**
     * Get the config properties for the current thread.
     *
     * @return The config properties for the current thread.
     */
    public static Properties getConfigProperties() {
        return TL_CONFIG_PROPERTIES.get();
    }

    /**
     * Get the web application context.
     */
    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    /**
     * Establish a Rice configuration providing enough mock services via
     * {@link GlobalResourceLoader} to support the use of KRAD UIF components in unit tests.
     *
     * @param applicationId The application ID for the fake environment.
     * @throws Exception
     */
    public static void establishMockConfig(String applicationId) throws Exception {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            SimpleConfig config = new SimpleConfig();
            Properties configProperties = new Properties();

            URL defaultsUrl = loader.getResource("KRAD-UifDefaults.properties");
            URL rootUrl = new URL(defaultsUrl.toExternalForm().substring(0,
                    defaultsUrl.toExternalForm().lastIndexOf('/')));
            configProperties.setProperty("root.url", rootUrl.toExternalForm());

            InputStream defaultPropertyResource = defaultsUrl.openStream();
            Assert.assertNotNull("KRAD-UifDefaults.properties", defaultPropertyResource);
            configProperties.load(defaultPropertyResource);

            InputStream appPropertyResource = loader.getResourceAsStream(applicationId + ".properties");
            Assert.assertNotNull(applicationId + ".properties", appPropertyResource);
            configProperties.load(appPropertyResource);

            for (String propName : configProperties.stringPropertyNames()) {
                String propValue = (String) configProperties.getProperty(propName);
                StringBuilder propBuilder = new StringBuilder(propValue);
                int exprStart = 0, exprEnd = 0;
                while (exprStart != -1) {
                    exprStart = propBuilder.indexOf("${", exprEnd);
                    if (exprStart == -1) {
                        continue;
                    }

                    exprEnd = propBuilder.indexOf("}", exprStart);
                    if (exprEnd - exprStart < 3) {
                        continue;
                    }

                    String expr = propBuilder.substring(exprStart + 2, exprEnd);
                    String exprValue = configProperties.getProperty(expr);
                    if (exprValue != null) {
                        propBuilder.delete(exprStart, exprEnd + 1);
                        propBuilder.insert(exprStart, exprValue);
                        configProperties.setProperty(propName, propBuilder.toString());
                        exprEnd = exprStart + exprValue.length();
                    }
                }
            }

            String resourceBundles = configProperties.getProperty("test.resource.bundles");
            if (resourceBundles != null) {
                for (String resourceBundle : resourceBundles.split(",")) {
                    InputStream propertyResource = loader.getResourceAsStream(resourceBundle);
                    Assert.assertNotNull(resourceBundle, resourceBundle);
                    configProperties.load(propertyResource);
                    LOG.info("Added resource bundle " + resourceBundle);
                }
            }

            config.putProperties(configProperties);
            config.putProperty("application.id", applicationId);

            ConfigContext.init(config);

            MockServletContext servletContext = new MockServletContext();
            GlobalResourceLoader.addResourceLoader(new SpringResourceLoader(new QName("KRAD-UifDefaults"), Arrays
                    .asList(
                    "KRAD-UifDefaults-test-context.xml"), servletContext));
            GlobalResourceLoader.addResourceLoader(new SpringResourceLoader(new QName(applicationId), Arrays.asList(
                    applicationId + "-test-context.xml"), servletContext));

            TL_CONFIG_PROPERTIES.set(ConfigContext.getCurrentContextConfig().getProperties());
            try {
                GlobalResourceLoader.start();
                Lifecycle viewService = GlobalResourceLoader.getService("viewService");

                if (viewService != null) {
                    viewService.start();
                }

            } finally {
                TL_CONFIG_PROPERTIES.remove();
            }

            configureKradWebApplicationContext();
        } catch (Throwable t) {
            LOG.error("Skipping tests, resource setup failed", t);
            Assume.assumeNoException("Skipping tests, resource setup failed", t);
        }
    }

    /**
     * Shut down the mock configuration. When {@link #establishMockConfig(String)} is used with
     * {@link BeforeClass}, then this method should be used with {@link AfterClass} to tear down
     * resources.
     */
    public static void tearDownMockConfig() throws Exception {
        GlobalResourceLoader.stop();
    }

    /**
     * Establish a user session with the given principal name.
     *
     * <p>
     * This method will use KIM API calls to look up a person with the provided principal name. Use
     * {@link #establishMockConfig(String)} to set up a mock KIM environment if needed.
     * </p>
     *
     * @param principalName The principal name of the user to establish a session with.
     */
    public static void establishMockUserSession(String principalName) {
        UserSession session = new UserSession(principalName);
        GlobalVariables.setUserSession(session);
    }

    /**
     * Shut down the mock user session. When {@link #establishMockUserSession(String)} is used with
     * {@link Before}, then this method should be used with {@link After} to tear down resources.
     */
    public static void tearDownMockUserSession() {
        GlobalVariables.setUserSession(null);
        GlobalVariables.clear();
    }

    /**
     * Get a view authorizer allowing most operations.
     *
     * @return A view authorizer allowing most operations.
     */
    public static ViewAuthorizer getAllowMostViewAuthorizer() {
        return new MockViewAuthorizer();
    }

}
