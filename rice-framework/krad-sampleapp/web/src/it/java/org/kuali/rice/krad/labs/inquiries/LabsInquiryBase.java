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

import org.kuali.rice.testtools.selenium.WebDriverAftBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class LabsInquiryBase extends WebDriverAftBase {

	final String CLOSE="Close";
	
    public static final String[][] LABELED_IAT_TEXT = {{"Travel Account Type Code:", "IAT"},
                                         {"Name:", "Income"},
                                         {"Account Type:", "IAT - Income"}};

    protected void navigateToInquiry(String screenLinkText) throws InterruptedException {
        waitAndClickByLinkText("Inquiries");
        waitAndClickByLinkText(screenLinkText);
    }

    protected void assertLabeledIatText() throws InterruptedException {
        assertLabeledTextPresent(LABELED_IAT_TEXT);
        clickCollapseAll();
        assertLabeledTextNotPresent(LABELED_IAT_TEXT);
        clickExpandAll();
        assertLabeledTextPresent(LABELED_IAT_TEXT);
    }

    protected void clickCollapseAll() throws InterruptedException {
        waitAndClickButtonByText("Collapse All");
        Thread.sleep(1000);
    }

    protected void clickExpandAll() throws InterruptedException {
        waitAndClickButtonByText("Expand All");
        Thread.sleep(1000);
    }
}
