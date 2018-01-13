/**
 * Copyright 2005-2018 The Kuali Foundation
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
package edu.sampleu.kim.api.reference;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AdminExternaldentifierTypeAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=External%20Identifier%20Type&channelUrl=" + WebDriverUtils
     * .getBaseUrlString() + "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo&docFormKey=88888888&returnLocation=" +
     * AutomatedFunctionalTestUtils.PORTAL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=External%20Identifier%20Type&channelUrl=" + WebDriverUtils
            .getBaseUrlString() + "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("External Identifier Type");
    }
    
    private void testExternaldentifierType() throws Exception{
        selectFrameIframePortlet();
        waitAndTypeByName("code","HR");
        waitAndClickByXpath("//input[@value='search']");
        String customSearchResult [] ={"HR","Human Resources ID","05","Yes"};
        assertTextPresent(customSearchResult);
        waitAndClickByXpath("//input[@value='clearValues']");
        waitAndClickByXpath("//input[@value='search']");
        waitForTextPresent("items retrieved, displaying all items.");
    }
    
    @Test
    public void testExternaldentifierTypeBookmark() throws Exception {
        testExternaldentifierType();
        passed();
    }

    @Test
    public void testExternaldentifierTypeNav() throws Exception {
        testExternaldentifierType();
        passed();
    }
    
}
