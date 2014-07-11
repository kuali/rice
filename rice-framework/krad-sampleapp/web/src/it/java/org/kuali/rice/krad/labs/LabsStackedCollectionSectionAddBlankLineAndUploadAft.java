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
public class LabsStackedCollectionSectionAddBlankLineAndUploadAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-LayoutTest-Stacked-Upload-AddBlankLine
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-LayoutTest-Stacked-Upload-AddBlankLine";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Stacked Collection Section w/ AddBlankLine and upload");
    }

    protected void testStackedCollectionSectionAddBlankLineAndUpload() throws InterruptedException {
    	waitForElementPresentByXpath("//input[@name='collection2[0].field1' and @value='A']");
    	waitAndClickButtonByExactText("Add Line");
    	waitForElementPresentByXpath("//input[@name='collection2[0].field1' and @value='']");
    	waitForElementPresentByXpath("//input[@type='file' and @name='collection2[0].fileUpload']");
    }

    @Test
    public void testStackedCollectionSectionAddBlankLineAndUploadBookmark() throws Exception {
    	testStackedCollectionSectionAddBlankLineAndUpload();
        passed();
    }

    @Test
    public void testStackedCollectionSectionAddBlankLineAndUploadNav() throws Exception {
    	testStackedCollectionSectionAddBlankLineAndUpload();
        passed();
    }
}
