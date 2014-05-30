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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupQuickFinderDisabledAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-QuickfinderDisabledView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-QuickfinderDisabledView&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Quickfinder Disabled");
    }

    @Test
    public void testLabsLookupQuickFinderDisabledBookmark() throws Exception {
        testLabsLookupQuickFinderDisabled();
        passed();
    }

    @Test
    public void testLabsLookupQuickFinderDisabledNav() throws Exception {
        testLabsLookupQuickFinderDisabled();
        passed();
    }
    
    protected void testLabsLookupQuickFinderDisabled()throws Exception {
        waitForElementPresentByXpath("//input[@name='lookupCriteria[number]' and @value='a2']");
        waitForElementNotPresent(By.xpath("//a[@class='icon-search']"));
    }
}
