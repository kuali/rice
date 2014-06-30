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

    protected void assertDataTableContains(String[][] data) throws InterruptedException {
        boolean dataPresent = true;
        String missingMessage = "";
        String dataTableRow;
        for (int i = 0, s = data.length; i < s; i++) {
            dataTableRow = findDataTableRow(data[i][0]).getText();
            for (int j = 1, t = data[i].length; j < t; j++) {
                if (!dataTableRow.contains(data[i][j])) {
                    dataPresent = false;
                    missingMessage += data[i][j] + " not present in data table row containing " + data[i][0] + ". ";
                }
            }
            WebDriverUtils.jGrowl(getDriver(), "Assert DataTable Row", false, "Assert datatable row '" + dataTableRow
                    + "' contains '" + data[i] + "' " + dataPresent);
        }
        if (!dataPresent) {
            jiraAwareFail(missingMessage);
        }
    }

    protected void assertDataTableContains(String[][] data, String tableClass) throws InterruptedException {
        boolean dataPresent = true;
        String missingMessage = "";
        String dataTableRow;
        for (int i = 0, s = data.length; i < s; i++) {
            dataTableRow = findDataTableRow(data[i][0], tableClass).getText();
            for (int j = 1, t = data[i].length; j < t; j++) {
                if (!dataTableRow.contains(data[i][j])) {
                    dataPresent = false;
                    missingMessage += data[i][j] + " not present in data table row containing " + data[i][0] + ". ";
                }
            }
        }
        if (!dataPresent) {
            jiraAwareFail(missingMessage);
        }
    }

    protected void assertElementPresentByName(String name) {
        assertElementPresentByName(name, this.getClass().toString());
    }

    protected void assertElementPresentByName(String name, String message) {
        try {
            findElement(By.name(name));
        } catch (Throwable t) {
            jiraAwareFail(name + " not present " + message);
        }
    }

    protected void assertElementPresentByXpath(String locator) {
        assertElementPresentByXpath(locator, this.getClass().toString());
    }

    protected void assertElementPresent(By by) {
        assertElementPresent(by, this.getClass().toString());
    }

    protected void assertElementPresent(By by, String message) {
        try {
            findElement(by);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
    }

    protected void assertElementPresentByXpath(String locator, String message) {
        try {
            findElement(By.xpath(locator));
        } catch (Throwable t) {
            jiraAwareFail(By.xpath(locator), message, t);
        }
    }

    protected void assertElementPresentByLinkText(String linkText) {
        try {
            findElement(By.linkText(linkText));
        } catch (Throwable t) {
            jiraAwareFail(By.cssSelector(linkText), this.getClass().toString(), t);
        }

    }

    protected void assertElementPresent(String locator) {
        try {
            findElement(By.cssSelector(locator));
        } catch (Throwable t) {
            jiraAwareFail(By.cssSelector(locator), this.getClass().toString(), t);
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
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead in " + getClass().toString());
        }
    }

    protected void assertEquals(String message, String expected, String actual) {
        if (!expected.equals(actual)) {
            jiraAwareFail("Expected \"" + expected + "\" but saw \"" + actual + "\" instead " + message);
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
        assertIsNotVisible(by, this.getClass().toString());
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

    protected void assertLabeledTextNotPresent(String[][] labeledText) {
        boolean allLabeledTextNotPresent = true;
        String missingMessage = "";
        for (int i = 0, s = labeledText.length; i < s; i++) {
            if (isLabeledTextPresent(labeledText[i][0], labeledText[i][1])) {
                allLabeledTextNotPresent = false;
                missingMessage += "Text: " + labeledText[i][1] + " labeled by: " + labeledText[i][0] + " present. ";
            }
        }
        if (!allLabeledTextNotPresent) {
            jiraAwareFail(missingMessage);
        }
    }

    protected void assertLabeledTextPresent(String[][] labeledText) {
        boolean allLabeledTextPresent = true;
        String missingMessage = "";
        for (int i = 0, s = labeledText.length; i < s; i++) {
            if (!isLabeledTextPresent(labeledText[i][0], labeledText[i][1])) {
                allLabeledTextPresent = false;
                missingMessage += "Text: " + labeledText[i][1] + " labeled by: " + labeledText[i][0] + " not present. ";
            }
        }
        if (!allLabeledTextPresent) {
            jiraAwareFail(missingMessage);
        }
    }

    protected void assertLabeledTextPresent(String label, String text) {
        if (!isLabeledTextPresent(label, text)) {
            jiraAwareFail("Text: " + text + " labeled by: " + label + " not present");
        }
    }

    protected void assertResultCount(String count) throws InterruptedException {
        jiraAwareWaitFor(By.cssSelector("div.dataTables_info"), "result count for " + this.getClass().toString());
        assertTextPresent("of " + count + " entries", "div.dataTables_info", this.getClass().toString());
    }

    /**
     * <b>WARNING:</b> this only does a check against the page source.  The form url can have random character that match
     * simple text.  A narrowly scoped locator for {@see #assertTextPresent(String String String)}
     *
     * @param text
     */
    protected void assertTextPresent(String text) {
        assertTextPresent(text, this.getClass().toString());
    }

    /**
     * <b>WARNING:</b> this only does a check against the page source.  The form url can have random character that match simple text
     * @param text
     */
    protected void assertTextPresent(String text, String message) {
        WebDriverUtils.jGrowl(getDriver(), "Assert Text Present", false, "Assert text '" + text + "' is present.");
        String pageSource = getDriver().getPageSource();
        if (!pageSource.contains(text)) {
            jiraAwareFail(text + " not present " + message);
        }
        WebDriverUtils.highlightElement(getDriver(), By.xpath("//*[contains(text(), '" + text + "')]"));
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
        assertTextNotPresent(text, this.getClass().toString());
    }

    /**
     * Assert that the given text does not occur in the page, and add an additional message to the failure
     * @param text the text to search for
     * @param message the message to add to the failure
     */
    protected void assertTextNotPresent(String text, String message) {
        String contents = getDriver().getPageSource();
        if (contents.contains(text)) {
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
        JiraAwareWebDriverUtils.assertTrue(getClass().toString(), booleanToAssertTrue, this);
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

    protected WebElement findDataTableRow(String keyText) throws InterruptedException {
        return findDataTableRow(keyText, "dataTable");
    }

    protected WebElement findDataTableRow(String keyText, String className) throws InterruptedException {
        jiraAwareWaitFor(By.className(className));
        WebElement element = findElement(By.className(className));
        return findElement(By.xpath("./*/tr//*[contains(text(), '" + keyText + "')]/ancestor::tr"), element);
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
        } catch (Throwable t) {
            jiraAwareFail(by.toString(), t.getMessage(), t);
        }
        return null; // required by compiler, never reached
    }

    protected WebElement findElement(By by, WebElement elementToFindOn) {
        try {
            WebElement found = elementToFindOn.findElement(by);
            WebDriverUtils.highlightElement(getDriver(), found);
            return found;
        } catch (Throwable t) {
            jiraAwareFail(by.toString(), t.getMessage() + " " + this.getClass().toString(), t);
        }
        return null; // required by compiler, never reached
    }

    protected boolean isLabeledTextPresent(String label, String text) {
        WebElement element = findElement(By.xpath("//tr/th/label[contains(text(), '" + label + "')]/ancestor::tr/td"));
        String labeledText = element.getText().trim();
        WebDriverUtils.jGrowl(getDriver(), "Is Labeled Text Present", false, "Is text '" + text + "' present for label '" + label + "'? " + labeledText.contains(text));
        return labeledText.contains(text);
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
        jiraAwareFail("", message, null, this);
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
        jiraAwareFail(contents, message, null, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtils#fail}.
     *
     * @param by to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     */
    protected void jiraAwareFail(By by, String message, Throwable throwable) {
        jiraAwareFail(by.toString(), message, throwable, this);
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
        jiraAwareFail(contents, message, throwable, this);
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
        if (!contents.startsWith("\nIncident report") && !message.startsWith("\nIncident report")) {
            String errorMessage = AutomatedFunctionalTestUtils.incidentReportMessage(getDriver().getPageSource(), "", message);
            if (errorMessage != null) {
                JiraAwareFailureUtils.failOnMatchedJira(errorMessage, message, failable);
                JiraAwareFailureUtils.fail(errorMessage, message, throwable, failable);
            }
        }
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

    protected WebElement jiraAwareType(By by, String text) {
        return jiraAwareType(by, text, this.getClass().toString().replace("class ", ""));
    }

    protected WebElement jiraAwareType(By by, String text, String failureMessage) {
        try {
            return type(by, text);
        } catch (Throwable t) {
            JiraAwareFailureUtils.failOnMatchedJira(by.toString(), failureMessage, this);
            jiraAwareFail(t.getMessage()
                    + " "
                    + by.toString()
                    + "  unable to type text '"
                    + text
                    + "'  "
                    + failureMessage
                    + " current url "
                    + getDriver().getCurrentUrl()
                    + "\n"
                    + AutomatedFunctionalTestUtils.deLinespace(getDriver().getPageSource()));
        }
        return null;
    }

    protected WebElement jiraAwareTypeByName(String name, String text) {
        return jiraAwareType(By.name(name), text, this.getClass().toString().replace("class ", ""));
    }

    protected WebElement jiraAwareTypeByName(String name, String text, String failureMessage) {
        return jiraAwareType(By.name(name), text, failureMessage);
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
            // possible future code of outputting clicked components in a more generic way, but need to look into duplicates, don't delete
//            String jgrowl = element.getAttribute("name");
//            if (jgrowl == null || "".equals(jgrowl)) {
//                jgrowl = element.getAttribute("id");
//            }
//            if (jgrowl == null || "".equals(jgrowl)) {
//                jgrowl = by.toString();
//            }
//            WebDriverUtils.jGrowl(getDriver(), "Click " + jgrowl, false, "Click " + jgrowl);
            element.click();
        } catch (Throwable t) {
            failable.jiraAwareFail(by.toString(), message, t);
        }
    }

    /**
     * {@see WebDriverUtils#waitFor}.
     *
     * @param by to find
     * @return WebElement found with given by
     * @throws InterruptedException
     */
    protected WebElement jiraAwareWaitFor(By by) throws InterruptedException {
        return jiraAwareWaitFor(by, this.getClass().toString());
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
            jiraAwareFail(by, message + " " + this.getClass().toString(), t);
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

        String name = select1.getAttribute("name");

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
                WebDriverUtils.jGrowl(getDriver(), "Select " + option.getText(), false, "Select " + option.getText() + " from " + name);
                option.click();
                break;
            }
        }
    }

    private WebElement type(By by, String text) {
        WebElement element = findElement(by);
        String name = element.getAttribute("name");
        WebDriverUtils.jGrowl(getDriver(), "Type", false, "Type into " + name + " the text: " + text);
        WebDriverUtils.highlightElement(getDriver(), element);
        element.sendKeys(text);
        return element;
    }

    private WebElement typeByName(String name, String text) {
        return type(By.name(name), text);
    }

    /**
     * <p>
     * Set the test state to passed, call jGrowl sticky with success, required to be called at the conclusion of a test
     * for the saucelabs state of a test to be updated to passed.
     * </p>
     */
    protected void passed() {
        if (passed == true) {
            WebDriverUtils.jGrowl(getDriver(), "Passed has been called more than once " + getClass().getSimpleName(), true, "Passed");
        }
        passed = true;
        WebDriverUtils.jGrowl(getDriver(), "Success " + getClass().getSimpleName(), true, "Passed");
    }

    protected WebElement waitAndType(By by, String text, String message) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message);
            return type(by, text);
        } catch (Throwable t) {
            JiraAwareFailureUtils.failOnMatchedJira(by.toString(), message, this);
            jiraAwareFail(t.getMessage()
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
        return null;
    }
}
