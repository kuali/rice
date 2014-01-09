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
public class LabsInquiryDefaultInquirableAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR1C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR1C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - default inquirable");
    }

    protected void testInquiryDefaultInquirable() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Inquiry");

        // Lightbox
        waitAndClickLinkContainingText("a14");
        gotoLightBox();
        String[][] lightBoxTravelAccountLabeledText = {{"Travel Account Number:", "a14"},
                                          {"Travel Account Name:", "Travel Account 14"},
                                          {"Code And Description:", "CAT - Clearing"},
                                        //{"Subsidized Percent:", ""}, skip field till a good way to handle empty strings is figured out
                                          };
        String[][] lightBoxFiscalOfficerLabeledText = {{"Travel Account Number:", "a14"},
                {"Fiscal Officer User ID:", "fran"},
                {"Fiscal Officer Name:", "fran, fran"}};
        assertLabeledTextPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextPresent(lightBoxFiscalOfficerLabeledText);
        
        //Not working perfectly as it is working for other pages.
        /*String[][] dataTable = {{"A", "Sub Account A"},
                                {"B", "Sub Account B"},
                                {"C", "Sub Account C"},
                                {"D", "Sub Account D"},
                                {"E", "Sub Account E"},
                                {"F", "Sub Account F"},
                                {"G", "Sub Account G"},
                                {"H", "Sub Account H"},
                                {"I", "Sub Account Eye"},
                                {"J", "Sub Account J"}};
        assertDataTableContains(dataTable);*/

        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextNotPresent(lightBoxFiscalOfficerLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextPresent(lightBoxFiscalOfficerLabeledText);
        waitAndClickButtonByText(CLOSE);
        selectTopFrame();
        assertLabeledTextPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextPresent(lightBoxFiscalOfficerLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextNotPresent(lightBoxFiscalOfficerLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxTravelAccountLabeledText);
        assertLabeledTextPresent(lightBoxFiscalOfficerLabeledText);
    }

    @Test
    public void testInquiryDefaultInquirableBookmark() throws Exception {
    	testInquiryDefaultInquirable();
        passed();
    }

    @Test
    public void testInquiryDefaultInquirableNav() throws Exception {
    	testInquiryDefaultInquirable();
        passed();
    }
}
