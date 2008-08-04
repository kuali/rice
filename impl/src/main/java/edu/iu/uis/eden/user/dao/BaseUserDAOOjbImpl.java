/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.user.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.BaseWorkflowUser;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUserId;

public class BaseUserDAOOjbImpl extends PersistenceBrokerDaoSupport implements BaseUserDAO {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    public BaseWorkflowUser getWorkflowUser(UserId userId) throws EdenUserNotFoundException {
    	if (userId == null) {
    		throw new IllegalArgumentException("UserId must be non-null.");
    	}
        BaseWorkflowUser user = (BaseWorkflowUser) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(BaseWorkflowUser.class, getUserCriteria(userId)));
        return getReturnSafeWorkflowUser(user);
    }

    /**
     * Not sure if this is necessary for the simple DAO but we'll leave it in anyway ;)
     */
    private BaseWorkflowUser getReturnSafeWorkflowUser(BaseWorkflowUser workflowUser) {
        // special case handling for the case where we only have a workflow id
        // and an emplid
        if (workflowUser != null) {
            if (workflowUser.getAuthenticationUserId() == null || workflowUser.getAuthenticationUserId().getAuthenticationId() == null) {
                workflowUser.setAuthenticationUserId(new AuthenticationUserId(workflowUser.getWorkflowUserId().getWorkflowId()));
            }
            if (workflowUser.getDisplayName() == null) {
                workflowUser.setDisplayName(workflowUser.getWorkflowUserId().getWorkflowId());
            }
            if (workflowUser.getEmailAddress() == null) {
                workflowUser.setEmailAddress("");
            }
            if (workflowUser.getGivenName() == null) {
                workflowUser.setGivenName("");
            }
            if (workflowUser.getLastName() == null) {
                workflowUser.setLastName(workflowUser.getWorkflowUserId().getWorkflowId());
            }
        }
        return workflowUser;
    }

    public List getSearchResults(String lastName, String firstName, String authenticationUserId, String workflowId, String emplId, String uuId) {
    	Criteria crit = new Criteria();
    	if (!StringUtils.isEmpty(lastName)) {
    		crit.addEqualTo("lastName", lastName);
    	}
    	if (!StringUtils.isEmpty(firstName)) {
    		crit.addEqualTo("givenName", firstName);
    	}
    	if (!StringUtils.isEmpty(workflowId)) {
    		crit.addEqualTo("workflowUserId", workflowId);
    	}
    	if (!StringUtils.isEmpty(authenticationUserId)) {
    		crit.addEqualTo("authenticationUserId", authenticationUserId);
    	}
    	if (!StringUtils.isEmpty(emplId)) {
    		crit.addEqualTo("emplId", emplId);
    	}
    	if (!StringUtils.isEmpty(uuId)) {
        	crit.addEqualTo("uuId", uuId);
    	}
    	return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BaseWorkflowUser.class, crit));
    }


    private Criteria getUserCriteria(UserId userId) throws EdenUserNotFoundException {
        Criteria crit = new Criteria();
        if (userId.isEmpty()) {
            LOG.error("Attempting to lookup user with empty Id " + userId);
            throw new EdenUserNotFoundException("Attempting to lookup user with empty Id");
        }
        if (userId instanceof EmplId) {
            LOG.debug("Creating example user with EMPLID " + userId.toString());
            crit.addEqualTo("emplId", ((EmplId) userId).getEmplId());
        } else if (userId instanceof UuId) {
            LOG.debug("Creating example user with UUID " + userId.toString());
            crit.addEqualTo("uuId", ((UuId) userId).getUuId());
        } else if (userId instanceof AuthenticationUserId) {
            LOG.debug("Creating example user with AuthenticationUserId " + ((AuthenticationUserId) userId).getAuthenticationId());
            crit.addEqualTo("authenticationUserId", ((AuthenticationUserId) userId).getAuthenticationId());
        } else if (userId instanceof WorkflowUserId) {
            LOG.debug("Creating example user with WorkflowUserId " + ((WorkflowUserId) userId).getWorkflowId());
            crit.addEqualTo("workflowUserId", ((WorkflowUserId) userId).getWorkflowId());
        }
        return crit;
    }
    
    public void save(BaseWorkflowUser user){
        getPersistenceBrokerTemplate().store(user);
    }
    
}