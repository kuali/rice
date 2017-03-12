/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.labs.maintenance;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMaintenanceEdlAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR7C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR7C1";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToMaintenance("Maintenance Test - Primary Key of Type Long");
    }

    /**
     * If this seems odd, it is, in that this test does not actually test a Maintenance View, and
     * instead only verifies the change made for KULRICE-13943.  If the Maintenance View
     * comes up, and "2008" appears for the "id", then the test passes.
     */
    protected void testMaintenanceEdl() throws InterruptedException {
    	waitAndClickByLinkText("Maintenance Test - Primary Key of Type Long");
        waitForTextPresent("2008");
    }
    
    @Test
    public void testMaintenanceEdlBookmark() throws Exception {
    	testMaintenanceEdl();
        passed();
    }

    @Test
    public void testMaintenanceEdlNav() throws Exception {
        testMaintenanceEdl();
        passed();
    }
}
