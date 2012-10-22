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
import edu.samplu.common.WebDriverLegacyITBase;

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerInfoLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

    @Test
    public void testServerInfoIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Info Messages')]");
        waitIsVisibleByXpath("//div[@data-messagesfor='Demo-ValidationLayout-SectionsPage']");
        //Thread.sleep(3000);
        Assert.assertTrue(isVisibleByXpath("//div[@data-messagesfor='Demo-ValidationLayout-SectionsPage']"));
        Assert.assertTrue(isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-infoMessageItem"));
        Assert.assertTrue(isVisible("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
        Assert.assertTrue(isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-infoMessageItem"));
        Assert.assertTrue(isElementPresentByXpath("//div[@data-role='InputField']//img[@alt='Information']"));
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        Assert.assertTrue(isElementPresent(".uif-infoHighlight"));
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isVisible(".jquerybubblepopup-innerHtml")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
        Assert.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isVisible(".jquerybubblepopup-innerHtml")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        waitAndTypeByName("field1", "b");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (!isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        fireEvent("field1", "blur");
        Thread.sleep(3000);
        Assert.assertTrue(!isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-infoMessageItem-field"));
        Assert.assertFalse(isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        fireEvent("field1", "focus");
        clearTextByName("field1");
        fireEvent("field1", "blur");
        Assert.assertTrue(isElementPresent("div.uif-hasError"));
        Assert.assertTrue(isElementPresent("img[src*=\"error.png\"]"));
    }
}