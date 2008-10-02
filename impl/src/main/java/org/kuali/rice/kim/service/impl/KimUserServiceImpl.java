/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.dto.EmplIdDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.UuIdDTO;
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.BaseUserService;
import org.kuali.rice.kew.user.BaseWorkflowUser;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.UserCapabilities;
import org.kuali.rice.kew.user.UuId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.xml.UserXmlHandler;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.impl.EntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimUserServiceImpl extends BaseUserService {

	protected PersonService<Person> personService;

	protected UserCapabilities workflowUserCapabilities;

	public UserCapabilities getCapabilities() {
		if (workflowUserCapabilities == null) {
			workflowUserCapabilities = super.capabilities;
		}
		return workflowUserCapabilities;
	}

	public void setWorkflowUserCapabilities(UserCapabilities workflowUserCapabilities) {
		this.workflowUserCapabilities = workflowUserCapabilities;
	}

	public void save(WorkflowUser user) {
		if (user == null) {
			return;
		}
		Long entityId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("kr_kim_ENTITY_ENT_TYPE_ID_seq");
		KimEntityImpl entity = new KimEntityImpl();
		entity.setActive(true);
		entity.setEntityId("" + entityId);
		
		Long entityTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("kr_kim_ENTITY_ENT_TYPE_ID_seq");
		EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
		entity.getEntityTypes().add(entityType);
		entityType.setEntityTypeCode("PERSON");
		entityType.setEntityId(entity.getEntityId());
		entityType.setEntityEntityTypeId(""+entityTypeId);
		entityType.setActive(true);
		
		Long entityNameId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("kr_kim_entity_name_id_seq");
		EntityNameImpl name = new EntityNameImpl();
		name.setActive(true);
		name.setEntityNameId("" + entityNameId);
		name.setEntityId(entity.getEntityId());
		name.setNameTypeCode("PREFERRED");
		name.setFirstName(user.getGivenName());
		name.setMiddleName("");
		name.setLastName(user.getLastName());
		name.setDefault(true);
		
		entity.getNames().add(name);
				
		KNSServiceLocator.getBusinessObjectService().save(entity);
		
		if (!StringUtils.isBlank(user.getEmailAddress())) {
			Long emailId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("kr_kim_entity_email_id_seq");
			EntityEmailImpl email = new EntityEmailImpl();
			email.setActive(true);
			email.setEntityEmailId("" + emailId);
			email.setEntityTypeCode("PERSON");
			email.setEmailTypeCode("CAMPUS");
			email.setEmailAddress(user.getEmailAddress());
			email.setDefault(true);
			email.setEntityId(entity.getEntityId());
			KNSServiceLocator.getBusinessObjectService().save(email);
		}
		
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setActive(true);
		principal.setPrincipalName(user.getAuthenticationUserId().getId());
		principal.setPrincipalId(user.getWorkflowId());
		principal.setEntityId(entity.getEntityId());
		KNSServiceLocator.getBusinessObjectService().save(principal);
	}

	public WorkflowUser getWorkflowUser(UserIdDTO userId) throws KEWUserNotFoundException {
		return getWorkflowUser(getWorkflowUserId(userId));
	}

	public WorkflowUser getWorkflowUser(org.kuali.rice.kew.user.UserId userId) throws KEWUserNotFoundException {
		WorkflowUser user = getFromCache(userId);
		if (user == null) {
			Person person = getPersonFromUserId(userId);
			if (person != null) {
				user = convertPersonToWorkflowUser( person );
				addToCache(user);
			} else {
				throw new KEWUserNotFoundException("User is invalid. userId " + userId.toString());
			}
		}
		return user;
	}

	public List search(WorkflowUser user, boolean useWildcards) {
		Map<String, String> criteria = new HashMap<String, String>();
		if (user != null) {
			if ((user.getAuthenticationUserId() != null) && StringUtils.isNotEmpty(user.getAuthenticationUserId().getAuthenticationId())) {
				criteria.put("principalName", user.getAuthenticationUserId().getAuthenticationId().trim() + "*");
			}
			if ((user.getEmplId() != null) && StringUtils.isNotEmpty(user.getEmplId().getEmplId())) {
				criteria.put("externalIdentifiers.externalIdentifierTypeCode", "emplId");
				criteria.put("externalIdentifiers.externalId", user.getEmplId().getEmplId().trim() + "*");
			}
			if ((user.getUuId() != null) && StringUtils.isNotEmpty(user.getUuId().getUuId())) {
				criteria.put("principalId", user.getUuId().getUuId().trim() + "*");
			}
			if ((user.getWorkflowUserId() != null) && StringUtils.isNotEmpty(user.getWorkflowUserId().getWorkflowId())) {
				criteria.put("principalId", user.getWorkflowUserId().getWorkflowId().trim() + "*");
			}
			if (StringUtils.isNotEmpty(user.getGivenName())) {
				criteria.put("firstName", user.getGivenName().trim() + "*");
			}
			if (StringUtils.isNotEmpty(user.getLastName())) {
				criteria.put("lastName", user.getLastName().trim() + "*");
			}
			// TODO: Not sure about parsing display name... need to look into workflow to see about this,
			// if (StringUtils.isNotBlank(user.getDisplayName())) {
			// criteria.put("displayName",
			// user.getDisplayName().trim().toUpperCase().trim() + "%");
			// }
			if (StringUtils.isNotEmpty(user.getEmailAddress())) {
				criteria.put("emailAddress", user.getEmailAddress().trim() + "*");
			}
		}

		List workflowUsers = new ArrayList();
		for (Person person : getPersonService().findPeople(criteria)) {
			workflowUsers.add(convertPersonToWorkflowUser(person));
		}

		return workflowUsers;
	}

	public static WorkflowUser convertPersonToWorkflowUser(Person person) {
		BaseWorkflowUser user = new org.kuali.rice.kns.workflow.bo.WorkflowUser();
		user.setWorkflowUserId(new WorkflowUserId(person.getPrincipalId()));
		user.setLockVerNbr(1);
		user.setAuthenticationUserId(new org.kuali.rice.kew.user.AuthenticationUserId(person.getPrincipalName()));
		user.setDisplayName(person.getName());
		user.setEmailAddress(person.getEmailAddress());
		user.setEmplId(new EmplId(person.getExternalId("EMPLOYEE")));
		user.setGivenName(person.getFirstName());
		user.setLastName(person.getLastName());
		user.setUuId(new UuId(person.getPrincipalId()));
		user.setCreateDate(new Timestamp(new Date().getTime()));
		user.setLastUpdateDate(new Timestamp(new Date().getTime()));
		return user;
	}

	private org.kuali.rice.kew.user.UserId getWorkflowUserId(UserIdDTO userId) throws KEWUserNotFoundException {
		org.kuali.rice.kew.user.UserId userIdInterface = null;
		if (userId instanceof EmplIdDTO) {
			userIdInterface = new org.kuali.rice.kew.user.EmplId(((EmplIdDTO) userId).getEmplId());
		} else if (userId instanceof NetworkIdDTO) {
			userIdInterface = new org.kuali.rice.kew.user.AuthenticationUserId(((NetworkIdDTO) userId).getNetworkId());
		} else if (userId instanceof UuIdDTO) {
			userIdInterface = new org.kuali.rice.kew.user.UuId(((UuIdDTO) userId).getUuId());
		} else if (userId instanceof WorkflowIdDTO) {
			userIdInterface = new WorkflowUserId(((WorkflowIdDTO) userId).getWorkflowId());
		} else {
			throw new KEWUserNotFoundException("Attempting to fetch user with unknown id type");
		}
		return userIdInterface;
	}

	private Person getPersonFromUserId(org.kuali.rice.kew.user.UserId userId) {
		Person person = null;
		if (userId instanceof AuthenticationUserId) {
			person = getPersonService().getPersonByPrincipalName(((AuthenticationUserId) userId).getAuthenticationId());
		} else if (userId instanceof EmplId) {
			person = getPersonService().getPersonByExternalIdentifier("emplId", ((EmplId) userId).getEmplId()).get(0);
		} else if (userId instanceof UuId) {
			person = getPersonService().getPerson( ((UuId) userId).getUuId() );
		} else if (userId instanceof WorkflowUserId) {
			person = getPersonService().getPerson(((WorkflowUserId) userId).getWorkflowId());
		}
		return person;
	}

	public WorkflowUser getBlankUser() {
		return new org.kuali.rice.kns.workflow.bo.WorkflowUser();
	}

	public void loadXml(InputStream stream, WorkflowUser user) {
        try {
            List parsedUsers = new UserXmlHandler().parseUserEntries(this, stream);
            for(Iterator iter = parsedUsers.iterator(); iter.hasNext();) {
            	BaseWorkflowUser xmlUser = (BaseWorkflowUser) iter.next();
            	save(xmlUser);
            }
        } catch (Exception e) {
        	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	}
            throw new RuntimeException("Caught Exception parsing user xml.", e);
        }
    }

	public PersonService<Person> getPersonService() {
		if (personService == null) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

}
