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
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.layout.collections.DataTablesPagingHelper;
import org.kuali.rice.krad.uif.layout.collections.DataTablesPagingHelper.DataTablesInputs;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.bind.UifServletRequestDataBinder;
import org.kuali.rice.krad.web.controller.UifControllerHelper;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.login.DummyLoginForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

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
        UifUnitTestUtils.establishMockConfig("KRAD-ViewLifecycleTest");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");

        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
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
        DummyLoginForm loginForm = new DummyLoginForm();
        request.setParameter(UifParameters.VIEW_ID, "DummyLoginView");
        new UifServletRequestDataBinder(loginForm).bind(request);
        UifControllerHelper.invokeViewLifecycle(request, loginForm);
        View dummyLogin = loginForm.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, dummyLogin.getViewStatus());
        assertEquals("LoginPage", dummyLogin.getCurrentPage().getId());
        assertEquals("Rice-UserName",
                ObjectPropertyUtils.getPropertyValue(dummyLogin,
                        "currentPage.items[0].items[1].items[1].items[1].items[3].id"));
    }

    private UifFormBase testFormView(String viewName) throws Throwable {
        return testFormView(null, viewName);
    }

    private UifFormBase testFormView(UifFormBase form, String viewName) throws Throwable {
        View view = KRADServiceLocatorWeb.getDataDictionaryService().getViewById(viewName);
        if (form == null) {
            form = (UifFormBase) view.getFormClass().newInstance();
        }
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(UifParameters.VIEW_ID, viewName);
        new UifServletRequestDataBinder(form).bind(request);
        UifControllerHelper.invokeViewLifecycle(request, form);
        view = form.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, view.getViewStatus());
        return form;
    }

    @Test
    public void testKitchenSinkView() throws Throwable {
        UifFormBase form = testFormView("UifCompView");
        form.setFormKey(null);
        form.setPageId("UifCompView-Page2");
        testFormView(form, "UifCompView");
        form.setFormKey(null);
        form.setPageId("UifCompView-Page7");
        testFormView(form, "UifCompView");
    }

    @Test
    public void testTransactionView() throws Throwable {
        testFormView((UifFormBase) Class
                .forName("org.kuali.rice.krad.labs.transaction.TransactionForm").newInstance(),
                "TransactionView");
    }

    @Test
    public void testLabsMenuView() throws Throwable {
        testFormView("LabsMenuView");
    }

    @Test
    public void testTransactionInitPhase() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        final View transactionView = viewService.getViewById("TransactionView");
        final UifFormBase tform = new UifFormBase();
        ViewLifecycle.encapsulateLifecycle(transactionView, tform, null, new Runnable() {
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
                ViewLifecyclePhase phase = LifecyclePhaseFactory.buildPhase(UifConstants.ViewPhases.INITIALIZE);
                phase.prepareView();
                processor.performPhase(phase);

                ProcessLogger.trace("end-init");
            }
        });
    }

    @Test
    public void testComponentLibrary() throws Throwable {
        testFormView("ComponentLibraryHome");
    }

    @Test
    public void testColumnCalculations() throws Throwable {
        testFormView("Demo-TableLayoutTotalingView");
    }

    @Ignore
    @Test
    public void testPerformanceMediumAll() throws Throwable {
        UifFormBase form = testFormView("Lab-PerformanceMedium");

        View view = form.getView();
        ViewPostMetadata viewPostMetadata = form.getViewPostMetadata();
        form.setView(null);

        final CollectionGroup table = (CollectionGroup) ((PageGroup) view.getItems().get(0)).getItems().get(1);
        // TODO: determine why this changes intermittently in async mode
        // assertEquals("u1c5ay4e", table.getId());

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("methodToCall", "tableJsonRetrieval");
        request.setParameter("tableId", table.getId());
        request.setParameter("ajaxReturnType", "update-none");
        request.setParameter("ajaxRequest", "true");
        request.setParameter("sEcho", "1");
        request.setParameter("iColumns", "11");
        request.setParameter("sColumns", "");
        request.setParameter("iDisplayStart", "0");
        request.setParameter("iDisplayLength", "10");
        request.setParameter("mDataProp_0", "function");
        request.setParameter("mDataProp_1", "function");
        request.setParameter("mDataProp_2", "function");
        request.setParameter("mDataProp_3", "function");
        request.setParameter("mDataProp_4", "function");
        request.setParameter("mDataProp_5", "function");
        request.setParameter("mDataProp_6", "function");
        request.setParameter("mDataProp_7", "function");
        request.setParameter("mDataProp_8", "function");
        request.setParameter("mDataProp_9", "function");
        request.setParameter("mDataProp_10", "function");
        request.setParameter("sSearch", "");
        request.setParameter("bRegex", "false");
        request.setParameter("sSearch_0", "");
        request.setParameter("bRegex_0", "false");
        request.setParameter("bSearchable_0", "true");
        request.setParameter("sSearch_1", "");
        request.setParameter("bRegex_1", "false");
        request.setParameter("bSearchable_1", "true");
        request.setParameter("sSearch_2", "");
        request.setParameter("bRegex_2", "false");
        request.setParameter("bSearchable_2", "true");
        request.setParameter("sSearch_3", "");
        request.setParameter("bRegex_3", "false");
        request.setParameter("bSearchable_3", "true");
        request.setParameter("sSearch_4", "");
        request.setParameter("bRegex_4", "false");
        request.setParameter("bSearchable_4", "true");
        request.setParameter("sSearch_5", "");
        request.setParameter("bRegex_5", "false");
        request.setParameter("bSearchable_5", "true");
        request.setParameter("sSearch_6", "");
        request.setParameter("bRegex_6", "false");
        request.setParameter("bSearchable_6", "true");
        request.setParameter("sSearch_7", "");
        request.setParameter("bRegex_7", "false");
        request.setParameter("bSearchable_7", "true");
        request.setParameter("sSearch_8", "");
        request.setParameter("bRegex_8", "false");
        request.setParameter("bSearchable_8", "true");
        request.setParameter("sSearch_9", "");
        request.setParameter("bRegex_9", "false");
        request.setParameter("bSearchable_9", "true");
        request.setParameter("sSearch_10", "");
        request.setParameter("bRegex_10", "false");
        request.setParameter("bSearchable_10", "true");
        request.setParameter("iSortCol_0", "0");
        request.setParameter("sSortDir_0", "asc");
        request.setParameter("iSortingCols", "1");
        request.setParameter("bSortable_0", "false");
        request.setParameter("bSortable_1", "false");
        request.setParameter("bSortable_2", "true");
        request.setParameter("bSortable_3", "true");
        request.setParameter("bSortable_4", "true");
        request.setParameter("bSortable_5", "true");
        request.setParameter("bSortable_6", "true");
        request.setParameter("bSortable_7", "true");
        request.setParameter("bSortable_8", "false");
        request.setParameter("bSortable_9", "true");
        request.setParameter("bSortable_10", "false");

        ViewLifecycle.encapsulateLifecycle(view, form, viewPostMetadata,
                viewPostMetadata.getComponentPostMetadata(table.getId()), request, new Runnable() {
                    @Override
                    public void run() {
                        DataTablesPagingHelper.processPagingRequest(ViewLifecycle.getView(),
                                (ViewModel) ViewLifecycle.getModel(), table, new DataTablesInputs(request));
                    }
                });
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
