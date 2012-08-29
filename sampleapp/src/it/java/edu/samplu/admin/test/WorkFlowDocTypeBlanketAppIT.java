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

import edu.samplu.common.AdminMenuBlanketAppITBase;
import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;

import java.util.Calendar;

import static org.junit.Assert.assertTrue;


/**
 * tests that user 'admin', on blanket approving a new Document Type maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowDocTypeBlanketAppIT extends AdminMenuBlanketAppITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Document Type";
    }

    @Override
    public String blanketApprove() throws Exception {
        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        String docId = selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR);
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Document Type " + ITUtil.DTS);
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        ITUtil.waitAndClick(selenium, parentDocType);
        ITUtil.waitAndClick(selenium, "//input[@name='methodToCall.search' and @value='search']");
        ITUtil.waitAndClick(selenium, "link=return value");

        selenium.waitForPageToLoad("30000");
        String docTypeName = "Validation Test Doc Type " + Calendar.getInstance().getTimeInMillis();
        selenium.type("//input[@id='document.newMaintainableObject.name']", docTypeName);
        selenium.type("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']", "${kr.url}/maintenance.do?methodToCall=docHandler");
        selenium.type("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        selenium.type("//input[@id='document.newMaintainableObject.label']", "Workflow Maintenance Document Type Document");
        selenium.type("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']", "default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        return docId;
    }
}
