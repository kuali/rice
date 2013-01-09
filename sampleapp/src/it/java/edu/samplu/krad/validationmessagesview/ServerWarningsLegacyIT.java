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

import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerWarningsLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

    @Test
    public void testServerWarningsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Warning Messages')]");
        waitForPageToLoad();
        Thread.sleep(3000);
        assertTrue( "div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] not visible",
                isVisible("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]"));
        assertTrue("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem not present",
                isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-warningMessageItem"));
        assertTrue("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] not visible", isVisible(
                "div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
        assertTrue("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem not present",
                isElementPresent("div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-warningMessageItem"));
        assertTrue("div[data-role=\"InputField\"] img[alt=\"Warning\"] not present", isElementPresent(
                "div[data-role=\"InputField\"] img[alt=\"Warning\"]"));
        fireMouseOverEvent(By.xpath("//a[contains(.,'Field 1')]"));
        assertTrue(".uif-warningHighlight no present when //a[contains(.,'Field 1')] is moused over",
                isElementPresent(".uif-warningHighlight"));
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        waitForElementVisible(".jquerybubblepopup-innerHtml", " after click on //a[contains(.,'Field 1')]");

        assertTrue(".jquerybubblepopup-innerHtml > .uif-serverMessageItems not visible", isVisible(
                ".jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
        assertTrue(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible",
                isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));
        waitAndTypeByName("field1", "");
        fireEvent("field1","blur");
        fireMouseOverEventByName("field1");
     //   fireEvent("field1","hover");
        waitForElementVisible(".jquerybubblepopup-innerHtml", " not visible after typing nothing in name=field1 then firing blur and focus events");

        assertTrue(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events",
                isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));

        waitForElementVisible(".jquerybubblepopup-innerHtml> .uif-clientMessageItems", " not visible after typing nothing in name=field1 then firing blur and focus events");

        assertTrue(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field not visible after typing nothing in name=field1 then firing blur and focus events",
                isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));

        waitAndTypeByName("field1", "b");
        fireEvent("field1","blur");
        fireMouseOverEventByName("field1");
      //  fireEvent("field1","hover");
        waitForElementVisible(".jquerybubblepopup-innerHtml> .uif-serverMessageItems","");

        assertTrue(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field not visible after typing b in name=field1 then firing blur and focus events",
                isVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-warningMessageItem-field"));
        assertTrue(".jquerybubblepopup-innerHtml > .uif-clientMessageItems", !isElementPresent(
                ".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));

//        waitAndTypeByName("field1", "");
        clearTextByName("field1");
   //     fireEvent("field1", "focus");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
//        fireEvent("field1","hover");
        assertTrue(".uif-hasError is not present after typing nothing in name=field1 and then firing focus and blur events",
                isElementPresent(".uif-hasError"));
        assertTrue( "img[src*=\"error.png\"] is not present after typing nothing in name=field1 and then firing focus and blur events",
                isElementPresent("img[src*=\"error.png\"]"));
        passed();
    }

    private void typeBlurFocus(String name, String text) throws InterruptedException{
        waitAndTypeByName(name, text);
        fireEvent(name, "blur");
        fireEvent(name, "focus");
    }
}