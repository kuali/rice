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
public class LabsMaintenanceConfiguringHelpLinkAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR2C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR2C1";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Configuring Help Links");
    }

    protected void testMaintenanceConfiguringHelpLink() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Configuring the help link using the externalHelpUrl property of Uif-Help");
    	waitAndClickByXpath("//button[@title='Help for Company Information']");
        Thread.sleep(3000);
        switchToWindow("Kuali Foundation");
    }

    @Test
    public void testMaintenanceConfiguringHelpLinkBookmark() throws Exception {
    	testMaintenanceConfiguringHelpLink();
        passed();
    }

    @Test
    public void testMaintenanceConfiguringHelpLinkNav() throws Exception {
    	testMaintenanceConfiguringHelpLink();
        passed();
    }
}
