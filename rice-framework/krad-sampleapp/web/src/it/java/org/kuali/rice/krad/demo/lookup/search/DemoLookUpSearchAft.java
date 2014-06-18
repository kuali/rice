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
package org.kuali.rice.krad.demo.lookup.search;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpSearchAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=search&viewId=LookupSampleViewURLSearch&lookupCriteria['number']=a1*&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=search&viewId=LookupSampleViewURLSearch&lookupCriteria['number']=a1*&hideReturnLink=true";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Lookup Search");
    }

    protected void testLookUpSearch() throws InterruptedException {
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        assertTextPresent("a1*");
        assertElementPresentByXpath("//a[contains(text(), 'a14')]");
        findElements(By.xpath("//a[contains(text(), 'a2')]"));
    }

    @Test
    public void testLookUpSearchBookmark() throws Exception {
        testLookUpSearch();
        passed();
    }

    @Test
    public void testLookUpSearchNav() throws Exception {
        testLookUpSearch();
        passed();
    }
}
