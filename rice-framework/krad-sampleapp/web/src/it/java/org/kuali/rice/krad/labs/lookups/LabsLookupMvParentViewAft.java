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
public class LabsLookupMvParentViewAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-UseResultsLimitOnParentView&multipleValuesSelect=true&lookupCollectionName=travelAccounts
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-UseResultsLimitOnParentView&multipleValuesSelect=true&lookupCollectionName=travelAccounts";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup MV Parent View");
    }

    @Test
    public void testLabsLookupMvParentViewBookmark() throws Exception {
        testLabsLookupMvParentView();
        passed();
    }

    @Test
    public void testLabsLookupMvParentViewNav() throws Exception {
        testLabsLookupMvParentView();
        passed();
    }
    
    protected void testLabsLookupMvParentView()throws Exception {
        waitAndTypeByName("lookupCriteria[name]","*");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","eri*");
        waitAndClickSearch3();
        assertResultCount("4");

        waitAndClickButtonByText("Clear Values");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","xxx*");
        waitAndClickSearch3();
        waitForTextPresent("No values match this search.");

        waitAndClickButtonByText("Clear Values");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","xxx");
        waitAndClickSearch3();
        waitForTextPresent("No values match this search.");

        waitAndClickButtonByText("Clear Values");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","eric*");
        waitAndClickSearch3();
        assertResultCount("3");
    }
}
