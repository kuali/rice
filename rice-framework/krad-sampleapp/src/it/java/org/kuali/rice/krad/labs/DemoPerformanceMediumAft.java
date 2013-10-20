package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoPerformanceMediumAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/labs?viewId=Lab-PerformanceMedium
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-PerformanceMedium";

    /**
     * /kr-krad/labs?viewId=Lab-PerformanceMedium&pageId=Lab-Performance-Page1#Lab-Performance-Page2
     */
    public static final String BOOKMARK_URL_2 = "/kr-krad/labs?viewId=Lab-PerformanceMedium&pageId=Lab-Performance-Page1#Lab-Performance-Page2&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Performance Medium");
    }

    @Test
    public void testPerformanceMediumBookmark() throws Exception {
        testPerformanceMedium();
        navigateToSecondPage(); // how to bookmark the second page?
        //        driver.navigate().to(ITUtil.getBaseUrlString() + BOOKMARK_URL_2);
        passed();
    }

    @Test
    public void testPerformanceMediumNav() throws Exception {
        testPerformanceMedium();
        navigateToSecondPage();
        passed();
    }

    private void navigateToSecondPage() throws InterruptedException {
        waitAndClickByLinkText("Page 2");
        jiraAwareWaitFor(By.xpath("//div[@class='blockUI blockMsg blockPage']"),11,"Timeout 11s - Page is taking too long to load.");
        waitForBottomButton();
    }

    private void waitForBottomButton() throws InterruptedException {
    	jiraAwareWaitFor(By.xpath("//button[contains(text(), 'Refresh - Non-Ajax')]"),11,"Timeout 11s - Button Not Present");
    }

    protected void testPerformanceMedium()throws Exception {
        waitForBottomButton();
    }
}
