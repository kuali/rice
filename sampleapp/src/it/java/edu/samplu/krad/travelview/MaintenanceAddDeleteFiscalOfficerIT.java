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

public class MaintenanceAddDeleteFiscalOfficerIT extends UpgradedSeleniumITBase {

    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    /**
     * Verify a fiscal officer line can be added and deleted
     */
    public void testVerifyAddDeleteFiscalOfficer() throws Exception {
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("50000");
        selenium.click("link=Travel Account Maintenance (New)");
        selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
        selenium.type("id=u1067_add_control", "1234567890");
        selenium.type("id=u1101_add_control", "2");
        selenium.click("id=u1066_add");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (selenium.isElementPresent("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number")) {
                break;
            }

            Thread.sleep(1000);
        }

        assertEquals("1234567890", selenium.getValue("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number"));
        assertEquals("2", selenium.getValue("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId"));
        selenium.click("css=#u1065_line0");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (!selenium.isElementPresent("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number")) {
                break;
            }

            Thread.sleep(1000);
        }
    }
}
