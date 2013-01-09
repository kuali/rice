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

/**
 * tests that user 'admin', on blanket approving a new Permission maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPermissionBlanketAppIT extends AdminMenuBlanketAppITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Permission";
    }

    @Override
    public String blanketApprove() throws Exception {
        String docId = waitForDocId();
        waitAndType("//input[@name='document.documentHeader.documentDescription']", "Validation Test Permission");
        waitAndType("//input[@name='document.documentHeader.organizationDocumentNumber']", "10012");
        select("//select[@name='document.newMaintainableObject.namespaceCode']", AdminMenuITBase.LABEL_KUALI_KUALI_SYSTEMS);
        select("//select[@name='document.newMaintainableObject.templateId']", AdminMenuITBase.LABEL_KUALI_DEFAULT);
        waitAndType("//input[@name='document.newMaintainableObject.name']","Validation Test Responsibility " + ITUtil.DTS);
        return docId;
    }
}
 