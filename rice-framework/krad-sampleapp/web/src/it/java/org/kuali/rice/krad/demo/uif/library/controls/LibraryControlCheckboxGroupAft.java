/**
 * Copyright 2005-2017 The Kuali Foundation
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
public class LibraryControlCheckboxGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CheckboxGroupControlView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CheckboxGroupControlView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Checkbox Group");
    }

    protected void testLibraryControlCheckboxGroupOptionsFinder() throws Exception {
        waitAndClickByLinkText("optionsFinder");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField1' and @value='1']");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField1' and @value='5']");
    }
    
    protected void testLibraryControlCheckboxGroupKeyValuePairs() throws Exception {
        waitAndClickByLinkText("Key Value Pairs");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField2' and @value='1']");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField2' and @value='3']");
    }
    
    protected void testLibraryControlCheckboxGroupHorizontal() throws Exception {
        waitAndClickByLinkText("Horizontal");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField3' and @value='1']");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField3' and @value='2']");
    }
    
    protected void testLibraryControlCheckboxGroupDelimiter() throws Exception {
        waitAndClickByLinkText("Delimiter");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField4' and @value='1']");
        assertTextPresent("\n|");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField4' and @value='2']");
    }

    protected void testLibraryControlCheckboxGroupDirtyValidation() throws Exception {
        // If checkboxes are selected then the "Form has unsaved data" message should appear when the user attempts
        // to leave the page.
        waitAndClickByLinkText("optionsFinder");
        checkByName("checkboxesField1");
        waitAndClickByLinkText("File");
        Thread.sleep(3000);
        if(isAlertPresent())    {
            alertAccept();
        } else {
            fail("An alert should have popped up to warn the user about unsaved data.");
        }

        waitForTextPresent("File Control");
        waitAndClickByLinkText("Checkbox Group");
        waitAndClickByLinkText("optionsFinder");
        waitForElementPresentByXpath("//input[@type='checkbox' and @name='checkboxesField1' and @value='1']");
        waitAndClickByLinkText("File");
        waitForTextPresent("File Control");
        if(isAlertPresent())    {
            fail("No data was changed so there should be no alert.");
        }
    }

    protected boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        }   // try
        catch (Exception Ex) {
            return false;
        }   // catch
    }

    @Test
    public void testControlCheckboxGroupBookmark() throws Exception {
        testLibraryControlCheckboxGroupOptionsFinder();
        testLibraryControlCheckboxGroupKeyValuePairs();
        testLibraryControlCheckboxGroupHorizontal();
        testLibraryControlCheckboxGroupDelimiter();
        testLibraryControlCheckboxGroupDirtyValidation();
        passed();
    }

    @Test
    public void testControlCheckboxGroupNav() throws Exception {
        testLibraryControlCheckboxGroupOptionsFinder();
        testLibraryControlCheckboxGroupKeyValuePairs();
        testLibraryControlCheckboxGroupHorizontal();
        testLibraryControlCheckboxGroupDelimiter();
        testLibraryControlCheckboxGroupDirtyValidation();
        passed();
    }  
}
