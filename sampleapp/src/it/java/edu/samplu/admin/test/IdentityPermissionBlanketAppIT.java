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
import static org.junit.Assert.assertTrue;

import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;
import org.junit.Test;

/**
 * tests that user 'admin', on blanket approving a new Permission maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPermissionBlanketAppIT extends AdminMenuITBase {
    @Override
    protected String getLinkLocator() {
        return "link=Permission";
    }

    @Test
    public void testPermission() throws Exception {
        gotoCreateNew();
        // selenium.setSpeed("2000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        selenium.type("//input[@name='document.documentHeader.documentDescription']", "Validation Test Permission");
        selenium.type("//input[@name='document.documentHeader.organizationDocumentNumber']", "10012");
        selenium.select("//select[@name='document.newMaintainableObject.namespaceCode']", IdentityResponsibilityBlanketAppIT.LABEL_KUALI_KUALI_SYSTEMS);
        selenium.select("//select[@name='document.newMaintainableObject.templateId']", IdentityResponsibilityBlanketAppIT.LABEL_KUALI_DEFAULT);
        selenium.type("//input[@name='document.newMaintainableObject.name']","Validation Test Responsibility " + ITUtil.DTS);
        selenium.click("methodToCall.blanketApprove");
        ITUtil.waitAndClick(selenium, "//img[@alt='doc search']");
//        selenium.selectWindow("null");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        docId= "link=" + docId;
        assertTrue(selenium.isElementPresent(docId));
        if(selenium.isElementPresent(docId)){            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }
}
