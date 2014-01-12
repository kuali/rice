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
package org.kuali.rice.kew.routeheader.dao;

import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;

import java.util.Collection;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentRouteHeaderDAO {

    /**
     * "Locks" the route header at the datasource level.
     */
    void lockRouteHeader(String documentId);

    DocumentRouteHeaderValue findRouteHeader(String documentId, boolean clearCache);

    Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<String> documentIds);

    Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<String> documentIds, boolean clearCache);

    String getNextDocumentId();

    Collection<String> findPendingByResponsibilityIds(Set<String> responsibilityIds);

    void clearRouteHeaderSearchValues(String documentId);

    Collection<SearchableAttributeValue> findSearchableAttributeValues(String documentId);

    String getApplicationIdByDocumentId(String documentId);

    DocumentRouteHeaderValueContent getContent(String documentId);

    boolean hasSearchableAttributeValue(String documentId, String searchableAttributeKey,
            String searchableAttributeValue);

    String getDocumentStatus(String documentId);

    void save(SearchableAttributeValue searchableAttribute);

    String getAppDocId(String documentId);

    String getAppDocStatus(String documentId);

    Collection findByDocTypeAndAppId(String documentTypeName, String appId);

}
