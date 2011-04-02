/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;

/**
 * Provides metadata such as relationships and key fields for data objects
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectMetaDataService {

    /**
     * Checks the DataDictionary and OJB Repository File to determine the primary
     * fields names for a given class.
     *
     * @param clazz The Class to check for primary keys
     * @return a list of the primary key field names or an empty list if none are found
     */
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz);
   
    /**
     * @param DataObject object whose primary key field name,value pairs you want
     * @return a Map containing the names and values of fields for the given class which
     *         are designated as key fields in the OJB repository file or DataDictionary
     * @throws IllegalArgumentException if the given Object is null
     */
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject);

    /**
     * @param persistableObject object whose primary key field name,value pairs you want
     * @param sortFieldNames if true, the returned Map will iterate through its entries sorted by fieldName
     * @return a Map containing the names and values of fields for the given class which
     *         are designated as key fields in the OJB repository file or DataDictionary
     * @throws IllegalArgumentException if the given Object is null
     */
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames);

    /**
     * Compares two dataObject instances for equality of type and key values using toString()
     * of each value for comparison purposes.
     * 
     * @param do1
     * @param do2
     * @return boolean indicating whether the two objects are equal.
     */
    public boolean equalsByPrimaryKeys(Object do1, Object do2);
    
	/**
	 * Attempts to find a relationship for the given attribute within the given
	 * data object
	 * 
	 * <p>
	 * First the data dictionary is queried to find any relationship definitions
	 * setup that include the attribute, if found the
	 * <code>BusinessObjectRetationship</code> is build from that. If not and
	 * the data object class is persistent, relationships are retrieved from the
	 * persistence service. Nested attributes are handled in addition to
	 * external business objects. If multiple relationships are found, the one
	 * that contains the least amount of joining keys is returned
	 * </p>
	 * 
	 * @param dataObject
	 *            - data object instance that contains the attribute
	 * @param dataObjectClass
	 *            - class for the data object that contains the attribute
	 * @param attributeName
	 *            - property name for the attribute
	 * @param attributePrefix
	 *            - property prefix for the attribute
	 * @param keysOnly
	 *            - indicates whether only primary key fields should be returned
	 *            in the relationship
	 * @param supportsLookup
	 *            - indicates whether the relationship should support lookup
	 * @param supportsInquiry
	 *            - indicates whether the relationship should support inquiry
	 * @return BusinessObjectRelationship for the attribute, or null if not
	 *         found
	 */
	public BusinessObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
			String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
			boolean supportInquiry);

	/**
	 * This method fetches the RelationshipDefinition using the parameters.
	 * 
	 * @param dataObjectClass - data object class that contains the attribute
	 * @param attributeName - property name for the attribute
	 * @return RelationshipDefinition for the attribute, or null if not found
	 */
	public RelationshipDefinition getDictionaryRelationship(Class<?> dataObjectClass, String attributeName);
	
	/**
     * Returns the attribute to be associated with for object level markings.  This would
     * be the field chosen for inquiry links etc.
     * 
     * @param dataObjectClass - data object class to obtain title attribute of
     * @return property name of title attribute or null if data object entry not found
     * @throws IllegalArgumentException
     *             if the given Class is null
     */
    public String getTitleAttribute(Class<?> dataObjectClass);

}
