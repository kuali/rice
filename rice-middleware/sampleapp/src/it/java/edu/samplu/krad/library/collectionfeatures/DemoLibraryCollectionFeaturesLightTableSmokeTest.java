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
package edu.samplu.krad.library.collectionfeatures;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryCollectionFeaturesLightTableSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LightTable-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LightTable-View&methodToCall=start";

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
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup1_lightTable']/thead/tr/th[@aria-sort='ascending']");
        switchToWindow("Kuali");
    }
    
    protected void testCollectionFeaturesLightTableInquiryLinkActions() throws Exception {
        waitAndClickByLinkText("Inquiry, Links, and Actions");
        waitAndClickByXpath("//div[@id='Demo-LightTable-Example2']/div[@class='uif-verticalBoxLayout clearfix']/a");
        switchToWindow("Kuali :: Light Table test");
        switchToWindow("Kuali :: Light Table test");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup2_lightTable']/tbody/tr/td/div/span/a");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup2_lightTable']/tbody/tr/td[5]/div/fieldset/div/div/button");
        switchToWindow("Kuali");
     }
    
    protected void testCollectionFeaturesLightTableConditionalRender() throws Exception {
        waitAndClickByLinkText("Conditional Render");
        waitAndClickByXpath("//div[@id='Demo-LightTable-Example3']/div[@class='uif-verticalBoxLayout clearfix']/a");
        switchToWindow("Kuali :: Light Table test");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup3_lightTable']/tbody/tr/td[3]");
        switchToWindow("Kuali");
    }
    
    protected void testCollectionFeaturesLightTableBasicInput() throws Exception {
        waitAndClickByLinkText("Basic Input");
        waitAndClickByLinkText("Basic Inputs");
        switchToWindow("Kuali :: Light Table test");
        assertElementPresentByXpath("//table[@id='Demo-LightTableGroup4_lightTable']/tbody/tr/td/div/input");
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