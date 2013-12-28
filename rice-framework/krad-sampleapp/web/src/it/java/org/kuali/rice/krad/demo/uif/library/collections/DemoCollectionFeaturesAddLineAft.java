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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesAddLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionAddLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionAddLineView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Add Line");
    }

    protected void testCollectionFeaturesDefaultAddLine() throws Exception {
        waitAndTypeByXpath("//div[@id='Demo-CollectionAddLine-Example1']/div[2]/div[2]/div/table/tbody/tr[1]/td[2]/div/input","12");
        waitAndTypeByXpath("//div[@id='Demo-CollectionAddLine-Example1']/div[2]/div[2]/div/table/tbody/tr[1]/td[3]/div/input","5");
        waitAndClickButtonByText("add");
        assertElementPresentByXpath("//input[@name='collection1[0].field1' and @value='12']");
        assertElementPresentByXpath("//input[@name='collection1[0].field2' and @value='5']");
    }
    
    protected void testCollectionFeaturesDefaultAddViaLightbox() throws Exception {
        selectByName("exampleShown","Collection Add Via Lightbox");
        waitAndClickButtonByText("Add Line");
        waitAndTypeByXpath("//form[@class='uif-lightbox']/div/table/tbody/tr/td/div/input","12");
        waitAndTypeByXpath("//form[@class='uif-lightbox']/div/table/tbody/tr[2]/td/div/input","5");
        waitAndClickByXpath("//form[@class='uif-lightbox']/div/div[2]/button");
        waitForElementPresentByXpath("//input[@name='collection1_2[0].field1' and @value='12']");
        waitForElementPresentByXpath("//input[@name='collection1_2[0].field2' and @value='5']");
    }
    
    protected void testCollectionFeaturesDefaultAddBlankLine() throws Exception {
        selectByName("exampleShown","Collection Add Blank Line");
        waitAndClickByXpath("//div[@id='Demo-CollectionAddLine-Example3']/div[2]/div[2]/button");
        assertElementPresentByXpath("//input[@name='collection1_4[0].field1' and @value]");
        assertElementPresentByXpath("//input[@name='collection1_4[0].field2' and @value]");
    }
    
    @Test
    public void testCollectionFeaturesAddLineBookmark() throws Exception {
        testCollectionFeaturesDefaultAddLine();
        testCollectionFeaturesDefaultAddViaLightbox();
        testCollectionFeaturesDefaultAddBlankLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineNav() throws Exception {
        testCollectionFeaturesDefaultAddLine();
        testCollectionFeaturesDefaultAddViaLightbox();
        testCollectionFeaturesDefaultAddBlankLine();
        passed();
    }  
}
