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
package org.kuali.rice.krad.demo.travel.account;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

import java.io.File;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceViewPermissionAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

    /**
     * Provider of the temporary folder.
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Account Maintenance (New)");
    }

    private void createAndRouteDoc(String user) throws Exception {
        String docid = waitForElementPresentByXpath(DOC_ID_KRAD_XPATH).getText();

        String random = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        String accountNumber = random.substring(random.length() - 4, random.length());
        waitAndTypeByName("document.documentHeader.documentDescription", "Travel Account Maintenance AdHoc Recipients Document "
                + random);
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", accountNumber);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", random);
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.dataObject.accountTypeCode' and @value='CAT']");

        waitAndClickByXpath("//a/span[contains(text(),'Notes and Attachments')]");
        waitAndTypeByName("newCollectionLines['document.notes'].noteText", "Bonzo!");

        File file = temporaryFolder.newFile("attachment.oth");
        FileUtils.writeStringToFile(file, "Testing123");
        String path = file.getAbsolutePath().toString();
        waitAndTypeByName("attachmentFile", path);
        waitAndSelectByName("newCollectionLines['document.notes'].attachment.attachmentTypeCode", "OTH");

        waitAndClickByXpath("//button[@title='Add a Note']");
        waitForTextPresent("Bonzo!");
        assertTextPresent("attachment.oth");

        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", user);
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath("//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), '" + user + "']");

        submitSuccessfully();

        open(getBaseUrlString() + "/portal.jsp");
        impersonateUser(user);

        open(getBaseUrlString() + "/kr-krad/maintenance?methodToCall=docHandler&docId=" + docid
                + "&command=displayActionListView");
    }

    protected void testTravelAccountMaintenanceViewPermissionT1() throws Exception {
        createAndRouteDoc("erin");
        waitForElementNotPresent(By.xpath("//a/span[contains(text(),'Notes and Attachments')]"), 2);
        waitForElementNotPresent(By.xpath("//button[contains(text(),'Delete')]"), 0);
        assertTextNotPresent("Bonzo!");
        assertTextNotPresent("attachment.oth");
        waitAndClickButtonByText("Approve");
    }

    protected void testTravelAccountMaintenanceViewPermissionT2() throws Exception {
        createAndRouteDoc("dev1");
        waitAndClickByXpath("//a/span[contains(text(),'Notes and Attachments')]");
        assertTextPresent("Bonzo!");
        assertTextPresent("attachment.oth");
        assertButtonEnabledByText("Download Attachment");
        waitAndClickButtonByText("Approve");
    }

    protected void testTravelAccountMaintenanceViewPermissionT3() throws Exception {
        createAndRouteDoc("admin");

        assertTextPresent("Bonzo!");
        assertTextPresent("attachment.oth");
        waitForElementNotPresent(By.xpath("//button[contains(text(),'Download Attachment')]"), 0);
    }
    
    @Test
    public void testDemoTravelAccountViewPermissionT2Bookmark() throws Exception {
        testTravelAccountMaintenanceViewPermissionT2();
        passed();
    }

    @Test
    public void testDemoTravelAccountViewPermissionT2Nav() throws Exception {
        testTravelAccountMaintenanceViewPermissionT2();
        passed();
    }

    @Test
    public void testDemoTravelAccountViewPermissionT1Bookmark() throws Exception {
        testTravelAccountMaintenanceViewPermissionT1();
        passed();
    }

    @Test
    public void testDemoTravelAccountViewPermissionT1Nav() throws Exception {
        testTravelAccountMaintenanceViewPermissionT1();
        passed();
    }

    @Test
    public void testDemoTravelAccountViewPermissionT3Bookmark() throws Exception {
        testTravelAccountMaintenanceViewPermissionT3();
        passed();
    }

    @Test
    public void testDemoTravelAccountViewPermissionT3Nav() throws Exception {
        testTravelAccountMaintenanceViewPermissionT3();
        passed();
    }
    
}
