/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.parse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Creates and stores the information defined for the custom schema.  Loads the classes defined as having associated
 * custom schemas and creates the information for the schema by parsing there annotations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomTagAnnotations {
    // Logger
    private static final Log LOG = LogFactory.getLog(CustomTagAnnotations.class);

    private static Map<String, Map<String, BeanTagAttributeInfo>> attributeProperties;
    private static Map<String, BeanTagInfo> beanTags;
    private static List<Class<?>> customTagClasses;

    /**
     * Loads the list of class that have an associated custom schema.
     */
    private static void loadCustomTagClasses(){
        try{
            customTagClasses = findTagClasses("org.kuali.rice.krad");
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    private static List<Class<?>> findTagClasses(String basePackage) throws IOException, ClassNotFoundException
    {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resolvedBasePackage =
                ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                                   resolvedBasePackage + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader != null && isBeanTag(metadataReader)) {
                    classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return classes;
    }

    private static boolean isBeanTag(MetadataReader metadataReader)
    {
        try{
            try{
                Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
                if (c.getAnnotation(BeanTag.class) != null || c.getAnnotation(BeanTags.class) != null) {
                    return true;
                }
            }
            catch(Throwable e){
                //skip
            }
        }
        catch(Exception e){
        }
        return false;
    }

    public static Document generateSchemaFile(){
        Map<String, Map<String, BeanTagInfo>> nameTagMap = new HashMap<String, Map<String, BeanTagInfo>>();
        Map<String, BeanTagInfo> beanMap = CustomTagAnnotations.getBeanTags();
        BeanTagInfo infos[] = new BeanTagInfo[beanMap.values().size()];
        infos = beanMap.values().toArray(infos);
        String tags[] = new String[beanMap.entrySet().size()];
        try {
            tags = beanMap.keySet().toArray(tags);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < infos.length; i++) {
            String name = infos[i].getBeanClass().getName();
            String tag = tags[i];
            Map<String, BeanTagInfo> existingTags = nameTagMap.get(name);

            if(existingTags == null){
                existingTags = new HashMap<String, BeanTagInfo>();
            }

            if(infos[i].isDefaultTag() || existingTags.isEmpty()){
                infos[i].setDefaultTag(true);
                existingTags.put("default", infos[i]);
            }

            if(infos[i].getParent() != null){
                existingTags.put(infos[i].getParent(), infos[i]);
            }

            nameTagMap.put(name, existingTags);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            document = builder.newDocument();

            Element schema = document.createElement("xsd:schema");
            schema.setAttribute("xmlns", "http://www.kuali.org/schema");
            schema.setAttribute("targetNamespace", "http://www.kuali.org/schema");
            schema.setAttribute("elementFormDefault", "qualified");
            schema.setAttribute("attributeFormDefault", "unqualified");
            schema.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

            List<Element> types = new ArrayList<Element>();
            List<Element> elements = new ArrayList<Element>();
            Set<String> classKeys = nameTagMap.keySet();
            for (String className: classKeys) {
                Map<String, BeanTagInfo> tagMap = nameTagMap.get(className);
                BeanTagInfo typeInfo = tagMap.get("default");
                String currentType = typeInfo.getTag();

                Element complexType = document.createElement("xsd:complexType");
                complexType.setAttribute("name", currentType + "-type");
                Element sequence = document.createElement("xsd:all");

                List<Element> attributeProperties = new ArrayList<Element>();
                Map<String, BeanTagAttributeInfo> attributes = getAttributes(typeInfo.getBeanClass());

                if(attributes != null && !attributes.isEmpty()){
                    for(BeanTagAttributeInfo aInfo: attributes.values()){
                        boolean useAttribute = false;
                        String attrType = "xsd:anyType";

                        if(aInfo.getType().equals(BeanTagAttribute.AttributeType.SINGLEVALUE)){
                            useAttribute = true;
                            attrType = "xsd:string";
                        }

                        if(aInfo.getType().equals(BeanTagAttribute.AttributeType.SINGLEBEAN)){
                            useAttribute = true;
                        }

                        if(aInfo.getType().equals(BeanTagAttribute.AttributeType.LISTVALUE) ||
                                aInfo.getType().equals(BeanTagAttribute.AttributeType.SETVALUE)){
                            useAttribute = true;
                            attrType = "xsd:anyType";
                        }

                        if(useAttribute){
                            Element attribute = document.createElement("xsd:attribute");
                            attribute.setAttribute("name", aInfo.getName());
                            //attribute.setAttribute("type", attrType);
                            attributeProperties.add(attribute);
                        }

                        Element elementAttribute = document.createElement("xsd:element");
                        elementAttribute.setAttribute("name", aInfo.getName());
                        elementAttribute.setAttribute("type", attrType);
                        elementAttribute.setAttribute("minOccurs", "0");
                        sequence.appendChild(elementAttribute);
                    }
                }

                complexType.appendChild(sequence);

                Element parentAttribute = document.createElement("xsd:attribute");
                parentAttribute.setAttribute("name", "parent");
                parentAttribute.setAttribute("type", "xsd:string");
                attributeProperties.add(parentAttribute);
                Element anyAttribute = document.createElement("xsd:anyAttribute");
                anyAttribute.setAttribute("processContents", "skip");
                attributeProperties.add(anyAttribute);

                for(Element attribute: attributeProperties){
                    complexType.appendChild(attribute);
                }

                types.add(complexType);

                Element defaultElement = document.createElement("xsd:element");
                defaultElement.setAttribute("name", typeInfo.getTag());
                defaultElement.setAttribute("type", currentType + "-type");
                elements.add(defaultElement);

                Set<String> tagKeys = tagMap.keySet();
                for(String key: tagKeys){
                    String tag = tagMap.get(key).getTag();
                    if(!tag.equals(currentType)){
                        Element element = document.createElement("xsd:element");
                        element.setAttribute("name", tag);
                        element.setAttribute("type", currentType + "-type");
                        elements.add(element);
                    }
                }
            }

            for(Element element: elements){
                schema.appendChild(element);
            }

            for(Element type: types){
                schema.appendChild(type);
            }

            document.appendChild(schema);

            File file = new File("./krad_schema.xsd");
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(file)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;


    }

    /**
     * Loads the list of class that have an associated custom schema.
     *
     * @param file - The file with the class list
     */
    private static void loadCustomTagClasses(String file) {
        ArrayList<String> classes = getClassList(file);

        customTagClasses = new ArrayList<Class<?>>();

        for (int i = 0; i < classes.size(); i++) {
            try {
                customTagClasses.add(Class.forName(classes.get(i).trim()));
            } catch (Exception e) {
                LOG.error("Class not Found : " + classes.get(i), e);
            }
        }
    }

    /**
     * Load the attribute information of the properties in the class repersented by the new tag.
     *
     * @param tagName - The name of the xml tag being created.
     * @param tagClass - The class being defined by the xml tag.
     */
    private static void loadAttributeProperties(String tagName, Class<?> tagClass) {
        Map<String, BeanTagAttributeInfo> entries = new HashMap<String, BeanTagAttributeInfo>();

        entries.putAll(getAttributes(tagClass));

        attributeProperties.put(tagName, entries);
    }

    /**
     * Creates a map of entries the properties marked by the annotation of the bean class.
     *
     * @param tagClass - The class being mapped for attributes.
     * @return Return a map of the properties found in the class with there associated information.
     */
    private static Map<String, BeanTagAttributeInfo> getAttributes(Class<?> tagClass) {
        Map<String, BeanTagAttributeInfo> entries = new HashMap<String, BeanTagAttributeInfo>();

        try{
            // Search the methods of the class using reflection for the attribute annotation
            Method methods[] = tagClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                BeanTagAttribute attribute = methods[i].getAnnotation(BeanTagAttribute.class);
                if (attribute != null) {
                    BeanTagAttributeInfo info = new BeanTagAttributeInfo();
                    info.setName(getFieldName(methods[i].getName()));
                    info.setType(attribute.type());
                    validateBeanAttributes(tagClass.getName(), attribute.name(), entries);
                    entries.put(attribute.name(), info);
                }
            }
        }
        catch(Throwable e){
            //skip bad entry
        }

        return entries;
    }

    /**
     * Load the information for the xml bean tags defined in the custom schema through annotation of the represented
     * classes.
     */
    private static void loadBeanTags() {

        // Load the list of class to be searched for annotation definitions
        if (customTagClasses == null) {
            loadCustomTagClasses();
        }

        beanTags = new HashMap<String, BeanTagInfo>();

        attributeProperties = new HashMap<String, Map<String, BeanTagAttributeInfo>>();

        // For each class create the bean tag information and its associated attribute properties
        for (int i = 0; i < customTagClasses.size(); i++) {
            BeanTag[] annotations = new BeanTag[1];
            BeanTag tag = customTagClasses.get(i).getAnnotation(BeanTag.class);
            if(tag != null){
                //single tag case
                annotations[0] = tag;
            }
            else{
                //multi-tag case
                BeanTags tags = customTagClasses.get(i).getAnnotation(BeanTags.class);
                if(tags != null){
                    annotations = tags.value();
                }
                else{
                    //TODO throw exception instead?
                    continue;
                }
            }

            for(int j = 0; j < annotations.length; j++){
                BeanTag annotation = annotations[j];
                BeanTagInfo info = new BeanTagInfo();
                info.setTag(annotation.name());

                if(j == 0){
                    info.setDefaultTag(true);
                }

                info.setBeanClass(customTagClasses.get(i));
                info.setParent(annotation.parent());
                validateBeanTags(annotation.name());
                beanTags.put(annotation.name(), info);
                loadAttributeProperties(annotation.name(), customTagClasses.get(i));
            }
        }
    }

    /**
     * Retrieves the name of the property being defined by the tag by parsing the method name attached to the
     * annotation.  All annotations should be attached to the get method for the associated property.
     *
     * @param methodName - The name of the method attached to the annotation
     * @return Returns the property name associated witht he method.
     */
    private static String getFieldName(String methodName) {
        // Check if function is of the form isPropertyName()
        if (methodName.substring(0, 2).toLowerCase().compareTo("is") == 0) {
            String letter = methodName.substring(2, 3);
            return letter.toLowerCase() + methodName.substring(3, methodName.length());
        }
        // Since the annotation is attached to the get function the property name starts at the 4th letter and has been upper-cased as assumed by the Spring Beans.
        String letter = methodName.substring(3, 4);
        return letter.toLowerCase() + methodName.substring(4, methodName.length());
    }

    /**
     * Validates that the tag name is not already taken.
     *
     * @param tagName - The name of the tag for the new bean.
     * @return Returns true if the validation passes, false otherwise.
     */
    private static boolean validateBeanTags(String tagName) {
        boolean valid = true;
        String tags[] = new String[beanTags.keySet().size()];
        tags = beanTags.keySet().toArray(tags);

        for (int j = 0; j < tags.length; j++) {
            if (tagName.compareTo(tags[j]) == 0) {
                LOG.error("Duplicate tag name " + tagName);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates that the tagName for the next property is not already taken.
     *
     * @param className - The name of the class being checked.
     * @param tagName - The name of the new attribute tag.
     * @param attributes - A map of the attribute tags already created
     * @return Returns true if the validation passes, false otherwise.
     */
    private static boolean validateBeanAttributes(String className, String tagName,
            Map<String, BeanTagAttributeInfo> attributes) {
        boolean valid = true;

        // Check for reserved tag names: ref, parent, abstract
        if ((tagName.compareTo("parent") == 0) || (tagName.compareTo("ref") == 0) || (tagName.compareTo("abstract")
                == 0)) {
            //LOG.error("Reserved tag name " + tagName + " in bean " + className);
            return false;
        }

        String tags[] = new String[attributes.keySet().size()];
        tags = attributes.keySet().toArray(tags);
        for (int j = 0; j < tags.length; j++) {
            if (tagName.compareTo(tags[j]) == 0) {
                LOG.error("Duplicate attribute tag name " + tagName + " in bean " + className);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Retrieves the map of bean tags.  If the map has not been created yet the tags are loaded.
     * The Bean tag map is created using the xml tag name of the bean as the key with the value consisting of
     * information about the tag stored in a BeanTagInfo object.
     *
     * @return A map of xml tags and their associated information.
     */
    public static Map<String, BeanTagInfo> getBeanTags() {
        if (beanTags == null) {
            loadBeanTags();
        }
        if (beanTags.isEmpty()) {
            loadBeanTags();
        }
        return beanTags;
    }

    /**
     * Retrieves the map of bean tags.  If the map has not been created yet the tags are loaded.
     * The Bean tag map is created using the xml tag name of the bean as the key with the value consisting of
     * information about the tag stored in a BeanTagInfo object.
     *
     * @return A map of xml tags and their associated information.
     */
    public static Map<String, BeanTagInfo> getBeanTags(String file) {
        if (customTagClasses == null) {
            loadCustomTagClasses(file);
        }
        if (beanTags == null) {
            loadBeanTags();
        }
        if (beanTags.isEmpty()) {
            loadBeanTags();
        }
        return beanTags;
    }

    /**
     * Retrieves a map of attribute and property information for the bean tags.  if the map has not been created yet
     * the
     * bean tags are loaded.
     * The attribute map is a double layer map with the outer layer consisting of the xml tag as the key linked to a
     * inner map of all properties associated with it.  The inner map uses the attribute or xml sub tag as the key to
     * the information about the property stored in a BeanTagAttributeInfo object.
     *
     * @return A map of xml tags and their associated property information.
     */
    public static Map<String, Map<String, BeanTagAttributeInfo>> getAttributeProperties() {
        if (attributeProperties == null) {
            loadBeanTags();
        }
        if (attributeProperties.isEmpty()) {
            loadBeanTags();
        }
        return attributeProperties;
    }

    /**
     * Loads the list of classes involved in the custom schema from an xml document.  The list included in these xmls
     * can include lists from other documents so recursion is used to go through these other list and compile them all
     * together.
     *
     * @param path - The classpath resource to the list
     * @return A list of all classes to involved in the schema
     */
    private static ArrayList<String> getClassList(String path) {
        ArrayList<String> completeList = new ArrayList<String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            ApplicationContext app = new ClassPathXmlApplicationContext();
            InputStream stream = app.getResource(path).getInputStream();
            document = builder.parse(stream);

            // Read package names into a comma separated list
            NodeList classes = document.getElementsByTagName("class");
            String classList = "";
            for (int i = 0; i < classes.getLength(); i++) {
                classList = classList + classes.item(i).getTextContent() + ",";
            }

            // Split array into list by ,
            if (classList.length() > 0) {
                if (classList.charAt(classList.length() - 1) == ',') {
                    classList = classList.substring(0, classList.length() - 1);
                }
                String list[] = classList.split(",");
                for (int i = 0; i < list.length; i++) {
                    completeList.add(list[i]);
                }
            }

            // Add any schemas being built off of.
            NodeList includes = document.getElementsByTagName("include");
            for (int i = 0; i < includes.getLength(); i++) {
                completeList.addAll(getClassList(includes.item(i).getTextContent()));
            }

        } catch (Exception e) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document;
                File file = new File(path);
                document = builder.parse(file);

                // Read package names into a comma separated list
                NodeList classes = document.getElementsByTagName("class");
                String classList = "";
                for (int i = 0; i < classes.getLength(); i++) {
                    classList = classList + classes.item(i).getTextContent() + ",";
                }

                // Split array into list by ,
                if (classList.length() > 0) {
                    if (classList.charAt(classList.length() - 1) == ',') {
                        classList = classList.substring(0, classList.length() - 1);
                    }
                    String list[] = classList.split(",");
                    for (int i = 0; i < list.length; i++) {
                        completeList.add(list[i]);
                    }
                }

                // Add any schemas being built off of.
                NodeList includes = document.getElementsByTagName("include");
                for (int i = 0; i < includes.getLength(); i++) {
                    completeList.addAll(getClassList(includes.item(i).getTextContent()));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return completeList;
    }

}
