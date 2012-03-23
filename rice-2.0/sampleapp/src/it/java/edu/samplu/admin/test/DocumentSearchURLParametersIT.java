/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.admin.test;



import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessorKEWAdapter;
import org.kuali.rice.kew.impl.document.search.DocumentSearchCriteriaBoLookupableHelperService;
import org.kuali.rice.kew.util.Utilities;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.kuali.rice.krad.util.KRADConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests docsearch url parameters
 */
@Ignore
public class DocumentSearchURLParametersIT {
    private static final String ADMIN_USER_NETWORK_ID = "admin";

    private WebDriver driver;
    private String base;

    private static final String DOCUMENT_TYPE_NAME = "KualiNotification";
    private static final String ADVANCED_SEARCH_ONLY_FIELD = "applicationDocumentId";

    private static final Map<String, String> CORE_FIELDS = new HashMap<String, String>();
    static {
        // basic
        CORE_FIELDS.put("documentTypeName", DOCUMENT_TYPE_NAME);
        CORE_FIELDS.put("documentId", "1234");
        CORE_FIELDS.put("initiatorPrincipalName", "CURRENT_USER");
        CORE_FIELDS.put("dateCreated", "11/11/11");

    }
    private static final Map<String, String> BASIC_FIELDS = new HashMap<String, String>();
    static {
        BASIC_FIELDS.putAll(CORE_FIELDS);
        // searchable attrs
        BASIC_FIELDS.put("documentAttribute.notificationContentType", "testType");
        BASIC_FIELDS.put("documentAttribute.notificationChannel", "testChannel");
        BASIC_FIELDS.put("documentAttribute.notificationProducer", "testProducer");
        BASIC_FIELDS.put("documentAttribute.notificationPriority", "testPriority");
        BASIC_FIELDS.put("documentAttribute.notificationRecipients", "testRecipients");
        BASIC_FIELDS.put("documentAttribute.notificationSenders", "testSenders");
        BASIC_FIELDS.put("saveName", "testBasicSearchFields_saved_search");
        BASIC_FIELDS.put("isAdvancedSearch", "NO");
    }

    private static final Map<String, String> ADVANCED_FIELDS = new HashMap<String, String>();
    static {
        ADVANCED_FIELDS.put("approverPrincipalName", "testApproverName");
        ADVANCED_FIELDS.put("viewerPrincipalName", "testViewerName");
        ADVANCED_FIELDS.put("applicationDocumentId", "testApplicationDocumentId");
        // ADVANCED_FIELDS.put("routeNodeName", "Adhoc Routing");
        ADVANCED_FIELDS.put("dateApproved", "01/01/01");
        ADVANCED_FIELDS.put("dateLastModified", "02/02/02");
        ADVANCED_FIELDS.put("dateFinalized", "03/03/03");
        ADVANCED_FIELDS.put("title", "test title");
        ADVANCED_FIELDS.put("isAdvancedSearch", "YES");
    }

    @Before
    public void setUp() throws Exception {
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox());
        // default to locally running dev sampleapp
        base = StringUtils.defaultIfEmpty(System.getProperty("remote.public.url"), "http://localhost:8080/kr-dev");
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    private void performLogin() {
        driver.get(base);
        assertEquals("Login", driver.getTitle());
        driver.findElement(By.name("__login_user")).sendKeys(ADMIN_USER_NETWORK_ID);
        driver.findElement(By.xpath("//input[@value='Login']")).click();
    }

    private String getDocSearchURL(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry: params.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(entry.getValue()) + "&");
        }
        return getDocSearchURL(sb.toString());
        
    }
    private String getDocSearchURL(String params) {
        return base + "/kew/DocumentSearch.do?" + params;
    }
    
    private WebElement findElementByTagAndName(String tag, String name) {
        return driver.findElement(By.xpath("//" + tag + "[@name='" + name + "']"));
    }
    
    private WebElement findInput(String name) {
        return findElementByTagAndName("input", name);
    }
    
    private WebElement findModeToggleButton() {
        WebElement toggleAdvancedSearch = null;
        try {
            return driver.findElement(By.id("toggleAdvancedSearch"));
        } catch (NoSuchElementException e) {
            fail("toggleAdvancedSearch button not found");
            return null;
        }
    }

    private void assertSearchDetailMode(WebElement e, boolean advanced) {
        assertEquals((advanced ? "basic" : "detailed") + " search", e.getAttribute("title"));

        try {
            findInput(ADVANCED_SEARCH_ONLY_FIELD);
            if (!advanced) fail("Advanced search field found in basic search");
        } catch (NoSuchElementException nsee) {
            if (advanced) fail("Advanced search field not found in advancedsearch");
        }
    }

    @Test
    public void testBasicSearchMode() throws InterruptedException{
        performLogin();
        driver.get(getDocSearchURL(""));
        WebElement toggle = findModeToggleButton();
        assertSearchDetailMode(toggle, false);
        toggle.click();
        assertSearchDetailMode(findModeToggleButton(), true);
    }

    @Test
    public void testAdvancedSearchMode() {
        performLogin();
        driver.get(getDocSearchURL((DocumentSearchCriteriaProcessorKEWAdapter.ADVANCED_SEARCH_FIELD + "=YES")));
        WebElement toggle = findModeToggleButton();
        assertSearchDetailMode(toggle, true);
        toggle.click();
        assertSearchDetailMode(findModeToggleButton(), false);
    }

    @Test
    public void testHeaderBarDisabled() throws InterruptedException{
        performLogin();

        driver.get(getDocSearchURL("headerBarEnabled=false"));
        assertTrue(driver.findElements(By.id("headerarea-small")).isEmpty());
        assertInputPresence(CORE_FIELDS, true);
        driver.get(getDocSearchURL("headerBarEnabled=true"));
        assertFalse(driver.findElements(By.id("headerarea-small")).isEmpty());
        assertInputPresence(CORE_FIELDS, true);
    }

    @Test
    public void testCriteriaDisabled() throws InterruptedException{
        performLogin();
        driver.get(getDocSearchURL("searchCriteriaEnabled=NO"));
        assertInputPresence(CORE_FIELDS, false);
        driver.get(getDocSearchURL("searchCriteriaEnabled=true"));
        assertInputPresence(CORE_FIELDS, true);
    }

    @Test
    public void testBasicSearchFields() throws InterruptedException{
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        driver.get(getDocSearchURL(BASIC_FIELDS));

        assertInputValues(BASIC_FIELDS);

        driver.findElement(By.id("toggleAdvancedSearch")).click();

        Map<String, String> expected = new HashMap<String, String>(BASIC_FIELDS);
        for (Map.Entry<String, String> entry: ADVANCED_FIELDS.entrySet()) {
            if (!"isAdvancedSearch".equals(entry.getKey())) {
                expected.put(entry.getKey(), "");
            } else {
                expected.put(entry.getKey(), entry.getValue());
            }
        }
        assertInputValues(expected);
    }

    @Test
    public void testBasicSearchFieldsAndExecuteSearch() throws InterruptedException {
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        Map<String, String> fields = new HashMap<String, String>();
        fields.putAll(BASIC_FIELDS);
        fields.put("methodToCall", "search");
        driver.get(getDocSearchURL(fields));

        assertInputValues(BASIC_FIELDS);

        // verify that it attempted the search
        assertTrue(driver.getPageSource().contains("No values match this search"));

        driver.findElement(By.id("toggleAdvancedSearch")).click();

        Map<String, String> expected = new HashMap<String, String>(BASIC_FIELDS);
        for (Map.Entry<String, String> entry: ADVANCED_FIELDS.entrySet()) {
            if (!"isAdvancedSearch".equals(entry.getKey())) {
                expected.put(entry.getKey(), "");
            } else {
                expected.put(entry.getKey(), entry.getValue());
            }
        }
        assertInputValues(expected);

        // I guess switching modes doesn't re-execute the search
        // assertTrue(driver.getPageSource().contains("No values match this search"));
    }

    @Test
    public void testBasicSearchFieldsAndExecuteSearchWithHiddenCriteria() throws InterruptedException {
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        Map<String, String> fields = new HashMap<String, String>();
        fields.putAll(BASIC_FIELDS);
        fields.put("methodToCall", "search");
        fields.put("searchCriteriaEnabled", "NO");
        driver.get(getDocSearchURL(fields));

        assertInputPresence(BASIC_FIELDS, false);

        // verify that it attempted the search
        assertTrue(driver.getPageSource().contains("No values match this search"));

        // NOTE: toggling modes re-enables the search criteria
    }

    @Test
    public void testAdvancedSearchFields() throws InterruptedException{
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        Map<String, String> values = new HashMap<String, String>(BASIC_FIELDS);
        values.putAll(ADVANCED_FIELDS);
        driver.get(getDocSearchURL(values));

        assertInputValues(values);

        driver.findElement(By.id("toggleAdvancedSearch")).click();

        assertInputValues(BASIC_FIELDS);
    }

    @Test
    public void testAdvancedSearchFieldsAndExecuteSearch() throws InterruptedException{
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        Map<String, String> expected = new HashMap<String, String>(BASIC_FIELDS);
        expected.putAll(ADVANCED_FIELDS);
        
        Map<String, String> values = new HashMap<String, String>(expected);
        values.put("methodToCall", "search");
        driver.get(getDocSearchURL(values));

        assertInputValues(expected);

        // verify that it attempted the search
        assertTrue(driver.getPageSource().contains("No values match this search"));

        driver.findElement(By.id("toggleAdvancedSearch")).click();

        assertInputValues(BASIC_FIELDS);
    }

    @Test
    public void testAdvancedSearchFieldsAndExecuteSearchWithHiddenCriteria() throws InterruptedException {
        performLogin();

        // criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName +
        Map<String, String> expected = new HashMap<String, String>(BASIC_FIELDS);
        expected.putAll(ADVANCED_FIELDS);

        Map<String, String> values = new HashMap<String, String>(expected);
        values.put("methodToCall", "search");
        values.put("searchCriteriaEnabled", "NO");
        driver.get(getDocSearchURL(values));

        assertInputPresence(expected, false);

        // verify that it attempted the search
        assertTrue(driver.getPageSource().contains("No values match this search"));

        // NOTE: toggling modes re-enables the search criteria
    }

    /**
     * Supplying a saveName does not result in the saved search getting loaded.
     * @throws InterruptedException
     */
    @Test
    public void testSupplyingSavedSearchNameDoesNothing() throws InterruptedException {
        performLogin();

        // get the search saved
        driver.get(getDocSearchURL(BASIC_FIELDS));

        driver.get(getDocSearchURL("saveName=testBasicSearchFields_saved_search"));
        
        Map<String, String> emptyForm = new HashMap<String, String>();
        for (String key: CORE_FIELDS.keySet()) {
            emptyForm.put(key, "");
        }

        assertInputValues(emptyForm);
    }
    
    private void assertInputValues(Map<String, String> fields) {
        for (Map.Entry<String, String> entry: fields.entrySet()) {
            String value = findInput(entry.getKey()).getAttribute("value");
            assertEquals("Field '" + entry.getKey() + "' expected '" + entry.getValue() + "' got '" + value + "'", entry.getValue(), value);
        }
    }

    private void assertInputPresence(Map<String, String> fields, boolean present) {
        for (String name: fields.keySet()) {
            if (present) {
                assertTrue("Expected field '" + name + "' to be present", driver.findElements(By.name(name)).size() != 0);
            } else {
                assertEquals("Expected field '" + name + "' not to be present", 0, driver.findElements(By.name(name)).size());
            }
        }
    }
}
