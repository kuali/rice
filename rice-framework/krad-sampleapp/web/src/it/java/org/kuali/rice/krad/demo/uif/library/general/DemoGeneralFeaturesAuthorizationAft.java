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
public class DemoGeneralFeaturesAuthorizationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AuthorizationView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AuthorizationView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Authorization");
    }

    protected void testGeneralFeaturesAuthorizationSecureGroupView() throws Exception {
    	selectByName("exampleShown","Secure Group View");
        waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example1']/div/div[@id='Uif-BreadcrumbWrapper']");
    }
    
    protected void testGeneralFeaturesAuthorizationSecureGroupEdit() throws Exception {
    	selectByName("exampleShown","Secure Group Edit");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example2']/div/div/label");
    }
    
    protected void testGeneralFeaturesAuthorizationSecureFields() throws Exception {
    	selectByName("exampleShown","Secure Fields");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example3']/div/div/input");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example3']/div/div[2]/input"));
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example3']/div/div/label[contains(text(),'Field 2:')]"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureFieldGroup() throws Exception {
    	selectByName("exampleShown","Secure Fields");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example4']/div/div/input");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example4']/div/div[2]/input"));
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example4']/div/div/label[contains(text(),'Field 2:')]"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureActions() throws Exception {
    	selectByName("exampleShown","Secure Actions");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example5']/div/button[contains(text(),'Close')]");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example5']/div/button[contains(text(),'Save')]"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureWidgets() throws Exception {
    	selectByName("exampleShown","Secure Widgets");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example6']/div/div/input");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example6']/div/div/a"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureLineView() throws Exception {
    	selectByName("exampleShown","Secure Line View");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example7']/div/table");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example7']/div/table[2]"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureLineEdit() throws Exception {
    	selectByName("exampleShown","Secure Line Edit");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example8']/div/table/tbody/tr[2]/td[2]/div[contains(text(),'A')]");
    	waitAndClickByXpath("//section[@id='Demo-Authorization-Example8']/div/table/tbody/tr[2]/td[6]/div/fieldset/div/button");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example8']/div/table/tbody/tr[2]/td[2]/div[contains(text(),'A')]"));
    }
    
    protected void testGeneralFeaturesAuthorizationSecureLineFields() throws Exception {
    	selectByName("exampleShown","Secure Line Fields");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example9']/div/table/tbody/tr[2]/td[4]/div[contains(text(),'C')]");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example9']/div/table/tbody/tr[2]/td[3]/div[@style='display: none;']");
    }
    
    protected void testGeneralFeaturesAuthorizationSecureLineActions() throws Exception {
    	selectByName("exampleShown","Secure Line Actions");
    	waitForElementNotPresent(By.xpath("//section[@id='Demo-Authorization-Example10']/div/table/tbody/tr[2]/td[6]/div/fieldset/div/button"));
    }
    
    protected void testGeneralFeaturesAuthorizationEditModes() throws Exception {
    	selectByName("exampleShown","Edit Modes");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example11']/section/div[2]/input");
    	waitForElementPresentByXpath("//section[@id='Demo-Authorization-Example11']/section/div[2]/input");
    }
    
    @Test
    public void testGeneralFeaturesAuthorizationBookmark() throws Exception {
    	testGeneralFeaturesAuthorizationAll();
    }

    @Test
    public void testGeneralFeaturesAuthorizationNav() throws Exception {
    	testGeneralFeaturesAuthorizationAll();
    }  
    
    private void testGeneralFeaturesAuthorizationAll() throws Exception {
    	testGeneralFeaturesAuthorizationSecureGroupView();
    	testGeneralFeaturesAuthorizationSecureGroupEdit();
    	testGeneralFeaturesAuthorizationSecureFields();
    	testGeneralFeaturesAuthorizationSecureFieldGroup();
    	testGeneralFeaturesAuthorizationSecureActions();
    	testGeneralFeaturesAuthorizationSecureWidgets();
    	testGeneralFeaturesAuthorizationSecureLineView();
    	testGeneralFeaturesAuthorizationSecureLineEdit();
    	testGeneralFeaturesAuthorizationSecureLineFields();
    	testGeneralFeaturesAuthorizationSecureLineActions();
    	testGeneralFeaturesAuthorizationEditModes();
        passed();
    }
}
