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
package edu.sampleu.krad.configview;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurationTestViewAftBase extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView&methodToCall=start";
     */
    public static final String BOOKMARK_URL = "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView&methodToCall=start";
    
    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";

    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";


    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath("(//a[text()='Configuration Test View'])");
        switchToWindow(CONFIGURATION_VIEW_WINDOW_TITLE);
        waitForTitleToEqualKualiPortalIndex();   
    }

    protected void testConfigurationTestViewNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testConfigurationTestView(idPrefix);
        testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }

    protected void testConfigurationTestViewBookmark(JiraAwareFailable failable) throws Exception {
        testConfigurationTestView(idPrefix);
        testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }

    protected void testAddLineWithSpecificTime(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//label[@id='" + idPrefix + "TextInputField_label']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        String startTimeId = "//*[@id='" + idPrefix + "StartTime" + addLineIdSuffix + "']";
        String inputTime = "7:06";
        waitAndTypeByXpath(startTimeId, inputTime);
        String amPmSelectLocator = "//*[@id='" + idPrefix + "StartTimeAmPm" + addLineIdSuffix + "']";
        selectByXpath(amPmSelectLocator, "PM");
        assertEquals("PM", waitAndGetAttributeByXpath(amPmSelectLocator, "value"));
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickButtonByText("Add");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added
        assertTrue("line (//input[@value='7:06'])is not present", isElementPresentByXpath("//input[@value='7:06']"));
    }

    protected void testAddLineWithAllDay(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//label[@id='" + idPrefix + "TextInputField_label']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        String startTimeId = "//*[@id='" + idPrefix + "StartTime" + addLineIdSuffix + "']";
        String inputTime = "5:20";
        waitAndTypeByXpath(startTimeId, inputTime);
        String allDayId = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";
        waitAndClickByXpath(allDayId);
        checkForIncidentReport();
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickButtonByText("Add");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added
        assertTrue("line (//input[@checked='checked'])is not present", isElementPresentByXpath("//input[@checked='checked']"));
    }

    protected void testAddLineAllDay(String idPrefix, String addLineIdSuffix) throws Exception {
        waitForElementPresentByXpath("//label[@id='" + idPrefix + "TextInputField_label']");
        confirmAddLineControlsPresent(idPrefix, addLineIdSuffix);
        String allDayId = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";
        waitAndClickByXpath(allDayId);
        checkForIncidentReport();
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickButtonByText("Add");
        Thread.sleep(5000); //allow for line to be added

        //confirm that another line has been added (by checking the number of delete buttons)
        WebElement table = findElement(By.id("ConfigurationTestView-ProgressiveRender-TimeInfoSection_disclosureContent"));
        List<WebElement> columns = findElements(By.xpath("//button[contains(text(), 'Delete')]"), table);
        assertEquals("line was not added", 3, columns.size());

    }

    /**
     * verify that add line controls are present
     */
    protected void confirmAddLineControlsPresent(String idPrefix, String addLineIdSuffix) {
        String[] addLineIds = {"StartTime", "StartTimeAmPm", "AllDay"};

        for (String id : addLineIds) {
            String tagId = "//*[@id='" + idPrefix + id + addLineIdSuffix + "']";
            assertTrue("Did not find id " + tagId, isElementPresentByXpath(tagId));
        }
    }

}
