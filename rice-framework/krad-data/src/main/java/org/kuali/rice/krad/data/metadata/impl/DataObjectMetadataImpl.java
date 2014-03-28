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
package org.kuali.rice.krad.data.metadata.impl;

import com.google.common.annotations.Beta;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataMergeAction;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation class for the metadata related to the data object as a whole.
 *
 * <p>
 * Contains lists of all child elements.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectMetadataImpl extends MetadataCommonBase implements DataObjectMetadataInternal {
	private static final long serialVersionUID = 7722982931510558892L;

	protected DataObjectMetadataInternal embedded;
	/**
	 * Property used to help with debugging.
	 * 
	 * It is used in the toString() method so you can determine from which provider this metadata was extracted.
	 */
	protected String providerName;

	protected Class<?> type;

	protected List<DataObjectAttribute> attributes;
	protected Map<String, DataObjectAttribute> attributeMap;
	protected List<String> removedAttributeNames;
	protected List<String> orderedAttributeList = new ArrayList<String>();

	protected List<DataObjectCollection> collections;
	protected Map<String, DataObjectCollection> collectionMap;
	protected List<String> removedCollectionNames;

	protected List<DataObjectRelationship> relationships;
	protected Map<String, DataObjectRelationship> relationshipMap;
	protected List<String> removedRelationshipNames;

	protected Map<String, List<DataObjectRelationship>> attributeToRelationshipMap;
	protected Map<String, DataObjectRelationship> lastAttributeToRelationshipMap;

	protected List<String> primaryKeyAttributeNames;
	protected List<String> businessKeyAttributeNames;
	protected String primaryDisplayAttributeName;
	protected boolean primaryDisplayAttributeSetManually;

	protected Boolean supportsOptimisticLocking;

	protected Collection<UifAutoCreateViewType> autoCreateUifViewTypes;

	public DataObjectMetadataImpl() {
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public Object getUniqueKeyForMerging() {
		return type;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public Class<?> getType() {
		return type;
	}

    /**
    * Sets unknown class to determine type.
    *
    * @param type unknown class
    */
	public void setType(Class<?> type) {
		if (type == null) {
			throw new IllegalArgumentException("The data object type may not be set to null.");
		}
		this.type = type;
	}

    /**
    * Gets type based on unknown class.
    *
    * @return class type or null
    */
	public String getTypeClassName() {
		if (type == null) {
			return null;
		}
		return type.getName();
	}

	/**
	 * This is really a helper method for cases where these objects may need to be built up via Spring XML.
     *
     * @param typeClassName class type
	 */
	public void setTypeClassName(String typeClassName) {
        try {
			setType(Class.forName(typeClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("ClassNotFoundException when setting data object type class name "
					+ typeClassName, e);
        }
    }

    /**
    * {@inheritDoc}
    */
	@Override
	public List<String> getPrimaryKeyAttributeNames() {
		if (primaryKeyAttributeNames != null) {
			return primaryKeyAttributeNames;
		}
		if (embedded != null) {
			return embedded.getPrimaryKeyAttributeNames();
		}
		return Collections.emptyList();
	}

    /**
    * Sets list of primary attribute names which make up key.
    *
    * @param primaryKeyAttributeNames list of attribute names.
    */
	public void setPrimaryKeyAttributeNames(List<String> primaryKeyAttributeNames) {
		if (primaryKeyAttributeNames == null) {
			primaryKeyAttributeNames = Collections.emptyList();
		}
		this.primaryKeyAttributeNames = Collections.unmodifiableList( primaryKeyAttributeNames );
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public List<String> getBusinessKeyAttributeNames() {
		// If we have business keys, use that
		if (businessKeyAttributeNames != null) {
			return businessKeyAttributeNames;
		}
		// Otherwise, if we have an explicit PK, use that
		if (primaryKeyAttributeNames != null) {
			return primaryKeyAttributeNames;
		}
		// If neither has been set, go up the chain
		if (embedded != null) {
			return embedded.getBusinessKeyAttributeNames();
		}
		return Collections.emptyList();
	}

    /**
    * Sets list of attribute names that make up business key.
    *
    * @param businessKeyAttributeNames attribute names
    */
	public void setBusinessKeyAttributeNames(List<String> businessKeyAttributeNames) {
		if (businessKeyAttributeNames == null) {
			businessKeyAttributeNames = Collections.emptyList();
		}
		this.businessKeyAttributeNames = Collections.unmodifiableList(businessKeyAttributeNames);
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public Boolean hasDistinctBusinessKey() {
		return !getPrimaryKeyAttributeNames().equals(getBusinessKeyAttributeNames());
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String getPrimaryDisplayAttributeName() {
		if (primaryDisplayAttributeName == null && !getBusinessKeyAttributeNames().isEmpty()) {
			primaryDisplayAttributeName = getBusinessKeyAttributeNames().get(getBusinessKeyAttributeNames().size() - 1);
		}
		return primaryDisplayAttributeName;
		// Notes for potential use cases if deemed necessary to implement.
		// Since the last field of the PK does not generally change between
		// metadata layers, these cases may not need to be considered.
		// CASES:
		// 1) primaryDisplayAttributeName != null ==> primaryDisplayAttributeName
		// 2) primaryDisplayAttributeName == null && pk fields == null/empty && embedded != null ==>
		// embedded.getprimaryDisplayAttributeName()
		// 3) primaryDisplayAttributeName == null && pk fields == null/empty && embedded == null ==> null
		// 4) primaryDisplayAttributeName == null && pk fields != null/empty && embedded == null ==> last field of PK
		// 5) primaryDisplayAttributeName == null && pk fields != null/empty && embedded != null && embedded.primary
		// display == null ==> last field of PK
		// 6) primaryDisplayAttributeName == null && pk fields != null/empty && embedded != null && embedded.primary
		// display != null ==> embedded.getprimaryDisplayAttributeName()
		// If not set locally
		// need to check embedded
		// But - embedded could be dynamically or manually set as well
		// how do we detect whether it's been set explicitly on the embedded? If we don't, then we always get the last
		// attribute of the PK list on the embedding object
	}

    /**
    * Sets list of attribute names used for display.
    *
    * @param primaryDisplayAttributeName list of attribute names.
    */
	public void setPrimaryDisplayAttributeName(String primaryDisplayAttributeName) {
		if (StringUtils.isBlank(primaryDisplayAttributeName)) {
			this.primaryDisplayAttributeName = null;
			this.primaryDisplayAttributeSetManually = false;
		} else {
			this.primaryDisplayAttributeName = primaryDisplayAttributeName;
			this.primaryDisplayAttributeSetManually = true;
		}
	}

    /**
    * Orders attributes by defined order.
    *
    * <p>
    *     First looks to see if attributes are inherited, then looks at the declared fields based on the attribute
    *     type.
    * </p>
    *
    * @param attributes list of data object attributes
    * @return re-ordered list of data object attributes
    */
	public List<DataObjectAttribute> orderAttributesByDefinedOrder(List<DataObjectAttribute> attributes) {
		List<DataObjectAttribute> sorted = new ArrayList<DataObjectAttribute>(attributes.size());
		Map<String, DataObjectAttribute> keyedAttributes = new HashMap<String, DataObjectAttribute>(attributes.size());
		Map<String, List<DataObjectAttribute>> inheritedAttributes = new HashMap<String, List<DataObjectAttribute>>();
		for (DataObjectAttribute attr : attributes) {
			if (attr.isInherited()) {
				List<DataObjectAttribute> inheritedByProperty = inheritedAttributes.get(attr
						.getInheritedFromParentAttributeName());
				if (inheritedByProperty == null) {
					inheritedByProperty = new ArrayList<DataObjectAttribute>();
					inheritedAttributes.put(attr.getInheritedFromParentAttributeName(), inheritedByProperty);
				}
				inheritedByProperty.add(attr);
			} else {
				keyedAttributes.put(attr.getName(), attr);
			}
		}
		for (Field f : getType().getDeclaredFields()) {
			DataObjectAttribute attr = keyedAttributes.get(f.getName());
			if (attr != null) {
				sorted.add(attr);
				keyedAttributes.remove(f.getName());
			}
			if (inheritedAttributes.containsKey(f.getName())) {
				sorted.addAll(inheritedAttributes.get(f.getName()));
			}
		}
		sorted.addAll(keyedAttributes.values());
		return sorted;
	}

	List<DataObjectAttribute> mergedAttributes = null;

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectAttribute> getAttributes() {
		// We have a local list and no overrides - return the existing list
		if (attributes != null && embedded == null) {
			return orderAttributesByDefinedOrder(attributes);
		}
		if (embedded != null) {
			return orderAttributesByDefinedOrder(mergeLists(embedded.getAttributes(), attributes));
		}
		return Collections.emptyList();
		// if (mergedAttributes != null) {
		// return mergedAttributes;
		// }
		// // We have a local list and no overrides - return the existing list
		// if (attributes != null && embedded == null) {
		// mergedAttributes = orderAttributesByDefinedOrder(attributes);
		// } else if (embedded != null) {
		// mergedAttributes = orderAttributesByDefinedOrder(mergeLists(embedded.getAttributes(), attributes));
		// } else {
		// mergedAttributes = Collections.emptyList();
		// }
		// return mergedAttributes;
	}

    /**
    * Sets attributes.
     *
     * <p>
     *     Looks at merge actions when adding, so not all attributes are added.
     * </p>
    *
    * @param attributes list of data object attributes
    */
	public void setAttributes(List<DataObjectAttribute> attributes) {
		if (attributes == null) {
			attributes = Collections.emptyList();
		}
		this.attributes = Collections.unmodifiableList(attributes);
		mergedAttributes = null;
		attributeMap = new HashMap<String, DataObjectAttribute>(attributes.size());
		removedAttributeNames = new ArrayList<String>();
		for (DataObjectAttribute attr : attributes) {
			// TODO: This is not quite correct - we really only want to not add the NO_OVERRIDE items if they are
			// overriding something. However, at the point this is running, we don't know whether we will be embedding
			// anything...
			if (attr.getMergeAction() != MetadataMergeAction.REMOVE
					&& attr.getMergeAction() != MetadataMergeAction.NO_OVERRIDE) {
				attributeMap.put(attr.getName(), attr);
			}
			// since the attribute will still exist in the embedded metadata, we need to put a block in on the standard
			// cascade
			if (attr.getMergeAction() == MetadataMergeAction.REMOVE) {
				removedAttributeNames.add(attr.getName());
			}
		}
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectCollection> getCollections() {
		// We have a local list and no overrides - return the existing list
		if (collections != null && embedded == null) {
			return collections;
		}
		if (embedded != null) {
			return mergeLists(embedded.getCollections(), collections);
		}
		return Collections.emptyList();
	}

    /**
    * Sets collections.
    *
    * <p>
    *     Looks at merge actions when adding, so not all collections are added.
    * </p>
    *
    * @param collections list of data object collections or null
    */
	public void setCollections(List<DataObjectCollection> collections) {
		if (collections == null) {
			this.collections = null;
			return;
		}
		this.collections = Collections.unmodifiableList(collections);
		collectionMap = new HashMap<String, DataObjectCollection>(collections.size());
		removedCollectionNames = new ArrayList<String>();
		for (DataObjectCollection coll : collections) {
			// This is not quite correct - we really only want to not add the NO_OVERRIDE items if they are
			// overriding something. However, at the point this is running, we don't know whether we will be embedding
			// anything...
			if (coll.getMergeAction() != MetadataMergeAction.REMOVE
					&& coll.getMergeAction() != MetadataMergeAction.NO_OVERRIDE) {
				collectionMap.put(coll.getName(), coll);
			}
			// since the attribute will still exist in the embedded metadata, we need to put a block in on the standard
			// cascade
			if (coll.getMergeAction() == MetadataMergeAction.REMOVE) {
				removedCollectionNames.add(coll.getName());
			}
		}
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectRelationship> getRelationships() {
		// We have a local list and no overrides - return the existing list
		if (relationships != null && embedded == null) {
			return relationships;
		}
		if (embedded != null) {
			return mergeLists(embedded.getRelationships(), relationships);
		}
		return Collections.emptyList();
	}

    /**
    * Sets relationships.
    *
    * <p>
    *     Looks at merge actions and whether the relationship is empty when adding, so not all relationships are added.
    * </p>
    *
    * @param relationships list of data object relationships or null
    */
	public void setRelationships(List<DataObjectRelationship> relationships) {
		if (relationships == null) {
			this.relationships = null;
			relationshipMap = null;
			lastAttributeToRelationshipMap = null;
			attributeToRelationshipMap = null;
			return;
		}
		this.relationships = Collections.unmodifiableList(relationships);
		relationshipMap = new HashMap<String, DataObjectRelationship>(relationships.size());
		attributeToRelationshipMap = new HashMap<String, List<DataObjectRelationship>>();
		lastAttributeToRelationshipMap = new HashMap<String, DataObjectRelationship>(relationships.size());
		removedRelationshipNames = new ArrayList<String>();
		// Builds maps to link attribute names to their relationships
		for (DataObjectRelationship rel : relationships) {
			// This is not quite correct - we really only want to not add the NO_OVERRIDE items if they are
			// overriding something. However, at the point this is running, we don't know whether we will be embedding
			// anything...
			if (rel.getMergeAction() != MetadataMergeAction.REMOVE
					&& rel.getMergeAction() != MetadataMergeAction.NO_OVERRIDE) {
				// related object attribute name
				relationshipMap.put(rel.getName(), rel);
				// last attribute in list linking the objects
				if (!rel.getAttributeRelationships().isEmpty()) {
					DataObjectAttributeRelationship relAttr = rel.getAttributeRelationships().get(
							rel.getAttributeRelationships().size() - 1);
					lastAttributeToRelationshipMap.put(relAttr.getParentAttributeName(), rel);
				}
				// all relationships relating to an attribute
				for (DataObjectAttributeRelationship relAttr : rel.getAttributeRelationships()) {
					List<DataObjectRelationship> rels = attributeToRelationshipMap
							.get(relAttr.getParentAttributeName());
					if (rels == null) {
						rels = new ArrayList<DataObjectRelationship>();
						attributeToRelationshipMap.put(relAttr.getParentAttributeName(), rels);
					}
					rels.add(rel);
				}
			}
			// since the attribute will still exist in the embedded metadata, we need to put a block in on the standard
			// cascade
			if (rel.getMergeAction() == MetadataMergeAction.REMOVE) {
				removedRelationshipNames.add(rel.getName());
			}
		}
		relationshipMap = Collections.unmodifiableMap(relationshipMap);
		lastAttributeToRelationshipMap = Collections.unmodifiableMap(lastAttributeToRelationshipMap);
		attributeToRelationshipMap = Collections.unmodifiableMap(attributeToRelationshipMap);
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public DataObjectAttribute getAttribute(String attributeName) {
		if (attributeName == null) {
			return null;
		}
		DataObjectAttribute attribute = null;
		// attempt to get it from the local attribute map (if any attributed defined locally)
		if (attributes != null) {
			attribute = attributeMap.get(attributeName);
		}
		// if we don't find one, but we have an embedded metadata object, check it
		if (attribute == null && embedded != null) {
			attribute = embedded.getAttribute(attributeName);
			// but, ensure it's not on the removed attribute list
			if (attribute != null && removedAttributeNames != null
					&& removedAttributeNames.contains(attribute.getName())) {
				attribute = null;
			}
		}
		return attribute;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public DataObjectCollection getCollection(String collectionName) {
		if (collectionName == null) {
			return null;
		}
		DataObjectCollection collection = null;
		// attempt to get it from the local attribute map (if any attributed defined locally)
		if (collections != null) {
			collection = collectionMap.get(collectionName);
		}
		// if we don't find one, but we have an embedded metadata object, check it
		if (collection == null && embedded != null) {
			collection = embedded.getCollection(collectionName);
			// but, ensure it's not on the removed attribute list
			if (collection != null && removedCollectionNames != null
					&& removedCollectionNames.contains(collection.getName())) {
				collection = null;
			}
		}
		return collection;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public DataObjectRelationship getRelationship(String relationshipName) {
		if (relationshipName == null) {
			return null;
		}
		DataObjectRelationship relationship = null;
		// attempt to get it from the local attribute map (if any attributed defined locally)
		if (relationships != null) {
			relationship = relationshipMap.get(relationshipName);
		}
		// if we don't find one, but we have an embedded metadata object, check it
		if (relationship == null && embedded != null) {
			relationship = embedded.getRelationship(relationshipName);
			// but, ensure it's not on the removed attribute list
			if (relationship != null && removedRelationshipNames != null
					&& removedRelationshipNames.contains(relationship.getName())) {
				relationship = null;
			}
		}
		return relationship;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectRelationship> getRelationshipsInvolvingAttribute(String attributeName) {
		// somewhat complex, since it returns a list of all possible relationships
		//
		if (StringUtils.isBlank(attributeName)) {
			return null;
		}
		Map<Object, DataObjectRelationship> relationships = new HashMap<Object, DataObjectRelationship>();
		// Look locally
		if (attributeToRelationshipMap != null && attributeToRelationshipMap.containsKey(attributeName)) {
			for (DataObjectRelationship rel : attributeToRelationshipMap.get(attributeName)) {
				Object mergeKey = rel.getName();
				if (rel instanceof MetadataCommonInternal) {
					mergeKey = ((MetadataCommonInternal) rel).getUniqueKeyForMerging();
				}
				relationships.put(mergeKey, rel);
			}
		}
		// now, if we have an embedded object, look for matching ones, but exclude if the relationship is the same
		// as that means it was overridden by this bean
		if (embedded != null) {
			for (DataObjectRelationship rel : embedded.getRelationshipsInvolvingAttribute(attributeName)) {
				Object mergeKey = rel.getName();
				if (rel instanceof MetadataCommonInternal) {
					mergeKey = ((MetadataCommonInternal) rel).getUniqueKeyForMerging();
				}
				if (!relationships.containsKey(mergeKey)) {
					relationships.put(mergeKey, rel);
				}
			}
		}
		return new ArrayList<DataObjectRelationship>(relationships.values());
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public DataObjectRelationship getRelationshipByLastAttributeInRelationship(String attributeName) {
		// this returns a single record, so we can just use the first matching one we find
		if (StringUtils.isBlank(attributeName)) {
			return null;
		}
		DataObjectRelationship relationship = null;
		// Look locally
		if (lastAttributeToRelationshipMap != null) {
			relationship = lastAttributeToRelationshipMap.get(attributeName);
		}
		// if nothing found local, recurse into the embedded provider
		if (relationship == null && embedded != null) {
			relationship = embedded.getRelationshipByLastAttributeInRelationship(attributeName);
		}
		return relationship;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public DataObjectMetadataInternal getEmbedded() {
		return embedded;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public void setEmbedded(DataObjectMetadataInternal embedded) {
		this.embedded = embedded;
		setEmbeddedCommonMetadata(embedded);
	}

    /**
    * Gets the metadata source.
    *
    * <p>
    * Helper property to allow identification of the source of metadata. Value is transient, so it will not survive
    * serialization.
    * </p>
    *
    * @return metadata source
    */
	public String getProviderName() {
		return providerName;
	}

    /**
    * Sets provider name.
    *
    * @param providerName name of provider
    */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataObjectMetadata [");
		builder.append("type=").append(getType()).append(", ");
		builder.append("typeLabel=").append(label).append(", ");
		builder.append("backingObjectName=").append(backingObjectName);
		if (attributes != null && !attributes.isEmpty()) {
			builder.append(", ").append("attributes=").append(attributes);
		}
		if (primaryKeyAttributeNames != null && !primaryKeyAttributeNames.isEmpty()) {
			builder.append(", ").append("primaryKeyAttributeNames=").append(primaryKeyAttributeNames);
		}
		if (getPrimaryDisplayAttributeName() != null) {
			builder.append(", ").append("primaryDisplayAttributeName=").append(getPrimaryDisplayAttributeName());
		}
		if (businessKeyAttributeNames != null && !businessKeyAttributeNames.isEmpty()) {
			builder.append(", ").append("businessKeyAttributeNames=").append(businessKeyAttributeNames);
		}
		if (collections != null && !collections.isEmpty()) {
			builder.append(", ").append("collections=").append(collections);
		}
		if (relationships != null && !relationships.isEmpty()) {
			builder.append(", ").append("relationships=").append(relationships);
		}
		if (providerName != null) {
			builder.append(", ").append("providerName=").append(providerName);
		}
		if (embedded != null) {
			builder.append(", ").append("mergeAction=").append(mergeAction);
			builder.append(", ").append("embedded=").append(embedded);
		}
		builder.append("]");
		return builder.toString();
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isSupportsOptimisticLocking() {
		if (supportsOptimisticLocking != null) {
			return supportsOptimisticLocking;
		}
		if (embedded != null) {
			return embedded.isSupportsOptimisticLocking();
		}
		return false;
	}

    /**
    * Sets whether optimistic locking is supported.
    *
    * @param supportsOptimisticLocking whether optimistic locking is supported
    */
	public void setSupportsOptimisticLocking(boolean supportsOptimisticLocking) {
		this.supportsOptimisticLocking = supportsOptimisticLocking;
	}

    /**
    * {@inheritDoc}
    */
	@Override
    @Beta
	public boolean shouldAutoCreateUifViewOfType(UifAutoCreateViewType viewType) {
		if (getAutoCreateUifViewTypes() == null) {
			return false;
		}
		return getAutoCreateUifViewTypes().contains(viewType)
				|| getAutoCreateUifViewTypes().contains(UifAutoCreateViewType.ALL);
	}

    /**
    * {@inheritDoc}
    */
	@Override
    @Beta
	public Collection<UifAutoCreateViewType> getAutoCreateUifViewTypes() {
		if (autoCreateUifViewTypes != null) {
			return autoCreateUifViewTypes;
		}
		if (embedded != null) {
			return embedded.getAutoCreateUifViewTypes();
		}
		return null;
	}

    /**
    * BETA: Sets list of UIF view types that will be auto created.
    *
    * @param autoCreateUifViewTypes UIF view types
    */
    @Beta
	public void setAutoCreateUifViewTypes(Collection<UifAutoCreateViewType> autoCreateUifViewTypes) {
		this.autoCreateUifViewTypes = autoCreateUifViewTypes;
	}

    /**
    * Gets sorted attribute list.
    *
    * @return ordered attribute list
    */
	public List<String> getOrderedAttributeList() {
		return orderedAttributeList;
	}

    /**
    * Sets sorted attribute list.
    *
    * @param orderedAttributeList sorted attributes
    */
	public void setOrderedAttributeList(List<String> orderedAttributeList) {
		this.orderedAttributeList = orderedAttributeList;
	}

}
