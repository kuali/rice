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
public class LabsLookupResultsLimitAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ResultsLimitView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ResultsLimitView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Results Limit");
    }

    @Test
    public void testLabsLookupResultsLimitBookmark() throws Exception {
        testLabsLookupResultsLimit();
        passed();
    }

    @Test
    public void testLabsLookupResultsLimitNav() throws Exception {
        testLabsLookupResultsLimit();
        passed();
    }
    
    protected void testLabsLookupResultsLimit()throws Exception {
        waitAndClickSearch3();
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[2]");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[3]")) {
            jiraAwareFail("More than 2 results available for " + this.getClass().toString());
        }

        waitAndClickButtonByText("Clear Values");
        waitAndClickSearch3();
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[2]");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[3]")) {
            jiraAwareFail("More than 2 results available for " + this.getClass().toString());
        }
    }
}
