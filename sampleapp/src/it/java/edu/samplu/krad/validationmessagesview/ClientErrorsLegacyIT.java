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

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClientErrorsLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";
    }

    @Test
    public void testClientErrors() throws Exception {
        fireEvent("field1", "focus");
        waitAndTypeByName("field1","");
        fireEvent("field1", "blur");
        Assert.assertEquals("true", getAttributeByName("field1","aria-invalid"));
        Assert.assertTrue(getAttributeByName("field1","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//textarea[@name='field1']/../../img[@alt='Error']"));
        fireEvent("field1", "focus");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (isVisible(".jquerybubblepopup-innerHtml")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        waitAndTypeByName("field1", "a");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!isVisible(".jquerybubblepopup-innerHtml")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(isVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        fireEvent("field1", "blur");
        Assert.assertFalse(isElementPresentByXpath("//*[@name='field1' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByName("field1","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath("//textarea[@name='field1']/../img[@alt='Error']"));
        fireEvent("field2", "focus");
        waitAndTypeByName("field2", "");
        fireEvent("field2", "blur");
        Assert.assertEquals("true", getAttributeByName("field2","aria-invalid"));
        Assert.assertTrue(getAttributeByName("field2","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//input[@name='field2']/../img[@alt='Error']"));
        fireEvent("field2", "focus");
        waitAndTypeByName("field2", "a");
        fireEvent("field2", "blur");
        Assert.assertFalse(isElementPresentByXpath("//*[@name='field2' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByName("field2","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath("//textarea[@name='field2']/../img[@alt='Error']"));
        fireEvent("field3", "focus");
        selectByName("field3", "");
        fireEvent("field3", "blur");
        Assert.assertEquals("true", getAttributeByName("field3","aria-invalid"));
        Assert.assertTrue(getAttributeByName("field3","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//select[@name='field3']/../img[@alt='Error']"));
        fireEvent("field3", "focus");
        selectByName("field3", "Option 1");
        fireEvent("field3", "blur");
        Assert.assertFalse(isElementPresentByXpath("//*[@name='field3' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByName("field3","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath("//select[@name='field3']/../img[@alt='Error']"));
        fireEvent("field114", "focus");
   //    removeAllSelectionsByName("field114");
        driver.findElement(By.name("field114")).findElements(By.tagName("option")).get(0).click();
        fireEvent("field114", "blur");
        Assert.assertEquals("true", getAttributeByName("field114","aria-invalid"));
        Assert.assertTrue(getAttributeByName("field114","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//select[@name='field114']/../img[@alt='Error']"));
        fireEvent("field114", "focus");
        selectByName("field114", "Option 1");
        fireEvent("field114", "blur");
        Assert.assertFalse(isElementPresentByXpath("//*[@name='field114' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByName("field114","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath("//select[@name='field114']/../img[@alt='Error']"));
        fireEvent("field117", "3", "focus");
        uncheckByXpath("//*[@name='field117' and @value='3']");
        fireEvent("field117", "blur");
        for (int second = 0; ; second++) {
            if (second >= 10) {
                Assert.fail("timeout");
            }
            try {
                if (isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertEquals("true", getAttributeByXpath("//*[@name='field117' and @value='1']","aria-invalid"));
        Assert.assertTrue(getAttributeByXpath("//*[@name='field117' and @value='1']","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']"));
        fireEvent("field117", "3", "focus");
        checkByXpath("//*[@name='field117' and @value='3']");
        fireEvent("field117", "3", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(isElementPresentByXpath("//*[@name='field117' and @value='3' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByXpath("//*[@name='field117' and @value='3']","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath("//input[@name='field117']/../../../img[@alt='Error']"));
        fireEvent("bField1", "focus");
        uncheckByName("bField1");
        fireEvent("bField1", "blur");
        Assert.assertEquals("true", getAttributeByName("bField1","aria-invalid"));
        Assert.assertTrue(getAttributeByName("bField1","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath(
                "//input[@name='bField1' and following-sibling::img[@alt='Error']]"));
        fireEvent("bField1", "focus");
        checkByName("bField1");
        fireEvent("bField1", "blur");
        Assert.assertFalse(isElementPresentByXpath("//*[@name='bField1' and @aria-invalid]"));
        Assert.assertTrue(getAttributeByName("bField1","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(isElementPresentByXpath(
                "//input[@name='bField1' and following-sibling::img[@alt='Error']]"));
        fireEvent("field115", "3", "focus");
        uncheckByXpath("//*[@name='field115' and @value='3']");
        uncheckByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertEquals("true", getAttributeByXpath("//*[@name='field115' and @value='1']","aria-invalid"));
        Assert.assertTrue(getAttributeByXpath("//*[@name='field115' and @value='1']","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']"));
        fireEvent("field115","3", "focus");
        checkByXpath("//*[@name='field115' and @value='3']");
        checkByXpath("//*[@name='field115' and @value='4']");
        fireEvent("field115", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(isElementPresentByXpath("//*[@name='field115' and @value='3' and @aria-invalid]"));
        Assert.assertFalse(isElementPresentByXpath("//input[@name='field115']/../../../img[@alt='Error']"));
    }
}
