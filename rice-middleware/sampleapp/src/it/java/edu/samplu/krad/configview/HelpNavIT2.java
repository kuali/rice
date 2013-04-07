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

package edu.samplu.krad.configview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * Test the help widget
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpNavIT2 extends WebDriverLegacyITBase {

    /**
     * URL for the Configuration Test View - Help
     * 
     * <p>
     * Due to a WebDriver bug (feature?) the tooltips can not be tested with WebDriver.
     * {@link HelpWDIT} is being used to test help tooltips.
     * </p>
     * 
     * @see edu.samplu.common.WebDriverITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp2() throws Exception {
        navigateToHelp();
        super.testViewHelp2();
        passed();
    }

    /**
     * Test the external help on the section and fields
     */
    @Test
    public void testExternalHelp2() throws Exception {
        navigateToHelp();
        super.testExternalHelp2();
        passed();
    }

    /**
     * Test the external help on the sub-section and display only fields
     */
    @Test
    public void testDisplayOnlyExternalHelp2() throws Exception {
        navigateToHelp();
        super.testDisplayOnlyExternalHelp2();
        passed();
    }

    /**
     * Test the external help on the section and fields with missing help URL
     */
    @Test
    public void testMissingExternalHelp2() throws Exception {
        navigateToHelp();
        super.testMissingExternalHelp2();
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