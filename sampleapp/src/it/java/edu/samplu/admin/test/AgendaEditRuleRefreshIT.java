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

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * test that repeated ajax refreshes work
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditRuleRefreshIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

	@Test
    /**
     * test that repeated ajax refreshes work
     */
	public void testAgendaEditRuleRefreshIT() throws Exception {
		selenium.click("link=Agenda Lookup");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("iframeportlet");
		selenium.click("css=button:contains(earch)"); //  selenium.click("id=32");
        Thread.sleep(3000);
		selenium.waitForPageToLoad("30000");
		selenium.click("css=a[title='edit Agenda Definition withAgenda Id=T1000 ']");  // selenium.click("id=194_line0");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=div.uif-message:contains(Rule1: stub rule lorem ipsum)"); // selenium.click("//li[@id='473_node_0_parent_root']/a");
        selenium.click("css=//li/a/span.uif-message:contains('When TRUE')");
		selenium.click("link=[-] collapse all");

        // click refresh  several times
        for (int i=0; i<6; i++) {
            for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("css=button.kr-refresh-button")) break; } catch (Exception e) {}
                Thread.sleep(1000);
            }

            selenium.click("css=button.kr-refresh-button");
        }
	}
}
