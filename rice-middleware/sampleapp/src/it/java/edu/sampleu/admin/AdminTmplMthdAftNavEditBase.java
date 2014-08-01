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
package edu.sampleu.admin;

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AdminTmplMthdAftNavEditBase extends AdminTmplMthdAftNavBlanketAppBase {

    public void testLookUpEditSave() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("name", "name*"); // use a document created by one of our AFTs
        waitAndClickSearchSecond();
        waitAndClickByLinkText("edit");

        String desc = getDescriptionUnique();
        jiraAwareTypeByName("document.documentHeader.documentDescription", desc);
        String docId = verifyDocInitiated();
        waitAndClickSave();
        waitForElementVisibleBy(By.xpath(SAVE_SUCCESSFUL_XPATH));
        assertDocSearch(docId, "SAVED");
        waitAndClickRouteLogIcon();
        assertRouteStatus("SAVED");
        waitForElementPresentByXpath("//th[contains(.,'Title')]/../following-sibling::*/td[contains(.,'" + desc + "')]");
    }

    @Test
    public void testLookUpEditSaveBookmark() throws Exception {
        testLookUpEditSave();
        passed();
    }

    @Test
    public void testLookUpEditSaveNav() throws Exception {
        testLookUpEditSave();
        passed();
    }
}