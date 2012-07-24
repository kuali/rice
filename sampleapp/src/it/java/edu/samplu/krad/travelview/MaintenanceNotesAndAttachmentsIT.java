/*
 * Copyright 2006-2012 The Kuali Foundation
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
package edu.samplu.krad.travelview;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaintenanceNotesAndAttachmentsIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    /**
     * Verify Notes and Attachments section and fields exist
     */
    public void testVerifyNotesAndAttachments() throws Exception {
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
        selenium.click("css=#u168_col");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (selenium.isElementPresent("css=#u221_add")) {
                break;
            }

            Thread.sleep(1000);
        }

        assertTrue(selenium.isElementPresent("css=#u168_toggle > span.uif-headerText-span"));
        assertTrue(selenium.isElementPresent("//textarea[@name=\"newCollectionLines['document.notes'].noteText\"]"));
        assertTrue(selenium.isElementPresent("//input[@name='attachmentFile']"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.notes'].attachment.attachmentTypeCode\"]"));

    }
}
