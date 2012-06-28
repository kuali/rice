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

package edu.samplu.krad.compview;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import junit.framework.Assert;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class ValidCharsConstraintIT{
    private Selenium selenium;
    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver,
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4");

        // Login
        selenium.open("http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4");
        Assert.assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

	@Test
	public void testValidCharsConstraintIT() throws Exception {
		selenium.focus("name=field50");
		selenium.type("name=field50", "12.333");
		selenium.fireEvent("name=field50", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field50@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field50");
		selenium.type("name=field50", "123.33");
		selenium.fireEvent("name=field50", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field50@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field51");
		selenium.type("name=field51", "A");
		selenium.fireEvent("name=field51", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field51@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field51");
		selenium.type("name=field51", "-123.33");
		selenium.fireEvent("name=field51", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field51@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field77");
		selenium.type("name=field77", "1.1");
		selenium.fireEvent("name=field77", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field77@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field77");
		selenium.type("name=field77", "12");
		selenium.fireEvent("name=field77", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field77@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field52");
		selenium.type("name=field52", "5551112222");
		selenium.fireEvent("name=field52", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field52@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field52");
		selenium.type("name=field52", "555-111-1111");
		selenium.fireEvent("name=field52", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field52@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field53");
		selenium.type("name=field53", "1ClassName.java");
		selenium.fireEvent("name=field53", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field53@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field53");
		selenium.type("name=field53", "ClassName.java");
		selenium.fireEvent("name=field53", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field53@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field54");
		selenium.type("name=field54", "aaaaa");
		selenium.fireEvent("name=field54", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field54@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field54");
		selenium.type("name=field54", "aaaaa@kuali.org");
		selenium.fireEvent("name=field54", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field54@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field84");
		selenium.type("name=field84", "aaaaa");
		selenium.fireEvent("name=field84", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field84@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field84");
		selenium.type("name=field84", "http://www.kuali.org");
		selenium.fireEvent("name=field84", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field84@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field55");
		selenium.type("name=field55", "023512");
		selenium.fireEvent("name=field55", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field55@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field55");
		selenium.type("name=field55", "022812");
		selenium.fireEvent("name=field55", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field55@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field75");
		selenium.type("name=field75", "02/35/12");
		selenium.fireEvent("name=field75", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field75@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field75");
		selenium.type("name=field75", "02/28/12");
		selenium.fireEvent("name=field75", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field75@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field82");
		selenium.type("name=field82", "13:22");
		selenium.fireEvent("name=field82", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field82@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field82");
		selenium.type("name=field82", "02:33");
		selenium.fireEvent("name=field82", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field82@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field83");
		selenium.type("name=field83", "25:22");
		selenium.fireEvent("name=field83", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field83@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field83");
		selenium.type("name=field83", "14:33");
		selenium.fireEvent("name=field83", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field83@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field57");
		selenium.type("name=field57", "0");
		selenium.fireEvent("name=field57", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field57@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field57");
		selenium.type("name=field57", "2020");
		selenium.fireEvent("name=field57", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field57@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field58");
		selenium.type("name=field58", "13");
		selenium.fireEvent("name=field58", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field58@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field58");
		selenium.type("name=field58", "12");
		selenium.fireEvent("name=field58", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field58@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field61");
		selenium.type("name=field61", "5555-444");
		selenium.fireEvent("name=field61", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field61@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field61");
		selenium.type("name=field61", "55555-4444");
		selenium.fireEvent("name=field61", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field61@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field62");
		selenium.type("name=field62", "aa5bb6_a");
		selenium.fireEvent("name=field62", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field62@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field62");
		selenium.type("name=field62", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
		selenium.fireEvent("name=field62", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field62@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field63");
		selenium.type("name=field63", "fff555$");
		selenium.fireEvent("name=field63", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field63@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field63");
		selenium.type("name=field63", "aa22 _/");
		selenium.fireEvent("name=field63", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field63@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field64");
		selenium.type("name=field64", "AABB55");
		selenium.fireEvent("name=field64", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field64@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field64");
		selenium.type("name=field64", "ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z");
		selenium.fireEvent("name=field64", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field64@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field76");
		selenium.type("name=field76", "AA~BB%");
		selenium.fireEvent("name=field76", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field76@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field76");
		selenium.type("name=field76", "abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?");
		selenium.fireEvent("name=field76", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field76@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field65");
		selenium.type("name=field65", "sdfs$#$# dsffs");
		selenium.fireEvent("name=field65", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field65@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field65");
		selenium.type("name=field65", "sdfs$#$#sffs");
		selenium.fireEvent("name=field65", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field65@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field66");
		selenium.type("name=field66", "abcABCD");
		selenium.fireEvent("name=field66", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field66@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field66");
		selenium.type("name=field66", "ABCabc");
		selenium.fireEvent("name=field66", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field66@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field67");
		selenium.type("name=field67", "(111)B-(222)A");
		selenium.fireEvent("name=field67", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field67@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field67");
		selenium.type("name=field67", "(12345)-(67890)");
		selenium.fireEvent("name=field67", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field67@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field68");
		selenium.type("name=field68", "A.66");
		selenium.fireEvent("name=field68", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field68@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field68");
		selenium.type("name=field68", "a.4");
		selenium.fireEvent("name=field68", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field68@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		selenium.focus("name=field56");
		selenium.type("name=field56", "2020-06-02");
		selenium.fireEvent("name=field56", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field56@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		selenium.focus("name=field56");
		selenium.type("name=field56", "2020-06-02 03:30:30.22");
		selenium.fireEvent("name=field56", "blur");
		Assert.assertTrue(selenium.getAttribute("name=field56@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
