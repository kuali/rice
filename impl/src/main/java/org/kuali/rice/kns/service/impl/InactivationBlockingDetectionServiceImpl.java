/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.InactivationBlockingDetectionService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Transactional
public class InactivationBlockingDetectionServiceImpl implements InactivationBlockingDetectionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivationBlockingDetectionServiceImpl.class);

    protected BusinessObjectMetaDataService businessObjectMetaDataService;
    protected PersistenceStructureService persistenceStructureService;
    protected BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.rice.kns.service.InactivationBlockingDetectionService#listAllBlockerRecords(org.kuali.rice.kns.datadictionary.InactivationBlockingDefinition)
     */
    @SuppressWarnings("unchecked")
	public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
    	Map<String, Object> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Checking for blocker records for object: " + blockedBo );
    		LOG.debug("    With Metadata: " + inactivationBlockingMetadata );
    		LOG.debug("    Resulting Query Map: " + queryMap );
    	}
    	if (queryMap != null) {
    		return businessObjectService.findMatching(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
    	}
        // if queryMap were null, means that we couldn't perform a query, and hence, need to return false
    	return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.kns.service.InactivationBlockingDetectionService#hasABlockingRecord(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata)
     */
    public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Map<String, Object> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
        if (queryMap != null) {
            return businessObjectService.countMatching(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap) > 0;
        }
        // if queryMap were null, means that we couldn't perform a query, and hence, need to return false
        return false;
    }

    protected Map<String, Object> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        BusinessObject blockingBo = (BusinessObject) ObjectUtils.createNewObjectFromClass(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());
        
        BusinessObjectRelationship businessObjectRelationship = businessObjectMetaDataService.getBusinessObjectRelationship(blockingBo, inactivationBlockingMetadata.getBlockedReferencePropertyName());
        
        // note, this method assumes that all PK fields of the blockedBo have a non-null and, for strings, non-blank values
        if (businessObjectRelationship != null) {
            Map<String, String> parentToChildReferences = businessObjectRelationship.getParentToChildReferences();
            Map<String, Object> queryMap = new HashMap<String, Object>();
            for (Map.Entry<String, String> parentToChildReference : parentToChildReferences.entrySet()) {
                String fieldName = parentToChildReference.getKey();
                Object fieldValue = ObjectUtils.getPropertyValue(blockedBo, parentToChildReference.getValue());
                queryMap.put(fieldName, fieldValue);
            }
            addBlockableRowProperty(queryMap, blockedBo, inactivationBlockingMetadata);
            
            return queryMap;
        }
        return null;
    }

    /**
     * Adds a mapping in queryMap so that a query will only return the rows in a table that may block inactivation
     * 
     * @param queryMap a map of field name-value mappings
     * @param blockedBo the BO potentially blocked from inactivation
     * @param inactivationBlockingMetadata
     */
    protected void addBlockableRowProperty(Map<String, Object> queryMap, BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        queryMap.put(retrieveActiveIndicatorFieldName(blockedBo, inactivationBlockingMetadata), Boolean.TRUE);
    }
    
    /**
     * Retrieves the field name corresponding to the active indicator
     * 
     * @param blockedBo
     * @param inactivationBlockingMetadata
     * @return the active indicator field name
     */
    @SuppressWarnings("unchecked")
	protected String retrieveActiveIndicatorFieldName(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Class<? extends BusinessObject> blockingBoClass = inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass();
        if (Inactivateable.class.isAssignableFrom(blockingBoClass) && persistenceStructureService.listFieldNames(blockingBoClass).contains(KNSPropertyConstants.ACTIVE)) {
            return KNSPropertyConstants.ACTIVE;
        }
        LOG.error("Default implementation of inactivation blocking requires a class of type Inactivateable and to have a field named \"active\".  Found class was of type " + blockedBo.getClass().getName());
        throw new RuntimeException("Default implementation of inactivation blocking requires a class of type Inactivateable and to have a field named \"active\".  Found class was of type " + blockedBo.getClass().getName());
    }
    
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
