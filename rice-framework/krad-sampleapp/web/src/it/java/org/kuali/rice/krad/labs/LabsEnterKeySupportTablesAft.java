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
public class LabsEnterKeySupportTablesAft extends WebDriverLegacyITBase {

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

    protected void testDemoEnterKeySupportTables() throws InterruptedException {
    	waitAndTypeByXpath("//table/tbody/tr/td[2]/div/input","1");
    	waitAndTypeByXpath("//table/tbody/tr/td[3]/div/input","1");
    	pressEnterByXpath("//table/tbody/tr/td[3]/div/input");
    	waitAndClickButtonByText("Delete");
    }

    @Test
    public void testDemoEnterKeySupportTablesBookmark() throws Exception {
    	testDemoEnterKeySupportTables();
        passed();
    }

    @Test
    public void testDemoEnterKeySupportTablesNav() throws Exception {
    	testDemoEnterKeySupportTables();
        passed();
    }
}
