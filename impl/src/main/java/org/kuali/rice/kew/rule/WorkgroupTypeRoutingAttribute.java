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
package org.kuali.rice.kew.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.lookupable.Field;
import org.kuali.rice.kew.lookupable.Row;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KeyLabelPair;
import org.kuali.rice.kew.workgroup.WorkgroupType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * A {@link WorkflowAttribute} which can be used to route documents based on Workgroup Type.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeRoutingAttribute implements WorkflowAttribute {

    private static final long serialVersionUID = -5899909857754037846L;

    private static final String WORKGROUP_TYPE_PROPERTY = "workgroupType";
    private static final String WORKGROUP_TYPE_LABEL = "Workgroup Type";

    private String workgroupType;
    private List rows;
    private boolean required;

    public WorkgroupTypeRoutingAttribute(String workgroupType) {
        this();
        setWorkgroupType(workgroupType);
    }

    public WorkgroupTypeRoutingAttribute() {
        buildRows();
    }

    private void buildRows() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(WORKGROUP_TYPE_LABEL, "", Field.DROPDOWN, false, WORKGROUP_TYPE_PROPERTY, "", getWorkgroupTypeOptions(), null));
        rows.add(new Row(fields));
    }

    private List getWorkgroupTypeOptions() {
	List options = new ArrayList();
	options.add(new KeyLabelPair("", ""));
	options.add(new KeyLabelPair(KEWConstants.LEGACY_DEFAULT_WORKGROUP_TYPE, "Default"));
	List<WorkgroupType> workgroupTypes = KEWServiceLocator.getWorkgroupTypeService().findAll();
	for (WorkgroupType workgroupType : workgroupTypes) {
		options.add(new KeyLabelPair(workgroupType.getName(), workgroupType.getLabel()));
	}
	return options;
}

    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
	setWorkgroupType(getWorkgroupTypeFromRuleExtensions(ruleExtensions));
        Set<String> workgroupTypes = parseWorkgroupTypes(docContent);
        if (workgroupTypes.contains(getWorkgroupType())) {
            return true;
        }
        if (ruleExtensions.isEmpty()) {
            return true;
        }
        return false;
    }

    protected String getWorkgroupTypeFromRuleExtensions(List ruleExtensions) {
	for (Iterator extensionsIterator = ruleExtensions.iterator(); extensionsIterator.hasNext();) {
            RuleExtension extension = (RuleExtension) extensionsIterator.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(getClass().getName())) {
                for (Iterator valuesIterator = extension.getExtensionValues().iterator(); valuesIterator.hasNext();) {
                    RuleExtensionValue extensionValue = (RuleExtensionValue) valuesIterator.next();
                    String key = extensionValue.getKey();
                    String value = extensionValue.getValue();
                    if (key.equals(WORKGROUP_TYPE_PROPERTY)) {
                        return value;
                    }
                }
            }
        }
	return null;
    }

    protected Set<String> parseWorkgroupTypes(DocumentContent docContent) {
	Set<String> workgroupTypes = new HashSet<String>();
	XPath xpath = XPathHelper.newXPath(docContent.getDocument());
	try {
	    NodeList nodes = (NodeList)xpath.evaluate("//workgroupRouting/workgroupType", docContent.getDocument(), XPathConstants.NODESET);
	    for (int index = 0; index < nodes.getLength(); index++) {
		Element workgroupTypeElement = (Element)nodes.item(index);
		workgroupTypes.add(workgroupTypeElement.getTextContent());
	    }
	} catch (XPathExpressionException e) {
	    throw new WorkflowRuntimeException("Failed to run Xpath expression to find workgroup types in XML.", e);
	}
	return workgroupTypes;
    }

    public List getRuleRows() {
        return rows;
    }

    public List getRoutingDataRows() {
        return rows;
    }

    public String getDocContent() {
	return "<workgroupRouting><workgroupType>" + getWorkgroupType() + "</workgroupType></workgroupRouting>";
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();
        RuleExtensionValue extension = new RuleExtensionValue();
        extension.setKey(WORKGROUP_TYPE_PROPERTY);
        extension.setValue(getWorkgroupType());
        extensions.add(extension);
        return extensions;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        String workgroupTypeValue = (String) paramMap.get(WORKGROUP_TYPE_PROPERTY);
        if (isRequired() && StringUtils.isBlank(workgroupTypeValue)) {
            String message = "Workgroup Type was not specified.";
            errors.add(new WorkflowServiceErrorImpl(message, "general.message", message));
        }

        if (!StringUtils.isBlank(workgroupTypeValue) && !workgroupTypeValue.equals(KEWConstants.LEGACY_DEFAULT_WORKGROUP_TYPE)) {
            WorkgroupType workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(workgroupTypeValue);
            if (workgroupType == null) {
        	String message = "Specified workgroup type of " + workgroupTypeValue + " in invalid.";
        	errors.add(new WorkflowServiceErrorImpl(message, "general.message", message));
            }
        }
        setWorkgroupType(workgroupTypeValue);
        return errors;
    }

    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    public String getWorkgroupType() {
        return this.workgroupType;
    }

    public void setWorkgroupType(String workgroupType) {
	if (StringUtils.isBlank(workgroupType)) {
	    workgroupType = KEWConstants.LEGACY_DEFAULT_WORKGROUP_TYPE;
	}
        this.workgroupType = workgroupType;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }
}