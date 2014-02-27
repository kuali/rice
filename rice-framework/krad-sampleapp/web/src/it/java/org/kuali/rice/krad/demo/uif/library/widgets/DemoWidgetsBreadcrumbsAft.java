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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsBreadcrumbsAft extends DemoLibraryBase {

	  /**
     * /kr-krad/kradsampleapp?viewId=Demo-BreadcrumbsView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-BreadcrumbsView&methodToCall=start";

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
        waitAndClickByLinkText("Default");
        waitAndClickByLinkText("Default Breadcrumbs");
        assertNewWindow("1", "Default");
    }

    protected void testWidgetsBreadcrumbParentLocation() throws Exception {
        waitAndClickByLinkText("ParentLocation");
        waitAndClickByLinkText("Home ParentLocation");
        assertNewWindow("2", "ParentLocation");
    }

    protected void testWidgetsBreadcrumbParentLocationChain() throws Exception {
        waitAndClickByLinkText("ParentLocation Chain");
        waitAndClickByLinkText("ParentLocation Chain/Trail");
        assertNewWindow("3", "ParentLocation Chain");
    }

    protected void testWidgetsBreadcrumbParentLocationPage() throws Exception {
        waitAndClickByLinkText("ParentLocation Page");
        waitAndClickByLinkText("ParentLocation View and Page");
        assertNewWindow("4", "ParentLocation Page");
    }

    protected void testWidgetsBreadcrumbPreViewAndPrePage() throws Exception {
        waitAndClickByLinkText("preView and prePage");
        waitAndClickByLinkText("preView and prePage breadcrumbs");
        waitForPageToLoad();
        switchToWindow(TARGET_PAGE_TITLE);
        assertTrue("preView and prePage", driver.getCurrentUrl().contains(TARGET_URL_CHECK + "5"));
        assertElementPresentByName(FIELD_TO_CHECK);
        driver.close();
        switchToWindow(START_PAGE_TITLE);
    }

    protected void testWidgetsBreadcrumbBreadcrumbLabel() throws Exception {
        waitAndClickByLinkText("Breadcrumb Label");
        waitAndClickByLinkText("Override Breadcrumb Label");
        assertNewWindow("6", "Breadcrumb Label");
    }

    protected void testWidgetsBreadcrumbHomewardPath() throws Exception {
        waitAndClickByLinkText("Homeward Path");
        waitAndClickByLinkText("Homeward Path Breadcrumbs");
        assertNewWindow("7", "Homeward Path");
    }

    protected void testWidgetsBreadcrumbPathBased() throws Exception {
        waitAndClickByLinkText("Path-based");
        waitAndClickByLinkText("Path-based Breadcrumbs");
        waitForPageToLoad();
        switchToWindow(TARGET_PAGE_TITLE);
        assertTrue("Path-based", driver.getCurrentUrl().contains(TARGET_URL_CHECK + "8"));
        assertBreadcrumb(3);
        waitAndClickByLinkText("Click me to continue chaining path-based breadcrumbs", "first click");
        assertBreadcrumb(4);
        waitAndClickByLinkText("Click me to continue chaining path-based breadcrumbs", "second click");
        assertBreadcrumb(5);
        driver.close();
        switchToWindow(START_PAGE_TITLE);
    }

    protected void assertBreadcrumb(int depth) throws Exception {
        WebElement element = findElement(By.xpath("//ol[@role='navigation']"));
        List<WebElement> elements = element.findElements(By.xpath("li"));
        assertEquals(depth, elements.size());
        assertTrue(elements.get(0).getText().contains("Home"));

        for (int i = 1; i < elements.size() - 1; i++) {
            assertTrue(elements.get(i).getText().contains("View Title"));
        }

        assertTrue(elements.get(elements.size() - 1).getText().contains("Page 1 Title"));
    }

    protected void testWidgetsBreadcrumbOverrides() throws Exception {
        waitAndClickByLinkText("Overrides");
        waitAndClickByLinkText("Breadcrumb Overrides");
        assertNewWindow("9", "Demo Page", "Demo Page", "Overrides");
    }

    protected void testWidgetsBreadcrumbSiblingBreadcrumbs() throws Exception {
        waitAndClickByLinkText("Sibling Breadcrumbs");
        waitAndClickByXpath("//section[@id='Demo-Breadcrumbs-Example10']/a[contains(text(),'Sibling Breadcrumbs')]");
        assertNewWindow("10", "Sibling Breadcrumbs");
    }

    private void assertNewWindow(String urlNumber, String message) throws InterruptedException {
        assertNewWindow(urlNumber, "Page 1 Title", "Page 2 Title", message);
    }

    private void assertNewWindow(String urlNumber, String titleOne, String titleTwo, String message) throws InterruptedException {
        Thread.sleep(WebDriverUtils.configuredImplicityWait() * 2000);
        switchToWindow(TARGET_PAGE_TITLE);
        assertTrue(message, driver.getCurrentUrl().contains(TARGET_URL_CHECK + urlNumber));
        assertElementPresentByName(FIELD_TO_CHECK);
        WebElement element = findElement(By.xpath("//span[@data-role='breadcrumb']"));
        assertTrue(element.getText().contains(titleOne));
        waitAndClickByLinkText("Page 2");
        element = findElement(By.xpath("//span[@data-role='breadcrumb']"));
        int secondsToWait = WebDriverUtils.configuredImplicityWait() * 3000;
        while (!titleTwo.equals(element.getText().trim()) && secondsToWait > 0) {
            Thread.sleep(4000);
            secondsToWait -= 1000;
            element = findElement(By.xpath("//span[@data-role='breadcrumb']"));
        }
        assertEquals(titleTwo, element.getText().trim());
        driver.close();
        switchToWindow(START_PAGE_TITLE);
    }

    private void testAllBreadcrumb() throws Exception {
        testWidgetsBreadcrumbSiblingBreadcrumbs();
        testWidgetsBreadcrumbDefault();
        testWidgetsBreadcrumbParentLocation();
        testWidgetsBreadcrumbParentLocationChain();
        testWidgetsBreadcrumbParentLocationPage();
        testWidgetsBreadcrumbPreViewAndPrePage();
        testWidgetsBreadcrumbBreadcrumbLabel();
        testWidgetsBreadcrumbHomewardPath();
        testWidgetsBreadcrumbPathBased();
        testWidgetsBreadcrumbOverrides();
        driver.close();
    }

    @Test
    public void testWidgetsBreadcrumbBookmark() throws Exception {
        testAllBreadcrumb();
        passed();
    }

    @Test
    public void testWidgetsBreadcrumbNav() throws Exception {
        testAllBreadcrumb();
        passed();
    }
}
