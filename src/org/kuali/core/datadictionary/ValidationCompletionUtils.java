/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.datadictionary;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.core.util.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.document.Document;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.service.PersistenceStructureService;

public class ValidationCompletionUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidationCompletionUtils.class);
    private PersistenceStructureService persistenceStructureService;

    public ValidationCompletionUtils() {
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * @param clazz
     * @return true if the given Class is a descendent of BusinessObject
     */
    public boolean isBusinessObjectClass(Class clazz) {
        return isDescendentClass(clazz, BusinessObject.class);
    }

    /**
     * @param clazz
     * @return true if the given Class is a descendent of Maintainable
     */
    public boolean isMaintainableClass(Class clazz) {
        return isDescendentClass(clazz, Maintainable.class);
    }

    /**
     * @param clazz
     * @return true if the given Class is a descendent of Document
     */
    public boolean isDocumentClass(Class clazz) {
        return isDescendentClass(clazz, Document.class);
    }

    /**
     * @param clazz
     * @return true if the given Class is a descendent of PreRulesCheck
     */
    public boolean isPreRulesCheckClass(Class clazz) {
        return isDescendentClass(clazz, PreRulesCheck.class);
    }

    /**
     * @param descendentClass
     * @param ancestorClass
     * @return true if descendentClass is a descendent of ancestorClass
     */
    public boolean isDescendentClass(Class descendentClass, Class ancestorClass) {
        if (descendentClass == null) {
            throw new IllegalArgumentException("invalid (null) descendentClass");
        }
        else if (ancestorClass == null) {
            throw new IllegalArgumentException("invalid (null) ancestorClass");
        }

        return ancestorClass.isAssignableFrom(descendentClass);
    }

    /**
     * @param clazz
     * @param propertyName
     * @return true if the given propertyName names a property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public boolean isPropertyOf(Class targetClass, String propertyName) {
        if (targetClass == null) {
            throw new IllegalArgumentException("invalid (null) targetClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);

        boolean isPropertyOf = (propertyDescriptor != null);
        return isPropertyOf;
    }

    /**
     * @param clazz
     * @param propertyName
     * @return true if the given propertyName names a Collection property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public boolean isCollectionPropertyOf(Class targetClass, String propertyName) {
        boolean isCollectionPropertyOf = false;

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);
        if (propertyDescriptor != null) {
            Class clazz = propertyDescriptor.getPropertyType();

            if ((clazz != null) && Collection.class.isAssignableFrom(clazz)) {
                isCollectionPropertyOf = true;
            }
        }

        return isCollectionPropertyOf;
    }

    /**
     * This method determines the Class of the attributeName passed in. Null will be returned if the member is not available, or if
     * a reflection exception is thrown.
     * 
     * @param rootClass - Class that the attributeName property exists in.
     * @param attributeName - Name of the attribute you want a class for.
     * @return The Class of the attributeName, if the attribute exists on the rootClass. Null otherwise.
     */
    public Class getAttributeClass(Class boClass, String attributeName) {

        Class attributeClass = null;

        // fail loudly if the attributeName isnt a member of rootClass
        if (!isPropertyOf(boClass, attributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootClass '" + boClass.getName() + "'");
        }

        BusinessObject boInstance;
        try {
            boInstance = (BusinessObject) boClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // attempt to retrieve the class of the property
        try {
            attributeClass = ObjectUtils.getPropertyType(boInstance, attributeName, persistenceStructureService);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return attributeClass;
    }

    /**
     * This method determines the Class of the elements in the collectionName passed in.
     * 
     * @param boClass Class that the collectionName collection exists in.
     * @param collectionName the name of the collection you want the element class for
     * @return
     */
    public Class getCollectionElementClass(Class boClass, String collectionName) {
        if (boClass == null) {
            throw new IllegalArgumentException("invalid (null) boClass");
        }
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = collectionName.split("\\.");
        Class currentClass = boClass;

        for (int i = 0; i <intermediateProperties.length; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);


                if (propertyDescriptor != null) {

                    Class type = propertyDescriptor.getPropertyType();
                    if (Collection.class.isAssignableFrom(type)) {

                        if (persistenceStructureService.isPersistable(currentClass)) {

                            Map<String, Class> collectionClasses = new HashMap<String, Class>();
                            collectionClasses = persistenceStructureService.listCollectionObjectTypes(currentClass);
                            currentClass = collectionClasses.get(currentPropertyName);

                        }
                        else {
                            throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");
                        }

                    }
                    else {

                        currentClass = propertyDescriptor.getPropertyType();

                    }
                }
            }

        return currentClass;
    }

    static private Map<String, Map<String, PropertyDescriptor>> cache = new TreeMap<String, Map<String, PropertyDescriptor>>();

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    private PropertyDescriptor buildReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = propertyName.split("\\.");
        int lastLevel = intermediateProperties.length - 1;
        Class currentClass = propertyClass;

        for (int i = 0; i <= lastLevel; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);

            if (i < lastLevel) {

                if (propertyDescriptor != null) {

                    Class type = propertyDescriptor.getPropertyType();
                    if (Collection.class.isAssignableFrom(type)) {

                        if (persistenceStructureService.isPersistable(currentClass)) {

                            Map<String, Class> collectionClasses = new HashMap<String, Class>();
                            collectionClasses = persistenceStructureService.listCollectionObjectTypes(currentClass);
                            currentClass = collectionClasses.get(currentPropertyName);

                        }
                        else {

                            throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");

                        }

                    }
                    else {

                        currentClass = propertyDescriptor.getPropertyType();

                    }

                }

            }

        }

        return propertyDescriptor;
    }

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    private PropertyDescriptor buildSimpleReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor p = null;

        // check to see if we've cached this descriptor already. if yes, return true.
        String propertyClassName = propertyClass.getName();
        Map<String, PropertyDescriptor> m = cache.get(propertyClassName);
        if (null != m) {
            p = m.get(propertyName);
            if (null != p) {
                return p;
            }
        }

        String prefix = StringUtils.capitalize(propertyName);
        String getName = "get" + prefix;
        String isName = "is" + prefix;

        try {

            p = new PropertyDescriptor(propertyName, propertyClass, getName, null);

        }
        catch (IntrospectionException e) {
            try {

                p = new PropertyDescriptor(propertyName, propertyClass, isName, null);

            }
            catch (IntrospectionException f) {
                // ignore it
            }
        }

        // cache the property descriptor if we found it.
        if (null != p) {

            if (null == m) {

                m = new TreeMap<String, PropertyDescriptor>();
                cache.put(propertyClassName, m);

            }

            m.put(propertyName, p);

        }

        return p;
    }
}