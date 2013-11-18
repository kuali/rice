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
package org.kuali.rice.testtools.selenium;

import org.junit.Assert;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.common.JiraAwareFailureUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * <p>
 * Jira Aware Automated Functional Test Base.
 * </p><p>
 * <ul>
 *     <li>{@see JiraAwareWebDriverUtils}</li>
 *     <li>{@see JiraAwareFailable}</li>
 *     <li>{@see JiraAwareFailure}</li>
 * </ul>
 * TODO: promote the various jiraAware methods from WebDriverLegacyITBase
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class JiraAwareAftBase extends AutomatedFunctionalTestBase implements JiraAwareFailable {

    /**
     * Test state, used for Saucelabs REST API call to set test state via @{see SauceLabsWebDriverHelper#tearDown}.
     */
    private boolean passed = false;

    /**
     * Implement to check for Incident Report or other on screen errors, should call {@see JiraAwareFailable#fail} to fail,
     * without calling any of the jiraAwareFail methods to avoid an infinite loop.
     *
     * @param locator used in failure message if there is an incident report can be blank
     * @param message used in failure message if there is an incident report can be blank
     */
    protected abstract void checkForIncidentReport(String locator, String message);

    /**
     * WebDriver used in fail and pass to display jGrowl messages.
     *
     * @return WebDriver used to display jGrowl messages on fail and pass
     */
    protected abstract WebDriver getDriver();

    /**
     * {@see WebDriverUtils#assertButtonDisabledByText}
     *
     * @param buttonText of button to assert is disabled
     */
    protected void assertButtonDisabledByText(String buttonText) {
        JiraAwareWebDriverUtils.assertButtonDisabledByText(getDriver(), buttonText, this);
    }

    /**
     * {@see WebDriverUtils.assertButtonEnabledByText}.
     *
     * @param buttonText of button to assert is disabled
     */
    protected void assertButtonEnabledByText(String buttonText) {
        JiraAwareWebDriverUtils.assertButtonEnabledByText(getDriver(), buttonText, this);
    }

    protected void assertElementPresentByName(String name) {
        assertElementPresentByName(name, "");
    }

    protected void assertElementPresentByName(String name, String message) {
        try {
            findElement(By.name(name));
        } catch (Exception e) {
            jiraAwareFail(name + " not present " + message);
        }
    }

    protected void assertElementPresentByXpath(String locator) {
        assertElementPresentByXpath(locator, "");
    }

    protected void assertElementPresentByXpath(String locator, String message) {
        try {
            findElement(By.xpath(locator));
        } catch (Exception e) {
            jiraAwareFail(By.xpath(locator), message, e);
        }
    }

    protected void assertElementPresentByLinkText(String linkText) {
        try {
            findElement(By.linkText(linkText));
        } catch (Exception e) {
            jiraAwareFail(By.cssSelector(linkText), "", e);
        }

    }

    protected void assertElementPresent(String locator) {
        try {
            findElement(By.cssSelector(locator));
        } catch (Exception e) {
            jiraAwareFail(By.cssSelector(locator), "", e);
        }
    }

    protected void assertEquals(boolean expected, boolean actual) {
        if (expected != actual) {
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead");
        }
    }

    protected void assertEquals(int expected, int actual) {
        if (expected != actual) {
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead");
        }
    }

    protected void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead " + message);
        }
    }


    protected void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead");
        }
    }

    /**
     * If booleanToAssertFalse is true call {@see jiraAwareFail}.
     *
     * @param booleanToAssertFalse
     */
    protected void assertFalse(boolean booleanToAssertFalse) {
        JiraAwareWebDriverUtils.assertFalse(booleanToAssertFalse, this);
    }

    /**
     * If booleanToAssertFalse is true call {@see jiraAwareFail}.
     *
     * @param message to include if booleanToAssertTrue is true
     * @param booleanToAssertFalse
     */
    protected void assertFalse(String message, boolean booleanToAssertFalse) {
        JiraAwareWebDriverUtils.assertFalse(message, booleanToAssertFalse, this);
    }

    protected void assertIsVisible(String locator) {
        if (!isVisible(locator)) {
            jiraAwareFail(locator + " is not visible and should be");
        }
    }

    protected void assertIsVisible(By by, String message) {
        if (!isVisible(by)) {
            jiraAwareFail(by + " not visible " + message);
        }
    }

    protected void assertIsVisibleById(String id) {
        if (!isVisibleById(id)) {
            jiraAwareFail(id + " is not visible and should be");
        }
    }

    protected void assertIsVisibleByXpath(String xpath, String message) {
        if (!isVisibleByXpath(xpath)) {
            jiraAwareFail(xpath + " not visible " + message);
        }
    }

    protected void assertIsNotVisible(By by) {
        assertIsNotVisible(by, "");
    }

    protected void assertIsNotVisible(By by, String message) {
        if (isVisible(by)) {
            jiraAwareFail(by + " is visible and should not be " + message);
        }
    }

    protected void assertIsNotVisible(String locator) {
        if (isVisible(locator)) {
            jiraAwareFail(locator + " is visible and should not be");
        }
    }

    protected void assertIsNotVisibleByXpath(String xpath) {
        if (isVisible(By.xpath(xpath))) {
            jiraAwareFail(xpath + " is visible and should not be");
        }
    }

    protected void assertIsNotVisibleByXpath(String xpath, String message) {
        if (isVisibleByXpath(xpath)) {
            jiraAwareFail(xpath + " visible and should not be " + message);
        }
    }

    /**
     * <b>WARNING:</b> this only does a check against the page source.  The form url can have random character that match
     * simple text.  A narrowly scoped locator for {@see #assertTextPresent(String String String)}
     *
     * @param text
     */
    protected void assertTextPresent(String text) {
        assertTextPresent(text, "");
    }

    /**
     * <b>WARNING:</b> this only does a check against the page source.  The form url can have random character that match simple text
     * @param text
     */
    protected void assertTextPresent(String text, String message) {
        String pageSource = getDriver().getPageSource();
        if (!pageSource.contains(text)) {
            jiraAwareFail(text + " not present " + message);
        }
    }

    /**
     * @param text
     */
    protected void assertTextPresent(String text, String cssSelector, String message){
        WebElement element = findElement(By.cssSelector(cssSelector));
        if (!element.getText().contains(text)){
            jiraAwareFail(text + " for " + cssSelector + " not present " + message);
        }
    }

    /**
     * Asset that the given text does not occur in the page
     * Warning, this only does a check against the page source.  The form url can have random character that match simple text
     * @param text the text to search for
     */
    protected void assertTextNotPresent(String text) {
        assertTextNotPresent(text, "");
    }

    /**
     * Assert that the given text does not occur in the page, and add an additional message to the failure
     * @param text the text to search for
     * @param message the message to add to the failure
     */
    protected void assertTextNotPresent(String text, String message) {
        if (getDriver().getPageSource().contains(text)) {
            jiraAwareFail(text + " is present and should not be " + message);
        }
    }

    /**
     * @param text
     */
    protected void assertTextNotPresent(String text, String cssSelector, String message){
        WebElement element = findElement(By.cssSelector(cssSelector));
        if (element.getText().contains(text)){
            jiraAwareFail(text + " for " + cssSelector + " is present and shouldn't be " + message);
        }
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail}.
     *
     * @param booleanToAssertTrue
     */
    protected void assertTrue(boolean booleanToAssertTrue) {
        JiraAwareWebDriverUtils.assertTrue(booleanToAssertTrue, this);
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail}.
     *
     * @param message to include if booleanToAssertTrue is false
     * @param booleanToAssertTrue
     */
    protected void assertTrue(String message, boolean booleanToAssertTrue) {
        JiraAwareWebDriverUtils.assertTrue(message, booleanToAssertTrue, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Set passed to false, call jGrowl sticky with the given message, then fails using  {@see JiraAwareFailable#fail}.
     * </p>
     * @param message to display with failure
     */
    @Override
    public void fail(String message) {
        passed = false;
        WebDriverUtils.jGrowl(getDriver(), "Failure " + getClass().getSimpleName(), true, message);
        Assert.fail(message); // The final fail that JiraAwareFailure calls, do not change this to a JiraAwareFailure.
    }

    /**
     * {@see WebDriverUtils#findElement}.
     *
     * @param by to find element with
     * @return WebElement found with given by
     */
    protected WebElement findElement(By by) {
        try {
            return WebDriverUtils.findElement(getDriver(), by);
        } catch (Exception e) {
            jiraAwareFail(by.toString(), e.getMessage(), e);
        }
        return null; // required by compiler, never reached
    }

    protected WebElement findElement(By by, WebElement elementToFindOn) {
        try {
            WebElement found = elementToFindOn.findElement(by);
            WebDriverUtils.highlightElement(getDriver(), found);
            return found;
        } catch (Exception e) {
            jiraAwareFail(by.toString(), e.getMessage(), e);
        }
        return null; // required by compiler, never reached
    }

    protected boolean isVisible(String locator) {
        return isVisible(By.cssSelector(locator));
    }

    protected boolean isVisible(By by) {
        List<WebElement> elements = getDriver().findElements(by);
        for (WebElement element: elements) {
            if (element.isDisplayed()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isVisibleById(String id) {
        return isVisible(By.id(id));
    }

    protected boolean isVisibleByXpath(String locator) {
        return isVisible(By.xpath(locator));
    }

    /**
     * {@inheritDoc}
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param message to check for a Jira match and fail with.
     */
    @Override
    public void jiraAwareFail(String message) {
        checkForIncidentReport("", message);
        JiraAwareFailureUtils.fail(message, this);
    }

    /**
     * {@inheritDoc}
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     */
    @Override
    public void jiraAwareFail(String contents, String message) {
        checkForIncidentReport(contents, message);
        JiraAwareFailureUtils.fail(contents, message, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param by to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     */
    protected void jiraAwareFail(By by, String message, Throwable throwable) {
        jiraAwareFail(by.toString(), message, throwable);
    }

    /**
     * {@inheritDoc}
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     */
    @Override
    public void jiraAwareFail(String contents, String message, Throwable throwable) {
        checkForIncidentReport(contents, message);
        JiraAwareFailureUtils.fail(contents, message, throwable, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     * @param failable to call fail on
     */
    protected void jiraAwareFail(String contents, String message, Throwable throwable, JiraAwareFailable failable) {
        checkForIncidentReport(contents, message);
        JiraAwareFailureUtils.fail(contents, message, throwable, failable);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param by to click on
     * @param message on failure
     * @throws InterruptedException
     */
    protected void jiraAwareWaitAndClick(By by, String message) throws InterruptedException {
        jiraAwareWaitAndClick(by, message, this);
    }

    /**
     * {@see #jiraAwareWaitFor}
     *
     * @param by to click on
     * @param message on failure
     * @param failable to fail on if not found
     * @throws InterruptedException
     */
    protected void jiraAwareWaitAndClick(By by, String message, JiraAwareFailable failable) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message, failable);
            WebElement element = findElement(by);
            element.click();
        } catch (Exception e) {
            failable.jiraAwareFail(by.toString(), message, e);
        }
    }

    /**
     * {@see WebDriverUtils#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @return WebElement found with given by
     * @throws InterruptedException
     */
    protected WebElement jiraAwareWaitFor(By by, String message) throws InterruptedException {
        try {
            return WebDriverUtils.waitFor(getDriver(), WebDriverUtils.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
        return null; // required, but the jiraAwareFail will will end test before this statement is reached
    }

    /**
     * {@see WebDriverUtils#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @throws InterruptedException
     */
    protected void jiraAwareWaitFors(By by, String message) throws InterruptedException {
        try {
            WebDriverUtils.waitFors(getDriver(), WebDriverUtils.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
    }

    /**
     * {@see WebDriverUtils#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @param failable to fail if given by is not found
     * @throws InterruptedException
     */
    protected void jiraAwareWaitFor(By by, String message, JiraAwareFailable failable) throws InterruptedException {
        try {
            WebDriverUtils.waitFor(getDriver(), WebDriverUtils.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by.toString(), message, t, failable);
        }
    }

    /**
     * {@see WebDriverUtils#waitFor}.
     *
     * @param by to find
     * @param seconds to wait
     * @param message on failure
     * @return WebElement found with given by
     * @throws InterruptedException
     */
    protected WebElement jiraAwareWaitFor(By by, int seconds, String message) throws InterruptedException {
        try {
            return WebDriverUtils.waitFor(getDriver(), seconds, by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
        return null; // required, but the jiraAwareFail will will end test before this statement is reached
    }

    /**
     * @return passed
     */
    public boolean isPassed() {
        return passed;
    }

    protected void selectOptionByName(String name, String optionValue) throws InterruptedException {
        selectOption(By.name(name), optionValue);
    }

    protected void selectOptionByXpath(String locator, String optionValue) throws InterruptedException {
        selectOption(By.name(locator), optionValue);
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param by
     * @param optionValue
     * @throws InterruptedException
     */
    protected void selectOption(By by, String optionValue) throws InterruptedException {
        WebElement select1 = findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));

        if (options == null || options.size() == 0) {
            jiraAwareFail("No options for select "
                    + select1.toString()
                    + " was looking for value "
                    + optionValue
                    + " using "
                    + by.toString());
        }

        for (WebElement option : options) {
            if (option.getAttribute("value").equals(optionValue)) {
                option.click();
                break;
            }
        }
    }

    /**
     * <p>
     * Set the test state to passed, call jGrowl sticky with success, required to be called at the conclusion of a test
     * for the saucelabs state of a test to be updated to passed.
     * </p>
     */
    protected void passed() {
        passed = true;
        WebDriverUtils.jGrowl(getDriver(), "Success " + getClass().getSimpleName(), true, "Passed");
    }

    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message);
            WebElement element = findElement(by);
            WebDriverUtils.highlightElement(getDriver(), element);
            element.sendKeys(text);
        } catch (Exception e) {
            JiraAwareFailureUtils.failOnMatchedJira(by.toString(), message, this);
            jiraAwareFail(e.getMessage()
                    + " "
                    + by.toString()
                    + "  unable to type text '"
                    + text
                    + "'  "
                    + message
                    + " current url "
                    + getDriver().getCurrentUrl()
                    + "\n"
                    + AutomatedFunctionalTestUtils.deLinespace(getDriver().getPageSource()));
        }
    }
}
