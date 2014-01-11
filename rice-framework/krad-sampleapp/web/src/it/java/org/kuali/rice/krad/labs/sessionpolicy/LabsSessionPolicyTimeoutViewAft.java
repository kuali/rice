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
package org.kuali.rice.krad.labs.sessionpolicy;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsSessionPolicyTimeoutViewAft extends LabsSessionPolicyBase {

    /**
     * /kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-TimeoutView
     */
    public static final String BOOKMARK_URL = "/kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-TimeoutView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToSessionPolicy("Session Policy Timeout View");
    }

    protected void testDemoSessionPolicyTimeoutView() throws InterruptedException {
    	waitForElementPresentByXpath("//button[contains(text(),'Kill Session')]");
    	assertElementPresentByXpath("//button[contains(text(),'Ajax Request')]");
    	assertElementPresentByXpath("//button[contains(text(),'NonAjax Request')]");
    	waitAndClickByXpath("//button[contains(text(),'Kill Session')]");
    	waitAndClickByXpath("//button[contains(text(),'Ajax Request')]");
    	Thread.sleep(3000);
    	waitAndTypeByName("login_user","admin");
    	waitAndClickByXpath("//button[@id='Rice-LoginButton']");
    	Thread.sleep(3000);
    	assertTextPresent("Your Session has timed out and the request could not be completed.");
    }

    @Test
    public void testDemoSessionPolicyTimeoutViewBookmark() throws Exception {
    	testDemoSessionPolicyTimeoutView();
        passed();
    }

    @Test
    public void testDemoSessionPolicyTimeoutViewNav() throws Exception {
    	testDemoSessionPolicyTimeoutView();
        passed();
    }
}
