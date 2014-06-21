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
public class LabsLookupDefaultAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Default");
    }

    @Test
    public void testLabsLookupDefaultBookmark() throws Exception {
        testLabsLookupDefaultEmpty();
        testLabsLookupDefault();
        passed();
    }

    @Test
    public void testLabsLookupDefaultNav() throws Exception {
        testLabsLookupDefaultEmpty();
        testLabsLookupDefault();
        passed();
    }

    protected void testLabsLookupDefault()throws Exception {
        waitAndTypeByName("lookupCriteria[number]","a1*");
        waitAndTypeByName("lookupCriteria[name]","Travel *");
        waitAndClickSearch3();
        assertTextPresent(new String[] {"Travel Account 14", "a14"});
        waitAndClickButtonByText("Clear Values");
        waitAndClickSearch3();
        assertTextPresent(new String[] {"Travel Account 1", "a1"});
    }

    protected void testLabsLookupDefaultEmpty()throws Exception {
        waitAndClickSearch3();
        waitForTextPresent("items retrieved, displaying all items.");
    }
}
