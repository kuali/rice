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
public class MaintenanceAddDeleteFiscalOfficerIT extends KradMenuITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify a fiscal officer line can be added and deleted
     */
    public void testVerifyAddDeleteFiscalOfficer() throws Exception {
        gotoMenuLinkLocator();
        focusAndType("name=newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number", "1234567890");
        focusAndType("name=newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");
        
        selenium.click("//button[@data-loadingmessage='Adding Line...']");
        
        ITUtil.waitForElement(selenium, "name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number", "https://jira.kuali.org/browse/KULRICE-8038");
       
        assertEquals("1234567890", selenium.getValue("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number"));
        assertEquals("2", selenium.getValue("name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId"));
       
        selenium.click("//button[@data-loadingmessage='Deleting Line...']");
        
        ITUtil.waitForElement(selenium,  "name=document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");       
    }
    
    private void focusAndType(String fieldLocator, String typeText) {
        selenium.focus(fieldLocator);
        selenium.type(fieldLocator, typeText);
    }
}
