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
public class LabsLookupHeadingsAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-HeadingsView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-HeadingsView&hideReturnLink=true";

        
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Headings");
    }

    @Test
    public void testLabsLookupHeadingsBookmark() throws Exception {
        testLabsLookupHeadings();
        passed();
    }

    @Test
    public void testLabsLookupHeadingsNav() throws Exception {
        testLabsLookupHeadings();
        passed();
    }
    
    protected void testLabsLookupHeadings()throws Exception {
        waitForElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-upperGroup clearfix']/span");
        assertElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-lowerGroup clearfix']/p");
        assertElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-rightGroup']/p");
        waitAndClickButtonByText("Custom Button");
        alertAccept();
    }
}
