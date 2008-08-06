/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.rice.kns.workflow.service;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.kns.workflow.service.WorkflowGroupService;
import org.kuali.test.KNSTestBase;


/**
 * This class tests the WorkflowGroupr service.
 */
public class WorkflowGroupServiceTest extends KNSTestBase {

    private static final String SUPERVISOR_GROUP_NAME = "KUALI_ROLE_SUPERVISOR";

    @Test public void testGetByGroupName() throws Exception {
        WorkgroupDTO workgroup = KNSServiceLocator.getWorkflowGroupService().getWorkgroupByGroupName(SUPERVISOR_GROUP_NAME);
        assertNotNull(workgroup);
        assertEquals(SUPERVISOR_GROUP_NAME, workgroup.getWorkgroupName());
    }

    @Test public void testGetUsersGroups() throws Exception {
        WorkflowGroupService groupService = KNSServiceLocator.getWorkflowGroupService();
        WorkgroupDTO workgroup = groupService.getWorkgroupByGroupName(SUPERVISOR_GROUP_NAME);
        UserDTO[] users = (UserDTO[]) workgroup.getMembers();
        String username = users[0].getNetworkId();
        Collection workgroups = groupService.getWorkflowUsersGroups(new NetworkIdDTO(username));
        assertNotNull(workgroups);
        assertTrue(workgroupsContain(workgroups, workgroup));
        // the inquiry workgroup
        //workgroup = SpringContext.getBean(WorkflowGroupService.class).getWorkgroupByGroupName(INQUIRE_ONLY_GROUP_NAME);
        //assertFalse(workgroupsContain(workgroups, workgroup));
    }


    /**
     * @param workgroup
     * @return
     */
    private boolean workgroupsContain(Collection workgroups, WorkgroupDTO workgroup) {
        boolean workgroupContains = false;

        Iterator it = workgroups.iterator();
        while (it.hasNext()) {
            WorkgroupDTO group = (WorkgroupDTO) it.next();
            if (group.getWorkgroupId().longValue() == workgroup.getWorkgroupId().longValue()) {
                workgroupContains = true;
                break;
            }
        }

        return workgroupContains;
    }

}
