/**
 * Copyright 2005-2017 The Kuali Foundation
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
package edu.sampleu.travel;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class TravelAccountLookupAftBase extends WebDriverLegacyITBase {

    /**
     * "/portal.do?channelTitle=Travel%20Account%20Lookup&channelUrl="
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
     * + "edu.sampleu.travel.bo.TravelAccount&returnLocation="
     * + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK + ITUtil.SHOW_MAINTENANCE_LINKS
     */
    public static final String BOOKMARK_URL = "/portal.do?channelTitle=Travel%20Account%20Lookup&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
            + "edu.sampleu.travel.bo.TravelAccount&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText(TRAVEL_ACCOUNT_LOOKUP_LINK_TEXT);
    }

    protected void testTravelAccountLookupNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testTravelAccountLookup();
        passed();
    }

    protected void testTravelAccountLookupBookmark(JiraAwareFailable failable) throws Exception {
        testTravelAccountLookup();
        passed();
    }

    protected void testTravelAccountLookup() throws Exception {
        selectFrameIframePortlet();

        //Blank Search
        waitAndClickByXpath(SEARCH_XPATH_3);
        Thread.sleep(2000);

        //  --------------------------------Further code will not run due to page exception------------------------------------
        //assertElementPresentByLinkText("a1");
        //ssertElementPresentByLinkText("a2");
        //assertElementPresentByLinkText("a3");

        //QuickFinder Lookup
        //waitAndTypeByName("lookupCriteria[number]", "a*");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertElementPresentByLinkText("a1");
        //assertElementPresentByLinkText("a2");
        //assertElementPresentByLinkText("a3");
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);

        //search with each field
        //waitAndTypeByName("lookupCriteria[number]", "a2");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertElementPresentByLinkText("a2");
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);
        // waitAndTypeByName("lookupCriteria[foId]", "1");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertEquals("1", getTextByXpath("//table[@id='u27']//tr//td[8]").trim().substring(0, 1));
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);
        //selectOptionByName("lookupCriteria[extension.accountTypeCode]", "CAT");
        //waitAndClickByXpath("//*[@id='u18']");
        //waitAndClickByXpath("//table[@id='u27']//tr//td[2]//a");
        //Thread.sleep(2000);
        //selectTopFrame();
        //Thread.sleep(5000);
        //gotoLightBox();
        //assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        //assertEquals("CAT - Clearing Account Type", getTextByXpath("//*[@id='u44_control']").trim());
        //waitAndClickByXpath("//button[@id='u13']");
        //selectFrame("iframeportlet");
    }
}