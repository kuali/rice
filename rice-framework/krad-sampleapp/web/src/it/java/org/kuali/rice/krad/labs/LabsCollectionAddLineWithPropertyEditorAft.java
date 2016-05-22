/**
 * Copyright 2005-2016 The Kuali Foundation
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
import org.openqa.selenium.WebElement;

/**
 * This class test the functionality of property editors in collection field inputs
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsCollectionAddLineWithPropertyEditorAft extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL = "/kr-krad/travelAccountCollection?viewId=Lab-CollectionAddLineWithPropertyEditor";

    // Travel Account Collection Table with add line
    By acctNameInputLoc = By.xpath("//table/tbody/tr/td[2]/div/input");
    By acctNumInputLoc = By.xpath("//table/tbody/tr/td[3]/div/div/input");
    By acctTypeCdInputLoc = By.xpath("//table/tbody/tr/td[4]/div/div/input");
    By addButtonLoc = By.id("Lab-CollectionAddLineWithPropertyEditor-Table_add");


    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Collection Add Line: Using Property Editor");
    }

    protected void testDemoCollectionAddLineWithPropertyEditor() throws InterruptedException {
        waitAndType(acctNameInputLoc,"testcase");
        waitAndType(acctNumInputLoc,"asd");
        waitAndType(acctTypeCdInputLoc,"CAT");
        waitAndClick(addButtonLoc);
        waitForTextPresent("tes-tcase");
    }

    @Test
    public void testDemoCollectionAddLineWithPropertyEditorBookmark() throws Exception {
        testDemoCollectionAddLineWithPropertyEditor();
        passed();
    }

    @Test
    public void testDemoCollectionAddLineWithPropertyEditorNav() throws Exception {
        testDemoCollectionAddLineWithPropertyEditor();
        passed();
    }


}
