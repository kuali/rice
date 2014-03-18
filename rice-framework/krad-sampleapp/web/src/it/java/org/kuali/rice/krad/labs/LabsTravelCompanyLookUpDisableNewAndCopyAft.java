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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsTravelCompanyLookUpDisableNewAndCopyAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableField
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradMaintenanceSample-PageR1C2&formKey=baada552-384f-42b9-be02-11e86a72b165&cacheKey=u8vxw39te7rmg81wu62fv011qb";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

//    @Override
//    protected void navigate() throws Exception {
//    	waitAndClickByLinkText("Native Autocomplete Field Disabled");
//    }

    protected void testTravelCompanyLookUpDisableNewAndCopy() throws InterruptedException {
    	waitAndClickByLinkText("Travel Company Maintenance Sample - Disable New and Copy");
    	waitAndTypeByName("lookupCriteria[travelCompanyId]","10000");
    	waitAndClickButtonByText("Search");
    	waitForElementPresentByXpath("//a[contains(text(),'10000')]");
    	waitAndClickButtonByText("Clear Values");
    	waitAndTypeByName("lookupCriteria[travelCompanyName]","AAA Travel");
    	waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
    	waitAndClickButtonByText("Search");
    	waitForElementPresentByXpath("//a[contains(text(),'10001')]");
    	waitForTextPresent("AAA Travel");
    	waitForElementNotPresent(By.xpath("//a[contains(text(),'edit')]"));
    	waitForElementNotPresent(By.xpath("//a[contains(text(),'copy')]"));
    	waitForElementNotPresent(By.xpath("//a[contains(text(),'create new')]"));
    }

    @Test
    public void testTravelCompanyLookUpDisableNewAndCopyBookmark() throws Exception {
    	testTravelCompanyLookUpDisableNewAndCopy();
        passed();
    }

//    @Test
//    public void testTravelCompanyLookUpDisableNewAndCopyNav() throws Exception {
//    	testTravelCompanyLookUpDisableNewAndCopy();
//        passed();
//    }
}
