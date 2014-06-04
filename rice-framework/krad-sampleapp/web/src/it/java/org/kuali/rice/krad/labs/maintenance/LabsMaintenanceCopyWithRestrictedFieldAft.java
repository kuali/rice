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

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMaintenanceCopyWithRestrictedFieldAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Copy with 'restricted' fields in authorizer.");
    }

    protected void testMaintenanceCopyWithRestrictedField() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Copy with restricted field");
    	assertElementPresentByXpath("//div[@id='TravelAccount-Basic_disclosureContent']/table/tbody/tr/th/label[contains(text(),'Fiscal Officer:')]");
    	assertElementPresentByXpath("//div[@id='TravelAccount-Basic_disclosureContent']/table/tbody/tr/td/div[contains(text(),'fran')]");
        String newFiscalOfficerText = findElement(By.xpath("//div[@id='TravelAccount-Basic_disclosureContent']/table/tbody/tr[5]/td[2]/div")).getText();
        assertFalse("Maintenance Copy Restricted Field not restricted, fran not found in fiscal office text", newFiscalOfficerText.contains("fran"));
    }

    @Test
    public void testMaintenanceCopyWithRestrictedFieldBookmark() throws Exception {
    	testMaintenanceCopyWithRestrictedField();
        passed();
    }

    @Test
    public void testMaintenanceCopyWithRestrictedFieldNav() throws Exception {
    	testMaintenanceCopyWithRestrictedField();
        passed();
    }
}
