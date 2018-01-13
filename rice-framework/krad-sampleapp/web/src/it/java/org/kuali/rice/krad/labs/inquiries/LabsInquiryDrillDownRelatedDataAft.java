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
package org.kuali.rice.krad.labs.inquiries;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDrillDownRelatedDataAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Drill-down related data");
    }

    protected void testInquiryDrillDownRelatedData() throws InterruptedException {
    	waitAndClickByLinkText("KRAD Inquiry TravelPerDiemExpense = 10000");
        
    	// Lightbox
        waitAndClickByXpath("//a[@title='Display Travel Destination with Id=10000']");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Id:", "10000"},
                                          {"Destination:", "Colorado"},
                                          {"Country:", "United States"},
                                          {"State:", "Colorado"},
                                          {"Active:", "true"}};
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickLightBoxClose();
        selectTopFrame();
        String[][] LabeledText = {{"Id:", "10000"},
                {"Travel Authorization Document:", "10000"},
                {"Primary Destination:", "10000"},
//                {"Date of Use:", getDateToday()},
                {"Breakfast Value:", "10"}, 
                {"Lunch Value:", "10"},
                {"Dinner Value:", "15"},
                {"Amount estimated for incidentals:", "20"},
                {"Mileage rate:", "DO"},
                {"Number of estimated miles:", "30"}};
        assertLabeledTextPresent(LabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(LabeledText);
        clickExpandAll();
        assertLabeledTextPresent(LabeledText);
    }

    @Test
    public void testInquiryDrillDownRelatedDataBookmark() throws Exception {
    	testInquiryDrillDownRelatedData();
        passed();
    }

    @Test
    public void testInquiryDrillDownRelatedDataNav() throws Exception {
    	testInquiryDrillDownRelatedData();
        passed();
    }
}
