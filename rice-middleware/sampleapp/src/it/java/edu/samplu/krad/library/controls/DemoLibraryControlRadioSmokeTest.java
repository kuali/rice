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
package edu.samplu.krad.library.controls;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryControlRadioSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-RadioControl-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-RadioControl-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Radio");
    }

    protected void testLibraryControlRadioOptionsFinder() throws Exception {
        waitAndClickByLinkText("OptionsFinder");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='5']");
    }
    
    protected void testLibraryControlRadioKeyValuePairs() throws Exception {
        waitAndClickByLinkText("Key-value pr.");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='2']");
    }
    
    protected void testLibraryControlRadioDelimiter() throws Exception {
        waitAndClickByLinkText("Delimiter");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        assertTextPresent(";");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioDisableOnKeyEvent() throws Exception {
        waitAndClickByLinkText("Disable on Key event");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @disabled='disabled' and @value='2']");
        waitAndTypeByName("inputField1","a");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField2' and @value='2']");
    }
    
    protected void testLibraryControlRadioEnableWhenChanged() throws Exception {
        waitAndClickByLinkText("Enable when changed");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField4' and @disabled='disabled' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField4' and @disabled='disabled' and @value='2']");
        waitAndTypeByName("inputField3","a");
        waitAndClickByLinkText("Usage");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField4' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField4' and @value='2']");
    }
    
    protected void testLibraryControlRadioDisableWhenChanged() throws Exception {
        waitAndClickByLinkText("Disable when changed");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField6' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='radio' and @name='inputField6' and @value='2']");
        waitAndTypeByName("inputField5","a");
        waitAndClickByLinkText("Usage");
        waitForElementPresentByXpath("//div[@id='Demo-RadioControl-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@disabled]");
    }
    
    @Test
    public void testControlRadioBookmark() throws Exception {
        testLibraryControlRadioOptionsFinder();
        testLibraryControlRadioKeyValuePairs();
        testLibraryControlRadioDisabled();
        testLibraryControlRadioDelimiter();
        testLibraryControlRadioDisableOnKeyEvent();
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
        testLibraryControlRadioDisableOnKeyEvent();
        testLibraryControlRadioEnableWhenChanged();
        testLibraryControlRadioDisableWhenChanged();
        passed();
    }  
}