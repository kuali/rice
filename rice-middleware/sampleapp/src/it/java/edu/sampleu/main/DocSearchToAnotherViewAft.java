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
package edu.sampleu.main;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.Alert;

/**
 * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
 * and therefore interfere with JS functionality like validation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchToAnotherViewAft extends WebDriverLegacyITBase {

    @Override
    protected String getBookmarkUrl() {
        return AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Document%20Search&channelUrl=" + WebDriverUtils
                .getBaseUrlString()
                + "/kew/DocumentSearch.do?docFormKey=88888888&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
    }

	@Test
    /**
     * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
     * and therefore interfere with JS functionality like validation
     */
	public void testDocSearchToAnotherViewBookmark() throws Exception {
		waitAndClick("img[alt=\"doc search\"]");
		waitForPageToLoad();
		selectFrame("iframeportlet");
		waitAndClick("td.infoline > input[name=\"methodToCall.search\"]");
	//	selectFrame("relative=top");
		driver.switchTo().defaultContent();
		waitAndClickByLinkText("Main Menu");
		waitAndClickByLinkText("People Flow");
		waitForPageToLoad();
		selectFrame("iframeportlet");
		waitAndClickByLinkText("Create New");
		waitForPageToLoad();
		fireEvent("document.documentHeader.documentDescription", "focus");
		waitAndTypeByName("document.documentHeader.documentDescription", "sample description");
		fireEvent("document.documentHeader.explanation", "focus");
		waitAndTypeByName("document.documentHeader.explanation", "sample explanation");		
//		((JavascriptExecutor)driver).executeScript("document.getElementById(\"uif-cancel\").focus();");
        jGrowl("Click Cancel");
        waitAndClickByXpath("//div[@class='uif-footer clearfix']/button[contains(text(), 'Cancel')]");
		Thread.sleep(5000);
		final String text = "Form has unsaved data. Do you want to leave anyway?";
		Alert a=driver.switchTo().alert();
		assertTrue(a.getText().equals(text));
		a.dismiss();
        passed();
	}
}
