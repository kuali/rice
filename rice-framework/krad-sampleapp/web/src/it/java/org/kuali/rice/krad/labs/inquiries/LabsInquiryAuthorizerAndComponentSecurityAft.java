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
public class LabsInquiryAuthorizerAndComponentSecurityAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiries with Authorizer and Component Security");
    }

    protected void testInquiryAuthorizerAndComponentSecurity() throws InterruptedException {
    	
    	//Link to Inquiry with Authorizer configured
    	waitAndClickByLinkText("Link to Inquiry with Authorizer configured");
        waitAndClickByLinkText("10000");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Id:", "10000"},
                {"Travel Authorization Document:", "10000"},
                {"Primary Destination:", "10000"},
//                {"Date of Use:", getDateToday()},
                {"Breakfast Value:", "10"}, 
                {"Lunch Value:", "10"},
                {"Dinner Value:", "15"},
                {"Amount estimated for incidentals:", "20"},
                {"Mileage rate:", "DO"},
                {"Number of estimated miles:", "30"}};
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickLightBoxClose();
        selectTopFrame();
        String[][] LabeledText = {{"Id:", "10000"},
                {"Primary Destination:", "10000"},
//                {"Date of Use:", getDateToday()}
                };
        assertLabeledTextPresent(LabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(LabeledText);
        clickExpandAll();
        assertLabeledTextPresent(LabeledText);
        waitAndClickButtonByText("< Back");
        
        // Link to Inquiry with Component Security and Attribute Security configured on the view.
        waitAndClickByLinkText("Link to Inquiry with Component Security and Attribute Security configured on the view.");
        waitAndClickByLinkText("10000");
        gotoLightBox();
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickLightBoxClose();
        selectTopFrame();
        assertLabeledTextPresent(LabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(LabeledText);
        clickExpandAll();
        assertLabeledTextPresent(LabeledText);
    }

    @Test
    public void testInquiryAuthorizerAndComponentSecurityBookmark() throws Exception {
    	testInquiryAuthorizerAndComponentSecurity();
        passed();
    }

    @Test
    public void testInquiryAuthorizerAndComponentSecurityNav() throws Exception {
    	testInquiryAuthorizerAndComponentSecurity();
        passed();
    }
}
