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

import java.util.List;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Service for data access for document searches.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentSearchService {

    public DocumentSearchResultComponents getList(WorkflowUser user, DocSearchCriteriaVO criteria) throws EdenUserNotFoundException;
    public SavedSearchResult getSavedSearchResults(WorkflowUser user, String savedSearchName) throws EdenUserNotFoundException;
    public void clearNamedSearches(WorkflowUser user);
    public List getNamedSearches(WorkflowUser user);
    public List getMostRecentSearches(WorkflowUser user);
    
    public DocumentSearchGenerator getStandardDocumentSearchGenerator();
    public DocumentSearchResultProcessor getStandardDocumentSearchResultProcessor();

}
