/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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

/**
 * Transport object representing a RouteNode.
 */
public class RouteNodeDTO implements java.io.Serializable, Cloneable {
    
    private static final long serialVersionUID = 6547166752590755847L;
    
    private Long routeNodeId;
    private String documentTypeId;
    private String routeNodeName;
    private String routeMethodName;
    private String routeMethodCode;
    private boolean finalApprovalInd;
    private boolean mandatoryRouteInd;
    private String activationType;
    private String exceptionGroupId;
    private String nodeType;
    private String branchName;
    private Long[] previousNodeIds = new Long[0];
    private Long[] nextNodeIds = new Long[0];
    
    public RouteNodeDTO() {}
    
    public String getActivationType() {
        return activationType;
    }
    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }
    public String getBranchName() {
        return branchName;
    }
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    public String getDocumentTypeId() {
        return documentTypeId;
    }
    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }
    public String getExceptionGroupId() {
        return exceptionGroupId;
    }
    public void setExceptionGroupId(String exceptionGroupId) {
        this.exceptionGroupId = exceptionGroupId;
    }
    public boolean isFinalApprovalInd() {
        return finalApprovalInd;
    }
    public void setFinalApprovalInd(boolean finalApprovalInd) {
        this.finalApprovalInd = finalApprovalInd;
    }
    public boolean isMandatoryRouteInd() {
        return mandatoryRouteInd;
    }
    public void setMandatoryRouteInd(boolean mandatoryRouteInd) {
        this.mandatoryRouteInd = mandatoryRouteInd;
    }
    public Long[] getNextNodeIds() {
        return nextNodeIds;
    }
    public void setNextNodeIds(Long[] nextNodeIds) {
        this.nextNodeIds = nextNodeIds;
    }
    public String getNodeType() {
        return nodeType;
    }
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
    public Long[] getPreviousNodeIds() {
        return previousNodeIds;
    }
    public void setPreviousNodeIds(Long[] previousNodeIds) {
        this.previousNodeIds = previousNodeIds;
    }
    public String getRouteMethodCode() {
        return routeMethodCode;
    }
    public void setRouteMethodCode(String routeMethodCode) {
        this.routeMethodCode = routeMethodCode;
    }
    public String getRouteMethodName() {
        return routeMethodName;
    }
    public void setRouteMethodName(String routeMethodName) {
        this.routeMethodName = routeMethodName;
    }
    public Long getRouteNodeId() {
        return routeNodeId;
    }
    public void setRouteNodeId(Long routeNodeId) {
        this.routeNodeId = routeNodeId;
    }
    public String getRouteNodeName() {
        return routeNodeName;
    }
    public void setRouteNodeName(String routeNodeName) {
        this.routeNodeName = routeNodeName;
    }

}
