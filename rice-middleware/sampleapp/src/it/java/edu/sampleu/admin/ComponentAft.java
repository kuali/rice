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
package edu.sampleu.admin;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentAft extends ConfigComponentAftBase {

    String docId;

    /**
     * ITUtil.PORTAL + "?channelTitle=Component&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=" +
     ITUtil.PORTAL_URL + "&hideReturnLink=true";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Component&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + "&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void testComponentParameter() throws Exception {
        //Create New
        namespaceCode = "KR-IDM";
        uniqueString = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        String componentName = "name" + uniqueString; // same pattern as used in testCreateNewEnterDetails
        String componentCode = "code" + uniqueString; // same pattern as used in testCreateNewEnterDetails
        docId = testCreateNew();
        submitAndClose();

        //Lookup
        navigate();
        testLookUpComponent(docId, componentName, componentCode);

        //edit
        testEditComponent(docId, componentName, componentCode);

        //Verify if its edited
        navigate();
        testVerifyEditedComponent(docId, componentName, componentCode);

        //copy
        testCopyComponent(docId, componentName + "copy", componentCode + "copy");

        //Verify if its copied
        navigate();
        testVerifyCopyComponent(docId, componentName + "copy", componentCode + "copy");
        passed();
    }

    @Test
    public void testComponentParameterBookmark() throws Exception {
        testComponentParameter();
    }

    @Test
    public void testComponentParameterNav() throws Exception {
        testComponentParameter();
    }
}
