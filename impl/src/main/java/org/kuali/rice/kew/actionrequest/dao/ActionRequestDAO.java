/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.actionrequest.dao;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;

import java.util.Collection;
import java.util.List;


/**
 * Data Access Object for {@link ActionRequestValue}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionRequestDAO {

    public ActionRequestValue getActionRequestByActionRequestId(Long actionRequestId);

    public void saveActionRequest(ActionRequestValue actionRequest);

    public List findPendingRootRequestsByDocIdAtRouteLevel(String documentId, Integer routeLevel);

    public List findPendingByDocIdAtOrBelowRouteLevel(String documentId, Integer routeLevel);

    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(String documentId, Integer routeLevel);

    public void delete(Long actionRequestId);

     public List findPendingByActionRequestedAndDocId(String actionRequestedCd, String documentId);

    public List findAllPendingByDocId(String documentId);

    public List findAllByDocId(String documentId);

    public List findAllRootByDocId(String documentId);

    public List<ActionRequestValue> findByStatusAndDocId(String statusCd, String documentId);

    public List findByDocumentIdIgnoreCurrentInd(String documentId);

    public List findActivatedByGroup(String groupId);

    public List findPendingByResponsibilityIds(Collection responsibilityIds);

    public  void deleteByDocumentId(String documentId);

    public List findPendingRootRequestsByDocumentType(String documentTypeId);

    public List findPendingRootRequestsByDocIdAtRouteNode(String documentId, Long nodeInstanceId);

    public List findRootRequestsByDocIdAtRouteNode(String documentId, Long nodeInstanceId);

    //public List findFutureAdHocRequestsByDocIdAtRouteNode(String documentId, String nodeName);

    public boolean doesDocumentHaveUserRequest(String workflowId, String documentId);
  
    public List<String> getRequestGroupIds(String documentId);

}
