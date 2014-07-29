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
package org.kuali.rice.krad.demo.uif.library.validation;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoValidationAnyCharacterConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AnyCharacterPatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AnyCharacterPatternConstraintView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Any Character Constraint");
    }

    protected void testValidationAnyCharacterConstraintsBasic() throws Exception {
        //Scenario-1
        waitAndTypeByName("inputField1","a x u");
        typeTab();
        fireMouseOverEventByName("inputField1");
        assertElementPresentByXpath("//input[@name='inputField1' and @class='form-control input-sm uif-textControl validChar-inputField10 dirty error']");
    }
    
    protected void testValidationAnyCharacterConstraintsAllowWhitespace() throws Exception {
        waitAndClickByLinkText("Allow Whitespace");
        waitAndTypeByName("inputField2","a x u");
        typeTab();
        fireMouseOverEventByName("inputField2");
        if (isElementPresentByXpath("//input[@name='inputField2' and @class='uif-textControl validChar-inputField20 dirty error']")) {
            fail("Criteria not satisfied.");
        }
    }
    
    @Test
    public void testValidationAnyCharacterConstraintsBookmark() throws Exception {
        testValidationAnyCharacterConstraintsBasic();
        testValidationAnyCharacterConstraintsAllowWhitespace();
        passed();
    }

    @Test
    public void testValidationAnyCharacterConstraintsNav() throws Exception {
        testValidationAnyCharacterConstraintsBasic();
        testValidationAnyCharacterConstraintsAllowWhitespace();
        passed();
    }
}
