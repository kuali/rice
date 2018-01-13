/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsKSAdminRegistrationNoCssAft extends WebDriverLegacyITBase {

    /**
     * /ksworkshop?viewId=KS-AdminRegistration
     */
    public static final String BOOKMARK_URL = "/kr-krad/ksworkshop?viewId=KS-AdminRegistration-NoCss";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("KS Admin Registration - No Css");
    }

    protected void testKSAdminRegistration() throws InterruptedException {
        //Enter Student ID, confirm appropriate data appears on screen.
    	waitAndTypeByName("studentId","1");
        waitAndClickById("KS-AdminRegistration-StudentInfoGo");
        waitForTextPresent("Allison Glass (Allison Glass)");

        //Delete a line item and confirms it no longer displays.
        String textToAssert =getTextByXpath("//section[@id='KS-AdminRegistration-Registered']/table/tbody/tr/td/div/div");
        waitAndClickByXpath("//button[@title='Drop Course']");
        waitAndClickByXpath("//div[@class='modal-footer' and @data-parent='KS-AdminRegistration-DropRegisteredDialog']/button");
        waitForTextNotPresent(textToAssert);
        waitForTextPresent("You have deleted an item from Registered Courses");

        //Edit a line item and change values for Cr and Reg Options; cancel; confirm page reloads appropriately and changes are not retained.
        textToAssert =getTextByXpath("//section[@id='KS-AdminRegistration-Registered']/table/tbody/tr/td[3]/div/span");
        waitAndClickByXpath("//button[@title='Edit Course']");
        waitForElementPresentByName("registeredCourses[0].credits");
        selectByName("registeredCourses[0].credits","1.0");
        selectByName("registeredCourses[0].regOptions","Audit");
        waitAndClickByXpath("//button[@title='Cancel']");
        waitForTextPresent(textToAssert);

        // Edit a line item and change values for Cr and Reg Options; save; confirm page reloads appropriately and changes are retained.
        textToAssert =getTextByXpath("//section[@id='KS-AdminRegistration-Registered']/table/tbody/tr/td[3]/div/span");
        waitAndClickByXpath("//button[@title='Edit Course']");
        waitForElementPresentByName("registeredCourses[0].credits");
        selectByName("registeredCourses[0].credits","1.0");
        selectByName("registeredCourses[0].regOptions","Audit");
        waitAndClickByXpath("//button[@title='Save']");
        waitForTextPresent("Audit");
        waitForTextPresent("1.0");

        // Click Add Another; change or add values; click Deny under Registration Issues; confirm new line item does not appear.
        waitAndClickById("KS-AdminRegistration-RegFor_add");
        waitAndTypeByName("pendingCourses[1].code","1");
        waitAndTypeByName("pendingCourses[1].section","1");
        waitAndClickByXpath("//button[contains(text(),'Register')]");
        waitForElementPresentByXpath("//select[@name='pendingCourses[0].credits']");
        selectByName("pendingCourses[0].credits","2.0");
        waitAndClickByXpath("//div[@data-parent='KS-AdminRegistration-RegisterDialogResponse']/button[contains(text(),'Cancel')]");

        // Click Add Another; change or add values; click Allow under Registration Issues; confirm new line item appears and displays corrects values.
        waitAndClickById("KS-AdminRegistration-RegFor_add");
        waitAndTypeByName("pendingCourses[1].code","1");
        waitAndTypeByName("pendingCourses[1].section","1");
        waitAndClickByXpath("//button[contains(text(),'Register')]");
        waitForElementPresentByXpath("//select[@name='pendingCourses[0].credits']");
        selectByName("pendingCourses[0].credits","2.0");
        waitAndClickByXpath("//button[contains(text(),'Confirm Registration')]");
        waitForTextPresent("2.0");
    }

    @Test
    public void testKSAdminRegistrationBookmark() throws Exception {
        testKSAdminRegistration();
        passed();
    }

    @Test
    public void testKSAdminRegistrationNav() throws Exception {
        testKSAdminRegistration();
        passed();
    }
}
