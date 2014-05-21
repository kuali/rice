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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupDefaultCreateNewBlanketApproveAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    public String getUserName() {
        return "admin"; // must have blanket approve rights
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Default");
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveBookmark() throws Exception {
        String account = uniqueAccount();
        testLabsLookupDefaultCreateNewBlanketApprove(account);
        passed();
    }

    private String uniqueAccount() {
        return "z" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccountBookmark() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount(uniqueAccount());
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveNav() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApprove(uniqueAccount());
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccountNav() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount(uniqueAccount());
        passed();
    }

    protected void testLabsLookupDefaultCreateNewBlanketApprove(String account)throws Exception {
        navigateToCreateNew();
        waitAndTypeByName("document.documentHeader.documentDescription","Labs Default LookUp Created " + account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name",account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.foId","fran");
//        waitAndTypeByName("document.newMaintainableObject.dataObject.createDate", "01/01/2012"); // no longer input field
        waitAndClickByXpath("//input[@value='CAT']");

        waitAndClickByLinkText("Notes and Attachments (0)");
        waitAndTypeByXpath("//textarea[@maxlength='800']","My Note " + account);
        waitAndClickByXpath("//button[@title='Add a Note']");
        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/input","admin");
        waitAndClickAdHocPersonAdd();
        waitForTextPresent("admin, admin");
        waitAndClickByXpath("//button[@id='Uif-AdHocPersonCollection_add']");
        waitAndClickBlanketApprove();
//        waitForElementPresent("img[src*=\"info.png\"]"); // Loading to quick?

// Blanket submit has been updated to go to the hub page so error messages and doc state are no longer testable
//        if (isElementPresentByXpath("//li[@class='uif-errorMessageItem']")) {
//            failOnDocErrors();
//        }
//
//        assertTextPresent(new String[] {"Document was successfully approved.", "ENROUTE"});
    }

    protected void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount(String account)throws Exception {
        navigateToCreateNew();
        waitAndTypeByName("document.documentHeader.documentDescription","Labs Default LookUp Created " + account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", account);
        waitAndTypeByName("document.newMaintainableObject.dataObject.foId","fran");
//        waitAndTypeByName("document.newMaintainableObject.dataObject.createDate", "01/01/2012"); // no longer an input field
        waitAndClickByXpath("//input[@value='CAT']");

        waitAndTypeByXpath("//div[@data-label='Travel Sub Account Number']/input","1");
        waitAndTypeByXpath("//div[@data-label='Sub Account Name']/input","Sub Account");
        waitAndClickButtonByText("Add");

        waitAndClickByLinkText("Notes and Attachments (0)");
        waitAndTypeByXpath("//textarea[@maxlength='800']","My Note " + account);
        waitAndClickByXpath("//button[@title='Add a Note']");
        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/input","admin");
        waitAndClickAdHocPersonAdd();
        waitForTextPresent("admin, admin");
        waitAndClickByXpath("//button[@id='Uif-AdHocPersonCollection_add']");
        waitAndClickBlanketApprove();
//        waitForElementPresent("img[src*=\"info.png\"]"); // Loading to quick?
// Blanket approve now redirects to the hub so error messagea and doc status are no longer testable https://jira.kuali.org/browse/KULRICE-11463
//        if (isElementPresentByXpath("//li[@class='uif-errorMessageItem']")) {
//            failOnDocErrors();
//        }
//
//        assertTextPresent(new String[] {"Document was successfully approved.", "ENROUTE"});
    }
}
