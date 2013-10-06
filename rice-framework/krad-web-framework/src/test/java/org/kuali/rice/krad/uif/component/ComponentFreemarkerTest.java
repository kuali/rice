/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.component;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.freemarker.FreeMarkerInlineRenderBootstrap;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Macro;
import freemarker.core.TemplateElement;
import freemarker.template.Template;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ComponentFreemarkerTest {

    private static ConfigurableWebApplicationContext webApplicationContext;

    @BeforeClass
    public static void setupClass() {
        MockServletContext sctx = new MockServletContext();
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.setServletContext(sctx);

        MutablePropertyValues mpv = new MutablePropertyValues();
        mpv.add("preferFileSystemAccess", false);
        mpv.add("templateLoaderPath", "/krad-web_2_4_M2");
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
        webApplicationContext = ctx;
    }

    @AfterClass
    public static void teardownClass() {
        if (webApplicationContext != null) {
            webApplicationContext.stop();
            webApplicationContext.close();
        }
    }

    @Test
    public void testMessage() throws Throwable {
        Message m = ViewLifecycle.encapsulateInitialization(new Callable<Message>() {

            @Override
            public Message call() throws Exception {
                Message m = new Message();
                m.setTemplate("/krad/WEB-INF/ftl/components/element/message.ftl");
                m.setTemplateName("uif_message");
                m.setCssClasses(Arrays.asList("uif-message"));
                m.setMessageText("foobar");
                return m;
            }
        });

        FreeMarkerViewResolver viewResolver = (FreeMarkerViewResolver) webApplicationContext.getBean("viewResolver");
        assertNotNull(viewResolver);

        assert m.getTemplate().endsWith(".ftl");
        FreeMarkerView v = (FreeMarkerView) viewResolver.resolveViewName(
                m.getTemplate().substring(0, m.getTemplate().length() - 4),
                Locale.getDefault());
        assertNotNull(v);

        Method getTemplate = FreeMarkerView.class.getDeclaredMethod("getTemplate", Locale.class);
        getTemplate.setAccessible(true);
        Template template = (Template) getTemplate.invoke(v, Locale.getDefault());
        assertNotNull(template);

        Macro macro = (Macro) template.getMacros().get(m.getTemplateName());
        assertNotNull(macro);

        Map<String, Object> rootMap = new java.util.HashMap<String, Object>();
        rootMap.put("component", m);
        StringWriter out = new StringWriter();
        Environment env = template.createProcessingEnvironment(rootMap, out);

        env.importLib("/krad/WEB-INF/ftl/lib/krad.ftl", "krad");
        env.importLib("/krad/WEB-INF/ftl/lib/spring.ftl", "spring");

        Class<?> identifier = Class.forName("freemarker.core.Identifier");
        Constructor<?> newIdentifier = identifier.getDeclaredConstructor(String.class);
        newIdentifier.setAccessible(true);
        Map<String, Expression> args = new java.util.HashMap<String, Expression>();
        args.put(m.getComponentTypeName(), (Expression) newIdentifier.newInstance("component"));

        Method visit = Environment.class.getDeclaredMethod("visit", Macro.class, Map.class, List.class, List.class,
                TemplateElement.class);
        visit.setAccessible(true);
        visit.invoke(env, macro, args, null, null, null);

        assertEquals("<span id=\"_span\" class=\"uif-message\"   >\r\n" +
                "foobar    </span>", out.toString().trim());
    }

    @Test
    public void testMessageNoReflection() throws Throwable {
        final Message m = ViewLifecycle.encapsulateInitialization(new Callable<Message>() {
            @Override
            public Message call() throws Exception {
                Message m = new Message();
                m.setTemplate("/krad/WEB-INF/ftl/components/element/message.ftl");
                m.setTemplateName("uif_message");
                m.setCssClasses(Arrays.asList("uif-message"));
                m.setMessageText("foobar");
                return m;
            }
        });

        View view = mock(View.class);
        ViewLifecycle.encapsulateLifecycle(view, new Runnable() {
            @Override
            public void run() {
                Message msg = m.copy();
                ViewLifecycle.getActiveLifecycle().performComponentRender(msg);
                
                assertTrue(msg.isSelfRendered());
                assertEquals("<span id=\"_span\" class=\"uif-message\"   >\r\n" +
                        "foobar    </span>", msg.getRenderedHtmlOutput().trim());
            }
        });
    }

}
