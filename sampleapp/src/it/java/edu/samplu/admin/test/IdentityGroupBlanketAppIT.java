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
 * tests that user 'admin', on blanket approving a new group maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityGroupBlanketAppIT extends AdminMenuBlanketAppITBase {
  
    @Override
    protected String getLinkLocator() {
        return "link=Group";
    }

    @Override
    public String blanketApprove() throws Exception {
        String docId = waitForDocId();
        waitAndType("//input[@id='document.documentHeader.documentDescription']", "Validation Test Group");
        select("//select[@id='document.groupNamespace']", AdminMenuITBase.LABEL_KUALI_KUALI_SYSTEMS);
        waitAndType("//input[@id='document.groupName']", "Validation Test Group1 " + ITUtil.DTS);
        waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitAndClick("link=return value");
        waitAndClick("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        return docId;
    }
}
 