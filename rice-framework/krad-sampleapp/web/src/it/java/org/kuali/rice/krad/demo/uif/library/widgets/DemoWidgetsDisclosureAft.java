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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsDisclosureAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DisclosureView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DisclosureView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Disclosure");
    }

    protected void testWidgetsDisclosureDefault() throws Exception {
        waitAndClickByLinkText("Default");
        if (!isElementPresentByXpath("//a/span[contains(text(),'Disclosure Section')]") && !isElementPresentByXpath("//a/span[contains(text(),'Predefined Disclosure Section')]")) {
            fail("First disclosure not displayed");
        }

        waitAndClickByLinkText("Disclosure Section");
        Thread.sleep(1000);
        waitAndClickByLinkText("Predefined Disclosure Section");
        Thread.sleep(1000);

        if (isElementPresentByXpath("//div[@id='Demo-Disclosure-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@data-open='true']")) {
            fail("First disclosure did not close");
        }
    }

    protected void testWidgetsDisclosureClosed() throws Exception {
        waitAndClickByLinkText("Closed");
        WebElement exampleDiv = navigateToExample("Demo-Disclosure-Example2");
        WebElement disclosure = findElement(By.cssSelector(".uif-disclosureContent"), exampleDiv);

        if (disclosure.isDisplayed()) {
            fail("Disclosure did not default closed");
        }

        waitAndClickByLinkText("Default Closed Section");
        Thread.sleep(1000);

        if (!disclosure.isDisplayed()) {
            fail("Disclosure did not open");
        }
    }

    @Test
    public void testWidgetsDisclosureBookmark() throws Exception {
        testWidgetsDisclosureDefault();
        testWidgetsDisclosureClosed();
        driver.close();
        passed();
    }

    @Test
    public void testWidgetsDisclosureNav() throws Exception {
        testWidgetsDisclosureDefault();
        testWidgetsDisclosureClosed();
        driver.close();
        passed();
    }
}
