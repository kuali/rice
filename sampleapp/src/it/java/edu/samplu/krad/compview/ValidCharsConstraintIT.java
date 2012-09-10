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

import edu.samplu.common.UpgradedSeleniumITBase;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidCharsConstraintIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4";
    }

	@Test
	public void testValidCharsConstraintIT() throws Exception {
		focus("name=field50");
		waitAndType("name=field50", "12.333");
		fireEvent("name=field50", "blur");
		Assert.assertTrue(getAttribute("name=field50@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field50");
		waitAndType("name=field50", "123.33");
		fireEvent("name=field50", "blur");
		Assert.assertTrue(getAttribute("name=field50@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field51");
		waitAndType("name=field51", "A");
		fireEvent("name=field51", "blur");
		Assert.assertTrue(getAttribute("name=field51@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field51");
		waitAndType("name=field51", "-123.33");
		fireEvent("name=field51", "blur");
		Assert.assertTrue(getAttribute("name=field51@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field77");
		waitAndType("name=field77", "1.1");
		fireEvent("name=field77", "blur");
		Assert.assertTrue(getAttribute("name=field77@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field77");
		waitAndType("name=field77", "12");
		fireEvent("name=field77", "blur");
		Assert.assertTrue(getAttribute("name=field77@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field52");
		waitAndType("name=field52", "5551112222");
		fireEvent("name=field52", "blur");
		Assert.assertTrue(getAttribute("name=field52@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field52");
		waitAndType("name=field52", "555-111-1111");
		fireEvent("name=field52", "blur");
		Assert.assertTrue(getAttribute("name=field52@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field53");
		waitAndType("name=field53", "1ClassName.java");
		fireEvent("name=field53", "blur");
		Assert.assertTrue(getAttribute("name=field53@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field53");
		waitAndType("name=field53", "ClassName.java");
		fireEvent("name=field53", "blur");
		Assert.assertTrue(getAttribute("name=field53@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field54");
		waitAndType("name=field54", "aaaaa");
		fireEvent("name=field54", "blur");
		Assert.assertTrue(getAttribute("name=field54@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field54");
		waitAndType("name=field54", "aaaaa@kuali.org");
		fireEvent("name=field54", "blur");
		Assert.assertTrue(getAttribute("name=field54@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field84");
		waitAndType("name=field84", "aaaaa");
		fireEvent("name=field84", "blur");
		Assert.assertTrue(getAttribute("name=field84@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field84");
		waitAndType("name=field84", "http://www.kuali.org");
		fireEvent("name=field84", "blur");
		Assert.assertTrue(getAttribute("name=field84@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field55");
		waitAndType("name=field55", "023512");
		fireEvent("name=field55", "blur");
		Assert.assertTrue(getAttribute("name=field55@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field55");
		waitAndType("name=field55", "022812");
		fireEvent("name=field55", "blur");
		Assert.assertTrue(getAttribute("name=field55@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field75");
		waitAndType("name=field75", "02/35/12");
		fireEvent("name=field75", "blur");
		Assert.assertTrue(getAttribute("name=field75@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field75");
		waitAndType("name=field75", "02/28/12");
		fireEvent("name=field75", "blur");
		Assert.assertTrue(getAttribute("name=field75@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field82");
		waitAndType("name=field82", "13:22");
		fireEvent("name=field82", "blur");
		Assert.assertTrue(getAttribute("name=field82@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field82");
		waitAndType("name=field82", "02:33");
		fireEvent("name=field82", "blur");
		Assert.assertTrue(getAttribute("name=field82@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field83");
		waitAndType("name=field83", "25:22");
		fireEvent("name=field83", "blur");
		Assert.assertTrue(getAttribute("name=field83@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field83");
		waitAndType("name=field83", "14:33");
		fireEvent("name=field83", "blur");
		Assert.assertTrue(getAttribute("name=field83@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field57");
		waitAndType("name=field57", "0");
		fireEvent("name=field57", "blur");
		Assert.assertTrue(getAttribute("name=field57@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field57");
		waitAndType("name=field57", "2020");
		fireEvent("name=field57", "blur");
		Assert.assertTrue(getAttribute("name=field57@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field58");
		waitAndType("name=field58", "13");
		fireEvent("name=field58", "blur");
		Assert.assertTrue(getAttribute("name=field58@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field58");
		waitAndType("name=field58", "12");
		fireEvent("name=field58", "blur");
		Assert.assertTrue(getAttribute("name=field58@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field61");
		waitAndType("name=field61", "5555-444");
		fireEvent("name=field61", "blur");
		Assert.assertTrue(getAttribute("name=field61@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field61");
		waitAndType("name=field61", "55555-4444");
		fireEvent("name=field61", "blur");
		Assert.assertTrue(getAttribute("name=field61@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field62");
		waitAndType("name=field62", "aa5bb6_a");
		fireEvent("name=field62", "blur");
		Assert.assertTrue(getAttribute("name=field62@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field62");
		waitAndType("name=field62", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
		fireEvent("name=field62", "blur");
		Assert.assertTrue(getAttribute("name=field62@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field63");
		waitAndType("name=field63", "fff555$");
		fireEvent("name=field63", "blur");
		Assert.assertTrue(getAttribute("name=field63@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field63");
		waitAndType("name=field63", "aa22 _/");
		fireEvent("name=field63", "blur");
		Assert.assertTrue(getAttribute("name=field63@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field64");
		waitAndType("name=field64", "AABB55");
		fireEvent("name=field64", "blur");
		Assert.assertTrue(getAttribute("name=field64@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field64");
		waitAndType("name=field64", "ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z");
		fireEvent("name=field64", "blur");
		Assert.assertTrue(getAttribute("name=field64@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field76");
		waitAndType("name=field76", "AA~BB%");
		fireEvent("name=field76", "blur");
		Assert.assertTrue(getAttribute("name=field76@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field76");
		waitAndType("name=field76", "abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?");
		fireEvent("name=field76", "blur");
		Assert.assertTrue(getAttribute("name=field76@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field65");
		waitAndType("name=field65", "sdfs$#$# dsffs");
		fireEvent("name=field65", "blur");
		Assert.assertTrue(getAttribute("name=field65@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field65");
		waitAndType("name=field65", "sdfs$#$#sffs");
		fireEvent("name=field65", "blur");
		Assert.assertTrue(getAttribute("name=field65@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field66");
		waitAndType("name=field66", "abcABCD");
		fireEvent("name=field66", "blur");
		Assert.assertTrue(getAttribute("name=field66@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field66");
		waitAndType("name=field66", "ABCabc");
		fireEvent("name=field66", "blur");
		Assert.assertTrue(getAttribute("name=field66@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field67");
		waitAndType("name=field67", "(111)B-(222)A");
		fireEvent("name=field67", "blur");
		Assert.assertTrue(getAttribute("name=field67@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field67");
		waitAndType("name=field67", "(12345)-(67890)");
		fireEvent("name=field67", "blur");
		Assert.assertTrue(getAttribute("name=field67@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field68");
		waitAndType("name=field68", "A.66");
		fireEvent("name=field68", "blur");
		Assert.assertTrue(getAttribute("name=field68@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field68");
		waitAndType("name=field68", "a.4");
		fireEvent("name=field68", "blur");
		Assert.assertTrue(getAttribute("name=field68@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		focus("name=field56");
		waitAndType("name=field56", "2020-06-02");
		fireEvent("name=field56", "blur");
		Assert.assertTrue(getAttribute("name=field56@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		focus("name=field56");
		waitAndType("name=field56", "2020-06-02 03:30:30.22");
		fireEvent("name=field56", "blur");
		Assert.assertTrue(getAttribute("name=field56@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
	}
}
