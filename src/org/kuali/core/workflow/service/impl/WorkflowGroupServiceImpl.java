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
package org.kuali.core.workflow.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.core.util.spring.Cached;
import org.kuali.core.workflow.service.WorkflowGroupService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class implements the WorkflowGroup Service using OneStart Workflow.
 * 
 * 
 */
@Transactional
public class WorkflowGroupServiceImpl implements WorkflowGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowGroupServiceImpl.class);

    /**
     * @see org.kuali.core.workflow.service.WorkflowGroupService#getWorkflowUsersGroups(edu.iu.uis.eden.user.UserId)
     */
    public Collection getWorkflowUsersGroups(UserIdVO userId) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving workflowGroups for '" + userId + "'");
        }

        List workgroupList = null;
        WorkgroupVO[] workgroupArray = KNSServiceLocator.getWorkflowInfoService().getUserWorkgroups(userId);
        if (workgroupArray != null) {
            workgroupList = Arrays.asList(workgroupArray);
        }
        else {
            workgroupList = new ArrayList();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved workflowGroups for '" + userId + "'");
        }
        return workgroupList;
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.WorkflowGroupService#getByGroupName(java.lang.String)
     */
    @Cached
    public WorkgroupVO getWorkgroupByGroupName(String groupName) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving workgroup by name '" + groupName + "'");
        }
        WorkgroupVO workgroupVO = KNSServiceLocator.getWorkflowInfoService().getWorkgroup(new WorkgroupNameIdVO(groupName));
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved workgroup by name '" + groupName + "'");
        }

        return workgroupVO;
    }
}