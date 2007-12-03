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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * An attribute for handling routing of RemoveReplace documents according to the Document Types of the
 * rules that are being changed.  Leverages the RuleRoutingAttribute and adds some additional logic
 * to verify that at least one rule exists for each Document Type which is represented in the rule
 * changeset.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoveReplaceWorkgroupTypeRoutingAttribute extends WorkgroupTypeRoutingAttribute implements MassRuleAttribute {

    private static final String WORKGROUP_XPATH = "//workgroups/workgroup";

    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
	Set<String> changedWorkgroupTypes = parseWorkgroupTypes(routeContext.getDocumentContent());

	for (RuleBaseValues rule : (List<RuleBaseValues>)rules) {
	    if (changedWorkgroupTypes.isEmpty()) {
		break;
	    }
	    String workgroupType = getWorkgroupTypeFromRuleExtensions(rule.getRuleExtensions());
	    changedWorkgroupTypes.remove(workgroupType);
	}

	if (!changedWorkgroupTypes.isEmpty()) {
	    String message = "No rules found for the following workgroup types: ";
	    int index = 0;
	    for (String workgroupType : changedWorkgroupTypes) {
		message += workgroupType;
		index++;
		if (index < changedWorkgroupTypes.size()) {
		    message += ", ";
		}
	    }
	    throw new WorkflowRuntimeException(message);
	}

	return rules;
    }

    @Override
    protected Set<String> parseWorkgroupTypes(DocumentContent docContent) {
	try {
	    Set<String> workgroupTypes = new HashSet<String>();
	    Document document = docContent.getDocument();
	    XPath xpath = XPathHelper.newXPath(document);
	    NodeList workgroupNodes = (NodeList)xpath.evaluate(WORKGROUP_XPATH, document, XPathConstants.NODESET);
	    for (int index = 0; index < workgroupNodes.getLength(); index++) {
		Element workgroupNode = (Element)workgroupNodes.item(index);
		String workgroupType = XmlHelper.getChildElementText(workgroupNode, XmlConstants.WORKGROUP_TYPE);
		// if there is no workgroup type, let's set it to a default value
		if (StringUtils.isBlank(workgroupType)) {
		    workgroupType = EdenConstants.LEGACY_DEFAULT_WORKGROUP_TYPE;
		}
		workgroupTypes.add(workgroupType);
	    }
	    return workgroupTypes;
	} catch (XPathExpressionException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

}
