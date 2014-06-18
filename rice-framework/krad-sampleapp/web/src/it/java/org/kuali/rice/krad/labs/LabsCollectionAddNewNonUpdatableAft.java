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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsCollectionAddNewNonUpdatableAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/travelAccountCollection?viewId=Lab-CollectionAddLineNonUpdateableRefreshes
     */
    public static final String BOOKMARK_URL = "/kr-krad/travelAccountCollection?viewId=Lab-CollectionAddLineNonUpdateableRefreshes";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Collection Add Line: Non-updateable Refreshes");
    }

    protected void testDemoCollectionAddNewNonUpdatable() throws InterruptedException {
    	waitAndTypeByXpath("//table/tbody/tr/td[2]/div/input","asd");
    	waitAndTypeByXpath("//table/tbody/tr/td[3]/div/div/input","asd");
    	waitAndTypeByXpath("//table/tbody/tr/td[4]/div/div/input","CAT");
    	waitAndClickByXpath("//button[@id='Lab-NonUpdateableRefreshes-Table_add']");
    	waitForElementNotPresent(By.xpath("//button[contains(text(),'Update')]"));
    }

    @Test
    public void testDemoCollectionAddNewNonUpdatableBookmark() throws Exception {
    	testDemoCollectionAddNewNonUpdatable();
        passed();
    }

    @Test
    public void testDemoCollectionAddNewNonUpdatableNav() throws Exception {
    	testDemoCollectionAddNewNonUpdatable();
        passed();
    }
}
