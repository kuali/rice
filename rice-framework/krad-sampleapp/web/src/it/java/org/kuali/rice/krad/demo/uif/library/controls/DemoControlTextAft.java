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
package org.kuali.rice.krad.demo.uif.library.controls;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlTextAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TextControlView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TextControlView&methodToCall=start";

    /**
     * //div[@id='Demo-TextControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField1']
     */
    private static final String DEFAULT_TXT_FIELD_XPATH = "//div[@id='Demo-TextControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField1']";
    
    /**
     * //div[@id='Demo-TextControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField2']
     */
    private static final String SIZE_TXT_FIELD_XPATH = "//div[@id='Demo-TextControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField2']";
    
    /**
     * //div[@id='Demo-TextControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/a[@title='Expand']
     */
    private static final String EXPAND_TXT_FIELD_XPATH = "//a[@title='Expand']";
    
    /**
     * //div[@id='Demo-TextControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField4' and @disabled='disabled']
     */
    private static final String DISABLED_TXT_FIELD_XPATH = "//div[@id='Demo-TextControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField4' and @disabled='disabled']";
    
    /**
     * //div[@id='Demo-TextControl-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField5' and @placeholder='watermark text ']
     */
    private static final String WATERMARK_TXT_FIELD_XPATH = "//div[@id='Demo-TextControl-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField5' and @placeholder='watermark text ']";

    /**
     * //div[@id='Demo-TextControl-Example8']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField7' and @disabled='disabled']
     */
    private static final String DISABLE_ON_KEY_EVENT_TXT_FIELD_XPATH_BEFORE = "//div[@id='Demo-TextControl-Example8']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField7' and @disabled='disabled']";
    
    /**
     * //div[@id='Demo-TextControl-Example8']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField7']
     */
    private static final String DISABLE_ON_KEY_EVENT_TXT_FIELD_XPATH_AFTER = "//div[@id='Demo-TextControl-Example8']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField7']";
    
    /**
     * //div[@id='Demo-TextControl-Example9']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField9' and @disabled='disabled']
     */
    private static final String ENABLE_WHEN_CHANGED_TXT_FIELD_XPATH_BEFORE = "//div[@id='Demo-TextControl-Example9']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField9' and @disabled='disabled']";
    
    /**
     * //div[@id='Demo-TextControl-Example9']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField9']
     */
    private static final String ENABLE_WHEN_CHANGED_TXT_FIELD_XPATH_AFTER = "//div[@id='Demo-TextControl-Example9']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField9']";
    
    /**
     * //div[@id='Demo-TextControl-Example10']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField11' and @disabled='disabled']
     */
    private static final String DISABLE_WHEN_CHANGED_TXT_FIELD_XPATH_AFTER = "//div[@id='Demo-TextControl-Example10']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField11' and @disabled]";
    
    /**
     * //div[@id='Demo-TextControl-Example10']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField11']
     */
    private static final String DISABLE_WHEN_CHANGED_TXT_FIELD_XPATH_BEFORE = "//div[@id='Demo-TextControl-Example10']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='text' and @name='inputField11']";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Text");
    }

    protected void testLibraryControlTextDefault() throws Exception {
        waitForElementPresentByXpath(DEFAULT_TXT_FIELD_XPATH);
        waitAndTypeByXpath(DEFAULT_TXT_FIELD_XPATH,"HiText!");
    }
    
    protected void testLibraryControlTextSize() throws Exception {
        waitAndClickByLinkText("Size");
        waitForElementPresentByXpath(SIZE_TXT_FIELD_XPATH);
        waitAndTypeByXpath(SIZE_TXT_FIELD_XPATH,"HiTextHiTextHiTextHiTextHiTextHiTextHiTextHiTextHiTextHiText");
    }
    
    protected void testLibraryControlTextExpand() throws Exception {
        waitAndClickByLinkText("Text expand");
        waitForElementPresentByXpath(EXPAND_TXT_FIELD_XPATH);
    }
    
    protected void testLibraryControlTextDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        waitForElementPresentByXpath(DISABLED_TXT_FIELD_XPATH);
    }
    
    protected void testLibraryControlTextWatermarkText() throws Exception {
        waitAndClickByLinkText("WatermarkText");
        waitForElementPresentByXpath(WATERMARK_TXT_FIELD_XPATH);
    }

    protected void testLibraryControlTextDisableOnKeyEvent() throws Exception {
        waitAndClickByLinkText("Disable on Key event");
        assertElementPresentByXpath(DISABLE_ON_KEY_EVENT_TXT_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField6","a");
        assertElementPresentByXpath(DISABLE_ON_KEY_EVENT_TXT_FIELD_XPATH_AFTER);
    }
    
    protected void testLibraryControlTextEnableWhenChanged() throws Exception {
        waitAndClickByLinkText("Enable when changed");
        assertElementPresentByXpath(ENABLE_WHEN_CHANGED_TXT_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField8","a");
        waitAndClickByLinkText("Usage");
        assertElementPresentByXpath(ENABLE_WHEN_CHANGED_TXT_FIELD_XPATH_AFTER);
    }
    
    protected void testLibraryControlTextDisableWhenChanged() throws Exception {
        waitAndClickByLinkText("Disable when changed");
        assertElementPresentByXpath(DISABLE_WHEN_CHANGED_TXT_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField10","a");
        waitAndClickByLinkText("Usage");
        assertElementPresentByXpath(DISABLE_WHEN_CHANGED_TXT_FIELD_XPATH_AFTER);
    }
    
    @Test
    public void testControlTextBookmark() throws Exception {
        testLibraryControlTextDefault();
        testLibraryControlTextSize();
        testLibraryControlTextExpand();
        testLibraryControlTextDisabled();
        testLibraryControlTextWatermarkText();
        testLibraryControlTextDisableOnKeyEvent();
        testLibraryControlTextEnableWhenChanged();
        testLibraryControlTextDisableWhenChanged();
        passed();
    }

    @Test
    public void testControlTextNav() throws Exception {
        testLibraryControlTextDefault();
        testLibraryControlTextSize();
        testLibraryControlTextExpand();
        testLibraryControlTextDisabled();
        testLibraryControlTextWatermarkText();
        testLibraryControlTextDisableOnKeyEvent();
        testLibraryControlTextEnableWhenChanged();
        testLibraryControlTextDisableWhenChanged();
        passed();
    }  
}
