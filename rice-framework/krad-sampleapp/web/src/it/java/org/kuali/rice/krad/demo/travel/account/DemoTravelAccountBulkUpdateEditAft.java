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
package org.kuali.rice.krad.demo.travel.account;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountBulkUpdateEditAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?viewTypeName=MAINTENANCE&methodToCall=start&docTypeName=TravelAccountBulkUpdateMaintenanceDocument
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?viewTypeName=MAINTENANCE&methodToCall=start&docTypeName=TravelAccountBulkUpdateMaintenanceDocument";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    public void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Account Bulk Update (Edit)");
    }

    protected void testTravelAccountBulkUpdateEdit() throws Exception {
       waitAndTypeByName("document.documentHeader.documentDescription","Dummy Description");
       waitAndClickByXpath("//input[@name='document.newMaintainableObject.dataObject.updateFieldValues[accountTypeCode]' and @value='IAT']");
       waitAndClickByXpath("//div[@data-label='Travel Account Number']/div/div/button");
       gotoLightBox();
       waitAndClickButtonByExactText(SEARCH);
       String assertContent [] = {"a11","EAT - Expense","a12","EAT - Expense","a13","CAT - Clearing"};
       assertTextPresent(assertContent);
       waitAndClickByXpath("//button[contains(text(),'Close')]");
       selectTopFrame();
       waitAndTypeByXpath("//div[@data-label='Travel Account Number']/div/input","a11");
       waitAndClickByXpath("//button[contains(text(),'Add')]");
       waitAndTypeByXpath("//div[@data-label='Travel Account Number']/div/input","a13");
       waitAndClickByXpath("//button[contains(text(),'Add')]");
       waitAndClickByXpath("//button[contains(text(),'Submit')]");
       waitAndClickConfirmationOk();
    }

    @Test
    public void testTravelAccountBulkUpdateEditNav() throws Exception {
        testTravelAccountBulkUpdateEdit();
        passed();
    }

    @Test
    public void testTravelAccountBulkUpdateEditBookmark() throws Exception {
        testTravelAccountBulkUpdateEdit();
        passed();
    }
}
