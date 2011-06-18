/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

import java.io.Serializable;

/**
 * Transport object for document type.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypeDTO implements Serializable {
    static final long serialVersionUID = 5266872451859573823L;

    private String docTypeParentId;
    private String docTypeParentName;
    private boolean docTypeActiveInd;
    private boolean docTypeActiveInherited;
    private String docTypeDescription;
    // this value is the resolved and potentially inherited value
    private String docTypeHandlerUrl;
    // this value is the resolved and potentially inherited value
    private String helpDefinitionUrl;
    private String docSearchHelpUrl;
    private String docTypeId;
    private String docTypeLabel;
    private String name;
    private Integer docTypeVersion = new Integer(0);
    private String postProcessorName;
    private boolean docTypeDefaultApprovePolicy;
    private boolean docTypeDefaultApprovePolicyInherited;
    private String docTypeJndiFactoryClass;
    private String docTypeJndiUrl;
    private String docTypeCurrentInd;
    private String blanketApproveGroupId;
    private String blanketApprovePolicy;
    private RoutePathDTO routePath;
    
    public DocumentTypeDTO() {
    }

    /**
     * True if the documents for this group should default to approve status if
     * no action requests are generated for the document.
     * 
     * @return true if the documents should be auto-approved if no action
     *         requests are generated
     */
    public boolean isDefaultApprovePolicy() {
        return docTypeDefaultApprovePolicy;
    }

    public String getDocTypeParentId() {
        return docTypeParentId;
    }

    public boolean isDocTypeActiveInd() {
        return docTypeActiveInd;
    }

    public boolean isDocTypeActiveInherited() {
        return docTypeActiveInherited;
    }

    public String getDocTypeDescription() {
        return docTypeDescription;
    }

    public String getDocTypeHandlerUrl() {
        return docTypeHandlerUrl;
    }

    public String getDocTypeId() {
        return docTypeId;
    }

    public String getName() {
        return name;
    }

    /**
     * @deprecated No longer needed or used
     * @return
     */
    public Integer getDocTypeVersion() {
        return docTypeVersion;
    }

    public String getPostProcessorName() {
        return postProcessorName;
    }

    /**
     * @deprectated No longer needed or used
     * @param docTypeVersion
     */
    public void setDocTypeVersion(Integer docTypeVersion) {
        this.docTypeVersion = docTypeVersion;
    }

    public void setName(String docTypeShortname) {
        this.name = docTypeShortname;
    }

    public void setDocTypeId(String docTypeId) {
        this.docTypeId = docTypeId;
    }

    public void setDocTypeHandlerUrl(String docTypeHandlerUrl) {
        this.docTypeHandlerUrl = docTypeHandlerUrl;
    }

    public void setDocTypeDescription(String docTypeDescription) {
        this.docTypeDescription = docTypeDescription;
    }

    public void setDocTypeActiveInherited(boolean docTypeActiveInherited) {
        this.docTypeActiveInherited = docTypeActiveInherited;
    }

    public void setDocTypeActiveInd(boolean docTypeActiveInd) {
        this.docTypeActiveInd = docTypeActiveInd;
    }

    public void setDocTypeParentId(String docGrpId) {
        this.docTypeParentId = docGrpId;
    }

    public void setPostProcessorName(String postProcessorName) {
        this.postProcessorName = postProcessorName;
    }

    public String getHelpDefinitionUrl() {
        return this.helpDefinitionUrl;
    }

    public void setHelpDefinitionUrl(String helpDefinitionUrl) {
        this.helpDefinitionUrl = helpDefinitionUrl;
    }
    
    /**
     * @return the docSearchHelpUrl
     */
    public String getDocSearchHelpUrl() {
        return this.docSearchHelpUrl;
    }
    
    /**
     * @param docSearchHelpUrl the docSearchHelpUrl to set
     */
    public void setDocSearchHelpUrl(String docSearchHelpUrl) {
        this.docSearchHelpUrl = docSearchHelpUrl;
    }

    public String getDocTypeLabel() {
        return docTypeLabel;
    }

    public void setDocTypeLabel(String docTypeLabel) {
        this.docTypeLabel = docTypeLabel;
    }

    public boolean getDocTypeDefaultApprovePolicy() {
        return docTypeDefaultApprovePolicy;
    }

    public void setDocTypeDefaultApprovePolicy(
            boolean docTypeDefaultApprovePolicy) {
        this.docTypeDefaultApprovePolicy = docTypeDefaultApprovePolicy;
    }

    public boolean isDocTypeDefaultApprovePolicyInherited() {
        return docTypeDefaultApprovePolicyInherited;
    }

    public void setDocTypeDefaultApprovePolicyInherited(
            boolean docTypeDefaultApprovePolicyInherited) {
        this.docTypeDefaultApprovePolicyInherited = docTypeDefaultApprovePolicyInherited;
    }

    public String getDocTypeJndiFactoryClass() {
        return docTypeJndiFactoryClass;
    }

    public String getDocTypeJndiUrl() {
        return docTypeJndiUrl;
    }

    public void setDocTypeJndiFactoryClass(String docTypeJndiFactoryClass) {
        this.docTypeJndiFactoryClass = docTypeJndiFactoryClass;
    }

    public void setDocTypeJndiUrl(String docTypeJndiUrl) {
        this.docTypeJndiUrl = docTypeJndiUrl;
    }
    
    public String getDocTypeCurrentInd() {
        return docTypeCurrentInd;
    }

    public void setDocTypeCurrentInd(String docTypeCurrentInd) {
        this.docTypeCurrentInd = docTypeCurrentInd;
    }

    public boolean equals(Object object) {
        /* just compare the doctype id for now */
        try {
            DocumentTypeDTO docTypeVO = (DocumentTypeDTO) object;

            return this.docTypeId.equals(docTypeVO.getDocTypeId());
        } catch (Exception ex) {
            return false;
        }
    }

    public String getBlanketApprovePolicy() {
		return blanketApprovePolicy;
	}

	public void setBlanketApprovePolicy(String blanketApprovePolicy) {
		this.blanketApprovePolicy = blanketApprovePolicy;
	}

	public String getBlanketApproveGroupId() {
        return blanketApproveGroupId;
    }

    public void setBlanketApproveGroupId(String blanketApproveGroupId) {
        this.blanketApproveGroupId = blanketApproveGroupId;
    }
    public String getDocTypeParentName() {
        return docTypeParentName;
    }
    public void setDocTypeParentName(String docTypeParentName) {
        this.docTypeParentName = docTypeParentName;
    }

    public RoutePathDTO getRoutePath() {
        return routePath;
    }

    public void setRoutePath(RoutePathDTO routePath) {
        this.routePath = routePath;
    }
}
