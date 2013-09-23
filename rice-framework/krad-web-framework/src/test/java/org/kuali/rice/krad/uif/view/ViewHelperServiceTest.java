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
package org.kuali.rice.krad.uif.view;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.sampleapp_2_4_M2.labs.transaction.TransactionForm;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Unit tests for proving correct operation of the ViewHelperService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceTest extends ProcessLoggingUnitTest {

    private static ViewService viewService;

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ViewHelperServiceTest");
        viewService = KRADServiceLocatorWeb.getViewService();
    }

    @Before
    public void setUp() throws Throwable {
        UifUnitTestUtils.establishMockUserSession("admin");
    }

    @After
    public void tearDown() throws Throwable {
        GlobalVariables.setUserSession(null);
        GlobalVariables.clear();
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        GlobalResourceLoader.stop();
    }

    @Test
    public void testSanity() throws Throwable {
        assertNotNull(viewService);
        View transactionView = viewService.getViewById("TransactionView");
        assertNotNull(transactionView);
        assertNotNull(transactionView.getViewHelperService());
        TransactionForm form = new TransactionForm();
        viewService.buildView(transactionView, form, Collections.<String, String> emptyMap());
    }

}
