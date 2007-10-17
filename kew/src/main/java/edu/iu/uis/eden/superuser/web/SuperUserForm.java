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
package edu.iu.uis.eden.superuser.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.web.KeyValue;
import edu.iu.uis.eden.web.WorkflowRoutingForm;

/**
 * A Struts ActionForm for the {@link SuperUserAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserForm extends WorkflowRoutingForm {
    
    private static final long serialVersionUID = 982228198266403397L;
    private Long routeHeaderId;
    private String docHandlerUrl;
    private Integer routeLevel;
    private Set futureNodeNames = new HashSet();
    private String destNodeName;
    private String returnDestNodeName;
    private String action;
    private List actionRequests = new ArrayList();
    private String actionTakenActionRequestId;
    private String actionTakenNetworkId;
    private String actionTakenWorkGroupId;
    private String actionTakenRecipientCode;
    private boolean authorized;
    private boolean blanketApprove;
    private String methodToCall = "";
    
    private String lookupableImplServiceName;
    private String lookupType;

    private DocumentRouteHeaderValue routeHeader;
    
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    
    public boolean isBlanketApprove() {
        return blanketApprove;
    }

    public void setBlanketApprove(boolean blanketApprove) {
        this.blanketApprove = blanketApprove;
    }

    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public String getDocHandlerUrl() {
        return docHandlerUrl;
    }

    public void setDocHandlerUrl(String docHandlerUrl) {
        this.docHandlerUrl = docHandlerUrl;
    }

    public String getRouteHeaderIdString() {
        return routeHeaderId.toString();
    }

    public Integer getRouteLevel() {
        return routeLevel;
    }

    public void setRouteLevel(Integer routeLevel) {
        this.routeLevel = routeLevel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List getActionRequests() {
        return actionRequests;
    }

    public void setActionRequests(List actionRequests) {
        this.actionRequests = actionRequests;
    }

    public String getActionTakenActionRequestId() {
        return actionTakenActionRequestId;
    }

    public void setActionTakenActionRequestId(String actionTakenActionRequestId) {
        this.actionTakenActionRequestId = actionTakenActionRequestId;
    }

    public String getActionTakenNetworkId() {
        return actionTakenNetworkId;
    }

    public void setActionTakenNetworkId(String actionTakenNetworkId) {
        this.actionTakenNetworkId = actionTakenNetworkId;
    }

    public String getActionTakenWorkGroupId() {
        return actionTakenWorkGroupId;
    }

    public void setActionTakenWorkGroupId(String actionTakenWorkGroupId) {
        this.actionTakenWorkGroupId = actionTakenWorkGroupId;
    }

    /*public List getRouteLevels() {
        return routeLevels;
    }

    public void setRouteLevels(List routeLevels) {
        this.routeLevels = routeLevels;
    }*/

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
    
    public void reset(ActionMapping mapping, HttpServletRequest request){
        this.futureNodeNames = new HashSet();
    }

    public String getActionTakenRecipientCode() {
        return actionTakenRecipientCode;
    }

    public void setActionTakenRecipientCode(String actionTakenRecipientCode) {
        this.actionTakenRecipientCode = actionTakenRecipientCode;
    }
    
    public boolean isSUDocument() {
	if (routeHeader.isStateInitiated() || routeHeader.isStateSaved()) {
            return false;
        }
        return true;
    }
    
    public boolean isStateAllowsAction() {
        if ( routeHeader.isApproved() || routeHeader.isProcessed() || routeHeader.isDisaproved() ) {
            return false;
        }
        return true;
    }
    
    public DocumentType getDocumentType() {
        return getRouteHeader().getDocumentType();
    }
    
    public Set getPreviousNodes() throws Exception {
    	String[] nodeNames = new WorkflowInfo().getPreviousRouteNodeNames(routeHeader.getRouteHeaderId());
        Set previousNodes = new HashSet();
        for (int i = 0; i < nodeNames.length; i++) {
			String nodeName = nodeNames[i];
			previousNodes.add(new KeyValue(nodeName, nodeName));
		}
        return previousNodes;
    }
    
    public String getDestNodeName() {
        return destNodeName;
    }
    public void setDestNodeName(String previousNodeName) {
        this.destNodeName = previousNodeName;
    }
    public Set getFutureNodeNames() {
        return futureNodeNames;
    }
    public void setFutureNodeNames(Set futureNodeNames) {
        this.futureNodeNames = futureNodeNames;
    }
    public String getReturnDestNodeName() {
        return returnDestNodeName;
    }
    public void setReturnDestNodeName(String returnDestNodeName) {
        this.returnDestNodeName = returnDestNodeName;
    }
    
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public String getLookupType() {
        return lookupType;
    }
    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }
    

}