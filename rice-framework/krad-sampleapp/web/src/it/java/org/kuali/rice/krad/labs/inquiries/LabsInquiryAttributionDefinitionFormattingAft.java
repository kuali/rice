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
public class LabsInquiryAttributionDefinitionFormattingAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR2C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR2C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - AttributionDefinition Formatting");
    }

    protected void testInquiryAttributionDefinitionFormatting() throws InterruptedException {
    	
    	// Inquiry - AttributionDefinition Formatting
    	waitAndClickByLinkText("Inquiry - AttributionDefinition Formatting");
        
    	// Lightbox
        waitAndClickLinkContainingText("10000");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Id:", "10000"},
                                          {"Travel Authorization Document:", "10000"},
                                          {"Travel Company Name:", "Discount Travel"},
                                          {"Expense Type:", "ME"},
                                          {"Expense Description:", "Family Related"}, 
                                          {"Expense Date:", "/"},
                                          {"Expense Amount:", "1,278.97"},
                                          {"Reimbursable:", "true"},
                                          {"Taxable:", "true"}};
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickButtonByText(CLOSE);
        selectTopFrame();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickButtonByText("< Back");
        
        //Inquiry - AttributionDefinition Formatting (Partial Attribute Masking, Additional Display Attribute Name)
        waitAndClickByLinkText("Inquiry - AttributionDefinition Formatting (Partial Attribute Masking, Additional Display Attribute Name)");
        String[][] LabeledText = {{"Id:", "1"},
                {"Document Number:", "??"},
                {"Principal Id:", "fred"},
                {"Traveler Name:", ""},
                {"Traveler User ID:", ""},
                {"First and Last Name (additionalDisplayAttributeName example):", "Test *-* Traveler"},
                {"Middle Name:", "A"},
                {"Last Name:", "Traveler"},
                {"Street Address Line1:", "123 Nowhere St."},
                {"Street Address Line2:", ""},
                {"City Name:", "Davis"},
                {"State", "CA"},
                {"Zip:", "95616"},
                {"Country:", "US"},
                {"Email Address:", ""},
                {"Gender:", "M"},
                {"Phone Number:", "(xxx)xxx-xxxx"},
                {"Traveler Type Code:", "123"},
                {"Customer Number:", "CUST"},
                {"Drivers License:", "*****45678"},
                {"Drivers License Exp Date:", "/"},
                {"Active:", "true"},
                {"Non Resident Alien:", "false"},
                {"Liability Insurance:", "false"},};
        assertLabeledTextPresent(LabeledText);
    }

    @Test
    public void testInquiryAttributionDefinitionFormattingBookmark() throws Exception {
    	testInquiryAttributionDefinitionFormatting();
        passed();
    }

    @Test
    public void testInquiryAttributionDefinitionFormattingNav() throws Exception {
    	testInquiryAttributionDefinitionFormatting();
        passed();
    }
}
