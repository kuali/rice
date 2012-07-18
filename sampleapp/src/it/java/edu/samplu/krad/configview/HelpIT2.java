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

package edu.samplu.krad.configview;

import edu.samplu.common.WebDriverITBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the help widget
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpIT2 extends WebDriverITBase {

    /**
     * URL for the Configuration Test View - Help
     *
     * <p>
     * Due to a WebDriver bug (feature?) the tooltips can not be tested with WebDriver.  {@link HelpIT} is being used
     * to test help tooltips.
     * </p>
     *
     * @see edu.samplu.common.WebDriverITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        return "http://localhost:8080/kr-dev/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView-Help&methodToCall=start";
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp() throws Exception {
        // test tooltip help
        if (isElementPresentQuick(By.cssSelector("td.jquerybubblepopup-innerHtml"))) {
            assertFalse(driver.findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).isDisplayed());
        }
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(By.cssSelector("h1 .uif-headerText-span"))).perform();
        assertEquals(driver.findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).getText(), "Sample text for view help");
        assertTrue(driver.findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).isDisplayed());
        action.moveToElement(driver.findElement(By.id("mouse-out"))).perform();
        // TODO: moveToElement does not remove mouse focus on previous element. (WebDriver bug?) Therefore we
        //       can not check for the proper behavior of the mouse out events.
        //       Also since multiple tooltips would be displayed we can not test multiple tooltips in one test.  Thus
        //       Tooltips are being tested in the Selenium RC test HelpIT.java
        //       This code has been left in place to show a sample of how tooltip help testing would be performed, in
        //       the hopes that eventually all the testing can be converted to WebDriver.
//        if (isElementPresentQuick(By.cssSelector("td.jquerybubblepopup-innerHtml"))) {
//            assertFalse(driver.findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).isDisplayed());
//        }

        // test external help
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Configuration Test View - Help\"]"), "HelpWindow", "http://www.kuali.org/?view");
    }

    /**
     * Test the external help on the section and fields
     */
    @Test
    public void testExternalHelp() throws Exception {
        // test external help of section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for External Help\"]"), "HelpWindow", "http://www.kuali.org/?section");

        // test external help of field with label left
        assertPopUpWindowUrl(By.cssSelector("#field-label-left-external-help .uif-helpImage"), "HelpWindow",
                "http://www.kuali.org/?label_left");

        // test external help of field with label right
        assertPopUpWindowUrl(By.cssSelector("#field-label-right-external-help .uif-helpImage"), "HelpWindow",
                "http://www.kuali.org/?label_right");

        // test external help of field with label top and help URL from system parameters
        assertPopUpWindowUrl(By.cssSelector("#field-label-top-external-help .uif-helpImage"), "HelpWindow",
                "http://www.kuali.org/?system_parm");

        // test external help of standalone help widget
        assertPopUpWindowUrl(By.id("standalone-external-help"), "HelpWindow", "http://www.kuali.org/?widget_only");
    }

    /**
     * Test the external help on the sub-section and display only fields
     */
    @Test
    public void testDisplayOnlyExternalHelp() throws Exception {
        // test external help of sub-section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Display only fields\"]"), "HelpWindow", "http://www.kuali.org/?sub_section");

        // test external help of display only data field
        assertPopUpWindowUrl(By.cssSelector("#display-field-external-help .uif-helpImage"), "HelpWindow",
                "http://www.kuali.org/?display_field");
    }

    /**
     * Test the external help on the section and fields with missing help URL
     */
    @Test
    public void testMissingExternalHelp() throws Exception {
        // test external help of section is not rendered
        isElementPresent(By.cssSelector("input[title=\"Help for Missing External Help\"]"));

        // test external help of field with blank externalHelpURL is not rendered
        isElementPresentQuick(By.cssSelector("#external-help-externalHelpUrl-empty .uif-helpImage"));

        // test external help of field with empty helpDefinition is not rendered
        isElementPresentQuick(By.cssSelector("#external-help-helpdefinition-empty .uif-helpImage"));

        // test external help of field with missing system parameter is not rendered
        isElementPresentQuick(By.cssSelector("#external-help-system-parm-missing .uif-helpImage"));

        // test external help of standalone help widget is not rendered
        isElementPresentQuick(By.id("standalone-external-help-missing"));
    }
}