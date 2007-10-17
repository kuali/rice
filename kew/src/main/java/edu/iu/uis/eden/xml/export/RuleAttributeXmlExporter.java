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
package edu.iu.uis.eden.xml.export;

import java.io.StringReader;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports {@link RuleAttribute}s to XML.
 * 
 * @see RuleAttribute
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttributeXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private ExportRenderer renderer = new ExportRenderer(RULE_ATTRIBUTE_NAMESPACE);
    
    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getRuleAttributes().isEmpty()) {
            Element rootElement = renderer.renderElement(null, RULE_ATTRIBUTES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, RULE_ATTRIBUTE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getRuleAttributes().iterator(); iterator.hasNext();) {
                RuleAttribute template = (RuleAttribute)iterator.next();
                exportRuleAttribute(rootElement, template);
            }
            return rootElement;
        }
        return null;
    }
    
    private void exportRuleAttribute(Element parent, RuleAttribute ruleAttribute) {
        Element attributeElement = renderer.renderElement(parent, RULE_ATTRIBUTE);
        renderer.renderTextElement(attributeElement, NAME, ruleAttribute.getName());
        renderer.renderTextElement(attributeElement, CLASS_NAME, ruleAttribute.getClassName());
        renderer.renderTextElement(attributeElement, LABEL, ruleAttribute.getLabel());
        renderer.renderTextElement(attributeElement, DESCRIPTION, ruleAttribute.getDescription());
        renderer.renderTextElement(attributeElement, TYPE, ruleAttribute.getType());
        renderer.renderTextElement(attributeElement, MESSAGE_ENTITY, ruleAttribute.getMessageEntity());
        if (!Utilities.isEmpty(ruleAttribute.getXmlConfigData())) {
            try {
                Document configDoc = new SAXBuilder().build(new StringReader(ruleAttribute.getXmlConfigData()));
                XmlHelper.propogateNamespace(configDoc.getRootElement(), RULE_ATTRIBUTE_NAMESPACE);
                attributeElement.addContent(configDoc.getRootElement().detach());
            } catch (Exception e) {
            	LOG.error("Error parsing attribute XML configuration.", e);
                throw new WorkflowRuntimeException("Error parsing attribute XML configuration.");
            }
        }
    }
    
}
