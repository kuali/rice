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

public class MaintenanceSaveIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    /**
     * Verify Save
     */
    public void testVerifySave() throws Exception {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
        selenium.type("id=u116_control", "Test Document");
        selenium.click("id=u802_control");
        selenium.type("id=u802_control", "1234567890");
        selenium.type("id=u836_control", "EAT");
        selenium.type("id=u853_control", "a1");
        selenium.click("id=u1147");

        for (int second = 0;; second++) {
            if (second >= 15) {
                break;
            }

            if (selenium.isTextPresent("error")) {
                fail("An error appeared after clicking save");
            }

            Thread.sleep(1000);
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
