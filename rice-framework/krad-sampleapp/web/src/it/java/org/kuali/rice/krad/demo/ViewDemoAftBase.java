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
package org.kuali.rice.krad.demo;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Extending this class to inherit testing Help links.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ViewDemoAftBase extends WebDriverLegacyITBase{

    /**
     * http://site.kuali.org/rice/2-5
     *
     * In theory it would be possible to get the rice version from the AbstractBaseConfig, but then we have a requirement
     * of having the app configured for AFTs to run and AFTs frequently are run against a remote app.  Also the rice config
     * can be overridden in the app.xml, which some of us in QA do frequently which would result in test failures.
     *
     * The DB rice.version (which is where the help urls are stored) seems to be independent from the app.xml, using that value might be better than hardcoding
     */
    public static final String HELP_URL_RICE_VERSION = "site.kuali.org/rice/2.5.";

    protected void assertHelp() throws InterruptedException {
        checkForIncidentReport();
        String kualiWindowHandle = driver.getWindowHandle();

        WebElement help = waitForElementPresent(By.xpath("//button[@class='uif-iconOnly uif-helpAction icon-question']"));
        jGrowl("Click Help.");
        help.click();

        int timeout = 0;
        while (driver.getWindowHandles().size() != 2 && timeout <= WebDriverUtils.configuredImplicityWait()) {
            Thread.sleep(1000);
            timeout++;
        }

        for (String handle : driver.getWindowHandles()) {
            if (!kualiWindowHandle.equals(handle)) {
                driver.switchTo().window(handle);
            }
        }

        assertTrue(driver.getTitle().contains("Online Help"));
        assertTrue(driver.getTitle().contains("Kuali Rice 2.5."));
        assertTrue(driver.getCurrentUrl().contains(HELP_URL_RICE_VERSION));

        driver.close();
        driver.switchTo().window(kualiWindowHandle);
    }

    private void testHelp() throws InterruptedException {
        assertHelp();
        passed();
    }

    @Test
    public void testHelpNav() throws Exception {
        testHelp();
    }

    @Test
    public void testHelpBookmark() throws Exception {
        testHelp();
    }
}
