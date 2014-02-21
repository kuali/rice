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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
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
import org.kuali.rice.krad.web.controller.UifControllerHelper;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.login.DummyLoginForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Unit tests for proving correct operation of the ViewHelperService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecycleTest extends ProcessLoggingUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(ViewLifecycleTest.class);

    @Override
    protected int getRepetitions() {
        return 2;
    }

    @BeforeClass
    public static void setUpClass() throws Throwable {
        try {
            UifUnitTestUtils.establishMockConfig("KRAD-ViewLifecycleTest");
        } catch (Throwable t) {
            Assume.assumeNoException("Skipping tests, resource setup failed", t);
        }
    }

    @Before
    public void setUp() throws Throwable {
        UifUnitTestUtils.establishMockUserSession("admin");
        String async = Boolean.toString(getRepetition() == 1);
        ProcessLogger.trace("async:" + async);
        ConfigContext.getCurrentContextConfig().getProperties().setProperty(
                KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_ASYNCHRONOUS, async);
    }

    @After
    public void tearDown() throws Throwable {
        UifUnitTestUtils.tearDownMockUserSession();
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        UifUnitTestUtils.tearDownMockConfig();
    }

    @Test
    public void testSanity() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        DummyLoginForm loginForm = new DummyLoginForm();
        request.setParameter(UifParameters.VIEW_ID, "DummyLoginView");
        new UifServletRequestDataBinder(loginForm).bind(request);
        UifControllerHelper.prepareViewForRendering(request, response, loginForm);
        View dummyLogin = loginForm.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, dummyLogin.getViewStatus());
        assertEquals("LoginPage", dummyLogin.getCurrentPage().getId());
        assertEquals("Rice-UserName",
                ObjectPropertyUtils.getPropertyValue(dummyLogin,
                        "currentPage.items[0].items[1].items[1].items[1].items[3].id"));

//        ViewCleaner.cleanView(dummyLogin);
    }

    private UifFormBase testFormView(String viewName, String initialStateId) throws Throwable {
        return testFormView(null, viewName, initialStateId);
    }

    private UifFormBase testFormView(UifFormBase form, String viewName, String initialStateId) throws Throwable {
        View view = KRADServiceLocatorWeb.getDataDictionaryService().getViewById(viewName);
        if (form == null) {
            form = (UifFormBase) view.getFormClass().newInstance();
        }
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter(UifParameters.VIEW_ID, viewName);
        new UifServletRequestDataBinder(form).bind(request);
        UifControllerHelper.prepareViewForRendering(request, response, form);
        view = form.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, view.getViewStatus());
//        ViewCleaner.cleanView(view);
        return form;
    }


    @Test
    public void testKitchenSinkView() throws Throwable {
        UifFormBase form = testFormView("UifCompView", null);
        form.setFormKey(null);
        form.setPageId("UifCompView-Page2");
        testFormView(form, "UifCompView", null);
        form.setFormKey(null);
        form.setPageId("UifCompView-Page7");
        testFormView(form, "UifCompView", null);
    }
    @Ignore
    @Test
    public void testTransactionView() throws Throwable {
        testFormView((UifFormBase) Class
                .forName("org.kuali.rice.krad.labs.transaction.TransactionForm").newInstance(),
                "TransactionView", null);
    }

    @Test
    public void testLabsMenuView() throws Throwable {
        testFormView("LabsMenuView", null);
    }

    @Test
    public void testTransactionInitPhase() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        final View transactionView = viewService.getViewById("TransactionView");
        final UifFormBase tform = new UifFormBase();
        ViewLifecycle.encapsulateLifecycle(transactionView, tform, null, null, new Runnable() {
            @Override
            public void run() {
                View view = ViewLifecycle.getView();
                assertSame(transactionView, view);

                assertEquals("TransactionView", view.getId());

                ProcessLogger.trace("begin-init");
                ViewLifecycle.getHelper().populateViewFromRequestParameters(Collections.<String, String> emptyMap());

                ProcessLogger.trace("populate-request");
                tform.setViewRequestParameters(view.getViewRequestParameters());

                ViewLifecycle.getHelper().performCustomViewInitialization(tform);

                ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
                processor.performPhase(LifecyclePhaseFactory.initialize(view, tform, "", null));

                ProcessLogger.trace("end-init");
            }
        });
    }

    @Test
    public void testComponentLibrary() throws Throwable {
        testFormView("ComponentLibraryHome", null);
    }

    @Test
    public void testColumnCalculations() throws Throwable {
        testFormView("Demo-TableLayoutTotalingView", null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMutability() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        View loginView = viewService.getViewById("DummyLoginView");
        Group group = ComponentFactory.getGroupWithDisclosureGridLayout();
        group.setId("foo");
        group.setHeaderText("bar");
        group.setItems(new ArrayList<Component>());
        ((List<Group>) loginView.getItems()).add(group);
        assertSame(group, loginView.getItems().get(loginView.getItems().size() - 1));
    }

}
