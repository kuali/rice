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

import edu.samplu.common.KradMenuLegacyITBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceAddDeleteFiscalOfficerLegacyIT extends KradMenuLegacyITBase {

    @Override
    protected String getLinkLocator() {
        return "Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify a fiscal officer line can be added and deleted
     */
    public void testVerifyAddDeleteFiscalOfficerLegacy() throws Exception {
        gotoMenuLinkLocator();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number", "1234567890");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");
        
        waitAndClickByXpath("//button[@data-loadingmessage='Adding Line...']");

        assertElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number", "https://jira.kuali.org/browse/KULRICE-8038");

        assertEquals("1234567890", getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number","value"));
        assertEquals("2", getAttributeByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId","value"));
       
        waitAndClickByXpath("//button[@data-loadingmessage='Deleting Line...']");
        
        assertElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");
        passed();
    }

}
