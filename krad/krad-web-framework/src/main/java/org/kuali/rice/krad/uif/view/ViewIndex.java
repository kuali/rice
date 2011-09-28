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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.util.ComponentUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds field indexes of a <code>View</code> instance for retrieval
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewIndex implements Serializable {
    private static final long serialVersionUID = 4700818801272201371L;

    private Map<String, Component> index;
    private Map<String, AttributeField> attributeFieldIndex;
    private Map<String, CollectionGroup> collectionsIndex;

    private Map<String, Component> initialComponentStates;

    /**
     * Constructs new instance
     */
    public ViewIndex() {
        initialComponentStates = new HashMap<String, Component>();
    }

    /**
     * Walks through the View tree and indexes all components found. All components
     * are indexed by their IDs with the special indexing done for certain components
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
    protected void index(View view) {
        index = new HashMap<String, Component>();
        attributeFieldIndex = new HashMap<String, AttributeField>();
        collectionsIndex = new HashMap<String, CollectionGroup>();

        indexComponent(view);
    }

    /**
     * Adds an entry to the main index for the given component. If the component
     * is of type <code>AttributeField</code> or <code>CollectionGroup</code> an
     * entry is created in the corresponding indexes for those types as well. Then
     * the #indexComponent method is called for each of the component's children
     *
     * <p>
     * If the component is already contained in the indexes, it will be replaced
     * </p>
     *
     * @param component - component instance to index
     */
    public void indexComponent(Component component) {
        if (component == null) {
            return;
        }

        index.put(component.getId(), component);

        if (component instanceof AttributeField) {
            AttributeField field = (AttributeField) component;
            attributeFieldIndex.put(field.getBindingInfo().getBindingPath(), field);
        } else if (component instanceof CollectionGroup) {
            CollectionGroup collectionGroup = (CollectionGroup) component;
            collectionsIndex.put(collectionGroup.getBindingInfo().getBindingPath(), collectionGroup);
        }

        for (Component nestedComponent : component.getNestedComponents()) {
            indexComponent(nestedComponent);
        }
    }

    /**
     * Retrieves a <code>Component</code> from the view index by Id
     *
     * @param id - id for the component to retrieve
     * @return Component instance found in index, or null if no such component exists
     */
    public Component getComponentById(String id) {
        return index.get(id);
    }

    /**
     * Retrieves a <code>AttributeField</code> instance from the index
     *
     * @param attributePath - full path of the attribute (from the form)
     * @return AttributeField instance for the path or Null if not found
     */
    public AttributeField getAttributeFieldByPath(String attributePath) {
        return attributeFieldIndex.get(attributePath);
    }

    /**
     * Retrieves a <code>AttributeField</code> instance that has the given property name
     * specified (note this is not the full binding path and first match is returned)
     *
     * @param propertyName - property name for field to retrieve
     * @return AttributeField instance found or null if not found
     */
    public AttributeField getAttributeFieldByPropertyName(String propertyName) {
        AttributeField attributeField = null;

        for (AttributeField field : attributeFieldIndex.values()) {
            if (StringUtils.equals(propertyName, field.getPropertyName())) {
                attributeField = field;
                break;
            }
        }

        return attributeField;
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
     * Retrieves a <code>CollectionGroup</code> instance from the index
     *
     * @param collectionPath - full path of the collection (from the form)
     * @return CollectionGroup instance for the collection path or Null if not
     *         found
     */
    public CollectionGroup getCollectionGroupByPath(String collectionPath) {
        return collectionsIndex.get(collectionPath);
    }

    /**
     * Preserves initial state of components needed for doing component refreshes
     *
     * <p>
     * Some components, such as those that are nested or created in code cannot be requested from the
     * spring factory to get new instances. For these a copy of the component in its initial state is
     * set in this map which will be used when doing component refreshes (which requires running just that
     * component's lifecycle)
     * </p>
     *
     * <p>
     * Map entries are added during the perform initialize phase from {@link org.kuali.rice.krad.uif.service.ViewHelperService}
     * </p>
     *
     * @return Map<String, Component> - map with key giving the factory id for the component and the value the
     *         component
     *         instance
     */
    public Map<String, Component> getInitialComponentStates() {
        return initialComponentStates;
    }

    /**
     * Adds a copy of the given component instance to the map of initial component states keyed by the component
     * factory id
     *
     * @param component - component instance to add
     */
    public void addInitialComponentState(Component component) {
        initialComponentStates.put(component.getFactoryId(), ComponentUtils.copy(component));
    }

    /**
     * Setter for the map holding initial component states
     *
     * @param initialComponentStates
     */
    public void setInitialComponentStates(Map<String, Component> initialComponentStates) {
        this.initialComponentStates = initialComponentStates;
    }
}
