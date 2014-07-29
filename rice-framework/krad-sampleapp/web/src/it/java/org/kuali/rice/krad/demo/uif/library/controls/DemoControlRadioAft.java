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
package org.kuali.rice.krad.demo.uif.library.controls;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlRadioAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-RadioControlView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-RadioControlView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Radio");
    }

    protected void testLibraryControlRadioOptionsFinder() throws Exception {
        waitAndClickByLinkText("OptionsFinder");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example1']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example1']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='5']");
    }
    
    protected void testLibraryControlRadioKeyValuePairs() throws Exception {
        waitAndClickByLinkText("Key-value pr.");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example2']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example2']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example3']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example3']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioDelimiter() throws Exception {
        waitAndClickByLinkText("Delimiter");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example4']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        assertTextPresent(";");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example4']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioEvaluateDisabledOnKeyUp() throws Exception {
        waitAndClickByLinkText("Evaluate disabled on key up");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example5']/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example5']/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='2']");
        waitAndTypeByName("inputField1","a");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example5']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example5']/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioEnableWhenChanged() throws Exception {
        waitAndClickByLinkText("Enable when changed");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example6']/fieldset/span/input[@type='radio' and @name='inputField4' and @disabled='disabled' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example6']/fieldset/span/input[@type='radio' and @name='inputField4' and @disabled='disabled' and @value='2']");
        waitAndTypeByName("inputField3","a");
        typeTab();
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example6']/fieldset/span/input[@type='radio' and @name='inputField4' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example6']/fieldset/span/input[@type='radio' and @name='inputField4' and @value='2']");
    }
    
    protected void testLibraryControlRadioDisableWhenChanged() throws Exception {
        waitAndClickByLinkText("Disable when changed");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example7']/fieldset/span/input[@type='radio' and @name='inputField6' and @value='1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example7']/fieldset/span/input[@type='radio' and @name='inputField6' and @value='2']");
        waitAndTypeByName("inputField5","a");
        typeTab();
        waitForElementPresentByXpath("//div[@data-parent='Demo-RadioControl-Example7']/fieldset/span/input[@disabled]");
    }
    
    @Test
    public void testControlRadioBookmark() throws Exception {
        testLibraryControlRadioOptionsFinder();
        testLibraryControlRadioKeyValuePairs();
        testLibraryControlRadioDisabled();
        testLibraryControlRadioDelimiter();
        testLibraryControlRadioEvaluateDisabledOnKeyUp();
        testLibraryControlRadioEnableWhenChanged();
        testLibraryControlRadioDisableWhenChanged();
        passed();
    }

    @Test
    public void testControlRadioNav() throws Exception {
        testLibraryControlRadioOptionsFinder();
        testLibraryControlRadioKeyValuePairs();
        testLibraryControlRadioDisabled();
        testLibraryControlRadioDelimiter();
        testLibraryControlRadioEvaluateDisabledOnKeyUp();
        testLibraryControlRadioEnableWhenChanged();
        testLibraryControlRadioDisableWhenChanged();
        passed();
    }  
}
