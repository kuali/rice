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

import edu.samplu.common.AdminMenuBlanketAppLegacyITBase;
import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;

/**
 * tests that user 'admin', on blanket approving a new Role maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityRoleBlanketAppLegacyIT extends AdminMenuBlanketAppLegacyITBase {
    
     @Override
     protected String getLinkLocator() {
         return "Role";
     }

    @Override
    public String blanketApprove() throws Exception {
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");
       
        String docId = waitForDocId();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Validation Test Role " + ITUtil.DTS_TWO);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.roleNamespace']", AdminMenuLegacyITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.roleName']", "Validation Test Role " + ITUtil.DTS_TWO, "No Role Name input to type in.");
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClickByLinkText("return value", "No return value link");
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        
        return docId;
    }
}
