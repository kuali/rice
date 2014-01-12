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
public class DemoValidationCaseConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CaseConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CaseConstraintView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Case Constraint");
    }

    protected void testValidationCaseConstraintsBasic() throws Exception {
        waitAndClickByLinkText("Basic Examples");

        //Scenario-1
        waitAndClickByXpath("//input[@type='radio' and @value='case1']");
        waitAndTypeByName("inputField1","");
        waitAndTypeByName("inputField2","");
        isVisible(By.xpath("//li[@class='uif-errorMessageItem-field']"));
      
        //Scenario-2
        waitAndClickByXpath("//input[@type='radio' and @value='case2']");
        waitAndTypeByName("inputField1","a_+");
        waitAndTypeByName("inputField2","");
        fireMouseOverEventByName("inputField1");
        isVisible(By.xpath("//li[@class='uif-errorMessageItem-field']"));
       
        //Scenario-3
        waitAndClickByXpath("//input[@type='radio' and @value='case3']");
        waitAndTypeByName("inputField2","567823");
        waitAndTypeByName("inputField1","");
        fireMouseOverEventByName("inputField2");
        isVisible(By.xpath("//li[@class='uif-errorMessageItem-field']"));
       
        //Scenario-4
        waitAndClickByXpath("//input[@type='radio' and @value='case4']");
        waitAndTypeByName("inputField3","a");
        waitAndTypeByName("inputField4","");
        waitAndTypeByName("inputField3","");
        fireMouseOverEventByName("inputField4");
        isVisible(By.xpath("//li[@class='uif-errorMessageItem-field']"));
    }
    
    protected void testValidationCaseConstraintsNested() throws Exception {
        waitAndClickByLinkText("Nested Example");
       
        //Scenario-1
        waitAndTypeByName("inputField5","a");
        waitAndTypeByName("inputField6","");
        String id = waitAndTypeByName("inputField7","").getAttribute("id");
        fireEvent("inputField7", "blur");
        fireMouseOverEventByName("inputField7");
        Thread.sleep(1000);
        assertTrue(findElement(By.id(id)).getAttribute("class").contains("error"));
    }
    
    @Test
    public void testValidationCaseConstraintsBookmark() throws Exception {
        testValidationCaseConstraintsBasic();
        testValidationCaseConstraintsNested();
        passed();
    }

    @Test
    public void testValidationCaseConstraintsNav() throws Exception {
        testValidationCaseConstraintsBasic();
        testValidationCaseConstraintsNested();
        passed();
    }
}
