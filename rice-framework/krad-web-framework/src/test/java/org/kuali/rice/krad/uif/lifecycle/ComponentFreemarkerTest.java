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
package org.kuali.rice.krad.uif.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Macro;
import freemarker.core.TemplateElement;
import freemarker.template.Template;

public class ComponentFreemarkerTest extends ProcessLoggingUnitTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ComponentFreemarkerTest");
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        UifUnitTestUtils.tearDownMockConfig();
    }

    @Test
    public void testMessage() throws Throwable {
        Message m = ComponentFactory.getMessage();
        m.setMessageText("foobar");

        FreeMarkerViewResolver viewResolver = (FreeMarkerViewResolver)
                UifUnitTestUtils.getWebApplicationContext().getBean("viewResolver");
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
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        View view = mock(View.class);
        ViewLifecycle.encapsulateLifecycle(view, new Object(), request, response, new Runnable() {
            @Override
            public void run() {
                Message msg = ComponentFactory.getMessage().copy();
                msg.setMessageText("foobar");
//                ViewLifecycle.getRenderingContext().importTemplate(msg.getTemplate());
                msg.setViewStatus(UifConstants.ViewStatus.FINAL);

                RenderComponentPhase renderPhase = LifecyclePhaseFactory.render(msg, null, 0);
                
                ViewLifecycle.getProcessor().performPhase(renderPhase);

                assertTrue(msg.isSelfRendered());
                assertEquals("<span id=\"_span\" class=\"uif-message\"   >\r\n" +
                        "foobar    </span>", msg.getRenderedHtmlOutput().trim());
            }
        });
    }

}
