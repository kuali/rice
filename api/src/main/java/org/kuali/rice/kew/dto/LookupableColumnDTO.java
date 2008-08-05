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
package org.kuali.rice.kew.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a virtual object representing a Column object
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LookupableColumnDTO implements Serializable {

    private static final long serialVersionUID = 1383049310325271008L;

    private String columnTitle;
    private boolean sortable;
    private String key;
    private String propertyName;
    private String sortPropertyName;
    private String type;
    private List<KeyValueDTO> displayParameters = new ArrayList<KeyValueDTO>();

    public String getColumnTitle() {
        return this.columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public boolean isSortable() {
        return this.sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSortPropertyName() {
        return this.sortPropertyName;
    }

    public void setSortPropertyName(String sortPropertyName) {
        this.sortPropertyName = sortPropertyName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<KeyValueDTO> getDisplayParameters() {
        return this.displayParameters;
    }

    public void setDisplayParameters(List<KeyValueDTO> displayParameters) {
        this.displayParameters = displayParameters;
    }

}
