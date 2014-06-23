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
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryCollectionOfInactivaTablesAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C3";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry with collection of Inactivatables");
    }

    protected void testInquiryCollectionOfInactivaTables() throws InterruptedException {
        assertTextNotPresent("No records found for the given parameters.");
    	waitAndClickByLinkText("Link to Inquiry with a collection of inactivatable elements");
        String[][] lightBoxLabeledText = {{"Id:", "10001"},
                {"Company Name:", "AAA Travel"},
                {"Active:", "false"}};
        assertLabeledTextNotPresent(lightBoxLabeledText);
        waitAndClickButtonByText(SHOW_INACTIVE);
        assertTextPresent(new String [] {"10001","AAA Travel","false"});
        String[][] dataTableBefore = {{"1", "10000", "Value Rentals","true"}};
        String[][] dataTableAfter = {{"1", "10000", "Value Rentals","true"},
        		{"2", "10001", "AAA Travel","false"}};
        assertDataTableContains(dataTableBefore);
        waitAndClickButtonByText(SHOW_INACTIVE); // now the second show inactive button as the first is hide inactive at this point
//        waitAndClickByXpath("//div[@data-parent='TravelCompanyCategory-InquiryView-CompanyTable']/button");
        assertDataTableContains(dataTableAfter);
    }

    @Test
    public void testInquiryCollectionOfInactivaTablesBookmark() throws Exception {
    	testInquiryCollectionOfInactivaTables();
        passed();
    }

    @Test
    public void testInquiryCollectionOfInactivaTablesNav() throws Exception {
    	testInquiryCollectionOfInactivaTables();
        passed();
    }
}
