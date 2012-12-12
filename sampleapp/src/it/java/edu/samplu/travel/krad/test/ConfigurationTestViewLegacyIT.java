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

package edu.samplu.travel.krad.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * test that configuration test view items work as expected
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigurationTestViewLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";
    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";

    	
    /**
     * test for text input field label - style setting and refreshWhenChanged for components not in collection
     */
    @Test
	public void testConfigurationTestView() throws Exception {
        openConfigurationTestView();

        selectFrame("iframeportlet");
        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = getAttributeByXpath("//span[@id='" + idPrefix + "TextInputField_label_span']","style");
        // log.info("styleValue is " + styleValue);
        Assert.assertTrue(idPrefix + "textInputField label does not contain expected style", styleValue.replace(" ", "").contains(
                "color:red"));

        // get current list of options
        String refreshTextSelectLocator = "//select[@id='" + idPrefix + "RefreshTextField_control']";
        String[] options1 = getSelectOptionsByXpath(refreshTextSelectLocator);
        String dropDownSelectLocator = "//select[@id='" + idPrefix + "DropDown_control']";
        selectByXpath(dropDownSelectLocator, "Vegetables");        
        Thread.sleep(3000);
        //get list of options after change
        String[] options2 = getSelectOptionsByXpath(refreshTextSelectLocator);
        //verify that the change has occurred
        assertFalse("Field 1 selection did not change Field 2 options https://jira.kuali.org/browse/KULRICE-8163 Configuration Test View Conditional Options doesn't change Field 2 options based on Field 1 selection", options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));
        //confirm that control gets disabled
        selectByXpath(dropDownSelectLocator, "None");
        Thread.sleep(3000);
        assertEquals("true", getAttributeByXpath(refreshTextSelectLocator, "disabled"));
        passed();
	}

    /**
     * open the configuration test view page
     */
    private void openConfigurationTestView() throws InterruptedException {
        waitAndClickByLinkText("KRAD");
        waitAndClickByLinkText("Configuration Test View");
        waitForPageToLoad();
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test    
    public void testAddLineWithSpecificTime() throws Exception{
        openConfigurationTestView();
        
        selectFrame("iframeportlet");
        
        confirmAddLineControlsPresent();
        
        String startTimeId = "//*[@id='" +idPrefix + "StartTime" + addLineIdSuffix + "']";      

        String inputTime = "7:06";
        waitAndTypeByXpath(startTimeId, inputTime);
        String amPmSelectLocator = "//*[@id='" + idPrefix + "StartTimeAmPm" + addLineIdSuffix + "']";

        selectByXpath(amPmSelectLocator, "PM");
        assertEquals("PM", getAttributeByXpath(amPmSelectLocator, "value"));
        Thread.sleep(5000); //allow for ajax refresh        
        waitAndClickByXpath("//button");
        Thread.sleep(5000); //allow for line to be added
        //confirm that line has been added
        assertTrue("line (//input[@value='7:06'])is not present https://jira.kuali.org/browse/KULRICE-8162 Configuration Test View Time Info add line button doesn't addline", isElementPresentByXpath("//input[@value='7:06']"));
        passed();
    }

    /**
     * test adding a line to a collection which has the property refreshWhenChangedPropertyNames set
     * on more than one component.
     */
    @Test
    public void testAddLineWithAllDay() throws Exception {
        openConfigurationTestView();

        selectFrame("iframeportlet");

        confirmAddLineControlsPresent();

        String startTimeId = "//*[@id='" + idPrefix + "StartTime" + addLineIdSuffix + "']";

        String inputTime = "5:20";
        waitAndTypeByXpath(startTimeId, inputTime);

        String allDaySelector = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";

        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickByXpath(allDaySelector);

        Thread.sleep(5000); //allow for ajax refresh
        waitAndClick("div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added           
        passed();
    }

    /**
     * verify that add line controls are present
     */
    private void confirmAddLineControlsPresent() {
        String[] addLineIds = {"StartTime", "StartTimeAmPm", "AllDay"};

        for (String id: addLineIds) {
           String tagId = "//*[@id='" + idPrefix + id + addLineIdSuffix + "']";
           assertTrue("Did not find id " + tagId, isElementPresentByXpath(tagId));
        }
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test  
    public void testAddLineAllDay() throws Exception{
        openConfigurationTestView();
        
        selectFrame("iframeportlet");
        
        confirmAddLineControlsPresent();

        //store number of rows before adding the lines
        String cssCountRows = "div#ConfigurationTestView-ProgressiveRender-TimeInfoSection.uif-group div#ConfigurationTestView-ProgressiveRender-TimeInfoSection_disclosureContent.uif-disclosureContent table tbody tr";
        int rowCount = (getCssCount(cssCountRows));

        String allDayId = "//*[@id='" + idPrefix + "AllDay" + addLineIdSuffix + "']";        
        Thread.sleep(5000); //allow for ajax refresh
        waitAndClickByXpath(allDayId);
        waitAndClick("div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added (by checking for the new delete button)
        assertEquals("line was not added", rowCount + 1, (getCssCount(cssCountRows)));
        passed();
    }
}
