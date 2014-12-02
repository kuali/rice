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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.bind.UifServletRequestDataBinder;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.login.DummyLoginForm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Integration tests for proving correct operation of the ViewHelperService.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecycleTest extends KRADTestCase {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");

        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        UifUnitTestUtils.establishMockUserSession("admin");
    }

    @Override
    @After
    public void tearDown()throws Exception {
        UifUnitTestUtils.tearDownMockUserSession();

        super.tearDown();
    }

    @Test
    public void testSanity() throws Throwable {
        DummyLoginForm form = new DummyLoginForm();

        testFormView(form, "DummyLoginView");

        View view = form.getView();

        assertEquals("LoginPage", view.getCurrentPage().getId());
        assertEquals("Rice-UserName", ObjectPropertyUtils.getPropertyValue(view,
                "currentPage.items[0].items[1].items[1].items[1].items[3].id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMutability() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        View view = viewService.getViewById("DummyLoginView");

        Group group = ComponentFactory.getGroupWithDisclosureGridLayout();
        group.setId("foo");
        group.setHeaderText("bar");
        group.setItems(new ArrayList<Component>());

        ((List<Group>) view.getItems()).add(group);
        assertSame(group, view.getItems().get(view.getItems().size() - 1));
    }

    @Test
    public void testPagedView() throws Throwable {
        UifFormBase form = new UifFormBase();

        testFormView(form, "TestPagedView");

        form.setFormKey(null);
        form.setPageId("TestPagedView-Page2");

        testFormView(form, "TestPagedView");

        form.setFormKey(null);
        form.setPageId("TestPagedView-Page3");

        testFormView(form, "TestPagedView");
    }

    @Test
    public void testInitializationPhase() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        final View view = viewService.getViewById("TestPagedView");
        final UifFormBase form = new UifFormBase();

        ViewLifecycle.encapsulateLifecycle(view, form, null, new Runnable() {
            @Override
            public void run() {
                View currentView = ViewLifecycle.getView();

                assertSame(view, currentView);
                assertEquals("TestPagedView", currentView.getId());

                ProcessLogger.trace("begin-init");
                ViewLifecycle.getHelper().populateViewFromRequestParameters(Collections.<String, String> emptyMap());

                ProcessLogger.trace("populate-request");
                form.setViewRequestParameters(currentView.getViewRequestParameters());

                ViewLifecycle.getHelper().performCustomViewInitialization(form);

                ViewLifecyclePhase phase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                        UifConstants.ViewPhases.INITIALIZE, view, null, "", null);
                ViewLifecycle.getProcessor().performPhase(phase);

                ProcessLogger.trace("end-init");
            }
        });
    }

    private UifFormBase testFormView(UifFormBase form, String viewName) throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        View view = viewService.getViewById(viewName);
        form.setView(view);
        assertEquals(UifConstants.ViewStatus.CREATED, view.getViewStatus());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(UifParameters.VIEW_ID, viewName);
        new UifServletRequestDataBinder(form).bind(request);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, form);

        KRADServiceLocatorWeb.getModelAndViewService().prepareView(request, modelAndView);

        view = form.getView();
        assertEquals(UifConstants.ViewStatus.FINAL, view.getViewStatus());

        return form;
    }

}