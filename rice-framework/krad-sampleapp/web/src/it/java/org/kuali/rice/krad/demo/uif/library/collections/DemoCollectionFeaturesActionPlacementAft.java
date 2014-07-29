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
public class DemoCollectionFeaturesActionPlacementAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionActionPlacementView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionActionPlacementView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Action Placement");
    }

    protected void testCollectionFeaturesActionPlacement() throws Exception {
    	waitForElementPresentByXpath("//table/tbody/tr[1]/td[1]/div/fieldset/div/button");
    	waitForElementPresentByXpath("//table/tbody/tr[1]/td[2]/div/fieldset/div/button");
    }
    
    @Test
    public void testCollectionFeaturesActionPlacementBookmark() throws Exception {
        testCollectionFeaturesActionPlacement();
        passed();
    }

    @Test
    public void testCollectionFeaturesActionPlacementNav() throws Exception {
        testCollectionFeaturesActionPlacement();
        passed();
    }  
}