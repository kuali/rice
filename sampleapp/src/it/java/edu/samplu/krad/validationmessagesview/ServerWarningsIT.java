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

package edu.samplu.krad.validationmessagesview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerWarningsIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

	@Test
	public void testServerWarningsIT() throws Exception {
		selenium.click("//button[contains(.,'Get Warning Messages')]");
		selenium.waitForPageToLoad(ITUtil.DEFAULT_WAIT_FOR_PAGE_TO_LOAD_TIMEOUT);
		ITUtil.assertTrue(selenium.isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]"), "css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] not visible");
		ITUtil.assertTrue(selenium.isElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem"), "css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem not present");
		ITUtil.assertTrue(selenium.isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"), "css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] not visible");
		ITUtil.assertTrue(selenium.isElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem"), "css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem not present");
		ITUtil.assertTrue(selenium.isElementPresent("css=div[data-role=\"InputField\"] img[alt=\"Warning\"]"), "css=div[data-role=\"InputField\"] img[alt=\"Warning\"] not present");
		selenium.mouseOver("//a[contains(.,'Field 1')]");
		ITUtil.assertTrue(selenium.isElementPresent("css=.uif-warningHighlight"), "css=.uif-warningHighlight no present when //a[contains(.,'Field 1')] is moused over");
		selenium.click("//a[contains(.,'Field 1')]");
        ITUtil.waitForElementVisible(selenium, "css=.jquerybubblepopup-innerHtml", " after click on //a[contains(.,'Field 1')]");

		ITUtil.assertTrue(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems"), "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems not visible");
		ITUtil.assertTrue(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"), "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible");
        typeBlurFocus("name=field1", "");

        ITUtil.waitForElementVisible(selenium, "css=.jquerybubblepopup-innerHtml", " not visible after typing nothing in name=field1 then firing blur and focus events");

		ITUtil.assertTrue(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"), "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events");

        ITUtil.waitForElementVisible(selenium, "css=.jquerybubblepopup-innerHtml> .uif-clientMessageItems", " not visible after typing nothing in name=field1 then firing blur and focus events");

		ITUtil.assertTrue(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"), "css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events" );

        typeBlurFocus("name=field1", "b");

        ITUtil.waitForElementVisible(selenium, "css=.jquerybubblepopup-innerHtml> .uif-clientMessageItems", " not visible after typing b in name=field1 then firing blur and focus events");

		ITUtil.assertTrue(!selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"), "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing b in name=field1 then firing blur and focus events");
		ITUtil.assertTrue(!selenium.isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems"),
                "css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems");

		selenium.type("name=field1", "");
		selenium.fireEvent("name=field1", "focus");
		selenium.fireEvent("name=field1", "blur");
		ITUtil.assertTrue(selenium.isElementPresent("css=.uif-hasError"), "css=.uif-hasError is not present after typing nothing in name=field1 and then firing focus and blur events");
		ITUtil.assertTrue(selenium.isElementPresent("css=img[src*=\"error.png\"]"), "css=img[src*=\"error.png\"] is not present after typing nothing in name=field1 and then firing focus and blur events");
	}

    private void typeBlurFocus(String name, String text) {
        selenium.type(name, text);
        selenium.fireEvent(name, "blur");
        selenium.fireEvent(name, "focus");
    }
}
