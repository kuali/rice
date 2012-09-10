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
        // open Other Examples page in kitchen sink view
        return "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
    }

    @Test
	public void testDirtyFieldsCheck() throws Exception {
        ITUtil.checkForIncidentReport(selenium, getTestUrl());
        selenium.waitForPageToLoad("50000");
//		selectFrame("iframeportlet");
        Thread.sleep(5000);
        selectWindow("title=Kuali :: Uif Components");
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
        //Asserting text-field style to uppercase. This style would display input text in uppercase.
        assertEquals("uppercase",selenium.getEval("window.document.getElementsByName('field112')[0].style.textTransform;"));
        
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

    private void assertCancelConfirmation() throws InterruptedException {
        selenium.chooseCancelOnNextConfirmation();
        waitAndClick("link=Cancel");
        assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
    }
}
