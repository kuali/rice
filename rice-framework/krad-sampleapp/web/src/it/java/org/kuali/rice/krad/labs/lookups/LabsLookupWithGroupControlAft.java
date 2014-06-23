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
public class LabsLookupWithGroupControlAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsGroupControl-LookupView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsGroupControl-LookupView&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup with Group Control");
    }

    @Test
    public void testLabsLookupWithGroupControlBookmark() throws Exception {
        testLabsLookupWithGroupControl();
        passed();
    }

    @Test
    public void testLabsLookupWithGroupControlNav() throws Exception {
        testLabsLookupWithGroupControl();
        passed();
    }
    
    protected void testLabsLookupWithGroupControl()throws Exception {
        waitAndTypeByName("lookupCriteria[memberId]","testuser1");
        waitAndClickSearch3();
        assertDataTableContains(new String[][]{{"2001"}, {"2003"}});
        waitAndClickButtonByText("Clear Values");
        Thread.sleep(1000);
        waitAndClickSearch3();
        Thread.sleep(3000);
        assertTextPresent("Showing 1 to 10 of", "div.dataTables_info", this.getClass().toString());
    }
}
