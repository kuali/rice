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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StateBasedValidationClientTestsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Demo-StateBasedValidation-Client&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/uicomponents?viewId=Demo-StateBasedValidation-Client&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("State-based Validation (Client test)");
        switchToWindow("Kuali :: State-based Validation client-side Test View");
    }
    
    private void testStateBasedValidationClientTests() throws Exception{
        //State 1
        waitAndClickButtonByText("Check Form");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasWarning' and @data-label='Field 1']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasWarning' and @data-label='Field 2']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 3']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 4']");
        waitForElementPresentByXpath("//input[@maxlength='50']");
        
        //State 2
        waitAndClickButtonByText("Change to State 2");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 1']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 2']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 3']");
        waitForElementPresentByXpath("//input[@maxlength='50']");
    
        //State 3
        waitAndTypeByName("field5","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        waitAndClickButtonByText("Change to State 3");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 2']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 4']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 5']");
        fireMouseOverEventByName("field5");
        waitForTextPresent("For State 3, Must be at most 25 characters");
    
        //State 4
        waitAndClickButtonByText("Change to State 4");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 2']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-inputField-labelTop uif-boxLayoutVerticalItem clearfix uif-hasError' and @data-label='Field 5']");
        fireMouseOverEventByName("field5");
        waitForTextPresent("For State 4, Must be at most 15 characters");
    }
    
    @Test
    public void testStateBasedValidationClientTestsBookmark() throws Exception {
        testStateBasedValidationClientTests();
        passed();
    }

    @Test
    public void testStateBasedValidationClientTestsNav() throws Exception {
        testStateBasedValidationClientTests();
        passed();
    }
    
}
