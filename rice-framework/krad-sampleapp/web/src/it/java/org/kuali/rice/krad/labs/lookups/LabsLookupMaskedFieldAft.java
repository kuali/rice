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
public class LabsLookupMaskedFieldAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-MaskedFieldView
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-MaskedFieldView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup with Masked Field");
    }

    @Test
    public void testLabsLookupMaskedFieldBookmark() throws Exception {
        testLabsLookupMaskedFieldNoSearchCriteria();
        testLabsLookupMaskedField();
        testLabsLookupMaskedFieldWildcard();
        passed();
    }

    @Test
    public void testLabsLookupMaskedFieldNav() throws Exception {
        testLabsLookupMaskedFieldNoSearchCriteria();
        testLabsLookupMaskedField();
        testLabsLookupMaskedFieldWildcard();
        passed();
    }

    protected void testLabsLookupMaskedField()throws Exception {
        waitAndTypeByName("lookupCriteria[number]","a1*");
        waitAndSelectByName("uLookupResults_layout_length", "25");
        assertTextPresent(new String[] {"a14", "*********"});
        waitAndClickButtonByText("Clear Values");
        waitAndTypeByName("lookupCriteria[name]","Travel Account 1");
        waitAndClickButtonByText("Search");
        assertTextPresent(new String[] {"a1", "*********"});
    }

    protected void testLabsLookupMaskedFieldWildcard()throws Exception {
        clearTextByName("lookupCriteria[name]");
        waitAndTypeByName("lookupCriteria[number]","a1*");
        waitAndTypeByName("lookupCriteria[name]","Travel *");
        waitAndClickButtonByText("Search");
        waitForTextPresent("Travel Account Name is a secure field. Wildcards are not allowed on secure fields.");
    }

    protected void testLabsLookupMaskedFieldNoSearchCriteria()throws Exception {
        waitAndClickButtonByText("Search");
        waitForTextPresent("Showing 1 to 10 of");
    }
}
