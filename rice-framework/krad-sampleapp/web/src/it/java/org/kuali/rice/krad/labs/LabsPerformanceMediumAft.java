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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsPerformanceMediumAft extends WebDriverLegacyITBase {

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
        alertAccept();
        waitForBottomButton();
    }

    private void waitForBottomButton() throws InterruptedException {
    	jiraAwareWaitFor(By.xpath("//button[contains(text(), 'Refresh - Non-Ajax')]"),11,"Timeout 11s - Button Not Present");
    }

    protected void testPerformanceMedium()throws Exception {
        waitForBottomButton();
        selectByName("inputField6","Option 2");
        selectByName("inputField7","Option 2");
        assertElementPresentByXpath("//select[@name='inputField8' and @disabled]");
        assertElementPresentByXpath("//button[contains(text(),'Add')]");
        assertElementPresentByXpath("//button[contains(text(),'Delete')]");
        assertTextPresent("null ( ab extra )");
        assertElementPresentByXpath("//input[@name='mediumCollection2[0].field1' and @value='ab extra']");
        assertTextPresent("SubCollection 1");
    }
}
