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
public class DemoValidationNumericConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-NumericPatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-NumericPatternConstraintView&methodToCall=start";
   
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
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Numeric Constraint");
    }

    protected void testValidationNumericConstraints() throws Exception {
        waitAndClickByLinkText("Default");

        //Scenario-1
        assertFocusTypeBlurError("inputField1","a");
    }
    
    protected void testValidationNumericConstraintsFlags() throws Exception {
        waitAndClickByLinkText("Flags");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField2","a s");
        assertFocusTypeBlurError("inputField3","a#s");
    }
    
    protected void testValidationNumericConstraintsPreconfiguredBeans() throws Exception {
        waitAndClickByLinkText("Preconfigured Bean(s)");
        
        //Scenario-1
        waitAndTypeByName("inputField4","1@2");
        typeTab();
        fireMouseOverEventByName("inputField4");
        assertTextPresent(ERROR_MSG);
     }
    
    @Test
    public void testValidationNumericConstraintsBookmark() throws Exception {
        testValidationNumericConstraints();
        testValidationNumericConstraintsFlags();
        testValidationNumericConstraintsPreconfiguredBeans();
        passed();
    }

    @Test
    public void testValidationNumericConstraintsNav() throws Exception {
        testValidationNumericConstraints();
        testValidationNumericConstraintsFlags();
        testValidationNumericConstraintsPreconfiguredBeans();
        passed();
    }  
}
