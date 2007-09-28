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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.Timer;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.core.workflow.service.WorkflowGroupService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the service implementation for the KualiGroupService structure. This is the default implementation, that is
 * delivered with Kuali which utilizes the workgroup concept in OneStart Workflow.
 */
@Transactional
public class KualiGroupServiceImpl implements KualiGroupService {

    private WorkflowGroupService workflowGroupService;
    private KualiWorkflowInfo kualiWorkflowInfo;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiGroupServiceImpl.class);

    /**
     * @see org.kuali.core.service.KualiGroupService#getByGroupName(java.lang.String)
     */
    public KualiGroup getByGroupName(String groupName) throws GroupNotFoundException {
        Timer t0 = new Timer("getByGroupName");
        KualiGroup kualiGroup = null;

        if (StringUtils.isEmpty(groupName)) {
            throw new GroupNotFoundException("unable to process empty groupName");
        }

        if (StringUtils.equals(groupName, KualiGroup.KUALI_UNIVERSAL_GROUP.getGroupName())) {
            kualiGroup = KualiGroup.KUALI_UNIVERSAL_GROUP;
        }
        else {

            WorkgroupVO workgroup = null;

            try {
                workgroup = getKualiWorkflowInfo().getWorkgroup(new WorkgroupNameIdVO(groupName));
            }
            catch (WorkflowException e) {
                t0.log();
                throw new GroupNotFoundException("Error retrieving groupName " + groupName + " from workflow", e);
            }

            if (workgroup != null) {
                kualiGroup = new KualiGroup();
                kualiGroup.setGroupDescription(workgroup.getDescription());
                kualiGroup.setGroupName(workgroup.getWorkgroupName());
                kualiGroup.setGroupUsers(getGroupUsers(workgroup));
            }
        }
        t0.log();
        return kualiGroup;

    }

    public boolean groupExists(String groupName) {
        boolean exists = false;

        if (!StringUtils.isEmpty(groupName)) {
            if (StringUtils.equals(groupName, KualiGroup.KUALI_UNIVERSAL_GROUP.getGroupName() )) {
                exists = true;
            }
            else {
                try {
                    exists = getKualiWorkflowInfo().getWorkgroup(new WorkgroupNameIdVO(groupName)) != null;
                }
                catch (WorkflowException e) {
                    throw new InfrastructureException("error retrieving groupName " + groupName + " from workflow", e);
                }
            }
        }

        return exists;

    }

    /**
     * @see org.kuali.core.service.KualiGroupService#getUsersGroups(org.kuali.bo.KualiUser)
     */
    public List getUsersGroups(UniversalUser universalUser) {
        List usersGroups = null;

        String userId = universalUser.getPersonUserIdentifier();
        if (StringUtils.isNotBlank(userId)) {
            userId = "";
        }
        try {
            Collection workflowUsersGroups = getWorkflowGroupService().getWorkflowUsersGroups(new NetworkIdVO(userId.toUpperCase()));
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
            throw new RuntimeException("Caught workflow exception in KualiGroupServiceImpl.getUsersGroups", e);
        }

        return usersGroups;
    }

    /**
     * Retrieves all the users in a OneStart Workflow workgroup.
     * 
     * @param workgroup
     * @return
     */
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

    /**
     * @param workflowGroupService The workflowGroupService to set.
     */
    public void setWorkflowGroupService(WorkflowGroupService workflowGroupService) {
        this.workflowGroupService = workflowGroupService;
    }

    /**
     * @return Returns the workflowGroupService.
     */
    public WorkflowGroupService getWorkflowGroupService() {
        return workflowGroupService;
    }

    public void setKualiWorkflowInfo(KualiWorkflowInfo kualiWorkflowInfo) {
        this.kualiWorkflowInfo = kualiWorkflowInfo;
    }

    public KualiWorkflowInfo getKualiWorkflowInfo() {
        return kualiWorkflowInfo;
    }
}