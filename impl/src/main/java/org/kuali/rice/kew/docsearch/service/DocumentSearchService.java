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
package org.kuali.rice.kew.docsearch.service;

import org.kuali.rice.kew.docsearch.*;
import org.kuali.rice.core.util.KeyValue;

import java.util.List;


/**
 * Service for data access for document searches.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchService {

    /**
     * This method performs a standard document search
     *
     * @param principalId - user executing the search
     * @param criteria - criteria to use to search documents
     * @return a {@link DocumentSearchResultComponents} object holding the search result columns and search result rows
     *         represented by a list of {@link DocumentSearchResult} objects
     */
    public DocumentSearchResultComponents getList(String principalId, DocSearchCriteriaDTO criteria);

    /**
     * This method performs a standard document search but uses the value returned by
     * {@link DocSearchCriteriaDTO#getThreshold()} as the maximum search results returned
     *
     * @param principalId - user executing the search
     * @param criteria - criteria to use to search documents
     * @return a {@link DocumentSearchResultComponents} object holding the search result columns and search result rows
     *         represented by a list of {@link DocumentSearchResult} objects
     */
    public DocumentSearchResultComponents getListRestrictedByCriteria(String principalId, DocSearchCriteriaDTO criteria);
    public SavedSearchResult getSavedSearchResults(String principalId, String savedSearchName);
    public void clearNamedSearches(String principalId);
    public List<KeyValue> getNamedSearches(String principalId);
    public List<KeyValue> getMostRecentSearches(String principalId);

    public DocumentSearchGenerator getStandardDocumentSearchGenerator();
    public DocumentSearchResultProcessor getStandardDocumentSearchResultProcessor();
    public void validateDocumentSearchCriteria(DocumentSearchGenerator docSearchGenerator,DocSearchCriteriaDTO criteria);
}
