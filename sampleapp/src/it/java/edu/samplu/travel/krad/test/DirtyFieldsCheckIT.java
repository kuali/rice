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

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
/**
 * test that dirty fields check happens for all pages in a view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DirtyFieldsCheckIT {
    private DefaultSelenium selenium;

    @Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
		selenium.start();
	}

	@Test
	public void testDirtyFieldsCheck() throws Exception {
		selenium.open("/kr-dev/portal.do");
		selenium.type("name=__login_user", "admin");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=KRAD");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Uif Components (Kitchen Sink)");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("iframeportlet");
        Thread.sleep(3000);
		selenium.focus("id=u73_control");
		selenium.type("id=u73_control", "test 1");
		selenium.focus("id=u103_control");
		selenium.type("id=u103_control", "test 2");
		// 'Other Fields' navigation link
		selenium.click("id=u967");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u51_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u51_control");
		selenium.type("id=u51_control", "here");
        selenium.focus("id=u81_control");
		selenium.type("id=u81_control", "there");
		// 'Validation' navigation link
		selenium.click("id=u970");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u114_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u114_control");
		selenium.type("id=u114_control", "this");
        selenium.focus("id=u144_control");
		selenium.type("id=u144_control", "that");
		// 'Validation - Regex' navigation link
		selenium.click("id=u973");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(2000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u44_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u44_control");
		selenium.type("id=u44_control", "1");
        selenium.focus("id=u74_control");
		selenium.type("id=u74_control", "2");
        // 'Progressive Disclosure' navigation link
		selenium.click("id=u976");
		assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
