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
		selenium.focus("id=u111");
		selenium.type("id=u111", "test 1");
		selenium.focus("id=u137");
		selenium.type("id=u137", "test 2");
		//selenium.click("id=u13");
		selenium.click("id=u901");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u89")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
		selenium.type("id=u89", "here");
		selenium.type("id=u115", "there");
		//selenium.click("id=u13");
		selenium.click("id=u904");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u144")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u144");
		selenium.type("id=u144", "this");
        selenium.focus("id=u170");
		selenium.type("id=u170", "that");
		//selenium.click("id=u13");
		selenium.click("id=u907");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        Thread.sleep(2000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u82")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u82");
		selenium.type("id=u82", "1");
        selenium.focus("id=u108");
		selenium.type("id=u108", "2");
		selenium.click("id=u913");
		assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
