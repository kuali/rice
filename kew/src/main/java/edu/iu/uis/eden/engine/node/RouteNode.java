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
package edu.iu.uis.eden.engine.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Represents the prototype definition of a node in the route path of {@link DocumentType}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteNode implements Serializable {    

    private static final long serialVersionUID = 4891233177051752726L;

    public static final String CONTENT_FRAGMENT_CFG_KEY = "contentFragment";
    public static final String RULE_SELECTOR_CFG_KEY = "ruleSelector";

    private Long routeNodeId;
    private Long documentTypeId;
    private String routeNodeName;
    private String routeMethodName;
    private Boolean finalApprovalInd;
    private Boolean mandatoryRouteInd;
    private Long exceptionWorkgroupId;
    private String routeMethodCode;
    private String activationType = ActivationTypeEnum.PARALLEL.getCode();

    private Integer lockVerNbr;
    private DocumentType documentType;
    private String exceptionWorkgroupName;

    private RuleTemplate ruleTemplate;
    private String nodeType = RequestsNode.class.getName();
    private List<RouteNode> previousNodes = new ArrayList<RouteNode>();
    private List<RouteNode> nextNodes = new ArrayList<RouteNode>();
    private BranchPrototype branch;
    private List<RouteNodeConfigParam> configParams  = new ArrayList<RouteNodeConfigParam>(0);

    /**
     * Looks up a config parameter for this route node definition
     * @param key the config param key 
     * @return the RouteNodeConfigParam if present
     */
    protected RouteNodeConfigParam getConfigParam(String key) {
        Map<String, RouteNodeConfigParam> configParamMap = Utilities.getKeyValueCollectionAsLookupTable(configParams);
        return configParamMap.get(key);
    }

    /**
     * Sets a config parameter for this route node definition.  If the key already exists
     * the existing RouteNodeConfigParam is modified, otherwise a new one is created
     * @param key the key of the parameter to set
     * @param value the value to set
     */
    protected void setConfigParam(String key, String value) {
        Map<String, RouteNodeConfigParam> configParamMap = Utilities.getKeyValueCollectionAsLookupTable(configParams);
        RouteNodeConfigParam cfCfgParam = configParamMap.get(key);
        if (cfCfgParam == null) {
            cfCfgParam = new RouteNodeConfigParam(this, key, value);
            configParams.add(cfCfgParam);
        } else {
            cfCfgParam.setValue(value);
        }
    }

    public List<RouteNodeConfigParam> getConfigParams() {
        return configParams;
    }

    public void setConfigParams(List<RouteNodeConfigParam> configParams) {
        this.configParams = configParams;
    }

    /**
     * @return the RouteNodeConfigParam value under the 'contentFragment'  key
     */
    public String getContentFragment() {
        RouteNodeConfigParam cfCfgParam = getConfigParam(CONTENT_FRAGMENT_CFG_KEY);
        if (cfCfgParam == null) return null;
        return cfCfgParam.getValue();
    }

    /**
     * @param contentFragment the content fragment of the node, which will be set as a RouteNodeConfigParam under the 'contentFragment' key
     */
    public void setContentFragment(String contentFragment) {
        setConfigParam(CONTENT_FRAGMENT_CFG_KEY, contentFragment);
    }

    public String getActivationType() {
        return activationType;
    }

    public void setActivationType(String activationType) {
        /* Cleanse the input.
         * This is surely not the best way to validate the activation types;
         * it would probably be better to use typesafe enums accross the board
         * but that would probably entail refactoring large swaths of code, not
         * to mention reconfiguring OJB (can typesafe enums be used?) and dealing
         * with serialization compatibility issues (if any).
         * So instead, let's just be sure to fail-fast.
         */
        ActivationTypeEnum at = ActivationTypeEnum.lookupCode(activationType);
        this.activationType = at.getCode();
    }

    public Workgroup getExceptionWorkgroup() throws EdenUserNotFoundException {
        return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(this.exceptionWorkgroupId));
    }

    public Long getExceptionWorkgroupId() {
        return exceptionWorkgroupId;
    }

    public void setExceptionWorkgroupId(Long workgroupId) {
        this.exceptionWorkgroupId = workgroupId;
    }

    public void setFinalApprovalInd(Boolean finalApprovalInd) {
        this.finalApprovalInd = finalApprovalInd;
    }

    public void setMandatoryRouteInd(Boolean mandatoryRouteInd) {
        this.mandatoryRouteInd = mandatoryRouteInd;
    }

    public String getRouteMethodName() {
        return routeMethodName;
    }

    public void setRouteMethodName(String routeMethodName) {
        this.routeMethodName = routeMethodName;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
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

    public void setRouteNodeName(String routeLevelName) {
        this.routeNodeName = routeLevelName;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getRouteMethodCode() {
        return routeMethodCode;
    }

    public void setRouteMethodCode(String routeMethodCode) {
        this.routeMethodCode = routeMethodCode;
    }

    public String getExceptionWorkgroupName() {
        if (exceptionWorkgroupName == null || exceptionWorkgroupName.equals("")) {
            if (exceptionWorkgroupId != null) {
                return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(exceptionWorkgroupId)).getGroupNameId().getNameId();
            }
        }
        return exceptionWorkgroupName;
    }

    public void setExceptionWorkgroupName(String exceptionWorkgroupName) {
        this.exceptionWorkgroupName = exceptionWorkgroupName;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public boolean isFlexRM() {
        return routeMethodCode != null && routeMethodCode.equals(EdenConstants.ROUTE_LEVEL_FLEX_RM);
    }

    public Boolean getFinalApprovalInd() {
        return finalApprovalInd;
    }

    public Boolean getMandatoryRouteInd() {
        return mandatoryRouteInd;
    }

    public void addNextNode(RouteNode nextNode) {
        getNextNodes().add(nextNode);
        nextNode.getPreviousNodes().add(this);
    }

    public List<RouteNode> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(List<RouteNode> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public List<RouteNode> getPreviousNodes() {
        return previousNodes;
    }

    public void setPreviousNodes(List<RouteNode> parentNodes) {
        this.previousNodes = parentNodes;
    }

    public RuleTemplate getRuleTemplate() {
        if (ruleTemplate == null) {
            RuleTemplateService ruleTemplateService = (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
            ruleTemplate = ruleTemplateService.findByRuleTemplateName(getRouteMethodName());
        }
        return ruleTemplate;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public BranchPrototype getBranch() {
        return branch;
    }

    public void setBranch(BranchPrototype branch) {
        this.branch = branch;
    }

}