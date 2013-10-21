/*
 * Copyright 2005-2013 The Kuali Foundation
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

public class DemoEventExampleAft extends DemoKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=307ef15f-6e22-469b-bb4e-dc0d13056b5d&cacheKey=fkxfb8lxq8rozbctlgbz0k0zoa&pageId=UifCompView-Page11#UifCompView-Page11";
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Event Examples");
	}
	
	@Test
    public void testKitchenSinkEventExamplesBookmark() throws Exception {
        testKitchenSinkEventExamples();
        passed();
    }

    @Test
    public void testKitchenSinkEventExamplesNav() throws Exception {
        testKitchenSinkEventExamples();
        passed();
    }
    
    protected void testKitchenSinkEventExamples() throws InterruptedException 
    {
    	//OnClick examples on various components
    	waitAndClickByXpath("//div[@id='UifCompView-WindowEvents-Group1']/div[@class='uif-horizontalBoxLayout clearfix']/div/input[@name='field100' and @class='uif-textControl']");
    	alertAccept();
    	waitAndClickByXpath("//div[@id='UifCompView-WindowEvents-Group1']/div[@class='uif-horizontalBoxLayout clearfix']/div/input[@name='field112' and @class='uif-dateControl hasDatepicker']");
    	alertAccept();
    	waitAndClickByXpath("//div[@data-parent='UifCompView-WindowEvents-Group1']/fieldset/span/input[@name='field118' and @value='blue']");
    	waitForElementPresentByXpath("//div[@style='border: 1px solid rgb(153, 153, 153); width: auto; background-color: rgb(0, 0, 255);']");
    	waitAndClickByXpath("//div[@data-parent='UifCompView-WindowEvents-Group1']/input[@type='checkbox' and @name='bField1']");
    	alertAccept();
    	waitAndClickByXpath("//input[@type='checkbox' and @name='field115' and @value='1']");
    	alertAccept();
    	
    	//Other Window Events: OnChange, onFocus, onBlur
    	selectByXpath("//div[@data-parent='UifCompView-WindowEvents-Group2']/select","Lime");
    	waitForElementPresentByXpath("//div[@style='background-color: rgb(0, 255, 0);']");
    	fireEvent("field110", "focus");
    	waitAndClickById("Group2-principalName_control");
    	alertAccept();
    	waitAndClickByXpath("//input[@class='uif-spinnerControl ui-spinner-input']");
    	alertAccept();
    	
    	//Click on image element
    	assertElementPresentByXpath("//div[@id='Group4-Image1']");
    	waitAndClickByXpath("//div[@id='Group4-Image1']/img");
    	waitForElementPresentByXpath("//div[@style='margin-left: 80px;' and @id='Group4-Image1']");
    	
    	//Mouse over events
    	fireMouseOverEventByXpath("//div[@style='border: 0px solid #999999; width:10%;']");
    
    	//Mouse Event on Button
    	waitAndClickByXpath("//button[@style='margin-top:10px;']");
    	alertAccept();
    	
    	//Other div elements
    	fireMouseOverEventByXpath("//div[@style='background-color:red;height:200px; width:300px;']");
    	waitForElementPresentByXpath("//div[@style='background-color: rgb(255, 255, 255); height: 200px; width: 300px;']");
    	
    	//OnKeyPress, KeyUp, KeyDown
    	waitAndTypeByXpath("//div[@id='UifCompView-KeyEventsGroup_disclosureContent']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-horizontalBoxLayout clearfix']/div/input[@name='field100' and @class='uif-textControl']","a");
    	alertAccept();
    }
}
