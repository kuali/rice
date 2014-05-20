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
public class LabsMaintenanceSampleBasicAft extends LabsMaintenanceBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR1C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR1C1";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToMaintenance("Maintenance Sample - Basic");
    }

    protected void testMaintenanceSampleBasicNew() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Basic New");
    	waitAndTypeByName("document.documentHeader.documentDescription","Test Maintenance Sample Basic Desc");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.travelCompanyName","Kuali");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        waitForTextPresent(" Document was successfully submitted.");
    }
    
    protected void testMaintenanceSampleBasicEdit() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Basic Edit");
    	waitAndTypeByName("document.documentHeader.documentDescription","Test Maintenance Sample Basic Edit");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.travelCompanyName","Kuali");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
    	waitForTextPresent(" Document was successfully submitted.");
    }
    
    protected void testMaintenanceSampleBasicCopy() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Basic Copy");
    	String companyName=getTextByXpath("//div[@data-label='Company Name']");
    	assertElementPresentByXpath("//input[@value='"+companyName+"']");
    }

    @Test
    public void testMaintenanceSampleBasicNewBookmark() throws Exception {
    	testMaintenanceSampleBasicNew();
        passed();
    }

    @Test
    public void testMaintenanceSampleBasicNewNav() throws Exception {
    	testMaintenanceSampleBasicNew();
        passed();
    }
    
    @Test
    public void testMaintenanceSampleBasicEditBookmark() throws Exception {
    	testMaintenanceSampleBasicEdit();
        passed();
    }

    @Test
    public void testMaintenanceSampleBasicEditNav() throws Exception {
    	testMaintenanceSampleBasicEdit();
        passed();
    }
    
    @Test
    public void testMaintenanceSampleBasicCopyBookmark() throws Exception {
    	testMaintenanceSampleBasicCopy();
        passed();
    }

    @Test
    public void testMaintenanceSampleBasicCopyNav() throws Exception {
    	testMaintenanceSampleBasicCopy();
        passed();
    }
}
