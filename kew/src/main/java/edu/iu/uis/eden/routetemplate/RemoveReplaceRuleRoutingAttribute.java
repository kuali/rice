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
package edu.iu.uis.eden.routetemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * An attribute for handling routing of RemoveReplace documents according to the Document Types of the
 * rules that are being changed.  Leverages the RuleRoutingAttribute and adds some additional logic
 * to verify that at least one rule exists for each Document Type which is represented in the rule
 * changeset.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoveReplaceRuleRoutingAttribute extends RuleRoutingAttribute implements MassRuleAttribute {

    private static final long serialVersionUID = 6377571664060038747L;

    private static final String RULE_XPATH = "//rules/rule/documentType";

    /**
     * Overrides the parseDocContent from the super class to handle the different XML on a Remove/Replace User Document.
     *
     * <p>This method returns a List of RuleRoutingAttributes which store the document type name found in the rule XML.
     *
     * @see edu.iu.uis.eden.routetemplate.RuleRoutingAttribute#parseDocContent(edu.iu.uis.eden.routeheader.DocumentContent)
     */
    @Override
    public List<RuleRoutingAttribute> parseDocContent(DocumentContent docContent) {
	List<RuleRoutingAttribute> ruleRoutingAttributes = new ArrayList<RuleRoutingAttribute>();
	Set<RuleDocumentType> changedRuleDocTypes = calculateChangedRuleDocumentTypes(docContent);
	for (RuleDocumentType ruleDocumentType : changedRuleDocTypes) {
	    ruleRoutingAttributes.add(new RuleRoutingAttribute(ruleDocumentType.getDocumentTypeName()));
	}
	return ruleRoutingAttributes;
    }

    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
	Set<RuleDocumentType> changedRuleDocTypes = calculateChangedRuleDocumentTypes(routeContext.getDocumentContent());

	for (RuleBaseValues rule : (List<RuleBaseValues>)rules) {
	    if (changedRuleDocTypes.isEmpty()) {
		break;
	    }
	    String ruleDocumentType = getRuleDocumentTypeFromRuleExtensions(rule.getRuleExtensions());
	    for (Iterator<RuleDocumentType> iterator = changedRuleDocTypes.iterator(); iterator.hasNext();) {
		RuleDocumentType ruleDocType = iterator.next();
		if (ruleDocType.isDocTypeSatisfied(ruleDocumentType)) {
		    iterator.remove();
		}
	    }
	}

	if (!changedRuleDocTypes.isEmpty()) {
	    String message = "No rules found for document types: ";
	    int index = 0;
	    for (RuleDocumentType docType : changedRuleDocTypes) {
		message += docType.getDocumentTypeName();
		index++;
		if (index < changedRuleDocTypes.size()) {
		    message += ", ";
		}
	    }
	    throw new WorkflowRuntimeException(message);
	}

	return rules;
    }

    protected Set<RuleDocumentType> calculateChangedRuleDocumentTypes(DocumentContent documentContent) {
	try {
	    Set<RuleDocumentType> docTypes = new HashSet<RuleDocumentType>();
	    Document document = documentContent.getDocument();
	    XPath xpath = XPathHelper.newXPath(document);
	    NodeList ruleDocTypeNodes = (NodeList)xpath.evaluate(RULE_XPATH, document, XPathConstants.NODESET);
	    for (int index = 0; index < ruleDocTypeNodes.getLength(); index++) {
		Element docTypeNode = (Element)ruleDocTypeNodes.item(index);
		String docTypeName = docTypeNode.getTextContent();
		docTypes.add(new RuleDocumentType(docTypeName));
	    }
	    return docTypes;
	} catch (XPathExpressionException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    private class RuleDocumentType {
	private String documentTypeName;
	private Set<String> documentTypeNames = new HashSet<String>();
	public RuleDocumentType(String documentTypeName) {
	    this.documentTypeName = documentTypeName;
	    DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
	    addNameAndClimb(documentType);
	}
	private void addNameAndClimb(DocumentType documentType) {
	    if (documentType == null) {
		return;
	    }
	    documentTypeNames.add(documentType.getName());
	    addNameAndClimb(documentType.getParentDocType());
	}
	public boolean isDocTypeSatisfied(String documentTypeNameToCheck) {
	    return documentTypeNames.contains(documentTypeNameToCheck);
	}
	public String getDocumentTypeName() {
	    return documentTypeName;
	}
	@Override
	public boolean equals(Object obj) {
	    return documentTypeName.equals(obj);
	}
	@Override
	public int hashCode() {
	    return documentTypeName.hashCode();
	}

    }

}
