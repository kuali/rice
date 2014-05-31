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
package org.kuali.rice.krad.schema

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttributeInfo
import org.kuali.rice.krad.datadictionary.parse.BeanTagInfo
import org.kuali.rice.krad.datadictionary.parse.CustomTagAnnotations
import org.kuali.rice.krad.devtools.datadictionary.FactoryExposingDataDictionary
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.util.xml.DomUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.NodeList

/**
 * Class for converting KRAD data dictionary files in spring bean format to the custom schema format.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class SchemaConverter {
    static final Logger LOG = Logger.getRootLogger()

    public static final String CONVERTED_FILE_PREFIX = "Converted-"
    static final String BEAN_CLASS_ATTRIBUTE = "class"
    static final String BEAN_PARENT_ATTRIBUTE = "parent"
    static final String BEAN_TAG = "bean"
    static final String BEANS_TAG = "beans"
    static final String COMP_ID_ATTRIBUTE = "compId"
    static final String ENTRY_TAG = "entry"
    static final String KEY_ATTRIBUTE = "key"
    static final String LIST_TAG = "list"
    static final String MAP_TAG = "map"
    static final String MERGE_ATTRIBUTE = "merge"
    static final String NAME_ATTRIBUTE = "name"
    static final String NULL_TAG = "null"
    static final String REF_TAG = "ref"
    static final String INC_TAG = "inc"
    static final String SET_TAG = "set"
    static final String SPRING_ELEMENT_PREFIX = "spring:"
    static final String UTIL_ELEMENT_PREFIX = "util:"
    static final String P_ATTRIBUTE_PREFIX = "p:"
    static final String PROPERTY_TAG = "property"
    static final String VALUE_ATTRIBUTE = "value"
    static final String VALUE_REF_ATTRIBUTE = "value-ref"

    static final String SPRING_NAMESPACE = "http://www.springframework.org/schema/beans"

    static Map<String, Map<String, String>> beanTagMap
    static Map<String, String> parentTagMap

    Document convertedDocument

    boolean hasUtilNamespace

    String[] scanPackages
    FactoryExposingDataDictionary dataDictionary

    def SchemaConverter(File fileToConvert, String[] scanPackages, FactoryExposingDataDictionary dataDictionary) {
        this.scanPackages = scanPackages
        this.dataDictionary = dataDictionary

        if (beanTagMap == null) {
            buildClassBeanTagMapping()
        }

        // back up file being converted
        File backupFile = new File(fileToConvert.getParentFile(), fileToConvert.getName() + ".bak")
        fileToConvert.renameTo(backupFile)

        File convertedFile = new File(fileToConvert.getParentFile(), fileToConvert.getName())

        convertSpringFile(backupFile, convertedFile)
    }

    def buildClassBeanTagMapping() {
        beanTagMap = new HashMap<String, Map<String, String>>()
        parentTagMap = new HashMap<String, String>()

        CustomTagAnnotations.loadTagClasses(scanPackages)

        Map<String, BeanTagInfo> beanMap = CustomTagAnnotations.getBeanTags()

        beanMap.each { tagName, info ->
            String className = info.getBeanClass().getName()

            Map<String, String> existingTags = beanTagMap[className]

            if (existingTags == null) {
                existingTags = new HashMap<String, String>()
            }

            if (info.isDefaultTag() || existingTags.isEmpty()) {
                existingTags.default = tagName
            }

            if (info.getParent() != null) {
                existingTags[info.getParent()] = tagName
                parentTagMap[info.getParent()] = tagName
            }

            beanTagMap[className] = existingTags
        }
    }

    def convertSpringFile(File file, File convertedFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document;
        document = builder.parse(file);

        convertedDocument = builder.newDocument()

        // Converts the beans tag into the custom schema
        NodeList beansNodeList = document.getElementsByTagName(BEANS_TAG);
        if (beansNodeList != null && beansNodeList.getLength() > 0) {
            Element beans = (Element) beansNodeList.item(0);

            Element convertedBeans = convertBeans(beans);
            convertedDocument.appendChild(convertedBeans);
        }

        writeDocument(convertedFile)
    }

    def Element convertBeans(Element beans) {
        Element newElement = convertedDocument.createElement("spring:beans")

        newElement.setAttribute("xmlns", "http://www.kuali.org/krad/schema");
        newElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        newElement.setAttribute("xmlns:spring", "http://www.springframework.org/schema/beans");
        newElement.setAttribute("xmlns:p", "http://www.springframework.org/schema/p");
        newElement.setAttribute("xsi:schemaLocation", "http://www.springframework.org/schema/beans " +
                "http://www.springframework.org/schema/beans/spring-beans-3.1.xsd " +
                "http://www.kuali.org/krad/schema http://www.kuali.org/krad/schema.xsd")

        ArrayList<Element> children = (ArrayList<Element>) DomUtils.getChildElements(beans)
        children.each { childElement ->
            Element convertedChildElement
            if (childElement.getTagName().equals(BEAN_TAG)) {
                convertedChildElement = convertBean(childElement)
            } else {
                convertedChildElement = convertNonBean(childElement)
            }

            newElement.appendChild(convertedChildElement)
        }

        if (hasUtilNamespace) {
            newElement.setAttribute("xmlns:util", "http://www.springframework.org/schema/util");
            newElement.setAttribute("xsi:schemaLocation", "http://www.springframework.org/schema/beans " +
                    "http://www.springframework.org/schema/beans/spring-beans-3.1.xsd " +
                    "http://www.springframework.org/schema/util " +
                    "http://www.springframework.org/schema/util/spring-util-3.1.xsd " +
                    "http://www.kuali.org/krad/schema http://www.kuali.org/krad/schema.xsd")
        }

        return newElement
    }

    def Element convertBean(Element bean) {
        String tagName = findCustomTagName(bean)

        if (tagName == null) {
            return copyAndPrefixSpringBean(bean)
        }

        Element tagElement = convertedDocument.createElement(tagName)

        Map<String, String> propertyAttributeMap = createPropertyAttributeMap(tagName)

        convertBeanAttributes(bean, tagElement, propertyAttributeMap)
        convertBeanProperties(bean, tagElement, propertyAttributeMap)

        return tagElement
    }

    def Element convertNonBean(Element element) {
        Element newElement = element

        if (element.getTagName().equals(UTIL_ELEMENT_PREFIX + LIST_TAG) || element.getTagName().
                equals(UTIL_ELEMENT_PREFIX + SET_TAG) || element.getTagName().equals(UTIL_ELEMENT_PREFIX + MAP_TAG)) {
            hasUtilNamespace = true

            newElement = convertedDocument.createElement(element.getTagName())

            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Node attribute = element.getAttributes().item(i)

                newElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue())
            }

            List<Element> children = DomUtils.getChildElements(element)
            children.each { childElement ->
                Element convertedElement

                if (element.getTagName().equals(UTIL_ELEMENT_PREFIX + MAP_TAG)) {
                    convertedElement = convertMapElement(childElement)
                } else if (element.getTagName().equals(UTIL_ELEMENT_PREFIX + SET_TAG)) {
                    convertedElement = convertSetElement(childElement)
                } else {
                    convertedElement = convertListElement(childElement)
                }

                newElement.appendChild(convertedElement)
            }
        } else {
            // for unidentified tag force them into the bean namespace to hopefully retain compatibility
            newElement.setAttribute("xmlns", SPRING_NAMESPACE)
        }

        return newElement
    }

    def String findCustomTagName(Element bean) {
        String className = bean.getAttribute(BEAN_CLASS_ATTRIBUTE)

        if (StringUtils.isNotBlank(className)) {
            return getCustomTagByClassName(className)
        }

        String parent = bean.getAttribute(BEAN_PARENT_ATTRIBUTE)

        return getCustomTagByParent(parent)
    }

    def String getCustomTagByClassName(String className) {
        Map<String, String> classTags = this.beanTagMap[className]

        if (classTags != null) {
            return classTags.default
        }

        return null
    }

    def String getCustomTagByParent(String parent) {
        if (StringUtils.isEmpty(parent)) {
            return null
        }

        if (this.parentTagMap.containsKey(parent)) {
            return this.parentTagMap[parent]
        }

        BeanDefinition beanDefinition = dataDictionary.getDictionaryBeanFactory().getBeanDefinition(parent)
        if (StringUtils.isNotBlank(beanDefinition.getBeanClassName())) {
            return getCustomTagByClassName(beanDefinition.getBeanClassName())
        }

        return getCustomTagByParent(beanDefinition.getParentName())
    }

    def Element copyAndPrefixSpringBean(Element bean) {
        Element copiedBean = convertedDocument.createElement(SPRING_ELEMENT_PREFIX + bean.getNodeName())

        for (int i = 0; i < bean.getAttributes().getLength(); i++) {
            Node attribute = bean.getAttributes().item(i)

            copiedBean.setAttribute(attribute.getNodeName(), attribute.getNodeValue().trim())
        }

        ArrayList<Element> childElements = (ArrayList<Element>) DomUtils.getChildElements(copiedBean)
        childElements.each { childElement ->
            convertedDocument.renameNode(childElement, childElement.getNamespaceURI(), SPRING_ELEMENT_PREFIX + childElement.getNodeName())
        }

        return copiedBean
    }

    def Map<String, String> createPropertyAttributeMap(String tagName) {
        Map<String, String> propertyAttributeMap = new HashMap<String, String>()

        Map<String, BeanTagAttributeInfo> infos = CustomTagAnnotations.getAttributeProperties()[tagName]

        infos.values().each { info -> propertyAttributeMap[info.getPropertyName()] = info.getName() }

        return propertyAttributeMap
    }

    def convertBeanAttributes(Element bean, Element tagElement, Map<String, String> propertyAttributeMap) {
        for (int i = 0; i < bean.getAttributes().getLength(); i++) {
            Node attribute = bean.getAttributes().item(i)

            String attributeName = attribute.getNodeName()
            String attributeValue = attribute.getNodeValue().trim()

            if (StringUtils.equals(attributeName, BEAN_CLASS_ATTRIBUTE)) {
                continue
            }

            // if their is a custom tag for the given parent we don't need to add the attribute
            if (StringUtils.equals(attributeName, BEAN_PARENT_ATTRIBUTE) && parentTagMap.containsKey(attributeValue)) {
                continue
            }

            // if attribute is a property, do any necessary conversion for custom tag attribute name
            if (attributeName.startsWith(P_ATTRIBUTE_PREFIX)) {
                attributeName = StringUtils.substringAfter(attributeName, P_ATTRIBUTE_PREFIX)

                if (propertyAttributeMap.containsKey(attributeName)) {
                    attributeName = propertyAttributeMap[attributeName]
                }
            }

            tagElement.setAttribute(attributeName, attributeValue)
        }
    }

    def convertBeanProperties(Element bean, Element tagElement, Map<String, String> propertyAttributeMap) {
        ArrayList<Element> propertyElements = (ArrayList<Element>) DomUtils.
                getChildElementsByTagName(bean, PROPERTY_TAG)
        propertyElements.each { propertyElement ->
            String name = propertyElement.getAttribute(NAME_ATTRIBUTE)
            if (propertyAttributeMap.containsKey(name)) {
                name = propertyAttributeMap[name]
            }

            BeanTagAttribute.AttributeType type

            BeanTagAttributeInfo info
            Map<String, BeanTagAttributeInfo> infos = CustomTagAnnotations.getAttributeProperties() \
                    [tagElement.getTagName()]
            if (infos != null) {
                info = infos[name]
            }

            if (info == null) {
                type = inferTypeFromContents(propertyElement)
            } else {
                type = info.getType()
            }

            Element newProp = null
            if (type == BeanTagAttribute.AttributeType.SINGLEVALUE) {
                tagElement.setAttribute(name, parseSingleValue(propertyElement))
            } else if ((type == BeanTagAttribute.AttributeType.SINGLEBEAN) || (type == BeanTagAttribute.AttributeType
                    .DIRECT) || (type == BeanTagAttribute.AttributeType.BYTYPE) ||
                    (type == BeanTagAttribute.AttributeType.DIRECTORBYTYPE)) {
                newProp = convertBeanValue(propertyElement, name,
                        ((type == BeanTagAttribute.AttributeType.BYTYPE) || (type == BeanTagAttribute.AttributeType
                                .DIRECTORBYTYPE)))
            } else if (type == BeanTagAttribute.AttributeType.LISTVALUE) {
                newProp = convertListValue(tagElement, propertyElement, name)
            } else if (type == BeanTagAttribute.AttributeType.LISTBEAN) {
                newProp = convertListValue(tagElement, propertyElement, name)
            } else if (type == BeanTagAttribute.AttributeType.MAPVALUE) {
                newProp = convertMapValue(tagElement, propertyElement, name)
            } else if (type == BeanTagAttribute.AttributeType.SETVALUE) {
                newProp = convertSetValue(propertyElement, name)
            } else if (type == BeanTagAttribute.AttributeType.MAPBEAN) {
                //newBean.appendChild(parseMapValue(property, propertyName))
            } else if (type == BeanTagAttribute.AttributeType.SETBEAN) {
                newProp = convertSetValue(propertyElement, name)
            }

            if (newProp != null) {
                tagElement.appendChild(newProp);
            }
        }
    }

    def BeanTagAttribute.AttributeType inferTypeFromContents(Element propertyElement) {
        if (DomUtils.getChildElements(propertyElement).size() == 0) {
            return BeanTagAttribute.AttributeType.SINGLEVALUE
        }

        Element child = DomUtils.getChildElements(propertyElement).get(0)

        if (child.getNodeName().compareTo(BEAN_TAG) == 0) {
            return BeanTagAttribute.AttributeType.SINGLEBEAN
        }

        if (child.getNodeName().compareTo(LIST_TAG) == 0) {
            Element grandChild = DomUtils.getChildElements(child).get(0)

            if (grandChild.getNodeName().compareTo(BEAN_TAG) == 0) {
                return BeanTagAttribute.AttributeType.LISTBEAN
            } else {
                return BeanTagAttribute.AttributeType.LISTVALUE
            }
        }

        if (child.getNodeName().compareTo(MAP_TAG) == 0) {
            Element grandChild = DomUtils.getChildElements(child).get(0)

            if (grandChild.getNodeName().compareTo(BEAN_TAG) == 0) {
                return BeanTagAttribute.AttributeType.MAPBEAN
            } else {
                return BeanTagAttribute.AttributeType.MAPVALUE
            }
        }

        if (child.getNodeName().compareTo(SET_TAG) == 0) {
            Element grandChild = DomUtils.getChildElements(child).get(0)

            if (grandChild.getNodeName().compareTo(BEAN_TAG) == 0) {
                return BeanTagAttribute.AttributeType.SETBEAN
            } else {
                return BeanTagAttribute.AttributeType.SETVALUE
            }
        }

        return null
    }

    def String parseSingleValue(Element property) {
        String value = property.getAttribute(VALUE_ATTRIBUTE)

        if (value == null) {
            DomUtils.getChildElementByTagName(property, VALUE_ATTRIBUTE).getTextContent()
        }

        return value
    }

    def Element convertBeanValue(Element propertyElement, String propertyName, boolean byType) {
        Element convertedProperty = convertedDocument.createElement(propertyName)

        if (propertyName.contains(".")) {
            convertedProperty = convertedDocument.createElement(PROPERTY_TAG)
            convertedProperty.setAttribute(NAME_ATTRIBUTE, propertyName)
        }

        Element valueElement
        String ref = propertyElement.getAttribute(REF_TAG)
        if (StringUtils.isNotBlank(ref)) {
            valueElement = convertedDocument.createElement(INC_TAG)
            valueElement.setAttribute(COMP_ID_ATTRIBUTE, ref)
        }

        Element child = DomUtils.getChildElementByTagName(propertyElement, REF_TAG)
        if (child != null) {
            valueElement = convertedDocument.createElement(INC_TAG)
            valueElement.setAttribute(COMP_ID_ATTRIBUTE, child.getAttribute(BEAN_TAG))
        }

        child = DomUtils.getChildElementByTagName(propertyElement, BEAN_TAG)
        if (child != null) {
            valueElement = convertBean(child)
        }

        child = DomUtils.getChildElementByTagName(propertyElement, NULL_TAG)
        if (child != null) {
            valueElement = convertedDocument.createElement(NULL_TAG)
        }

        if (byType) {
            return valueElement
        }

        if (valueElement != null) {
            convertedProperty.appendChild(valueElement)
        }

        return convertedProperty
    }

    def Element convertListValue(Element tagElement, Element property, String propertyName) {
        Element convertedProperty = convertedDocument.createElement(propertyName)
        Element convertedList = convertedProperty

        if (propertyName.contains(".")) {
            convertedProperty = convertedDocument.createElement(SPRING_ELEMENT_PREFIX + PROPERTY_TAG)
            convertedProperty.setAttribute(NAME_ATTRIBUTE, propertyName)

            convertedList = convertedDocument.createElement(SPRING_ELEMENT_PREFIX + LIST_TAG)
            convertedProperty.appendChild(convertedList)
        }

        if (property.hasAttribute(VALUE_ATTRIBUTE)) {
            String value = property.getAttribute(VALUE_ATTRIBUTE)
            tagElement.setAttribute(propertyName, value)

            return null;
        }

        ArrayList<Element> values

        Element list = DomUtils.getChildElementByTagName(property, LIST_TAG)
        if (list == null) {
            values = (ArrayList<Element>) DomUtils.getChildElements(property)
        } else {
            String merge = list.getAttribute(MERGE_ATTRIBUTE)
            if (StringUtils.isNotBlank(merge)) {
                convertedList.setAttribute(MERGE_ATTRIBUTE, merge)
            }

            values = (ArrayList<Element>) DomUtils.getChildElements(list)
        }

        if (values != null) {
            values.each { value ->
                Element newValue = convertListElement(value)

                if (newValue != null) {
                    convertedList.appendChild(newValue)
                }
            }
        }

        return convertedProperty
    }

    def Element convertListElement(Element listElement) {
        if (StringUtils.equals(listElement.getTagName().toLowerCase(), VALUE_ATTRIBUTE)) {
            Element value = convertedDocument.createElement(VALUE_ATTRIBUTE);
            value.setTextContent(listElement.getTextContent());

            return value;
        }

        if (StringUtils.equals(listElement.getTagName().toLowerCase(), REF_TAG)) {
            Element value = convertedDocument.createElement(INC_TAG);
            value.setAttribute(COMP_ID_ATTRIBUTE, listElement.getAttribute(BEAN_TAG));

            return value;
        }

        if (StringUtils.equals(listElement.getTagName().toLowerCase(), BEAN_TAG)) {
            return convertBean(listElement);
        }

        return convertNonBean(listElement);
    }

    def Element convertSetValue(Element property, String propertyName) {
        Element convertedProperty = convertedDocument.createElement(propertyName)
        Element convertedSet = convertedProperty

        if (propertyName.contains(".")) {
            convertedProperty = property.getOwnerDocument().createElement(PROPERTY_TAG)
            convertedProperty.setAttribute(NAME_ATTRIBUTE, propertyName)

            convertedSet = property.getOwnerDocument().createElement(SPRING_ELEMENT_PREFIX + SET_TAG)
            convertedProperty.appendChild(convertedSet)
        }

        Element set = DomUtils.getChildElementByTagName(property, SET_TAG)

        String merge = set.getAttribute(MERGE_ATTRIBUTE)
        if (StringUtils.isNotBlank(merge)) {
            convertedSet.setAttribute(MERGE_ATTRIBUTE, merge)
        }

        ArrayList<Element> setEntries = (ArrayList<Element>) DomUtils.getChildElements(set)
        setEntries.each { entry -> convertedSet.appendChild(getListElement(entry))
        }

        return convertedProperty
    }

    def Element convertMapValue(Element tagElement, Element property, String propertyName) {
        Element convertedProperty = convertedDocument.createElement(propertyName)
        Element convertedMap = convertedProperty

        if (propertyName.contains(".")) {
            convertedProperty = convertedDocument.createElement(PROPERTY_TAG)
            convertedProperty.setAttribute(NAME_ATTRIBUTE, propertyName)

            convertedMap = convertedDocument.createElement(SPRING_ELEMENT_PREFIX + MAP_TAG)
            convertedProperty.appendChild(convertedMap)
        }

        if (property.hasAttribute(VALUE_ATTRIBUTE)) {
            String value = property.getAttribute(VALUE_ATTRIBUTE)
            tagElement.setAttribute(propertyName, value)

            return null
        }

        Element map = DomUtils.getChildElementByTagName(property, MAP_TAG)

        String merge = map.getAttribute(MERGE_ATTRIBUTE)
        if (StringUtils.isNotBlank(merge)) {
            convertedMap.setAttribute(MERGE_ATTRIBUTE, merge)
        }

        ArrayList<Element> mapEntries = (ArrayList<Element>) DomUtils.getChildElementsByTagName(map, ENTRY_TAG)
        mapEntries.each { entry ->
            Element newEntry = convertMapElement(entry)

            if (newEntry != null) {
                convertedMap.appendChild(newEntry)
            }
        }

        return convertedProperty
    }

    def Element convertMapElement(Element mapElement) {
        Element value = convertedDocument.createElement(SPRING_ELEMENT_PREFIX + ENTRY_TAG)
        value.setAttribute(KEY_ATTRIBUTE, mapElement.getAttribute(KEY_ATTRIBUTE))

        if (!mapElement.getAttribute(VALUE_ATTRIBUTE).isEmpty()) {
            value.setAttribute(VALUE_ATTRIBUTE, mapElement.getAttribute(VALUE_ATTRIBUTE))

            return value
        }

        if (!mapElement.getAttribute(VALUE_REF_ATTRIBUTE).isEmpty()) {
            Element ref = newXml.getOwnerDocument().createElement(REF_TAG)
            ref.setAttribute(COMP_ID_ATTRIBUTE, mapElement.getAttribute(VALUE_REF_ATTRIBUTE))

            value.appendChild(ref)

            return value
        }

        if (mapElement.getElementsByTagName(VALUE_ATTRIBUTE).getLength() == 1) {
            value.setTextContent(mapElement.getElementsByTagName(VALUE_ATTRIBUTE).item(0).getTextContent())

            return value
        }

        if (mapElement.getElementsByTagName(BEAN_TAG).getLength() == 1) {
            value.appendChild(convertBean((Element) mapElement.getElementsByTagName(BEAN_TAG).item(0)))

            return value
        }

        return convertNonBean(mapElement)
    }

    def writeDocument(File file) {
        LOG.info("Writing converted file: " + file.getPath())

        Transformer transformer = TransformerFactory.newInstance().newTransformer()

        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        transformer.transform(new DOMSource(convertedDocument), new StreamResult(new FileWriter(file)))
    }

}
