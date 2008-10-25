/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.service.impl.GroupServiceImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * Override kuali workgroup service because it's going directly against the workflow group service but 
 * toUppering every networkid witch is messing things up.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceGroupServiceImpl extends GroupServiceImpl {

    private static final Logger LOG = Logger.getLogger(RiceGroupServiceImpl.class);
    
    /**
     * @see org.kuali.rice.kns.service.KimGroupService#getUsersGroups(org.kuali.bo.KualiUser)
     */
    public List getUsersGroups(Person person) {
        List usersGroups = null;

        String userId = person.getPrincipalName();

        try {

            Collection workflowUsersGroups = KNSServiceLocator.getWorkflowGroupService().getWorkflowUsersGroups(new NetworkIdDTO(person.getPrincipalName()));
            if (workflowUsersGroups != null) {
                usersGroups = new ArrayList(workflowUsersGroups.size());

                Iterator iter = workflowUsersGroups.iterator();
                while (iter.hasNext()) {
                    WorkgroupDTO workgroup = (WorkgroupDTO) iter.next();
                    KimGroupImpl kimGroup = new KimGroupImpl();
                    kimGroup.setGroupDescription(workgroup.getDescription());
                    kimGroup.setGroupName(workgroup.getWorkgroupName());

                    List groupUsers = getGroupUsers(workgroup);

                    kimGroup.setGroupUsers(groupUsers);
                    usersGroups.add(kimGroup);
                }
            }
        }
        catch (WorkflowException e) {
            LOG.error("Caught a WorkflowException: " + userId, e);
            throw new RuntimeException("WorkflowException: ", e);
        }

        return usersGroups;
    }
    
    private List getGroupUsers(WorkgroupDTO workgroup) {
        // TODO do we want empty list here instead of null groupUsers attribute?
        List groupUsers = new ArrayList();

        List members = Arrays.asList(workgroup.getMembers());
        if (members != null) {
            Iterator iter = members.iterator();
            while (iter.hasNext()) {
                groupUsers.add(((UserDTO) iter.next()).getNetworkId());
            }
        }
        return groupUsers;

    }

}
