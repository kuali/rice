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
package edu.samplu.krad.travelview;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MaintenanceFieldsIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    /**
     * Verify text fields are present and match expected length and max length
     */
    public void testVerifyFields() throws Exception {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
        assertTrue(selenium.isElementPresent("//input[@name='document.newMaintainableObject.dataObject.number' and @type='text' and @size=10 and @maxlength=10]"));
        assertTrue(selenium.isElementPresent("//input[@name='document.newMaintainableObject.dataObject.extension.accountTypeCode' and @type='text' and @size=2 and @maxlength=3]"));
        assertTrue(selenium.isElementPresent("//input[@name='document.newMaintainableObject.dataObject.subAccount' and @type='text' and @size=10 and @maxlength=10]"));
        assertTrue(selenium.isElementPresent("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @size=6 and @maxlength=20]"));
        assertTrue(selenium.isElementPresent("//input[@name='document.newMaintainableObject.dataObject.foId' and @type='text' and @size=5 and @maxlength=10]"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number\" and @type='text' and @size=10 and @maxlength=10]"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId\" and @type='text' and @size=5 and @maxlength=10]"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
