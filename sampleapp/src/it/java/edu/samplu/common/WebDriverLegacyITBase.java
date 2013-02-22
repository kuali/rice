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

import org.apache.commons.lang.RandomStringUtils;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.junit.Assert.assertEquals;
import org.openqa.selenium.support.ui.Select;

/**
 * Class to upgrade UpgradedSeleniumITBase tests to WebDriver.
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverLegacyITBase { //implements com.saucelabs.common.SauceOnDemandSessionIdProvider {

    public static final int DEFAULT_WAIT_SEC = 60;
    public static final String REMOTE_PUBLIC_USERPOOL_PROPERTY = "remote.public.userpool";
    public static final String REMOTE_PUBLIC_USER_PROPERTY = "remote.public.user";

    public abstract String getTestUrl();

    protected WebDriver driver;
    protected String user = "admin";
    protected boolean passed = false;
    static ChromeDriverService chromeDriverService;

    public @Rule
    TestName testName = new TestName();

    String sessionId = null;

    public String getSessionId() {
        return sessionId;
    }

    @BeforeClass
    public static void createAndStartService() throws Exception {
        // this isn't so great... if the chrome driver isn't configured null is returned, but chromeDriverService needs to run before.
        chromeDriverService = WebDriverUtil.createAndStartService();
        if (chromeDriverService != null)
            chromeDriverService.start();
    }

    /**
     * Setup the WebDriver test, login and load the tested web page
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // {"test":"1","user":"1"}
        try {
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

    @After
    public void tearDown() throws Exception {
        try {
            //            if (System.getProperty(SauceLabsWebDriverHelper.SAUCE_PROPERTY) != null) {
            //                SauceLabsWebDriverHelper.tearDown(passed, sessionId, System.getProperty(SauceLabsWebDriverHelper.SAUCE_USER_PROPERTY), System.getProperty(SauceLabsWebDriverHelper.SAUCE_KEY_PROPERTY));
            //            }
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
            Assert.fail(name + " not present " + message);
        }
    }

    protected void assertElementPresentByXpath(String locator) {
        driver.findElement(By.xpath(locator));
    }

    protected void assertElementPresentByXpath(String locator, String message) {
        try {
            driver.findElement(By.xpath(locator));
        } catch (Exception e) {
            Assert.fail(locator + " not present " + message);
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
            Assert.fail(text + " not present " + message);
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

                //                if (selenium.isElementPresent("//div[@class='left-errmsg']/div")) {
                //                    errorText = errorText + " " + selenium.getText("//div[@class='left-errmsg']/div/div[1]");
                //                }
                Assert.fail(errorText);
            }
        }
        ITUtil.checkForIncidentReport(driver.getPageSource(), "//img[@alt='doc search']", "Blanket Approve failure");
        waitAndClickByXpath("//img[@alt='doc search']");
        assertEquals("Kuali Portal Index", driver.getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
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
        waitFor(by);
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
        driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_SEC, TimeUnit.SECONDS);
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
        if ("iframeportlet".equals(locator)) {
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
        selectFrame("iframeportlet");
        waitAndCreateNew();
        waitAndSearch();
        waitAndReturnValue();
        waitAndCancelConfirmation();
        passed();
    }

    protected void testSearchEditCancel() throws InterruptedException {
        selectFrame("iframeportlet");
        waitAndSearch();
        waitAndEdit();
        testCancelConfirmation();
    }

    protected void testVerifyAddDeleteFiscalOfficerLegacy() throws Exception {
        selectFrame("iframeportlet");
        waitAndTypeByName("document.documentHeader.documentDescription", ITUtil.DTS_TWO);
        waitAndTypeByName(
                "newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number",
                "1234567890");
        waitAndTypeByName(
                "newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");

        waitAndClickByXpath("//button[@data-loadingmessage='Adding Line...']");

        waitForElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");

        assertEquals(
                "1234567890",
                getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number",
                        "value"));
        assertEquals("2",
                getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId", "value"));

        waitAndClickByXpath("//button[@data-loadingmessage='Deleting Line...']");
        Thread.sleep(3000);
        assertEquals(Boolean.FALSE,
                isElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number"));
        passed();
    }

    protected void waitAndCancelConfirmation() throws InterruptedException {
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
    }

    protected void waitAndCreateNew() throws InterruptedException {
        waitAndClickByXpath("//img[@alt='create new']");
        //        waitAndClickByXpath("//a[@title='Create a new record']");
    }

    protected void waitAndEdit() throws InterruptedException {
        waitAndClickByLinkText("edit");
    }

    protected void waitAndReturnValue() throws InterruptedException {
        waitAndClickByLinkText("return value");
    }

    protected void waitAndSearch() throws InterruptedException {
        waitAndClickByXpath("//input[@value='search']");
        //        waitAndClickByXpath("//input[@name='methodToCall.search']");
        //        waitAndClick("input[alt='search']");
        //        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
    }

    protected String waitForDocId() throws InterruptedException {
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        return driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
    }

    protected void waitForElementPresent(String locator) throws InterruptedException {
        waitFor(By.cssSelector(locator));
    }

    protected void waitForElementPresentByXpath(String locator) throws InterruptedException {
        waitFor(By.xpath(locator));
    }

    protected void waitForElementPresentByName(String name) throws InterruptedException {
        waitFor(By.name(name));
    }

    protected void waitForTitleToEqualKualiPortalIndex() throws InterruptedException {
        waitForTitleToEqualKualiPortalIndex("");
    }

    protected void waitForTitleToEqualKualiPortalIndex(String message) throws InterruptedException {
        Thread.sleep(2000);
        // This started failing in CI....
        //        boolean failed = false;
        //        for (int second = 0;; second++) {
        //            Thread.sleep(1000);
        //            if (second >= 60) failed = true;
        //            try { if (failed || ITUtil.KUALI_PORTAL_TITLE.equals(driver.getTitle())) break; } catch (Exception e) {}
        //        }
        //        WebDriverUtil.checkForIncidentReport(driver, message); // after timeout to be sure page is loaded
        //        if (failed) fail("timeout of " + 60 + " seconds " + message);
    }

    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, "");
    }

    protected void waitForPageToLoad() {
        // noop webdriver doesn't it need it, except when it does...
    }

    protected void waitFor(By by) throws InterruptedException {
        waitFor(by, "");
    }

    protected void waitFor(By by, String message) throws InterruptedException {
        //        for (int second = 0;; second++) {
        Thread.sleep(1000);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        //            if (second >= DEFAULT_WAIT_SEC) fail(by.toString() + " " + message + " " + DEFAULT_WAIT_SEC + " sec timeout.");
        try {
            driver.findElement(by);
            //break;
        } catch (Exception e) {}
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //        }
    }

    protected void waitAndClick(By by) throws InterruptedException {
        waitAndClick(by, "");
    }

    protected void waitAndClick(By by, String message) throws InterruptedException {
        waitFor(by, message);
        try {
            (driver.findElement(by)).click();
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " " + message + " " + driver.getCurrentUrl());
            e.printStackTrace();
        }
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        waitAndClick(By.cssSelector(locator), message);
    }

    protected void waitAndClickByLinkText(String text) throws InterruptedException {
        waitAndClick(By.linkText(text), "");
    }

    protected void waitAndClickByLinkText(String text, String message) throws InterruptedException {
        waitAndClick(By.linkText(text), message);
    }

    protected void waitAndClickByName(String name) throws InterruptedException {
        waitAndClick(By.name(name), "");
    }

    protected void waitAndClickByXpath(String xpath) throws InterruptedException {
        waitAndClick(By.xpath(xpath));
    }

    protected void waitAndClickByName(String name, String message) throws InterruptedException {
        waitAndClick(By.name(name), message);
    }

    protected void waitAndClickByXpath(String xpath, String message) throws InterruptedException {
        waitAndClick(By.xpath(xpath), message);
    }

    protected void waitAndType(By by, String text) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " unable to type text '" + text + "' current url "
                    + driver.getCurrentUrl()
                    + "\n" + ITUtil.deLinespace(driver.getPageSource()));
            e.printStackTrace();
        }
    }

    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + "  unable to type text '" + text + "'  " + message
                    + " current url " + driver.getCurrentUrl()
                    + "\n" + ITUtil.deLinespace(driver.getPageSource()));
            e.printStackTrace();
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
            Assert.fail(errorText + message);
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
            if (second >= 60) {
                Assert.fail("timeout");
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
            if (second >= 60) {
                Assert.fail("timeout");
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
            if (second >= 60)
                failed = true;
            try {
                if (failed || (driver.findElements(By.cssSelector(elementLocator))).size() > 0)
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        checkForIncidentReport(elementLocator); // after timeout to be sure page is loaded
        if (failed)
            fail("timeout of 60 seconds waiting for " + elementLocator + " " + message + " " + driver.getCurrentUrl());
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

    public void switchToWindow(String title) {
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(title)) {
                return;
            }
        }
    }

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

    public void fireMouseOverEventByName(String name) {
        this.fireMouseOverEvent(By.name(name));
    }

    public void fireMouseOverEventByXpath(String locator) {
        this.fireMouseOverEvent(By.xpath(locator));
    }

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
        selectFrame("iframeportlet");
        waitAndClickByXpath("//div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[1]"); //  waitAndClick("id=32");
        Thread.sleep(3000);
        waitAndClickByXpath("//a[@title='edit Agenda Definition with Agenda Id=T1000']",
                "Does user have edit permissions?"); // waitAndClick("id=194_line0");
        checkForIncidentReport("");
        Thread.sleep(3000);
        waitAndClickByXpath("//li/a[@class='agendaNode ruleNode']"); // waitAndClick("//li[@id='473_node_0_parent_root']/a");
        waitAndClickByXpath("//li/a[@class='agendaNode logicNode whenTrueNode']");
        waitAndClickByLinkText("[-] collapse all");

        // click refresh  several times
        for (int i = 0; i < 6; i++) {
            for (int second = 0;; second++) {
                if (second >= 60)
                    Assert.fail("timeout");
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
        assertElementPresentByName("methodToCall.blanketApprove", "Blanket Approve button not present does " + user
                + " have permssion?");
        assertElementPresentByName("methodToCall.close");
        assertElementPresentByName("methodToCall.cancel");
    }

    protected void assertDocFinal(String docId) throws InterruptedException {
        waitFor(By.linkText("spreadsheet"));
        if (isElementPresent(By.linkText(docId))) {
            assertEquals("FINAL", getDocStatus());
        } else {
            assertEquals(docId, driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]")));
            assertEquals("FINAL", getDocStatus());
        }
    }

    private String getDocStatus() {
        return driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[4]")).getText();
    }

    protected String configNameSpaceBlanketApprove() throws Exception {
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Namespace "
                + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", "VTN" + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']",
                "Validation Test NameSpace " + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.applicationId']", "RICE");
        return docId;
    }

    protected void testConfigNamespaceBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = configNameSpaceBlanketApprove();
        blanketApproveTest();
        assertDocFinal(docId);
        passed();
    }

    protected void testConfigParamaterBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Parameter ");
        assertBlanketApproveButtonsPresent();
        assertEquals("", getTextByName("methodToCall.cancel"));
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']", "KR-NS - Kuali Nervous System");

        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(componentLookUp);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
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
        selectFrame("iframeportlet");
        waitAndCreateNew();
        testCancelConfirmation();
    }

    protected void testCreateDocType() throws Exception {
        selectFrame("iframeportlet");
        waitAndCreateNew();
        assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']",
                "save button does not exist on the page");
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        String docId = driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Creating new Document Type");
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        String docTypeName = "TestDocType" + ITUtil.DTS;
        waitForElementPresentByXpath("//input[@id='document.newMaintainableObject.name']");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']",
                "${kr.url}/maintenance.do?methodToCall=docHandler");
        //waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']", "TestDocument Label");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']",
                "default.htm?turl=WordDocuments%2Fdocumenttype.htm");

        waitAndClickByXpath("//*[@name='methodToCall.route' and @alt='submit']");
        checkForIncidentReport();
        waitForPageToLoad();
        driver.switchTo().defaultContent();

        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);

        assertEquals(docId, driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]")).getText());

    }

    protected void testIdentityGroupBlanketApprove() throws Exception
    {
        selectFrame("iframeportlet");
        waitAndCreateNew();

        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Group "
                + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.groupNamespace']", AdminMenuLegacyITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.groupName']", "Validation Test Group1 " + ITUtil.DTS_TWO);
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("return value");
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();

        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityPermissionBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']",
                "Validation Test Permission " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']", "10012");
        selectByXpath("//select[@name='document.newMaintainableObject.namespaceCode']",
                AdminMenuLegacyITBase.LABEL_KUALI_KUALI_SYSTEMS);
        selectByXpath("//select[@name='document.newMaintainableObject.templateId']",
                AdminMenuLegacyITBase.LABEL_KUALI_DEFAULT);
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.name']", "ValidationTestPermission"
                + ITUtil.DTS_TWO);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityPersonBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Person");
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.principalName']", "principal"
                + RandomStringUtils.randomAlphabetic(3).toLowerCase());
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
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']",
                "Validation Test Responsibility " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']",
                AdminMenuLegacyITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "Validation Test Responsibility "
                + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.documentTypeName']", "Test " + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.routeNodeName']", "Test " + ITUtil.DTS_TWO);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.actionDetailsAtRoleMemberLevel']");
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.required']");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testIdentityRoleBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");

        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Role "
                + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.roleNamespace']", AdminMenuLegacyITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.roleName']", "Validation Test Role " + ITUtil.DTS_TWO,
                "No Role Name input to type in.");
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        blanketApproveTest();
        assertDocFinal(docId);

    }

    protected void testLocationCampusBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
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
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        String twoUpperCaseLetters = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        String countryName = "Validation Test Country " + ITUtil.DTS + " " + twoUpperCaseLetters;
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", countryName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", twoUpperCaseLetters);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countryName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.alternateCode']", "V" + twoUpperCaseLetters);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationCountyBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test County");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("return value");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", RandomStringUtils.randomAlphabetic(2)
                .toUpperCase());
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndTypeByName("code", "IN");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("return value");
        String countyName = "Validation Test County" + ITUtil.DTS;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countyName);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.active']");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationPostBlanketApprove() throws Exception {
        //         open(ITUtil.getBaseUrlString()+getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Postal Code");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("return value");
        String code = RandomStringUtils.randomNumeric(5);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", code);
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByXpath("//table[@id='row']/tbody/tr[4]/td[1]/a");
        String cityName = "Validation Test Postal Code " + code;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.cityName']", cityName);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testLocationStateBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test State");
        assertBlanketApproveButtonsPresent();
        //waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4");
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
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
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Campus Type "
                + ITUtil.DTS_TWO);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", RandomStringUtils.randomAlphabetic(2));
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "Indianapolis" + ITUtil.DTS_TWO);
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testWorkFlowDocTypeBlanketApprove() throws Exception {
        //        open(ITUtil.getBaseUrlString() + getTestUrl());
        selectFrame("iframeportlet");
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']",
                "Validation Test Document Type " + ITUtil.DTS);
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("return value");
        String docTypeName = "DocType" + ITUtil.DTS;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']",
                "${kr.url}/maintenance.do?methodToCall=docHandler");
        //waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']",
                "Workflow Maintenance Document Type Document");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']",
                "default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        blanketApproveTest();
        assertDocFinal(docId);
    }

    protected void testVerifyAddDeleteNoteLegacy() throws Exception {
        selectFrame("iframeportlet");
        waitAndClick("div.tableborders.wrap.uif-boxLayoutVerticalItem.clearfix  span.uif-headerText-span > img.uif-disclosure-image");
        waitForElementPresent("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        waitAndClickByName("newCollectionLines['document.notes'].noteText");
        waitAndTypeByName("newCollectionLines['document.notes'].noteText", "Test note");
        waitAndClick("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        waitForElementPresentByName("document.notes[0].noteText");

        assertEquals("Test note", getTextByXpath("//pre"));
        waitAndClick("button[title='Delete a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        assertEquals(Boolean.FALSE, isElementPresentByName("document.notes[0].noteText"));
        passed();
    }

    protected void testVerifyAdHocRecipientsLegacy() throws Exception {
        selectFrame("iframeportlet");
        waitAndClickByLinkText("Fiscal Officer Accounts");

        assertElementPresentByXpath("//select[@name=\"newCollectionLines['document.adHocRoutePersons'].actionRequested\"]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRoutePersons'].name\" and @type=\"text\"]");

        assertElementPresentByXpath("//select[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].actionRequested\"]");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientNamespaceCode\" and @type='text']");
        assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientName\" and @type='text']");
        passed();
    }

    protected void testVerifyButtonsLegacy() throws Exception {
        selectFrame("iframeportlet");
        assertElementPresentByXpath("//button[contains(.,'ubmit')]");
        assertElementPresentByXpath("//button[contains(.,'ave')]");
        assertElementPresentByXpath("//button[contains(.,'lanket approve')]");
        assertElementPresentByXpath("//button[contains(.,'lose')]");
        assertElementPresentByXpath("//a[contains(.,'ancel')]");
        passed();
    }

    protected void testVerifyConstraintText() throws Exception {
        selectFrame("iframeportlet");
        assertEquals(
                "* indicates required field",
                getText("div.uif-boxLayout.uif-horizontalBoxLayout.clearfix > span.uif-message.uif-requiredInstructionsMessage.uif-boxLayoutHorizontalItem"));

        assertEquals(
                "Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));

        assertEquals(
                "Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Sub Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));

        assertEquals(
                "Must not be more than 10 characters",
                getText("div.uif-group.uif-gridGroup.uif-collectionItem.uif-gridCollectionItem.uif-collectionAddItem div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        passed();
    }

    protected void testVerifyDisclosures() throws Exception {
        selectFrame("iframeportlet");
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Account Information')]");
        assertElementPresentByXpath("//span[contains(text(),'Fiscal Officer Accounts')]");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//span[contains(text(),'Ad Hoc Recipients')]");
        assertElementPresentByXpath("//span[contains(text(),'Route Log')]");

        colapseExpandByXpath("//span[contains(text(),'Document Overview')]//img",
                "//label[contains(text(),'Organization Document Number')]");
        colapseExpandByXpath("//span[contains(text(),'Account Information')]//img",
                "//label[contains(text(),'Travel Account Type Code')]");
        colapseExpandByXpath("//span[contains(text(),'Fiscal Officer Accounts')]//img",
                "//a[contains(text(),'Lookup/Add Multiple Lines')]");

        expandColapseByXpath("//span[contains(text(),'Notes and Attachments')]//img",
                "//label[contains(text(),'Note Text')]");
        expandColapseByXpath("//span[contains(text(),'Ad Hoc Recipients')]",
                "//span[contains(text(),'Ad Hoc Group Requests')]");

        // Handle frames
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitIsVisibleByXpath("//img[@alt='refresh']");

        // relative=top iframeportlet might look weird but either alone results in something not found.
        selectTopFrame();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");

        waitNotVisibleByXpath("//img[@alt='refresh']");
        passed();
    }

    protected void testVerifyDocumentOverviewLegacy() throws Exception {
        selectFrame("iframeportlet");
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//input[@name='document.documentHeader.documentDescription']");
        assertElementPresentByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']");
        assertElementPresentByXpath("//textarea[@name='document.documentHeader.explanation']");
        passed();
    }

    protected void testVerifyExpandCollapse() throws Exception {
        selectFrame("iframeportlet");
        assertElementPresentByXpath("//button[contains(@class, 'uif-expandDisclosuresButton')]");
        assertElementPresentByXpath("//button[contains(@class, 'uif-collapseDisclosuresButton')]");
        passed();
    }

    protected void testVerifyFieldsLegacy() throws Exception {
        selectFrame("iframeportlet");
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
        selectFrame("iframeportlet");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentNumber')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentInitiatorNetworkId')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentStatus')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentCreateDate')]");
        passed();
    }

    protected void testVerifyLookupAddMultipleLinesLegacy() throws Exception {
        selectFrame("iframeportlet");
        assertElementPresentByXpath("//a[contains(text(),'Lookup/Add Multiple Lines')]");
        passed();
    }

    protected void testVerifyNotesAndAttachments() throws Exception {
        selectFrame("iframeportlet");
        waitAndClickByXpath("//span[contains(text(),'Notes and Attachments')]");
        waitForElementPresentByXpath("//button[@title='Add a Note']");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//textarea[@name=\"newCollectionLines['document.notes'].noteText\"]");
        assertElementPresentByXpath("//input[@name='attachmentFile']");
        //assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.notes'].attachment.attachmentTypeCode\"]");
        passed();
    }

    protected void testVerifyQuickfinderIconsLegacy() throws Exception {
        selectFrame("iframeportlet");
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//*[@id='quickfinder1']");
        assertElementPresentByXpath("//*[@id='quickfinder2']");
        assertElementPresentByXpath("//*[@id='quickfinder3']");
        assertElementPresentByXpath("//*[@id='quickfinder4_add']");
        // TODO it would be better to test that the image isn't 404
        passed();
    }

    protected void testVerifyRouteLog() throws Exception {
        selectFrame("iframeportlet");
        waitAndClickByLinkText("Route Log");
        waitForElementPresent("//iframe[contains(@src,'RouteLog.do')]");
        passed();
    }

    protected void testVerifySave() throws Exception {
        selectFrame("iframeportlet");
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
        selectFrame("iframeportlet");
        // May be blowing up due to multiple locators
        //assertTrue(isElementPresent("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @placeholder='##.##   ']"));
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent']");
        passed();
    }

    private void testLookUp() throws Exception {
        waitForPageToLoad();
        selectFrame("iframeportlet");
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
        selectFrame("iframeportlet");
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
        //         selectFrame("name=fancybox-frame1343151577256"); // TODO parse source to get name
        //         waitAndClick("css=button:contains(Close)"); // looks lower case, but is upper
        //         Thread.sleep(500);
        //         waitAndClick("css=button:contains(cancel)");

        // AttributeDefinition's don't have actions (yet)
        //         waitAndClick("id=u80");
        //         waitForPageToLoad();
        //         waitAndClick("id=u86");
        //         waitForPageToLoad();
        //         selectWindow("null");
        //         waitAndClick("xpath=(//input[@name='imageField'])[2]");
        //         waitForPageToLoad();
        passed();
    }

    protected void contextLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Notes and Attachments");
        waitAndClick(By.xpath("//a[contains(text(), 'Cancel')]"));
        passed();
    }

    protected void testCreateNewAgenda() throws Exception {
        selectFrame("iframeportlet");
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
        selectFrame("iframeportlet");
        //Click Main Menu and Create New
        //         waitAndCreateNew();
        //         waitForPageToLoad();
        waitAndClickByLinkText("Create New");
        //         waitAndClick(By.linkText("Create New"));
        //Save docId
        waitForElementPresent("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");
        String docId = getText("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");

        driver.findElement(By.name("document.documentHeader.documentDescription")).clear();
        driver.findElement(By.name("document.documentHeader.documentDescription")).sendKeys("Description for Document");
        new Select(driver.findElement(By.name("document.newMaintainableObject.dataObject.namespaceCode")))
                .selectByVisibleText("KUALI - Kuali Systems");
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).clear();
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).sendKeys(
                "Document Name" + ITUtil.DTS);

        //Add Row1
        driver.findElement(
                By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(
                By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName"))
                .sendKeys("kr");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);
        //Add Row2
        driver.findElement(
                By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(
                By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName"))
                .sendKeys("admin");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);

        //Blanket approve
        driver.findElement(
                By.cssSelector("div[data-parent='PeopleFlow-MaintenanceView'] > div.uif-footer button~button~button"))
                .click();
        Thread.sleep(5000);
        //Close the Doc
        //         driver.findElement(By.id("uif-close")).click();
        //         Thread.sleep(3000);

        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        driver.findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);

        selectFrame("iframeportlet");
        driver.findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        Thread.sleep(5000);
        assertEquals("FINAL", driver.findElement(By.xpath("//table[@id='row']/tbody/tr/td[4]")).getText());
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
        selectFrame("iframeportlet");
        waitAndCreateNew();
        waitForPageToLoad();
        assertElementPresentByXpath("//*[@name='methodToCall.save' and @alt='save']",
                "save button does not exist on the page");
        //Enter details for Namespace.
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding PEANUTS");
        waitAndTypeByXpath("//*[@id='document.documentHeader.explanation']", "I want to add PEANUTS to test KIM");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", "PEANUTS");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "The Peanuts Gang");
        checkByXpath("//input[@id='document.newMaintainableObject.active']");
        waitAndClickByXpath("//*[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        checkForIncidentReport();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]",
                "Document is not saved successfully");
        //checks it is saved and initiator is admin.
        assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a"))
                .getText());

    }

    protected void testAddingBrownGroup() throws Exception {

        selectFrame("iframeportlet");
        waitAndCreateNew();
        waitForPageToLoad();
        String docId = driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
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
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]",
                "Document is not saved successfully");
        checkForIncidentReport();
        //checks it is saved and initiator is admin.
        assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a"))
                .getText());
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        waitAndClickByLinkText("Group");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndTypeByName("name", groupName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(groupName);
    }

    protected void testDocTypeLookup() throws Exception {
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@title='Search Parent Name']");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        //waitForPageToLoad();
        waitAndClickByXpath("//table[@id='row']/tbody/tr[contains(td[3],'RiceDocument')]/td[1]/a");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertEquals("RiceDocument", getTextByXpath("//table[@id='row']/tbody/tr/td[4]/a"));
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
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
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

        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
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
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("Y", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
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
        selectFrame("iframeportlet");
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.value");
        waitAndTypeByName("document.newMaintainableObject.value", "N");
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
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
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        return params;
    }

    protected List<String> testCopyParameter(String docId, String parameterName) throws Exception
    {
        selectFrame("iframeportlet");
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
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
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        return params;
    }

    protected List<String> testCreateNewParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter Type");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);

        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Parameter Type Created-------------------------");

        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        return params;
    }

    protected List<String> testLookUpParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim()
                .toLowerCase());
        assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim()
                .toLowerCase());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("--------------------------------Lookup And View Successful-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        return params;

    }

    protected List<String> testEditParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        selectFrame("iframeportlet");
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Type Edited-------------------------");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        return params;
    }

    protected List<String> testVerifyEditedParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim()
                .toLowerCase());
        assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim()
                .toLowerCase());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        return params;
    }

    protected List<String> testCopyParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        selectFrame("iframeportlet");
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Type Edited-------------------------");

        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);
        return params;
    }

    protected List<String> testVerifyCopyParameterType(String docId, String parameterType, String parameterCode)
            throws Exception
    {
        waitAndTypeByName("name", parameterType);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterType);
        waitAndClickByLinkText(parameterType);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        //        assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim().toLowerCase());
        assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim()
                .toLowerCase());
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
        /*assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']",
                "save button does not exist on the page");*/
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'Document Description (Description) is a required field.')]/img[@alt='error']");
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding Permission removeme");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
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
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out
                .println("------------------------------------Permission document submitted successfully--------------------------");

        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        return params;
    }

    protected List<String> testLookUpPermission(String docId, String permissionName) throws Exception
    {
        waitForPageToLoad();
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
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
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Editing Permission removeme");
        uncheckByName("document.newMaintainableObject.active");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        System.out
                .println("------------------------------------Inactivation of Permission successfull--------------------------");
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
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(permissionName);

        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);
        return params;
    }

    protected List<String> testCreateNewPerson(String docId, String personName) throws Exception
    {

        waitForPageToLoad();
        Thread.sleep(2000);
        /*assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']",
                "save button does not exist on the page");*/
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding Charlie Brown");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Charlie Brown to test KIM");
        //here You should also check for lower case validation for principalName, but it is skipped for now as there is an incident report error there.
        personName = "cbrown" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.principalName", personName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'At least one affiliation must be entered.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'At least one name must be entered.')]/img[@alt='error']");
        System.out.println("------------------------------------Validation Test Successful--------------------------");

        selectOptionByName("newAffln.affiliationTypeCode", "STDNT");
        selectOptionByName("newAffln.campusCode", "BL");
        checkByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitForPageToLoad();
        //waitAndClickByName("methodToCall.toggleTab.tabContact");
        waitForPageToLoad();
        Thread.sleep(3000);
        selectOptionByName("newName.nameCode", "PRM");
        selectOptionByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "Charlie");
        waitAndTypeByName("newName.lastName", "Brown");
        checkByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out
                .println("------------------------------------Person document submitted successfully--------------------------");

        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);
        return params;
    }

    protected List<String> testLookUpPerson(String docId, String personName) throws Exception
    {
        waitForPageToLoad();
        waitAndTypeByName("principalName", personName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("firstName", "Charlie");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("lastName", "Brown");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("campusCode", "BL");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
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
        assertEquals(personName, getTextByXpath("//div[@class='tab-container']/table//tr[2]/td[1]/div").trim());
        assertEquals("BL - BLOOMINGTON", getTextByXpath("//div[@class='tab-container']/table[3]//tr[2]/td[2]/div")
                .trim());
        assertEquals("Student", getTextByXpath("//select/option[@selected]").trim());
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
        System.out
                .println("------------------------------------Viewing from Inquiry Framework Test Successful--------------------------");
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
        Assert.assertTrue(getAttributeByName("field9", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field9", "focus");
        clearTextByName("field9");
        waitAndTypeByName("field9", "12345");
        fireEvent("field9", "blur");
        Assert.assertTrue(getAttributeByName("field9", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field10", "focus");
        waitAndTypeByName("field10", "2");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10", "focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "51");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10", "focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "25");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field6", "focus");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field7", "B");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "");
        fireEvent("field6", "blur");
        Assert.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field7");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        Assert.assertTrue(getAttributeByName("field6", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field14", "A");
        fireEvent("field14", "blur");
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field11", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field12");
        waitAndTypeByName("field12", "A");
        fireEvent("field12", "blur");
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field13");
        waitAndTypeByName("field13", "A");
        fireEvent("field13", "blur");
        Assert.assertTrue(getAttributeByName("field13", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field11", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field18", "A");
        fireEvent("field18", "blur");
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field15", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field16", "A");
        fireEvent("field16", "blur");
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field17");
        waitAndTypeByName("field17", "A");
        fireEvent("field17", "blur");
        Assert.assertTrue(getAttributeByName("field17", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field18", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field23", "A");
        fireEvent("field23", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "A");
        fireEvent("field19", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "");
        fireEvent("field19", "blur");
        waitAndTypeByName("field20", "B");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field21");
        waitAndTypeByName("field21", "C");
        fireEvent("field21", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field22");
        waitAndTypeByName("field22", "D");
        fireEvent("field22", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "D");
        fireEvent("field19", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "D");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        clearTextByName("field25");
        waitAndTypeByName("field25", "");
        fireEvent("field25", "blur");
        Assert.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        fireEvent("field24", "blur");
        clearTextByName("field25");
        waitAndTypeByName("field25", "$100");
        fireEvent("field25", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case2']");
        fireEvent("field24", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field25");
        waitAndTypeByName("field25", "A100");
        fireEvent("field25", "blur");
        Assert.assertTrue(getAttributeByName("field25", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "501");
        fireEvent("field26", "blur");
        Assert.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "499");
        fireEvent("field26", "blur");
        Assert.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field26", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        clearTextByName("field27");
        waitAndTypeByName("field27", "A");
        fireEvent("field27", "blur");
        clearTextByName("field28");
        waitAndTypeByName("field28", "");
        fireEvent("field28", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field28");
        waitAndTypeByName("field28", "B");
        fireEvent("field28", "blur");
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        for (int second = 0;; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field31");
        waitAndTypeByName("field31", "B");
        clearTextByName("field32");
        waitAndTypeByName("field32", "B");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field33");
        waitAndTypeByName("field33", "B");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field32");
        waitAndTypeByName("field32", "A");
        clearTextByName("field33");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
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
            if (second >= 60)
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
        //        Assert.assertTrue(isVisible("div.jquerybubblepopup.jquerybubblepopup-black") && isVisible("td.jquerybubblepopup-innerHtml"));
        Assert.assertEquals("This tooltip is triggered by focus or and mouse over.",
                getText("td.jquerybubblepopup-innerHtml"));

        // check if tooltip closed on blur
        fireEvent(NAME_FIELD_1, "blur");
        Assert.assertFalse(isVisible("div.jquerybubblepopup.jquerybubblepopup-black")
                && isVisible("td.jquerybubblepopup-innerHtml"));
        //Assert.assertFalse(isVisible("//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));

        Thread.sleep(5000);
        fireEvent("field119", "focus");
        // check if tooltip opens on mouse over
        fireMouseOverEventByName(NAME_FIELD_2);
        //        Assert.assertTrue(isVisible("td.jquerybubblepopup-innerHtml"));
        //        Assert.assertTrue(isVisibleByXpath("//td[@class='jquerybubblepopup-innerHtml']"));
        //        Assert.assertEquals("This is a tool-tip with different position and tail options", getText("td.jquerybubblepopup-innerHtml"));
        Assert.assertTrue(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check if tooltip closed on mouse out
        waitAndTypeByName(NAME_FIELD_2, "a");
        Thread.sleep(5000);
        Assert.assertFalse(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));

        // check that default tooltip does not display when there are an error message on the field
        waitAndTypeByName(NAME_FIELD_1, "1");
        fireEvent(NAME_FIELD_1, "blur");
        //        fireEvent(NAME_FIELD_1, "focus");
        fireMouseOverEventByName(NAME_FIELD_1);
        Thread.sleep(10000);
        assertTrue(
                "https://jira.kuali.org/browse/KULRICE-8141 Investigate why UifTooltipIT.testTooltip fails around jquerybubblepopup",
                isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']") &&
                        !(isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-black']")));
        // TODO figure out this last assert
        //Assert.assertFalse(isVisible("//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
        passed();
    }

    protected void testValidCharsConstraintIT() throws Exception {
        fireEvent("field50", "focus");
        waitAndTypeByName("field50", "12.333");
        fireEvent("field50", "blur");
        Assert.assertTrue(getAttributeByName("field50", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field50", "focus");
        waitAndTypeByName("field50", "123.33");
        fireEvent("field50", "blur");
        Assert.assertTrue(getAttributeByName("field50", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field51", "focus");
        waitAndTypeByName("field51", "A");
        fireEvent("field51", "blur");
        Assert.assertTrue(getAttributeByName("field51", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field51", "focus");
        waitAndTypeByName("field51", "-123.33");
        fireEvent("field51", "blur");
        Assert.assertTrue(getAttributeByName("field51", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field77", "focus");
        waitAndTypeByName("field77", "1.1");
        fireEvent("field77", "blur");
        Assert.assertTrue(getAttributeByName("field77", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field77", "focus");
        waitAndTypeByName("field77", "12");
        fireEvent("field77", "blur");
        Assert.assertTrue(getAttributeByName("field77", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field52", "focus");
        waitAndTypeByName("field52", "5551112222");
        fireEvent("field52", "blur");
        Assert.assertTrue(getAttributeByName("field52", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field52", "focus");
        waitAndTypeByName("field52", "555-111-1111");
        fireEvent("field52", "blur");
        Assert.assertTrue(getAttributeByName("field52", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field53", "focus");
        waitAndTypeByName("field53", "1ClassName.java");
        fireEvent("field53", "blur");
        Assert.assertTrue(getAttributeByName("field53", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field53", "focus");
        waitAndTypeByName("field53", "ClassName.java");
        fireEvent("field53", "blur");
        Assert.assertTrue(getAttributeByName("field53", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field54", "focus");
        waitAndTypeByName("field54", "aaaaa");
        fireEvent("field54", "blur");
        Assert.assertTrue(getAttributeByName("field54", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field54", "focus");
        waitAndTypeByName("field54", "aaaaa@kuali.org");
        fireEvent("field54", "blur");
        Assert.assertTrue(getAttributeByName("field54", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field84", "focus");
        waitAndTypeByName("field84", "aaaaa");
        fireEvent("field84", "blur");
        Assert.assertTrue(getAttributeByName("field84", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field84", "focus");
        waitAndTypeByName("field84", "http://www.kuali.org");
        fireEvent("field84", "blur");
        Assert.assertTrue(getAttributeByName("field84", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field55", "focus");
        waitAndTypeByName("field55", "023512");
        fireEvent("field55", "blur");
        Assert.assertTrue(getAttributeByName("field55", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field55", "focus");
        waitAndTypeByName("field55", "022812");
        fireEvent("field55", "blur");
        Assert.assertTrue(getAttributeByName("field55", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field75", "focus");
        waitAndTypeByName("field75", "02/35/12");
        fireEvent("field75", "blur");
        Assert.assertTrue(getAttributeByName("field75", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field75", "focus");
        waitAndTypeByName("field75", "02/28/12");
        fireEvent("field75", "blur");
        Assert.assertTrue(getAttributeByName("field75", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field82", "focus");
        waitAndTypeByName("field82", "13:22");
        fireEvent("field82", "blur");
        Assert.assertTrue(getAttributeByName("field82", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field82", "focus");
        waitAndTypeByName("field82", "02:33");
        fireEvent("field82", "blur");
        Assert.assertTrue(getAttributeByName("field82", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field83", "focus");
        waitAndTypeByName("field83", "25:22");
        fireEvent("field83", "blur");
        Assert.assertTrue(getAttributeByName("field83", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field83", "focus");
        waitAndTypeByName("field83", "14:33");
        fireEvent("field83", "blur");
        Assert.assertTrue(getAttributeByName("field83", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field57", "focus");
        waitAndTypeByName("field57", "0");
        fireEvent("field57", "blur");
        Assert.assertTrue(getAttributeByName("field57", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field57", "focus");
        waitAndTypeByName("field57", "2020");
        fireEvent("field57", "blur");
        Assert.assertTrue(getAttributeByName("field57", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field58", "focus");
        waitAndTypeByName("field58", "13");
        fireEvent("field58", "blur");
        Assert.assertTrue(getAttributeByName("field58", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field58", "focus");
        waitAndTypeByName("field58", "12");
        fireEvent("field58", "blur");
        Assert.assertTrue(getAttributeByName("field58", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field61", "focus");
        waitAndTypeByName("field61", "5555-444");
        fireEvent("field61", "blur");
        Assert.assertTrue(getAttributeByName("field61", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field61", "focus");
        waitAndTypeByName("field61", "55555-4444");
        fireEvent("field61", "blur");
        Assert.assertTrue(getAttributeByName("field61", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field62", "focus");
        waitAndTypeByName("field62", "aa5bb6_a");
        fireEvent("field62", "blur");
        Assert.assertTrue(getAttributeByName("field62", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field62", "focus");
        waitAndTypeByName("field62", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        fireEvent("field62", "blur");
        Assert.assertTrue(getAttributeByName("field62", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field63", "focus");
        waitAndTypeByName("field63", "fff555$");
        fireEvent("field63", "blur");
        Assert.assertTrue(getAttributeByName("field63", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field63", "focus");
        waitAndTypeByName("field63", "aa22 _/");
        fireEvent("field63", "blur");
        Assert.assertTrue(getAttributeByName("field63", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field64", "focus");
        waitAndTypeByName("field64", "AABB55");
        fireEvent("field64", "blur");
        Assert.assertTrue(getAttributeByName("field64", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field64", "focus");
        waitAndTypeByName("field64", "ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z");
        fireEvent("field64", "blur");
        Assert.assertTrue(getAttributeByName("field64", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field76", "focus");
        waitAndTypeByName("field76", "AA~BB%");
        fireEvent("field76", "blur");
        Assert.assertTrue(getAttributeByName("field76", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field76", "focus");
        waitAndTypeByName("field76", "abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?");
        fireEvent("field76", "blur");
        Assert.assertTrue(getAttributeByName("field76", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field65", "focus");
        waitAndTypeByName("field65", "sdfs$#$# dsffs");
        fireEvent("field65", "blur");
        Assert.assertTrue(getAttributeByName("field65", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field65", "focus");
        waitAndTypeByName("field65", "sdfs$#$#sffs");
        fireEvent("field65", "blur");
        Assert.assertTrue(getAttributeByName("field65", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field66", "focus");
        waitAndTypeByName("field66", "abcABCD");
        fireEvent("field66", "blur");
        Assert.assertTrue(getAttributeByName("field66", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field66", "focus");
        waitAndTypeByName("field66", "ABCabc");
        fireEvent("field66", "blur");
        Assert.assertTrue(getAttributeByName("field66", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field67", "focus");
        waitAndTypeByName("field67", "(111)B-(222)A");
        fireEvent("field67", "blur");
        Assert.assertTrue(getAttributeByName("field67", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field67", "focus");
        waitAndTypeByName("field67", "(12345)-(67890)");
        fireEvent("field67", "blur");
        Assert.assertTrue(getAttributeByName("field67", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field68", "focus");
        waitAndTypeByName("field68", "A.66");
        fireEvent("field68", "blur");
        Assert.assertTrue(getAttributeByName("field68", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field68", "focus");
        waitAndTypeByName("field68", "a.4");
        fireEvent("field68", "blur");
        Assert.assertTrue(getAttributeByName("field68", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field56", "focus");
        waitAndTypeByName("field56", "2020-06-02");
        fireEvent("field56", "blur");
        Assert.assertTrue(getAttributeByName("field56", "class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field56", "focus");
        waitAndTypeByName("field56", "2020-06-02 03:30:30.22");
        fireEvent("field56", "blur");
        Assert.assertTrue(getAttributeByName("field56", "class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        passed();
    }

    protected void testSubCollectionSize() throws Exception {
        checkForIncidentReport("link=Collections");
        //        // click on collections page link
        waitAndClickByLinkText("Collections");
        //        // wait for collections page to load by checking the presence of a sub collection line item
        for (int second = 0;; second++) {
            if (second >= 30)
                fail("timeout");
            try {
                if (getText(
                        "div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span")
                        .equals("SubCollection - (3 lines)"))
                {
                    break;
                }

            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        // verify that sub collection sizes are displayed as expected
        assertEquals(
                "SubCollection - (3 lines)",
                getText("div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span"));
        assertEquals("SubCollection - (2 lines)", getTextByXpath("//a[@id='subCollection1_line1_toggle']/span"));

    }

    protected void verifyRichMessagesValidationBasicFunctionality() throws Exception
    {
        Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field1']"));
        Assert.assertTrue(isElementPresentByXpath("//a[contains(text(), 'Kuali')]"));
        Assert.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='field2']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationAdvancedFunctionality() throws Exception
    {
        //Color Options
        Assert.assertTrue(isElementPresentByXpath("//span[@style='color: green;']"));
        Assert.assertTrue(isElementPresentByXpath("//span[@style='color: blue;']"));

        //Css class
        Assert.assertTrue(isElementPresentByXpath("//span[@class='fl-text-underline fl-text-larger']"));

        //Combinations
        Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field3']"));
        Assert.assertTrue(isElementPresentByXpath("//select[@name='field4']"));
        Assert.assertTrue(isElementPresentByXpath("//button[contains(text(), 'Action Button')]"));

        //Rich Message Field
        Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/span[contains(., 'Color')]"));
        Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/i/b[contains(., 'Html')]"));
        Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/img[@class='uif-image inlineBlock']"));

        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationLettersNumbersValidation() throws Exception
    {
        //For letters only Validation
        Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field5']"));
        waitAndTypeByXpath(
                "//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']",
                "abc");
        Assert.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']");
        waitAndTypeByXpath(
                "//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']",
                "abc12");

        waitAndTypeByXpath("//input[@name= 'field6']", "");
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        Thread.sleep(3000);
        clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']");
        waitAndTypeByXpath(
                "//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']",
                "abc");
        waitAndTypeByXpath("//input[@name= 'field6']", "");

        //For numbers only validation
        waitAndTypeByXpath("//input[@name= 'field6']", "123");
        Assert.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath("//input[@name= 'field6']");
        waitAndTypeByXpath("//input[@name= 'field6']", "123ab");
        fireEvent("field6", "blur");
        Thread.sleep(5000);
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));

        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationRadioAndCheckBoxGroupFunctionality() throws Exception
    {
        //Radio Group
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='1']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='2']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='3']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='4']"));

        //Checkbox Group
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='1']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='2']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='3']"));
        Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/label/div/select[@name='field4']"));

        //Checkbox Control
        Assert.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='bField1']"));
        Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field103']"));

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
        //        Assert.assertTrue(isElementPresentByXpath("//div[@class='fancybox-wrap fancybox-desktop fancybox-type-html fancybox-opened']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));

        //Testing methodToCall action (no client validation check)
        waitAndClickByXpath("//div[contains(., 'Testing methodToCall action (no client validation check)')]/a");
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages-error']"));
        Thread.sleep(3000);
    }

    public void testDefaultTestsTableLayout() throws Exception {

        assertTableLayout();
        waitAndTypeByName("newCollectionLines['list1'].field1", "asdf1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "asdf2");
        waitAndTypeByName("newCollectionLines['list1'].field3", "asdf3");
        waitAndTypeByName("newCollectionLines['list1'].field4", "asdf4");
        waitAndClickByXpath("//button[contains(.,'add')]"); // the first button is the one we want

        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (getAttributeByName("newCollectionLines['list1'].field1", "value").equals(""))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        Assert.assertEquals("asdf1", getAttributeByName("list1[0].field1", "value"));
        Assert.assertEquals("asdf2", getAttributeByName("list1[0].field2", "value"));
        Assert.assertEquals("asdf3", getAttributeByName("list1[0].field3", "value"));
        Assert.assertEquals("asdf4", getAttributeByName("list1[0].field4", "value"));

        Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-Base-TableLayout_disclosureContent']/div/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        passed();
    }

    protected void assertTableLayout() {
        Assert.assertTrue(driver.getPageSource().contains("Table Layout"));
        Assert.assertTrue(driver.getPageSource().contains("Field 1"));
        Assert.assertTrue(driver.getPageSource().contains("Field 2"));
        Assert.assertTrue(driver.getPageSource().contains("Field 3"));
        Assert.assertTrue(driver.getPageSource().contains("Field 4"));
        Assert.assertTrue(driver.getPageSource().contains("Actions"));
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
        Assert.assertEquals("", getAttributeByName("list1[0].field1", "value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field2", "value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field3", "value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field4", "value"));
        Assert.assertEquals("5", getAttributeByName("list1[1].field1", "value"));
        Assert.assertEquals("6", getAttributeByName("list1[1].field2", "value"));
        Assert.assertEquals("7", getAttributeByName("list1[1].field3", "value"));
        Assert.assertEquals("8", getAttributeByName("list1[1].field4", "value"));

        Assert.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        waitAndTypeByName("list1[0].field1", "1");
        waitAndTypeByName("list1[0].field2", "1");
        waitAndTypeByName("list1[0].field3", "1");
        waitAndTypeByName("list1[0].field4", "1");
        Assert.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
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
        //waitAndClick("css=div.jGrowl-close");
        // check if actions column RIGHT by default
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection1']//tr[2]/td[6]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button"));

        // check if actions column is LEFT
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button"));

        // check if actions column is 3rd in a sub collection
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-subCollection2_line0']//tr[2]/td[3]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button"));
        passed();

    }

    protected void testAddViaLightbox() throws Exception {

        waitAndClickByLinkText("Add Via Lightbox");
        Assert.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000);
        waitAndTypeByXpath("//form/div/table/tbody/tr/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[2]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[3]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[4]/td/div/input", "1");
        waitAndClickByXpath("//button[@id='Collections-AddViaLightbox-TableTop_add']");
        Thread.sleep(3000);
        Assert.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
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
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field1", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field2", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field3", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field4", "value"));

        //Check for the added if delete is present or not
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-ColumnSequence-TableDefault_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
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
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3", "value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field1", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field2", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field3", "value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field4", "value"));

        //Check for the added if delete is present or not
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        //        Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[@class='uif-action uif-secondaryActionButton uif-smallActionButton uif-saveLineAction']"));
        passed();
    }
}
