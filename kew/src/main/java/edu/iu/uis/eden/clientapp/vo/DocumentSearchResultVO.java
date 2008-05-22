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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.docsearch.DocumentSearchResultComponents;

/**
 * This is a virtual object representing the {@link DocumentSearchResultComponents} class
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchResultVO implements Serializable {

    private static final long serialVersionUID = -2555393598328802683L;

    private List<LookupableColumnVO> columns;
    private List<DocumentSearchResultRowVO> searchResults = new ArrayList<DocumentSearchResultRowVO>();
    private boolean isOverThreshold = false;
    private Integer securityFilteredRows = Integer.valueOf(0);

    public List<LookupableColumnVO> getColumns() {
        return this.columns;
    }

    public void setColumns(List<LookupableColumnVO> columns) {
        this.columns = columns;
    }

    public List<DocumentSearchResultRowVO> getSearchResults() {
        return this.searchResults;
    }

    public void setSearchResults(List<DocumentSearchResultRowVO> searchResults) {
        this.searchResults = searchResults;
    }

    public boolean isOverThreshold() {
        return this.isOverThreshold;
    }

    public void setOverThreshold(boolean isOverThreshold) {
        this.isOverThreshold = isOverThreshold;
    }

    public Integer getSecurityFilteredRows() {
        return this.securityFilteredRows;
    }

    public void setSecurityFilteredRows(Integer securityFilteredRows) {
        this.securityFilteredRows = securityFilteredRows;
    }

}
