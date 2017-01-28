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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Ignore;
import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryClientResponsivenessAjaxFieldQueryAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("AJAX Field Query");
    }

    protected void testClientResponsivenessAjaxFieldQuery() throws Exception {
        waitAndClickByLinkText("Ajax Field Query");
        waitForElementPresentByXpath("//input[@name='inputField3' and @value='a1']");
        clearTypeAndFocus("inputField3", "a1");
        checkIfFocusSuccessful("inputField3", "a1", "Travel Account 1");
        assertTextPresent(new String[] {"Travel Account 1", "fred"});
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethod() throws Exception {
        waitAndClickByLinkText("Ajax Field Query Custom Method");
        waitForElementPresentByXpath("//input[@name='inputField6' and @value='a2']");
        clearTypeAndFocus("inputField6", "a2");
        checkIfFocusSuccessful("inputField6", "a2", "Travel Account 2");
        assertTextPresent(new String[] {"Travel Account 2", "fran"});
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethodAndService() throws Exception {
        waitAndClickByLinkText("Ajax Field Query Custom Method and Service");
    	waitForElementPresentByXpath("//input[@name='inputField9' and @value='a3']");
        clearTypeAndFocus("inputField9", "a3");
        checkIfFocusSuccessful("inputField9", "a3", "Travel Account 3");
        assertTextPresent(new String[] {"Travel Account 3", "frank"});
    }

    /**
     * focus, blur seem real flaky on xvfb, maybe clear, enter value, and tab will be better
     *
     * @param fieldName name of the field that needs to be focused on.
     * @param fieldValue value to be placed in field
     *
     * @throws InterruptedException
     */
    private void clearTypeAndFocus(String fieldName, String fieldValue) throws InterruptedException {
        clearTextByName(fieldName);
        WebElement element = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.name(
                fieldName), this.getClass().toString());
        element.sendKeys("", fieldValue);
        fireEvent(fieldName, "focus");
        fireEvent(fieldName, "blur");
        assertTextPresent(fieldValue);
    }

    /**
     * If the expected message did not appear it means that the field did not gain focus so there was no tab off of the
     * field.  This will attempt to regain focus of the page and try again.
     *
     * @param fieldName name of the field that needs to be focused on.
     * @param fieldValue value to be placed in field
     * @param expectedMessage message that should be present on the screen after tabbing out of the field
     *
     * @throws InterruptedException
     */
    private void checkIfFocusSuccessful(String fieldName, String fieldValue, String expectedMessage)
            throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(3000);
            if (isTextPresent(expectedMessage)) {
                break;
            } else {
                jGrowl("Focus failed - Focusing back on the test window before trying to enter text again.");
                driver.switchTo().window(driver.getWindowHandle());
                clearTypeAndFocus(fieldName, fieldValue);
            }
        }
    }

    @Test
    public void testClientResponsivenessAjaxFieldQueryBookmark() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        passed();
    }

    @Test
    public void testClientResponsivenessAjaxFieldQueryNav() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        passed();
    }

    @Test
    @Ignore //Ignore this test due to the issues found in KULRICE-13108.  They will fail until a solution can be found.
    public void testClientResponsivenessAjaxFieldQueryCustomMethodAndServiceBookmark() throws Exception {
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }

    @Test
    @Ignore //Ignore this test due to the issues found in KULRICE-13108.  They will fail until a solution can be found.
    public void testClientResponsivenessAjaxFieldQueryCustomMethodAndServiceNav() throws Exception {
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }
}
