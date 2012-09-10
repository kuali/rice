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
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerInfoIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

	@Test
	public void testServerInfoIT() throws Exception {
		waitAndClick("//button[contains(.,'Get Info Messages')]");
		waitForPageToLoad();
        ITUtil.waitForElementVisible(selenium, "css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]");
		Assert.assertTrue(isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]"));
		Assert.assertTrue(isElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-infoMessageItem"));
		Assert.assertTrue(isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
		Assert.assertTrue(isElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-infoMessageItem"));
		Assert.assertTrue(isElementPresent("css=div[data-role=\"InputField\"] img[alt=\"Information\"]"));
		mouseOver("//a[contains(.,'Field 1')]");
		Assert.assertTrue(isElementPresent("css=.uif-infoHighlight"));
		waitAndClick("//a[contains(.,'Field 1')]");
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (isVisible("css=.jquerybubblepopup-innerHtml")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		Assert.assertTrue(isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
		Assert.assertTrue(isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
		waitAndType("name=field1", "");
		fireEvent("name=field1", "blur");
		fireEvent("name=field1", "focus");
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (isVisible("css=.jquerybubblepopup-innerHtml")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		Assert.assertTrue(isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
		for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (isVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		Assert.assertTrue(isVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
		waitAndType("name=field1", "b");
        fireEvent("name=field1", "blur");
        fireEvent("name=field1", "focus");
        for (int second = 0;; second++) {
			if (second >= 60) Assert.fail("timeout");
			try { if (!isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
        fireEvent("name=field1", "blur");

		Assert.assertTrue(!isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
		Assert.assertFalse(isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
		waitAndType("name=field1", "");
		fireEvent("name=field1", "focus");
		fireEvent("name=field1", "blur");
		Assert.assertTrue(isElementPresent("css=.uif-hasError"));
		Assert.assertTrue(isElementPresent("css=img[src*=\"error.png\"]"));
	}
}
