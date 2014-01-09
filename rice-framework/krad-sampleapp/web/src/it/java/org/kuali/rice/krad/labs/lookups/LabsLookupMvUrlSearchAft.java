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
public class LabsLookupMvUrlSearchAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=search&viewId=LabsLookup-ResultsLimitView&renderLookupCriteria=false&hideReturnLink=true&multipleValuesSelect=true&lookupCollectionName=travelAccounts
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=search&viewId=LabsLookup-ResultsLimitView&renderLookupCriteria=false&hideReturnLink=true&multipleValuesSelect=true&lookupCollectionName=travelAccounts";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup MV URL - Search");
    }

    @Test
    public void testLabsLookupMVURLSearchBookmark() throws Exception {
        testLabsLookupMVURLSearch();
        passed();
    }

    @Test
    public void testLabsLookupMVURLSearchNav() throws Exception {
        testLabsLookupMVURLSearch();
        passed();
    }
    
    protected void testLabsLookupMVURLSearch()throws Exception {
        if(isElementPresentByXpath("//button[contains(text(), 'Search')]")) {
            jiraAwareFail("LabsLookupMvUrlSearchAft Search Button Present");
        }
        assertResultCount("4");
    }
}
