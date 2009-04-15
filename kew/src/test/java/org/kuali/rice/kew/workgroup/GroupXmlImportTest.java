/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.workgroup;

import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupXmlImportTest extends KEWTestCase {
    /**
     *
     * Verify that a workgroup with a bad user in the xml is not going to be put in the db.
     *
     * @throws Exception
     */

    @Test public void testGroupImportXml() throws Exception {
    	loadXmlFile("GroupXmlImportTest.xml");

        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
        //verify the workgroup did not get into the db
        KimGroup group = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestUserGroup");

        List<String> members = identityManagementService.getGroupMemberPrincipalIds(group.getGroupId());
        assertNotNull(group);
        List<String> groups = identityManagementService.getMemberGroupIds(group.getGroupId());
        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("ewestfal").getPrincipalId(), group.getGroupId()));
        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("rkirkend").getPrincipalId(), group.getGroupId()));
        assertTrue(identityManagementService.isMemberOfGroup("2015", group.getGroupId()));
        assertTrue(KIMServiceLocator.getGroupService().isGroupMemberOfGroup(identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestWorkgroup").getGroupId(), group.getGroupId()));
    }
}
