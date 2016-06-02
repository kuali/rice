/**
 * Copyright 2005-2016 The Kuali Foundation
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
package edu.sampleu.kim.api.reference;

import edu.sampleu.krad.reference.AddressTypeAft;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReferenceAddressTypeAft extends AddressTypeAft {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Address%20Type&channelUrl=" + WebDriverUtils
     * .getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
     * "org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo&renderReturnLink=true"
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Address%20Type&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
                    "org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo&hideReturnLink=false&docFormKey=88888888&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL;

    @Override
    protected void clickSearch() throws InterruptedException {
        waitAndClickSearch();
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Address Type");
    }

    @Override
    protected void testAddressType() throws Exception {
        selectFrameIframePortlet();

        //Search by "Both" Filter in Active Indicator
        clickSearch();
        String[][] data = {{"HM", "Home", "b"},
                {"OTH", "Other", "c"},
                {"WRK", "Work", "a"}};
        assertTextPresent(data);
        waitAndClickByName("methodToCall.clearValues");

        //Search by "Yes" Filter in Active Indicator
        waitAndClickById("activeYes");
        clickSearch();
        assertTextPresent(data);
        waitAndClickByName("methodToCall.clearValues");

        //Search by "No" Filter in Active Indicator
        waitAndClickById("activeNo");
        clickSearch();
        waitForTextPresent("No values match this search.");
        waitAndClickByName("methodToCall.clearValues");
        waitAndClickById("activeYes");

        //Search by Address Type Code Filter
        waitAndTypeByName("code","HM");
        clickSearch();
        String[] assertSearchResultForTypeCode = {"HM", "Home", "b"};
        assertTextPresent(assertSearchResultForTypeCode);
        waitAndClickByName("methodToCall.clearValues");

        //Search by Address Type Name Filter
        waitAndTypeByName("name","Home");
        clickSearch();
        String[] assertSearchResultForTypeName = {"HM", "Home", "b"};
        assertTextPresent(assertSearchResultForTypeName);
        waitAndClickByName("methodToCall.clearValues");
    }
}
