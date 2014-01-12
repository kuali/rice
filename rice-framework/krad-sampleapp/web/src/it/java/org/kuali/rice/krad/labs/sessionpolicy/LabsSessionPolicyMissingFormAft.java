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
public class LabsSessionPolicyMissingFormAft extends LabsSessionPolicyBase {

    /**
     * /kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-MissingForm
     */
    public static final String BOOKMARK_URL = "/kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-MissingForm";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToSessionPolicy("Session Policy Missing Form");
    }

    protected void testDemoSessionPolicyMissingForm() throws InterruptedException {
    	waitAndClickByLinkText("Post Missing Form");
    	Thread.sleep(3000);
    	assertTextPresent("Your Session has timed out and the request could not be completed.");
    }

    @Test
    public void testDemoSessionPolicyMissingFormBookmark() throws Exception {
    	testDemoSessionPolicyMissingForm();
        passed();
    }

    @Test
    public void testDemoSessionPolicyMissingFormNav() throws Exception {
    	testDemoSessionPolicyMissingForm();
        passed();
    }
}
