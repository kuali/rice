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
package org.kuali.rice.krad.labs.transactional;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupSecurityTravelAuthorizationDocumentBase extends LabsTransactionalBase {

    /**
     * /kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization&viewName=LabsLookupSecurityTravelAuthorization
     */
    public static final String BOOKMARK_URL = "/kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization&viewName=LabsLookupSecurityTravelAuthorization";

    public static final String FRAME_URL = "/kr-krad/lookup?conversionFields=";

    private static final String PHONE_NUMBER_NAME = "document.travelerDetail.phoneNumber";
    private static final String PHONE_NUMBER_DECRYPTED = "8005551212";

    private static final String CUSTOMER_NUMBER_NAME = "document.travelerDetail.customerNumber";
    private static final String CUSTOMER_NUMBER_DECRYPTED = "CUST";

    private static final String EMAIL_ADDRESS_NAME = "document.travelerDetail.emailAddress";

    private static final String TRAVELER_TYPE_CODE_NAME = "travelerType.code";

    private static final String CONVERSION_FIELDS = "conversionFields=";
    private static final String ERRANT_CONVERSION_FIELD = TRAVELER_TYPE_CODE_NAME + "%3A" + EMAIL_ADDRESS_NAME + "%2C";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToTransactional("Transactional Sample - Lookup Security");
        waitAndClickByLinkText("Travel Authorization Transactional Sample - Lookup Security");
    }

    /**
     * Tests the basic case in which the phone number does not appear anywhere on the page decrypted.
     *
     * @throws Exception
     */
    protected void testTransactionalLookupSecurity() throws Exception {
        waitAndClickTravelerQuickfinder();

        gotoLightBoxIframe();
        waitAndClickSearch3();
        waitAndClickReturnValue();
        String phoneNumber = waitAndGetLabeledText("Phone Number:");

        assertTrue("Secure field phoneNumber was not empty", StringUtils.isBlank(phoneNumber));
        assertTextNotPresent(PHONE_NUMBER_DECRYPTED);
    }

    private void waitAndClickTravelerQuickfinder() throws InterruptedException {
        jGrowl("Click Traveler Quickfinder Icon");
        waitAndClick(By.id("travelerQuickfinder_quickfinder_act"));
        waitForPageToLoad();
    }

    /**
     * Tests the case in which the data dictionary phone number conversion field is changed to have it appear in the
     * email address field, which is not secured.
     *
     * @throws Exception
     */
    protected void testTransactionalLookupSecurityAddDataDictionaryConversionField() throws Exception {
        waitAndClickTravelerQuickfinder();

        final String xpathExpression = "//iframe[contains(@src,'" + FRAME_URL + "')]";
        driver.switchTo().frame(driver.findElement(By.xpath(xpathExpression)));

        String newUrl = StringUtils.replace(driver.getCurrentUrl(), PHONE_NUMBER_NAME, EMAIL_ADDRESS_NAME);
        jGrowl("Opening -> "+newUrl);
        open(newUrl);
        waitForPageToLoad();

        waitAndClickSearch3();
        waitAndClickReturnValue();

        final String xpathExpression2 = "//div[contains(@data-label,'Email Address')]";
        String emailAddress = waitAndGetAttribute(By.xpath(xpathExpression2),"value");

        assertTrue("Non-secure field emailAddress was not empty", StringUtils.isBlank(emailAddress));
        assertTextNotPresent(PHONE_NUMBER_DECRYPTED);
    }

    /**
     * Tests the case in which the UIf customer number conversion field is changed to have it appear in the email
     * address field, which is not secured.
     *
     * @throws Exception
     */
    protected void testTransactionalLookupSecurityAddUifConversionField() throws Exception {
        waitAndClickTravelerQuickfinder();

        final String xpathExpression = "//iframe[contains(@src,'" + FRAME_URL + "')]";
        driver.switchTo().frame(driver.findElement(By.xpath(xpathExpression)));

        String newUrl = StringUtils.replace(driver.getCurrentUrl(), CUSTOMER_NUMBER_NAME, EMAIL_ADDRESS_NAME);
        jGrowl("Opening -> "+newUrl);
        open(newUrl);
        waitForPageToLoad();

        waitAndClickSearch3();
        waitAndClickReturnValue();

        final String xpathExpression2 = "//div[contains(@data-label,'Email Address')]";
        String emailAddress = waitAndGetAttribute(By.xpath(xpathExpression2),"value");

        assertTrue("Non-secure field emailAddress was not empty", StringUtils.isBlank(emailAddress));
        assertTextNotPresent(CUSTOMER_NUMBER_DECRYPTED);
    }

    /**
     * Tests the case in which the a new conversion field is added so that a field that is not referenced in either the
     * data dictionary or the Uif (the traveler type code) appears in the email address field, which is not secured.
     *
     * @throws Exception
     */
    protected void testTransactionalLookupSecurityAddHiddenConversionField() throws Exception {
        waitAndClickTravelerQuickfinder();

        final String xpathExpression = "//iframe[contains(@src,'" + FRAME_URL + "')]";
        driver.switchTo().frame(driver.findElement(By.xpath(xpathExpression)));

        final String currentUrl = driver.getCurrentUrl();

        assertTrue("Url doesn't have CONVERSION_FIELDS (" + CONVERSION_FIELDS + ")", StringUtils.indexOf(currentUrl, CONVERSION_FIELDS) > -1);
        int splitPosition = StringUtils.indexOf(currentUrl, CONVERSION_FIELDS) + CONVERSION_FIELDS.length();
        String before = StringUtils.substring(currentUrl, 0, splitPosition);
        String after = StringUtils.substring(currentUrl, splitPosition);
        String newUrl = before + ERRANT_CONVERSION_FIELD + after;
        jGrowl("Opening -> "+newUrl);
        open(newUrl);
        waitForPageToLoad();

        waitAndClickSearch3();
        waitAndClickReturnValue();

        final String xpathExpression2 = "//div[contains(@data-label,'Email Address')]";
        String emailAddress = waitAndGetAttribute(By.xpath(xpathExpression2),"value");

        assertTrue("Non-secure field emailAddress was not empty", StringUtils.isBlank(emailAddress));
    }

}
