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
import groovy.xml.QName
import groovy.xml.XmlUtil
import org.apache.commons.lang.StringUtils

/**
 * This class handles basic conversion of properties
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class SpringBeanTransformer {

    public static String PARENT_BEAN_SUFFIX = "-parentBean";
    public static String UIF_PREFIX = "Uif-";

    // Used as the default prefix on all krad converted files
    public static String OUTPUT_CONV_FILE_PREFIX = "KradConv";

    // holds all variables
    def config;

    // dictionary properties transform map
    def ddPropertiesMap;

    // control definition transform map
    def ddBeanControlMap;

    // bean property removal list
    def ddPropertiesRemoveList;

    // namespace schema (p and xsi)
    def pNamespaceSchema;
    def xsiNamespaceSchema;

    Map<String, String> definitionDataObjects = [:];
    Map<String, String> parentBeans = [:];
    Map<String, Map<String,String>> attributeDefinitionControls = [:];

    def useCarryoverAttributes;
    def useCarryoverProperties;
    def replacePropertyDuringConversion;
    boolean maintainBusinessObjectStructure = false;
    def controlPropertiesMap;
    def validationPatternMap;
    def validationPatternPropertiesMap;

    def init(config) {
        ddPropertiesMap = config.map.convert.dd_prop
        ddBeanControlMap = config.map.convert.dd_bean_control
        ddPropertiesRemoveList = config.list.remove.dd_beans
        pNamespaceSchema = config.msg_bean_schema
        xsiNamespaceSchema = config.msg_xml_schema_legacy
        useCarryoverAttributes = config.bool.dictionaryconversion.carryoverAttributes;
        useCarryoverProperties = config.bool.dictionaryconversion.carryoverProperties;
        controlPropertiesMap = config.map.convert_control_properties
        validationPatternMap = config.map.convert.dd_validation_patterns
        validationPatternPropertiesMap = config.map.convert_validation_pattern_properties
        replacePropertyDuringConversion = config.bool.dictionaryconversion.replaceControlProperty;
        maintainBusinessObjectStructure = config.bool.dictionaryconversion.maintainBusinessObjectStructure;
    }

    public String getTranslatedBeanId(String beanId, String originalBeanType, String transformBeanType) {
        if (originalBeanType.equals(beanId)) {
            return transformBeanType;
        }

        String translatedBeanId = beanId;
        def origBeanTypePattern = ~"(?i)${originalBeanType}";
        transformBeanType = transformBeanType.replaceFirst(UIF_PREFIX, "");

        if (beanId =~ origBeanTypePattern) {
            translatedBeanId = translatedBeanId.replaceAll(origBeanTypePattern, transformBeanType);
        } else {
            if (beanId?.contains(PARENT_BEAN_SUFFIX)) {
                translatedBeanId = translatedBeanId.replaceFirst(PARENT_BEAN_SUFFIX, "") + '-' + transformBeanType + PARENT_BEAN_SUFFIX;
            } else {
                translatedBeanId = translatedBeanId + '-' + transformBeanType;
            }
        }

        return translatedBeanId;
    }

    public String getTransformableBeanType(Node beanNode) {
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

    protected String getAlternateTransformableBeanType(String parentName) {
        def relevantBeans = ["AttributeDefinition": "BusinessObjectEntry", "AttributeReferenceDummy-genericSystemId": "BusinessObjectEntry"];
        if (relevantBeans.containsKey(parentName)) {
            return relevantBeans.get(parentName);
        }
        return parentName;
    }

    // bean utilities

    /**
     * returns the property value of a bean, null if property is not found
     *
     * @param bean
     * @param propertyName
     * @return
     */
     def getPropertyValue(Node beanNode, String propertyName) {
        def propertyValue = null;

        // get standard property value,
        propertyValue = beanNode?.property?.find { propertyName.equals(it.@name)  }?.@value;

        // if null check namespace attribute
        if (propertyValue == null) {
            propertyValue = beanNode?.attributes()?.find {
                key, value -> key instanceof QName && propertyName.equals(((QName) key).getLocalPart())
            }?.value;
        }

        return propertyValue;
    }

    /**
     *
     * @param builderDelegate
     * @param beanNode
     * @param renamedBeanNames
     */
    public void renamePropertyBeans(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedBeanNames) {
        beanNode.property.each { beanProperty ->
            beanProperty.beans.each { propertyBeans ->
                if (renamedBeanNames.containsKey(propertyBeans.@parent)) {
                    propertyBeans.@parent = renamedBeanNames.get(propertyBeans.@parent);
                }
            }
        }
    }

    /**
     * function assists with relabeled beans within a property
     * should handle individual beans and beans within a list
     *
     * TODO: make sure list case is tested
     *
     * @param beanNode
     * @param renamedBeanNames
     * @param useStub
     */
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

    /**
     * Removes the children bean of the bean its called on
     * TODO: review if this is necessary
     *
     * @param beanNode
     */
    public void removeChildrenBeans(Node beanNode) {
        removeChildrenBeans(beanNode, beanNode.@id);
    }

    /**
     * Searching the current xml structure the bean node is in and removes any children
     * any beans where the parent id matches the bean id.
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
     * Transform property bean list into new property beans
     * TODO: see if it can be merged with rename property beans
     *
     * @param builder
     * @param beanNode
     */
    def transformPropertyBeanList(NodeBuilder builder, Node parentBean, Map<String, String> properties,
            Closure gatherAttributes, Closure transformNode) {
        def relevantProperties = parentBean.property.findAll { properties.keySet().contains(it.@name) };
        relevantProperties.each { propertyNode ->
            builder.property(name: properties.get(propertyNode.@name)) {
                list {
                    propertyNode.list.bean.each { innerBean -> transformNode(builder, gatherAttributes(innerBean)); }
                }
            }
        }
    }

    def genericGatherAttributes = { Node beanNode, Map searchAttrs ->
        def attributes = [:];

        // locate attributes and special cases (i.e. '*name')
        beanNode.attributes().each { key, value ->
            if (searchAttrs.any { matchesAttr(it.key, key.toString()) }) {
                attributes.put(searchAttrs.find { matchesAttr(it.key, key.toString()) }.value, value);
            }
        }

        return attributes;
    }

    def gatherPropertyTagsAndPropertyAttributes = { Node beanNode, Map searchAttrs ->
        def attributes = gatherPropertyTags(beanNode, searchAttrs);
        // locate attributes and special cases (i.e. '*name')
        attributes += gatherPropertyAttrs(beanNode, searchAttrs);
        return attributes;
    }

    def gatherPropertyAttrs = { Node beanNode, Map searchAttrs ->
        def attributes = [:];
        // locate attributes and special cases (i.e. '*name')
        beanNode.attributes().each { key, value ->
            def keyString = key.toString();
            if(key instanceof QName) {
                keyString = ((QName)key).getLocalPart();
            }
            if (searchAttrs.any { matchesAttr(it.key, keyString) }) {
                attributes.put(searchAttrs.find { matchesAttr(it.key, keyString) }.value, value);
            }
        }
        return attributes;
    }

    def gatherPropertyTags = { Node beanNode, Map searchAttrs ->
        def attributes = [:];
        beanNode.property.each { beanProperty ->
            if (searchAttrs.any { matchesAttr(it.key, beanProperty.@name) }) {
                attributes.put(searchAttrs.find { matchesAttr(it.key, beanProperty.@name) }.value, beanProperty.@value);
            }
        }
        return attributes;
    }

    /**
     * For copying properties over if they exist without conversion.
     * (may be replaced by the
     */
    def copyGatherProperties = { Node beanNode, List copyAttrs -> return genericGatherAttributes(beanNode, copyAttrs.collectEntries { ["*" + it, "p:" + it] }); }


    /**
     * Provides a case insensitive check if a pattern matches. Search Patterns may be plain text
     * or contain a '*' prefix for wildcard prefix.
     *
     * */
    def matchesAttr(String searchPattern, String value) {
        if (searchPattern.startsWith("*")) {
            return value.toLowerCase().endsWith(searchPattern.toLowerCase().replaceFirst(/^\*/, ""));
        } else {
            return searchPattern.equalsIgnoreCase(value);
        }

    }

    /**
     * Transform property value list into a new property
     *
     * @param builder
     * @param beanNode
     * @param replaceProperties
     * @param beanTransform
     * @return
     */
    def transformPropertyValueList(NodeBuilder builder, Node beanNode, Map<String, String> replaceProperties, Closure nodeTransform) {
        beanNode.property.findAll { replaceProperties.keySet().contains(it.@name) }.each { propertyNode ->
            builder.property(name: replaceProperties.get(propertyNode.@name)) {
                list {
                    propertyNode.list.value.each { valueNode -> nodeTransform(builder, ["value": valueNode.value()]) }
                }
            }
        }
    }

    // helper closures - attribute conditional checks

    def gatherIdAttribute = { Node beanNode -> return genericGatherAttributes(beanNode, ["*id": "id"]); }
    def gatherAttributeNameAttribute = { Node beanNode -> return genericGatherAttributes(beanNode, ["*attributeName": "p:propertyName"]); }

    def gatherNameAttribute = { Node beanNode -> return genericGatherAttributes(beanNode, ["*name": "p:propertyName"]); }

    def gatherValidationPatternProperties = { Node beanNode -> return gatherPropertyTagsAndPropertyAttributes(beanNode, validationPatternPropertiesMap); }
    def gatherControlAttributes = { Node beanNode -> return genericGatherAttributes(beanNode, controlPropertiesMap); }

    // helper closures - bean transforms
    def genericNodeTransform = { NodeBuilder builderDelegate, String nodeType, Map<String, String> attributes, String value -> builderDelegate.createNode(nodeType, attributes, value); }

    def genericBeanTransform = { NodeBuilder builderDelegate, Map<String, String> attributes ->
        if (!attributes['xmlns:p']) {
            attributes.put('xmlns:p', pNamespaceSchema);
        }
        genericNodeTransform(builderDelegate, 'bean', attributes, "");
    }

    def inputFieldBeanTransform = { NodeBuilder builderDelegate, Map attributes ->
        attributes.put("parent", "Uif-InputField");
        genericBeanTransform(builderDelegate, attributes);
    }

    def attributeFieldBeanTransform = { NodeBuilder builderDelegate, Map attributes ->
        attributes.put("parent", "Uif-InputField");
        genericBeanTransform(builderDelegate, attributes);
    }

    def dataFieldBeanTransform = { NodeBuilder builderDelegate, Map attributes ->
        attributes.put("parent", "Uif-DataField");
        genericBeanTransform(builderDelegate, attributes);
    }

    def lookupCriteriaFieldBeanTransform = { NodeBuilder builderDelegate, Map attributes ->
        attributes.put("parent", "Uif-LookupCriteriaInputField");
        genericBeanTransform(builderDelegate, attributes);
    }

    def valueFieldTransform = { NodeBuilder builderDelegate, Map attributes ->
        def value = attributes["value"];
        builderDelegate.createNode("value", null, value);
    }

    def propertyNameValueFieldTransform = { NodeBuilder builderDelegate, Map attributes ->
        def value = attributes["p:propertyName"];
        builderDelegate.createNode("value", null, value);
    }

    // Property utilities
    @Deprecated
    def copyProperties(NodeBuilder builderDelegate, Node beanNode, List<String> propertyNames) {
        copyBeanProperties(builderDelegate, beanNode, propertyNames);
    }
    /**
     * Copies properties from bean to the builder delegate based on the property names list
     *
     * @param builderDelegate
     * @param beanNode
     * @param propertyNames
     * @return
     */
    def copyBeanProperties(NodeBuilder builderDelegate, Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { Node beanProperty ->
            if (beanProperty.list) {
                builderDelegate.property(beanProperty.attributes().clone()) {
                    list {
                        beanProperty.list.value.each { Node listItem -> createNode(listItem.name(), listItem.attributes(), listItem.value()) }
                    }
                }
            } else {
                builderDelegate.createNode(beanProperty.name(), beanProperty.attributes(), beanProperty.value())
            }
        }
    }

    /**
     * renames properties within the bean based on the map containing property names and their replacement
     *
     * @param beanNode
     * @param renamedPropertyNames
     * @return
     */
    def renameProperties(Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.attributes().entrySet().each { attributeProperty ->
            if (attributeProperty.key instanceof QName && renamedPropertyNames.containsKey(attributeProperty.key.localPart)) {
                attributeProperty.key.localPart = renamedPropertyNames.get(attributeProperty.key.localPart)
            }
        }
        beanNode.property.findAll { renamedPropertyNames.containsKey(it.@name) }.each { beanProperty -> beanProperty.@name = renamedPropertyNames.get(beanProperty.@name) }
    }

    /**
     * Copies properties from the bean node to the delegate and renames using a map containing property names and their replacements
     *
     * @param builderDelegate
     * @param beanNode
     * @param renamedPropertyNames
     * @return
     */
    def renameProperties(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.attributes().entrySet().each { attributeProperty ->
            if (attributeProperty.key instanceof QName && renamedPropertyNames.containsKey(attributeProperty.key.localPart)) {
                attributeProperty.key.localPart = renamedPropertyNames.get(attributeProperty.key.localPart)
                builderDelegate.currentNode.attributes().put(attributeProperty.key, attributeProperty.value);
            }
        }
        beanNode.property.each { beanProperty ->
            if (renamedPropertyNames.containsKey(beanProperty.@name)) {
                builderDelegate.property(name: renamedPropertyNames.get(beanProperty.@name), value: beanProperty.@value)
            }
        }
    }

    def removeProperties(def beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { Node beanProperty -> beanProperty.replaceNode {} }
    }

    /**
     * Retrieves the object class name based on related business object entry bean
     *
     * @param beanNode
     * @return
     */
    protected String getObjectClassName(Node beanNode) {
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

    /**
     * extracts object names from bean id
     *
     * @param beanNode
     * @return
     */
    private String getMaintenanceDocumentObjectName(Node beanNode) {
        def suffixes = ["-parentBean", "MaintenanceDocument"];
        def objectName = beanNode.@id.toString();
        suffixes.each { suffix -> objectName = objectName.replaceFirst(suffix); }
        return objectName;
    }

    /**
     * Turns bean id into a more readable form
     *
     * @param beanId
     * @return
     */
    def getTitleFromBeanId(String beanId) {
        // TODO: review whether camel case check is necessary
        return beanId?.replaceAll(~/\s/, '-')?.replaceAll(/-+/, '-');
    }

    /**
     *
     * @param builder
     * @param viewName
     */
    def addViewNameProperty(NodeBuilder builder, String viewName) {
        def modifiedViewName = getTitleFromBeanId(viewName);
        if (StringUtils.isNotBlank(modifiedViewName)) {
            modifiedViewName = OUTPUT_CONV_FILE_PREFIX + modifiedViewName;
        }

        createProperty(builder, "viewName", modifiedViewName)
    }


    /**
     * placeholder beans should not
     *
     * @param beanNode
     */
    def isPlaceholder(Node beanNode) {
        if (beanNode?.property?.size() > 0) {
            return false;
        }

        return true;
    }

    def collectBeanCopyAttributes(Node beanNode, List<String> copyAttributes) {
        def returnAttributes = beanNode?.attributes()?.clone()?.findAll { copyAttributes.contains(it.key) };
        return returnAttributes != null ? returnAttributes : [:];
    }

    def collectBeanRenameAttributes(Node beanNode, Map<String, String> renameAttributes) {
        def copyAttributes = new ArrayList<String>(renameAttributes?.keySet());
        def beanAttributes = collectBeanCopyAttributes(beanNode, copyAttributes);
        def returnAttributes = beanAttributes?.collectEntries { key, value ->
            [renameAttributes.get(key),value]
        };

        return returnAttributes != null ? returnAttributes : [:];
    }

    def cloneNode(Node node) {
        return new XmlParser().parseText(XmlUtil.serialize(node));
    }

    def collectNamespaceProperties(Node beanNode) {
        def beanNSAttributes = beanNode?.attributes()?.clone()?.findAll { it.key instanceof QName};
        return beanNSAttributes != null ? beanNSAttributes : [:];
    }

    def collectCopyNamespaceProperties(Node beanNode, List<String> copyProperties) {
        def beanNSAttributes = beanNode?.attributes()?.clone()?.findAll { it.key instanceof QName};
        def returnProperties = beanNSAttributes?.findAll { copyProperties?.contains(((QName)it.key).getLocalPart()) };
        return returnProperties != null ? returnProperties : [:];
    }

    def collectRenameNamespaceProperties(Node beanNode, Map<String, String> renameProperties) {
        def copyProperties = new ArrayList<String>(renameProperties?.keySet());
        def beanAttributes = collectCopyNamespaceProperties(beanNode, copyProperties);
        def returnProperties = beanAttributes?.collectEntries { QName key, value ->
            [new QName(key.getNamespaceURI(), renameProperties.get(key.getLocalPart()), key.getPrefix()), value]
        }

        return returnProperties != null ? returnProperties : [:];
    }

    /**
     * Convert the bean attributes of while ignoring namespace properties
     *
     * @param beanNode of the view
     * @param originalBeanType of the view
     * @param transformBeanType new view type
     * @param ignoreAttributes list of know attributes that should not be carried over
     * @return
     */
    def Map convertBeanAttributes(Node beanNode, String originalBeanType, String transformBeanType, List copyAttributes, Map renameAttributes, List ignoreAttributes) {
        return convertBeanAttributes(beanNode, originalBeanType, transformBeanType, copyAttributes, renameAttributes, ignoreAttributes, [],[:],[]);
    }

    /**
     *
     * @param beanNode
     * @param copyAttributes
     * @param renameAttributes
     * @param ignoreAttributes
     * @return
     */
    def Map collectBeanAttributes(Node beanNode, List copyAttributes, Map renameAttributes, List ignoreAttributes) {
        def beanAttributes = [:];
        def carryoverBeanAttributes = [:];

        def copiedAttributes = collectBeanCopyAttributes(beanNode, copyAttributes);
        def renamedAttributes = collectBeanRenameAttributes(beanNode, renameAttributes);
        beanAttributes.putAll(copiedAttributes + renamedAttributes);

        if (useCarryoverAttributes && beanNode?.attributes()?.size() > 0) {
            carryoverBeanAttributes = beanNode?.attributes()?.clone();
            carryoverBeanAttributes?.keySet()?.removeAll{it instanceof QName};
            carryoverBeanAttributes?.keySet()?.removeAll(copyAttributes);
            carryoverBeanAttributes?.keySet()?.removeAll(renameAttributes.keySet());

            if (ignoreAttributes.size() > 0) {
                carryoverBeanAttributes.keySet().removeAll(ignoreAttributes);
            }
        }

        return beanAttributes + carryoverBeanAttributes;
    }

    def Map collectNamespaceProperties(Node beanNode, List copyProperties, Map<String, String> renameProperties, List<String> ignoreProperties) {
        def namespaceProperties = [:];
        def carryoverBeanAttributes = [:];

        // transform namespace properties (p:*) (copy, rename, transform)
        def copiedNamespaceProperties = collectCopyNamespaceProperties(beanNode, copyProperties);
        def renamedNamespaceProperties = collectRenameNamespaceProperties(beanNode, renameProperties);

        log.fine "copied properties for " + copyProperties.join(",") + " " + copiedNamespaceProperties?.keySet().join(",");
        log.fine "renamed properties for " + renameProperties.keySet().join(",") + renamedNamespaceProperties?.keySet().join(",");
        namespaceProperties.putAll(copiedNamespaceProperties + renamedNamespaceProperties);

        if(useCarryoverProperties && beanNode?.attributes()?.size() > 0) {
            carryoverBeanAttributes = beanNode?.attributes()?.clone();
            carryoverBeanAttributes.keySet().removeAll{!(it instanceof QName)};
            carryoverBeanAttributes.keySet().removeAll{
                copyProperties.contains(((QName)it).getLocalPart())
            };
            carryoverBeanAttributes.keySet().removeAll{
                renameProperties.keySet().contains(((QName)it).getLocalPart())
            };

            if (ignoreProperties.size() > 0) {
                carryoverBeanAttributes.keySet().removeAll{
                    ignoreProperties.contains(((QName)it).getLocalPart())
                };
            }
        }

        return namespaceProperties + carryoverBeanAttributes;
    }

    /**
     * copy, renames, and carryover (if configured) attributes and namespace properties of a bean
     *
     * @param beanNode
     * @param originalBeanType
     * @param transformBeanType
     * @param copyAttributes
     * @param renameAttributes
     * @param ignoreAttributes
     * @param copyProperties
     * @param renameProperties
     * @param ignoreProperties
     *
     * @return Map containing attibutes and namespace properties
     */
    def Map convertBeanAttributes(Node beanNode, String originalBeanType, String transformBeanType, List copyAttributes, Map renameAttributes,
            List ignoreAttributes, List copyProperties, Map<String, String> renameProperties, List<String> ignoreProperties) {
        Map idAttribute = [:];
        Map parentAttribute = [:];
        Map beanAttributes = [:];
        Map namespaceProperties = [:];

        // transform id and parent
        if(beanNode?.@id) {
            def translatedBeanId = getTranslatedBeanId(beanNode.@id, originalBeanType, transformBeanType);
            idAttribute.put("id",translatedBeanId);
        }

        if(beanNode?.@parent) {
            def translatedParentBeanId = getTranslatedBeanId(beanNode.@parent, originalBeanType, transformBeanType);
            parentAttribute.put("parent",translatedParentBeanId);
        }

        // transform standard attributes (copy, rename, transform) and namespace properties
        beanAttributes = collectBeanAttributes(beanNode, copyAttributes, renameAttributes, ignoreAttributes.plus(["id", "parent"]));
        namespaceProperties = collectNamespaceProperties(beanNode, copyProperties, renameProperties, ignoreProperties);

        // return the set with ordering id, attributes, properties, and parent
        def returnAttributes = idAttribute + beanAttributes + namespaceProperties + parentAttribute;
        return returnAttributes;
    }

    public static String getNodeString(Node rootNode) {
        def writer = new StringWriter()
        XmlUtil.serialize(rootNode, writer)
        return writer.toString()
    }

    /**
     * replaces namespace properties (p:name) with a property tag
     * Allows transformation scripts to handle property tags properly
     *
     * @param beanNode
     */
    def fixNamespaceProperties(Node beanNode) {
        def count = 0;
        log.finer "loading " + beanNode?.attributes()?.clone()
        def remAttrs = []
        if (beanNode?.attributes()?.size()) {
            def attrs = beanNode?.attributes()?.clone()

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
     * @param parent node
     * @param comment
     */
    def addComment(Node parentNode, String comment) {
        if (parentNode == null) {
            throw new IllegalArgumentException("parentNode must be specified");
        }

        if (comment == null) {
            throw new IllegalArgumentException("comment must be specified");
        }

        new Node(parentNode, "meta", [key: "comment", value: comment]);
    }

    /**
     * Add comment if it doesn't exist yet.  Guarantees uniqueness.
     *
     * @param parent node
     * @param comment
     */
    def addCommentIfNotExists(Node parentNode, String comment) {
        if (parentNode == null) {
            throw new IllegalArgumentException("parentNode must be specified");
        }

        if (comment == null) {
            throw new IllegalArgumentException("comment must be specified");
        }

        def metaComment = parentNode.meta.find { it.@key == "comment" && it.@value == comment };
        if (metaComment == null) {
            addComment(parentNode, comment);
        }
    }

    /**
     * transform summary field properties into values
     *
     * @param builder
     * @param beanNode
     */
    def transformSummaryFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["summaryFields": "layoutManager.summaryFields"], gatherAttributeNameAttribute, propertyNameValueFieldTransform);
    }

    /**
     * Converts title attribute into property bean along with a primary keys property
     *
     * @param beanNode
     */
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

}