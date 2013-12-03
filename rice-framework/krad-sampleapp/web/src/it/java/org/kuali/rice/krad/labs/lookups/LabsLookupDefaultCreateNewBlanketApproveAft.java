/*
 * Copyright 2006-2012 The Kuali Foundation
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
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Default");
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveBookmark() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApprove();
        passed();
    }

    @Test
    public void testLabsLookupDefaultCreateNewBlanketApproveNav() throws Exception {
        testLabsLookupDefaultCreateNewBlanketApprove();
        passed();
    }

    protected void testLabsLookupDefaultCreateNewBlanketApprove()throws Exception {
        navigateToCreateNew();
        waitAndTypeByName("document.documentHeader.documentDescription","Labs Default LookUp Created");
        waitAndTypeByName("document.newMaintainableObject.dataObject.fiscalOfficer.principalName","A");
        waitAndTypeByXpath("//div[@data-label='Travel Sub Account Number']/input","1");
        waitAndTypeByXpath("//div[@data-label='Travel Sub Account Number']/input[2]","Sub Account");
        //By adding this it is generating Freemarker Error.
        //        waitAndClickButtonByText("add");
        waitAndClickByLinkText("Notes and Attachments (0)");
        waitAndTypeByXpath("//textarea[@maxlength='800']","My Note");
        waitAndClickByXpath("//button[@title='Add a Note']");
        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/input","admin, admin");
        //Document not being able to create if we add Adhoc (Showing missing Person Network ID which is not preesnt on page)
        //        waitAndClickByXpath("//button[@id='Uif-AdHocPersonCollection_add']");
        waitAndClickButtonByText("blanket approve");
        waitForElementPresentByXpath("//img[@src='/krad/images/validation/info.png']");
        assertTextPresent("Document was successfully approved.");
        assertTextPresent("ENROUTE");
    }
}
