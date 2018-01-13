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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.LibraryBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryWidgetsInquiryAft extends LibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-InquiryView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-InquiryView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Inquiry");
    }

    protected void testWidgetsInquiry() throws Exception {
        waitAndClickByLinkText("a2");
    	gotoLightBox();

        // the primary key should be a text field with no link
        waitForElementPresentByXpath("//div[@data-label = 'Travel Account Number']");
        waitForElementNotPresent(By.xpath("//a[contains(text(),'a2')]"));
        assertElementPresent(By.xpath("//div[@data-label = 'Travel Account Number']/span[contains(text(),'a2')]"));

    	waitForTextPresent("Travel Account 2");

        waitAndClickLightBoxClose();
    	selectTopFrame();
    }

    protected void testWidgetDirectInquiry() throws Exception {
        waitAndClickByLinkText("Direct Inquiry");
        jGrowl("Click Direct Inquiry button");
        waitAndClickByXpath("//button[@title='Direct Inquiry']");
        gotoLightBox();

        // the primary key should be a text field with no link
        waitForElementPresentByXpath("//div[@data-label = 'Travel Account Number']");
        waitForElementNotPresent(By.xpath("//a[contains(text(),'a2')]"));
        assertElementPresent(By.xpath("//div[@data-label = 'Travel Account Number']/span[contains(text(),'a1')]"));

   	    waitForTextPresent("Travel Account 1");

        waitAndClickLightBoxClose();
    }
 
    private void testAllInquiry() throws Exception {
    	testWidgetsInquiry();
    	testWidgetDirectInquiry();
	    passed();
    }

    @Test
    public void testWidgetsInquiryBookmark() throws Exception {
    	testAllInquiry();
    }

    @Test
    public void testWidgetsInquiryNav() throws Exception {
    	testAllInquiry();
    }
}
