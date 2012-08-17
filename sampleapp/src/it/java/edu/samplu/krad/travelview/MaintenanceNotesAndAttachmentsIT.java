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

import edu.samplu.common.ITUtil;
import edu.samplu.common.KradMenuITBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceNotesAndAttachmentsIT  extends KradMenuITBase {
    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify Notes and Attachments section and fields exist
     */
    public void testVerifyNotesAndAttachments() throws Exception {
        gotoMenuLinkLocator();
        selenium.click("css=span:contains('Notes and Attachments')");

        ITUtil.waitForElement(selenium, "//button[@title='Add a Note']", 15);
        
        assertTrue(selenium.isElementPresent("css=span:contains('Notes and Attachments')"));
        assertTrue(selenium.isElementPresent("//textarea[@name=\"newCollectionLines['document.notes'].noteText\"]"));
        assertTrue(selenium.isElementPresent("//input[@name='attachmentFile']"));
        assertTrue(selenium.isElementPresent("//input[@name=\"newCollectionLines['document.notes'].attachment.attachmentTypeCode\"]"));

    }
}
