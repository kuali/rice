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
package edu.sampleu.krad.validationmessagesview;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ClientErrorsAftBase extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start"
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }


    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath(VALIDATION_FRAMEWORK_DEMO_XPATH);
        switchToWindow(KUALI_VIEW_WINDOW_TITLE);
    }

    protected void testClientErrorsNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testClientErrors();
        passed();
    }

    protected void testClientErrorsBookmark(JiraAwareFailable failable) throws Exception {
        testClientErrors();
        passed();
    }

    protected void testClientErrors() throws Exception {
        fireEvent("field1", "focus");
        fireEvent("field1", "blur");
        Thread.sleep(3000);
        fireMouseOverEventByName("field1");
        waitIsVisibleByXpath("//div[@class='popover top in uif-tooltip-error-cs']");

        waitAndTypeByName("field1", "a");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        assertFalse(isVisibleByXpath(
                "//div[@class='popover top in uif-tooltip-error-cs']"));

        fireEvent("field2", "focus");
        fireEvent("field2", "blur");
        fireMouseOverEventByName("field2");
        Thread.sleep(500);
        //        SeleneseTestBase.assertEquals("true", waitAndGetAttributeByName("field2", "aria-invalid"));
        SeleneseTestBase.assertEquals("true", waitAndGetAttributeByName("field2", "aria-required"));

        fireEvent("field2", "focus");
        waitAndTypeByName("field2", "a");
        fireEvent("field2", "blur");
        Thread.sleep(500);
        assertFalse(isElementPresentByXpath("//*[@name='field2' and @aria-invalid]"));

        fireEvent("field3", "focus");
        fireEvent("field3", "blur");
        fireMouseOverEventByName("field3");

        fireEvent("field3", "focus");
        selectByName("field3", "Option 1");
        fireEvent("field3", "blur");
        Thread.sleep(500);
        assertFalse(isElementPresentByXpath("//*[@name='field3' and @aria-invalid]"));

        fireEvent("field114", "focus");
        fireMouseOverEventByName("field114");
        driver.findElement(By.name("field114")).findElements(By.tagName("option")).get(0).click();
        fireEvent("field114", "blur");
        Thread.sleep(500);
        SeleneseTestBase.assertEquals("true", waitAndGetAttributeByName("field114", "aria-invalid"));

        fireEvent("field114", "focus");
        selectByName("field114", "Option 1");
        fireEvent("field114", "blur");
        Thread.sleep(500);
        assertFalse(isElementPresentByXpath("//*[@name='field114' and @aria-invalid]"));

        fireEvent("field117", "3", "focus");
        checkByXpath("//*[@name='field117' and @value='3']");
        fireEvent("field117", "3", "blur");

        for (int second = 0;; second++) {
            if (second >= waitSeconds) {
                SeleneseTestBase.fail(TIMEOUT_MESSAGE);
            }

            try {
                if (!isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {}

            Thread.sleep(1000);
        }

        assertFalse(isElementPresentByXpath("//*[@name='field117' and @value='3' and @aria-invalid]"));
        assertTrue(waitAndGetAttributeByXpath("//*[@name='field117' and @value='3']", "class").matches(REGEX_VALID));
        assertFalse(isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']"));

        fireEvent("bField1", "focus");
        uncheckByName("bField1");
        fireEvent("bField1", "blur");
        fireMouseOverEventByName("bField1");
        Thread.sleep(500);
        SeleneseTestBase.assertEquals("true", waitAndGetAttributeByName("bField1", "aria-invalid"));
        assertAttributeClassRegexMatches("bField1", REGEX_ERROR);
        assertTrue(isTextPresent("Required"));

        fireEvent("bField1", "focus");
        checkByName("bField1");
        fireEvent("bField1", "blur");
        Thread.sleep(500);
        assertFalse(isElementPresentByXpath("//*[@name='bField1' and @aria-invalid]"));
        assertAttributeClassRegexMatches("bField1", REGEX_VALID);
        assertFalse(isElementPresentByXpath("//input[@name='bField1' and following-sibling::img[@alt='Error']]"));

        fireEvent("field115", "3", "focus");
        uncheckByXpath("//*[@name='field115' and @value='3']");
        uncheckByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");
        fireMouseOverEventByName("field115");

        fireEvent("field115", "3", "focus");
        checkByXpath("//*[@name='field115' and @value='3']");
        checkByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");

        for (int second = 0;; second++) {
            if (second >= waitSeconds) {
                SeleneseTestBase.fail(TIMEOUT_MESSAGE);
            }

            try {
                if (!isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {}

            Thread.sleep(1000);
        }

        assertFalse(isElementPresentByXpath("//*[@name='field115' and @value='3' and @aria-invalid]"));
        assertFalse(isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']"));
    }
}
