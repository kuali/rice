/**
 * Copyright 2005-2013 The Kuali Foundation
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
import java.util.Arrays;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.mock.web.MockServletContext;

/**
 * Utilities class for establishing a minimal environment for testing operations involving Uif
 * components.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifUnitTestUtils {

    private static ThreadLocal<Properties> TL_CONFIG_PROPERTIES = new ThreadLocal<Properties>();

    /**
     * Get the config properties for the current thread.
     * @return The config properties for the current thread.
     */
    public static Properties getConfigProperties() {
        return TL_CONFIG_PROPERTIES.get();
    }

    /**
     * Establish a Rice configuration providing enough mock services via
     * {@link GlobalResourceLoader} to support the use of KRAD UIF components in unit tests.
     * 
     * @param applicationId The application ID for the fake environment.
     * @throws Exception
     */
    public static void establishMockConfig(String applicationId) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        SimpleConfig config = new SimpleConfig();
        Properties configProperties = new Properties();

        InputStream defaultPropertyResource = loader.getResourceAsStream("KRAD-UifDefaults.properties");
        Assert.assertNotNull("KRAD-UifDefaults.properties", defaultPropertyResource);
        configProperties.load(defaultPropertyResource);

        InputStream appPropertyResource = loader.getResourceAsStream(applicationId + ".properties");
        Assert.assertNotNull(applicationId + ".properties", appPropertyResource);
        configProperties.load(appPropertyResource);

        config.putProperties(configProperties);
        config.putProperty("application.id", applicationId);
        
        ConfigContext.init(config);

        MockServletContext servletContext = new MockServletContext();
        GlobalResourceLoader.addResourceLoader(new SpringResourceLoader(new QName("KRAD-UifDefaults"), Arrays
                .asList("KRAD-UifDefaults-test-context.xml"), servletContext));
        GlobalResourceLoader.addResourceLoader(new SpringResourceLoader(new QName(applicationId), Arrays
                .asList(applicationId + "-test-context.xml"), servletContext));

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
     * Get a view authorizer allowing most operations.
     * @return A view authorizer allowing most operations.
     */
    public static ViewAuthorizer getAllowMostViewAuthorizer() {
        return new MockViewAuthorizer();
    }

}
