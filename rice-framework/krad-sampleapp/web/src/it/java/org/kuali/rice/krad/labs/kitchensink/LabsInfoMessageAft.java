/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInfoMessageAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView?viewId=UifCompView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
        waitAndClickByLinkText("Kitchen Sink");
	}
	
	@Test
    public void testOtherExamplesBookmark() throws Exception {
        testInfoMessage();
        passed();
    }

    @Test
    public void testOtherExamplesNav() throws Exception {
        testInfoMessage();
        passed();
    }
    
    protected void testInfoMessage() throws InterruptedException {
    	waitForTextPresent("Selection Controls: 1 messages");
        waitForTextPresent("This info message should be on the Selection Controls section. There should also be a link to this message at the top of the page.");
        assertElementPresentByXpath("//div[@id='UifCompView-SelectFields_messages']");
        waitAndClickByLinkText("Selection Controls: 1 messages");
    }
}
