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
package org.kuali.rice.krad.uif.view;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;

/**
 * Unit tests for proving correct operation of demo lookup views.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookupViewTest extends ProcessLoggingUnitTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-DemoLookupViewTest");
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
/*

    @Test
    public void testSanity() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LookupForm lookupForm = new LookupForm();
        request.setParameter(UifParameters.VIEW_ID, "LookupSampleView");
        new UifServletRequestDataBinder(lookupForm).bind(request);
        // TODO: tie in mock data service
        //        UifControllerHelper.invokeViewLifecycle(request, response, lookupForm);
        //        View dummyLogin = lookupForm.getView();
        //        assertEquals(UifConstants.ViewStatus.RENDERED, dummyLogin.getViewStatus());
        //        ViewCleaner.cleanView(dummyLogin);
    }
*/

}
