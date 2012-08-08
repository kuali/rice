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

import edu.samplu.common.KradMenuITBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceAddDeleteNoteIT extends KradMenuITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify we can add and delete a note
     */
    public void testVerifyAddDeleteNote() throws Exception {
        gotoMenuLinkLocator();
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

        selenium.click("id=u334_add_control");
        selenium.type("id=u334_add_control", "Test note");
        selenium.click("id=u282_add");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (selenium.isElementPresent("name=document.notes[0].noteText")) {
                break;
            }

            Thread.sleep(1000);
        }

        assertEquals("Test note", selenium.getText("name=document.notes[0].noteText"));
        selenium.click("id=u280_line0");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (!selenium.isElementPresent("name=document.notes[0].noteText")) {
                break;
            }

            Thread.sleep(1000);
        }
    }
}
