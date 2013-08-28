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
		DataObjectWrapper<?> wrap = getDataObjectService().wrap(persistableObject);
        for (DataObjectRelationship referenceDescriptor : objectReferences) {
            // get the actual reference object
            String fieldName = referenceDescriptor.getName();
            Object referenceObject = wrap.getPropertyValue(fieldName);
			boolean updatableRelationship = referenceDescriptor.isSavedWithParent();
			if (referenceObject == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Referenced object for field " + fieldName + " is null, skipping");
				}
				continue;
			} else if (referenceSet.contains(referenceObject)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("We've previously linked the object assigned to " + fieldName + ", skipping");
				}
                continue;
            }

            // recursively link object
            linkObjectsWithCircularReferenceCheck(referenceObject, referenceSet);

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
			DataObjectWrapper<?> referenceWrap = getDataObjectService().wrap(referenceObject);
			if (updatableRelationship) {
				for (DataObjectAttributeRelationship attrRel : refAttrs) {
					Object parentPropertyValue = wrap.getPropertyValue(attrRel.getParentAttributeName());

					// if fk is set in main object, take value from there
					if (parentPropertyValue != null && StringUtils.isNotBlank(parentPropertyValue.toString())) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Parent Object has FK value set (" + attrRel.getParentAttributeName() + "="
									+ parentPropertyValue + "): using that");
						}
						referenceWrap.setPropertyValue(attrRel.getChildAttributeName(), parentPropertyValue);
					}
				}
			} else { // non-updatable (reference-only) relationship
				for (DataObjectAttributeRelationship attrRel : refAttrs) {
					Object parentPropertyValue = wrap.getPropertyValueNullSafe(attrRel.getParentAttributeName());
					Object childPropertyValue = referenceWrap.getPropertyValueNullSafe(attrRel.getChildAttributeName());
					if (parentPropertyValue != null && StringUtils.isNotBlank(parentPropertyValue.toString())) {
						// if the keys have changed, then blank out the referenced object
						// JHK: Commented out for now - object is not auto-refreshing after the save
						// if (ObjectUtils.notEqual(parentPropertyValue, childPropertyValue)) {
						// wrap.setPropertyValue(referenceDescriptor.getName(), null);
						// }
						// Skip this property, it has already been set on the parent object
						continue;
					}
					// don't bother setting parent if it's not set itself
					if (childPropertyValue == null || StringUtils.isBlank(childPropertyValue.toString())) {
						continue;
					}
					if (LOG.isDebugEnabled()) {
						LOG.debug("Child Object has FK value set (" + attrRel.getParentAttributeName() + "="
								+ parentPropertyValue + "): using that");
                    }
					wrap.setPropertyValue(attrRel.getParentAttributeName(), childPropertyValue);
				}
			}
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
				for (DataObjectAttributeRelationship rel : collectionAttributeRelationships) {
					collItemWrapper.setPropertyValue(rel.getChildAttributeName(),
							parentObjectWrapper.getPropertyValueNullSafe(rel.getParentAttributeName()));
				}
			}
		}
    }
}
