/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch.xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAbstractControl;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableAttributeLookupSettings;
import org.kuali.rice.core.api.uif.RemotableDatepicker;
import org.kuali.rice.core.api.uif.RemotableHiddenInput;
import org.kuali.rice.core.api.uif.RemotableRadioButtonGroup;
import org.kuali.rice.core.api.uif.RemotableSelect;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.xml.XmlJotter;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.attribute.XMLAttributeUtils;
import org.kuali.rice.kew.docsearch.DocumentLookupInternalUtils;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Implementation of a {@code SearchableAttribute} whose configuration is driven from XML.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardGenericXMLSearchableAttribute implements SearchableAttribute {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardGenericXMLSearchableAttribute.class);
    private static final String FIELD_DEF_E = "fieldDef";

    @Override
    public String generateSearchContent(ExtensionDefinition extensionDefinition, String documentTypeName, WorkflowAttributeDefinition attributeDefinition) {
		XPath xpath = XPathHelper.newXPath();
		String findDocContent = "//searchingConfig/xmlSearchContent";
        Map<String, String> propertyDefinitionMap = attributeDefinition.getPropertyDefinitionsAsMap();
		try {
			Node xmlDocumentContent = (Node) xpath.evaluate(findDocContent, getConfigXML(extensionDefinition), XPathConstants.NODE);
			if (xmlDocumentContent != null && xmlDocumentContent.hasChildNodes()) {
				// Custom doc content in the searchingConfig xml.
				String docContent = "";
				NodeList customNodes = xmlDocumentContent.getChildNodes();
				for (int i = 0; i < customNodes.getLength(); i++) {
					Node childNode = customNodes.item(i);
					docContent += XmlJotter.jotNode(childNode);
				}
				String findField = "//searchingConfig/" + FIELD_DEF_E;
				NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(extensionDefinition), XPathConstants.NODESET);
				if (nodes == null || nodes.getLength() == 0) {
					return "";
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					Node field = nodes.item(i);
					NamedNodeMap fieldAttributes = field.getAttributes();
					if (propertyDefinitionMap != null && !StringUtils.isBlank(propertyDefinitionMap.get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
						docContent = docContent.replaceAll("%" + fieldAttributes.getNamedItem("name").getNodeValue() + "%", propertyDefinitionMap.get(fieldAttributes.getNamedItem("name").getNodeValue()));
					}
				}
				return docContent;
			} else {
				// Standard doc content if no doc content is found in the searchingConfig xml.
				StringBuffer documentContent = new StringBuffer("<xmlRouting>");
				String findField = "//searchingConfig/" + FIELD_DEF_E;
				NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(extensionDefinition), XPathConstants.NODESET);
				if (nodes == null || nodes.getLength() == 0) {
					return "";
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					Node field = nodes.item(i);
					NamedNodeMap fieldAttributes = field.getAttributes();
					if (propertyDefinitionMap != null && !StringUtils.isBlank(propertyDefinitionMap.get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
						documentContent.append("<field name=\"");
						documentContent.append(fieldAttributes.getNamedItem("name").getNodeValue());
						documentContent.append("\"><value>");
						documentContent.append(propertyDefinitionMap.get(fieldAttributes.getNamedItem("name").getNodeValue()));
						documentContent.append("</value></field>");
					}
				}
				documentContent.append("</xmlRouting>");
				return documentContent.toString();
			}
		} catch (XPathExpressionException e) {
			LOG.error("error in getSearchContent ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression", e);
		} catch (Exception e) {
			LOG.error("error in getSearchContent attempting to find xml search content", e);
			throw new RuntimeException("Error trying to get xml search content.", e);
		}
	}

    @Override
    public List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition,
            DocumentWithContent documentWithContent) {
		List<DocumentAttribute> searchStorageValues = new ArrayList<DocumentAttribute>();
		Document document;
        String fullDocumentContent = documentWithContent.getDocumentContent().getFullContent();
        if (StringUtils.isBlank(documentWithContent.getDocumentContent().getFullContent())) {
            LOG.warn("Empty Document Content found for document id: " + documentWithContent.getDocument().getDocumentId());
            return searchStorageValues;
        }
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new BufferedReader(new StringReader(fullDocumentContent))));
		} catch (Exception e){
			LOG.error("error parsing docContent: "+documentWithContent.getDocumentContent(), e);
			throw new RuntimeException("Error trying to parse docContent: "+documentWithContent.getDocumentContent(), e);
		}
		XPath xpath = XPathHelper.newXPath(document);
		String findField = "//searchingConfig/" + FIELD_DEF_E;
		try {
			NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(extensionDefinition), XPathConstants.NODESET);
            if (nodes == null) {
                LOG.error("Could not find searching configuration (<searchingConfig>) for this XMLSearchAttribute");
            } else {

    			for (int i = 0; i < nodes.getLength(); i++) {
    				Node field = nodes.item(i);
    				NamedNodeMap fieldAttributes = field.getAttributes();

    				String findXpathExpressionPrefix = "//searchingConfig/" + FIELD_DEF_E + "[@name='" + fieldAttributes.getNamedItem("name").getNodeValue() + "']";
    				String findDataTypeXpathExpression = findXpathExpressionPrefix + "/searchDefinition/@dataType";
    				String findXpathExpression = findXpathExpressionPrefix + "/fieldEvaluation/xpathexpression";
    				String fieldDataType = null;
    				String xpathExpression = null;
    				try {
                        fieldDataType = (String) xpath.evaluate(findDataTypeXpathExpression, getConfigXML(extensionDefinition), XPathConstants.STRING);
    					if (org.apache.commons.lang.StringUtils.isEmpty(fieldDataType)) {
    						fieldDataType = KEWConstants.SearchableAttributeConstants.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
    					}
    				    xpathExpression = (String) xpath.evaluate(findXpathExpression, getConfigXML(extensionDefinition), XPathConstants.STRING);
    					if (!org.apache.commons.lang.StringUtils.isEmpty(xpathExpression)) {

                            try {
                                NodeList searchValues = (NodeList) xpath.evaluate(xpathExpression, document.getDocumentElement(), XPathConstants.NODESET);
                                // being that this is the standard xml attribute we will return the key with an empty value
                                // so we can find it from a doc search using this key
                                if (searchValues.getLength() == 0) {
                                	DocumentAttribute searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), null);
                                	if (searchableValue != null) {
                                        searchStorageValues.add(searchableValue);
                                	}
                                } else {
                                	for (int j = 0; j < searchValues.getLength(); j++) {
                                        Node searchValue = searchValues.item(j);
                                        String value = null;
                                        if (searchValue.getFirstChild() != null && (!StringUtils.isEmpty(searchValue.getFirstChild().getNodeValue()))) {
                                        	value = searchValue.getFirstChild().getNodeValue();
                                        }
                                    	DocumentAttribute searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), value);
                                    	if (searchableValue != null) {
                                            searchStorageValues.add(searchableValue);
                                    	}
                                    }
                                }
                            } catch (XPathExpressionException e) {
                                //try for a string being returned from the expression.  This
                                //seems like a poor way to determine our expression return type but
                                //it's all I can come up with at the moment.
                                String searchValue = (String) xpath.evaluate(xpathExpression, DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                                		new InputSource(new BufferedReader(new StringReader(documentWithContent.getDocumentContent().getFullContent())))).getDocumentElement(), XPathConstants.STRING);
                                String value = null;
                                if (StringUtils.isNotBlank(searchValue)) {
                                    value = searchValue;
                                }
                            	DocumentAttribute searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), value);
                            	if (searchableValue != null) {
                                    searchStorageValues.add(searchableValue);
                            	}
                            }
    					}
    				} catch (XPathExpressionException e) {
    					LOG.error("error in isMatch ", e);
    					throw new RuntimeException("Error trying to find xml content with xpath expressions: " + findXpathExpression + " or " + xpathExpression, e);
    				} catch (Exception e){
    					LOG.error("error parsing docContent: " + documentWithContent.getDocumentContent(), e);
    					throw new RuntimeException("Error trying to parse docContent: " + documentWithContent.getDocumentContent(), e);
    				}
                }
			}
		} catch (XPathExpressionException e) {
			LOG.error("error in getSearchStorageValues ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression: " + findField, e);
		}
		return searchStorageValues;
	}

	private DocumentAttribute setupSearchableAttributeValue(String dataType, String key, String value) {
		SearchableAttributeValue attValue = DocumentLookupInternalUtils.getSearchableAttributeValueByDataTypeString(
                dataType);
		if (attValue == null) {
			String errorMsg = "Cannot find a SearchableAttributeValue associated with the data type '" + dataType + "'";
		    LOG.error("setupSearchableAttributeValue() " + errorMsg);
		    throw new RuntimeException(errorMsg);
		}
        value = (value != null) ? value.trim() : null;
        if ( (StringUtils.isNotBlank(value)) && (!attValue.isPassesDefaultValidation(value)) ) {
            String errorMsg = "SearchableAttributeValue with the data type '" + dataType + "', key '" + key + "', and value '" + value + "' does not pass default validation and cannot be saved to the database";
            LOG.error("setupSearchableAttributeValue() " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
		attValue.setSearchableAttributeKey(key);
		attValue.setupAttributeValue(value);
    	return attValue.toDocumentAttribute();
	}

    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {

        List<RemotableAttributeField> searchFields = new ArrayList<RemotableAttributeField>();
        List<SearchableAttributeValue> searchableAttributeValues = DocumentLookupInternalUtils
                .getSearchableAttributeValueObjectTypes();
        NodeList fieldNodeList = getConfigXML(extensionDefinition).getElementsByTagName(FIELD_DEF_E);
        for (int i = 0; i < fieldNodeList.getLength(); i++) {
            Node field = fieldNodeList.item(i);
            NamedNodeMap fieldAttributes = field.getAttributes();
            
            boolean hasXPathExpression = false;

            String attributeName = fieldAttributes.getNamedItem("name").getNodeValue();
            String attributeTitle = fieldAttributes.getNamedItem("title").getNodeValue();
            RemotableAttributeField.Builder fieldBuilder = RemotableAttributeField.Builder.create(attributeName);
            fieldBuilder.setLongLabel(attributeTitle);
            RemotableAttributeLookupSettings.Builder attributeLookupSettings = RemotableAttributeLookupSettings.Builder.create();
            fieldBuilder.setAttributeLookupSettings(attributeLookupSettings);

            for (int j = 0; j < field.getChildNodes().getLength(); j++) {
                Node childNode = field.getChildNodes().item(j);
                if ("value".equals(childNode.getNodeName())) {
                    String defaultValue = childNode.getFirstChild().getNodeValue();
                    fieldBuilder.setDefaultValues(Collections.singletonList(defaultValue));
                } else if ("display".equals(childNode.getNodeName())) {

                    String typeValue = null;
                    List<KeyValue> options = new ArrayList<KeyValue>();
                    List<String> selectedOptions = new ArrayList<String>();


                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                        Node displayChildNode = childNode.getChildNodes().item(k);
                        if ("type".equals(displayChildNode.getNodeName())) {
                            typeValue = displayChildNode.getFirstChild().getNodeValue();
                        } else if ("meta".equals(displayChildNode.getNodeName())) {

                        } else if ("values".equals(displayChildNode.getNodeName())) {
                            NamedNodeMap valuesAttributes = displayChildNode.getAttributes();
                            // this is to allow an empty drop down choice and can probably implemented in a better way
                            if (displayChildNode.getFirstChild() != null) {
                                options.add(new ConcreteKeyValue(displayChildNode.getFirstChild().getNodeValue(), valuesAttributes.getNamedItem("title").getNodeValue()));
                                if (valuesAttributes.getNamedItem("selected") != null) {
                                    selectedOptions.add(displayChildNode.getFirstChild().getNodeValue());
                                }
                            } else {
                                options.add(new ConcreteKeyValue("", valuesAttributes.getNamedItem("title").getNodeValue()));
                            }
                        }
                    }

                    RemotableAbstractControl.Builder controlBuilder = constructControl(typeValue, options);
                    fieldBuilder.setControl(controlBuilder);

                    if ("date".equals(typeValue)) {
                        fieldBuilder.getWidgets().add(RemotableDatepicker.Builder.create());
                        fieldBuilder.setDataType(DataType.DATE);
                    }

                    if (selectedOptions != null && !selectedOptions.isEmpty()) {
                        fieldBuilder.setDefaultValues(selectedOptions);
                    }
                } else if ("visibility".equals(childNode.getNodeName())) {
                    applyVisibility(fieldBuilder, attributeLookupSettings, (Element)childNode);
                } else if ("searchDefinition".equals(childNode.getNodeName())) {
                    NamedNodeMap searchDefAttributes = childNode.getAttributes();
                    // data type operations
                    String dataTypeValue = (searchDefAttributes.getNamedItem("dataType") == null) ? null : searchDefAttributes.getNamedItem("dataType").getNodeValue();
                    DataType dataType = convertValueToDataType(dataTypeValue);
                    fieldBuilder.setDataType(dataType);
                    if (DataType.DATE == fieldBuilder.getDataType()) {
                        fieldBuilder.getWidgets().add(RemotableDatepicker.Builder.create());
                    }

                    boolean isRangeSearchField = isRangeSearchField(searchableAttributeValues, fieldBuilder.getDataType(), searchDefAttributes, childNode);
                    if (!isRangeSearchField) {
                        Boolean caseSensitive = getBooleanValue(searchDefAttributes, "caseSensitive");
                        if (caseSensitive != null) {
                            attributeLookupSettings.setCaseSensitive(caseSensitive);
                        }
                    } else {
                        applyAttributeRange(attributeLookupSettings, fieldBuilder, childNode);
                    }


                    /**

                     TODO - Rice 2.0 - Figure out how to handle these formatters

                     String formatterClass = (searchDefAttributes.getNamedItem("formatterClass") == null) ? null : searchDefAttributes.getNamedItem("formatterClass").getNodeValue();
                     if (!StringUtils.isEmpty(formatterClass)) {
                     try {
                     myField.setFormatter((Formatter)Class.forName(formatterClass).newInstance());
                     } catch (InstantiationException e) {
                     LOG.error("Unable to get new instance of formatter class: " + formatterClass);
                     throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass);
                     }
                     catch (IllegalAccessException e) {
                     LOG.error("Unable to get new instance of formatter class: " + formatterClass);
                     throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass);
                     } catch (ClassNotFoundException e) {
                     LOG.error("Unable to find formatter class: " + formatterClass);
                     throw new RuntimeException("Unable to find formatter class: " + formatterClass);
                     }
                     }

                     */

                } else if ("resultColumn".equals(childNode.getNodeName())) {
                    NamedNodeMap columnAttributes = childNode.getAttributes();
                    Node showNode = columnAttributes.getNamedItem("show");
                    boolean isColumnVisible = true;
                    if (showNode != null && showNode.getNodeValue() != null) {
                        isColumnVisible = Boolean.valueOf(showNode.getNodeValue());
                    }
                    attributeLookupSettings.setInResults(isColumnVisible);
                } else if ("fieldEvaluation".equals(childNode.getNodeName())) {
                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                        Node displayChildNode = childNode.getChildNodes().item(k);
                        if ("xpathexpression".equals(displayChildNode.getNodeName())) {
                            hasXPathExpression = true;
                            break;
                        }
                    }
                } else if ("lookup".equals(childNode.getNodeName())) {
                    XMLAttributeUtils.establishFieldLookup(fieldBuilder, childNode);
                }
            }
                
            searchFields.add(fieldBuilder.build());

        }
		return searchFields;
	}

    private DataType convertValueToDataType(String dataTypeValue) {
        if (StringUtils.isBlank(dataTypeValue)) {
            return DataType.STRING;
        } else if (KEWConstants.SearchableAttributeConstants.DATA_TYPE_STRING.equals(dataTypeValue)) {
            return DataType.STRING;
        } else if (KEWConstants.SearchableAttributeConstants.DATA_TYPE_DATE.equals(dataTypeValue)) {
            return DataType.DATE;
        } else if (KEWConstants.SearchableAttributeConstants.DATA_TYPE_LONG.equals(dataTypeValue)) {
            return DataType.LONG;
        } else if (KEWConstants.SearchableAttributeConstants.DATA_TYPE_FLOAT.equals(dataTypeValue)) {
            return DataType.FLOAT;
        }
        throw new IllegalArgumentException("Invalid dataTypeValue was given: " + dataTypeValue);
    }

    private boolean isRangeSearchField(List<SearchableAttributeValue> searchableAttributeValues, DataType dataType, NamedNodeMap searchDefAttributes, Node searchDefNode) {
        for (SearchableAttributeValue attValue : searchableAttributeValues)
        {
            DataType attributeValueDataType = convertValueToDataType(attValue.getAttributeDataType());
            if (attributeValueDataType == dataType) {
                return isRangeSearchField(attValue, searchDefAttributes, searchDefNode);
            }
        }
        String errorMsg = "Could not find searchable attribute value for data type '" + dataType + "'";
        LOG.error("isRangeSearchField(List, String, NamedNodeMap, Node) " + errorMsg);
        throw new WorkflowRuntimeException(errorMsg);
    }

    private boolean isRangeSearchField(SearchableAttributeValue searchableAttributeValue, NamedNodeMap searchDefAttributes, Node searchDefNode) {
        boolean allowRangedSearch = searchableAttributeValue.allowsRangeSearches();
        Boolean rangeSearchBoolean = getBooleanValue(searchDefAttributes, "rangeSearch");
        boolean rangeSearch = (rangeSearchBoolean != null) && rangeSearchBoolean;
        Node rangeDefinition = getPotentialChildNode(searchDefNode, "rangeDefinition");
        return ( (allowRangedSearch) && ((rangeDefinition != null) || (rangeSearch)) );
    }

    private void applyAttributeRange(RemotableAttributeLookupSettings.Builder attributeLookupSettings, RemotableAttributeField.Builder fieldBuilder, Node searchDefinitionNode) {
        NamedNodeMap searchDefAttributes = searchDefinitionNode.getAttributes();
        Node rangeDefinitionNode = getPotentialChildNode(searchDefinitionNode, "rangeDefinition");
        String lowerBoundDefaultName = KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldBuilder.getName();
        String upperBoundDefaultName = KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldBuilder.getName();
        attributeLookupSettings.setRanged(true);
        attributeLookupSettings.setLowerBoundName(lowerBoundDefaultName);
        attributeLookupSettings.setUpperBoundName(upperBoundDefaultName);
        if (rangeDefinitionNode != null) {
            NamedNodeMap rangeDefinitionAttributes = rangeDefinitionNode.getAttributes();
            NamedNodeMap lowerBoundNodeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "lower");
            NamedNodeMap upperBoundNodeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "upper");
            // below methods allow for nullable attribute NamedNodeMaps
            RangeBound lowerRangeBound = determineRangeBoundProperties(searchDefAttributes, rangeDefinitionAttributes, lowerBoundNodeAttributes);
            if (lowerRangeBound != null) {
                if (lowerRangeBound.inclusive != null) {
                    attributeLookupSettings.setLowerBoundInclusive(lowerRangeBound.inclusive);
                }
                if (StringUtils.isNotBlank(lowerRangeBound.label)) {
                    attributeLookupSettings.setLowerBoundLabel(lowerRangeBound.label);
                }
            }
            RangeBound upperRangeBound = determineRangeBoundProperties(searchDefAttributes, rangeDefinitionAttributes, upperBoundNodeAttributes);
            if (upperRangeBound != null) {
                if (upperRangeBound.inclusive != null) {
                    attributeLookupSettings.setUpperBoundInclusive(upperRangeBound.inclusive);
                }
                if (StringUtils.isNotBlank(upperRangeBound.label)) {
                    attributeLookupSettings.setUpperBoundLabel(upperRangeBound.label);
                }
            }
        }
    }

	private NamedNodeMap getAttributesForPotentialChildNode(Node node, String potentialChildNodeName) {
		Node testNode = getPotentialChildNode(node, potentialChildNodeName);
		return testNode != null ? testNode.getAttributes() : null;
	}

	private Node getPotentialChildNode(Node node, String childNodeName) {
		if (node != null) {
			for (int k = 0; k < node.getChildNodes().getLength(); k++) {
				Node testNode = node.getChildNodes().item(k);
				if (testNode.getNodeName().equals(childNodeName)) {
					return testNode;
				}
			}
		}
		return null;
	}

    private RangeBound determineRangeBoundProperties(NamedNodeMap searchDefinitionAttributes, NamedNodeMap rangeDefinitionAttributes, NamedNodeMap rangeBoundAttributes) {
        RangeBound rangeBound = new RangeBound();
        rangeBound.label = getPotentialRangeBoundLabelFromAttributes(rangeBoundAttributes);
        List<NamedNodeMap> namedNodeMapsByImportance = new ArrayList<NamedNodeMap>();
		namedNodeMapsByImportance.add(rangeBoundAttributes);
		namedNodeMapsByImportance.add(rangeDefinitionAttributes);
		namedNodeMapsByImportance.add(searchDefinitionAttributes);
		rangeBound.inclusive = getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "inclusive");
        return rangeBound;
    }

    private String getPotentialRangeBoundLabelFromAttributes(NamedNodeMap rangeBoundAttributes) {
        if (rangeBoundAttributes != null) {
            String boundLabel = (rangeBoundAttributes.getNamedItem("label") == null) ? null : rangeBoundAttributes.getNamedItem("label").getNodeValue();
            if (!StringUtils.isBlank(boundLabel)) {
                return boundLabel;
            }
        }
        return null;
    }

	private Boolean getBooleanWithPotentialOverrides(List<NamedNodeMap> namedNodeMapsByImportance, String attributeName) {
        for (NamedNodeMap aNamedNodeMapsByImportance : namedNodeMapsByImportance) {
            NamedNodeMap nodeMap = (NamedNodeMap) aNamedNodeMapsByImportance;
            Boolean booleanValue = getBooleanValue(nodeMap, attributeName);
            if (booleanValue != null) {
                return booleanValue;
            }
        }
		return null;
    }

	private Boolean getBooleanValue(NamedNodeMap nodeMap, String attributeName) {
		String nodeValue = getStringValue(nodeMap, attributeName);
		if (nodeValue != null) {
			return Boolean.valueOf(nodeValue);
		}
		return null;
	}

	private String getStringValue(NamedNodeMap nodeMap, String attributeName) {
        if (nodeMap == null || nodeMap.getNamedItem(attributeName) == null || StringUtils.isBlank(nodeMap.getNamedItem(attributeName).getNodeValue())) {
            return null;
        }
        return nodeMap.getNamedItem(attributeName).getNodeValue();
	}

	private void applyVisibility(RemotableAttributeField.Builder fieldBuilder, RemotableAttributeLookupSettings.Builder attributeLookupSettings, Element visibilityElement) {
		for (int vIndex = 0; vIndex < visibilityElement.getChildNodes().getLength(); vIndex++) {
			Node visibilityChildNode = visibilityElement.getChildNodes().item(vIndex);
			if (visibilityChildNode.getNodeType() == Node.ELEMENT_NODE) {
				boolean visible = true;
				NamedNodeMap visibilityAttributes = visibilityChildNode.getAttributes();
				Node visibleNode = visibilityAttributes.getNamedItem("visible");
				if (visibleNode != null && visibleNode.getNodeValue() != null) {
					visible = Boolean.valueOf(visibleNode.getNodeValue());
				} else {
					NodeList visibilityDecls = visibilityChildNode.getChildNodes();
					for (int vdIndex = 0; vdIndex < visibilityDecls.getLength(); vdIndex++) {
						Node visibilityDecl = visibilityDecls.item(vdIndex);
                        if (visibilityDecl.getNodeType() == Node.ELEMENT_NODE) {
                        	boolean hasIsMemberOfGroupElement = false;
                        	String groupName = null;
                        	String groupNamespace = null;
                        	if (XmlConstants.IS_MEMBER_OF_GROUP.equals(visibilityDecl.getNodeName())) { // Found an "isMemberOfGroup" element.
                        		hasIsMemberOfGroupElement = true;
                        		groupName = Utilities.substituteConfigParameters(visibilityDecl.getTextContent()).trim();
                        		groupNamespace = Utilities.substituteConfigParameters(((Element)visibilityDecl).getAttribute(XmlConstants.NAMESPACE)).trim();
                        	}
                        	else if (XmlConstants.IS_MEMBER_OF_WORKGROUP.equals(visibilityDecl.getNodeName())) { // Found a deprecated "isMemberOfWorkgroup" element.
                        		LOG.warn((new StringBuilder()).append("Rule Attribute XML is using deprecated element '").append(
                        				XmlConstants.IS_MEMBER_OF_WORKGROUP).append("', please use '").append(XmlConstants.IS_MEMBER_OF_GROUP).append(
                        						"' instead.").toString());
                        		hasIsMemberOfGroupElement = true;
    							String workgroupName = Utilities.substituteConfigParameters(visibilityDecl.getFirstChild().getNodeValue());
    							groupNamespace = Utilities.parseGroupNamespaceCode(workgroupName);
    							groupName = Utilities.parseGroupName(workgroupName);
    						}
    						if (hasIsMemberOfGroupElement) { // Found one of the "isMemberOf..." elements.
    							UserSession session = GlobalVariables.getUserSession();
    							if (session == null) {
    								throw new WorkflowRuntimeException("UserSession is null!  Attempted to render the searchable attribute outside of an established session.");
    							}
                                GroupService groupService = KimApiServiceLocator.getGroupService();

    						    Group group = groupService.getGroupByName(groupNamespace, groupName);
		                        visible =  group == null ? false : groupService.isMemberOfGroup(session.getPerson().getPrincipalId(), group.getId());
    						}
                        }
					}
				}
				String type = visibilityChildNode.getNodeName();
				if ("field".equals(type) || "fieldAndColumn".equals(type)) {
					// if it's not visible, coerce this field to a hidden type
					if (!visible) {
                        fieldBuilder.setControl(RemotableHiddenInput.Builder.create());
					}
				}
				if ("column".equals(type) || "fieldAndColumn".equals(type)) {
					attributeLookupSettings.setInResults(visible);
				}
			}
		}
	}

    private RemotableAbstractControl.Builder constructControl(String type, List<KeyValue> options) {

        RemotableAbstractControl.Builder control = null;
        Map<String, String> optionMap = new HashMap<String, String>();
        for (KeyValue option : options) {
            optionMap.put(option.getKey(), option.getValue());
        }
        if ("text".equals(type) || "date".equals(type)) {
			control = RemotableTextInput.Builder.create();
		} else if ("select".equals(type)) {
            control = RemotableSelect.Builder.create(optionMap);
		} else if ("radio".equals(type)) {
            control = RemotableRadioButtonGroup.Builder.create(optionMap);
		} else if ("hidden".equals(type)) {
            control = RemotableHiddenInput.Builder.create();
		} else if ("multibox".equals(type)) {
            RemotableSelect.Builder builder = RemotableSelect.Builder.create(optionMap);
            builder.setMultiple(true);
            control = builder;
        } else {
		    throw new IllegalArgumentException("Illegal field type found: " + type);
        }
        return control;

    }

    @Override
    public List<RemotableAttributeError> validateSearchFieldParameters(ExtensionDefinition extensionDefinition, Map<String, List<String>> parameters, String documentTypeName) {
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

		XPath xpath = XPathHelper.newXPath();
		String findField = "//searchingConfig/" + FIELD_DEF_E;
		try {
			NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(extensionDefinition), XPathConstants.NODESET);
			if (nodes == null) {
				// no field definitions is de facto valid
			    LOG.warn("Could not find any field definitions (<" + FIELD_DEF_E + ">) or possibly a searching configuration (<searchingConfig>) for this XMLSearchAttribute");
			} else {
    			for (int i = 0; i < nodes.getLength(); i++) {
    				Node field = nodes.item(i);
    				NamedNodeMap fieldAttributes = field.getAttributes();
					String fieldDefName = fieldAttributes.getNamedItem("name").getNodeValue();
                    String fieldDefTitle = ((fieldAttributes.getNamedItem("title")) != null) ? fieldAttributes.getNamedItem("title").getNodeValue() : "";

                    // check for range search members in the parameter map
                    boolean rangeMemberInSearchParams = false;

                    if (parameters != null) {

                        String lowerBoundFieldDefName = KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldDefName;
                        String upperBoundFieldDefName = KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldDefName;
                        List<String> lowerBoundValues = parameters.get(lowerBoundFieldDefName);
                        rangeMemberInSearchParams |= CollectionUtils.isNotEmpty(lowerBoundValues) && StringUtils.isNotBlank(lowerBoundValues.get(0));
                        List<String> upperBoundValues = parameters.get(upperBoundFieldDefName);
                        rangeMemberInSearchParams |= CollectionUtils.isNotEmpty(upperBoundValues) && StringUtils.isNotBlank(upperBoundValues.get(0));

                        List<String> testObject = parameters.get(fieldDefName);
    					if (testObject != null || rangeMemberInSearchParams) {

                            // check to see if we need to process this field at all
                            if (!rangeMemberInSearchParams) {
                                if (testObject.size() == 1) {
                                    String stringVariable = testObject.get(0);
                                    if (StringUtils.isBlank(stringVariable)) {
                                        // field is not multi value and is empty... skip it
                                        continue;
                                    }
                                } else {
                                    boolean allAreBlank = true;
                                    for (String testString : testObject) {
                                        if (StringUtils.isNotBlank(testString)) {
                                            allAreBlank = false;
                                            break;
                                        }
                                    }
                                    if (allAreBlank) {
                                        // field is multivalue but all values are blank... skip it
                                        continue;
                                    }
                                }
                            }
                            String findXpathExpressionPrefix = "//searchingConfig/" + FIELD_DEF_E + "[@name='" + fieldDefName + "']";
        					Node searchDefNode = (Node) xpath.evaluate(findXpathExpressionPrefix + "/searchDefinition", getConfigXML(extensionDefinition), XPathConstants.NODE);
        					NamedNodeMap searchDefAttributes = null;
            				String fieldDataType = null;
        					if (searchDefNode != null) {
            					// get the data type from the xml
        						searchDefAttributes = searchDefNode.getAttributes();
        						if (searchDefAttributes.getNamedItem("dataType") != null) {
            						fieldDataType = searchDefAttributes.getNamedItem("dataType").getNodeValue();
        						}

        					}
        					if (org.apache.commons.lang.StringUtils.isEmpty(fieldDataType)) {
        						fieldDataType = KEWConstants.SearchableAttributeConstants.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
        					}
        					// get the searchable attribute value by using the data type
        					SearchableAttributeValue attributeValue = DocumentLookupInternalUtils
                                    .getSearchableAttributeValueByDataTypeString(fieldDataType);
        					if (attributeValue == null) {
        						String errorMsg = "Cannot find SearchableAttributeValue for field data type '" + fieldDataType + "'";
        						LOG.error("validateUserSearchInputs() " + errorMsg);
        						throw new RuntimeException(errorMsg);
        					}

        					if (rangeMemberInSearchParams) {

                                NamedNodeMap lowerBoundRangeAttributes = null;
                                NamedNodeMap upperBoundRangeAttributes = null;
        						Node rangeDefinitionNode = getPotentialChildNode(searchDefNode, "rangeDefinition");
        						NamedNodeMap rangeDefinitionAttributes = rangeDefinitionNode != null ? rangeDefinitionNode.getAttributes() : null;

                                String lowerBoundValue = null;
                                if (CollectionUtils.isNotEmpty(lowerBoundValues)) {
                                    if (lowerBoundValues.size() > 1) {
                                        throw new WorkflowRuntimeException("Encountered an illegal lower bound with more then one value for field: " + fieldDefName);
                                    }
                                    lowerBoundValue = lowerBoundValues.get(0);
                                }
                                String upperBoundValue = null;
                                if (CollectionUtils.isNotEmpty(upperBoundValues)) {
                                    if (upperBoundValues.size() > 1) {
                                        throw new WorkflowRuntimeException("Encountered an illegal upper bound with more then one value for field: " + fieldDefName);
                                    }
                                    upperBoundValue = upperBoundValues.get(0);
                                }

        						if (StringUtils.isNotBlank(lowerBoundValue)) {
                                    lowerBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "lower");
        							errors.addAll(performValidation(extensionDefinition, attributeValue,
        									lowerBoundFieldDefName, lowerBoundValue, constructRangeFieldErrorPrefix(fieldDefTitle,lowerBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (StringUtils.isNotBlank(upperBoundValue)) {
                                    upperBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "upper");
        							errors.addAll(performValidation(extensionDefinition, attributeValue,
        									upperBoundFieldDefName, upperBoundValue, constructRangeFieldErrorPrefix(fieldDefTitle, upperBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (errors.isEmpty()) {
                                    Boolean rangeValid = attributeValue.isRangeValid(lowerBoundValue, upperBoundValue);
                                    if (rangeValid != null && !rangeValid) {
                                        String lowerLabel = getPotentialRangeBoundLabelFromAttributes(lowerBoundRangeAttributes);
                                        String upperLabel = getPotentialRangeBoundLabelFromAttributes(upperBoundRangeAttributes);
                                        String errorMsg = "The " + fieldDefTitle + " range is incorrect.  The " + (StringUtils.isNotBlank(lowerLabel) ? lowerLabel : KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL) + " value entered must come before the " + (StringUtils.isNotBlank(upperLabel) ? upperLabel : KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL) + " value";
                                        LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
                                        errors.add(RemotableAttributeError.Builder.create(fieldDefName, errorMsg).build());
                                    }
                                }

        					} else {
                                List<String> enteredValue = parameters.get(fieldDefName);
                                if (enteredValue.size() == 1) {
                                    String stringVariable = enteredValue.get(0);
                                    errors.addAll(performValidation(extensionDefinition, attributeValue, fieldDefName, stringVariable, fieldDefTitle, findXpathExpressionPrefix));
                                } else {
                                    for (String stringVariable : enteredValue) {
                                        errors.addAll(performValidation(extensionDefinition, attributeValue, fieldDefName, stringVariable, "One value for " + fieldDefTitle, findXpathExpressionPrefix));
                                    }

                                }
            				}
        				}
                    }
    			}
            }
		} catch (XPathExpressionException e) {
			LOG.error("error in validateUserSearchInputs ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression: " + findField, e);
		}
		return errors;
	}

    private String constructRangeFieldErrorPrefix(String fieldDefLabel, NamedNodeMap rangeBoundAttributes) {
        String potentialLabel = getPotentialRangeBoundLabelFromAttributes(rangeBoundAttributes);
        if ( (StringUtils.isNotBlank(potentialLabel)) && (StringUtils.isNotBlank(fieldDefLabel)) ) {
            return fieldDefLabel + " " + potentialLabel + " Field";
        } else if (StringUtils.isNotBlank(fieldDefLabel)) {
            return fieldDefLabel + " Range Field";
        } else if (StringUtils.isNotBlank(potentialLabel)) {
            return "Range Field " + potentialLabel + " Field";
        }
        return null;
    }

	private List<RemotableAttributeError> performValidation(ExtensionDefinition extensionDefinition, SearchableAttributeValue attributeValue, String fieldDefName, String enteredValue, String errorMessagePrefix, String findXpathExpressionPrefix) throws XPathExpressionException {
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
		XPath xpath = XPathHelper.newXPath();
		if ( attributeValue.allowsWildcards()) {
			enteredValue = enteredValue.replaceAll(KEWConstants.SearchableAttributeConstants.SEARCH_WILDCARD_CHARACTER_REGEX_ESCAPED, "");
		}
		if (!attributeValue.isPassesDefaultValidation(enteredValue)) {
            errorMessagePrefix = (StringUtils.isNotBlank(errorMessagePrefix)) ? errorMessagePrefix : "Field";
			String errorMsg = errorMessagePrefix + " with value '" + enteredValue + "' does not conform to standard validation for field type.";
			LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
			errors.add(RemotableAttributeError.Builder.create(fieldDefName, errorMsg).build());
		} else {
			String findValidation = findXpathExpressionPrefix + "/validation/regex";
			String regex = (String) xpath.evaluate(findValidation, getConfigXML(extensionDefinition), XPathConstants.STRING);
			if (!org.apache.commons.lang.StringUtils.isEmpty(regex)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(enteredValue);
				if (!matcher.matches()) {
					String findErrorMessage = findXpathExpressionPrefix + "/validation/message";
					String message = (String) xpath.evaluate(findErrorMessage, getConfigXML(extensionDefinition), XPathConstants.STRING);
					errors.add(RemotableAttributeError.Builder.create(fieldDefName, message).build());
				}
			}
		}
		return errors;
	}

	public Element getConfigXML(ExtensionDefinition extensionDefinition) {
		try {
            String xmlConfigData = extensionDefinition.getConfiguration().get(RuleAttribute.XML_CONFIG_DATA);
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(xmlConfigData)))).getDocumentElement();
		} catch (Exception e) {
			String ruleAttrStr = (extensionDefinition == null ? null : extensionDefinition.getName());
			LOG.error("error parsing xml data from search attribute: " + ruleAttrStr, e);
			throw new RuntimeException("error parsing xml data from searchable attribute: " + ruleAttrStr, e);
		}
	}

    /**
     * Simple structure for internal usage that includes a case sensitive indicator and label value for one end of
     * a bounded range.
     */
    private static final class RangeBound {
        Boolean inclusive;
        String label;
    }

}
