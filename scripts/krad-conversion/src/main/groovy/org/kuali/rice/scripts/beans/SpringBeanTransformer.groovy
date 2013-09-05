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
import groovy.xml.QName
import org.apache.commons.lang.StringUtils

/**
 * This class handles basic conversion of properties
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class SpringBeanTransformer {

    // Used as the default prefix on all krad converted files
    public static String OUTPUT_CONV_FILE_PREFIX = "KradConv";

    // holds all variables
    def config

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

    def carryoverAttributes;
    def carryoverProperties;

    def init(config) {
        ddPropertiesMap = config.map.convert.dd_prop
        ddBeanControlMap = config.map.convert.dd_bean_control
        ddPropertiesRemoveList = config.list.remove.dd_beans
        pNamespaceSchema = config.msg_bean_schema
        xsiNamespaceSchema = config.msg_xml_schema_legacy
        carryoverAttributes = config.bool.dictionaryconversion.carryoverAttributes;
        carryoverProperties = config.bool.dictionaryconversion.carryoverProperties;
    }

    public String getTranslatedBeanId(String beanId, String originalBeanType, String transformBeanType) {
        if (originalBeanType.equals(beanId)) {
            return transformBeanType;
        }

        String prefix = beanId.split('-')[0];
        transformBeanType = transformBeanType.replaceFirst("Uif-", "");
        String suffix = beanId.contains("-parentBean") ? "-parentBean" : "";
        return prefix + "-" + transformBeanType + suffix;
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
    public Object getPropertyValue(Object bean, String propertyName) {
        def propertyValue = null;

        def property = bean.property.find { it.@name == propertyName };
        if (property != null) {
            propertyValue = property.@value;
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
        def value = attributes["p:propertyName"];
        builderDelegate.createNode("value", null, value);
    }

    // Property utilities

    /**
     * Copies properties from bean to the builder delegate based on the property names list
     *
     * @param builderDelegate
     * @param beanNode
     * @param propertyNames
     * @return
     */
    def copyProperties(NodeBuilder builderDelegate, Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { beanProperty ->
            if (beanProperty.list) {
                builderDelegate.property(name: beanProperty.@name) {
                    list {
                        beanProperty.list.value.each {
                            value(it.@value)
                        }
                    }
                }
            } else {
                builderDelegate.property(name: beanProperty.@name, value: beanProperty.@value)
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
        if (beanNode.property.size() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Convert the bean attributes of view beans
     *
     * @param beanNode of the view
     * @param originalBeanType of the view
     * @param transformBeanType new view type
     * @param ignoreAttributes list of know attributes that should not be carried over
     * @return
     */
    def Map convertBeanAttributes(Node beanNode, String originalBeanType, String transformBeanType, List ignoreAttributes) {
        def translatedBeanId = getTranslatedBeanId(beanNode.@id, originalBeanType, transformBeanType);
        def translatedParentBeanId = getTranslatedBeanId(beanNode.@parent, originalBeanType, transformBeanType);

        def beanAttributesCarriedOver = [:]
        if (carryoverAttributes) {
            beanAttributesCarriedOver = beanNode.attributes();
            beanAttributesCarriedOver.keySet().removeAll(["id", "parent"])
            if (ignoreAttributes.size() > 0) {
                beanAttributesCarriedOver.keySet().removeAll(ignoreAttributes)
            };
        } else {
            // always carry over the abstract attribute
            if (beanNode.attribute("abstract") != null) {
                beanAttributesCarriedOver = [abstract: beanNode.attribute("abstract")];
            }
        }

        return [id: translatedBeanId] + beanAttributesCarriedOver + [parent: translatedParentBeanId];
    }

    def Map somethingBeanProperties

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
     * @param parent node
     * @param comment
     */
    def addComment(Node parentNode, String comment) {
        if (parentNode == null) {
            throw new IllegalArgumentException ("parentNode must be specified");
        }

        if (comment == null) {
            throw new IllegalArgumentException ("comment must be specified");
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
            throw new IllegalArgumentException ("parentNode must be specified");
        }

        if (comment == null) {
            throw new IllegalArgumentException ("comment must be specified");
        }

        def metaComment = parentNode.meta.find {it.@key=="comment" && it.@value==comment};
        if (metaComment == null) {
            addComment(parentNode, comment);
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
        def controlProperty = beanNode?.property?.find { "control".equals(it.@name) };
        def controlFieldProperty = beanNode?.property?.find { "controlField".equals(it.@name) };
        if (controlProperty) {
            def controlDefBean = controlProperty.bean.find { it.@parent?.endsWith("Definition") };
            def controlDefParent = controlDefBean.@parent;
            if (controlFieldProperty) {
                this.removeProperties(beanNode, ["control"]);
            } else if (renamedControlBeans.get(controlDefParent) != null) {
                controlProperty.replaceNode {
                    property(name: "controlField") {
                        transformControlDefinitionBean(delegate, controlDefBean, renamedControlBeans)
                    }
                }

                if ("Uif-DropdownControl".equals(renamedControlBeans.get(controlDefParent))) {
                    controlProperty.plus {
                        property(name: "optionsFinder") {
                            bean(class: "org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder")
                        }
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

    /**
     * Used for transforming control definitions into control field properties
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
            def attributes = genericGatherAttributes(controlDefBean, ["*rows": "p:rows", "*cols": "p:cols"]);
            attributes.put("parent", "Uif-TextAreaControl");
            genericBeanTransform(builder, attributes);
        } else if (controlDefReplacements[controlDefParent] != null) {
            builder.bean(parent: controlDefReplacements[controlDefParent])
        } else {
            builder.bean(parent: "Uif-" + controlDefParent.replace("Definition", ""))
        }
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
