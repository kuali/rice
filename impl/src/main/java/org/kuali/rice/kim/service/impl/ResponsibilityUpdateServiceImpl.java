/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityAttributeBo;
import org.kuali.rice.kim.service.ResponsibilityUpdateService;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityUpdateServiceImpl extends ResponsibilityServiceBase implements ResponsibilityUpdateService {
	private static final Logger LOG = Logger.getLogger( ResponsibilityUpdateServiceImpl.class );
	// --------------------
    // ResponsibilityUpdateService methods
    // --------------------

    public void saveResponsibility(String responsibilityId,
    		String responsibilityTemplateId, String namespaceCode, String name,
    		String description, boolean active, AttributeSet responsibilityDetails ) {
    	// look for an existing responsibility of the given type
    	try {
	    	KimResponsibilityImpl resp = getBusinessObjectService().findBySinglePrimaryKey(KimResponsibilityImpl.class, responsibilityId);
	    	if ( resp == null ) {
	    		resp = new KimResponsibilityImpl();
	    		resp.setResponsibilityId(responsibilityId);
	    	}
	    	resp.setTemplateId(responsibilityTemplateId);
	    	resp.refreshReferenceObject( "template" );
	    	resp.setNamespaceCode(namespaceCode);
	    	resp.setName(name);
	    	resp.setDescription(description);
	    	resp.setActive(active);
	    	AttributeSet attributesToAdd = new AttributeSet( responsibilityDetails );
	    	List<ResponsibilityAttributeBo> details = resp.getDetailObjects();
	    	Iterator<ResponsibilityAttributeBo> detailIter = details.iterator();
	    	while ( detailIter.hasNext() ) {
	    		ResponsibilityAttributeBo detail = detailIter.next();
	    		String attrName = detail.getKimAttribute().getAttributeName();
	    		String attrValue = attributesToAdd.get(attrName);
	    		// if not present in the list or is blank, remove from the list
	    		if ( StringUtils.isBlank(attrValue) ) {
	    			detailIter.remove();
	    		} else {
	    			detail.setAttributeValue(attrValue);
	    		}
	    		// remove from detail map - used to add new ones later
	    		attributesToAdd.remove(attrName);
	    	}
	    	for ( Entry<String,String> attrEntry : attributesToAdd.entrySet() ) {

                KimTypeAttribute attr = KimApiServiceLocator.getKimTypeInfoService().getKimType(resp.getTemplate().getKimTypeId()).getAttributeDefinitionByName(attrEntry.getKey());
	    		if (attr != null && StringUtils.isNotBlank(attrEntry.getValue()) ) {
	    			ResponsibilityAttributeBo newDetail = new ResponsibilityAttributeBo();
	    			newDetail.setId(getNewAttributeDataId());
	    			newDetail.setKimAttributeId(attr.getKimAttribute().getId());
	    			newDetail.setKimTypeId(resp.getTemplate().getKimTypeId());
	    			newDetail.setAssignedToId(responsibilityId);
	    			newDetail.setAttributeValue(attrEntry.getValue());
	    			details.add(newDetail);
	    		}
	    	}
	    	getBusinessObjectService().save(resp);
	    	// flush the IdM service caches
	    	KimApiServiceLocator.getIdentityManagementService().flushResponsibilityCaches();
	    	// flush the local implementation class cache
	    	flushResponsibilityImplCache();
    	} catch ( RuntimeException ex ) {
    		LOG.error( "Exception in saveResponsibility: ", ex );
    		throw ex;
    	}
    }

}
