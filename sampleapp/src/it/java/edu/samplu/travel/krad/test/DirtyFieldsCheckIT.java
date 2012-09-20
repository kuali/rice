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
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import edu.samplu.common.WebDriverITBase;

/**
 * test that dirty fields check happens for all pages in a view
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DirtyFieldsCheckIT extends WebDriverITBase {
	@Override
	public String getTestUrl() {
		// open Other Examples page in kitchen sink view
		return "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
	}

	@Test
	public void testDirtyFieldsCheck() throws Exception {
		checkForIncidentReport(getTestUrl());
		Thread.sleep(5000);
		
		waitAndTypeByName("field1", "test 1");
		waitAndTypeByName("field102", "test 2");
		
		assertCancelConfirmation(); 
	
		// testing manually
		waitForElementPresentByName("field100");
		waitAndTypeByName("field100", "here");
		waitAndTypeByName("field103", "there");
		
	    // 'Validation' navigation link
		assertCancelConfirmation();
	
		// testing manually
		waitForElementPresentByName("field106");
		// //Asserting text-field style to uppercase. This style would display
		// input text in uppercase.
		assertEquals("text-transform: uppercase;",getAttributeValueByName("field112", "style"));
		assertCancelConfirmation(); 
		waitForElementPresentByName("field101");
		assertEquals("val", getAttributeValueByName("field101","value")); 
		clearTextByName("field101");
		waitAndTypeByName("field101", "1");
		waitAndTypeByName("field104", "");

		assertEquals("1", getAttributeValueByName("field101","value"));
		waitAndTypeByName("field104", "2");
		// 'Progressive Disclosure' navigation link
		assertCancelConfirmation();
									
	}

	private void assertCancelConfirmation() throws InterruptedException {
		waitAndClickByLinkText("Cancel");
		dismissAlert();
	}
}
