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
package org.kuali.rice.krad.uif.layout.collections;

import org.kuali.rice.krad.uif.field.Field;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the components that make up a row within the table layout.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.layout.TableLayoutManager
 */
public class TableRow implements Serializable {
    private static final long serialVersionUID = -3566983879980459225L;

    private List<Field> columns;

    /**
     * Empty Constructor.
     */
    public TableRow() {

    }

    /**
     * Constructor with columns for the row.
     *
     * @param columns list of fields that make up the row's columns
     */
    public TableRow(List<Field> columns) {
        this.columns = columns;
    }

    /**
     * List of field components that make up the row's columns.
     *
     * @return list of field components
     */
    public List<Field> getColumns() {
        return columns;
    }

    /**
     * @see TableRow#getColumns()
     */
    public void setColumns(List<Field> columns) {
        this.columns = columns;
    }

    /**
     * Returns the field instance that makes up the column with the given index.
     *
     * @param columnIndex index for the column to return
     * @return field instance at that column, or null if column does not exist
     */
    public Field getColumn(int columnIndex) {
        Field column = null;

        if ((this.columns != null) && (this.columns.size() > columnIndex)) {
            column = this.columns.get(columnIndex);
        }

        return column;
    }

    /**
     * Returns the number of columns within the row.
     *
     * @return number of columns
     */
    public int getNumberOfColumns() {
        if (this.columns != null) {
            return this.columns.size();
        }

        return 0;
    }
}
