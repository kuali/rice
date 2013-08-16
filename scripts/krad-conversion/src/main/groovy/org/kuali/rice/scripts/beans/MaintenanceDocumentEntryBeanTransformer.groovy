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
 * This class transforms maintenance document entry beans and its properties/children beans into their uif equivalent
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class MaintenanceDocumentEntryBeanTransformer extends SpringBeanTransformer {

    /**
     * Transforms maintenance document entries into uif maintenance views
     *
     * @param beanNode
     * @return
     */
    def transformMaintenanceDocumentEntryBean(Node beanNode) {
        String objName = getMaintenanceDocumentObjectName(beanNode);
        def copyProps = ["businessObjectClass", "maintainableClass", "documentTypeName", "documentAuthorizerClass", "lockingKeys"];
        if (beanNode.@parent == "MaintenanceDocumentEntry") {
            def maintDocParentBeanNode = beanNode;
            def titlePropNode = maintDocParentBeanNode.property.find { it.@name == "title" }
            def maintSectPropNode = maintDocParentBeanNode.property.find { it.@name == "maintainableSections" }
            beanNode.replaceNode {
                bean(id: beanNode.@id, parent: beanNode.@parent) {
                    copyProperties(delegate, beanNode, copyProps)
                }
            }

            def originalBeanType = "MaintenanceDocumentEntry";
            def transformBeanType = "Uif-MaintenanceView";
            def translatedBeanId = getTranslatedBeanId(beanNode.@id, originalBeanType, transformBeanType);
            def translatedParentId = getTranslatedBeanId(beanNode.@parent, originalBeanType, transformBeanType);

            beanNode.replaceNode {
                addComment(delegate, "Maintenance View")
                bean(id: translatedBeanId, parent: translatedParentId) {
                    renameProperties(delegate, maintDocParentBeanNode, ["title": "headerText", "businessObjectClass": "dataObjectClassName"])
                    addViewNameProperty(delegate, titlePropNode?.@value)
                    transformMaintainableSectionsProperty(delegate, maintDocParentBeanNode)
                }
            }

        }
    }

    /**
     *
     *
     * @param beanNode
     * @return
     */
    private String getMaintenanceDocumentObjectName(Node beanNode) {
        return beanNode.@id.toString().replaceFirst("-parentBean", "").replaceFirst("MaintenanceDocument", "");
    }

    def transformMaintainableSectionDefinitionBean(Node beanNode) {
        if ("MaintainableSectionDefinition".equals(beanNode.@parent)) {
            beanNode.replaceNode {
                transformMaintainableSectionDefinitionBean(delegate, beanNode);
            }
        }
    }

    def transformMaintainableSectionDefinitionBean(NodeBuilder builder, Node beanNode) {
        if (!beanNode.property.list.bean.find { "MaintainableCollectionDefinition".equals(it.@parent) }) {
            def beanAttributes = [parent: 'Uif-MaintenanceGridSection']
            if (beanNode.@id) {
                beanAttributes.put("id", beanNode.@id)
            }
            builder.bean(beanAttributes) {
                copyProperties(delegate, beanNode, ["title", "collectionObjectClass", "propertyName"])
                renameProperties(delegate, beanNode, ["numberOfColumns": "layoutManager.numberOfColumns"]);
                transformMaintainableItemsProperty(delegate, beanNode);
            }
        } else {
            builder.bean(parent: 'Uif-MaintenanceStackedCollectionSection') {
                copyProperties(delegate, beanNode, ["title", "collectionObjectClass", "propertyName"]);
                renameProperties(delegate, beanNode, ["title": "headerText", "businessObjectClass": "collectionObjectClass"]);
                transformMaintainableFieldsProperty(delegate, beanNode);
                transformSummaryFieldsProperty(delegate, beanNode);

            }
        }
    }

    def transformMaintainableSectionsProperty(NodeBuilder builder, Node beanNode) {
        def maintainableSectionsProperty = beanNode.property.find { it.@name == "maintainableSections" }
        if (maintainableSectionsProperty != null) {
            builder.property(name: "items") {
                list {
                    maintainableSectionsProperty.list.'*'.each { beanOrRefNode ->
                        if ("bean".equals(beanOrRefNode.name()?.localPart) && "MaintainableSectionDefinition".equals(beanOrRefNode.@parent)) {
                            transformMaintainableSectionDefinitionBean(builder, beanOrRefNode); // replace with grid or stack collection
                        } else if ("ref".equals(beanOrRefNode.name()?.localPart)) {
                            ref(bean: beanOrRefNode.@bean)
                        } // copy ref over; let it be converted in bean conversions
                    }
                }
            }
        }
    }

    def transformMaintainableItemsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["maintainableItems": "items"], gatherNameAttribute, inputFieldBeanTransform);
    }

    def transformMaintainableFieldsProperty(NodeBuilder builder, Node beanNode) {
        def maintainableItemsProperty = beanNode.property.find { "maintainableItems".equals(it.@name) };

        maintainableItemsProperty.list.bean.each { maintItemBean ->
            builder.property(name: "items") {
                list {
                    if ("MaintainableCollectionDefinition".equals(maintItemBean.@parent)) {
                        def maintFields = maintItemBean.property.find { "maintainableFields".equals(it.@name) };
                        maintFields.list.bean.each { fieldBean -> attributeFieldBeanTransform(builder, gatherNameAttribute(fieldBean)); }
                    }
                }
            }
        }
    }

    /**
     * transform summary field properties into values
     *
     * @param builder
     * @param beanNode
     */
    def transformSummaryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["summaryFields": "layoutManager.summaryFields"], gatherAttributeNameAttribute, valueFieldTransform);
    }

}
