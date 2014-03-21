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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class UifComponentsKitchenSinkSelectActionFieldsAft extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
	    waitAndClickKRAD();
	    waitAndClickByLinkText("Uif Components (Kitchen Sink)");
	    switchToWindow("Kuali :: Uif Components");
	}
	
	@Test
    public void testSelectActionBookmark() throws Exception {
        testSelectAction();
        passed();
    }

    @Test
    public void testSelectActionNav() throws Exception {
        testSelectAction();
        passed();
    }
    
    protected void testSelectAction() throws InterruptedException {
        waitAndClickByXpath("//a/span[contains(text(),'Select Action')]");
    	waitAndClickLinkContainingText("Action Script1");
    	acceptAlertIfPresent();
    	waitAndClickLinkContainingText("Action Script2");
        acceptAlertIfPresent();
        waitAndClickLinkContainingText("Action Script3");
        acceptAlertIfPresent();
        waitAndClickLinkContainingText("Action Script4");
        acceptAlertIfPresent();
    }
}
