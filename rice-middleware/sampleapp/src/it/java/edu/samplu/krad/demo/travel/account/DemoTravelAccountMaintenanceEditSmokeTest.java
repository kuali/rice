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

import edu.samplu.common.Failable;
import edu.samplu.common.SmokeTestBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceEditSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true";
  
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Maintenance (Edit)");
    }

    protected void testTravelAccountMaintenanceEdit() throws Exception {
        if(!isTextPresent("Stacktrace (only in dev mode)")) {
            //code goes here
        } else {
            fail("Development Exception (Error) on page. Test cannot be executed.");
        }
    }

    public void testDemoTravelAccountMaintenanceEditBookmark(Failable failable) throws Exception {
        testTravelAccountMaintenanceEdit();
        passed();
    }

    public void testDemoTravelAccountMaintenanceEditNav(Failable failable) throws Exception {
        navigate();
        testTravelAccountMaintenanceEdit();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditBookmark() throws Exception {
        testDemoTravelAccountMaintenanceEditBookmark(this);
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditNav() throws Exception {
        testDemoTravelAccountMaintenanceEditNav(this);
    }
}