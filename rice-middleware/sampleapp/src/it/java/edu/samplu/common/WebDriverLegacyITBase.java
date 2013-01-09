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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.junit.Assert.assertEquals;

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

    public @Rule TestName testName= new TestName();

    String sessionId = null;

    public String getSessionId() {
        return sessionId;
    }

    @BeforeClass
    public static void createAndStartService() throws Exception {
        chromeDriverService = WebDriverUtil.createAndStartService();
        if (chromeDriverService != null) chromeDriverService.start();
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
                String userResponse = getHTML(ITUtil.prettyHttp(System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test=" + this.toString().trim()));
                user = userResponse.substring(userResponse.lastIndexOf(":" ) + 2, userResponse.lastIndexOf("\""));
            }
            driver = WebDriverUtil.setUp(getUserName(), ITUtil.getBaseUrlString() + getTestUrl(), getClass().getSimpleName(), testName);
            this.sessionId = ((RemoteWebDriver)driver).getSessionId().toString();
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
                getHTML(ITUtil.prettyHttp(System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test=" + this.toString() + "&user=" + user));
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
                System.out.println("WebDriver is null, if using saucelabs, has sauceleabs been uncommented in WebDriverUtil.java?  If using a remote hub did you include the port?");
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
    
    protected void assertElementPresentByName(String name,String message) {
        try{
                driver.findElement(By.name(name));
        }catch(Exception e){
                Assert.fail(name+ " not present "+ message);
        }
    }

    protected void assertElementPresentByXpath(String locator) {
        driver.findElement(By.xpath(locator));
    }
    
    protected void assertElementPresentByXpath(String locator,String message) {
        try{
                driver.findElement(By.xpath(locator));
        }catch(Exception e){
                Assert.fail(locator+ " not present "+ message);                
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
        waitAndClickByName("methodToCall.blanketApprove", "No blanket approve button does the user " + getUserName() + " have permission?");
        Thread.sleep(2000);

        if (driver.findElements(By.xpath(ITUtil.DIV_ERROR_LOCATOR)).size()>0) {
            String errorText = driver.findElement(By.xpath(ITUtil.DIV_ERROR_LOCATOR)).getText();
            if (errorText != null && errorText.contains("error(s) found on page.")) {
                errorText = ITUtil.blanketApprovalCleanUpErrorText(errorText);
                if (driver.findElements(By.xpath(ITUtil.DIV_EXCOL_LOCATOR)).size()>0) { // not present if errors are at the bottom of the page (see left-errmsg below)
                    errorText = ITUtil.blanketApprovalCleanUpErrorText(driver.findElement(By.xpath(ITUtil.DIV_EXCOL_LOCATOR)).getText()); // replacing errorText as DIV_EXCOL_LOCATOR includes the error count
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

    protected void clearText(By by)  throws InterruptedException {
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
           
    protected String getBaseUrlString() {
        return ITUtil.getBaseUrlString();
    }

    protected String getText(By by)  throws InterruptedException {
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
     * Handles simple nested frame content; validates that a frame and nested frame exists before switching to it
     */
    protected void gotoNestedFrame() {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.switchTo().defaultContent();
        if(driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement containerFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(containerFrame);
        }
        if(driver.findElements(By.xpath("//iframe")).size() > 0) {
            WebElement contentFrame = driver.findElement(By.xpath("//iframe"));
            driver.switchTo().frame(contentFrame);
        }
        driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_SEC, TimeUnit.SECONDS);
    }

    protected boolean isElementPresent(By by) {
        return (driver.findElements(by)).size()>0;
    }
    
    protected boolean isElementPresent(String locator) {
        return (driver.findElements(By.cssSelector(locator))).size()>0;
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
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number", "1234567890");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");

        waitAndClickByXpath("//button[@data-loadingmessage='Adding Line...']");

        assertElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number", "https://jira.kuali.org/browse/KULRICE-8564");

        assertEquals("1234567890", getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number","value"));
        assertEquals("2", getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId","value"));

        waitAndClickByXpath("//button[@data-loadingmessage='Deleting Line...']");

        assertElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");
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
            try { driver.findElement(by);
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
            fail(e.getMessage() + " " + by.toString() + " " + message + " " + driver.getCurrentUrl() );
            e.printStackTrace();
        }
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        waitAndClick(By.cssSelector(locator), message);
    }

    protected void waitAndClickByLinkText(String text) throws InterruptedException {
        waitAndClick(By.linkText(text),"");
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
            fail(e.getMessage() + " " + by.toString() + " unable to type text '" + text + "' current url " + driver.getCurrentUrl()
                    + "\n" + ITUtil.deLinespace(driver.getPageSource())
            );
            e.printStackTrace();
        }
    }
    
    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + "  unable to type text '" + text + "'  " + message + " current url " + driver.getCurrentUrl()
                    + "\n" + ITUtil.deLinespace(driver.getPageSource())
            );
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
    
    protected void select(By by, String selectText)  throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        for(WebElement option : options){
            if(option.getText().equals(selectText)){
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

    protected void selectOption(By by, String optionValue)  throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        for(WebElement option : options){
            if(option.getAttribute("value").equals(optionValue)){
                option.click();
                break;
            }
        }
    }

    protected String[] getSelectOptions(By by) throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        String[] optionValues = new String[options.size()];
        int counter=0;
        for(WebElement option : options){
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
        String errorText=null;
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
            if (second >= 60) failed = true;                 
            try { if (failed || (driver.findElements(By.cssSelector(elementLocator))).size()>0) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        checkForIncidentReport(elementLocator); // after timeout to be sure page is loaded
        if (failed) fail("timeout of 60 seconds waiting for " + elementLocator + " " + message + " " + driver.getCurrentUrl());
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
    
    public String [] getAllWindowTitles() {        
        return (String[]) driver.getWindowHandles().toArray();
    }
    
    
    protected void check(By by)  throws InterruptedException {
        WebElement element =driver.findElement(by);
        if(!element.isSelected()){
            element.click();
        }
    }
    
    protected void checkByName(String name) throws InterruptedException {
        check(By.name(name));
    }
    
    protected void checkByXpath(String locator) throws InterruptedException {
        check(By.xpath(locator));
    }

    protected void uncheck(By by)  throws InterruptedException {
        WebElement element =driver.findElement(by);
        if(element.isSelected()){
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
        ((JavascriptExecutor)driver).executeScript(
                "var elements=document.getElementsByName(\""+name+"\");"+
                "for (var i = 0; i < elements.length; i++){"+
                        "elements[i]."+event+"();}"
                        );
    }
    
    protected void fireEvent(String name, String value, String event) {
        ((JavascriptExecutor)driver).executeScript(
                "var elements=document.getElementsByName(\""+name+"\");"+
                "for (var i = 0; i < elements.length; i++){"+
                        "if(elements[i].value=='"+value+"')"+
                        "elements[i]."+event+"();}"
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
}
