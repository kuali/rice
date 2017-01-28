/**
 * Copyright 2005-2017 The Kuali Foundation
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
public class LibraryValidationIntegerConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-IntegerPatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-IntegerPatternConstraintView&methodToCall=start";

    /**
     *  Can only be alpha characters, whitespace, newlines, periods, parentheses, forward slashes, double quotes, apostrophes, colons, semi-colons, question marks, exclaimation marks, dashes
     */
    private static final String ERROR_MSG= "  Can only be numeric characters, whitespace, newlines, periods, parentheses, forward slashes, dashes, plus signs, equals signs, *";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Integer Constraint");
    }

    protected void testValidationNumericConstraints() throws Exception {
        waitAndClickByLinkText("Default");

        //Scenario-1
        assertFocusTypeBlurError("inputField1","1.3");
    }
    
    protected void testValidationNumericConstraintsFlags() throws Exception {
        waitAndClickByLinkText("Flags");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField2","1.2");
        assertFocusTypeBlurError("inputField3","-0");
        assertFocusTypeBlurError("inputField4","0");
//        assertElementPresentByXpath("//input[@name='inputField2' and @class='uif-textControl validChar-inputField20 dirty error']");
//        assertElementPresentByXpath("//input[@name='inputField3' and @class='uif-textControl validChar-inputField30 dirty error']");
//        waitAndTypeByName("inputField2","");
//        assertElementPresentByXpath("//input[@name='inputField4' and @class='uif-textControl validChar-inputField40 dirty error']");
    }
    
    @Test
    public void testValidationNumericConstraintsBookmark() throws Exception {
        testValidationNumericConstraints();
        testValidationNumericConstraintsFlags();
        passed();
    }

    @Test
    public void testValidationNumericConstraintsNav() throws Exception {
        testValidationNumericConstraints();
        testValidationNumericConstraintsFlags();
        passed();
    }
}
