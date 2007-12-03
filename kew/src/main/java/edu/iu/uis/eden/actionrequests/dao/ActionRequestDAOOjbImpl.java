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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * OJB implementation of the {@link ActionRequestDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRequestDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionRequestDAO {

    public ActionRequestValue getActionRequestByActionRequestId(Long actionRequestId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("actionRequestId", actionRequestId);
        return (ActionRequestValue) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public void saveActionRequest(ActionRequestValue actionRequest) {
        if (actionRequest.getActionRequestId() == null) {
            loadDefaultValues(actionRequest);
        }
        this.getPersistenceBrokerTemplate().store(actionRequest);
    }

    public List findPendingByResponsibilityIds(Collection responsibilityIds) {
        if (responsibilityIds == null || responsibilityIds.size() == 0) return Collections.EMPTY_LIST;
        Criteria crit = new Criteria();
        Criteria statusCriteria = new Criteria();
        Criteria activatedCriteria = new Criteria();
        activatedCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_ACTIVATED);

        Criteria initializedCriteria = new Criteria();
        initializedCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_INITIALIZED);

        statusCriteria.addOrCriteria(activatedCriteria);
        statusCriteria.addOrCriteria(initializedCriteria);
        crit.addAndCriteria(statusCriteria);
        crit.addIn("responsibilityId", responsibilityIds);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findPendingByActionRequestedAndDocId(String actionRequestedCd, Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("actionRequested", actionRequestedCd);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        crit.addAndCriteria(getPendingCriteria());
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findByStatusAndDocId(String statusCd, Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("status", statusCd);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));

        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    private void loadDefaultValues(ActionRequestValue actionRequest) {
        checkNull(actionRequest.getActionRequested(), "action requested");
        checkNull(actionRequest.getResponsibilityId(), "responsibility ID");
        checkNull(actionRequest.getRouteLevel(), "route level");
        checkNull(actionRequest.getDocVersion(), "doc version");
        if (actionRequest.getIgnorePrevAction() == null) {
            actionRequest.setIgnorePrevAction(Boolean.FALSE);
        }
        if (actionRequest.getStatus() == null) {
            actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
        }
        if (actionRequest.getPriority() == null) {
            actionRequest.setPriority(new Integer(EdenConstants.ACTION_REQUEST_DEFAULT_PRIORITY));
        }
        if (actionRequest.getCurrentIndicator() == null) {
            actionRequest.setCurrentIndicator(new Boolean(true));
        }
        actionRequest.setCreateDate(new Timestamp(System.currentTimeMillis()));
    }

    //TODO Runtime might not be the right thing to do here...
    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("Null value for " + valueName);
        }
    }

    public List findPendingRootRequestsByDocIdAtRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeLevel", routeLevel);
        crit.addNotEqualTo("status", EdenConstants.ACTION_REQUEST_DONE_STATE);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        crit.addIsNull("parentActionRequest");
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findPendingByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Criteria crit = new Criteria();
        crit.addLessOrEqualThan("routeLevel", routeLevel);
        crit.addNotEqualTo("status", EdenConstants.ACTION_REQUEST_DONE_STATE);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Criteria crit = new Criteria();
        crit.addLessOrEqualThan("routeLevel", routeLevel);
        crit.addNotEqualTo("status", EdenConstants.ACTION_REQUEST_DONE_STATE);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        crit.addIsNull("parentActionRequest");
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public void delete(Long actionRequestId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("actionRequestId", actionRequestId);
    	this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findAllPendingByDocId(Long routeHeaderId) {
        Criteria initializedStatCriteria = new Criteria();
        initializedStatCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_INITIALIZED);

        Criteria activatedStatCriteria = new Criteria();
        activatedStatCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_ACTIVATED);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addOrCriteria(initializedStatCriteria);
        statusCriteria.addOrCriteria(activatedStatCriteria);

        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        crit.addAndCriteria(statusCriteria);

        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findAllByDocId(Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findAllRootByDocId(Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        crit.addIsNull("parentActionRequest");
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findActivatedByWorkgroup(Workgroup workgroup) {
        Criteria statusCriteria = new Criteria();
        statusCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_ACTIVATED);
        Criteria crit = new Criteria();
        crit.addEqualTo("workgroupId", workgroup.getWorkflowGroupId().getGroupId());
        crit.addEqualTo("currentIndicator", new Boolean(true));
        crit.addAndCriteria(statusCriteria);

        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    private Criteria getPendingCriteria() {
        Criteria pendingCriteria = new Criteria();
        Criteria activatedCriteria = new Criteria();
        activatedCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_ACTIVATED);
        Criteria initializedCriteria = new Criteria();
        initializedCriteria.addEqualTo("status", EdenConstants.ACTION_REQUEST_INITIALIZED);
        pendingCriteria.addOrCriteria(activatedCriteria);
        pendingCriteria.addOrCriteria(initializedCriteria);
        return pendingCriteria;
    }

    public  void deleteByRouteHeaderId(Long routeHeaderId){
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findPendingRootRequestsByDocumentType(Long documentTypeId) {
    	Criteria crit = new Criteria();
        crit.addEqualTo("routeHeader.documentTypeId", documentTypeId);
        crit.addAndCriteria(getPendingCriteria());
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        crit.addIsNull("parentActionRequest");
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findPendingRootRequestsByDocIdAtRouteNode(Long routeHeaderId, Long nodeInstanceId) {
    	Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addAndCriteria(getPendingCriteria());
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        crit.addIsNull("parentActionRequest");
        crit.addEqualTo("nodeInstance.routeNodeInstanceId", nodeInstanceId);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public List findRootRequestsByDocIdAtRouteNode(Long documentId, Long nodeInstanceId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", documentId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        crit.addIsNull("parentActionRequest");
        crit.addEqualTo("nodeInstance.routeNodeInstanceId", nodeInstanceId);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public boolean doesDocumentHaveUserRequest(String workflowId, Long documentId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", documentId);
    	crit.addEqualTo("recipientTypeCd", EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
    	crit.addEqualTo("workflowId", workflowId);
    	crit.addEqualTo("currentIndicator", Boolean.TRUE);
    	int count = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(ActionRequestValue.class, crit));
    	return count > 0;
    }

    public List<Long> getRequestWorkgroupIds(Long documentId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", documentId);
    	crit.addEqualTo("recipientTypeCd", EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
    	crit.addEqualTo("currentIndicator", Boolean.TRUE);

    	ReportQueryByCriteria query = QueryFactory.newReportQuery(ActionRequestValue.class, crit);
    	query.setAttributes(new String[] { "workgroupId" });

    	List<Long> workgroupIds = new ArrayList<Long>(10);
    	Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    	while (iter.hasNext()) {
			Object[] row = (Object[]) iter.next();
			BigDecimal id = (BigDecimal)row[0];
			workgroupIds.add((Long)id.longValue());
		}
    	return workgroupIds;
    }

}
