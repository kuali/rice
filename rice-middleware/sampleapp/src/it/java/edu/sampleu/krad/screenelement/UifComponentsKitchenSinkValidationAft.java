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
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class UifComponentsKitchenSinkValidationAft extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
	    waitAndClickKRAD();
	    waitAndClickByLinkText("Uif Components (Kitchen Sink)");
	    switchToWindow("Kuali :: Uif Components");
	}
	
	@Test
    public void testSelectActionBookmark() throws Exception {
        testSelectAction();
        passed();
    }

    @Test
    public void testSelectActionNav() throws Exception {
        testSelectAction();
        passed();
    }
    
    protected void testSelectAction() throws InterruptedException {
        waitAndClickByXpath("//a/span[contains(text(),'Validation')]");
        waitAndClickByXpath("//a[@id='UifCompView-Navigation3']");
        
        //Minmax length and value
        waitAndTypeByName("field9","a");
        waitAndTypeByName("field10","1");
        fireMouseOverEventByName("field9");
        waitForTextPresent("Please enter at least 2 characters.");
        waitAndTypeByName("field9","");
        fireMouseOverEventByName("field10");
        waitForTextPresent("Value must be greater than 2");
        waitAndTypeByName("field10","51");
        fireMouseOverEventByName("field10");
        waitForTextPresent("Value cannot be greater than 50");
        
        //Required Constraint
        assertElementPresentByXpath("//input[@name='field2' and @aria-required='true' and @size='30' and @disabled]");
        assertElementPresentByXpath("//textarea[@name='field3' and @aria-required='true']");
        assertElementPresentByXpath("//input[@name='field4' and @type='radio' and @aria-required='true']");
        assertElementPresentByXpath("//select[@name='field5' and @aria-required='true']");
        waitAndClickButtonByText("Save");
        waitForElementPresentByXpath("//textarea[@name='field3' and @aria-required='true' and @class='form-control input-sm uif-textAreaControl required error']");
        assertElementPresentByXpath("//input[@name='field4' and @type='radio' and @aria-required='true' and @class='uif-verticalRadioControl required error']");
        assertElementPresentByXpath("//select[@name='field5' and @aria-required='true' and @class='form-control input-sm uif-dropdownControl required error']");
        
        //PreRequisite constraint
        assertElementPresentByXpath("//input[@name='field7' and @disabled]");
        waitAndClickByName("booleanField");
        waitAndTypeByName("field8","a");
        waitForElementPresentByXpath("//input[@name='field7' and @class='form-control input-sm uif-textControl dependsOn-booleanField required error']");
    
        //Must Occur Constraints
        waitAndTypeByName("field14","a");
        waitAndTypeByName("field13","");
        fireMouseOverEventByName("field14");
        waitForTextPresent("1-2 of the following must occur: (Field 1A) OR (Field 2A and Field 2B)");
        waitAndTypeByName("field18","a");
        fireMouseOverEventByName("field18");
        waitAndTypeByName("field23","");
        waitForTextPresent("The following must occur: (Field 3A) OR (Field 4A and Field 4B)");
        waitAndTypeByName("field23","a");
        waitAndTypeByName("field18","");
        fireMouseOverEventByName("field23");
        waitForTextPresent("1-2 of the following must occur: (Field 5A or Field 5B) OR (Field 6A and Field 6B)");
               
        //Case constraint
        waitAndClickByXpath("//input[@name='field24' and @value='case1']");
        waitAndTypeByName("field25","");
        waitAndTypeByName("field26","");
        assertElementPresentByXpath("//input[@name='field25' and @class='form-control input-sm uif-textControl dependsOn-field24 required error']");
        waitAndClickByXpath("//input[@name='field24' and @value='case2']");
        waitAndTypeByName("field25","_+");
        waitAndTypeByName("field26","");
        fireMouseOverEventByName("field25");
        waitForTextPresent("Can only be alphanumeric characters");
        waitAndClickByXpath("//input[@name='field24' and @value='case3']");
        waitAndTypeByName("field26","1111");
        waitAndTypeByName("field25","");
        fireMouseOverEventByName("field26");
        waitForTextPresent("Must be at most 3 characters");
        waitAndClickByXpath("//input[@name='field24' and @value='case4']");
        waitAndTypeByName("field27","a");
        waitAndTypeByName("field25","");
        fireMouseOverEventByName("field28");
        waitForTextPresent("Required by Field Z1");
        waitAndTypeByName("field31","a");
        waitAndTypeByName("field33","");
        fireMouseOverEventByName("field33");
        waitForTextPresent("Required");
    }
}
