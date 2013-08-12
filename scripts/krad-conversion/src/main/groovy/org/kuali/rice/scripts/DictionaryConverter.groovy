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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import groovy.xml.QName
import groovy.xml.XmlUtil
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.ClassUtils
import org.apache.commons.lang.StringUtils

import java.util.regex.Pattern

/**
 * DictionaryConverter.groovy
 *
 * A groovy class which can be used to updates KNS to KRAD. Splits the focus into
 * Business Objects, Attribute Definitions, Maintenance (and Transactional) Documents
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class DictionaryConverter {

    public static String OUTPUT_CONV_FILE_PREFIX = "KradConv";

    def config

    // directory and path structure
    def inputDir = ""
    def inputPaths = [:]

    def outputDir = ""
    def outputPaths = [:]

    // dictionary properties transform map
    def ddPropertiesMap

    // control definition transform map
    def ddBeanControlMap

    // bean property removal list
    def ddPropertiesRemoveList

    // namespace schema (p and xsi)
    def pNamespaceSchema
    def xsiNamespaceSchema

    Map<String, String> definitionDataObjects = [:];
    Map<String, String> parentBeans = [:];

    public DictionaryConverter(config) {
        init(config)
    }

    def init(config) {
        inputDir = config.input.dir
        outputDir = config.output.dir

        inputPaths = config.input.path
        outputPaths = config.output.path

        ddPropertiesMap = config.map.convert.dd_prop
        ddBeanControlMap = config.map.convert.dd_bean_control
        ddPropertiesRemoveList = config.list.remove.dd_beans
        pNamespaceSchema = config.msg_bean_schema
        xsiNamespaceSchema = config.msg_xml_schema_legacy
    }


    /**
     * loads properties and runs through DataDictionary beans and related Maintenance Document beans
     * generating a new maintenance document
     */
    public void convertDataDictionaryFiles() {
        // Load Configurable Properties
        log.finer "finished loading config files";

        def inputResourceDir = FilenameUtils.normalize(inputDir, true) + inputPaths.src.resources;
        def outputResourceDir = FilenameUtils.normalize(outputDir, true) + outputPaths.src.resources;

        def xmlInclPattern = ~/.*\.xml$/;
        def uifLibExclPattern = ~/uif\/library/;

        // locate all relevant spring bean files based on bean parent and properties
        def springBeanFiles = locateSpringBeanFiles(inputResourceDir, xmlInclPattern,
                uifLibExclPattern, ["MaintenanceDocumentEntry"], ["businessObjectClass"]);
        preloadSpringData(springBeanFiles);
        processSpringBeanFiles(springBeanFiles, inputResourceDir, outputResourceDir);

    }

    /**
     * Used to gather information related to parent/child and data object relationships
     * that may not exist in current file being processed
     *
     * @param files
     */
    private void preloadSpringData(List<File> files) {
        files.each { File springFile ->
            Node rootNode = parseSpringXml(springFile.text);
            preloadParentBeans(rootNode);
            preloadDefinitionDataObjects(rootNode);
        }
    }

    private void preloadParentBeans(def rootNode) {
        rootNode.bean.each { parentBeans.put(it.@id, it.@parent) }
    }

    /**
     * Used to track business object classes and their relationship to
     * lookup and inquiry definitions
     *
     * @param rootNode
     */
    private void preloadDefinitionDataObjects(def rootNode) {
        rootNode?.bean?.findAll { "BusinessObjectEntry".equals(it.@parent) }.each {
            def dataObjectName = "";
            it.property.findAll { "businessObjectClass".equals(it.@name) }.each {
                dataObjectName = it.@value;
            }

            it.property.findAll { ["inquiryDefinition", "lookupDefinition"].contains(it.@name) }.each {
                if (it.bean?.@parent) {
                    definitionDataObjects.put(it.bean?.@parent, dataObjectName);
                } else if (it.ref?.@bean) {
                    definitionDataObjects.put(it.ref.@bean[0], dataObjectName);
                }

            }
        }
    }

    /**
     * process through all spring files and convert their beans into appropriate
     * krad equivalent
     *
     * @param files
     * @param inputBaseDir
     * @param outputBaseDir
     */
    protected void processSpringBeanFiles(List<File> files, String inputBaseDir, String outputBaseDir) {
        files.each { File springFile ->
            Node rootNode = parseSpringXml(springFile.text);
            if (rootNode != null) {
                transformSpringBeans(rootNode);
                String filename = FilenameUtils.normalize(outputBaseDir, true) + ConversionUtils.getRelativePath(inputBaseDir, springFile.path);
                generateSpringBeanFile(rootNode, filename, OUTPUT_CONV_FILE_PREFIX + springFile.name)
            }
        }
    }

    /**
     * If the parent is transformable based on existing methods and transforms the bean accordingly
     *
     * @param rootNode
     */
    protected void transformSpringBeans(Node rootNode) {
        rootNode.bean.each { beanNode ->
            if (isBeanTransformable(beanNode.@parent)) {
                delegate.invokeMethod("transform" + getTransformableBeanType(beanNode.@parent).capitalize() + "Bean", [beanNode]);
            }
        }
    }

    /**
     * checks against mapping to confirm beanType can be converted into relevant krad counterpart
     * If not found, returns empty string
     *
     * @param parentName
     * @return
     */
    protected String getTransformableBeanType(String parentName) {
        def relevantBeans = ["AttributeDefinition": "BusinessObjectEntry", "AttributeReferenceDummy-genericSystemId": "BusinessObjectEntry"];
        if (relevantBeans.containsKey(parentName)) {
            return relevantBeans.get(parentName);
        }
        return parentName;
    }


    /**
     * Checks if parent has a transform bean method or is mapped to a relevant transform bean method
     *
     * @param parentName
     * @return
     */
    protected boolean isBeanTransformable(String parentName) {
        parentName = getTransformableBeanType(parentName);
        if (DictionaryConverter.metaClass.methods.find { it.name == "transform" + parentName.capitalize() + "Bean" }) {
            return true;
        }
        return false;
    }

    /**
     * tests if transform property method exists for property name
     *
     * @param propertyName
     * @return
     */
    protected boolean isPropertyTransformable(String propertyName) {
        if (DictionaryConverter.metaClass.methods.find { it.name == "transform" + propertyName.capitalize() + "Property" }) {
            return true;
        }
        return false;
    }

    /**
     * Used to find reusuable property types tranform methods
     *
     * @param propertyName
     * @return
     */
    protected String getTransformablePropertyType(String propertyName) {
        return propertyName;
    }

    /**
     * locate spring xml files and filters based on bean and property values inside the  file
     *
     * @param srcPath
     * @param inclPatterns
     * @param exclPatterns
     * @param inclBeanType
     * @param inclPropType
     * @return
     */
    def locateSpringBeanFiles(String srcPath, Pattern includeFilePattern, Pattern excludeFilePattern, List<String> inclBeanTypes, List<String> inclPropTypes) {
        def fileList = [];
        log.finer "lookup path for " + srcPath;
        def patternResultList = ConversionUtils.findFilesByPattern(srcPath, includeFilePattern, excludeFilePattern)

        patternResultList.each { resultFile ->
            try {
                def ddRootNode = parseSpringXml(resultFile.text);
                if (ddRootNode.bean.find { inclBeanTypes.contains(it.@parent) } || ddRootNode.bean.property.find { inclPropTypes.contains(it.@name) }) {
                    log.finer "processing file path " + resultFile.path + " for IBT " + inclBeanTypes + " or IPT " + inclPropTypes;
                    fileList << resultFile;
                }
            } catch (Exception e) {
                log.info "failed loading " + resultFile.path + "\n" + e.message + "\n---\n" + resultFile.text + "\n----\n";
            }
        }
        return fileList;
    }

    /**
     * Simple spring xml parser
     *
     * @param inputText
     * @return
     */
    def parseSpringXml(String inputText) {
        inputText = inputText.replaceFirst(/(?ms)^(\<\!.*?--\>\s*)/, "");
        def springBeanRootNode = new XmlParser().parseText(inputText);
        return springBeanRootNode;
    }

    /**
     * formats spring root node into xml and saves to file
     *
     * @param rootBean
     * @param outputFile
     */
    private void generateSpringBeanFile(rootBean, path, filename) {
        try {
            def writer = new StringWriter();
            XmlUtil.serialize(rootBean, writer);
            def result = writer.toString();
            result = addBlankLinesBetweenMajorBeans(result);
            result = fixComments(result);
            ConversionUtils.buildFile(path, filename, result);
        } catch (FileNotFoundException ex) {
            log.info "unable to generate output for " + outputFile.name;
            errorText();
        }
    }

    /**
     * Processes BusinessObjectEntry into DataObjectEntry and can be used on attribute definitions
     *
     * @param beanNode
     * @return
     */
    def transformBusinessObjectEntryBean(Node beanNode) {
        if (beanNode?.@parent == "BusinessObjectEntry") {
            beanNode.@parent = "DataObjectEntry";
        }
        transformControlProperty(beanNode, ddBeanControlMap);
        this.removeProperties(beanNode, ddPropertiesRemoveList);
        this.renameProperties(beanNode, ddPropertiesMap);
        renamePropertyBeans(beanNode, ddPropertiesMap, true);

        return beanNode
    }

    private void transformTitleAttribute(beanNode) {
        def titleAttrBeanNode = beanNode.property.find { it.@name == "titleAttribute" }
        if (titleAttrBeanNode != null) {
            titleAttrBeanNode.replaceNode {
                property(name: "titleAttribute", value: titleAttrBeanNode.@value)
                property(name: "primaryKeys") {
                    list {
                        value(titleAttrBeanNode.@value)
                    }
                }
            }
        }
    }

    /**
     * Modifies control and controlField elements into Uif Control elements
     *
     * @param beanNode
     * @param renamedControlBeans
     * @return
     */
    def transformControlProperty(def beanNode, Map<String, String> renamedControlBeans) {
        if (beanNode.property.findAll { "control".equals(it.@name) }.size() > 0) {
            Node beanProperty = beanNode.property.find { "control".equals(it.@name) };
            String controlDefParent = beanProperty.bean[0].@parent.toString();
            if (beanProperty != null) {
                if (beanNode.property.findAll { ["control", "controlField"].contains(it.@name) }.size() == 2) {
                    this.removeProperties(beanNode, ["control"]);
                } else {
                    beanProperty.@parent = "controlField"; // rename property as control field
                    beanProperty.replaceNode {
                        if (renamedControlBeans.get(controlDefParent) != null) {
                            property(name: "controlField") {
                                transformControlDefinitionBean(delegate, beanProperty.bean[0], renamedControlBeans)
                            }
                        }
                    }

                    if ("Uif-DropdownControl".equals(renamedControlBeans.get(controlDefParent))) {
                        beanProperty.plus {
                            property(name: "optionsFinder") {
                                bean(class: "org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Used for tranforming control definitions into control field properties
     *
     * @param builder
     * @param controlDefBean
     * @param controlDefReplacements
     * @return
     */
    def transformControlDefinitionBean(NodeBuilder builder, Node controlDefBean, Map<String, String> controlDefReplacements) {
        String controlDefParent = controlDefBean.@parent.toString();
        if (controlDefReplacements[controlDefParent] != null && controlDefReplacements[controlDefParent] == "Uif-DropdownControl") {
            builder.bean(parent: "Uif-DropdownControl")
        } else if (controlDefReplacements[controlDefParent] != null && controlDefReplacements[controlDefParent] == "Uif-TextAreaControl") {
            //
        } else if (controlDefReplacements[controlDefParent] != null) {
            builder.bean(parent: controlDefReplacements[controlDefParent])
        } else {
            builder.bean(parent: "Uif-" + controlDefParent.replace("Definition", ""))
        }
    }

    /**
     * reformats to handle excess p:namespace schemas in the xml
     *
     * @param fileText
     * @return
     */
    def modifyBeanSchema(String fileText) {
        // replace and reinsert namespace spring property namespace
        fileText = fileText.replace("xmlns:p=" + "\"$pNamespaceSchema\"", "")
        fileText = fileText.replace(xsiNamespaceSchema, "$xsiNamespaceSchema xmlns:p=" + "\"$pNamespaceSchema\"")
        return fileText
    }

    /**
     * @param beanNode
     */
    def transformInquiryDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode)
        def busObjClassQualName = getObjectClassName(beanNode);
        def busObjName = ClassUtils.getShortClassName(busObjClassQualName);
        def inquiryParentBeanNode = beanNode
        def titlePropNode = inquiryParentBeanNode.property.find { it.@name == "title" }
        def inquirySectionsPropertyNode = inquiryParentBeanNode.property.find { it.@name == "inquirySections" }
        log.finer "transform bean node for inquiry"
        beanNode.replaceNode {
            addComment(delegate, "Inquiry View")
            bean(id: "$busObjName-InquiryView", parent: "Uif-InquiryView") {
                renameProperties(delegate, beanNode, ["title": "headerText"])
                addViewNameProperty(delegate, titlePropNode.@value)
                property(name: "dataObjectClassName", value: busObjClassQualName)
                transformInquirySectionsProperty(delegate, beanNode)
            }
        }
    }

    /**
     * to be used on inplace beans and refs
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
     * produces Uif-LookupView based on information in LookupDefinition
     *
     * @param beanNode
     */
    def transformLookupDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode);
        def lookupDefParentBeanNode = beanNode
        def lookupTitle = lookupDefParentBeanNode.property.find { it.@name == "title" }.@value;

        def objClassName = getObjectClassName(lookupDefParentBeanNode);
        def objName = ClassUtils.getShortClassName(objClassName);
        beanNode.replaceNode {
            addComment(delegate, "Lookup View")
            bean(id: "$objName-LookupView", parent: "Uif-LookupView") {
                property(name: "headerText", value: lookupTitle)
                addViewNameProperty(delegate, lookupTitle)
                property(name: "dataObjectClassName", value: objClassName)
                transformMenubarProperty(delegate, beanNode)
                transformDefaultSortProperty(delegate, beanNode)
                transformLookupFieldsProperty(delegate, beanNode)
                transformResultFieldsProperty(delegate, beanNode)
            }
        }
    }

    def transformMaintenanceDocumentEntryBean(Node beanNode) {
        String objName = getMaintenanceDocumentObjectName(beanNode);
        def copyProps = ["businessObjectClass", "maintainableClass", "documentTypeName", "documentAuthorizerClass", "lockingKeys"];
        if (beanNode.@parent == "MaintenanceDocumentEntry") {
            def maintDocParentBeanNode = beanNode;
            def titlePropNode = maintDocParentBeanNode.property.find { it.@name == "title" }
            def maintSectPropNode = maintDocParentBeanNode.property.find { it.@name == "maintainableSections" }
            beanNode.replaceNode {
                bean(id: objName + "MaintenanceDocument", parent: "MaintenanceDocumentEntry") {
                    copyProperties(delegate, beanNode, copyProps)
                }
            }

            beanNode.replaceNode {
                addComment(delegate, "Maintenance View")
                bean(id: "$objName-MaintenanceView", parent: "Uif-MaintenanceView") {
                    renameProperties(delegate, maintDocParentBeanNode, ["title": "headerText", "businessObjectClass": "dataObjectClassName"])
                    addViewNameProperty(delegate, titlePropNode?.@value)
                    transformMaintainableSectionsProperty(delegate, maintDocParentBeanNode)
                }
            }

        }
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

    /**
     * transform lookup field properties into criteria fields
     *
     * @param builder
     * @param beanNode
     */
    def transformLookupFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["lookupFields": "criteriaFields"], attributeNameAttrCondition, lookupCriteriaFieldBeanTransform);
    }

    /**
     * transform inquiry field properties into input fields
     *
     * @param builder
     * @param beanNode
     */
    def transformInquiryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["inquiryFields": "items"], attributeNameAttrCondition, inputFieldBeanTransform);
    }


    def transformMaintainableItemsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["maintainableItems": "items"], nameAttrCondition, inputFieldBeanTransform);
    }

    def transformMaintainableFieldsProperty(NodeBuilder builder, Node beanNode) {
        def maintainableItemsProperty = beanNode.property.find { "maintainableItems".equals(it.@name) };

        maintainableItemsProperty.list.bean.each { maintItemBean ->
            builder.property(name: "items") {
                list {
                    if ("MaintainableCollectionDefinition".equals(maintItemBean.@parent)) {
                        def maintFields = maintItemBean.property.find { "maintainableFields".equals(it.@name) };
                        maintFields.list.bean.each { fieldBean ->
                            fieldBean.attributes().each { key, value ->
                                if (nameAttrCondition(key, value)) {
                                    attributeFieldBeanTransform(builder, value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getNodeString(Node rootNode) {
        def writer = new StringWriter()
        XmlUtil.serialize(rootNode, writer)
        return writer.toString()
    }

    /**
     * transform summary field properties into values
     *
     * @param builder
     * @param beanNode
     */
    def transformSummaryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["summaryFields": "layoutManager.summaryFields"], attributeNameAttrCondition, valueFieldTransform);
    }

    /**
     * transform result field properties into data fields
     *
     * @param builder
     * @param beanNode
     */
    def transformResultFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["resultFields": "resultFields"], attributeNameAttrCondition, dataFieldBeanTransform);
    }

    def transformTitleProperty(NodeBuilder builder, Node node) {
        if (node != null) {
            builder.property(name: "headerText", value: node.@value)
        }
    }

    def addViewNameProperty(NodeBuilder builder, String viewName) {
        createProperty(builder, "viewName", viewName?.replaceAll(~/\s/, '-'))
    }

    def transformMenubarProperty(NodeBuilder builder, Node node) {
        def menubarPropertyNode = node.property.find { it.@name == "menubar" };
        if (menubarPropertyNode != null) {
            builder.property(name: "page.header.lowerGroup.items") {
                list(merge: "true") {
                    bean(parent: "Uif-Message") {
                        property(name: "messageText", value: "[" + menubarPropertyNode.@value + "]");
                    }
                }
            }
        }
    }

    def transformDefaultSortProperty(NodeBuilder builder, Node node) {
        def defaultSortPropertyNode = node.property.find { it.@name == "defaultSort" };
        if (defaultSortPropertyNode) {
            defaultSortPropertyNode.bean.each { sortDefinitionBean ->
                def sortAscendingPropertyNode = sortDefinitionBean.find {it.@name == "sortAscending"};
                if (sortAscendingPropertyNode != null) {
                    builder.property(name: "defaultSortAscending", value: sortAscendingPropertyNode.@value);
                }
                transformPropertyValueList(builder, sortDefinitionBean, ["attributeNames": "defaultSortAttributeNames"], valueFieldTransform);
            }
        }
    }

    // bean utilities

    public void renamePropertyBeans(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedBeanNames) {
        beanNode.property.each { beanProperty ->
            beanProperty.beans.each { propertyBeans ->
                if (renamedBeanNames.containsKey(propertyBeans.@parent)) {
                    propertyBeans.@parent = renamedBeanNames.get(propertyBeans.@parent);
                }
            }
        }
    }

    public void renamePropertyBeans(Node beanNode, Map<String, String> renamedBeanNames, boolean useStub) {
        beanNode.property.bean.findAll { renamedBeanNames.containsKey(it.@parent) }.each { replaceBean ->
            String replaceBeanParent = replaceBean.@parent;
            if (useStub) {
                replaceBean.replaceNode {
                    bean(parent: renamedBeanNames.get(replaceBeanParent))
                }
            } else {
                replaceBean.@parent = renamedBeanNames.get(replaceBeanParent);
            }
        }
    }

    public void removeChildrenBeans(Node beanNode) {
        removeChildrenBeans(beanNode, beanNode.@id);
    }

    /**
     * removes children beans from xml
     *
     * @param beanNode
     */
    public void removeChildrenBeans(Node beanNode, String beanId) {
        // loop through the nodes til you reach the root
        def rootNode = beanNode;
        while (rootNode instanceof Node && rootNode.parent() != null) {
            rootNode = rootNode.parent();
        }

        rootNode.bean.findAll { it.@parent == beanNode.@id }.each { childNode ->
            if (childNode != null) {
                childNode.replaceNode({})
            }
        }
    }

    /**
     * transform property bean list into new property beans
     *
     * @param builder
     * @param beanNode
     */
    def transformPropertyBeanList(NodeBuilder builder, Node beanNode, Map<String, String> replaceProperties, Closure attrCondition, Closure beanTransform) {
        beanNode.property.findAll { replaceProperties.keySet().contains(it.@name) }.each { propertyNode ->
            builder.property(name: replaceProperties.get(propertyNode.@name)) {
                list {
                    propertyNode.list.bean.each { innerBean ->
                        def attributeValue;
                        innerBean.attributes().each { key, value ->
                            if (attrCondition(key, value)) {
                                attributeValue = value
                            }
                        }

                        beanTransform(builder, attributeValue)
                    }
                }
            }
        }
    }

    def transformPropertyValueList (NodeBuilder builder, Node beanNode, Map<String, String> replaceProperties, Closure beanTransform) {
        beanNode.property.findAll { replaceProperties.keySet().contains(it.@name) }.each { propertyNode ->
            builder.property(name: replaceProperties.get(propertyNode.@name)) {
                list {
                    propertyNode.list.value.each { value ->
                        beanTransform(builder, value.text())
                    }
                }
            }
        }
    }

    // helper closures

    def attributeNameAttrCondition = {
        key, value -> key.toString().endsWith("attributeName") }

    def nameAttrCondition = {
        key, value -> key.toString().endsWith("name") }

    def genericBeanTransform = {
        builderDelegate, beanParent, attrValue -> builderDelegate.bean('xmlns:p': pNamespaceSchema, parent: beanParent, 'p:propertyName': attrValue) }

    def inputFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-InputField', attrValue) }

    def attributeFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'AttributeField', attrValue) }

    def dataFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-DataField', attrValue) }

    def lookupCriteriaFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-LookupCriteriaInputField', attrValue) }

    def valueFieldTransform = {
        builderDelegate, attrValue -> builderDelegate.value(attrValue) }

    // property utilities

    public void copyProperties(NodeBuilder builderDelegate, Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { beanProperty -> builderDelegate.property(name: beanProperty.@name, value: beanProperty.@value) }
    }

    public void renameProperties(Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.property.findAll { renamedPropertyNames.containsKey(it.@name) }.each { beanProperty -> beanProperty.@name = renamedPropertyNames.get(beanProperty.@name) }
    }

    public void renameProperties(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.property.each { beanProperty ->
            if (renamedPropertyNames.containsKey(beanProperty.@name)) {
                builderDelegate.property(name: renamedPropertyNames.get(beanProperty.@name), value: beanProperty.@value)
            }
        }
    }

    public void removeProperties(Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { Node beanProperty -> beanProperty.replaceNode {} }
    }

    // other utilities

    private String getObjectClassName(Node beanNode) {
        String definitionName = beanNode.@id;
        String definitionChildName = StringUtils.removeEnd(definitionName, "-parentBean");
        String objClassName = "";
        if (definitionDataObjects.get(definitionName)) {
            objClassName = definitionDataObjects.get(definitionName);
        } else if (definitionDataObjects.get(definitionChildName)) {
            objClassName = definitionDataObjects.get(definitionChildName);
        } else if (beanNode.property.find { it.@name == "businessObjectClass" }) {
            objClassName = beanNode.property.find { it.@name == "businessObjectClass" }?.@value;
        }
        return objClassName;
    }

    private String getMaintenanceDocumentObjectName(Node beanNode) {
        return beanNode.@id.toString().replaceFirst("-parentBean", "").replaceFirst("MaintenanceDocument", "");
    }

    /**
     * replaces namespace properties (p:name) with a property tag
     * Allows transformation scripts to handle property tags properly
     *
     * @param beanNode
     */
    def fixNamespaceProperties(beanNode) {
        def count = 0;
        log.finer "loading " + beanNode.attributes()
        def remAttrs = []
        if (beanNode.attributes()) {
            def attrs = beanNode.attributes()

            attrs.keySet().each {
                count++
                log.finer "adding property: " + it + " " + it.class.name
                if (it instanceof QName) {
                    beanNode.appendNode("property", [name: it.getLocalPart(), value: attrs.get(it)])
                    remAttrs.add(it)
                }
            }
            remAttrs.each { beanNode.attributes().remove(it) }
        }
        log.finer "finishing fix properties: " + beanNode
    }

    protected void createProperty(NodeBuilder builder, String name, String value) {
        if (value != null) {
            builder.property(name: name, value: value);
        }
    }

    /**
     * used to add comments; current implementation uses meta tags in place of standard
     * comments (node.plus and the xml serialize did not handle xml comments well)
     *
     * @param builder
     * @param comment
     * @return
     */
    def addComment(NodeBuilder builder, String comment) {
        if (comment != null) {
            builder.meta(key: "comment", value: comment)
        }
    }

    protected String addBlankLinesBetweenMajorBeans(String fileText) {
        return fileText.replaceAll('  </bean>', '  </bean>\r\n');
    }

    protected String fixComments(String fileText) {
        return fileText.replaceAll(/<meta key="comment" value="(.*?)"\/>/, '<!-- $1 -->\r\n');
    }

    /**
     * @deprecated
     */
    def errorText() {
        log.info("=====================\nFatal Error in Script\n=====================\n")
        System.exit(2)
    }

}