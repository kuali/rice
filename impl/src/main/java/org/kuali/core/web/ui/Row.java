/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a row of fields on the ui.
 */
public class Row implements java.io.Serializable {
    
    private static final long serialVersionUID = 5920833652172097098L;
    private List<Field> fields;
    private boolean hidden;

    public Row() {
        fields = new ArrayList<Field>();
        hidden = false;
    }

    public Row(List<Field> fields) {
        this.fields = fields;
        hidden = false;
    }

    public Row(Field field) {
        this.fields = new ArrayList<Field>();
        fields.add(field);
        hidden = false;
    }

    /**
     * @return the fields contained in the row
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to be displayed in the row.
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
}
