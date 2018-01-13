/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.application;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Tests basic attributes about a transactional document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAuthorizationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization
     */
    public static final String BOOKMARK_URL = "/kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Authorization Document");
    }

    /**
     * Tests that the header text is being pulled directly from the document type label.
     *
     * @throws Exception for any test exceptions
     */
    protected void testHeaderText() throws Exception {
        WebElement element = findElement(By.cssSelector("#TravelAuthorization_header"));
        assertEquals("Header content is incorrect", "Travel Authorization Document", element.getText());
    }

    protected void testTravelAuthorization() throws Exception {
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName("document.documentHeader.documentDescription","Description : "+randomCode);
        waitAndClickByXpath("//button[@id='travelerQuickfinder_quickfinder_act']");
        gotoIframeByXpath("//iframe[@class='uif-iFrame uif-lookupDialog-iframe']");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickByXpath("//a[contains(text(),'return value')]");
        waitAndTypeByName("document.cellPhoneNumber","8000884215");
        waitAndClickByXpath("//div[@data-label='Primary Destination Id']/div/div/button");
        gotoIframeByXpath("//iframe[@class='uif-iFrame uif-lookupDialog-iframe']");
        waitAndClickByXpath("//button[contains(text(),'Clear Values')]");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickByXpath("//a[contains(text(),'return value')]");
        waitAndClickByXpath("//button[contains(text(),'Submit')]");
        waitAndClickConfirmSubmitOk();
        waitForTextPresent("Document was successfully submitted.");
    }

    @Test
    public void testHeaderTextBookmark() throws Exception {
        testHeaderText();
        testTravelAuthorization();
        passed();
    }

    @Test
    public void testHeaderTextNav() throws Exception {
        testHeaderText();
        passed();
    }
}