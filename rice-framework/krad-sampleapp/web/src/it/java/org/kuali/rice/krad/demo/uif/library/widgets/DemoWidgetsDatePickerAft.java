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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsDatePickerAft extends DemoLibraryBase {

	 /**
     * /kr-krad/kradsampleapp?viewId=Demo-DatePickerView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DatePickerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "DatePicker");
    }

    protected void testWidgetsDatePickerDefault() throws Exception {
        selectByName("exampleShown","Default");

        jGrowl("Click Datepicker");
        waitAndClickByXpath("//div[@data-parent=\"Demo-DatePicker-Example1\"]/div/div/a[@title=\"...\"]");

        jGrowl("Select Today");
        waitAndClick(By.cssSelector(".ui-datepicker-current"));
    }

    protected void testWidgetsDatePickerWidget() throws Exception {
        selectByName("exampleShown","Widget Input Only");

        assertElementPresentByXpath("//input[@name='inputField1' and @readonly]");

        jGrowl("Click Datepicker");
        waitAndClickByXpath("//div[@data-parent=\"Demo-DatePicker-Example2\"]/div/div/a[@title=\"...\"]");

        waitAndClickByXpath("//button[@class='ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all']");
    }

    @Test
    public void testWidgetsDatePickerBookmark() throws Exception {
        testWidgetsDatePickerDefault();
        testWidgetsDatePickerWidget();
        driver.close();
        passed();
    }

    @Test
    public void testWidgetsDatePickerNav() throws Exception {
        testWidgetsDatePickerDefault();
        testWidgetsDatePickerWidget();
        driver.close();
        passed();
    }
}
