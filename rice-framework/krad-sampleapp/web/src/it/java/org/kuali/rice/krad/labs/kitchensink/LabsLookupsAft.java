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

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupsAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page6&lightbox=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page6&lightbox=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Lookups, etc");
	}

    @Test
    @Ignore //Ignore this test due to the issues found in KULRICE-13108.  They will fail until a solution can be found.
    public void testAjaxLookupBookmark() throws Exception {
        testAjaxLookup();
        passed();
    }

    @Test
    @Ignore //Ignore this test due to the issues found in KULRICE-13108.  They will fail until a solution can be found.
    public void testAjaxLookupNav() throws Exception {
        testAjaxLookup();
        passed();
    }

	@Test
    public void testLookupsBookmark() throws Exception {
        testLookups();
        passed();
    }

    @Test
    public void testLookupsNav() throws Exception {
        testLookups();
        passed();
    }

    protected void testAjaxLookup() throws InterruptedException {
        clearTextByName("field79");
        waitAndTypeByName("field79", "a3");
        ((JavascriptExecutor)driver).executeScript("document.getElementsByName('field79')[0].blur();");
        waitForTextPresent("Travel Account 3");
    }

    protected void testLookups() throws InterruptedException {
// a2 link is now gone, bug or feature?
//        waitAndClickByLinkText("a2");
//        gotoLightBox();
//        assertTextPresent(new String[] {"Travel Account Number:", "a2", "Travel Account Name:", "Travel Account 2", "Code And Description:", "EAT - Expense"});
//        waitAndClickButtonByText("Close");

        clearTextByName("field72");
    	waitAndTypeByName("field72", "a2");
        fireEvent("field72", "blur");
        waitAndClickByName("field76"); // force blur on field72
    	waitForTextPresent("Travel Account 2");

        clearTextByName("field76");
    	waitAndTypeByName("field76", "a1");
        fireEvent("field76", "blur");
        waitForTextPresent("Travel Account 1");

        waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
    	gotoLightBoxIframe();
        waitAndClickSearchByText();
    	waitAndClickReturnValue();
        waitAndClickByXpath("//div[@data-parent='refreshLookups1']/div/div/button[@class='btn btn-default uif-action icon-search']");
        gotoLightBoxIframe();
        waitAndClickSearchByText();
        waitAndClickReturnValue();

        clearTextByName("field70");
        waitAndTypeByName("field70", "a1");
        waitAndClickByXpath("//button[@title='Direct Inquiry']");
        gotoLightBox();
        assertTextPresent(new String[] {"Travel Account Number:", "a1", "Travel Account Name:", "Travel Account 1", "Code And Description:", "IAT - Income"});
        waitAndClickButtonByText("Close");
    }
}
