package org.kuali.rice.krad.data.provider.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
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

        DataObjectWrapper<?> wrap = getDataObjectService().wrap(persistableObject);
        // iterate through all object references for the persistableObject
        List<DataObjectRelationship> objectReferences = metadata.getRelationships();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Obtained relationships for linking: " + objectReferences);
		}
        for (DataObjectRelationship referenceDescriptor : objectReferences) {
            // get the actual reference object
            String fieldName = referenceDescriptor.getName();
            Object referenceObject = wrap.getPropertyValue(fieldName);
			if (referenceObject == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Referenced object for field " + fieldName + " is null, skipping");
				}
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

			// Get the relationship FK field name -> value as a Map
			// This pulls the *values* from the referenced object but is indexed by the parent object
			// Map<String, Object> childObjectFkValues = new HashMap<String, Object>();
			// for (DataObjectAttributeRelationship attrRel : refAttrs) {
			// childObjectFkValues.put(attrRel.getParentAttributeName(),
			// referenceWrap.getPropertyValue(attrRel.getChildAttributeName()));
			// }

			if (LOG.isDebugEnabled()) {
				LOG.debug("Linking Referenced fields with parent's FK fields:" + "\n***Refs: " + refAttrs);
			}

			for (DataObjectAttributeRelationship attrRel : refAttrs) {
				String fkPropertyName = attrRel.getParentAttributeName();
				Object parentObjectFkValue = wrap.getPropertyValue(fkPropertyName);
                Object fkValue = null;

                // if fk is set in main object, take value from there
				if (parentObjectFkValue != null && StringUtils.isNotBlank(parentObjectFkValue.toString())) {
					fkValue = parentObjectFkValue;
					if (LOG.isDebugEnabled()) {
						LOG.debug("Parent Object has FK value set (" + fkPropertyName + "=" + fkValue + "): using that");
                    }
				} else {
					// get the FK value from the linked object
					if (referenceObject != null) {
						DataObjectWrapper<?> referenceWrap = getDataObjectService().wrap(referenceObject);
						fkValue = referenceWrap.getPropertyValue(attrRel.getChildAttributeName());
					}
					if (fkValue == null || StringUtils.isBlank(fkValue.toString())) {
						// find the value from one of the other reference objects
						for (DataObjectRelationship checkDescriptor : objectReferences) {
							fkValue = getReferenceFKValue(persistableObject, wrap, checkDescriptor, fkPropertyName);
							if (fkValue != null && StringUtils.isNotBlank(fkValue.toString())) {
								break;
							}
						}
					}
                }

                // set the fk value
				if (fkValue != null) {
					// fieldName = attrRel.getChildAttributeName();
					// referenceWrap.setPropertyValue(fieldName, fkValue);
                    // set fk in main object
					if (parentObjectFkValue == null) {
						wrap.setPropertyValue(fkPropertyName, fkValue);
                    }
                }
            }
        }

    }

    private Object getReferenceFKValue(Object persistableObject, DataObjectWrapper<?> wrap, DataObjectRelationship chkRefCld, String fkName) {
        DataObjectMetadata classDescriptor = getMetadataRepository().getMetadata(persistableObject.getClass());
        Object referenceObject = wrap.getPropertyValue(chkRefCld.getName());

        if (referenceObject == null) {
            return null;
        }

        List<DataObjectAttributeRelationship> refFkNames = chkRefCld.getAttributeRelationships();
        DataObjectMetadata refCld = getMetadataRepository().getMetadata(chkRefCld.getRelatedType());
        List<String> refPkNames = refCld.getPrimaryKeyAttributeNames();

        DataObjectWrapper<?> referenceWrap = getDataObjectService().wrap(referenceObject);
        Object fkValue = null;
        for (int i = 0; i < refFkNames.size(); i++) {
            DataObjectAttributeRelationship fkField = refFkNames.get(i);

            if (fkField.getParentAttributeName().equals(fkName)) {
                fkValue = referenceWrap.getPropertyValueNullSafe(refPkNames.get(i));
                break;
            }
        }

        return fkValue;
    }
}
