package org.kuali.rice.testtools.selenium;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.openqa.selenium.WebDriver;

/**
 * All asserts call {@see JiraAwareFailable#jiraAwareFail} on failure.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class JiraAwareWebDriverUtils {

    /**
     * If booleanToAssertFalse is true call {@see JiraAwareFailable#jiraAwareFail}.
     *
     * @param booleanToAssertFalse to assert is false
     * @param failable to call jiraAwareFail on if booleanToAssertFalse is true
     */
    public static void assertFalse(boolean booleanToAssertFalse, JiraAwareFailable failable) {
        if (booleanToAssertFalse) {
            failable.jiraAwareFail("expected false, but was true");
        }
    }

    /**
     * If booleanToAssertFalse is true call {@see jiraAwareFail}.
     *
     * @param message to include if booleanToAssertFalse is true
     * @param booleanToAssertFalse
     */
    public static void assertFalse(String message, boolean booleanToAssertFalse, JiraAwareFailable failable) {
        if (booleanToAssertFalse) {
            failable.jiraAwareFail(message + " expected false, but was true");
        }
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail}.
     *
     * @param booleanToAssertTrue
     */
    public static void assertTrue(boolean booleanToAssertTrue, JiraAwareFailable failable) {
        if (!booleanToAssertTrue) {
            failable.jiraAwareFail("expected true, but was false");
        }
    }

    /**
     * If booleanToAssertTrue is false call {@see jiraAwareFail} with the given message.
     *
     * @param message to include if booleanToAssertTrue is false
     * @param booleanToAssertTrue
     * @param failable
     */
    public static void assertTrue(String message, boolean booleanToAssertTrue, JiraAwareFailable failable) {
        if (!booleanToAssertTrue) {
            failable.jiraAwareFail(message + " expected true, but was false");
        }
    }

    /**
     * <p>
     * Fail if the button defined by the buttonText is enabled.
     * </p>
     *
     * @param driver to get the button from
     * @param buttonText to identify the button
     * @param failable to fail on if button identified by buttonText is enabled.
     */
    public static void assertButtonDisabledByText(WebDriver driver, String buttonText, JiraAwareFailable failable) {
        WebDriverUtil.jGrowl(driver, "Assert", false, "Assert " + buttonText + " button is disabled");
        if (WebDriverUtil.findButtonByText(driver, buttonText).isEnabled()) {
            failable.jiraAwareFail(buttonText + " button is not disabled");
        }
    }

    /**
     * <p>
     * Fail if the button defined by the buttonText is disabled.
     * </p>
     *
     * @param driver to get the button from
     * @param buttonText to identify the button
     * @param failable to fail on if button identified by buttonText is disabled.
     */
    public static void assertButtonEnabledByText(WebDriver driver, String buttonText, JiraAwareFailable failable) {
        WebDriverUtil.jGrowl(driver, "Assert", false, "Assert " + buttonText + " button is enabled");
        if (!WebDriverUtil.findButtonByText(driver, buttonText).isEnabled()) {
            failable.jiraAwareFail(buttonText + " button is not enabled");
        }
    }
}
