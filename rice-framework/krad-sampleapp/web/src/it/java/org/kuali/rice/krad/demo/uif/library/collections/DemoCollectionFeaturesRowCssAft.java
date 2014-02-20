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
public class DemoCollectionFeaturesRowCssAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutRowCssView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutRowCssView";

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
      assertElementPresentByXpath("//table/tbody/tr[@class='demo-all demo-odd odd']");
      assertElementPresentByXpath("//table/tbody/tr[@class='demo-all demo-even even']");
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
