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

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated see WebDriverAftBase
 */
public abstract class WebDriverITBase {

    public WebDriver driver;
    static ChromeDriverService chromeDriverService;

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

    @BeforeClass
    public static void createAndStartService() throws Exception {
        chromeDriverService = WebDriverUtils.chromeDriverCreateCheck();
        if (chromeDriverService != null) chromeDriverService.start();
    }


    /**
     * <p>
     * Logs in using the KRAD Login Page, if the JVM arg remote.autologin is set, auto login as admin will not be done.
     * </p>
     *
     * @param driver to login with
     * @param userName to login with
     * @param failable to fail on if there is a login problem
     * @throws InterruptedException
     */
    public void login(WebDriver driver, String userName, JiraAwareFailable failable) throws InterruptedException {
        if ("true".equalsIgnoreCase(System.getProperty(WebDriverUtils.REMOTE_AUTOLOGIN_PROPERTY, "true"))) {
            driver.findElement(By.name("login_user")).clear();
            driver.findElement(By.name("login_user")).sendKeys(userName);
            driver.findElement(By.id("Rice-LoginButton")).click();
            Thread.sleep(1000);
            String contents = driver.getPageSource();
            AutomatedFunctionalTestUtils.failOnInvalidUserName(userName, contents, failable);
            AutomatedFunctionalTestUtils.checkForIncidentReport(driver.getPageSource(), "Login",
                    "Login failure", failable);
        }
    }

    public void fail(String message) { // should this method be abstract or overridden, no jira aware fail?
        SeleneseTestBase.fail(message);
    }

    /**
     * Setup the WebDriver test, login and load the tested web page
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        driver = WebDriverUtils.setUp(getUserName(), WebDriverUtils.getBaseUrlString() + "/" + getTestUrl());
        login(driver, getUserName(), new JiraAwareFailable() {
            @Override
            public void fail(String message) {
                SeleneseTestBase.fail(message);
            }

            @Override
            public void jiraAwareFail(String message) {
                SeleneseTestBase.fail(message);
            }

            @Override
            public void jiraAwareFail(String contents, String message) {
                SeleneseTestBase.fail(contents + " " + message);
            }

            @Override
            public void jiraAwareFail(String contents, String message, Throwable throwable) {
                SeleneseTestBase.fail(contents + " " + message + " " + throwable.getMessage());
            }
        });
    }

    /**
     * Tear down the WebDriver test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (WebDriverUtils.dontTearDownPropertyNotSet()) {
            if (driver != null) {
                driver.quit(); // TODO not tested with chrome, the service stop might need this check too
            } else {
                System.out.println("WebDriver was null in WebDriverUtils.tearDown()");
            }
        }
    }

    /**
     * Tear down the WebDriver test
     *
     * @throws Exception
     */
    @AfterClass
    public static void stopService() throws Exception {
        if (chromeDriverService != null) {
            chromeDriverService.stop();
        }
    }

    /**
     * Check if an element is present
     *
     * <p>
     * This test takes a while due to the 'implicit wait' time.
     * </p>
     *
     * @param by The locating mechanism of the element
     * @return true if the element is present, false otherwise
     */
    public boolean isElementPresent(By by) {
        if (driver.findElements(by).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Quickly check if an element is present
     *
     * <p>
     * Just like {@link #isElementPresent(org.openqa.selenium.By)} but with a short 'implicit wait' time.  Use this only
     * if it is guaranteed that all elements are rendered.
     * </p>
     *
     * @param by The locating mechanism of the element
     * @return true if the element is present, false otherwise
     */
    public boolean isElementPresentQuick(By by) {
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.IMPLICIT_WAIT_TIME_LOOP_MS, TimeUnit.MILLISECONDS);
        boolean result = isElementPresent(by);
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.IMPLICIT_WAIT_TIME_SECONDS_DEFAULT, TimeUnit.SECONDS);
        return result;
    }

    /**
     * Assert that clicking an element causes a popup window with a specific URL
     *
     * @param by The locating mechanism of the element to be clicked
     * @param windowName The name of the popup window
     * @param url The URL of the popup window
     */
    public void assertPopUpWindowUrl(By by, String windowName, String url) {
        driver.findElement(by).click();
        String parentWindowHandle = driver.getWindowHandle();
        // wait page to be loaded
        driver.switchTo().window(windowName).findElements(By.tagName("head"));
        SeleneseTestBase.assertEquals(url, driver.getCurrentUrl());
        driver.switchTo().window(parentWindowHandle);
    }
    
    /**
     * 
     *
     * @param by The locating mechanism of the element
    */
    protected void waitFor(By by) throws InterruptedException {
        waitFor(by, "");
    }

    /**
     * TODO Investigate using WebDriverUtils.waitFor
     *
     * @param by The locating mechanism of the element
     * @param message User defined message to display
     */
    protected void waitFor(By by, String message) throws InterruptedException {
        Thread.sleep(1000);
        try {
            driver.findElement(by);
        } catch (Exception e) {
            // ignore, fail on use if required
        }
    }

    /**
     * 
     *
     * @param by The locating mechanism of the element
     * @param text The text to type
    */
    protected void waitAndType(By by, String text) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " " + text);
            e.printStackTrace();
        }
    }
    
    /**
     * 
     *
     * @param by The locating mechanism of the element
     * @param text The text to type
     * @param message User defined message to display
    */
    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " " + text + "  "+message);
            e.printStackTrace();
        }
    }
    
    /**
     * 
     *
     * @param locator The locating mechanism of the element
     * @param text The text to type
    */
    protected void waitAndTypeByXpath(String locator, String text) throws InterruptedException {
        waitAndType(By.xpath(locator), text);
    }
    
    /**
     * 
     *
     * @param locator The locating mechanism of the element
     * @param text The text to type
     * @param message User defined message to display
    */
    protected void waitAndTypeByXpath(String locator, String text, String message) throws InterruptedException {
        waitAndType(By.xpath(locator), text, message);
    }
    
    /**
     * 
     *
     * @param name The name of the element
     * @param text The text to type
    */
    protected void waitAndTypeByName(String name, String text) throws InterruptedException {
        waitAndType(By.name(name), text);
    }
    
    /**
     * Clear the text written in an input field by name of an element
     *
     * @param name The name of the element
    */
    protected void clearTextByName(String name) throws InterruptedException {
        clearText(By.name(name));
    }
    
    /**
     * Clear the text written in an input field by xpath of an element
     *
     * @param locator The locating mechanism of the element
    */
    protected void clearTextByXpath(String locator) throws InterruptedException {
        clearText(By.xpath(locator));
    }
    
    /**
     * Clear the text written in an input field by xpath of an element
     *
     * @param by method used for finding the element
    */
    protected void clearText(By by)  throws InterruptedException {
        driver.findElement(by).clear();        
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
    
    protected String getEval(String script)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String)js.executeScript(script);
    }
    
    /**
     * Switch to new window
     *
    */
    protected void switchWindow()
    {
        Set<String> winSet = driver.getWindowHandles();
        List<String> winList = new ArrayList<String>(winSet);
        String newTab = winList.get(winList.size() - 1);
        driver.switchTo().window(newTab);
    }
    
    /**
     * Get value of any attribute by using element name
     *
     *@param name name of an element
     *@param attribute the name of an attribute whose value is to be retrieved
    */
    protected String getAttributeByName(String name,String attribute) throws InterruptedException {
        return getAttribute(By.name(name),attribute);
    }
    
    /**
     * Get value of any attribute by using element xpath
     *
     *@param locator locating mechanism of an element
     *@param attribute the name of an attribute whose value is to be retrieved
    */
    protected String getAttributeByXpath(String locator,String attribute) throws InterruptedException {
        return getAttribute(By.xpath(locator),attribute);
    }
    
    /**
     * Get value of any attribute of an element
     *
     * @param by method used for finding the element
     *@param attribute the name of an attribute whose value is to be retrieved
    */
    protected String getAttribute(By by,String attribute)  throws InterruptedException {
        return driver.findElement(by).getAttribute(attribute);        
    }
    
    /**
     * 
     *
     * @param text text of the link
    */
    protected void waitAndClickByLinkText(String text) throws InterruptedException {
        waitAndClick(By.linkText(text),"");
    }

    /**
     * 
     *
     * @param text text of the link
     * @param message user defined message to display
    */
    protected void waitAndClickByLinkText(String text, String message) throws InterruptedException {
        waitAndClick(By.linkText(text), message);
    }
    
    /**
     * 
     *
     * @param by method used for finding the element
    */
    protected void waitAndClick(By by) throws InterruptedException {
        waitAndClick(by, "");
    }

    /**
     * 
     *
     * @param by method used for finding the element
     * @param message user defined message to display
    */
    protected void waitAndClick(By by, String message) throws InterruptedException {
        waitFor(by, message);
        try {
            (driver.findElement(by)).click();
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " " + message);
        }
    }

    /**
     * 
     *
     * @param locator mechanism to locate element by xpath
    */
    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, "");
    }
    
    /**
     * 
     *
     * @param locator mechanism to locate element by xpath
     * @param message user defined message to display
    */
    protected void waitAndClick(String locator, String message) throws InterruptedException {
        waitAndClick(By.cssSelector(locator), message);
    }

    /**
     * 
     *
     * @param locator mechanism to locate element by xpath
    */
    protected void waitForElementPresent(String locator) throws InterruptedException {
        waitFor(By.cssSelector(locator));
    }

    /**
     * 
     *
     * @param locator mechanism to locate element by xpath
    */    
    protected void waitForElementPresentByXpath(String locator) throws InterruptedException {
        waitFor(By.xpath(locator));
    }
    
    /**
     * 
     *
     * @param name name of an element
    */ 
    protected void waitForElementPresentByName(String name) throws InterruptedException {
        waitFor(By.name(name));
    }
    
    protected void checkForIncidentReport(JiraAwareFailable failable) {
        checkForIncidentReport("", failable, "");
    }

    protected void checkForIncidentReport(String locator, JiraAwareFailable failable) {
        checkForIncidentReport(locator, failable, "");
    }
    
    protected void checkForIncidentReport(String locator, JiraAwareFailable failable, String message) {
        AutomatedFunctionalTestUtils.checkForIncidentReport(driver.getPageSource(), locator, message, failable);
    }


}

