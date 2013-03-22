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
package edu.samplu.common;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.admin.test.AdminMenuNavITBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Originally used to upgrade UpgradedSeleniumITBase (Selenium 1.0) tests to WebDriver (Selenium 2.0).
 *
 * Now we have a large amount refactoring work ahead.
 *
 * 1.  Replace Locator strings with Constants and further wrap them in easier to understand method names.  See
 * waitAndClickMainMenu and LOGOUT_XPATH as an example.
 * 2.  Replace large chunks of duplication
 * 3.  Invert dependencies on fields and extract methods to WebDriverUtil so inheritance doesn't have to be used for
 * reuse.  See WebDriverUtil.waitFor
 * 4.  Nav specific stuff should maybe extracted.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverLegacyITBase { //implements com.saucelabs.common.SauceOnDemandSessionIdProvider {

    /**
     * Default "long" wait period is 30 seconds.  See REMOTE_PUBLIC_WAIT_SECONDS_PROPERTY to configure
     */
    public static final int DEFAULT_WAIT_SEC = 30;

    /**
     * You probably don't want to really be using a userpool, set -Dremote.public.userpool= to base url if you must.
     */
    public static final String REMOTE_PUBLIC_USERPOOL_PROPERTY = "remote.public.userpool";

    /**
     * Set -Dremote.public.user= to the username to login as
     */
    public static final String REMOTE_PUBLIC_USER_PROPERTY = "remote.public.user";

    /**
     * Set -Dremote.public.wait.seconds to override DEFAULT_WAIT_SEC
     */
    public static final String REMOTE_PUBLIC_WAIT_SECONDS_PROPERTY = "remote.public.wait.seconds";

    /**
     * //div[@id='headerarea']/div/table/tbody/tr[1]/td[1]
     */
    public static final String DOC_ID_XPATH = "//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]";

    /**
     * //table[@id='row']/tbody/tr[1]/td[1]/a
     */
    public static final String DOC_ID_TABLE_LINK_XPATH="//table[@id='row']/tbody/tr[1]/td[1]/a";

    /**
     * //input[@id='document.documentHeader.documentDescription']
     */
    public static final String DOCUMENT_DESCRIPTION_XPATH="//input[@id='document.documentHeader.documentDescription']";

    /**
     * //input[@name='methodToCall.search' and @value='search']
     */
    public static final String SEARCH_XPATH="//input[@name='methodToCall.search' and @value='search']";

    /**
     * //input[@name='methodToCall.save' and @alt='save']
     */
    public static final String SAVE_XPATH="//input[@name='methodToCall.save' and @alt='save']";

    /**
     * //input[@name='methodToCall.route' and @alt='submit']
     */
    public static final String SUBMIT_XPATH="//input[@name='methodToCall.route' and @alt='submit']";

    /**
     * //div[contains(div,'Document was successfully submitted.')]
     */
    public static final String DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH="//div[contains(div,'Document was successfully submitted.')]";

    /**
     * //input[@name='imageField' and @value='Logout']
     */
    public static final String LOGOUT_XPATH = "//input[@name='imageField' and @value='Logout']";
    public static final String IFRAMEPORTLET_NAME = "iframeportlet";

    /**
     * For Bookmark tests this is the url under final test.  For nav tests this is from the url navigation will
     * start from.
     * @return string
     */
    public abstract String getTestUrl();

    protected WebDriver driver;
    protected String user = "admin";
    protected int waitSeconds = DEFAULT_WAIT_SEC;
    protected boolean passed = false;
    static ChromeDriverService chromeDriverService;
    private Log log = LogFactory.getLog(getClass());

    public @Rule
    TestName testName = new TestName();

    String sessionId = null;

    /**
     *
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * If WebDriverUtil.chromeDriverCreateCheck() returns a ChromeDriverService, start it.
     * @see WebDriverUtil.chromeDriverCreateCheck()
     * @throws Exception
     */
    @BeforeClass
    public static void chromeDriverService() throws Exception {
        chromeDriverService = WebDriverUtil.chromeDriverCreateCheck();
        if (chromeDriverService != null)
            chromeDriverService.start();
    }

    /**
     * Setup the WebDriver properties, test, and login
     *
     * @see WebDriverUtil.setUp()
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // {"test":"1","user":"1"}
        try {
            waitSeconds = Integer.parseInt(System.getProperty(REMOTE_PUBLIC_WAIT_SECONDS_PROPERTY, DEFAULT_WAIT_SEC + ""));
            if (System.getProperty(REMOTE_PUBLIC_USER_PROPERTY) != null) {
                user = System.getProperty(REMOTE_PUBLIC_USER_PROPERTY);
            } else if (System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) { // deprecated
                String userResponse = getHTML(ITUtil.prettyHttp(System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY)
                        + "?test=" + this.toString().trim()));
                user = userResponse.substring(userResponse.lastIndexOf(":") + 2, userResponse.lastIndexOf("\""));
            }
            driver = WebDriverUtil.setUp(getUserName(), ITUtil.getBaseUrlString() + getTestUrl(), getClass()
                    .getSimpleName(), testName);
            this.sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
        } catch (Exception e) {
            fail("Exception in setUp " + e.getMessage());
            e.printStackTrace();
        }
        ITUtil.login(driver, user);
    }

    /**
     * Tear down test as configured.
     * @see WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY
     * @see ITUtil.dontTearDownPropertyNotSet()
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        try {		          
        	if (System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) {
                getHTML(ITUtil.prettyHttp(System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test="
                        + this.toString() + "&user=" + user));
            }
        } catch (Exception e) {
            System.out.println("Exception in tearDown " + e.getMessage());
            e.printStackTrace();
        } finally {            
        	if (driver != null) {                
        		if (ITUtil.dontTearDownPropertyNotSet()) {
                    driver.close();
                    driver.quit();
                }
            } else {
                System.out
                        .println("WebDriver is null, if using saucelabs, has sauceleabs been uncommented in WebDriverUtil.java?  If using a remote hub did you include the port?");
            }
        }
    }

    protected String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    protected void passed() {
        passed = true;
    }

    protected void assertElementPresentByName(String name) {
        driver.findElement(By.name(name));
    }

    protected void assertElementPresentByName(String name, String message) {
        try {
            driver.findElement(By.name(name));
        } catch (Exception e) {
            SeleneseTestBase.fail(name + " not present " + message);
        }
    }

    protected void assertElementPresentByXpath(String locator) {
        driver.findElement(By.xpath(locator));
    }

    protected void assertElementPresentByXpath(String locator, String message) {
        try {
            driver.findElement(By.xpath(locator));
        } catch (Exception e) {
            SeleneseTestBase.fail(locator + " not present " + message);
        }
    }

    protected void assertElementPresentByLinkText(String linkText) {
        driver.findElement(By.linkText(linkText));
    }

    protected void assertElementPresent(String locator) {
        driver.findElement(By.cssSelector(locator));
    }

    protected void assertTextPresent(String text) {
        assertTextPresent(text, "");
    }

    protected void assertTextPresent(String text, String message) {
        if (!driver.getPageSource().contains(text)) {
            SeleneseTestBase.fail(text + " not present " + message);
        }
    }

    protected void blanketApproveTest() throws InterruptedException {
        ITUtil.checkForIncidentReport(driver.getPageSource(), "methodToCall.blanketApprove", "");
        waitAndClickByName("methodToCall.blanketApprove", "No blanket approve button does the user " + getUserName()
                + " have permission?");
        Thread.sleep(2000);

        if (driver.findElements(By.xpath(ITUtil.DIV_ERROR_LOCATOR)).size() > 0) {
            String errorText = driver.findElement(By.xpath(ITUtil.DIV_ERROR_LOCATOR)).getText();         
            if (errorText != null && errorText.contains("error(s) found on page.")) {
                errorText = ITUtil.blanketApprovalCleanUpErrorText(errorText);                
                if (driver.findElements(By.xpath(ITUtil.DIV_EXCOL_LOCATOR)).size() > 0) { // not present if errors are at the bottom of the page (see left-errmsg below)
                    errorText = ITUtil.blanketApprovalCleanUpErrorText(driver.findElement(
                            By.xpath(ITUtil.DIV_EXCOL_LOCATOR)).getText()); // replacing errorText as DIV_EXCOL_LOCATOR includes the error count
                }
                SeleneseTestBase.fail(errorText);
            }
        }
        
        ITUtil.checkForIncidentReport(driver.getPageSource(), "//img[@alt='doc search']", "Blanket Approve failure");
        waitAndClickByXpath("//img[@alt='doc search']");
        SeleneseTestBase.assertEquals("Kuali Portal Index", driver.getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
    }

    protected void checkForIncidentReport() {
        checkForIncidentReport("", "");
    }

    protected void checkForIncidentReport(String locator) {
        checkForIncidentReport(locator, "");
    }

    protected void checkForIncidentReport(String locator, String message) {
        WebDriverUtil.checkForIncidentReport(driver, locator, message);
    }

    protected void clearText(By by) throws InterruptedException {
        driver.findElement(by).clear();
    }

    protected void clearText(String selector) throws InterruptedException {
        clearText(By.cssSelector(selector));
    }

    protected void clearTextByName(String name) throws InterruptedException {
        clearText(By.name(name));
    }

    protected void clearTextByXpath(String locator) throws InterruptedException {
        clearText(By.xpath(locator));
    }

    protected String getAttribute(By by, String attribute) throws InterruptedException {
        jiraAwareWaitFor(by, "");
        
        return driver.findElement(by).getAttribute(attribute);
    }

    /**
     * Get value of any attribute by using element name
     * 
     * @param name name of an element
     * @param attribute the name of an attribute whose value is to be retrieved
     */
    protected String getAttributeByName(String name, String attribute) throws InterruptedException {
        return getAttribute(By.name(name), attribute);
    }

    /**
     * Get value of any attribute by using element xpath
     * 
     * @param locator locating mechanism of an element
     * @param attribute the name of an attribute whose value is to be retrieved
     */
    protected String getAttributeByXpath(String locator, String attribute) throws InterruptedException {
        return getAttribute(By.xpath(locator), attribute);
    }

    protected String getBaseUrlString() {
        return ITUtil.getBaseUrlString();
    }

    protected String getText(By by) throws InterruptedException {
        return driver.findElement(by).getText();
    }

    protected String getTextByName(String name) throws InterruptedException {
        return getText(By.name(name));
    }

    protected String getText(String locator) throws InterruptedException {
        return getText(By.cssSelector(locator));
    }

    protected String getTextByXpath(String locator) throws InterruptedException {
        return getText(By.xpath(locator));
    }

    protected String getTitle() {
        return driver.getTitle();
    }

    /**
     * Override in test to define a user other than admin
     * @return
     */
    public String getUserName() {
        return user;
    }

    /**
     * Handles simple nested frame content; validates that a frame and nested frame exists before
     * switching to it
     */
    protected void gotoNestedFrame() {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.switchTo().defaultContent();
       
        if (driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement containerFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(containerFrame);
        }
        
        if (driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement contentFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(contentFrame);
        }
        
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
    }

    /**
     * Until UpdatedLegacyDriver is deleted.
     * @param driver
     */
    public static void gotoNestedFrame(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.switchTo().defaultContent();
        
        if (driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement containerFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(containerFrame);
        }
       
        if (driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement contentFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(contentFrame);
        }
        
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(System.getProperty(REMOTE_PUBLIC_WAIT_SECONDS_PROPERTY, DEFAULT_WAIT_SEC + "")), TimeUnit.SECONDS);
    }

    protected boolean isElementPresent(By by) {
        return (driver.findElements(by)).size() > 0;
    }

    protected boolean isElementPresent(String locator) {
        return (driver.findElements(By.cssSelector(locator))).size() > 0;
    }

    protected boolean isElementPresentByName(String name) {
        return isElementPresent(By.name(name));
    }

    protected boolean isElementPresentByXpath(String locator) {
        return isElementPresent(By.xpath(locator));
    }

    protected boolean isElementPresentByLinkText(String locator) {
        return isElementPresent(By.linkText(locator));
    }

    protected void open(String url) {
        driver.get(url);
    }

    protected void selectFrame(String locator) {
        
        if (IFRAMEPORTLET_NAME.equals(locator)) {
            gotoNestedFrame();
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

    protected void selectWindow(String locator) {
        driver.switchTo().window(locator);
    }

    protected void close() {
        driver.close();
    }

    protected void testCancelConfirmation() throws InterruptedException {
        waitAndCancelConfirmation();
        passed();
    }

    protected void testCreateNewSearchReturnValueCancelConfirmation() throws InterruptedException, Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        waitAndSearch();
        waitAndReturnValue();
        waitAndCancelConfirmation();
        passed();
    }

    protected void testSearchEditCancel() throws InterruptedException {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndSearch();
        waitAndEdit();
        testCancelConfirmation();
    }

    protected void testVerifyAddDeleteFiscalOfficerLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndTypeByName("document.documentHeader.documentDescription", ITUtil.DTS_TWO);
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number","1234567890");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");
        waitAndClickByXpath("//button[@data-loadingmessage='Adding Line...']");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");
        SeleneseTestBase.assertEquals("1234567890",getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number","value"));
        SeleneseTestBase.assertEquals("2",getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId", "value"));
        waitAndClickByXpath("//button[@data-loadingmessage='Deleting Line...']");
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals(Boolean.FALSE, (Boolean) isElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number"));
        passed();
    }

    protected void waitAndCancelConfirmation() throws InterruptedException {
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
    }

    protected void waitAndCreateNew() throws InterruptedException {
        try {
            waitAndClickByXpath("//img[@alt='create new']"); // timing out in CI rice-trunk-smoke-test-jdk7/494
        } catch (Exception e) {
            System.out.println("waitAndClickByXpath(\"//img[@alt='create new']\") failed trying title method with " + e.getMessage());
            waitAndClickByXpath("//a[@title='Create a new record']");
        }
    }

    protected void waitAndEdit() throws InterruptedException {
        waitAndClickByLinkText("edit");
    }

    protected void waitAndReturnValue() throws InterruptedException {
        waitAndClickByLinkText("return value");
    }

    protected void waitAndSearch() throws InterruptedException {
        waitAndClickByXpath("//input[@value='search']");     
    }

    protected String waitForDocId() throws InterruptedException {
        waitForElementPresentByXpath(DOC_ID_XPATH);
        
        return driver.findElement(By.xpath(DOC_ID_XPATH)).getText();
    }

    protected void waitForElementPresent(String locator) throws InterruptedException {
        jiraAwareWaitFor(By.cssSelector(locator), "");
    }

    protected void waitForElementPresentByXpath(String locator) throws InterruptedException {
        jiraAwareWaitFor(By.xpath(locator), "");
    }

    protected void waitForElementPresentByName(String name) throws InterruptedException {
        jiraAwareWaitFor(By.name(name), "");
    }

    protected void waitForTitleToEqualKualiPortalIndex() throws InterruptedException {
        waitForTitleToEqualKualiPortalIndex("");
    }

    protected void waitForTitleToEqualKualiPortalIndex(String message) throws InterruptedException {
        Thread.sleep(2000);
        // This started failing in CI....
        // boolean failed = false;
        //         
        // for (int second = 0;; second++) {
        //     Thread.sleep(1000);
        //     if (second >= waitSeconds) failed = true;
        //     try { if (failed || ITUtil.KUALI_PORTAL_TITLE.equals(driver.getTitle())) break; } catch (Exception e) {}
        // }
         
        // WebDriverUtil.checkForIncidentReport(driver, message); // after timeout to be sure page is loaded
        // if (failed) fail("timeout of " + waitSeconds + " seconds " + message);
    }

    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, "");
    }

    protected void waitForPageToLoad() {
        // noop webdriver doesn't it need it, except when it does...
    }

    protected void waitFor(By by) throws InterruptedException {
        jiraAwareWaitFor(by, "");
    }

    /**
     * Should be called from jiraAwareWaitFor to get KULRICE error output in CI.
     *
     * Inner most waitFor, let it throw the failure so the timeout message reflects the waitSeconds time, not the 1
     * second it is set to before returning.
     * @param by
     * @param message
     * @throws InterruptedException
     */
    private void waitFor(By by, String message) throws InterruptedException {
        WebDriverUtil.waitFor(this.driver, this.waitSeconds, by, message);
    }

    private void jiraAwareFail(By by, String message, Throwable t) {
        ITUtil.failOnMatchedJira(by.toString());
        // if there isn't a matched jira to fail on, then fail
        fail(t.getMessage() + " " + by.toString() + " " + message + " " + driver.getCurrentUrl());
    }

    protected void jiraAwareWaitFor(By by, String message) throws InterruptedException {
        try {
            WebDriverUtil.waitFor(this.driver, this.waitSeconds, by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
    }

    protected void jiraAwareWaitFor(By by, String message, SeleneseFailable failable) throws InterruptedException {
        try {
            WebDriverUtil.waitFor(this.driver, this.waitSeconds, by, message);
        } catch (Throwable t) {
            jiraAwareFail(by, message, t);
        }
    }

    protected void waitAndClick(By by) throws InterruptedException {
        jiraAwareWaitAndClick(by, "");
    }

    protected void waitAndClick(By by, SeleneseFailable failable) throws InterruptedException {
        jiraAwareWaitAndClick(by, "", failable);
    }

    protected void jiraAwareWaitAndClick(By by, String message) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message);
            (driver.findElement(by)).click();
        } catch (Exception e) {
            jiraAwareFail(by, message, e);
        }
    }

    protected void jiraAwareWaitAndClick(By by, String message, SeleneseFailable failable) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, message, failable);
            (driver.findElement(by)).click();
        } catch (Exception e) {
            jiraAwareFail(by, message, e);
        }
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.cssSelector(locator), message);
    }

    protected void waitAndClickByLinkText(String text) throws InterruptedException {
        jiraAwareWaitAndClick(By.linkText(text), "");
    }

    protected void waitAndClickByLinkText(String text, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.linkText(text), message);
    }

    protected void waitAndClickByLinkText(String text, SeleneseFailable failable) throws InterruptedException {
        jiraAwareWaitAndClick(By.linkText(text), "", failable);
    }

    protected void waitAndClickByName(String name) throws InterruptedException {
        jiraAwareWaitAndClick(By.name(name), "");
    }

    protected void waitAndClickByXpath(String xpath) throws InterruptedException {
        waitAndClick(By.xpath(xpath));
    }

    protected void waitAndClickByXpath(String xpath, SeleneseFailable failable) throws InterruptedException {
        waitAndClick(By.xpath(xpath), failable);
    }

    protected void waitAndClickByName(String name, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.name(name), message);
    }

    protected void waitAndClickByXpath(String xpath, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.xpath(xpath), message);
    }

    protected void waitAndClickLogout() throws InterruptedException {
         waitAndClickByXpath(LOGOUT_XPATH);
    }

    protected void waitAndClickLogout(SeleneseFailable failable) throws InterruptedException {
        waitAndClickByXpath(LOGOUT_XPATH, failable);
    }

    protected void waitAndClickMainMenu() throws InterruptedException {
        waitAndClickByLinkText("Main Menu");
    }

    protected void waitAndClickMainMenu(SeleneseFailable failable) throws InterruptedException {
        waitAndClickByLinkText("Main Menu", failable);
    }

    protected void waitAndType(By by, String text) throws InterruptedException {
        waitAndType(by, text,  "");
    }

    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        try {
            jiraAwareWaitFor(by, "");
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            ITUtil.failOnMatchedJira(by.toString());
            fail(e.getMessage() + " " + by.toString() + "  unable to type text '" + text + "'  " + message
                    + " current url " + driver.getCurrentUrl()
                    + "\n" + ITUtil.deLinespace(driver.getPageSource()));
        }
    }

    protected void waitAndType(String selector, String text) throws InterruptedException {
        waitAndType(By.cssSelector(selector), text);
    }

    protected void waitAndTypeByXpath(String locator, String text) throws InterruptedException {
        waitAndType(By.xpath(locator), text);
    }

    protected void waitAndTypeByXpath(String locator, String text, String message) throws InterruptedException {
        waitAndType(By.xpath(locator), text, message);
    }

    protected void waitAndTypeByName(String name, String text) throws InterruptedException {
        waitAndType(By.name(name), text);
    }

    protected void selectByXpath(String locator, String selectText) throws InterruptedException {
        select(By.xpath(locator), selectText);
    }

    protected void selectByName(String name, String selectText) throws InterruptedException {
        select(By.name(name), selectText);
    }

    protected void select(By by, String selectText) throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        
        for (WebElement option : options) {            
        	if (option.getText().equals(selectText)) {
                option.click();
                break;
            }
        }
    }

    protected void selectOptionByName(String name, String optionValue) throws InterruptedException {
        selectOption(By.name(name), optionValue);
    }

    protected void selectOptionByXpath(String locator, String optionValue) throws InterruptedException {
        selectOption(By.name(locator), optionValue);
    }

    protected void selectOption(By by, String optionValue) throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        
        for (WebElement option : options) {            
        	if (option.getAttribute("value").equals(optionValue)) {
                option.click();
                break;
            }
        }
    }

    protected String[] getSelectOptions(By by) throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        String[] optionValues = new String[options.size()];
        int counter = 0;
        
        for (WebElement option : options) {
            optionValues[counter] = option.getAttribute("value");
            counter++;
        }
        
        return optionValues;
    }

    protected String[] getSelectOptionsByName(String name) throws InterruptedException {
        return getSelectOptions(By.name(name));
    }

    protected String[] getSelectOptionsByXpath(String locator) throws InterruptedException {
        return getSelectOptions(By.xpath(locator));
    }

    protected int getCssCount(String selector) {
        return getCssCount(By.cssSelector(selector));
    }

    protected int getCssCount(By by) {
        return (driver.findElements(by)).size();
    }

    protected void checkErrorMessageItem(String message)
    {
        final String error_locator = "//li[@class='uif-errorMessageItem']";
        assertElementPresentByXpath(error_locator);
        String errorText = null;
        
        try {
            errorText = getTextByXpath(error_locator);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (errorText != null && errorText.contains("errors")) {
            SeleneseTestBase.fail(errorText + message);
        }
    }

    protected boolean isVisible(String locator) {
        return driver.findElement(By.cssSelector(locator)).isDisplayed();
    }

    protected boolean isVisible(By by) {
        return driver.findElement(by).isDisplayed();
    }

    protected boolean isVisibleByXpath(String locator) {
        return isVisible(By.xpath(locator));
    }

    protected void waitNotVisible(By by) throws InterruptedException {
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            if (!isVisible(by)) {
                break;
            }
            Thread.sleep(1000);
        }
    }

    protected void waitNotVisibleByXpath(String locator) throws InterruptedException {
        waitNotVisible(By.xpath(locator));
    }

    protected void waitIsVisible(By by) throws InterruptedException {
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }           
        	if (isVisible(by)) {
                break;
            }
            Thread.sleep(1000);
        }
    }

    protected void waitForElementVisible(String elementLocator, String message) throws InterruptedException {
        boolean failed = false;
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds)
                failed = true;
            try {                
            	if (failed || (driver.findElements(By.cssSelector(elementLocator))).size() > 0)
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        
        checkForIncidentReport(elementLocator); // after timeout to be sure page is loaded
        
        if (failed)
            fail("timeout of " + waitSeconds + " seconds waiting for " + elementLocator + " " + message + " " + driver.getCurrentUrl());
    }

    protected void waitIsVisible(String locator) throws InterruptedException {
        waitIsVisible(By.cssSelector(locator));
    }

    protected void waitIsVisibleByXpath(String locator) throws InterruptedException {
        waitIsVisible(By.xpath(locator));
    }

    protected void colapseExpandByXpath(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClickByXpath(clickLocator);
        waitNotVisibleByXpath(visibleLocator);
        waitAndClickByXpath(clickLocator);
        waitIsVisibleByXpath(visibleLocator);
    }

    protected void expandColapseByXpath(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClickByXpath(clickLocator);
        waitIsVisibleByXpath(visibleLocator);
        waitAndClickByXpath(clickLocator);
        waitNotVisibleByXpath(visibleLocator);
    }

    /**
     * If a window contains the given title switchTo it.
     * @param title
     */
    public void switchToWindow(String title) {
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);            
            if (driver.getTitle().contains(title)) {
                return;
            }
        }
    }

    /**
     * @see WebDriver.getWindowHandles()
     * @return
     */
    public String[] getAllWindowTitles() {
        return (String[]) driver.getWindowHandles().toArray();
    }

    protected void check(By by) throws InterruptedException {
        WebElement element = driver.findElement(by);
        
        if (!element.isSelected()) {
            element.click();
        }
    }

    protected void checkByName(String name) throws InterruptedException {
        check(By.name(name));
    }

    protected void checkByXpath(String locator) throws InterruptedException {
        check(By.xpath(locator));
    }

    protected void uncheck(By by) throws InterruptedException {
        WebElement element = driver.findElement(by);        
        if (element.isSelected()) {
            element.click();
        }
    }

    protected void uncheckByName(String name) throws InterruptedException {
        uncheck(By.name(name));
    }

    protected void uncheckByXpath(String locator) throws InterruptedException {
        uncheck(By.xpath(locator));
    }

    protected void fireEvent(String name, String event) {
        ((JavascriptExecutor) driver).executeScript(
                "var elements=document.getElementsByName(\"" + name + "\");" +
                        "for (var i = 0; i < elements.length; i++){" +
                        "elements[i]." + event + "();}"
                );
    }

    protected void fireEvent(String name, String value, String event) {
        ((JavascriptExecutor) driver).executeScript(
                "var elements=document.getElementsByName(\"" + name + "\");" +
                        "for (var i = 0; i < elements.length; i++){" +
                        "if(elements[i].value=='" + value + "')" +
                        "elements[i]." + event + "();}"
                );
    }

    /**
     * @see Actions.moveToElement
     * @param name
     */
    public void fireMouseOverEventByName(String name) {
        this.fireMouseOverEvent(By.name(name));
    }

    /**
     * @see Actions.moveToElement
     * @param locator
     */
    public void fireMouseOverEventByXpath(String locator) {
        this.fireMouseOverEvent(By.xpath(locator));
    }

    /**
     * @see Actions.moveToElement
     * @param by
     */
    public void fireMouseOverEvent(By by) {
        Actions builder = new Actions(driver);
        Actions hover = builder.moveToElement(driver.findElement(by));
        hover.perform();
    }

    protected Boolean isTextPresent(String text)
    {
        if (driver.getPageSource().contains(text)) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }

    protected void testAgendaEditRuleRefreshIT() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[1]"); //  jiraAwareWaitAndClick("id=32");
        Thread.sleep(3000);
        waitAndClickByXpath("//a[@title='edit Agenda Definition with Agenda Id=T1000']",
                "Does user have edit permissions?"); // jiraAwareWaitAndClick("id=194_line0");
        checkForIncidentReport("");
        Thread.sleep(3000);
        waitAndClickByXpath("//li/a[@class='agendaNode ruleNode']"); // jiraAwareWaitAndClick("//li[@id='473_node_0_parent_root']/a");
        waitAndClickByXpath("//li/a[@class='agendaNode logicNode whenTrueNode']");
        waitAndClickByLinkText("[-] collapse all");

        // click refresh  several times
        for (int i = 0; i < 6; i++) {
            for (int second = 0;; second++) {               
                if (second >= waitSeconds)
                    SeleneseTestBase.fail("timeout");
                try {                    
                    if (isElementPresent(".kr-refresh-button"))
                        break;
                } catch (Exception e) {}
                Thread.sleep(1000);
            }
            waitAndClick("button.kr-refresh-button");
        }
    }

    protected void assertBlanketApproveButtonsPresent() {
        assertElementPresentByName("methodToCall.route");
        assertElementPresentByName("methodToCall.save");
        assertElementPresentByName("methodToCall.blanketApprove", "Blanket Approve button not present does " + user + " have permssion?");
        assertElementPresentByName("methodToCall.close");
        assertElementPresentByName("methodToCall.cancel");
    }

    protected void assertDocFinal(String docId) throws InterruptedException {
        jiraAwareWaitFor(By.linkText("spreadsheet"), "");
        
        if (isElementPresent(By.linkText(docId))) {
            SeleneseTestBase.assertEquals("FINAL", getDocStatus());
        } else {
            SeleneseTestBase.assertEquals(docId,driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]")));
            SeleneseTestBase.assertEquals("FINAL", getDocStatus());
        }
    }

    private String getDocStatus() {
        return driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[4]")).getText();
    }

    protected String configNameSpaceBlanketApprove() throws Exception {
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Namespace "+ ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", "VTN" + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']","Validation Test NameSpace " + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.applicationId']", "RICE");
        
        return docId;
    }

    protected void testConfigNamespaceBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        String docId = configNameSpaceBlanketApprove();
        blanketApproveTest();
        assertDocFinal(docId);
        passed();
    }

    protected void testConfigParamaterBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Parameter ");
        assertBlanketApproveButtonsPresent();
        SeleneseTestBase.assertEquals("", getTextByName("methodToCall.cancel"));
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']", "KR-NS - Kuali Nervous System");
        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(componentLookUp);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        String parameterName = "ValidationTestParameter" + ITUtil.DTS;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", parameterName);
        waitAndTypeByXpath("//textarea[@id='document.newMaintainableObject.description']",
                "Validation Test Parameter Description" + ITUtil.DTS);
        selectByXpath("//select[@id='document.newMaintainableObject.parameterTypeCode']", "Document Validation");
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.evaluationOperatorCodeAllowed']");
        waitForPageToLoad();
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testCreateNewCancel() throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        testCancelConfirmation();
    }

    protected void testCreateDocType() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']","save button does not exist on the page");
        
        //waitForElementPresentByXpath(DOC_ID_XPATH);
        //String docId = driver.findElement(By.xpath(DOC_ID_XPATH)).getText();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Creating new Document Type");
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        String docTypeName = "TestDocType" + ITUtil.DTS;
        waitForElementPresentByXpath("//input[@id='document.newMaintainableObject.name']");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']","${kr.url}/maintenance.do?methodToCall=docHandler");
        
        //waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']", "TestDocument Label");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']","default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        waitAndClickByXpath("//*[@name='methodToCall.route' and @alt='submit']");
        checkForIncidentReport();
        waitForPageToLoad();
        driver.switchTo().defaultContent();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]")).getText());
    }

    protected void testIdentityGroupBlanketApprove() throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();                
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Group "+ ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.groupNamespace']", AdminMenuNavITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.groupName']", "Validation Test Group1 " + ITUtil.DTS_TWO);
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityPermissionBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']", "Validation Test Permission " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']", "10012");
        selectByXpath("//select[@name='document.newMaintainableObject.namespaceCode']",AdminMenuNavITBase.LABEL_KUALI_KUALI_SYSTEMS);
        selectByXpath("//select[@name='document.newMaintainableObject.templateId']",AdminMenuNavITBase.LABEL_KUALI_DEFAULT);
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.name']", "ValidationTestPermission" + ITUtil.DTS_TWO);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityPersonBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Person");
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.principalName']", "principal" + RandomStringUtils.randomAlphabetic(3).toLowerCase());
        selectByName("newAffln.affiliationTypeCode", "Affiliate");
        selectByName("newAffln.campusCode", "BX - BLGTN OFF CAMPUS");
        selectByName("newAffln.campusCode", "BL - BLOOMINGTON");
        assertElementPresentByName("newAffln.dflt");
        waitAndClickByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitAndClickByName("methodToCall.toggleTab.tabContact");
        selectByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "First");
        waitAndTypeByName("newName.lastName", "Last");
        selectByName("newName.nameSuffix", "Mr");
        waitAndClickByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitForPageToLoad();
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityResponsibilityBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Responsibility " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']", AdminMenuNavITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "Validation Test Responsibility " + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.documentTypeName']", "Test " + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.routeNodeName']", "Test " + ITUtil.DTS_TWO);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.actionDetailsAtRoleMemberLevel']");
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.required']");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityRoleBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        waitAndClickByXpath(SEARCH_XPATH, "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");        
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Role " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.roleNamespace']", AdminMenuNavITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.roleName']", "Validation Test Role " + ITUtil.DTS_TWO,"No Role Name input to type in.");
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath(SEARCH_XPATH, "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationCampusBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Validation Test Campus");
        assertBlanketApproveButtonsPresent();
        waitAndTypeByName("document.newMaintainableObject.code", RandomStringUtils.randomAlphabetic(2));
        waitAndTypeByName("document.newMaintainableObject.name", "Validation Test Campus" + ITUtil.DTS);
        waitAndTypeByName("document.newMaintainableObject.shortName", "VTC");
        selectByName("document.newMaintainableObject.campusTypeCode", "B - BOTH");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationCountryBlanketApprove() throws InterruptedException {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        String twoUpperCaseLetters = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        String countryName = "Validation Test Country " + ITUtil.DTS + " " + twoUpperCaseLetters;
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, countryName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", twoUpperCaseLetters);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countryName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.alternateCode']", "V" + twoUpperCaseLetters);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationCountyBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test County");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", RandomStringUtils.randomAlphabetic(2).toUpperCase());
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndTypeByName("code", "IN");
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        String countyName = "Validation Test County" + ITUtil.DTS;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countyName);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.active']");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationPostBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();        
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Postal Code");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        String code = RandomStringUtils.randomNumeric(5);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", code);
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByXpath("//table[@id='row']/tbody/tr[4]/td[1]/a");
        String cityName = "Validation Test Postal Code " + code;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.cityName']", cityName);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationStateBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test State");
        assertBlanketApproveButtonsPresent();
        
        //jiraAwareWaitAndClick("methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4");
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        String code = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", code);
        String state = "Validation Test State " + code;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", state);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.active']");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testReferenceCampusTypeBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Validation Test Campus Type "+ ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", RandomStringUtils.randomAlphabetic(2));
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "Indianapolis" + ITUtil.DTS_TWO);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testWorkFlowDocTypeBlanketApprove() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH,"Validation Test Document Type " + ITUtil.DTS);
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("return value");
        String docTypeName = "DocType" + ITUtil.DTS;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']","${kr.url}/maintenance.do?methodToCall=docHandler");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']","Workflow Maintenance Document Type Document");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']","default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testVerifyAddDeleteNoteLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClick("div.tableborders.wrap.uif-boxLayoutVerticalItem.clearfix  span.uif-headerText-span > img.uif-disclosure-image");
        waitForElementPresent("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        waitAndClickByName("newCollectionLines['document.notes'].noteText");
        waitAndTypeByName("newCollectionLines['document.notes'].noteText", "Test note");
        waitAndClick("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        waitForElementPresentByName("document.notes[0].noteText");
        SeleneseTestBase.assertEquals("Test note", getTextByXpath("//pre"));
        waitAndClick("button[title='Delete a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        SeleneseTestBase.assertEquals(Boolean.FALSE,(Boolean) isElementPresentByName("document.notes[0].noteText"));
        passed();
    }

    protected void testVerifyAdHocRecipientsLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("Fiscal Officer Accounts");
        assertElementPresentByXpath("//select[@name=\"newCollectionLines['document.adHocRoutePersons'].actionRequested\"]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRoutePersons'].name\" and @type=\"text\"]");
        assertElementPresentByXpath("//select[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].actionRequested\"]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientNamespaceCode\" and @type='text']");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientName\" and @type='text']");
        passed();
    }

    protected void testVerifyButtonsLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//button[contains(.,'ubmit')]");
        assertElementPresentByXpath("//button[contains(.,'ave')]");
        assertElementPresentByXpath("//button[contains(.,'lanket approve')]");
        assertElementPresentByXpath("//button[contains(.,'lose')]");
        assertElementPresentByXpath("//a[contains(.,'ancel')]");
        passed();
    }

    protected void testVerifyConstraintText() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        SeleneseTestBase.assertEquals("* indicates required field",
                getText("div.uif-boxLayout.uif-horizontalBoxLayout.clearfix > span.uif-message.uif-requiredInstructionsMessage.uif-boxLayoutHorizontalItem"));
        SeleneseTestBase.assertEquals("Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        SeleneseTestBase.assertEquals("Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Sub Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        SeleneseTestBase.assertEquals("Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-collectionItem.uif-gridCollectionItem.uif-collectionAddItem div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        passed();
    }

    protected void testVerifyDisclosures() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Account Information')]");
        assertElementPresentByXpath("//span[contains(text(),'Fiscal Officer Accounts')]");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//span[contains(text(),'Ad Hoc Recipients')]");
        assertElementPresentByXpath("//span[contains(text(),'Route Log')]");
        colapseExpandByXpath("//span[contains(text(),'Document Overview')]//img", "//label[contains(text(),'Organization Document Number')]");
        colapseExpandByXpath("//span[contains(text(),'Account Information')]//img","//label[contains(text(),'Travel Account Type Code')]");
        colapseExpandByXpath("//span[contains(text(),'Fiscal Officer Accounts')]//img","//a[contains(text(),'Lookup/Add Multiple Lines')]");
        expandColapseByXpath("//span[contains(text(),'Notes and Attachments')]//img","//label[contains(text(),'Note Text')]");
        expandColapseByXpath("//span[contains(text(),'Ad Hoc Recipients')]","//span[contains(text(),'Ad Hoc Group Requests')]");

        // Handle frames
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");
        waitIsVisibleByXpath("//img[@alt='refresh']");

        // relative=top iframeportlet might look weird but either alone results in something not found.
        selectTopFrame();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");
        waitNotVisibleByXpath("//img[@alt='refresh']");
        passed();
    }

    protected void testVerifyDocumentOverviewLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//input[@name='document.documentHeader.documentDescription']");
        assertElementPresentByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']");
        assertElementPresentByXpath("//textarea[@name='document.documentHeader.explanation']");
        passed();
    }

    protected void testVerifyExpandCollapse() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//button[contains(@class, 'uif-expandDisclosuresButton')]");
        assertElementPresentByXpath("//button[contains(@class, 'uif-collapseDisclosuresButton')]");
        passed();
    }

    protected void testVerifyFieldsLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.number' and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.extension.accountTypeCode' and @type='text' and @size=2 and @maxlength=3]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subAccount' and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @size=6 and @maxlength=20]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.foId' and @type='text' and @size=5 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number\" and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId\" and @type='text' and @size=5 and @maxlength=10]");
        passed();
    }

    protected void testVerifyHeaderFieldsLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentNumber')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentInitiatorNetworkId')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentStatus')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentCreateDate')]");
        passed();
    }

    protected void testVerifyLookupAddMultipleLinesLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertElementPresentByXpath("//a[contains(text(),'Lookup/Add Multiple Lines')]");
        passed();
    }

    protected void testVerifyNotesAndAttachments() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//span[contains(text(),'Notes and Attachments')]");
        waitForElementPresentByXpath("//button[@title='Add a Note']");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//textarea[@name=\"newCollectionLines['document.notes'].noteText\"]");
        assertElementPresentByXpath("//input[@name='attachmentFile']");
        
        //assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.notes'].attachment.attachmentTypeCode\"]");
        passed();
    }

    protected void testVerifyQuickfinderIconsLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//*[@id='quickfinder1']");
        assertElementPresentByXpath("//*[@id='quickfinder2']");
        assertElementPresentByXpath("//*[@id='quickfinder3']");
        assertElementPresentByXpath("//*[@id='quickfinder4_add']");
        
        // TODO it would be better to test that the image isn't 404
        passed();
    }

    protected void testVerifyRouteLog() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("Route Log");
        waitForElementPresent("//iframe[contains(@src,'RouteLog.do')]");
        passed();
    }

    protected void testVerifySave() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndTypeByName("document.documentHeader.documentDescription", "Test Document " + ITUtil.DTS);
        waitAndClickByName("document.newMaintainableObject.dataObject.number");
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", "1234567890");
        waitAndTypeByName("document.newMaintainableObject.dataObject.extension.accountTypeCode", "EAT");
        waitAndTypeByName("document.newMaintainableObject.dataObject.subAccount", "a1");
        waitAndClick("button[data-loadingmessage='Saving...'].uif-action.uif-primaryActionButton.uif-boxLayoutHorizontalItem");
        Thread.sleep(2000);
        
        // checkErrorMessageItem(" also digit validation jira https://jira.kuali.org/browse/KULRICE-8038");
        passed();
    }

    protected void testVerifySubsidizedPercentWatermarkLegacy() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        
        // May be blowing up due to multiple locators
        //assertTrue(isElementPresent("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @placeholder='##.##   ']"));
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent']");
        passed();
    }

    private void testLookUp() throws Exception {
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        
        // Mixed capitalization
        waitAndClick(By.xpath("//button[contains(text(),'Search')]"));
        waitAndClickByLinkText("edit", "edit button not present does user " + user + " have permission?");
        checkForIncidentReport("submit");
        assertTextPresent("ubmit");
        assertTextPresent("ave");
        assertTextPresent("pprove");
        assertTextPresent("lose");
        assertTextPresent("ancel");
    }

    protected void agendaLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Rules");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testAttributeDefinitionLookUp() throws Exception {
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//button[contains(.,'earch')]");
        Thread.sleep(3000);
        waitForPageToLoad();
        driver.findElement(By.tagName("body")).getText().contains("Actions"); // there are no actions, but the header is the only unique text from searching
        waitAndClickByLinkText("1000");
        waitForPageToLoad();
        driver.findElement(By.tagName("body")).getText().contains("Attribute Inquiry");
        driver.findElement(By.tagName("body")).getText().contains("KRMS Attributes");
        driver.findElement(By.tagName("body")).getText().contains("Attribute Label");
        driver.findElement(By.tagName("body")).getText().contains("1000");
        driver.findElement(By.tagName("body")).getText().contains("peopleFlowId");
        driver.findElement(By.tagName("body")).getText().contains("KR-RULE");
        driver.findElement(By.tagName("body")).getText().contains("PeopleFlow");
        
        // selectFrame("name=fancybox-frame1343151577256"); // TODO parse source to get name
        // jiraAwareWaitAndClick("css=button:contains(Close)"); // looks lower case, but is upper
        // Thread.sleep(500);
        // jiraAwareWaitAndClick("css=button:contains(cancel)");
        // AttributeDefinition's don't have actions (yet)
        // jiraAwareWaitAndClick("id=u80");
        // waitForPageToLoad();
        // jiraAwareWaitAndClick("id=u86");
        // waitForPageToLoad();
        // selectWindow("null");
        // jiraAwareWaitAndClick("xpath=(//input[@name='imageField'])[2]");
        // waitForPageToLoad();
        passed();
    }

    protected void contextLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Notes and Attachments");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testCreateNewAgenda() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        selectByName("document.newMaintainableObject.dataObject.namespace", "Kuali Rules Test");
        String agendaName = "Agenda Date :" + Calendar.getInstance().getTime().toString();
        waitAndTypeByName("document.newMaintainableObject.dataObject.agenda.name", "Agenda " + agendaName);
        waitAndTypeByName("document.newMaintainableObject.dataObject.contextName", "Context1");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.agenda.typeId");
        selectByName("document.newMaintainableObject.dataObject.agenda.typeId", "Campus Agenda");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]", "BL");
        waitAndClickByXpath("//div[2]/button");
        waitForPageToLoad();
        waitAndClickByXpath("//div[2]/button[3]");
        waitForPageToLoad();
        selectTopFrame();
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        passed();
    }

    protected void testPeopleFlow() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        
        //Click Main Menu and Create New
        // waitAndCreateNew();
        // waitForPageToLoad();
        waitAndClickByLinkText("Create New");
        
        //jiraAwareWaitAndClick(By.linkText("Create New"));
        //Save docId
        waitForElementPresent("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");
        String docId = getText("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");
        driver.findElement(By.name("document.documentHeader.documentDescription")).clear();
        driver.findElement(By.name("document.documentHeader.documentDescription")).sendKeys("Description for Document");
        new Select(driver.findElement(By.name("document.newMaintainableObject.dataObject.namespaceCode"))).selectByVisibleText("KUALI - Kuali Systems");
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).clear();
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).sendKeys("Document Name" + ITUtil.DTS);

        //Add Row1
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).sendKeys("kr");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);
        
        //Add Row2
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).sendKeys("admin");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);

        //Blanket approve
        driver.findElement(By.cssSelector("div[data-parent='PeopleFlow-MaintenanceView'] > div.uif-footer button~button~button")).click();
        Thread.sleep(5000);
        
        //Close the Doc
        //driver.findElement(By.id("uif-close")).click();
        //Thread.sleep(3000);
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        driver.findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        selectFrame(IFRAMEPORTLET_NAME);
        driver.findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("FINAL", driver.findElement(By.xpath("//table[@id='row']/tbody/tr/td[4]")).getText());
        driver.switchTo().defaultContent();
        driver.findElement(By.name("imageField")).click();
    }

    protected void testTermLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Term Parameters");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testTermSpecificationLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Context");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testAddingNamespace() throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        waitForPageToLoad();
        assertElementPresentByXpath("//*[@name='methodToCall.save' and @alt='save']", "save button does not exist on the page");
        
        //Enter details for Namespace.
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Adding PEANUTS");
        waitAndTypeByXpath("//*[@id='document.documentHeader.explanation']", "I want to add PEANUTS to test KIM");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", "PEANUTS");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "The Peanuts Gang");
        checkByXpath("//input[@id='document.newMaintainableObject.active']");
        waitAndClickByXpath("//*[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        checkForIncidentReport();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]", "Document is not saved successfully");
        
        //checks it is saved and initiator is admin.
        SeleneseTestBase.assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        SeleneseTestBase.assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
    }

    protected void testAddingBrownGroup() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndCreateNew();
        waitForPageToLoad();
        String docId = waitForDocId();
        
        //Enter details for BrownGroup.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Brown Group");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Brown Group to test KIM");
        selectOptionByName("document.groupNamespace", "KR-IDM");
        waitForPageToLoad();
        String groupName = "BrownGroup " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.groupName", groupName);
        checkByName("document.active");
        waitAndClickByXpath("//*[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]","Document is not saved successfully");
        checkForIncidentReport();
        
        //checks it is saved and initiator is admin.
        SeleneseTestBase.assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        SeleneseTestBase.assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        waitAndClickByLinkText("Group");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndTypeByName("name", groupName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(groupName);
    }

    protected void testDocTypeLookup() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//input[@title='Search Parent Name']");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        waitAndClickByXpath("//table[@id='row']/tbody/tr[contains(td[3],'RiceDocument')]/td[1]/a");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        SeleneseTestBase.assertEquals("RiceDocument", getTextByXpath("//table[@id='row']/tbody/tr/td[4]/a"));
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("name", "Kuali*D");
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]");
        String docIdOld = getTextByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]/td[2]/a");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("label", "KualiDocument");
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[5], 'KualiDocument')]");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("documentTypeId", docIdOld);
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[2], '" + docIdOld + "')]");
    }

    protected List<String> testCreateNewParameter(String docId, String parameterName) throws Exception
    {
        waitForPageToLoad();
        docId = waitForDocId();
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndTypeByName("document.newMaintainableObject.value", "Y");
        waitAndTypeByName("document.newMaintainableObject.description", "for testing");
        selectOptionByName("document.newMaintainableObject.parameterTypeCode", "HELP");
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.evaluationOperatorCode' and @value='A']");
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Parameter Created-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        
        return params;
    }

    protected List<String> testLookUpParameter(String docId, String parameterName) throws Exception
    {
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals("Y", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("--------------------------------Lookup And View Successful-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        
        return params;     
    }

    protected List<String> testEditParameter(String docId, String parameterName) throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.value");
        waitAndTypeByName("document.newMaintainableObject.value", "N");
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Edited-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        return params;
    }

    protected List<String> testVerifyEditedParameter(String docId, String parameterName) throws Exception
    {
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        
        return params;
    }

    protected List<String> testCopyParameter(String docId, String parameterName) throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Edited-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        
        return params;
    }

    protected List<String> testVerifyCopyParameter(String docId, String parameterName) throws Exception
    {
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        
        return params;
    }

    protected List<String> testCreateNewParameterType(String docId, String parameterType, String parameterCode)throws Exception
    {
        waitForPageToLoad();
        docId = waitForDocId();
        
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter Type");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Parameter Type Created-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testLookUpParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim().toLowerCase());
        SeleneseTestBase.assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim().toLowerCase());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testEditParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Type Edited-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testVerifyEditedParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim().toLowerCase());
        SeleneseTestBase.assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim().toLowerCase());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testCopyParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Type Edited-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testVerifyCopyParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim().toLowerCase());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        
        return params;
    }

    protected List<String> testCreateNewPermission(String docId, String permissionName) throws Exception
    {
        waitForPageToLoad();
        Thread.sleep(2000);
        docId = waitForDocId();
        waitAndClickByXpath(SAVE_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'Document Description (Description) is a required field.')]/img[@alt='error']");
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Adding Permission removeme");
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath("//div[@class='error']");
        assertElementPresentByXpath("//div[contains(.,'Template (Template) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Namespace (Permission Namespace) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Name (Permission Name) is a required field.')]/img[@alt='error']");
        System.out.println("------------------------------------Validation Test Successful--------------------------");
        selectOptionByName("document.newMaintainableObject.templateId", "36");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-SYS");
        permissionName = "removeme" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", permissionName);
        waitAndTypeByName("document.newMaintainableObject.description", "namespaceCode=KR*");
        checkByName("document.newMaintainableObject.active");
        waitAndClickByXpath(SAVE_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        SeleneseTestBase.assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        SeleneseTestBase.assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out.println("------------------------------------Permission document submitted successfully--------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        
        return params;
    }

    protected List<String> testLookUpPermission(String docId, String permissionName) throws Exception
    {
        waitForPageToLoad();
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(permissionName);
        System.out.println("----------------------------------Lookup successful-----------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        
        return params;
    }

    protected List<String> testEditPermission(String docId, String permissionName) throws Exception
    {
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Editing Permission removeme");
        uncheckByName("document.newMaintainableObject.active");
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        System.out.println("------------------------------------Inactivation of Permission successfull--------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        
        return params;
    }

    protected List<String> testVerifyPermission(String docId, String permissionName) throws Exception
    {
        waitForPageToLoad();
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath("//input[@title='Active Indicator - No']");
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(permissionName);
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        
        return params;
    }

    protected List<String> testCreateNewPerson(String docId, String personName) throws Exception
    {
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Adding Charlie Brown");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Charlie Brown to test KIM");
        
        //here You should also check for lower case validation for principalName, but it is skipped for now as there is an incident report error there.
        personName = "cbrown" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.principalName", personName);
        waitAndClickByXpath(SAVE_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        SeleneseTestBase.assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'At least one affiliation must be entered.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'At least one name must be entered.')]/img[@alt='error']");
        System.out.println("------------------------------------Validation Test Successful--------------------------");     
        selectOptionByName("newAffln.affiliationTypeCode", "STDNT");
        selectOptionByName("newAffln.campusCode", "BL");
        checkByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitForPageToLoad();
        Thread.sleep(3000);
        selectOptionByName("newName.nameCode", "PRM");
        selectOptionByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "Charlie");
        waitAndTypeByName("newName.lastName", "Brown");
        checkByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitForPageToLoad();
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        SeleneseTestBase.assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out.println("------------------------------------Person document submitted successfully--------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);
        
        return params;
    }

    protected List<String> testLookUpPerson(String docId, String personName) throws Exception
    {
        waitForPageToLoad();
        waitAndTypeByName("principalName", personName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("firstName", "Charlie");
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("lastName", "Brown");
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("campusCode", "BL");
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(personName);
        System.out.println("----------------------------------Lookup successful-----------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);
        
        return params;
    }

    protected List<String> testVerifyPerson(String docId, String personName) throws Exception
    {
        waitAndClickByLinkText(personName);
        waitForPageToLoad();
        Thread.sleep(5000);
        switchToWindow("Kuali :: Person");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(personName, getTextByXpath("//div[@class='tab-container']/table//tr[2]/td[1]/div").trim());
        SeleneseTestBase.assertEquals("BL - BLOOMINGTON", getTextByXpath("//div[@class='tab-container']/table[3]//tr[2]/td[2]/div").trim());
        SeleneseTestBase.assertEquals("Student", getTextByXpath("//select/option[@selected]").trim());
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickByName("methodToCall.showAllTabs");
        Thread.sleep(3000);
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Membership']");
        waitAndClickByName("methodToCall.hideAllTabs");
        Thread.sleep(3000);
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("------------------------------------Viewing from Inquiry Framework Test Successful--------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);
        
        return params;
    }

    protected void testContraintsIT() throws Exception {
        checkForIncidentReport("field9");
        fireEvent("field9", "focus");
        waitAndTypeByName("field9", "1");
        fireEvent("field9", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field9", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field9", "focus");
        clearTextByName("field9");
        waitAndTypeByName("field9", "12345");
        fireEvent("field9", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field9", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field10", "focus");
        waitAndTypeByName("field10", "2");
        fireEvent("field10", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10", "focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "51");
        fireEvent("field10", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10", "focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "25");
        fireEvent("field10", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field6", "focus");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field7", "B");
        fireEvent("field7", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "");
        fireEvent("field6", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field7");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field14", "A");
        fireEvent("field14", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field11", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "");
        fireEvent("field11", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field12");
        waitAndTypeByName("field12", "A");
        fireEvent("field12", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field13");
        waitAndTypeByName("field13", "A");
        fireEvent("field13", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field13", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field11", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field18", "A");
        fireEvent("field18", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field15", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "");
        fireEvent("field15", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field16", "A");
        fireEvent("field16", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field17");
        waitAndTypeByName("field17", "A");
        fireEvent("field17", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field17", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field23", "A");
        fireEvent("field23", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "A");
        fireEvent("field19", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "");
        fireEvent("field19", "blur");
        waitAndTypeByName("field20", "B");
        fireEvent("field20", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "");
        fireEvent("field20", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field21");
        waitAndTypeByName("field21", "C");
        fireEvent("field21", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field22");
        waitAndTypeByName("field22", "D");
        fireEvent("field22", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "D");
        fireEvent("field19", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "D");
        fireEvent("field20", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        clearTextByName("field25");
        waitAndTypeByName("field25", "");
        fireEvent("field25", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        
        for (int second = 0;; second++) {           
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        fireEvent("field24", "blur");
        clearTextByName("field25");
        waitAndTypeByName("field25", "$100");
        fireEvent("field25", "blur");
        
        for (int second = 0;; second++) {            
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case2']");
        fireEvent("field24", "blur");
        
        for (int second = 0;; second++) {            
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field25");
        waitAndTypeByName("field25", "A100");
        fireEvent("field25", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        
        for (int second = 0;; second++) {            
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {               
                if (getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "501");
        fireEvent("field26", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "499");
        fireEvent("field26", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        
        for (int second = 0;; second++) {           
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {               
                if (getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        clearTextByName("field27");
        waitAndTypeByName("field27", "A");
        fireEvent("field27", "blur");
        clearTextByName("field28");
        waitAndTypeByName("field28", "");
        fireEvent("field28", "blur");
        
        for (int second = 0;; second++) {           
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {               
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        
        for (int second = 0;; second++) {            
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field28");
        waitAndTypeByName("field28", "B");
        fireEvent("field28", "blur");
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        
        for (int second = 0;; second++) {        
            if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {               
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field31");
        waitAndTypeByName("field31", "B");
        clearTextByName("field32");
        waitAndTypeByName("field32", "B");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field33");
        waitAndTypeByName("field33", "B");
        fireEvent("field33", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field32");
        waitAndTypeByName("field32", "A");
        clearTextByName("field33");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        passed();
    }

    protected void deleteSubCollectionLine() throws Exception {
        // click on collections page link
        waitAndClickByLinkText("Collections");
        Thread.sleep(5000);
        
        // wait for collections page to load by checking the presence of a sub collection line item       
        waitForElementPresentByName("list4[0].subList[0].field1");
        
        // change a value in the line to be deleted
        waitAndTypeByName("list4[0].subList[0].field1", "selenium");
        
        // click the delete button
        waitAndClickByXpath("//div[@id='collection4_disclosureContent']/div[@class='uif-stackedCollectionLayout']/div[@class='uif-group uif-gridGroup uif-collectionItem uif-gridCollectionItem']/table/tbody/tr[5]/td/div/fieldset/div/div[@class='uif-disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button");
        Thread.sleep(2000);
        
        // confirm that the input box containing the modified value is not present
        for (int second = 0;; second++) {            
            if (second >= waitSeconds)
                fail("timeout");
            try {
                System.out.println("Loop ----- " + second);                
                if (!"selenium".equals(getAttributeByName("list4[0].subList[0].field1", "value")))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        
        // verify that the value has changed for the input box in the line that has replaced the deleted one
        assertNotSame("selenium", getAttributeByName("list4[0].subList[0].field1", "value"));
        passed();
    }

    protected void testUifTooltip(String NAME_FIELD_1, String NAME_FIELD_2) throws Exception {
        // check if tooltip opens on focus
        fireEvent(NAME_FIELD_1, "focus");
        fireMouseOverEventByName(NAME_FIELD_1);
        
        // SeleneseTestBase.assertTrue(isVisible("div.jquerybubblepopup.jquerybubblepopup-black") && isVisible("td.jquerybubblepopup-innerHtml"));
        SeleneseTestBase.assertEquals("This tooltip is triggered by focus or and mouse over.",getText("td.jquerybubblepopup-innerHtml"));

        // check if tooltip closed on blur
        fireEvent(NAME_FIELD_1, "blur");
        SeleneseTestBase.assertFalse(isVisible("div.jquerybubblepopup.jquerybubblepopup-black") && isVisible("td.jquerybubblepopup-innerHtml"));
        Thread.sleep(5000);
        fireEvent("field119", "focus");
        
        // check if tooltip opens on mouse over
        fireMouseOverEventByName(NAME_FIELD_2);        
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check if tooltip closed on mouse out
        waitAndTypeByName(NAME_FIELD_2, "a");
        Thread.sleep(5000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check that default tooltip does not display when there are an error message on the field
        waitAndTypeByName(NAME_FIELD_1, "1");
        fireEvent(NAME_FIELD_1, "blur");
        fireMouseOverEventByName(NAME_FIELD_1);
        Thread.sleep(10000);
        SeleneseTestBase.assertTrue("https://jira.kuali.org/browse/KULRICE-8141 Investigate why UifTooltipIT.testTooltip fails around jquerybubblepopup",
                        isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']") &&
                                !(isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-black']")));
       
        // TODO figure out this last assert     
        passed();
    }

    protected void testValidCharsConstraintIT() throws Exception {
        fireEvent("field50", "focus");
        waitAndTypeByName("field50", "12.333");
        fireEvent("field50", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field50", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field50", "focus");
        waitAndTypeByName("field50", "123.33");
        fireEvent("field50", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field50", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field51", "focus");
        waitAndTypeByName("field51", "A");
        fireEvent("field51", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field51", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field51", "focus");
        waitAndTypeByName("field51", "-123.33");
        fireEvent("field51", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field51", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field77", "focus");
        waitAndTypeByName("field77", "1.1");
        fireEvent("field77", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field77", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field77", "focus");
        waitAndTypeByName("field77", "12");
        fireEvent("field77", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field77", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field52", "focus");
        waitAndTypeByName("field52", "5551112222");
        fireEvent("field52", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field52", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field52", "focus");
        waitAndTypeByName("field52", "555-111-1111");
        fireEvent("field52", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field52", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field53", "focus");
        waitAndTypeByName("field53", "1ClassName.java");
        fireEvent("field53", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field53", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field53", "focus");
        waitAndTypeByName("field53", "ClassName.java");
        fireEvent("field53", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field53", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field54", "focus");
        waitAndTypeByName("field54", "aaaaa");
        fireEvent("field54", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field54", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field54", "focus");
        waitAndTypeByName("field54", "aaaaa@kuali.org");
        fireEvent("field54", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field54", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field84", "focus");
        waitAndTypeByName("field84", "aaaaa");
        fireEvent("field84", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field84", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field84", "focus");
        waitAndTypeByName("field84", "http://www.kuali.org");
        fireEvent("field84", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field84", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field55", "focus");
        waitAndTypeByName("field55", "023512");
        fireEvent("field55", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field55", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field55", "focus");
        waitAndTypeByName("field55", "022812");
        fireEvent("field55", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field55", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field75", "focus");
        waitAndTypeByName("field75", "02/35/12");
        fireEvent("field75", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field75", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field75", "focus");
        waitAndTypeByName("field75", "02/28/12");
        fireEvent("field75", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field75", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field82", "focus");
        waitAndTypeByName("field82", "13:22");
        fireEvent("field82", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field82", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field82", "focus");
        waitAndTypeByName("field82", "02:33");
        fireEvent("field82", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field82", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field83", "focus");
        waitAndTypeByName("field83", "25:22");
        fireEvent("field83", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field83", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field83", "focus");
        waitAndTypeByName("field83", "14:33");
        fireEvent("field83", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field83", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field57", "focus");
        waitAndTypeByName("field57", "0");
        fireEvent("field57", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field57", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field57", "focus");
        waitAndTypeByName("field57", "2020");
        fireEvent("field57", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field57", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field58", "focus");
        waitAndTypeByName("field58", "13");
        fireEvent("field58", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field58", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field58", "focus");
        waitAndTypeByName("field58", "12");
        fireEvent("field58", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field58", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field61", "focus");
        waitAndTypeByName("field61", "5555-444");
        fireEvent("field61", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field61", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field61", "focus");
        waitAndTypeByName("field61", "55555-4444");
        fireEvent("field61", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field61", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field62", "focus");
        waitAndTypeByName("field62", "aa5bb6_a");
        fireEvent("field62", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field62", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field62", "focus");
        waitAndTypeByName("field62", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        fireEvent("field62", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field62", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field63", "focus");
        waitAndTypeByName("field63", "fff555$");
        fireEvent("field63", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field63", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field63", "focus");
        waitAndTypeByName("field63", "aa22 _/");
        fireEvent("field63", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field63", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field64", "focus");
        waitAndTypeByName("field64", "AABB55");
        fireEvent("field64", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field64", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field64", "focus");
        waitAndTypeByName("field64", "ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z");
        fireEvent("field64", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field64", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field76", "focus");
        waitAndTypeByName("field76", "AA~BB%");
        fireEvent("field76", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field76", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field76", "focus");
        waitAndTypeByName("field76", "abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?");
        fireEvent("field76", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field76", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field65", "focus");
        waitAndTypeByName("field65", "sdfs$#$# dsffs");
        fireEvent("field65", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field65", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field65", "focus");
        waitAndTypeByName("field65", "sdfs$#$#sffs");
        fireEvent("field65", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field65", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field66", "focus");
        waitAndTypeByName("field66", "abcABCD");
        fireEvent("field66", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field66", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field66", "focus");
        waitAndTypeByName("field66", "ABCabc");
        fireEvent("field66", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field66", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field67", "focus");
        waitAndTypeByName("field67", "(111)B-(222)A");
        fireEvent("field67", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field67", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field67", "focus");
        waitAndTypeByName("field67", "(12345)-(67890)");
        fireEvent("field67", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field67", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field68", "focus");
        waitAndTypeByName("field68", "A.66");
        fireEvent("field68", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field68", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field68", "focus");
        waitAndTypeByName("field68", "a.4");
        fireEvent("field68", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field68", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field56", "focus");
        waitAndTypeByName("field56", "2020-06-02");
        fireEvent("field56", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field56", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field56", "focus");
        waitAndTypeByName("field56", "2020-06-02 03:30:30.22");
        fireEvent("field56", "blur");
        SeleneseTestBase.assertTrue(getAttributeByName("field56", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        passed();
    }

    protected void testSubCollectionSize() throws Exception {
        checkForIncidentReport("link=Collections");
        
        // click on collections page link
        waitAndClickByLinkText("Collections");
        
        // wait for collections page to load by checking the presence of a sub collection line item
        for (int second = 0;; second++) {                   
            if (second >= waitSeconds)
                fail("timeout");
            try {                
                if (getText("div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span")
                        .equals("SubCollection - (3 lines)"))
                {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        
        // verify that sub collection sizes are displayed as expected
        SeleneseTestBase.assertEquals("SubCollection - (3 lines)", getText(
                "div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span"));
        SeleneseTestBase.assertEquals("SubCollection - (2 lines)", getTextByXpath(
                "//a[@id='subCollection1_line1_toggle']/span"));
    }

    protected void verifyRichMessagesValidationBasicFunctionality() throws Exception
    {
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field1']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//a[contains(text(), 'Kuali')]"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='field2']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationAdvancedFunctionality() throws Exception
    {
        //Color Options
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//span[@style='color: green;']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//span[@style='color: blue;']"));

        //Css class
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//span[@class='fl-text-underline fl-text-larger']"));

        //Combinations
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field3']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//select[@name='field4']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//button[contains(text(), 'Action Button')]"));

        //Rich Message Field
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/span[contains(., 'Color')]"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/i/b[contains(., 'Html')]"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/img[@class='uif-image inlineBlock']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationLettersNumbersValidation() throws Exception
    {
        //For letters only Validation
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field5']"));
        waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']");
        waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc12");
        waitAndTypeByXpath("//input[@name= 'field6']", "");
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        Thread.sleep(3000);
        clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']");
        waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']","abc");
        waitAndTypeByXpath("//input[@name= 'field6']", "");

        //For numbers only validation
        waitAndTypeByXpath("//input[@name= 'field6']", "123");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath("//input[@name= 'field6']");
        waitAndTypeByXpath("//input[@name= 'field6']", "123ab");
        fireEvent("field6", "blur");
        Thread.sleep(5000);
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationRadioAndCheckBoxGroupFunctionality() throws Exception
    {
        //Radio Group
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='1']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='2']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='3']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='4']"));

        //Checkbox Group
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='1']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='2']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='3']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/label/div/select[@name='field4']"));

        //Checkbox Control
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='bField1']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field103']"));
    }

    protected void verifyRichMessagesValidationLinkDeclarationsFunctionality() throws Exception
    {
        //Testing link tag
        waitAndClickByXpath("//div[contains(., 'Testing link tag')]/a");
        Thread.sleep(9000);
        switchToWindow("Open Source Software | www.kuali.org");
        switchToWindow("Kuali :: Rich Messages");

        //Testing methodToCall Action
        waitAndClickByXpath("//div[contains(., 'Testing methodToCall action')]/a");
        Thread.sleep(3000);        
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));

        //Testing methodToCall action (no client validation check)
        waitAndClickByXpath("//div[contains(., 'Testing methodToCall action (no client validation check)')]/a");
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        Thread.sleep(3000);
    }

    protected void testDefaultTestsTableLayout() throws Exception {
        assertTableLayout();
        waitAndTypeByName("newCollectionLines['list1'].field1", "asdf1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "asdf2");
        waitAndTypeByName("newCollectionLines['list1'].field3", "asdf3");
        waitAndTypeByName("newCollectionLines['list1'].field4", "asdf4");
        waitAndClickByXpath("//button[contains(.,'add')]"); // the first button is the one we want

        for (int second = 0;; second++) {            
            if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {               
                if (getAttributeByName("newCollectionLines['list1'].field1", "value").equals(""))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        SeleneseTestBase.assertEquals("asdf1", getAttributeByName("list1[0].field1", "value"));
        SeleneseTestBase.assertEquals("asdf2", getAttributeByName("list1[0].field2", "value"));
        SeleneseTestBase.assertEquals("asdf3", getAttributeByName("list1[0].field3", "value"));
        SeleneseTestBase.assertEquals("asdf4", getAttributeByName("list1[0].field4", "value"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Collections-Base-TableLayout_disclosureContent']/div/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        passed();
    }

    protected void assertTableLayout() {
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Table Layout"));
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Field 1"));
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Field 2"));
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Field 3"));
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Field 4"));
        SeleneseTestBase.assertTrue(driver.getPageSource().contains("Actions"));
    }

    /**
     * Test adding a column of values to the Add Blank Line Tests Table Layout
     */
    protected void testAddBlankLine() throws Exception {
        waitAndClickByLinkText("Add Blank Line");
        waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000); //  TODO a wait until the loading.gif isn't visible woudl be better
        assertElementPresentByName("list1[0].field1");
        assertTableLayout();
        SeleneseTestBase.assertEquals("", getAttributeByName("list1[0].field1", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("list1[0].field2", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("list1[0].field3", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("list1[0].field4", "value"));
        SeleneseTestBase.assertEquals("5", getAttributeByName("list1[1].field1", "value"));
        SeleneseTestBase.assertEquals("6", getAttributeByName("list1[1].field2", "value"));
        SeleneseTestBase.assertEquals("7", getAttributeByName("list1[1].field3", "value"));
        SeleneseTestBase.assertEquals("8", getAttributeByName("list1[1].field4", "value"));
        SeleneseTestBase.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        waitAndTypeByName("list1[0].field1", "1");
        waitAndTypeByName("list1[0].field2", "1");
        waitAndTypeByName("list1[0].field3", "1");
        waitAndTypeByName("list1[0].field4", "1");
        SeleneseTestBase.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        passed();
    }

    /**
     * Test action column placement in table layout collections
     */
    protected void testActionColumnPlacement() throws Exception {
        //Lack of proper locators its not possible to uniquely identify/locate this elements without use of ID's.
        //This restricts us to use the XPath to locate elements from the dome. 
        //This test is prone to throw error in case of any changes in the dom Html graph.
        waitAndClickByLinkText("Column Sequence");
        Thread.sleep(2000);
        
        //jiraAwareWaitAndClick("css=div.jGrowl-close");
        // check if actions column RIGHT by default
        //SeleneseTestBase.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection1']//tr[2]/td[6]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {            
            if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
                if (isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button"));

        // check if actions column is LEFT
        //SeleneseTestBase.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {           
            if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
                if (isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button"));

        // check if actions column is 3rd in a sub collection
        //SeleneseTestBase.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-subCollection2_line0']//tr[2]/td[3]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {            
            if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
                if (isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button"));
        passed();
    }

    protected void testAddViaLightbox() throws Exception {
        waitAndClickByLinkText("Add Via Lightbox");
        SeleneseTestBase.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000);
        waitAndTypeByXpath("//form/div/table/tbody/tr/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[2]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[3]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[4]/td/div/input", "1");
        waitAndClickByXpath("//button[@id='Collections-AddViaLightbox-TableTop_add']");
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        passed();
    }

    protected void testColumnSequence() throws Exception {
        waitAndClickByLinkText("Column Sequence");
        Thread.sleep(3000);
        waitAndTypeByName("newCollectionLines['list1'].field1", "1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "1");
        waitAndTypeByName("newCollectionLines['list1'].field3", "1");
        waitAndTypeByName("newCollectionLines['list1'].field4", "1");
        waitAndClick(By.id("Collections-ColumnSequence-TableDefault_add"));
        Thread.sleep(3000);

        //Check if row has been added really or not
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field1", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field2", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field3", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field4", "value"));

        //Check for the added if delete is present or not
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Collections-ColumnSequence-TableDefault_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        passed();
    }

    protected void testSequencerow() throws Exception {
        waitAndClickByLinkText("Save Row");
        Thread.sleep(3000);
        waitAndTypeByName("newCollectionLines['list1'].field1", "1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "1");
        waitAndTypeByName("newCollectionLines['list1'].field3", "1");
        waitAndTypeByName("newCollectionLines['list1'].field4", "1");
        waitAndClickByXpath("//button[contains(.,'add')]");
        Thread.sleep(3000);
        
        //Check if row has been added really or not
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        SeleneseTestBase.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field1", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field2", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field3", "value"));
        SeleneseTestBase.assertEquals("1", getAttributeByName("list1[0].field4", "value"));

        //Check for the added if delete is present or not
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        //        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[@class='uif-action uif-secondaryActionButton uif-smallActionButton uif-saveLineAction']"));
        passed();
    }

    //Code for KRAD Test Package.
    protected void testCollectionTotalling() throws Exception {
        //Scenario Asserts Changes in Total at client side        
        waitForElementPresent("div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']");
        SeleneseTestBase.assertEquals("Total: 419", getText(
                "div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']"));
        clearText("div#Demo-CollectionTotaling-Section1 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section1]  input[name='list1[0].field1']");
        waitAndType("div#Demo-CollectionTotaling-Section1 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section1]  input[name='list1[0].field1']","10");
        waitAndClick("div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']");
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("Total: 424", getText(
                "div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']"));
        
        //Scenario Asserts Changes in Total at client side on keyUp
        SeleneseTestBase.assertEquals("Total: 419", getText(
                "div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']"));
        clearText("div#Demo-CollectionTotaling-Section2 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section2] input[name='list1[0].field1']");
        waitAndType("div#Demo-CollectionTotaling-Section2 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section2] input[name='list1[0].field1']","9");
        waitAndClick("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']");
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("Total: 423", getText(
                "div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']"));

        //Asserts absence of Total in 2nd column at the footer for Demonstrating totaling on only some columns 
        SeleneseTestBase.assertEquals("", getTextByXpath("//div[3]/div[3]/table/tfoot/tr/th[2]"));
        
        //Asserts Presence of Total in 2nd column at the footer for Demonstrating totaling on only some columns 
        SeleneseTestBase.assertEquals("Total: 369", getTextByXpath(
                "//div[3]/div[3]/table/tfoot/tr/th[3]/div/fieldset/div/div[2]/div[2]"));

        //Asserts Presence of Total in left most column only being one with no totaling itself 
        SeleneseTestBase.assertEquals("Total:", getTextByXpath("//*[@id='u100213_span']"));
        SeleneseTestBase.assertEquals("419", getTextByXpath(
                "//div[4]/div[3]/table/tfoot/tr/th[2]/div/fieldset/div/div[2]/div[2]"));

        //Asserts changes in value in Total and Decimal for Demonstrating multiple types of calculations for a single column (also setting average to 3 decimal places to demonstrate passing data to calculation function) 
        SeleneseTestBase.assertEquals("Total: 382", getTextByXpath("//div[2]/div/fieldset/div/div[2]/div[2]"));
        clearText("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']");
        waitAndType("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']","11");
        waitAndClick("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']");
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("Total: 385", getTextByXpath("//div[2]/div/fieldset/div/div[2]/div[2]"));

        // Assert changes in Decimal..
        clearText("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']");
        waitAndType("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']","15.25");
        waitAndClick("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']");
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("Page Average: 11.917", getTextByXpath("//div[2]/fieldset/div/div[2]/div"));
    }

    protected void testConfigurationTestView(String idPrefix) throws Exception {
        waitForElementPresentByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']");
        
        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = getAttributeByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']", "style");
        
        // log.info("styleValue is " + styleValue);
        SeleneseTestBase.assertTrue(idPrefix + "textInputField label does not contain expected style", styleValue.replace(" ", "").contains("color:red"));
        
        // get current list of options
        String refreshTextSelectLocator = "//select[@id='" + idPrefix + "RefreshTextField_control']";
        String[] options1 = getSelectOptionsByXpath(refreshTextSelectLocator);
        String dropDownSelectLocator = "//select[@id='" + idPrefix + "DropDown_control']";
        selectByXpath(dropDownSelectLocator, "Vegetables");
        Thread.sleep(3000);
        
        //get list of options after change
        String[] options2 = getSelectOptionsByXpath(refreshTextSelectLocator);
        
        //verify that the change has occurred
        SeleneseTestBase.assertFalse(
                "Field 1 selection did not change Field 2 options https://jira.kuali.org/browse/KULRICE-8163 Configuration Test View Conditional Options doesn't change Field 2 options based on Field 1 selection",
                options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));
        
        //confirm that control gets disabled
        selectByXpath(dropDownSelectLocator, "None");
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals("true", getAttributeByXpath(refreshTextSelectLocator, "disabled"));
        passed();
    }

    /**
     * verify that add line controls are present
     */
    protected void confirmAddLineControlsPresent(String idPrefix, String addLineIdSuffix) {
        String[] addLineIds = {"StartTime", "StartTimeAmPm", "AllDay"};

        for (String id : addLineIds) {
            String tagId = "//*[@id='" + idPrefix + id + addLineIdSuffix + "']";
            SeleneseTestBase.assertTrue("Did not find id " + tagId, isElementPresentByXpath(tagId));
        }
    }

    protected void testAddLineWithSpecificTime(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        String startTimeId = "//*[@id='" + idPrefix + "StartTime" + addLineIdSuffix + "']";
        String inputTime = "7:06";
        waitAndTypeByXpath(startTimeId, inputTime);
        String amPmSelectLocator = "//*[@id='" + idPrefix + "StartTimeAmPm" + addLineIdSuffix + "']";
        selectByXpath(amPmSelectLocator, "PM");
        SeleneseTestBase.assertEquals("PM", getAttributeByXpath(amPmSelectLocator, "value"));
        Thread.sleep(5000); //allow for ajax refresh        
        waitAndClickByXpath("//button");
        Thread.sleep(5000); //allow for line to be added
        
        //confirm that line has been added
        SeleneseTestBase.assertTrue("line (//input[@value='7:06'])is not present https://jira.kuali.org/browse/KULRICE-8162 Configuration Test View Time Info add line button doesn't addline",
                        isElementPresentByXpath("//input[@value='7:06']"));
        passed();
    }

    protected void testAddLineWithAllDay(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        String startTimeId = "//*[@id='" + idPrefix + "StartTime" + addLineIdSuffix + "']";
        String inputTime = "5:20";
        waitAndTypeByXpath(startTimeId, inputTime);
        String allDaySelector = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickByXpath(allDaySelector);
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClick("div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added           
        passed();
    }

    protected void testAddLineAllDay(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        
        //store number of rows before adding the lines
        String cssCountRows = "div#ConfigurationTestView-ProgressiveRender-TimeInfoSection.uif-group div#ConfigurationTestView-ProgressiveRender-TimeInfoSection_disclosureContent.uif-disclosureContent table tbody tr";
        int rowCount = (getCssCount(cssCountRows));
        String allDayId = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickByXpath(allDayId);
        waitAndClick("div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added
        
        //confirm that line has been added (by checking for the new delete button)
        assertEquals("line was not added", rowCount + 1, (getCssCount(cssCountRows)));
        passed();
    }

    protected void testTravelAccountTypeLookup() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);

        //Blank Search
        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
        Thread.sleep(4000);
        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'CAT')]");
        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'EAT')]");
        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'IAT')]");

        //search with each field
        waitAndTypeByName("lookupCriteria[accountTypeCode]", "CAT");
        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
        Thread.sleep(2000);
        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'CAT')]");
        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[2]");
        Thread.sleep(2000);
        waitAndTypeByName("lookupCriteria[name]", "Expense Account Type");
        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
        Thread.sleep(4000);
        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'EAT')]");

        //Currently No links available for Travel Account Type Inquiry so cant verify heading and values.
    }

    protected void testValidCharacterConstraint() throws Exception {
        waitAndClickByXpath("//a[contains(text(),'Validation - Regex')]");
        
        //---------------------------------------------Fixed Point------------------------------//       
        waitAndTypeByName("field50", "123.123");
        fireEvent("field50", "blur");
        validateErrorImage(true);
        clearTextByName("field50");
        waitAndTypeByXpath("//input[@name='field50']", "1234.4");
        fireEvent("field50", "blur");
        validateErrorImage(true);
        clearTextByName("field50");
        waitAndTypeByXpath("//input[@name='field50']", "1234.434");
        fireEvent("field50", "blur");
        validateErrorImage(true);
        clearTextByName("field50");
        waitAndTypeByXpath("//input[@name='field50']", "123.67");
        fireEvent("field50", "blur");
        validateErrorImage(false);
        clearTextByName("field50");

        //---------------------------------------------Floating Point------------------------------//     
        waitAndTypeByXpath("//input[@name='field51']", "127.");
        fireEvent("field51", "blur");
        validateErrorImage(true);
        clearTextByName("field51");
        waitAndTypeByXpath("//input[@name='field51']", "1234()98");
        fireEvent("field51", "blur");
        validateErrorImage(true);
        clearTextByName("field51");
        waitAndTypeByXpath("//input[@name='field51']", "-123.67");
        fireEvent("field51", "blur");
        validateErrorImage(false);
        clearTextByName("field51");

        //---------------------------------------------Integer Pattern constraint------------------------------//       
        waitAndTypeByXpath("//input[@name='field77']", "127.");
        fireEvent("field77", "blur");
        validateErrorImage(true);
        clearTextByName("field77");
        waitAndTypeByXpath("//input[@name='field77']", "1234.4123");
        fireEvent("field77", "blur");
        validateErrorImage(true);
        clearTextByName("field77");
        waitAndTypeByXpath("//input[@name='field77']", "123E123");
        fireEvent("field77", "blur");
        validateErrorImage(true);
        clearTextByName("field77");
        waitAndTypeByXpath("//input[@name='field77']", "-123");
        fireEvent("field77", "blur");
        validateErrorImage(false);
        clearTextByName("field77");

        //---------------------------------------------Phone Text------------------------------//
        waitAndTypeByXpath("//input[@name='field52']", "1271231234");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "123-123-123");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "12-12-123445");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "1234-12-1234");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "123.123.1234");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "123-123-12345");
        fireEvent("field52", "blur");
        validateErrorImage(true);
        clearTextByName("field52");
        waitAndTypeByXpath("//input[@name='field52']", "123-123-1234");
        fireEvent("field52", "blur");
        validateErrorImage(false);
        clearTextByName("field52");

        //---------------------------------------------JavaClass Text------------------------------//
        waitAndTypeByXpath("//input[@name='field53']", "127");
        fireEvent("field53", "blur");
        validateErrorImage(true);
        clearTextByName("field53");
        waitAndTypeByXpath("//input[@name='field53']", "TestJava!@#Class");
        fireEvent("field53", "blur");
        validateErrorImage(true);
        clearTextByName("field53");
        waitAndTypeByXpath("//input[@name='field53']", "Test JavaClass");
        fireEvent("field53", "blur");
        validateErrorImage(true);
        clearTextByName("field53");
        waitAndTypeByXpath("//input[@name='field53']", "Test JavaClass");
        fireEvent("field53", "blur");
        validateErrorImage(true);
        clearTextByName("field53");
        waitAndTypeByXpath("//input[@name='field53']", "TestJavaClass");
        fireEvent("field53", "blur");
        validateErrorImage(false);
        clearTextByName("field53");

        //---------------------------------------------Email Text------------------------------//
        waitAndTypeByXpath("//input[@name='field54']", "123@123.123");
        fireEvent("field54", "blur");
        validateErrorImage(true);
        clearTextByName("field54");
        waitAndTypeByXpath("//input[@name='field54']", "email.com@emailServer");
        fireEvent("field54", "blur");
        validateErrorImage(true);
        clearTextByName("field54");
        waitAndTypeByXpath("//input[@name='field54']", "emailemailServer@.com");
        fireEvent("field54", "blur");
        validateErrorImage(true);
        clearTextByName("field54");
        waitAndTypeByXpath("//input[@name='field54']", "email@emailServercom");
        fireEvent("field54", "blur");
        validateErrorImage(true);
        clearTextByName("field54");
        waitAndTypeByXpath("//input[@name='field54']", "email@emailServer.com");
        fireEvent("field54", "blur");
        validateErrorImage(false);
        clearTextByName("field54");

        //---------------------------------------------URL pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field84']", "www.google.com");
        fireEvent("field84", "blur");
        validateErrorImage(true);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "https:www.google.com");
        fireEvent("field84", "blur");
        validateErrorImage(true);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "ftp://www.google.comsdfa123!#@");
        fireEvent("field84", "blur");
        validateErrorImage(true);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "ftp:/www.google.coms");
        fireEvent("field84", "blur");
        validateErrorImage(true);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "ftp://www.google.com");
        fireEvent("field84", "blur");
        validateErrorImage(false);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "https://www.google.com");
        fireEvent("field84", "blur");
        validateErrorImage(false);
        clearTextByName("field84");
        waitAndTypeByXpath("//input[@name='field84']", "http://www.google.com");
        fireEvent("field84", "blur");
        validateErrorImage(false);
        clearTextByName("field84");

        //---------------------------------------------Date pattern Text------------------------------//
        //-------------invalid formats
        waitAndTypeByXpath("//input[@name='field55']", "12/12/2112 12:12:87 am");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12-12-2112 12:12 am");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12-12-2112 12:12");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12/12/2112 12:12");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12-12-2112 12:12:78");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12 Sept");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "Sept 12 12:12");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "221299 12:12:13");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "111222 12:12");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "9/9/2012 12:12 am");
        fireEvent("field55", "blur");
        validateErrorImage(true);
        clearTextByName("field55");

        //-------------valid formats      
        waitAndTypeByXpath("//input[@name='field55']", "09/09/2012 12:12 pm");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "090923");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "Sept 12");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "2034");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12/12/2012 23:12:59");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "12-12-12 23:12:59");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "121212 23:12:32");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "Sept 12 23:45:50");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");
        waitAndTypeByXpath("//input[@name='field55']", "2011 12:23:32");
        fireEvent("field55", "blur");
        validateErrorImage(false);
        clearTextByName("field55");

        //---------------------------------------------BasicDate pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field75']", "12122012");
        fireEvent("field75", "blur");
        validateErrorImage(true);
        clearTextByName("field75");
        waitAndTypeByXpath("//input[@name='field75']", "13-12-34");
        fireEvent("field75", "blur");
        validateErrorImage(true);
        clearTextByName("field75");
        waitAndTypeByXpath("//input[@name='field75']", "12:12:2034");
        fireEvent("field75", "blur");
        validateErrorImage(true);
        clearTextByName("field75");
        waitAndTypeByXpath("//input[@name='field75']", "12-12-2034");
        fireEvent("field75", "blur");
        validateErrorImage(false);
        clearTextByName("field75");

        //---------------------------------------------Time12H Pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field82']", "13:00:12");
        fireEvent("field82", "blur");
        validateErrorImage(true);
        clearTextByName("field82");
        waitAndTypeByXpath("//input[@name='field82']", "09:00:");
        fireEvent("field82", "blur");
        validateErrorImage(true);
        clearTextByName("field82");
        waitAndTypeByXpath("//input[@name='field82']", "3-00:12");
        fireEvent("field82", "blur");
        validateErrorImage(true);
        clearTextByName("field82");
        waitAndTypeByXpath("//input[@name='field82']", "3:00:34");
        fireEvent("field82", "blur");
        validateErrorImage(false);
        clearTextByName("field82");
        waitAndTypeByXpath("//input[@name='field82']", "3:00");
        fireEvent("field82", "blur");
        validateErrorImage(false);
        clearTextByName("field82");

        //---------------------------------------------Time24H Pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field83']", "24:00:12");
        fireEvent("field83", "blur");
        validateErrorImage(true);
        clearTextByName("field83");
        waitAndTypeByXpath("//input[@name='field83']", "14:00:");
        fireEvent("field83", "blur");
        validateErrorImage(true);
        clearTextByName("field83");
        waitAndTypeByXpath("//input[@name='field83']", "13:00:76");
        fireEvent("field83", "blur");
        validateErrorImage(true);
        clearTextByName("field83");
        waitAndTypeByXpath("//input[@name='field83']", "13:00:23");
        fireEvent("field83", "blur");
        validateErrorImage(false);
        clearTextByName("field83");
        waitAndTypeByXpath("//input[@name='field83']", "23:00:12");
        fireEvent("field83", "blur");
        validateErrorImage(false);
        clearTextByName("field83");

        //---------------------------------------------Timestamp pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field56']", "1000-12-12 12:12:12.103");
        fireEvent("field56", "blur");
        validateErrorImage(true);
        clearTextByName("field56");
        waitAndTypeByXpath("//input[@name='field56']", "2000/12/12 12-12-12.87");
        fireEvent("field56", "blur");
        validateErrorImage(true);
        clearTextByName("field56");
        waitAndTypeByXpath("//input[@name='field56']", "2000/12/12 12-12-12.87");
        fireEvent("field56", "blur");
        validateErrorImage(true);
        clearTextByName("field56");
        waitAndTypeByXpath("//input[@name='field56']", "2011-08-12 12:12:12");
        fireEvent("field56", "blur");
        validateErrorImage(true);
        clearTextByName("field56");

        //--------this should not be allowed
        /*
        clearTimeStampText();
        waitAndType("//input[@name='field56']", "2999-12-12 12:12:12.103");
        focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        */
        waitAndTypeByXpath("//input[@name='field56']", "2099-12-12 12:12:12.103");
        fireEvent("field56", "blur");
        validateErrorImage(false);
        clearTextByName("field56");

        //---------------------------------------------Year Pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field57']", "1599");
        fireEvent("field57", "blur");
        validateErrorImage(true);
        clearTextByName("field57");
        waitAndTypeByXpath("//input[@name='field57']", "2200");
        fireEvent("field57", "blur");
        validateErrorImage(true);
        clearTextByName("field57");
        waitAndTypeByXpath("//input[@name='field57']", "20000");
        fireEvent("field57", "blur");
        validateErrorImage(true);
        clearTextByName("field57");
        waitAndTypeByXpath("//input[@name='field57']", "-202");
        fireEvent("field57", "blur");
        validateErrorImage(true);
        clearTextByName("field57");
        waitAndTypeByXpath("//input[@name='field57']", "2000");
        fireEvent("field57", "blur");
        validateErrorImage(false);
        clearTextByName("field57");

        //---------------------------------------------Month Pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field58']", "0");
        fireEvent("field58", "blur");
        validateErrorImage(true);
        clearTextByName("field58");
        waitAndTypeByXpath("//input[@name='field58']", "-12");
        fireEvent("field58", "blur");
        validateErrorImage(true);
        clearTextByName("field58");
        waitAndTypeByXpath("//input[@name='field58']", "100");
        fireEvent("field58", "blur");
        validateErrorImage(true);
        clearTextByName("field58");
        waitAndTypeByXpath("//input[@name='field58']", "12");
        fireEvent("field58", "blur");
        validateErrorImage(false);
        clearTextByName("field58");

        //---------------------------------------------ZipCode Pattern Text------------------------------//
        waitAndTypeByXpath("//input[@name='field61']", "123");
        fireEvent("field61", "blur");
        validateErrorImage(true);
        clearTextByName("field61");
        waitAndTypeByXpath("//input[@name='field61']", "2341 12");
        fireEvent("field61", "blur");
        validateErrorImage(true);
        clearTextByName("field61");
        waitAndTypeByXpath("//input[@name='field61']", "0-1231");
        fireEvent("field61", "blur");
        validateErrorImage(true);
        clearTextByName("field61");
        waitAndTypeByXpath("//input[@name='field61']", "12345");
        fireEvent("field61", "blur");
        validateErrorImage(false);
        clearTextByName("field61");

        //---------------------------------------------Alpha Numeric w/o options Text------------------------------//
        waitAndTypeByXpath("//input[@name='field62']", "123 23 @#");
        fireEvent("field62", "blur");
        validateErrorImage(true);
        clearTextByName("field62");
        waitAndTypeByXpath("//input[@name='field62']", "-asd123");
        fireEvent("field62", "blur");
        validateErrorImage(true);
        clearTextByName("field62");
        waitAndTypeByXpath("//input[@name='field62']", "asd/123");
        fireEvent("field62", "blur");
        validateErrorImage(true);
        clearTextByName("field62");
        waitAndTypeByXpath("//input[@name='field62']", "asd123");
        fireEvent("field62", "blur");
        validateErrorImage(false);
        clearTextByName("field62");

        //---------------------------------------------Alpha Numeric with options Text------------------------------//
        waitAndTypeByXpath("//input[@name='field63']", "123^we");
        fireEvent("field63", "blur");
        validateErrorImage(true);
        clearTextByName("field63");
        waitAndTypeByXpath("//input[@name='field63']", "-123_asd");
        fireEvent("field63", "blur");
        validateErrorImage(true);
        clearTextByName("field63");
        waitAndTypeByXpath("//input[@name='field63']", "123 23 @#");
        fireEvent("field63", "blur");
        clearTextByName("field63");
        waitAndTypeByXpath("//input[@name='field63']", "as_de 456/123");
        fireEvent("field63", "blur");
        validateErrorImage(false);
        clearTextByName("field63");

        //---------------------------------------------Alpha with Whitespace and commas Text------------------------------//
        waitAndTypeByXpath("//input[@name='field64']", "123^we");
        fireEvent("field64", "blur");
        validateErrorImage(true);
        clearTextByName("field64");
        waitAndTypeByXpath("//input[@name='field64']", "asd_pqr");
        fireEvent("field64", "blur");
        validateErrorImage(true);
        clearTextByName("field64");
        waitAndTypeByXpath("//input[@name='field64']", "asd/def");
        fireEvent("field64", "blur");
        validateErrorImage(true);
        clearTextByName("field64");
        waitAndTypeByXpath("//input[@name='field64']", "asd ,pqr");
        fireEvent("field64", "blur");
        validateErrorImage(false);
        clearTextByName("field64");

        //---------------------------------------------AlphaPatterrn with disallowed charset Text------------------------------//
        waitAndTypeByXpath("//input[@name='field76']", "123");
        fireEvent("field76", "blur");
        validateErrorImage(true);
        clearTextByName("field76");
        waitAndTypeByXpath("//input[@name='field76']", "`abcd`");
        fireEvent("field76", "blur");
        validateErrorImage(true);
        clearTextByName("field76");
        waitAndTypeByXpath("//input[@name='field76']", "|abcd|");
        fireEvent("field76", "blur");
        validateErrorImage(true);
        clearTextByName("field76");
        waitAndTypeByXpath("//input[@name='field76']", "~abcd~");
        fireEvent("field76", "blur");
        validateErrorImage(true);
        clearTextByName("field76");
        waitAndTypeByXpath("//input[@name='field76']", " ab_c d_ef ");
        fireEvent("field76", "blur");
        validateErrorImage(false);
        clearTextByName("field76");

        //---------------------------------------------Anything with No Whitespace Text------------------------------//
        waitAndTypeByXpath("//input[@name='field65']", "123 ^we");
        fireEvent("field65", "blur");
        validateErrorImage(true);
        clearTextByName("field65");
        waitAndTypeByXpath("//input[@name='field65']", "123^we!@#^&*~:");
        fireEvent("field65", "blur");
        validateErrorImage(false);
        clearTextByName("field65");

        //---------------------------------------------CharacterSet Text------------------------------//
        waitAndTypeByXpath("//input[@name='field66']", "123 ^we");
        fireEvent("field66", "blur");
        validateErrorImage(true);
        clearTextByName("field66");
        waitAndTypeByXpath("//input[@name='field66']", "123_^we");
        fireEvent("field66", "blur");
        validateErrorImage(true);
        clearTextByName("field66");
        waitAndTypeByXpath("//input[@name='field66']", "abc ABC");
        fireEvent("field66", "blur");
        validateErrorImage(true);
        clearTextByName("field66");
        waitAndTypeByXpath("//input[@name='field66']", "aAbBcC");
        fireEvent("field66", "blur");
        validateErrorImage(false);
        clearTextByName("field66");

        //---------------------------------------------Numeric Character Text------------------------------//
        waitAndTypeByXpath("//input[@name='field67']", "123 ^we");
        fireEvent("field67", "blur");
        validateErrorImage(true);
        clearTextByName("field67");
        waitAndTypeByXpath("//input[@name='field67']", "123/10");
        fireEvent("field67", "blur");
        validateErrorImage(true);
        clearTextByName("field67");
        waitAndTypeByXpath("//input[@name='field67']", "(123.00)");
        fireEvent("field67", "blur");
        validateErrorImage(true);
        clearTextByName("field67");
        waitAndTypeByXpath("//input[@name='field67']", "(12-3)");
        fireEvent("field67", "blur");
        validateErrorImage(false);
        clearTextByName("field67");

        //---------------------------------------------Valid Chars Custom Text------------------------------//
        waitAndTypeByXpath("//input[@name='field68']", "123.123");
        fireEvent("field68", "blur");
        validateErrorImage(true);
        clearTextByName("field68");
        waitAndTypeByXpath("//input[@name='field68']", "a.b");
        fireEvent("field68", "blur");
        validateErrorImage(true);
        clearTextByName("field68");
        waitAndTypeByXpath("//input[@name='field68']", "123 qwe");
        fireEvent("field68", "blur");
        validateErrorImage(true);
        clearTextByName("field68");
        waitAndTypeByXpath("//input[@name='field68']", "5.a");
        fireEvent("field68", "blur");
        validateErrorImage(true);
        clearTextByName("field68");
        waitAndTypeByXpath("//input[@name='field68']", "a.0,b.4");
        fireEvent("field68", "blur");
        validateErrorImage(true);
        clearTextByName("field68");
        waitAndTypeByXpath("//input[@name='field68']", "a.0");
        fireEvent("field68", "blur");
        validateErrorImage(false);
        clearTextByName("field68");
        passed();
    }

    protected void validateErrorImage(boolean validateVisible) throws Exception {
        Thread.sleep(500);
        
        for (int second = 0;; second++) {            
        	if (second >= 5)
                SeleneseTestBase.fail("timeout");
            try {                
                if (validateVisible) {                   
                    if (isElementPresentByXpath("//input[@aria-invalid]"))
                        ;
                    break;
                } else {                    
                    if (!isElementPresentByXpath("//input[@aria-invalid]"))
                        break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        
        if (validateVisible) {
            SeleneseTestBase.assertTrue(isElementPresentByXpath("//input[@aria-invalid]"));
        } else {
            SeleneseTestBase.assertTrue(!isElementPresentByXpath("//input[@aria-invalid]"));
        }
    }

    //Code for Validation Messages package tests.
    protected void testClientErrors() throws Exception {
        fireEvent("field1", "focus");
        fireEvent("field1", "blur");
        System.out.println("This is value ----------------" + getAttributeByName("field1", "aria-invalid"));
        Thread.sleep(3000);
        fireMouseOverEventByName("field1");
        SeleneseTestBase.assertEquals("true", getAttributeByName("field1", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByName("field1", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireMouseOverEventByName("field1");
        
        for (int second = 0;; second++) {            
        	if (second >= 10) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        waitAndTypeByName("field1", "a");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        
        for (int second = 0;; second++) {            
        	if (second >= 10) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (!isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertFalse(isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']"));
        fireEvent("field1", "blur");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field1' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByName("field1", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field2", "focus");
        fireEvent("field2", "blur");
        fireMouseOverEventByName("field2");
        SeleneseTestBase.assertEquals("true", getAttributeByName("field2", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByName("field2", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field2", "focus");
        waitAndTypeByName("field2", "a");
        fireEvent("field2", "blur");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field2' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByName("field2", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//textarea[@name='field2']/../img[@alt='Error']"));
        fireEvent("field3", "focus");
        fireEvent("field3", "blur");
        fireMouseOverEventByName("field3");
        SeleneseTestBase.assertEquals("true", getAttributeByName("field3", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByName("field3", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field3", "focus");
        selectByName("field3", "Option 1");
        fireEvent("field3", "blur");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field3' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByName("field3", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//select[@name='field3']/../img[@alt='Error']"));
        fireEvent("field114", "focus");
        fireMouseOverEventByName("field114");
        driver.findElement(By.name("field114")).findElements(By.tagName("option")).get(0).click();
        fireEvent("field114", "blur");
        SeleneseTestBase.assertEquals("true", getAttributeByName("field114", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByName("field114", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field114", "focus");
        selectByName("field114", "Option 1");
        fireEvent("field114", "blur");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field114' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByName("field114", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//select[@name='field114']/../img[@alt='Error']"));
        fireEvent("field117", "3", "focus");
        uncheckByXpath("//*[@name='field117' and @value='3']");
        fireEvent("field117", "blur");
        fireMouseOverEventByName("field117");
        
        for (int second = 0;; second++) {            
        	if (second >= 10) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (isElementPresentByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertEquals("true", getAttributeByXpath("//*[@name='field117' and @value='1']", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByXpath("//*[@name='field117' and @value='1']", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field117", "3", "focus");
        checkByXpath("//*[@name='field117' and @value='3']");
        fireEvent("field117", "3", "blur");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (!isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field117' and @value='3' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByXpath("//*[@name='field117' and @value='3']", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']"));
        fireEvent("bField1", "focus");
        uncheckByName("bField1");
        fireEvent("bField1", "blur");
        fireMouseOverEventByName("bField1");
        SeleneseTestBase.assertEquals("true", getAttributeByName("bField1", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByName("bField1", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("bField1", "focus");
        checkByName("bField1");
        fireEvent("bField1", "blur");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='bField1' and @aria-invalid]"));
        SeleneseTestBase.assertTrue(getAttributeByName("bField1", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//input[@name='bField1' and following-sibling::img[@alt='Error']]"));
        fireEvent("field115", "3", "focus");
        uncheckByXpath("//*[@name='field115' and @value='3']");
        uncheckByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");
        fireMouseOverEventByName("field115");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (isElementPresentByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertEquals("true", getAttributeByXpath("//*[@name='field115' and @value='1']", "aria-invalid"));
        SeleneseTestBase.assertTrue(getAttributeByXpath("//*[@name='field115' and @value='1']", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        SeleneseTestBase.assertTrue(isTextPresent("Required"));
        fireEvent("field115", "3", "focus");
        checkByXpath("//*[@name='field115' and @value='3']");
        checkByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (!isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@name='field115' and @value='3' and @aria-invalid]"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']"));
        passed();
    }

    protected void testServerErrorsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Error Messages')]");
        waitForPageToLoad();
        Thread.sleep(5000);
        assertElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-errorMessageItem");
        waitIsVisibleByXpath("//div[@data-headerfor='Demo-ValidationLayout-Section1']");
        assertElementPresentByXpath("//*[@data-messageitemfor='Demo-ValidationLayout-Section1' and @class='uif-errorMessageItem']");
        assertElementPresent("div[data-role=\"InputField\"] img[alt=\"Error\"]");
        assertElementPresentByXpath("//a[contains(.,'Section 1 Title')]");
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        assertElementPresent(".uif-errorMessageItem-field");
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        Thread.sleep(2000);
        waitIsVisible(".jquerybubblepopup-innerHtml");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        waitIsVisible(".jquerybubblepopup-innerHtml");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "t");

        for (int second = 0;; second++) {            
        	if (second >= waitSeconds) {
                SeleneseTestBase.fail("timeout");
            }
            try {                
            	if (!isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        SeleneseTestBase.assertFalse(isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        passed();
    }

    protected void testServerInfoIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Info Messages')]");
        waitIsVisibleByXpath("//div[@data-messagesfor='Demo-ValidationLayout-SectionsPage']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//div[@data-messagesfor='Demo-ValidationLayout-SectionsPage']"));
        SeleneseTestBase.assertTrue(isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-infoMessageItem"));
        SeleneseTestBase.assertTrue(isVisible("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
        SeleneseTestBase.assertTrue(isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-infoMessageItem"));
        SeleneseTestBase.assertTrue(isElementPresentByXpath("//div[@data-role='InputField']//img[@alt='Information']"));
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        SeleneseTestBase.assertTrue(isElementPresent(".uif-infoHighlight"));
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
            	if (isVisible(".jquerybubblepopup-innerHtml"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
        SeleneseTestBase.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
            	if (isVisible(".jquerybubblepopup-innerHtml"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        for (int second = 0;; second++) {            
            if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
            	if (isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        SeleneseTestBase.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        waitAndTypeByName("field1", "b");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        
        for (int second = 0;; second++) {            
        	if (second >= waitSeconds)
                SeleneseTestBase.fail("timeout");
            try {                
                if (!isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        
        fireEvent("field1", "blur");
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(!isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        SeleneseTestBase.assertFalse(isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        fireEvent("field1", "focus");
        clearTextByName("field1");
        fireEvent("field1", "blur");
        SeleneseTestBase.assertTrue(isElementPresent("div.uif-hasError"));
        SeleneseTestBase.assertTrue(isElementPresent("img[src*=\"error.png\"]"));
        passed();
    }

    protected void testServerWarningsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Warning Messages')]");
        waitForPageToLoad();
        Thread.sleep(3000);
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] not visible",
                isVisible("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        "div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem not present",
                        isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                "div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] not visible", isVisible(
                "div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                "div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem not present",
                isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                "div[data-role=\"InputField\"] img[alt=\"Warning\"] not present", isElementPresent(
                "div[data-role=\"InputField\"] img[alt=\"Warning\"]"));
        fireMouseOverEvent(By.xpath("//a[contains(.,'Field 1')]"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                ".uif-warningHighlight no present when //a[contains(.,'Field 1')] is moused over",
                isElementPresent(".uif-warningHighlight"));
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        waitForElementVisible(".jquerybubblepopup-innerHtml", " after click on //a[contains(.,'Field 1')]");
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                ".jquerybubblepopup-innerHtml > .uif-serverMessageItems not visible", isVisible(
                ".jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                ".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible",
                isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        waitForElementVisible(".jquerybubblepopup-innerHtml",
                " not visible after typing nothing in name=field1 then firing blur and focus events");
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        ".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events",
                        isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));
        waitForElementVisible(".jquerybubblepopup-innerHtml> .uif-clientMessageItems",
                " not visible after typing nothing in name=field1 then firing blur and focus events");
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        ".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events",
                        isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        waitAndTypeByName("field1", "b");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        waitForElementVisible(".jquerybubblepopup-innerHtml> .uif-serverMessageItems", "");
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        ".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing b in name=field1 then firing blur and focus events",
                        isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(".jquerybubblepopup-innerHtml > .uif-clientMessageItems",
                !isElementPresent(
                ".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        clearTextByName("field1");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        ".uif-hasError is not present after typing nothing in name=field1 and then firing focus and blur events",
                        isElementPresent(".uif-hasError"));
        com.thoughtworks.selenium.SeleneseTestBase.assertTrue(
                        "img[src*=\"error.png\"] is not present after typing nothing in name=field1 and then firing focus and blur events",
                        isElementPresent("img[src*=\"error.png\"]"));
        passed();
    }

    private void typeBlurFocus(String name, String text) throws InterruptedException {
        waitAndTypeByName(name, text);
        fireEvent(name, "blur");
        fireEvent(name, "focus");
    }

    protected void testCategoryLookUp() throws Exception {
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//button[contains(.,'earch')]");
        Thread.sleep(3000);
        waitForPageToLoad();
        driver.findElement(By.tagName("body")).getText().contains("Actions"); // there are no actions, but the header is the only unique text from searching
        
        // Category's don't have actions (yet)
        //waitAndClick("id=u80");
        //waitForPageToLoad();
        //waitAndClick("id=u86");
        //waitForPageToLoad();
        //selectWindow("null");
        //waitAndClick("xpath=(//input[@name='imageField'])[2]");
        //waitForPageToLoad();
        //passed();
    }

    protected void testCreateSampleEDocLite() throws Exception {
        waitForPageToLoad();
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForPageToLoad();
        
        // click on the create new.
        waitAndClickByLinkText("Create Document");
        waitForPageToLoad();
        Thread.sleep(3000);
        String docId = getTextByXpath("//table/tbody/tr[4]/td[@class='datacell1']");
        waitAndTypeByName("userName", "Viral Chauhan");
        waitAndTypeByName("rqstDate", "12/03/2020");
        checkByName("fundedBy");
        waitAndTypeByName("addText", "Note Added.");
        waitAndClickByXpath("//td[@class='datacell']/div/img");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@value='submit']");
        SeleneseTestBase.assertEquals(Boolean.FALSE,(Boolean) isElementPresentByXpath("//input[@value='submit']"));
        SeleneseTestBase.assertEquals(Boolean.FALSE, (Boolean) isElementPresentByXpath("//input[@value='save']"));
        SeleneseTestBase.assertEquals(Boolean.FALSE,(Boolean) isElementPresentByXpath("//input[@value='cancel']"));
        waitForPageToLoad();
        selectTopFrame();
        waitAndClickByXpath("//img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForPageToLoad();
        isElementPresent(By.linkText(docId));
    }

    protected void testTermLookUp() throws Exception {
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        
        // Mixed capitalization
        waitAndClick(By.xpath("//button[contains(text(),'Search')]"));
        waitAndClickByLinkText("edit", "edit button not present does user " + user + " have permission?");
        checkForIncidentReport("submit");
        assertTextPresent("ubmit");
        assertTextPresent("ave");
        assertTextPresent("pprove");
        assertTextPresent("lose");
        assertTextPresent("ancel");
        assertTextPresent("Term Parameters");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testWorkFlowRouteRulesBlanketApp() throws Exception {
        waitForPageToLoad();
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        
        // click on the create new button
        waitAndClickByXpath("//img[@alt='create new']");
        waitForPageToLoad();
        
        // lookup on the Document Type Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        
        // type in the name field the text RoutingRuleDocument
        waitAndTypeByName("name", "RoutingRuleDocument");
        
        // click the search button
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        
        // click the return value link
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        
        // lookup on the Rule Template Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        
        // type in the name field the text RuleRoutingTemplate
        waitAndTypeByName("name", "RuleRoutingTemplate");
        
        // click the search button
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        
        // click the return value link
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        
        // click the create new button
        waitAndClickByName("methodToCall.createRule");
        waitForPageToLoad();
        String docId = waitForDocId();
        SeleneseTestBase.assertTrue(isElementPresentByName("methodToCall.cancel"));
       
        // type in the Document Overview Description the text Test Routing Rule
        waitAndTypeByXpath(DOCUMENT_DESCRIPTION_XPATH, "Test Routing Rule");
       
        // click the Force Action checkbox
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.forceAction']");
       
        // type in the Description text area the text Test Routing Rule1
        waitAndTypeByXpath("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1");
       
        // type in the Document type name field the text DocumentTypeDocument
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.fieldValues(1321~docTypeFullName)']",
                "DocumentTypeDocument");
        
        // lookup on Person
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor15");
        waitForPageToLoad();
       
        // click the search button
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        
        // click the return value
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        
        // select from the Action Request ACKNOWLEDGE
        selectByXpath("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']",
                "ACKNOWLEDGE");
        
        // type in the Priority field the text 1
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");
       
        // click the add button
        waitAndClickByName("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!).(:::;15;:::).anchor15");
        waitForPageToLoad();
        checkForIncidentReport("methodToCall.blanketApprove");
        waitAndClickByName("methodToCall.blanketApprove");
        waitForPageToLoad();
        driver.switchTo().defaultContent(); //selectWindow("null");
        Thread.sleep(2000);
        waitAndClickByXpath("//img[@alt='doc search']");
        waitForPageToLoad();
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        SeleneseTestBase.assertTrue(isElementPresent(By.linkText(docId)));
        
        if (isElementPresent(By.linkText(docId))) {
            assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"), "https://jira.kuali.org/browse/KULRICE-9051 WorkFlow Route Rules Blanket Approval submit status results in Enroute, not Final");
        } else {
            SeleneseTestBase.assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]"));
            SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }

    protected void testEditRouteRulesDelegation() throws Exception {
        waitForPageToLoad();
        Thread.sleep(3000);
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(isElementPresentByName("methodToCall.cancel"));
        waitAndClickByName("methodToCall.cancel");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        passed();
    }

    protected void testCreateNewRRDTravelRequestDestRouting() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClick("img[alt=\"create new\"]");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.RuleBaseValues!!).(((id:parentRuleId))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        waitAndClickByXpath("//td[@class='infoline']/input[@name='methodToCall.search']");
        waitForPageToLoad();
        waitAndClick("a[title=\"return valueRule Id=1046 \"]");
        waitForPageToLoad();
        waitAndClickByName("parentResponsibilityId");
        waitAndClickByName("methodToCall.createDelegateRule");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.cancel");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        driver.switchTo().defaultContent();
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        waitForPageToLoad();
        passed();
    }

    protected void testWorkFlowRouteRulesCreateNew() throws Exception {
        waitForPageToLoad();
        Thread.sleep(5000);
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//img[@alt='create new']");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.cancel","https://jira.kuali.org/browse/KULRICE-8161 Work Flow Route Rules cancel new yields 404 not found");
   
        // KULRICE-7753 : WorkFlowRouteRulesIT cancel confirmation missing from create new Route Rules.
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.processAnswer.button0",
                "https://jira.kuali.org/browse/KULRICE-7753 : WorkFlowRouteRulesIT cancel confirmation missing from create new Route Rules.");
        passed();
    }

    /**
     * tests that a Routing Rule maintenance document is created for an edit operation originating
     * from a lookup screen
     */
    protected void testWorkFlowRouteRulesEditRouteRules() throws Exception {
        waitForPageToLoad();
        SeleneseTestBase.assertEquals("Kuali Portal Index", getTitle());
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.cancel");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.processAnswer.button0");
        passed();
    }
    
    protected List<String> testCreateNewComponent(String docId, String componentName, String componentCode) throws Exception
    {
        waitForPageToLoad();
        docId = waitForDocId();
        
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Component");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-IDM");
        componentCode = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.code", componentCode);
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        checkByName("document.newMaintainableObject.active");
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Component Created-------------------------");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    
    protected List<String> testLookUpComponent(String docId, String componentName, String componentCode) throws Exception
    {
        //Lookup
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("--------------------------------Lookup And View Successful-------------------------");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    protected List<String> testEditComponent(String docId, String componentName, String componentCode) throws Exception
    {
        //edit
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Component");
        clearTextByName("document.newMaintainableObject.name");
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Component Edited-------------------------");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    protected List<String> testVerifyEditedComponent(String docId, String componentName, String componentCode) throws Exception
    {
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    protected List<String> testCopyComponent(String docId, String componentName, String componentCode) throws Exception
    {
        //copy
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Component");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-IDM");
        componentCode = "test" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.code", componentCode);
        clearTextByName("document.newMaintainableObject.name");
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickByXpath(SAVE_XPATH);
        waitAndClickByXpath(SUBMIT_XPATH);
        waitForPageToLoad();
        assertElementPresentByXpath(DOCUMENT_SUBMIT_SUCCESS_MSG_XPATH,"Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath(SEARCH_XPATH);
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(docId, getTextByXpath(DOC_ID_TABLE_LINK_XPATH));
        SeleneseTestBase.assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Component Copied-------------------------");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    protected List<String> testVerifyCopyComponent(String docId, String componentName, String componentCode) throws Exception
    {
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath(SEARCH_XPATH);
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        SeleneseTestBase.assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        SeleneseTestBase.assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");        
        List<String> parameterList=new ArrayList<String>();
        parameterList.add(docId);
        parameterList.add(componentName);
        parameterList.add(componentCode);
        
        return parameterList;
    }
    
    /**
     * Test the tooltip and external help on the view
     */
    protected void testViewHelp() throws Exception {
        // test tooltip help
        fireMouseOverEventByXpath("//h1/span[@class='uif-headerText-span']");
        SeleneseTestBase.assertEquals("View help", getText("td.jquerybubblepopup-innerHtml"));

        // test external help
        waitAndClickByXpath("//input[@alt='Help for Configuration Test View']");
        Thread.sleep(5000);
        switchToWindow("Kuali Foundation");
        Thread.sleep(5000);
        switchToWindow("Kuali :: Configuration Test View");
    }

    /**
     * Test the tooltip and external help on the page
     */
    protected void testPageHelp() throws Exception {
        // test tooltip help
        fireMouseOverEventByXpath("//h2/span[@class='uif-headerText-span']");
        SeleneseTestBase.assertEquals("Sample text for page help", getText("td.jquerybubblepopup-innerHtml"));

        // test external help
        waitAndClickByXpath("//input[@alt='Help for Help Page']");
        Thread.sleep(5000);
        switchToWindow("Kuali Foundation");
        Thread.sleep(5000);      
        switchToWindow("Kuali :: Configuration Test View");
    }

    /**
     * Test the tooltip help on the section and fields
     */
    protected void testTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for section help - tooltip help')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for section help - tooltip help')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for field help - label left')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label left')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for field help - label right')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label right')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for field help - label top')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label top')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for standalone help widget tooltip which will never be rendered')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for standalone help widget tooltip which will never be rendered')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for field help - there is also an on-focus tooltip')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also an on-focus tooltip')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for on-focus event tooltip')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for on-focus event tooltip')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for check box help')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for check box help')]"));
        }
       
        // test tooltip help of section header
        fireMouseOverEventByXpath("//div[@id='ConfigurationTestView-Help-Section1']/div/h3[@class='uif-headerText']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for section help - tooltip help')]"));
        String javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" + "element[0].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for section help - tooltip help')]"));
        
        // verify that no external help exist
        SeleneseTestBase.assertFalse(isElementPresent("#ConfigurationTestView-Help-Section1 input.uif-helpImage"));
    
        // test tooltip help of field with label to the left
        fireMouseOverEventByXpath("//label[@id='field-label-left_label']");
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label left')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" +
                "element[1].style.display='none'"; 
        Thread.sleep(3000);
        ((JavascriptExecutor) driver).executeScript(javascript);
        System.out.println("==============="+isVisibleByXpath("//td[contains(text(),'Sample text for field help - label left')]"));
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label left')]"));
        
        // test tooltip help of field with label to the right
        fireMouseOverEventByXpath("//label[@id='field-label-right_label']");
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label righ')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" +"element[2].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label righ')]"));

        // test tooltip help of field with label to the top
        fireMouseOverEventByXpath("//label[@id='field-label-top_label']");
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label top')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" + "element[3].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - label top')]"));

        // verify that standalone help with tooltip is not rendered
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@id='standalone-help-not-rendered']"));

        // test tooltip help when it overrides a tooltip
        fireMouseOverEventByXpath("//label[@id='override-tooltip_label']");
        Thread.sleep(3000);
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip')]"));
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip')]")) {
            SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for label tooltip - this will not be rendered as it is overridden by the help tooltip')]"));
        }        
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" + "element[4].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also a tooltip on the label but it is overridden by the help tooltip')]"));

        // test tooltip help in conjunction with a focus event tooltip
        fireMouseOverEventByXpath("//input[@id='on-focus-tooltip_control']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for on-focus event tooltip')]"));
        fireMouseOverEventByXpath("//label[@id='on-focus-tooltip_label']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also an on-focus tooltip')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" +"element[5].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);                
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" + "element[6].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);    
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for field help - there is also an on-focus tooltip')]"));
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for on-focus event tooltip')]"));

        // test tooltip help against a check box - help contains html
        fireMouseOverEventByXpath("//label[@id='checkbox_label']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for check box help')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" + "element[7].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        Thread.sleep(3000);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for check box help')]"));
    }

    /**
     * Test the tooltip help on the sub-section and fields that are display only
     */
     protected void testDisplayOnlyTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for sub-section help')]")) {
            SeleneseTestBase.assertFalse(isVisible("//td[contains(text(),'Sample text for sub-section help')]"));
        }
        
        if (isElementPresentByXpath("//td[contains(text(),'Sample text for read only field help')]")) {
            SeleneseTestBase.assertFalse(isVisible("//td[contains(text(),'Sample text for read only field help')]"));
        }

        // test tooltip help of sub-section header
        fireMouseOverEventByXpath("//span[contains(text(),'Display only fields')]");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for sub-section help')]"));
        String javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" +
                "element[0].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
        SeleneseTestBase.assertFalse(isVisibleByXpath("//td[contains(text(),'Sample text for sub-section help')]"));

        // test tooltip help of display only data field
        fireMouseOverEventByXpath("//label[@for='display-field_control']");
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'Sample text for read only field help')]"));
        javascript="var element = document.getElementsByClassName('jquerybubblepopup jquerybubblepopup-black');" +
                "element[0].style.display='none'"; 
        ((JavascriptExecutor) driver).executeScript(javascript);
    }

    /**
     * Test the tooltip help on the section and fields with no content
     */
    protected void testMissingTooltipHelp() throws Exception {
        // verify that no tooltips are displayed initially
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@class='jquerybubblepopup jquerybubblepopup-black']"));

        // verify that no external help exist
        SeleneseTestBase.assertFalse(isElementPresent("#ConfigurationTestView-Help-Section2 input.uif-helpImage"));
       
        // test tooltip help of section header
        fireMouseOverEventByXpath("//div[@id='ConfigurationTestView-Help-Section2']/div");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@class='jquerybubblepopup jquerybubblepopup-black']"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@class='jquerybubblepopup jquerybubblepopup-black']"));

        // test tooltip help of field
        fireMouseOverEventByXpath("//label[@id='missing-tooltip-help_label']");
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@class='jquerybubblepopup jquerybubblepopup-black']"));
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//*[@class='jquerybubblepopup jquerybubblepopup-black']"));
    }
    
    /**
     * Assert that clicking an element causes a popup window with a specific URL
     *
     * @param by The locating mechanism of the element to be clicked
     * @param windowName The name of the popup window
     * @param url The URL of the popup window
     */
    protected void assertPopUpWindowUrl(By by, String windowName, String url) {
        driver.findElement(by).click();
        String parentWindowHandle = driver.getWindowHandle();
        // wait page to be loaded
        driver.switchTo().window(windowName).findElements(By.tagName("head"));
        SeleneseTestBase.assertEquals(url, driver.getCurrentUrl());
        driver.switchTo().window(parentWindowHandle);
    }
    

    /**
     * Test the tooltip and external help on the view
     */

    protected void testViewHelp2() throws Exception {
        // test tooltip help
        if (isElementPresentByXpath("//td[@class='jquerybubblepopup-innerHtml']")) {
            SeleneseTestBase.assertFalse(driver.findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).isDisplayed());
        }
        
        // test tooltip help
        fireMouseOverEventByXpath("//h1/span[@class='uif-headerText-span']");
        Thread.sleep(2000);        
        SeleneseTestBase.assertTrue(isVisibleByXpath("//td[contains(text(),'View help')]"));
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Configuration Test View\"]"), "HelpWindow", "http://www.kuali.org/");
    }

    /**
     * Test the external help on the section and fields
     */

    protected void testExternalHelp2() throws Exception {
        // test external help of section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for External Help\"]"), "HelpWindow", "http://www.kuali.org/?section");

        // test external help of field with label left
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-left-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?label_left");

        // test external help of field with label right
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-right-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?label_right");

        // test external help of field with label top and help URL from system parameters
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-top-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?system_parm");

        // test external help of standalone help widget
        assertPopUpWindowUrl(By.id("standalone-external-help"), "HelpWindow", "http://www.kuali.org/?widget_only");
    }

    /**
     * Test the external help on the sub-section and display only fields
     */

    protected void testDisplayOnlyExternalHelp2() throws Exception {
        // test external help of sub-section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Display only fields\"]"), "HelpWindow", "http://www.kuali.org/?sub_section");

        // test external help of display only data field
        assertPopUpWindowUrl(By.xpath("//div[@id='display-field-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?display_field");
    }

    /**
     * Test the external help on the section and fields with missing help URL
     */

    protected void testMissingExternalHelp2() throws Exception {
        // test external help of section is not rendered
        SeleneseTestBase.assertFalse(isElementPresent(By.cssSelector("input[title=\"Help for Missing External Help\"]")));

        // test external help of field with blank externalHelpURL is not rendered
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@id='external-help-externalHelpUrl-empty']/*[@class='uif-helpImage']"));

        // test external help of field with empty helpDefinition is not rendered
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@id='external-help-helpdefinition-empty']/*[@class='uif-helpImage']"));

        // test external help of field with missing system parameter is not rendered
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@id='external-help-system-parm-missing']/*[@class='uif-helpImage']"));

        // test external help of standalone help widget is not rendered
        SeleneseTestBase.assertFalse(isElementPresentByXpath("//div[@id='standalone-external-help-missing']"));
    }
    
    protected void testTravelAccountLookup() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);

        //Blank Search
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        Thread.sleep(2000);

        //  --------------------------------Further code will not run due to page exception------------------------------------
        //assertElementPresentByLinkText("a1");
        //ssertElementPresentByLinkText("a2");
        //assertElementPresentByLinkText("a3");
        
        //QuickFinder Lookup
        //waitAndTypeByName("lookupCriteria[number]", "a*");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertElementPresentByLinkText("a1");
        //assertElementPresentByLinkText("a2");
        //assertElementPresentByLinkText("a3");
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);        
        
        //search with each field
        //waitAndTypeByName("lookupCriteria[number]", "a2");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertElementPresentByLinkText("a2");
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);               
        // waitAndTypeByName("lookupCriteria[foId]", "1");
        //waitAndClickByXpath("//*[@id='u18']");
        //Thread.sleep(2000);
        //assertEquals("1", getTextByXpath("//table[@id='u27']//tr//td[8]").trim().substring(0, 1));
        //waitAndClickByXpath("//button[@id='u19']");
        //Thread.sleep(2000);        
        //selectOptionByName("lookupCriteria[extension.accountTypeCode]", "CAT");
        //waitAndClickByXpath("//*[@id='u18']");
        //waitAndClickByXpath("//table[@id='u27']//tr//td[2]//a");
        //Thread.sleep(2000);
        //selectTopFrame();
        //Thread.sleep(5000);
        //WebElement iframe1= driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        //driver.switchTo().frame(iframe1);
        //assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        //assertEquals("CAT - Clearing Account Type", getTextByXpath("//*[@id='u44_control']").trim());
        //waitAndClickByXpath("//button[@id='u13']");
        //selectFrame("iframeportlet");
    }

    protected void testInquiry() throws Exception {
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndTypeByName("lookupCriteria[number]", "a1");
        waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        selectTopFrame();
        Thread.sleep(5000);
        WebElement iframe1 = driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        driver.switchTo().frame(iframe1);
        SeleneseTestBase.assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        assertElementPresentByLinkText("a1");
        waitAndClickByXpath("//button[@id='u16']"); // close
        selectFrame(IFRAMEPORTLET_NAME);
        waitAndClickByXpath("//button[contains(text(),'Clear Values')]");

        //        -----------------------------Code will not work as page has freemarker exceptions------------------------
        //Thread.sleep(2000);
        //waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        //Alert a1 = driver.switchTo().alert();
        //assertEquals("Please enter a value in the appropriate field.", a1.getText());
        //a1.accept();
        //switchToWindow("null");
        //selectFrame("iframeportlet");

        //No Direct Inquiry Option for Fiscal Officer.
        //waitAndTypeByName("lookupCriteria[foId]", "1");
        //waitAndClickByXpath("//*[@id='u229']");
        //selectTopFrame();
        //Thread.sleep(5000);
        //WebElement iframe2 = driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        //driver.switchTo().frame(iframe2);
        //assertEquals("Fiscal Officer Lookup", getTextByXpath("//h1/span").trim());
        //assertEquals("1", getAttributeByName("lookupCriteria[id]", "value"));
        //waitAndClickByXpath("//div[contains(button, 'Search')]/button[3]");
        //selectFrame("iframeportlet");
        //selectOptionByName("lookupCriteria[extension.accountTypeCode]", "CAT");
        //waitAndClickByXpath("//fieldset[@id='u232_fieldset']/input[@alt='Search Field']");
        //selectTopFrame();
        //Thread.sleep(5000);
        //WebElement iframe3 = driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        //driver.switchTo().frame(iframe3);
        //assertEquals("Travel Account Type Lookup", getTextByXpath("//h1/span").trim());
        //assertEquals("CAT", getAttributeByName("lookupCriteria[accountTypeCode]", "value"));
        //waitAndClickByXpath("//div[contains(button, 'Search')]/button[3]");
        //selectFrame("iframeportlet");       
    }
    
    /**
     * Dismiss the javascript alert (clicking Cancel)
     *
    */
    protected void dismissAlert()
    {
        Alert alert = driver.switchTo().alert();
        //update is executed
        alert.dismiss();
    }
    
    /**
     * Accept the javascript alert (clicking OK)
     *
    */
    protected void acceptAlert()
    {
        Alert alert = driver.switchTo().alert();
        //update is executed
        alert.accept();
    }
    
    protected void testDirtyFieldsCheck() throws Exception {
        checkForIncidentReport(getTestUrl());
        Thread.sleep(5000);        
        waitAndTypeByName("field1", "test 1");
        waitAndTypeByName("field102", "test 2");        
        assertCancelConfirmation(); 
    
        // testing manually
        waitForElementPresentByName("field100");
        waitAndTypeByName("field100", "here");
        waitAndTypeByName("field103", "there");
        
        // 'Validation' navigation link
        assertCancelConfirmation();
    
        // testing manually
        waitForElementPresentByName("field106");
        
        // //Asserting text-field style to uppercase. This style would display
        // input text in uppercase.
        SeleneseTestBase.assertEquals("text-transform: uppercase;",getAttributeByName("field112", "style"));
        assertCancelConfirmation(); 
        waitForElementPresentByName("field101");
        SeleneseTestBase.assertEquals("val", getAttributeByName("field101","value"));
        clearTextByName("field101");
        waitAndTypeByName("field101", "1");
        waitAndTypeByName("field104", "");
        SeleneseTestBase.assertEquals("1", getAttributeByName("field101","value"));
        waitAndTypeByName("field104", "2");
        
        // 'Progressive Disclosure' navigation link
        assertCancelConfirmation();                                    
    }
    
    private void assertCancelConfirmation() throws InterruptedException {
        waitAndClickByLinkText("Cancel");
        dismissAlert();
    }
    
    protected void testFiscalOfficerInfoMaintenanceNew() throws Exception {
      selectFrame(IFRAMEPORTLET_NAME);
      checkForIncidentReport("", "https://jira.kuali.org/browse/KULRICE-7723 FiscalOfficerInfoMaintenanceNewIT.testUntitled need a better name and user permission error");
      String docId = getTextByXpath("//*[@id='u13_control']");
      waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");
      waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.id']", "5");
      waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.userName']", "Jigar");      
      waitAndClickByXpath("//button[@id='usave']");      
      Integer docIdInt = Integer.valueOf(docId).intValue(); 
      selectTopFrame();
      waitAndClickByXpath("//img[@alt='action list']");
      selectFrame(IFRAMEPORTLET_NAME);
      
      if(isElementPresentByLinkText("Last")){
          waitAndClickByLinkText("Last");
          waitAndClickByLinkText(docIdInt.toString());
      } else {                                  
          waitAndClickByLinkText(docIdInt.toString());
      }
            
		//      ------------------------------- Not working in code when click docId link in list--------------------------
      //Thread.sleep(5000); 
      //String[] windowTitles = getAllWindowTitles();
      //selectWindow(windowTitles[1]);
      //windowFocus();
      //assertEquals(windowTitles[1], getTitle());
      //checkForIncidentReport("Action List Id link opened window.", "https://jira.kuali.org/browse/KULRICE-9062 Action list id links result in 404 or NPE");
      
      //------submit-----//
      //selectFrame("relative=up");
      //waitAndClick("//button[@value='submit']");
      //waitForPageToLoad50000();
      //close();
      //------submit over---//        
      
      //----step 2----//  
      //selectWindow("null");
      //windowFocus();
      //waitAndClick("//img[@alt='doc search']");
      //waitForPageToLoad50000();
      //assertEquals(windowTitles[0], getTitle());
      //selectFrame("iframeportlet");
      //waitAndClick(SEARCH_XPATH);
      //waitForPageToLoad50000();
      //----step 2 over ----//
      
      //-----Step 3 verifies that doc is final-------//        
      //assertEquals("FINAL", getText("//table[@id='row']/tbody/tr[1]/td[4]"));
      //selectFrame("relative=up");
      //waitAndClick("link=Main Menu");
      //waitForPageToLoad50000();
      //assertEquals(windowTitles[0], getTitle());
      //System.out.println("---------------------- :: Test complete :: ----------------------");
      //-----Step 3 verified that doc is final -------
  }
}