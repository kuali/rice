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

import static org.junit.Assert.fail;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceSaveIT extends KradMenuITBase {
    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

     @Test
    /**
     * Verify Save
     */
    public void testVerifySave() throws Exception {
        gotoMenuLinkLocator();
        selenium.type("name=document.documentHeader.documentDescription", "Test Document " + ITUtil.DTS);
        selenium.click("name=document.newMaintainableObject.dataObject.number");
        selenium.type("name=document.newMaintainableObject.dataObject.number", "1234567890");
        selenium.type("name=document.newMaintainableObject.dataObject.extension.accountTypeCode", "EAT");
        selenium.type("name=document.newMaintainableObject.dataObject.subAccount", "a1");
        selenium.click("css=button[data-loadingmessage='Saving...'].uif-action.uif-primaryActionButton.uif-boxLayoutHorizontalItem");
        Thread.sleep(2000);
        if (selenium.isElementPresent("//li[@class='uif-errorMessageItem']")) {
            String errorText = selenium.getText("//li[@class='uif-errorMessageItem']");
            if (errorText != null && errorText.contains("errors")) {
                fail(errorText);
            }
        }
    }
}
