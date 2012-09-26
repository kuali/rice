package edu.samplu.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static com.thoughtworks.selenium.SeleneseTestBase.seleniumEquals;
import static org.junit.Assert.assertEquals;

/**
 * Class to upgrade UpgradedSeleniumITBase tests to WebDriver.
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverLegacyITBase {

    public static final int DEFAULT_WAIT_SEC = 60;

    public abstract String getTestUrl();

    public WebDriver driver;
    static ChromeDriverService chromeDriverService;

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
        driver = WebDriverUtil.setUp(getUserName(), ITUtil.getBaseUrlString() + "/" + getTestUrl());
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
        waitAndClickByName("methodToCall.blanketApprove");
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
        return "admin";
    }

    protected boolean isElementPresent(By by) {
        return (driver.findElements(by)).size()>0;
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
        boolean failed = false;
        for (int second = 0;; second++) {
            Thread.sleep(1000);
            if (second >= 60) failed = true;
            try { if (failed || ITUtil.KUALI_PORTAL_TITLE.equals(driver.getTitle())) break; } catch (Exception e) {}
        }
        WebDriverUtil.checkForIncidentReport(driver, message); // after timeout to be sure page is loaded
        if (failed) fail("timeout of " + 60 + " seconds " + message);
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
//            if (second >= DEFAULT_WAIT_SEC) fail(by.toString() + " " + message + " " + DEFAULT_WAIT_SEC + " sec timeout.");
            try { driver.findElement(by);
                //break;
            } catch (Exception e) {}
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
}
