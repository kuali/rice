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
package edu.sampleu.admin.servicebus;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SecurityManagementAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Security%20Management&channelUrl="+WebDriverUtils
     * .getBaseUrlString()+"/ksb/JavaSecurityManagement.do";
     */
    public static final String BOOKMARK_URL =AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Security%20Management&channelUrl="+WebDriverUtils
            .getBaseUrlString()+"/ksb/JavaSecurityManagement.do";
  
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Security Management");
    }
    
    private void testSecurityManagement() throws Exception{
        selectFrameIframePortlet();
        waitAndClickByName("methodToCall.generateClientKeyStore");
        waitForTextPresent("Alias must have a valid value.");
        waitForTextPresent("Password must have a valid value in both fields.");
        String [] assertText={"Alias","Create Date","Type","Actions","rice","Private Key"};
        assertTextPresent(assertText);
    }
    
    @Test
    public void testSecurityManagementBookmark() throws Exception {
        testSecurityManagement();
        passed();
    }

    @Test
    public void testSecurityManagementNav() throws Exception {
        testSecurityManagement();
        passed();
    }
    
}
