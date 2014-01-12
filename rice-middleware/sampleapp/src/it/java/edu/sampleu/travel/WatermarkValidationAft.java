/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.travel;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverITBase;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

import static junit.framework.Assert.assertEquals;

/**
 * tests whether the watermarks is work as expected even when they contain an
 * apostrophe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WatermarkValidationAft extends WebDriverITBase {
	@Override
	public String getTestUrl() {
		return AutomatedFunctionalTestUtils.PORTAL;
	}

	@Test
	/**
	 * if watermarking is ok, the cancel link will bring up a confirmation if something was typed into a textbox i.e
	 * the scripts will be working ok
	 */
	public void testWatermarking() throws Exception {
		waitAndClickByLinkText("KRAD");
		waitAndClickByLinkText(WebDriverLegacyITBase.UIF_COMPONENTS_KITCHEN_SINK_LINK_TEXT);
		Thread.sleep(5000);
		//Switch to new window.
		switchWindow();
        waitAndClickByLinkText("Text Controls");
        assertEquals("It's watermarked ",getAttributeByName("field106", "placeholder"));
		assertEquals("Watermark... ",getAttributeByName("field110", "placeholder"));
	}
}
