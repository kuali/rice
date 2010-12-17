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
package org.kuali.rice.kew.routeheader.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;


/**
 * A service providing data access for documents (a.k.a route headers).
 *
 * @see DocumentRouteHeaderValue
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RouteHeaderService {

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId);
    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId, boolean clearCache);
    public Collection<DocumentRouteHeaderValue> getRouteHeaders (Collection<Long> routeHeaderIds);
    public Collection<DocumentRouteHeaderValue> getRouteHeaders (Collection<Long> routeHeaderIds, boolean clearCache);
    public Map<Long,DocumentRouteHeaderValue> getRouteHeadersForActionItems(Collection<ActionItem> actionItems);
    public void lockRouteHeader(Long routeHeaderId, boolean wait);
    public void saveRouteHeader(DocumentRouteHeaderValue routeHeader);
    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader);
    public Long getNextRouteHeaderId();
    public void validateRouteHeader(DocumentRouteHeaderValue routeHeader);
    public Collection findPendingByResponsibilityIds(Set responsibilityIds);
    public Collection findByDocTypeAndAppId(String documentTypeName, String appId);
    
    /**
     * Removes all SearchableAttributeValues associated with the RouteHeader.
     * @param routeHeader
     */
    public void clearRouteHeaderSearchValues(Long routeHeaderId);

    /**
     * Updates the searchable attribute values for the document with the given id to the given values.
     * This method will clear existing search attribute values and replace with the ones given.
     */
    public void updateRouteHeaderSearchValues(Long routeHeaderId, List<SearchableAttributeValue> searchAttributes);
    
    /**
     * Returns the Service Namespace of the {@link DocumentType} for the Document with the given ID.
     */
    public String getServiceNamespaceByDocumentId(Long documentId);

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId);

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue);

    public String getDocumentStatus(Long documentId);

    public String getAppDocId(Long documentId);
    
    /**
     *
     * This method is a more direct way to get the searchable attribute values
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<String> getSearchableAttributeStringValuesByKey(Long documentId, String key);
    /**
     *
     * This method is a more direct way to get the searchable attribute values
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(Long documentId, String key);
    /**
     *
     * This method is a more direct way to get the searchable attribute values
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<BigDecimal> getSearchableAttributeFloatValuesByKey(Long documentId, String key);
    /**
     *
     * This method is a more direct way to get the searchable attribute values
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<Long> getSearchableAttributeLongValuesByKey(Long documentId, String key);

}
