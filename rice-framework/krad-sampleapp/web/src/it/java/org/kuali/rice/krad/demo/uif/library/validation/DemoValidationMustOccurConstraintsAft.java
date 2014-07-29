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
public class DemoValidationMustOccurConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-MustOccurConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-MustOccurConstraintView&methodToCall=start";

    /**
     *   1-2 of the following must occur: (Field 1A) OR (Field 2A and Field 2B)
     */
    private static final String EXAMPLE1_VALIDATION_MESSAGE="  1-2 of the following must occur: (Field 1A) OR (Field 2A and Field 2B)";
    
    /**
     *   The following must occur: (Field 1A) OR (Field 2A and Field 2B)
     */
    private static final String EXAMPLE2_VALIDATION_MESSAGE="  The following must occur: (Field 1A) OR (Field 2A and Field 2B)";
    
    /**
     *   1-2 of the following must occur: (Field 1A or Field 1B) OR (Field 2A and Field 2B)
     */
    private static final String EXAMPLE3_VALIDATION_MESSAGE="  1-2 of the following must occur: (Field 1A or Field 1B) OR (Field 2A and Field 2B)";
    
    /**
     * //input[@name='inputField4' and @class='uif-textControl dependsOn-inputField1 dependsOn-inputField2 dependsOn-inputField3 dirty error']
     */
    private static final String EXAMPLE1_ERROR_XPATH="//input[@name='inputField4' and @class='error']";
    
    /**
     * //input[@name='inputField8' and @class='uif-textControl dependsOn-inputField5 dependsOn-inputField6 dependsOn-inputField7 dirty error']
     */
    private static final String EXAMPLE2_ERROR_XPATH="//input[@name='inputField8' and @class='error']";
    
    /**
     * //input[@name='inputField13' and @class='uif-textControl dependsOn-inputField9 dependsOn-inputField10 dependsOn-inputField11 dependsOn-inputField12 dirty error']
     */
    private static final String EXAMPLE3_ERROR_XPATH="//input[@name='inputField13' and @class='error']";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Must Occur Constraint");
    }

    protected void testValidationMustOccurConstraintsExample1() throws Exception {
        waitAndClickByLinkText("Example 1");

       //Scenario-1
        assertFocusTypeBlurError("inputField4", "a");
        fireMouseOverEventByName("inputField4");
        assertTextPresent(EXAMPLE1_VALIDATION_MESSAGE);
       
        //Scenario-2
        waitAndTypeByName("inputField1","a");
        waitAndTypeByName("inputField2","");
        if(isElementPresentByXpath(EXAMPLE1_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
       
        //Scenario-3
        clearTextByName("inputField1");
        waitAndTypeByName("inputField4","a");
        waitAndTypeByName("inputField2", "a");
        waitAndTypeByName("inputField1","");
        String id = findElement(By.name("inputField4")).getAttribute("id");
        assertTrue(findElement(By.id(id)).getAttribute("class").contains("error"));
        fireEvent("inputField1", "blur");
        fireMouseOverEventByName("inputField4");
        assertTextPresent(EXAMPLE1_VALIDATION_MESSAGE);
       
        //Scenario-4
        waitAndTypeByName("inputField3","a");
        waitAndTypeByName("inputField1","");
        if(isElementPresentByXpath(EXAMPLE1_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
    }
    
    protected void testValidationMustOccurConstraintsExample2() throws Exception {
        waitAndClickByLinkText("Example 2");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField8","a");
        fireMouseOverEventByName("inputField8");
        assertTextPresent(EXAMPLE2_VALIDATION_MESSAGE);
        
        //Scenario-2
        waitAndTypeByName("inputField5","a");
        waitAndTypeByName("inputField6","");
        if(isElementPresentByXpath(EXAMPLE2_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
        
        //Scenario-3
        clearTextByName("inputField5");
        waitAndTypeByName("inputField6","a");
        waitAndTypeByName("inputField8","a");
        waitAndTypeByName("inputField5","");
        String id = findElement(By.name("inputField8")).getAttribute("id");
        assertTrue(findElement(By.id(id)).getAttribute("class").contains("error"));
        fireEvent("inputField5", "blur");
        fireMouseOverEventByName("inputField8");
        assertTextPresent(EXAMPLE2_VALIDATION_MESSAGE);
        
        //Scenario-4
        waitAndTypeByName("inputField7","a");
        waitAndTypeByName("inputField5","");
        if (isElementPresentByXpath(EXAMPLE2_ERROR_XPATH)){
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
        
        //Scenario-5
        waitAndTypeByName("inputField5","a");
        waitAndTypeByName("inputField8","a");
        assertTrue(findElement(By.id(id)).getAttribute("class").contains("error"));
        fireEvent("inputField8", "blur");
        fireMouseOverEventByName("inputField8");
        assertTextPresent(EXAMPLE2_VALIDATION_MESSAGE);
    }

    protected void testValidationMustOccurConstraintsExample3() throws Exception {
        waitAndClickByLinkText("Example 3");
        
        //Scenario-1
        assertFocusTypeBlurError("inputField13","a");
        fireMouseOverEventByName("inputField13");
        assertTextPresent(EXAMPLE3_VALIDATION_MESSAGE);
        
        //Scenario-2
        waitAndTypeByName("inputField9","a");
        waitAndTypeByName("inputField13","");
        if (isElementPresentByXpath(EXAMPLE3_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
        clearTextByName("inputField9");
        
        //Scenario-3
        waitAndTypeByName("inputField10","a");
        waitAndTypeByName("inputField13","");
        if (isElementPresentByXpath(EXAMPLE3_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
        clearTextByName("inputField10");
        
        //Scenario-4
        waitAndTypeByName("inputField11","a");
        waitAndTypeByName("inputField12","a");
        waitAndTypeByName("inputField13","");
        if (isElementPresentByXpath(EXAMPLE3_ERROR_XPATH)) {
            jiraAwareFail("Criteria Not Satisfied " + this.getClass().toString());
        }
    }
    

    @Test
    public void testValidationMustOccurConstraintsBookmark() throws Exception {
        testValidationMustOccurConstraintsExample1();
        testValidationMustOccurConstraintsExample2();
        testValidationMustOccurConstraintsExample3();
        passed();
    }

    @Test
    public void testValidationMustOccurConstraintsNav() throws Exception {
        testValidationMustOccurConstraintsExample1();
        testValidationMustOccurConstraintsExample2();
        testValidationMustOccurConstraintsExample3();
        passed();
    }
}
