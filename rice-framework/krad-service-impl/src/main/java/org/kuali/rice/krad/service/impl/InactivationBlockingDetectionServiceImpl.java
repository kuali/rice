/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.InactivationBlockingDetectionService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs checking of inactivation blocking 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class InactivationBlockingDetectionServiceImpl implements InactivationBlockingDetectionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivationBlockingDetectionServiceImpl.class);

    protected DataObjectMetaDataService dataObjectMetaDataService;
    private volatile DataObjectService dataObjectService;
    protected LegacyDataAdapter legacyDataAdapter;
    
    /**
     * Note we are checking the active getting after retrieving potential blocking records instead of setting criteria on the
	 * active field. This is because some implementations of {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable} might not have the active field, for example
	 * instances of {@link org.kuali.rice.krad.bo.InactivatableFromTo}
	 * 
     * @see org.kuali.rice.krad.service.InactivationBlockingDetectionService#listAllBlockerRecords(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    @Override
	public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		Collection<BusinessObject> blockingRecords = new ArrayList<BusinessObject>();

		Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking for blocker records for object: " + blockedBo);
			LOG.debug("    With Metadata: " + inactivationBlockingMetadata);
			LOG.debug("    Resulting Query Map: " + queryMap);
		}

		if (queryMap != null) {
			Collection potentialBlockingRecords = legacyDataAdapter.findMatching(
					inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
			for (Iterator iterator = potentialBlockingRecords.iterator(); iterator.hasNext();) {
				MutableInactivatable businessObject = (MutableInactivatable) iterator.next();
				if (businessObject.isActive()) {
					blockingRecords.add((BusinessObject) businessObject);
				}
			}
		}

		return blockingRecords;
	}

	/**
	 * Note we are checking the active getting after retrieving potential blocking records instead of setting criteria on the
	 * active field. This is because some implementations of {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable} might not have the active field, for example
	 * instances of {@link org.kuali.rice.krad.bo.InactivatableFromTo}
	 * 
	 * @see org.kuali.rice.krad.service.InactivationBlockingDetectionService#hasABlockingRecord(org.kuali.rice.krad.bo.BusinessObject,
	 *      org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)
	 * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
	 */
    @Deprecated
    @Override
	public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		boolean hasBlockingRecord = false;

		Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
		if (queryMap != null) {
			Collection potentialBlockingRecords = legacyDataAdapter.findMatching(
					inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
			for (Iterator iterator = potentialBlockingRecords.iterator(); iterator.hasNext();) {
				MutableInactivatable businessObject = (MutableInactivatable) iterator.next();
				if (businessObject.isActive()) {
					hasBlockingRecord = true;
					break;
				}
			}
		}

		// if queryMap were null, means that we couldn't perform a query, and hence, need to return false
		return hasBlockingRecord;
	}

    @Deprecated
	protected Map<String, String> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		BusinessObject blockingBo = (BusinessObject) KRADUtils.createNewObjectFromClass(
                inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());

		org.kuali.rice.krad.bo.DataObjectRelationship dataObjectRelationship = legacyDataAdapter
				.getDataObjectRelationship(blockingBo, blockedBo.getClass(),
                        inactivationBlockingMetadata.getBlockedReferencePropertyName(), "", true, false, false);

        RelationshipDefinition relationshipDefinition = KRADServiceLocatorWeb.getLegacyDataAdapter().getDictionaryRelationship(blockedBo.getClass(),inactivationBlockingMetadata.getBlockedReferencePropertyName());

		// note, this method assumes that all PK fields of the blockedBo have a non-null and, for strings, non-blank values
		if (dataObjectRelationship != null) {
			Map<String, String> parentToChildReferences = dataObjectRelationship.getParentToChildReferences();
			Map<String, String> queryMap = new HashMap<String, String>();
			for (Map.Entry<String, String> parentToChildReference : parentToChildReferences.entrySet()) {
				String fieldName = parentToChildReference.getKey();
				Object fieldValue = KradDataServiceLocator.getDataObjectService().wrap(blockedBo).getPropertyValueNullSafe(parentToChildReference.getValue());
				if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
					queryMap.put(fieldName, fieldValue.toString());
				} else {
					LOG.error("Found null value for foreign key field " + fieldName
							+ " while building inactivation blocking query map.");
					throw new RuntimeException("Found null value for foreign key field '" + fieldName
							+ "' while building inactivation blocking query map.");
				}
			}

			return queryMap;
		}

		return null;
	}

    /**
     * Implementation which calls the legacy {@link #hasABlockingRecord(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)}
     * if the given data object is a legacy object. Calls new code to make the equivalent check if the given object is
     * non-legacy.
     */
    @Override
    public boolean detectBlockingRecord(Object dataObject, InactivationBlockingMetadata inactivationBlockingMetadata) {
        if (LegacyUtils.useLegacyForObject(dataObject)) {
            return hasABlockingRecord((BusinessObject)dataObject, inactivationBlockingMetadata);
        }
        QueryByCriteria criteria = buildInactivationBlockerCriteria(dataObject, inactivationBlockingMetadata);
        if (criteria != null) {
            Class<?> blockingType = inactivationBlockingMetadata.getBlockingDataObjectClass();
            QueryResults<?> potentialBlockingRecords = getDataObjectService().findMatching(blockingType, criteria);
            for (Object result : potentialBlockingRecords.getResults()) {
                if (!(result instanceof Inactivatable)) {
                    throw new IllegalStateException("Blocking records must implement Inactivatable, but encountered one which does not: " + result);
                }
                Inactivatable inactivatable = (Inactivatable)result;
                if (inactivatable.isActive()) {
                    return true;
                }
            }
        }
        // if criteria is null, means that we couldn't perform a query, and hence, need to return false
        return false;
    }

    /**
     * Implementation which calls the legacy {@link #listAllBlockerRecords(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)}
     * if the given data object is a legacy object. Calls new code to make the equivalent check if the given object is
     * non-legacy.
     */
    @Override
    public Collection<?> detectAllBlockingRecords(Object dataObject,
            InactivationBlockingMetadata inactivationBlockingMetadata) {
        if (LegacyUtils.useLegacyForObject(dataObject)) {
            return listAllBlockerRecords((BusinessObject) dataObject, inactivationBlockingMetadata);
        }
        List<Object> blockingRecords = new ArrayList<Object>();

        QueryByCriteria criteria = buildInactivationBlockerCriteria(dataObject, inactivationBlockingMetadata);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking for blocker records for object: " + dataObject);
            LOG.debug("    With Metadata: " + inactivationBlockingMetadata);
            LOG.debug("    Resulting QueryByCriteria: " + criteria);
        }

        if (criteria != null) {
            Class<?> blockingType = inactivationBlockingMetadata.getBlockingDataObjectClass();
            QueryResults<?> potentialBlockingRecords = getDataObjectService().findMatching(blockingType, criteria);
            for (Object result : potentialBlockingRecords.getResults()) {
                if (!(result instanceof Inactivatable)) {
                    throw new IllegalStateException("Blocking records must implement Inactivatable, but encountered one which does not: " + result);
                }
                Inactivatable inactivatable = (Inactivatable)result;
                if (inactivatable.isActive()) {
                    blockingRecords.add(result);
                }
            }
        }

        return blockingRecords;
    }

    protected QueryByCriteria buildInactivationBlockerCriteria(Object blockedObject, InactivationBlockingMetadata inactivationBlockingMetadata) {
        DataObjectMetadata blockingObjectMetadata =
                getDataObjectService().getMetadataRepository().getMetadata(inactivationBlockingMetadata.getBlockingDataObjectClass());
        DataObjectRelationship dataObjectRelationship =
                blockingObjectMetadata.getRelationship(inactivationBlockingMetadata.getBlockedAttributeName());

        // note, this method assumes that all PK fields of the blockedBo have a non-null and, for strings, non-blank values
        if (dataObjectRelationship != null && !dataObjectRelationship.getAttributeRelationships().isEmpty()) {
            DataObjectWrapper<?> wrap = getDataObjectService().wrap(blockedObject);
            List<Predicate> predicates = new ArrayList<Predicate>();
            for (DataObjectAttributeRelationship relationship : dataObjectRelationship.getAttributeRelationships()) {
                String fieldName = relationship.getParentAttributeName();
                Object fieldValue = wrap.getPropertyValue(relationship.getChildAttributeName());
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    predicates.add(PredicateFactory.equal(fieldName, fieldValue));
                }
            }
            return QueryByCriteria.Builder.fromPredicates(predicates.toArray(new Predicate[predicates.size()]));
        }
        return null;
    }


    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    public DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KradDataServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
