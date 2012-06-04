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

import com.thoughtworks.selenium.DefaultSelenium;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

import static junit.framework.Assert.*;

/**
 * test that configuration test view items work as expected
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigurationTestViewIT {
    private DefaultSelenium selenium;
    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";
    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";
    @Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
		selenium.start();
	}

	@Test
    /**
     * test for text input field label - style setting and refreshWhenChanged for components not in collection
     */
	public void testConfigurationTestView() throws Exception {
        openConfigurationTestView();

        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = selenium.getAttribute("//span[@id='" + idPrefix + "TextInputField_label_span']@style");
        // log.info("styleValue is " + styleValue);
        Assert.assertTrue(idPrefix + "textInputField label does not contain expected style", styleValue.replace(" ", "").contains(
                "color:red"));

        // testing for refreshWhenChanged when using spel expressions
        selenium.selectFrame("iframeportlet");
        // get current list of options
        String refreshTextSelectLocator = "id=" + idPrefix + "RefreshTextField_control";
        String[] options1 = selenium.getSelectOptions(refreshTextSelectLocator);
        String dropDownSelectLocator = "id=" + idPrefix + "DropDown_control";
        selenium.select(dropDownSelectLocator, "label=Vegetables");
        selenium.click("//option[@value='Vegetables']");
        Thread.sleep(3000);
        //get list of options after change
        String[] options2 = selenium.getSelectOptions(refreshTextSelectLocator);
        //verify that the change has occurred
        assertFalse(options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));
        //confirm that control gets disabled
        selenium.select(dropDownSelectLocator, "label=None");
        Thread.sleep(3000);
        assertEquals("true", selenium.getAttribute(refreshTextSelectLocator+ "@disabled"));

	}

    /**
     * open the configuration test view page
     */
    private void openConfigurationTestView() {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("30000");
        selenium.isElementPresent("link=Configuration Test View");
        selenium.click("link=Configuration Test View");
        selenium.waitForPageToLoad("30000");
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test
    public void testAddLineWithSpecificTime() throws Exception{
        openConfigurationTestView();
        confirmAddLineControlsPresent();

        String startTimeId = "id=" +idPrefix + "StartTime" + addLineIdSuffix;
        selenium.focus(startTimeId);
        String inputTime = "7:06";
        selenium.type(startTimeId, inputTime);

        String amPmSelectLocator = "id=" + idPrefix + "StartTimeAmPm" + addLineIdSuffix;
        selenium.click(amPmSelectLocator);
        selenium.select(amPmSelectLocator, "label=PM");
        assertEquals("PM", selenium.getSelectedLabel(amPmSelectLocator));

        Thread.sleep(5000); //allow for ajax refresh
        selenium.click("css=div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added
        assertTrue("line is not present", selenium.isElementPresent("//input[@value='7:06']"));
    }

    /**
         * test adding a line to a collection which  has the property refreshWhenChangedPropertyNames
         * set on more than one component.
         */
        @Test
        public void testAddLineWithAllDay() throws Exception{
            openConfigurationTestView();
            confirmAddLineControlsPresent();

            String startTimeId = "id=" +idPrefix + "StartTime" + addLineIdSuffix;
            selenium.focus(startTimeId);
            String inputTime = "5:20";
            selenium.type(startTimeId, inputTime);

            String allDaySelector = "id=" + idPrefix + "AllDay" + addLineIdSuffix;
            selenium.focus(allDaySelector);
            Thread.sleep(5000); //allow for ajax refresh
            selenium.click(allDaySelector);

            String amPmSelectLocator = "id=" + idPrefix + "StartTimeAmPm" + addLineIdSuffix;
            selenium.click(amPmSelectLocator);
            selenium.select(amPmSelectLocator, "label=PM");
            assertEquals("PM", selenium.getSelectedLabel(amPmSelectLocator));

            Thread.sleep(5000); //allow for ajax refresh
            selenium.click("css=div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
            Thread.sleep(5000); //allow for line to be added

            //confirm that line has been added
            assertTrue("line is not present", selenium.isElementPresent("//input[@value='5:20']"));
        }

    /**
     * verify that add line controls are present
     */
    private void confirmAddLineControlsPresent() {
        String[] addLineIds = {"StartTime", "StartTimeAmPm", "AllDay"};

        for (String id: addLineIds) {
            String tagId = "id=" + idPrefix + id + addLineIdSuffix;
           assertTrue("Did not find id " + tagId, selenium.isElementPresent(tagId));
        }
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test
    @Ignore("count rows through CSS or XPATH fails")
    public void testAddLineAllDay() throws Exception{
        openConfigurationTestView();
        confirmAddLineControlsPresent();

        //store number of rows before adding the lines
        String cssCountRows = "div#ConfigurationTestView-ProgressiveRender-TimeInfoSection.uif-group div#ConfigurationTestView-ProgressiveRender-TimeInfoSection_disclosureContent.uif-disclosureContent table tbody tr";
        int rowCount = (selenium.getCssCount(cssCountRows)).intValue();

        String allDayId = "id=" + idPrefix + "AllDay" + addLineIdSuffix;
        selenium.focus(allDayId);
        Thread.sleep(5000); //allow for ajax refresh
        selenium.click(allDayId);
        selenium.click("css=div#ConfigurationTestView-ProgressiveRender-TimeInfoSection button");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added (by checking for the new delete button)
        assertEquals("line was not added", rowCount + 1, (selenium.getCssCount(cssCountRows)).intValue());
    }


    @After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
