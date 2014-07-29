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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDocumentHeaderAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Document Header");
    }

    protected void testInquiryDocumentHeader() throws InterruptedException {
        String docId = waitForElementPresent(By.name("documentNumber")).getAttribute("value");
    	waitAndClickByXpath("//button[@title='Direct Inquiry']");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Document Number:", docId},
//                                          {"Document Description:", "Test"}}; // is this data flapping
//                                          {"Document Description:", "Travel Authorization Description"}}; // is this data flapping?
        {"Document Description:", "es"}};
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickLightBoxClose();
    }

    @Test
    public void testInquiryDocumentHeaderBookmark() throws Exception {
    	testInquiryDocumentHeader();
        passed();
    }

    @Test
    public void testInquiryDocumentHeaderNav() throws Exception {
    	testInquiryDocumentHeader();
        passed();
    }
}
