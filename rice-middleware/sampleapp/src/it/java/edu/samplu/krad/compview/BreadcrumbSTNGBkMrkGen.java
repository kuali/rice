/*
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.krad.compview;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.WebDriverLegacyITBase;
import org.testng.AssertJUnit;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BreadcrumbSTNGBkMrkGen extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page1";

    /**
     * u6610_label
     */
    public static final String NAVIGATE_TO_LABEL_ID = "u6610_label";

    /**
     * u13_control
     */
    public static final String SECOND_BREADCRUMB_NAV_XPATH = "//*[@class='uif-optionList']";

    /**
     * //*[@id='Uif-BreadcrumbWrapper']/ol/li[3]/a
     */
    public static final String SECOND_DOWN_TRIANGLE_XPATH = "//*[@id='Uif-BreadcrumbWrapper']/ol/li[3]/a";

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Override
    public void fail(String message){
        passed = false;
        AssertJUnit.fail(message);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBookmark() throws Exception {
        selectTopFrame();

        // div id="Uif-BreadcrumbWrapper" class="uif-sticky" data-sticky="true" style="position:fixed; left: 0; top: 39.55000305175781px;">
        waitForElementPresentById("Uif-BreadcrumbWrapper");

        // <span data-role="breadcrumb" id="u12">Input Fields and Controls</span>
        waitForElementPresentById("u12");
        SeleneseTestBase.assertEquals("Input Fields and Controls",getTextById("u12"));

        // <label id="u6610_label" for="u6610_control" data-label_for="u6610">
        //        Navigate to:
        // </label>
        SeleneseTestBase.assertFalse(isVisibleById(NAVIGATE_TO_LABEL_ID));
        // the first ▼
        waitAndClickByLinkText("▼");
        SeleneseTestBase.assertTrue(isVisibleById(NAVIGATE_TO_LABEL_ID));
        SeleneseTestBase.assertEquals("Navigate to:",getTextById(NAVIGATE_TO_LABEL_ID));
        // the first ▼
        waitAndClickByLinkText("▼");
        SeleneseTestBase.assertFalse(isVisibleById(NAVIGATE_TO_LABEL_ID));

        testBreadcrumb(2);
        testBreadcrumb(3);
        testBreadcrumb(4);
        testBreadcrumb(5);
        testBreadcrumb(6);
        testBreadcrumb(7);
        testBreadcrumb(8);
        testBreadcrumb(9);
        testBreadcrumb(10);
        testBreadcrumb(11);
        testBreadcrumb(1);

        passed();
    }

    protected void testBreadcrumb(int pageNumber) throws Exception {
        // <ul id="u13_control" class="uif-optionList" data-control_for="u13" tabindex="0"><li class="uif-optionList-item uif-optionList-selectedItem"><a href="http://env1.rice.kuali.org/kr-krad/uicomponents?methodToCall=start&pageId=UifCompView-Page1&viewId=UifCompView" data-key="UifCompView-Page1">
        //         Input Fields and Controls
        // </a></li>
        // <li class="uif-optionList-item"><a href="http://env1.rice.kuali.org/kr-krad/uicomponents?methodToCall=start&pageId=UifCompView-Page2&viewId=UifCompView" data-key="UifCompView-Page2">
        //         Other Fields
        // </a></li>
        // etc.
        SeleneseTestBase.assertFalse(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        // The second ▼
        waitAndClickByXpath(SECOND_DOWN_TRIANGLE_XPATH);
        SeleneseTestBase.assertTrue(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        waitAndClickByXpath(SECOND_DOWN_TRIANGLE_XPATH);
        SeleneseTestBase.assertFalse(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        waitAndClickByXpath(SECOND_DOWN_TRIANGLE_XPATH);

        // The Second selection of the second ▼
        // you can't just click by link text as the same clickable text is on the left navigation.
        waitAndClickByXpath(SECOND_BREADCRUMB_NAV_XPATH +"/li[" + pageNumber + "]/a");
        waitForElementPresentById("TopLink" + pageNumber); // bottom jump to top link
        driver.getCurrentUrl().contains("pageId=UifCompView-Page" + pageNumber);
    }
}
