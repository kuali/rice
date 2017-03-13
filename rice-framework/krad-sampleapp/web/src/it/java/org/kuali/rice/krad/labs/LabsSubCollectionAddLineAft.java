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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsSubCollectionAddLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableField
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-AddLineTest";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Sub-collection Add Line");
    }

    protected void testSubCollectionAddLine() throws InterruptedException {
        waitAndClickByXpath("/html/body/form/div/div[2]/main/section/div/button");
        waitAndTypeByName("newCollectionLines['collection5'].field1", "test1");
        waitAndClickButtonByExactText("Add");
        waitForElementPresentByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[1]/td/div/input[@value='test1']");

        // TODO input by field name (similar to waitAndTypeLabeledInput
        waitAndTypeByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div/div[1]/input","test2");
        jGrowl("Type test2 into second field");
        waitAndClickByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div/div[2]/button");
        waitForElementPresentByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[1]/input[@value='test2']");

//        waitAndClickButtonByText("Add Line"); // first add line button
        jGrowl("Click Add Line Button");
        waitAndClickByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[2]/fieldset/section/div/button");

        jGrowl("Verify column added");
        waitForElementPresentByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[2]/fieldset/section/div/div/table/tbody/tr/td[2]/div/input");
        waitForElementPresentByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[2]/fieldset/section/div/div/table/tbody/tr/td[3]/div/input");
        waitForElementPresentByXpath("/html/body/form/div/div[2]/main/section/div/div[1]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[2]/fieldset/section/div/div/table/tbody/tr/td[4]/div/input");
    }

    @Test
    public void testSubCollectionAddLineBookmark() throws Exception {
    	testSubCollectionAddLine();
        passed();
    }

    @Test
    public void testSubCollectionAddLineNav() throws Exception {
    	testSubCollectionAddLine();
        passed();
    }
}
