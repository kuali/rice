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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsValidationAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page3#UifCompView-Page3
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page3#UifCompView-Page3";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Validation");
	}
	
	@Test
    public void testValidationBookmark() throws Exception {
        testValidation();
        passed();
    }

    @Test
    public void testValidationNav() throws Exception {
        testValidation();
        passed();
    }
    
    protected void testValidation() throws InterruptedException 
    {
    	//Minmax length and value
    	waitAndTypeByName("field9","a");
    	waitAndTypeByName("field10","1");
    	fireMouseOverEventByName("field9");
    	assertTextPresent("  Please enter at least 2 characters.");
    	waitAndTypeByName("field9","");
    	fireMouseOverEventByName("field10");
    	assertTextPresent("  Value must be greater than 2");
    	waitAndTypeByName("field10","51");
    	fireMouseOverEventByName("field10");
    	assertTextPresent("  Value cannot be greater than 50");
    	
    	//Required Constraint
    	assertElementPresentByXpath("//textarea[@name='field3' and @aria-required='true']");
    	assertElementPresentByXpath("//input[@name='field4' and @aria-required='true']");
    	assertElementPresentByXpath("//select[@name='field5' and @aria-required='true']");
    	
    	//PreRequisite constraint
    	waitAndTypeByName("field6","a");
        waitAndClickSaveByText();
    	waitForElementPresentByXpath("//input[@name='field7' and @class='form-control input-sm uif-textControl dependsOn-field6 required error']");
    	waitForElementPresentByXpath("//input[@name='field8' and @class='form-control input-sm uif-textControl dependsOn-field6 prConstraint-field81 required error']");
    
    	//Must Occur Constraints
    	waitAndTypeByName("field14","a");
    	fireMouseOverEventByName("field14");
    	waitAndTypeByName("field18","a");
    	assertTextPresent("  1-2 of the following must occur: (Field 1A) OR (Field 2A and Field 2B)");
    	waitAndTypeByName("field15","a");
    	waitAndTypeByName("field16","a");
    	waitAndTypeByName("field17","a");
    	waitAndTypeByName("field18","");
    	fireMouseOverEventByName("field18");
    	assertTextPresent("  The following must occur: (Field 3A) OR (Field 4A and Field 4B)");
    	
    	//Case constraint
    	waitAndClickByXpath("//input[@name='field24' and @value='case1']");
    	waitAndTypeByName("field25","");
    	waitAndTypeByName("field26","");
    	assertElementPresentByXpath("//input[@name='field25' and @class='form-control input-sm uif-textControl dependsOn-field24 required error']");
    }
}
