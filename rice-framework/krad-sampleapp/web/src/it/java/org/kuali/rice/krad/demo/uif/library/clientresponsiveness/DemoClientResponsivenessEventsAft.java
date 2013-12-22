/**
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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessEventsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-EventsView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-EventsView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Events");
    }

    protected void testClientResponsivenessEventsClick() throws Exception {
    	waitAndClickByName("inputField1");
    	acceptAlertIfPresent();
    	waitAndClickByName("inputField2");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//input[@name='inputField3' and @value='blue']");
    	waitForElementPresentByXpath("//div[@style='border: 1px solid rgb(153, 153, 153); width: auto; background-color: rgb(0, 0, 255);']");
    	waitAndClickByName("inputField4");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//input[@name='inputField5' and @value='1']");
    	acceptAlertIfPresent();
    }
    
    protected void testClientResponsivenessEventsChangeFocusBlur() throws Exception {
    	waitAndClickByLinkText("OnChange, OnFocus, OnBlur Examples");
    	selectByName("inputField6","Navy");
    	waitForElementPresentByXpath("//div[@id='Demo-Events-Example2A' and @style='background-color: rgb(0, 0, 128);']");
    	fireEvent("inputField7", "focus");
    }
    
    protected void testClientResponsivenessEventsMouseEvent() throws Exception {
    	waitAndClickByLinkText("Mouse Events");
    	fireMouseOverEventByXpath("//div[@data-label='Checkbox' and @data-parent='Demo-Events-Example3']");
    	waitAndClickByXpath("//input[@name='inputField8' and @value='red']");
    	fireMouseOverEventByName("inputField8");
    }
    
    protected void testClientResponsivenessEventsMouseEventOnButtons() throws Exception {
    	waitAndClickByLinkText("Mouse Events on Buttons");
    	waitForElementPresentByXpath("//button[@style='margin-top:10px;']");
    }
    
    protected void testClientResponsivenessEventsGroupAndSectionEvents() throws Exception {
    	waitAndClickByLinkText("Group and Section Events");
    	fireMouseOverEventByXpath("//div[@data-parent='Demo-Events-Example5']");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-Events-Example5' and @style='background-color: rgb(255, 255, 255); height: 200px; width: 300px;']");
    }
    
    protected void testClientResponsivenessEventsOnKeyPressUpDown() throws Exception {
    	waitAndClickByLinkText("OnKeyPress, OnKeyUp, OnKeyDown Examples");
    	waitAndTypeByName("inputField13","a");
    	acceptAlertIfPresent();
    }

    
    @Test
    public void testClientResponsivenessEventsBookmark() throws Exception {
    	testClientResponsivenessEventsAll();
    }

    @Test
    public void testClientResponsivenessEventsNav() throws Exception {
    	testClientResponsivenessEventsAll();
    }  
    
    private void testClientResponsivenessEventsAll() throws Exception {
    	testClientResponsivenessEventsClick();
    	testClientResponsivenessEventsChangeFocusBlur();
    	testClientResponsivenessEventsMouseEvent();
    	testClientResponsivenessEventsMouseEventOnButtons();
    	testClientResponsivenessEventsGroupAndSectionEvents();
    	testClientResponsivenessEventsOnKeyPressUpDown();
    	passed();
    }
}
