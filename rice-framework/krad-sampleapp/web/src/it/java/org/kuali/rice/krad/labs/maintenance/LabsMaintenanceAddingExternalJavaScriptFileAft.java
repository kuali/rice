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
public class LabsMaintenanceAddingExternalJavaScriptFileAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Adding external JavaScript file");
    }

    protected void testMaintenanceAddingExternalJavaScriptFile() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance Sample - Inclusion of external JavaScript");
    	acceptAlert();
    }

    @Test
    public void testMaintenanceAddingExternalJavaScriptFileBookmark() throws Exception {
    	testMaintenanceAddingExternalJavaScriptFile();
        passed();
    }

    @Test
    public void testMaintenanceAddingExternalJavaScriptFileNav() throws Exception {
    	testMaintenanceAddingExternalJavaScriptFile();
        passed();
    }
}
