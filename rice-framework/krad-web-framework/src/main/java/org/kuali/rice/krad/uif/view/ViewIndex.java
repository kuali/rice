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
package org.kuali.rice.krad.uif.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Holds component indexes of a {@link View} instance for convenient retrieval during the lifecycle.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewIndex implements Serializable {
    private static final long serialVersionUID = 4700818801272201371L;
    private static final Logger LOG = Logger.getLogger(ViewIndex.class);

    private Map<String, Component> index;
    private Map<String, DataField> dataFieldIndex;

    private Map<String, CollectionGroup> collectionsIndex;
    private Map<String, LifecycleElement> lifecycleElementsByPath;

    private Set<String> assignedIds;

    /**
     * Default Constructor.
     */
    public ViewIndex() {
        index = new HashMap<String, Component>();
        dataFieldIndex = new HashMap<String, DataField>();
        collectionsIndex = new HashMap<String, CollectionGroup>();
        lifecycleElementsByPath = new HashMap<String, LifecycleElement>();
        assignedIds = new HashSet<String>();
    }

    /**
     * Clears view indexes, for reinitializing indexes at the start of each phase.
     */
    protected void clearIndex(View view) {
        index = new HashMap<String, Component>();
        dataFieldIndex = new HashMap<String, DataField>();
        collectionsIndex = new HashMap<String, CollectionGroup>();
        lifecycleElementsByPath = new HashMap<String, LifecycleElement>();
    }

    /**
     * Adds an entry to the main index for the given component. If the component is of type
     * <code>DataField</code> or <code>CollectionGroup</code> an entry is created in the
     * corresponding indexes for those types as well. Then the #indexComponent method is called for
     * each of the component's children
     *
     * <p>
     * If the component is already contained in the indexes, it will be replaced
     * </p>
     *
     * <p>
     * <code>DataField</code> instances are indexed by the attribute path. This is useful for
     * retrieving the InputField based on the incoming request parameter
     * </p>
     *
     * <p>
     * <code>CollectionGroup</code> instances are indexed by the collection path. This is useful for
     * retrieving the CollectionGroup based on the incoming request parameter
     * </p>
     *
     * @param component component instance to index
     */
    public void indexComponent(Component component) {
        if (component == null) {
            return;
        }

        synchronized (index) {
            index.put(component.getId(), component);
        }

        synchronized (lifecycleElementsByPath) {
            lifecycleElementsByPath.put(component.getViewPath(), component);
        }

        if (component instanceof Container) {
            Container container = (Container) component;
            if (container.getLayoutManager() != null) {
                synchronized (lifecycleElementsByPath) {
                    lifecycleElementsByPath.put(container.getLayoutManager().getViewPath(),
                            container.getLayoutManager());
                }
            }
        }

        if (component instanceof DataField) {
            DataField field = (DataField) component;

            synchronized (dataFieldIndex) {
                dataFieldIndex.put(field.getBindingInfo().getBindingPath(), field);
            }
        } else if (component instanceof CollectionGroup) {
            CollectionGroup collectionGroup = (CollectionGroup) component;

            synchronized (collectionsIndex) {
                collectionsIndex.put(collectionGroup.getBindingInfo().getBindingPath(), collectionGroup);
            }
        }
    }

    /**
     * Observe an assigned ID.
     *
     * @param id ID to observe
     * @return true if the ID is unique, false if the ID has already been observed
     */
    public boolean observeAssignedId(String id) {
        if (assignedIds.contains(id)) {
            return false;
        }

        synchronized (assignedIds) {
            return assignedIds.add(id);
        }
    }

    /**
     * Retrieves a <code>Component</code> from the view index by Id
     *
     * @param id id for the component to retrieve
     * @return Component instance found in index, or null if no such component exists
     */
    public Component getComponentById(String id) {
        return index.get(id);
    }

    /**
     * Retrieves a <code>DataField</code> instance from the index
     *
     * @param propertyPath full path of the data field (from the form)
     * @return DataField instance for the path or Null if not found
     */
    public DataField getDataFieldByPath(String propertyPath) {
        return dataFieldIndex.get(propertyPath);
    }

    /**
     * Retrieves a <code>DataField</code> instance that has the given property name specified (note
     * this is not the full binding path and first match is returned)
     *
     * @param propertyName property name for field to retrieve
     * @return DataField instance found or null if not found
     */
    public DataField getDataFieldByPropertyName(String propertyName) {
        DataField dataField = null;

        for (DataField field : dataFieldIndex.values()) {
            if (StringUtils.equals(propertyName, field.getPropertyName())) {
                dataField = field;
                break;
            }
        }

        return dataField;
    }

    /**
     * Gets the Map of lifecycle elements that are indexed by their path relative to the view.
     *
     * @return map of all indexed lifecycle elements, key is the element path and value is the element instance
     */
    public Map<String, LifecycleElement> getLifecycleElementsByPath() {
        return lifecycleElementsByPath;
    }

    /**
     * Gets a lifecycle element instance by the given path (relative to the view).
     *
     * @param path path of the element that should be returned
     * @return lifecycle element instance for given path or null if element at that path does not exist
     */
    public LifecycleElement getLifecycleElementByPath(String path) {
        if ((lifecycleElementsByPath != null) && lifecycleElementsByPath.containsKey(path)) {
            return lifecycleElementsByPath.get(path);
        }

        return null;
    }

    /**
     * Gets the Map that contains attribute field indexing information. The Map key points to an
     * attribute binding path, and the Map value is the <code>DataField</code> instance
     *
     * @return data fields index map
     */
    public Map<String, DataField> getDataFieldIndex() {
        return this.dataFieldIndex;
    }

    /**
     * Gets the Map that contains collection indexing information. The Map key gives the binding
     * path to the collection, and the Map value givens the <code>CollectionGroup</code> instance
     *
     * @return collection index map
     */
    public Map<String, CollectionGroup> getCollectionsIndex() {
        return this.collectionsIndex;
    }

    /**
     * Retrieves a <code>CollectionGroup</code> instance from the index
     *
     * @param collectionPath full path of the collection (from the form)
     * @return CollectionGroup instance for the collection path or Null if not found
     */
    public CollectionGroup getCollectionGroupByPath(String collectionPath) {
        return collectionsIndex.get(collectionPath);
    }

    /**
     * Returns a clone of the view index.
     *
     * @return ViewIndex clone
     */
    public ViewIndex copy() {
        ViewIndex viewIndexCopy = new ViewIndex();

        if (this.index != null) {
            Map<String, Component> indexCopy = new HashMap<String, Component>();
            for (Map.Entry<String, Component> indexEntry : this.index.entrySet()) {
                if (indexEntry.getValue() instanceof View) {
                    LOG.warn("View reference at " + indexEntry);
                } else {
                    indexCopy.put(indexEntry.getKey(), (Component) CopyUtils.copy(indexEntry.getValue()));
                }
            }

            viewIndexCopy.index = indexCopy;
        }

        if (this.dataFieldIndex != null) {
            Map<String, DataField> dataFieldIndexCopy = new HashMap<String, DataField>();
            for (Map.Entry<String, DataField> indexEntry : this.dataFieldIndex.entrySet()) {
                dataFieldIndexCopy.put(indexEntry.getKey(), (DataField) CopyUtils.copy(indexEntry.getValue()));
            }

            viewIndexCopy.dataFieldIndex = dataFieldIndexCopy;
        }

        if (this.collectionsIndex != null) {
            Map<String, CollectionGroup> collectionsIndexCopy = new HashMap<String, CollectionGroup>();
            for (Map.Entry<String, CollectionGroup> indexEntry : this.collectionsIndex.entrySet()) {
                collectionsIndexCopy.put(indexEntry.getKey(), (CollectionGroup) CopyUtils.copy(indexEntry.getValue()));
            }

            viewIndexCopy.collectionsIndex = collectionsIndexCopy;
        }

        return viewIndexCopy;
    }

}
