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
package org.kuali.rice.krad.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.BusinessObjectRelationship;
import org.kuali.rice.krad.datadictionary.FieldDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.lookup.valuefinder.ValueFinder;

/**
 * Provides Metadata about a specific BusinessObject. Depending on the circumstance or type
 * of BO it will retrieve the data it needs from either the DataDictionary or through the
 * PersistenceStructureService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface BusinessObjectMetaDataService extends DataObjectMetaDataService {

	@Deprecated
	public BusinessObjectRelationship getBusinessObjectRelationship(RelationshipDefinition ddReference,
			BusinessObject bo, Class boClass, String attributeName, String attributePrefix, boolean keysOnly);

	@Deprecated
	public RelationshipDefinition getBusinessObjectRelationshipDefinition(Class c, String attributeName);

	@Deprecated
	public RelationshipDefinition getBusinessObjectRelationshipDefinition(BusinessObject bo, String attributeName);

	/**
	 * 
	 * This method returns a list of inquirable field names
	 * 
	 * @param bo
	 * @return a collection of inquirable field names
	 */
	@Deprecated
	public Collection<String> getInquirableFieldNames(Class boClass, String sectionTitle);

	/**
	 * 
	 * This method returns a list of lookupable fields
	 * 
	 * @param bo
	 * @return a collection of lookupable fields
	 */
	@Deprecated
	public List<String> getLookupableFieldNames(Class boClass);

	/**
	 * 
	 * This method looks up the default value for a given attribute and returns
	 * it
	 * 
	 * @param businessObjectClass
	 * @param attributeName
	 * @return default value for an attribute
	 */
	@Deprecated
	public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName);

	/**
	 * 
	 * This method returns the value finder class for a given attribute
	 * 
	 * @param businessObjectClass
	 * @param attributeName
	 * @return value finder class
	 */
	@Deprecated
	public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName);

	/**
	 * 
	 * This method looks up the quickfinder parameter string for a given
	 * attribute and returns it. See
	 * {@link FieldDefinition#getQuickfinderParameterString()}.
	 * 
	 * @param businessObjectClass
	 * @param attributeName
	 * @return default values for attributes
	 */
	@Deprecated
	public String getLookupFieldQuickfinderParameterString(Class businessObjectClass, String attributeName);

	/**
	 * This method returns the quickfinder parameter string builder class for a
	 * given attribute. See
	 * {@link FieldDefinition#getQuickfinderParameterStringBuilderClass()}.
	 * 
	 * @param businessObjectClass
	 * @param attributeName
	 * @return value finder class
	 */
	@Deprecated
	public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(Class businessObjectClass,
			String attributeName);

	/**
	 * 
	 * This method returns a list of collection names a business object contains
	 * 
	 * @param bo
	 * @return
	 */
	@Deprecated
	public Collection<String> getCollectionNames(BusinessObject bo);

	/**
	 * 
	 * This method determines if a given field(attribute) is inquirable or not
	 * This handles both nested and non-nested attributes
	 * 
	 * @param bo
	 * @param attributeName
	 * @param sectionTitle
	 * @return true if field is inquirable
	 */
	@Deprecated
	public boolean isAttributeInquirable(Class boClass, String attributeName, String sectionTitle);

	/**
	 * 
	 * This method determines if a given business object is inquirable
	 * 
	 * @param bo
	 * @return true if bo is inquirable
	 */
	@Deprecated
	public boolean isInquirable(Class boClass);

	/**
	 * 
	 * This method determines if a given field(attribute) is lookupable or not
	 * This handles both nested and non-nested attributes
	 * 
	 * @param bo
	 * @param attributeName
	 * @return true if field is lookupable
	 */
	@Deprecated
	public boolean isAttributeLookupable(Class boClass, String attributeName);

	/**
	 * 
	 * This method determines if a given business object is lookupable
	 * 
	 * @param bo
	 * @return true if bo is lookupable
	 */
	@Deprecated
	public boolean isLookupable(Class boClass);

	/**
	 * 
	 * This method will return a class that is related to the parent BO (either
	 * through the DataDictionary or through the PersistenceStructureService)
	 * 
	 * @param bo
	 * @param attributes
	 * @return related class
	 */
	@Deprecated
	public BusinessObjectRelationship getBusinessObjectRelationship(BusinessObject bo, String attributeName);

	@Deprecated
	public BusinessObjectRelationship getBusinessObjectRelationship(BusinessObject bo, Class boClass,
			String attributeName, String attributePrefix, boolean keysOnly);



	/**
	 * Get all the business object relationships for the given business object.
	 * These relationships may be defined at the ORM-layer or within the data
	 * dictionary.
	 */
	@Deprecated
	public List<BusinessObjectRelationship> getBusinessObjectRelationships(BusinessObject bo);

	/**
	 * Get all the business object relationships for the given class. These
	 * relationships may be defined at the ORM-layer or within the data
	 * dictionary.
	 */
	@Deprecated
	public List<BusinessObjectRelationship> getBusinessObjectRelationships(Class<? extends BusinessObject> boClass);

	/**
	 * This method accepts a business object and one of its foreign key
	 * attribute names. It returns a map that has a foreign key attribute name
	 * as a key and its respective related class as value. If the passed in
	 * attributeName is not a foreign key, this method will return an empty map.
	 * 
	 * @param BusinessObject
	 *            businessObject
	 * @param String
	 *            attributeName
	 * @return Map<String, Class>
	 */
	@Deprecated
	public Map<String, Class> getReferencesForForeignKey(BusinessObject businessObject, String attributeName);

	/**
	 * 
	 * This method ...
	 * 
	 * @param businessObjectClass
	 * @param attributeName
	 * @param targetName
	 * @return
	 */
	@Deprecated
	public String getForeignKeyFieldName(Class businessObjectClass, String attributeName, String targetName);
}
