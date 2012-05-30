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

package edu.samplu.krad.compview;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

/**
 * tests that a line in a sub collection can be deleted
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DeleteSubCollectionLineIT {
    private DefaultSelenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome",  System.getProperty("remote.public.url"));//"http://localhost:8080/"
        selenium.start();
    }

    @Test
    /**
     * tests that a line in a sub collection can be deleted
     */
    public void deleteSubCollectionLine() throws Exception {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Uif Components (Kitchen Sink)");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        // click on collections page link
        selenium.click("id=u961");
        // Thread.sleep(30000);
        // wait for collections page to load by checking the presence of a sub collection line item
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u1089_line0_line0_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        // change a value in the line to be deleted
        selenium.type("id=u1089_line0_line0_control", "selenium");
        // click the delete button
        selenium.click("id=u1140_line0_line0");
        // confirm that the input box containing the modified value is not present
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (!"selenium".equals(selenium.getValue("id=u1089_line0_line0_control"))) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        // verify that the value has changed for the input box in the line that has replaced the deleted one
        assertNotSame("selenium", selenium.getValue("id=u1089_line0_line0_control"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
