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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesActionPlacementAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionActionPlacementView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionActionPlacementView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Action Placement");
    }

    /**
     * Tests whether the buttons are showing up in the left column and that the sequence is showing up in the next
     * column and is sequential.
     *
     * @throws Exception for any test errors
     */
    protected void testCollectionFeaturesActionPlacementLeft() throws Exception {
        waitAndSelectByName("exampleShown", "Action in Left Column");

        String parent = "section[data-parent = 'Demo-CollectionActionPlacement-Example1']";

        // buttons should show up in the left column
        waitForElementPresent(parent + " table tbody tr td:first-child button");

        // sequence should show up in the second column and be sequential
        assertSequenceColumn(parent, 2);
    }

    /**
     * Tests whether the sequence is showing up in the first column and is sequential and that the buttons are showing
     * up in the left column.
     *
     * @throws Exception for any test errors
     */
    protected void testCollectionFeaturesActionPlacementSecondColumn() throws Exception {
        waitAndSelectByName("exampleShown", "Action in Second Column");

        String parent = "section[data-parent = 'Demo-CollectionActionPlacement-Example2']";

        // sequence should show up in the first column and be sequential
        assertSequenceColumn(parent, 1);

        // buttons should show up in the second column
        waitForElementPresent(parent + "table tbody tr td:nth-child(2) button");
    }

    /**
     * Asserts that the sequence column is in order.
     *
     * @param parent the parent css selector
     * @param column the column number
     *
     * @throws Exception for any test errors
     */
    protected void assertSequenceColumn(String parent, int column) throws Exception {
        int rowCount = getCssCount(By.cssSelector(parent + " table tbody tr"));

        for (int i = 1; i <= rowCount; i++) {
            String text = getText(parent + " table tbody tr:nth-child(" + i + ") td:nth-child(" + column + ")");

            if (i == 1) {
                assertEquals("add", text);
            } else {
                assertEquals(Integer.toString(i - 1), text);
            }
        }
    }

    /**
     * Tests all features.
     *
     * @throws Exception for any test errors
     */
    protected void testCollectionFeaturesActionPlacement() throws Exception {
        testCollectionFeaturesActionPlacementLeft();
        testCollectionFeaturesActionPlacementSecondColumn();
    }
    
    @Test
    public void testCollectionFeaturesActionPlacementBookmark() throws Exception {
        testCollectionFeaturesActionPlacement();
        passed();
    }

    @Test
    public void testCollectionFeaturesActionPlacementNav() throws Exception {
        testCollectionFeaturesActionPlacement();
        passed();
    }

}