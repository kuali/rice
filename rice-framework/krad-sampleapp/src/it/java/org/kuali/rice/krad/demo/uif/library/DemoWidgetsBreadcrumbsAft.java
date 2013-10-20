/**
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
package org.kuali.rice.krad.demo.uif.library;

import com.thoughtworks.selenium.SeleneseTestBase;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsBreadcrumbsAft extends DemoLibraryBase {

	  /**
     * /kr-krad/kradsampleapp?viewId=Demo-Breadcrumbs-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Breadcrumbs-View&methodToCall=start";

    /**
     * inputField9
     */
    public static final String FIELD_TO_CHECK = "inputField9";

    /**
     * Kuali
     */
    public static final String START_PAGE_TITLE = "Kuali";

    /**
     * Kuali :: View Title
     */
    public static final String TARGET_PAGE_TITLE = "Kuali :: View Title";

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Breadcrumbs-View
     */
    public static final String TARGET_URL_CHECK = "/kr-krad/kradsampleapp?viewId=Demo-Breadcrumbs-View";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Breadcrumbs");
    }

    protected void testWidgetsBreadcrumbDefault() throws Exception {
        waitAndClickByLinkText("Default Breadcrumbs");
        assertNewWindow("1");
    }

    protected void testWidgetsBreadcrumbParentLocation() throws Exception {
        waitAndClickByLinkText("ParentLocation");
        waitAndClickByLinkText("Home ParentLocation");
        assertNewWindow("2");
    }

    protected void testWidgetsBreadcrumbParentLocationChain() throws Exception {
        waitAndClickByLinkText("ParentLocation Chain");
        waitAndClickByLinkText("ParentLocation Chain/Trail");
        assertNewWindow("3");
    }

    protected void testWidgetsBreadcrumbParentLocationPage() throws Exception {
        waitAndClickByLinkText("ParentLocation Page");
        waitAndClickByLinkText("ParentLocation View and Page");
        assertNewWindow("4");
    }

    protected void testWidgetsBreadcrumbPreViewAndPrePage() throws Exception {
        waitAndClickByLinkText("preView and prePage");
        waitAndClickByLinkText("preView and prePage breadcrumbs");
        assertNewWindow("5");
    }

    protected void testWidgetsBreadcrumbBreadcrumbLabel() throws Exception {
        waitAndClickByLinkText("Breadcrumb Label");
        waitAndClickByLinkText("Override Breadcrumb Label");
        assertNewWindow("6");
    }

    protected void testWidgetsBreadcrumbHomewardPath() throws Exception {
        waitAndClickByLinkText("Homeward Path");
        waitAndClickByLinkText("Homeward Path Breadcrumbs");
        assertNewWindow("7");
    }

    protected void testWidgetsBreadcrumbPathBased() throws Exception {
        waitAndClickByLinkText("Path-based");
        waitAndClickByLinkText("Path-based Breadcrumbs");
        waitForPageToLoad();
        switchToWindow(TARGET_PAGE_TITLE);
        SeleneseTestBase.assertTrue(driver.getCurrentUrl().contains(TARGET_URL_CHECK + "8"));
        waitAndClickByLinkText("Page 2");
        assertElementPresentByName("inputField9");
        driver.close();
        switchToWindow(START_PAGE_TITLE);
    }

    protected void testWidgetsBreadcrumbOverrides() throws Exception {
        waitAndClickByLinkText("Overrides");
        waitAndClickByLinkText("Breadcrumb Overrides");
        assertNewWindow("9");
    }

    protected void testWidgetsBreadcrumbSiblingBreadcrumbs() throws Exception {
        waitAndClickByLinkText("Sibling Breadcrumbs");
        waitAndClickByXpath("//div[@class='uif-verticalBoxLayout clearfix']/a[contains(text(),'Sibling Breadcrumbs')]");
        assertNewWindow("10");
    }

    private void assertNewWindow(String urlNumber) throws InterruptedException {
        waitForPageToLoad();
        switchToWindow(TARGET_PAGE_TITLE);
        SeleneseTestBase.assertTrue(driver.getCurrentUrl().contains(TARGET_URL_CHECK + urlNumber));
        assertElementPresentByName(FIELD_TO_CHECK);
        driver.close();
        switchToWindow(START_PAGE_TITLE);
    }

    @Test
    public void testWidgetsBreadcrumbBookmark() throws Exception {
        testWidgetsBreadcrumbDefault();
        testWidgetsBreadcrumbParentLocation();
        testWidgetsBreadcrumbParentLocationChain();
        testWidgetsBreadcrumbParentLocationPage();
        testWidgetsBreadcrumbPreViewAndPrePage();
        testWidgetsBreadcrumbBreadcrumbLabel();
        testWidgetsBreadcrumbHomewardPath();
        testWidgetsBreadcrumbPathBased();
        testWidgetsBreadcrumbOverrides();
        testWidgetsBreadcrumbSiblingBreadcrumbs();
        driver.close();
        passed();
    }

    @Test
    public void testWidgetsBreadcrumbNav() throws Exception {
        testWidgetsBreadcrumbDefault();
        testWidgetsBreadcrumbParentLocation();
        testWidgetsBreadcrumbParentLocationChain();
        testWidgetsBreadcrumbParentLocationPage();
        testWidgetsBreadcrumbPreViewAndPrePage();
        testWidgetsBreadcrumbBreadcrumbLabel();
        testWidgetsBreadcrumbHomewardPath();
        testWidgetsBreadcrumbPathBased();
        testWidgetsBreadcrumbOverrides();
        testWidgetsBreadcrumbSiblingBreadcrumbs();
        driver.close();
        passed();
    }
}
