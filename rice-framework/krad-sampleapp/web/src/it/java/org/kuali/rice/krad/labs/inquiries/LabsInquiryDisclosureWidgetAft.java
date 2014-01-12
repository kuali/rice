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
package org.kuali.rice.krad.labs.inquiries;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDisclosureWidgetAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - Disclosure widget");
    }

    protected void testDemoInquiryDisclosureWidget() throws InterruptedException {
    	waitAndClickByLinkText("Link to demonstrate the use of the disclosure widget to open/close sections by default.");
    	waitForElementPresentByXpath("//div[@id='Demo-Disclosure-Example2']/div[2]/div[@class='uif-disclosureContent' and @style='display: none;']");
    }

    @Test
    public void testDemoInquiryDisclosureWidgetBookmark() throws Exception {
    	testDemoInquiryDisclosureWidget();
        passed();
    }

    @Test
    public void testDemoInquiryDisclosureWidgetNav() throws Exception {
    	testDemoInquiryDisclosureWidget();
        passed();
    }
}
