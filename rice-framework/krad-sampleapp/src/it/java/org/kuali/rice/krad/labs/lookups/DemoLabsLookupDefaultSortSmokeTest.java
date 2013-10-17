/*
 * Copyright 2006-2012 The Kuali Foundation
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

public class DemoLabsLookupDefaultSortSmokeTest extends DemoLabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultSortView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultSortView&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Default Sort");
    }

    @Test
    public void testLabsLookupDefaultSortBookmark() throws Exception {
        testLabsLookupDefaultSort();
        passed();
    }

    @Test
    public void testLabsLookupDefaultSortNav() throws Exception {
        testLabsLookupDefaultSort();
        passed();
    }
    
    protected void testLabsLookupDefaultSort()throws Exception {
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr/td/div/span/a[contains(text(), 'a9')]");
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[2]/td/div/span/a[contains(text(), 'a8')]");
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[3]/td/div/span/a[contains(text(), 'a6')]");
    }
}
