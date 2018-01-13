/**
 * Copyright 2005-2018 The Kuali Foundation
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
public class LibraryClientResponsivenessProgressiveDisclosureAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ProgressiveDisclosureView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ProgressiveDisclosureView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }
    
    /**
     * //div[@data-parent='Demo-ProgressiveDisclosure-Example9']/div[@data-role='disclosureContent']
     */
    private static final String CWGR_GENERIC_XPATH= "//div[@data-parent='Demo-ProgressiveDisclosure-Example9']/div[@data-role='disclosureContent']";

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Progressive Disclosure");
    }

    protected void testClientResponsivenessProgressiveDisclosure() throws Exception {
         //Scenario-1
        waitAndClickByLinkText("Default");
        assertIsNotVisibleByXpath("//input[@name='inputField1']", "Is Visible");
        waitAndClickByName("booleanField1");
        waitIsVisibleByXpath("//input[@name='inputField1']","Not Visible");
        waitAndClickByName("booleanField1");
        assertIsNotVisibleByXpath("//input[@name='inputField1']", "Is Visible");
        jiraAwareTypeByName("inputField2", "show");
        waitForElementPresentByXpath("//input[@name='inputField3' and @disabled]");
        waitAndClickByLinkText("Documentation");
        waitForElementPresentByXpath("//input[@name='inputField3']");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureAjaxRetrieval() throws Exception {
        waitAndClickByLinkText("Ajax Retrieval");
        checkForIncidentReport("LibraryClientResponsivenessProgressiveDisclosureAft Ajax Retrieval");
        assertIsNotVisibleByXpath("//input[@name='inputField18']", "element");
        waitAndClickByName("booleanField2");
        waitIsVisibleByXpath("//input[@name='inputField18']", "element");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureRefreshWhenShown() throws Exception {
        waitAndClickByLinkText("Refresh when Shown");
        assertIsNotVisibleByXpath("//input[@name='inputField5']", "element");
        waitAndClickByName("booleanField3");
        waitIsVisibleByXpath("//input[@name='inputField5']", "element");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureShowFieldThroughMatching() throws Exception {
    	waitAndClickByLinkText("Show Field Through Matching");
//    	assertElementPresentByXpath("//input[@name='inputField7' and @disabled]");
//    	assertElementPresentByXpath("//input[@name='inputField8' and @disabled]");
    	waitAndTypeByName("inputField6","A");
    	waitAndClickByLinkText("Documentation");
    	waitForElementPresentByXpath("//input[@name='inputField7']");
    	waitAndTypeByName("inputField6","B");
    	waitAndClickByLinkText("Documentation");
    	waitForElementPresentByXpath("//input[@name='inputField8']");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureOfGroup() throws Exception {
    	waitAndClickByLinkText("Progressive Disclosure of Groups");
    	waitForElementPresentByXpath("//input[@name='inputField10' and @disabled]");
    	waitForElementPresentByXpath("//input[@name='inputField11' and @disabled]");
    	waitForElementPresentByXpath("//input[@name='inputField12' and @disabled]");
    	waitAndClickByXpath("//input[@name='inputField9' and @value='show1']");
    	waitForElementPresentByXpath("//input[@name='inputField10']");
    	waitForElementPresentByXpath("//input[@name='inputField11']");
    	waitForElementPresentByXpath("//input[@name='inputField12']");
    	waitAndClickByXpath("//input[@name='inputField9' and @value='show2']");
        waitForProgressLoading();
    	waitForElementPresentByXpath("//input[@name='inputField13']");
    	waitForElementPresentByXpath("//input[@name='inputField14']");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureConditionalRefresh() throws Exception {
    	waitAndClickByXpath("//li[@data-tabfor='Demo-ProgressiveDisclosure-Example6']/a[contains(text(),'Conditional Refresh')]");
    	waitAndClickByXpath("//input[@name='inputField15' and @value='show1']");
        waitForProgressLoading();
    	waitAndTypeByName("inputField16","Hello World!");
    	waitAndTypeByName("inputField17","Hello Deep!");
    	waitAndClickByXpath("//input[@name='inputField15' and @value='show2']");
        waitForProgressLoading();
    	waitForTextPresent("Hello Deep!");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureConditionalOptions() throws Exception {
    	waitAndClickByLinkText("Conditional Options");
    	selectByName("inputField19","Apples");
    	waitAndClickButtonByText("Refresh Group");
        waitForProgressLoading(15);
    	selectByName("inputField4","Vegetables");
    	// Test page gives exception after this step.
        waitAndClickButtonByText("Refresh Field");
        waitForProgressLoading(15);
        waitAndClickButtonByText("Refresh Field but with Server Errors");
        waitForTextPresent("Field 1: This field caused a server test error");
        waitAndClickButtonByText("Refresh Page");
        waitForProgressLoading(60);
        waitForTextNotPresent("Field 1: This field caused a server test error");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureRefreshBasedOnTimer() throws Exception {
    	waitAndClickByLinkText("Refresh Based on Timer");
    	//There are no component to perform test on the page.
        checkForIncidentReport();
    }
    
    protected void testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefresh() throws Exception {
    	waitAndClickByLinkText("Collection Group With Refresh");
        acceptAlertIfPresent();
        waitAndTypeByName("newCollectionLines['collection1'].field2", "ref");
        fireEvent("focus", "collection1[0].field1");
        waitForProgressLoading();
    	//Test cannot be written ahead as there is a freemarker error in page
        checkForIncidentReport();
    }
    
    protected void testClientResponsivenessProgressiveDisclosureFieldWithCheckBoxFieldset() throws Exception {
    	waitAndClickByLinkText("Field Within a Checkbox Field Set");
    	waitForElementPresentByXpath("//input[@name='inputField21' and @disabled]");
    	waitAndClickByXpath("//input[@name='checkboxesField1' and @value='1']");
    	waitForElementPresentByXpath("//input[@name='inputField21']");
        checkForIncidentReport();
    }
    
    protected void testClientResponsivenessProgressiveDisclosureFieldWithRadioFieldset() throws Exception {
    	waitAndClickByLinkText("Field Within a Radio Field Set");
    	waitForElementPresentByXpath("//input[@name='inputField23' and @disabled]");
    	waitAndClickByXpath("//input[@name='checkboxesField2' and @value='X']");
    	waitForElementPresentByXpath("//input[@name='inputField23']");
        checkForIncidentReport();
    }
    
    @Test
    public void testClientResponsivenessProgressiveDisclosureBookmark() throws Exception {
    	testClientResponsivenessProgressiveDisclosureAll();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureNav() throws Exception {
    	testClientResponsivenessProgressiveDisclosureAll();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureConditionalOptionsBookmark() throws Exception {
        testClientResponsivenessProgressiveDisclosureConditionalOptions();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureConditionalOptionsNav() throws Exception {
        testClientResponsivenessProgressiveDisclosureConditionalOptions();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefreshBookmark() throws Exception {
        testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefresh();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefreshNav() throws Exception {
        testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefresh();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureConditionalRefreshBookmark() throws Exception {
        testClientResponsivenessProgressiveDisclosureConditionalRefresh();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureConditionalRefreshNav() throws Exception {
        testClientResponsivenessProgressiveDisclosureConditionalRefresh();
        passed();
    }

    private void testClientResponsivenessProgressiveDisclosureAll() throws Exception {
        testClientResponsivenessProgressiveDisclosure();
    	testClientResponsivenessProgressiveDisclosureAjaxRetrieval();
        testClientResponsivenessProgressiveDisclosureRefreshWhenShown();
        testClientResponsivenessProgressiveDisclosureOfGroup();
        testClientResponsivenessProgressiveDisclosureRefreshBasedOnTimer();
        testClientResponsivenessProgressiveDisclosureShowFieldThroughMatching();
//        testClientResponsivenessProgressiveDisclosureConditionalRefresh();
//        testClientResponsivenessProgressiveDisclosureCollectionWithGroupRefresh();
//        testClientResponsivenessProgressiveDisclosureConditionalOptions();
        testClientResponsivenessProgressiveDisclosureFieldWithCheckBoxFieldset();
        testClientResponsivenessProgressiveDisclosureFieldWithRadioFieldset();
    }
}
