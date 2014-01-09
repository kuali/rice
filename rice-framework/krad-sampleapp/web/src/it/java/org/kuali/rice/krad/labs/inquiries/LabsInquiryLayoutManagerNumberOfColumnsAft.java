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
public class LabsInquiryLayoutManagerNumberOfColumnsAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - LayoutManager number of columns.");
    }

    protected void testInquiryLayoutManagerNumberOfColumns() throws InterruptedException {
    	waitAndClickByLinkText("Link to specify the number of columns within an non-collection inquiry section.");

        // Lightbox
        String[][] lightBoxLabeledText = {{"Context Id:", "CONTEXT1"},
                                          {"Context Name:", "Context1"},
                                          {"Context Namespace:", "KR-RULE-TEST - Kuali Rules Test"},
                                          {"Context Type:", "CONTEXT [KR-RULE-TEST]"}};
        assertLabeledTextPresent(lightBoxLabeledText);

        String[][] dataTable = {
                                {"T1001", "SimpleAgendaCompoundProp","T1004"},
                                {"T1002", "One Big Rule","T1004"}};
        assertDataTableContains(dataTable);
        clickCollapseAll();
        assertLabeledTextNotPresent(lightBoxLabeledText);
        clickExpandAll();
        assertLabeledTextPresent(lightBoxLabeledText);
    }

    @Test
    public void testInquiryLayoutManagerNumberOfColumnsBookmark() throws Exception {
    	testInquiryLayoutManagerNumberOfColumns();
        passed();
    }

    @Test
    public void testInquiryLayoutManagerNumberOfColumnsNav() throws Exception {
    	testInquiryLayoutManagerNumberOfColumns();
        passed();
    }
}
