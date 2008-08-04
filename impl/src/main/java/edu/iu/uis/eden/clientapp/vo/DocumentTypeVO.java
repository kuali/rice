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
package edu.iu.uis.eden.clientapp.vo;


/**
 * Transport object for document type.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class DocumentTypeVO implements java.io.Serializable {
    static final long serialVersionUID = 5266872451859573823L;

    private Long docTypeParentId;
    private String docTypeParentName;
    private boolean docTypeActiveInd;
    private boolean docTypeActiveInherited;
    private String docTypeDescription;
    private String docTypeHandlerUrl;
    private Long docTypeId;
    private String docTypeLabel;
    private String name;
    private Integer docTypeVersion = new Integer(0);
    private String postProcessorName;
    private boolean docTypeDefaultApprovePolicy;
    private boolean docTypeDefaultApprovePolicyInherited;
    private boolean docTypePreApprovalPolicy;
    private boolean docTypePreApprovalPolicyInherited;
    private String docTypeJndiFactoryClass;
    private String docTypeJndiUrl;
    private String docTypeCurrentInd;
    private Long blanketApproveWorkgroupId;
    private String blanketApprovePolicy;
    public RouteTemplateEntryVO[] routeTemplates;
    private boolean routeTemplateInherited;
    private RoutePathVO routePath;
    
    public DocumentTypeVO() {
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

    public Long getDocTypeParentId() {
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

    public Long getDocTypeId() {
        return docTypeId;
    }

    /**
     * @deprecated Use getDocTypeLabel instead
     * @return
     */
    public String getDocTypeName() {
        return docTypeLabel;
    }

    public String getName() {
        return name;
    }

    /**
     * @deprectated No longer needed or used
     * @return
     */
    public Integer getDocTypeVersion() {
        return docTypeVersion;
    }

    public String getPostProcessorName() {
        return postProcessorName;
    }

    public RouteTemplateEntryVO[] getRouteTemplates() {
        return routeTemplates;
    }

    public void setRouteTemplates(RouteTemplateEntryVO[] routeTemplates) {
        this.routeTemplates = routeTemplates;
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

    /**
     * @deprecated userSetDocTypeLabel instead
     * @param docTypeName
     */
    public void setDocTypeName(String docTypeName) {
        this.docTypeLabel = docTypeName;
    }

    public void setDocTypeId(Long docTypeId) {
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

    public void setDocTypeParentId(Long docGrpId) {
        this.docTypeParentId = docGrpId;
    }

//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }

    public void setPostProcessorName(String postProcessorName) {
        this.postProcessorName = postProcessorName;
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

    public boolean isDocTypePreApprovalPolicy() {
        return docTypePreApprovalPolicy;
    }

    public void setDocTypePreApprovalPolicy(boolean docTypePreApprovalPolicy) {
        this.docTypePreApprovalPolicy = docTypePreApprovalPolicy;
    }

    public boolean isDocTypePreApprovalPolicyInherited() {
        return docTypePreApprovalPolicyInherited;
    }

    public void setDocTypePreApprovalPolicyInherited(
            boolean docTypePreApprovalPolicyInherited) {
        this.docTypePreApprovalPolicyInherited = docTypePreApprovalPolicyInherited;
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

    public boolean isRouteTemplateInherited() {
        return routeTemplateInherited;
    }

    public void setRouteTemplateInherited(boolean routeTemplateInherited) {
        this.routeTemplateInherited = routeTemplateInherited;
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
            DocumentTypeVO docTypeVO = (DocumentTypeVO) object;

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

	public Long getBlanketApproveWorkgroupId() {
        return blanketApproveWorkgroupId;
    }

    public void setBlanketApproveWorkgroupId(Long blanketApproveWorkgroupId) {
        this.blanketApproveWorkgroupId = blanketApproveWorkgroupId;
    }
    public String getDocTypeParentName() {
        return docTypeParentName;
    }
    public void setDocTypeParentName(String docTypeParentName) {
        this.docTypeParentName = docTypeParentName;
    }

    public RoutePathVO getRoutePath() {
        return routePath;
    }

    public void setRoutePath(RoutePathVO routePath) {
        this.routePath = routePath;
    }
}