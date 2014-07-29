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
package org.kuali.rice.krad.uif.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeNoException;
import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Macro;
import freemarker.core.TemplateElement;
import freemarker.template.Template;

public class ComponentFreemarkerTest extends ProcessLoggingUnitTest {

    private static final Logger LOG = Logger.getLogger(ComponentFreemarkerTest.class);
    
    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ComponentFreemarkerTest");
        try {
            ComponentFactory.getMessage();
        } catch (Throwable t) {
            LOG.error("Skipping tests, message component is not available", t);
            Assume.assumeNoException("Skipping tests, message component is not available", t);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        UifUnitTestUtils.tearDownMockConfig();
    }

    @Test
    public void testMessage() throws Throwable {
        Message m = ComponentFactory.getMessage();
        m.setMessageText("foobar");
        m.setWrapperTag("span");
        m.setId("_span");

        FreeMarkerViewResolver viewResolver = (FreeMarkerViewResolver)
                UifUnitTestUtils.getWebApplicationContext().getBean("viewResolver");
        assertNotNull(viewResolver);

        assert m.getTemplate().endsWith(".ftl");
        FreeMarkerView v = (FreeMarkerView) viewResolver.resolveViewName(
                m.getTemplate().substring(0, m.getTemplate().length() - 4),
                Locale.getDefault());
        assumeNotNull(v);

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

        assertEquals("<span id=\"_span\" class=\"uif-message\"     >\r\n" +
                "foobar  </span>", out.toString().trim());
    }

    @Test
    public void testMessageNoReflection() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        View view = mock(View.class);
        ViewLifecycle.encapsulateLifecycle(view, new Object(), request, new Runnable() {
            @Override
            public void run() {
                Message msg = CopyUtils.copy(ComponentFactory.getMessage());
                msg.setMessageText("foobar");
                msg.setId("_naps");
                msg.setWrapperTag("pans");

                msg.setViewStatus(UifConstants.ViewStatus.FINAL);

                RenderComponentPhase renderPhase = (RenderComponentPhase) KRADServiceLocatorWeb
                        .getViewLifecyclePhaseBuilder().buildPhase(UifConstants.ViewPhases.RENDER, msg, null, "", null);

                try {
                    ViewLifecycle.getProcessor().performPhase(renderPhase);
                } catch (IllegalStateException e) {
                    if (e.getCause() instanceof FileNotFoundException) {
                        assumeNoException(e.getCause());
                    } else {
                        throw e;
                    }
                }

                assertTrue(msg.isSelfRendered());
                assertEquals("<pans id=\"_naps\" class=\"uif-message\"     >\r\n" +
                        "foobar  </pans>", msg.getRenderedHtmlOutput().trim());
            }
        });
    }

}
