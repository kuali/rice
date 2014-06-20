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
        acceptAlert();
        waitAndClickByName("inputField2");
        acceptAlert();
    	waitAndClickByXpath("//input[@name='inputField3' and @value='blue']");
    	waitForElementPresentByXpath("//div[@style='border: 1px solid rgb(153, 153, 153); width: auto; background-color: rgb(0, 0, 255);']");
    	waitAndClickByName("inputField4");
        acceptAlert();
    	waitAndClickByXpath("//input[@name='inputField5' and @value='1']");
        acceptAlert();
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
    	waitForElementPresentByXpath("//div[@style='border: 10px solid rgb(153, 153, 153); width: 50%; overflow; hidden; font-size: 3em; margin-left: 0px;']");
    	waitAndClickByXpath("//input[@name='inputField8' and @value='red']");
    	fireMouseOverEventByName("inputField8");
    	waitForElementPresentByXpath("//div[@style='width: auto; background-color: rgb(255, 0, 0);']");
    	waitAndClickByXpath("//div[@class='kr-photo uif-boxLayoutVerticalItem pull-left clearfix']");
    }
    
    protected void testClientResponsivenessEventsMouseEventOnButtons() throws Exception {
    	waitAndClickByLinkText("Mouse Events on Buttons");
    	waitForElementPresentByXpath("//button[@style='margin-top:10px;']");
    }
    
    protected void testClientResponsivenessEventsGroupAndSectionEvents() throws Exception {
    	waitAndClickByLinkText("Group and Section Events");
    	fireMouseOverEventByXpath("//div[@data-parent='Demo-Events-Example5']");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-Events-Example5' and @style='height: 200px; width: 300px; background-color: rgb(255, 255, 255);']");
    }
    
    protected void testClientResponsivenessEventsOnKeyPressUpDown() throws Exception {
    	waitAndClickByLinkText("OnKeyPress, OnKeyUp, OnKeyDown Examples");
    	waitAndTypeByName("inputField13","a");
        acceptAlert();
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
