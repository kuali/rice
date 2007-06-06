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
package org.kuali.core.util;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectExtension;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.cache.CacheException;
import org.kuali.core.web.format.FormatException;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.pojo.PojoPropertyUtilsBean;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class contains various Object, Proxy, and serialization utilities.
 */
public class ObjectUtils {
    private static final Logger LOG = Logger.getLogger(ObjectUtils.class);

    private ObjectUtils() {
    }

    /**
     * Uses Serialization mechanism to create a deep copy of the given Object. As a special case, deepCopy of null returns null,
     * just to make using this method simpler. For a detailed discussion see:
     * http://www.javaworld.com/javaworld/javatips/jw-javatip76.html
     * 
     * @param src
     * @return deep copy of the given Serializable
     */
    public static Serializable deepCopy(Serializable src) {
        CopiedObject co = deepCopyForCaching(src);
        return co.getContent();
    }


    /**
     * Uses Serialization mechanism to create a deep copy of the given Object, and returns a CacheableObject instance containing the
     * deepCopy and its size in bytes. As a special case, deepCopy of null returns a cacheableObject containing null and a size of
     * 0, to make using this method simpler. For a detailed discussion see:
     * http://www.javaworld.com/javaworld/javatips/jw-javatip76.html
     * 
     * @param src
     * @return CopiedObject containing a deep copy of the given Serializable and its size in bytes
     */
    public static CopiedObject deepCopyForCaching(Serializable src) {
        Timer t0 = new Timer("deepCopy");
        CopiedObject co = new CopiedObject();

        int copySize = 0;
        if (src != null) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                ByteArrayOutputStream serializer = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(serializer);
                oos.writeObject(src);

                co.setSize(serializer.size());

                ByteArrayInputStream deserializer = new ByteArrayInputStream(serializer.toByteArray());
                ois = new ObjectInputStream(deserializer);
                Serializable copy = (Serializable) ois.readObject();

                co.setContent(copy);
            }
            catch (IOException e) {
                throw new CacheException("unable to complete deepCopy from src '" + src.toString() + "'", e);
            }
            catch (ClassNotFoundException e) {
                throw new CacheException("unable to complete deepCopy from src '" + src.toString() + "'", e);
            }
            finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                }
                catch (IOException e) {
                    // ignoring this IOException, since the streams are going to be abandoned now anyway
                }
            }
        }
        t0.log();
        return co;
    }


    /**
     * Converts the object to a byte array using the output stream.
     * 
     * @param object
     * @return byte array of the object
     */
    public static byte[] toByteArray(Object object) throws Exception {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(object); // C
            // oos.flush(); // D
            return bos.toByteArray();
        }
        catch (Exception e) {
            LOG.warn("Exception in ObjectUtil = " + e);
            throw (e);
        }
        finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    /**
     * reconsitiutes the object that was converted into a byte array by toByteArray
     * @param bytes
     * @return
     * @throws Exception
     */
    public static Object fromByteArray(byte[] bytes) throws Exception {
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            return obj;
        }
        catch (Exception e) {
            LOG.warn("Exception in ObjectUtil = " + e);
            throw (e);
        }
        finally {
            if (ois != null) {
                ois.close();
            }
        }
    }
    
    /**
     * use MD5 to create a one way hash of an object
     * 
     * @param object
     * @return
     */
    public static String getMD5Hash(Object object) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toByteArray(object));
            return new String(md.digest());
        }
        catch (Exception e) {
            LOG.warn(e);
            throw e;
        }
    }

    /**
     * Creates a new instance of a given BusinessObject, copying fields specified in template from the given source BO. For example,
     * this can be used to create an AccountChangeDetail based on a particular Account.
     * 
     * @param template a map defining the relationships between the fields of the newly created BO, and the source BO.  For each K (key), V (value)
     * entry, the value of property V on the source BO will be assigned to the K property of the newly created BO
     * 
     * @see org.kuali.core.maintenance.util.MaintenanceUtils
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws FormatException
     */

    public static BusinessObject createHybridBusinessObject(Class businessObjectClass, BusinessObject source, Map<String, String> template) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        BusinessObject obj = null;
        try {
            obj = (BusinessObject) businessObjectClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + businessObjectClass.getName(), e);
        }

        for (String name : template.keySet()) {
            String sourcePropertyName = template.get(name);
            setObjectProperty(obj, name, easyGetPropertyType(source, sourcePropertyName), getPropertyValue(source, sourcePropertyName));
        }

        return obj;
    }

    /**
     * This method simply uses PojoPropertyUtilsBean logic to get the Class of a Class property.
     * This method does not have any of the logic needed to obtain the Class of an element of a Collection specified in the DataDictionary.
     * 
     * @param object An instance of the Class of which we're trying to get the property Class.
     * @param propertyName The name of the property.

     * @return
     * 
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    static public Class easyGetPropertyType(Object object, String propertyName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // FIXME (laran) This dependence should be inverted. Instead of having a core class
        // depend on PojoPropertyUtilsBean, which is in the web layer, the web layer
        // should depend downward to the core.
        return (new PojoPropertyUtilsBean()).getPropertyType(object, propertyName);

    }

    /**
     * Returns the type of the property in the object. This implementation is not smart enough to look through a Collection to get the property type
     * of an attribute of an element in the collection.
     * 
     * NOTE: A patch file attached to https://test.kuali.org/jira/browse/KULRNE-4435 contains a modified version of this method which IS smart enough
     * to look through Collections. This patch is currently under review.
     * 
     * @param object An instance of the Class for which we're trying to get the property type.
     * @param propertyName The name of the property of the Class the Class of which we're trying to get. Dot notation is used to separate properties.
     *                     TODO: The rules about this dot notation needs to be explained in Confluence using examples. 
     * @param persistenceStructureService Needed to get the type of elements in a Collection from OJB. 
     * 
     * @return Object will be null if any parent property for the given property is null.
     */
    public static Class getPropertyType(Object object, String propertyName, PersistenceStructureService persistenceStructureService) {

        if (object == null || propertyName == null) {
            throw new RuntimeException("Business object and property name can not be null");
        }

        Class propertyType = null;

        try {

            try {

                // Try to simply use the default or simple way of getting the property type.
                propertyType = PropertyUtils.getPropertyType(object, propertyName);
                
            } catch(IllegalArgumentException ex) {
                // swallow the exception, propertyType stays null
            } catch(NoSuchMethodException nsme) {
                // swallow the exception, propertyType stays null
            }

            // if the property type as determined from the object is PersistableBusinessObject,
            // then this must be an extension attribute -- attempt to get the property type from the
            // persistence structure service
            if ( propertyType != null && propertyType.equals( PersistableBusinessObjectExtension.class ) ) {
            	propertyType = persistenceStructureService.getBusinessObjectAttributeClass( ProxyHelper.getRealClass( object ), propertyName );
            }
            
            // If the easy way didn't work ...
            if (null == propertyType && -1 != propertyName.indexOf('.')) {

                if (null == persistenceStructureService) {

                    LOG.info("PropertyType couldn't be determined simply and no PersistenceStructureService was given. If you pass in a PersistenceStructureService I can look in other places to try to determine the type of the property.");

                } else {
                    
                    String prePeriod = StringUtils.substringBefore(propertyName, ".");
                    String postPeriod = StringUtils.substringAfter(propertyName, ".");
                    
                    Class prePeriodClass = getPropertyType(object, prePeriod, persistenceStructureService);
                    Object prePeriodClassInstance = prePeriodClass.newInstance();
                    propertyType = getPropertyType(prePeriodClassInstance, postPeriod, persistenceStructureService);

                } 

            } else if(Collection.class.isAssignableFrom(propertyType)) {
                
                Map<String, Class> map = persistenceStructureService.listCollectionObjectTypes(object.getClass());
                propertyType = map.get(propertyName);
                
            }

        }
        catch (Exception e) {

            LOG.debug("unable to get property type for " + propertyName + " " + e.getMessage());
            // continue and return null for propertyType

        }

        return propertyType;
    }

    /**
     * Returns the value of the property in the object.
     * 
     * @param businessObject
     * @param propertyName
     * 
     * @return Object will be null if any parent property for the given property is null.
     */
    public static Object getPropertyValue(Object businessObject, String propertyName) {
        if (businessObject == null || propertyName == null) {
            throw new RuntimeException("Business object and property name can not be null");
        }

        Object propertyValue = null;
        try {
            propertyValue = PropertyUtils.getProperty(businessObject, propertyName);
        }
        catch (NestedNullException e) {
            // continue and return null for propertyValue
        }
        catch (IllegalAccessException e1) {
            LOG.error("error getting property value for  " + propertyName + " " + e1.getMessage());
            throw new RuntimeException("error getting property value for  " + propertyName + " " + e1.getMessage());
        }
        catch (InvocationTargetException e1) {
            // continue and return null for propertyValue
        }
        catch (NoSuchMethodException e1) {
            LOG.error("error getting property value for  " + propertyName + " " + e1.getMessage());
            throw new RuntimeException("error getting property value for  " + propertyName + " " + e1.getMessage());
        }

        return propertyValue;
    }
    
    /**
     * Sets the property of an object with the given value. Converts using the formatter of the type for the property.
     * Note: propertyType does not need passed, is found by util method.
     */
    public static void setObjectProperty(Object bo, String propertyName, Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class propertyType = easyGetPropertyType(bo, propertyName);
        setObjectProperty(bo, propertyName, propertyType, propertyValue);
    }


    /**
     * Sets the property of an object with the given value. Converts using the formatter of the given type if one is found.
     * 
     * @param bo
     * @param propertyName
     * @param propertyType
     * @param propertyValue
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setObjectProperty(Object bo, String propertyName, Class propertyType, Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // reformat propertyValue, if necessary
        boolean reformat = false;
        if (propertyValue != null && propertyType.isAssignableFrom(String.class)) {
            // always reformat if the destination is a String
            reformat = true;
        }
        else if (propertyValue != null && !propertyType.isAssignableFrom(propertyValue.getClass())) {
            // otherwise, only reformat if the propertyValue can't be assigned into the property
            reformat = true;
        }
        
        // attempting to set boolean fields to null throws an exception, set to false instead
        if (boolean.class.isAssignableFrom(propertyType) && propertyValue == null) {
            propertyValue = false;
        }

        if (reformat && Formatter.findFormatter(propertyType) != null) {
            Formatter formatter = Formatter.getFormatter(propertyType);
            LOG.debug("reformatting propertyValue using Formatter " + formatter.getClass().getName());
            propertyValue = formatter.convertFromPresentationFormat(propertyValue);
        }


        // set property in the object
        (new PojoPropertyUtilsBean()).setNestedProperty(bo, propertyName, propertyValue);
    }


    /**
     * Sets the property of an object with the given value. Converts using the given formatter, if it isn't null.
     * 
     * @param formatter
     * @param bo
     * @param propertyName
     * @param type
     * @param propertyValue
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setObjectProperty(Formatter formatter, Object bo, String propertyName, Class type, Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // convert value using formatter for type
        if (formatter != null) {
            propertyValue = formatter.convertFromPresentationFormat(propertyValue);
        }

        // set property in the object
        PropertyUtils.setNestedProperty(bo, propertyName, propertyValue);
    }


    /**
     * Recursive; sets all occurences of the property in the object, its nested objects and its object lists with the given value.
     * 
     * @param bo
     * @param propertyName
     * @param type
     * @param propertyValue
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setObjectPropertyDeep(Object bo, String propertyName, Class type, Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // Base return cases to avoid null pointers & infinite loops
        if (isNull(bo) || !PropertyUtils.isReadable(bo, propertyName) || (propertyValue != null && propertyValue.equals(getPropertyValue(bo, propertyName))) || (type != null && !type.equals(easyGetPropertyType(bo, propertyName)))) {
            return;
        }
        
        // need to materialize the updateable collections before resetting the property, because it may be used in the retrieval
        materializeUpdateableCollections(bo);
        
        // Set the property in the BO
        setObjectProperty(bo, propertyName, type, propertyValue);

        // Now drill down and check nested BOs and BO lists
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bo.getClass());
        for (int i = 0; i < propertyDescriptors.length; i++) {

            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            // Business Objects
            if (propertyDescriptor.getPropertyType() != null && (BusinessObject.class).isAssignableFrom(propertyDescriptor.getPropertyType()) && PropertyUtils.isReadable(bo, propertyDescriptor.getName())) {
                BusinessObject nestedBo = (BusinessObject) getPropertyValue(bo, propertyDescriptor.getName());
                setObjectPropertyDeep(nestedBo, propertyName, type, propertyValue);
            }

            // Lists
            else if (propertyDescriptor.getPropertyType() != null && (List.class).isAssignableFrom(propertyDescriptor.getPropertyType()) && getPropertyValue(bo, propertyDescriptor.getName()) != null) {

                List propertyList = (List) getPropertyValue(bo, propertyDescriptor.getName());
                for(Object listedBo: propertyList) {
                    if (listedBo != null && listedBo instanceof BusinessObject) {
                        setObjectPropertyDeep(listedBo, propertyName, type, propertyValue);
                    }
                } // end for
            }
        } // end for
    }
    /*
    * Recursive up to a given depth; sets all occurences of the property in the object, its nested objects and its object lists with the given value.
    */  
    public static void setObjectPropertyDeep(Object bo, String propertyName, Class type, Object propertyValue, int depth) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Base return cases to avoid null pointers & infinite loops
        if (depth == 0 || isNull(bo) || !PropertyUtils.isReadable(bo, propertyName)) {
            return;
        }
        
        // need to materialize the updateable collections before resetting the property, because it may be used in the retrieval
        materializeUpdateableCollections(bo);
        
        // Set the property in the BO
        setObjectProperty(bo, propertyName, type, propertyValue);

        // Now drill down and check nested BOs and BO lists
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bo.getClass());
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            // Business Objects
            if (propertyDescriptor.getPropertyType() != null && (BusinessObject.class).isAssignableFrom(propertyDescriptor.getPropertyType()) && PropertyUtils.isReadable(bo, propertyDescriptor.getName())) {
                BusinessObject nestedBo = (BusinessObject) getPropertyValue(bo, propertyDescriptor.getName());
                setObjectPropertyDeep(nestedBo, propertyName, type, propertyValue, depth - 1);
            }

            // Lists
            else if (propertyDescriptor.getPropertyType() != null && (List.class).isAssignableFrom(propertyDescriptor.getPropertyType()) && getPropertyValue(bo, propertyDescriptor.getName()) != null) {

                List propertyList = (List) getPropertyValue(bo, propertyDescriptor.getName());
                for(Object listedBo: propertyList) {
                    if (listedBo != null && listedBo instanceof BusinessObject) {
                        setObjectPropertyDeep(listedBo, propertyName, type, propertyValue, depth - 1);
                    }
                } // end for
            }
        } // end for
    }
    
    /**
     * 
     * This method checks for updateable collections on the business object provided and materializes the corresponding collection proxies
     * 
     * @param bo The business object for which you want unpdateable, proxied collections materialized
     * @throws FormatException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static void materializeUpdateableCollections(Object bo) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (isNotNull(bo)) {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bo.getClass());
            for (int i = 0; i < propertyDescriptors.length; i++) {
                if (KNSServiceLocator.getPersistenceStructureService().hasCollection(bo.getClass(), propertyDescriptors[i].getName()) && KNSServiceLocator.getPersistenceStructureService().isCollectionUpdatable(bo.getClass(), propertyDescriptors[i].getName())) {
                    Collection updateableCollection = (Collection) getPropertyValue(bo, propertyDescriptors[i].getName());
                    if ((updateableCollection != null) && ProxyHelper.isCollectionProxy(updateableCollection)) {
                        materializeObjects(updateableCollection);
                    }
                }
            }
        }
    }


    /**
     * Removes all query characters from a string.
     * 
     * @param string
     * 
     * @return Cleaned string
     */
    public static String clean(String string) {
        for (int i = 0; i < Constants.QUERY_CHARACTERS.length; i++) {
            string = StringUtils.replace(string, Constants.QUERY_CHARACTERS[i], Constants.EMPTY_STRING);
        }
        return string;
    }


    /**
     * Compares two business objects for equality of type and key values.
     * 
     * @param bo1
     * @param bo2
     * 
     * @return boolean indicating whether the two objects are equal.
     */
    public static boolean equalByKeys(BusinessObject bo1, BusinessObject bo2) {
        boolean equal = true;

        if (bo1 == null && bo2 == null) {
            equal = true;
        }
        else if (bo1 == null || bo2 == null) {
            equal = false;
        }
        else if (!bo1.getClass().getName().equals(bo2.getClass().getName())) {
            equal = false;
        }
        else {
            Map bo1Keys = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(bo1);
            Map bo2Keys = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(bo2);
            for (Iterator iter = bo1Keys.keySet().iterator(); iter.hasNext();) {
                String keyName = (String) iter.next();
                if (bo1Keys.get(keyName) != null && bo2Keys.get(keyName) != null) {
                    if (!bo1Keys.get(keyName).toString().equals(bo2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                }
            }
        }


        return equal;
    }

    /**
     * Compares a business object with a List of BOs to determine if an object with the same key as the BO exists in the list.
     * 
     * @param controlList - The list of items to check
     * @param bo - The BO whose keys we are looking for in the controlList
     * 
     * @return boolean
     */
    public static boolean collectionContainsObjectWithIdentitcalKey(Collection controlList, BusinessObject bo) {
        boolean objectExistsInList = false;

        for (Iterator i = controlList.iterator(); i.hasNext();) {
            if (equalByKeys((BusinessObject) i.next(), bo)) {
                return true;
            }
        }

        return objectExistsInList;
    }

    /**
     * Compares a business object with a Collection of BOs to count how many have the same key as the BO.
     * 
     * @param collection - The collection of items to check
     * @param bo - The BO whose keys we are looking for in the collection
     * 
     * @return how many have the same keys
     */
    public static int countObjectsWithIdentitcalKey(Collection<? extends BusinessObject> collection, BusinessObject bo) {
        // todo: genericize collectionContainsObjectWithIdentitcalKey() to leverage this method?
        int n = 0;
        for (BusinessObject item : collection) {
            if (equalByKeys(item, bo)) {
                n++;
            }
        }
        return n;
    }

    /**
     * Compares a business object with a List of BOs to determine if an object with the same key as the BO exists in the list. If it
     * does, the item is removed from the List. This is functionally similar to List.remove() that operates only on Key values.
     * 
     * @param controlList - The list of items to check
     * @param bo - The BO whose keys we are looking for in the controlList
     */

    public static void removeObjectWithIdentitcalKey(Collection controlList, BusinessObject bo) {
        for (Iterator i = controlList.iterator(); i.hasNext();) {
            BusinessObject listBo = (BusinessObject) i.next();
            if (equalByKeys(listBo, bo)) {
                i.remove();
            }
        }
    }

    /**
     * Compares a business object with a List of BOs to determine if an object with the same key as the BO exists in the list. If it
     * does, the item is returned.
     * 
     * @param controlList - The list of items to check
     * @param bo - The BO whose keys we are looking for in the controlList
     */

    public static BusinessObject retrieveObjectWithIdentitcalKey(Collection controlList, BusinessObject bo) {
        BusinessObject returnBo = null;

        for (Iterator i = controlList.iterator(); i.hasNext();) {
            BusinessObject listBo = (BusinessObject) i.next();
            if (equalByKeys(listBo, bo)) {
                returnBo = listBo;
            }
        }

        return returnBo;
    }

    /**
     * Determines if a given string could represent a nested attribute of an object.
     * 
     * @param attributeName
     * 
     * @return true if the attribute is nested
     */
    public static boolean isNestedAttribute(String attributeName) {
        boolean isNested = false;

        if (StringUtils.contains(attributeName, ".")) {
            isNested = true;
        }

        return isNested;
    }

    /**
     * Returns the prefix of a nested attribute name, or the empty string if the attribute name is not nested.
     * 
     * @param attributeName
     * 
     * @return everything BEFORE the last "." character in attributeName
     */
    public static String getNestedAttributePrefix(String attributeName) {
        String prefix = "";

        if (StringUtils.contains(attributeName, ".")) {
            prefix = StringUtils.substringBeforeLast(attributeName, ".");
        }

        return prefix;
    }

    /**
     * Returns the primitive part of an attribute name string.
     * 
     * @param attributeName
     * 
     * @return everything AFTER the last "." character in attributeName
     */
    public static String getNestedAttributePrimitive(String attributeName) {
        String primitive = attributeName;

        if (StringUtils.contains(attributeName, ".")) {
            primitive = StringUtils.substringAfterLast(attributeName, ".");
        }

        return primitive;
    }

    /**
     * This method is a OJB Proxy-safe way to test for null on a proxied object that may or may not be materialized yet. It is safe
     * to use on a proxy (materialized or non-materialized) or on a non-proxy (ie, regular object). Note that this will force a
     * materialization of the proxy if the object is a proxy and unmaterialized.
     * 
     * @param object - any object, proxied or not, materialized or not
     * 
     * @return true if the object (or underlying materialized object) is null, false otherwise
     */
    public static boolean isNull(Object object) {

        // regardless, if its null, then its null
        if (object == null) {
            return true;
        }

        // only try to materialize the object to see if its null if this is a
        // proxy object
        if (ProxyHelper.isProxy(object) || ProxyHelper.isCollectionProxy(object)) {
            if (ProxyHelper.getRealObject(object) == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method is a OJB Proxy-safe way to test for notNull on a proxied object that may or may not be materialized yet. It is
     * safe to use on a proxy (materialized or non-materialized) or on a non-proxy (ie, regular object). Note that this will force a
     * materialization of the proxy if the object is a proxy and unmaterialized.
     * 
     * @param object - any object, proxied or not, materialized or not
     * 
     * @return true if the object (or underlying materialized object) is not null, true if its null
     */
    public static boolean isNotNull(Object object) {
        return !ObjectUtils.isNull(object);
    }

    /**
     * This method runs the ObjectUtils.isNotNull() method for each item in a list of BOs. ObjectUtils.isNotNull() will materialize
     * the objects if they are currently OJB proxies.
     * 
     * @param possiblyProxiedObjects - a Collection of objects that may be proxies
     */
    public static void materializeObjects(Collection possiblyProxiedObjects) {
        for (Iterator i = possiblyProxiedObjects.iterator(); i.hasNext();) {
            ObjectUtils.isNotNull(i.next());
        }
    }

    /**
     * This method attempts to materialize all of the proxied reference objects (ie, sub-objects) hanging off the passed-in BO
     * object. It will do it down to the specified depth. An IllegalArgumentException will be thrown if the bo object passed in is
     * itself a non-materialized proxy object. If the bo passed in has no proxied sub-objects, then the object will not be modified,
     * and no errors will be thrown. WARNING: Be careful using depth any greater than 2. The number of DB hits, time, and memory
     * consumed grows exponentially with each additional increment to depth. Make sure you really need that depth before doing so.
     * 
     * @param bo A valid, populated BusinessObject containing (possibly) proxied sub-objects. This object will be modified in place.
     * @param depth int Value 0-5 indicating how deep to recurse the materialization. If a zero (0) is passed in, then no work will
     *        be done.
     */
    public static void materializeSubObjectsToDepth(PersistableBusinessObject bo, int depth) {
        if (bo == null) {
            throw new IllegalArgumentException("The bo passed in was null.");
        }
        if (depth < 0 || depth > 5) {
            throw new IllegalArgumentException("The depth passed in was out of bounds.  Only values " + "between 0 and 5, inclusively, are allowed.");
        }

        // if depth is zero, then we're done recursing and can just exit
        if (depth == 0) {
            return;
        }

        // deal with the possibility that the bo passed in (ie, the parent object) is an un-materialized proxy
        if (ProxyHelper.isProxy(bo)) {
            if (!ProxyHelper.isMaterialized(bo)) {
                throw new IllegalArgumentException("The bo passed in is an un-materialized proxy, and cannot be used.");
            }
        }

        // get the list of reference objects hanging off the parent BO
        Map<String, Class> references = KNSServiceLocator.getPersistenceStructureService().listReferenceObjectFields(bo);

        // initialize our in-loop objects
        String referenceName = "";
        Class referenceClass = null;
        Object referenceValue = null;
        Object realReferenceValue = null;

        // for each reference object on the parent bo
        for (Iterator iter = references.keySet().iterator(); iter.hasNext();) {
            referenceName = (String) iter.next();
            referenceClass = references.get(referenceName);

            // if its a proxy, replace it with a non-proxy
            referenceValue = getPropertyValue(bo, referenceName);
            if (referenceValue != null) {
                if (ProxyHelper.isProxy(referenceValue)) {
                    realReferenceValue = ProxyHelper.getRealObject(referenceValue);
                    if (realReferenceValue != null) {
                        try {
                            setObjectProperty(bo, referenceName, referenceClass, realReferenceValue);
                        }
                        catch (FormatException e) {
                            throw new RuntimeException("FormatException: could not set the property '" + referenceName + "'.", e);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("IllegalAccessException: could not set the property '" + referenceName + "'.", e);
                        }
                        catch (InvocationTargetException e) {
                            throw new RuntimeException("InvocationTargetException: could not set the property '" + referenceName + "'.", e);
                        }
                        catch (NoSuchMethodException e) {
                            throw new RuntimeException("NoSuchMethodException: could not set the property '" + referenceName + "'.", e);
                        }
                    }
                }

                // recurse down through this reference object
                if (realReferenceValue instanceof PersistableBusinessObject && depth > 1) {
                    materializeSubObjectsToDepth((PersistableBusinessObject) realReferenceValue, depth - 1);
                }
            }

        }
    }

    /**
     * This method attempts to materialize all of the proxied reference objects (ie, sub-objects) hanging off the passed-in BO
     * object. It will do it just three levels down. In other words, it will only materialize the objects that are direct members of
     * the bo, objects that are direct members of those bos, that one more time, and no further down. An IllegalArgumentException 
     * will be thrown if the bo object passed in is itself a non-materialized proxy object. If the bo passed in has no proxied 
     * sub-objects, then the object will not be modified, and no errors will be thrown.
     * 
     * @param bo A valid, populated BusinessObject containing (possibly) proxied sub-objects. This object will be modified in place.
     */
    public static void materializeAllSubObjects(PersistableBusinessObject bo) {
        materializeSubObjectsToDepth(bo, 3);
    }

    /**
     * This method safely extracts either simple values OR nested values. For example, if the bo is SubAccount, and the fieldName is
     * a21SubAccount.subAccountTypeCode, this thing makes sure it gets the value off the very end attribute, no matter how deeply
     * nested it is. The code would be slightly simpler if this was done recursively, but this is safer, and consumes a constant
     * amount of memory, no matter how deeply nested it goes.
     * 
     * @param bo
     * @param fieldName
     * 
     * @return The field value if it exists. If it doesnt, and the name is invalid, and
     */
    public static Object getNestedValue(BusinessObject bo, String fieldName) {

        if (bo == null) {
            throw new IllegalArgumentException("The bo passed in was null.");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("The fieldName passed in was blank.");
        }

        // okay, this section of code is to handle sub-object values, like
        // SubAccount.a21SubAccount.subAccountTypeCode. it basically walks
        // through the period-delimited list of names, and ends up with the
        // final value.
        String[] fieldNameParts = fieldName.split("\\.");
        Object currentObject = null;
        Object priorObject = bo;
        for (int i = 0; i < fieldNameParts.length; i++) {
            String fieldNamePart = fieldNameParts[i];

            try {
                if (fieldNamePart.indexOf("]") > 0) {
                    currentObject = PropertyUtils.getIndexedProperty(priorObject, fieldNamePart);
                }
                else {
                    currentObject = PropertyUtils.getSimpleProperty(priorObject, fieldNamePart);
                }
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Caller does not have access to the property accessor method.", e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Property accessor method threw an exception.", e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("The accessor method requested for this property cannot be found.", e);
            }

            // materialize the proxy, if it is a proxy
            if (ProxyHelper.isProxy(currentObject)) {
                currentObject = ProxyHelper.getRealObject(currentObject);
            }

            // if a node or the leaf is null, then we're done, there's no need to
            // continue accessing null things
            if (currentObject == null) {
                return currentObject;
            }

            priorObject = currentObject;
        }
        return currentObject;
    }

    /**
     * Returns the equality of the two given objects, automatically handling when one or both of the objects is null.
     * 
     * @param obj1
     * @param obj2
     * 
     * @return true if both objects are null or both are equal
     */
    public static boolean nullSafeEquals(Object obj1, Object obj2) {
        if (obj1 != null && obj2 != null) {
            return obj1.equals(obj2);
        }
        else {
            return (obj1 == obj2);
        }
    }
    
    /**
     * 
     * This method safely creates a object from a class
     * Convenience method to create new object and throw a runtime exception if it cannot
     * 
     * @param boClass
     * 
     * @return a newInstance() of clazz
     */
    public static Object createNewObjectFromClass(Class clazz) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        }
        catch (InstantiationException ite) {
            LOG.info(ite);
            throw new RuntimeException("ObjectUtils had a problem creating a new object of " + clazz.getName(), ite);
        }
        catch (IllegalAccessException iae) {
            LOG.info(iae);
            throw new RuntimeException("ObjectUtils had a problem creating a new object of " + clazz.getName(), iae);
        }
        return obj;
    }

}
