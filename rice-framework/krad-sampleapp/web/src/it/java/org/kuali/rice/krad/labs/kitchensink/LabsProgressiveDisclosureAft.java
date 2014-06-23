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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsProgressiveDisclosureAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page5#UifCompView-Page5
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page5#UifCompView-Page5";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Progressive Disclosure");
	}
	
	@Test
    public void testProgressiveDisclosureBookmark() throws Exception {
        testProgressiveDisclosure();
        passed();
    }

    @Test
    public void testProgressiveDisclosureNav() throws Exception {
        testProgressiveDisclosure();
        passed();
    }
    
    protected void testProgressiveDisclosure() throws InterruptedException {
    	//Progressive Disclosure of Fields
    	if(isElementPresentByName("field35") && isElementPresentByName("field36")) {
    		fail("Elements already present.");
    	}
    	waitAndClickByXpath("//input[@name='field34' and @value='show1']");
    	waitForElementPresentByName("field35");
    	waitAndClickByXpath("//input[@name='field34' and @value='show2']");
    	waitForElementPresentByName("field36");
    	waitAndClickByXpath("//input[@name='field34' and @value='show3']");
    	waitForElementPresentByName("field37");
//    	assertElementPresentByXpath("//input[@name='field48' and @disabled]");
//    	assertElementPresentByXpath("//input[@name='field49' and @disabled]");
    	waitAndTypeByName("field47","A");
    	assertTextPresent("Cancel");
    	waitForElementPresentByName("field48");
    	waitAndTypeByName("field47","B");
    	assertTextPresent("Cancel");
    	waitForElementPresentByName("field49");
    	
    	//Progressive Disclosure of Groups
    	if(isElementPresentByXpath("//div[@id='prog2-group1' and @style='display: block;']")
           || isElementPresentByXpath("//div[@id='prog2-group2' and @style='display: block;']")) {
    		fail("Groups already present.");
    	}
    	waitAndClickByXpath("//input[@name='field38' and @value='show1']");
    	assertElementPresentByXpath("//div[@data-parent='prog2-group1']");
    	waitAndClickByXpath("//input[@name='field38' and @value='show2']");
    	assertElementPresentByXpath("//div[@data-parent='prog2-group2']");
    	
    	//Conditional Refresh
    	waitAndTypeByName("field46","deepmoteria");
    	waitAndClickByXpath("//input[@name='field44' and @value='show2']");
    	waitForTextPresent("deepmoteria");
    	selectByName("field88","Fruits");
    	selectByName("field89","Apples");
    	selectByName("field88","Vegetables");
    	Thread.sleep(2000);
    	selectByName("field89","Beans");
    	
    	//Collection Group With Refresh
    	waitAndTypeByXpath("//div[@data-label='SubField 1']/input","show2");
    	assertTextPresent("add");
    	Thread.sleep(2000);
    	assertElementPresentByXpath("//label[contains(text(),'SubField 2:')]");
    }
}
