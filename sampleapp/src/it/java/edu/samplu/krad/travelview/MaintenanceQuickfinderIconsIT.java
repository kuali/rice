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
package edu.samplu.krad.travelview;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceQuickfinderIconsIT extends UpgradedSeleniumITBase{

    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    /**
     * Verify quickfinder icons appear for account and fiscal officer section fields
     */
    @Test
    public void testVerifyQuickfinderIcons() throws Exception {
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
        assertTrue(selenium.isTextPresent("Document Overview"));
        assertTrue(selenium.isElementPresent("css=#u849"));
        assertTrue(selenium.isElementPresent("css=#u866"));
        assertTrue(selenium.isElementPresent("css=#u934"));
        assertTrue(selenium.isElementPresent("css=#u1114_add"));
    }
}
