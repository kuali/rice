/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMaintenancePromptBeforeRoutingAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/account?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&viewId=LabsMaintenance-PromptBeforeRoutingView
     */
    public static final String BOOKMARK_URL = "/kr-krad/account?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&viewId=LabsMaintenance-PromptBeforeRoutingView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Prompt Before Routing");
    	waitAndClickByLinkText("Travel Account Maintenance prompting before routing on submit");
    }

    protected void testMaintenancePromptBeforeRouting() throws InterruptedException {
        // fill out the document
    	waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
    	waitAndTypeByName("document.newMaintainableObject.dataObject.number", uniqueString);
    	waitAndTypeByName("document.newMaintainableObject.dataObject.name", "Travel Account 14" + uniqueString);
    	waitAndClickByXpath("//input[@name='document.newMaintainableObject.dataObject.accountTypeCode' and @value='CAT']");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.fiscalOfficer.principalName", "fred");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.createDate", "04/09/2014");

        // submit and click OK on the first dialog (default to all documents)
        waitAndClickSubmitByText();
        waitAndClickConfirmSubmitOk();

        // click Yes on the next dialog (specific to this document)
        waitAndClickByXpath("//div[@data-parent='TravelAccount-RouteConfirmationDialog']/button[contains(text(),'Yes')]");

        // verify successful submit
        waitForProgressLoading(WebDriverUtils.configuredImplicityWait());
        Thread.sleep(500);
        checkForDocError();
        checkForDocErrorKrad();
        waitForTextPresent("Document was successfully submitted.");
    }

    @Test
    public void testMaintenancePromptBeforeRoutingBookmark() throws Exception {
    	testMaintenancePromptBeforeRouting();
        passed();
    }

    @Test
    public void testMaintenancePromptBeforeRoutingNav() throws Exception {
    	testMaintenancePromptBeforeRouting();
        passed();
    }
}
