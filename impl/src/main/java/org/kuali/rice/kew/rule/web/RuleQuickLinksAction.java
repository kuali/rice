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
package org.kuali.rice.kew.rule.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.BranchPrototype;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeConfigParam;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * A Struts Action for building and interacting with the Rule Quick Links.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleQuickLinksAction extends KewKualiAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	makeLookupPathParam(mapping, request);
    	establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    @SuppressWarnings("unchecked")
	public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        RuleQuickLinksForm qlForm = (RuleQuickLinksForm) form;
        List<DocumentType> documentTypes;
        if (qlForm.getRootDocTypeName() != null) {
        	documentTypes = new ArrayList<DocumentType>();
            DocumentType docType = getDocumentTypeService().findByName(qlForm.getRootDocTypeName());
            documentTypes.add(docType);
            request.setAttribute("renderOpened", Boolean.TRUE);
        } else {
        	documentTypes = getDocumentTypeService().findAllCurrentRootDocuments();
        	if ( documentTypes.size() == 1 ) {
                request.setAttribute("renderOpened", Boolean.TRUE);
        	}
        }
        qlForm.setDocumentTypeQuickLinksStructures(getDocumentTypeDataStructure(documentTypes));

        return null;
    }

    public ActionForward addDelegationRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long ruleTemplateId = new Long(request.getParameter("delegationRuleBaseValues.ruleTemplate.ruleTemplateId"));
        String docTypeName = request.getParameter("delegationRuleBaseValues.documentType.name");
        List rules = getRuleService().search(docTypeName, null, ruleTemplateId, "", null, null, Boolean.FALSE, Boolean.TRUE, new HashMap(), null);
        if (rules.size() == 1) {
            RuleBaseValues rule = (RuleBaseValues)rules.get(0);
            String url = "../kew/DelegateRule.do?methodToCall=start" +
            	"&parentRuleId=" + rule.getRuleBaseValuesId();
            return new ActionForward(url, true);
        }
        makeLookupPathParam(mapping, request);
        return new ActionForward("../kr/lookup.do?methodToCall=start&"+ stripMethodToCall(request.getQueryString()), true);
    }

	private List getDocumentTypeDataStructure(List rootDocuments) {
		List documentTypeQuickLinksStructures = new ArrayList();
		for (Iterator iter = rootDocuments.iterator(); iter.hasNext();) {
			DocumentTypeQuickLinksStructure quickLinkStruct =new DocumentTypeQuickLinksStructure((DocumentType) iter.next());
			if (! quickLinkStruct.getFlattenedNodes().isEmpty() || ! quickLinkStruct.getChildrenDocumentTypes().isEmpty()) {
				documentTypeQuickLinksStructures.add(quickLinkStruct);
			}

		}

		return documentTypeQuickLinksStructures;
	}


	/**
     * A bean to hold a DocumentType with its flattened nodes for rendering purposes
     * on the quick links.
     *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    public static class DocumentTypeQuickLinksStructure {
        private DocumentType documentType;
        private List<RouteNode> flattenedNodes = new ArrayList<RouteNode>();
        private List<DocumentTypeQuickLinksStructure> childrenDocumentTypes = new ArrayList<DocumentTypeQuickLinksStructure>();
        private List<KimPermissionInfo> permissions = null;
        
        private DocumentTypeQuickLinksStructure(DocumentType documentType) {
			this.documentType = documentType;
			if ( documentType != null ) {
				List tempFlattenedNodes = KEWServiceLocator.getRouteNodeService()
						.getFlattenedNodes( documentType, true );
				for ( Iterator iter = tempFlattenedNodes.iterator(); iter.hasNext(); ) {
					RouteNode routeNode = (RouteNode)iter.next();
					if ( routeNode.isFlexRM() || routeNode.isRoleNode() ) {
						flattenedNodes.add( new RouteNodeForDisplay( routeNode ) );
					}
				}
				for ( Iterator iter = documentType.getChildrenDocTypes().iterator(); iter.hasNext(); ) {
					childrenDocumentTypes.add( new DocumentTypeQuickLinksStructure(
							(DocumentType)iter.next() ) );
				}
			}
		}

        public List getChildrenDocumentTypes() {
            return childrenDocumentTypes;
        }
        public void setChildrenDocumentTypes(List<DocumentTypeQuickLinksStructure> childrenDocumentTypes) {
            this.childrenDocumentTypes = childrenDocumentTypes;
        }
        public DocumentType getDocumentType() {
            return documentType;
        }
        public void setDocumentType(DocumentType documentType) {
            this.documentType = documentType;
        }
        public List getFlattenedNodes() {
            return flattenedNodes;
        }
        public void setFlattenedNodes(List<RouteNode> flattenedNodes) {
            this.flattenedNodes = flattenedNodes;
        }
        public boolean isShouldDisplay() {
			if (flattenedNodes.isEmpty()) {
				for (DocumentTypeQuickLinksStructure docType : childrenDocumentTypes) {
					if (docType.isShouldDisplay()) {
						return true;
					}
				}
				return false;
			}
			return true;
		}

		public List<KimPermissionInfo> getPermissions() {
			if ( permissions == null ) {
				Map<String,String> searchCriteria = new HashMap<String,String>();
				searchCriteria.put("detailCriteria",
						KimAttributes.DOCUMENT_TYPE_NAME+"="+getDocumentType().getName()
						);
				permissions = KIMServiceLocator.getPermissionService().lookupPermissions( searchCriteria, false );
			}
			return permissions;
		}
		
		public boolean isHasRelatedPermissions() {
			return !getPermissions().isEmpty();
		}
		
		public int getRelatedPermissionCount() {
			return getPermissions().size();
		}
    }

    public static class RouteNodeForDisplay extends RouteNode {
    	private RouteNode baseNode;
    	
		public RouteNodeForDisplay( RouteNode baseNode ) {
			this.baseNode = baseNode;
		}

		public boolean equals(Object obj) {
			return this.baseNode.equals(obj);
		}

		public String getActivationType() {
			return this.baseNode.getActivationType();
		}

		public BranchPrototype getBranch() {
			return this.baseNode.getBranch();
		}

		public List<RouteNodeConfigParam> getConfigParams() {
			return this.baseNode.getConfigParams();
		}

		public String getContentFragment() {
			return this.baseNode.getContentFragment();
		}

		public DocumentType getDocumentType() {
			return this.baseNode.getDocumentType();
		}

		public Long getDocumentTypeId() {
			return this.baseNode.getDocumentTypeId();
		}
		public KimGroup getExceptionWorkgroup() {
			return this.baseNode.getExceptionWorkgroup();
		}
		public String getExceptionWorkgroupId() {
			return this.baseNode.getExceptionWorkgroupId();
		}
		public String getExceptionWorkgroupName() {
			return this.baseNode.getExceptionWorkgroupName();
		}
		public Boolean getFinalApprovalInd() {
			return this.baseNode.getFinalApprovalInd();
		}
		public Integer getLockVerNbr() {
			return this.baseNode.getLockVerNbr();
		}
		public Boolean getMandatoryRouteInd() {
			return this.baseNode.getMandatoryRouteInd();
		}
		public List<RouteNode> getNextNodes() {
			return this.baseNode.getNextNodes();
		}
		public String getNodeType() {
			return this.baseNode.getNodeType();
		}
		public List<RouteNode> getPreviousNodes() {
			return this.baseNode.getPreviousNodes();
		}
		public String getRouteMethodCode() {
			return this.baseNode.getRouteMethodCode();
		}
		public String getRouteMethodName() {
			return this.baseNode.getRouteMethodName();
		}
		public Long getRouteNodeId() {
			return this.baseNode.getRouteNodeId();
		}
		public String getRouteNodeName() {
			return this.baseNode.getRouteNodeName();
		}
		public RuleTemplate getRuleTemplate() {
			return this.baseNode.getRuleTemplate();
		}
		public int hashCode() {
			return this.baseNode.hashCode();
		}
		public boolean isExceptionGroupDefined() {
			return this.baseNode.isExceptionGroupDefined();
		}
		public boolean isFlexRM() {
			return this.baseNode.isFlexRM();
		}
		public boolean isRoleNode() {
			return this.baseNode.isRoleNode();
		}
		public String toString() {
			return this.baseNode.toString();
		}
		
		private List<? extends KimResponsibilityInfo> responsibilities = null;
		
		public List<? extends KimResponsibilityInfo> getResponsibilities() {
			if ( responsibilities == null ) {
				Map<String,String> searchCriteria = new HashMap<String,String>();
				searchCriteria.put("template.namespaceCode", KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE);
				searchCriteria.put("template.name", "Review");
				searchCriteria.put("detailCriteria",
						KimAttributes.DOCUMENT_TYPE_NAME+"="+getDocumentType().getName()
						+ ","
						+ KimAttributes.ROUTE_NODE_NAME+"="+getRouteNodeName() );
				responsibilities = KIMServiceLocator.getResponsibilityService().lookupResponsibilityInfo(searchCriteria, true);
			}
			return responsibilities;
		}
		
		public int getResponsibilityCount() {
			return getResponsibilities().size();
		}
		
		public boolean isHasResponsibility() {
			return !getResponsibilities().isEmpty();
		}
    }
    
    private void makeLookupPathParam(ActionMapping mapping, HttpServletRequest request) {
    	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
    	request.setAttribute("basePath", basePath);
    }

    private String stripMethodToCall(String queryString) {
        return queryString.replaceAll("methodToCall=addDelegationRule&", "");
    }

    private DocumentTypeService getDocumentTypeService() {
        return KEWServiceLocator.getDocumentTypeService();
    }

    private RuleService getRuleService() {
    	return KEWServiceLocator.getRuleService();
    }

    private RuleTemplateService getRuleTemplateService() {
    	return KEWServiceLocator.getRuleTemplateService();
    }

}