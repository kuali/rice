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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupDefaultCreateNewBlanketApproveAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true";

    protected String[][] inputVerifyDetails;

    @Before
    @Override
    public void testSetUp() {
        super.testSetUp();
        getDescriptionUnique(); // init uniqueString
        inputVerifyDetails = new String[][]{
                {"Travel Account Name:", uniqueString},
                {"Travel Account Number:", "z" + uniqueString.substring(4, 13)},
                {"Fiscal Officer User ID:", "fran"},
                {"Fiscal Officer:", "fran"},
                {"Fiscal Officer Name:", "fran"},
                {"Code And Description:", "CAT - Clearing"}
        };
    }

    @Override
    protected void blanketApproveSuccessfully() throws InterruptedException {
        waitAndClickBlanketApprove();
        waitAndClickConfirmBlanketApproveOk();
        acceptAlertIfPresent(); // LabsLookupDefaultCreateNewBlanketApproveAft
        waitForProgressLoading();
// Blanket submit has been updated to go to the hub page so error messages and doc state are no longer testable
//        checkForDocErrorKrad();
//        waitForTextPresent("Document was successfully approved.");
    }


    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    public String getUserName() {
        return "admin"; // must have blanket approve rights
    }

    @Override
    protected String getDescriptionUnique() {
        // "z" + to keep these at the bottom of the search results so as not to interfere with other AFTs.
        if (uniqueString == null) {
            uniqueString = "z" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        }
        return getDescriptionBase() + " " + uniqueString;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Default");
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveBookmark() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApprove();
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccountBookmark() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount();
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveNav() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApprove();
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccountNav() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount();
        passed();
    }

    protected void testLabsLookupDefaultCreateNewBlanketApprove()throws Exception {
        navigateToCreateNew();

        // TODO method that accepts full array
        // TODO flag requires or determine via UI (*) what is required (could miss changes)
        waitAndTypeLabeledInput("Description:", getDescriptionUnique());
        waitAndTypeLabeledInput(inputVerifyDetails[0][0], inputVerifyDetails[0][1]);
        waitAndTypeLabeledInput("Travel Account Number:", inputVerifyDetails[1][1]);
        waitAndTypeLabeledInput("Fiscal Officer User ID:", inputVerifyDetails[2][1]);
        waitAndClickByXpath("//input[@value='CAT']");

        // TODO convenience method
        waitAndClickByLinkText("Notes and Attachments (0)");
        waitAndTypeByXpath("//textarea[@maxlength='800']", "My Note " + uniqueString);
        waitAndClickByXpath("//button[@title='Add a Note']");
        waitForProgressLoading();

        // TODO convenience method
        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/input", "admin");
        waitAndClickAdHocPersonAdd();
        waitForTextPresent("admin, admin");

        blanketApproveSuccessfully();

        // search based on name and verify
        open(getBaseUrlString() + LabsLookupDefaultAft.BOOKMARK_URL);
        waitAndTypeLabeledInput("Travel Account Name:", inputVerifyDetails[0][1]);
        waitAndClickSearchByText();
        waitForProgressLoading();
        waitAndClickLinkContainingText(inputVerifyDetails[1][1]); // "Travel Account Number:"
        waitForProgressLoading();
        gotoLightBox();
        assertLabeledTextPresent(inputVerifyDetails);
    }

    protected void testLabsLookupDefaultCreateNewBlanketApproveWithSubAccount()throws Exception {
        navigateToCreateNew();

        waitAndTypeLabeledInput("Description:", getDescriptionUnique());
        waitAndTypeLabeledInput("Travel Account Name:", inputVerifyDetails[0][1]);
        waitAndTypeLabeledInput("Travel Account Number:", inputVerifyDetails[1][1]);
        waitAndTypeLabeledInput("Fiscal Officer User ID:", inputVerifyDetails[2][1]);
        waitAndClickByXpath("//input[@value='CAT']");

        waitAndTypeLabeledInput("Travel Sub Account Number:", uniqueString.substring(0,7) + "sa");
        waitAndTypeLabeledInput("Sub Account Name:", "Sub Account " + uniqueString);
        waitAndClickButtonByText("Add");
        waitForProgressAddingLine();

        waitAndClickByLinkText("Notes and Attachments (0)");
        waitAndTypeByXpath("//textarea[@maxlength='800']", "My Note " + uniqueString);
        waitAndClickByXpath("//button[@title='Add a Note']");
        waitForProgressLoading();

        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/input", "admin");
        waitAndClickAdHocPersonAdd();
        waitForTextPresent("admin, admin");

        blanketApproveSuccessfully();

        // search based on name and verify
        open(getBaseUrlString() + LabsLookupDefaultAft.BOOKMARK_URL);
        waitAndTypeLabeledInput("Travel Account Name:", inputVerifyDetails[0][1]);
        waitAndClickSearchByText();
        waitForProgressLoading();
        waitAndClickLinkContainingText(inputVerifyDetails[1][1]); // "Travel Account Number:"
        waitForProgressLoading();
        gotoLightBox();
        assertLabeledTextPresent(inputVerifyDetails);
        // Travel Sub Account Number should be uppercased via UI
        assertTextPresent(new String[]{(uniqueString.substring(0,7) + "sa").toUpperCase(), "Sub Account " + uniqueString});
    }
}
