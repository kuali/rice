/**
 * Copyright 2005-2016 The Kuali Foundation
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
public class LabsInquiryPresentationControllerAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR3C3";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry with Presentation Controller");
    }

    protected void testInquiryPresentationController() throws InterruptedException {
    	waitAndClickByLinkText("Link to Inquiry with Authorizer configured");

        String[][] labeledText = {{"Id:", "10000"},
                                  {"Primary Destination:", "10000"},
                                  {"Date of Use:", "/"}}; // getDateToday()

        assertLabeledTextPresent(labeledText);
        clickCollapseAll();
        assertLabeledTextNotPresent(labeledText);
        clickExpandAll();
        assertLabeledTextPresent(labeledText);
    }

    @Test
    public void testInquiryPresentationControllerBookmark() throws Exception {
    	testInquiryPresentationController();
        passed();
    }

    @Test
    public void testInquiryPresentationControllerNav() throws Exception {
    	testInquiryPresentationController();
        passed();
    }
}
