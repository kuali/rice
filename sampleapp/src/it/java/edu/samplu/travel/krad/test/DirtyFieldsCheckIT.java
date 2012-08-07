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

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
/**
 * test that dirty fields check happens for all pages in a view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DirtyFieldsCheckIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

	@Test
	public void testDirtyFieldsCheck() throws Exception {
		selenium.click("link=KRAD");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Uif Components (Kitchen Sink)");
		selenium.waitForPageToLoad("50000");		
//		selenium.selectFrame("iframeportlet");
        Thread.sleep(5000);
        selenium.selectWindow("title=Kuali :: Uif Components");		
		selenium.focus("id=u50_control");
		selenium.type("id=u50_control", "test 1");
		selenium.focus("id=u101_control");
		selenium.type("id=u101_control", "test 2");
		// 'Other Fields' navigation link
		//selenium.chooseCancelOnNextConfirmation();
		//selenium.click("id=u30");		
        //assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        //Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u65_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u65_control");
		selenium.type("id=u65_control", "here");
        selenium.focus("id=u116_control");
		selenium.type("id=u116_control", "there");
		// 'Validation' navigation link
		//	selenium.click("id=u30");
		//  assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        //  Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u167_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u167_control");
		selenium.type("id=u167_control", "this");
        selenium.focus("id=u227_control");
		selenium.type("id=u227_control", "that");
		// 'Validation - Regex' navigation link
		//selenium.click("id=u29");
        //assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
        //Thread.sleep(3000);
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u80_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.focus("id=u80_control");
		selenium.type("id=u80_control", "1");
        selenium.focus("id=u131_control");
		selenium.type("id=u131_control", "2");
        // 'Progressive Disclosure' navigation link
		selenium.click("id=u30");
		//assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
	}
}
