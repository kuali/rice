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
import org.kuali.rice.core.api.util.type.TypeUtils
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttributeInfo
import org.kuali.rice.krad.datadictionary.parse.BeanTagInfo
import org.kuali.rice.krad.datadictionary.parse.CustomTagAnnotations
import org.kuali.rice.krad.uif.component.Component
import org.kuali.rice.krad.uif.component.ComponentBase
import org.kuali.rice.krad.uif.element.Content
import org.w3c.dom.CDATASection
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Generates the a custom schema file for the given schema.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class SchemaGenerator {
    static final Logger LOG = Logger.getRootLogger()
    static final String KRAD_SCHEMA = "krad"

    Map<String, Map<String, BeanTagInfo>> nameTagMap
    List<OtherSchemaTags> otherSchemaTagsList

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
    DocumentBuilder builder = factory.newDocumentBuilder()

    List<Element> types = new ArrayList<Element>()
    List<Element> elements = new ArrayList<Element>()
    Set<Class<?>> processedClassChoiceTypes = new HashSet<Class<?>>()

    Map<String, Element> elementObjects = new HashMap<String, Element>()
    Set<String> mixedTypes = new HashSet<String>()

    Map<String, BeanTagAttributeInfo> componentAttributes = CustomTagAnnotations.getAttributes(ComponentBase.class)
    Element componentBaseElement

    Set<String> schemaFileNames

    ResourceBundle doc
    String[] scanPackages
    String outputPath
    ResourceBundle additionalSchemaTagsProperties
    String schemaName
    Map<String, List<String>> otherSchemaPackages

    def generateSchema(ResourceBundle doc, String[] scanPackages, String outputPath,
            ResourceBundle additionalSchemaTagsProperties, String schemaName,
            Map<String, List<String>> otherSchemaPackages) {
        LOG.info("Generating Custom Schema ...")

        this.doc = doc
        this.scanPackages = scanPackages
        this.outputPath = outputPath
        this.additionalSchemaTagsProperties = additionalSchemaTagsProperties
        this.schemaName = schemaName
        this.otherSchemaPackages = otherSchemaPackages

        nameTagMap = buildNameTagMap()
        schemaFileNames = new HashSet<String>()

        buildSchemaElements()
        fillAndWriteSchema()
    }

    def buildSchemaElements() {
        Document document = builder.newDocument()

        Set<String> classKeys = nameTagMap.keySet()

        componentBaseElement = buildClassSchemaType(document, "componentAttributes-type",
                ComponentBase.class)

        // create types for each element
        for (String className : classKeys) {
            Map<String, BeanTagInfo> tagMap = nameTagMap.get(className)
            Class<?> clazz = Class.forName(className)

            // content component is handled specially in base types
            if (clazz.equals(Content.class)) {
                continue
            }

            BeanTagInfo typeInfo = tagMap.get("default")
            String currentType = typeInfo.getTag()
            String type = currentType + "-type"

            Element complexType = buildClassSchemaType(document, type, clazz)
            types.add(complexType)

            // create the tag type element for the currentType
            Element typeElement = createElement(document, typeInfo.getTag(), type, null, null)

            typeElement.appendChild(getDocAnnotation(document, doc, className, null, null));
            elements.add(typeElement)

            // generate the remaining tag type elements for the rest of the tags of this class
            List<String> tagNames = new ArrayList<String>()
            tagMap.each { key, tagInfo -> tagNames.add(tagInfo.getTag())
            }

            if ((additionalSchemaTagsProperties != null) && additionalSchemaTagsProperties.containsKey(className)) {
                String[] tagBeanMappings = additionalSchemaTagsProperties.getString(className).split(",")

                tagBeanMappings.each { tagBeanMapping ->
                    String tag = StringUtils.substringBefore(tagBeanMapping, ":")
                    tagNames.add(tag)
                }
            }

            tagNames.each { tag ->
                if (!tag.equals(currentType)) {
                    Element element = createElement(document, tag, type, null, null)

                    element.appendChild(getDocAnnotation(document, doc, className, null, null));
                    elements.add(element)
                }
            }
        }

        for (Element element : elements) {
            String mixedTypeName = element.getAttribute("name") + "-mixedType"
            if (mixedTypes.contains(mixedTypeName)) {
                element.setAttribute("type", mixedTypeName)
            }
        }
    }

    def Map<String, Map<String, BeanTagInfo>> buildNameTagMap() {
        nameTagMap = new HashMap<String, Map<String, BeanTagInfo>>()

        CustomTagAnnotations.loadTagClasses(scanPackages)

        Map<String, BeanTagInfo> beanMap = CustomTagAnnotations.getBeanTags()

        beanMap.each { tagName, info ->
            String name = info.getBeanClass().getName()

            Map<String, BeanTagInfo> existingTags = nameTagMap.get(name)
            if (existingTags == null) {
                existingTags = new HashMap<String, BeanTagInfo>()
            }

            if (info.isDefaultTag() || existingTags.isEmpty()) {
                info.setDefaultTag(true)
                existingTags.put("default", info)
            }

            if (info.getParent() != null) {
                existingTags.put(info.getParent(), info)
            }

            nameTagMap.put(name, existingTags)
        }

        return nameTagMap
    }

    def Element buildClassSchemaType(Document document, String type, Class<?> clazz) {
        boolean isComponent = Component.class.isAssignableFrom(clazz)
        boolean isComponentBase = ComponentBase.class.equals(clazz)

        Element complexType = document.createElement("xsd:complexType")
        complexType.setAttribute("name", type)

        Element extension = null
        if (isComponent && !isComponentBase) {
            Element complexContent = document.createElement("xsd:complexContent")
            extension = document.createElement("xsd:extension")

            if (isKradSchema()) {
                extension.setAttribute("base", "componentAttributes-type")
            } else {
                extension.setAttribute("base", "krad:componentAttributes-type")
            }

            complexContent.appendChild(extension)
            complexType.appendChild(complexContent)
        }

        List<Element> attributeProperties = new ArrayList<Element>()

        Element sequence = processClassAttributes(document, clazz, attributeProperties)

        // extension for attributes if this is a sub-class of component
        if (isComponent && !isComponentBase) {
            extension.appendChild(sequence)
        } else {
            complexType.appendChild(sequence)
        }

        // add parent attribute to base types (ie, not component child classes)
        if (!isComponent || isComponentBase) {
            Element parentAttribute = document.createElement("xsd:attribute")
            parentAttribute.setAttribute("name", "parent")
            parentAttribute.setAttribute("type", "xsd:string")
            attributeProperties.add(parentAttribute)
        }

        // add anyAttribute to allow any arbitrary attribute (ie, dot notation nested property)
        Element anyAttribute = document.createElement("xsd:anyAttribute")
        anyAttribute.setAttribute("processContents", "skip")
        attributeProperties.add(anyAttribute)

        // add all the attributes to type
        for (Element attribute : attributeProperties) {
            if (isComponent && !isComponentBase) {
                extension.appendChild(attribute)
            } else {
                complexType.appendChild(attribute)
            }
        }

        return complexType
    }

    def Element processClassAttributes(Document document, Class<?> clazz, List<Element> attributeProperties) {
        boolean isComponent = Component.class.isAssignableFrom(clazz)
        boolean isComponentBase = ComponentBase.class.equals(clazz)

        Element sequence = document.createElement("xsd:choice")
        sequence.setAttribute("minOccurs", "0")
        sequence.setAttribute("maxOccurs", "unbounded")

        Map<String, BeanTagAttributeInfo> attributes = CustomTagAnnotations.getAttributes(clazz)

        if (attributes != null && !attributes.isEmpty()) {
            for (BeanTagAttributeInfo aInfo : attributes.values()) {
                if (!isComponent || !isComponentBase) {
                    if (aInfo.getType().equals(BeanTagAttribute.AttributeType.BYTYPE) || aInfo.getType().
                            equals(BeanTagAttribute.AttributeType.DIRECTORBYTYPE)) {
                        Element byTypeSequence = buildByTypeChoiceElement(document, aInfo)
                        sequence.appendChild(byTypeSequence)

                        continue
                    }

                    if (aInfo.getType().equals(BeanTagAttribute.AttributeType.DIRECT)) {
                        createMixedType(document, aInfo)

                        Element directRef = document.createElement("xsd:element")
                        directRef.setAttribute("ref", aInfo.getName())

                        sequence.appendChild(directRef)

                        continue
                    }

                    // default to anyType
                    String attrType = "xsd:anyType"

                    //Process each type of content below by setting a type and special processing flags
                    if (aInfo.getType().equals(BeanTagAttribute.AttributeType.SINGLEVALUE)) {
                        attrType = "xsd:string"
                    } else if (aInfo.getType().equals(BeanTagAttribute.AttributeType.SINGLEBEAN)) {
                        String attributeClass = aInfo.getValueType().getName()

                        buildClassChoiceBaseType(document, Class.forName(attributeClass))
                        if (elementObjects.containsKey(attributeClass)) {
                            attrType = attributeClass
                        }
                    } else if (aInfo.getType().equals(BeanTagAttribute.AttributeType.LISTVALUE) || aInfo.getType().
                            equals(BeanTagAttribute.AttributeType.SETVALUE)) {
                        attrType = "list-type"
                    } else if (aInfo.getType().equals(BeanTagAttribute.AttributeType.MAPVALUE) || aInfo.getType().
                            equals(BeanTagAttribute.AttributeType.MAPBEAN)) {
                        attrType = "map-type"
                    } else if (aInfo.getType().equals(BeanTagAttribute.AttributeType.LISTBEAN) || aInfo.getType().
                            equals(BeanTagAttribute.AttributeType.SETBEAN)) {
                        attrType = "spring:listOrSetType"
                    }

                    //create the element and documentation
                    Element element = createElement(document, aInfo.getName(), null, "0", "1");
                    element.appendChild(getDocAnnotation(document, doc, clazz.getName(), aInfo.getName(),
                            aInfo.getValueType().getName()));

                    if (aInfo.getType().equals(BeanTagAttribute.AttributeType.LISTBEAN) || aInfo.getType().
                            equals(BeanTagAttribute.AttributeType.SETBEAN)) {
                        Element extensionType = getListOrSetExtension(document, aInfo, attrType)
                        if (extensionType != null) {
                            element.appendChild(extensionType)
                        } else {
                            element.setAttribute("type", attrType)
                        }
                    } else if (aInfo.getType().equals(BeanTagAttribute.AttributeType.ANY)) {
                        Element anyComplexType = createAnyTypeElement(document)
                        element.appendChild(anyComplexType)
                    } else {
                        element.setAttribute("type", attrType)
                    }

                    sequence.appendChild(element)
                }

                // only append attributes for properties that can be input as string values
                boolean useAttribute = !aInfo.getType().equals(BeanTagAttribute.AttributeType.SINGLEBEAN) && !aInfo.
                        getType().equals(BeanTagAttribute.AttributeType.LISTBEAN) &&
                        !aInfo.getType().equals(BeanTagAttribute.AttributeType.MAPBEAN) &&
                        !aInfo.getType().equals(BeanTagAttribute.AttributeType.SETBEAN)
                if (useAttribute && (!isComponent || (isComponent && !componentAttributes.containsValue(aInfo)) ||
                        isComponentBase)) {
                    Element attribute = document.createElement("xsd:attribute")
                    attribute.setAttribute("name", aInfo.getName())
                    attribute.appendChild(getDocAnnotation(document, doc, clazz.getName(), aInfo.getName(),
                            aInfo.getValueType().getName()));
                    attributeProperties.add(attribute);
                }
            }
        }

        // spring:property element
        if (!isComponent || !isComponentBase) {
            Element nestedSpringPropertiesElement = document.createElement("xsd:element")
            nestedSpringPropertiesElement.setAttribute("ref", "spring:property")
            nestedSpringPropertiesElement.setAttribute("minOccurs", "0")
            nestedSpringPropertiesElement.setAttribute("maxOccurs", "unbounded")
            sequence.appendChild(nestedSpringPropertiesElement)

            Element nestedPropertiesElement = document.createElement("xsd:element")
            nestedPropertiesElement.setAttribute("ref", "property")
            nestedPropertiesElement.setAttribute("minOccurs", "0")
            nestedPropertiesElement.setAttribute("maxOccurs", "unbounded")
            sequence.appendChild(nestedPropertiesElement)
        }

        return sequence
    }

    def void createMixedType(Document document, BeanTagAttributeInfo aInfo) {
        String mixedTypeName = aInfo.getName() + "-mixedType"

        if (mixedTypes.contains(mixedTypeName)) {
            return
        }

        String attributeClass = aInfo.getValueType().getName()
        if (aInfo.getValueType().isInterface()) {
            attributeClass = aInfo.getValueType().getName() + "Base"
        }

        Map<String, BeanTagInfo> tagMap = nameTagMap.get(attributeClass)

        Element complexType = document.createElement("xsd:complexType")
        complexType.setAttribute("name", aInfo.getName() + "-mixedType")

        Element complexContent = document.createElement("xsd:complexContent")
        Element extension = document.createElement("xsd:extension")

        BeanTagInfo typeInfo = tagMap.get("default")
        String defaultType = typeInfo.getTag()

        extension.setAttribute("base", defaultType + "-type")

        complexContent.appendChild(extension)
        complexType.appendChild(complexContent)

        buildClassChoiceBaseType(document, Class.forName(attributeClass))

        Element tagChoiceElement = buildTypeChoiceElement(document, aInfo, true)
        if (tagChoiceElement != null) {
            extension.appendChild(tagChoiceElement)
        }

        types.add(complexType)

        mixedTypes.add(mixedTypeName)
    }

    def Element buildByTypeChoiceElement(Document document, BeanTagAttributeInfo aInfo) {
        Element tagChoiceElement = buildTypeChoiceElement(document, aInfo, false)

        String attributeName = aInfo.getName()

        buildClassChoiceBaseType(document, aInfo.getValueType())

        String type = aInfo.getValueType().getName()
        if (aInfo.getType().equals(BeanTagAttribute.AttributeType.DIRECTORBYTYPE)) {
            createMixedType(document, aInfo)
        } else {
            Element propertyElement = createElement(document, attributeName, type, "0", "1")

            tagChoiceElement.appendChild(propertyElement)
        }

        return tagChoiceElement
    }

    def Element buildTypeChoiceElement(Document document, BeanTagAttributeInfo aInfo, boolean forMixed) {
        Element tagChoiceElement = document.createElement("xsd:choice")
        tagChoiceElement.setAttribute("minOccurs", "0")
        tagChoiceElement.setAttribute("maxOccurs", "1")

        Class<?> targetType = aInfo.getValueType()
        if (targetType.isInterface()) {
            targetType = Class.forName(targetType.getName() + "Base")
        }

        Map<String, BeanTagAttributeInfo> targetTagAttributes = CustomTagAnnotations.getAttributes(targetType)

        // if building mixed type we don't want to add the type refs if the value type already
        // contains those ref (since mixed type extends the base type)
        if (forMixed && !TypeUtils.isSimpleType(targetType) &&
                containsByTypeAttribute(targetTagAttributes, targetType)) {
            return
        }

        List<String> added = new ArrayList<String>()
        for (String tagClassName : nameTagMap.keySet()) {
            Class<?> tagClazz = Class.forName(tagClassName)

            if (!targetType.isAssignableFrom(tagClazz)) {
                continue
            }

            Map<String, BeanTagInfo> tagMap = nameTagMap.get(tagClassName)
            for (String key : tagMap.keySet()) {
                String tag = tagMap.get(key).getTag()

                if (forMixed && (containsAttribute(targetTagAttributes, tag))) {
                    continue
                }

                if (!added.contains(tag)) {
                    added.add(tag)

                    Element ref = document.createElement("xsd:element")
                    ref.setAttribute("ref", tag)
                    tagChoiceElement.appendChild(ref)
                }
            }
        }

        return tagChoiceElement
    }

    def boolean containsByTypeAttribute(Map<String, BeanTagAttributeInfo> targetTagAttributes, Class<?> type) {
        boolean hasByTypeAttribue = false

        targetTagAttributes.values().each { tagAttribute ->
            if ((tagAttribute.getType().equals(BeanTagAttribute.AttributeType.BYTYPE) || tagAttribute.getType().
                    equals(BeanTagAttribute.AttributeType.DIRECTORBYTYPE)) && type.
                    isAssignableFrom(tagAttribute.getValueType())) {
                hasByTypeAttribue = true
            }
        }

        return hasByTypeAttribue
    }

    def boolean containsAttribute(Map<String, BeanTagAttributeInfo> targetTagAttributes, String attribute) {
        boolean hasAttribue = false

        targetTagAttributes.values().each { tagAttribute ->
            if (StringUtils.equalsIgnoreCase(tagAttribute.name, attribute)) {
                hasAttribue = true
            }
        }

        return hasAttribue
    }

    def Element createElement(Document document, String name, String type, String minOccurs,
            String maxOccurs) {
        Element element = document.createElement("xsd:element")

        element.setAttribute("name", name)

        if (minOccurs != null) {
            element.setAttribute("minOccurs", minOccurs)
        }

        if (maxOccurs != null) {
            element.setAttribute("maxOccurs", maxOccurs)
        }

        if (type != null) {
            element.setAttribute("type", type)
        }

        return element;
    }

    /**
     * Fills in the schema documents with the content passed in and writes them out.  Multiple schema files and
     * includes are used due to a file size limitation of 45k lines in intelliJ for xsd files.*/
    def void fillAndWriteSchema() throws TransformerException, IOException {
        // create top level schema
        Document topLevelDocument = builder.newDocument()
        Element schema = getSchemaInstance(topLevelDocument)

        Element include = topLevelDocument.createElement("xsd:include")
        include.setAttribute("schemaLocation", this.schemaName + "-" + "elements.xsd")
        schema.appendChild(include)

        topLevelDocument.appendChild(schema)

        // start elements document
        Document elementsDocument = builder.newDocument()
        schema = getSchemaInstance(elementsDocument)

        for (Element element : elements) {
            schema.appendChild(elementsDocument.importNode(element, true))
        }

        elementsDocument.appendChild(schema)

        // base type documents
        List<Document> baseTypeDocuments = writeBaseTypesDocuments()

        // type documents
        List<Document> typeDocuments = writeTypesDocuments(baseTypeDocuments)

        org.w3c.dom.Node elementsSchema = elementsDocument.getFirstChild()
        for (int i = 0; i < typeDocuments.size(); i++) {
            //write includes in element document
            include = elementsDocument.createElement("xsd:include")
            include.setAttribute("schemaLocation", this.schemaName + "-" + "types" + (i + 1) + ".xsd")
            elementsSchema.insertBefore(include, elementsSchema.getFirstChild())
        }

        writeDocument(topLevelDocument, "schema.xsd")
        writeDocument(elementsDocument, "elements.xsd")
    }

    def List<Document> writeBaseTypesDocuments() {
        Document baseTypesDocument = builder.newDocument()
        Element schema = getSchemaInstance(baseTypesDocument)

        int startIndex = 0;
        int endIndex = 70;

        List<String> elementObjectKeys = new ArrayList<String>(elementObjects.keySet())
        if (endIndex > elementObjectKeys.size()) {
            endIndex = elementObjectKeys.size()
        }

        List<Document> documentsToWrite = new ArrayList<Document>()

        boolean complete = false
        while (!complete) {
            List<String> elementObjectSubKeys = elementObjectKeys.subList(startIndex, endIndex)

            Element include = baseTypesDocument.createElement("xsd:include")
            include.setAttribute("schemaLocation", this.schemaName + "-" + "elements.xsd")
            schema.appendChild(include)

            // if first base type document add the generic types
            if (documentsToWrite.isEmpty() && isKradSchema()) {
                addGenericBaseTypes(baseTypesDocument, schema)
            }

            for (String objectName : elementObjectSubKeys) {
                schema.appendChild(baseTypesDocument.importNode(elementObjects[objectName], true))
            }

            //add to write list
            baseTypesDocument.appendChild(schema)
            documentsToWrite.add(baseTypesDocument)

            //setup next subList indices
            startIndex = endIndex
            endIndex = endIndex + 30

            if (endIndex > elementObjectKeys.size()) {
                endIndex = elementObjectKeys.size()
            }

            if (startIndex == elementObjectKeys.size()) {
                complete = true
            }

            //reset document and schema for next phase
            baseTypesDocument = builder.newDocument()
            schema = getSchemaInstance(baseTypesDocument)
        }

        int part = 1;
        for (Document document : documentsToWrite) {
            writeDocument(document, "baseTypes" + part + ".xsd")
            part++
        }

        return documentsToWrite
    }

    def addGenericBaseTypes(Document baseTypesDocument, Element schema) {
        Element propertySubstitution = baseTypesDocument.createElement("xsd:element")
        propertySubstitution.setAttribute("name", "property")
        propertySubstitution.setAttribute("type", "spring:propertyType")
        schema.appendChild(propertySubstitution)

        Element valueElement = baseTypesDocument.createElement("xsd:element")
        valueElement.setAttribute("name", "value")

        Element valueComplexType = baseTypesDocument.createElement("xsd:complexType")
        valueComplexType.setAttribute("mixed", "true")
        valueElement.appendChild(valueComplexType)

        Element valueChoiceElement = baseTypesDocument.createElement("xsd:choice")
        valueChoiceElement.setAttribute("minOccurs", "0")
        valueChoiceElement.setAttribute("maxOccurs", "unbounded")
        valueComplexType.appendChild(valueChoiceElement)

        schema.appendChild(valueElement)

        Element beanSubstitution = baseTypesDocument.createElement("xsd:element")
        beanSubstitution.setAttribute("name", "bean")

        Element beanComplexType = baseTypesDocument.createElement("xsd:complexType")
        beanSubstitution.appendChild(beanComplexType)

        Element beanComplexContent = baseTypesDocument.createElement("xsd:complexContent")
        beanComplexType.appendChild(beanComplexContent)

        Element beanExtension = baseTypesDocument.createElement("xsd:extension")
        beanExtension.setAttribute("base", "spring:identifiedType")
        beanComplexContent.appendChild(beanExtension)

        Element beanGroup = baseTypesDocument.createElement("xsd:group")
        beanGroup.setAttribute("ref", "spring:beanElements")
        beanExtension.appendChild(beanGroup)

        Element beanAttributeGroup = baseTypesDocument.createElement("xsd:attributeGroup")
        beanAttributeGroup.setAttribute("ref", "spring:beanAttributes")
        beanExtension.appendChild(beanAttributeGroup)
        schema.appendChild(beanSubstitution)

        // add type for custom inc element
        Element incElement = baseTypesDocument.createElement("xsd:element")
        incElement.setAttribute("name", "inc")

        Element incComplexType = baseTypesDocument.createElement("xsd:complexType")
        incElement.appendChild(incComplexType)

        Element incComplexContent = baseTypesDocument.createElement("xsd:complexContent")
        incComplexType.appendChild(incComplexContent)

        Element incRestriction = baseTypesDocument.createElement("xsd:restriction")
        incRestriction.setAttribute("base", "xsd:anyType")
        incComplexContent.appendChild(incRestriction)

        Element incAttribute = baseTypesDocument.createElement("xsd:attribute")
        incAttribute.setAttribute("name", "compId")
        incAttribute.setAttribute("type", "xsd:string")
        incRestriction.appendChild(incAttribute)

        schema.appendChild(incElement)

        // add type for custom ref element
        Element refElement = baseTypesDocument.createElement("xsd:element")
        refElement.setAttribute("name", "ref")

        Element refComplexType = baseTypesDocument.createElement("xsd:complexType")
        refElement.appendChild(refComplexType)

        Element refComplexContent = baseTypesDocument.createElement("xsd:complexContent")
        refComplexType.appendChild(refComplexContent)

        Element refRestriction = baseTypesDocument.createElement("xsd:restriction")
        refRestriction.setAttribute("base", "xsd:anyType")
        refComplexContent.appendChild(refRestriction)

        Element refAttribute = baseTypesDocument.createElement("xsd:attribute")
        refAttribute.setAttribute("name", "bean")
        refAttribute.setAttribute("type", "xsd:string")
        refRestriction.appendChild(refAttribute)

        schema.appendChild(refElement)

        // generic content element type
        Element contentElement = baseTypesDocument.createElement("xsd:element")
        contentElement.setAttribute("name", "content")

        Element contentComplexType = createAnyTypeElement(baseTypesDocument)
        contentElement.appendChild(contentComplexType)

        Element contentIdAttribute = baseTypesDocument.createElement("xsd:attribute")
        contentIdAttribute.setAttribute("name", "id")
        contentIdAttribute.setAttribute("type", "xsd:string")
        contentComplexType.appendChild(contentIdAttribute)

        schema.appendChild(contentElement)

        Element entryElement = baseTypesDocument.createElement("xsd:element")
        entryElement.setAttribute("name", "entry")
        entryElement.setAttribute("type", "spring:entryType")

        schema.appendChild(entryElement)

        // create map type
        Element mapType = baseTypesDocument.createElement("xsd:complexType")
        mapType.setAttribute("name", "map-type")

        Element mapSequenceContent = baseTypesDocument.createElement("xsd:sequence")
        mapType.appendChild(mapSequenceContent)

        Element mapSpringEntryElement = baseTypesDocument.createElement("xsd:element")
        mapSpringEntryElement.setAttribute("minOccurs", "0")
        mapSpringEntryElement.setAttribute("maxOccurs", "unbounded")
        mapSpringEntryElement.setAttribute("ref", "spring:entry")
        mapSequenceContent.appendChild(mapSpringEntryElement)

        Element mapEntryElement = baseTypesDocument.createElement("xsd:element")
        mapEntryElement.setAttribute("minOccurs", "0")
        mapEntryElement.setAttribute("maxOccurs", "unbounded")
        mapEntryElement.setAttribute("ref", "entry")
        mapSequenceContent.appendChild(mapEntryElement)

        Element mapMergeElement = baseTypesDocument.createElement("xsd:attribute")
        mapMergeElement.setAttribute("name", "merge")
        mapMergeElement.setAttribute("type", "xsd:boolean")
        mapType.appendChild(mapMergeElement)

        schema.appendChild(mapType)

        //create basicList type
        Element basicListType = baseTypesDocument.createElement("xsd:complexType")
        basicListType.setAttribute("name", "list-type")

        Element basicListSequence = baseTypesDocument.createElement("xsd:sequence")

        Element springValueRefElement = baseTypesDocument.createElement("xsd:element")
        springValueRefElement.setAttribute("minOccurs", "0")
        springValueRefElement.setAttribute("maxOccurs", "unbounded")
        springValueRefElement.setAttribute("ref", "spring:value")
        basicListSequence.appendChild(springValueRefElement)

        Element valueRefElement = baseTypesDocument.createElement("xsd:element")
        valueRefElement.setAttribute("minOccurs", "0")
        valueRefElement.setAttribute("maxOccurs", "unbounded")
        valueRefElement.setAttribute("ref", "value")
        basicListSequence.appendChild(valueRefElement)

        Element basicListMergeAttribute = baseTypesDocument.createElement("xsd:attribute")
        basicListMergeAttribute.setAttribute("name", "merge")
        basicListMergeAttribute.setAttribute("type", "xsd:boolean")

        basicListType.appendChild(basicListSequence)
        basicListType.appendChild(basicListMergeAttribute)
        schema.appendChild(basicListType)

        schema.appendChild(baseTypesDocument.importNode(componentBaseElement, true))
    }

    def List<Document> writeTypesDocuments(List<Document> baseTypeDocuments) {
        Document typesDocument = builder.newDocument()
        Element schema = getSchemaInstance(typesDocument)

        int startIndex = 0;
        int endIndex = 20;

        if (endIndex > types.size()) {
            endIndex = types.size()
        }

        List<Document> documentsToWrite = new ArrayList<Document>()

        boolean complete = false
        while (!complete) {
            List<Element> typesSubList = types.subList(startIndex, endIndex)

            int part = 1;
            for (Document document : baseTypeDocuments) {
                Element include = typesDocument.createElement("xsd:include")
                include.setAttribute("schemaLocation", this.schemaName + "-" + "baseTypes" + part + ".xsd")
                schema.appendChild(include)

                part++
            }

            //add all types
            for (Element type : typesSubList) {
                schema.appendChild(typesDocument.importNode(type, true))
            }

            //add to write list
            typesDocument.appendChild(schema)
            documentsToWrite.add(typesDocument)

            //setup next subList indices
            startIndex = endIndex
            endIndex = endIndex + 20

            if (endIndex > types.size()) {
                endIndex = types.size()
            }

            if (startIndex == types.size()) {
                complete = true
            }

            //reset document and schema for next phase
            typesDocument = builder.newDocument()
            schema = getSchemaInstance(typesDocument)
        }

        int part = 1;
        for (Document document : documentsToWrite) {
            writeDocument(document, "types" + part + ".xsd")
            part++
        }

        return documentsToWrite
    }

    def Element createAnyTypeElement(Document document) {
        Element contentComplexType = document.createElement("xsd:complexType")

        Element sequenceElement = document.createElement("xsd:sequence")
        contentComplexType.appendChild(sequenceElement)

        Element anyElement = document.createElement("xsd:any")
        anyElement.setAttribute("minOccurs", "0")
        anyElement.setAttribute("processContents", "skip")
        sequenceElement.appendChild(anyElement)

        return contentComplexType
    }

    /**
     * Writes the document out with the provided documentName to the current directory
     *
     * @param document document to be written
     * @param documentName name of document to write to
     * @throws TransformerException
     * @throws IOException
     */
    def void writeDocument(Document document, String documentName) throws TransformerException, IOException {
        documentName = this.schemaName + "-" + documentName
        this.schemaFileNames.add(documentName)

        File file = new File(this.outputPath + "/" + documentName)

        LOG.info("Writing schema file: " + file.getPath())

        Transformer transformer = TransformerFactory.newInstance().newTransformer()

        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(file)))
    }

    /**
     * Gets a new schema element instance with the krad namespace
     *
     * @param document the document
     * @return schema element with properties/imports filled in
     */
    def Element getSchemaInstance(Document document) {
        //set up base schema tag
        Element schema = document.createElement("xsd:schema")

        schema.setAttribute("xmlns", "http://www.kuali.org/" + this.schemaName + "/schema")
        schema.setAttribute("targetNamespace", "http://www.kuali.org/" + this.schemaName + "/schema")
        schema.setAttribute("elementFormDefault", "qualified")
        schema.setAttribute("attributeFormDefault", "unqualified")

        if (!isKradSchema()) {
            schema.setAttribute("xmlns:krad", "http://www.kuali.org/krad/schema")
        }

        schema.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema")
        schema.setAttribute("xmlns:spring", "http://www.springframework.org/schema/beans")
        schema.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
        schema.setAttribute("xsi:schemaLocation",
                "http://www.springframework.org/schema/beans http://www.springframework" +
                        ".org/schema/beans/spring-beans-3.1.xsd  http://www.springframework.org/schema/util " +
                        "http://www.springframework.org/schema/util/spring-util-3.1.xsd")

        //add spring import
        Element springImport = document.createElement("xsd:import")
        springImport.setAttribute("namespace", "http://www.springframework.org/schema/beans")
        schema.appendChild(springImport)

        if (!isKradSchema()) {
            Element kradImport = document.createElement("xsd:import")
            kradImport.setAttribute("namespace", "http://www.kuali.org/krad/schema")
            schema.appendChild(kradImport)
        }

        return schema
    }

    def buildClassChoiceBaseType(Document document, Class<?> clazz) {
        if (processedClassChoiceTypes.contains(clazz)) {
            return
        }

        if (TypeUtils.isSimpleType(clazz) || Enum.class.isAssignableFrom(clazz) || clazz.isArray()) {
            return
        }

        Element elementObject = document.createElement("xsd:complexType")
        elementObject.setAttribute("name", clazz.getName())

        Element choice = document.createElement("xsd:choice")
        choice.setAttribute("minOccurs", "0")
        choice.setAttribute("maxOccurs", "unbounded")

        Element beanElement = document.createElement("xsd:element")
        beanElement.setAttribute("ref", "bean")
        choice.appendChild(beanElement)

        Element ref = document.createElement("xsd:element")
        ref.setAttribute("ref", "ref")
        choice.appendChild(ref)

        Element inc = document.createElement("xsd:element")
        inc.setAttribute("ref", "inc")
        choice.appendChild(inc)

        List<String> added = new ArrayList<String>()

        nameTagMap.each { className, tagMap ->
            Class<?> clazzKey = Class.forName(className)

            if (clazz.isAssignableFrom(clazzKey)) {
                tagMap.each { bean, tagInfo ->
                    String tag = tagInfo.getTag()

                    if (!added.contains(tag)) {
                        added.add(tag)
                        Element tagElement = document.createElement("xsd:element")
                        tagElement.setAttribute("ref", tag)

                        choice.appendChild(tagElement)
                    }
                }
            }
        }

        elementObject.appendChild(choice)

        elementObjects[clazz.getName()] = elementObject
        processedClassChoiceTypes.addAll(clazz)
    }

    /**
     * Get the documentation annonation element for the class or property information passed in
     *
     * @param document the document
     * @param doc the ResourceBundle documentation resource
     * @param className name of the class to get documentation for.  If property and property type are not supplied,
     * returns the class documentation Element
     * @param property (optional) when supplied with propertyType the Element returned will be the property
     * documentation
     * @param propertyType (optional) must be supplied with property, the property's type
     * @return xsd:annotation Element representing the documentation for the class/property
     */
    def Element getDocAnnotation(Document document, ResourceBundle doc, String className, String property,
            String propertyType) {
        try {
            Class clazz = Class.forName(className)

            Element annotation = document.createElement("xsd:annotation")
            Element documentation = document.createElement("xsd:documentation")
            documentation.setAttribute("source", clazz.getName())
            documentation.setAttribute("xml:lang", "en")

            String content = "documentation not available"

            if (property == null || propertyType == null) {
                if (doc.containsKey(clazz.getName())) {
                    content = "Backing Class: " + className + "\n\n" + doc.getString(clazz.getName())
                }
            } else {
                int begin = 0
                int end = propertyType.length()

                if (propertyType.lastIndexOf('.') != -1) {
                    begin = propertyType.lastIndexOf('.') + 1
                }

                if (propertyType.indexOf('<') != -1) {
                    end = propertyType.indexOf('<')
                }
                String key = clazz.getName() + "|" + property + "|" + propertyType.substring(begin, end)

                if (doc.containsKey(key)) {
                    content = doc.getString(key)
                } else {
                    //find the documentation content for this property on a parent class
                    boolean foundContent = false
                    while (!clazz.equals(Object.class) && !foundContent) {
                        for (Class currentInterface : clazz.getInterfaces()) {
                            if (currentInterface.getName().startsWith("org.kuali")) {
                                key = currentInterface.getName() + "|" + property + "|" + propertyType.substring(begin,
                                        end)
                                foundContent = doc.containsKey(key) && StringUtils.isNotBlank(doc.getString(key))
                            }
                        }

                        if (foundContent) {
                            break
                        }

                        key = clazz.getName() + "|" + property + "|" + propertyType.substring(begin, end)
                        foundContent = doc.containsKey(key) && StringUtils.isNotBlank(doc.getString(key))
                        clazz = clazz.getSuperclass()
                    }

                    if (foundContent) {
                        content = doc.getString(key)
                    }
                }
            }

            content = content.replaceAll("\\<li\\>", "\n-")
            content = content.replaceAll("\\<\\\\ol\\>", "\n")
            content = content.replaceAll("\\<\\\\ul\\>", "\n")
            content = content.replaceAll("\\<.*?\\>", "")
            content = content.replaceAll("\\{\\@code\\s(.*?)\\}", '$1')
            content = content.replaceAll("\\{\\@link\\s(.*?)\\}", '$1')
            content = content.replaceAll("\\{\\@see\\s(.*?)\\}", '$1')

            CDATASection cdata = document.createCDATASection(content)
            documentation.appendChild(cdata)

            //append doc
            annotation.appendChild(documentation)

            return annotation
        } catch (Exception e) {
            throw new RuntimeException("class not found ", e)
        }
    }

    /**
     * For list bean types build an extension based on the generic type.
     *
     * @param document the document
     * @param aInfo attribute info for this element
     * @param attrType the xsd attribute type for this element
     * @return element with a type extension
     */
    def Element getListOrSetExtension(Document document, BeanTagAttributeInfo aInfo, String attrType) {
        String baseType = null
        if (aInfo.getGenericType() instanceof ParameterizedType) {
            Type genericParm = ((ParameterizedType) aInfo.getGenericType()).getActualTypeArguments()[0]

            if (genericParm instanceof Class<?>) {
                Class<?> parmClass = (Class<?>) genericParm

                buildClassChoiceBaseType(document, parmClass)
                baseType = parmClass.getName()
            }
        }

        if (StringUtils.isBlank(baseType)) {
            return null
        }

        Element complexType = document.createElement("xsd:complexType")
        Element simpleContent = document.createElement("xsd:complexContent")

        Element extension = document.createElement("xsd:extension")
        extension.setAttribute("base", baseType)

        Element mergeAttribute = document.createElement("xsd:attribute")
        mergeAttribute.setAttribute("name", "merge")
        mergeAttribute.setAttribute("type", "xsd:boolean")

        extension.appendChild(mergeAttribute)
        simpleContent.appendChild(extension)
        complexType.appendChild(simpleContent)

        return complexType
    }

    def boolean isKradSchema() {
        return KRAD_SCHEMA.equals(this.schemaName)
    }

    def class OtherSchemaTags {
        String schemaName
        Map<String, Map<String, BeanTagInfo>> nameTagMap

        public OtherSchemaTags(schemaName, Map<String, Map<String, BeanTagInfo>> nameTagMap) {
            this.schemaName = schemaName
            this.nameTagMap = nameTagMap
        }

        String getSchemaName() {
            return schemaName
        }

        void setSchemaName(String schemaName) {
            this.schemaName = schemaName
        }

        Map<String, Map<String, BeanTagInfo>> getNameTagMap() {
            return nameTagMap
        }

        void setNameTagMap(Map<String, Map<String, BeanTagInfo>> nameTagMap) {
            this.nameTagMap = nameTagMap
        }
    }
}
