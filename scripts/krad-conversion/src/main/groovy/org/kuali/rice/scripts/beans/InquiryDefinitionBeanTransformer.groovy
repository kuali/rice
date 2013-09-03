/**
 * Copyright 2005-2013 The Kuali Foundation
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

/**
 * This class converts inquiry definitions into inquiry views
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class InquiryDefinitionBeanTransformer extends SpringBeanTransformer {

    String inquiryDefinitionBeanType = "InquiryDefinition";
    String inquiryViewBeanType = "Uif-InquiryView";

    /**
     * @param beanNode
     */
    def transformInquiryDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode)
        def busObjClassQualName = getObjectClassName(beanNode);
        def inquiryParentBeanNode = beanNode;
        def titlePropNode = inquiryParentBeanNode.property.find { it.@name == "title" }

        def translatedBeanId = getTranslatedBeanId(beanNode.@id, inquiryDefinitionBeanType, inquiryViewBeanType);
        def translatedParentId = getTranslatedBeanId(beanNode.@parent, inquiryDefinitionBeanType, inquiryViewBeanType);

        // these attributes are being converted and should not be copied when carryoverAttributes is enabled
        List ignoreAttributes = [];

        // these properties are being converted and should not be copied when carryoverProperties is enabled
        List ignoreOnCopyProperties = ["inquirySections"];

        def beanAttributes = somethingBeanAttributes(beanNode, inquiryDefinitionBeanType, inquiryViewBeanType, ignoreAttributes);

        List copiedProperties;
        if (carryoverProperties) {
            copiedProperties = beanNode.property.collect { it.@name };
            copiedProperties.removeAll(ignoreOnCopyProperties);
        } else {
            copiedProperties = [];
        }


        log.finer "transform bean node for inquiry"
        if (isPlaceholder(beanNode)) {
            beanNode.@id = translatedBeanId;
            beanNode.@parent = translatedParentId;
        } else {
            beanNode.replaceNode {
                addComment(delegate, "Inquiry View")
                bean(beanAttributes) {
                    copyProperties(delegate, beanNode, copiedProperties);
                    renameProperties(delegate, beanNode, ["title": "headerText"])
                    if (titlePropNode?.@value) {
                        addViewNameProperty(delegate, titlePropNode.@value)
                    }
                    property(name: "dataObjectClassName", value: busObjClassQualName)
                    transformInquirySectionsProperty(delegate, beanNode)
                }
            }
        }

    }

    /**
     * Transforms inquiry section definition bean into uif counterpart
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformInquirySectionDefinitionBean(NodeBuilder builder, Node beanNode) {
        // if it contains a inquiry collection add a uif stack collection, else replace with a Uif-Disclosure-GridSection
        if (!beanNode.property.list.bean.find { it.@parent == "InquiryCollectionDefinition" }) {
            builder.bean(parent: 'Uif-Disclosure-GridSection') {
                copyProperties(delegate, beanNode, ["title"]);
                renameProperties(delegate, beanNode, ["numberOfColumns": "layoutManager.numberOfColumns"]);
                transformInquiryFieldsProperty(delegate, beanNode);
            }
        } else {
            builder.bean(parent: 'Uif-StackedCollectionSection') {
                copyProperties(delegate, beanNode, ["title", "collectionObjectClass", "propertyName"]);
                renameProperties(delegate, beanNode, ["title": "layoutManager.summaryTitle"]);
                transformInquiryFieldsProperty(delegate, beanNode);
                transformSummaryFieldsProperty(delegate, beanNode);
            }
        }
    }

    def transformInquirySectionDefinitionBean(Node beanNode) {
        beanNode.replaceNode {
            transformInquirySectionDefinitionBean(delegate, beanNode);
        }
    }

    /**
     *  transforms inquiry sections property into an items property along with transforming all beans and references
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
                            transformInquirySectionDefinitionBean(builder, beanOrRefNode); // replace with grid or stack collection
                        } else if ("ref".equals(beanOrRefNode.name()?.localPart)) {
                            ref(bean: beanOrRefNode.@bean) // ref copied, will be converted at later stage
                        }
                    }
                }
            }
        }
    }

    /**
     * transform inquiry field properties into input fields
     *
     * @param builder
     * @param beanNode
     */
    def transformInquiryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["inquiryFields": "items"], gatherAttributeNameAttribute, inputFieldBeanTransform);
    }

}
