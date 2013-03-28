/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import java.util.List;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ComponentAbstractSmokeTestBase extends WebDriverLegacyITBase implements Failable {
    String docId;

    /**
     * ITUtil.PORTAL + "?channelTitle=Component&channelUrl=" + ITUtil.getBaseUrlString() +
     "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=" +
     ITUtil.PORTAL_URL + "&hideReturnLink=true";
     */
    public static final String BOOKMARK_URL = ITUtil.PORTAL + "?channelTitle=Component&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";

    protected void bookmark() {
        open(ITUtil.getBaseUrlString() + BOOKMARK_URL);
    }

    /**
     * Bookmark tests should call bookmark(), navigation tests should call navigation()
     * @throws Exception
     */
    protected abstract void gotoTest() throws Exception;

    protected void navigtaion() throws InterruptedException {
        waitAndClickAdministration(this);
        waitForTitleToEqualKualiPortalIndex();
        checkForIncidentReport("Component");
        selectFrameIframePortlet();
        waitAndClickByLinkText("Component");
//        selectFrame("relative=up");
        checkForIncidentReport("Component");
    }

    protected void testComponentCreateNewCancelBookmark(Failable failable) throws Exception {
        waitAndCreateNew();
        testCancelConfirmation();
        passed();
    }

    protected void testComponentCreateNewCancelNav(Failable failable) throws Exception {
        navigtaion(); // setUp only takes us to the portal, need to navigate to the test
        waitAndCreateNew();
        testCancelConfirmation();
        passed();
    }

    protected void testComponentParameterBookmark(Failable failable) throws Exception {
        testComponentParameter();
        passed();
    }

    protected void testComponentParameterNav(Failable failable) throws Exception {
        navigtaion(); // setUp only takes us to the portal, need to navigate to the test
        testComponentParameter();
        passed();
    }

    protected void testComponentParameter() throws Exception {
        //Create New
        waitAndCreateNew();
        List<String> params;
        params = testCreateNewComponent(docId, "testName" + ITUtil.DTS_TWO, "testCode" + ITUtil.DTS_TWO);

        //Lookup
        gotoTest();
        params = testLookUpComponent(params.get(0), params.get(1), params.get(2));

        //edit
        params = testEditComponent(params.get(0), params.get(1), params.get(2));

        //Verify if its edited
        gotoTest();
        params = testVerifyEditedComponent(params.get(0), params.get(1), params.get(2));

        //copy
        params = testCopyComponent(params.get(0), params.get(1) + "copy", params.get(2) + "copy");

        //Verify if its copied
        gotoTest();
        testVerifyCopyComponent(params.get(0), params.get(1), params.get(2));
    }
}
