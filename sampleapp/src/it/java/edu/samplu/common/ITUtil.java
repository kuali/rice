package edu.samplu.common;

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;

/**
 * Common selenium test methods that should be reused rather than recreated for each test.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ITUtil {

    /**
     * In order to run as a smoke test the ability to set the baseUrl via the JVM arg remote.public.url is required.
     * Trailing slashes are trimmed.  If the remote.public.url does not start with http:// it will be added.
     * @return http://localhost:8080 by default else the value of remote.public.url
     */
    public static String getBaseUrlString() {
        String baseUrl = System.getProperty("remote.public.url");
        if (baseUrl == null) {
            baseUrl = "http://localhost:8080";
        } else if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else if (!baseUrl.startsWith("http")) {
            baseUrl = "http://" + baseUrl;
        }
        return baseUrl;
    }

    /**
     * If the JVM arg remote.autologin is set, auto login as admin will not be done.
     * @param selenium to login with
     */
    public static void login(Selenium selenium) {
        if (System.getProperty("remote.autologin") == null) {
            Assert.assertEquals("Login", selenium.getTitle());
            selenium.type("__login_user", "admin");
            selenium.click("//input[@value='Login']");
            selenium.waitForPageToLoad("30000");
        }
    }

    /**
     * Setting the JVM arg remote.driver.dontTearDown to y or t leaves the browser window open when the test has completed.  Valuable when debugging, updating, or creating new tests.
     * When implementing your own tearDown method rather than an inherited one, it is a common courtesy to include this check and not stop and shutdown the browser window to make it easy debug or update your test.
     * {@code }
     * @return true if the dontTearDownProperty is not set.
     */
    public static boolean dontTearDownPropertyNotSet() {
        return System.getProperty("remote.driver.dontTearDown") == null ||
                "f".startsWith(System.getProperty("remote.driver.dontTearDown").toLowerCase()) ||
                "n".startsWith(System.getProperty("remote.driver.dontTearDown").toLowerCase());
    }
}
