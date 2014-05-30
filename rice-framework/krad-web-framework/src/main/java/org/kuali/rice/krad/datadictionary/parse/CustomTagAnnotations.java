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
package org.kuali.rice.krad.datadictionary.parse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.util.KRADConstants;
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
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates and stores the information defined for the custom schema.  Loads the classes defined as having associated
 * custom schemas and creates the information for the schema by parsing there annotations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomTagAnnotations {
    private static final Log LOG = LogFactory.getLog(CustomTagAnnotations.class);

    private static Map<String, Map<String, BeanTagAttributeInfo>> attributeProperties;
    private static Map<String, BeanTagInfo> beanTags;
    private static Set<Class<?>> customTagClasses;

    private static Map<Class<?>, Set<String>> beanTagsByClass;

    private CustomTagAnnotations() {}

    /**
     * Loads component classes for the custom schema by scanning configured packages.
     *
     * <p>Packages to scan are configured using org.kuali.rice.krad.util.KRADConstants.ConfigParameters#SCHEMA_PACKAGES</p>
     */
    public static void loadTagClasses() {
        String scanPackagesStr = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.ConfigParameters.SCHEMA_PACKAGES);

        String[] scanPackages = StringUtils.split(scanPackagesStr, ",");

        loadTagClasses(scanPackages);
    }

    /**
     * Loads component classes for the custom schema by scanning the given packages.
     *
     * @param scanPackages array of packages to scan
     */
    public static void loadTagClasses(String[] scanPackages) {
        customTagClasses = new HashSet<Class<?>>();

        for (String scanPackage : scanPackages) {
            try {
                customTagClasses.addAll(findTagClasses(StringUtils.trim(scanPackage)));
            } catch (Exception e) {
                throw new RuntimeException("unable to scan package: " + scanPackage, e);
            }
        }
    }

    /**
     * Finds all the classes which have a BeanTag or BeanTags annotation
     *
     * @param basePackage the package to start in
     * @return classes which have BeanTag or BeanTags annotation
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected static List<Class<?>> findTagClasses(String basePackage) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> classes = new ArrayList<Class<?>>();

        String resolvedBasePackage = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(
                basePackage));
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

    /**
     * Returns true if the metadataReader representing the class has a BeanTag or BeanTags annotation
     *
     * @param metadataReader MetadataReader representing the class to analyze
     * @return true if BeanTag or BeanTags annotation is present
     */
    protected static boolean isBeanTag(MetadataReader metadataReader) {
        try {
            Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
            if (c.getAnnotation(BeanTag.class) != null || c.getAnnotation(BeanTags.class) != null) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Load the attribute information of the properties in the class repersented by the new tag.
     *
     * @param doc the RescourceBundle containing the documentation
     */
    protected static void loadAttributeProperties(String tagName, Class<?> tagClass) {
        Map<String, BeanTagAttributeInfo> entries = new HashMap<String, BeanTagAttributeInfo>();

        entries.putAll(getAttributes(tagClass));

        attributeProperties.put(tagName, entries);
    }

    /**
     * Creates a map of entries the properties marked by the annotation of the bean class.
     *
     * @param tagClass class being mapped for attributes
     * @return Return a map of the properties found in the class with there associated information.
     */
    public static Map<String, BeanTagAttributeInfo> getAttributes(Class<?> tagClass) {
        Map<String, BeanTagAttributeInfo> entries = new HashMap<String, BeanTagAttributeInfo>();

        // search the methods of the class using reflection for the attribute annotation
        Method methods[] = tagClass.getMethods();
        for (Method attributeMethod : methods) {
            BeanTagAttribute attribute = attributeMethod.getAnnotation(BeanTagAttribute.class);
            if (attribute == null) {
                continue;
            }

            BeanTagAttributeInfo info = new BeanTagAttributeInfo();

            String propertyName = getFieldName(attributeMethod.getName());
            info.setPropertyName(propertyName);

            if (StringUtils.isBlank(attribute.name())) {
                info.setName(propertyName);
            } else {
                info.setName(attribute.name());
            }

            if (BeanTagAttribute.AttributeType.NOTSET.equals(attribute.type())) {
                BeanTagAttribute.AttributeType derivedType = deriveTypeFromMethod(attributeMethod);

                if (derivedType != null) {
                    info.setType(derivedType);
                }
            } else {
                info.setType(attribute.type());
            }

            info.setValueType(attributeMethod.getReturnType());
            info.setGenericType(attributeMethod.getGenericReturnType());

            validateBeanAttributes(tagClass.getName(), attribute.name(), entries);

            entries.put(info.getName(), info);
        }

        return entries;
    }

    protected static BeanTagAttribute.AttributeType deriveTypeFromMethod(Method attributeMethod) {
        BeanTagAttribute.AttributeType type = null;

        Class<?> returnType = attributeMethod.getReturnType();
        Type genericReturnType = attributeMethod.getGenericReturnType();

        if (TypeUtils.isSimpleType(returnType) || TypeUtils.isClassClass(returnType) || Enum.class.isAssignableFrom(
                returnType)) {
            type = BeanTagAttribute.AttributeType.SINGLEVALUE;
        } else if (returnType.isArray() || List.class.isAssignableFrom(returnType)) {
            type = BeanTagAttribute.AttributeType.LISTBEAN;
            if (genericReturnType instanceof ParameterizedType
                    && ((ParameterizedType) genericReturnType).getActualTypeArguments().length == 1) {
                Type genericParm = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];

                if (isValueType(genericParm)) {
                    type = BeanTagAttribute.AttributeType.LISTVALUE;
                }
            }
        } else if (Set.class.isAssignableFrom(returnType)) {
            type = BeanTagAttribute.AttributeType.SETBEAN;
            if (genericReturnType instanceof ParameterizedType
                    && ((ParameterizedType) genericReturnType).getActualTypeArguments().length == 1) {
                Type genericParm = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];

                if (isValueType(genericParm)) {
                    type = BeanTagAttribute.AttributeType.SETVALUE;
                }
            }
        } else if (Map.class.isAssignableFrom(returnType)) {
            type = BeanTagAttribute.AttributeType.MAPBEAN;
            if (genericReturnType instanceof ParameterizedType
                    && ((ParameterizedType) genericReturnType).getActualTypeArguments().length == 2) {
                Type genericParmKey = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                Type genericParmValue = ((ParameterizedType) genericReturnType).getActualTypeArguments()[1];

                if (isValueType(genericParmKey) && isValueType(genericParmValue)) {
                    type = BeanTagAttribute.AttributeType.MAPVALUE;
                }
            }
        } else {
            type = BeanTagAttribute.AttributeType.SINGLEBEAN;
        }

        return type;
    }

    protected static boolean isValueType(Type type) {
        if (type instanceof Class<?>) {
            Class<?> typeClass = (Class<?>) type;

            if (TypeUtils.isSimpleType(typeClass) || Object.class.equals(typeClass)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean isBeanType(Type type) {
        if (type instanceof Class<?>) {
            Class<?> typeClass = (Class<?>) type;

            if (!TypeUtils.isSimpleType(typeClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Load the information for the xml bean tags defined in the custom schema through annotation of the represented
     * classes.
     */
    protected static void loadBeanTags() {
        // Load the list of class to be searched for annotation definitions
        if (customTagClasses == null) {
            loadTagClasses();
        }

        beanTags = new HashMap<String, BeanTagInfo>();
        attributeProperties = new HashMap<String, Map<String, BeanTagAttributeInfo>>();
        beanTagsByClass = new HashMap<Class<?>, Set<String>>();

        // for each class create the bean tag information and its associated attribute properties
        for (Class<?> tagClass : customTagClasses) {
            BeanTag[] annotations = new BeanTag[1];

            BeanTag tag = tagClass.getAnnotation(BeanTag.class);
            if (tag != null) {
                //single tag case
                annotations[0] = tag;
            } else {
                //multi-tag case
                BeanTags tags = tagClass.getAnnotation(BeanTags.class);

                if (tags != null) {
                    annotations = tags.value();
                } else {
                    //TODO throw exception instead?
                    continue;
                }
            }

            Set<String> classBeanTags = new HashSet<String>();
            for (int j = 0; j < annotations.length; j++) {
                BeanTag annotation = annotations[j];

                BeanTagInfo info = new BeanTagInfo();
                info.setTag(annotation.name());

                if (j == 0) {
                    info.setDefaultTag(true);
                }

                info.setBeanClass(tagClass);
                info.setParent(annotation.parent());

                validateBeanTag(annotation.name());

                beanTags.put(annotation.name(), info);

                loadAttributeProperties(annotation.name(), tagClass);

                classBeanTags.add(annotation.name());
            }

            beanTagsByClass.put(tagClass, classBeanTags);
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

        // Since the annotation is attached to the get function the property name starts at the 4th letter
        // and has been upper-cased as assumed by the Spring Beans.
        String letter = methodName.substring(3, 4);
        return letter.toLowerCase() + methodName.substring(4, methodName.length());
    }

    /**
     * Validates that the tag name is not already taken.
     *
     * @param tagName the name of the tag for the new bean
     * @return true if the validation passes, false otherwise
     */
    protected static boolean validateBeanTag(String tagName) {
        boolean valid = true;

        Set<String> tagNames = beanTags.keySet();
        if (tagNames.contains(tagName)) {
            LOG.error("Duplicate tag name " + tagName);

            valid = false;
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
        if (beanTags == null || beanTags.isEmpty()) {
            loadBeanTags();
        }

        return beanTags;
    }

    /**
     * Retrieves the set of tag names that are valid beans for the given class.
     *
     * @param clazz class to retrieve tag names for
     * @return set of tag names as string, or null if none are found
     */
    public static Set<String> getBeanTagsByClass(Class<?> clazz) {
        Set<String> beanTags = null;

        if (beanTagsByClass != null && beanTagsByClass.containsKey(clazz)) {
            beanTags = beanTagsByClass.get(clazz);
        }
        if (beanTags == null) {
            loadBeanTags();
        }

        return beanTags;
    }

    /**
     * Retrieves a map of attribute and property information for the bean tags.  if the map has not been created yet
     * the bean tags are loaded. The attribute map is a double layer map with the outer layer consisting of the xml
     * tag as the key linked to a inner map of all properties associated with it.  The inner map uses the attribute
     * or xml sub tag as the key to the information about the property stored in a BeanTagAttributeInfo object.
     *
     * @return A map of xml tags and their associated property information.
     */
    public static Map<String, Map<String, BeanTagAttributeInfo>> getAttributeProperties() {
        if ((attributeProperties == null) || attributeProperties.isEmpty()) {
            loadBeanTags();
        }

        return attributeProperties;
    }

    public static String findPropertyByType(String parentTag, String childTag) {
        String propertyName = null;

        Class<?> childTagClass = beanTags.get(childTag).getBeanClass();

        Map<String, BeanTagAttributeInfo> propertyInfos = attributeProperties.get(parentTag);

        for (Map.Entry<String, BeanTagAttributeInfo> propertyInfo : propertyInfos.entrySet()) {
            BeanTagAttributeInfo info = propertyInfo.getValue();

            if (info.getType().equals(BeanTagAttribute.AttributeType.BYTYPE)
                    || info.getType().equals(BeanTagAttribute.AttributeType.DIRECTORBYTYPE)) {
                if (info.getValueType().isAssignableFrom(childTagClass)) {
                    propertyName = info.getPropertyName();
                }
            }
        }

        return propertyName;
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

            ApplicationContext app = new ClassPathXmlApplicationContext();
            InputStream stream = app.getResource(path).getInputStream();

            Document document = builder.parse(stream);

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

                File file = new File(path);
                Document document = builder.parse(file);

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
                throw new RuntimeException(e1);
            }
        }

        return completeList;
    }

}
