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
package org.kuali.rice.krad.uif.lifecycle;

import org.kuali.rice.krad.uif.component.Component;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewPostMetadata implements Serializable {
    private static final long serialVersionUID = -515221881981451818L;

    private String id;
    
    private boolean persistFormToSession;

    private Map<String, ComponentPostMetadata> componentPostMetadataMap;

    private Map<String, PropertyEditor> fieldPropertyEditors;
    private Map<String, PropertyEditor> secureFieldPropertyEditors;

    private Set<String> inputFieldIds;

    public ViewPostMetadata() {
        fieldPropertyEditors = new HashMap<String, PropertyEditor>();
        secureFieldPropertyEditors = new HashMap<String, PropertyEditor>();
        inputFieldIds = new HashSet<String>();
    }

    public ViewPostMetadata(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentPath(String componentId) {
        if ((componentPostMetadataMap == null) || !componentPostMetadataMap.containsKey(componentId)) {
            return null;
        }

        ComponentPostMetadata postMetadata = componentPostMetadataMap.get(componentId);

        return postMetadata.getPath();
    }

    public Map<String, ComponentPostMetadata> getComponentPostMetadataMap() {
        return componentPostMetadataMap;
    }

    public void setComponentPostMetadataMap(Map<String, ComponentPostMetadata> componentPostMetadataMap) {
        this.componentPostMetadataMap = componentPostMetadataMap;
    }

    public ComponentPostMetadata getComponentPostMetadata(String componentId) {
        ComponentPostMetadata componentPostMetadata = null;

        if (componentPostMetadataMap != null && (componentPostMetadataMap.containsKey(componentId))) {
            componentPostMetadata = componentPostMetadataMap.get(componentId);
        }

        return componentPostMetadata;
    }

    public void addComponentPostData(Component component, String key, Object value) {
        if (component == null) {
            throw new IllegalArgumentException("Component must not be null for adding post data");
        }

        addComponentPostData(component.getId(), key, value);
    }

    public void addComponentPostData(String componentId, String key, Object value) {
        ComponentPostMetadata componentPostMetadata = initializeComponentPostMetadata(componentId);

        componentPostMetadata.addData(key, value);
    }

    public Object getComponentPostData(String componentId, String key) {
        ComponentPostMetadata componentPostMetadata = getComponentPostMetadata(componentId);

        if (componentPostMetadata != null) {
            return componentPostMetadata.getData(key);
        }

        return null;
    }

    public ComponentPostMetadata initializeComponentPostMetadata(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Component must not be null to initialize post metadata");
        }

        return initializeComponentPostMetadata(component.getId());
    }

    public ComponentPostMetadata initializeComponentPostMetadata(String componentId) {
        ComponentPostMetadata componentPostMetadata;

        if (componentPostMetadataMap != null && (componentPostMetadataMap.containsKey(componentId))) {
            componentPostMetadata = componentPostMetadataMap.get(componentId);
        } else {
            componentPostMetadata = new ComponentPostMetadata(componentId);

            if (componentPostMetadataMap == null) {
                componentPostMetadataMap = new HashMap<String, ComponentPostMetadata>();
            }

            componentPostMetadataMap.put(componentId, componentPostMetadata);
        }

        return componentPostMetadata;
    }

    /**
     * Maintains configuration of properties that have been configured for the view (if render was
     * set to true) and there corresponding PropertyEdtior (if configured).
     *
     * <p>Information is pulled out of the View during the lifecycle so it can be used when a form post
     * is done from the View. Note if a field is secure, it will be placed in the
     * {@link #getSecureFieldPropertyEditors()} map instead</p>
     *
     * @return map of property path (full) to PropertyEditor
     */
    public Map<String, PropertyEditor> getFieldPropertyEditors() {
        return fieldPropertyEditors;
    }

    /**
     * Associates a property editor instance with the given property path.
     *
     * @param propertyPath path for the property the editor should be associated with
     * @param propertyEditor editor instance to use when binding data for the property
     */
    public void addFieldPropertyEditor(String propertyPath, PropertyEditor propertyEditor) {
        if (fieldPropertyEditors == null) {
            fieldPropertyEditors = new HashMap<String, PropertyEditor>();
        }

        fieldPropertyEditors.put(propertyPath, propertyEditor);
    }

    /**
     * Maintains configuration of secure properties that have been configured for the view (if
     * render was set to true) and there corresponding PropertyEdtior (if configured).
     *
     * <p>Information is pulled out of the View during the lifecycle so it can be used when a form post
     * is done from the View. Note if a field is non-secure, it will be placed in the
     * {@link #getFieldPropertyEditors()} map instead</p>
     *
     * @return map of property path (full) to PropertyEditor
     */
    public Map<String, PropertyEditor> getSecureFieldPropertyEditors() {
        return secureFieldPropertyEditors;
    }

    /**
     * Associates a secure property editor instance with the given property path.
     *
     * @param propertyPath path for the property the editor should be associated with
     * @param propertyEditor secure editor instance to use when binding data for the property
     */
    public void addSecureFieldPropertyEditor(String propertyPath, PropertyEditor propertyEditor) {
        if (secureFieldPropertyEditors == null) {
            secureFieldPropertyEditors = new HashMap<String, PropertyEditor>();
        }

        secureFieldPropertyEditors.put(propertyPath, propertyEditor);
    }

    public Set<String> getInputFieldIds() {
        return inputFieldIds;
    }

    public void setInputFieldIds(Set<String> inputFieldIds) {
        this.inputFieldIds = inputFieldIds;
    }

    public boolean isPersistFormToSession() {
        return persistFormToSession;
    }

    public void setPersistFormToSession(boolean persistFormToSession) {
        this.persistFormToSession = persistFormToSession;
    }
}
