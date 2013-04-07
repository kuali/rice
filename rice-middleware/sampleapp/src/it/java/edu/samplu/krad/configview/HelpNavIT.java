/**
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
package edu.samplu.krad.configview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * Test the help widget
 * 
 * <p>
 * Selenium RC does not allow us to test the external help popup windows due to an error on
 * JavaScrips window.close method when selenium is running. To test the external help we use the
 * {@link HelpWDIT2} test which utilizes WebDriver. Unfortunately due to a WebDriver bug/feature we
 * can't test the tooltip help there.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpNavIT extends WebDriverLegacyITBase {

    // Delay in milliseconds used to allow the help window reload the new help page.
    // waitForPopUp will not work since the window already exists.
    //    private long HELP_WINDOW_LOAD_DELAY = 3000;

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp() throws Exception {
        navigateToHelp();
        super.testViewHelp();
        passed();
    }

    /**
     * Test the tooltip and external help on the page
     */
    @Test
    public void testPageHelp() throws Exception {
        navigateToHelp();
        super.testPageHelp();
        passed();
    }

    /**
     * Test the tooltip help on the section and fields
     */
    @Test
    public void testTooltipHelp() throws Exception {
        navigateToHelp();
        super.testTooltipHelp();
        passed();
    }

    /**
     * Test the tooltip help on the sub-section and fields that are display only
     */
    @Test
    public void testDisplayOnlyTooltipHelp() throws Exception {
        navigateToHelp();
        super.testDisplayOnlyTooltipHelp();
        passed();
    }

    /**
     * Test the tooltip help on the section and fields with no content
     */
    @Test
    public void testMissingTooltipHelp() throws Exception {
        navigateToHelp();
        super.testMissingTooltipHelp();
        passed();
    }

    private void navigateToHelp() throws Exception
    {
        waitAndClickKRAD();
        waitAndClickByXpath(CONFIGURATION_VIEW_XPATH);
        switchToWindow(CONFIGURATION_VIEW_WINDOW_TITLE);
        waitAndClickByLinkText("Help");
        Thread.sleep(5000);
        selectFrameIframePortlet();
    }
}