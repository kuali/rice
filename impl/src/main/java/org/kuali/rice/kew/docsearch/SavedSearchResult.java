/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;

import java.io.Serializable;

/**
 * Bean representing a saved search result in from a document search.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SavedSearchResult implements Serializable {
	
	private static final long serialVersionUID = -764778230573234367L;

	private final DocumentLookupCriteria criteria;
	private DocumentSearchResultComponents searchResult;

    public SavedSearchResult(DocumentLookupCriteria criteria, DocumentSearchResultComponents searchResult) {
        this.criteria = criteria;
        this.searchResult = searchResult;
    }

    public DocumentLookupCriteria getCriteria() {
        return criteria;
    }

	public DocumentSearchResultComponents getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(DocumentSearchResultComponents searchResult) {
		this.searchResult = searchResult;
	}
}
