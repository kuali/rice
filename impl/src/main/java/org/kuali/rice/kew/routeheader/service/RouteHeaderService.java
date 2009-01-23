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
package org.kuali.rice.kew.routeheader.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;


/**
 * A service providing data access for documents (a.k.a route headers).
 *
 * @see DocumentRouteHeaderValue
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RouteHeaderService {

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId);
    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId, boolean clearCache);
    public void lockRouteHeader(Long routeHeaderId, boolean wait);
    public void saveRouteHeader(DocumentRouteHeaderValue routeHeader);
    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader);
    public Long getNextRouteHeaderId();
    public void validateRouteHeader(DocumentRouteHeaderValue routeHeader);
    public Collection findPendingByResponsibilityIds(Set responsibilityIds);

    /**
     * Removes all SearchableAttributeValues associated with the RouteHeader.
     * @param routeHeader
     */
    public void clearRouteHeaderSearchValues(DocumentRouteHeaderValue routeHeader);

    /**
     * Returns the Service Namespace of the {@link DocumentType} for the Document with the given ID.
     */
    public String getServiceNamespaceByDocumentId(Long documentId);

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId);

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue);

    public String getDocumentStatus(Long documentId);

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
