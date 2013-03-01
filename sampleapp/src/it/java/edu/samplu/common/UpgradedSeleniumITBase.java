/*
 * Copyright 2006-2012 The Kuali Foundation
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
package edu.samplu.common;

import com.thoughtworks.selenium.Selenium;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

import static edu.samplu.common.WebDriverLegacyITBase.gotoNestedFrame;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UpgradedSeleniumITBase {
    private Selenium selenium;
    protected WebDriver driver;

    protected String getBaseUrlString() {
        return ITUtil.getBaseUrlString();
    }

    /**
     * Returns the URL to be used with this test
     *
     * @return URL of the test
     */
    public abstract String getTestUrl();

    /**
     * Override in test to define a user other than admin
     * @return
     */
    public String getUserName() {
        return "admin";
    }

    @Before
    public void setUp() throws Exception {
        driver = ITUtil.getWebDriver();
        if (!getTestUrl().startsWith("/")) {
            fail("getTestUrl does not start with /"); // TODO add it?
        }
        selenium = new WebDriverBackedSelenium(driver, ITUtil.getBaseUrlString() + getTestUrl());

        // Login
        selenium.open(ITUtil.getBaseUrlString() + getTestUrl());
        ITUtil.loginSe(selenium, getUserName());
    }

    protected void assertDocFinal(String docId) {
        ITUtil.assertDocFinal(selenium, docId);
    }

    protected void assertElementPresent(String locator) {
        assertTrue(isElementPresent(locator));
    }

    protected void assertTextPresent(String text) {
        assertTextPresent("", text);
    }

    protected void assertTextPresent(String message, String text) {
        assertTrue(text + " text not present " + message, selenium.isTextPresent(text));
    }

    protected void blanketApproveTest() throws InterruptedException {
        ITUtil.blanketApprove(selenium);
    }

    protected void close() {
        selenium.close();
    }

    protected void check(String locator) {
        selenium.check(locator);
    }
    
    protected void checkErrorMessageItem(String message) {
        ITUtil.checkErrorMessageItem(selenium, message);
    }

    protected void checkForIncidentReport(String locator) {
        checkForIncidentReport(locator, "");
    }

    protected void checkForIncidentReport(String locator, String message) {
        ITUtil.checkForIncidentReport(selenium, message);
    }

    protected void chooseCancelOnNextConfirmation() {
        selenium.chooseCancelOnNextConfirmation();
    }

    protected void colapseExpand(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClick(clickLocator);
        waitNotVisible(visibleLocator);

        waitAndClick(clickLocator);
        waitIsVisible(visibleLocator);
    }

    protected void expandColapse(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClick(clickLocator);
        waitIsVisible(visibleLocator);

        waitAndClick(clickLocator);
        waitNotVisible(visibleLocator);
    }

    protected void fireEvent(String locator, String event) {
        selenium.fireEvent(locator, event);
    }

    protected void focus(String locator) {
        selenium.focus(locator);
    }

    protected void focusAndType(String fieldLocator, String typeText) {
        selenium.focus(fieldLocator);
        selenium.type(fieldLocator, typeText);
    }

    protected String[] getAllWindowTitles() {
        return selenium.getAllWindowTitles();
    }

    protected String getAttribute(String attributeLocator) {
        return selenium.getAttribute(attributeLocator);
    }

    protected String getConfirmation() {
        return selenium.getConfirmation();
    }

    protected Number getCssCount(String cssCountRows) {
        return selenium.getCssCount(cssCountRows);
    }

    protected String getSelectedLabel(String locator) {
        return selenium.getSelectedLabel(locator);
    }

    protected String[] getSelectOptions(String locator) {
        return selenium.getSelectOptions(locator);
    }
    
    protected String getEval(String script) {
        return selenium.getEval(script);
    }
    
    protected String getLocation() {
        return selenium.getLocation();
    }
    
    protected String getText(String locator) {
        return selenium.getText(locator);
    }

    protected String getTitle() {
        return selenium.getTitle();
    }

    protected String getValue(String locator) {
        return selenium.getValue(locator);
    }

    protected boolean isElementPresent(String locator) {
        return selenium.isElementPresent(locator);
    }
    
    protected boolean isTextPresent(String locator) {
        return selenium.isTextPresent(locator);
    }

    protected boolean isVisible(String locator) {
        return selenium.isVisible(locator);
    }

    protected void keyDown(String locator, String key) {
        selenium.keyDown(locator, key);
    }

    protected void keyPress(String locator, String key) {
        selenium.keyPress(locator, key);
    }

    protected void keyUp(String locator, String key) {
        selenium.keyUp(locator, key);
    }

    protected void mouseOver(String locator) {
        selenium.mouseOver(locator);
    }
    
    protected void mouseOut(String locator) {
        selenium.mouseOut(locator);
    }
    
    protected void open(String url) {
        selenium.open(url);
    }
    
    protected void removeAllSelections(String locator) {
        selenium.removeAllSelections(locator);
    }
    
    protected void select(String locator, String select) {
        selenium.select(locator, select);
    }

    protected void selectFrame(String locator) {
        if ("iframeportlet".equals(locator)) {
            gotoNestedFrame(driver);
        } else {
            try {
                driver.switchTo().frame(locator);
            } catch (NoSuchFrameException nsfe) {
                // don't fail
            }
        }
    }

    protected void selectTopFrame() {
        driver.switchTo().defaultContent();
    }

    protected void setSpeed(String speed) {
        selenium.setSpeed(speed);
    }

    protected void selectWindow(String windowName) {
        selenium.selectWindow(windowName);
    }

    protected void selectWindowContaining(String windowName) {
        String[] windowNames = selenium.getAllWindowNames();
        for (int i = 0, s = windowNames.length; i < s; i++) {
            if (windowNames[i] != null && windowNames[i].contains(windowName)) {
                selenium.selectWindow(windowNames[i]);
                break;
            }
        }
    }

    protected void uncheck(String locator) {
        selenium.uncheck(locator);
    }
    
    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, "");
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        ITUtil.waitAndClick(selenium, locator, message);
    }

    protected String waitForDocId() throws InterruptedException {
        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        return selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR);
    }

    protected void waitAndType(String elementLocator, String text) throws InterruptedException {
        waitAndType(elementLocator, text, "");
    }

    protected void waitAndType(String elementLocator, String text, String message) throws InterruptedException {
        ITUtil.waitAndType(selenium, elementLocator, text, message);
    }

    protected void waitNotVisible(String visibleLocator) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 15) {
                Assert.fail("timeout");
            }

            if (!isVisible(visibleLocator)) {
                break;
            }

            Thread.sleep(1000);
        }
    }

    protected void waitIsVisible(String visibleLocator) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 15) {
                Assert.fail("timeout");
            }
            if (isVisible(visibleLocator)) {
                break;
            }
            Thread.sleep(1000);
        }
    }

    protected void waitForElementPresent(String locator) throws InterruptedException {
        waitForElementPresent(locator, "");
    }

    // TODO this should be message first
    protected void waitForElementPresent(String locator, String message) throws InterruptedException {
        ITUtil.waitForElement(selenium, locator, message);
    }
    
    protected void waitForElementVisible(String locator, String message) throws InterruptedException {
        ITUtil.waitForElementVisible(selenium, locator, message);
    }

    protected void waitForPageToLoad() {
        waitForPageToLoad(ITUtil.DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
    }

    protected void waitForPageToLoad50000() {
        waitForPageToLoad("50000");
    }

    protected void waitForPageToLoad(String number) {
        selenium.waitForPageToLoad(number);
    }
    
    protected void waitForTitleToEqualKualiPortalIndex() throws InterruptedException {
        waitForTitleToEqualKualiPortalIndex("");
    }

    protected void waitForTitleToEqualKualiPortalIndex(String message) throws InterruptedException {
        ITUtil.waitForTitleToEqual(selenium, ITUtil.KUALI_PORTAL_TITLE, message);
    }

    protected void windowFocus() {
        selenium.windowFocus();
    }

    /**
     * Useful to set -Dremote.driver.dontTearDown=f  -Dremote.driver.dontTearDown=n to not shutdown the browser when
     * working on tests.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (ITUtil.dontTearDownPropertyNotSet()) {
            selenium.stop();
            driver.quit(); // TODO not tested with chrome, the service stop might need this check too
        }
    }
}
