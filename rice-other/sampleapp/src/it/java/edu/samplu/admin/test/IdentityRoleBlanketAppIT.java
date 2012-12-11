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
 * tests that user 'admin', on blanket approving a new Role maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityRoleBlanketAppIT extends AdminMenuBlanketAppITBase {
    
     @Override
     protected String getLinkLocator() {
         return "link=Role";
     }

        @Override
    public String blanketApprove() throws Exception {
        waitAndClick("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClick("link=return value", "No return value link");
        String docId = waitForDocId();
        waitAndType("//input[@id='document.documentHeader.documentDescription']", "Validation Test Role");
        select("//select[@id='document.roleNamespace']", AdminMenuITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndType("//input[@id='document.roleName']", "Validation Test Role " +ITUtil.DTS, "No Role Name input to type in.");
        waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']", "No search button to click.");
        waitAndClick("link=return value", "No return value link");
        waitAndClick("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        
        return docId;
    }
}
