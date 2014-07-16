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
package org.kuali.rice.krad.labs.clientside.stateview;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverAftBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsClientSideStateViewAft extends WebDriverAftBase {

    /**
     * /kr-krad/labs?viewId=Lab-ClientSideState&pageId=Lab-ClientSideState-Page1
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-ClientSideState&pageId=Lab-ClientSideState-Page1";

    @Override
    protected void navigate() throws Exception {
        waitAndClickLinkContainingText("Client Side State Test View");
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testClientSideStateViewBookmark() throws Exception {
        testClientSideStateView();
        passed();
    }

    @Test
    public void testClientSideStateViewNav() throws Exception {
        testClientSideStateView();
        passed();
    }

    public void testClientSideStateView() throws Exception {
        waitAndClickButtonByText(SHOW_INACTIVE); // first
        Thread.sleep(500);
        waitAndClickButtonByText(SHOW_INACTIVE); // second, first has changed
//        waitAndClickByXpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"]/header/div/button[contains(.,'show inactive')]"); // first show inactive
//        waitAndClickByXpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"]/header/div/button[contains(.,'show inactive')]"); // the second show inactive is now the only one
        waitAndClickButtonByText("Refresh - Ajax");
        Thread.sleep(2000);
        waitForElementNotPresent(By.xpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"][1]/header/div/button[contains(.,'" + SHOW_INACTIVE + "')]")); // first inactive, still Hide inactive
        waitForElementPresent(By.xpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"]/header/div/button[contains(.,'Hide inactive')]")); // first inactive is Hide inactive
        waitAndClickButtonByText("Refresh - Non-Ajax");
        Thread.sleep(2000);
        waitForElementNotPresent(By.xpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"][1]/header/div/button[contains(.,'" + SHOW_INACTIVE + "')]")); // first show inactive, still Hide inactive
        waitForElementPresent(By.xpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"]/header/div/button[contains(.,'Hide inactive')]")); // first inactive is Hide inactive
        assertFalse(isElementPresent(By.xpath("//section[@data-parent=\"Lab-ClientSideState-Page1\"][2]/header/div/button[contains(.,'" + SHOW_INACTIVE + "')]"))); // second show inactive, still Hide inactive
    }
}
