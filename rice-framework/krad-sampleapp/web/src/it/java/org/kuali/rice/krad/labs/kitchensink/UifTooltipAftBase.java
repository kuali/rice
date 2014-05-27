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
package org.kuali.rice.krad.labs.kitchensink;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.openqa.selenium.By;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifTooltipAftBase extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page10
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page10";
    
    private static final String NAME_FIELD_1 = "field1";
    private static final String NAME_FIELD_2 = "field2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws Exception {
        navigateToKitchenSink("Other Examples");
    }

    protected void testUifTooltipByName(String nameField1, String nameField2) throws Exception {
        findElement(By.name(nameField2)); // fields must be in view for tooltips to be displayed

        // check if tooltip opens on focus
        fireEvent(nameField1, "focus");
        fireMouseOverEventByName(nameField1);

        String tooltipContents = waitForToolTipPresent().getText();
        assertEquals("This tooltip is triggered by focus or and mouse over.", tooltipContents);
        fireEvent(nameField1, "blur");

        fireEvent(nameField2, "focus");
        Thread.sleep(5000);

        // check if tooltip opens on mouse over
        fireMouseOverEventByName(nameField2);
        assertFalse("unable to detect tooltip", isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check if tooltip closed on mouse out of nameField2
        fireEvent(nameField2, "blur");
        fireMouseOverEventByName(nameField1);
        waitAndTypeByName(nameField1, "");
        Thread.sleep(5000);
        assertFalse("able to detect tooltip", isVisibleByXpath(
                "//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check that default tooltip does not display when there are an error message on the field
        waitAndTypeByName(nameField1, "1");
        fireEvent(nameField1, "blur");
        fireMouseOverEventByName(nameField1);
        Thread.sleep(10000);
    }

    protected void testUifTooltipNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testUifTooltipByName(NAME_FIELD_1, NAME_FIELD_2);
        passed();
    }

    protected void testUifTooltipBookmark(JiraAwareFailable failable) throws Exception {
        testUifTooltipByName(NAME_FIELD_1, NAME_FIELD_2);
        passed();
    }
}
