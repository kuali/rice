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
import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.common.JiraAwareFailureUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * <p>
 * Jira Aware Automated Functional Test Base.
 * </p><p>
 * <ul>
 *     <li>{@see Failable}</li>
 *     <li>{@see JiraAwareFailure}</li>
 * </ul>
 * TODO: promote the various jiraAware methods from WebDriverLegacyITBase
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class JiraAwareAftBase extends AutomatedFunctionalTestBase implements Failable {

    /**
     * Test state, used for Saucelabs REST API call to set test state via @{see SauceLabsWebDriverHelper#tearDown}.
     */
    private boolean passed = false;

    /**
     * Implement to check for Incident Report or other on screen errors, should call {@see Failable#fail} to fail, without
     * calling any of the jiraAwareFail methods to avoid an infinite loop.
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
     * {@see WebDriverUtil#assertButtonDisabledByText}
     *
     * @param buttonText of button to assert is disabled
     */
    protected void assertButtonDisabledByText(String buttonText) {
        WebDriverUtil.assertButtonDisabledByText(getDriver(), buttonText, this);
    }

    /**
     * {@see WebDriverUtil.assertButtonEnabledByText}.
     *
     * @param buttonText of button to assert is disabled
     */
    protected void assertButtonEnabledByText(String buttonText) {
        WebDriverUtil.assertButtonEnabledByText(getDriver(), buttonText, this);
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail}.
     *
     * @param booleanToAssertTrue
     */
    protected void assertTrue(boolean booleanToAssertTrue) {
        if (!booleanToAssertTrue) {
            jiraAwareFail("expected true, but was false");
        }
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail}.
     *
     * @param booleanToAssertTrue
     * @param message to include if booleanToAssertTrue is false
     */
    protected void assertTrue(String message, boolean booleanToAssertTrue) {
        if (!booleanToAssertTrue) {
            jiraAwareFail(message + " expected true, but was false");
        }
    }

    /**
     * <p>
     * Set passed to false, call jGrowl sticky with the given message, then fails using  {@see Failable#fail}.
     * </p>
     * @param message to display with failure
     */
    @Override
    public void fail(String message) {
        passed = false;
        WebDriverUtil.jGrowl(getDriver(), "Failure " + getClass().getSimpleName(), true, message);
        Assert.fail(message); // The final fail that JiraAwareFailure calls, do not change this to a JiraAwareFailure.
    }

    /**
     * {@see WebDriverUtil#findElement}.
     *
     * @param by to find element with
     * @return WebElement found with given by
     */
    protected WebElement findElement(By by) {
        return WebDriverUtil.findElement(getDriver(), by);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtil#fail}.
     *
     * @param message to check for a Jira match and fail with.
     */
    protected void jiraAwareFail(String message) {
        checkForIncidentReport("", message);
        JiraAwareFailureUtil.fail(message, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtil#fail}.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     */
    protected void jiraAwareFail(String contents, String message) {
        checkForIncidentReport(contents, message);
        JiraAwareFailureUtil.fail(contents, message, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtil#fail}.
     *
     * @param by to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     */
    protected void jiraAwareFail(By by, String message, Throwable throwable) {
        checkForIncidentReport(by.toString(), message);
        JiraAwareFailureUtil.fail(by.toString(), message, throwable, this);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtil#fail}.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     * @param failable to call fail on
     */
    protected void jiraAwareFail(String contents, String message, Throwable throwable, Failable failable) {
        checkForIncidentReport(contents, message);
        JiraAwareFailureUtil.fail(contents, message, throwable, failable);
    }

    /**
     * {@see #checkForIncidentReport} and {@see JiraAwareFailureUtil#fail}.
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
    protected void jiraAwareWaitAndClick(By by, String message, Failable failable) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message, failable);
            WebElement element = findElement(by);
            element.click();
        } catch (Exception e) {
//            jiraAwareFail(by, message, e);
        }
    }

    /**
     * {@see WebDriverUtil#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @return WebElement found with given by
     * @throws InterruptedException
     */
    protected WebElement jiraAwareWaitFor(By by, String message) throws InterruptedException {
        try {
            return WebDriverUtil.waitFor(getDriver(), WebDriverUtil.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
        return null; // required, but the jiraAwareFail will will end test before this statement is reached
    }

    /**
     * {@see WebDriverUtil#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @throws InterruptedException
     */
    protected void jiraAwareWaitFors(By by, String message) throws InterruptedException {
        try {
            WebDriverUtil.waitFors(getDriver(), WebDriverUtil.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
    }

    /**
     * {@see WebDriverUtil#waitFor}.
     *
     * @param by to find
     * @param message on failure
     * @param failable to fail if given by is not found
     * @throws InterruptedException
     */
    protected void jiraAwareWaitFor(By by, String message, Failable failable) throws InterruptedException {
        try {
            WebDriverUtil.waitFor(getDriver(), WebDriverUtil.configuredImplicityWait(), by, message);
        } catch (Throwable t) {
            jiraAwareFail(by.toString(), message, t, failable);
        }
    }

    /**
     * {@see WebDriverUtil#waitFor}.
     *
     * @param by to find
     * @param seconds to wait
     * @param message on failure
     * @return WebElement found with given by
     * @throws InterruptedException
     */
    protected WebElement jiraAwareWaitFor(By by, int seconds, String message) throws InterruptedException {
        try {
            return WebDriverUtil.waitFor(getDriver(), seconds, by, message);
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

    /**
     * <p>
     * Set the test state to passed, call jGrowl sticky with success, required to be called at the conclusion of a test
     * for the saucelabs state of a test to be updated to passed.
     * </p>
     */
    protected void passed() {
        passed = true;
        WebDriverUtil.jGrowl(getDriver(), "Success " + getClass().getSimpleName(), true, "Passed");
    }
}
