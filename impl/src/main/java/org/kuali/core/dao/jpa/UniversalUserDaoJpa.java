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
package org.kuali.core.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.UniversalUserDao;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.rice.jpa.criteria.Criteria;
import org.kuali.rice.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;


/**
 * This class is the JPA implementation of the UniversalUserDao interface.
 */
public class UniversalUserDaoJpa implements UniversalUserDao {
	
	@PersistenceContext
	private EntityManager entityManager;

	public UniversalUser getUser(org.kuali.core.bo.user.UserId userId) throws UserNotFoundException {
		Criteria criteria = new Criteria(UniversalUser.class.getName());
		if (userId instanceof org.kuali.core.bo.user.AuthenticationUserId) {
			criteria.eq("personUserIdentifier", StringUtils.upperCase(((org.kuali.core.bo.user.AuthenticationUserId) userId).getAuthenticationId()));
		}
		if (userId instanceof org.kuali.core.bo.user.PersonPayrollId) {
			criteria.eq("personPayrollIdentifier", ((org.kuali.core.bo.user.PersonPayrollId) userId).getPayrollId());
		}
		if (userId instanceof org.kuali.core.bo.user.UuId) {
			criteria.eq("personUniversalIdentifier", ((org.kuali.core.bo.user.UuId) userId).getUuId());
		}
		if (userId instanceof org.kuali.core.bo.user.PersonTaxId) {
			criteria.eq("personTaxIdentifier", ((org.kuali.core.bo.user.PersonTaxId) userId).getTaxId());
		}
		return (UniversalUser) new QueryByCriteria(entityManager, criteria).toQuery().getSingleResult();
	}

	public WorkflowUser getWorkflowUser(org.kuali.rice.kew.user.UserId userId) throws KEWUserNotFoundException {
		if ((userId == null) || userId.isEmpty()) {
			throw new KEWUserNotFoundException("Attempting to lookup user with null or empty id");
		}
		Criteria criteria = new Criteria(org.kuali.core.workflow.bo.WorkflowUser.class.getName());
		if (userId instanceof org.kuali.rice.kew.user.AuthenticationUserId) {
			criteria.eq("authenticationUserId", ((org.kuali.rice.kew.user.AuthenticationUserId) userId).getAuthenticationId().trim().toUpperCase());
		}
		if (userId instanceof org.kuali.rice.kew.user.EmplId) {
			criteria.eq("emplId", ((org.kuali.rice.kew.user.EmplId) userId).getEmplId().trim().toUpperCase());
		}
		if (userId instanceof WorkflowUserId) {
			criteria.eq("uuId", ((WorkflowUserId) userId).getWorkflowId().trim().toUpperCase());
		}
		if (userId instanceof org.kuali.rice.kew.user.UuId) {
			criteria.eq("uuId", ((org.kuali.rice.kew.user.UuId) userId).getUuId().trim().toUpperCase());
		}
		WorkflowUser user = (WorkflowUser) new QueryByCriteria(entityManager, criteria).toQuery().getSingleResult();
		if (user == null) {
			throw new KEWUserNotFoundException(new StringBuffer("Unable to locate user with ").append(userId.getClass().getName()).append(": ").append(userId.getId()).toString());
		}
		return user;
	}

	public void save(WorkflowUser workflowUser) {
		entityManager.merge(workflowUser);
	}

	public List search(WorkflowUser user, boolean useWildCards) {
		Criteria criteria = new Criteria(org.kuali.core.workflow.bo.WorkflowUser.class.getName());
		if (user != null) {
			if ((user.getAuthenticationUserId() != null) && StringUtils.isNotBlank(user.getAuthenticationUserId().getAuthenticationId())) {
				criteria.like("authenticationUserId", user.getAuthenticationUserId().getAuthenticationId().trim().toUpperCase() + "%");
			}
			if ((user.getEmplId() != null) && StringUtils.isNotBlank(user.getEmplId().getEmplId())) {
				criteria.like("emplId", user.getEmplId().getEmplId().trim().toUpperCase() + "%");
			}
			if ((user.getUuId() != null) && StringUtils.isNotBlank(user.getUuId().getUuId())) {
				criteria.like("uuId", user.getUuId().getUuId().trim().toUpperCase() + "%");
			}
			if ((user.getWorkflowUserId() != null) && StringUtils.isNotBlank(user.getWorkflowUserId().getWorkflowId())) {
				criteria.like("workflowUserId", user.getWorkflowUserId().getWorkflowId().trim().toUpperCase() + "%");
			}
			if (StringUtils.isNotBlank(user.getGivenName())) {
				criteria.like("givenName", user.getGivenName().trim().toUpperCase() + "%");
			}
			if (StringUtils.isNotBlank(user.getLastName())) {
				criteria.like("lastName", user.getLastName().trim().toUpperCase() + "%");
			}
			if (StringUtils.isNotBlank(user.getDisplayName())) {
				criteria.like("displayName", user.getDisplayName().trim().toUpperCase().trim() + "%");
			}
			if (StringUtils.isNotBlank(user.getEmailAddress())) {
				criteria.like("emailAddress", user.getEmailAddress().trim() + "%");
			}
		}
		Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(org.kuali.core.workflow.bo.WorkflowUser.class);
		return new ArrayList(new QueryByCriteria(entityManager, criteria).toQuery().setMaxResults(searchResultsLimit).getResultList());
	}

}