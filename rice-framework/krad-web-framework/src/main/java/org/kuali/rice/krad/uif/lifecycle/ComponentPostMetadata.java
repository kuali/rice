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

import org.kuali.rice.core.api.util.tree.Tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentPostMetadata implements Serializable {
    private static final long serialVersionUID = -6090575873840392956L;

    private String id;
    private String path;

    private Map<String, Tree<String, String>> refreshPathMappings;
    private Map<String, Object> data;

    public ComponentPostMetadata(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Tree<String, String>> getRefreshPathMappings() {
        return refreshPathMappings;
    }

    public void setRefreshPathMappings(Map<String, Tree<String, String>> refreshPathMappings) {
        this.refreshPathMappings = refreshPathMappings;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }

        this.data.put(key, value);
    }

    public Object getData(String key) {
        if (this.data != null) {
            return this.data.get(key);
        }

        return null;
    }
}
