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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.LegacyDataFramework;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * This class is the service implementation for the Persistence structure.
 * OjbRepositoryExplorer provides functions for extracting information from the
 * OJB repository at runtime. This is the default implementation, that is
 * delivered with Kuali.
 */
@Deprecated
@Transactional
@LegacyDataFramework
public class PersistenceServiceImpl extends PersistenceServiceImplBase implements PersistenceService {

	private static Logger LOG = Logger.getLogger(PersistenceServiceImpl.class);

    private KualiModuleService kualiModuleService;

	private PersistenceService persistenceServiceOjb;

	public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
		this.persistenceServiceOjb = persistenceServiceOjb;
	}

	private PersistenceService getService(Class clazz) {
    	if (isJpaEnabledForKradClass(clazz)) {
            throw new IllegalStateException("PersistenceService invoked for non-legacy class: " + clazz);
        }
		return persistenceServiceOjb;
	}

	// This method is for OJB specfic features. It is now being called directly where needed.
	@Override
	public void clearCache() {
		throw new UnsupportedOperationException("This should be called directly from the OJB Impl if needed.");
	}

	// This method is for OJB specfic features. It is now being called directly where needed.
	@Override
	public void loadRepositoryDescriptor(String ojbRepositoryFilePath) {
		throw new UnsupportedOperationException("This should be called directly from the OJB Impl if needed.");
	}

	@Override
	public Object resolveProxy(Object o) {
		return getService(o.getClass()).resolveProxy(o);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#retrieveNonKeyFields(java.lang.Object)
	 */
	@Override
	public void retrieveNonKeyFields(Object persistableObject) {
        if (persistableObject != null &&
                ExternalizableBusinessObjectUtils.isExternalizableBusinessObject(persistableObject.getClass())) {
            //
            // special handling for EBOs
            //
            Map<String, ?> criteria = KRADServiceLocatorWeb.getLegacyDataAdapter().getPrimaryKeyFieldValues(persistableObject);
            if (!CollectionUtils.isEmpty(criteria)) {
                ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(persistableObject.getClass());
                if (moduleService != null) {
                    Class<? extends ExternalizableBusinessObject> clazz =
                            ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(persistableObject.getClass());
                    ExternalizableBusinessObject freshEbo = moduleService.getExternalizableBusinessObject(clazz, (Map<String, Object>)criteria);
                    if (freshEbo != null) {
                        BeanUtils.copyProperties(freshEbo, persistableObject);
                    }
                }
            }
        } else {
            getService(persistableObject.getClass()).retrieveNonKeyFields(persistableObject);
        }
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	@Override
	public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
		getService(persistableObject.getClass()).retrieveReferenceObject(persistableObject, referenceObjectName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	@Override
	public void retrieveReferenceObjects(Object persistableObject, List referenceObjectNames) {
		getService(persistableObject.getClass()).retrieveReferenceObjects(persistableObject, referenceObjectNames);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	@Override
	public void retrieveReferenceObjects(List persistableObjects, List referenceObjectNames) {
		if (persistableObjects == null) {
			throw new IllegalArgumentException("invalid (null) persistableObjects");
		}
		if (persistableObjects.isEmpty()) {
			throw new IllegalArgumentException("invalid (empty) persistableObjects");
		}
		if (referenceObjectNames == null) {
			throw new IllegalArgumentException("invalid (null) referenceObjectNames");
		}
		if (referenceObjectNames.isEmpty()) {
			throw new IllegalArgumentException("invalid (empty) referenceObjectNames");
		}

		for (Iterator i = persistableObjects.iterator(); i.hasNext();) {
			Object persistableObject = i.next();
			retrieveReferenceObjects(persistableObject, referenceObjectNames);
		}
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getFlattenedPrimaryKeyFieldValues(java.lang.Object)
	 */
	@Override
	public String getFlattenedPrimaryKeyFieldValues(Object persistableObject) {
		return getService(persistableObject.getClass()).getFlattenedPrimaryKeyFieldValues(persistableObject);
	}

	/**
	 * For each reference object to the parent persistableObject, sets the key
	 * values for that object. First, if the reference object already has a
	 * value for the key, the value is left unchanged. Otherwise, for
	 * non-anonymous keys, the value is taken from the parent object. For
	 * anonymous keys, all other persistableObjects are checked until a value
	 * for the key is found.
	 *
	 * @see org.kuali.rice.krad.service.PersistenceService#getReferencedObject(java.lang.Object,
	 *      org.apache.ojb.broker.metadata.ObjectReferenceDescriptor)
	 */
	@Override
	public void linkObjects(Object persistableObject) {
		getService(persistableObject.getClass()).linkObjects(persistableObject);
	}

	/**
	 *
	 * @see org.kuali.rice.krad.service.PersistenceService#allForeignKeyValuesPopulatedForReference(org.kuali.rice.krad.bo.BusinessObject,
	 *      java.lang.String)
	 */
	@Override
	public boolean allForeignKeyValuesPopulatedForReference(PersistableBusinessObject bo, String referenceName) {
		return getService(bo.getClass()).allForeignKeyValuesPopulatedForReference(bo, referenceName);
	}

	/**
	 *
	 * @see org.kuali.rice.krad.service.PersistenceService#refreshAllNonUpdatingReferences(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
	public void refreshAllNonUpdatingReferences(PersistableBusinessObject bo) {
		getService(bo.getClass()).refreshAllNonUpdatingReferences(bo);
	}

	/**
	 * Defers to the service for the given class
	 *
	 * @see org.kuali.rice.krad.service.PersistenceService#isProxied(java.lang.Object)
	 */
	@Override
	public boolean isProxied(Object bo) {
		return getService(bo.getClass()).isProxied(bo);
	}

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
