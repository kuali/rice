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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoValidationAlphaConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AlphaPatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AlphaPatternConstraintView&methodToCall=start";

    /**
     *  Can only be alpha characters, whitespace, newlines, periods, parentheses, forward slashes, double quotes, apostrophes, colons, semi-colons, question marks, exclaimation marks, dashes
     */
    private static final String ERROR_MSG= "  Can only be alpha characters, whitespace, newlines, periods, parentheses, forward slashes, double quotes, apostrophes, colons, semi-colons, question marks, exclaimation marks, dashes";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Alpha Constraint");
    }

    protected void testValidationAlphaConstraints() throws Exception {
        //Scenario-1
        assertFocusTypeTabError("inputField1", "12");
    }
    
    protected void testValidationAlphaConstraintsFlags() throws Exception {
        waitAndClickByLinkText("Flags");
        
        //Scenario-2
        assertFocusTypeTabError("inputField2", "1 2");
        assertFocusTypeTabError("inputField3", "1,2");
    }
    
    protected void testValidationAlphaConstraintsPreconfiguredBeans() throws Exception {
        waitAndClickByLinkText("Preconfigured Bean(s)");
        
        //Scenario-3
        waitAndTypeByName("inputField4","as=-0");
        typeTab();
        fireMouseOverEventByName("inputField4");
        assertTextPresent(ERROR_MSG);
     }
    
    @Test
    public void testValidationAlphaConstraintsBookmark() throws Exception {
        testValidationAlphaConstraints();
        testValidationAlphaConstraintsFlags();
        testValidationAlphaConstraintsPreconfiguredBeans();
        passed();
    }

    @Test
    public void testValidationAlphaConstraintsNav() throws Exception {
        testValidationAlphaConstraints();
        testValidationAlphaConstraintsFlags();
        testValidationAlphaConstraintsPreconfiguredBeans();
        passed();
    }  
}
