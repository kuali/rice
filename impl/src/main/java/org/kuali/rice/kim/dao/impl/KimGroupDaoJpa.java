/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;

import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimGroupDaoJpa implements KimGroupDao {
	
    @PersistenceContext(unitName="kim-unit")
    private EntityManager entityManager;
    
    public List<GroupImpl> getGroups(Map<String,String> fieldValues) {
        Criteria crit = new Criteria(GroupImpl.class.getName());
        BusinessObjectEntry boEntry = KNSServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry("org.kuali.rice.kim.bo.impl.GroupImpl");
        List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        String kimTypeId = null;
        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
        	if (entry.getKey().equals("kimTypeId")) {
        		kimTypeId=entry.getValue();
        		break;
        	}
        }
        for (Entry<String, String> entry : fieldValues.entrySet()) {
        	if (StringUtils.isNotBlank(entry.getValue())) {
        		if (entry.getKey().contains(".")) {
        	        Criteria subCrit = new Criteria(GroupAttributeDataImpl.class.getName());
        			String value = entry.getValue().replace('*', '%');
        	        
        			subCrit.like("attributeValue",value);
        			subCrit.eq("kimAttributeId",entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().length()));
        			subCrit.eq("kimTypeId", kimTypeId);
        			
        			crit.and(subCrit);
        			
        		} else {
        			if (lookupNames.contains(entry.getKey())) {
            			String value = entry.getValue().replace('*', '%');
        				crit.like(entry.getKey(), value);
        			} else {
        				if (entry.getKey().equals(KIMPropertyConstants.Person.PRINCIPAL_NAME)) {
                	        Criteria subCrit = new Criteria(KimPrincipalImpl.class.getName());
                			String principalName = entry.getValue().replace('*', '%');
                			subCrit.like(KIMPropertyConstants.Person.PRINCIPAL_NAME, principalName );
                			subCrit.eq(KIMPropertyConstants.Person.PRINCIPAL_ID, crit.getAlias() + KIMPropertyConstants.KimMember.MEMBER_ID);
                	        
                	        Criteria memberSubCrit = new Criteria(GroupMemberImpl.class.getName());
                	        memberSubCrit.eq(KIMPropertyConstants.Group.GROUP_ID, crit.getAlias() + KIMPropertyConstants.Group.GROUP_ID);
                	        memberSubCrit.and(subCrit);
                			
                			crit.and(memberSubCrit);        					
        				}
        			}
        		}
        	}
        }
        return (List)new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
