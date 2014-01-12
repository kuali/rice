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

public class LabsInputFieldsAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page1#UifCompView-Page1";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Input Fields");
	}
	
	@Test
    public void testInputFieldsBookmark() throws Exception {
        testInputFields();
        passed();
    }

    @Test
    public void testInputFieldsNav() throws Exception {
        testInputFields();
        passed();
    }
    
    protected void testInputFields() throws InterruptedException {
    	//Label
    	assertElementPresentByXpath("//label[@class='labelTop uif-labelBlock uif-tooltip' and contains(text(),'Field Label')]");
    	assertElementPresentByXpath("//label[@class='uif-label' and contains(text(),'Field Label:')]");
    	assertElementPresentByXpath("//span[@class='uif-constraintMessage']");
    	
    	//TextBox
    	assertElementPresentByXpath("//input[@placeholder and @size='30' and @maxlength='40']");
    	assertElementPresentByXpath("//input[@size='60']");
    	assertElementPresentByXpath("//a[@title='Expand']");
    	assertElementPresentByXpath("//input[@disabled and @name='field109']");
    	assertElementPresentByXpath("//input[@style='text-transform: uppercase;']");
    	
    	//TextArea
    	assertElementPresentByXpath("//textarea[@placeholder='Watermark... ' and @rows='5' and @cols='40']");
    	assertElementPresentByXpath("//textarea[@rows='10' and @cols='50']");
    	
    	//Date Input
    	waitAndTypeByName("date1","");
    	waitAndTypeByName("date2","asd");
    	assertElementPresentByXpath("//input[@name='date2' and @placeholder='mm/dd/yyyy ']");
    	assertElementPresentByXpath("//input[@name='field132' and @placeholder='mm-dd-yy ']");
    	assertElementPresentByXpath("//input[@name='field133' and @placeholder='mm-dd-yyyy ']");
    	waitAndTypeByName("field133","asd");
    	waitAndTypeByName("date5","2");
    	assertElementPresentByXpath("//input[@name='field133' and @class='form-control input-sm uif-dateControl validChar-field1330 hasDatepicker dirty error']");
    	waitAndTypeByName("date6","");
    	assertElementPresentByXpath("//button[contains(text(),'Today')]");
    	assertElementPresentByXpath("//input[@class='form-control input-sm uif-dateControl validChar-date50 hasDatepicker dirty error' and @name='date5']");
    	assertElementPresentByXpath("//input[@name='date7' and @disabled]");
    	
    	//Selection Control
    	//Checkbox and Radio
    	assertElementPresentByXpath("//input[@type='checkbox' and @name='bField1']");
    	waitAndClickByXpath("//input[@type='checkbox' and @name='field114' and @value='1']");
    	waitAndClickByXpath("//input[@type='checkbox' and @name='field116' and @disabled]");
    	waitAndClickByXpath("//input[@type='radio' and @name='field117' and @value='1']");
    	
    	//Select and Dropdown
    	selectByName("field119","Option 1");
    	assertElementPresentByXpath("//select[@name='field120' and @multiple and @size='3']");
    	
    	//Others
    	assertElementPresentByXpath("//input[@type='file']");
    	waitAndClickByXpath("//a[@class='ui-spinner-button ui-spinner-up ui-corner-tr']");
    	assertElementPresentByXpath("//input[@name='field131' and @aria-valuenow='1']");
    }
}
