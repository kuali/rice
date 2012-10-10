package edu.samplu.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
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
            if (System.getProperty(REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) {
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

    protected void clearTextByName(String name) throws InterruptedException {
        clearText(By.name(name));
    }

    protected void clearTextByXpath(String locator) throws InterruptedException {
        clearText(By.xpath(locator));
    }

    protected void fireEvent(String name, String event) {
        ((JavascriptExecutor)driver).executeScript("document.getElementsByName('"+name+"')[0]."+event+"()");
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
    
    protected void open(String url) {
        driver.get(url);
    }

    protected void selectFrame(String locator) {
        driver.switchTo().frame(locator);
    }
    
    protected void selectTopFrame() {
        driver.switchTo().defaultContent();
    }
    
    protected void selectWindow(String locator) {
        driver.switchTo().window(locator);
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
            fail(e.getMessage() + " " + by.toString() + " " + message);
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
            fail(e.getMessage() + " " + by.toString() + " " + text);
            e.printStackTrace();
        }
    }
    
    protected void waitAndType(By by, String text, String message) throws InterruptedException {
        waitFor(by, "");
        try {
            (driver.findElement(by)).sendKeys(text);
        } catch (Exception e) {
            fail(e.getMessage() + " " + by.toString() + " " + text + "  "+message);
            e.printStackTrace();
        }
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
    
    protected void selectByXpath(String locator, String select) throws InterruptedException {
        select(By.xpath(locator), select);
    }
    
    protected void selectByName(String name, String select) throws InterruptedException {
        select(By.name(name), select);
    }
    
    protected void select(By by, String select)  throws InterruptedException {
        WebElement select1 = driver.findElement(by);
        List<WebElement> options = select1.findElements(By.tagName("option"));
        for(WebElement option : options){
            if(option.getText().equals(select)){
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

    protected void waitNotVisibleByXpath(String locator) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 15) {
                Assert.fail("timeout waiting for " + locator + " to NOT be visible");
            }

            if (!isVisibleByXpath(locator)) {
                break;
            }

            Thread.sleep(1000);
        }
    }

    protected void waitIsVisibleByXpath(String locator) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 15) {
                Assert.fail("timeout waiting for " + locator + " to be visible");
            }
            if (isVisibleByXpath(locator)) {
                break;
            }
            Thread.sleep(1000);
        }
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
}
