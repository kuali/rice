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
 * Like LabsEnterKey test for this screen, but testing that the UI adds as well.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsEntryKeySupportTablesAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-LayoutTest-EnterKeyTables
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-LayoutTest-EnterKeyTables";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Enter Key Support - Tables");
    }

    protected void testDemoEntryKeySupportTables() throws InterruptedException {
    	waitAndTypeByXpath("//table/tbody/tr/td[2]/div/input","1");
    	waitAndTypeByXpath("//table/tbody/tr/td[3]/div/input","1");
    	waitAndClickButtonByText("Add");
    	waitAndClickButtonByText("Delete");
    }

    @Test
    public void testDemoEntryKeySupportTablesBookmark() throws Exception {
    	testDemoEntryKeySupportTables();
        passed();
    }

    @Test
    public void testDemoEntryKeySupportTablesNav() throws Exception {
    	testDemoEntryKeySupportTables();
        passed();
    }
}
