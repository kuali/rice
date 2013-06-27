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

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoTravelAccountInquirySmokeTestBase extends WebDriverLegacyITBase {

    /**
     * //a[@title='Travel Account ']
     */
    public static final String ANCHOR_TITLE_TRAVEL_ACCOUNT = "//a[@title='Travel Account ']";

    /**
     * /kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount
     */
    public static final String BOOKMARK_URL = "/kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount";

    /**
     * Collapse All
     */
    public static final String COLLAPSE_ALL = "Collapse All";

    /**
     * Expand All
     */
    public static final String EXPAND_ALL = "Expand All";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Inquiry");
    }

    protected void testInquiryBackButton() throws Exception {
        waitAndClickButtonByText("Back");
        waitForElementPresentByClassName("uif-headerText");
        assertTextPresent("Demo - Travel Application");
    }

    public void testInquiryBackButtonBookmark(Failable failable) throws Exception {
        testInquiryBackButton();
        passed();
    }

    public void testInquiryBackButtonNav(Failable failable) throws Exception {
        navigation();
        testInquiryBackButton();
        passed();
    }

    protected void testCollapseExpand() throws InterruptedException {
        assertTextPresent("Travel Account Number:");
        assertTextPresent(EXPAND_ALL);
        assertTextPresent(COLLAPSE_ALL);
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, "");

        waitAndClickButtonByText(COLLAPSE_ALL);
        assertIsNotVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, " after clicking " + COLLAPSE_ALL);

        waitAndClickButtonByText(EXPAND_ALL);
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, " after clicking " + EXPAND_ALL);
    }

    public void testInquiryCollapseExpandBookmark(Failable failable) throws Exception {
        testCollapseExpand();
        passed();
    }

    public void testInquiryCollapseExpandNav(Failable failable) throws Exception {
        navigation();
        testCollapseExpand();
        passed();
    }

    protected void testInquiryLightBox() throws Exception {
        waitAndClickByLinkText("a6");
        assertIsVisibleByXpath(ANCHOR_TITLE_TRAVEL_ACCOUNT, "");
    }

    public void testInquiryLightBoxBookmark(Failable failable) throws Exception {
        testInquiryLightBox();
        passed();
    }

    public void testInquiryLightBoxNav(Failable failable) throws Exception {
        navigation();
        testInquiryLightBox();
        passed();
    }
}