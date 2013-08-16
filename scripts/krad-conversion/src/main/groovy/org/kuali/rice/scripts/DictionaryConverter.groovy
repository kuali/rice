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
import org.kuali.rice.scripts.beans.BusinessObjectEntryBeanTransformer
import org.kuali.rice.scripts.beans.InquiryDefinitionBeanTransformer
import org.kuali.rice.scripts.beans.LookupDefinitionBeanTransformer
import org.kuali.rice.scripts.beans.MaintenanceDocumentEntryBeanTransformer

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

    // namespace schema (p and xsi)
    def pNamespaceSchema
    def xsiNamespaceSchema

    // conversion related patterns
    // process xml pattern used for which spring xml files to process
    def processInclPattern

    // spring xml include pattern and uif lib exclude pattern used for files for pre-processing
    def preprocessInclPattern
    def preprocessExclPattern

    // spring bean maps (id: dataObject, id, parent) preloaded before conversion
    Map<String, String> definitionDataObjects = [:];
    Map<String, String> parentBeans = [:];


    // contains 'transform*Bean' and 'transform*Property' methods
    @Delegate LookupDefinitionBeanTransformer lookupDefinitionBeanTransformer = new LookupDefinitionBeanTransformer();
    @Delegate InquiryDefinitionBeanTransformer inquiryDefinitionBeanTransformer = new InquiryDefinitionBeanTransformer();
    @Delegate MaintenanceDocumentEntryBeanTransformer maintenanceDocumentEntryBeanTransformer = new MaintenanceDocumentEntryBeanTransformer();
    @Delegate BusinessObjectEntryBeanTransformer businessObjectEntryBeanTransformer = new BusinessObjectEntryBeanTransformer();

    public DictionaryConverter(config) {
        init(config)

        lookupDefinitionBeanTransformer.init(config);
        inquiryDefinitionBeanTransformer.init(config);
        maintenanceDocumentEntryBeanTransformer.init(config);
        businessObjectEntryBeanTransformer.init(config);
    }


    def init(config) {
        inputDir = config.input.dir
        outputDir = config.output.dir

        inputPaths = config.input.path
        outputPaths = config.output.path

        pNamespaceSchema = config.msg_bean_schema
        xsiNamespaceSchema = config.msg_xml_schema_legacy

        preprocessInclPattern = config.pattern.dictionaryconversion.preProcessInclude;
        preprocessExclPattern = config.pattern.dictionaryconversion.preProcessExclude;
        processInclPattern = config.pattern.dictionaryconversion.processInclude;
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

        // locate all relevant spring bean files based on bean parent and properties
        def springBeanFiles = locateSpringBeanFiles(inputResourceDir, preprocessInclPattern,
                preprocessExclPattern, ["MaintenanceDocumentEntry"], ["businessObjectClass"]);
        preloadSpringData(springBeanFiles);
        processSpringBeanFiles(springBeanFiles, inputResourceDir, outputResourceDir);

    }

    /**
     * Used to gather information related to parent/child and data object relationships
     * that may not exist in current file being processed
     *
     * @param files - spring xml files to be processed
     */
    private void preloadSpringData(List<File> files) {
        files.each { File springFile ->
            Node rootNode = parseSpringXml(springFile.text);
            preloadParentBeans(rootNode);
            preloadDefinitionDataObjects(rootNode);
            lookupDefinitionBeanTransformer.definitionDataObjects = definitionDataObjects;
            inquiryDefinitionBeanTransformer.definitionDataObjects = definitionDataObjects;
            maintenanceDocumentEntryBeanTransformer.definitionDataObjects = definitionDataObjects;
            lookupDefinitionBeanTransformer.parentBeans = parentBeans;
            inquiryDefinitionBeanTransformer.parentBeans = parentBeans;
            maintenanceDocumentEntryBeanTransformer.parentBeans = parentBeans;
        }
    }

    /**
     *
     *
     * @param rootNode
     */
    protected void preloadParentBeans(def rootNode) {
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
        files.findAll { it.path =~ processInclPattern }.each { File springFile ->
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
            if (isBeanTransformable(beanNode)) {
                delegate.invokeMethod("transform" + getTransformableBeanType(beanNode).capitalize() + "Bean", [beanNode]);
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
    protected String getAlternateTransformableBeanType(String parentName) {
        def relevantBeans = ["AttributeDefinition": "BusinessObjectEntry", "AttributeReferenceDummy-genericSystemId": "BusinessObjectEntry"];
        if (relevantBeans.containsKey(parentName)) {
            return relevantBeans.get(parentName);
        }
        return parentName;
    }

    protected boolean isBeanTransformable(Node beanNode) {
        def transformType = getTransformableBeanType(beanNode);
        if (transformType != null && DictionaryConverter.metaClass.methods.find { it.name == "transform" + transformType?.capitalize() + "Bean" }) {
            return true;
        } else {
            return false;
        }
    }

    protected String getTransformableBeanType(Node beanNode) {
        def parentName = beanNode.@parent;
        def transformType = parentName;
        boolean isTransformable = false;
        while (parentName != null && !isTransformable) {
            transformType = getAlternateTransformableBeanType(parentName);
            if (DictionaryConverter.metaClass.methods.find { it.name == "transform" + transformType.capitalize() + "Bean" }) {
                isTransformable = true;
            } else {
                parentName = parentBeans[parentName];
            }
        }

        if (isTransformable) {
            return transformType
        } else {
            return beanNode.@parent
        };
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

    protected String addBlankLinesBetweenMajorBeans(String fileText) {
        // (?m) tells Java to accept the anchors ^ and $ to match at the start and end
        // of each line (otherwise they only match at the start/end of the entire string).
        return fileText.replaceAll('(?m)^  </bean>', '  </bean>\r\n');
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