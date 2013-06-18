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
package edu.samplu.krad.demo.travel.account.inquiry;

import edu.samplu.common.WebDriverLegacyITBase;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoTravelAccountInquirySTNGBkMrkGen extends WebDriverLegacyITBase {

    /**
     * //a[@title='Travel Account ']
     */
    public static final String ANCHOR_TITLE_TRAVEL_ACCOUNT = "//a[@title='Travel Account ']";


    /**
     * /kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount
     */
    public static final String BOOKMARK_URL = "/kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount";

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryCollapseExpand")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryCollapseExpand() throws Exception {
        assertTextPresent("Travel Account Number:");
        assertTextPresent("Expand All");
        assertTextPresent("Collapse All");
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, "");

        waitAndClickButtonByText("Collapse All");
        assertIsNotVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, " after clicking Collapse All");

        waitAndClickButtonByText("Expand All");
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, " after clicking Expand All");
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryLightBox")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryLightBox() throws Exception {
        waitAndClickByLinkText("a6");
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, "");
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryBackButton")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryBackButton() throws Exception {
        waitAndClickByLinkText("< Back");
    }
}
