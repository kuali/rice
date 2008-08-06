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
package org.kuali.rice.kew.docsearch;

import java.util.List;

import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;


/**
 * Service for data access for document searches.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentSearchService {

    /**
     * This method performs a standard document search
     * 
     * @param user - user executing the search
     * @param criteria - criteria to use to search documents
     * @return a {@link DocumentSearchResultComponents} object holding the search result columns and search result rows
     *         represented by a list of {@link DocumentSearchResult} objects
     * @throws KEWUserNotFoundException
     */
    public DocumentSearchResultComponents getList(WorkflowUser user, DocSearchCriteriaVO criteria) throws KEWUserNotFoundException;

    /**
     * This method performs a standard document search but uses the value returned by
     * {@link DocSearchCriteriaVO#getThreshold()} as the maximum search results returned
     * 
     * @param user - user executing the search
     * @param criteria - criteria to use to search documents
     * @return a {@link DocumentSearchResultComponents} object holding the search result columns and search result rows
     *         represented by a list of {@link DocumentSearchResult} objects
     * @throws KEWUserNotFoundException
     */
    public DocumentSearchResultComponents getListRestrictedByCriteria(WorkflowUser user, DocSearchCriteriaVO criteria) throws KEWUserNotFoundException;
    public SavedSearchResult getSavedSearchResults(WorkflowUser user, String savedSearchName) throws KEWUserNotFoundException;
    public void clearNamedSearches(WorkflowUser user);
    public List getNamedSearches(WorkflowUser user);
    public List getMostRecentSearches(WorkflowUser user);
    
    public DocumentSearchGenerator getStandardDocumentSearchGenerator();
    public DocumentSearchResultProcessor getStandardDocumentSearchResultProcessor();

}
