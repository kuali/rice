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

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page1#UifCompView-Page1
     */
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
    	waitForElementPresentByXpath("//label[@class='uif-label uif-labelBlock uif-tooltip' and contains(text(),'Field Label')]");
    	waitForElementPresentByXpath("//label[@class='uif-label uif-labelBlock' and contains(text(),'Field Label:')]");
    	waitForElementPresentByXpath("//label[@class='uif-label uif-labelBlock' and contains(text(),'Field Label:')]");
    	waitForElementPresentByXpath("//p[contains(text(),'Constraint text')]");
    	
    	//TextBox
    	waitForElementPresentByXpath("//input[@placeholder and @size='30' and @maxlength='40']");
    	waitForElementPresentByXpath("//input[@size='60']");
    	waitAndClickByXpath("//a[@title='Expand']");
    	waitForElementPresentByXpath("//textarea[@id='textarea_popout_control']");
    	waitAndClickByXpath("//input[@id='done_btn']");
    	waitForElementPresentByXpath("//input[@disabled and @name='field109']");
    	waitForElementPresentByXpath("//input[@style='text-transform: uppercase;']");
    	
    	//TextArea
    	waitForElementPresentByXpath("//textarea[@placeholder='Watermark... ' and @rows='5' and @cols='40']");
    	waitForElementPresentByXpath("//textarea[@rows='10' and @cols='50']");
    	
    	//Date Input
    	waitAndTypeByName("date1","");
    	waitAndTypeByName("date2","asd");
    	assertElementPresentByXpath("//input[@name='date2' and @placeholder='mm/dd/yyyy ']");
    	assertElementPresentByXpath("//input[@name='field132' and @placeholder='mm-dd-yy ']");
    	assertElementPresentByXpath("//input[@name='field133' and @placeholder='mm-dd-yyyy ']");
    	waitAndTypeByName("field133","asd");
    	waitAndTypeByName("date5","2");
    	waitForElementPresentByXpath("//input[@name='field133' and @class='form-control input-sm uif-dateControl validChar-field1330 hasDatepicker dirty error']");
    	waitAndClickByXpath("//input[@name='date6']");
    	waitForElementPresentByXpath("//div[@class='ui-datepicker-group ui-datepicker-group-first']");
    	waitForElementPresentByXpath("//div[@class='ui-datepicker-group ui-datepicker-group-last']");
    	waitAndClickByXpath("//button[@class='ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all']");//Today
    	waitForElementPresentByXpath("//input[@class='form-control input-sm uif-dateControl validChar-date50 hasDatepicker dirty error' and @name='date5']");
    	waitForElementPresentByXpath("//input[@name='date7' and @disabled]");
    	
    	//Selection Control
    	//Checkbox and Radio
    	assertElementPresentByXpath("//input[@type='checkbox' and @name='bField1']");
    	waitAndClickByXpath("//input[@type='checkbox' and @name='field114' and @value='1']");
    	waitAndClickByXpath("//input[@type='checkbox' and @name='field116' and @disabled]");
    	waitAndClickByXpath("//input[@type='radio' and @name='field117' and @value='1']");
    	
    	//Select and Dropdown
    	selectByName("field119","Option 1");
    	waitForElementPresentByXpath("//select[@name='field119']");
    	waitForElementPresentByXpath("//div[@data-label='Dropdown Navigation']/select[@class='uif-dropdownControl valid']");
    	waitForElementPresentByXpath("//ul/li/a[contains(text(),'Kuali.org')]");
    	waitForElementPresentByXpath("//div[@data-label='Option List Nav - UrlInfo configured']/ul/li/a[contains(text(),'Other Fields')]");
    	waitForElementPresentByXpath("//ul/li/a[contains(text(),'Kuali.org')]");
    	assertElementPresentByXpath("//select[@name='field120' and @multiple and @size='3']");
    	
    	//Others
    	assertElementPresentByXpath("//input[@type='file']");
    	waitAndTypeByName("testPerson.principalName","fred");
    	waitAndTypeByName("field131","1");
    	Thread.sleep(3000);//Needed as the text wont appears else
    	waitForTextPresent("fred, fred");
    	clearTextByName("testPerson.principalName");
    	waitAndTypeByName("testPerson.principalName","deep");
    	waitAndTypeByName("field131","1");
       	Thread.sleep(3000);//Needed as the text wont appears else
    	waitForTextPresent("user control not found");
    	assertElementPresentByXpath("//a[@class='ui-spinner-button ui-spinner-up ui-corner-tr']");
    	assertElementPresentByXpath("//a[@class='ui-spinner-button ui-spinner-down ui-corner-br']");
    	
    	//Page Navigation links
    	waitAndClickByXpath("//a[@data-onclick='jumpToBottom();' and contains(text(),'Jump To Bottom Demo')]");
    	waitAndClickByXpath("//a[@id='ActionLinkField_JumpToField119' and contains(text(),'Jump to field with property name field119 (drop down)')]");
    	waitAndClickByXpath("//a[@id='ActionLinkField_FocusOnField107' and contains(text(),'Focus on field with property name field107 (size 60 text field)')]");
    	waitAndClickByXpath("//a[@id='ActionLinkField_FocusOnIdMyDate' and contains(text(),'Focus on field with id myDate (Date2 field)')]");
    }
}
