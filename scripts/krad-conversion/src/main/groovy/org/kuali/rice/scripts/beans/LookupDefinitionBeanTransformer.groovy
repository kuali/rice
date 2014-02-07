/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.scripts.beans

import groovy.util.logging.Log;
import groovy.xml.XmlUtil;
import org.apache.commons.lang.ClassUtils;

/**
 * This class transforms lookup definitions into their uif counterpart as well as
 * any properties and children beans
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class LookupDefinitionBeanTransformer extends SpringBeanTransformer {

    String lookupDefinitionBeanType = "LookupDefinition";
    String lookupViewBeanType = "Uif-LookupView";

    /**
     * Produces Uif-LookupView based on information in LookupDefinition
     *
     * @param beanNode
     */
    def transformLookupDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode);

        List copiedProperties;

        def lookupDefParentBeanNode = beanNode
        def lookupTitle = lookupDefParentBeanNode.property.find { it.@name == "title" }?.@value;

        def objClassName = getObjectClassName(lookupDefParentBeanNode);
        def objName = ClassUtils.getShortClassName(objClassName);
        def translatedBeanId = getTranslatedBeanId(beanNode.@id, lookupDefinitionBeanType, lookupViewBeanType);
        def translatedParentId = getTranslatedBeanId(beanNode.@parent, lookupDefinitionBeanType, lookupViewBeanType);

        // these attributes are being converted and should not be copied when useCarryoverAttributes is enabled
        List ignoreAttributes = [];

        // these properties are being converted and should not be copied when useCarryoverProperties is enabled
        List ignoreOnCopyProperties = ["title", "translateCodes", "menubar", "defaultSort", "numOfColumns", "extraButtonSource", "extraButtonParams", "disableSearchButtons", "lookupFields", "resultFields"]

        def beanAttributes = convertBeanAttributes(beanNode, lookupDefinitionBeanType, lookupViewBeanType, [],[:], ignoreAttributes);

        if (useCarryoverProperties) {
            copiedProperties = beanNode.property.collect { it.@name };
            copiedProperties.removeAll(ignoreOnCopyProperties);
        } else {
            copiedProperties = [];
        }
        if (isPlaceholder(beanNode)) {
            addCommentIfNotExists(beanNode.parent(),"Lookup View");
            beanNode.@id = translatedBeanId;
            beanNode.@parent = translatedParentId;
            beanNode.parent().append(beanNode);
            beanNode.parent().remove(beanNode);
        } else {
            beanNode.replaceNode {
                addCommentIfNotExists(beanNode.parent(),"Lookup View");
                bean(beanAttributes) {
                    if (objClassName) {
                        property(name: "dataObjectClassName", value: objClassName)
                    }
                    renameProperties(delegate, lookupDefParentBeanNode, ["title": "headerText",
                            "translateCodes": "translateCodesOnReadOnlyDisplay"])
                    copyBeanProperties(delegate, beanNode, copiedProperties);
                    transformMenubarProperty(delegate, beanNode)
                    transformDefaultSortProperty(delegate, beanNode)
                    transformNumOfColumns(delegate, beanNode)
                    transformExtraButtonParams(delegate, beanNode)
                    transformDisableSearchButtons(delegate, beanNode)
                    transformLookupFieldsProperty(delegate, beanNode)
                    transformResultFieldsProperty(delegate, beanNode)
                    transformResultColumnsTotalling(delegate, beanNode)
                }
            }
        }
    }

    /**
     * Replaces menubar property with uif message
     */
    def transformMenubarProperty(NodeBuilder builder, Node node) {
        def menubarPropertyNode = node.property.find { it.@name == "menubar" };
        if (menubarPropertyNode != null) {
            builder.property(name: "page.header.lowerGroup") {
                bean(parent: "Uif-HeaderLowerGroup") {
                    property(name: "items"){
                        list(merge: "true") {
                            bean(parent: "Uif-Message") {
                                property(name: "messageText", value: "[" + menubarPropertyNode.@value + "]");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Replaces defaultSort with defaultSortAscending and defaultSortAttributeNames
     */
    def transformDefaultSortProperty(NodeBuilder builder, Node node) {
        def defaultSortPropertyNode = node.property.find { it.@name == "defaultSort" };
        if (defaultSortPropertyNode) {
            defaultSortPropertyNode.bean.each { sortDefinitionBean ->
                def sortAscendingPropertyNode = sortDefinitionBean.find { it.@name == "sortAscending" };
                if (sortAscendingPropertyNode != null) {
                    builder.property(name: "defaultSortAscending", value: sortAscendingPropertyNode.@value);
                }
                transformPropertyValueList(builder, sortDefinitionBean, ["attributeNames": "defaultSortAttributeNames"], valueFieldTransform);
            }
        }
    }

    /**
     * Replaces numOfColumns with criteriaGroup.layoutManager.numberOfColumns
     */
    def transformNumOfColumns(NodeBuilder builder, Node node) {
        def numOfColumnsPropertyText = getPropertyValue(node, "numOfColumns");
        if (numOfColumnsPropertyText != null && numOfColumnsPropertyText.isNumber()) {
            def numOfColumns = Integer.parseInt(numOfColumnsPropertyText);
            if (numOfColumns > 1) {
                builder.property(name: "criteriaGroup.layoutManager.numberOfColumns", value: numOfColumns * 2)
            }
        }
    }

    /**
     * Replaces extraButtonParams with criteriaGroup.footer. extraButtonSource is being dropped.
     */
    def transformExtraButtonParams(NodeBuilder builder, Node node) {
        def extraButtonParams = getPropertyValue(node, "extraButtonParams");
        if (extraButtonParams != null) {
            builder.property(name: "criteriaGroup.footer") {
                bean(parent: "Uif-LookupCriteriaFooter") {
                    property(name: "items") {
                        list(merge: "true") {
                            bean(parent: "Uif-PrimaryActionButton", "xmlns:p": pNamespaceSchema, "p:methodToCall": extraButtonParams,
                                    "p:actionLabel": extraButtonParams.replaceAll(/\B[A-Z]/) { ' ' + it }.toLowerCase())
                        }
                    }
                }
            }
        }
    }

    /**
     * Convert the disableSearchButtons attribute to renderSearchButtons.  The boolean value needs to be inverted as well.
     */
    def transformDisableSearchButtons(NodeBuilder builder, Node node) {
        def disableSearchButtons = getPropertyValue(node, "disableSearchButtons");
        if (disableSearchButtons != null && disableSearchButtons == "true") {
            builder.property(name: "renderCriteriaActions", value: "false");
        }
    }

    /**
     * Transforms lookup field properties into criteria fields
     */
    def transformLookupFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["lookupFields": "criteriaFields"], gatherLookupFieldAttributes, lookupCriteriaFieldBeanTransform);
    }

    /**
     *  Retrieve attributes of the criteriaField and translate them to their KRAD equivalent.
     */
    def gatherLookupFieldAttributes = { Node beanNode -> return (gatherIdAttribute(beanNode) + gatherAttributeNameAttribute(beanNode) + gatherNoLookupAttribute(beanNode) + genericGatherAttributes(beanNode, ["*treatWildcardsAndOperatorsAsLiteral": "p:disableWildcardsAndOperators"]) + copyGatherProperties(beanNode, ["readOnly"])); }

    /**
     * Convert the noLookup attribute to quickfinder.render.  The boolean value needs to be inverted as well.
     */
    def gatherNoLookupAttribute = { Node beanNode ->
        def noLookup = beanNode?.attributes()?.clone().find { matchesAttr("*noLookup", it.key.toString()) };
        if (noLookup?.value == "true") {
            return ["p:quickfinder.render": "false"];
        } else if (noLookup?.value == "false") {
            return ["p:quickfinder.render": "true"];
        } else {
            return [:];
        }
    }

    def transformHelpDefinitionProperty(NodeBuilder builder, Node beanNode) {
        Node helpDefinitionProperty = ((Node)beanNode?.property?.find { "helpDefinition".equals(it.@name) });
        Node helpUrlProperty = (Node) beanNode?.property?.find { "helpUrl".equals(it.@name) };
        // if it contains a HelpDefinition bean
        if (helpDefinitionProperty) {
            Node helpDefinitionPropertyCopy  = cloneNode(helpDefinitionProperty);
            // remove children, rename as help and copy clone of helpDefinition into help bean
            helpDefinitionProperty.children().clear();
            helpDefinitionProperty.attributes().put("name","help");
            Node helpNode = new Node(helpDefinitionProperty, "bean", ["parent":"Uif-Help"]);
            helpNode.append(helpDefinitionPropertyCopy);
        } else if (helpUrlProperty) {
            def helpUrl = helpUrlProperty.@value;
            builder.property(name: "help") {
                bean("parent": "Uif-Help") {
                    property("name": "helpUrl", "value": helpUrl)
                }
            }
        }
    }

    /**
     * Transforms result field properties into data fields
     */
    def transformResultFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["resultFields": "resultFields"], gatherResultFieldAttributes, dataFieldBeanTransform);
    }

    /**
     *  Retrieve attributes of the resultField and translate them to their KRAD equivalent.
     */
    def gatherResultFieldAttributes = { Node beanNode -> return (gatherIdAttribute(beanNode) + gatherAttributeNameAttribute(beanNode) + copyGatherProperties(beanNode, ["readOnly"])); }

    def transformResultColumnsTotalling(NodeBuilder builder, Node beanNode) {
        def columnsWithTotalling = [];
        def resultFieldsProperty = beanNode.property.find { it.@name == "resultFields" }
        if (resultFieldsProperty != null) {
            resultFieldsProperty.list.bean.each { resultFieldsNode ->
                def attributes = genericGatherAttributes(resultFieldsNode, ["*total": "total", "*attributeName": "attributeName"]);
                if (attributes.containsKey("total") && attributes.get("total") == "true") {
                    columnsWithTotalling.add(attributes.get("attributeName"));
                }
            }
        }

        if (columnsWithTotalling.size() > 0) {
            builder.property(name: "resultsGroup.layoutManager.columnCalculations") {
                list {
                    columnsWithTotalling.each { propertyName -> bean(parent: "Uif-ColumnCalculationInfo-Sum", "xmlns:p": pNamespaceSchema, "p:propertyName": propertyName) }
                }
            }
        }
    }

}
