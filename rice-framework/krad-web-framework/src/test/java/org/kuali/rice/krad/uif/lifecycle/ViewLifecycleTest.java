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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.sampleapp_2_4_M2.labs.KradLabsForm;
import org.kuali.rice.krad.sampleapp_2_4_M2.labs.kitchensink.UifComponentsTestForm;
import org.kuali.rice.krad.sampleapp_2_4_M2.labs.transaction.TransactionForm;
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
import org.kuali.rice.krad.uif.util.ViewCleaner;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.bind.UifServletRequestDataBinder;
import org.kuali.rice.krad.web.controller.UifControllerHelper;
import org.kuali.rice.krad.web.controller.helper.DataTablesPagingHelper;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.login.DummyLoginForm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Unit tests for proving correct operation of the ViewHelperService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecycleTest extends ProcessLoggingUnitTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ViewHelperServiceTest");
    }

    @Before
    public void setUp() throws Throwable {
        UifUnitTestUtils.establishMockUserSession("admin");
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
                ObjectPropertyUtils.getPropertyValue(dummyLogin, "currentPage.items[0].items[1].items[1].items[1].items[3].id"));
        
        ViewCleaner.cleanView(dummyLogin);
    }

    @Test
    public void testKitchenSinkView() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        View uifCompView = viewService.getViewById("UifCompView");
        UifComponentsTestForm uifcompform = new UifComponentsTestForm();
        // TODO:
        //        ViewLifecycle.buildView(uifCompView, uifcompform, Collections.<String, String> emptyMap());
    }
    
    @Test
    public void testTransactionView() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        TransactionForm tform = new TransactionForm();
        request.setParameter(UifParameters.VIEW_ID, "TransactionView");
        new UifServletRequestDataBinder(tform).bind(request);
        UifControllerHelper.prepareViewForRendering(request, response, tform);
        View transactionView = tform.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, transactionView.getViewStatus());
        ViewCleaner.cleanView(transactionView);
    }
    
    @Test
    public void testTransactionViewOnly() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        viewService.getViewById("TransactionView");
    }
    
    @Test
    public void testTransactionInitPhase() throws Throwable {
        ViewService viewService = KRADServiceLocatorWeb.getViewService();
        final View transactionView = viewService.getViewById("TransactionView");
        final UifFormBase tform = new UifFormBase();
        ViewLifecycle.encapsulateLifecycle(transactionView, tform, null, null, new Runnable() {
            @Override
            public void run() {
                ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
                View view = ViewLifecycle.getView();
                assertSame(transactionView, view);
                
                assertEquals("TransactionView", view.getId());
                
                ProcessLogger.trace("begin-init");
                viewLifecycle.populateViewFromRequestParameters(Collections.<String, String> emptyMap());
                
                ProcessLogger.trace("populate-request");
                tform.setViewRequestParameters(view.getViewRequestParameters());
                
                ViewLifecycle.getHelper().performCustomViewInitialization(tform);

                ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
                processor.performPhase(LifecyclePhaseFactory.initialize(view, tform));
                
                ProcessLogger.trace("end-init");
            }});
    }

    @Test
    public void testPerformanceMediumAll() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        KradLabsForm pform = new KradLabsForm();
        request.setParameter(UifParameters.VIEW_ID, "Lab-PerformanceMedium");
        new UifServletRequestDataBinder(pform).bind(request);
        UifControllerHelper.prepareViewForRendering(request, response, pform);
        View performanceView = pform.getView();
        assertEquals(UifConstants.ViewStatus.RENDERED, performanceView.getViewStatus());
        ViewCleaner.cleanView(performanceView);
        pform.setPostedView(performanceView);
        pform.setView(null);

        assertEquals("uwdv4lg", performanceView.getItems().get(0).getItems().get(1).getId());
        String tableId = "uwdv4lg";

        request = new MockHttpServletRequest();
        request.setParameter("methodToCall", "tableJsonRetrieval");
        request.setParameter("tableId", tableId);
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

        DataTablesPagingHelper.DataTablesInputs dataTablesInputs =
                new DataTablesPagingHelper.DataTablesInputs(request);
        DataTablesPagingHelper pagingHelper = new DataTablesPagingHelper();
        pagingHelper.processPagingRequest(tableId, pform, request, response, dataTablesInputs);
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
        assertSame(group, loginView.getItems().get(loginView.getItems().size()-1));
    }

}
