/*
 * Copyright 2005-2013 The Kuali Foundation
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

public class LabsLookupsAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=49dbef40-65ef-424a-ae8a-1741cf947fee&cacheKey=eu0yn3vhdhhb753reybcvd&pageId=UifCompView-Page6&lightbox=true#UifCompView-Page6";

    private static final String IFRAME_XPATH="//iframe[@class='fancybox-iframe']";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Lookups, etc");
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
    
    protected void testLookups() throws InterruptedException {
        waitAndClickByLinkText("a2");
        gotoLightBox();
        assertTextPresent(new String[] {"Travel Account Number:", "a2", "Travel Account Name:", "Travel Account 2", "Code And Description:", "EAT - Expense"});
        waitAndClickButtonByText("Close");

    	clearTextByName("field72");
    	waitAndTypeByName("field72","a2");
        fireEvent("field72", "blur");
    	waitForTextPresent("Travel Account 2");

        clearTextByName("field76");
    	waitAndTypeByName("field76","a1");
        fireEvent("field76", "blur");
        waitForTextPresent("Travel Account 1");

        waitAndTypeByName("field79", "");
        fireEvent("field79", "blur");
        waitForTextPresent("Travel Account 3");

    	waitAndClickByXpath("//a[@class='uif-actionLink icon-search']");
    	gotoIframeByXpath(IFRAME_XPATH);
        waitAndClickButtonByText("Search");
    	waitAndClickReturnValue();
    	waitAndClickByXpath("//div[@data-parent='refreshLookups1']/div/span/a");
    	gotoIframeByXpath(IFRAME_XPATH);
        waitAndClickButtonByText("Search");
        waitAndClickReturnValue();

        waitAndClickByXpath("//input[@title='Direct Inquiry']");
        gotoLightBox();
        assertTextPresent(new String[] {"Travel Account Number:", "a1", "Travel Account Name:", "Travel Account 1", "Code And Description:", "IAT - Income"});
        waitAndClickButtonByText("Close");
    }
}
