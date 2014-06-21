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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupWithUserControlAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsGroupControl-LookupView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsUserControl-LookupView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup with User Control");
    }

    @Test
    public void testLabsLookupWithUserControlBookmark() throws Exception {
        testLabsLookupWithUserControl();
        passed();
    }

    @Test
    public void testLabsLookupWithUserControlNav() throws Exception {
        testLabsLookupWithUserControl();
        passed();
    }
    
    protected void testLabsLookupWithUserControl()throws Exception {
        waitAndTypeByName("lookupCriteria[myPrincipalName]","admin");
        //force js function to set hidden principalId to the same value as myPrincipalName
        //if this is not done and the search gets called before principalId is set you will get an exception
        waitAndTypeByName("lookupCriteria[entityId]","");
        waitAndClickSearch3();
        waitForElementPresentByClassName("dataTables_info");
        assertTextPresent("1100");
        assertTextPresent("admin");
        waitAndClickButtonByText("Clear Values");
        Thread.sleep(2000);
        waitAndClickSearch3();
        waitForTextPresent("Showing 1 to 10 of");
    }
}
