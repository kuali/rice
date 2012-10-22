
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

import junit.framework.Assert;

import org.junit.Test;
import org.openqa.selenium.By;

import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerErrorsLegacyIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

    @Test
    public void testServerErrorsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Error Messages')]");
        waitForPageToLoad();
//        Assert.assertTrue(isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]")); // bugged isVisible? you can see it on the screen...
        Thread.sleep(5000);
        assertElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-errorMessageItem");
        waitIsVisibleByXpath("//div[@data-headerfor='Demo-ValidationLayout-Section1']");
        assertElementPresentByXpath("//*[@data-messageitemfor='Demo-ValidationLayout-Section1' and @class='uif-errorMessageItem']");
        assertElementPresent("div[data-role=\"InputField\"] img[alt=\"Error\"]");
        assertElementPresentByXpath("//a[contains(.,'Section 1 Title')]");
       // waitAndClickByXpath("//a[contains(.,'Section 1 Title')]");
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        assertElementPresent(".uif-errorMessageItem-field");
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        Thread.sleep(2000);
        waitIsVisible(".jquerybubblepopup-innerHtml");

        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        waitIsVisible(".jquerybubblepopup-innerHtml");

        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "t");
    //    keyDown(By.name("field1"), Keys('t'));
    //    keyPress("name=field1", "t");
    //    keyUp("field1", "t");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        Assert.assertFalse(isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
    }
}
