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

import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.LegacyDataFramework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was originally introduced a proxy to decide between OJB and JPA implementations, but new work on krad-data
 * module has rendered this service deprecated so this implementation is largely vestigial.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated use new KRAD Data framework {@link org.kuali.rice.krad.data.DataObjectService}
 */
@Deprecated
@LegacyDataFramework
public class PersistenceStructureServiceImpl extends PersistenceServiceImplBase implements PersistenceStructureService {

	/**
	 *
	 * special case when the attributeClass passed in doesnt match the class of
	 * the reference-descriptor as defined in ojb-repository. Currently the only
	 * case of this happening is ObjectCode vs. ObjectCodeCurrent.
	 *
	 * NOTE: This method makes no real sense and is a product of a hack
	 * introduced by KFS for an unknown reason. If you find yourself using this
	 * map stop and go do something else.
	 *
	 * @param from
	 *            the class in the code
	 * @param to
	 *            the class in the repository
	 */
	public static Map<Class, Class> referenceConversionMap = new HashMap<Class, Class>();

	private PersistenceStructureService persistenceStructureServiceOjb;

	public void setPersistenceStructureServiceOjb(PersistenceStructureService persistenceStructureServiceOjb) {
		this.persistenceStructureServiceOjb = persistenceStructureServiceOjb;
	}

	private PersistenceStructureService getService(Class clazz) {
		//return (isJpaEnabledForKradClass(clazz)) ?
		//				persistenceStructureServiceJpa : persistenceStructureServiceOjb;
        // TODO remove this entirely, we are no longer sending any JPA stuff through this class as this class is now legacy!
        return persistenceStructureServiceOjb;
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#isPersistable(java.lang.Class)
	 */

	@Override
	public boolean isPersistable(Class clazz) {
		return getService(clazz).isPersistable(clazz);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getPrimaryKeys(java.lang.Class)
	 */

	@Override
	public List getPrimaryKeys(Class clazz) {
		return getService(clazz).getPrimaryKeys(clazz);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceMetadataExplorerService#listFieldNames(java.lang.Class)
	 */

	@Override
	public List listFieldNames(Class clazz) {
		return getService(clazz).listFieldNames(clazz);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceMetadataService#clearPrimaryKeyFields(java.lang.Object)
	 */
	// Unit tests only
	@Override
	public Object clearPrimaryKeyFields(Object persistableObject) {
		return getService(persistableObject.getClass()).clearPrimaryKeyFields(persistableObject);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceMetadataExplorerService#listPersistableSubclasses(java.lang.Class)
	 */

	// Unit tests only
	@Override
	public List listPersistableSubclasses(Class superclazz) {
		return getService(superclazz).listPersistableSubclasses(superclazz);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getRelationshipMetadata(java.lang.Class,
	 *      java.lang.String)
	 */

	@Override
	public Map<String, DataObjectRelationship> getRelationshipMetadata(Class persistableClass, String attributeName, String attributePrefix) {
		return getService(persistableClass).getRelationshipMetadata(persistableClass, attributeName, attributePrefix);
	}


	// Unit tests only
	@Override
	public Map<String, DataObjectRelationship> getRelationshipMetadata(Class persistableClass, String attributeName) {
		return getService(persistableClass).getRelationshipMetadata(persistableClass, attributeName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getForeignKeyFieldName(java.lang.Object,
	 *      java.lang.String, java.lang.String)
	 */

	@Override
	public String getForeignKeyFieldName(Class persistableObjectClass, String attributeName, String pkName) {
		return getService(persistableObjectClass).getForeignKeyFieldName(persistableObjectClass, attributeName, pkName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getReferencesForForeignKey(java.lang.Class,
	 *      java.lang.String)
	 */

	@Override
	public Map getReferencesForForeignKey(Class persistableObjectClass, String attributeName) {
		return getService(persistableObjectClass).getReferencesForForeignKey(persistableObjectClass, attributeName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getForeignKeysForReference(java.lang.Class, java.lang.String)
	 */

	@Override
	public Map getForeignKeysForReference(Class clazz, String attributeName) {
		return getService(clazz).getForeignKeysForReference(clazz, attributeName);
	}


	@Override
	public Map<String, String> getInverseForeignKeysForCollection(Class boClass, String collectionName) {
		return getService(boClass).getInverseForeignKeysForCollection(boClass, collectionName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getNestedForeignKeyMap(java.lang.Class)
	 */

	@Override
	public Map getNestedForeignKeyMap(Class persistableObjectClass) {
		return getService(persistableObjectClass).getNestedForeignKeyMap(persistableObjectClass);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceMetadataService#hasPrimaryKeyFieldValues(java.lang.Object)
	 */
	@Override
	public boolean hasPrimaryKeyFieldValues(Object persistableObject) {
		return getService(persistableObject.getClass()).hasPrimaryKeyFieldValues(persistableObject);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceService#getForeignKeyFieldsPopulationState(org.kuali.rice.krad.bo.BusinessObject,
	 *      java.lang.String)
	 */
	@Override
	public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(PersistableBusinessObject bo, String referenceName) {
		return getService(bo.getClass()).getForeignKeyFieldsPopulationState(bo, referenceName);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceStructureService#listReferenceObjectFieldNames(java.lang.Class)
	 */

	@Override
	public Map<String, Class> listReferenceObjectFields(Class boClass) {
		return getService(boClass).listReferenceObjectFields(boClass);
	}


	@Override
	public Map<String, Class> listCollectionObjectTypes(Class boClass) {
		return getService(boClass).listCollectionObjectTypes(boClass);
	}

	@Override
	public Map<String, Class> listCollectionObjectTypes(PersistableBusinessObject bo) {
		return getService(bo.getClass()).listCollectionObjectTypes(bo);
	}

	/**
	 * @see org.kuali.rice.krad.service.PersistenceStructureService#listReferenceObjectFieldNames(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
	public Map<String, Class> listReferenceObjectFields(PersistableBusinessObject bo) {
		return getService(bo.getClass()).listReferenceObjectFields(bo);
	}


	@Override
	public boolean isReferenceUpdatable(Class boClass, String referenceName) {
		return getService(boClass).isReferenceUpdatable(boClass, referenceName);
	}


	@Override
	public boolean isCollectionUpdatable(Class boClass, String collectionName) {
		return getService(boClass).isCollectionUpdatable(boClass, collectionName);
	}


	@Override
	public boolean hasCollection(Class boClass, String collectionName) {
		return getService(boClass).hasCollection(boClass, collectionName);
	}


	@Override
	public boolean hasReference(Class boClass, String referenceName) {
		return getService(boClass).hasReference(boClass, referenceName);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.PersistenceStructureService#getTableName(java.lang.Class)
	 */
	@Override
	public String getTableName(
			Class<? extends PersistableBusinessObject> boClass) {
		return getService(boClass).getTableName(boClass);
	}


}
