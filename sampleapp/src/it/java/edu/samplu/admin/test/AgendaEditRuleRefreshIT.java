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

package edu.samplu.admin.test;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

/**
 * test that repeated ajax refreshes work
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditRuleRefreshIT extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
		selenium.start();
	}

	@Test
    /**
     * test that repeated ajax refreshes work
     */
	public void testAgendaEditRuleRefreshIT() throws Exception {
		selenium.open("/kr-dev/portal.do");
		selenium.type("name=__login_user", "admin");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Agenda Lookup");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("iframeportlet");
		selenium.click("id=32");
		selenium.waitForPageToLoad("30000");
		selenium.click("id=194_line0");
		selenium.waitForPageToLoad("30000");
		selenium.click("//li[@id='473_node_0_parent_root']/a");
		selenium.click("id=472_node_0_parent_node_0_parent_node_0_parent_root_span");
		selenium.click("id=361");

        // click refresh  several times
        for (int i=0; i<6; i++) {
            for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("id=440")) break; } catch (Exception e) {}
                Thread.sleep(1000);
            }

            selenium.click("id=440");
        }
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
