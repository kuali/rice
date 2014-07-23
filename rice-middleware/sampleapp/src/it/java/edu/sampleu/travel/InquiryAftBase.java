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
package edu.sampleu.travel;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Assert;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class InquiryAftBase extends WebDriverLegacyITBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Travel%20Account%20Lookup&channelUrl="
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
     * +"edu.sampleu.travel.bo.TravelAccount&returnLocation="
     * + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK + ITUtil.SHOW_MAINTENANCE_LINKS
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL
            + "?channelTitle=Travel%20Account%20Lookup&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
            + "edu.sampleu.travel.bo.TravelAccount"
            + "&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS;

    /**
     * //*[contains(button,"Search")]/button
     */
    public static final String SEARCH_BUTTON_XPATH ="//div[contains(button, 'Search')]/button[3]";
    
    /**
     * lookupCriteria
     */
    public static final String CRITERIA_NAME="lookupCriteria";
    
    protected void navigation() throws Exception {
        waitAndClickKRAD();
        //waitAndClickByLinkText(TRAVEL_ACCOUNT_LOOKUP_LINK_TEXT);
        waitAndClickByXpath("(//a[contains(text(),'Travel Account Lookup')])[3]");
    }

    protected void testInquiryNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testInquiry();
        passed();
    }

    protected void testInquiryBookmark(JiraAwareFailable failable) throws Exception {
        testInquiry();
        passed();
    }

    protected void testInquiry() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName(CRITERIA_NAME+"[number]", "a1");
        waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        selectTopFrame();
        Thread.sleep(5000);
        gotoLightBox();
        SeleneseTestBase.assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        assertElementPresentByLinkText("a1");
        selectTopFrame();
        waitAndClickByXpath("//div[@class='fancybox-item fancybox-close']");
        selectFrameIframePortlet();
        waitAndClickByXpath("//button[contains(text(),'Clear Values')]");

        //-----------------------------Code will not work as page has freemarker exceptions------------------------
        Thread.sleep(2000);
        waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        Alert a1 = driver.switchTo().alert();
        Assert.assertEquals("Please enter a value in the appropriate field.", a1.getText());
        a1.accept();
        switchToWindow("null");
        selectFrameIframePortlet();

        //No Direct Inquiry Option for Fiscal Officer.
        waitAndTypeByName(CRITERIA_NAME+"[foId]", "1");
        waitAndClickByXpath("//*[@id='u229']");
        selectTopFrame();
        Thread.sleep(5000);
        gotoLightBox();
        Assert.assertEquals("Fiscal Officer Lookup", getTextByXpath("//h1/span").trim());
        Assert.assertEquals("1", waitAndGetAttributeByName(CRITERIA_NAME + "[id]", "value"));
        waitAndClickByXpath(SEARCH_BUTTON_XPATH);
        selectFrameIframePortlet();
        selectOptionByName(CRITERIA_NAME+"[extension.accountTypeCode]", "CAT");
        waitAndClickByXpath("//fieldset[@id='u232_fieldset']/input[@alt='Search Field']");
        selectTopFrame();
        Thread.sleep(5000);
        gotoLightBox();
        Assert.assertEquals("Travel Account Type Lookup", getTextByXpath("//h1/span").trim());
        Assert.assertEquals("CAT", waitAndGetAttributeByName(CRITERIA_NAME + "[accountTypeCode]", "value"));
        waitAndClickByXpath(SEARCH_BUTTON_XPATH);
        selectFrameIframePortlet();
    }
}
