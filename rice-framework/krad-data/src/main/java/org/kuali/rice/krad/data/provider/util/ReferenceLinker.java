/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.provider.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataRepository;

/**
 * Links parent-child object references
 */
public class ReferenceLinker {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferenceLinker.class);

    private DataObjectService dataObjectService;

    public ReferenceLinker(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    protected DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    protected MetadataRepository getMetadataRepository() {
        return getDataObjectService().getMetadataRepository();
    }

    /**
     * For each reference object to the parent persistableObject, sets the key
     * values for that object. First, if the reference object already has a
     * value for the key, the value is left unchanged. Otherwise, for
     * non-anonymous keys, the value is taken from the parent object. For
     * anonymous keys, all other persistableObjects are checked until a value
     * for the key is found.
     */
    public void linkObjects(Object persistableObject) {
        linkObjectsWithCircularReferenceCheck(persistableObject, new HashSet<Object>());
    }

    protected void linkObjectsWithCircularReferenceCheck(Object persistableObject, Set<Object> referenceSet) {
        if (referenceSet.contains(persistableObject) || DataObjectUtils.isNull(persistableObject)) {
            return;
        }
		if (LOG.isDebugEnabled()) {
			LOG.debug("Attempting to link reference objects on " + persistableObject);
		}
        referenceSet.add(persistableObject);
        DataObjectMetadata metadata = getMetadataRepository().getMetadata(persistableObject.getClass());

        if (metadata == null) {
            LOG.warn("Unable to find metadata for "
                    + persistableObject.getClass()
                    + " when linking references, skipping");
            return;
        }

		linkRelationships(metadata, persistableObject, referenceSet);
		linkCollections(metadata, persistableObject, referenceSet);
	}

	protected void linkRelationships(DataObjectMetadata metadata, Object persistableObject, Set<Object> referenceSet) {
        // iterate through all object references for the persistableObject
        List<DataObjectRelationship> objectReferences = metadata.getRelationships();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Obtained relationships for linking: " + objectReferences);
		}
		DataObjectWrapper<?> parentWrap = getDataObjectService().wrap(persistableObject);
        for (DataObjectRelationship referenceDescriptor : objectReferences) {
            // get the actual reference object
            String fieldName = referenceDescriptor.getName();
			Object childObject = parentWrap.getPropertyValue(fieldName);
			boolean updatableRelationship = referenceDescriptor.isSavedWithParent();
			if (childObject == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Referenced object for field " + fieldName + " is null, skipping");
				}
				continue;
			}

            // recursively link object
			linkObjectsWithCircularReferenceCheck(childObject, referenceSet);

            // iterate through the keys for the reference object and set
            // value
			List<DataObjectAttributeRelationship> refAttrs = referenceDescriptor.getAttributeRelationships();
            DataObjectMetadata refCld = getMetadataRepository().getMetadata(referenceDescriptor.getRelatedType());
            if (refCld == null) {
                LOG.warn("No metadata found in repository for referenced object: " + referenceDescriptor);
                continue;
            }
			// List<String> refPkNames = refCld.getPrimaryKeyAttributeNames();

			if (LOG.isDebugEnabled()) {
				LOG.debug("Linking Referenced fields with parent's FK fields:" + "\n***Refs: " + refAttrs);
			}

			// Two cases: Updatable Reference (owned child object) or non-updatable (reference data)
			// In the former case, we always want to push our keys down into the child, since that
			// is what will maintain the relationship.
			// In the latter: We assume that the parent object's key fields are correct, only
			// setting them from the embedded object *IF* they are null.
			// (Since we can't effectively tell which one is the master.)

			// Go through all the attribute relationships to copy the key fields as appropriate
			DataObjectWrapper<?> childWrap = getDataObjectService().wrap(childObject);
			if (updatableRelationship) {
				linkUpdatableChild(parentWrap, childWrap, referenceDescriptor.getName(), refAttrs);
			} else { // non-updatable (reference-only) relationship
				linkNonUpdatableChild(parentWrap, childWrap, referenceDescriptor.getName(), refAttrs);
			}
		}
	}

	/**
	 * Attempt to ensure that, for an updatable reference object that the FK fields and the reference object remain
	 * consistent.
	 * 
	 * <ol>
	 * <li>If the referenced key on the child object is not set and the matching key on the parent is set, set the key
	 * field on the child object.</li>
	 * <li></li>
	 * </ol>
	 * 
	 * @param parentWrap
	 * @param childWrap
	 * @param refAttrs
	 */
	protected void linkUpdatableChild(DataObjectWrapper<?> parentWrap, DataObjectWrapper<?> childWrap,
			String childObjectPropertyName, List<DataObjectAttributeRelationship> refAttrs) {
		DataObjectMetadata referenceMetadata = childWrap.getMetadata();
		List<String> childPkAttributes = referenceMetadata.getPrimaryKeyAttributeNames();
		List<String> parentPkAttributes = parentWrap.getMetadata().getPrimaryKeyAttributeNames();
		for (DataObjectAttributeRelationship attrRel : refAttrs) {
			Object parentPropertyValue = parentWrap.getPropertyValue(attrRel.getParentAttributeName());
			Object childPropertyValue = childWrap.getPropertyValueNullSafe(attrRel.getChildAttributeName());

			// if fk is set in main object, take value from there
			if (parentPropertyValue != null && StringUtils.isNotBlank(parentPropertyValue.toString())) {
				// if the child's value is a PK field then we don't want to set it
				// *unless* it's null, which we assume is an invalid situation
				// and indicates that it has not been set yet
				if (childPkAttributes.contains(attrRel.getChildAttributeName()) && childPropertyValue != null
						&& StringUtils.isNotBlank(childPropertyValue.toString())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Relationship is to PK value on updatable child object - it may not be changed.  Skipping: "
								+ childWrap.getWrappedClass().getName() + "." + attrRel.getChildAttributeName());
					}
					continue;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Parent Object Of Updateable Child has FK value set (" + attrRel.getParentAttributeName()
							+ "="
							+ parentPropertyValue + "): using that");
				}
				childWrap.setPropertyValue(attrRel.getChildAttributeName(), parentPropertyValue);
			} else {
				// The key field on the parent is blank, and so can not link to a child object
				// Blank out the child reference object.
				// parentWrap.setPropertyValue(childObjectPropertyName, null);

				// The FK field on the parent is blank,
				// but the child has key values - so set the parent so they link properly
				if (childPropertyValue != null && StringUtils.isNotBlank(childPropertyValue.toString())) {
					if (parentPkAttributes.contains(attrRel.getParentAttributeName())) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Relationship is to PK value on parent object - it may not be changed.  Skipping: "
									+ parentWrap.getWrappedClass().getName() + "." + attrRel.getParentAttributeName());
						}
						continue;
					}
					if (LOG.isDebugEnabled()) {
						LOG.debug("Updatable Child Object has FK value set (" + attrRel.getChildAttributeName() + "="
								+ childPropertyValue + "): using that");
					}
					parentWrap.setPropertyValue(attrRel.getParentAttributeName(), childPropertyValue);
				}
			}
		}
	}

	protected void linkNonUpdatableChild(DataObjectWrapper<?> parentWrap, DataObjectWrapper<?> childWrap,
			String childObjectPropertyName, List<DataObjectAttributeRelationship> refAttrs) {
		for (DataObjectAttributeRelationship attrRel : refAttrs) {
			Object parentPropertyValue = parentWrap.getPropertyValueNullSafe(attrRel.getParentAttributeName());
			Object childPropertyValue = childWrap.getPropertyValueNullSafe(attrRel.getChildAttributeName());
			// if (parentPropertyValue != null && StringUtils.isNotBlank(parentPropertyValue.toString())) {
			// // Skip this property, it has already been set on the parent object
			// continue;
			// }
			if (ObjectUtils.notEqual(parentPropertyValue, childPropertyValue)) {
				parentWrap.setPropertyValue(childObjectPropertyName, null);
				break;
				// we have nothing else to do - one of the parent properties
				// was blank (or mismatched) so we can quit
			}

			// The key field on the parent is blank, and so can not link to a child object
			// Blank out the child reference object.


			// Object childPropertyValue = childWrap.getPropertyValueNullSafe(attrRel.getChildAttributeName());
			// // don't bother setting parent if it's not set itself
			// if (childPropertyValue == null || StringUtils.isBlank(childPropertyValue.toString())) {
			// continue;
			// }
			// if (LOG.isDebugEnabled()) {
			// LOG.debug("Non-Updatable Child Object has FK value set (" + attrRel.getChildAttributeName() + "="
			// + childPropertyValue + "): using that");
			// }
			// parentWrap.setPropertyValue(attrRel.getParentAttributeName(), childPropertyValue);
		}
	}

	protected void linkCollections(DataObjectMetadata metadata, Object persistableObject, Set<Object> referenceSet) {
		List<DataObjectCollection> collections = metadata.getCollections();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Obtained collections for linking: " + collections);
        }

		for (DataObjectCollection collectionMetadata : collections) {
			// We only process collections if they are being saved with the parent
			if (!collectionMetadata.isSavedWithParent()) {
				continue;
			}
			// get the actual reference object
			String fieldName = collectionMetadata.getName();
			DataObjectWrapper<?> parentObjectWrapper = getDataObjectService().wrap(persistableObject);
			Collection<?> collection = (Collection<?>) parentObjectWrapper.getPropertyValue(fieldName);
			if (collection == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Referenced collection for field " + fieldName + " is null, skipping");
				}
				continue;
			} else if (referenceSet.contains(collection)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("We've previously linked the object assigned to " + fieldName + ", skipping");
				}
				continue;
			}
			List<DataObjectAttributeRelationship> collectionAttributeRelationships = collectionMetadata
					.getAttributeRelationships();

			// Need to iterate through the collection, setting FK values as needed and telling each child object to link
			// itself
			for (Object collectionItem : collection) {
				// recursively link object
				linkObjectsWithCircularReferenceCheck(collectionItem, referenceSet);

				DataObjectWrapper<Object> collItemWrapper = getDataObjectService().wrap(collectionItem);
				// Now - go through the keys relating the parent object to each child and set them so that they are
				// linked properly
				// This will also reset them to the parent's values in case they were changed.
				// If this updates the PK fields of the collection objects, then the user is doing
				// something they shouldn't (swapping Collection items between parent data objects)
				// And it will blow up with an JPA exception anyway.
				for (DataObjectAttributeRelationship rel : collectionAttributeRelationships) {
                    if(rel.getChildAttributeName() != null && rel.getParentAttributeName() != null){
					    collItemWrapper.setPropertyValue(rel.getChildAttributeName(),
							parentObjectWrapper.getPropertyValueNullSafe(rel.getParentAttributeName()));
                    }
				}
			}
		}
    }

}
