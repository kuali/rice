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
public class DemoGeneralFeaturesUnifiedViewHeaderAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-UnifiedHeaderView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-UnifiedHeaderView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Unified View Header");
    }

    protected void testGeneralFeaturesUnifiedHeader() throws Exception {
        selectByName("exampleShown", "Unified Header");
        waitAndClickByLinkText("Unified example");
        switchToWindow("Kuali :: View Header");
        waitForElementPresent(By.xpath("//h1/span[@class='uif-headerText-span']"));
        assertElementPresentByXpath("//h1/span[2]/p[@class='uif-viewHeader-supportTitle']");
        driver.close();
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesNonUnifiedHeader() throws Exception {
        selectByName("exampleShown", "Non-Unified Header");
        waitAndClickByLinkText("Non-Unified example");
        switchToWindow("Kuali :: View Header");
        waitForElementPresent(By.xpath("//h1/span[@class='uif-headerText-span']"));
        assertElementPresentByXpath("//h2/span[@class='uif-headerText-span']");
        driver.close();
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesUnifiedHeaderOptions() throws Exception {
        selectByName("exampleShown", "Options");
        waitAndClickByLinkText("Additional unified header options");
        switchToWindow("Kuali :: View Header");
        assertTextPresent(new String[]{"metadata information", "Custom Support/Page title"});
        assertElementPresentByXpath("//h1/p[@class='uif-viewHeader-areaTitle']");
        assertElementPresentByXpath("//h1/span[@class='uif-headerText-span']");
        driver.close();
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeaderBookmark() throws Exception {
        testGeneralFeaturesUnifiedHeaderOptions();
        testGeneralFeaturesUnifiedHeader();
        testGeneralFeaturesNonUnifiedHeader();
        passed();
    }

    @Test
    public void testGeneralFeaturesUnifiedViewHeaderNav() throws Exception {
        testGeneralFeaturesUnifiedHeaderOptions();
        testGeneralFeaturesUnifiedHeader();
        testGeneralFeaturesNonUnifiedHeader();
        passed();
    }  
}
