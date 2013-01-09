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

import edu.samplu.common.UpgradedSeleniumITBase;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerErrorsIT extends UpgradedSeleniumITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

    @Test
    public void testServerErrorsIT() throws Exception {
        waitAndClick("//button[contains(.,'Get Error Messages')]");
        waitForPageToLoad();
//        Assert.assertTrue(isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]")); // bugged isVisible? you can see it on the screen...
        Thread.sleep(1000);
        assertElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-errorMessageItem");
        waitIsVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]");
        assertElementPresent("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-errorMessageItem");
        assertElementPresent("css=div[data-role=\"InputField\"] img[alt=\"Error\"]");
        waitAndClick("//a[contains(.,'\"Section 1 Title\"')]");
        mouseOver("//a[contains(.,'Field 1')]");
        assertElementPresent("css=.uif-errorHighlight");
        waitAndClick("//a[contains(.,'Field 1')]");
        waitIsVisible("css=.jquerybubblepopup-innerHtml");

        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems");
        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitAndType("name=field1", "");
        fireEvent("name=field1", "blur");
        fireEvent("name=field1", "focus");
        waitIsVisible("css=.jquerybubblepopup-innerHtml");

        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems");
        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field");
        keyDown("name=field1", "t");
        keyPress("name=field1", "t");
        keyUp("name=field1", "t");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        waitIsVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        Assert.assertFalse(isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
    }
}
