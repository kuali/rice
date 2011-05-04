/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif.container;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.field.GroupField;
import org.kuali.rice.kns.uif.util.ComponentUtils;

/**
 * Holds field indexes of a <code>View</code> instance for retrieval
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewIndex implements Serializable {
	private static final long serialVersionUID = 4700818801272201371L;
	
	private Set<AttributeField> attributeFields;
	private Map<String, AttributeField> attributeFieldIndex;

	private Set<CollectionGroup> collections;
	private Map<String, CollectionGroup> collectionsIndex;

	public ViewIndex() {
		attributeFields = new HashSet<AttributeField>();
		collections = new HashSet<CollectionGroup>();
	}

	/**
	 * Creates the field indexes based on the currently held fields
	 * 
	 * <p>
	 * <code>AttributeField</code> instances are indexed by the attribute path.
	 * This is useful for retrieving the AttributeField based on the incoming
	 * request parameter
	 * </p>
	 * 
	 * <p>
	 * <code>CollectionGroup</code> instances are indexed by the collection
	 * path. This is useful for retrieving the CollectionGroup based on the
	 * incoming request parameter
	 * </p>
	 */
	public void index() {
		attributeFieldIndex = new HashMap<String, AttributeField>();
		for (AttributeField field : attributeFields) {
			attributeFieldIndex.put(field.getBindingInfo().getBindingPath(), field);
		}

		collectionsIndex = new HashMap<String, CollectionGroup>();
		for (CollectionGroup collection : collections) {
			collectionsIndex.put(collection.getBindingInfo().getBindingPath(), collection);
		}
	}

	/**
	 * Retrieves a <code>AttributeField</code> instance from the index
	 * 
	 * @param attributePath
	 *            - full path of the attribute (from the form)
	 * @return AttributeField instance for the path or Null if not found
	 */
	public AttributeField getAttributeFieldByPath(String attributePath) {
		return attributeFieldIndex.get(attributePath);
	}

	/**
	 * Set of <code>AttributeField</code> instances that are held in the index
	 * 
	 * @return Set<AttributeField>
	 */
	public Set<AttributeField> getAttributeFields() {
		return this.attributeFields;
	}

	/**
	 * Setter for the AttributeField set
	 * 
	 * @param attributeFields
	 */
	public void setAttributeFields(Set<AttributeField> attributeFields) {
		this.attributeFields = attributeFields;
	}

	/**
	 * Adds an <code>AttributeField</code> instance to the Set of fields to
	 * index
	 * 
	 * @param field
	 *            - AttributeField instance to index
	 */
	public void addAttributeField(AttributeField field) {
		attributeFields.add(field);
	}

	/**
	 * Adds any <code>AttributeField</code> instances to the Set of fields to
	 * index. If the field is a <code>GroupField</code> it is recursively
	 * checked for attribute field items
	 * 
	 * @param fields
	 *            - List of fields instances to add
	 */
	public void addFields(List<? extends Field> fields) {
		for (Field field : fields) {
			if (field instanceof AttributeField) {
				attributeFields.add((AttributeField) field);
			}
			else if (field instanceof GroupField) {
				List<? extends Field> groupFields = ComponentUtils.getComponentsOfType(((GroupField) field).getItems(),
						Field.class);
				addFields(groupFields);
			}
		}
	}

	/**
	 * Gets the Map that contains attribute field indexing information. The Map
	 * key points to an attribute binding path, and the Map value is the
	 * <code>AttributeField</code> instance
	 * 
	 * @return Map<String, AttributeField> attribute fields index map
	 */
	public Map<String, AttributeField> getAttributeFieldIndex() {
		return this.attributeFieldIndex;
	}

	/**
	 * Setter for the attribute fields index map
	 * 
	 * @param attributeFieldIndex
	 */
	public void setAttributeFieldIndex(Map<String, AttributeField> attributeFieldIndex) {
		this.attributeFieldIndex = attributeFieldIndex;
	}

	/**
	 * Set of <code>CollectionGroup</code> instances that are held in the index
	 * 
	 * @return Set<CollectionGroup>
	 */
	public Set<CollectionGroup> getCollections() {
		return this.collections;
	}

	/**
	 * Setter for the Set of <code>CollectionGroup</code> instances
	 * 
	 * @param collections
	 */
	public void setCollections(Set<CollectionGroup> collections) {
		this.collections = collections;
	}

	/**
	 * Adds a <code>CollectionGroup</code> instances to the set of collections
	 * to index
	 * 
	 * @param collectionGroup
	 *            - collection group instance to index
	 */
	public void addCollection(CollectionGroup collectionGroup) {
		collections.add(collectionGroup);
	}

	/**
	 * Gets the Map that contains collection indexing information. The Map key
	 * gives the binding path to the collection, and the Map value givens the
	 * <code>CollectionGroup</code> instance
	 * 
	 * @return Map<String, CollectionGroup> collection index map
	 */
	public Map<String, CollectionGroup> getCollectionsIndex() {
		return this.collectionsIndex;
	}

	/**
	 * Setter for the collections index map
	 * 
	 * @param collectionsIndex
	 */
	public void setCollectionsIndex(Map<String, CollectionGroup> collectionsIndex) {
		this.collectionsIndex = collectionsIndex;
	}

	/**
	 * Retrieves a <code>CollectionGroup</code> instance from the index
	 * 
	 * @param collectionPath
	 *            - full path of the collection (from the form)
	 * @return CollectionGroup instance for the collection path or Null if not
	 *         found
	 */
	public CollectionGroup getCollectionGroupByPath(String collectionPath) {
		return collectionsIndex.get(collectionPath);
	}

	/**
	 * This method finds the attribute field based on the binding path. First, it tries
	 * to find in the attribute collection. If not present there, search in the collection
	 * 
	 * @param bindingInfo based on this this, attribute field will be return
	 * @return AttributeField
	 */
	public AttributeField getAttributeField(BindingInfo bindingInfo){
		
		/**
		 * Find in the attribute index first.
		 */
    	AttributeField attributeField = getAttributeFieldByPath(bindingInfo.getBindingPath());
    	
    	if (attributeField == null){
    		
    		/**
			 * Lets search the collections (by collection's binding path)
			 */
			String path = bindingInfo.getBindingObjectPath() + "." + bindingInfo.getBindByNamePrefix();
			
			 CollectionGroup collectionGroup = getCollectionGroupByPath(stripIndexesFromPropertyPath(path));
			 if (collectionGroup != null){
    			 for (Component item : ((CollectionGroup)collectionGroup).getItems()) {
     				if (item instanceof AttributeField){
        				if (StringUtils.equals(((AttributeField)item).getPropertyName(), bindingInfo.getBindingName())){
        					attributeField = (AttributeField)item;
        					break;
        				}
     				}
     			}
			 }
    	}
    	
    	return attributeField;
    }
	
	/**
     * Strips indexes from the property path. 
     * bo.fiscalOfficer.accounts[0].name returns bo.fiscalOfficer.accounts.name which can be used 
     * to find the components from the CollectionGroup index
     * 
     */
    private String stripIndexesFromPropertyPath(String propertyPath){
    	String returnValue = propertyPath;
    	String index = StringUtils.substringBetween(propertyPath, "[", "]");
    	if (StringUtils.isNotBlank(index)){
    		returnValue = StringUtils.remove(propertyPath, "[" + index + "]");
    		return stripIndexesFromPropertyPath(returnValue);
    	}else{
    		return returnValue;
    	}
    }
}
