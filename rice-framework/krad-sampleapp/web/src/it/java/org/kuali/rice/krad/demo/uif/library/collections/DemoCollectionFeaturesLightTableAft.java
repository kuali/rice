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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesLightTableAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LightTableView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LightTableView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Light Table");
    }

    protected void testCollectionFeaturesLightTableBasicData() throws Exception {
        waitAndClickByLinkText("Basic Data");
        waitAndClickByLinkText("Basic DataFields LightTable");
        switchToWindow("Kuali :: Light Table test");
        waitForElementPresentByXpath("//table[@id='Demo-LightTableGroup1_lightTable']/thead/tr/th[@aria-sort='ascending']");
        switchToWindow("Kuali");
    }
    
    protected void testCollectionFeaturesLightTableInquiryLinkActions() throws Exception {
        waitAndClickByLinkText("Inquiry, Links, and Actions");
        waitAndClickByXpath("//section[@id='Demo-LightTable-Example2']/a");
        switchToWindow("Kuali :: Light Table test");
        waitForElementPresentByXpath("//table[@id='Demo-LightTableGroup2_lightTable']/tbody/tr/td/div/a");
        waitForElementPresentByXpath("//table[@id='Demo-LightTableGroup2_lightTable']/tbody/tr/td[5]/div/fieldset/div/button");
        switchToWindow("Kuali");
     }
    
    protected void testCollectionFeaturesLightTableConditionalRender() throws Exception {
        waitAndClickByLinkText("Conditional Render");
        waitAndClickByXpath("//section[@id='Demo-LightTable-Example3']/a");
        switchToWindow("Kuali :: Light Table test");
        waitForElementPresentByXpath("//table[@id='Demo-LightTableGroup3_lightTable']/tbody/tr/td[3]");
        switchToWindow("Kuali");
    }
    
    protected void testCollectionFeaturesLightTableBasicInput() throws Exception {
        waitAndClickByLinkText("Basic Input");
        waitAndClickByLinkText("Basic Inputs");
        switchToWindow("Kuali :: Light Table test");
        waitForElementPresentByXpath("//table[@id='Demo-LightTableGroup4_lightTable']/tbody/tr/td/div/input");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup4_lightTable']/tbody/tr/td[3]/div/input[@type='checkbox']");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup4_lightTable']/tbody/tr/td[4]/div/select");
        switchToWindow("Kuali");
    }
    
    @Test
    public void testCollectionFeaturesLightTableBasicDataBookmark() throws Exception {
        testCollectionFeaturesLightTableBasicData();
        passed();
    }
    
    @Test
    public void testCollectionFeaturesLightTableInquiryLinkActionsBookmark() throws Exception {
        testCollectionFeaturesLightTableInquiryLinkActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesLightTableConditionalRenderBookmark() throws Exception {
        testCollectionFeaturesLightTableConditionalRender();
        passed();
    }
    
    @Test
    public void testCollectionFeaturesLightTableBasicInputBookmark() throws Exception {
        testCollectionFeaturesLightTableBasicInput();
        passed();
    }

    @Test
    public void testCollectionFeaturesLightTableBasicDataNav() throws Exception {
        testCollectionFeaturesLightTableBasicData();
        passed();
    }
    
    @Test
    public void testCollectionFeaturesLightTableInquiryLinkActionsNav() throws Exception {
        testCollectionFeaturesLightTableInquiryLinkActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesLightTableConditionalRenderNav() throws Exception {
        testCollectionFeaturesLightTableConditionalRender();
        passed();
    }
    
    @Test
    public void testCollectionFeaturesLightTableBasicInputNav() throws Exception {
        testCollectionFeaturesLightTableBasicInput();
        passed();
    }
}
