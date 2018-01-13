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
package org.kuali.rice.krad.labs.transactional;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class tests lookup and copy actions for transactional document
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupTravelAuthorizationDocumentCopyActionAft extends LabsTransactionalBase {
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=TravelAuthorization-LookupView";
    public static final String COPIED_FROM_DOCUMENT_ID = "3034";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToTransactional("Transactional Sample - Lookup With Copy Actions");
    }

    @Test
    public void testTransactionalLookupAndCopyBookmark() throws Exception {
        testTransactionalLookupAndCopy();
        passed();
    }

    @Test
    public void testTransactionalLookupAndCopyNav() throws Exception {
        testTransactionalLookupAndCopy();
        passed();
    }

    public void testTransactionalLookupAndCopy() throws Exception {
        waitAndClickButtonByExactText("Search");
        waitForElementPresent(By.id("uLookupResults_layout"));
        jGrowl("Click on 2nd View Link");
        waitAndClick(By.xpath("(//a[contains(text(),'View')])[2]"));
        waitForPageToLoad();
        waitAndClickButtonByExactText("Copy");
        waitForPageToLoad();
        waitAndClickByLinkText("Notes and Attachments (1)");
    }

    public String getCopiedFromDocumentId() {
        return COPIED_FROM_DOCUMENT_ID;
    }
}
