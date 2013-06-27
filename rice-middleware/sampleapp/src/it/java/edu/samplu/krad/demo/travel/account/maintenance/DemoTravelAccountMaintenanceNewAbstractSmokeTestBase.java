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
package edu.samplu.krad.demo.travel.account.maintenance;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoTravelAccountMaintenanceNewAbstractSmokeTestBase extends WebDriverLegacyITBase {

    /**
     * //div[@class='fancybox-item fancybox-close']
     */
    public static final String FANCY_BOX_CLOSE_XPATH = "//div[@class='fancybox-item fancybox-close']";

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Maintenance (New)");
    }

    protected void testTravelAccountMaintenanceNew() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Maintenance New Test Document");
        waitAndTypeByName("document.newMaintainableObject.dataObject.number","a1");
        waitAndClickByXpath("//input[@alt='Direct Inquiry']");
        gotoNestedFrame();
        assertTextPresent("a1");
        waitAndClickByXpath(FANCY_BOX_CLOSE_XPATH);
    }

    public void testDemoTravelAccountMaintenanceNewBookmark(Failable failable) throws Exception {
        testTravelAccountMaintenanceNew();
        passed();
    }

    public void testDemoTravelAccountMaintenanceNewNav(Failable failable) throws Exception {
        navigation();
        testTravelAccountMaintenanceNew();
        passed();
    }
}