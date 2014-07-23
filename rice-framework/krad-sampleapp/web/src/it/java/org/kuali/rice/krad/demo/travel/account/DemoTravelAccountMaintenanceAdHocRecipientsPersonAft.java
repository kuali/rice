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

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceAdHocRecipientsPersonAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Account Maintenance (New)");
    }

    protected void testTravelAccountMaintenanceAdHocRecipients() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription", "Travel Account Maintenance AdHoc Recipients Document");
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", "adhoc");
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", "adhoc");
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.dataObject.accountTypeCode' and @value='CAT']");

        waitAndClickByLinkText("Ad Hoc Recipients");

        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", "kr");
        jGrowl("Click Add button");
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath("//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), 'KR']"); // kr added and now displayed on the page
        waitAndClickSaveByText();

        waitForTextPresent("Document was successfully saved.");

        clearTextByName("document.adHocRoutePersons[0].id");
        waitAndTypeByName("document.adHocRoutePersons[0].id", "1");
        waitAndClickSaveByText();

        waitForTextPresent("Invalid Ad Hoc Routing Person Network Id");
    }

    @Test
    public void testDemoTravelAccountMaintenanceAdHocRecipientsBookmark() throws Exception {
        testTravelAccountMaintenanceAdHocRecipients();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceAdHocRecipientsNav() throws Exception {
        testTravelAccountMaintenanceAdHocRecipients();
        passed();
    }

}
