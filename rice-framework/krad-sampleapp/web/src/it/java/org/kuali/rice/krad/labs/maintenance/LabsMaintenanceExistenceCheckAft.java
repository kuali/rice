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
package org.kuali.rice.krad.labs.maintenance;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMaintenanceExistenceCheckAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR1C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR6C1";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Existence Check");
    }

    protected void testMaintenanceExistenceCheck() throws InterruptedException {
        waitAndClickByLinkText("Travel Account Maintenance Existence Check - The account type code and fiscal officer must exist.");

        waitAndTypeByName("document.documentHeader.documentDescription", "New Travel Account");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName("document.newMaintainableObject.dataObject.number",randomCode);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name","Test Account Name");

        // Enter a nonexistent account type code
        waitAndTypeByName("document.newMaintainableObject.dataObject.accountTypeCode","xxx");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        assertTextPresent("INITIATED");
        waitForTextPresent("Travel Account Type Code: The specified Travel Account Type Code does not exist.");

        // Correct the account type code and enter a nonexistent fiscal officer
        Thread.sleep(1000);
        clearTextByName("document.newMaintainableObject.dataObject.accountTypeCode");
        waitAndTypeByName("document.newMaintainableObject.dataObject.accountTypeCode", "CAT");
        clearTextByName("document.newMaintainableObject.dataObject.foId");
        waitAndTypeByName("document.newMaintainableObject.dataObject.foId", "xxxx");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        assertTextPresent("INITIATED");
        waitForTextPresent("Fiscal Officer User ID: The specified Fiscal Officer does not exist.");

        // Correct the fiscal officer and submit the document
        Thread.sleep(1000);
        clearTextByName("document.newMaintainableObject.dataObject.foId");
        waitAndTypeByName("document.newMaintainableObject.dataObject.foId", "fred");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        assertTextPresent("ENROUTE");
    }

    //
    // Ignore these tests until KULRICE-12849 and KULRICE-12850 are fixed.  They will fail until that time.
    //
    @Ignore
    @Test
    public void testMaintenanceExistenceCheckBookmark() throws Exception {
        testMaintenanceExistenceCheck();
        passed();
    }

    @Ignore
    @Test
    public void testMaintenanceExistenceCheckNav() throws Exception {
        testMaintenanceExistenceCheck();
        passed();
    }
}
