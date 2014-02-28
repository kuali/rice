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
package org.kuali.rice.krad.uif.lifecycle;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds data about a component that might be needed to handle a post request.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewPostMetadata
 */
public class ComponentPostMetadata implements Serializable {
    private static final long serialVersionUID = -6090575873840392956L;

    private String id;
    private String path;

    private Map<String, String> phasePathMapping;
    private Map<String, List<String>> refreshPathMappings;
    private boolean isDetachedComponent;

    private Map<String, Object> unmodifiableData;
    private Map<String, Object> data;

    /**
     * Constructor taking the id for the component to store metadata for.
     *
     * @param id component id
     */
    public ComponentPostMetadata(String id) {
        this.id = id;
    }

    /**
     * Id for the component the post metadata is associated with.
     *
     * <p>The id can be used to retrieve the component post metadata from the view post metadata</p>
     *
     * @return component id
     */
    public String getId() {
        return id;
    }

    /**
     * @see ComponentPostMetadata#getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Path of the component within the view structure (tree).
     *
     * <p>This is set during the lifecycle process and used to retrieve the component for refresh calls</p>
     *
     * @return path from view
     */
    public String getPath() {
        return path;
    }

    /**
     * @see ComponentPostMetadata#getPath()
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Map containing the path for the component at each lifecycle phase.
     *
     * <p>Note this is only stored if the path of the component is different from the {@link #getPath()}
     * at any of the phases</p>
     *
     * @return map where key is the phase name and the value is the component's path
     */
    public Map<String, String> getPhasePathMapping() {
        return phasePathMapping;
    }

    /**
     * @see ComponentPostMetadata#getPhasePathMapping()
     */
    public void setPhasePathMapping(Map<String, String> phasePathMapping) {
        this.phasePathMapping = phasePathMapping;
    }

    /**
     * Map of property paths whose lifecycle will be run when the component is refreshed.
     *
     * <p>Each map entry contains a tree of paths to process for each of the lifecycle phases. This is so parents
     * of the component (in each phase) are picked up during the refresh process</p>
     *
     * @return map of refresh paths
     */
    public Map<String, List<String>> getRefreshPathMappings() {
        return refreshPathMappings;
    }

    /**
     * @see ComponentPostMetadata#getRefreshPathMappings()
     */
    public void setRefreshPathMappings(Map<String, List<String>> refreshPathMappings) {
        this.refreshPathMappings = refreshPathMappings;
    }

    /**
     * Indicates whether the component is detached from the view (not in the view's structure, but an external
     * component, for example a dialog).
     *
     * <p>This is used by the component refresh process to determine whether it can get the component instance
     * by its path from the view (in the case of the component being attached), or if it must use its id (in the
     * case of the component being detached).</p>
     *
     * @return boolean true if the component is detached, false if not
     */
    public boolean isDetachedComponent() {
        return isDetachedComponent;
    }

    /**
     * @see ComponentPostMetadata#isDetachedComponent
     */
    public void setDetachedComponent(boolean isDetachedComponent) {
        this.isDetachedComponent = isDetachedComponent;
    }

    /**
     * General post data that has been stored for the component.
     *
     * <p>Holds the general post data for a component. Any piece of data can be added to this map and then
     * retrieved on a post call (for example in a controller method)</p>
     *
     * <p>Note map returned is unmodifiable. Use {@link ComponentPostMetadata#addData(java.lang.String,
     * java.lang.Object)}
     * to add new data entries</p>
     *
     * @return unmodifiable map of data
     */
    public Map<String, Object> getData() {
        if (unmodifiableData == null) {
            if (data == null) {
                unmodifiableData = Collections.emptyMap();
            } else {
                unmodifiableData = Collections.unmodifiableMap(data);
            }
        }

        return unmodifiableData;
    }

    /**
     * @see ComponentPostMetadata#getData()
     */
    public void setData(Map<String, Object> data) {
        this.unmodifiableData = null;
        this.data = data;
    }

    /**
     * Adds a new data entry to the components post data.
     *
     * @param key key for data, which is used to retrieve the data
     * @param value data value
     * @see ComponentPostMetadata#getData()
     */
    public void addData(String key, Object value) {
        if (this.data == null) {
            setData(new HashMap<String, Object>());
        }

        if (this.data.get(key) != value) {
            synchronized (this.data) {
                this.data.put(key, value);
            }
        }
    }

    /**
     * Retrieves a post data value for the component.
     *
     * @param key key for the data value to retrieve
     * @return data value, or null if data does not exist
     * @see ComponentPostMetadata#getData()
     */
    public Object getData(String key) {
        if (this.data != null) {
            return this.data.get(key);
        }

        return null;
    }
}
