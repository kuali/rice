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
package org.kuali.rice.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.impl.KualiGroupServiceImpl;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Override kuali workgroup service because it's going directly against the workflow group service but 
 * toUppering every networkid witch is messing things up.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceGroupServiceImpl extends KualiGroupServiceImpl {

    private static final Logger LOG = Logger.getLogger(RiceGroupServiceImpl.class);
    
    /**
     * @see org.kuali.core.service.KualiGroupService#getUsersGroups(org.kuali.bo.KualiUser)
     */
    public List getUsersGroups(UniversalUser universalUser) {
        List usersGroups = null;

        String userId = universalUser.getPersonUserIdentifier();

        try {

            Collection workflowUsersGroups = getWorkflowGroupService().getWorkflowUsersGroups(new NetworkIdVO(universalUser.getPersonUserIdentifier()));
            if (workflowUsersGroups != null) {
                usersGroups = new ArrayList(workflowUsersGroups.size());

                Iterator iter = workflowUsersGroups.iterator();
                while (iter.hasNext()) {
                    WorkgroupVO workgroup = (WorkgroupVO) iter.next();
                    KualiGroup kualiGroup = new KualiGroup();
                    kualiGroup.setGroupDescription(workgroup.getDescription());
                    kualiGroup.setGroupName(workgroup.getWorkgroupName());

                    List groupUsers = getGroupUsers(workgroup);

                    kualiGroup.setGroupUsers(groupUsers);
                    usersGroups.add(kualiGroup);
                }

                usersGroups.add(KualiGroup.KUALI_UNIVERSAL_GROUP);
            }
        }
        catch (WorkflowException e) {
            LOG.error("Caught a WorkflowException: " + userId, e);
            throw new RuntimeException("EdenException: ", e);
        }

        return usersGroups;
    }
    
    private List getGroupUsers(WorkgroupVO workgroup) {
        // TODO do we want empty list here instead of null groupUsers attribute?
        List groupUsers = new ArrayList();

        List members = Arrays.asList(workgroup.getMembers());
        if (members != null) {
            Iterator iter = members.iterator();
            while (iter.hasNext()) {
                groupUsers.add(((UserVO) iter.next()).getNetworkId());
            }
        }
        return groupUsers;

    }

}