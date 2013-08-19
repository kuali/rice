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
package edu.samplu.krad.demo.travel.account;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceNewSmokeTest extends SmokeTestBase {

    /**
     * //div[@class='fancybox-item fancybox-close']
     */
    public static final String FANCY_BOX_CLOSE_XPATH = "//div[@class='fancybox-item fancybox-close']";
    
    /**
     * //div[@class='fancybox-item fancybox-close']
     */
    public static final String FANCY_BOX_IFRAME_XPATH = "//iframe[@class='fancybox-iframe']";

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Maintenance (New)");
    }

    protected void testTravelAccountMaintenanceNew() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Maintenance New Test Document");
        waitAndTypeByName("document.newMaintainableObject.dataObject.number","a1");
        waitAndClickByXpath("//input[@alt='Direct Inquiry']");
        waitAndClickByXpath(FANCY_BOX_CLOSE_XPATH);
        assertTextPresent("Travel Account Maintenance");
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewBookmark() throws Exception {
        testTravelAccountMaintenanceNew();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewNav() throws Exception {
        testTravelAccountMaintenanceNew();
        passed();
    }
}