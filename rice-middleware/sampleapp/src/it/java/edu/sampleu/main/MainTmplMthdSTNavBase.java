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
package edu.sampleu.main;

import edu.sampleu.common.NavTemplateMethodAftBase;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MainTmplMthdSTNavBase extends NavTemplateMethodAftBase {

    public static final String DATA_TABLES_INFO = "dataTables_info";

    @Override
    protected String getCreateNewLinkLocator() {
        return "Create New";
    }

    @Override
    protected String getMenuLinkLocator() {
        return "Main Menu";
    }

    protected void testSearchEditBack(JiraAwareFailable failable) throws Exception {
        waitAndClickSearch3();
        waitForElementPresentByClassName(DATA_TABLES_INFO);
        String pageBannerText = getText(By.className(DATA_TABLES_INFO));
        waitAndClickEdit();
        waitForElementPresentByClassName("uif-headerText-span");
        back();
        waitForElementPresentByClassName(DATA_TABLES_INFO);
        if (!pageBannerText.equals(getText(By.className(DATA_TABLES_INFO)))) {
            failable.fail("https://jira.kuali.org/browse/KULRICE-9709 Search Edit Back does not show search results in Firefox");
        }
    }

    protected void testSearchSearchBack(JiraAwareFailable failable, String fieldName, String searchText) throws Exception {
        waitAndClickSearch3();
        waitForElementPresentByClassName(DATA_TABLES_INFO);
        String pageBannerText = getText(By.className(DATA_TABLES_INFO));
        waitAndTypeByName(fieldName, searchText);
        waitAndClickSearch3();
        waitForElementPresentByClassName(DATA_TABLES_INFO, "Problem with second search");
        back();
        acceptAlertIfPresent();
        waitForElementsPresentByClassName(DATA_TABLES_INFO, "Going back from Search to Search results not available");
        assertTextPresent("https://jira.kuali.org/browse/KULRICE-9710 Search Search back does not display first search results in Firefox", pageBannerText);
    }
}
