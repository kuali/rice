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
package edu.iu.uis.eden.routeheader;

import java.util.Collection;
import java.util.Set;

import edu.iu.uis.eden.doctype.DocumentType;

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
     * Returns the Message Entity of the {@link DocumentType} for the Document with the given ID.
     */
    public String getMessageEntityByDocumentId(Long documentId);

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId);

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue);

    public String getDocumentStatus(Long documentId);
}
