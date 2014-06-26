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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.common.JiraAwareFailureUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Ideally the AFT code here should be generic for KRAD AFT testing and not specific to KRAD sampleapp testing.  The idea
 * being that code in this class should be generic enough to be useful to other projects using and testing KRAD without having
 * to pull in code to specific to the KRAD sampleapp.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverAftBase extends JiraAwareAftBase {

    protected static ChromeDriverService chromeDriverService;

    /**
     * div.dataTables_wrapper thead th
     */
    public static final String DATA_TABLE_TH_CSS = "div.dataTables_wrapper thead th";

    protected WebDriver driver;

    protected String jGrowlHeader;

    /**
     *  lookupCriteria[number]
     */
    public static final String LOOKUP_CRITERIA_NUMBER_NAME="lookupCriteria[number]";

    /**
     * ^[\s\S]*error[\s\S]*$"
     */
    public static final String REGEX_ERROR = "^[\\s\\S]*error[\\s\\S]*$";

    /**
     * ^[\s\S]*valid[\s\S]*$
     */
    public static final String REGEX_VALID = "^[\\s\\S]*valid[\\s\\S]*$";

    /**
     * return selected
     */
    public static final String RETURN_SELECTED_BUTTON_TEXT = "return selected";

    /**
     * return value
     */
    public static final String RETURN_VALUE_LINK_TEXT = "return value";

    /**
     * //button[contains(text(),'earch')]
     */
    public static final String SEARCH_XPATH_3 = "//button[contains(text(),'earch')]";

    /**
     * show inactive
     */
    public static final String SHOW_INACTIVE = "show inactive";

    /**
     * div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span
     */
    public static final String SUB_COLLECTION_UIF_DISCLOSURE_SPAN_UIF_HEADER_TEXT_SPAN_XPATH =
            "div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure span.uif-headerText-span";


    protected String sessionId = null;

    protected String testMethodName;

    public @Rule TestName testName = new TestName();

    /**
     * timeout
     */
    public static final String TIMEOUT_MESSAGE = "timeout";

    protected String user = "admin";

    protected int waitSeconds;

    protected WebDriverScreenshotHelper webDriverScreenshotHelper = new WebDriverScreenshotHelper();

    protected void acceptAlertIfPresent() {
        WebDriverUtils.acceptAlertIfPresent(driver);
    }

    /**
     * Accept the javascript alert (clicking OK)
     */
    protected void alertAccept() {
        WebDriverUtils.alertAccept(driver);
    }

    /**
     * Dismiss the javascript alert (clicking Cancel)
     */
    protected void alertDismiss() {
        WebDriverUtils.alertDismiss(driver);
    }

    protected boolean areAllMultiValueSelectsChecked() throws InterruptedException {
        acceptAlertIfPresent();
        WebElement tbody = waitAndGetElementByAttributeValue("role", "alert"); // results table body
        List<WebElement> checkboxes = findElements(By.className("uif-checkboxControl"),tbody);
        for (WebElement checkbox: checkboxes) {
            if (!"true".equals(checkbox.getAttribute("checked"))) {
                return false;
            }
        }
        return true;
    }

    protected boolean areNoMultiValueSelectsChecked() throws InterruptedException {
        WebElement tbody = waitAndGetElementByAttributeValue("role", "alert"); // results table body
        List<WebElement> checkboxes = findElements(By.className("uif-checkboxControl"),tbody);
        for (WebElement checkbox: checkboxes) {
            if (null != checkbox.getAttribute("checked")) {
                return false;
            }
        }
        return true;
    }

    protected void assertAttributeClassRegexDoesntMatch(String field, String regex) throws InterruptedException {
        Thread.sleep(1000);
        String attribute = waitAndGetAttributeByName(field, "class");
        assertTrue("waitAndGetAttributeByName(" + field + ", \"class\") should not be null", attribute != null);
        assertFalse("attribute " + attribute + " matches regex " + regex + " and it should not", attribute.matches(
                regex));
    }

    protected void assertAttributeClassRegexMatches(String field, String regex) throws InterruptedException {
        Thread.sleep(1000);
        String attribute = waitAndGetAttributeByName(field, "class");
        assertTrue("waitAndGetAttributeByName(" + field + ", \"class\") should not be null", attribute != null);
        assertTrue("attribute " + attribute + " doesn't match regex " + regex, attribute.matches(regex));
    }

    protected void assertElementPresentInResultPages(By searchBy) throws Exception {
        while(!isElementPresent(searchBy)) {
            assertTrue("Didn't find expected results in result pages", isNextLinkEnabled());
            waitAndClickByLinkText("Next");
        }
    }

    protected void assertElementsPresentInResultPages(By[] searchBys) throws Exception {
        boolean[] founds = new boolean[searchBys.length];
        boolean allFound = false;

        waitForElementPresentById("uLookupResults_layout_next");

        while (!allFound) {

            for (int i = 0; i < founds.length; i++) {
                if (!founds[i]) {
                    founds[i] = isElementPresent(searchBys[i]);
                }
            }

            allFound = true; // assume we found them all, verify that assumption in the for loop
            for (int i = 0; i < founds.length; i++) {
                if (!founds[i]) {
                    allFound = false;
                }
            }

            if (!allFound) {
                assertTrue("Didn't find expected results in result pages for " + this.getClass().getSimpleName(), isNextLinkEnabled());
                waitAndClickByLinkText("Next");
            }

        }
    }

    protected void assertEmptyInputByName(String name) throws InterruptedException {
        assertTrue(name + " not empty for " + this.getClass().getSimpleName(), waitForElementPresentByName(name)
                .getAttribute("value").equals(""));
    }

    protected void assertFocusTypeBlurError(String field, String textToType) throws InterruptedException {
        fireEvent(field, "focus");
        waitAndTypeByName(field, textToType);
        fireEvent(field, "blur");
        Thread.sleep(500);
        assertAttributeClassRegexMatches(field, REGEX_ERROR);
        clearTextByName(field);
    }

    protected void assertFocusTypeBlurError(String field, String[] errorInputs) throws InterruptedException {
        for (String errorInput: errorInputs) {
            assertFocusTypeBlurError(field, errorInput);
            clearTextByName(field);
        }
    }

    protected void assertFocusTypeBlurValid(String field, String textToType) throws InterruptedException {
        fireEvent(field, "focus");
        waitAndTypeByName(field, textToType);
        fireEvent(field, "blur");
        Thread.sleep(200);
        assertAttributeClassRegexMatches(field, REGEX_VALID);
        assertAttributeClassRegexDoesntMatch(field, REGEX_ERROR);
        clearTextByName(field);
    }

    protected void assertFocusTypeBlurValid(String field, String[] validInputs) throws InterruptedException {
        for (String validInput: validInputs) {
            assertFocusTypeBlurValid(field, validInput);
            clearTextByName(field);
        }
    }

    protected void assertFocusTypeBlurValidation(String field, String[] errorInputs, String[] validInputs) throws InterruptedException {
        assertFocusTypeBlurError(field, errorInputs);
        clearTextByName(field);
        assertFocusTypeBlurValid(field, validInputs);
    }

    protected void assertFocusTypeTabError(String field, String textToType) throws InterruptedException {
        fireEvent(field, "focus");
        waitAndTypeByName(field, textToType);
        driver.switchTo().activeElement().sendKeys(Keys.TAB);
        Thread.sleep(500);
        assertAttributeClassRegexMatches(field, REGEX_ERROR);
        clearTextByName(field);
    }

    protected void assertJgrowlText(String jGrowlText) throws InterruptedException {
        waitForElementPresentByClassName("jGrowl-message");

        // wait for any flash not present errors to fade out
        while (findElement(By.className("jGrowl-message")).getText().contains("Unable to load SWF file")) {
            try {
                driver.findElement(By.className("jGrowl-close")).click(); // no wait, click quick
            } catch (Throwable t) {
                // don't fail because the swf jgrowl has gone away
            }
        }

        // get growl texts
        StringBuilder sb = new StringBuilder("");
        List<WebElement> jGrowls = findElements(By.className("jGrowl-message"));
        for (WebElement jGrowl : jGrowls) {
            if (jGrowl.getText() != null) {
                sb.append(jGrowl.getText()).append("\n");
            }
        }
        String growlText = sb.toString();

        WebDriverUtils.stepMessage("Do jGrowls contain text '" + jGrowlText + "'? " + growlText.contains(jGrowlText));

        //check growl text is present
        assertTrue(growlText + " does not contain " + jGrowlText, growlText.contains(jGrowlText));
    }

    protected void assertLabelWithTextPresent(String labelText) throws InterruptedException {
        jGrowl("Assert Label containing the text " + labelText + " is present");
        waitForElementPresentByXpath("//label[contains(text(), '" + labelText + "')]");
    }

    protected void assertLabelFor(String forElementId, String labelText) {
        assertEquals(labelText, getForLabelText(forElementId));
    }

    protected void assertMultiValueDeselectAllThisPage() throws InterruptedException {
        waitAndClickDropDown("deselect all items on this page");
        if (!areNoMultiValueSelectsChecked()) {
            jiraAwareFail("deselect all items on this page failure");
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    protected void assertMultiValueSelectAllThisPage() throws InterruptedException {
        waitAndClickDropDown("select all items on this page");
        if (!areAllMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("select all items on this page failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    protected void assertTextPresent(String[] text) throws InterruptedException {
        StringBuilder missingText = new StringBuilder("");
        boolean present = true;
        for (int i = 0, s = text.length; i < s; i++) {
            if (i == 0) {
                present = waitForIsTextPresent(text[0]); // wait for the first check
                if (!present) {
                    missingText.append(text[0]);
                }
            } else {
                if (!isTextPresent(text[i])) {
                    present = false;
                    missingText.append(" " + text[i]);
                }
            }
        }
        if (!present) {
            jiraAwareFail(missingText + " not present for " + this.getClass().toString());
        }
    }

    protected void assertTextPresent(String[][] text) throws InterruptedException {
        StringBuilder missingText = new StringBuilder("");
        boolean present = true;
        for (int i = 0, s = text.length; i < s; i++) {
            for (int j = 0, t = text[i].length; j < t; j++) {
                if (i == 0 && j == 0) {
                    present = waitForIsTextPresent(text[0][0]); // wait for the first check
                    if (!present) {
                        missingText.append(text[0][0]);
                    }
                } else {
                    if (!isTextPresent(text[i][j])) {
                        present = false;
                        missingText.append(" " + text[i][j]);
                    }
                }
            }
        }
        if (!present) {
            jiraAwareFail(missingText + " not present for " + this.getClass().toString());
        }
    }

    /**
     * Assert that clicking an element causes a popup window with a specific URL
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param by The locating mechanism of the element to be clicked
     * @param windowName The name of the popup window
     * @param url The URL of the popup window
     */
    protected void assertPopUpWindowUrl(By by, String windowName, String url) {
        findElement(by).click();
        String parentWindowHandle = driver.getWindowHandle();
        // wait page to be loaded
        driver.switchTo().window(windowName).findElements(By.tagName("head"));
        assertEquals(url, driver.getCurrentUrl());
        driver.switchTo().window(parentWindowHandle);
    }

    protected void back() {
        jGrowl("Click browser back button");
        driver.navigate().back();
    }

    protected void check(By by) throws InterruptedException {
        WebElement element = findElement(by);

        if (!element.isSelected()) {
            element.click();
        }
    }

    protected void checkById(String id) throws InterruptedException {
        check(By.id(id));
    }

    protected void checkByName(String name) throws InterruptedException {
        check(By.name(name));
    }

    protected void checkByXpath(String locator) throws InterruptedException {
        check(By.xpath(locator));
    }

    protected void checkForIncidentReport() {
        checkForIncidentReport("", this.getClass().toString());
    }

    protected String incidentReportMessage() {
        return AutomatedFunctionalTestUtils.incidentReportMessage(driver.getPageSource(), "", this.getClass().toString());
    }

    protected void checkForIncidentReport(String locator) {
        checkForIncidentReport(locator, this.getClass().toString());
    }

    protected void checkForIncidentReport(String locator, String message) {
        AutomatedFunctionalTestUtils.checkForIncidentReport(driver.getPageSource(), locator, message, this);
    }

    /**
     * @deprecated {@see #checkForIncidentReport(String, String)}
     */
    protected void checkForIncidentReport(String locator, JiraAwareFailable failable, String message) {
        AutomatedFunctionalTestUtils.checkForIncidentReport(driver.getPageSource(), locator, message, failable);
    }

    protected void clearText(By by) throws InterruptedException {
        findElement(by).clear();
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

    protected void close() {
        driver.close();
    }

    protected void colapseExpandByXpath(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClickByXpath(clickLocator);
        waitNotVisibleByXpath(visibleLocator);
        waitAndClickByXpath(clickLocator);
        waitIsVisibleByXpath(visibleLocator);
    }

    /**
     * If WebDriverUtils.chromeDriverCreateCheck() returns a ChromeDriverService, start it.
     * {@link org.kuali.rice.testtools.selenium.WebDriverUtils#chromeDriverCreateCheck()}
     * @throws Exception
     */
    @BeforeClass
    public static void chromeDriverService() throws Exception {
        chromeDriverService = WebDriverUtils.chromeDriverCreateCheck();
        if (chromeDriverService != null)
            chromeDriverService.start();
    }

    protected void closeAndQuitWebDriver() {
        if (driver != null) {
            if (WebDriverUtils.dontTearDownPropertyNotSet() && WebDriverUtils.dontTearDownOnFailure(isPassed())) {
                try {
                    driver.close();
                } catch (NoSuchWindowException nswe) {
                    System.out.println("NoSuchWindowException closing WebDriver " + nswe.getMessage());
                } finally {
                    if (driver != null) {
                        driver.quit();
                    }
                }
            }
        } else {
            System.out.println("WebDriver is null for " + this.getClass().toString() + " if using a remote hub did you include the port?");
        }
    }

    protected void determineImplicitWait() {
        waitSeconds = WebDriverUtils.configuredImplicityWait();
    }

    protected void determineJgrowlHeader() {
        jGrowlHeader = getClass().getSimpleName() + "." + testMethodName;
        System.out.println(jGrowlHeader + " sessionId is " + sessionId);
    }

    protected void determineTestMethodName() {
        if (testName != null && testName.getMethodName() != null) { // JUnit
            testMethodName = testName.getMethodName();
        }
    }

    protected void determineUser() {
        String givenUser = WebDriverUtils.determineUser(this.toString());
        if (givenUser != null) {
            user = givenUser;
        }
    }

    protected WebElement findButtonByText(String buttonText) {
        return WebDriverUtils.findButtonByText(driver, buttonText);
    }

    protected List<WebElement> findVisibleElements(By by) {
        List<WebElement> webElements = driver.findElements(by);
        List<WebElement> visibleWebElements = new LinkedList<WebElement>();
        for (WebElement webElement: webElements) {
            if (webElement.isDisplayed()) {
                visibleWebElements.add(webElement);
            }
        }

        return visibleWebElements;
    }

    protected List<WebElement> findElements(By by) {
        List<WebElement> found = driver.findElements(by);
        return found;
    }

    protected List<WebElement> findElements(By by, WebElement element) {
        if (element == null) {
            checkForIncidentReport();
            throw new AssertionError("element to findElements on for " + by.toString() + " is null in class " + this.getClass().toString());
        }
        List<WebElement> found = element.findElements(by);
        return found;
    }

    protected void fireEvent(String name, String event) {
        ((JavascriptExecutor) driver).executeScript("var elements=document.getElementsByName(\"" + name + "\");" +
                "for (var i = 0; i < elements.length; i++){" +
                "elements[i]." + event + "();}");
    }

    protected void fireEvent(String name, String value, String event) {
        ((JavascriptExecutor) driver).executeScript("var elements=document.getElementsByName(\"" + name + "\");" +
                "for (var i = 0; i < elements.length; i++){" +
                "if(elements[i].value=='" + value + "')" +
                "elements[i]." + event + "();}");
    }

    /**
     * {@link org.openqa.selenium.interactions.Actions#moveToElement(org.openqa.selenium.WebElement)}
     * @param name
     */
    public void fireMouseOverEventByName(String name) {
        this.fireMouseOverEvent(By.name(name));
    }

    /**
     * {@link org.openqa.selenium.interactions.Actions#moveToElement(org.openqa.selenium.WebElement)}
     * @param id
     */
    public void fireMouseOverEventById(String id) {
        this.fireMouseOverEvent(By.id(id));
    }

    /**
     * {@link org.openqa.selenium.interactions.Actions#moveToElement(org.openqa.selenium.WebElement)}
     * @param locator
     */
    public void fireMouseOverEventByXpath(String locator) {
        this.fireMouseOverEvent(By.xpath(locator));
    }

    /**
     * {@link org.openqa.selenium.interactions.Actions#moveToElement(org.openqa.selenium.WebElement)}
     * @param by
     */
    public void fireMouseOverEvent(By by) {
        Actions builder = new Actions(driver);
        Actions hover = builder.moveToElement(findElement(by));
        hover.perform();
    }

    /**
     * {@link org.openqa.selenium.WebDriver#getWindowHandles()}
     * @return
     */
    public String[] getAllWindowTitles() {
        return (String[]) driver.getWindowHandles().toArray();
    }

    protected String getBaseUrlString() {
        return WebDriverUtils.getBaseUrlString();
    }

    protected int getCssCount(String selector) {
        return getCssCount(By.cssSelector(selector));
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param by
     * @return
     */
    protected int getCssCount(By by) {
        return (findElements(by)).size();
    }

    protected String getDateTimeStampFormatted() {
        return WebDriverUtils.getDateTimeStampFormatted();
    }

    protected String getDateToday() {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(now);
    }

    protected String getDateTomorrow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(now.getTime());
    }

    /**
     * {@inheritDoc}
     *
     * @return WebDriver
     */
    @Override
    protected WebDriver getDriver() {
        return driver;
    }

    protected WebElement getElementByAttribute(String attributeName){
        return findElement(By.cssSelector("[" + attributeName + "]"));
    }

    protected WebElement getElementByDataAttribute(String dataAttributeName){
        return findElement(By.cssSelector("[data-" + dataAttributeName + "]"));
    }

    protected WebElement getElementByDataAttributeValue(String dataAttributeName, String value){
        return findElement(By.cssSelector("[data-" + dataAttributeName + "='" + value + "']"));
    }

    protected WebElement getElementByAttributeValue(String attributeName, String value){
        return findElement(By.cssSelector("[" + attributeName + "='" + value +"']"));
    }

    protected List<WebElement> getElementsByAttributeValue(String attributeName, String value){
        return findElements(By.cssSelector("[" + attributeName + "='" + value + "']"));
    }

    @Override
    protected String getNavigationUrl() {
        String classString = this.getClass().toString();
        if (classString.contains("krad.demo")) {
            return AutomatedFunctionalTestUtils.KRAD_PORTAL;
        } else if (classString.contains("krad.labs")) {
            return AutomatedFunctionalTestUtils.LABS;
        } else {
            return AutomatedFunctionalTestUtils.PORTAL;
        }
    }

    /**
     * Uses Selenium's findElements for getting the options (findElement for the select) method which does not throw a test exception if not found.
     * @param by
     * @return
     * @throws InterruptedException
     */
    protected String[] getSelectOptions(By by) throws InterruptedException {
        WebElement select1 = driver.findElement(by); // don't highlight
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

    /**
     *
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    protected String getText(By by) throws InterruptedException {
        WebElement element = findElement(by);
        return element.getText();
    }

    protected String getTextByClassName(String className) throws InterruptedException {
        return getText(By.className(className));
    }

    protected String getTextById(String id) throws InterruptedException {
        return getText(By.id(id));
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
     * "admin" by default.  Can be overridden using {@see WebDriverUtils#REMOTE_PUBLIC_USER_PROPERTY}
     * @return string
     */
    public String getUserName() {
        return user;
    }

    /**
     * Returns the label text of a label-for element
     * <p>
     * For usage with elements like this: <label for="some-element-id">The text of the Label</label>
     * </p>
     *
     * @param forElementId the id of the element for which to find the label text
     * @return label text
     */
    protected String getForLabelText(String forElementId) {
        return findElement(By.cssSelector("label[for=" + forElementId + "]")).getText();
    }

    protected void gotoIframeById(final String iframeId) {
        if (driver.findElements(By.id(iframeId)).size() > 0) { // find elements so an exception isn't thrown if not found
            WebElement contentFrame = driver.findElement(By.id(iframeId)); // don't highlight
            driver.switchTo().frame(contentFrame);
        } else {
            System.out.println("Unable to find " + iframeId);
        }
    }
    protected void gotoIframeByXpath(final String iframeXpath) {
        if (driver.findElements(By.xpath(iframeXpath)).size() > 0) {  // find elements so an exception isn't thrown if not found
            WebElement contentFrame = driver.findElement(By.xpath(iframeXpath)); // don't highlight
            driver.switchTo().frame(contentFrame);
        } else {
            System.out.println("Unable to find " + iframeXpath);
        }
    }

    protected void gotoLightBox() {
        driver.switchTo().frame(driver.findElement(By.cssSelector(".fancybox-iframe")));
    }

    protected int howManyAreVisible(By by) throws InterruptedException {
        int count = 0;
        if (by == null) {

            return count;
        }

        List<WebElement> webElementsFound = driver.findElements(by);
        for (WebElement webElement: webElementsFound) {
            if (webElement.isDisplayed()) {
                count++;
            }
        }

        return count;
    }

    protected boolean isNextLinkEnabled() {
        return findElements(By.xpath("//a[@id='uLookupResults_layout_next' and @class='next paginate_button paginate_button_disabled']")).size() != 1;
    }

    protected boolean isChecked(By by) {
        return findElement(by).isSelected();
    }

    protected boolean isCheckedById(String id) {
        return isChecked(By.id(id));
    }

    protected boolean isCheckedByName(String name) {
        return isChecked(By.name(name));
    }

    protected boolean isCheckedByXpath(String locator) {
        return isChecked(By.xpath(locator));
    }

    protected boolean isEnabled(By by) {
        return findElement(by).isEnabled();
    }

    protected boolean isEnabledById(String id) {
        return isEnabled(By.id(id));
    }

    protected boolean isEnabledByName(String name) {
        return isEnabled(By.name(name));
    }

    protected boolean isEnabledByXpath(String locator) {
        return isEnabled(By.xpath(locator));
    }

    protected boolean isVisible(By[] bys) {
        if (bys == null || bys.length == 0 ) {
            return false;
        }

        for (int i = 0, s = bys.length; i < s; i++) {

            try {

                if (isVisible(bys[i])) {
                    return true;
                }

            } catch (NoSuchElementException nsee) {
                // don't fail
            }

        }

        return false;
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param by
     * @return
     */
    protected boolean isElementPresent(By by) {
        return (driver.findElements(by)).size() > 0;
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param locator
     * @return
     */
    protected boolean isElementPresent(String locator) {
        return (driver.findElements(By.cssSelector(locator))).size() > 0;
    }

    protected boolean isElementPresentById(String id) {
        return isElementPresent(By.id(id));
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

    protected boolean isElementPresentByDataAttributeValue(String dataAttributeName, String dataAttributeValue) {
        return isElementPresent(By.cssSelector("[data-" + dataAttributeName + "='" + dataAttributeValue + "']"));
    }

    protected boolean isNotVisible(By by) {
        return !(isVisible(by));
    }

    protected Boolean isTextPresent(String text) {
        return WebDriverUtils.isTextPresent(driver, driver.getPageSource(), text);
    }

    protected void jGrowl(String message) {
        WebDriverUtils.jGrowl(driver, jGrowlHeader, false, message);
        if (webDriverScreenshotHelper.screenshotSteps()) {
            try {
                screenshot();
            } catch (IOException ioe) {
                System.err.println("Exception taking screenshot " + ioe.getMessage());
            }
        }
    }

    /**
     * Sticky is used on fail, making a call to jGrowl(String) from this method will result
     * in an infinite loop if JGROWL_ERROR_FAILURE is true so please don't.
     */
    protected void jGrowlSticky(String message) {
        WebDriverUtils.jGrowl(driver, jGrowlHeader, true, message);
        if (webDriverScreenshotHelper.screenshotSteps()) {
            try {
                screenshot();
            } catch (IOException ioe) {
                System.err.println("Exception taking screenshot " + ioe.getMessage());
            }
        }
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

    protected void logout() throws InterruptedException {
        // KRAD Logout requires server configuration, currently env14 is not configured so throws Incident Report.
        //        } else {
        //            String logoutUrl = getBaseUrlString() + "/kr-krad/login?methodToCall=logout";
        //            jGrowl("Logging out with " + logoutUrl);
        //            open(logoutUrl);
    }

    protected String multiValueResultCount() throws InterruptedException {
        WebElement dataTableInfo = waitAndGetElementByAttributeValue("class", "dataTables_info");
        String resultsCount = dataTableInfo.getText();
        resultsCount = resultsCount.substring(resultsCount.indexOf(" of ") + 4, resultsCount.indexOf(" entries")).trim();
        return resultsCount;
    }

    protected void open(String url) {
        driver.get(url);
    }

    @Override
    protected void navigate() throws Exception {
        // No-op for convenience
    }

    protected void screenshot() throws IOException {
        webDriverScreenshotHelper.screenshot(driver, this.getClass().getSimpleName(), testName.getMethodName());
    }

    protected void startSession(Method method) throws Exception {
        testMethodName = method.getName(); // TestNG
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param by
     * @param selectText
     * @throws InterruptedException
     */
    protected void select(By by, String selectText) throws InterruptedException {
        //        checkForIncidentReport(by.toString(), "trying to select text " + selectText); // I think a report will now be picked-up by the jiraAwareFail
        WebElement select1 = findElement(by);
        String name = select1.getAttribute("name");
        WebDriverUtils.jGrowl(getDriver(), "Select " + selectText, false, "Select " + selectText + " from " + name);
        List<WebElement> options = select1.findElements(By.tagName("option"));

        for (WebElement option : options) {
            if (option.getText().equals(selectText)) {
                option.click();
                break; // continuing the loop after clicking on an option often causes cache problems other times it seems breaking here causes hangs?!
            }
        }
    }

    protected void selectByXpath(String locator, String selectText) throws InterruptedException {
        select(By.xpath(locator), selectText);
    }

    protected void selectByName(String name, String selectText) throws InterruptedException {
        select(By.name(name), selectText);
    }

    protected void selectChildWindow() {
        selectWindow(driver.getWindowHandles().toArray()[1].toString());
    }

    protected void selectParentWindow() {
        selectWindow(driver.getWindowHandles().toArray()[0].toString());
    }

    protected void selectTopFrame() {
        driver.switchTo().defaultContent();
    }

    protected void selectWindow(String locator) {
        driver.switchTo().window(locator);
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
     * Tear down test as configured.  Do not allow exceptions to be thrown by tearDown, it kills the test run.
     * {@link WebDriverUtils#tearDown(boolean, String, String, String)}
     * {@link WebDriverUtils#REMOTE_PUBLIC_USERPOOL_PROPERTY}
     * {@link WebDriverUtils#dontTearDownPropertyNotSet()}
     * @throws Exception
     */
    @After
    public void tearDown() {
        try {
            if (isPassed() && WebDriverUtils.dontTearDownPropertyNotSet() && WebDriverUtils.dontTearDownOnFailure(isPassed())) {
                logout();
            } else {
                System.out.println("Last AFT URL: " + driver.getCurrentUrl());
                if (webDriverScreenshotHelper.screenshotOnFailure() || webDriverScreenshotHelper.screenshotSteps()) {
                    screenshot();
                }
            }
            WebDriverUtils.tearDown(isPassed(), sessionId, this.toString().trim(), user, getClass().getSimpleName(), testName.getMethodName());
        } catch (Throwable t) {
            System.out.println("Exception in tearDown " + t.getMessage());
            t.printStackTrace();
        }

        finally {
            try {
                closeAndQuitWebDriver();
            } catch (Throwable t) {
                System.out.println(t.getMessage() + " occurred during tearDown, ignoring to avoid killing test run.");
                t.printStackTrace();
                System.out.println(t.getMessage() + " occurred during tearDown, ignoring to avoid killing test run.");
            }
        }
    }

    /**
     * Failures in testSetup cause the test to not be recorded.  Future plans are to extract form @Before and call at the start of each test.
     * Setup the WebDriver properties, test, and login.  Named testSetUp so it runs after TestNG's startSession(Method)
     * {@link WebDriverUtils#determineUser(String)}
     * {@link WebDriverUtils#setUp(String, String, String, String)}
     */
    @Before
    public void testSetUp() {
        // TODO it would be better if all opening of urls and logging in was not done in setUp, failures in setUp case the test to not be recorded. extract to setUp and call first for all tests.
        try { // Don't throw any exception from this methods, exceptions in Before annotations really mess up maven, surefire, or failsafe
            determineTestMethodName();

            determineImplicitWait();

            determineUser();

            String testUrl = getTestUrl();

            driver = WebDriverUtils.setUp(getUserName(), testUrl, getClass().getSimpleName(), testMethodName);
            this.sessionId = ((RemoteWebDriver) driver).getSessionId().toString();

            determineJgrowlHeader();

            WebDriverUtils.jGrowl(driver, "Open URL", false, "Open " + testUrl);
            login(driver, getUserName(), this);

            navigateInternal(); // SeleniumBaseTest.fail from navigateInternal results in the test not being recorded as a failure in CI.

        } catch (Throwable t) {
            System.out.println("Throwable " + t.getMessage() + " in Before annotated method is very bad, ignoring and letting first method of test class to fail.");
            t.printStackTrace();
            System.out.println("Throwable "
                    + t.getMessage()
                    + " in Before annotated method is very bad, ignoring and letting first method of test class to fail.");
        }
    }

    protected void uncheck(By by) throws InterruptedException {
        WebElement element = findElement(by);
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

    protected void waitAndClick(String locator) throws InterruptedException {
        waitAndClick(locator, this.getClass().toString());
    }

    protected void waitAndClick(By by) throws InterruptedException {
        jiraAwareWaitAndClick(by, this.getClass().toString());
    }

    protected void waitAndClick(By by, JiraAwareFailable failable) throws InterruptedException {
        jiraAwareWaitAndClick(by, this.getClass().toString(), failable);
    }

    protected void waitAndClick(String locator, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.cssSelector(locator), message);
    }

    protected void waitAndClickById(String id) throws InterruptedException {
        jiraAwareWaitAndClick(By.id(id), this.getClass().toString());
    }

    protected void waitAndClickById(String id, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.id(id), message);
    }

    protected void waitAndClickByLinkText(String text) throws InterruptedException {
        waitAndClickByLinkText(text, this.getClass().toString());
    }

    protected void waitAndClickByLinkText(String text, String message) throws InterruptedException {
        waitAndClickByLinkText(text, message, this);
    }

    protected void waitAndClickByLinkText(String text, JiraAwareFailable failable) throws InterruptedException {
        waitAndClickByLinkText(text, this.getClass().toString(), failable);
    }

    protected void waitAndClickByLinkText(String text, String message, JiraAwareFailable failable) throws InterruptedException {
        jGrowl("Click " + text + " link.");
        jiraAwareWaitAndClick(By.linkText(text), message, failable);
    }

    protected void waitAndClickLabeledLink(String label, String linkText) throws InterruptedException {
        jGrowl("Click link " + linkText + " labeled with " + label);
        waitAndClick(By.xpath("//th/label[contains(text(), '"
                + label
                + "')]/../following-sibling::*/div/a[contains(text(), '"
                + linkText
                + "')]"));
    }

    protected void waitAndClickLinkContainingText(String linkText) throws InterruptedException {
        waitAndClickLinkContainingText(linkText, this.getClass().toString());
    }

    protected void waitAndClickLinkContainingText(String linkText, String message) throws InterruptedException {
        jGrowl("Click link containing " + linkText + " .");
        waitAndClickByXpath("//a[contains(text(), '" + linkText + "')]", message);
    }

    protected void waitAndClickByName(String name) throws InterruptedException {
        jGrowl("Click By Name " + name);
        jiraAwareWaitAndClick(By.name(name), this.getClass().toString());
    }

    protected void waitAndClickByValue(String value) throws InterruptedException {
        waitAndGetElementByAttributeValue("value", value).click();
    }

    protected void waitAndClickByXpath(String xpath) throws InterruptedException {
        waitAndClick(By.xpath(xpath));
    }

    protected void waitAndClickByXpath(String xpath, JiraAwareFailable failable) throws InterruptedException {
        waitAndClick(By.xpath(xpath), failable);
    }

    protected void waitAndClickByName(String name, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.name(name), message);
    }

    protected void waitAndClickByXpath(String xpath, String message) throws InterruptedException {
        jiraAwareWaitAndClick(By.xpath(xpath), message);
    }

    protected void waitAndClickButtonByText(String buttonText) throws InterruptedException {
        waitAndClickButtonByText(buttonText, this.getClass().toString());
    }

    protected void waitAndClickButtonByText(String buttonText, String message) throws InterruptedException {
        jGrowl("Click " + buttonText + " button.");
        waitAndClickByXpath("//button[contains(text(), '" + buttonText + "')]", message);
    }

    protected void waitAndClickButtonByExactText(String buttonText) throws InterruptedException {
        waitAndClickButtonByExactText(buttonText, this.getClass().toString());
    }

    protected void waitAndClickButtonByExactText(String buttonText, String message) throws InterruptedException {
        jGrowl("Click " + buttonText + " button.");
        waitAndClickByXpath("//button[normalize-space(.)='" + buttonText + "']", message);
    }

    protected void waitAndClickAllByName(String name) throws InterruptedException{
        List<WebElement> elements = driver.findElements(By.name(name));
        for(WebElement ele : elements){
            ele.click();
        }
    }

    protected void waitAndClickDropDown(String dropDownText) throws InterruptedException {
        jGrowl("Click the " + dropDownText + " drop down.");
        WebElement dropdownMenu = waitAndGetElementByAttributeValue("class", "dropdown-toggle");
        Thread.sleep(1000);
        dropdownMenu.click();
        waitAndClickByLinkText(dropDownText, "dropdown click " + dropDownText + " problem");
    }

    protected void waitAndClickReturnValue() throws InterruptedException {
        waitAndClickByLinkText(RETURN_VALUE_LINK_TEXT, "Unable to click return value " + this.getClass().toString());
    }

    protected void waitAndClickReturnValue(String message) throws InterruptedException {
        waitAndClickByLinkText(RETURN_VALUE_LINK_TEXT, message);
    }

    protected void waitAndClickSearch3() throws InterruptedException {
        jGrowl("Click Search");
        waitAndClickByXpath(SEARCH_XPATH_3);
    }

    protected String waitAndGetAttribute(By by, String attribute) throws InterruptedException {
        jiraAwareWaitFor(by, attribute);

        return findElement(by).getAttribute(attribute);
    }

    /**
     * Get value of any attribute by using element name
     *
     * @param name name of an element
     * @param attribute the name of an attribute whose value is to be retrieved
     */
    protected String waitAndGetAttributeByName(String name, String attribute) throws InterruptedException {
        return waitAndGetAttribute(By.name(name), attribute);
    }

    /**
     * Get value of any attribute by using element xpath
     *
     * @param locator locating mechanism of an element
     * @param attribute the name of an attribute whose value is to be retrieved
     */
    protected String waitAndGetAttributeByXpath(String locator, String attribute) throws InterruptedException {
        return waitAndGetAttribute(By.xpath(locator), attribute);
    }

    protected WebElement waitAndGetElementByAttributeValue(String attribute, String attributeValue) throws InterruptedException {
        return WebDriverUtils.waitAndGetElementByAttributeValue(driver, attribute, attributeValue, waitSeconds);
    }

    protected List<WebElement> waitAndGetElementsByAttributeValue(String attribute, String attributeValue) throws InterruptedException {
        // jenkins implies that implicitlyWait is worse than sleep loop for finding elements by 100+ test failures on the old sampleapp
        //        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        //        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        boolean failed = false;

        for (int second = 0;; second++) {
            Thread.sleep(1000);
            if (second >= waitSeconds)
                failed = true;
            try {
                if (failed || (getElementsByAttributeValue(attribute, attributeValue) != null)) {
                    break;
                }
            } catch (Exception e) {}
        }

        List<WebElement> elements = getElementsByAttributeValue(attribute, attributeValue);
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.configuredImplicityWait(), TimeUnit.SECONDS);
        return elements;
    }

    protected String[] waitAndGetText(By by) throws InterruptedException {
        WebDriverUtils.waitFors(driver, WebDriverUtils.configuredImplicityWait(), by, this.getClass().toString());
        List<WebElement> found = findElements(by);
        String[] texts = new String[found.size()];
        int i = 0;

        for (WebElement element: found) {
            texts[i++] = element.getText();
        }

        if (texts.length == 0) {
            jiraAwareFail(by.toString());
        }

        return texts;
    }


    protected void waitAndSelectByName(String name, String selectText) throws InterruptedException {
        waitFor(By.name(name), selectText + " not found.");
        select(By.name(name), selectText);
    }

    protected WebElement waitAndType(By by, String text) throws InterruptedException {
        return waitAndType(by, text, this.getClass().toString());
    }

    protected WebElement waitAndType(String selector, String text) throws InterruptedException {
        return waitAndType(By.cssSelector(selector), text);
    }

    protected WebElement waitAndTypeById(String id, String text) throws InterruptedException {
        return waitAndType(By.id(id), text);
    }

    protected WebElement waitAndTypeByXpath(String locator, String text) throws InterruptedException {
        return waitAndType(By.xpath(locator), text);
    }

    protected WebElement waitAndTypeByXpath(String locator, String text, String message) throws InterruptedException {
        return waitAndType(By.xpath(locator), text, message);
    }

    protected WebElement waitAndTypeByName(String name, String text) throws InterruptedException {
        return waitAndType(By.name(name), text);
    }

    protected boolean waitAreAnyVisible(By[] bys) throws InterruptedException {
        if (bys == null || bys.length == 0 ) {
            return false;
        }

        for (int second = 0; second < waitSeconds; second++) {

            if (isVisible(bys)) {
                return true;
            }

            Thread.sleep(1000);
        }

        return false;
    }

    /**
     * {@deprecated} use any of the various wait methods over this, waitForElementPresent for example.
     * @throws InterruptedException
     */
    protected void waitForPageToLoad() throws InterruptedException {
        Thread.sleep(5000);
    }

    protected WebElement waitFor(By by) throws InterruptedException {
        return jiraAwareWaitFor(by, this.getClass().toString());
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
    protected void waitFor(By by, String message) throws InterruptedException {
        WebDriverUtils.waitFor(this.driver, this.waitSeconds, by, message);
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param elementLocator
     * @param message
     * @throws InterruptedException
     */
    protected void waitForElementVisible(String elementLocator, String message) throws InterruptedException {
        waitForElementVisibleBy(By.cssSelector(elementLocator), message);
    }

    protected WebElement waitForElementVisibleBy(By by, String message) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        boolean failed = false;

        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                failed = true;
            try {
                if (failed || (driver.findElements(by)).size() > 0)
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        checkForIncidentReport(by.toString()); // after timeout to be sure page is loaded

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);

        if (failed) {
            jiraAwareFail("timeout of " + waitSeconds + " seconds waiting for " + by + " " + message + " " + driver
                    .getCurrentUrl());
            return null;
        }
        return driver.findElements(by).get(0);
    }

    protected void waitForElementVisibleById(String id, String message) throws InterruptedException {
        waitForElementVisibleBy(By.id(id), message);
    }

    protected WebElement waitForElementPresent(By by) throws InterruptedException {
        return jiraAwareWaitFor(by, this.getClass().toString());
    }

    protected WebElement waitForElementPresent(By by, String message) throws InterruptedException {
        return jiraAwareWaitFor(by, message);
    }

    protected WebElement waitForElementPresent(String locator) throws InterruptedException {
        return jiraAwareWaitFor(By.cssSelector(locator), this.getClass().toString());
    }

    protected WebElement waitForElementPresentByClassName(String name) throws InterruptedException {
        return jiraAwareWaitFor(By.className(name), this.getClass().toString());
    }

    protected WebElement waitForElementPresentByClassName(String name, String message) throws InterruptedException {
        return jiraAwareWaitFor(By.className(name), message);
    }

    protected WebElement waitForElementPresentByClassName(String name, int seconds) throws InterruptedException {
        return jiraAwareWaitFor(By.className(name), seconds, this.getClass().toString());
    }

    protected void waitForElementsPresentByClassName(String name, String message) throws InterruptedException {
        jiraAwareWaitFors(By.className(name), message);
    }

    protected WebElement waitForElementPresentById(String id) throws InterruptedException {
        return jiraAwareWaitFor(By.id(id), this.getClass().toString());
    }

    protected void waitForElementPresentById(String id, String message) throws InterruptedException {
        jiraAwareWaitFor(By.id(id), message);
    }

    protected void waitForElementPresentById(String id, String message, int seconds) throws InterruptedException {
        jiraAwareWaitFor(By.id(id), seconds, message);
    }

    protected void waitForElementsPresentById(String id, String message) throws InterruptedException {
        jiraAwareWaitFors(By.id(id), message);
    }

    protected WebElement waitForElementPresentByName(String name) throws InterruptedException {
        return waitForElementPresentByName(name, this.getClass().toString());
    }

    protected WebElement waitForElementPresentByName(String name, String message) throws InterruptedException {
        return jiraAwareWaitFor(By.name(name), message);
    }

    protected WebElement waitForElementPresentByXpath(String xpath) throws InterruptedException {
        return jiraAwareWaitFor(By.xpath(xpath), this.getClass().toString());
    }

    protected WebElement waitForElementPresentByXpath(String xpath, String message) throws InterruptedException {
        return jiraAwareWaitFor(By.xpath(xpath), message);
    }

    protected void waitForElementsPresentByXpath(String xpathLocator) throws InterruptedException {
        jiraAwareWaitFors(By.xpath(xpathLocator), this.getClass().toString());
    }

    protected void waitForElementNotPresent(By by) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        int secondsToWait = WebDriverUtils.configuredImplicityWait();
        while (isElementPresent(by) && secondsToWait > 0) {
            secondsToWait -= 1;
            Thread.sleep(1000);
        }
        if (isElementPresent(by)) {
            jiraAwareFail(by + " is still present");
        }
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
    }

    protected boolean waitForIsTextPresent(String text) throws InterruptedException {
        boolean present = false;
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        int secondsToWait = WebDriverUtils.configuredImplicityWait();
        while (!isTextPresent(text) && secondsToWait > 0) {
            secondsToWait -= 1;
            Thread.sleep(1000);
        }
        if (isTextPresent(text)) {
            present = true;
        }
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        return present;
    }

    protected void waitForTextPresent(String text) throws InterruptedException {
        waitForTextPresent(text, WebDriverUtils.configuredImplicityWait());
    }

    protected void waitForTextPresent(String text, int secondsToWait) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        while (!isTextPresent(text) && secondsToWait > 0) {
            secondsToWait -= 1;
            Thread.sleep(1000);
        }
        if (!isTextPresent(text)) {
            jiraAwareFail(text + " is not present for " + this.getClass().toString());
        }
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
    }

    protected void waitForTextNotPresent(String text) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        int secondsToWait = WebDriverUtils.configuredImplicityWait();
        while (isTextPresent(text) && secondsToWait > 0) {
            secondsToWait -= 1;
            Thread.sleep(1000);
        }
        if (isTextPresent(text)) {
            jiraAwareFail(text + " is still present for " + this.getClass().toString());
        }
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
    }

    protected void waitIsVisible(By by) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        isVisible(by);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        //        for (int second = 0;; second++) {
        //            if (second >= waitSeconds) {
        //                jiraAwareFail(TIMEOUT_MESSAGE + " " + by.toString());
        //            }
        //            if (isVisible(by)) {
        //                break;
        //            }
        //            Thread.sleep(1000);
        //        }
    }

    protected void waitIsVisible(By by, String message) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= waitSeconds) {
                jiraAwareFail(TIMEOUT_MESSAGE + " " + by.toString() + " " + message);
            }
            if (isVisible(by)) {
                break;
            }
            Thread.sleep(1000);
        }
    }

    protected void waitIsVisible(String locator) throws InterruptedException {
        waitIsVisible(By.cssSelector(locator));
    }

    protected void waitIsVisibleByXpath(String locator) throws InterruptedException {
        waitIsVisible(By.xpath(locator));
    }

    protected void waitIsVisibleByXpath(String locator, String message) throws InterruptedException {
        waitIsVisible(By.xpath(locator), message);
    }

    protected void waitNotVisible(By by) throws InterruptedException {
        waitNotVisible(by, this.getClass().getSimpleName());
    }

    protected void waitNotVisible(By by, String message) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= waitSeconds) {
                jiraAwareFail(TIMEOUT_MESSAGE + " " + message);
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

    protected void waitNotVisibleByXpath(String locator, String message) throws InterruptedException {
        waitNotVisible(By.xpath(locator), message);
    }
}
