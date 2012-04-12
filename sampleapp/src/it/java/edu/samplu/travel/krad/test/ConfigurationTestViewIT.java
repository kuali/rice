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

    @Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
		selenium.start();
	}

	@Test
	public void testConfigurationTestView() throws Exception {
        openConfigurationTestView();

        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = selenium.getAttribute("//span[@id='textInputField_config_label_span']@style");
        // log.info("styleValue is " + styleValue);
        Assert.assertTrue("textInputField label does not contain expected style", styleValue.replace(" ", "").contains(
                "color:red"));

        // testing for refreshWhenChanged when using spel expressions
        selenium.selectFrame("iframeportlet");
        // get current list of options
        String[] options1 = selenium.getSelectOptions("id=refreshTextField_control");
        selenium.select("id=dropDown_control", "label=Vegetables");
        selenium.click("//option[@value='Vegetables']");
        Thread.sleep(3000);
        //get list of options after change
        String[] options2 = selenium.getSelectOptions("id=refreshTextField_control");
        //verify that the change has occurred
        assertFalse(options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));
        //confirm that control gets disabled
        selenium.select("id=dropDown_control", "label=None");
        Thread.sleep(3000);
        assertEquals("true", selenium.getAttribute("id=refreshTextField_control@disabled"));

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
     */
    @Test
    public void testAddLine() throws Exception{
        openConfigurationTestView();
        
        String[] addLineIds = {"startTime", "startTimeAmPm", "allDay"};
        String idSuffix = "InputField_test_add_control";
        for (String id: addLineIds) {
            String tagId = "id=" + id + idSuffix;
           assertTrue("Did not find id " + tagId, selenium.isElementPresent(tagId));
        }

        String startTimeId = "id=startTime" + idSuffix;
        selenium.focus(startTimeId);
        String inputTime = "7:06";
        selenium.type(startTimeId, inputTime);

        String allDayId = "allDay" + idSuffix;
        selenium.focus(allDayId);
        Thread.sleep(5000); //allow for ajax refresh
        selenium.click(allDayId);
        selenium.click("css=div#timeInfoCollection_test button");
        Thread.sleep(5000); //allow for line to be added

        //confirm that line has been added
        assertTrue("line is not present",selenium.isElementPresent("//input[@value='7:06']"));
    }

    @After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
