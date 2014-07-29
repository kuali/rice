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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsDataTableAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DataTableView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DataTableView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Data Table");
    }

    protected void testLibraryElementsDataTable() throws Exception {
        waitForElementPresentByXpath("//div[@id='Demo-DataTable-Example1']/div[2]/div/div[2]/table/tbody/tr/td/span");
        waitForElementPresentByXpath("//div[@class='dataTables_scrollBody']");
    }
    
    @Test
    public void testElementsDataTableBookmark() throws Exception {
        testLibraryElementsDataTable();
        passed();
    }

    @Test
    public void testElementsDataTableNav() throws Exception {
        testLibraryElementsDataTable();
        passed();
    }  
}
