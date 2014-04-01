/**
 * Copyright 2005-2014 The Kuali Foundation
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
public class LabsMaintenanceDefineControlAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR4C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR4C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    public String getUserName() {
        return "admin"; // must have blanket approve rights
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Define Control");
    }

    protected void testMaintenanceDefineControl() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance (New Account)");
        assertTextPresent(new String[] {"Document Overview", "Account Information", "Sub Accounts", "Notes and Attachments (0)",
            "Ad Hoc Recipients", "Route Log", "submit", "Save", "blanket approve"});
    }

    @Test
    public void testMaintenanceDefineControlBookmark() throws Exception {
    	testMaintenanceDefineControl();
        passed();
    }

    @Test
    public void testMaintenanceDefineControlNav() throws Exception {
    	testMaintenanceDefineControl();
        passed();
    }
}
