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
package edu.iu.uis.eden.docsearch;

import java.io.Serializable;

/**
 * Bean representing a saved search result in from a document search.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SavedSearchResult implements Serializable {
	
	private static final long serialVersionUID = -764778230573234367L;
	private DocSearchCriteriaVO docSearchCriteriaVO;
	private DocumentSearchResultComponents searchResult;
    
    /**
     * @deprecated
     */
    public boolean isAdvancedSearch() {
        return docSearchCriteriaVO.isAdvancedSearch();
    }
    public SavedSearchResult(DocSearchCriteriaVO docSearchCriteriaVO, DocumentSearchResultComponents searchResult) {
        this.docSearchCriteriaVO = docSearchCriteriaVO;
        this.searchResult = searchResult;
    }
    public DocSearchCriteriaVO getDocSearchCriteriaVO() {
        return docSearchCriteriaVO;
    }
    public void setDocSearchCriteriaVO(DocSearchCriteriaVO docSearchCriteriaVO) {
        this.docSearchCriteriaVO = docSearchCriteriaVO;
    }
	public DocumentSearchResultComponents getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(DocumentSearchResultComponents searchResult) {
		this.searchResult = searchResult;
	}
}
