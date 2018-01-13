/**
 * Copyright 2005-2018 The Kuali Foundation
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
public class LibraryCollectionFeaturesDeleteLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionDeleteLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionDeleteLineView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Delete Line");
    }

    protected void testCollectionFeaturesDeleteLineOnNewLine() throws Exception {
        selectByName("exampleShown","Delete Line on New Lines");
        if(isElementPresentByXpath("//button[contains(text(),'Delete')]")) {
            fail("Delete button should not be present.");
        }
        waitAndTypeByXpath("//section[@id='Demo-CollectionDeleteLine-Example1']/section/div/div/table/tbody/tr[1]/td[2]/div/input","121");
        waitAndTypeByXpath("//section[@id='Demo-CollectionDeleteLine-Example1']/section/div/div/table/tbody/tr[1]/td[3]/div/input","55");
        waitAndClickButtonByText("Add");
        waitForElementPresentByXpath("//input[@name='collection1_5[0].field1' and @value='121']");
        assertElementPresentByXpath("//input[@name='collection1_5[0].field2' and @value='55']");
        assertElementPresentByXpath("//button[contains(text(),'Delete')]");
        waitAndClickButtonByText("Delete");
        waitForProgress("Deleting Line...");
        waitForTextPresent("You have deleted an item from Delete line action on newly added lines.");
        if(isElementPresentByXpath("//button[contains(text(),'Delete')]")) {
            fail("Delete button should not be present.");
        }
    }

    @Test
    public void testCollectionFeaturesDeleteLineBookmark() throws Exception {
        testCollectionFeaturesDeleteLineOnNewLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesDeleteLineNav() throws Exception {
        testCollectionFeaturesDeleteLineOnNewLine();
        passed();
    }  
}
