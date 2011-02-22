/*
 * Copyright 2007 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.*;
import org.kuali.rice.kns.lookup.valuefinder.ValueFinder;
import org.kuali.rice.kns.service.*;
import org.kuali.rice.kns.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * Implementation of the <code>BusinessObjectMetaDataService</code> which uses
 * the following services to gather its meta data:
 *
 * @see BusinessObjectDictionaryService
 * @see DataDictionaryService
 * @see PersistenceStructureService
 */
public class BusinessObjectMetaDataServiceImpl implements BusinessObjectMetaDataService {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger .getLogger(BusinessObjectMetaDataServiceImpl.class);

	private BusinessObjectDictionaryService businessObjectDictionaryService;
	private DataDictionaryService dataDictionaryService;
	private PersistenceStructureService persistenceStructureService;
	private KualiModuleService kualiModuleService;

	public Collection<String> getCollectionNames(BusinessObject bo) {
		return dataDictionaryService.getDataDictionary()
				.getBusinessObjectEntry(bo.getClass().getName())
				.getCollectionNames();
	}

	public Collection<String> getInquirableFieldNames(Class boClass,
			String sectionTitle) {
		return businessObjectDictionaryService.getInquiryFieldNames(boClass,
				sectionTitle);
	}

	public List<String> getLookupableFieldNames(Class boClass) {
		return businessObjectDictionaryService.getLookupFieldNames(boClass);
	}

	public String getLookupFieldDefaultValue(Class businessObjectClass,
			String attributeName) {
		return businessObjectDictionaryService.getLookupFieldDefaultValue(
				businessObjectClass, attributeName);
	}

	public Class getLookupFieldDefaultValueFinderClass(
			Class businessObjectClass, String attributeName) {
		return businessObjectDictionaryService
				.getLookupFieldDefaultValueFinderClass(businessObjectClass,
						attributeName);
	}

	/** {@inheritDoc} */
	public String getLookupFieldQuickfinderParameterString(Class businessObjectClass,
			String attributeName) {
		return businessObjectDictionaryService.getLookupFieldQuickfinderParameterString(
				businessObjectClass, attributeName);
	}

	/** {@inheritDoc} */
	public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(
			Class businessObjectClass, String attributeName) {
		return businessObjectDictionaryService
				.getLookupFieldQuickfinderParameterStringBuilderClass(businessObjectClass,
						attributeName);
	}

	public boolean isAttributeInquirable(Class boClass, String attributeName,
			String sectionTitle) {
		Collection sections = businessObjectDictionaryService
				.getInquirySections(boClass);
		boolean isInquirable = true;

		Iterator iter = sections.iterator();

		while (iter.hasNext()) {
			InquirySectionDefinition def = (InquirySectionDefinition) iter
					.next();
			for (FieldDefinition field : def.getInquiryFields()) {
				if (field.getAttributeName().equalsIgnoreCase(attributeName)) {
					isInquirable = !field.isNoInquiry();
				}
			}
		}
		if (isInquirable) {
			Object obj = null;
			if (boClass != null
					&& BusinessObject.class.isAssignableFrom(boClass)) {
				obj = ObjectUtils.createNewObjectFromClass(boClass);
			}

			if (obj != null) {
				BusinessObject bo = (BusinessObject) obj;
				Class clazz = getNestedBOClass(bo, attributeName);
				if (clazz != null
						&& BusinessObject.class.isAssignableFrom(clazz)) {
					return businessObjectDictionaryService.isInquirable(clazz);
				} else {
					return false;
				}
			} else {
				return false;
			}

		}

		return isInquirable;
	}

	public boolean isInquirable(Class boClass) {
		boolean inquirable = false;
		ModuleService moduleService = kualiModuleService
				.getResponsibleModuleService(boClass);
		if (moduleService != null && moduleService.isExternalizable(boClass)) {
			inquirable = moduleService
					.isExternalizableBusinessObjectInquirable(boClass);
		} else {
			Boolean isLookupable = businessObjectDictionaryService
					.isInquirable(boClass);
			if (isLookupable != null) {
				inquirable = isLookupable.booleanValue();
			}
		}
		return inquirable;
	}

	public boolean isAttributeLookupable(Class boClass, String attributeName) {
		Object obj = null;
		if (boClass != null && BusinessObject.class.isAssignableFrom(boClass)) {
			obj = ObjectUtils.createNewObjectFromClass(boClass);
		}
		if (obj != null) {
			BusinessObject bo = (BusinessObject) obj;
			BusinessObjectRelationship relationship = getBusinessObjectRelationship(
					bo, attributeName);

			if (relationship != null
					&& relationship.getRelatedClass() != null
					&& BusinessObject.class.isAssignableFrom(relationship
							.getRelatedClass())) {
				return isLookupable(relationship.getRelatedClass());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isLookupable(Class boClass) {
		boolean lookupable = false;
		ModuleService moduleService = kualiModuleService
				.getResponsibleModuleService(boClass);
		if (moduleService != null && moduleService.isExternalizable(boClass)) {
			lookupable = moduleService
					.isExternalizableBusinessObjectLookupable(boClass);
		} else {
			Boolean isLookupable = businessObjectDictionaryService
					.isLookupable(boClass);
			if (isLookupable != null) {
				lookupable = isLookupable.booleanValue();
			}
		}
		return lookupable;
	}

	public BusinessObjectRelationship getBusinessObjectRelationship(
			BusinessObject bo, String attributeName) {
		return getBusinessObjectRelationship(bo, bo.getClass(), attributeName,
				"", true);
	}
	//TODO: four different exit points?!
	public BusinessObjectRelationship getBusinessObjectRelationship(
			RelationshipDefinition ddReference, BusinessObject bo,
			Class boClass, String attributeName, String attributePrefix,
			boolean keysOnly) {

		BusinessObjectRelationship relationship = null;

		// if it is nested then replace the bo and attributeName with the
		// sub-refs
		if (ObjectUtils.isNestedAttribute(attributeName)) {
			if (ddReference != null) {
				relationship = new BusinessObjectRelationship(boClass,
						ddReference.getObjectAttributeName(), ddReference
								.getTargetClass());
				for (PrimitiveAttributeDefinition def : ddReference
						.getPrimitiveAttributes()) {
					if (StringUtils.isNotBlank(attributePrefix)) {
						relationship.getParentToChildReferences().put(
								attributePrefix + "." + def.getSourceName(),
								def.getTargetName());
					} else {
						relationship.getParentToChildReferences().put(
								def.getSourceName(), def.getTargetName());
					}
				}
				if (!keysOnly) {
					for (SupportAttributeDefinition def : ddReference
							.getSupportAttributes()) {
						if (StringUtils.isNotBlank(attributePrefix)) {
							relationship.getParentToChildReferences()
									.put(
											attributePrefix + "."
													+ def.getSourceName(),
											def.getTargetName());
							if (def.isIdentifier()) {
								relationship
										.setUserVisibleIdentifierKey(attributePrefix
												+ "." + def.getSourceName());
							}
						} else {
							relationship.getParentToChildReferences().put(
									def.getSourceName(), def.getTargetName());
							if (def.isIdentifier()) {
								relationship.setUserVisibleIdentifierKey(def
										.getSourceName());
							}
						}
					}
				}
				return relationship;
			}
			// recurse down to the next object to find the relationship

			String localPrefix = StringUtils
					.substringBefore(attributeName, ".");
			String localAttributeName = StringUtils.substringAfter(
					attributeName, ".");
			if (bo == null) {
				bo = (BusinessObject) ObjectUtils
						.createNewObjectFromClass(boClass);
			}
			Class nestedClass = ObjectUtils.getPropertyType(bo, localPrefix,
					getPersistenceStructureService());
			String fullPrefix = localPrefix;
			if (StringUtils.isNotBlank(attributePrefix)) {
				fullPrefix = attributePrefix + "." + localPrefix;
			}

            try {
                if (BusinessObject.class.isAssignableFrom(nestedClass)) {
				    relationship = getBusinessObjectRelationship(null, nestedClass,
					    	localAttributeName, fullPrefix, keysOnly);
			    }
            } catch (NullPointerException e) {
                 System.err.println("bo: " + bo + " localPrefix: " + localPrefix + " localAttributeName: " + localAttributeName + " fullPrefix: "  + fullPrefix + " keysOnly: " + keysOnly);
            }

            return relationship;
		}
		int maxSize = Integer.MAX_VALUE;
		// try persistable reference first
		if (PersistableBusinessObject.class.isAssignableFrom(boClass) && persistenceStructureService.isPersistable(boClass) ) {
			Map<String, BusinessObjectRelationship> rels = persistenceStructureService
					.getRelationshipMetadata(boClass, attributeName,
							attributePrefix);
			if (rels.size() > 0) {
				for (BusinessObjectRelationship rel : rels.values()) {
					if (rel.getParentToChildReferences().size() < maxSize
							&& isLookupable(rel.getRelatedClass())) {
						maxSize = rel.getParentToChildReferences().size();
						relationship = rel;
					}
				}
			}
		} else {
			ModuleService moduleService = KNSServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(boClass);
			if(moduleService!=null && moduleService.isExternalizable(boClass)){
				relationship = getRelationshipMetadata(boClass, attributeName, attributePrefix);
				//relationship = moduleService.getBusinessObjectRelationship(boClass, attributeName, attributePrefix);
				if(relationship!=null) {
					return relationship;
				}
			}
		}

		// then check the DD for relationships defined there
		// TODO move out to a separate method
		// so that the logic for finding the relationships is similar to
		// primitiveReference
		if (ddReference != null
				&& isLookupable(ddReference.getTargetClass())
				&& bo != null
				&& ddReference.getPrimitiveAttributes().size() < maxSize ) {
			relationship = new BusinessObjectRelationship(boClass,
				ddReference.getObjectAttributeName(), ddReference
						.getTargetClass());
			for (PrimitiveAttributeDefinition def : ddReference
					.getPrimitiveAttributes()) {
				relationship.getParentToChildReferences().put(
						def.getSourceName(), def.getTargetName());
			}
			if (!keysOnly) {
				for (SupportAttributeDefinition def : ddReference
						.getSupportAttributes()) {
					relationship.getParentToChildReferences().put(
							def.getSourceName(), def.getTargetName());
				}
			}
		}

		return relationship;

	}

	public RelationshipDefinition getBusinessObjectRelationshipDefinition(
			Class c, String attributeName) {
		return getDDRelationship(c, attributeName);
	}

	public RelationshipDefinition getBusinessObjectRelationshipDefinition(
			BusinessObject bo, String attributeName) {
		return getBusinessObjectRelationshipDefinition(bo.getClass(),
				attributeName);
	}

	public BusinessObjectRelationship getBusinessObjectRelationship(
			BusinessObject bo, Class boClass, String attributeName,
			String attributePrefix, boolean keysOnly) {
		RelationshipDefinition ddReference = getBusinessObjectRelationshipDefinition(
				boClass, attributeName);
		return getBusinessObjectRelationship(ddReference, bo, boClass,
				attributeName, attributePrefix, keysOnly);
	}

	/**
	 * Gets the dataDictionaryService attribute.
	 *
	 * @return Returns the dataDictionaryService.
	 */
	public DataDictionaryService getDataDictionaryService() {
		return dataDictionaryService;
	}

	/**
	 * Sets the dataDictionaryService attribute value.
	 *
	 * @param dataDictionaryService
	 *            The dataDictionaryService to set.
	 */
	public void setDataDictionaryService(
			DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	/**
	 * Gets the businessObjectDictionaryService attribute.
	 *
	 * @return Returns the businessObjectDictionaryService.
	 */
	public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
		return businessObjectDictionaryService;
	}

	/**
	 * Sets the businessObjectDictionaryService attribute value.
	 *
	 * @param businessObjectDictionaryService
	 *            The BusinessObjectDictionaryService to set.
	 */
	public void setBusinessObjectDictionaryService(
			BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}

	/**
	 * Gets the persistenceStructureService attribute.
	 *
	 * @return Returns the persistenceStructureService.
	 */
	public PersistenceStructureService getPersistenceStructureService() {
		return persistenceStructureService;
	}

	/**
	 * Sets the persistenceStructureService attribute value.
	 *
	 * @param persistenceStructureService
	 *            The persistenceStructureService to set.
	 */
	public void setPersistenceStructureService(
			PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	/**
	 *
	 * This method retrieves the business object class for a specific attribute
	 *
	 * @param bo
	 * @param attributeName
	 * @return a business object class for a specific attribute
	 */
	private Class getNestedBOClass(BusinessObject bo, String attributeName) {

		String[] nestedAttributes = StringUtils.split(attributeName, ".");
		String attributeRefName = "";
		Class clazz = null;
		if (nestedAttributes.length > 1) {
			String attributeStringSoFar = "";
			for (int i = 0; i < nestedAttributes.length - 1; i++) {
				try {
					// we need to build a string of the attribute names
					// depending on which iteration we're in.
					// so if the original attributeName string we're using is
					// "a.b.c.d.e", then first iteration would use
					// "a", 2nd "a.b", 3rd "a.b.c", etc.
					if (i != 0) {
						attributeStringSoFar = attributeStringSoFar + ".";
					}
					attributeStringSoFar = attributeStringSoFar
							+ nestedAttributes[i];
					clazz = ObjectUtils.easyGetPropertyType(bo,
							attributeStringSoFar);
				} catch (InvocationTargetException ite) {
					LOG.info(ite);
					return null;
				} catch (NoSuchMethodException nsme) {
					LOG.info(nsme);
					return null;
				} catch (IllegalAccessException iae) {
					LOG.info(iae);
					return null;
				}
			}
		}
		return clazz;
	}

	public RelationshipDefinition getDDRelationship(Class c,
			String attributeName) {
		DataDictionaryEntry entryBase = dataDictionaryService
				.getDataDictionary().getDictionaryObjectEntry(c.getName());
		if (entryBase == null) {
			return null;
		}

		List<RelationshipDefinition> ddRelationships = entryBase
				.getRelationships();
		RelationshipDefinition relationship = null;
		int minKeys = Integer.MAX_VALUE;
		for (RelationshipDefinition def : ddRelationships) {
			// favor key sizes of 1 first
			if (def.getPrimitiveAttributes().size() == 1) {
				for (PrimitiveAttributeDefinition primitive : def
						.getPrimitiveAttributes()) {
					if (primitive.getSourceName().equals(attributeName)
							|| def.getObjectAttributeName().equals(
									attributeName)) {
						relationship = def;
						minKeys = 1;
						break;
					}
				}
			} else if (def.getPrimitiveAttributes().size() < minKeys) {
				for (PrimitiveAttributeDefinition primitive : def
						.getPrimitiveAttributes()) {
					if (primitive.getSourceName().equals(attributeName)
							|| def.getObjectAttributeName().equals(
									attributeName)) {
						relationship = def;
						minKeys = def.getPrimitiveAttributes().size();
						break;
					}
				}
			}
		}
		// check the support attributes
		if (relationship == null) {
			for (RelationshipDefinition def : ddRelationships) {
				if (def.hasIdentifier()) {
					if (def.getIdentifier().getSourceName().equals(
							attributeName)) {
						relationship = def;
					}
				}
			}
		}
		return relationship;
	}

	public List<BusinessObjectRelationship> getBusinessObjectRelationships( BusinessObject bo ) {
		if ( bo == null ) {
			return null;
		}
		return getBusinessObjectRelationships( bo.getClass() );
	}

	@SuppressWarnings("unchecked")
	public List<BusinessObjectRelationship> getBusinessObjectRelationships( Class<? extends BusinessObject> boClass) {
		if (boClass == null) {
			return null;
		}

		Map<String, Class> referenceClasses = null;
		if (PersistableBusinessObject.class.isAssignableFrom( boClass ) && getPersistenceStructureService().isPersistable(boClass)) {
			referenceClasses = getPersistenceStructureService().listReferenceObjectFields(boClass);
		}
		DataDictionaryEntry ddEntry = dataDictionaryService.getDataDictionary().getDictionaryObjectEntry(boClass.getName());
		List<RelationshipDefinition> ddRelationships = (ddEntry == null ? new ArrayList<RelationshipDefinition>() : ddEntry.getRelationships());
		List<BusinessObjectRelationship> relationships = new ArrayList<BusinessObjectRelationship>();

		// loop over all relationships
		if (referenceClasses != null) {
			for (Map.Entry<String, Class> entry : referenceClasses.entrySet()) {
				if (isLookupable(entry.getValue())) {
					Map<String, String> fkToPkRefs = persistenceStructureService.getForeignKeysForReference(boClass, entry.getKey());
					BusinessObjectRelationship rel = new BusinessObjectRelationship( boClass, entry.getKey(), entry.getValue() );
					for (Map.Entry<String, String> ref : fkToPkRefs.entrySet()) {
						rel.getParentToChildReferences().put(ref.getKey(), ref.getValue());
					}
					relationships.add(rel);
				}
			}
		}

		for (RelationshipDefinition rd : ddRelationships) {
			if (isLookupable(rd.getTargetClass())) {
				BusinessObjectRelationship rel = new BusinessObjectRelationship( boClass, rd.getObjectAttributeName(), rd.getTargetClass());
				for (PrimitiveAttributeDefinition def : rd.getPrimitiveAttributes()) {
					rel.getParentToChildReferences().put(def.getSourceName(),def.getTargetName());
				}
				relationships.add(rel);
			}
		}

		return relationships;
	}

	public Map<String, Class> getReferencesForForeignKey(BusinessObject bo,
			String attributeName) {
		List<BusinessObjectRelationship> businessObjectRelationships = getBusinessObjectRelationships(bo);
		Map<String, Class> referencesForForeignKey = new HashMap<String, Class>();
		for (BusinessObjectRelationship businessObjectRelationship : businessObjectRelationships) {
			if (businessObjectRelationship != null
					&& businessObjectRelationship.getParentToChildReferences() != null
					&& businessObjectRelationship.getParentToChildReferences()
							.containsKey(attributeName)) {
				referencesForForeignKey.put(businessObjectRelationship
						.getParentAttributeName(), businessObjectRelationship
						.getRelatedClass());
			}
		}
		return referencesForForeignKey;
	}

	public List listPrimaryKeyFieldNames(Class clazz) {
		if (persistenceStructureService.isPersistable(clazz)) {
			return persistenceStructureService.listPrimaryKeyFieldNames(clazz);
		}
		ModuleService responsibleModuleService = getKualiModuleService()
				.getResponsibleModuleService(clazz);
		if (responsibleModuleService != null
				&& responsibleModuleService.isExternalizable(clazz))
			return responsibleModuleService.listPrimaryKeyFieldNames(clazz);

		// give the option to declare primary keys in the dd.
		// this is primarly used for transient objects that lack db persistence.
		List<String> pks = dataDictionaryService.getDataDictionary()
			.getBusinessObjectEntry(clazz.getName()).getPrimaryKeys();
		if(pks != null && !pks.isEmpty())
			return pks;

		return new ArrayList();




	}

	public KualiModuleService getKualiModuleService() {
		return this.kualiModuleService;
	}

	public void setKualiModuleService(KualiModuleService kualiModuleService) {
		this.kualiModuleService = kualiModuleService;
	}

	public BusinessObjectRelationship getRelationshipMetadata(
			Class businessObjectClass, String attributeName, String attributePrefix) {

		RelationshipDefinition relationshipDefinition = getDDRelationship(businessObjectClass, attributeName);
		if(relationshipDefinition==null) return null;
		BusinessObjectRelationship businessObjectRelationship =
			new BusinessObjectRelationship(relationshipDefinition.getSourceClass(),
					relationshipDefinition.getObjectAttributeName(), relationshipDefinition.getTargetClass());
		if(!StringUtils.isEmpty(attributePrefix))
			attributePrefix += ".";
		List<PrimitiveAttributeDefinition> primitives = relationshipDefinition.getPrimitiveAttributes();
		for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitives){
			businessObjectRelationship.getParentToChildReferences().put(
					attributePrefix+primitiveAttributeDefinition.getSourceName(), primitiveAttributeDefinition.getTargetName());
		}
		/*List<SupportAttributeDefinition> supports = relationshipDefinition.getSupportAttributes();
		for(SupportAttributeDefinition supportAttributeDefinition: supports){
			rel.getParentToChildReferences().put(
					supportAttributeDefinition.getSourceName(), supportAttributeDefinition.getTargetName());
		}*/
		return businessObjectRelationship;
	}

	public String getForeignKeyFieldName(Class businessObjectClass, String attributeName, String targetName) {

		String fkName = "";

		// first try DD-based relationships
		RelationshipDefinition relationshipDefinition = getDDRelationship(businessObjectClass, attributeName);

		if(relationshipDefinition!=null){
			List<PrimitiveAttributeDefinition> primitives = relationshipDefinition.getPrimitiveAttributes();
			for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitives){
				if(primitiveAttributeDefinition.getTargetName().equals(targetName)){
					fkName = primitiveAttributeDefinition.getSourceName();
					break;
				}
			}
		}

		// if we can't find anything in the DD, then try the persistence service
		if(StringUtils.isBlank(fkName) && PersistableBusinessObject.class.isAssignableFrom(businessObjectClass) && getPersistenceStructureService().isPersistable(businessObjectClass)) {
			fkName =
				getPersistenceStructureService().getForeignKeyFieldName(businessObjectClass, attributeName, targetName);
		}
		return fkName;
	}
}
