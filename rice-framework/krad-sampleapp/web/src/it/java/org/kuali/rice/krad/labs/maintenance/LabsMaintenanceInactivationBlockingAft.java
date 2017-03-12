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
 * Created by sona on 9/10/14.
 */
public class LabsMaintenanceInactivationBlockingAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR4C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR6C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToMaintenance("Maintenance Sample - Inactivation Blocking");
    }

    protected void testMaintenanceInactivationBlocking() throws InterruptedException {

        waitAndClickByLinkText("Traveler Type Maintenance Record cannot be inactivated since it is referenced by Traveler Detail.");
        waitAndTypeByName("document.documentHeader.documentDescription","Inactivation Blocking");
        waitForElementPresentByXpath("//div//input[@name='document.newMaintainableObject.dataObject.active' and @checked = 'checked']");
        waitAndClickByXpath("//div//input[@name='document.newMaintainableObject.dataObject.active']");
        waitAndClickSubmitByText();
        waitAndClickConfirmSubmitOk();
        String errorMessage []={"This document cannot be Routed because this document inactivates a record referenced by other active records."};
        assertTextPresent(errorMessage);
    }

    @Test
    public void testMaintenanceInactivationBlockingBookmark() throws Exception {
        testMaintenanceInactivationBlocking();
        passed();
    }

    @Test
    public void testMaintenanceInactivationBlockingNav() throws Exception {
        testMaintenanceInactivationBlocking();
        passed();
    }
}
