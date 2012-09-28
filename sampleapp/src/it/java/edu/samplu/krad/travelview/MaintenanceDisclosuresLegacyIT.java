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

import edu.samplu.common.KradMenuLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDisclosuresLegacyIT extends KradMenuLegacyITBase{
   
    @Override
    protected String getLinkLocator() {
        return "Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify disclosures are present and functional
     */
    public void testVerifyDisclosures() throws Exception {
        gotoMenuLinkLocator();
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Account Information')]");
        assertElementPresentByXpath("//span[contains(text(),'Fiscal Officer Accounts')]");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//span[contains(text(),'Ad Hoc Recipients')]");
        assertElementPresentByXpath("//span[contains(text(),'Route Log')]");

        colapseExpandByXpath("//span[contains(text(),'Document Overview')]//img",
                "//label[contains(text(),'Organization Document Number')]");
        colapseExpandByXpath("//span[contains(text(),'Account Information')]//img",
                "//label[contains(text(),'Travel Account Type Code')]");
        colapseExpandByXpath("//span[contains(text(),'Fiscal Officer Accounts')]//img",
                "//a[contains(text(),'Lookup/Add Multiple Lines')]");

        expandColapseByXpath("//span[contains(text(),'Notes and Attachments')]//img", "//label[contains(text(),'Note Text')]");
        expandColapseByXpath("//span[contains(text(),'Ad Hoc Recipients')]", "//span[contains(text(),'Ad Hoc Group Requests')]");

        // Handle frames
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitIsVisibleByXpath("//img[@alt='refresh']");

        // relative=top iframeportlet might look weird but either alone results in something not found.
        selectTopFrame();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitNotVisibleByXpath("//img[@alt='refresh']");
    }
}
