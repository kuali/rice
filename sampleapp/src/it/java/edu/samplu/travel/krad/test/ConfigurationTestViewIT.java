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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
		selenium.open("/kr-dev/portal.do");
		selenium.type("name=__login_user", "admin");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=KRAD");
		selenium.waitForPageToLoad("30000");
        selenium.isElementPresent("link=Configuration Test View");
        selenium.click("link=Configuration Test View");
		selenium.waitForPageToLoad("30000");

        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = selenium.getAttribute("//span[@id='textInputField_label_span']@style");
        // log.info("styleValue is " + styleValue);
        Assert.assertTrue("textInputField label does not contain expected style", styleValue.replace(" ", "").contains(
                "color:red"));

        // testing for refreshWhenChanged when using spel expressions
        selenium.selectFrame("iframeportlet");
        // get current list of options
        String[] options1 = selenium.getSelectOptions("id=refreshTextField_control");
        selenium.select("id=dropDown_control", "label=Vegetables");
        selenium.click("//option[@value='Vegetables']");
        Thread.sleep(5000);
        //get list of options after change
        String[] options2 = selenium.getSelectOptions("id=refreshTextField_control");
        //verify that the change has occurred
        assertFalse(options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));

	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
