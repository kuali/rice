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

import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceEditSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";
  
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Maintenance (Edit)");
    }

    protected void testTravelAccountMaintenanceEdit() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Edit"+RandomStringUtils.randomAlphabetic(2));
        waitAndTypeByName("document.newMaintainableObject.dataObject.subAccount","a1");
        waitAndTypeByName("document.newMaintainableObject.dataObject.subAccountName","Sub Account 1"+RandomStringUtils.randomAlphabetic(2));
        waitAndClickButtonByText("submit");
        Thread.sleep(10000);
        assertTextPresent("Document was successfully submitted.");
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditBookmark() throws Exception {
        testTravelAccountMaintenanceEdit();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditNav() throws Exception {
        testTravelAccountMaintenanceEdit();
        passed();
    }
}