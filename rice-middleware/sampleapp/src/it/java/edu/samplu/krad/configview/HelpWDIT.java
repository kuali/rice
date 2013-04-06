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

import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * Test the help widget
 *
 * <p>
 * Selenium RC does not allow us to test the external help popup windows due to an error on JavaScrips window.close
 * method when selenium is running.  To test the external help we use the {@link HelpWDIT2} test
 * which utilizes WebDriver.  Unfortunately due to a WebDriver bug/feature we can't test the tooltip help there.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpWDIT extends WebDriverLegacyITBase {

    // Delay in milliseconds used to allow the help window reload the new help page.
    // waitForPopUp will not work since the window already exists.
//    private long HELP_WINDOW_LOAD_DELAY = 3000;

    @Override
    public String getTestUrl() {
        return "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView&methodToCall=start&pageId=ConfigurationTestView-Help-Page";
    }


    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp() throws Exception {
        selectFrameIframePortlet();
        super.testViewHelp();
        passed();
    }

    /**
     * Test the tooltip and external help on the page
     */
    @Test
    public void testPageHelp() throws Exception {
        selectFrameIframePortlet();
        super.testPageHelp();
        passed();
    }

    /**
     * Test the tooltip help on the section and fields
     */
    @Test
    public void testTooltipHelp() throws Exception {
        selectFrameIframePortlet();
        super.testTooltipHelp();
        passed();
    }

    /**
     * Test the tooltip help on the sub-section and fields that are display only
     */
    @Test
    public void testDisplayOnlyTooltipHelp() throws Exception {
        selectFrameIframePortlet();
        super.testDisplayOnlyTooltipHelp();
        passed();
    }

    /**
     * Test the tooltip help on the section and fields with no content
     */
    @Test
    public void testMissingTooltipHelp() throws Exception {
        selectFrameIframePortlet();
        super.testMissingTooltipHelp();
        passed();
    }
}