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
public class DemoControlCheckboxGroupAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CheckboxGroupControl-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CheckboxGroupControl-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Checkbox Group");
    }

    protected void testLibraryControlCheckboxGroupOptionsFinder() throws Exception {
        waitAndClickByLinkText("optionsFinder");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField1' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField1' and @value='5']");
    }
    
    protected void testLibraryControlCheckboxGroupKeyValuePairs() throws Exception {
        waitAndClickByLinkText("Key Value Pairs");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField2' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField2' and @value='3']");
    }
    
    protected void testLibraryControlCheckboxGroupHorizontal() throws Exception {
        waitAndClickByLinkText("Horizontal");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField3' and @value='1']");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField3' and @value='2']");
    }
    
    protected void testLibraryControlCheckboxGroupDelimiter() throws Exception {
        waitAndClickByLinkText("Delimiter");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField4' and @value='1']");
        assertTextPresent("|");
        waitForElementPresentByXpath("//div[@id='Demo-CheckboxGroupControl-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/span/input[@type='checkbox' and @name='checkboxesField4' and @value='2']");
    }
    
    @Test
    public void testControlCheckboxGroupBookmark() throws Exception {
        testLibraryControlCheckboxGroupOptionsFinder();
        testLibraryControlCheckboxGroupKeyValuePairs();
        testLibraryControlCheckboxGroupHorizontal();
        testLibraryControlCheckboxGroupDelimiter();
        passed();
    }

    @Test
    public void testControlCheckboxGroupNav() throws Exception {
        testLibraryControlCheckboxGroupOptionsFinder();
        testLibraryControlCheckboxGroupKeyValuePairs();
        testLibraryControlCheckboxGroupHorizontal();
        testLibraryControlCheckboxGroupDelimiter();
        passed();
    }  
}
