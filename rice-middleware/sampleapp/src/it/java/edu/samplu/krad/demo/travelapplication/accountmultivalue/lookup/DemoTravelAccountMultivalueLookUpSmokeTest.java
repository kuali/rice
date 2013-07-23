/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.krad.demo.travelapplication.accountmultivalue.lookup;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMultivalueLookUpSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo";
   
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Multi-Value Lookup");
    }

    protected void testTravelAccountMultivalueLookUp() throws Exception {
       waitAndTypeByName("lookupCriteria[number]","a*");
       selectByName("lookupCriteria[extension.accountTypeCode]", "Clearing Account Type");
       waitAndClickButtonByText(SEARCH);
       Thread.sleep(5000);
       assertTextPresent("a14");
       assertTextPresent("a6");
       assertTextPresent("a9");
       selectByName("lookupCriteria[extension.accountTypeCode]", "Expense Account Type");
       waitAndClickButtonByText(SEARCH);
       Thread.sleep(5000);
       assertTextPresent("a2");
       assertTextPresent("a8");
       selectByName("lookupCriteria[extension.accountTypeCode]", "Income Account Type");
       waitAndClickButtonByText(SEARCH);
       Thread.sleep(5000);
       assertTextPresent("a1");
       assertTextPresent("a3");
    }

    @Test
    public void testTravelAccountMultivalueLookUpBookmark() throws Exception {
        testTravelAccountMultivalueLookUp();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpNav() throws Exception {
        testTravelAccountMultivalueLookUp();
        passed();
    }
}