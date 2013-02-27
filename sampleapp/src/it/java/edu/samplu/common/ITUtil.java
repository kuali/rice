/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Common selenium test methods that should be reused rather than recreated for each test.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ITUtil {

    public static final String KUALI_PORTAL_TITLE = "Kuali Portal Index";
    public static final String DEFAULT_BASE_URL = "http://localhost:8080/kr-dev";
    public final static String PORTAL = "/portal.do";
    public final static String PORTAL_URL =  ITUtil.getBaseUrlString() + ITUtil.PORTAL;
    public final static String PORTAL_URL_ENCODED = URLEncoder.encode(PORTAL_URL);
    public static final String DTS = Calendar.getInstance().getTimeInMillis() + "";
    public static final String DTS_TWO = Calendar.getInstance().getTimeInMillis() + "" + RandomStringUtils.randomAlphabetic(2).toLowerCase();
    public static String WAIT_TO_END_TEST = "5000";
    public static final String DIV_ERROR_LOCATOR = "//div[@class='error']";
    public static final String DIV_EXCOL_LOCATOR = "//div[@class='msg-excol']";
    public static final int WAIT_DEFAULT_SECONDS = 60;
    public static final String DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT = "30000";
    static Map<String, String> jiraMatches;
    public static final String REMOTE_PUBLIC_URL_PROPERTY = "remote.public.url";
    public static final String REMOTE_AUTOLOGIN_PROPERTY = "remote.autologin";
    public static final String HUB_PROPERTY = "remote.public.hub";
    public static final String HUB_DRIVER_PROPERTY = "remote.public.driver";
    public static final String HUB_URL_PROPERTY = "http://localhost:4444/wd/hub";
    public static final String DONT_TEAR_DOWN_PROPERTY = "remote.driver.dontTearDown";

    static {
        jiraMatches = new HashMap<String, String>();
        jiraMatches.put("Error setting property values; nested exception is org.springframework.beans.NotWritablePropertyException: Invalid property 'refreshWhenChanged' of bean class [org.kuali.rice.krad.uif.element.Action]: Bean property 'refreshWhenChanged' is not writable or has an invalid setter method. Does the parameter type of the setter match the return type of the getter?",
                "KULRICE-8137 Agenda Rule edit Incident report Invalid property 'refreshWhenChanged'");

        jiraMatches.put("org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules(MaintenanceDocumentRuleBase.",
                "KULRICE-8142 NPE in MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules");

        jiraMatches.put("at org.kuali.rice.krad.rules.DocumentRuleBase.isDocumentOverviewValid(DocumentRuleBase.",
                "KULRICE-8134 NPE in DocumentRuleBase.isDocumentOverviewValid(DocumentRuleBase");

        jiraMatches.put("org.kuali.rice.krad.uif.layout.TableLayoutManager.buildLine(TableLayoutManager.",
                "KULRICE-8160 NPE at TableLayoutManager.buildLine(TableLayoutManager");

        jiraMatches.put("Bean property 'configFileLocations' is not writable or has an invalid setter method. Does the parameter type of the setter match the return type of the getter?",
                "KULRICE-8173 Bean property 'configFileLocations' is not writable or has an invalid setter method");

        jiraMatches.put("Bean property 'componentSecurity' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?",
                "KULRICE-8182 JDK7 Bean property 'componentSecurity' is not readable...");

        jiraMatches.put("java.sql.SQLSyntaxErrorException: ORA-00904: \"ROUTEHEADERID\": invalid identifier",
                "KULRICE-8277 Several ITs fail with OJB operation; bad SQL grammar []; nested exception is java.sql.SQLException: ORA-00904: \"ROUTEHEADERID\": invalid identifier");
//        jiraMatches.put("",
//                "");

    }

    /**
     * "FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"
     * @param selenium
     * @param docId
     */
    public static void assertDocFinal(Selenium selenium, String docId) {
        docId= "link=" + docId;
        if(selenium.isElementPresent(docId)){
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }

    protected static String blanketApprovalCleanUpErrorText(String errorText) {
        errorText = errorText.replace("* required field", "").replace("\n", " ").trim(); // bit of extra ui text we don't care about
        return errorText;
    }

    /**
     * Generic blanket approve behavior
     * @param selenium
     * @throws InterruptedException
     */
    public static void blanketApprove(Selenium selenium) throws InterruptedException {
        ITUtil.checkForIncidentReport(selenium, "methodToCall.blanketApprove");
        ITUtil.waitAndClick(selenium, "methodToCall.blanketApprove");
        selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
        Thread.sleep(2000);

        if (selenium.isElementPresent(DIV_ERROR_LOCATOR)) {
            String errorText = selenium.getText(DIV_ERROR_LOCATOR);
            if (errorText != null && errorText.contains("error(s) found on page.")) {
                errorText = blanketApprovalCleanUpErrorText(errorText);
                if (selenium.isElementPresent(DIV_EXCOL_LOCATOR)) { // not present if errors are at the bottom of the page (see left-errmsg below)
                    errorText = blanketApprovalCleanUpErrorText(selenium.getText(DIV_EXCOL_LOCATOR));// + "\n" + selenium.getHtmlSource()); // replacing errorText as DIV_EXCOL_LOCATOR includes the error count
                }
                if (selenium.isElementPresent("//div[@class='left-errmsg-tab']/div/div")) {
                    errorText = errorText + blanketApprovalCleanUpErrorText(selenium.getText("//div[@class='left-errmsg-tab']/div/div"));
                }

                //                if (selenium.isElementPresent("//div[@class='left-errmsg']/div")) {
                //                    errorText = errorText + " " + selenium.getText("//div[@class='left-errmsg']/div/div[1]");
                //                }
                Assert.fail(errorText);
            }
        }
        ITUtil.checkForIncidentReport(selenium, "//img[@alt='doc search']");
        waitAndClick(selenium, "//img[@alt='doc search']");
        selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
        assertEquals("Kuali Portal Index", selenium.getTitle());
        try {
            selenium.selectFrame("iframeportlet");
        } catch (NoSuchFrameException nsfe) {
            // do nothing don't fail on missing frames
        }
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
    }

    /**
     * "//li[@class='uif-errorMessageItem']"
     * @param selenium
     * @param message
     */
    public static void checkErrorMessageItem(Selenium selenium, String message) {
        final String error_locator = "//li[@class='uif-errorMessageItem']";
        if (selenium.isElementPresent(error_locator)) {
            String errorText = selenium.getText(error_locator);
            if (errorText != null && errorText.contains("errors")) {
                Assert.fail(errorText + message);
            }
        }
    }


    /**
     * In order to run as a smoke test the ability to set the baseUrl via the JVM arg remote.public.url is required.
     * Trailing slashes are trimmed.  If the remote.public.url does not start with http:// it will be added.
     * @return http://localhost:8080/kr-dev by default else the value of remote.public.url
     */
    public static String getBaseUrlString() {
        String baseUrl = System.getProperty(REMOTE_PUBLIC_URL_PROPERTY);
        if (baseUrl == null) {
            baseUrl = DEFAULT_BASE_URL;
        }
        baseUrl = prettyHttp(baseUrl);
        return baseUrl;
    }

    /**
     * Append http:// if not present.  Remove trailing /
     * @param baseUrl
     * @return
     */
    public static String prettyHttp(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!baseUrl.startsWith("http")) {
            baseUrl = "http://" + baseUrl;
        }
        return baseUrl;
    }

    /**
     * In order to run as a smoke test under selenium grid the ability to set the hubUrl via the JVM arg remote.public.hub is required.
     * Trailing slashes are trimmed.  If the remote.public.hub does not start with http:// it will be added.
     * @return http://localhost:4444/wd/hub by default else the value of remote.public.hub
     */
    public static String getHubUrlString() {
        String hubUrl = System.getProperty(HUB_PROPERTY);
        if (hubUrl == null) {
            hubUrl = HUB_URL_PROPERTY;
        }
        hubUrl = prettyHttp(hubUrl);
        if (!hubUrl.endsWith("/wd/hub")) {
            hubUrl = hubUrl + "/wd/hub";
        }
        return hubUrl;
    }

    /**
     * remote.public.driver set to chrome or firefox (null assumes firefox)
     * if remote.public.hub is set a RemoteWebDriver is created (Selenium Grid)
     * @return WebDriver or null if unable to create
     */
    public static WebDriver getWebDriver() {
        String driverParam = System.getProperty(HUB_DRIVER_PROPERTY);
        String hubParam = System.getProperty(HUB_PROPERTY);
        if (hubParam == null) {
            if (driverParam == null || "firefox".equalsIgnoreCase(driverParam)) {
                FirefoxProfile profile = new FirefoxProfile();
                profile.setEnableNativeEvents(false);
                return new FirefoxDriver(profile);
            } else if ("chrome".equalsIgnoreCase(driverParam)) {
                return new ChromeDriver();
            } else if ("safari".equals(driverParam)) {
                System.out.println("SafariDriver probably won't work, if it does please contact Erik M.");
                return new SafariDriver();
            }
        } else {
            try {
                if (driverParam == null || "firefox".equalsIgnoreCase(driverParam)) {
                    return new RemoteWebDriver(new URL(ITUtil.getHubUrlString()), DesiredCapabilities.firefox());
                } else if ("chrome".equalsIgnoreCase(driverParam)) {
                    return new RemoteWebDriver(new URL(ITUtil.getHubUrlString()), DesiredCapabilities.chrome());
                }
            } catch (MalformedURLException mue) {
                System.out.println(ITUtil.getHubUrlString() + " " + mue.getMessage());
                mue.printStackTrace();
            }
        }
        return null;
    }

    /**
     * If the JVM arg remote.autologin is set, auto login as admin will not be done.
     * @param selenium to login with
     */
    public static void loginSe(Selenium selenium) {
        loginSe(selenium, "admin");
    }

    /**
     * If the JVM arg remote.autologin is set, auto login as admin will not be done.
     * @param driver
     * @param userName
     * @throws InterruptedException
     */
    public static void login(WebDriver driver, String userName) throws InterruptedException {
        if (System.getProperty(REMOTE_AUTOLOGIN_PROPERTY) == null) {
            driver.findElement(By.name("__login_user")).clear();
            driver.findElement(By.name("__login_user")).sendKeys(userName);
            driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
            Thread.sleep(1000);
            String contents = driver.getPageSource();
            checkForInvalidUserName(userName, contents);
        }
    }

    private static void checkForInvalidUserName(String userName, String contents) {
        if (contents.indexOf("Invalid username") > -1) {
            Assert.fail("Invalid username " + userName);
        }
    }

    /**
     * If the JVM arg remote.autologin is set, auto login as admin will not be done.
     * @param selenium to login with
     */
    public static void loginSe(Selenium selenium, String user) {
        if (System.getProperty(REMOTE_AUTOLOGIN_PROPERTY) == null) {
            try {
                selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
            } catch (Exception e) {
                Assert.fail("Login page not loaded app started?");
            }
            if (!"Login".equals(selenium.getTitle())) {
                fail("Title is not Login as expected, but " + selenium.getTitle());
            }
            selenium.type("__login_user", user);
            selenium.click("//input[@type='submit']"); //using css selector fails
            selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
            String contents = selenium.getHtmlSource();
            checkForInvalidUserName(user, contents);
        }
    }

    /**
     * Write the given stack trace into a String
     * @param throwable whose stack trace to return
     * @return String of the given throwable's stack trace.
     */
    public static String stackTrace(Throwable throwable) {
        StringWriter wrt=new StringWriter();
        PrintWriter pw=new PrintWriter(wrt);
        throwable.printStackTrace(pw);
        pw.flush();
        return wrt.toString();
    }

    /**
     * Setting the JVM arg remote.driver.dontTearDown to y or t leaves the browser window open when the test has completed.  Valuable when debugging, updating, or creating new tests.
     * When implementing your own tearDown method rather than an inherited one, it is a common courtesy to include this check and not stop and shutdown the browser window to make it easy debug or update your test.
     * {@code }
     * @return true if the dontTearDownProperty is not set.
     */
    public static boolean dontTearDownPropertyNotSet() {
        return System.getProperty(DONT_TEAR_DOWN_PROPERTY) == null ||
                "f".startsWith(System.getProperty(DONT_TEAR_DOWN_PROPERTY).toLowerCase()) ||
                "n".startsWith(System.getProperty(DONT_TEAR_DOWN_PROPERTY).toLowerCase());
    }

    /**
     * Wait 60 seconds for the elementLocator to be present or fail.  Click if present
     * @param selenium
     * @param elementLocator
     * @throws InterruptedException
     */
    public static void waitAndClick(Selenium selenium, String elementLocator) throws InterruptedException {
        waitAndClick(selenium, elementLocator, WAIT_DEFAULT_SECONDS);
    }

    /**
     * Wait 60 seconds for the elementLocator to be present or fail.  Click if present
     * @param selenium
     * @param elementLocator
     * @param message
     * @throws InterruptedException
     */
    public static void waitAndClick(Selenium selenium, String elementLocator, String message) throws InterruptedException {
        waitAndClick(selenium, elementLocator, WAIT_DEFAULT_SECONDS, message);
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @throws InterruptedException
     */
    public static void waitAndClick(Selenium selenium, String elementLocator, int seconds) throws InterruptedException {
        waitAndClick(selenium, elementLocator, seconds, "");
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @param message
     * @throws InterruptedException
     */
    public static void waitAndClick(Selenium selenium, String elementLocator, int seconds, String message) throws InterruptedException {
        waitForElement(selenium, elementLocator, seconds, message);
        selenium.click(elementLocator);
        Thread.sleep(1000);
        ITUtil.checkForIncidentReport(selenium, elementLocator, message);
    }

    /**
     * Wait the 60 seconds for the elementLocator to be present or fail, when present type the text.
     * @param selenium
     * @param elementLocator
     * @param text
     * @throws InterruptedException
     */
    public static void waitAndType(Selenium selenium, String elementLocator, String text) throws InterruptedException {
        waitAndType(selenium, elementLocator, text, "");
    }

    /**
     * Wait the 60 seconds for the elementLocator to be present or fail, when present type the text.  Include failure message on fail.
     * @param selenium
     * @param elementLocator
     * @param text
     * @param message
     * @throws InterruptedException
     */
    public static void waitAndType(Selenium selenium, String elementLocator, String text, String message) throws InterruptedException {
        waitAndType(selenium, elementLocator, WAIT_DEFAULT_SECONDS, text, message);
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail, when present type the text.
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @param text
     * @param message
     * @throws InterruptedException
     */
    public static void waitAndType(Selenium selenium, String elementLocator, int seconds, String text, String message) throws InterruptedException {
        waitForElement(selenium, elementLocator, seconds, message);
        selenium.type(elementLocator, text);
        Thread.sleep(1000);
    }

    /**
     * Wait 60 seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @throws InterruptedException
     */
    public static void waitForElement(Selenium selenium, String elementLocator) throws InterruptedException {
        waitForElement(selenium, elementLocator, WAIT_DEFAULT_SECONDS);
    }

    /**
     * Wait 60 seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param message
     * @throws InterruptedException
     */
    public static void waitForElement(Selenium selenium, String elementLocator, String message) throws InterruptedException {
        waitForElement(selenium, elementLocator, WAIT_DEFAULT_SECONDS, message);
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @throws InterruptedException
     */
    public static void waitForElement(Selenium selenium, String elementLocator, int seconds) throws InterruptedException {
        waitForElement(selenium, elementLocator, WAIT_DEFAULT_SECONDS, "");
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @param message
     * @throws InterruptedException
     */
    public static void waitForElement(Selenium selenium, String elementLocator, int seconds, String message) throws InterruptedException {
        boolean failed = false;
        for (int second = 0;; second++) {
            if (second >= seconds) failed = true;
            try { if (failed || selenium.isElementPresent(elementLocator)) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        ITUtil.checkForIncidentReport(selenium, elementLocator); // after timeout to be sure page is loaded
        if (failed) fail("timeout of " + seconds + " seconds waiting for " + elementLocator + " " + message);
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @throws InterruptedException
     */
    public static void waitForElementVisible(Selenium selenium, String elementLocator) throws InterruptedException {
        waitForElementVisible(selenium, elementLocator, WAIT_DEFAULT_SECONDS, "");
    }

    /**
     * Wait 60 seconds for the elementLocator to be present or fail including the given message
     * @param selenium
     * @param elementLocator
     * @param message
     * @throws InterruptedException
     */
    public static void waitForElementVisible(Selenium selenium, String elementLocator, String message) throws InterruptedException {
        waitForElementVisible(selenium, elementLocator, WAIT_DEFAULT_SECONDS, message);
    }

    /**
     * Wait the given seconds for the elementLocator to be present or fail
     * @param selenium
     * @param elementLocator
     * @param seconds
     * @throws InterruptedException
     */
    public static void waitForElementVisible(Selenium selenium, String elementLocator, int seconds, String message) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= seconds) fail("timeout of " + seconds + " seconds waiting for " + elementLocator + " " + message);
            try { if (selenium.isVisible(elementLocator)) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
    }

    /**
     * Wait for 60 seconds for the selenium.getTitle to match the given title then fail.
     * @param selenium
     * @param title
     * @throws InterruptedException
     */
    public static void waitForTitleToEqual(Selenium selenium, String title) throws InterruptedException {
        waitForTitleToEqual(selenium, title, "");
    }

    /**
     * Wait for 60 seconds for the selenium.getTitle to match the given title then fail including the given message.
     * @param selenium
     * @param title
     * @param message
     * @throws InterruptedException
     */
    public static void waitForTitleToEqual(Selenium selenium, String title, String message) throws InterruptedException {
        Thread.sleep(2000);
//        for (int second = 0;; second++) {
//            if (second >= WAIT_DEFAULT_SECONDS) fail(("timeout of " + WAIT_DEFAULT_SECONDS + " seconds waiting for title to equal " + title + " " + message).trim());
//            try { if (title.equals(selenium.getTitle())) break; } catch (Exception e) {}
//            Thread.sleep(1000);
//        }
    }

    /**
     * Check the selenium contents for an Incident Report failure with Incident Report Details
     * @param selenium
     * @param linkLocator
     */
    public static void checkForIncidentReport(Selenium selenium, String linkLocator) {
        checkForIncidentReport(selenium, linkLocator, "");
    }

    /**
     * Fails if a Incident Report is detected, extracting and reporting the View Id, Document Id, and StackTrace
     * @param selenium
     * @param linkLocator used only in the failure message
     */
    public static void checkForIncidentReport(Selenium selenium, String linkLocator, String message) {
        selenium.waitForPageToLoad(DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
        String contents = selenium.getHtmlSource();
        checkForIncidentReport(contents, linkLocator, message);
    }

    protected static void checkForIncidentReport(String contents, String linkLocator, String message) {
        if (contents == null) { //guard clause
            return;
        }

        if (contents.contains("Incident Report") &&
           !contents.contains("portal.do?channelTitle=Incident%20Report") && // Incident Report link on sampleapp KRAD tab
           !contents.contains("portal.do?channelTitle=Incident Report") &&   // Incident Report link on sampleapp KRAD tab IE8
           !contents.contains("uitest?viewId=Travel-testView2") &&
           !contents.contains("SeleniumException")) {                        // selenium timeouts have Incident Report in them
            try {
                processIncidentReport(contents, linkLocator, message);
            } catch (IndexOutOfBoundsException e) {
                Assert.fail("\nIncident report detected " + message + " but there was an exception during processing: " + e.getMessage() + "\nStack Trace from processing exception" + stackTrace(e) + "\nContents that triggered exception: " + deLinespace(
                        contents));
            }
        }

        if (contents.contains("HTTP Status 404")) {
            Assert.fail("\nHTTP Status 404 " + linkLocator + " " + message + " " + "\ncontents:" + contents);
        }

        if (contents != null && contents.contains("Java backtrace for programmers:")) { // freemarker exception
            // TODO parse out exception info
            Assert.fail("\nFreemarker exception " + linkLocator + " " + message + " " + "\ncontents:" + contents);
        }
    }

    private static void processIncidentReport(String contents, String linkLocator, String message) {
        if (contents.indexOf("Incident Feedback") == -1) {
            Assert.fail("\nIncident report detected " + message + "\nContents that triggered exception: " + deLinespace(contents));
        }

        Iterator<String> iter = jiraMatches.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
            key = iter.next();
            if (contents.contains(key)) {
                Assert.fail("https://jira.kuali.org/browse/" + jiraMatches.get(key));
            }
        }

        String chunk =  contents.substring(contents.indexOf("Incident Feedback"), contents.lastIndexOf("</div>") );
        String docId = chunk.substring(chunk.lastIndexOf("Document Id"), chunk.indexOf("View Id"));
        docId = docId.substring(0, docId.indexOf("</span>"));
        docId = docId.substring(docId.lastIndexOf(">") + 2, docId.length());

        String viewId = chunk.substring(chunk.lastIndexOf("View Id"), chunk.indexOf("Error Message"));
        viewId = viewId.substring(0, viewId.indexOf("</span>"));
        viewId = viewId.substring(viewId.lastIndexOf(">") + 2, viewId.length());

        String stackTrace = chunk.substring(chunk.lastIndexOf("(only in dev mode)"), chunk.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf("<span id=\"") + 3, stackTrace.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf("\">") + 2, stackTrace.indexOf("</span>"));

        //            System.out.println(docId);
        //            System.out.println(viewId);
        //            System.out.println(stackTrace);
        Assert.fail("\nIncident report " + message + " navigating to "
                + linkLocator
                + " : View Id: "
                + viewId.trim()
                + " Doc Id: "
                + docId.trim()
                + "\nStackTrace: "
                + stackTrace.trim());
    }

    protected static String deLinespace(String contents) {
        while (contents.contains("\n\n")) {
            contents = contents.replaceAll("\n\n", "\n");
        }
        return contents;
    }
}
