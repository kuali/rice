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
public class LabsMaintenanceDisableDeleteAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR3C3";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Disable Delete");
    }

    protected void testMaintenanceDisableDelete() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance Sample - Basic Delete");
    	waitForTextPresent("Unable to delete. Object does not support delete action.");
    	String assertText [] ={"a14","Travel Account 14","Clearing","fran"};
    	assertTextPresent(assertText);
    }
    
    protected void testMaintenanceBasicEdit() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance Sample - Basic Edit");
    	String assertText [] ={"a14","Travel Account 14","Clearing","fran"};
    	assertTextPresent(assertText);
    	waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.name' and @value='Travel Account 14']");
    	waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.accountTypeCode' and @value='CAT' and @checked='checked']");
    	waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.foId' and @value='fran']");
    }

    @Test
    public void testMaintenanceDisableDeleteBookmark() throws Exception {
    	testMaintenanceDisableDelete();
        passed();
    }

    @Test
    public void testMaintenanceDisableDeleteNav() throws Exception {
    	testMaintenanceDisableDelete();
        passed();
    }
    
    @Test
    public void testMaintenanceBasicEditBookmark() throws Exception {
    	testMaintenanceBasicEdit();
        passed();
    }

    @Test
    public void testMaintenanceBasicEditNav() throws Exception {
    	testMaintenanceBasicEdit();
        passed();
    }
}
