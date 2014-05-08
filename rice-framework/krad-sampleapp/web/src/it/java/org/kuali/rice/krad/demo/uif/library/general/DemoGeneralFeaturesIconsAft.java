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
public class DemoGeneralFeaturesIconsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-IconsView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-IconsView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Icons");
    }

    protected void testGeneralFeaturesIcons() throws Exception {
        waitForElementPresentByXpath("//label[@class='clearfix icon-home uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-home2 uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-home3 uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-office uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-newspaper uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-pencil uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-pencil2 uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-quill uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-pen uif-boxLayoutVerticalItem pull-left clearfix']");
        waitForElementPresentByXpath("//label[@class='clearfix icon-blog uif-boxLayoutVerticalItem pull-left clearfix']");
    }
    
    @Test
    public void testGeneralFeaturesIconsBookmark() throws Exception {
        testGeneralFeaturesIcons();
        passed();
    }

    @Test
    public void testGeneralFeaturesIconsNav() throws Exception {
        testGeneralFeaturesIcons();
        passed();
    }  
}
