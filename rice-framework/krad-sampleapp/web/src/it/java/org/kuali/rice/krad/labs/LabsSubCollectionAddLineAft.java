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
    	waitAndTypeByXpath("//section/div/div/table/tbody/tr/td/div/input","a");
    	waitAndClickByXpath("//section/div/div/div/button[contains(text(),'add')]");
    	waitForElementPresentByXpath("//section/div/div[2]/table/tbody/tr/td/div/input[@value='a']");
    	waitAndClickByXpath("//section/div/div[2]/table/tbody/tr[2]/td/div/fieldset/section/div/button[contains(text(),'Add Line')]");
    	waitAndTypeByXpath("//form[@id='kualiLightboxForm']/div/div/input","b");
    	waitAndClickByXpath("//form[@id='kualiLightboxForm']/div/div[2]/button[contains(text(),'add')]");
    	waitForElementPresentByXpath("//section/div/div[2]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div/input[@value='b']");
    	waitAndClickByXpath("//section/div/div[2]/table/tbody/tr[2]/td/div/fieldset/section/div/div[2]/div[2]/fieldset/section/div/button[contains(text(),'Add Line')]");
    	waitForElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']");
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
