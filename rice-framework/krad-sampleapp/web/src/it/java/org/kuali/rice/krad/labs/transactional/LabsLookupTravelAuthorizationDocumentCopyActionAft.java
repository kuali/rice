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
        navigateToTransactional("Transactional Sample - Lookup with Copy Actions");

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

        // Lookup Page
        waitAndClickButtonByExactText("Search");
        waitForElementPresent(By.id("uLookupResults_layout"));
        WebElement resultsTable = driver.findElement(By.id("uLookupResults_layout"));
        selectActionLinkByTableRowData(resultsTable, "View", "Document Number", getCopiedFromDocumentId() );

        // Original Document Page
        waitForPageToLoad();
        waitAndClickButtonByExactText("Copy");

        // Copied Document Page
        waitForPageToLoad();
        waitAndClickByLinkText("Notes and Attachments (1)");

    }

    public String getCopiedFromDocumentId() {
        return COPIED_FROM_DOCUMENT_ID;
    }

    /** Helper function used with results table for finding a row with the right data
     * and selecting its link
     *
     * @param linkText
     * @param headerLabel
     * @param columnText
     */
    protected void selectActionLinkByTableRowData(WebElement resultsTable, String linkText, String headerLabel, String columnText) {
        int index = 1;
        int headerIndex = -1;
        int actionIndex = -1;
        String actionLabel = "Actions";

        List<WebElement> tableRows = resultsTable.findElements(By.tagName("tr"));
        List<WebElement> headerColumns = tableRows.get(0).findElements(By.tagName("th"));

        // identify the correct header and actions columns for review
        if(tableRows != null && tableRows.size() > 0) {
            for (int columnIndex = 0; columnIndex < headerColumns.size(); columnIndex++) {
                if (headerColumns.get(columnIndex).getText().contains(headerLabel)) {
                    headerIndex = columnIndex;
                }
                // Actions are generally in the first column but this is not guaranteed
                if (headerColumns.get(columnIndex).getText().contains(actionLabel)) {
                    actionIndex = columnIndex;
                }
            }

            if(headerIndex > 0 && actionIndex >= 0 ) {
                boolean hasClicked = false;
                for (int rowIndex = 1; rowIndex < tableRows.size() && !hasClicked; rowIndex++) {
                    List<WebElement> dataColumns = tableRows.get(rowIndex).findElements(By.tagName("td"));
                    if (dataColumns.get(headerIndex).getText().contains(columnText)) {
                        dataColumns.get(actionIndex).findElement(By.linkText(linkText)).click();
                        hasClicked = true;
                    }
                }
            }
            else {
                // Error: header and/or actions column not found
            }

        }
    }

}
