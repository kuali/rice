/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.krad.configview;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the help widget
 *
 * <p>
 * Selenium RC does not allow us to test the external help popup windows due to an error on JavaScrips window.close
 * method when selenium is running.  To test the external help we use the {@link HelpIT2} test
 * which utilizes WebDriver.  Unfortunately due to a WebDriver bug/feature we can't test the tooltip help there.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpIT extends UpgradedSeleniumITBase {

    // Delay in milliseconds used to allow the help window reload the new help page.
    // waitForPopUp will not work since the window already exists.
    private long HELP_WINDOW_LOAD_DELAY = 3000;

    @Override
    public String getTestUrl() {
        return "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView-Help&methodToCall=start";
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp() throws Exception {
        // test tooltip help
        mouseOver("css=h1 .uif-headerText-span");
        assertEquals("Sample text for view help", getText("css=td.jquerybubblepopup-innerHtml"));

        // test external help
        waitAndClick("css=input[title=\"Help for Configuration Test View - Help\"]");
        //selenium.waitForPopUp("Open Source Software | www.kuali.org", "30000");
        //selenium.selectPopUp("HelpWindow");
        Thread.sleep(5000);
        selectWindow("Kuali Foundation");
        Thread.sleep(5000);
        assertEquals("http://www.kuali.org/?view", getLocation());
        //selenium.deselectPopUp();
        selectWindow("Kuali :: Configuration Test View - Help");
    }

    /**
     * Test the tooltip and external help on the page
     */
    @Test
    public void testPageHelp() throws Exception {
        // test tooltip help
        mouseOver("css=h2 .uif-headerText-span");
        assertEquals("Sample text for page help", getText("css=td.jquerybubblepopup-innerHtml"));

        // test external help
        waitAndClick("css=input[title=\"Help for Help Page\"]");
        //selenium.waitForPopUp("HelpWindow", "30000");
        //selenium.selectPopUp("HelpWindow");
        Thread.sleep(5000);
        selectWindow("Kuali Foundation");
        Thread.sleep(5000);      
        assertEquals("http://www.kuali.org/?page", getLocation());
        //selenium.deselectPopUp();
        selectWindow("Kuali :: Configuration Test View - Help");
    }

    /**
     * Test the tooltip help on the section and fields
     */
    @Test
    public void testTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (isElementPresent("css=td:contains(\"Sample text for section help - tooltip help\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for field help - label left\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for field help - label left\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for field help - label right\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for field help - label right\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for field help - label top\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for field help - label top\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for standalone help widget tooltip which will never be rendered\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for standalone help widget tooltip which will never be rendered\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for on-focus event tooltip\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for check box help\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for check box help\")"));
        }

        // test tooltip help of section header
        mouseOver("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertTrue(isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));
        mouseOut("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertFalse(isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));

        // verify that no external help exist
        assertFalse(isElementPresent("css=#ConfigurationTestView-Help-Section1 input.uif-helpImage"));

        // test tooltip help of field with label to the left
        mouseOver("id=field-label-left_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for field help - label left\")"));
        mouseOut("id=field-label-left_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for field help - label left\")"));

        // test tooltip help of field with label to the right
        mouseOver("id=field-label-right_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for field help - label righ\")"));
        mouseOut("id=field-label-right_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for field help - label right\")"));

        // test tooltip help of field with label to the top
        mouseOver("id=field-label-top_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for field help - label top\")"));
        mouseOut("id=field-label-top_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for field help - label top\")"));

        // verify that standalone help with tooltip is not rendered
        assertFalse(isElementPresent("id=standalone-help-not-rendered"));

        // test tooltip help when it overrides a tooltip
        mouseOver("id=override-tooltip_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));
        if (isElementPresent("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")"));
        }
        mouseOut("id=override-tooltip_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));

        // test tooltip help in conjunction with a focus event tooltip
        mouseOver("id=on-focus-tooltip_control");
        assertTrue(isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));
        mouseOver("id=on-focus-tooltip_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        mouseOut("id=on-focus-tooltip_control");
        mouseOut("id=on-focus-tooltip_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        assertFalse(isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));

        // test tooltip help against a check box - help contains html
        mouseOver("id=checkbox_label");
        assertTrue(isVisible("css=td:contains(\"Sample text for check box help\")"));
        mouseOut("id=checkbox_label");
        assertFalse(isVisible("css=td:contains(\"Sample text for check box help\")"));
    }

    /**
     * Test the tooltip help on the sub-section and fields that are display only
     */
    @Test
    public void testDisplayOnlyTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (isElementPresent("css=td:contains(\"Sample text for sub-section help\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for sub-section help\")"));
        }
        if (isElementPresent("css=td:contains(\"Sample text for read only field help\")")) {
            assertFalse(isVisible("css=td:contains(\"Sample text for read only field help\")"));
        }

        // test tooltip help of sub-section header
        mouseOver("css=h4 .uif-headerText-span");
        assertTrue(isVisible("css=td:contains(\"Sample text for sub-section help\")"));
        mouseOut("css=h4 .uif-headerText-span");
        assertFalse(isVisible("css=td:contains(\"Sample text for sub-section help\")"));

        // test tooltip help of display only data field
        mouseOver("css=#display-field label");
        assertTrue(isVisible("css=td:contains(\"Sample text for read only field help\")"));
        mouseOut("css=#display-field label");
        assertFalse(isVisible("css=td:contains(\"Sample text for read only field help\")"));
    }

    /**
     * Test the tooltip help on the section and fields with no content
     */
    @Test
    public void testMissingTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        assertFalse(isElementPresent("css=.jquerybubblepopup"));

        // verify that no external help exist
        assertFalse(isElementPresent("css=#ConfigurationTestView-Help-Section2 input.uif-helpImage"));

        // test tooltip help of section header
        mouseOver("css=#ConfigurationTestView-Help-Section2 h3 .uif-headerText-span");
        assertFalse(isElementPresent("css=.jquerybubblepopup"));
        mouseOut("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertFalse(isElementPresent("css=.jquerybubblepopup"));

        // test tooltip help of field
        mouseOver("id=missing-tooltip-help_label");
        assertFalse(isElementPresent("css=.jquerybubblepopup"));
        mouseOut("id=missing-tooltip-help_label");
        assertFalse(isElementPresent("css=.jquerybubblepopup"));
    }
}