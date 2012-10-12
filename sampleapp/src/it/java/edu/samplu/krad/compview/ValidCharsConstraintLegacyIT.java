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

import edu.samplu.common.WebDriverLegacyITBase;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidCharsConstraintLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4";
    }

	@Test
	public void testValidCharsConstraintIT() throws Exception {
		fireEvent("field50","focus");
		waitAndTypeByName("field50", "12.333");
		fireEvent("field50", "blur");
		Assert.assertTrue(getAttributeByName("field50","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field50","focus");
		waitAndTypeByName("field50", "123.33");
		fireEvent("field50", "blur");
		Assert.assertTrue(getAttributeByName("field50","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field51","focus");
		waitAndTypeByName("field51", "A");
		fireEvent("field51", "blur");
		Assert.assertTrue(getAttributeByName("field51","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field51","focus");
		waitAndTypeByName("field51", "-123.33");
		fireEvent("field51", "blur");
		Assert.assertTrue(getAttributeByName("field51","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field77","focus");
		waitAndTypeByName("field77", "1.1");
		fireEvent("field77", "blur");
		Assert.assertTrue(getAttributeByName("field77","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field77","focus");
		waitAndTypeByName("field77", "12");
		fireEvent("field77", "blur");
		Assert.assertTrue(getAttributeByName("field77","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field52","focus");
		waitAndTypeByName("field52", "5551112222");
		fireEvent("field52", "blur");
		Assert.assertTrue(getAttributeByName("field52","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field52","focus");
		waitAndTypeByName("field52", "555-111-1111");
		fireEvent("field52", "blur");
		Assert.assertTrue(getAttributeByName("field52","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field53","focus");
		waitAndTypeByName("field53", "1ClassName.java");
		fireEvent("field53", "blur");
		Assert.assertTrue(getAttributeByName("field53","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field53","focus");
		waitAndTypeByName("field53", "ClassName.java");
		fireEvent("field53", "blur");
		Assert.assertTrue(getAttributeByName("field53","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field54","focus");
		waitAndTypeByName("field54", "aaaaa");
		fireEvent("field54", "blur");
		Assert.assertTrue(getAttributeByName("field54","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field54","focus");
		waitAndTypeByName("field54", "aaaaa@kuali.org");
		fireEvent("field54", "blur");
		Assert.assertTrue(getAttributeByName("field54","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field84","focus");
		waitAndTypeByName("field84", "aaaaa");
		fireEvent("field84", "blur");
		Assert.assertTrue(getAttributeByName("field84","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field84","focus");
		waitAndTypeByName("field84", "http://www.kuali.org");
		fireEvent("field84", "blur");
		Assert.assertTrue(getAttributeByName("field84","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field55","focus");
		waitAndTypeByName("field55", "023512");
		fireEvent("field55", "blur");
		Assert.assertTrue(getAttributeByName("field55","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field55","focus");
		waitAndTypeByName("field55", "022812");
		fireEvent("field55", "blur");
		Assert.assertTrue(getAttributeByName("field55","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field75","focus");
		waitAndTypeByName("field75", "02/35/12");
		fireEvent("field75", "blur");
		Assert.assertTrue(getAttributeByName("field75","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field75","focus");
		waitAndTypeByName("field75", "02/28/12");
		fireEvent("field75", "blur");
		Assert.assertTrue(getAttributeByName("field75","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field82","focus");
		waitAndTypeByName("field82", "13:22");
		fireEvent("field82", "blur");
		Assert.assertTrue(getAttributeByName("field82","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field82","focus");
		waitAndTypeByName("field82", "02:33");
		fireEvent("field82", "blur");
		Assert.assertTrue(getAttributeByName("field82","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field83","focus");
		waitAndTypeByName("field83", "25:22");
		fireEvent("field83", "blur");
		Assert.assertTrue(getAttributeByName("field83","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field83","focus");
		waitAndTypeByName("field83", "14:33");
		fireEvent("field83", "blur");
		Assert.assertTrue(getAttributeByName("field83","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field57","focus");
		waitAndTypeByName("field57", "0");
		fireEvent("field57", "blur");
		Assert.assertTrue(getAttributeByName("field57","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field57","focus");
		waitAndTypeByName("field57", "2020");
		fireEvent("field57", "blur");
		Assert.assertTrue(getAttributeByName("field57","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field58","focus");
		waitAndTypeByName("field58", "13");
		fireEvent("field58", "blur");
		Assert.assertTrue(getAttributeByName("field58","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field58","focus");
		waitAndTypeByName("field58", "12");
		fireEvent("field58", "blur");
		Assert.assertTrue(getAttributeByName("field58","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field61","focus");
		waitAndTypeByName("field61", "5555-444");
		fireEvent("field61", "blur");
		Assert.assertTrue(getAttributeByName("field61","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field61","focus");
		waitAndTypeByName("field61", "55555-4444");
		fireEvent("field61", "blur");
		Assert.assertTrue(getAttributeByName("field61","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field62","focus");
		waitAndTypeByName("field62", "aa5bb6_a");
		fireEvent("field62", "blur");
		Assert.assertTrue(getAttributeByName("field62","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field62","focus");
		waitAndTypeByName("field62", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
		fireEvent("field62", "blur");
		Assert.assertTrue(getAttributeByName("field62","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field63","focus");
		waitAndTypeByName("field63", "fff555$");
		fireEvent("field63", "blur");
		Assert.assertTrue(getAttributeByName("field63","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field63","focus");
		waitAndTypeByName("field63", "aa22 _/");
		fireEvent("field63", "blur");
		Assert.assertTrue(getAttributeByName("field63","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field64","focus");
		waitAndTypeByName("field64", "AABB55");
		fireEvent("field64", "blur");
		Assert.assertTrue(getAttributeByName("field64","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field64","focus");
		waitAndTypeByName("field64", "ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z");
		fireEvent("field64", "blur");
		Assert.assertTrue(getAttributeByName("field64","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field76","focus");
		waitAndTypeByName("field76", "AA~BB%");
		fireEvent("field76", "blur");
		Assert.assertTrue(getAttributeByName("field76","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field76","focus");
		waitAndTypeByName("field76", "abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?");
		fireEvent("field76", "blur");
		Assert.assertTrue(getAttributeByName("field76","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field65","focus");
		waitAndTypeByName("field65", "sdfs$#$# dsffs");
		fireEvent("field65", "blur");
		Assert.assertTrue(getAttributeByName("field65","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field65","focus");
		waitAndTypeByName("field65", "sdfs$#$#sffs");
		fireEvent("field65", "blur");
		Assert.assertTrue(getAttributeByName("field65","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field66","focus");
		waitAndTypeByName("field66", "abcABCD");
		fireEvent("field66", "blur");
		Assert.assertTrue(getAttributeByName("field66","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field66","focus");
		waitAndTypeByName("field66", "ABCabc");
		fireEvent("field66", "blur");
		Assert.assertTrue(getAttributeByName("field66","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field67","focus");
		waitAndTypeByName("field67", "(111)B-(222)A");
		fireEvent("field67", "blur");
		Assert.assertTrue(getAttributeByName("field67","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field67","focus");
		waitAndTypeByName("field67", "(12345)-(67890)");
		fireEvent("field67", "blur");
		Assert.assertTrue(getAttributeByName("field67","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field68","focus");
		waitAndTypeByName("field68", "A.66");
		fireEvent("field68", "blur");
		Assert.assertTrue(getAttributeByName("field68","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field68","focus");
		waitAndTypeByName("field68", "a.4");
		fireEvent("field68", "blur");
		Assert.assertTrue(getAttributeByName("field68","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
		fireEvent("field56","focus");
		waitAndTypeByName("field56", "2020-06-02");
		fireEvent("field56", "blur");
		Assert.assertTrue(getAttributeByName("field56","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
		fireEvent("field56","focus");
		waitAndTypeByName("field56", "2020-06-02 03:30:30.22");
		fireEvent("field56", "blur");
		Assert.assertTrue(getAttributeByName("field56","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
	}
}
