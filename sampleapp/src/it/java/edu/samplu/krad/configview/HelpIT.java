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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

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
public class HelpIT {
    private Selenium selenium;

    // Delay in milliseconds used to allow the help window reload the new help page.
    // waitForPopUp will not work since the window already exists.
    private long HELP_WINDOW_LOAD_DELAY = 3000;

    @Before
    public void setUp() throws Exception {
        System.setProperty("remote.public.url", "http://localhost:8080/kr-dev/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView-Help&methodToCall=start");
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();

        // Login
        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp() throws Exception {
        // test tooltip help
        selenium.mouseOver("css=h1 .uif-headerText-span");
        assertEquals("Sample text for view help", selenium.getText("css=td.jquerybubblepopup-innerHtml"));

        // test external help
        selenium.click("css=input[title=\"Help for Configuration Test View - Help\"]");
        selenium.waitForPopUp("HelpWindow", "30000");
        selenium.selectPopUp("HelpWindow");
        assertEquals("http://www.kuali.org/?view", selenium.getLocation());
        selenium.deselectPopUp();
    }

    /**
     * Test the tooltip and external help on the page
     */
    @Test
    public void testPageHelp() throws Exception {
        // test tooltip help
        selenium.mouseOver("css=h2 .uif-headerText-span");
        assertEquals("Sample text for page help", selenium.getText("css=td.jquerybubblepopup-innerHtml"));

        // test external help
        selenium.click("css=input[title=\"Help for Help Page\"]");
        selenium.waitForPopUp("HelpWindow", "30000");
        selenium.selectPopUp("HelpWindow");
        assertEquals("http://www.kuali.org/?page", selenium.getLocation());
        selenium.deselectPopUp();
    }

    /**
     * Test the tooltip help on the section and fields
     */
    @Test
    public void testTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (selenium.isElementPresent("css=td:contains(\"Sample text for section help - tooltip help\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for field help - label left\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label left\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for field help - label right\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label right\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for field help - label top\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label top\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for standalone help widget tooltip which will never be rendered\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for standalone help widget tooltip which will never be rendered\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for on-focus event tooltip\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for check box help\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for check box help\")"));
        }

        // test tooltip help of section header
        selenium.mouseOver("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));
        selenium.mouseOut("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for section help - tooltip help\")"));

        // verify that no external help exist
        assertFalse(selenium.isElementPresent("css=#ConfigurationTestView-Help-Section1 input.uif-helpImage"));

        // test tooltip help of field with label to the left
        selenium.mouseOver("id=field-label-left_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for field help - label left\")"));
        selenium.mouseOut("id=field-label-left_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label left\")"));

        // test tooltip help of field with label to the right
        selenium.mouseOver("id=field-label-right_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for field help - label righ\")"));
        selenium.mouseOut("id=field-label-right_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label right\")"));

        // test tooltip help of field with label to the top
        selenium.mouseOver("id=field-label-top_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for field help - label top\")"));
        selenium.mouseOut("id=field-label-top_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - label top\")"));

        // verify that standalone help with tooltip is not rendered
        assertFalse(selenium.isElementPresent("id=standalone-help-not-rendered"));

        // test tooltip help when it overrides a tooltip
        selenium.mouseOver("id=override-tooltip_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));
        if (selenium.isElementPresent("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip\")"));
        }
        selenium.mouseOut("id=override-tooltip_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip\")"));

        // test tooltip help in conjunction with a focus event tooltip
        selenium.mouseOver("id=on-focus-tooltip_control");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));
        selenium.mouseOver("id=on-focus-tooltip_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        selenium.mouseOut("id=on-focus-tooltip_control");
        selenium.mouseOut("id=on-focus-tooltip_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for field help - there is also an on-focus tooltip\")"));
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for on-focus event tooltip\")"));

        // test tooltip help against a check box - help contains html
        selenium.mouseOver("id=checkbox_label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for check box help\")"));
        selenium.mouseOut("id=checkbox_label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for check box help\")"));
    }

    /**
     * Test the tooltip help on the sub-section and fields that are display only
     */
    @Test
    public void testDisplayOnlyTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (selenium.isElementPresent("css=td:contains(\"Sample text for sub-section help\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for sub-section help\")"));
        }
        if (selenium.isElementPresent("css=td:contains(\"Sample text for read only field help\")")) {
            assertFalse(selenium.isVisible("css=td:contains(\"Sample text for read only field help\")"));
        }

        // test tooltip help of sub-section header
        selenium.mouseOver("css=h4 .uif-headerText-span");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for sub-section help\")"));
        selenium.mouseOut("css=h4 .uif-headerText-span");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for sub-section help\")"));

        // test tooltip help of display only data field
        selenium.mouseOver("css=#display-field label");
        assertTrue(selenium.isVisible("css=td:contains(\"Sample text for read only field help\")"));
        selenium.mouseOut("css=#display-field label");
        assertFalse(selenium.isVisible("css=td:contains(\"Sample text for read only field help\")"));
    }

    /**
     * Test the tooltip help on the section and fields with no content
     */
    @Test
    public void testMissingTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        assertFalse(selenium.isElementPresent("css=.jquerybubblepopup"));

        // verify that no external help exist
        assertFalse(selenium.isElementPresent("css=#ConfigurationTestView-Help-Section2 input.uif-helpImage"));

        // test tooltip help of section header
        selenium.mouseOver("css=#ConfigurationTestView-Help-Section2 h3 .uif-headerText-span");
        assertFalse(selenium.isElementPresent("css=.jquerybubblepopup"));
        selenium.mouseOut("css=#ConfigurationTestView-Help-Section1 h3 .uif-headerText-span");
        assertFalse(selenium.isElementPresent("css=.jquerybubblepopup"));

        // test tooltip help of field
        selenium.mouseOver("id=missing-tooltip-help_label");
        assertFalse(selenium.isElementPresent("css=.jquerybubblepopup"));
        selenium.mouseOut("id=missing-tooltip-help_label");
        assertFalse(selenium.isElementPresent("css=.jquerybubblepopup"));
    }

   @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}