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
package edu.samplu.common;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @deprecated use WebDriverITBase
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MainMenuLookupITBase extends MenuITBase {
    @Override
    protected String getCreateNewLinkLocator() {
        return "link=Create New";
    }

    @Override
    protected String getMenuLinkLocator() {
        return "link=Main Menu";
    }

    /**
     * Override to execute assertions once a looked-up item's edit action is clicked.
     */
    public abstract void lookupAssertions();

    @Test
    public void testLookUp() throws Exception {
        selenium.click(getLinkLocator());
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        selenium.click("//button[contains(.,'earch')]");
        ITUtil.waitAndClick(selenium, "link=edit");
        selenium.waitForPageToLoad("30000");
        ITUtil.checkForIncidentReport(selenium, "submit");
        assertTrue("submit text not present", selenium.isTextPresent("submit"));
        assertTrue("save text not present", selenium.isTextPresent("Save"));
        assertTrue("blanket approve text not present", selenium.isTextPresent("blanket approve"));
        assertTrue("Close text is not present", selenium.isTextPresent("Close"));
        assertTrue("Cancel text is not present", selenium.isTextPresent("Cancel"));
        lookupAssertions();
        selenium.click("link=Cancel");
        selenium.waitForPageToLoad("30000");
    }
}
