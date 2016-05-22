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
package org.kuali.rice.krad.demo.travel.account;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountTypeExportInquiryAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccountType&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType&hideReturnLink=true";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Account Type Lookup");
    }

    protected void testTravelAccountTypeExportInquiryButton() throws Exception {
        // lookup the existing 'CAT' record via inquiry
        waitAndTypeByName("lookupCriteria[accountTypeCode]", "CAT");
        waitAndClick(By.cssSelector("div[data-label = 'Travel Account Type Code'] button"));
        gotoLightBox();

        // there should be data and the export button should exist
        waitForTextPresent("CAT");
        waitForElementPresent(By.xpath("//div[@data-parent = 'LabsInquiry-DefaultView']//button[contains(text(), 'Export')]"));
    }

    protected void testTravelAccountTypeExportInquiryNoButton() throws Exception {
        // lookup the non-existing 'TAC' record via inquiry
        waitAndTypeByName("lookupCriteria[accountTypeCode]", "TAC");
        waitAndClick(By.cssSelector("div[data-label = 'Travel Account Type Code'] button"));
        gotoLightBox();

        // there should be no data and the export button should not exist
        waitForTextNotPresent("TAC");
        waitForElementNotPresent(By.xpath("//div[@data-parent = 'LabsInquiry-DefaultView']//button[contains(text(), 'Export')]"));
    }

    @Test
    public void testTravelAccountTypeExportInquiryButtonNav() throws Exception {
        testTravelAccountTypeExportInquiryButton();
        passed();
    }

    @Test
    public void testTravelAccountTypeExportInquiryButtonBookmark() throws Exception {
        testTravelAccountTypeExportInquiryButton();
        passed();
    }

    @Test
    public void testTravelAccountTypeExportInquiryNoButtonNav() throws Exception {
        testTravelAccountTypeExportInquiryNoButton();
        passed();
    }

    @Test
    public void testTravelAccountTypeExportInquiryNoButtonBookmark() throws Exception {
        testTravelAccountTypeExportInquiryNoButton();
        passed();
    }

}