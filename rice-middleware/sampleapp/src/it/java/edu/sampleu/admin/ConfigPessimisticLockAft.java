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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigPessimisticLockAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Pessimistic%20Lock&channelUrl="+ WebDriverUtils
     * .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.krad.document.authorization.PessimisticLock&returnLocation="+
     *  AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK+"&docFormKey=88888888";
     */    
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Pessimistic%20Lock&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.krad.document.authorization.PessimisticLock&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK+"&docFormKey=88888888";
   
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickAdministration();
        waitAndClickByLinkText("Pessimistic Lock");
    }

    @Test
    public void testPessimisticLockBookmark() throws Exception {
        testPessimisticLock();
        passed();
    }

    @Test
    public void testPessimisticLockNav() throws Exception {
        testPessimisticLock();
        passed();
    }
    
    protected void testPessimisticLock() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForTextPresent("No values match this search.");
    }
}
