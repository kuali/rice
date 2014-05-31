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
package org.kuali.rice.krad.demo.uif.library.general;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoGeneralFeaturesReadOnlyAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-UnifiedHeaderView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ReadOnlyView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Read Only");
    }

    protected void testGeneralFeaturesReadOnly() throws Exception {
        jGrowl("Click Read Only Example link");
        waitAndClickByXpath("//header[@data-header_for=\"Demo-ReadOnly-Example1\"]/following-sibling::a");
        switchToWindow("Kuali :: ReadOnly Test");
        waitForElementPresentByXpath("//input[@name='inputField1']");
        waitForElementPresentByXpath("//textarea[@name='inputField2']");
        waitForElementPresentByXpath("//select[@name='inputField3']");
        waitForElementPresentByXpath("//select[@name='inputField4']");
        waitForElementPresentByXpath("//input[@name='inputField5']");
        waitForElementPresentByXpath("//input[@name='booleanField1']");
        waitForElementPresentByXpath("//input[@name='inputField6']");
        waitForElementPresentByXpath("//input[@name='inputField7']");
        waitForElementPresentByXpath("//select[@name='inputField8']");
        waitForElementPresentByXpath("//input[@name='inputField9']");
        waitForElementPresentByXpath("//input[@name='checkboxesField1']");
        waitForElementPresentByXpath("//input[@name='checkboxesField2']");
        waitAndClickButtonByText("Make ReadOnly");
        waitForElementNotPresent(By.xpath("//input[@name='inputField1']"));
        waitForElementNotPresent(By.xpath("//textarea[@name='inputField2']"));
        waitForElementNotPresent(By.xpath("//select[@name='inputField3']"));
        waitForElementNotPresent(By.xpath("//select[@name='inputField4']"));
        waitForElementNotPresent(By.xpath("//input[@name='inputField5']"));
        waitForElementNotPresent(By.xpath("//input[@name='booleanField1']"));
        waitForElementNotPresent(By.xpath("//input[@name='inputField6']"));
        waitForElementNotPresent(By.xpath("//input[@name='inputField7']"));
        waitForElementNotPresent(By.xpath("//select[@name='inputField8']"));
        waitForElementNotPresent(By.xpath("//input[@name='inputField9']"));
        waitForElementNotPresent(By.xpath("//input[@name='checkboxesField1']"));
        waitForElementNotPresent(By.xpath("//input[@name='checkboxesField2']"));
        waitForTextPresent("false");
        waitForTextPresent("No Options Selected");
        driver.close();
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesReadOnlyBookmark() throws Exception {
        testGeneralFeaturesReadOnly();
        passed();
    }

    @Test
    public void testGeneralFeaturesReadOnlyNav() throws Exception {
        testGeneralFeaturesReadOnly();
        passed();
    }  
}
