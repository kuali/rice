/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.actionrequests.dao;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Data Access Object for {@link ActionRequestValue}s.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionRequestDAO {

    public ActionRequestValue getActionRequestByActionRequestId(Long actionRequestId);

    public void saveActionRequest(ActionRequestValue actionRequest);

    public List findPendingRootRequestsByDocIdAtRouteLevel(Long routeHeaderId, Integer routeLevel);

    public List findPendingByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel);

    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel);

    public void delete(Long actionRequestId);

    public List findPendingByActionRequestedAndDocId(String actionRequestedCd, Long routeHeaderId);

    public List findAllPendingByDocId(Long routeHeaderId);

    public List findAllByDocId(Long routeHeaderId);

    public List findAllRootByDocId(Long routeHeaderId);

    public List findByStatusAndDocId(String statusCd, Long routeHeaderId);

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId);

    public List findActivatedByWorkgroup(Workgroup workgroup);

    public List findPendingByResponsibilityIds(Collection responsibilityIds);

    public  void deleteByRouteHeaderId(Long routeHeaderId);

    public List findPendingRootRequestsByDocumentType(Long documentTypeId);

    public List findPendingRootRequestsByDocIdAtRouteNode(Long routeHeaderId, Long nodeInstanceId);

    public List findRootRequestsByDocIdAtRouteNode(Long documentId, Long nodeInstanceId);

    //public List findFutureAdHocRequestsByDocIdAtRouteNode(Long documentId, String nodeName);

    public boolean doesDocumentHaveUserRequest(String workflowId, Long documentId);

    public List<Long> getRequestWorkgroupIds(Long documentId);

}