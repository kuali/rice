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
package org.kuali.rice.kew.actionrequest.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.dao.ActionRequestDAO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Group;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a description of what this class does - sgibson don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionRequestDAOJpaImpl implements ActionRequestDAO {
    
    @PersistenceContext(name = "kew-unit")
    private EntityManager entityManager;

    /**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void delete(Long actionRequestId) {
        ActionRequestValue actionRequestValue = (ActionRequestValue) entityManager.find(ActionRequestValue.class, actionRequestId);
        entityManager.remove(actionRequestValue);
    }

    public void deleteByRouteHeaderId(Long routeHeaderId) {
        // FIXME should be jpa bulk update?
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindByRouteHeaderId");
        query.setParameter("routeHeaderId", routeHeaderId);
        List<ActionRequestValue> actionRequestValues = (List<ActionRequestValue>) query.getSingleResult();
        for(ActionRequestValue arv : actionRequestValues) {
            entityManager.remove(arv);
        }
    }

    public boolean doesDocumentHaveUserRequest(String principalId, Long documentId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.GetUserRequestCount");
        query.setParameter("principalId", principalId);
        query.setParameter("routeHeaderId", documentId);
        query.setParameter("recipientTypeCd", KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return ((Long)query.getSingleResult()) > 0;
    }

    public List<?> findActivatedByGroup(Group group) {
        
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindActivatedByGroup");
        query.setParameter("groupId", group.getGroupId());
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("status", KEWConstants.ACTION_REQUEST_ACTIVATED);
        
        return query.getResultList();
    }

    public List findAllByDocId(Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindAllByDocId");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return query.getResultList();
    }

    public List findAllPendingByDocId(Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindAllPendingByDocId");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ActionRequestValue> findAllRootByDocId(Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindAllRootByDocId");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return (List<ActionRequestValue>) query.getResultList();
    }

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindByRouteHeaderId");
        query.setParameter("routeHeaderId", routeHeaderId);
        
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ActionRequestValue> findByStatusAndDocId(String statusCd, Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindByStatusAndDocId");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("status", statusCd);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return (List<ActionRequestValue>)query.getResultList();
    }

    public List findPendingByActionRequestedAndDocId(String actionRequestedCd, Long routeHeaderId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingByActionRequestedAndDocId");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("actionRequested", actionRequestedCd);
        
        return query.getResultList();
    }

    public List findPendingByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingByDocIdAtOrBelowRouteLevel");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("routeLevel", routeLevel);
        query.setParameter("status", KEWConstants.ACTION_REQUEST_DONE_STATE);
        
        return query.getResultList();
    }

    public List findPendingByResponsibilityIds(Collection responsibilityIds) {
        if (responsibilityIds == null || responsibilityIds.size() == 0)
            return Collections.emptyList();

        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingByDocIdAtOrBelowRouteLevel");
        query.setParameter("responsibilityIds", responsibilityIds);
        
        return query.getResultList();
    }

    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingRootRequestsByDocIdAtOrBelowRouteLevel");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("status", KEWConstants.ACTION_REQUEST_DONE_STATE);
        query.setParameter("routeLevel", routeLevel);
        
        return query.getResultList();
    }

    public List findPendingRootRequestsByDocIdAtRouteLevel(Long routeHeaderId, Integer routeLevel) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingRootRequestsByDocIdAtRouteLevel");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("status", KEWConstants.ACTION_REQUEST_DONE_STATE);
        query.setParameter("routeLevel", routeLevel);
        
        return query.getResultList();
    }

    public List findPendingRootRequestsByDocIdAtRouteNode(Long routeHeaderId, Long nodeInstanceId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingRootRequestsByDocIdAtRouteNode");
        query.setParameter("routeHeaderId", routeHeaderId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("routeNodeInstanceId", nodeInstanceId);
        
        return query.getResultList();
    }

    public List findPendingRootRequestsByDocumentType(Long documentTypeId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindPendingRootRequestsByDocumentType");
        query.setParameter("documentTypeId", documentTypeId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        
        return query.getResultList();
    }

    public List findRootRequestsByDocIdAtRouteNode(Long documentId, Long nodeInstanceId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.FindRootRequestsByDocIdAtRouteNode");
        query.setParameter("routeHeaderId", documentId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("routeNodeInstanceId", nodeInstanceId);
        
        return query.getResultList();
    }

    public ActionRequestValue getActionRequestByActionRequestId(Long actionRequestId) {
        return entityManager.find(ActionRequestValue.class, actionRequestId);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRequestGroupIds(Long documentId) {
        Query query = entityManager.createNamedQuery("ActionRequestValue.GetRequestGroupIds");
        query.setParameter("routeHeaderId", documentId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("recipientTypeCd", KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD);
        
        return query.getResultList();
    }

    public void saveActionRequest(ActionRequestValue actionRequest) {
        if ( actionRequest.getAnnotation() != null && actionRequest.getAnnotation().length() > 2000 ) {
        	actionRequest.setAnnotation( StringUtils.abbreviate(actionRequest.getAnnotation(), 2000) );
        }
    	if(actionRequest.getActionRequestId() == null) {
        	loadDefaultValues(actionRequest);
        	entityManager.persist(actionRequest);
        }else{
            OrmUtils.merge(entityManager, actionRequest);
        }
    }
    private void loadDefaultValues(ActionRequestValue actionRequest) {
        checkNull(actionRequest.getActionRequested(), "action requested");
        checkNull(actionRequest.getResponsibilityId(), "responsibility ID");
        checkNull(actionRequest.getRouteLevel(), "route level");
        checkNull(actionRequest.getDocVersion(), "doc version");
        if (actionRequest.getForceAction() == null) {
            actionRequest.setForceAction(Boolean.FALSE);
        }
        if (actionRequest.getStatus() == null) {
            actionRequest.setStatus(KEWConstants.ACTION_REQUEST_INITIALIZED);
        }
        if (actionRequest.getPriority() == null) {
            actionRequest.setPriority(KEWConstants.ACTION_REQUEST_DEFAULT_PRIORITY);
        }
        if (actionRequest.getCurrentIndicator() == null) {
            actionRequest.setCurrentIndicator(true);
        }
        actionRequest.setCreateDate(new Timestamp(System.currentTimeMillis()));
    }
    //TODO Runtime might not be the right thing to do here...
    private void checkNull(Serializable value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("Null value for " + valueName);
        }
    }
    
	public List findActivatedByGroup(String groupId) {
		Query query = entityManager.createNamedQuery("ActionRequestValue.FindByStatusAndGroupId");
        query.setParameter("status", KEWConstants.ACTION_REQUEST_ACTIVATED);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("groupId", groupId);
        
        return query.getResultList();
	}
    
}
