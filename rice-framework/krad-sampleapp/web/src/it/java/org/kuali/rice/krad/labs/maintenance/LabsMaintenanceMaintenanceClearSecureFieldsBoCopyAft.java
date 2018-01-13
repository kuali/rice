/**
 * Copyright 2005-2018 The Kuali Foundation
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
public class LabsMaintenanceMaintenanceClearSecureFieldsBoCopyAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR4C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR4C3";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Clear secure fields on BO Copy");
    }

    protected void testMaintenanceClearSecureFieldsBoCopyAsUserDev1() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance clearing out secure fields upon a BO copy operation as user dev1");
    	waitForElementPresentByXpath("//div[@data-label='Phone Number']");
    	String phoneNumber=getTextByXpath("//div[@data-label='Phone Number']");
    	assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.phoneNumber' and @value='"+phoneNumber+"']");
    }
    
    protected void testMaintenanceClearSecureFieldsBoCopyAsUserAdmin() throws InterruptedException {
    	waitAndClickByLinkText("Travel Account Maintenance clearing out secure fields upon a BO copy operation as user admin");
    	waitForElementPresentByXpath("//div[@data-label='Phone Number']");
    	String phoneNumber=getTextByXpath("//div[@data-label='Phone Number']");
    	if(!phoneNumber.equalsIgnoreCase("(xxx)xxx-xxxx")){
    		jiraAwareFail("Phone Number is not Hidden from the User in BO Copy");
    	}
    }

    @Test
    public void testMaintenanceClearSecureFieldsBoCopyAsUserDev1Bookmark() throws Exception {
    	testMaintenanceClearSecureFieldsBoCopyAsUserDev1();
        passed();
    }

    @Test
    public void testMaintenanceClearSecureFieldsBoCopyAsUserDev1Nav() throws Exception {
    	testMaintenanceClearSecureFieldsBoCopyAsUserDev1();
        passed();
    }
    
    @Test
    public void testMaintenanceClearSecureFieldsBoCopyAsUserAdminBookmark() throws Exception {
    	testMaintenanceClearSecureFieldsBoCopyAsUserAdmin();
        passed();
    }

    @Test
    public void testMaintenanceClearSecureFieldsBoCopyAsUserAdminNav() throws Exception {
    	testMaintenanceClearSecureFieldsBoCopyAsUserAdmin();
        passed();
    }
}
