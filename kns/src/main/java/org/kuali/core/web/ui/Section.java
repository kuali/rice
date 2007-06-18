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
 * This class represents a section (tab) in a maintenance document.
 */
public class Section implements java.io.Serializable {
    private static final long serialVersionUID = 390440643941774650L;
    String sectionTitle;
    String errorKey = "";
    int numberOfColumns;
    boolean isCollapsible;
    String extraButtonSource;
    
    Class sectionClass;
    List<Row> rows;
    List<String> containedCollectionNames;

    /**
     * Default constructor, initializes
     */
    public Section() {
        isCollapsible = true;
        containedCollectionNames = new ArrayList();
    }

    /**
     * Constructor which sets section rows
     * 
     * @param rows the rows to be displayed in the section
     */
    public Section(List rows) {
        this.rows = rows;
        isCollapsible = true;
        containedCollectionNames = new ArrayList();
    }


    /**
     * @return Returns the errorKey.
     */
    public String getErrorKey() {
        return errorKey;
    }


    /**
     * @param errorKey The errorKey to set.
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }


    /**
     * @return Returns the rows.
     */
    public List<Row> getRows() {
        return rows;
    }


    /**
     * @param rows The rows to set.
     */
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }


    /**
     * @return Returns the sectionTitle.
     */
    public String getSectionTitle() {
        return sectionTitle;
    }


    /**
     * @param sectionTitle The sectionTitle to set.
     */
    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }


    /**
     * @return Returns the isCollapsible.
     */
    public boolean isCollapsible() {
        return isCollapsible;
    }


    /**
     * @param isCollapsible The isCollapsible to set.
     */
    public void setCollapsible(boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }


    /**
     * @return Returns the sectionClass.
     */
    public Class getSectionClass() {
        return sectionClass;
    }


    /**
     * @param sectionClass The sectionClass to set.
     */
    public void setSectionClass(Class sectionClass) {
        this.sectionClass = sectionClass;
    }


    public int getNumberOfColumns() {
        if (numberOfColumns != 0) {
            return numberOfColumns;
        } else {
            //by default, return 1 if not specified in the maintenance document data dictionary
            return 1;
        }
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }
    

    /**
     * Gets the containedCollectionNames attribute. 
     * @return Returns the containedCollectionNames.
     */
    public List<String> getContainedCollectionNames() {
        return containedCollectionNames;
    }

    /**
     * Sets the containedCollectionNames attribute value.
     * @param containedCollectionNames The containedCollectionNames to set.
     */
    public void setContainedCollectionNames(List<String> containedCollectionNames) {
        this.containedCollectionNames = containedCollectionNames;
    }
    
    /**
     * @return the extraButtonSource
     */
    public String getExtraButtonSource() {
        return extraButtonSource;
    }

    /**
     * @param extraButtonSource the extraButtonSource to set
     */
    public void setExtraButtonSource(String extraButtonSource) {
        this.extraButtonSource = extraButtonSource;
    }

    /**
     * @return Returns the fieldCnt.
     */
    public int getFieldCnt() {
        if (rows != null && !rows.isEmpty()) {
            Row firstRow = rows.get(0);
            List<Field> rowFields = firstRow.getFields();
            Field firstElement = rowFields.get(0);
            //if the field is a container, set the rowFields to its containerRows.
            if (Field.CONTAINER.equals(firstElement.getFieldType())) {
                if (firstElement.getContainerRows().size() > 0) {
                    rowFields = firstElement.getContainerRows().get(0).getFields();
                }
            }
            if (rowFields.size() == 1) {
                int i = 1;
                while (i < rows.size() &&(Field.SUB_SECTION_SEPARATOR.equals(firstElement.getFieldType()) ||
                                          Field.HIDDEN.equals(firstElement.getFieldType()))) {
                    Row aRow = rows.get(i);
                    rowFields = aRow.getFields();
                    firstElement = rowFields.get(0);
                    i++;
                }
            }
            int cnt = 0;
            for (Field element : rowFields ) {
                // all fields except image type have a label and control cell
                if (!Field.IMAGE_SUBMIT.equals(element.getFieldType())) {
                    cnt += 2;
                }
            }
            return cnt;
        }
        else {
            return 0;
        }
    }
}