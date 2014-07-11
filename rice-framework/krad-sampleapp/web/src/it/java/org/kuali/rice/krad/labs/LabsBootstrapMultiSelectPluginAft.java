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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsBootstrapMultiSelectPluginAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Labs-BootstrapMultiSelect
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Labs-BootstrapMultiSelect";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Bootstrap multi-select plugin");
    }

    protected void testBootstrapMultiSelectPlugin() throws InterruptedException {
    	waitForElementPresentByXpath("//div[@class='btn-group']/ul[@class='multiselect-container dropdown-menu']");
    	waitForElementPresentByXpath("//div[@class='dropdown-menu open']/ul[@class='dropdown-menu inner selectpicker']");
    }

    @Test
    public void testBootstrapMultiSelectPluginBookmark() throws Exception {
    	testBootstrapMultiSelectPlugin();
        passed();
    }

    @Test
    public void testBootstrapMultiSelectPluginNav() throws Exception {
    	testBootstrapMultiSelectPlugin();
        passed();
    }
}
