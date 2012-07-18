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

import static org.junit.Assert.*;

public class MaintenanceAdHocRecipientsIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    /**
     * Verify the Ad Hoc Recipients section and fields
     */
    public void testVerifyAdHocRecipients() throws Exception {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");

        selenium.click("css=#u416_toggle > span.uif-headerText-span");
        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (selenium.isElementPresent("css=#u440 > h4.uif-headerText > span.uif-headerText-span")) {
                break;
            }

            Thread.sleep(1000);
        }

        assertTrue(selenium.isElementPresent("//select[@name=\"newCollectionLines['document.adHocRoutePersons'].actionRequested\"]"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.adHocRoutePersons'].name\" and @type=\"text\"]"));
        assertTrue(selenium.isElementPresent("css=#u551_add"));
        assertTrue(selenium.isElementPresent("//select[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].actionRequested\"]"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientNamespaceCode\" and @type='text']"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientName\" and @type='text']"));
        assertTrue(selenium.isElementPresent("css=#u700_add"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
