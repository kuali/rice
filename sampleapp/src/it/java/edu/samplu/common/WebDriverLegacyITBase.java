package edu.samplu.common;

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
            if (second >= 60) failed = true;
            try { if (failed || ITUtil.KUALI_PORTAL_TITLE.equals(driver.getTitle())) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        WebDriverUtil.checkForIncidentReport(driver, message); // after timeout to be sure page is loaded
        if (failed) fail("timeout of " + 60 + " seconds " + message);
    }

    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, "");
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 60) fail(locator + " " + message + " timeout.");
            try { if (driver.findElement(By.cssSelector(locator)) != null ) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        try {
            (driver.findElement(By.cssSelector(locator))).click();
        } catch (Exception e) {
            fail(e.getMessage() + " " + locator + " " + message);
            e.printStackTrace();
        }
    }
}
