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
package org.kuali.rice.kew.impl.document.search;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.krad.bo.BusinessObject;

import java.sql.Timestamp;

/**
 * Defines the business object that specifies the criteria used on document searches.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCriteriaBo implements BusinessObject {

    private String documentTypeName;
    private String documentId;
    private String statusCode;
    private String applicationDocumentId;
    private String applicationDocumentStatus;
    private String title;
    private String initiatorPrincipalName;
    private String initiatorPrincipalId;
    private String viewerPrincipalName;
    private String viewerPrincipalId;
    private String groupViewerName;
    private String groupViewerId;
    private String approverPrincipalName;
    private String approverPrincipalId;
    private String routeNodeName;
    private String routeNodeLogic;
    private Timestamp dateCreated;
    private Timestamp dateLastModified;
    private Timestamp dateApproved;
    private Timestamp dateFinalized;
    private Timestamp dateApplicationDocumentStatusChanged;
    private String saveName;

    @Override
    public void refresh() {
        // nothing to refresh
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getApplicationDocumentId() {
        return applicationDocumentId;
    }

    public void setApplicationDocumentId(String applicationDocumentId) {
        this.applicationDocumentId = applicationDocumentId;
    }

    public String getApplicationDocumentStatus() {
        return applicationDocumentStatus;
    }

    public void setApplicationDocumentStatus(String applicationDocumentStatus) {
        this.applicationDocumentStatus = applicationDocumentStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitiatorPrincipalName() {
        return initiatorPrincipalName;
    }

    public void setInitiatorPrincipalName(String initiatorPrincipalName) {
        this.initiatorPrincipalName = initiatorPrincipalName;
    }

    public String getInitiatorPrincipalId() {
        return initiatorPrincipalId;
    }

    public void setInitiatorPrincipalId(String initiatorPrincipalId) {
        this.initiatorPrincipalId = initiatorPrincipalId;
    }

    public String getViewerPrincipalName() {
        return viewerPrincipalName;
    }

    public void setViewerPrincipalName(String viewerPrincipalName) {
        this.viewerPrincipalName = viewerPrincipalName;
    }

    public String getViewerPrincipalId() {
        return viewerPrincipalId;
    }

    public void setViewerPrincipalId(String viewerPrincipalId) {
        this.viewerPrincipalId = viewerPrincipalId;
    }

    public String getGroupViewerName() {
        return groupViewerName;
    }

    public void setGroupViewerName(String groupViewerName) {
        this.groupViewerName = groupViewerName;
    }

    public String getGroupViewerId() {
        return groupViewerId;
    }

    public void setGroupViewerId(String groupViewerId) {
        this.groupViewerId = groupViewerId;
    }

    public String getApproverPrincipalName() {
        return approverPrincipalName;
    }

    public void setApproverPrincipalName(String approverPrincipalName) {
        this.approverPrincipalName = approverPrincipalName;
    }

    public String getApproverPrincipalId() {
        return approverPrincipalId;
    }

    public void setApproverPrincipalId(String approverPrincipalId) {
        this.approverPrincipalId = approverPrincipalId;
    }

    public String getRouteNodeName() {
        return routeNodeName;
    }

    public void setRouteNodeName(String routeNodeName) {
        this.routeNodeName = routeNodeName;
    }

    public String getRouteNodeLogic() {
        return routeNodeLogic;
    }

    public void setRouteNodeLogic(String routeNodeLogic) {
        this.routeNodeLogic = routeNodeLogic;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Timestamp dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public Timestamp getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(Timestamp dateApproved) {
        this.dateApproved = dateApproved;
    }

    public Timestamp getDateFinalized() {
        return dateFinalized;
    }

    public void setDateFinalized(Timestamp dateFinalized) {
        this.dateFinalized = dateFinalized;
    }

    public Timestamp getDateApplicationDocumentStatusChanged() {
        return dateApplicationDocumentStatusChanged;
    }

    public void setDateApplicationDocumentStatusChanged(Timestamp dateApplicationDocumentStatusChanged) {
        this.dateApplicationDocumentStatusChanged = dateApplicationDocumentStatusChanged;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public DocumentType getDocumentType() {
        if (documentTypeName == null) {
            return null;
        }
        return KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    }

    public Person getInitiatorPerson() {
        if (initiatorPrincipalId == null) {
            return null;
        }
        return KimApiServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
    }

    public String getInitiatorDisplayName() {
        if (initiatorPrincipalId != null) {
            EntityNamePrincipalName entityNamePrincipalName = KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalId(initiatorPrincipalId);
            if (entityNamePrincipalName != null){
                EntityName entityName = entityNamePrincipalName.getDefaultName();
                return entityName == null ? null : entityName.getCompositeName();
            }
        }
        return null;
    }

    public Person getApproverPerson() {
        if (approverPrincipalName == null) {
            return null;
        }
        return KimApiServiceLocator.getPersonService().getPersonByPrincipalName(approverPrincipalName);
    }

    public Person getViewerPerson() {
        if (viewerPrincipalName == null) {
            return null;
        }
        return KimApiServiceLocator.getPersonService().getPersonByPrincipalName(viewerPrincipalName);
    }

    public GroupBo getGroupViewer() {
        if (groupViewerId == null) {
            return null;
        }
        Group grp = KimApiServiceLocator.getGroupService().getGroup(groupViewerId);
        if (null != grp){
            return GroupBo.from(grp);
        }  else {
            return null;
        }
    }

    public String getStatusLabel() {
        if (statusCode == null) {
            return "";
        }
        return DocumentStatus.fromCode(statusCode).getLabel();
    }

    public String getDocumentTypeLabel() {
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            return documentType.getLabel();
        }
        return "";
    }

    /**
     * Returns the route image which can be used to construct the route log link in custom lookup helper code.
     */
    public String getRouteLog() {
        return "<img alt=\"Route Log for Document\" src=\"images/my_route_log.gif\"/>";
    }

    public void populateFromDocumentSearchResult(DocumentSearchResult result) {
        Document document = result.getDocument();
        documentTypeName = document.getDocumentTypeName();
        documentId = document.getDocumentId();
        statusCode = document.getStatus().getCode();
        applicationDocumentId = document.getApplicationDocumentId();
        applicationDocumentStatus = document.getApplicationDocumentStatus();
        title = document.getTitle();
        initiatorPrincipalName = principalIdToName(document.getInitiatorPrincipalId());
        initiatorPrincipalId = document.getInitiatorPrincipalId();
        dateCreated = new Timestamp(document.getDateCreated().getMillis());
    }

    private String principalIdToName(String principalId) {
        if (StringUtils.isNotBlank(principalId)) {
            Principal principal =  KimApiServiceLocator.getIdentityService().getPrincipal(principalId);
            if (principal != null){
                return principal.getPrincipalName();
            }
        }
        return null;
    }

}

