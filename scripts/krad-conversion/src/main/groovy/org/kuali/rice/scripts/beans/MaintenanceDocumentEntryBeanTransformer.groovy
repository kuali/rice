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
import org.apache.commons.lang.StringUtils
import org.kuali.rice.scripts.ConversionUtils

import javax.xml.namespace.QName

/**
 * This class transforms maintenance document entry beans and its properties/children beans into their uif equivalent
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class MaintenanceDocumentEntryBeanTransformer extends SpringBeanTransformer {

    String maintenanceDefinitionBeanType = "MaintenanceDocumentEntry";
    String maintenanceDocEntryBeanType = "uifMaintenanceDocumentEntry";
    String maintenanceViewBeanType = "Uif-MaintenanceView";

    // MDE Conversion Components
    def mdeCopyProperties = ["businessRulesClass", "maintainableClass", "documentTypeName",
             "lockingKeys", "allowsRecordDeletion", "preserveLockingKeysOnCopy","allowsNewOrCopy","documentClass"];
    def mdeRenameProperties = ["businessObjectClass": "dataObjectClass"];
    def mdeIgnoreOnCarryoverProperties = ["documentAuthorizerClass","documentPresentationControllerClass",
             "webScriptFiles","maintainableSections"]
    def mdeIgnoreOnCarryoverAttributes = []

    // UMV Conversion Components - include
    def umvCopyProperties = []
    def umvRenameProperties = ["title": "headerText", "businessObjectClass": "dataObjectClassName"] // , "dataObjectClass": "dataObjectClassName"
    def umvIgnoreOnCarryoverProperties = ["title", "maintainableClass", "lockingKeys","maintainableSections",
            "documentAuthorizerClass","documentPresentationControllerClass","webScriptFiles", "documentTypeName"]
    def umvIgnoreOnCarryoverAttributes = []

    String currentDataObjectClassName = "";

    /**
     * Transforms maintenance document entry which results in an updated document entry
     * and a maintenance View
     *
     * @param beanNode
     * @return
     */
    def transformMaintenanceDocumentEntryBean(Node beanNode) {
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
            beanNode?.replaceNode {
                addCommentIfNotExists(beanNode.parent(), "Maintenance View")
                findDataObjectClass(beanNode);
                bean(umvBeanAttributes) {
                    copyBeanProperties(delegate, beanNode, umvCopyProperties + umvCarryoverProperties)
                    renameProperties(delegate, maintDocParentBeanNode, umvRenameProperties)
                    transformTitleProperty(delegate, beanNode);
                    transformWebScriptFilesProperty(delegate, beanNode);
                    transformMaintainableSectionsProperty(delegate, beanNode)
                }
                addCommentIfNotExists(beanNode.parent(), "Maintenance Document Entry")
                bean(mdeBeanAttributes) {
                    copyBeanProperties(delegate, beanNode, mdeCopyProperties + mdeCarryoverProperties) ;
                    renameProperties(delegate, beanNode, mdeRenameProperties) ;
                    transformDocumentAuthorizerClassProperty(delegate, beanNode);
                    transformDocumentPresentationControllerClassProperty(delegate, beanNode);

                }
            }
        }
        return beanNode;
    }

    def findCarryoverProperties(Node beanNode, def copyPropertiesList, def renamePropertiesList, def ignorePropertiesList) {
        def carryoverPropertiesList = []
        if (useCarryoverProperties && beanNode?.property?.size() > 0) {
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
            def maintainableBeanItems = beanNode.property.list.bean;
            transformMaintainableItems(builder, beanNode, maintainableBeanItems, beanNode.@id);
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
            renameProperties(delegate, beanNode, ["title": "headerText", "defaultOpen": "disclosure.defaultOpen" ]);
            transformHelpUrlProperty(delegate, beanNode);
            property(name: "items") {
                list {
                    beanNode.property.find { it.@name == "maintainableItems" }.list.bean.each { beanItem ->
                        if (!"MaintainableCollectionDefinition".equals(beanItem.@parent)) {
                            itemsList.add(beanItem);
                        } else {
                            transformMaintainableItems(builder, beanNode, itemsList, null);
                            itemsList = [];
                            builder.bean(parent: 'Uif-MaintenanceStackedCollectionSection') {
                                copyBeanProperties(delegate, beanItem, ["collectionObjectClass", "propertyName"]);
                                renameProperties(delegate, beanItem, ["businessObjectClass": "collectionObjectClass",
                                    "name": "propertyName"]);
                                renameProperties(delegate, beanItem, ["defaultOpen": "disclosure.defaultOpen"]);
                                transformMaintainableFieldsProperty(delegate, beanItem);
                                transformSummaryFieldsProperty(delegate, beanNode);
                                transformIncludeAddLineProperty(delegate,beanItem);
                                transformAlwaysAllowCollectionDeletion(delegate, beanItem)
                                transformIncludeMultipleLookupLineProperty(delegate,beanItem);
                                transformDuplicateIdentificationFieldsProperty(delegate,beanItem);

                            }
                        }
                    }

                    transformMaintainableItems(builder, beanNode, itemsList, null);
                    itemsList = [];
                }
            }
        }
    }

    /**
     * Transforms a list of MaintainableFieldDefinitions into a Uif-MaintenanceGridSection
     *
     * @param builder
     * @param beanNode
     * @param beanItems  List of MaintainableFieldDefinition inside of maintainableItems
     * @param beanId Bean Id to be added to the new Uif-MaintenanceGridSection
     * @return
     */
    def transformMaintainableItems(NodeBuilder builder, Node beanNode, List beanItems, String beanId) {
        if (beanItems.size() > 0) {
            def beanAttributes = [parent: 'Uif-MaintenanceGridSection'];
            if (beanId) {
                beanAttributes.put("id", beanId);
            }

            builder.bean(beanAttributes) {
                copyBeanProperties(delegate, beanNode, ["collectionObjectClass", "propertyName"]);

                renameProperties(delegate, beanNode, ["defaultOpen": "disclosure.defaultOpen",
                        "title": "headerText" ]);
                transformHelpUrlProperty(delegate, beanNode);

                property(name: "items") {
                    list {
                        beanItems.each { maintainableField  ->
                            // Using  transform field definition instead of generic transform node
                            transformMaintainableFieldDefinitionBean(builder, maintainableField);
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
     * replaces includeAddLine property with a addLineActions property
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformIncludeAddLineProperty(NodeBuilder builder, Node beanNode) {
        if(beanNode?.property?.findAll { "includeAddLine".equals(it.@name) }?.size > 0) {
            String includeAddLine = beanNode?.property?.find  { "includeAddLine".equals(it.@name) }.@value;

            builder.property(name:"addLineActions") {
                list('xmlns:p':'http://www.springframework.org/schema/p') {
                    bean (parent:"Uif-SecondaryActionButton-Small",'p:methodToCall':"addLine" ,'p:actionLabel':"add",'p:hidden': !(includeAddLine)  )
                }
            }
        }
    }

    /**
     *  KNS's default is that only newly added lines can be deleted while in KRAD all lines can be deleted by default.
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformAlwaysAllowCollectionDeletion(NodeBuilder builder, Node beanNode) {
        if(beanNode?.property?.findAll { "alwaysAllowCollectionDeletion".equals(it.@name) }?.size > 0) {

            boolean alwaysAllowCollectionDeletion = Boolean.parseBoolean(beanNode?.property?.find  { "alwaysAllowCollectionDeletion".equals(it.@name) }.@value);

            if (!alwaysAllowCollectionDeletion) {
                addUifDeleteLineAction(builder)
            }
        } else {
            // KNS's default is that only newly added lines can be deleted while in KRAD all lines can be deleted by default.
            addUifDeleteLineAction(builder)
        }
    }

    /**
     * Add Uif-DeleteLineAction.
     *
     * @param builder
     * @return
     */
    def addUifDeleteLineAction(NodeBuilder builder) {
        builder.property(name: "lineActions") {
            list('xmlns:p': 'http://www.springframework.org/schema/p') {
                bean(parent: "Uif-DeleteLineAction", 'p:render': "@{isAddedCollectionItem(#line)}")
                bean(parent: "Uif-SaveLineAction")
            }
        }
    }

    /**
     * Transforms includeMultipleLookupLine property with a collectionLookup property initialized with the
     * Uif-CollectionQuickfinder bean. sourceClassName is translated to dataObjectClassName if specified and
     * includeMultipleLookupLine is not set to false.  templates are translated to field conversions.
     *
     * @param builder
     * @param beanNode
     * @return
     *
     */
    def transformIncludeMultipleLookupLineProperty(NodeBuilder builder, Node beanNode) {
        String includeMultipleLookupLine = "";
        if(beanNode?.property?.findAll { "includeMultipleLookupLine".equals(it.@name) }?.size > 0) {
            includeMultipleLookupLine = beanNode?.property?.find  { "includeMultipleLookupLine".equals(it.@name) }.@value;
        }
        if(includeMultipleLookupLine == null || !includeMultipleLookupLine.equalsIgnoreCase("false")) {
            String dataObjectClassName = beanNode?.property?.find  { "sourceClassName".equals(it.@name) }?.@value;

            if(dataObjectClassName == null || dataObjectClassName.empty)   {
                dataObjectClassName = beanNode?.property?.find  { "businessObjectClass".equals(it.@name) }.@value
            }
            def maintainableFieldsProperty = beanNode.property.find { "maintainableFields".equals(it.@name) };
            String fieldConversion = "";
            if (maintainableFieldsProperty) {

                maintainableFieldsProperty.list.bean.each { fieldBean ->
                    def attrPropMap = gatherPropertyTagsAndPropertyAttributes(fieldBean, ["*name":"name","*template":"template"]);
                    String name = attrPropMap?.get("name");
                    String template = attrPropMap?.get("template");
                    if(template == null || name.equals(template))   {
                        fieldConversion += name + ":" + name + ",";
                    } else {
                        fieldConversion += name + ":" + template + "," ;
                    }
                }

                if(fieldConversion.length() > 1) {
                    fieldConversion = fieldConversion.substring(0,fieldConversion.lastIndexOf(','))
                }
            }

            builder.property(name:"collectionLookup") {
                bean(parent:"Uif-CollectionQuickFinder") {
                    property(name:"dataObjectClassName", value:dataObjectClassName )
                    property(name:"fieldConversions", value:fieldConversion )

                }
            }
        }
    }

    /**
     * Transforms duplicateIdentificationFields property to duplicateLinePropertyName. The fields that should be used to
     * check for duplicate records are converted in a list of  property names
     *
     * @param builder
     * @param beanNode
     * @return
     *
     */

    def transformDuplicateIdentificationFieldsProperty(NodeBuilder builder, Node beanNode) {
        if (beanNode?.property?.findAll { "duplicateIdentificationFields".equals(it.@name) }?.size > 0) {
            def duplicateIdentificationFieldsProperty = beanNode.property.find { "duplicateIdentificationFields".equals(it.@name) };
            builder.property(name: "duplicateLinePropertyNames") {
                list {
                    if (duplicateIdentificationFieldsProperty) {

                        duplicateIdentificationFieldsProperty.list.bean.each { fieldBean ->
                            def attrPropMap = gatherPropertyTagsAndPropertyAttributes(fieldBean, ["*name": "name"]);
                            String name = attrPropMap?.get("name");
                            builder.createNode("value", null, name);
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
        def maintainableItemsProperty = beanNode.property.find { "maintainableItems".equals(it.@name) };
        if (maintainableItemsProperty) {
            builder.property(name: "items") {
                list {
                    maintainableItemsProperty.list.bean.each { itemBean ->
                        transformMaintainableFieldDefinitionBean(builder, itemBean);
                    }
                }
            }
        }
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
     * Replaces webScriptFiles property with additionalScriptFiles
     *
     * @param builder
     * @param beanNode
     * @return
     */
    def transformWebScriptFilesProperty(NodeBuilder builder, Node beanNode) {
        if (beanNode?.property?.find { "webScriptFiles".equals(it.@name) }) {
            addCommentIfNotExists(beanNode.parent(), "TODO: Check if script files are still relevant and correct")
            builder.property(name: "additionalScriptFiles") {
                list {
                    beanNode?.property?.find { "webScriptFiles".equals(it?.@name) }?.list.value.each { valueNode -> builder.createNode("value", null, valueNode.text()); }

                }
            }
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
        def mfdCopyProperties = ["required", "defaultValueFinderClass","quickfinder.fieldConversions"];
        def mfdRenameProperties = ["name":"propertyName",
                "alternateDisplayAttributeName": "readOnlyDisplayReplacement",
                "additionalDisplayAttributeName": "readOnlyDisplayReplacementPropertyName",
                "overrideLookupClass": "quickfinder.dataObjectClassName",
                "overrideFieldConversions": "quickfinder.fieldConversions"];

        def mfdIgnoreProperties = ["externalHelpUrl", "unconditionallyReadOnly","readOnlyAfterAdd"];

        // collect attributes and replace parent node with input field
        def beanAttributes = convertBeanAttributes(beanNode, "MaintainableFieldDefinition", "Uif-InputField", [],[:], [],
                mfdCopyProperties, mfdRenameProperties, []);
        beanAttributes.putAt("parent", "Uif-InputField");

        String propName = getPropertyName(beanAttributes);
        if ( propName !=null ) {
            String controlDef = attributeDefinitionControls.get(currentDataObjectClassName)?.get(propName);
            if (controlDef!=null && controlDef.equals("FileControlDefinition")) {
                def templateText = buildPropertyReplacerAttachment(propName);
                def templateSpringBeanRootNode = new XmlParser(false,true).parseText(templateText);
                getBeanFromTemplate(templateSpringBeanRootNode, builder)
                return;
            }
            //KULRICE-11711
            if (controlDef!=null && controlDef.equals("LookupReadonlyControlDefinition")) {
                beanAttributes.putAt("p:windgetInputOnly", "true");
            }
        }

        builder.bean(beanAttributes) {
            renameProperties(builder, beanNode, mfdRenameProperties);
            copyBeanProperties(builder, beanNode, mfdCopyProperties);
            transformReadOnlyProperty(delegate,beanNode)
            transformWebUILeaveFieldFunctionProperty(delegate,beanNode)
        }
    }

    def findDataObjectClass (Node parentNode) {
        for (Node childNode : (NodeList)parentNode.value()) {
            boolean check = childNode.attributes().containsValue("businessObjectClass");
            if (check) {
                currentDataObjectClassName = childNode.attributes().get("value");
            }
        }
    }

    /**
     * Sets readOnly property based on maintainableFieldDefinition values for unconditionallyRead©tOnly
     * and readOnlyAfterAdd
     *
     * @param builder
     * @param beanNode
     */
    def transformReadOnlyProperty(NodeBuilder builder, Node beanNode) {
        def attrPropMap = gatherPropertyTagsAndPropertyAttributes(beanNode,
                ["unconditionallyReadOnly":"unconditionallyReadOnly",
                "readOnlyAfterAdd":"readOnlyAfterAdd"]);

        if(attrPropMap?.get("unconditionallyReadOnly")) {
            builder.property(name:"readOnly", value:attrPropMap?.get("unconditionallyReadOnly"));
        }
        else if(attrPropMap?.get("readOnlyAfterAdd")?.equals("true")) {
            builder.property(name:"readOnly", value:"@{!#isAddLine}");
        }
    }

    /**
     * Replaces WebUILeaveFieldFunction Property with onBlurScript property. Translated the call back function and
     * function parameters and a passes them to the function being called at onBlur event
     *
     * @param builder
     * @param beanNode
     * @return
     */

    def transformWebUILeaveFieldFunctionProperty(NodeBuilder builder, Node beanNode) {
        def attrPropMap = gatherPropertyTagsAndPropertyAttributes(beanNode, ["webUILeaveFieldFunction":"webUILeaveFieldFunction",
                "webUILeaveFieldCallbackFunction":"webUILeaveFieldCallbackFunction"]);

        if(attrPropMap?.get("webUILeaveFieldFunction")) {
            def webUILeaveFieldCallbackFunction = attrPropMap?.get("webUILeaveFieldCallbackFunction");
            String paramList = "this,";
            if(webUILeaveFieldCallbackFunction){
                paramList +=  webUILeaveFieldCallbackFunction + ",";
            }

            beanNode?.property?.find{"webUILeaveFieldFunctionParameters".equals(it?.@name)}?.list?.value.each { valueNode ->
                paramList += '{@' + valueNode.text() +"},"
            }

            if(paramList.length() > 1) {
                paramList = paramList.substring(0,paramList.lastIndexOf(','))
            }

            addCommentIfNotExists(beanNode,"TODO - Check if javascript is still relevant and correct")  ;
            builder.property(name:"onBlurScript", value:attrPropMap.get("webUILeaveFieldFunction") + "(" + paramList + ");");
        }
    }


    private String getPropertyName(beanAttributes) {
        QName pattern = new QName("http://www.springframework.org/schema/p","propertyName");
        for (Object key : beanAttributes.keySet()) {
            if (key.toString().equals("{http://www.springframework.org/schema/p}propertyName")) {
                return beanAttributes.get(key);
            }
        }
    }

    /**
     * builds a uif view page from the following
     * form-bean element (contains the form name and class), form title and id comes from name
     * jspRoot: contains the pages and the beans
     *
     * @param jspRoot
     * @param formClass
     * @param actionClass
     * @return
     */
    public static def buildPropertyReplacerAttachment(attachmentBinding) {
        attachmentBinding = [fileName: "PLACEHOLDER", attachmentFile: attachmentBinding]
        def fileText = ConversionUtils.buildTemplateToString(ConversionUtils.getTemplateDir(), "UifAttachment.fragment.tmpl", attachmentBinding)
        return fileText
    }

    /**
     * Helper method for extracting a bean from a beans collection
     *
     * @param templateRootNode
     * @param builder
     * @return
     */

    private def getBeanFromTemplate(Node templateRootNode, NodeBuilder builder) {
        NodeList beansList = templateRootNode.value();
        builder.createNode("bean", beansList.get(0).attributes(), beansList.get(0).value() );
    }
}