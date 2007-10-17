/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.lookupable;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;

/**
 * A Row is a collection of {@link Field} objects that represents a row of results
 * from a {@link WorkflowLookupable}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Row implements java.io.Serializable {
    private static final long serialVersionUID = -3716365412750461233L;
	private List<Field> fields;
    private String rowsGroupLabel;
    private int numberOfGroupRows;

    public Row() {
    	this.fields = new ArrayList<Field>();
    }
    
    public Row(List<Field> fields) {
        this.fields = fields;
    }
    public Row(List<Field> fields, String rowsGroupLabel, int numberOfGroupRows) {
        this.fields = fields;
        this.rowsGroupLabel = rowsGroupLabel;
        this.numberOfGroupRows = numberOfGroupRows;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Field getField(int index) {
        while (fields.size() <= index) {
        	Field field = new Field();
        	fields.add(field);
        }
        return (Field) fields.get(index);
    }

    public void setField(Field field, int index) {
        this.fields.set(index, field);
    }

    public String getRowsGroupLabel() {
        return rowsGroupLabel;
    }

    public void setRowsGroupLabel(String rowsGroupLabel) {
        this.rowsGroupLabel = rowsGroupLabel;
    }

    public int getNumberOfGroupRows() {
        return numberOfGroupRows;
    }

    public void setNumberOfGroupRows(int numberOfGroupRows) {
        this.numberOfGroupRows = numberOfGroupRows;
    }
}
