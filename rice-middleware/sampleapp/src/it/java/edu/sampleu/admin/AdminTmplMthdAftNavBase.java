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
package edu.sampleu.admin;

import edu.sampleu.common.NavTemplateMethodAftBase;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AdminTmplMthdAftNavBase extends NavTemplateMethodAftBase {

    public static final String CREATE_NEW_LOCATOR = "//img[contains(@alt,'create new')]";
//    public static final String CREATE_NEW_LOCATOR = "//a[contains(@href,'command=initiate')]"; // not with IE8
//    public static final String CREATE_NEW_LOCATOR = "//a[@title='Create a new record']"; // not with IE8
    public static final String ADMIN_LOCATOR = "Administration";
    public static final String SPAN_CLASS_PAGEBANNER = "//span[@class='pagebanner']";

    @Override
    protected String getCreateNewLinkLocator() {
        return CREATE_NEW_LOCATOR;
    }

    @Override
    protected String getMenuLinkLocator() {
        return ADMIN_LOCATOR;
    }

    /**
     * tests that a getLinkLocator maintenance document can be cancelled
     */
    public void testCreateNewCancelNav() throws Exception {
        gotoCreateNew();
        testCancelConfirmation();
    }

    /**
     * tests that a getLinkLocator maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditCancel() throws Exception {
        testSearchEditCancel();
    }

    public void testSearchEditBack(JiraAwareFailable failable) throws Exception {
        waitAndClickSearch2();
        waitAndClickByLinkText("edit");
        waitFor(By.name(BLANKET_APPROVE_NAME));
        back();
        waitForTextPresent(getTextByXpath(SPAN_CLASS_PAGEBANNER));
    }

    public void testSearchSearchBack(JiraAwareFailable failable, String fieldName, String searchText) throws Exception {
        waitAndClickSearch2();
        waitAndTypeByName(fieldName, searchText);
        waitAndClickSearch2();
        back();
        waitForTextPresent(getTextByXpath(SPAN_CLASS_PAGEBANNER));
    }
}
