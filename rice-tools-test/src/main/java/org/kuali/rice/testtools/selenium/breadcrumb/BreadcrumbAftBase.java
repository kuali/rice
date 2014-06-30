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
package org.kuali.rice.testtools.selenium.breadcrumb;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class BreadcrumbAftBase extends WebDriverLegacyITBase {

    /**
     * //div[@class='uif-breadcrumbSiblingContent']//div[@class='uif-inputField']//ul[@class='uif-optionList']
     */
    public static final String SECOND_BREADCRUMB_NAV_XPATH = "//div[@class='uif-breadcrumbSiblingContent']//div[@class='uif-inputField']//ul[@class='uif-optionList']";

    /**
     * (//a[@class='uif-breadcrumbSiblingLink'])[2]
     */
    public static final String SECOND_DOWN_TRIANGLE_XPATH = "(//a[@class='uif-breadcrumbSiblingLink'])[2]";

    String[][] selectAsserts = {{"UifCompView", "Uif Components"}};

    int[] breadcrumbOrderIndexes = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1};

    protected String getTriangleXpath() {
        return SECOND_DOWN_TRIANGLE_XPATH;
    }

    protected void testBreadcrumb(int pageNumber) throws Exception {
        // <ul id="u13_control" class="uif-optionList" data-control_for="u13" tabindex="0"><li class="uif-optionList-item uif-optionList-selectedItem"><a href="http://env1.rice.kuali.org/kr-krad/uicomponents?methodToCall=start&pageId=UifCompView-Page1&viewId=UifCompView" data-key="UifCompView-Page1">
        //         Input Fields and Controls
        // </a></li>
        // <li class="uif-optionList-item"><a href="http://env1.rice.kuali.org/kr-krad/uicomponents?methodToCall=start&pageId=UifCompView-Page2&viewId=UifCompView" data-key="UifCompView-Page2">
        //         Other Fields
        // </a></li>
        // etc.
        jiraAwareWaitFor(By.xpath(SECOND_BREADCRUMB_NAV_XPATH));
        assertFalse(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        // The second ▼
        waitAndClickByXpath(getTriangleXpath(), "failed on breadcrumb pageNumber " + pageNumber);
        Thread.sleep(100);
        assertTrue(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        waitAndClickByXpath(getTriangleXpath());
        assertFalse(isVisibleByXpath(SECOND_BREADCRUMB_NAV_XPATH));
        waitAndClickByXpath(getTriangleXpath());

        // The Second selection of the second ▼
        // you can't just click by link text as the same clickable text is on the left navigation.
        waitAndClickByXpath(SECOND_BREADCRUMB_NAV_XPATH +"/li[" + pageNumber + "]/a");
        waitForElementPresentById("TopLink" + pageNumber, "Breadcrumb number " + pageNumber + " failure", 30); // bottom jump to top link
        driver.getCurrentUrl().contains("pageId=UifCompView-Page" + pageNumber);
    }

    protected void testBreadcrumbs() throws Exception {
        for (int i = 0, s = breadcrumbOrderIndexes.length; i < s; i++) {
            testBreadcrumb(breadcrumbOrderIndexes[i]);
        }
    }

    protected void testBreadcrumbsShuffled() throws Exception {
        int[] copiedBreadcrumbOrderIndex = Arrays.copyOf(breadcrumbOrderIndexes, breadcrumbOrderIndexes.length);

        Collections.shuffle(Arrays.asList(copiedBreadcrumbOrderIndex));
        for (int i = 0, s = copiedBreadcrumbOrderIndex.length; i < s; i++) {
            jGrowl("Click on Bread crumb index number " + i);
            testBreadcrumb(copiedBreadcrumbOrderIndex[i]);
        }
    }

    @Test
    public void testBreadcrumbBookmark() throws Exception {
        testBreadcrumbs();
        passed();
    }

    @Test
    public void testBreadcrumbShuffledBookmark() throws Exception {
        testBreadcrumbsShuffled();
        passed();
    }

    @Test
    public void testBreadcrumbNav() throws Exception {
        testBreadcrumbs();
        passed();
    }

    @Test
    public void testBreadcrumbShuffledNav() throws Exception {
        testBreadcrumbsShuffled();
        passed();
    }
}
