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
package edu.iu.uis.eden.doctype.web;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.web.WorkflowRoutingForm;

/**
 * Struts form for {@link DocumentTypeAction}
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeForm extends WorkflowRoutingForm {
	private static final long serialVersionUID = 6490421152680911514L;
    private DocumentType documentType;
    private String exportedXml; // used for report screen XML output 
    private DocumentType existingDocumentType;
    private String methodToCall = "";
    private String command;
    private Integer routeLevelIndex;
    private Integer searchableAttributeIndex;
    private String lookupType;
    private String parentDocTypeName;
    private String annotation;
    private boolean docTypeVisible;
    private boolean policyVisible;
    private boolean routeLevelVisible;
    private boolean searchableAttributeVisible;
    private String visibleSelected;
    private RuleAttribute searchableAttribute;
    private Boolean editingRouteLevelInd;
    private Long docTypeId;
    private List routeModules;
    private Integer moveRouteLevel;
    private int routeLevelSize;
    private int searchableAttributeSize;
    private String routeModuleName;
    private String newRouteModuleName;
    private String ruleTemplate;
    private String newRouteModuleVisible;
    
    private String defaultApprove;
    private String preApprove;
    private String initiatorMustRoute;
    private String initiatorMustSave;
    
    private String lookupableImplServiceName;

    public DocumentTypeForm() {
        documentType = new DocumentType();
        searchableAttribute = new RuleAttribute();
        editingRouteLevelInd = new Boolean(false);
    }
    
    public String getDefaultApprove() {
        return defaultApprove;
    }

    public void setDefaultApprove(String defaultApprove) {
        this.defaultApprove = defaultApprove;
    }

    public String getPreApprove() {
        return preApprove;
    }

    public void setPreApprove(String preApprove) {
        this.preApprove = preApprove;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getExportedXml() {
        return exportedXml;
    }
    
    public void setExportedXml(String exportedXml) {
        this.exportedXml = exportedXml;
    }

    public DocumentType getExistingDocumentType() {
        return existingDocumentType;
    }

    public void setExistingDocumentType(DocumentType existingDocumentType) {
        this.existingDocumentType = existingDocumentType;
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Integer getRouteLevelIndex() {
        return routeLevelIndex;
    }

    public void setRouteLevelIndex(Integer routeLevelIndex) {
        this.routeLevelIndex = routeLevelIndex;
    }

    /**
	 * @param searchableAttributeIndex The searchableAttributeIndex to set.
	 */
	public void setSearchableAttributeIndex(Integer searchableAttributeIndex) {
		this.searchableAttributeIndex = searchableAttributeIndex;
	}

	/**
	 * @return Returns the searchableAttributeIndex.
	 */
	public Integer getSearchableAttributeIndex() {
		return searchableAttributeIndex;
	}

	/**
     * set variables on form from lookup return value
     */
    public void setDocTypeFullName(String docTypeFullName) {
        if (docTypeFullName != null && !docTypeFullName.equals("")) {
            this.setParentDocTypeName(docTypeFullName);

            this.documentType.setDocTypeParentId(getDocumentTypeService().findByName(docTypeFullName).getDocumentTypeId());
            this.documentType.getPreApprovePolicy().setPolicyValue(null);
            this.documentType.getDefaultApprovePolicy().setPolicyValue(null);
            this.documentType.getInitiatorMustRoutePolicy().setPolicyValue(null);
            this.documentType.getInitiatorMustSavePolicy().setPolicyValue(null);

            defaultApprove = EdenConstants.INHERITED_CD;
            preApprove = EdenConstants.INHERITED_CD;
            initiatorMustRoute = EdenConstants.INHERITED_CD;
            initiatorMustSave = EdenConstants.INHERITED_CD;

            documentType.setRouteLevels(new ArrayList());

            //documentType.setSearchableAttributesInherited(new Boolean(true));
            setSearchableAttribute(new RuleAttribute());
            documentType.setDocumentTypeAttributes(new ArrayList());
        } else {
            documentType.setRouteLevels(new ArrayList());
            documentType.setDocumentTypeAttributes(new ArrayList());
            //DocumentTypeAction.clearParent(this);
        }
        setEditingRouteLevelInd(new Boolean(false));
    }

//    public void setSuperUserWorkgroupId(String workgroupId) throws Exception {
//        if (workgroupId != null && !workgroupId.equals("")) {
//            this.documentType.setWorkgroupId(new Long(workgroupId));
//        } else {
//            this.documentType.setWorkgroupId(null);
//        }
//    }
//
//    public void setBlanketApproveWorkgroupId(String workgroupId) throws Exception {
//        if (workgroupId != null && !workgroupId.equals("")) {
//            this.documentType.setBlanketApproveWorkgroupId(new Long(workgroupId));
//        } else {
//            this.documentType.setBlanketApproveWorkgroupId(null);
//        }
//    }

    private DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }

    public String getParentDocTypeName() {
        return parentDocTypeName;
    }

    public void setParentDocTypeName(String parentDocTypeName) {
        this.parentDocTypeName = parentDocTypeName;
    }

    public boolean isDocTypeVisible() {
        return docTypeVisible;
    }

    public void setDocTypeVisible(boolean docTypeVisible) {
        this.docTypeVisible = docTypeVisible;
    }

    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public boolean isPolicyVisible() {
        return policyVisible;
    }

    public void setPolicyVisible(boolean policyVisible) {
        this.policyVisible = policyVisible;
    }

    public boolean isRouteLevelVisible() {
        return routeLevelVisible;
    }

    public void setRouteLevelVisible(boolean routeLevelVisible) {
        this.routeLevelVisible = routeLevelVisible;
    }

    public boolean isSearchableAttributeVisible() {
        return searchableAttributeVisible;
    }

    public void setSearchableAttributeVisible(boolean searchableAttributeVisible) {
        this.searchableAttributeVisible = searchableAttributeVisible;
    }

    public String getVisibleSelected() {
        return visibleSelected;
    }

    public void setVisibleSelected(String visibleSelected) {
        this.visibleSelected = visibleSelected;
    }

    public Integer getMoveRouteLevel() {
        return moveRouteLevel;
    }

    public void setMoveRouteLevel(Integer moveRouteLevel) {
        this.moveRouteLevel = moveRouteLevel;
    }

    public int getRouteLevelSize() {
        return routeLevelSize;
    }

    public void setRouteLevelSize(int routeLevelSize) {
        this.routeLevelSize = routeLevelSize;
    }

    /**
	 * @param searchableAttributeSize The searchableAttributeSize to set.
	 */
	public void setSearchableAttributeSize(int searchableAttributeSize) {
		this.searchableAttributeSize = searchableAttributeSize;
	}

	/**
	 * @return Returns the searchableAttributeSize.
	 */
	public int getSearchableAttributeSize() {
		return searchableAttributeSize;
	}

	public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    /**
	 * @param searchableAttribute The searchableAttribute to set.
	 */
	public void setSearchableAttribute(RuleAttribute searchableAttribute) {
		this.searchableAttribute = searchableAttribute;
	}

	/**
	 * @return Returns the searchableAttribute.
	 */
	public RuleAttribute getSearchableAttribute() {
		return searchableAttribute;
	}

	public Boolean getEditingRouteLevelInd() {
        return editingRouteLevelInd;
    }

    public void setEditingRouteLevelInd(Boolean editingRouteLevelInd) {
        this.editingRouteLevelInd = editingRouteLevelInd;
    }

    public String getCommand() {
        return command;
    }
 
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getSearchLink(){
        return "Lookup.do?lookupableImplServiceName=DocumentTypeLookupableImplService";
    }
    
    public String getSearchLinkText(){
        return "Document Type Search";
    }
    public Long getDocTypeId() {
        return docTypeId;
    }
    public void setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
    }
    public String getInitiatorMustRoute() {
        return initiatorMustRoute;
    }
    public void setInitiatorMustRoute(String intiatorMustRoute) {
        this.initiatorMustRoute = intiatorMustRoute;
    }
    public String getInitiatorMustSave() {
        return initiatorMustSave;
    }
    public void setInitiatorMustSave(String intiatorMustSave) {
        this.initiatorMustSave = intiatorMustSave;
    }

    public List getRouteModules() {
        return routeModules;
    }

    public void setRouteModules(List routeModules) {
        this.routeModules = routeModules;
    }

    public String getRouteModuleName() {
        return routeModuleName;
    }

    public void setRouteModuleName(String routeModuleName) {
        this.routeModuleName = routeModuleName;
    }

    public String getNewRouteModuleName() {
        return newRouteModuleName;
    }

    public void setNewRouteModuleName(String newRouteModuleName) {
        this.newRouteModuleName = newRouteModuleName;
    }

    public String getNewRouteModuleVisible() {
        return newRouteModuleVisible;
    }

    public void setNewRouteModuleVisible(String newRouteModuleVisible) {
        this.newRouteModuleVisible = newRouteModuleVisible;
    }

    public String getRuleTemplate() {
        return ruleTemplate;
    }

    public void setRuleTemplate(String ruleTemplate) {
        this.ruleTemplate = ruleTemplate;
    }
}