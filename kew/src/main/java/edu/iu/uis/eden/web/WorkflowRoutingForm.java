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
package edu.iu.uis.eden.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * A Struts ActionForm which can be extended by ActionForms which need to add
 * support for document routing to a Struts Action.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowRoutingForm extends ActionForm {

	private static final long serialVersionUID = -3537002710069757806L;
	private WorkflowDocument flexDoc;
    private Long docId;
    private String docTypeName;
    private String initiateURL;
    private String command;
    private String annotation;

    //private Integer destRouteLevel;
    private boolean showBlanketApproveButton;
    protected Map appSpecificRouteActionRequestCds = new HashMap();
    protected AppSpecificRouteRecipient appSpecificRouteRecipient = new AppSpecificRouteRecipient();
    protected List appSpecificRouteList = new ArrayList();

    protected String appSpecificRouteRecipientType = "person";
    protected String appSpecificRouteActionRequestCd;
    protected Integer recipientIndex;
    protected String docHandlerReturnUrl;
    protected String removedAppSpecificRecipient;

    public void resetAppSpecificRoute(){
        appSpecificRouteRecipient = new AppSpecificRouteRecipient();
    }

    public Map getAppSpecificRouteActionRequestCds() {
        return appSpecificRouteActionRequestCds;
    }

    /**
     * @return Returns the destRouteLevel.
     */
    /*public Integer getDestRouteLevel() {
        return destRouteLevel;
    }*/

    /**
     * @param destRouteLevel The destRouteLevel to set.
     */
    /*public void setDestRouteLevel(Integer destRouteLevel) {
        this.destRouteLevel = destRouteLevel;
    }*/
    /**
     * @return Returns the initiateURL.
     */
    public String getInitiateURL() {
        return initiateURL;
    }
    /**
     * @param initiateURL The initiateURL to set.
     */
    public void setInitiateURL(String initiateURL) {
        this.initiateURL = initiateURL;
    }
    /**
     * @return Returns the command.
     */
    public String getCommand() {
        return command;
    }
    /**
     * @param command The command to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }
    /**
     * @return Returns the annotation.
     */
    public String getAnnotation() {
        return annotation;
    }
    /**
     * @param annotation The annotation to set.
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    /**
     * @return Returns the showBlanketApproveButton.
     */
    public boolean isShowBlanketApproveButton() {
        return showBlanketApproveButton;
    }
    /**
     * @param showBlanketApproveButton The showBlanketApproveButton to set.
     */
    public void setShowBlanketApproveButton(boolean blanketApprove) {
        this.showBlanketApproveButton = blanketApprove;
    }
    /**
     * @return Returns the docId.
     */
    public Long getDocId() {
        return docId;
    }
    /**
     * @param docId The docId to set.
     */
    public void setDocId(Long docId) {
        this.docId = docId;
    }
    /**
     * @return Returns the flexDoc.
     */
    public WorkflowDocument getFlexDoc() {
        return flexDoc;
    }
    /**
     * @param flexDoc The flexDoc to set.
     */
    public void setFlexDoc(WorkflowDocument flexDoc) {
        this.flexDoc = flexDoc;
    }
    /**
     * @return Returns the previousRouteLevels.
     */
    /*public List getPreviousRouteLevels() {
        List previousRouteLevels = new ArrayList();
        if (flexDoc != null && flexDoc.getDocRouteLevel() != null) {
            for (int i = flexDoc.getDocRouteLevel().intValue(); i > 0; --i) {
                previousRouteLevels.add(new KeyValue(Integer.toString(i - 1), Integer.toString(i - 1)));
            }
        }
        return previousRouteLevels;
    }*/
    /**
     * @return Returns the superUserSearch.
     */
    public boolean isSuperUserSearch() {
        return (command != null && command.equals(IDocHandler.SUPERUSER_COMMAND));
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public void setAppSpecificPersonId(String networkId){
        if(networkId != null && !networkId.trim().equals("")){
            getAppSpecificRouteRecipient().setId(networkId);
        }
        getAppSpecificRouteRecipient().setType("person");
    }

    public void setAppSpecificWorkgroupId(Long workgroupId){
        if(workgroupId != null){
            Workgroup workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
            if(workgroup != null){
                getAppSpecificRouteRecipient().setId(workgroup.getGroupNameId().getNameId());
            }
        }
        getAppSpecificRouteRecipient().setType("workgroup");
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

    public AppSpecificRouteRecipient getAppSpecificRouteRecipient() {
        return appSpecificRouteRecipient;
    }
    public void setAppSpecificRouteRecipient(AppSpecificRouteRecipient appSpecificRouteRecipient) {
        this.appSpecificRouteRecipient = appSpecificRouteRecipient;
    }
    public List getAppSpecificRouteList() {
        return appSpecificRouteList;
    }
    public void setAppSpecificRouteList(List appSpecificRouteList) {
        this.appSpecificRouteList = appSpecificRouteList;
    }


    public void setAppSpecificRouteRecipientType(
            String appSpecificRouteRecipientType) {
        this.appSpecificRouteRecipientType = appSpecificRouteRecipientType;
    }
    public String getAppSpecificRouteRecipientType() {
        return appSpecificRouteRecipientType;
    }

    public AppSpecificRouteRecipient getAppSpecificRoute(int index) {
        while (getAppSpecificRouteList().size() <= index) {
            getAppSpecificRouteList().add(new AppSpecificRouteRecipient());
        }
        return (AppSpecificRouteRecipient) getAppSpecificRouteList().get(index);
    }


    public void setAppSpecificRoute(int index, AppSpecificRouteRecipient appSpecificRouteRecipient) {
        appSpecificRouteList.set(index, appSpecificRouteRecipient);
    }


    public String getAppSpecificRouteActionRequestCd() {
        return appSpecificRouteActionRequestCd;
    }
    public void setAppSpecificRouteActionRequestCd(
            String appSpecificRouteActionRequestCd) {
        this.appSpecificRouteActionRequestCd = appSpecificRouteActionRequestCd;
    }
    public Integer getRecipientIndex() {
        return recipientIndex;
    }
    public void setRecipientIndex(Integer recipientIndex) {
        this.recipientIndex = recipientIndex;
    }


    public void establishVisibleActionRequestCds(){
    	try {
	        if(getFlexDoc() != null){
		    	Long docId = flexDoc.getRouteHeaderId();
		    	Workgroup suWorkgroup = KEWServiceLocator.getRouteHeaderService().getRouteHeader(docId).getDocumentType().getSuperUserWorkgroup();
		    	WorkflowUser docUser = KEWServiceLocator.getUserService().getWorkflowUser(flexDoc.getUserId());
		    	boolean isSuperUser = (suWorkgroup == null ? false : suWorkgroup.hasMember(docUser));
		    	if (isSuperUser){
		    		appSpecificRouteActionRequestCds = CodeTranslator.arLabels;
		    	}else if(flexDoc.isFYIRequested()){
	                appSpecificRouteActionRequestCds.clear();
	                appSpecificRouteActionRequestCds.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	            } else if (flexDoc.isAcknowledgeRequested()){
	                appSpecificRouteActionRequestCds.clear();
	                appSpecificRouteActionRequestCds.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
	                appSpecificRouteActionRequestCds.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	            } else if(flexDoc.isApprovalRequested() || flexDoc.isCompletionRequested() || flexDoc.stateIsInitiated()){
	                appSpecificRouteActionRequestCds = CodeTranslator.arLabels;
	            }
	        }
    	} catch (Exception e) {
    		throw new RuntimeException("Caught exception building ad hoc action dropdown", e);
    	}
    }
    public String getDocHandlerReturnUrl() {
        return docHandlerReturnUrl;
    }
    public void setDocHandlerReturnUrl(String docHandlerReturnUrl) {
        this.docHandlerReturnUrl = docHandlerReturnUrl;
    }

    public String getRemovedAppSpecificRecipient() {
        return removedAppSpecificRecipient;
    }
    public void setRemovedAppSpecificRecipient(
            String removedAppSpecificRecipient) {
        this.removedAppSpecificRecipient = removedAppSpecificRecipient;
    }
}
