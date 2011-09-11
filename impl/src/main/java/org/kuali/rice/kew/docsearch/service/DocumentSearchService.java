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

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults;
import org.kuali.rice.kew.impl.document.lookup.DocumentLookupGenerator;
import org.kuali.rice.kew.doctype.bo.DocumentType;

import java.util.List;


/**
 * Service for data access for document searches.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchService {

    /**
     * This method performs a standard document search for the given criteria.
     *
     * @param principalId the id of the principal who is executing the search, this may be null to indicate the
     * search could be executed by an arbitrary user
     * @param criteria criteria to use to search documents
     * @return the results of the search, will never return null
     */
    DocumentLookupResults lookupDocuments(String principalId, DocumentLookupCriteria criteria);

    DocumentLookupCriteria getSavedSearchCriteria(String principalId, String savedSearchName);

    void clearNamedSearches(String principalId);

    List<KeyValue> getNamedSearches(String principalId);

    List<KeyValue> getMostRecentSearches(String principalId);

    DocumentLookupCriteria clearCriteria(DocumentType documentType, DocumentLookupCriteria criteria);

    DocumentLookupGenerator getStandardDocumentSearchGenerator();

    void validateDocumentSearchCriteria(DocumentLookupGenerator docLookupGenerator, DocumentLookupCriteria.Builder criteria);
}
