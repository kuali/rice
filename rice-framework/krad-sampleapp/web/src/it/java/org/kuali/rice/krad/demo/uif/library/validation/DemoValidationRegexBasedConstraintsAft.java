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
public class DemoValidationRegexBasedConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ConfigurationBasedRegexPatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ConfigurationBasedRegexPatternConstraintView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Regex Based Constraints");
    }

    protected void testValidationRegexBasedConstraintsPhone() throws Exception {
        waitAndClickByLinkText("Phone");

        //Scenario-1
        assertFocusTypeBlurError("inputField1","1234567890");
    }
    
    protected void testValidationRegexBasedConstraintsEmail() throws Exception {
        waitAndClickByLinkText("Email");

        assertEquals("Default value 2,4 for inputField2 not found", "2,4", waitAndGetAttributeByName("inputField2", "value"));

        assertFocusTypeBlurValid("inputField2", "a@kuali.org");
        assertFocusTypeBlurValid("inputField2", "aa@kuali.org");
        assertFocusTypeBlurValid("inputField2", "a.a@kuali.org");
        assertFocusTypeBlurValid("inputField2", "a.aa@kuali.org");
        assertFocusTypeBlurValid("inputField2", "aa.a@kuali.org");
        assertFocusTypeBlurValid("inputField2", "aa.aa@kuali.org");

        assertFocusTypeBlurError("inputField2", "s1!@f.xoh");
    }
    
    protected void testValidationRegexBasedConstraintsUrl() throws Exception {
        waitAndClickByLinkText("Url");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField3","www.google.com");
    }
    
    protected void testValidationRegexBasedConstraintsNowhitespace() throws Exception {
        waitAndClickByLinkText("No whitespace");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField4","aw e");
    }
    
    protected void testValidationRegexBasedConstraints12hTime() throws Exception {
        waitAndClickByLinkText("12h Time");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField5","22:00");
    }
    
    protected void testValidationRegexBasedConstraints24hTime() throws Exception {
        waitAndClickByLinkText("24h Time");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField6","01:00AM");
    }
    
    protected void testValidationRegexBasedConstraintsTimestamp() throws Exception {
        waitAndClickByLinkText("Timestamp");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField7","1234-12-30 23:23:23.23");
    }
    
     protected void testValidationRegexBasedConstraintsYear() throws Exception {
         waitAndClickByLinkText("Year");
        
         //Scenario-1
         assertFocusTypeBlurError("inputField8","1599");
    }
    
     protected void testValidationRegexBasedConstraintsMonth() throws Exception {
         waitAndClickByLinkText("Month");
        
         //Scenario-1
         assertFocusTypeBlurError("inputField9","13");
    }
    
     protected void testValidationRegexBasedConstraintsZipcode() throws Exception {
         waitAndClickByLinkText("Zipcode");
        
         //Scenario-1
         assertFocusTypeBlurError("inputField10","941012");
    }
    
    protected void testValidationRegexBasedConstraintsJavaclassname() throws Exception {
        waitAndClickByLinkText("Java classname");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField11","123");
    }
    
     protected void testValidationRegexBasedConstraintsCustom() throws Exception {
         waitAndClickByLinkText("Custom");
        
         //Scenario-1
         assertFocusTypeBlurError("inputField12","ab.9");
         assertFocusTypeBlurError("inputField13","Demo-hi hello");
    }
    
    @Test
    public void testValidationRegexBasedConstraintsBookmark() throws Exception {
        testValidationRegexBasedConstraintsPhone();
        testValidationRegexBasedConstraintsEmail();
        testValidationRegexBasedConstraintsUrl();
        testValidationRegexBasedConstraintsNowhitespace();
        testValidationRegexBasedConstraints12hTime();
        testValidationRegexBasedConstraints24hTime();
        testValidationRegexBasedConstraintsTimestamp();
        testValidationRegexBasedConstraintsYear();
        testValidationRegexBasedConstraintsMonth();
        testValidationRegexBasedConstraintsZipcode();
        testValidationRegexBasedConstraintsJavaclassname();
        testValidationRegexBasedConstraintsCustom();
        passed();
    }

    @Test
    public void testValidationRegexBasedConstraintsNav() throws Exception {
        testValidationRegexBasedConstraintsPhone();
        testValidationRegexBasedConstraintsEmail();
        testValidationRegexBasedConstraintsUrl();
        testValidationRegexBasedConstraintsNowhitespace();
        testValidationRegexBasedConstraints12hTime();
        testValidationRegexBasedConstraints24hTime();
        testValidationRegexBasedConstraintsTimestamp();
        testValidationRegexBasedConstraintsYear();
        testValidationRegexBasedConstraintsMonth();
        testValidationRegexBasedConstraintsZipcode();
        testValidationRegexBasedConstraintsJavaclassname();
        testValidationRegexBasedConstraintsCustom();
        passed();
    }
}
