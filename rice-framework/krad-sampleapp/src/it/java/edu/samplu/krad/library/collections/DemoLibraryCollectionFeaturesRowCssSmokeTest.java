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
package edu.samplu.krad.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.SmokeTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryCollectionFeaturesRowCssSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutRowCss-View
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutRowCss-View";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Row Css");
    }

    protected void testCollectionFeaturesRowCss() throws Exception {
      assertElementPresentByXpath("//table/tbody/tr[@class='demo-odd demo-all odd']");
      assertElementPresentByXpath("//table/tbody/tr[@class='demo-even demo-all even']");
    }
    
    @Test
    public void testCollectionFeaturesRowCssBookmark() throws Exception {
        testCollectionFeaturesRowCss();
        passed();
    }

    @Test
    public void testCollectionFeaturesRowCssNav() throws Exception {
        testCollectionFeaturesRowCss();
        passed();
    }  
}