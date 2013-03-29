/*
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.krad.travelview;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceAddDeleteNoteNavIT extends KradTmplMthdSTNavBase {

    @Override
    protected String getLinkLocator() {
        return "Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify we can add and delete a note
     */
    public void testVerifyAddDeleteNoteLegacy() throws Exception {
        gotoMenuLinkLocator();
        super.testVerifyAddDeleteNoteLegacy();
//        waitAndClick("div.tableborders.wrap.uif-boxLayoutVerticalItem.clearfix  span.uif-headerText-span > img.uif-disclosure-image");
//        
//        waitForElementPresent("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
//        
//        waitAndClickByName("newCollectionLines['document.notes'].noteText");
//        waitAndTypeByName("newCollectionLines['document.notes'].noteText", "Test note");
//        waitAndClick("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
//        waitForElementPresentByName("document.notes[0].noteText");
//
//        assertEquals("Test note", getTextByName("document.notes[0].noteText"));
//        waitAndClick("button[title='Delete a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
//
//        waitForElementPresentByName("document.notes[0].noteText");
//        passed();
    }
}
