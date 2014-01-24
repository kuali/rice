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

import groovy.util.logging.Log
import org.apache.commons.lang.ClassUtils

/**
 * This class transforms inquiry definitions into their uif counterpart as well as
 * any properties and children beans
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class InquiryDefinitionBeanTransformer extends SpringBeanTransformer {

    String inquiryDefinitionBeanType = "InquiryDefinition";
    String inquiryViewBeanType = "Uif-InquiryView";

    /**
     * Produces Uif-InquiryView based on information in InquiryDefinition
     *
     * @param beanNode
     */
    def transformInquiryDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode);

        List copiedProperties;

        def inquiryDefParentBeanNode = beanNode;
        def inquiryTitle = inquiryDefParentBeanNode.property.find { it.@name == "title" }?.@value;

        def objClassName = getObjectClassName(beanNode);
        def objName = ClassUtils.getShortClassName(objClassName);
        def translatedBeanId = getTranslatedBeanId(beanNode.@id, inquiryDefinitionBeanType, inquiryViewBeanType);
        def translatedParentId = getTranslatedBeanId(beanNode.@parent, inquiryDefinitionBeanType, inquiryViewBeanType);

        // these attributes are being converted and should not be copied when useCarryoverAttributes is enabled
        List ignoreAttributes = [];

        // these properties are being converted and should not be copied when useCarryoverProperties is enabled
        List ignoreOnCopyProperties = ["title", "inquirableClass", "authorizerClass", "presentationControllerClass", "inquirySections"];

        def beanAttributes = convertBeanAttributes(beanNode, inquiryDefinitionBeanType, inquiryViewBeanType, [],[:], ignoreAttributes);

        if (useCarryoverProperties) {
            copiedProperties = beanNode.property.collect { it.@name };
            copiedProperties.removeAll(ignoreOnCopyProperties);
        } else {
            copiedProperties = [];
        }
        if (isPlaceholder(beanNode)) {
            addCommentIfNotExists(beanNode.parent(), "Inquiry View")
            beanNode.@id = translatedBeanId;
            beanNode.@parent = translatedParentId;
            beanNode.parent().append(beanNode);
            beanNode.parent().remove(beanNode);
        } else {
            beanNode.replaceNode {
                addCommentIfNotExists(beanNode.parent(), "Inquiry View")
                bean(beanAttributes) {
                    if (inquiryTitle) {
                        property(name: "headerText", value: inquiryTitle)
                    }
                    if (objClassName) {
                        property(name: "dataObjectClassName", value: objClassName)
                    }
                    copyProperties(delegate, beanNode, copiedProperties);

                    renameProperties(delegate, beanNode, ["inquirableClass": "viewHelperServiceClass"]);
                    copyProperties(delegate, beanNode, ["authorizerClass", "presentationControllerClass"]);
                    transformInquirySectionsProperty(delegate, beanNode)
                }
            }
        }
    }

    /**
     *  Transforms inquiry sections property into an items property along with transforming all beans and references
     *
     * @param builder
     * @param beanNode
     */
    def void transformInquirySectionsProperty(NodeBuilder builder, Node beanNode) {
        def inquirySectionsPropertyNode = beanNode.property.find { it.@name == "inquirySections" }
        if (inquirySectionsPropertyNode != null) {
            builder.property(name: "items") {
                list {
                    inquirySectionsPropertyNode.list.'*'.each { beanOrRefNode ->
                        if ("bean".equals(beanOrRefNode.name()?.localPart) && "InquirySectionDefinition".equals(beanOrRefNode.@parent)) {
                            transformInquirySectionDefinitionBean(builder, beanOrRefNode);
                        } else if ("ref".equals(beanOrRefNode.name()?.localPart)) {
                            ref(bean: beanOrRefNode.@bean);
                        }
                    }
                }
            }
        }
    }

    /**
     * Transforms inquiry section definition bean.
     * <p>
     * This transformation only handles the most common and simplest cases:
     * <ul>
     * <li>Sections that contain one or more simple properties</li>
     * <li>Sections that contain one or more collections that contain one or more simple properties</li>
     * </ul>
     * Complex and rare formulations (such as sections with both simple properties and collections together or
     * collections within collections) are not supported and must be manually converted.
     * </p>
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformInquirySectionDefinitionBean(NodeBuilder builder, Node beanNode) {
        // if it contains a inquiry collection add a uif stack collection, else replace with a Uif-Disclosure-GridSection
        def inquiryFieldsBeans = beanNode?.property?.find { "inquiryFields".equals(it.@name) }?.list?.bean;
        if (!inquiryFieldsBeans?.find { it.@parent == "InquiryCollectionDefinition" }) {
            transformInquirySectionDefinitionFields(builder, beanNode);
        } else {
            transformInquiryCollectionDefinitionBean(builder, beanNode);
        }
    }


    /**
     * Helper method to {@code transformInquirySectionDefinitionBean}.
     * @param beanNode
     * @return
     */
    def transformInquirySectionDefinitionBean(Node beanNode) {
        beanNode.replaceNode {
            transformInquirySectionDefinitionBean(delegate, beanNode);
        }
    }

    /**
     * Transforms inquiry section definition fields.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformInquirySectionDefinitionFields(NodeBuilder builder, Node beanNode) {
        def attributes = gatherIdAttribute(beanNode) + [parent: 'Uif-Disclosure-GridSection'];
        builder.bean(attributes) {
            renameProperties(builder, beanNode, ["title": "headerText", "defaultOpen": "disclosure.defaultOpen"]);
            transformNumberOfColumns(builder, beanNode);
            transformInquiryFieldsProperty(builder, beanNode);
        }
    }

    /**
     * Transforms inquiry section definition collections.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformInquiryCollectionDefinitionBean(NodeBuilder builder, Node beanNode) {
        def attributes = gatherIdAttribute(beanNode) + [parent: 'Uif-StackedCollectionSection'];
        builder.bean(attributes) {
            renameProperties(builder, beanNode, ["title": "headerText", "defaultOpen": "disclosure.defaultOpen"]);
            //transformNumberOfColumns(builder, beanNode);
            def inquiryFieldsPropertyNode = beanNode.property.find { it.@name == "inquiryFields" }
            if (inquiryFieldsPropertyNode != null) {
                inquiryFieldsPropertyNode.list.'*'.each { beanOrRefNode ->
                    if ("bean".equals(beanOrRefNode.name()?.localPart) && "InquiryCollectionDefinition".equals(beanOrRefNode.@parent)) {
                        transformInquiryCollectionDefinitionFields(builder, beanOrRefNode);
                    } else if ("ref".equals(beanOrRefNode.name()?.localPart)) {
                        ref(bean: beanOrRefNode.@bean);
                    }
                }
            }
        }
    }

    /**
     * Transforms inquiry collection definition fields.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformInquiryCollectionDefinitionFields(NodeBuilder builder, Node beanNode) {
        transformNumberOfColumns(builder, beanNode);
        renameProperties(builder, beanNode, ["businessObjectClass": "collectionObjectClass", "attributeName": "propertyName"]);
        transformInquiryFieldsProperty(builder, beanNode);
        renameProperties(builder, beanNode, ["summaryTitle": "layoutManager.summaryTitle"]);
        transformSummaryFieldsProperty(builder, beanNode);
    }

    /**
     * Helper method to {@code transformInquiryCollectionDefinitionFields}.
     * @param beanNode
     * @return
     */
    def transformInquiryCollectionDefinitionFields(Node beanNode) {
        beanNode.replaceNode {
            transformInquiryCollectionDefinitionFields(delegate, beanNode);
        }
    }

    /**
     * Transforms inquiry field properties into input fields
     *
     * @param builder
     * @param beanNode
     */
    def transformInquiryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["inquiryFields": "items"], gatherInquiryFieldAttributes, dataFieldBeanTransform);
    }

    /**
     *  Retrieve attributes of the inquiryFields and translate them to their KRAD equivalent.
     */
    def gatherInquiryFieldAttributes = { Node beanNode -> return (gatherIdAttribute(beanNode) + gatherAttributeNameAttribute(beanNode) + gatherNoInquiryAttribute(beanNode)); }

    /**
     * Convert the noInquiry attribute to inquiry.render.  The boolean value needs to be inverted as well.
     */
    def gatherNoInquiryAttribute = { Node beanNode ->
        def noInquiry = beanNode?.attributes()?.clone().find { matchesAttr("*noInquiry", it.key.toString()) };
        if (noInquiry?.value == "true") {
            return ["p:inquiry.render": "false"];
        } else if (noInquiry?.value == "false") {
            return ["p:inquiry.render": "true"];
        } else {
            return [:];
        }
    }

    /**
     * Replaces numberOfColumns with layoutManager.numberOfColumns
     *
     * @param builder
     * @param beanNode
     */
    def transformNumberOfColumns(NodeBuilder builder, Node beanNode) {
        def numberOfColumnsPropertyText = getPropertyValue(beanNode, "numberOfColumns");
        if (numberOfColumnsPropertyText != null && numberOfColumnsPropertyText.isNumber()) {
            def numberOfColumns = Integer.parseInt(numberOfColumnsPropertyText);
            if (numberOfColumns >= 1) {
                builder.property(name: "layoutManager.numberOfColumns", value: numberOfColumns * 2)
            }
        }
    }

}
