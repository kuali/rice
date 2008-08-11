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
package org.kuali.rice.kns.workflow.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.spring.Cached;
import org.kuali.rice.kns.workflow.service.WorkflowGroupService;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class implements the WorkflowGroup Service using OneStart Workflow.
 * 
 * 
 */
@Transactional
public class WorkflowGroupServiceImpl implements WorkflowGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowGroupServiceImpl.class);

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowGroupService#getWorkflowUsersGroups(org.kuali.rice.kew.user.UserId)
     */
    public Collection getWorkflowUsersGroups(UserIdDTO userId) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving workflowGroups for '" + userId + "'");
        }

        List workgroupList = null;
        WorkgroupDTO[] workgroupArray = KNSServiceLocator.getWorkflowInfoService().getUserWorkgroups(userId);
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
     * @see org.kuali.rice.kns.workflow.service.WorkflowGroupService#getByGroupName(java.lang.String)
     */
    @Cached
    public WorkgroupDTO getWorkgroupByGroupName(String groupName) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving workgroup by name '" + groupName + "'");
        }
        WorkgroupDTO workgroupVO = KNSServiceLocator.getWorkflowInfoService().getWorkgroup(new WorkgroupNameIdDTO(groupName));
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved workgroup by name '" + groupName + "'");
        }

        return workgroupVO;
    }
}