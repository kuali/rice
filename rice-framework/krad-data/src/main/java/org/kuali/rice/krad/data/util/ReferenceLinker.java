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
package org.kuali.rice.krad.data.util;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataChild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* The ReferenceLinker provides functionality that allows for ensuring that relationships and foreign key state are
* populated and kept in sync as changes are made to a data object.
*
* <p>
*     This may include fetching relationships as keys are changed that would necessitate updating the object graph, and
*     may also ensure that foreign key values are kept in sync in situations where a data object may have more than one
*     field or object that stores the same foreign key.
* </p>
*
* <p>
*     This class has a single method {@link #linkChanges(Object, java.util.Set)} which takes a data object and a list
*     of property paths for fields which have been modified. It then uses this information determine how to link up
*     relationships and foreign key fields, recursing through the object graph as needed.
* </p>
*
* <p>
*     Linking occurs from the bottom up, such that this class will attempt to perform a post-order traversal to visit
*     the modified objects furthest from the root first, and then backtracking and linking back to the root. The linking
*     algorithm handles circular references as well to ensure that the linking process terminates successfully.
* </p>
*
* <p>
*     The ReferenceLinker requires access to the {@link DataObjectService} so it must be injected using the
*     provided {@link #setDataObjectService(org.kuali.rice.krad.data.DataObjectService)} method.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public class ReferenceLinker {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceLinker.class);

    private DataObjectService dataObjectService;

    /**
    * Returns the DataObjectService used by this class
    *
    * @return the DataObjectService used by this class
    */
    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    /**
    * Specify the DataObjectService to be used during linking.
    *
    * <p>
    *     The linker will use the DataObjectService to fetch relationships and query metadata.
    * </p>
    *
    * @param dataObjectService the DataObjectService to inject
    */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Performs linking of references and keys for the given root object based on the given set of changed property
     * paths that should be considered during the linking process.
     *
     * <p>
     *     The root object should be non-null and also a valid data object (such that
     *     {@link DataObjectService#supports(Class)} returns true for it). If neither of these conditions is true, this
     *     method will return immediately.
     * </p>
     * <p>
     *     See class-level documentation for specifics on how the linking algorithm functions.
     * </p>
     *
     * @param rootObject the root object from which to perform the linking
     * @param changedPropertyPaths the set of property paths relative to the root object that should be considered
     * modified by the linking algorithm
     */
    public void linkChanges(Object rootObject, Set<String> changedPropertyPaths) {
        if (rootObject == null || CollectionUtils.isEmpty(changedPropertyPaths)) {
            return;
        }
        Class<?> type = rootObject.getClass();
        if (!dataObjectService.supports(type)) {
            LOG.info("Object supplied for linking is not a supported data object type: " + type);
            return;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Performing linking on instance of " + type + " with the following changed property paths: " +
                    Arrays.toString(changedPropertyPaths.toArray()));
        }
        Map<String, Set<String>> decomposedPaths = decomposePropertyPaths(changedPropertyPaths);
        linkChangesInternal(rootObject, decomposedPaths, new HashSet<Object>());
    }

    /**
     * Internal implementation of link changes which is implemented to support recursion through the object graph.
     *
     * @param object the object from which to link
     * @param changedPropertyPaths a decomposed property path map where the key of the map is a direct property path
     * relative to the given object and the values are the remainder of the path relative to the parent path, see
     * {@link #decomposePropertyPaths(java.util.Set)}
     * @param linked a set containing objects which have already been linked, used to prevent infinite recursion due to
     * circular references
     */
    protected void linkChangesInternal(Object object, Map<String, Set<String>> changedPropertyPaths,
            Set<Object> linked) {
        if (object == null || linked.contains(object) || !dataObjectService.supports(object.getClass()) ||
                changedPropertyPaths.isEmpty()) {
            return;
        }
        linked.add(object);
        DataObjectWrapper<?> wrapped = dataObjectService.wrap(object);

        // execute the linking
        linkRelationshipChanges(wrapped, changedPropertyPaths, linked);
        linkCollectionChanges(wrapped, changedPropertyPaths, linked);
        cascadeLinkingAnnotations(wrapped, changedPropertyPaths, linked);
    }

    /**
    * Link changes for relationships on the given wrapped data object.
    *
    * @param wrapped the wrapped data object
    * @param decomposedPaths the decomposed map of changed property paths
    * @param linked a set containing objects which have already been linked
    */
    protected void linkRelationshipChanges(DataObjectWrapper<?> wrapped, Map<String, Set<String>> decomposedPaths,
            Set<Object> linked) {
        List<DataObjectRelationship> relationships = wrapped.getMetadata().getRelationships();
        for (DataObjectRelationship relationship : relationships) {
            String relationshipName = relationship.getName();

            // let's get the current value and recurse down if it's a relationship that is cascaded on save
            if (relationship.isSavedWithParent() && decomposedPaths.containsKey(relationshipName)) {
                Object value = wrapped.getPropertyValue(relationshipName);
                Map<String, Set<String>> nextPropertyPaths =
                        decomposePropertyPaths(decomposedPaths.get(relationshipName));
                linkChangesInternal(value, nextPropertyPaths, linked);
            }

            // once we have linked from the bottom up,
            // let's check if any FK attribute modifications have occurred for this relationship
            List<DataObjectAttributeRelationship> attributeRelationships = relationship.getAttributeRelationships();
            boolean modifiedAttributeRelationship = false;
            for (DataObjectAttributeRelationship attributeRelationship : attributeRelationships) {
                if (decomposedPaths.containsKey(attributeRelationship.getParentAttributeName())) {
                    modifiedAttributeRelationship = true;
                    break;
                }
            }
            if (modifiedAttributeRelationship) {
                // use FK attributes and nullify dangling relationship
                wrapped.fetchRelationship(relationshipName, true, true);
            } else if (decomposedPaths.containsKey(relationshipName)) {
                // check if any portion of the primary key has been modified
                Class<?> targetType = relationship.getRelatedType();
                DataObjectMetadata targetMetadata =
                        dataObjectService.getMetadataRepository().getMetadata(targetType);
                Set<String> modifiedPropertyPaths = decomposedPaths.get(relationshipName);
                if (isPrimaryKeyModified(targetMetadata, modifiedPropertyPaths)) {
                    // if the primary key is modified, fetch and replace the related object
                    // this will also copy FK values back to the parent object if it has FK values
                    wrapped.fetchRelationship(relationshipName, false, false);
                } else {
                    // otherwise, we still want to backward copy keys on the relationship, since this relationship has
                    // been explicity included in the set of changes, we pass false for onlyLinkReadyOnly because we
                    // don't care if the FK field is read only or not, we want to copy back the value regardless
                    wrapped.linkForeignKeys(relationshipName, false);
                }
            }
        }
    }

    /**
    * Link changes for collections on the given wrapped data object.
    *
    * @param wrapped the wrapped data object
    * @param decomposedPaths the decomposed map of changed property paths
    * @param linked a set containing objects which have already been linked
    */
    protected void linkCollectionChanges(DataObjectWrapper<?> wrapped, Map<String, Set<String>> decomposedPaths,
            Set<Object> linked) {
        List<DataObjectCollection> collections = wrapped.getMetadata().getCollections();
        for (DataObjectCollection collectionMetadata : collections) {
            // We only process collections if they are being saved with the parent
            if (collectionMetadata.isSavedWithParent()) {
                Set<Integer> modifiedIndicies = extractModifiedIndicies(collectionMetadata, decomposedPaths);
                if (!modifiedIndicies.isEmpty()) {
                    Object collectionValue = wrapped.getPropertyValue(collectionMetadata.getName());
                    if (collectionValue instanceof Iterable<?>) {
                        int index = 0;
                        // loop over all elements in the collection
                        for (Object element : (Iterable<?>)collectionValue) {
                            // check if index is modified, or we use MAX_VALUE to indicate a modification to the
                            // collection itself
                            if (modifiedIndicies.contains(Integer.valueOf(Integer.MAX_VALUE)) ||
                                    modifiedIndicies.contains(Integer.valueOf(index))) {
                                // recurse down and link the collection element
                                String pathKey = collectionMetadata.getName() + "[" + index + "]";
                                if (decomposedPaths.containsKey(pathKey)) {
                                    Map<String, Set<String>> nextPropertyPaths =
                                            decomposePropertyPaths(decomposedPaths.get(pathKey));
                                    linkChangesInternal(element, nextPropertyPaths, linked);
                                }
                                if (dataObjectService.supports(element.getClass())) {
                                    DataObjectWrapper<?> elementWrapper = dataObjectService.wrap(element);
                                    linkBiDirectionalCollection(wrapped, elementWrapper, collectionMetadata);
                                }
                            }
                            index++;
                        }
                    }
                }
            }
        }
    }

    /**
    * Performs bi-directional collection linking, ensuring that for bi-directional collection relationships that both
    * sides of the relationship are properly populated.
    *
    * @param collectionMetadata collection
    * @param elementWrapper element
    * @param parentWrapper parent
    */
    protected void linkBiDirectionalCollection(DataObjectWrapper<?> parentWrapper,
            DataObjectWrapper<?> elementWrapper, DataObjectCollection collectionMetadata) {
        MetadataChild inverseRelationship = collectionMetadata.getInverseRelationship();
        if (inverseRelationship != null) {
            // if there is an inverse relationship, make sure the element is the collection is pointing back to it's parent
            elementWrapper.setPropertyValue(inverseRelationship.getName(), parentWrapper.getWrappedInstance());
            // if there is a foreign key value to link, then link it, not that we pass false here, since we just set
            // our reference to the relationship, we know that we want to copy the key back
            elementWrapper.linkForeignKeys(inverseRelationship.getName(), false);
        }
    }

    /**
    * Gets indexes that have been modified.
    *
    * <p>
    *     Returns a set of indexes which have been modified in the given collection. If the returned set contains
    *     {@link java.lang.Integer#MAX_VALUE} then it means that it should be treated as if all items in the collection
    *     have been modified.
    * </p>
    *
    * @return indexes which have been modified in the given collection
    */
    private Set<Integer> extractModifiedIndicies(DataObjectCollection collectionMetadata,
            Map<String, Set<String>> decomposedPaths) {
        String relationshipName = collectionMetadata.getName();
        Set<Integer> modifiedIndicies = Sets.newHashSet();
        // if it contains *exactly* the collection relationship name, then indicate that all items modified
        if (decomposedPaths.containsKey(relationshipName)) {
            modifiedIndicies.add(Integer.valueOf(Integer.MAX_VALUE));
        }
        for (String propertyName : decomposedPaths.keySet()) {
            if (relationshipName.equals(PropertyAccessorUtils.getPropertyName(relationshipName))) {
                Integer index = extractIndex(propertyName);
                if (index != null) {
                    modifiedIndicies.add(index);
                }
            }
        }
        return modifiedIndicies;
    }

    /**
    * Gets index of property name.
    *
    * <p>
    *     Returns the index number of the location of the given property name.
    * </p>
    *
    * @param propertyName name of property to find index of.
    * @return index number representing location of property name.
    */
    private Integer extractIndex(String propertyName) {
        int firstIndex = propertyName.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR);
        int lastIndex = propertyName.lastIndexOf(PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR);
        if (firstIndex != -1 && lastIndex != -1) {
            String indexValue = propertyName.substring(firstIndex + 1, lastIndex);
            try {
                int index = Integer.parseInt(indexValue);
                return Integer.valueOf(index);
            } catch (NumberFormatException e) {
                // if we encounter this then it wasn't really an index, ignore
            }
        }
        return null;
    }

    /**
    * Checks if primary key can be modified.
    *
    * @return whether primary key can be modified.
    */
    protected boolean isPrimaryKeyModified(DataObjectMetadata metadata, Set<String> modifiedPropertyPaths) {
        Set<String> primaryKeyAttributeNames = new HashSet<String>(metadata.getPrimaryKeyAttributeNames());
        for (String modifiedPropertyPath : modifiedPropertyPaths) {
            if (primaryKeyAttributeNames.contains(modifiedPropertyPath)) {
                return true;
            }
        }
        return false;
    }

    /**
    * Gets indexes that have been modified.
    *
    * <p>
    *     Returns a list of cascade links in the field names that are also in the decomposed paths.
    * </p>
    *
    * @param decomposedPaths contains field names to be used.
    * @param linked
    * @param wrapped used to get all field names.
    */
    protected void cascadeLinkingAnnotations(DataObjectWrapper<?> wrapped, Map<String, Set<String>> decomposedPaths,
            Set<Object> linked) {
        Field[] fields = FieldUtils.getAllFields(wrapped.getWrappedClass());
        Map<String, Field> modifiedFieldMap = new HashMap<String, Field>();
        for (Field field : fields) {
            if (decomposedPaths.containsKey(field.getName())) {
                modifiedFieldMap.put(field.getName(), field);
            }
        }
        for (String modifiedFieldName : modifiedFieldMap.keySet()) {
            Field modifiedField = modifiedFieldMap.get(modifiedFieldName);
            Link link = modifiedField.getAnnotation(Link.class);
            if (link == null) {
                // check if they have an @Link on the class itself
                link = AnnotationUtils.findAnnotation(modifiedField.getType(), Link.class);
            }
            if (link != null && link.cascade()) {
                List<String> linkingPaths = assembleLinkingPaths(link);
                for (String linkingPath : linkingPaths) {
                    Map<String, Set<String>> decomposedLinkingPath =
                            decomposePropertyPaths(decomposedPaths.get(modifiedFieldName), linkingPath);
                    String valuePath = modifiedFieldName;
                    if (StringUtils.isNotBlank(linkingPath)) {
                        valuePath = valuePath + "." + link.path();
                    }
                    Object linkRootObject = wrapped.getPropertyValueNullSafe(valuePath);
                    linkChangesInternal(linkRootObject, decomposedLinkingPath, linked);
                }
            }
        }
    }

    /**
    * Returns a list of link paths based on provided link.
    *
    * @param link used get paths from.
    * @return list of paths
    */
    protected List<String> assembleLinkingPaths(Link link) {
        List<String> linkingPaths = new ArrayList<String>();
        if (ArrayUtils.isEmpty(link.path())) {
            linkingPaths.add("");
        } else {
            for (String path : link.path()) {
                linkingPaths.add(path);
            }
        }
        return linkingPaths;
    }

    /**
    * Returns decomposed property paths based on changedPropertyPaths
    *
    * @param changedPropertyPaths changes to property paths
    * @return map decomposed property paths with changedPropertyPaths
    */
    protected Map<String, Set<String>> decomposePropertyPaths(Set<String> changedPropertyPaths) {
        return decomposePropertyPaths(changedPropertyPaths, "");
    }

    /**
    * Returns decomposed property paths that start with the provide prefix
    *
    * @param changedPropertyPaths changes to property paths
    * @param prefix filter that paths must start with
    * @return map decomposed property paths with changedPropertyPaths
    */
    protected Map<String, Set<String>> decomposePropertyPaths(Set<String> changedPropertyPaths, String prefix) {
        // strip the prefix off any changed properties
        Set<String> processedProperties = new HashSet<String>();
        if (StringUtils.isNotBlank(prefix) && changedPropertyPaths != null) {
            for (String changedPropertyPath : changedPropertyPaths) {
                if (changedPropertyPath.startsWith(prefix)) {
                    processedProperties.add(changedPropertyPath.substring(prefix.length() + 1));
                }
            }
        } else {
            processedProperties = changedPropertyPaths;
        }
        Map<String, Set<String>> decomposedPropertyPaths = new HashMap<String, Set<String>>();
        for (String changedPropertyPath : processedProperties) {
            int index = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(changedPropertyPath);
            if (index == -1) {
                decomposedPropertyPaths.put(changedPropertyPath, new HashSet<String>());
            } else {
                String pathEntry = changedPropertyPath.substring(0, index);
                Set<String> remainingPaths = decomposedPropertyPaths.get(pathEntry);
                if (remainingPaths == null) {
                    remainingPaths = new HashSet<String>();
                    decomposedPropertyPaths.put(pathEntry, remainingPaths);
                }
                String remainingPath = changedPropertyPath.substring(index + 1);
                remainingPaths.add(remainingPath);
            }
        }
        return decomposedPropertyPaths;
    }

}
