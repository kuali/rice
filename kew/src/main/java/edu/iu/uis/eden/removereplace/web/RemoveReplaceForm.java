/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.removereplace.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.kuali.workflow.workgroup.WorkgroupType;

import edu.iu.uis.eden.removereplace.RemoveReplaceDocument;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.ShowHideTree;
import edu.iu.uis.eden.web.WorkflowRoutingForm;

/**
 * A struts form for Remove/Replace.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoveReplaceForm extends WorkflowRoutingForm {

    private String methodToCall = "";
    private String lookupableImplServiceName;
    private String conversionFields;
    private Map actionRequestCodes;
    private String searchLink = "";
    private String searchLinkText = "";

    // input fields
    private String userId;
    private String replacementUserId;
    private String ruleDocumentTypeName;
    private String ruleRuleTemplate;
    private String workgroupType;

    private String operation = RemoveReplaceDocument.REPLACE_OPERATION;
    private List<RemoveReplaceRule> rules = ListUtils.lazyList(new ArrayList<RemoveReplaceRule>(),
	    new Factory() {
	     	public Object create() {
	     	    return new RemoveReplaceRule();
	     	}
	     });
    private List<RemoveReplaceWorkgroup> workgroups = ListUtils.lazyList(new ArrayList<RemoveReplaceWorkgroup>(),
	    new Factory() {
	     	public Object create() {
	     	    return new RemoveReplaceWorkgroup();
	     	}
	     });

    // properties that are loaded by establishRequiredState
    private WorkflowUser user;
    private WorkflowUser replacementUser;
    private List<WorkgroupType> workgroupTypes = new ArrayList<WorkgroupType>();

    private RemoveReplaceDocument document;

    private boolean operationSelected = false;
    private ShowHideTree showHide = new ShowHideTree();
    private boolean report = false;

    public RemoveReplaceForm() {
    }

    public static WorkgroupType createDefaultWorkgroupType() {
    	WorkgroupType workgroupType = new WorkgroupType();
    	workgroupType.setActive(true);
    	workgroupType.setLabel("Default");
    	workgroupType.setName("Default");
    	return workgroupType;
    }

    public String getMethodToCall() {
        return this.methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public String getLookupableImplServiceName() {
        return this.lookupableImplServiceName;
    }

    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public String getRuleDocumentTypeName() {
        return this.ruleDocumentTypeName;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setRuleDocumentTypeName(String ruleDocumentType) {
        this.ruleDocumentTypeName = ruleDocumentType;
    }

    public String getRuleRuleTemplate() {
        return this.ruleRuleTemplate;
    }

    public void setRuleRuleTemplate(String ruleRuleTemplate) {
        this.ruleRuleTemplate = ruleRuleTemplate;
    }

    public List<RemoveReplaceRule> getRules() {
        return this.rules;
    }

    public void setRules(List<RemoveReplaceRule> rules) {
        this.rules = rules;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReplacementUserId() {
        return this.replacementUserId;
    }

    public void setReplacementUserId(String userIdToReplace) {
        this.replacementUserId = userIdToReplace;
    }

    public WorkflowUser getUser() {
        return this.user;
    }

    public void setUser(WorkflowUser user) {
        this.user = user;
    }

    public WorkflowUser getReplacementUser() {
        return this.replacementUser;
    }

    public void setReplacementUser(WorkflowUser replacementUser) {
        this.replacementUser = replacementUser;
    }

    public List<RemoveReplaceWorkgroup> getWorkgroups() {
        return this.workgroups;
    }

    public void setWorkgroups(List<RemoveReplaceWorkgroup> workgroups) {
        this.workgroups = workgroups;
    }

    public Map getActionRequestCodes() {
        return this.actionRequestCodes;
    }

    public void setActionRequestCodes(Map actionRequestCodes) {
        this.actionRequestCodes = actionRequestCodes;
    }

    public String getWorkgroupType() {
        return this.workgroupType;
    }

    public void setWorkgroupType(String workgroupType) {
        this.workgroupType = workgroupType;
    }

    public List<WorkgroupType> getWorkgroupTypes() {
        return this.workgroupTypes;
    }

    public void setWorkgroupTypes(List<WorkgroupType> workgroupTypes) {
        this.workgroupTypes = workgroupTypes;
    }

    public RemoveReplaceDocument getDocument() {
        return this.document;
    }

    public void setDocument(RemoveReplaceDocument document) {
        this.document = document;
    }

    public String getOperationDisplayName() {
	if (getOperation().equals(RemoveReplaceDocument.REMOVE_OPERATION)) {
	    return "Remove";
	} else if (getOperation().equals(RemoveReplaceDocument.REPLACE_OPERATION)) {
	    return "Replace";
	}
	return "";
    }

    public boolean isReplace() {
	return getOperation().equals(RemoveReplaceDocument.REPLACE_OPERATION);
    }

    public boolean isRemove() {
	return getOperation().equals(RemoveReplaceDocument.REMOVE_OPERATION);
    }

    public boolean isOperationSelected() {
        return this.operationSelected;
    }

    public void setOperationSelected(boolean operationSelected) {
        this.operationSelected = operationSelected;
    }

    public String getConversionFields() {
        return this.conversionFields;
    }

    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }

    public ShowHideTree getShowHide() {
        return this.showHide;
    }

    public void setShowHide(ShowHideTree showHide) {
        this.showHide = showHide;
    }

    public boolean isReport() {
        return this.report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public String getSearchLink() {
        return this.searchLink;
    }

    public void setSearchLink(String searchLink) {
        this.searchLink = searchLink;
    }

    public String getSearchLinkText() {
        return this.searchLinkText;
    }

    public void setSearchLinkText(String searchLink) {
        this.searchLinkText = searchLink;
    }

}
