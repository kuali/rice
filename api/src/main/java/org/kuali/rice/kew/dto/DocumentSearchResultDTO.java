/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a virtual object representing the DocumentSearchResultComponents class
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchResultDTO implements Serializable {

    private static final long serialVersionUID = -2555393598328802683L;

    private List<DocumentSearchResultRowDTO> searchResults = new ArrayList<DocumentSearchResultRowDTO>();
    private boolean isOverThreshold = false;
    private Integer securityFilteredRows = Integer.valueOf(0);

    public List<DocumentSearchResultRowDTO> getSearchResults() {
        return this.searchResults;
    }

    public void setSearchResults(List<DocumentSearchResultRowDTO> searchResults) {
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
