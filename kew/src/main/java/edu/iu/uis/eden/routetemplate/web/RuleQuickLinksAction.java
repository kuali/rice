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
package edu.iu.uis.eden.routetemplate.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for building and interacting with the Rule Quick Links.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleQuickLinksAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	makeLookupPathParam(mapping, request);
        return mapping.findForward("basic");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        RuleQuickLinksForm qlForm = (RuleQuickLinksForm) form;
        List documentTypes;
        if (qlForm.getRootDocTypeName() != null) {
        	documentTypes = new ArrayList();
            DocumentType docType = getDocumentTypeService().findByName(qlForm.getRootDocTypeName());
            documentTypes.add(docType);
            request.setAttribute("renderOpened", Boolean.TRUE);
        } else {
        	documentTypes = getDocumentTypeService().findAllCurrentRootDocuments();
        }
        qlForm.setDocumentTypeQuickLinksStructures(getDocumentTypeDataStructure(documentTypes));

        return null;
    }

    public ActionForward addDelegationRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long ruleTemplateId = new Long(request.getParameter("ruleTemplate.ruleTemplateId"));
        String docTypeName = request.getParameter("docTypeFullName");
        List rules = getRuleService().search(docTypeName, null, ruleTemplateId, "", null, null, "", Boolean.FALSE, Boolean.TRUE, new HashMap(), null);
        if (rules.size() == 1) {
            RuleBaseValues rule = (RuleBaseValues)rules.get(0);
            RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateId);
            String url = "DelegateRule.do?methodToCall=start" +
        		"&parentRule.getDocTypeName=" + docTypeName +
        		"&ruleCreationValues.ruleTemplateId=" + ruleTemplate.getDelegationTemplateId() +
        		"&ruleCreationValues.ruleTemplateName=" + ruleTemplate.getDelegationTemplate().getName() +
        		"&ruleCreationValues.ruleId=" + rule.getRuleBaseValuesId();
            return new ActionForward(url, true);
        }
        makeLookupPathParam(mapping, request);
        return new ActionForward("Lookup.do?"+stripMethodToCall(request.getQueryString()), true);
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

        private DocumentTypeQuickLinksStructure(DocumentType documentType) {
             this.documentType = documentType;
             List tempFlattendNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
            for (Iterator iter = tempFlattendNodes.iterator(); iter.hasNext();) {
				RouteNode routeNode = (RouteNode) iter.next();
				if (routeNode.isFlexRM()) {
					flattenedNodes.add(routeNode);
				}
			}
            for (Iterator iter = documentType.getChildrenDocTypes().iterator(); iter.hasNext();) {
                childrenDocumentTypes.add(new DocumentTypeQuickLinksStructure((DocumentType) iter.next()));
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
        public boolean isShouldDisplay () {
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