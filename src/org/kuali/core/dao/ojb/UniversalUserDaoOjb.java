/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.dao.ojb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.UniversalUserDao;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.lookup.LookupUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;

/**
 * This class is the OJB implementation of the UniversalUserDao interface.
 * 
 * 
 */
public class UniversalUserDaoOjb extends PlatformAwareDaoBaseOjb implements UniversalUserDao {
    public UniversalUser getUser(org.kuali.core.bo.user.UserId userId) throws UserNotFoundException {
        UniversalUser user = null;

        Criteria criteria = new Criteria();
        if (userId instanceof org.kuali.core.bo.user.AuthenticationUserId) {
            criteria.addEqualTo("personUserIdentifier", StringUtils.upperCase(((org.kuali.core.bo.user.AuthenticationUserId) userId).getAuthenticationId()));
        }
        if (userId instanceof org.kuali.core.bo.user.PersonPayrollId) {
            criteria.addEqualTo("personPayrollIdentifier", ((org.kuali.core.bo.user.PersonPayrollId) userId).getPayrollId());
        }
        if (userId instanceof org.kuali.core.bo.user.UuId) {
            criteria.addEqualTo("personUniversalIdentifier", ((org.kuali.core.bo.user.UuId) userId).getUuId());
        }
        if (userId instanceof org.kuali.core.bo.user.PersonTaxId) {
            criteria.addEqualTo("personTaxIdentifier", ((org.kuali.core.bo.user.PersonTaxId) userId).getTaxId());
        }
        return (UniversalUser) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(UniversalUser.class, criteria));
    }
    
    public WorkflowUser getWorkflowUser(edu.iu.uis.eden.user.UserId userId) throws EdenUserNotFoundException {
        if ((userId == null) || userId.isEmpty()) {
            throw new EdenUserNotFoundException("Attempting to lookup user with null or empty id");
        }
        Criteria criteria = new Criteria();
        if (userId instanceof edu.iu.uis.eden.user.AuthenticationUserId) {
            criteria.addEqualTo("authenticationUserId", ((edu.iu.uis.eden.user.AuthenticationUserId) userId).getAuthenticationId().trim().toUpperCase());
        }
        if (userId instanceof edu.iu.uis.eden.user.EmplId) {
            criteria.addEqualTo("emplId", ((edu.iu.uis.eden.user.EmplId) userId).getEmplId().trim().toUpperCase());
        }
        if (userId instanceof WorkflowUserId) {
            criteria.addEqualTo("uuId", ((WorkflowUserId) userId).getWorkflowId().trim().toUpperCase());
        }
        if (userId instanceof edu.iu.uis.eden.user.UuId) {
            criteria.addEqualTo("uuId", ((edu.iu.uis.eden.user.UuId) userId).getUuId().trim().toUpperCase());
        }
        WorkflowUser user = (WorkflowUser) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(org.kuali.core.workflow.bo.WorkflowUser.class, criteria));
        if (user == null) {
            throw new EdenUserNotFoundException(new StringBuffer("Unable to locate user with ").append(userId.getClass().getName()).append(": ").append(userId.getId()).toString());
        }
        return user;
    }

    public void save(WorkflowUser workflowUser) {
        getPersistenceBrokerTemplate().store(workflowUser);
    }

    public List search(WorkflowUser user, boolean useWildCards) {
        Criteria criteria = new Criteria();
        if (user != null) {
            if ((user.getAuthenticationUserId() != null) && StringUtils.isNotBlank(user.getAuthenticationUserId().getAuthenticationId())) {
                criteria.addLike("authenticationUserId", user.getAuthenticationUserId().getAuthenticationId().trim().toUpperCase() + "%");
            }
            if ((user.getEmplId() != null) && StringUtils.isNotBlank(user.getEmplId().getEmplId())) {
                criteria.addLike("emplId", user.getEmplId().getEmplId().trim().toUpperCase() + "%");
            }
            if ((user.getUuId() != null) && StringUtils.isNotBlank(user.getUuId().getUuId())) {
                criteria.addLike("uuId", user.getUuId().getUuId().trim().toUpperCase() + "%");
            }
            if ((user.getWorkflowUserId() != null) && StringUtils.isNotBlank(user.getWorkflowUserId().getWorkflowId())) {
                criteria.addLike("workflowUserId", user.getWorkflowUserId().getWorkflowId().trim().toUpperCase() + "%");
            }
            if (StringUtils.isNotBlank(user.getGivenName())) {
                criteria.addLike("givenName", user.getGivenName().trim().toUpperCase() + "%");
            }
            if (StringUtils.isNotBlank(user.getLastName())) {
                criteria.addLike("lastName", user.getLastName().trim().toUpperCase() + "%");
            }
            if (StringUtils.isNotBlank(user.getDisplayName())) {
                criteria.addLike("displayName", user.getDisplayName().trim().toUpperCase().trim() + "%");
            }
            if (StringUtils.isNotBlank(user.getEmailAddress())) {
                criteria.addLike("emailAddress", user.getEmailAddress().trim() + "%");
            }
        }
        LookupUtils.applySearchResultsLimit(criteria, getDbPlatform());
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(org.kuali.core.workflow.bo.WorkflowUser.class, criteria)));
    }
    
 
}