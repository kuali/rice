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
package org.kuali.rice.kew.workgroup.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.BaseWorkgroup;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.dao.BaseWorkgroupDAO;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class BaseWorkgroupDAOOjbImpl extends PersistenceBrokerDaoSupport implements BaseWorkgroupDAO {

	public void save(Workgroup workgroup) {
		// the OJB mapping does not automatically grab the id from the sequence, so if we attempt to save this document without
		// an id, we'll generate one before saving it
		if (workgroup.getWorkflowGroupId() == null || workgroup.getWorkflowGroupId().getGroupId() == null) {
			workgroup.setWorkflowGroupId(new WorkflowGroupId(KEWServiceLocator.getRouteHeaderService().getNextRouteHeaderId()));
		}
		getPersistenceBrokerTemplate().store(workgroup);
	}

	public List search(Workgroup workgroup, Map<String, String> extensionValues) {
		Criteria crit = buildStandardCriteria(workgroup, extensionValues);
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BaseWorkgroup.class, crit));
	}

	public List find(Workgroup workgroup, Map<String, String> extensionValues, String principalId) {
       return this.find(workgroup, extensionValues, KIMServiceLocator.getPersonService().getPerson(principalId));
	}

	public List find(Workgroup workgroup, Map<String, String> extensionValues, Person user) {
        Criteria crit = buildStandardCriteria(workgroup, extensionValues);

        Map<String,String> criteria = new HashMap<String,String>();
        if (user != null) {
        	if ((user.getPrincipalName() != null) && StringUtils.isNotEmpty(user.getPrincipalName())) {
				criteria.put("principalName", user.getPrincipalName().trim() + "*");
			}
			if ((user.getPrincipalId() != null) && StringUtils.isNotEmpty(user.getPrincipalId())) {
				criteria.put("principalId", user.getPrincipalId().trim() + "*");
			}
			if (StringUtils.isNotEmpty(user.getFirstName())) {
				criteria.put("firstName", user.getFirstName().trim() + "*");
			}
			if (StringUtils.isNotEmpty(user.getLastName())) {
				criteria.put("lastName", user.getLastName().trim() + "*");
			}
			if (StringUtils.isNotEmpty(user.getEmailAddress())) {
				criteria.put("emailAddress", user.getEmailAddress().trim() + "*");
			}


        	List<? extends Person> users = KIMServiceLocator.getPersonService().findPeople(criteria);
            if (users == null || users.size() == 0) {
                // this means the username they entered couldn't be found
                return new ArrayList();
            }
            Collection principalIds = new ArrayList();
            for (Iterator<? extends Person> iter = users.iterator(); iter.hasNext();) {
                Person p =  iter.next();
                principalIds.add(p.getPrincipalId());
            }
            crit.addIn("workgroupMembers.workflowId", principalIds);
            crit.addEqualTo("workgroupMembers.memberType", KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
        }
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BaseWorkgroup.class, crit));
	}

	protected Criteria buildStandardCriteria(Workgroup workgroup, Map<String, String> extensionValues) {
		Criteria crit = new Criteria();
		crit.addEqualTo("currentInd", new Boolean(true));
        if (workgroup.getActiveInd() != null) {
            crit.addEqualTo("activeInd", workgroup.getActiveInd());
        }
        if (!StringUtils.isEmpty(workgroup.getDescription())) {
            crit.addLike("UPPER(description)", "%" + workgroup.getDescription().toUpperCase() + "%");
        }
        if (workgroup.getGroupNameId() != null && !StringUtils.isEmpty(workgroup.getGroupNameId().getNameId())) {
            crit.addLike("UPPER(workgroupName)", "%"+workgroup.getGroupNameId().getNameId().toUpperCase()+"%");
        }
        if (!StringUtils.isBlank(workgroup.getWorkgroupType())) {
        	if (workgroup.getWorkgroupType().equals("Default")) {
        		Criteria orCrit = new Criteria();
        		Criteria nullType = new Criteria();
        		nullType.addIsNull("workgroupType");
        		Criteria legType = new Criteria();
        		legType.addEqualTo("workgroupType", KEWConstants.LEGACY_DEFAULT_WORKGROUP_TYPE);
        		orCrit.addOrCriteria(nullType);
        		orCrit.addOrCriteria(legType);
        		crit.addAndCriteria(orCrit);
        	} else {
        		crit.addEqualTo("workgroupType", workgroup.getWorkgroupType());
        	}
        }
        if (workgroup.getWorkflowGroupId() != null && workgroup.getWorkflowGroupId().getGroupId() != null) {
            crit.addEqualTo("workgroupId", workgroup.getWorkflowGroupId().getGroupId());
        }
        if (extensionValues != null && !extensionValues.isEmpty()) {
        	int index = 0;
        	for (String key : extensionValues.keySet()) {
            	String value = extensionValues.get(key);
                if (!StringUtils.isBlank(value)) {
                	Criteria extCrit = new Criteria();
                	extCrit.setAlias("EXT"+(index++));
                	extCrit.addEqualTo("extensions.data.key", key);
                	extCrit.addLike("UPPER(extensions.data.value)", ("%" + value.replace("*", "%") + "%").toUpperCase());
                	crit.addAndCriteria(extCrit);
                }
            }
        }
        return crit;
	}

	public Set<String> findWorkgroupNamesForUser(String workflowId) {
		Set<String> workgroupNames = new HashSet<String>();
		Criteria crit = new Criteria();
        crit.addEqualTo("workgroupMembers.workflowId", workflowId);
        crit.addEqualTo("currentInd", Boolean.TRUE);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(BaseWorkgroup.class, crit);
        query.setAttributes(new String[] { "workgroupName" });
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] names = (Object[])iterator.next();
            String name = (String)names[0];
            workgroupNames.add(name);
        }
        return workgroupNames;
	}

	public Set<Long> findWorkgroupIdsForUser(String workflowId) {
		Set<Long> workgroupIds = new HashSet<Long>();
		Criteria crit = new Criteria();
		crit.addEqualTo("workgroupMembers.workflowId", workflowId);
		crit.addEqualTo("currentInd", Boolean.TRUE);
		ReportQueryByCriteria query = QueryFactory.newReportQuery(BaseWorkgroup.class, crit);
		query.setAttributes(new String[] { "workgroupId" });
		Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
		while (iterator.hasNext()) {
		    Object[] ids = (Object[])iterator.next();
		    BigDecimal id = (BigDecimal)ids[0];
		    workgroupIds.add(id.longValue());
		}
		return workgroupIds;
	}


	public BaseWorkgroup findByWorkgroupId(Long workgroupId) {
		Criteria crit = new Criteria();
        crit.addEqualTo("workgroupId", workgroupId);
        crit.addEqualTo("currentInd", Boolean.TRUE);
        return (BaseWorkgroup) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(BaseWorkgroup.class, crit));
	}

	public List findByName(String workgroupName, boolean workgroupCurInd) {
		Criteria crit = new Criteria();
        crit.addEqualTo("workgroupName", workgroupName);
        crit.addEqualTo("currentInd", new Boolean(workgroupCurInd));
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BaseWorkgroup.class, crit));
	}

	public BaseWorkgroup findByName(String workgroupName) {
		List workgroups = findByName(workgroupName, true);
        if(workgroups.size() > 0){
    		return (BaseWorkgroup) workgroups.get(0);
        }
        return null;
    }

	public BaseWorkgroup findEnrouteWorkgroupByName(String workgroupName) {
		Criteria crit = new Criteria();
        crit.addEqualTo("workgroupName", workgroupName);
        QueryByCriteria query = new QueryByCriteria(BaseWorkgroup.class, crit);
        query.addOrderByDescending("versionNumber");
        Iterator workgroups = getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();
        while (workgroups.hasNext()) {
            return (BaseWorkgroup) workgroups.next();
        }
        return null;
	}

	public BaseWorkgroup findEnrouteWorkgroupById(Long workgroupId) {
		Criteria crit = new Criteria();
        crit.addEqualTo("workgroupId", workgroupId);
        QueryByCriteria query = new QueryByCriteria(BaseWorkgroup.class, crit);
        query.addOrderByDescending("versionNumber");
        Iterator workgroups = getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();
        while (workgroups.hasNext()) {
            return (BaseWorkgroup) workgroups.next();
        }
        return null;
	}

	public BaseWorkgroup findByDocumentId(Long documentId) {
		Criteria crit = new Criteria();
        crit.addEqualTo("documentId", documentId);
        return (BaseWorkgroup) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(BaseWorkgroup.class, crit));
	}

	public List<Long> getImmediateWorkgroupsGroupIds(Long workgroupId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("currentInd", Boolean.TRUE);
		crit.addEqualTo("workgroupMembers.workflowId", workgroupId.toString());
		crit.addEqualTo("workgroupMembers.memberType", KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD);
		ReportQueryByCriteria query = QueryFactory.newReportQuery(BaseWorkgroup.class, crit);
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
