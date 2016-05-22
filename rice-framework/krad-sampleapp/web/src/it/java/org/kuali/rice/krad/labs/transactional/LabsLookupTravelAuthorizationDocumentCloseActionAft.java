/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.labs.transactional;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class tests the lookup and close actions for the transactional document
 */
public class LabsLookupTravelAuthorizationDocumentCloseActionAft extends LabsTransactionalBase {
    public static final String BOOKMARK_URL = "/kr-krad/labs?methodToCall=start&viewId=DocSearchView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Iframe - Doc Search");
    }

    @Test
    public void testTravelAuthorizationDocumentCloseActionBookmark() throws Exception {
        testTravelAuthorizationDocumentLookupAndClose();
        passed();
    }

    @Test
    public void testTravelAuthorizationDocumentCloseActionNav() throws Exception {
        testTravelAuthorizationDocumentLookupAndClose();
        passed();
    }

    public void testTravelAuthorizationDocumentLookupAndClose() throws Exception {
        final String xpathExpression = "//iframe[1]";
        driver.switchTo().frame(driver.findElement(By.xpath(xpathExpression)));

        // search based on document type
        waitAndTypeLabeledInput("Document Type:", "TravelAuthorization");

        // click the search button
        jGrowl("Click the Search button");
        final String imgXpath = "//input[contains(@src,'/kr/static/images/buttonsmall_search.gif')]";
        waitAndClick(By.xpath(imgXpath));

        // click on the first item returned.
        jGrowl("Click on the first result in status SAVED returned from the search.");
        String windowHandle = driver.getWindowHandle();
        waitAndClick(By.xpath("//td[contains(text(),'SAVED')]/../td[1]/a"));

        // wait for the new window to pop up
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver driver) {
                //Wait until we have at least two windows.
                return driver.getWindowHandles().size() > 1;
            }
        });

        // switch focus to the new handle
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(windowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // click the close button
        waitAndClickButtonByText("Close", WebDriverUtils.configuredImplicityWait() * 10);

        waitAndClickCancelSaveOnClose();

        waitForTextPresent("Development made easy");
    }
}
