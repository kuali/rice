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
public class LabsInquiryForceUppercaseAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR1C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR1C3";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - forceUppercase");
    }

    protected void testInquiryForceUppercase() throws InterruptedException {
    	waitAndClickByLinkText("Inquiry with forceUppercase (subAccount=a&travelAccountNumber=a14)");
        
    	// Lightbox
        waitForTextNotPresent("No records found for the given parameters");
        waitAndClickLabeledLink("Travel Sub Account Number:", "A");
//        waitAndClickLinkContainingText("A");
        gotoLightBox();
        String[][] lightBoxLabeledText = {{"Travel Account Number:", "a14"},
                                          {"Travel Sub Account Number:", "A"},
                                          {"Sub Account Name:", "Sub Account A"}};
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
        waitAndClickLightBoxClose();
        selectTopFrame();
        assertLabeledTextPresent(lightBoxLabeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
    }

    @Test
    public void testInquiryForceUppercaseBookmark() throws Exception {
    	testInquiryForceUppercase();
        passed();
    }

    @Test
    public void testInquiryForceUppercaseNav() throws Exception {
    	testInquiryForceUppercase();
        passed();
    }
}
