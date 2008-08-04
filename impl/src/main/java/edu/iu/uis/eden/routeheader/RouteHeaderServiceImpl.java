/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.dao.DocumentRouteHeaderDAO;
import edu.iu.uis.eden.user.WorkflowUserId;

public class RouteHeaderServiceImpl implements RouteHeaderService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteHeaderServiceImpl.class);

    private DocumentRouteHeaderDAO routeHeaderDAO;

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId) {
        return getRouteHeaderDAO().findRouteHeader(routeHeaderId);
    }

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId, boolean clearCache) {
    	return getRouteHeaderDAO().findRouteHeader(routeHeaderId, clearCache);
    }

    public void lockRouteHeader(Long routeHeaderId, boolean wait) {
        getRouteHeaderDAO().lockRouteHeader(routeHeaderId, wait);
        LOG.debug("Successfully locked document [docId=" + routeHeaderId + "]");
    }

    public void saveRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.getRouteHeaderDAO().saveRouteHeader(routeHeader);
    }

    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader) {
        getRouteHeaderDAO().deleteRouteHeader(routeHeader);
    }

    public Long getNextRouteHeaderId() {
        return getRouteHeaderDAO().getNextRouteHeaderId();
    }

    public Collection findPendingByResponsibilityIds(Set responsibilityIds) {
        return getRouteHeaderDAO().findPendingByResponsibilityIds(responsibilityIds);
    }

    public void clearRouteHeaderSearchValues(DocumentRouteHeaderValue routeHeader) {
        getRouteHeaderDAO().clearRouteHeaderSearchValues(routeHeader);
    }

    public void validateRouteHeader(DocumentRouteHeaderValue routeHeader){
        LOG.debug("Enter validateRouteHeader(..)");
        List errors = new ArrayList();

        if (routeHeader.getDocRouteStatus() == null || routeHeader.getDocRouteStatus().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status null.", "routeheader.routestatus.empty"));
        } else if (!EdenConstants.DOCUMENT_STATUSES.containsKey(routeHeader.getDocRouteStatus())){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status invalid.", "routeheader.routestatus.invalid"));
        }

        if(routeHeader.getDocRouteLevel() == null || routeHeader.getDocRouteLevel().intValue() < 0){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route level invalid.", "routeheader.routelevel.invalid"));
        }

        if(routeHeader.getStatusModDate() == null){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader status modification date empty.", "routeheader.statusmoddate.empty"));
        }

        if(routeHeader.getCreateDate() == null){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader status create date empty.", "routeheader.createdate.empty"));
        }
        if(routeHeader.getDocVersion() == null || routeHeader.getDocVersion().intValue() < 0){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader doc version invalid.", "routeheader.docversion.invalid"));
        }

        if (routeHeader.getInitiatorWorkflowId () == null || routeHeader.getInitiatorWorkflowId().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("RouteHeader initiator null.", "routeheader.initiator.empty"));
        } else {
            try {
                KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(routeHeader.getInitiatorWorkflowId()));
            } catch(EdenUserNotFoundException e){
                errors.add(new WorkflowServiceErrorImpl("RouteHeader initiator id invalid.", "routeheader.initiator.invalid"));
            }
        }

        if(routeHeader.getDocumentTypeId() != null && routeHeader.getDocumentTypeId().intValue() != 0){
            DocumentType docType = KEWServiceLocator.getDocumentTypeService().findById(routeHeader.getDocumentTypeId());
            if(docType == null){
                errors.add(new WorkflowServiceErrorImpl("RouteHeader document type id invalid.", "routeheader.doctypeid.invalid"));
            }
        }

        LOG.debug("Exit validateRouteHeader(..) ");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RouteHeader Validation Error", errors);
        }
    }

    public String getMessageEntityByDocumentId(Long documentId) {
    	return getRouteHeaderDAO().getMessageEntityByDocumentId(documentId);
    }

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId) {
    	if (routeHeaderId == null) {
    		return new DocumentRouteHeaderValueContent();
    	}
    	DocumentRouteHeaderValueContent content = getRouteHeaderDAO().getContent(routeHeaderId);
    	if (content == null) {
    		content = new DocumentRouteHeaderValueContent(routeHeaderId);
    	}
    	return content;
    }

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue) {
	return getRouteHeaderDAO().hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue);
    }

    public String getDocumentStatus(Long documentId) {
	return getRouteHeaderDAO().getDocumentStatus(documentId);
    }

    public DocumentRouteHeaderDAO getRouteHeaderDAO() {
        return routeHeaderDAO;
    }

    public void setRouteHeaderDAO(DocumentRouteHeaderDAO routeHeaderDAO) {
        this.routeHeaderDAO = routeHeaderDAO;
    }
}