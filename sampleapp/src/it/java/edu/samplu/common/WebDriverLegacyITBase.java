package edu.samplu.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;

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

    protected void assertTextPresent(String text) {
        assertTextPresent(text, "");
    }

    protected void assertTextPresent(String text, String message) {
        if (!driver.getPageSource().contains(text)) {
            Assert.fail(text + " not present " + message);
        }
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

    /**
     * Override in test to define a user other than admin
     * @return
     */
    public String getUserName() {
        return "admin";
    }

    protected void selectFrame(String locator) {
        driver.switchTo().frame(locator);
    }

    protected void waitForElementPresent(String locator) {
        driver.findElement(By.cssSelector(locator));
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
        for (int second = 0;; second++) {
            Thread.sleep(1000);
            if (second >= DEFAULT_WAIT_SEC) fail(by.toString() + " " + message + " " + DEFAULT_WAIT_SEC + " sec timeout.");
            try { if (driver.findElement(by) != null ) break; } catch (Exception e) {}
        }
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

    protected void waitAndClickByName(String name, String message) throws InterruptedException {
        waitAndClick(By.name(name), message);
    }
}
