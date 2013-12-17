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

    String maintenanceDefinitionBeanType = "MaintenanceDocumentEntry";
    String maintenanceDocEntryBeanType = "uifMaintenanceDocumentEntry"
    String maintenanceViewBeanType = "Uif-MaintenanceView";

    // MDE Conversion Components
    def mdeCopyProperties = ["businessObjectClass", "maintainableClass", "documentTypeName", "documentAuthorizerClass", "lockingKeys"];
    def mdeRenameProperties = [:]
    def mdeIgnoreOnCarryoverProperties = []
    def mdeIgnoreOnCarryoverAttributes = []

    // UMV Conversion Components - include
    def umvCopyProperties = ["businessObjectClass", "maintainableClass", "documentTypeName", "documentAuthorizerClass", "lockingKeys"]
    def umvRenameProperties = ["title": "headerText", "businessObjectClass": "dataObjectClassName", "dataObjectClass": "dataObjectClassName"]
    def umvIgnoreOnCarryoverProperties = ["title", "maintainableSections"]
    def umvIgnoreOnCarryoverAttributes = []


    /**
     * Transforms maintenance document entry which results in an updated document entry
     * and a maintenance View
     *
     * @param beanNode
     * @return
     */
    def transformMaintenanceDocumentEntryBean(Node beanNode) {
        if (beanNode.@parent == "MaintenanceDocumentEntry") {
            def maintDocParentBeanNode = beanNode;
            def beanTitle = maintDocParentBeanNode.property.find { it.@name == "title" }?.@value;

            // these properties are being converted and should not be copied when carryoverProperties is enabled
            List ignoreOnCopyProperties = ["title", "maintainableSections"];

            def mdeBeanAttributes =  beanNode.attributes().clone(); // convertBeanAttributes(beanNode, maintenanceDefinitionBeanType, maintenanceDocEntryBeanType, mdeIgnoreOnCarryoverAttributes);
            def umvBeanAttributes = convertBeanAttributes(beanNode, maintenanceDefinitionBeanType, maintenanceViewBeanType, umvIgnoreOnCarryoverAttributes);

            def mdeCarryoverProperties = findCarryoverProperties(beanNode, mdeCopyProperties, mdeRenameProperties.keySet(), mdeIgnoreOnCarryoverProperties);
            def umvCarryoverProperties = findCarryoverProperties(beanNode, umvCopyProperties, umvRenameProperties.keySet(), umvIgnoreOnCarryoverProperties);

            log.finer "transform bean node for maintenance document entry"
            if (isPlaceholder(beanNode)) {
                addCommentIfNotExists(beanNode.parent(), "Maintenance View")
                beanNode.@id = getTranslatedBeanId(beanNode.@id, maintenanceDefinitionBeanType, maintenanceViewBeanType);
                beanNode.@parent = getTranslatedBeanId(beanNode.@parent, maintenanceDefinitionBeanType, maintenanceViewBeanType);
                beanNode.parent().append(beanNode);
                beanNode.parent().remove(beanNode);
            } else {
                beanNode.replaceNode {
                    addCommentIfNotExists(beanNode.parent(), "Maintenance View")
                    bean(umvBeanAttributes) {
                        copyProperties(delegate, beanNode, umvCopyProperties + umvCarryoverProperties)
                        renameProperties(delegate, maintDocParentBeanNode, umvRenameProperties)
                        addViewNameProperty(delegate, beanTitle);
                        transformMaintainableSectionsProperty(delegate, beanNode)
                    }
                    addCommentIfNotExists(beanNode.parent(), "Maintenance Document Entry")
                    bean(mdeBeanAttributes) {
                        copyProperties(delegate, beanNode, mdeCopyProperties + mdeCarryoverProperties)
                    }
                }
            }
        }

        return beanNode;
    }

    def findCarryoverProperties(Node beanNode, def copyPropertiesList, def renamePropertiesList, def ignorePropertiesList) {
        def carryoverPropertiesList = []
        if (carryoverProperties) {
            carryoverPropertiesList = beanNode.property.collect { it.@name };
            carryoverPropertiesList.removeAll(copyPropertiesList);
            carryoverPropertiesList.removeAll(renamePropertiesList);
            carryoverPropertiesList.removeAll(ignorePropertiesList);
        }

        return carryoverPropertiesList;
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
                transformMaintainableItemsProperty(delegate, beanNode);
            }
        } else {
            transformMaintainableSectionDefinitionBeanWithCollection(builder, beanNode);
        }
    }

    /**
     * Generates a vertical box section with all collection and non-collection elements as items of the vertical box
     *
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformMaintainableSectionDefinitionBeanWithCollection(NodeBuilder builder, Node beanNode) {
        // used to build up the list as its processes through the non-collection items
        def itemsList = [];
        def beanAttrs = [parent: "Uif-VerticalBoxSection"];
        if (beanNode.@id) {
            beanAttrs.put("id", beanNode.@id);
        }
        builder.bean(beanAttrs) {
            copyProperties(delegate, beanNode, ["title"]);
            property(name: "items") {
                list {
                    beanNode.property.find { it.@name == "maintainableItems" }.list.bean.each { beanItem ->
                        if (!"MaintainableCollectionDefinition".equals(beanItem.@parent)) {
                            itemsList.add(gatherNameAttribute(beanItem));
                        } else {
                            if (itemsList.size() > 0) {
                                builder.bean(parent: 'Uif-MaintenanceGridSection') {
                                    copyProperties(delegate, beanNode, ["title", "collectionObjectClass", "propertyName"])
                                    property(name: "items") {
                                        list {
                                            itemsList.each { attributes ->
                                                attributes.put("parent", "Uif-InputField");
                                                genericBeanTransform(builder, attributes);
                                            }
                                        }
                                    }
                                }
                            }

                            itemsList = [];
                            builder.bean(parent: 'Uif-MaintenanceStackedCollectionSection') {
                                copyProperties(delegate, beanNode, ["title", "collectionObjectClass", "propertyName"]);
                                renameProperties(delegate, beanNode, ["title": "headerText", "businessObjectClass": "collectionObjectClass"]);
                                transformMaintainableFieldsProperty(delegate, beanItem);
                                transformSummaryFieldsProperty(delegate, beanNode);

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param builder
     * @param beanNode
     * @return
     */
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

    /**
     * Maintainable fields returned as an items property list
     *
     *
     * @param builder - data output is appended to the builder
     * @param beanNode - generally a collection definition bean
     * @return
     */
    def transformMaintainableFieldsProperty(NodeBuilder builder, Node beanNode) {
        def maintainableFieldsProperty = beanNode.property.find { "maintainableFields".equals(it.@name) };
        if (maintainableFieldsProperty) {
            builder.property(name: "items") {
                list {
                    maintainableFieldsProperty?.list?.bean.each { maintainableItem -> inputFieldBeanTransform(builder, gatherNameAttribute(maintainableItem)); }
                }
            }
        }
    }

}
