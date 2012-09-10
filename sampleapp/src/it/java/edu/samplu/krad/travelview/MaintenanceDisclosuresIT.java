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

import edu.samplu.common.KradMenuITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDisclosuresIT extends KradMenuITBase{
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify disclosures are present and functional
     */
    public void testVerifyDisclosures() throws Exception {
        gotoMenuLinkLocator();
        assertElementPresent("//span[contains(.,'Document Overview')]");
        assertElementPresent("//span[contains(.,'Document Overview')]");
        assertElementPresent("//span[contains(.,'Account Information')]");
        assertElementPresent("//span[contains(.,'Fiscal Officer Accounts')]");
        assertElementPresent("//span[contains(.,'Notes and Attachments')]");
        assertElementPresent("//span[contains(.,'Ad Hoc Recipients')]");
        assertElementPresent("//span[contains(.,'Route Log')]");

        colapseExpand("//span[contains(.,'Document Overview')]//img",
                "//label[contains(.,'Organization Document Number')]");
        colapseExpand("//span[contains(.,'Account Information')]//img",
                "//label[contains(.,'Travel Account Type Code')]");
        colapseExpand("//span[contains(.,'Fiscal Officer Accounts')]//img",
                "//a[contains(.,'Lookup/Add Multiple Lines')]");

        expandColapse("//span[contains(.,'Notes and Attachments')]//img", "//label[contains(.,'Note Text')]");
        expandColapse("//span[contains(.,'Ad Hoc Recipients')]", "//span[contains(.,'Ad Hoc Group Requests')]");

        // Handle frames
        waitAndClick("//span[contains(.,'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitIsVisible("//img[@alt='refresh']");

        // relative=top iframeportlet might look weird but either alone results in something not found.
        selectFrame("relative=top");
        selectFrame("iframeportlet");
        waitAndClick("//span[contains(.,'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitNotVisible("//img[@alt='refresh']");
    }
}
