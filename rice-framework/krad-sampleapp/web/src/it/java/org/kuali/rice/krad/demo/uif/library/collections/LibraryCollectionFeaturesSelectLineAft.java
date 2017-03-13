/**
 * Copyright 2005-2017 The Kuali Foundation
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
public class LibraryCollectionFeaturesSelectLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionSelectLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionSelectLineView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Select Line");
    }

    protected void testCollectionFeaturesSelectLine() throws Exception {
        selectByName("exampleShown", "Line Selection");

        waitAndClickByXpath("//input[@name='collection1[0].bfield']");
        assertElementPresentByXpath("//input[@name='collection1[0].bfield' and @value='collection1[0]']");

        waitAndClickByXpath("//input[@name='collection1[2].bfield']");
        assertElementPresentByXpath("//input[@name='collection1[2].bfield' and @value='collection1[2]']");
    }

    protected void testCollectionFeaturesSelectLineExpressionVariable() throws Exception {
        selectByName("exampleShown", "Line Selection with Expression Variable");

        waitAndClickByXpath("//input[@name='collection2[0].bfield']");
        assertElementPresentByXpath("//input[@name='collection2[0].bfield' and @value='0']");

        waitAndClickByXpath("//input[@name='collection2[2].bfield']");
        assertElementPresentByXpath("//input[@name='collection2[2].bfield' and @value='2']");
    }

    @Test
    public void testCollectionFeaturesSelectLineBookmark() throws Exception {
        testCollectionFeaturesSelectLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesSelectLineExpressionVariableBookmark() throws Exception {
        testCollectionFeaturesSelectLineExpressionVariable();
        passed();
    }

    @Test
    public void testCollectionFeaturesSelectLineNav() throws Exception {
        testCollectionFeaturesSelectLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesSelectLineExpressionVariableNav() throws Exception {
        testCollectionFeaturesSelectLineExpressionVariable();
        passed();
    }

}
