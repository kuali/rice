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
package edu.iu.uis.eden.routetemplate.xmlrouting;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttributeXmlValidator;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * A generic WorkflowAttribute implementation that can be defined completely by XML.
 * <ol>
 *   <li>This attribute implementation takes "properties" defined on the the {@link edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO}
 *       and maps them to the param map of {@link GenericXMLRuleAttribute}, which relate directly to a set of fields defined by the
 *       XML <code>&lt;routingConfig&gt;</code> configuration.</li>
 *   <li>Application of the properties defined on the WorkflowAttributeDefinition
 *       to the actual attribute is performed in  {@link org.kuali.rice.resourceloader.ObjectDefinitionResolver#invokeProperties(Object, java.util.Collection)}</li>
 *   <li>These params are then used to perform one of either EITHER:
 *     <ul>
 *       <li>Replace parameters of the syntax <code>%<i>field name</i>%</code> in the doc content if doc content is
 *           defined in the <code>&lt;xmlDocumentContent&gt;</code></li>
 *       <li>Generate a generic doc content, containing the parameter key/value pairs, e.g.:
 *           <blockquote>
 *           <code><pre>
 *             &lt;xmlrouting&gt;
 *               &lt;field name="color"&gt;&lt;value&gt;red&lt;/value&gt;&lt;/field&gt;
 *               &lt;field name="shape"&gt;&lt;value&gt;circle&lt;/value&gt;&lt;/field&gt;
 *             &lt;/xmlrouting&gt;
 *           </pre></code>
 *           </blockquote>
 *       </li>
 *     </ul>
 *     Currently, only parameters that match fields configured in the routingConfig are honored (the others are ignored)
 *     (NOTE: to make this even more reusable we might want to consider generating content for all parameters, even those that
 *      do not have corresponding fields)
 *   </li>
 *   <li>The routingConfig element defines a set of <code>fieldDef</code>s, each of which may have an <code>xpathexpression</code> for field evaluation.
 *       This <code>xpathexpression</code> is used to determine whether the attribute's {@link #isMatch(DocumentContent, List)} will
 *       succeed.  Each fieldDef may also have a <code>validation</code> element which supplies a regular expression against which
 *       to validate the field value (given by the param map)</li>
 * </ol>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandardGenericXMLRuleAttribute implements GenericXMLRuleAttribute, WorkflowAttributeXmlValidator {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardGenericXMLRuleAttribute.class);

    private static final String FIELD_DEF_E = "fieldDef";

    private static NodeList getFields(XPath xpath, Element root, String[] types) throws XPathExpressionException {
        final String OR = " or ";
        StringBuffer findField = new StringBuffer("//routingConfig/" + FIELD_DEF_E);
        if (types != null && types.length > 0) {
            findField.append("[");
            for (int i = 0; i < types.length; i++) {
                findField.append("@workflowType='" + types[i] + "'" + OR);
                // missing workflowType is equivalent ("defaults") to ALL
                if ("ALL".equals(types[i])) {
                    findField.append("not(@workflowType)" + OR);
                }
            }
            if (types.length > 0) {
                // remove trailing " or "
                findField.setLength(findField.length() - OR.length());
            }
            findField.append("]");
        }

        try {
            return (NodeList) xpath.evaluate(findField.toString(), root, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOG.error("Error evaluating expression: '" + findField + "'");
            throw e;
        }
    }

    private static List getRows(Element root, String[] types) {
        List rows = new ArrayList();
        XPath xpath = XPathHelper.newXPath();
        NodeList fieldNodeList;
        try {
            fieldNodeList = getFields(xpath, root, types);
        } catch (XPathExpressionException e) {
            LOG.error("Error evaluating fields expression");
            return rows;
        }
        if (fieldNodeList != null) {
            for (int i = 0; i < fieldNodeList.getLength(); i++) {
                Node field = fieldNodeList.item(i);
                NamedNodeMap fieldAttributes = field.getAttributes();

                List fields = new ArrayList();
                Field myField = new Field(fieldAttributes.getNamedItem("title").getNodeValue(), "", "", false, fieldAttributes.getNamedItem("name").getNodeValue(), "", null, "");
                String quickfinderService = null;
                for (int j = 0; j < field.getChildNodes().getLength(); j++) {
                    Node childNode = field.getChildNodes().item(j);
                    if ("value".equals(childNode.getNodeName())) {
                        myField.setPropertyValue(childNode.getFirstChild().getNodeValue());
                    } else if ("display".equals(childNode.getNodeName())) {
                        List options = new ArrayList();
                        List selectedOptions = new ArrayList();
                        for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                            Node displayChildNode = childNode.getChildNodes().item(k);
                            if ("type".equals(displayChildNode.getNodeName())) {
                                myField.setFieldType(convertTypeToFieldType(displayChildNode.getFirstChild().getNodeValue()));
                            } else if ("meta".equals(displayChildNode.getNodeName())) {
                                // i don't think the rule creation support things in this node.
                                // i don't think the flex Routing report supports things in this node.
                            } else if ("values".equals(displayChildNode.getNodeName())) {
                                NamedNodeMap valuesAttributes = displayChildNode.getAttributes();
                                String optionValue = "";
                                // if element is empty then child will be null
                                Node firstChild = displayChildNode.getFirstChild();
                                if (firstChild != null) {
                                	optionValue = firstChild.getNodeValue();
                                }
                                if (valuesAttributes.getNamedItem("selected") != null) {
                                    selectedOptions.add(optionValue);
                                }
                                String title = "";
                                Node titleAttribute = valuesAttributes.getNamedItem("title");
                                if (titleAttribute != null) {
                                	title = titleAttribute.getNodeValue();
                            	}
                            	options.add(new KeyLabelPair(optionValue, title));
                            } else if ("parameters".equals(displayChildNode.getNodeName())) {
                                NamedNodeMap parametersAttributes = displayChildNode.getAttributes();
                                String parameterValue = (displayChildNode.getFirstChild() == null) ? "" : displayChildNode.getFirstChild().getNodeValue();
                                myField.addDisplayParameter(parametersAttributes.getNamedItem("name").getNodeValue(), parameterValue);
                            }
                        }
                        if (!options.isEmpty()) {
                            myField.setFieldValidValues(options);
                            if (!selectedOptions.isEmpty()) {
                                if (Field.MULTI_VALUE_FIELD_TYPES.contains(myField.getFieldType())) {
                                    String[] newSelectedOptions = new String[selectedOptions.size()];
                                    int k = 0;
                                    for (Iterator iter = selectedOptions.iterator(); iter.hasNext();) {
                                        String option = (String) iter.next();
                                        newSelectedOptions[k] = option;
                                        k++;
                                    }
                                    myField.setPropertyValues(newSelectedOptions);
                                } else {
                                    myField.setPropertyValue((String)selectedOptions.get(0));
                                }
                            }
                        }
                    } else if ("quickfinder".equals(childNode.getNodeName())) {
                        NamedNodeMap quickfinderAttributes = childNode.getAttributes();
                        String drawQuickfinder = quickfinderAttributes.getNamedItem("draw").getNodeValue();
                        if (!Utilities.isEmpty(drawQuickfinder) && "true".equals(drawQuickfinder)) {
                            quickfinderService = quickfinderAttributes.getNamedItem("service").getNodeValue();
                        }
                        myField.setQuickFinderClassNameImpl(quickfinderAttributes.getNamedItem("service").getNodeValue());
                        myField.setHasLookupable(true);
                        myField.setDefaultLookupableName(quickfinderAttributes.getNamedItem("appliesTo").getNodeValue());
                    }
                }
                fields.add(myField);
                if(!Utilities.isEmpty(quickfinderService)){
                    fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, quickfinderService));
                }
                rows.add(new Row(fields));
            }
        }
        return rows;
    }

    private static String convertTypeToFieldType(String type) {
        if ("text".equals(type)) {
            return Field.TEXT;
        } else if ("select".equals(type)) {
            return Field.DROPDOWN;
        } else if ("radio".equals(type)) {
            return Field.RADIO;
        } else if ("quickfinder".equals(type)) {
            return Field.QUICKFINDER;
        }
        return null;
    }

    private static interface ErrorGenerator {
        Object generateInvalidFieldError(Node field, String fieldName, String message);
        Object generateMissingFieldError(Node field, String fieldName, String message);
    }

    private RuleAttribute ruleAttribute;
    private Map paramMap = new HashMap();
    private List ruleRows = new ArrayList();
    private List routingDataRows = new ArrayList();
    private boolean required;

    public StandardGenericXMLRuleAttribute() {
    }

    public void setRuleAttribute(RuleAttribute ruleAttribute) {
        this.ruleAttribute = ruleAttribute;
    }

    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        XPath xpath = XPathHelper.newXPath(docContent.getDocument());
        WorkflowFunctionResolver resolver = XPathHelper.extractFunctionResolver(xpath);
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getName().equals(ruleAttribute.getName())) {
                resolver.setRuleExtension(extension);
                //xpath.setXPathFunctionResolver(resolver);
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    String findXpathExpression = "//routingConfig/" + FIELD_DEF_E + "[@name='" + value.getKey() + "']/fieldEvaluation/xpathexpression";
                    String xpathExpression = null;
                    try {
                        xpathExpression = (String) xpath.evaluate(findXpathExpression, getConfigXML(), XPathConstants.STRING);
                        LOG.debug("routingConfig XPath expression: " + xpathExpression);
                        if (!Utilities.isEmpty(xpathExpression)) {
                            LOG.debug("DocContent: " + docContent.getDocContent());
                            Boolean match = (Boolean) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.BOOLEAN);
                            LOG.debug("routingConfig match? " + match);
                            if (match != null && !match.booleanValue()) {
                                return false;
                            }
                        }
                    } catch (XPathExpressionException e) {
                        LOG.error("error in isMatch ", e);
                        throw new RuntimeException("Error trying to find xml content with xpath expressions: " + findXpathExpression + " or " + xpathExpression, e);
                    }
                }
                resolver.setRuleExtension(null);
            }
        }
        String findXpathExpression = "//routingConfig/globalEvaluations/xpathexpression";
        String xpathExpression = "";
        try {
            NodeList xpathExpressions = (NodeList) xpath.evaluate(findXpathExpression, getConfigXML(), XPathConstants.NODESET);
            for (int i = 0; i < xpathExpressions.getLength(); i++) {
                Node xpathNode = xpathExpressions.item(i);
                xpathExpression = xpathNode.getFirstChild().getNodeValue();
                LOG.debug("global XPath expression: " + xpathExpression);
                if (!Utilities.isEmpty(xpathExpression)) {
                    LOG.debug("DocContent: " + docContent.getDocContent());
                    Boolean match = (Boolean) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.BOOLEAN);
                    LOG.debug("Global match? " + match);
                    if (match != null && !match.booleanValue()) {
                        return false;
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("error in isMatch ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expressions: " + findXpathExpression, e);
        }
        return true;
    }

    public List getRuleRows() {
        if (ruleRows.isEmpty()) {
            ruleRows = getRows(getConfigXML(), new String[] { "ALL", "RULE" });
        }
        return ruleRows;
    }

    private static String getValidationErrorMessage(XPath xpath, Element root, String fieldName) throws XPathExpressionException {
        String findErrorMessage = "//routingConfig/" + FIELD_DEF_E + "[@name='" + fieldName + "']/validation/message";
        return (String) xpath.evaluate(findErrorMessage, root, XPathConstants.STRING);
    }

    private static List validate(Element root, String[] types, Map map, ErrorGenerator errorGenerator) throws XPathExpressionException {
        List errors = new ArrayList();
        XPath xpath = XPathHelper.newXPath();

        NodeList nodes = getFields(xpath, root, types);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node field = nodes.item(i);
            NamedNodeMap fieldAttributes = field.getAttributes();
            String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();

            LOG.debug("evaluating field: " + fieldName);
            String findValidation = "//routingConfig/" + FIELD_DEF_E + "[@name='" + fieldName + "']/validation";

            Node validationNode = (Node) xpath.evaluate(findValidation, root, XPathConstants.NODE);
            boolean fieldIsRequired = false;
            if (validationNode != null) {
                NamedNodeMap validationAttributes = validationNode.getAttributes();
                Node reqAttribNode = validationAttributes.getNamedItem("required");
                fieldIsRequired = reqAttribNode != null && "true".equalsIgnoreCase(reqAttribNode.getNodeValue());
            }

            String findRegex = "//routingConfig/" + FIELD_DEF_E + "[@name='" + fieldName + "']/validation/regex";

            String regex = null;
            Node regexNode = (Node) xpath.evaluate(findRegex, root, XPathConstants.NODE);

            if (regexNode != null && regexNode.getFirstChild() != null) {
                regex = regexNode.getFirstChild().getNodeValue();
                if (regex == null) {
                    throw new RuntimeException("Null regex text node");
                }
            }/* else {
                if (fieldIsRequired) {
                    fieldIsOnlyRequired = true;
                    LOG.debug("Setting empty regex to .+ as field is required");
                    // NOTE: ok, so technically .+ is not the same as checking merely
                    // for existence, because a field can be extant but "empty"
                    // however this has no relevance to the user as an empty field
                    // is for all intents and purposes non-existent (not-filled-in)
                    // so let's just use this regex to simplify the logic and
                    // pass everything through a regex check
                    regex = ".+";
                } else {
                    LOG.debug("Setting empty regex to .* as field is NOT required");
                    regex = ".*";
                }
            }*/

            LOG.debug("regex for field '" + fieldName + "': '" + regex + "'");

            String fieldValue = null;
            if (map != null) {
                fieldValue = (String) map.get(fieldName);
            }

            LOG.debug("field value: " + fieldValue);

            // fix up non-existent value for regex purposes only
            if (fieldValue == null) {
                fieldValue = "";
            }

            if (regex == null){
                if (fieldIsRequired) {
                    if (fieldValue.length() == 0) {
                        errors.add(errorGenerator.generateMissingFieldError(field, fieldName, getValidationErrorMessage(xpath, root, fieldName)));
                    }
                }
            } else {
                if (!Pattern.compile(regex).matcher(fieldValue).matches()) {
                    LOG.debug("field value does not match validation regex");
                    errors.add(errorGenerator.generateInvalidFieldError(field, fieldName, getValidationErrorMessage(xpath, root, fieldName)));
                }
            }
        }
        return errors;
    }

    public List getRoutingDataRows() {
        if (routingDataRows.isEmpty()) {
            routingDataRows = getRows(getConfigXML(), new String[] { "ALL", "REPORT" });
        }
        return routingDataRows;
    }

    public String getDocContent() {
        XPath xpath = XPathHelper.newXPath();
        final String findDocContent = "//routingConfig/xmlDocumentContent";
        try {
            Node xmlDocumentContent = (Node) xpath.evaluate(findDocContent, getConfigXML(), XPathConstants.NODE);

            NodeList nodes = getFields(xpath, getConfigXML(), new String[] { "ALL", "REPORT", "RULE" });
//            if (nodes == null || nodes.getLength() == 0) {
//                return "";
//            }

            if (xmlDocumentContent != null && xmlDocumentContent.hasChildNodes()) {
                // Custom doc content in the routingConfig xml.
                String documentContent = "";
                NodeList customNodes = xmlDocumentContent.getChildNodes();
                for (int i = 0; i < customNodes.getLength(); i++) {
                    Node childNode = customNodes.item(i);
                    documentContent += XmlHelper.writeNode(childNode);
                }

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node field = nodes.item(i);
                    NamedNodeMap fieldAttributes = field.getAttributes();
                    String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();
                    LOG.debug("Replacing field '" + fieldName + "'");
                    Map map = getParamMap();
                    String fieldValue = (String) map.get(fieldName);
                    if (map != null && !Utilities.isEmpty(fieldValue)) {
                        LOG.debug("Replacing %" + fieldName + "% with field value: '" + fieldValue + "'");
                        documentContent = documentContent.replaceAll("%" + fieldName + "%", fieldValue);
                    } else {
                        LOG.debug("Field map is null or fieldValue is empty");
                    }
                }
                return documentContent;
            } else {
                // Standard doc content if no doc content is found in the routingConfig xml.
                StringBuffer documentContent = new StringBuffer("<xmlRouting>");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node field = nodes.item(i);
                    NamedNodeMap fieldAttributes = field.getAttributes();
                    String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();
                    Map map = getParamMap();
                    if (map != null && !Utilities.isEmpty((String) map.get(fieldName))) {
                        documentContent.append("<field name=\"");
                        documentContent.append(fieldName);
                        documentContent.append("\"><value>");
                        documentContent.append((String) map.get(fieldName));
                        documentContent.append("</value></field>");
                    }
                }
                documentContent.append("</xmlRouting>");
                return documentContent.toString();
            }
        } catch (XPathExpressionException e) {
            LOG.error("error in getDocContent ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        } catch (Exception e) {
            LOG.error("error in getDocContent attempting to find xml doc content", e);
            throw new RuntimeException("Error trying to get xml doc content.", e);
        }
    }

    public List getRuleExtensionValues() {
        List extensionValues = new ArrayList();

        XPath xpath = XPathHelper.newXPath();
        try {
            NodeList nodes = getFields(xpath, getConfigXML(), new String[] { "ALL", "RULE" });
            for (int i = 0; i < nodes.getLength(); i++) {
                Node field = nodes.item(i);
                NamedNodeMap fieldAttributes = field.getAttributes();
                String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();
                Map map = getParamMap();
                if (map != null && !Utilities.isEmpty((String) map.get(fieldName))) {
                    RuleExtensionValue value = new RuleExtensionValue();
                    value.setKey(fieldName);
                    value.setValue((String) map.get(fieldName));
                    extensionValues.add(value);
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("error in getRuleExtensionValues ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        }
        return extensionValues;
    }

    public List validateRoutingData(Map paramMap) {
        this.paramMap = paramMap;
        try {
            return validate(getConfigXML(), new String[] { "ALL", "REPORT" }, paramMap, new ErrorGenerator() {
                public Object generateInvalidFieldError(Node field, String fieldName, String message) {
                    return new WorkflowAttributeValidationError("routetemplate.xmlattribute.error", message);
                }
                public Object generateMissingFieldError(Node field, String fieldName, String message) {
                    return new WorkflowAttributeValidationError("routetemplate.xmlattribute.required.error", field.getAttributes().getNamedItem("title").getNodeValue());
                }
            });
        } catch (XPathExpressionException e) {
            LOG.error("error in validateRoutingData ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        }
    }

    public List validateRuleData(Map paramMap) {
        this.paramMap = paramMap;
        try {
            return validate(getConfigXML(), new String[] { "ALL", "RULE" }, paramMap, new ErrorGenerator() {
                public Object generateInvalidFieldError(Node field, String fieldName, String message) {
                    return new WorkflowServiceErrorImpl("Xml attribute error.", "routetemplate.xmlattribute.error", message);
                }
                public Object generateMissingFieldError(Node field, String fieldName, String message) {
                    return new WorkflowServiceErrorImpl("Xml attribute error.", "routetemplate.xmlattribute.required.error", field.getAttributes().getNamedItem("title").getNodeValue());
                }
            });
        } catch (XPathExpressionException e) {
            LOG.error("error in validateRoutingData ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        }
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public Element getConfigXML() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(ruleAttribute.getXmlConfigData())))).getDocumentElement();
        } catch (Exception e) {
            String str = ruleAttribute == null ? "null" : ruleAttribute.getName();
            LOG.error("error parsing xml data from rule attribute: " + str, e);
            throw new RuntimeException("error parsing xml data from rule attribute: " + str, e);
        }
    }

    // TODO: possibly simplify this even further by making WorkflowAttributeValidationError a WorkflowServiceError, and
    // dispense with custom error generators...
    public List validateClientRoutingData() {
        LOG.debug("validating client routing data");
        try {
            return validate(getConfigXML(), new String[] { "ALL", "RULE" }, getParamMap(), new ErrorGenerator() {
                public Object generateInvalidFieldError(Node field, String fieldName, String message) {
                    if (Utilities.isEmpty(message)) {
                        message = "invalid field value";
                    } else {
                        LOG.info("Message: '" + message + "'");
                    }
                    return new WorkflowAttributeValidationError(fieldName, message);
                }
                public Object generateMissingFieldError(Node field, String fieldName, String message) {
                    return new WorkflowAttributeValidationError(fieldName, "Attribute is required; " + message);
                }
            });
        } catch (XPathExpressionException e) {
            LOG.error("error in validateClientRoutingData ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        }
    }

    public Map getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map paramMap) {
        this.paramMap = paramMap;
    }
}