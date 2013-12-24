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
import org.apache.commons.lang.StringUtils

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
    def mdeCopyProperties = ["businessObjectClass", "businessRulesClass", "maintainableClass", "documentTypeName",
             "lockingKeys", "allowsRecordDeletion", "preserveLockingKeysOnCopy","allowsNewOrCopy","documentClass"];
    def mdeRenameProperties = [:]
    def mdeIgnoreOnCarryoverProperties = ["documentAuthorizerClass","documentPresentationControllerClass"]
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
        fixNamespaceProperties(beanNode);
        def maintDocParentBeanNode = beanNode;

        def mdeBeanAttributes = convertBeanAttributes(beanNode, maintenanceDefinitionBeanType, maintenanceDocEntryBeanType, [],[:], mdeIgnoreOnCarryoverAttributes,
                mdeCopyProperties, mdeRenameProperties, mdeIgnoreOnCarryoverProperties);
        def mdeCarryoverProperties = findCarryoverProperties(beanNode, mdeCopyProperties, mdeRenameProperties.keySet(),mdeIgnoreOnCarryoverProperties);

        def umvBeanAttributes = convertBeanAttributes(beanNode, maintenanceDefinitionBeanType, maintenanceViewBeanType, [],[:], umvIgnoreOnCarryoverAttributes,
                umvCopyProperties, umvRenameProperties, umvIgnoreOnCarryoverProperties);
        def umvCarryoverProperties = findCarryoverProperties(beanNode, umvCopyProperties, umvRenameProperties.keySet(), umvIgnoreOnCarryoverProperties);


        log.finer "transform bean node for maintenance document entry"
        if (isPlaceholder(beanNode)) {
            addCommentIfNotExists(beanNode.parent(), "Maintenance View");
            beanNode.attributes().putAll(mdeBeanAttributes);
            addCommentIfNotExists(beanNode.parent(), "Maintenance Document Entry");
            beanNode.plus{ bean(umvBeanAttributes) }
        } else {
            beanNode.replaceNode {
                addCommentIfNotExists(beanNode.parent(), "Maintenance View")
                bean(umvBeanAttributes) {
                    copyProperties(delegate, beanNode, umvCopyProperties + umvCarryoverProperties)
                    renameProperties(delegate, maintDocParentBeanNode, umvRenameProperties)
                    transformTitleProperty(delegate, beanNode);
                    transformMaintainableSectionsProperty(delegate, beanNode)
                }
                addCommentIfNotExists(beanNode.parent(), "Maintenance Document Entry")
                bean(mdeBeanAttributes) {
                    copyProperties(delegate, beanNode, mdeCopyProperties + mdeCarryoverProperties)
                    transformDocumentAuthorizerClassProperty(delegate, beanNode)
                    transformDocumentPresentationControllerClassProperty(delegate, beanNode)
                }
            }
        }

        return beanNode;
    }

    def findCarryoverProperties(Node beanNode, def copyPropertiesList, def renamePropertiesList, def ignorePropertiesList) {
        def carryoverPropertiesList = []
        if (useCarryoverProperties) {
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
                transformHelpUrlProperty(delegate, beanNode)
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
            transformHelpUrlProperty(delegate, beanNode)
            property(name: "items") {
                list {
                    beanNode.property.find { it.@name == "maintainableItems" }.list.bean.each { beanItem ->
                        if (!"MaintainableCollectionDefinition".equals(beanItem.@parent)) {
                            itemsList.add(gatherNameAttribute(beanItem));
                        } else {
                            if (itemsList.size() > 0) {
                                builder.bean(parent: 'MaintenanceGridSection') {
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
     * Converts title property to view name property
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformTitleProperty(NodeBuilder builder, Node beanNode) {
        if(beanNode?.property?.findAll { "title".equals(it.@name) }?.size > 0) {
            def modifiedViewName = beanNode?.property?.find { it.@name == "title" }?.@value;
            if (StringUtils.isNotBlank(modifiedViewName)) {
                modifiedViewName = OUTPUT_CONV_FILE_PREFIX + modifiedViewName;
            }

            createProperty(builder, "viewName", modifiedViewName)
        }
    }

    /**
     * replaces helpUrl property with a help property and Uif-Help bean
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformHelpUrlProperty(NodeBuilder builder, Node beanNode) {
        if(beanNode?.property?.findAll { "helpUrl".equals(it.@name) }?.size > 0) {
            String helpUrl = beanNode?.property?.find  { "helpUrl".equals(it.@name) }.@value;

            builder.property(name:"help") {
                bean(parent:"Uif-Help") {
                    property(name:"externalHelpUrl", value: helpUrl)
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
                    maintainableFieldsProperty.list.bean.each { fieldBean ->
                        transformMaintainableFieldDefinitionBean(builder, fieldBean);
                    }
                }
            }
        }
    }
    /**
     * Creates the documentPresentationControllerClass property. Replaces the default value with the KRAD equivalent.
     * Adds comment to add new presentation controller java class if not using default.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformDocumentPresentationControllerClassProperty(NodeBuilder builder, Node beanNode) {
        def origClassName = "org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase";
        def modifiedClassName = "";
        if(beanNode?.property?.find{ "documentPresentationControllerClass".equals(it.@name) }) {
            def className = beanNode?.property?.find { it.@name == "documentPresentationControllerClass" }?.@value;
            if (StringUtils.isNotBlank(className) && className.equals(origClassName)) {
                modifiedClassName = "org.kuali.rice.krad.maintenance.MaintenanceViewPresentationControllerBase";
            }  else {
                addCommentIfNotExists(beanNode.parent(),"TODO - Add documentPresentationControllerClass for bean Id: " + beanNode.@id)
            }

            createProperty(builder, "documentPresentationControllerClass", modifiedClassName)
        }

    }

    /**
     * Creates the documentAuthorizerClass property. Replaces the default value with the KRAD equivalent.
     * Adds comment to add new authorizer java class if not using default.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformDocumentAuthorizerClassProperty(NodeBuilder builder, Node beanNode) {
        def origClassName = "org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase";
        def modifiedClassName = "";
        if(beanNode?.property?.find{ "documentAuthorizerClass".equals(it.@name) }) {
            def className = beanNode?.property?.find { it.@name == "documentAuthorizerClass" }?.@value;
            if (StringUtils.isNotBlank(className) && className.equals(origClassName)) {
                modifiedClassName = "org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizerBase";
            }  else {
                addCommentIfNotExists(beanNode.parent(),"TODO - Add documentAuthorizerClass for bean Id: " + beanNode.@id)
            }

            createProperty(builder, "documentAuthorizerClass", modifiedClassName)
        }

    }
    /**
     * Replaces a maintainable field definition bean with a Uif-InputField bean
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformMaintainableFieldDefinitionBean(NodeBuilder builder, Node beanNode) {
        // copy and rename properties
        def mfdCopyProperties = ["required"];
        def mfdRenameProperties = ["name":"attributeName"];

        // collect attributes and replace parent node with input field
        def beanAttributes = convertBeanAttributes(beanNode, "MaintainableFieldDefinition", "Uif-InputField", [],[:], [],
                mfdCopyProperties, mfdRenameProperties, []);
        beanAttributes.putAt("parent", "Uif-InputField");
        builder.bean(beanAttributes) {
            copyProperties(delegate, beanNode, mfdCopyProperties)
            renameProperties(delegate, beanNode, mfdRenameProperties)
        }
    }

}
