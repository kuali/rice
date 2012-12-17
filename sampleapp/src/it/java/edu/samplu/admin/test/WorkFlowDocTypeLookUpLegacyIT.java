/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * tests adding a namespace to Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowDocTypeLookUpLegacyIT extends AdminMenuLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "Document Type";
    }
    @Test
    public void testDocTypeLookup() throws Exception {
        super.gotoMenuLinkLocator();
        waitAndClickByXpath("//input[@title='Search Parent Name']");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        //waitForPageToLoad();
        waitAndClickByXpath("//table[@id='row']/tbody/tr[contains(td[3],'RiceDocument')]/td[1]/a");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertEquals("RiceDocument", getTextByXpath("//table[@id='row']/tbody/tr/td[4]/a"));
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("name", "Kuali*D");
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]");
        String docIdOld= getTextByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]/td[2]/a");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("label", "KualiDocument");
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[5], 'KualiDocument')]");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("documentTypeId", docIdOld);
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[2], '" + docIdOld + "')]");
        
        //below code is for lookup by active/inactive type, it is commented as inactive document cant be edited right now.
        /*waitAndClickByLinkText("edit");
        waitForPageToLoad();
        String docIdNew= getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription","Deactivating doc type");
        uncheckByName("document.newMaintainableObject.active");
        waitAndClickByName("methodToCall.route");
        waitForPageToLoad();
        selectTopFrame();
        super.gotoMenuLinkLocator();
        checkByXpath("//input[@id='activeNo']");
        //waitAndTypeByName("documentTypeId", docIdNew);
        waitAndClickByXpath("//input[@title='search' and @name='methodToCall.search']");
        waitAndClickByLinkText("edit");
        checkForIncidentReport();
        waitForPageToLoad();
        waitAndTypeByName("document.documentHeader.documentDescription","Activating doc type");
        checkByName("document.newMaintainableObject.active");
        waitAndClickByName("methodToCall.route");
        waitForPageToLoad();*/
        
    }

    
}