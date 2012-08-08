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

import edu.samplu.common.ITUtil;
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
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
        focusAndType("name=field1", "test 1");
        focusAndType("name=field102", "test 2");
		// 'Other Fields' navigation link
//        assertCancelConfirmation(); // failing in selenium, but present when testing manually

        ITUtil.waitForElement(selenium, "name=field100");
        focusAndType("name=field100", "here");
        focusAndType("name=field103", "there");
		// 'Validation' navigation link
//      assertCancelConfirmation(); // failing in selenium, but present when testing manually

        ITUtil.waitForElement(selenium, "name=field106");
        focusAndType("name=field106", "this");
        focusAndType("name=field112", "that");
        assertEquals("THAT", selenium.getValue("name=field112"));
		// 'Validation - Regex' navigation link
//      assertCancelConfirmation(); // failing in selenium, but present when testing manually
        ITUtil.waitForElement(selenium, "name=field101");
        assertEquals("val", selenium.getValue("name=field101")); // form is preset to val
        focusAndType("name=field101", "1");
        selenium.focus("name=field104");
        assertEquals("1", selenium.getValue("name=field101"));
		selenium.type("name=field104", "2");
        // 'Progressive Disclosure' navigation link
//      assertCancelConfirmation(); // failing in selenium, but present when testing manually
        fail("selenium.chooseCancelOnNextConfirmation(); is not finding the Dialog see https://jira.kuali.org/browse/KULRICE-7850 "
                + "selenium.chooseCancelOnNextConfirmation() isn't finding dialog");
	}

    private void assertCancelConfirmation() {
        selenium.chooseCancelOnNextConfirmation();
        selenium.click("link=Cancel");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
    }

    private void focusAndType(String fieldLocator, String typeText) {
        selenium.focus(fieldLocator);
        selenium.type(fieldLocator, typeText);
    }
}
