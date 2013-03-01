/**
 * Copyright 2005-2012 The Kuali Foundation
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
package edu.samplu.mainmenu.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.internal.seleniumemulation.IsElementPresent;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

public class MultipleLoginLogoutNavIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    @Override
    public String getUserName() {
        return "admin";
    }
    @Test
    public void testMultipleLoginLogout() throws Exception {
        waitAndClickByLinkText("Main Menu");
        waitForPageToLoad();
        assertEquals("Logged in User: admin",getTextByXpath("//div[@id='login-info']/strong[1]"));
        assertEquals(Boolean.FALSE, isElementPresentByXpath("//div[@id='login-info']/strong[2]"));
        waitAndTypeByName("backdoorId", "employee");
        waitAndClickByXpath("//input[@value='Login']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[@id='login-info']/strong[2]");
        assertEquals("  Impersonating User: employee",getTextByXpath("//div[@id='login-info']/strong[2]"));
        waitAndClickByXpath("//input[@name='imageField' and @value='Logout']");
        assertEquals(Boolean.FALSE, isElementPresentByXpath("//div[@id='login-info']/strong[2]"));
        waitAndClickByXpath("//input[@name='imageField' and @value='Logout']");
        passed();
    }
}
