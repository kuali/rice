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
package edu.samplu.krad.demo.travel.account;

import edu.samplu.common.Failable;
import edu.samplu.common.SmokeTestBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountInquirySmokeTest extends SmokeTestBase {

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
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
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
        testInquiryBackButton();
        passed();
    }

    protected void testCollapseExpand() throws InterruptedException {
        waitForElementPresentByClassName("demo-contactInfo");
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
        testInquiryLightBox();
        passed();
    }

    @Test
    public void testInquiryCollapseExpandBookmark() throws Exception {
        testInquiryCollapseExpandBookmark(this);
    }

    @Test
    public void testInquiryLightBoxBookmark() throws Exception {
        testInquiryLightBoxBookmark(this);
    }

    @Test
    public void testInquiryBackButtonBookmark() throws Exception {
        testInquiryBackButtonBookmark(this);
    }
    @Test
    public void testInquiryCollapseExpandNav() throws Exception {
        testInquiryCollapseExpandNav(this);
    }

    @Test
    public void testInquiryLightBoxNav() throws Exception {
        testInquiryLightBoxNav(this);
    }

    @Test
    public void testInquiryBackButtonNav() throws Exception {
        testInquiryBackButtonNav(this);
    }
}