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
public class LabsInquiryCustomLinkAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Custom Link");
    }

    protected void testInquiryCustomLink() throws InterruptedException {
    	waitAndClickByLinkText("Link to Inquiry with a Custom Link");
        assertElementPresentByXpath("//button[contains(text(), 'Export')]");

        // Lightbox
        waitAndClickLinkContainingText("IAT (Click for Example)");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Travel Account Number:", "a3"},
                                          {"Travel Account Name:", "Travel Account 3"},
                                          {"Code And Description:", "IAT - Income"},
                                          {"Subsidized Percent:", "20"},
//                                          {"Date Created:", ""}, // skip Date Created till a good way to handle empty strings is figured out
                                          {"Fiscal Officer:", "frank"},
                                          {"Fiscal Officer User ID:", "frank"},
                                          {"Fiscal Officer Name:", "frank, frank"}};
        assertLabeledTextPresent(lightBoxLabeledText);

        String[][] dataTable = {{"SUB1", "Sub Account 1"},
                                {"SUB2", "Sub Account 2"},
                                {"SUB3", "Sub Account 3"}};
        assertDataTableContains(dataTable);

        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);

        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);

        // Back opens previous page inside lightbox, is that correct behavior?
        waitAndClickButtonByText("< Back");
    }

    @Test
    public void testInquiryCustomLinkBookmark() throws Exception {
    	testInquiryCustomLink();
        passed();
    }

    @Test
    public void testInquiryCustomLinkNav() throws Exception {
    	testInquiryCustomLink();
        passed();
    }
}
