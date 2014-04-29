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
public class LabsQuickFinderLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Lab-QuickFinderLayout
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Lab-QuickFinderLayout";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("QuickFinder Layout");
    }

    protected void testDemoQuickFinderLayout() throws InterruptedException {
    	waitForElementPresentByXpath("//div[@data-label='Input Field']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Vertical Checkbox Group Control']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Horizontal Checkbox Group Control']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Vertical Radio Group Control']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Horizontal Radio Group Control']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Textarea control']/div[@class='input-group']/div/a");
    	waitForElementPresentByXpath("//div[@data-label='Select control']/div[@class='input-group']/div/a");
    }

    @Test
    public void testDemoQuickFinderLayoutBookmark() throws Exception {
    	testDemoQuickFinderLayout();
        passed();
    }

    @Test
    public void testDemoQuickFinderLayoutNav() throws Exception {
    	testDemoQuickFinderLayout();
        passed();
    }
}
