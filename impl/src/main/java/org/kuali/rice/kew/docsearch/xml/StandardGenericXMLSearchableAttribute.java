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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.xml.XmlJotter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.attribute.XMLAttributeUtils;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * implementation of {@link GenericXMLSearchableAttribute}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardGenericXMLSearchableAttribute implements GenericXMLSearchableAttribute {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardGenericXMLSearchableAttribute.class);

    private static final String FIELD_DEF_E = "fieldDef";

	private Map paramMap = new HashMap();
	private RuleAttribute ruleAttribute;
	private List<Row> searchRows = new ArrayList<Row>();

	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

	public Map getParamMap() {
		return paramMap;
	}

	public String getSearchContent(DocumentSearchContext documentSearchContext) {
		XPath xpath = XPathHelper.newXPath();
		String findDocContent = "//searchingConfig/xmlSearchContent";
		try {
			Node xmlDocumentContent = (Node) xpath.evaluate(findDocContent, getConfigXML(), XPathConstants.NODE);
			if (xmlDocumentContent != null && xmlDocumentContent.hasChildNodes()) {
				// Custom doc content in the searchingConfig xml.
				String docContent = "";
				NodeList customNodes = xmlDocumentContent.getChildNodes();
				for (int i = 0; i < customNodes.getLength(); i++) {
					Node childNode = customNodes.item(i);
					docContent += XmlJotter.jotNode(childNode);
				}
				String findField = "//searchingConfig/" + FIELD_DEF_E;
				NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(), XPathConstants.NODESET);
				if (nodes == null || nodes.getLength() == 0) {
					return "";
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					Node field = nodes.item(i);
					NamedNodeMap fieldAttributes = field.getAttributes();
					if (getParamMap() != null && !org.apache.commons.lang.StringUtils.isEmpty((String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
						docContent = docContent.replaceAll("%" + fieldAttributes.getNamedItem("name").getNodeValue() + "%", (String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()));
					}
				}
				return docContent;
			} else {
				// Standard doc content if no doc content is found in the searchingConfig xml.
				StringBuffer documentContent = new StringBuffer("<xmlRouting>");
				String findField = "//searchingConfig/" + FIELD_DEF_E;
				NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(), XPathConstants.NODESET);
				if (nodes == null || nodes.getLength() == 0) {
					return "";
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					Node field = nodes.item(i);
					NamedNodeMap fieldAttributes = field.getAttributes();
					if (getParamMap() != null && !org.apache.commons.lang.StringUtils.isEmpty((String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
						documentContent.append("<field name=\"");
						documentContent.append(fieldAttributes.getNamedItem("name").getNodeValue());
						documentContent.append("\"><value>");
						documentContent.append((String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()));
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

	public List getSearchStorageValues(DocumentSearchContext documentSearchContext) {
		List<SearchableAttributeValue> searchStorageValues = new ArrayList<SearchableAttributeValue>();
		Document document;
        if (StringUtils.isBlank(documentSearchContext.getDocumentContent())) {
            LOG.warn("Empty Document Content found '" + documentSearchContext.getDocumentContent() + "'");
            return searchStorageValues;
        }
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new BufferedReader(new StringReader(documentSearchContext.getDocumentContent()))));
		} catch (Exception e){
			LOG.error("error parsing docContent: "+documentSearchContext.getDocumentContent(), e);
			throw new RuntimeException("Error trying to parse docContent: "+documentSearchContext.getDocumentContent(), e);
		}
		XPath xpath = XPathHelper.newXPath(document);
		String findField = "//searchingConfig/" + FIELD_DEF_E;
		try {
			NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(), XPathConstants.NODESET);
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
                        fieldDataType = (String) xpath.evaluate(findDataTypeXpathExpression, getConfigXML(), XPathConstants.STRING);
    					if (org.apache.commons.lang.StringUtils.isEmpty(fieldDataType)) {
    						fieldDataType = KEWConstants.SearchableAttributeConstants.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
    					}
    				    xpathExpression = (String) xpath.evaluate(findXpathExpression, getConfigXML(), XPathConstants.STRING);
    					if (!org.apache.commons.lang.StringUtils.isEmpty(xpathExpression)) {

                            try {
                                NodeList searchValues = (NodeList) xpath.evaluate(xpathExpression, document.getDocumentElement(), XPathConstants.NODESET);
                              //being that this is the standard xml attribute we will return the key with an empty value
                                // so we can find it from a doc search using this key
                                if (searchValues.getLength() == 0) {
                                	SearchableAttributeValue searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), null);
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
                                    	SearchableAttributeValue searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), value);
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
                                		new InputSource(new BufferedReader(new StringReader(documentSearchContext.getDocumentContent())))).getDocumentElement(), XPathConstants.STRING);
                                String value = null;
                                if (StringUtils.isNotBlank(searchValue)) {
                                    value = searchValue;
                                }
                            	SearchableAttributeValue searchableValue = this.setupSearchableAttributeValue(fieldDataType, fieldAttributes.getNamedItem("name").getNodeValue(), value);
                            	if (searchableValue != null) {
                                    searchStorageValues.add(searchableValue);
                            	}
                            }
    					}
    				} catch (XPathExpressionException e) {
    					LOG.error("error in isMatch ", e);
    					throw new RuntimeException("Error trying to find xml content with xpath expressions: " + findXpathExpression + " or " + xpathExpression, e);
    				} catch (Exception e){
    					LOG.error("error parsing docContent: "+documentSearchContext.getDocumentContent(), e);
    					throw new RuntimeException("Error trying to parse docContent: "+documentSearchContext.getDocumentContent(), e);
    				}
                }
			}
		} catch (XPathExpressionException e) {
			LOG.error("error in getSearchStorageValues ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression: " + findField, e);
		}
		return searchStorageValues;
	}

	private SearchableAttributeValue setupSearchableAttributeValue(String dataType,String key,String value) {
		SearchableAttributeValue attValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(dataType);
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
    	return attValue;
	}

	public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
		if (searchRows.isEmpty()) {
			List<SearchableAttributeValue> searchableAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
			List<Row> rows = new ArrayList<Row>();
			NodeList fieldNodeList = getConfigXML().getElementsByTagName(FIELD_DEF_E);
			for (int i = 0; i < fieldNodeList.getLength(); i++) {
				Node field = fieldNodeList.item(i);
				NamedNodeMap fieldAttributes = field.getAttributes();

				List<Field> fields = new ArrayList<Field>();
				boolean isColumnVisible = true;
                boolean hasXPathExpression = false;
				Field myField = new Field(fieldAttributes.getNamedItem("name").getNodeValue(), fieldAttributes.getNamedItem("title").getNodeValue());

				String quickfinderService = null;
				// range search details
				Field rangeLowerBoundField = null;
				Field rangeUpperBoundField = null;
				myField.setUpperCase(true); // this defaults us to case insensitive.
				for (int j = 0; j < field.getChildNodes().getLength(); j++) {
					Node childNode = field.getChildNodes().item(j);
					if ("value".equals(childNode.getNodeName())) {
						myField.setPropertyValue(childNode.getFirstChild().getNodeValue());
					} else if ("display".equals(childNode.getNodeName())) {
						List<KeyValue> options = new ArrayList<KeyValue>();
                        List<String> selectedOptions = new ArrayList<String>();
						for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
							Node displayChildNode = childNode.getChildNodes().item(k);
							if ("type".equals(displayChildNode.getNodeName())) {
								String typeValue = displayChildNode.getFirstChild().getNodeValue();
								myField.setFieldType(convertTypeToFieldType(typeValue));
								if ("date".equals(typeValue)) {
									myField.setDatePicker(Boolean.TRUE);
									myField.setFieldDataType(KEWConstants.SearchableAttributeConstants.DATA_TYPE_DATE);
								}
							} else if ("meta".equals(displayChildNode.getNodeName())) {

							} else if ("values".equals(displayChildNode.getNodeName())) {
								NamedNodeMap valuesAttributes = displayChildNode.getAttributes();
//                              this is to allow an empty drop down choice and can probably implemented in a better way
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
						if (!options.isEmpty()) {
							myField.setFieldValidValues(options);
                            if (!selectedOptions.isEmpty()) {
                                if (Field.MULTI_VALUE_FIELD_TYPES.contains(myField.getFieldType())) {
                                    String[] newSelectedOptions = new String[selectedOptions.size()];
                                    int k = 0;
                                    for (String option : selectedOptions)
                                    {
                                        newSelectedOptions[k] = option;
                                        k++;
                                    }
                                    myField.setPropertyValues(newSelectedOptions);
                                } else {
                                    myField.setPropertyValue((String)selectedOptions.get(0));
                                }
                            }
						}
					} else if ("visibility".equals(childNode.getNodeName())) {
						parseVisibility(myField, (Element)childNode);
					} else if ("searchDefinition".equals(childNode.getNodeName())) {
						NamedNodeMap searchDefAttributes = childNode.getAttributes();
						// data type operations
						String dataType = (searchDefAttributes.getNamedItem("dataType") == null) ? null : searchDefAttributes.getNamedItem("dataType").getNodeValue();
						if (!org.apache.commons.lang.StringUtils.isEmpty(dataType)) {
							myField.setFieldDataType(dataType);
						} else {
							// no data type means we default to String which disallows range search
							myField.setFieldDataType(KEWConstants.SearchableAttributeConstants.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME);
						}
						if (KEWConstants.SearchableAttributeConstants.DATA_TYPE_DATE.equalsIgnoreCase(myField.getFieldDataType())) {
							myField.setDatePicker(Boolean.TRUE);
						}
						//if () {
						//    myField.setFormatter((Formatter) formatterClass.newInstance());
						//}

						// figure out if this is a range search
						myField.setMemberOfRange(isRangeSearchField(searchableAttributeValues, myField.getFieldDataType(), searchDefAttributes, childNode));
						if (!myField.isMemberOfRange()) {
							Boolean caseSensitive = getBooleanValue(searchDefAttributes, "caseSensitive");
							if (caseSensitive == null) {
								caseSensitive = false; // we mimmic the KNS. KNS is case insensitive by default
							}
							myField.setUpperCase(!caseSensitive);
						} else {
    						// by now we know we have a range that uses the default values at least
    						// these will be
    						rangeLowerBoundField = new Field("", KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL);
    						rangeLowerBoundField.setMemberOfRange(true);
    						rangeUpperBoundField = new Field("", KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL);
    						rangeUpperBoundField.setMemberOfRange(true);
    						setupBoundFields(childNode, rangeLowerBoundField, rangeUpperBoundField);
                        }

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

					} else if ("resultColumn".equals(childNode.getNodeName())) {
						NamedNodeMap columnAttributes = childNode.getAttributes();
						Node showNode = columnAttributes.getNamedItem("show");
						if (showNode != null && showNode.getNodeValue() != null) {
							isColumnVisible = Boolean.valueOf(showNode.getNodeValue());
						}
						myField.setColumnVisible(isColumnVisible);
                    } else if ("fieldEvaluation".equals(childNode.getNodeName())) {
                        for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                            Node displayChildNode = childNode.getChildNodes().item(k);
                            if ("xpathexpression".equals(displayChildNode.getNodeName())) {
                                hasXPathExpression = true;
                                break;
                            }
                        }
					} else if ("lookup".equals(childNode.getNodeName())) {
						XMLAttributeUtils.establishFieldLookup(myField, childNode);
					}
				}
                myField.setIndexedForSearch(hasXPathExpression);

				if (myField.isMemberOfRange()) {
					// we have a ranged search... we need to add the bound fields and NOT the myField object
					addRangeFields(KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX, rangeLowerBoundField, myField, rows, quickfinderService);
					addRangeFields(KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX, rangeUpperBoundField, myField, rows, quickfinderService);
				} else {
					fields.add(myField);
					if (!myField.getFieldType().equals(Field.HIDDEN)) {
						if (myField.isDatePicker()) {
							addDatePickerField(fields, myField.getPropertyName());
						}
					}
					rows.add(new Row(fields));
				}
			}
			searchRows = rows;
		}
		return searchRows;
	}

    private boolean isRangeSearchField(List<SearchableAttributeValue> searchableAttributeValues, String dataType, NamedNodeMap searchDefAttributes, Node searchDefNode) {
        for (SearchableAttributeValue attValue : searchableAttributeValues)
        {
            if (dataType.equalsIgnoreCase(attValue.getAttributeDataType()))
            {
                return isRangeSearchField(attValue, dataType, searchDefAttributes, searchDefNode);
            }
        }
        String errorMsg = "Could not find searchable attribute value for data type '" + dataType + "'";
        LOG.error("isRangeSearchField(List, String, NamedNodeMap, Node) " + errorMsg);
        throw new RuntimeException(errorMsg);
    }

    private boolean isRangeSearchField(SearchableAttributeValue searchableAttributeValue, String dataType, NamedNodeMap searchDefAttributes, Node searchDefNode) {
        boolean allowRangedSearch = searchableAttributeValue.allowsRangeSearches();
        Boolean rangeSearchBoolean = getBooleanValue(searchDefAttributes, "rangeSearch");
        boolean rangeSearch = (rangeSearchBoolean != null) && rangeSearchBoolean;
        Node rangeDefinition = getPotentialChildNode(searchDefNode, "rangeDefinition");
        return ( (allowRangedSearch) && ((rangeDefinition != null) || (rangeSearch)) );
    }

    private void setupBoundFields(Node searchDefinitionNode, Field lowerBoundField, Field upperBoundField) {
        NamedNodeMap searchDefAttributes = searchDefinitionNode.getAttributes();
        Node rangeDefinitionNode = getPotentialChildNode(searchDefinitionNode, "rangeDefinition");
        NamedNodeMap rangeDefinitionAttributes = null;
        NamedNodeMap lowerBoundNodeAttributes = null;
        NamedNodeMap upperBoundNodeAttributes = null;
        if (rangeDefinitionNode != null) {
            rangeDefinitionAttributes = rangeDefinitionNode.getAttributes();
            lowerBoundNodeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "lower");
            upperBoundNodeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "upper");
        }
        // below methods allow for nullable attribute NamedNodeMaps
        setupRangeBoundFieldOverridableSettings(searchDefAttributes, rangeDefinitionAttributes, lowerBoundNodeAttributes, lowerBoundField);
        setupRangeBoundFieldOverridableSettings(searchDefAttributes, rangeDefinitionAttributes, upperBoundNodeAttributes, upperBoundField);
    }

	private void addRangeFields(String propertyPrefix, Field rangeBoundField,Field mainField, List<Row> rows,String quickfinderService) {
		List<Field> rangeFields = new ArrayList<Field>();
		rangeBoundField.setColumnVisible(mainField.isColumnVisible());
		rangeBoundField.setFieldDataType(mainField.getFieldDataType());
		rangeBoundField.setFieldHelpUrl(mainField.getFieldHelpUrl());
		rangeBoundField.setFieldType(mainField.getFieldType());
        rangeBoundField.setMainFieldLabel(mainField.getFieldLabel());
		rangeBoundField.setFieldValidValues(mainField.getFieldValidValues());
		rangeBoundField.setPropertyName(propertyPrefix + mainField.getPropertyName());
		rangeBoundField.setQuickFinderClassNameImpl(mainField.getQuickFinderClassNameImpl());
		//rangeBoundField.setDefaultLookupableName(mainField.getDefaultLookupableName());
		rangeFields.add(rangeBoundField);
		if (!mainField.getFieldType().equals(Field.HIDDEN)) {
			// disabling the additional quickfinder field for now, should be included as a single field in the KNS instead of 2 as it was in KEW
//			if (!org.apache.commons.lang.StringUtils.isEmpty(quickfinderService)) {
//				rangeFields.add(new Field("", "", Field.QUICKFINDER, "", "", null, quickfinderService));
//			}
			if (rangeBoundField.isDatePicker()) {
				// variable was set on the bound field
				if (rangeBoundField.isDatePicker()) {
					addDatePickerField(rangeFields, rangeBoundField.getPropertyName());
				}
			} else {
				if (mainField.isDatePicker()) {
					addDatePickerField(rangeFields, rangeBoundField.getPropertyName());
				}
			}
		}
		rows.add(new Row(rangeFields));
	}

    private void addDatePickerField(List<Field> fields,String propertyName) {
        Field Field = new Field(propertyName,"");
        Field.setDatePicker(true);
		fields.add(Field);
    }

	private NamedNodeMap getAttributesForPotentialChildNode(Node node, String potentialChildNodeName) {
		Node testNode = getPotentialChildNode(node, potentialChildNodeName);
		return (testNode != null) ? testNode.getAttributes() : null;
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

	private void setupRangeBoundFieldOverridableSettings(NamedNodeMap searchDefinitionAttributes,NamedNodeMap rangeDefinitionAttributes,NamedNodeMap rangeBoundAttributes,Field boundField) {
        String potentialLabel = getPotentialRangeBoundLabelFromAttributes(rangeBoundAttributes);
        if (StringUtils.isNotBlank(potentialLabel)) {
            boundField.setFieldLabel(potentialLabel);
        }
		ArrayList<NamedNodeMap> namedNodeMapsByImportance = new ArrayList<NamedNodeMap>();
		namedNodeMapsByImportance.add(rangeBoundAttributes);
		namedNodeMapsByImportance.add(rangeDefinitionAttributes);
		namedNodeMapsByImportance.add(searchDefinitionAttributes);
		Boolean caseSensitive = getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "caseSensitive");
		if (caseSensitive == null) {
			caseSensitive = false; // we mimmic the KNS. KNS is case insensitive by default
		}
		boundField.setUpperCase(!caseSensitive);
		// TODO: after face-to-face work in december 2008, this was throwing a nullpointerexception for lookups with date pickers
		// assuming this code will go away after the document search conversion
		Boolean datePickerBoolean = getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "datePicker");
		if (datePickerBoolean == null) {
			datePickerBoolean = false;
		}
		boundField.setDatePicker(datePickerBoolean);
		boundField.setRangeFieldInclusive(getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "inclusive"));

	}

    private String getPotentialRangeBoundLabelFromAttributes(NamedNodeMap rangeBoundAttributes) {
        if (rangeBoundAttributes != null) {
            String boundLabel = (rangeBoundAttributes.getNamedItem("label") == null) ? null : rangeBoundAttributes.getNamedItem("label").getNodeValue();
            if (!org.apache.commons.lang.StringUtils.isEmpty(boundLabel)) {
                return boundLabel;
            }
        }
        return null;
    }

	private Boolean getBooleanWithPotentialOverrides(String attributeName,NamedNodeMap searchDefinitionAttributes,NamedNodeMap rangeDefinitionAttributes,NamedNodeMap rangeBoundAttributes) {
		ArrayList<NamedNodeMap> namedNodeMapsByImportance = new ArrayList<NamedNodeMap>();
		namedNodeMapsByImportance.add(rangeBoundAttributes);
		namedNodeMapsByImportance.add(rangeDefinitionAttributes);
		namedNodeMapsByImportance.add(searchDefinitionAttributes);
		return getBooleanWithPotentialOverrides(namedNodeMapsByImportance, attributeName);
	}

	private Boolean getBooleanWithPotentialOverrides(ArrayList<NamedNodeMap> namedNodeMapsByImportance, String attributeName) {
        for (NamedNodeMap aNamedNodeMapsByImportance : namedNodeMapsByImportance)
        {
            NamedNodeMap nodeMap = (NamedNodeMap) aNamedNodeMapsByImportance;
            Boolean booleanValue = getBooleanValue(nodeMap, attributeName);
            if (booleanValue != null)
            {
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
		return ( (nodeMap == null) || (nodeMap.getNamedItem(attributeName) == null) || (org.apache.commons.lang.StringUtils.isEmpty(nodeMap.getNamedItem(attributeName).getNodeValue())) ) ? null : nodeMap.getNamedItem(attributeName).getNodeValue();
	}

	private void parseVisibility(Field field, Element visibilityElement) {
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
    							visible = KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(session.getPerson().getPrincipalId(), groupNamespace, groupName);
    						}
                        }
					}
				}
				String type = visibilityChildNode.getNodeName();
				if ("field".equals(type) || "fieldAndColumn".equals(type)) {
					// if it's not visible, coerce this field to a hidden type
					if (!visible) {
						field.setFieldType(Field.HIDDEN);
					}
				}
				if ("column".equals(type) || "fieldAndColumn".equals(type)) {
					field.setColumnVisible(visible);
				}
			}

		}
	}

	private String convertTypeToFieldType(String type) {
		if ("text".equals(type)) {
			return Field.TEXT;
		} else if ("select".equals(type)) {
			return Field.DROPDOWN;
		} else if ("radio".equals(type)) {
			return Field.RADIO;
		} else if ("quickfinder".equals(type)) {
			return Field.QUICKFINDER;
		} else if ("hidden".equals(type)) {
			return Field.HIDDEN;
		} else if ("date".equals(type)) {
			return Field.TEXT;
        } else if ("multibox".equals(type)) {
            return Field.MULTIBOX;
        }
		throw new IllegalArgumentException("Illegal field type found: " + type);
	}

	public List validateUserSearchInputs(Map paramMap, DocumentSearchContext documentSearchContext) {
		this.paramMap = paramMap;
		List<WorkflowAttributeValidationError> errors = new ArrayList<WorkflowAttributeValidationError>();

		XPath xpath = XPathHelper.newXPath();
		String findField = "//searchingConfig/" + FIELD_DEF_E;
		try {
			NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(), XPathConstants.NODESET);
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
                    if (getParamMap() != null) {
                        Object lowerObj = getParamMap().get(KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldDefName);
                        if ( (lowerObj != null) && (lowerObj instanceof String) ) {
                            rangeMemberInSearchParams |= StringUtils.isNotBlank((String) lowerObj);
                        }
                        Object upperObj = getParamMap().get(KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldDefName);
                        if ( (upperObj != null) && (upperObj instanceof String) ) {
                            rangeMemberInSearchParams |= StringUtils.isNotBlank((String) upperObj);
                        }
                        Object testObject = getParamMap().get(fieldDefName);
    					if ( (testObject != null) || rangeMemberInSearchParams ) {
                            // check to see if we need to process this field at all
                            if (!rangeMemberInSearchParams) {
                                if (testObject instanceof String) {
                                    String stringVariable = (String) testObject;
                                    if (StringUtils.isBlank(stringVariable)) {
                                        // field is not multi value and is empty... skip it
                                        continue;
                                    }
                                } else if (testObject instanceof Collection) {
                                    Collection stringVariables = (Collection<String>)testObject;
                                    boolean allAreBlank = true;
                                    for (Iterator iter = stringVariables.iterator(); iter.hasNext();) {
                                        String testString = (String) iter.next();
                                        if (StringUtils.isNotBlank(testString)) {
                                            allAreBlank = false;
                                            break;
                                        }
                                    }
                                    if (allAreBlank) {
                                        // field is multivalue but all values are blank... skip it
                                        continue;
                                    }
                                } else {
                                    String errorMessage = "Only String or String[] objects should come from entered parameters of an attribute. Current parameter is '" + testObject.getClass() + "'";
                                    LOG.error(errorMessage);
                                    throw new RuntimeException(errorMessage);
                                }
                            }
                            String findXpathExpressionPrefix = "//searchingConfig/" + FIELD_DEF_E + "[@name='" + fieldDefName + "']";
        					Node searchDefNode = (Node) xpath.evaluate(findXpathExpressionPrefix + "/searchDefinition", getConfigXML(), XPathConstants.NODE);
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
        					SearchableAttributeValue attributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(fieldDataType);
        					if (attributeValue == null) {
        						String errorMsg = "Cannot find SearchableAttributeValue for field data type '" + fieldDataType + "'";
        						LOG.error("validateUserSearchInputs() " + errorMsg);
        						throw new RuntimeException(errorMsg);
        					}

        					if (rangeMemberInSearchParams) {
                                String lowerBoundFieldDefName = KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldDefName;
                                String upperBoundFieldDefName = KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldDefName;
                                String lowerBoundEnteredValue = null;
                                String upperBoundEnteredValue = null;
                                NamedNodeMap lowerBoundRangeAttributes = null;
                                NamedNodeMap upperBoundRangeAttributes = null;
        						Node rangeDefinitionNode = getPotentialChildNode(searchDefNode, "rangeDefinition");
        						NamedNodeMap rangeDefinitionAttributes = (rangeDefinitionNode != null) ? rangeDefinitionNode.getAttributes() : null;
        						lowerBoundEnteredValue = (String) getParamMap().get(lowerBoundFieldDefName);
        						upperBoundEnteredValue = (String) getParamMap().get(upperBoundFieldDefName);
        						if (!org.apache.commons.lang.StringUtils.isEmpty(lowerBoundEnteredValue)) {
                                    lowerBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "lower");
        							errors.addAll(performValidation(attributeValue,
        									lowerBoundFieldDefName, lowerBoundEnteredValue, constructRangeFieldErrorPrefix(fieldDefTitle,lowerBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (!org.apache.commons.lang.StringUtils.isEmpty(upperBoundEnteredValue)) {
                                    upperBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "upper");
        							errors.addAll(performValidation(attributeValue,
        									upperBoundFieldDefName, upperBoundEnteredValue, constructRangeFieldErrorPrefix(fieldDefTitle,upperBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (errors.isEmpty()) {
                                    Boolean rangeValid = attributeValue.isRangeValid(lowerBoundEnteredValue, upperBoundEnteredValue);
                                    if ( (rangeValid != null) && (!rangeValid) ) {
                                        String lowerLabel = getPotentialRangeBoundLabelFromAttributes(lowerBoundRangeAttributes);
                                        String upperLabel = getPotentialRangeBoundLabelFromAttributes(upperBoundRangeAttributes);
                                        String errorMsg = "The " + fieldDefTitle + " range is incorrect.  The " + (StringUtils.isNotBlank(lowerLabel) ? lowerLabel : KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL) + " value entered must come before the " + (StringUtils.isNotBlank(upperLabel) ? upperLabel : KEWConstants.SearchableAttributeConstants.DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL) + " value";
                                        LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
                                        errors.add(new WorkflowAttributeValidationError(fieldDefName, errorMsg));
                                    }
                                }

        					} else {
                                Object enteredValue = getParamMap().get(fieldDefName);
                                if (enteredValue instanceof String) {
                                    String stringVariable = (String) enteredValue;
                                    errors.addAll(performValidation(attributeValue, fieldDefName, stringVariable, fieldDefTitle, findXpathExpressionPrefix));
                                } else if (enteredValue instanceof Collection) {
                                    Collection stringVariables = (Collection<String>)enteredValue;
                                    for (Iterator iter = stringVariables.iterator(); iter.hasNext();) {
                                        String stringVariable = (String) iter.next();
                                        errors.addAll(performValidation(attributeValue, fieldDefName, stringVariable, "One value for " + fieldDefTitle, findXpathExpressionPrefix));
                                    }

                                } else {
                                    String errorMessage = "Only String or String[] objects should come from entered parameters of an attribute.";
                                    LOG.error(errorMessage);
                                    throw new RuntimeException(errorMessage);
                                }
            				}
        				} else {
//        				    String findValidation = "//searchingConfig/field[@name='" + fieldAttributes.getNamedItem("name").getNodeValue() + "']/validation";
//        				    Node validation = (Node) xpath.evaluate(findValidation, getConfigXML(), XPathConstants.NODE);
//        				    if (validation != null) {
//        				        NamedNodeMap validationAttributes = validation.getAttributes();
//        				        Node required = validationAttributes.getNamedItem("required");
//        				        if (required != null && "true".equalsIgnoreCase(required.getNodeValue())) {
//        				            errors.add(new WorkflowAttributeValidationError(fieldAttributes.getNamedItem("name").getNodeValue(),fieldAttributes.getNamedItem("title").getNodeValue()+" is required."));
//        				        }
//                            }
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

	private List<WorkflowAttributeValidationError> performValidation(SearchableAttributeValue attributeValue, String fieldDefName, String enteredValue, String errorMessagePrefix, String findXpathExpressionPrefix) throws XPathExpressionException {
		List<WorkflowAttributeValidationError> errors = new ArrayList<WorkflowAttributeValidationError>();
		XPath xpath = XPathHelper.newXPath();
		if ( attributeValue.allowsWildcards()) {
			enteredValue = enteredValue.replaceAll(KEWConstants.SearchableAttributeConstants.SEARCH_WILDCARD_CHARACTER_REGEX_ESCAPED, "");
		}
		if (!attributeValue.isPassesDefaultValidation(enteredValue)) {
            errorMessagePrefix = (StringUtils.isNotBlank(errorMessagePrefix)) ? errorMessagePrefix : "Field";
			String errorMsg = errorMessagePrefix + " with value '" + enteredValue + "' does not conform to standard validation for field type.";
			LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
			errors.add(new WorkflowAttributeValidationError(fieldDefName, errorMsg));
		} else {
			String findValidation = findXpathExpressionPrefix + "/validation/regex";
			String regex = (String) xpath.evaluate(findValidation, getConfigXML(), XPathConstants.STRING);
			if (!org.apache.commons.lang.StringUtils.isEmpty(regex)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(enteredValue);
				if (!matcher.matches()) {
					String findErrorMessage = findXpathExpressionPrefix + "/validation/message";
					String message = (String) xpath.evaluate(findErrorMessage, getConfigXML(), XPathConstants.STRING);
					errors.add(new WorkflowAttributeValidationError(fieldDefName, message));
				}
			}
		}
		return errors;
	}

	public Element getConfigXML() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(ruleAttribute.getXmlConfigData())))).getDocumentElement();
		} catch (Exception e) {
			String ruleAttrStr = (ruleAttribute == null ? null : ruleAttribute.getName());
			LOG.error("error parsing xml data from search attribute: " + ruleAttrStr, e);
			throw new RuntimeException("error parsing xml data from searchable attribute: " + ruleAttrStr, e);
		}
	}
}
