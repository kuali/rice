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
        waitAndClick(getLinkLocator());
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClick("//button[contains(.,'earch')]");
        waitAndClick("link=edit");
        waitForPageToLoad();
        checkForIncidentReport("submit");
        assertTextPresent("submit");
        assertTextPresent("Save");
        assertTextPresent("blanket approve");
        assertTextPresent("Close");
        assertTextPresent("Cancel");
        lookupAssertions();
        waitAndClick("link=Cancel");
        waitForPageToLoad();
    }
}
