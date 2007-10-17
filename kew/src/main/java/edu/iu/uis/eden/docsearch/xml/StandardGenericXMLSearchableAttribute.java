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
package edu.iu.uis.eden.docsearch.xml;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.docsearch.DocSearchUtils;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * implementation of {@link GenericXMLSearchableAttribute}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandardGenericXMLSearchableAttribute implements GenericXMLSearchableAttribute {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardGenericXMLSearchableAttribute.class);

    private static final String FIELD_DEF_E = "fieldDef";
    
	private Map paramMap = new HashMap();
	private RuleAttribute ruleAttribute;
	private List searchRows = new ArrayList();

	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

	public Map getParamMap() {
		return paramMap;
	}

	public String getSearchContent() {
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
					docContent += XmlHelper.writeNode(childNode);
				}
				String findField = "//searchingConfig/" + FIELD_DEF_E;
				NodeList nodes = (NodeList) xpath.evaluate(findField, getConfigXML(), XPathConstants.NODESET);
				if (nodes == null || nodes.getLength() == 0) {
					return "";
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					Node field = nodes.item(i);
					NamedNodeMap fieldAttributes = field.getAttributes();
					if (getParamMap() != null && !Utilities.isEmpty((String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
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
					if (getParamMap() != null && !Utilities.isEmpty((String) getParamMap().get(fieldAttributes.getNamedItem("name").getNodeValue()))) {
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

	public List getSearchStorageValues(String docContent) {
		List searchStorageValues = new ArrayList();
		Document document;
        if (StringUtils.isBlank(docContent)) {
            LOG.warn("Empty Document Content found '" + docContent + "'");
            return searchStorageValues;
        }
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(docContent))));
		} catch (Exception e){
			LOG.error("error parsing docContent: "+docContent, e);
			throw new RuntimeException("Error trying to parse docContent: "+docContent, e);
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
    					if (Utilities.isEmpty(fieldDataType)) {
    						fieldDataType = DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
    					}
    				    xpathExpression = (String) xpath.evaluate(findXpathExpression, getConfigXML(), XPathConstants.STRING);
    					if (!Utilities.isEmpty(xpathExpression)) {
                            
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
                                String searchValue = (String) xpath.evaluate(xpathExpression, DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(docContent)))).getDocumentElement(), XPathConstants.STRING);
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
    					LOG.error("error parsing docContent: "+docContent, e);
    					throw new RuntimeException("Error trying to parse docContent: "+docContent, e);
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
        // TODO delyea - should we be saving null values??
        value = (value != null) ? value.trim() : null;
        // TODO delyea - should we use differing validation for UI vs Data Saving here?
        if ( (StringUtils.isNotBlank(value)) && (!attValue.isPassesDefaultValidation(value)) ) {
            String errorMsg = "SearchableAttributeValue with the data type '" + dataType + "', key '" + key + "', and value '" + value + "' does not pass default validation and cannot be saved to the database";
            LOG.error("setupSearchableAttributeValue() " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
		attValue.setSearchableAttributeKey(key);
		attValue.setupAttributeValue(value);
    	return attValue;
	}

	public List getSearchingRows() {
		if (searchRows.isEmpty()) {
			List searchableAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
			List rows = new ArrayList();
			NodeList fieldNodeList = getConfigXML().getElementsByTagName(FIELD_DEF_E);
			for (int i = 0; i < fieldNodeList.getLength(); i++) {
				Node field = fieldNodeList.item(i);
				NamedNodeMap fieldAttributes = field.getAttributes();

				List fields = new ArrayList();
				boolean isColumnVisible = true;
                boolean hasXPathExpression = false;
				Field myField = new Field(fieldAttributes.getNamedItem("title").getNodeValue(), "", "", false, fieldAttributes.getNamedItem("name").getNodeValue(), "", null, "");
				String quickfinderService = null;
				// range search details
				Field rangeLowerBoundField = null;
				Field rangeUpperBoundField = null;
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
								String typeValue = displayChildNode.getFirstChild().getNodeValue();
								myField.setFieldType(convertTypeToFieldType(typeValue));
								if ("date".equals(typeValue)) {
									myField.setHasDatePicker(Boolean.TRUE);
									myField.setFieldDataType(DATA_TYPE_DATE);
								}
							} else if ("meta".equals(displayChildNode.getNodeName())) {

							} else if ("values".equals(displayChildNode.getNodeName())) {
								NamedNodeMap valuesAttributes = displayChildNode.getAttributes();
//                              this is to allow an empty drop down choice and can probably implemented in a better way
                                if (displayChildNode.getFirstChild() != null) { 
                                    options.add(new KeyLabelPair(displayChildNode.getFirstChild().getNodeValue(), valuesAttributes.getNamedItem("title").getNodeValue()));    
                                    if (valuesAttributes.getNamedItem("selected") != null) {
                                        selectedOptions.add(displayChildNode.getFirstChild().getNodeValue());
                                    }
                                } else {
                                    options.add(new KeyLabelPair("", valuesAttributes.getNamedItem("title").getNodeValue()));
                                }
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
					} else if ("visibility".equals(childNode.getNodeName())) {
						parseVisibility(myField, (Element)childNode);
					} else if ("searchDefinition".equals(childNode.getNodeName())) {
						NamedNodeMap searchDefAttributes = childNode.getAttributes();
						// data type operations
						String dataType = (searchDefAttributes.getNamedItem("dataType") == null) ? null : searchDefAttributes.getNamedItem("dataType").getNodeValue();
						if (!Utilities.isEmpty(dataType)) {
							myField.setFieldDataType(dataType);
						} else {
							// no data type means we default to String which disallows range search
							myField.setFieldDataType(DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME);
						}
						if (DATA_TYPE_DATE.equalsIgnoreCase(myField.getFieldDataType())) {
							myField.setHasDatePicker(Boolean.TRUE);
						}
						
						// figure out if this is a range search
						myField.setMemberOfRange(isRangeSearchField(searchableAttributeValues, myField.getFieldDataType(), searchDefAttributes, childNode));
						if (!myField.isMemberOfRange()) {
							// either we don't allow a range search... or the range search doesn't exist in the xml
							myField.setAllowWildcards(getBooleanValue(searchDefAttributes, "allowWildcards"));
							myField.setCaseSensitive(getBooleanValue(searchDefAttributes, "caseSensitive"));
							String autoWildcardAttributeValue = (searchDefAttributes.getNamedItem("autoWildcardLocation") == null) ? null : searchDefAttributes.getNamedItem("autoWildcardLocation").getNodeValue();
							setupAutoWildcardPolicy(myField,autoWildcardAttributeValue);
						} else {
    						// by now we know we have a range that uses the default values at least
    						// these will be 
    						rangeLowerBoundField = new Field(DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL,"","",false,"","",null,"");
    						rangeLowerBoundField.setMemberOfRange(true);
    						rangeUpperBoundField = new Field(DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL,"","",false,"","",null,"");
    						rangeUpperBoundField.setMemberOfRange(true);
    						setupBoundFields(childNode, rangeLowerBoundField, rangeUpperBoundField);
                        }
					} else if ("resultColumn".equals(childNode.getNodeName())) {
						NamedNodeMap columnAttributes = childNode.getAttributes();
						Node showNode = columnAttributes.getNamedItem("show");
						if (showNode != null && showNode.getNodeValue() != null) {
							isColumnVisible = Boolean.valueOf(showNode.getNodeValue()).booleanValue();
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
                myField.setSearchable(hasXPathExpression);

				if (myField.isMemberOfRange()) {
					// we have a ranged search... we need to add the bound fields and NOT the myField object
					addRangeFields(RANGE_LOWER_BOUND_PROPERTY_PREFIX, rangeLowerBoundField, myField, rows, quickfinderService);
					addRangeFields(RANGE_UPPER_BOUND_PROPERTY_PREFIX, rangeUpperBoundField, myField, rows, quickfinderService);
				} else {
					fields.add(myField);
					if (!myField.getFieldType().equals(Field.HIDDEN)) {
						if (!Utilities.isEmpty(quickfinderService)) {
							fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, quickfinderService));
						}
						if (myField.isUsingDatePicker()) {
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
	
    private boolean isRangeSearchField(List searchableAttributeValues, String dataType, NamedNodeMap searchDefAttributes, Node searchDefNode) {
        for (Iterator iter = searchableAttributeValues.iterator(); iter.hasNext();) {
            SearchableAttributeValue attValue = (SearchableAttributeValue) iter.next();
            if (dataType.equalsIgnoreCase(attValue.getAttributeDataType())) {
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
        boolean rangeSearch = (rangeSearchBoolean != null) ? rangeSearchBoolean.booleanValue() : false;
        Node rangeDefinition = searchDefNode.getFirstChild();
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
    
	private void addRangeFields(String propertyPrefix,Field rangeBoundField,Field mainField,List rows,String quickfinderService) {
		List rangeFields = new ArrayList();
		rangeBoundField.setColumnVisible(mainField.isColumnVisible());
		rangeBoundField.setFieldDataType(mainField.getFieldDataType());
		rangeBoundField.setFieldHelpUrl(mainField.getFieldHelpUrl());
		rangeBoundField.setFieldType(mainField.getFieldType());
        rangeBoundField.setMainFieldLabel(mainField.getFieldLabel());
		rangeBoundField.setFieldValidValues(mainField.getFieldValidValues());
		rangeBoundField.setPropertyName(propertyPrefix + mainField.getPropertyName());
		rangeBoundField.setSavablePropertyName(mainField.getPropertyName());
		rangeBoundField.setQuickFinderClassNameImpl(mainField.getQuickFinderClassNameImpl());
		rangeBoundField.setHasLookupable(mainField.isHasLookupable());
		rangeBoundField.setDefaultLookupableName(mainField.getDefaultLookupableName());
		rangeFields.add(rangeBoundField);
		if (!mainField.getFieldType().equals(Field.HIDDEN)) {
			if (!Utilities.isEmpty(quickfinderService)) {
				rangeFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, quickfinderService));
			}
			if (rangeBoundField.getHasDatePicker() != null) {
				// variable was set on the bound field
				if (rangeBoundField.isUsingDatePicker()) {
					addDatePickerField(rangeFields, rangeBoundField.getPropertyName());
				}
			} else {
				if (mainField.isUsingDatePicker()) {
					addDatePickerField(rangeFields, rangeBoundField.getPropertyName());
				}
			}
		}
		rows.add(new Row(rangeFields,mainField.getFieldLabel(),2));
	}
	
    private void addDatePickerField(List fields,String propertyName) {
        fields.add(new Field("", "", Field.DATEPICKER, false, propertyName + "_datepicker", "", null, ""));
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
		boundField.setAllowWildcards(getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "allowWildcards"));
		boundField.setCaseSensitive(getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "caseSensitive"));
		boundField.setHasDatePicker(getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "datePicker"));
		boundField.setRangeFieldInclusive(getBooleanWithPotentialOverrides(namedNodeMapsByImportance, "inclusive"));
		
		String autoWildcardValueToUse = null;
		for (int i = 0; i < namedNodeMapsByImportance.size(); i++) {
			NamedNodeMap nodeMap = (NamedNodeMap)namedNodeMapsByImportance.get(i);
			String attributeValue = getStringValue(nodeMap, "autoWildcardLocation");
			if (attributeValue != null) {
				autoWildcardValueToUse = attributeValue;
				break;
			}
		}
		setupAutoWildcardPolicy(boundField,autoWildcardValueToUse);
	}
    
    private String getPotentialRangeBoundLabelFromAttributes(NamedNodeMap rangeBoundAttributes) {
        if (rangeBoundAttributes != null) {
            String boundLabel = (rangeBoundAttributes.getNamedItem("label") == null) ? null : rangeBoundAttributes.getNamedItem("label").getNodeValue();
            if (!Utilities.isEmpty(boundLabel)) {
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
		for (int i = 0; i < namedNodeMapsByImportance.size(); i++) {
			NamedNodeMap nodeMap = (NamedNodeMap)namedNodeMapsByImportance.get(i);
			Boolean booleanValue = getBooleanValue(nodeMap, attributeName);
			if (booleanValue != null) {
				return booleanValue;
			}
		}
		return null;
	}

	private void setupAutoWildcardPolicy(Field field,String attributeValue) {
		if (attributeValue == null) {
			return;
		}
		if (attributeValue.trim().equalsIgnoreCase("prefixonly")) {
			field.setAutoWildcardBeginning(Boolean.TRUE);
			field.setAutoWildcardEnding(Boolean.FALSE);
			return;
		} else if (attributeValue.trim().equalsIgnoreCase("suffixonly")) {
			field.setAutoWildcardBeginning(Boolean.FALSE);
			field.setAutoWildcardEnding(Boolean.TRUE);
			return;
		} else if (attributeValue.trim().equalsIgnoreCase("bothsides")) {
			field.setAutoWildcardBeginning(Boolean.TRUE);
			field.setAutoWildcardEnding(Boolean.TRUE);
			return;
		} else if (attributeValue.trim().equalsIgnoreCase("none")) {
			field.setAutoWildcardBeginning(Boolean.FALSE);
			field.setAutoWildcardEnding(Boolean.FALSE);
			return;
		}
		throw new IllegalArgumentException("Illegal auto wildcard value being used: " + attributeValue.trim());
	}
	
	private Boolean getBooleanValue(NamedNodeMap nodeMap, String attributeName) {
		String nodeValue = getStringValue(nodeMap, attributeName);
		if (nodeValue != null) {
			return Boolean.valueOf(nodeValue);
		}
		return null;
	}
	
	private String getStringValue(NamedNodeMap nodeMap, String attributeName) {
		return ( (nodeMap == null) || (nodeMap.getNamedItem(attributeName) == null) || (Utilities.isEmpty(nodeMap.getNamedItem(attributeName).getNodeValue())) ) ? null : nodeMap.getNamedItem(attributeName).getNodeValue();
	}
	
	private void parseVisibility(Field field, Element visibilityElement) {
		for (int vIndex = 0; vIndex < visibilityElement.getChildNodes().getLength(); vIndex++) {
			Node visibilityChildNode = visibilityElement.getChildNodes().item(vIndex);
			if (visibilityChildNode.getNodeType() == Node.ELEMENT_NODE) {
				boolean visible = true;
				NamedNodeMap visibilityAttributes = visibilityChildNode.getAttributes();
				Node visibleNode = visibilityAttributes.getNamedItem("visible");
				if (visibleNode != null && visibleNode.getNodeValue() != null) {
					visible = Boolean.valueOf(visibleNode.getNodeValue()).booleanValue();
				} else {
					NodeList visibilityDecls = visibilityChildNode.getChildNodes();
					for (int vdIndex = 0; vdIndex < visibilityDecls.getLength(); vdIndex++) {
						Node visibilityDecl = visibilityDecls.item(vdIndex);
                        if (visibilityDecl.getNodeType() == Node.ELEMENT_NODE) {
    						if ("isMemberOfWorkgroup".equals(visibilityDecl.getNodeName())) {
    							String workgroupName = visibilityDecl.getFirstChild().getNodeValue();
    							UserSession session = UserSession.getAuthenticatedUser();
    							if (session == null) {
    								throw new WorkflowRuntimeException("UserSession is null!  Attempted to render the searchable attribute outside of an established session.");
    							}
    							GroupNameId groupId = new GroupNameId(workgroupName);
    							try {
    								visible = KEWServiceLocator.getWorkgroupService().isUserMemberOfGroup(groupId, session.getWorkflowUser());
    							} catch (EdenUserNotFoundException e) {
    								throw new RuntimeException("Error checking for workgroup membership permissions for workgroup '" + workgroupName + "'.  Error Message: " + e.getMessage(), e);
    							}
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
        } else if ("checkbox_indicator".equals(type)) {
            return Field.CHECKBOX_YES_NO;
        } else if ("checkbox_present".equals(type)) {
            return Field.CHECKBOX_PRESENT;
        }
		throw new IllegalArgumentException("Illegal field type found: " + type);
	}
	
	public List validateUserSearchInputs(Map paramMap) {
		this.paramMap = paramMap;
		List errors = new ArrayList();

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
                        Object lowerObj = getParamMap().get(RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldDefName);
                        if ( (lowerObj != null) && (lowerObj instanceof String) ) {
                            rangeMemberInSearchParams |= StringUtils.isNotBlank((String) lowerObj);
                        }
                        Object upperObj = getParamMap().get(RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldDefName);
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
        					if (Utilities.isEmpty(fieldDataType)) {
        						fieldDataType = DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
        					}
        					// get the searchable attribute value by using the data type
        					SearchableAttributeValue attributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(fieldDataType);
        					if (attributeValue == null) {
        						String errorMsg = "Cannot find SearchableAttributeValue for field data type '" + fieldDataType + "'";
        						LOG.error("validateUserSearchInputs() " + errorMsg);
        						throw new RuntimeException(errorMsg);
        					}
        					
        					if (rangeMemberInSearchParams) {
                                String lowerBoundFieldDefName = RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldDefName;
                                String upperBoundFieldDefName = RANGE_UPPER_BOUND_PROPERTY_PREFIX + fieldDefName;
                                String lowerBoundEnteredValue = null;
                                String upperBoundEnteredValue = null;
                                NamedNodeMap lowerBoundRangeAttributes = null;
                                NamedNodeMap upperBoundRangeAttributes = null;
        						Node rangeDefinitionNode = getPotentialChildNode(searchDefNode, "rangeDefinition");
        						NamedNodeMap rangeDefinitionAttributes = (rangeDefinitionNode != null) ? rangeDefinitionNode.getAttributes() : null;
        						lowerBoundEnteredValue = (String) getParamMap().get(lowerBoundFieldDefName);
        						upperBoundEnteredValue = (String) getParamMap().get(upperBoundFieldDefName);
        						if (!Utilities.isEmpty(lowerBoundEnteredValue)) {
                                    lowerBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "lower");
        							errors.addAll(performValidation(attributeValue, getBooleanWithPotentialOverrides("allowWildcards", searchDefAttributes, rangeDefinitionAttributes, lowerBoundRangeAttributes), 
        									lowerBoundFieldDefName, lowerBoundEnteredValue, constructRangeFieldErrorPrefix(fieldDefTitle,lowerBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (!Utilities.isEmpty(upperBoundEnteredValue)) {
                                    upperBoundRangeAttributes = getAttributesForPotentialChildNode(rangeDefinitionNode, "upper");
        							errors.addAll(performValidation(attributeValue, getBooleanWithPotentialOverrides("allowWildcards", searchDefAttributes, rangeDefinitionAttributes, upperBoundRangeAttributes), 
        									upperBoundFieldDefName, upperBoundEnteredValue, constructRangeFieldErrorPrefix(fieldDefTitle,upperBoundRangeAttributes), findXpathExpressionPrefix));
        						}
                                if (errors.isEmpty()) {
                                    Boolean rangeValid = attributeValue.isRangeValid(lowerBoundEnteredValue, upperBoundEnteredValue);
                                    if ( (rangeValid != null) && (!rangeValid) ) {
                                        String lowerLabel = getPotentialRangeBoundLabelFromAttributes(lowerBoundRangeAttributes);
                                        String upperLabel = getPotentialRangeBoundLabelFromAttributes(upperBoundRangeAttributes);
                                        String errorMsg = "The " + fieldDefTitle + " range is incorrect.  The " + (StringUtils.isNotBlank(lowerLabel) ? lowerLabel : DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL) + " value entered must come before the " + (StringUtils.isNotBlank(upperLabel) ? upperLabel : DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL) + " value";
                                        LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
                                        errors.add(new WorkflowAttributeValidationError(fieldDefName, errorMsg));
                                    }
                                }
        						
        					} else {
                                Object enteredValue = getParamMap().get(fieldDefName);
                                if (enteredValue instanceof String) {
                                    String stringVariable = (String) enteredValue;
                                    errors.addAll(performValidation(attributeValue, getBooleanValue(searchDefAttributes, "allowWildcards"), fieldDefName, stringVariable, fieldDefTitle, findXpathExpressionPrefix));
                                } else if (enteredValue instanceof Collection) {
                                    Collection stringVariables = (Collection<String>)enteredValue;
                                    for (Iterator iter = stringVariables.iterator(); iter.hasNext();) {
                                        String stringVariable = (String) iter.next();
                                        errors.addAll(performValidation(attributeValue, getBooleanValue(searchDefAttributes, "allowWildcards"), fieldDefName, stringVariable, "One value for " + fieldDefTitle, findXpathExpressionPrefix));
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
	
	private List performValidation(SearchableAttributeValue attributeValue, Boolean allowWildcards, String fieldDefName, String enteredValue, String errorMessagePrefix, String findXpathExpressionPrefix) throws XPathExpressionException {
		List errors = new ArrayList();
		XPath xpath = XPathHelper.newXPath();
		if ( attributeValue.allowsWildcards() && 
			     ( (allowWildcards == null) || (allowWildcards.booleanValue())) ) {
				enteredValue = enteredValue.replaceAll(SEARCH_WILDCARD_CHARACTER_REGEX_ESCAPED, "");
		}
		if (!attributeValue.isPassesDefaultValidation(enteredValue)) {
            errorMessagePrefix = (StringUtils.isNotBlank(errorMessagePrefix)) ? errorMessagePrefix : "Field";
			String errorMsg = errorMessagePrefix + " with value '" + enteredValue + "' does not conform to standard validation for field type.";
			LOG.debug("validateUserSearchInputs() " + errorMsg + " :: field type '" + attributeValue.getAttributeDataType() + "'");
			errors.add(new WorkflowAttributeValidationError(fieldDefName, errorMsg));
		} else {
			String findValidation = findXpathExpressionPrefix + "/validation/regex";
			String regex = (String) xpath.evaluate(findValidation, getConfigXML(), XPathConstants.STRING);
			if (!Utilities.isEmpty(regex)) {
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