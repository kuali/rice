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

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlPasswordAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-PasswordControl-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-PasswordControl-View&methodToCall=start";
    
    /**
     * //div[@id='Demo-PasswordControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']
     */
    private static final String DEFAULT_PWD_FIELD_XPATH = "//div[@id='Demo-PasswordControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']
     */
    private static final String SIZE_PWD_FIELD_XPATH = "//div[@id='Demo-PasswordControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2' and @disabled='disabled']
     */
    private static final String DISABLED_PWD_FIELD_XPATH = "//div[@id='Demo-PasswordControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2' and @disabled='disabled']";

    /**
     * //div[@id='Demo-PasswordControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2' and @disabled='disabled']
     */
    private static final String DISABLE_ON_KEY_EVENT_PWD_FIELD_XPATH_BEFORE = "//div[@id='Demo-PasswordControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2' and @disabled='disabled']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']
     */
    private static final String DISABLE_ON_KEY_EVENT_PWD_FIELD_XPATH_AFTER = "//div[@id='Demo-PasswordControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField2']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField4' and @disabled='disabled']
     */
    private static final String ENABLE_WHEN_CHANGED_PWD_FIELD_XPATH_BEFORE = "//div[@id='Demo-PasswordControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField4' and @disabled='disabled']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField4']
     */
    private static final String ENABLE_WHEN_CHANGED_PWD_FIELD_XPATH_AFTER = "//div[@id='Demo-PasswordControl-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField4']";
    
    /**
     * //div[@id='Demo-PasswordControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField6' and @disabled='disabled']
     */
    private static final String DISABLE_WHEN_CHANGED_PWD_FIELD_XPATH_AFTER = "//div[@id='Demo-PasswordControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField6' and @disabled]";
    
    /**
     * //div[@id='Demo-PasswordControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField6']
     */
    private static final String DISABLE_WHEN_CHANGED_PWD_FIELD_XPATH_BEFORE = "//div[@id='Demo-PasswordControl-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/input[@type='password' and @name='inputField6']";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Password");
    }

    protected void testLibraryControlPasswordDefault() throws Exception {
        waitForElementPresentByXpath(DEFAULT_PWD_FIELD_XPATH);
        waitAndTypeByXpath(DEFAULT_PWD_FIELD_XPATH,"HiPassword!");
    }
    
    protected void testLibraryControlPasswordSize() throws Exception {
        waitAndClickByLinkText("Size");
        waitForElementPresentByXpath(SIZE_PWD_FIELD_XPATH);
        waitAndTypeByXpath(SIZE_PWD_FIELD_XPATH,"HiPasswordHiPasswordHiPasswordHiPasswordHiPasswordHiPassword!");
    }
    
    protected void testLibraryControlPasswordDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        waitForElementPresentByXpath(DISABLED_PWD_FIELD_XPATH);
    }

    protected void testLibraryControlPasswordDisableOnKeyEvent() throws Exception {
        waitAndClickByLinkText("Disable on Key event");
        assertElementPresentByXpath(DISABLE_ON_KEY_EVENT_PWD_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField1","a");
        assertElementPresentByXpath(DISABLE_ON_KEY_EVENT_PWD_FIELD_XPATH_AFTER);
    }
    
    protected void testLibraryControlPasswordEnableWhenChanged() throws Exception {
        waitAndClickByLinkText("Enable when changed");
        assertElementPresentByXpath(ENABLE_WHEN_CHANGED_PWD_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField3","a");
        waitAndClickByLinkText("Usage");
        assertElementPresentByXpath(ENABLE_WHEN_CHANGED_PWD_FIELD_XPATH_AFTER);
    }
    
    protected void testLibraryControlPasswordDisableWhenChanged() throws Exception {
        waitAndClickByLinkText("Disable when changed");
        assertElementPresentByXpath(DISABLE_WHEN_CHANGED_PWD_FIELD_XPATH_BEFORE);
        waitAndTypeByName("inputField5","a");
        waitAndClickByLinkText("Usage");
        assertElementPresentByXpath(DISABLE_WHEN_CHANGED_PWD_FIELD_XPATH_AFTER);
    }
    
    @Test
    public void testControlPasswordBookmark() throws Exception {
        testLibraryControlPasswordDefault();
        testLibraryControlPasswordSize();
        testLibraryControlPasswordDisabled();
        testLibraryControlPasswordDisableOnKeyEvent();
        testLibraryControlPasswordEnableWhenChanged();
        testLibraryControlPasswordDisableWhenChanged();
        passed();
    }

    @Test
    public void testControlPasswordNav() throws Exception {
        testLibraryControlPasswordDefault();
        testLibraryControlPasswordSize();
        testLibraryControlPasswordDisabled();
        testLibraryControlPasswordDisableOnKeyEvent();
        testLibraryControlPasswordEnableWhenChanged();
        testLibraryControlPasswordDisableWhenChanged();
        passed();
    }  
}
