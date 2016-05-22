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
package org.kuali.rice.krad.demo.travel.application;

import org.junit.Test;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Tests the copy action in a transactional document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAuthorizationCopyAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization
     */
    public static final String BOOKMARK_URL = "/kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization";

    private static final String DOCUMENT_DESCRIPTION_FIELD = "document.documentHeader.documentDescription";
    private static final String CONTACT_NUMBER_FIELD = "document.cellPhoneNumber";

    private static final String DOCUMENT_DESCRIPTION_VALUE = "Test Pessimistic Locking";
    private static final String CONTACT_NUMBER_VALUE = "555-555-5555";

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
     * Tests whether a document is locked for editing for any other user opening it before it has been routed.
     *
     * @throws Exception for any test exceptions
     */
    protected void testCopy() throws Exception {
        String documentNumber = createTravelAuthorization();

        waitForElementNotPresent(By.cssSelector("div[data-label = 'Copied from Document Number']"));

        waitAndClick(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"copy\\\"}']"));

        assertElementPresent(By.cssSelector("div[data-label = 'Copied from Document Number']"));
        WebElement element = findElement(By.xpath("//div[@data-label = 'Copied from Document Number']/span"));
        assertEquals("Copied document number does not match old document", documentNumber, element.getText());
    }

    private String createTravelAuthorization() throws Exception {
        String documentNumber = waitForDocIdKrad();

        waitAndTypeByName(DOCUMENT_DESCRIPTION_FIELD, DOCUMENT_DESCRIPTION_VALUE);

        waitAndTypeByName(CONTACT_NUMBER_FIELD, CONTACT_NUMBER_VALUE);

        waitAndClick(By.cssSelector("div[data-label = 'Primary Destination Id'] button"));
        gotoLightBox();
        waitAndClickButtonByText(SEARCH);
        waitAndClickByLinkText(RETURN_VALUE_LINK_TEXT);

        saveSuccessfully();

        return documentNumber;
    }

    @Test
    public void testCopyBookmark() throws Exception {
        testCopy();
        passed();
    }

    @Test
    public void testCopyNav() throws Exception {
        testCopy();
        passed();
    }

}