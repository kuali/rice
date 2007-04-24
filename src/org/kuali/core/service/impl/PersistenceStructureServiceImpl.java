/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ClassNotPersistableException;
import org.kuali.core.exceptions.IntrospectionException;
import org.kuali.core.exceptions.ObjectNotABusinessObjectException;
import org.kuali.core.exceptions.ObjectNotABusinessObjectRuntimeException;
import org.kuali.core.exceptions.ReferenceAttributeDoesntExistException;
import org.kuali.core.exceptions.ReferenceAttributeNotAnOjbReferenceException;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ForeignKeyFieldsPopulationState;
import org.kuali.core.util.spring.Cached;

public class PersistenceStructureServiceImpl extends PersistenceServiceImplBase implements PersistenceStructureService {

    /**
     * 
     * special case when the attributeClass passed in doesnt match the
     * class of the reference-descriptor as defined in ojb-repository. Currently
     * the only case of this happening is ObjectCode vs. ObjectCodeCurrent.
     * 
     * NOTE:  This method makes no real sense and is a product of a hack introduced by KFS for 
     * an unknown reason.  If you find yourself using this map stop and go do something else.
     * 
     * @param from the class in the code
     * @param to the class in the repository
     */
    public static Map<Class, Class> referenceConversionMap = new HashMap<Class, Class>();
    
    /**
     * @see org.kuali.core.service.PersistenceService#isPersistable(java.lang.Class)
     */
    @Cached
    public boolean isPersistable(Class clazz) {
        boolean isPersistable = false;

        try {
            if (getClassDescriptor(clazz) != null) {
                isPersistable = true;
            }
        }
        catch (ClassNotPersistableException e) {
            isPersistable = false;
        }

        return isPersistable;
    }


    /**
     * @see org.kuali.core.service.PersistenceService#getPrimaryKeyFieldAnonymousMarking(java.lang.Class)
     */
    @Cached
    public Map getPrimaryKeyFieldAnonymousMarking(Class clazz) {
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);

        Map pkAnonyMap = new HashMap();
        FieldDescriptor keyDescriptors[] = classDescriptor.getPkFields();
        for (int i = 0; i < keyDescriptors.length; ++i) {
            FieldDescriptor keyDescriptor = keyDescriptors[i];
            pkAnonyMap.put(keyDescriptor.getAttributeName(), Boolean.valueOf(keyDescriptor.isAnonymous()));
        }

        return pkAnonyMap;
    }

    /**
     * @see org.kuali.core.service.PersistenceService#getPrimaryKeys(java.lang.Class)
     */
    @Cached
    public List getPrimaryKeys(Class clazz) {
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);

        List pkList = new ArrayList();
        FieldDescriptor keyDescriptors[] = classDescriptor.getPkFields();
        for (int i = 0; i < keyDescriptors.length; ++i) {
            FieldDescriptor keyDescriptor = keyDescriptors[i];
            pkList.add(keyDescriptor.getAttributeName());
        }

        return pkList;
    }

    /**
     * @see org.kuali.core.service.PersistenceMetadataExplorerService#listFieldNames(java.lang.Class)
     */
    @Cached
    public List listFieldNames(Class clazz) {
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);

        List fieldNames = new ArrayList();

        FieldDescriptor fieldDescriptors[] = classDescriptor.getFieldDescriptions();

        for (int i = 0; i < fieldDescriptors.length; ++i) {
            FieldDescriptor fieldDescriptor = fieldDescriptors[i];
            fieldNames.add(fieldDescriptor.getAttributeName());
        }

        return fieldNames;
    }


    /**
     * @see org.kuali.core.service.PersistenceMetadataService#clearPrimaryKeyFields(java.lang.Object)
     */
    public Object clearPrimaryKeyFields(Object persistableObject) {
        if (persistableObject == null) {
            throw new IllegalArgumentException("invalid (null) persistableObject");
        }

        String className = null;
        String fieldName = null;
        try {
            className = persistableObject.getClass().getName();
            List fields = listPrimaryKeyFieldNames(persistableObject.getClass());
            for (Iterator i = fields.iterator(); i.hasNext();) {
                fieldName = (String) i.next();

                PropertyUtils.setProperty(persistableObject, fieldName, null);
            }

            if (persistableObject instanceof PersistableBusinessObject) {
                ((PersistableBusinessObject) persistableObject).setObjectId(null);
            }
        }
        catch (NoSuchMethodException e) {
            throw new IntrospectionException("no setter for property '" + className + "." + fieldName + "'", e);
        }
        catch (IllegalAccessException e) {
            throw new IntrospectionException("problem accessing property '" + className + "." + fieldName + "'", e);
        }
        catch (InvocationTargetException e) {
            throw new IntrospectionException("problem invoking getter for property '" + className + "." + fieldName + "'", e);
        }

        return persistableObject;
    }


    /**
     * @see org.kuali.core.service.PersistenceMetadataExplorerService#listPersistableSubclasses(java.lang.Class)
     */
    @Cached
    public List listPersistableSubclasses(Class superclazz) {
        if (superclazz == null) {
            throw new IllegalArgumentException("invalid (null) uberclass");
        }

        Map allDescriptors = getDescriptorRepository().getDescriptorTable();
        List persistableSubclasses = new ArrayList();
        for (Iterator i = allDescriptors.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            Class persistableClass = ((ClassDescriptor) e.getValue()).getClassOfObject();
            if (!superclazz.equals(persistableClass) && superclazz.isAssignableFrom(persistableClass)) {
                persistableSubclasses.add(persistableClass);
            }
        }
        return persistableSubclasses;
    }


    /**
     * @see org.kuali.core.service.PersistenceService#getRelationshipMetadata(java.lang.Class, java.lang.String)
     */
    @Cached
    public Map<String,BusinessObjectRelationship> getRelationshipMetadata(Class persistableClass, String attributeName, String attributePrefix ) {
        if (persistableClass == null) {
            throw new IllegalArgumentException("invalid (null) persistableClass");
        }
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }

        Map<String,BusinessObjectRelationship> relationships = new HashMap<String,BusinessObjectRelationship>();
        
        // find table and column for given class and attribute
        ClassDescriptor classDescriptor = getClassDescriptor(persistableClass);
        Vector<ObjectReferenceDescriptor> references = classDescriptor.getObjectReferenceDescriptors();
        for ( ObjectReferenceDescriptor objRef : references ) {
            Vector fks = objRef.getForeignKeyFields();
            if ( fks.contains( attributeName ) || objRef.getAttributeName().equals( attributeName ) ) {
                FieldDescriptor[] pkFields = getClassDescriptor(objRef.getItemClass()).getPkFields();
                Map<String,String> fkToPkRefs = getForeignKeysForReference( persistableClass, objRef.getAttributeName() );
                BusinessObjectRelationship rel = new BusinessObjectRelationship( persistableClass, objRef.getAttributeName(), objRef.getItemClass() );
                for ( Map.Entry<String,String> ref : fkToPkRefs.entrySet() ) {
                    if ( StringUtils.isBlank( attributePrefix ) ) {
                        rel.getParentToChildReferences().put( ref.getKey(), ref.getValue() );
                    } else {
                        rel.getParentToChildReferences().put( attributePrefix + "." + ref.getKey(), ref.getValue() );
                    }
                }
                relationships.put( objRef.getAttributeName(), rel );
            }
        }
        return relationships;
    }

    @Cached
    public Map<String,BusinessObjectRelationship> getRelationshipMetadata(Class persistableClass, String attributeName ) {
        return getRelationshipMetadata( persistableClass, attributeName, null );
    }

    /**
     * @see org.kuali.core.service.PersistenceService#getForeignKeyFieldName(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Cached
    public String getForeignKeyFieldName(Class persistableObjectClass, String attributeName, String pkName) {
        String fkName = null;

        ClassDescriptor classDescriptor = getClassDescriptor(persistableObjectClass);
        ObjectReferenceDescriptor objectReferenceDescriptor = classDescriptor.getObjectReferenceDescriptorByName(attributeName);
        if (objectReferenceDescriptor == null) {
            throw new RuntimeException("Attribute name " + attributeName + " is not a valid reference to class " + persistableObjectClass.getName());
        }
        ClassDescriptor referenceDescriptor = this.getClassDescriptor(objectReferenceDescriptor.getItemClass());

        FieldDescriptor[] fkFields = objectReferenceDescriptor.getForeignKeyFieldDescriptors(classDescriptor);
        FieldDescriptor[] pkFields = referenceDescriptor.getPkFields();
        for (int i = 0; i < pkFields.length; i++) {
            FieldDescriptor pkField = pkFields[i];
            if (pkField.getAttributeName().equals(pkName)) {
                fkName = fkFields[i].getAttributeName();
            }
        }

        return fkName;
    }


    /**
     * @see org.kuali.core.service.PersistenceService#getReferencesForForeignKey(java.lang.Class, java.lang.String)
     */
    @Cached
    public Map getReferencesForForeignKey(Class persistableObjectClass, String attributeName) {
        Map referenceClasses = new HashMap();

        if ( PersistableBusinessObject.class.isAssignableFrom( persistableObjectClass ) ) {
            ClassDescriptor classDescriptor = getClassDescriptor(persistableObjectClass);
            Vector objectReferences = classDescriptor.getObjectReferenceDescriptors();
            for (Iterator iter = objectReferences.iterator(); iter.hasNext();) {
                ObjectReferenceDescriptor referenceDescriptor = (ObjectReferenceDescriptor) iter.next();
    
                /*
                 * iterate through the fk keys for the reference object and if matches the attributeName add the class as a reference
                 */
                FieldDescriptor[] refFkNames = referenceDescriptor.getForeignKeyFieldDescriptors(classDescriptor);
                for (int i = 0; i < refFkNames.length; i++) {
                    FieldDescriptor fkField = refFkNames[i];
                    if (fkField.getAttributeName().equals(attributeName)) {
                        referenceClasses.put(referenceDescriptor.getAttributeName(), referenceDescriptor.getItemClass());
                    }
    
                }
            }
        }

        return referenceClasses;
    }

    /**
     * @see org.kuali.core.service.PersistenceService#getForeignKeysForReference(java.lang.Class, java.lang.String) The Map
     *      structure is: Key(String fkFieldName) => Value(String pkFieldName) NOTE that this implementation depends on the ordering
     *      of foreign-key elements in the ojb-repository matching the ordering of primary-key declarations of the class on the
     *      other side of the relationship. This is done because: 1. The current version of OJB requires you to declare all of these
     *      things in the correct (and matching) order in the ojb-repository file for it to work at all. 2. There is no other way to
     *      match a given foreign-key reference to its corresponding primary-key on the opposing side of the relationship. Yes, this
     *      is a crummy way to do it, but OJB doesnt provide explicit matches of foreign-keys to primary keys, and always assumes
     *      that foreign-keys map to primary keys on the other object, and never to a set of candidate keys, or any other column.
     */
    @Cached
    public Map getForeignKeysForReference(Class clazz, String attributeName) {

        // yelp if nulls were passed in
        if (clazz == null) {
            throw new IllegalArgumentException("The Class passed in for the clazz argument was null.");
        }
        if (attributeName == null) {
            throw new IllegalArgumentException("The String passed in for the attributeName argument was null.");
        }

        Map fkMap = new HashMap();
        PropertyDescriptor propertyDescriptor = null;

        // make an instance of the class passed
        Object classInstance;
        try {
            classInstance = clazz.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // make sure the attribute exists at all, throw exception if not
        try {
            propertyDescriptor = PropertyUtils.getPropertyDescriptor(classInstance, attributeName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (propertyDescriptor == null) {
            throw new ReferenceAttributeDoesntExistException("Requested attribute: '" + attributeName + "' does not exist " + "on class: '" + clazz.getName() + "'. GFK");
        }

        // get the class of the attribute name
        Class attributeClass = propertyDescriptor.getPropertyType();

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!PersistableBusinessObject.class.isAssignableFrom(attributeClass)) {
            throw new ObjectNotABusinessObjectRuntimeException("Attribute requested (" + attributeName + ") is of class: " + "'" + attributeClass.getName() + "' and is not a " + "descendent of BusinessObject.  Only descendents of BusinessObject " + "can be used.");
        }

        // make sure the attribute designated is listed as a reference-descriptor
        // on the clazz specified, otherwise throw an exception (OJB); UniversalUser objects
        // will be excluded from this
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);
        if (!UniversalUser.class.equals(attributeClass)) {
            ObjectReferenceDescriptor referenceDescriptor = classDescriptor.getObjectReferenceDescriptorByName(attributeName);
            if (referenceDescriptor == null) {
                throw new ReferenceAttributeNotAnOjbReferenceException("Attribute requested (" + attributeName + ") is not listed " + "in OJB as a reference-descriptor for class: '" + clazz.getName() + "'");
            }

            // special case when the attributeClass passed in doesnt match the
            // class of the reference-descriptor as defined in ojb-repository. Currently
            // the only case of this happening is ObjectCode vs. ObjectCodeCurrent.
            if (!attributeClass.equals(referenceDescriptor.getItemClass())) {

                if (referenceConversionMap.containsKey(attributeClass)) {
                    attributeClass = referenceConversionMap.get(attributeClass);
                } else {
                    throw new RuntimeException("The Class of the Java member [" + attributeClass.getName() + "] '" + attributeName + "' does not match the class of the " + "reference-descriptor [" + referenceDescriptor.getItemClass().getName() + "]. " + "This is an unhandled special case for which special code needs to be written " + "in this class.");
                }
            }

            // get the list of the foreign-keys for this reference-descriptor (OJB)
            Vector fkFields = referenceDescriptor.getForeignKeyFields();
            Iterator fkIterator = fkFields.iterator();

            // get the list of the corresponding pk fields on the other side of the relationship
            List pkFields = getPrimaryKeys(attributeClass);
            Iterator pkIterator = pkFields.iterator();

            // make sure the size of the pkIterator is the same as the
            // size of the fkIterator, otherwise this whole thing is borked
            if (pkFields.size() != fkFields.size()) {
                throw new RuntimeException("KualiPersistenceStructureService Error: The number of " + "foreign keys doesnt match the number of primary keys.  This may be a " + "result of misconfigured OJB-repository files.");
            }

            // walk through the list of the foreign keys, get their types
            while (fkIterator.hasNext()) {

                // if there is a next FK but not a next PK, then we've got a big problem,
                // and cannot continue
                if (!pkIterator.hasNext()) {
                    throw new RuntimeException("The number of foriegn keys dont match the number of primary " + "keys for the reference '" + attributeName + "', on BO of type '" + clazz.getName() + "'.  " + "This should never happen under normal circumstances, as it means that the OJB repository " + "files are misconfigured.");
                }

                // get the field name of the fk & pk field
                String fkFieldName = (String) fkIterator.next();
                String pkFieldName = (String) pkIterator.next();

                // add the fieldName and fieldType to the map
                fkMap.put(fkFieldName, pkFieldName);

            }

        }

        return fkMap;
    }

    @Cached
    public Map<String, String> getInverseForeignKeysForCollection(Class boClass, String collectionName) {
        // yelp if nulls were passed in
        if (boClass == null) {
            throw new IllegalArgumentException("The Class passed in for the boClass argument was null.");
        }
        if (collectionName == null) {
            throw new IllegalArgumentException("The String passed in for the attributeName argument was null.");
        }

        Map fkMap = new HashMap();
        PropertyDescriptor propertyDescriptor = null;

        // make an instance of the class passed
        Object classInstance;
        try {
            classInstance = boClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // make sure the attribute exists at all, throw exception if not
        try {
            propertyDescriptor = PropertyUtils.getPropertyDescriptor(classInstance, collectionName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (propertyDescriptor == null) {
            throw new ReferenceAttributeDoesntExistException("Requested attribute: '" + collectionName + "' does not exist " + "on class: '" + boClass.getName() + "'. GFK");
        }

        // get the class of the attribute name
        Class attributeClass = propertyDescriptor.getPropertyType();

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!Collection.class.isAssignableFrom(attributeClass)) {
            throw new ObjectNotABusinessObjectRuntimeException("Attribute requested (" + collectionName + ") is of class: " + "'" + attributeClass.getName() + "' and is not a " + "descendent of Collection");
        }

        // make sure the collection designated is listed as a collection-descriptor
        // on the boClass specified, otherwise throw an exception
        ClassDescriptor classDescriptor = getClassDescriptor(boClass);
        CollectionDescriptor collectionDescriptor = classDescriptor.getCollectionDescriptorByName(collectionName);
        
        // in collections, the number of keys is equal to the number of keys in the parent class (the class with the collection).
        // Each of the primary keys on the parent object will be mapped to a field in the element object.
        
        List parentForeignKeys = getPrimaryKeys(boClass);
        Vector childPrimaryKeys = collectionDescriptor.getForeignKeyFields();
        
        if (parentForeignKeys.size() != childPrimaryKeys.size()) {
            throw new RuntimeException("The number of keys in the class descriptor and the inverse foreign key mapping for the collection descriptors do not match.");
        }
        Map<String, String> fkToPkMap = new HashMap<String, String>();
        Iterator pFKIter = parentForeignKeys.iterator();
        Iterator cPKIterator = childPrimaryKeys.iterator();
        
        while (pFKIter.hasNext()) {
            String parentForeignKey = (String) pFKIter.next();
            String childPrimaryKey = (String) cPKIterator.next();
            
            fkToPkMap.put(parentForeignKey, childPrimaryKey);
        }
        return fkToPkMap;
    }


    /**
     * @see org.kuali.core.service.PersistenceStructureService#getAttributeBoSubclass(java.lang.Class, java.lang.String)
     */
    @Cached
    public Class getAttributeBoSubclass(Class clazz, String attributeName) throws ObjectNotABusinessObjectException, IllegalAccessException, InstantiationException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        PersistableBusinessObject boInstance;

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!PersistableBusinessObject.class.isAssignableFrom(clazz)) {
            throw new ObjectNotABusinessObjectException("Class specified is of class: " + "'" + clazz.getName() + "' and is not a " + "descendent of BusinessObject.  Only descendents of BusinessObject " + "can be used.");
        }

        // attempt to get an instance of the class passed in
        boInstance = (PersistableBusinessObject) clazz.newInstance();

        return getAttributeBoSubclass(boInstance, attributeName);
    }

    /**
     * @see org.kuali.core.service.PersistenceStructureService#getAttributeBoSubclass(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    @Cached
    public Class getAttributeBoSubclass(PersistableBusinessObject bo, String attributeName) throws ObjectNotABusinessObjectException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // make sure the attribute exists at all, throw exception if not
        PropertyDescriptor propertyDescriptor;
        propertyDescriptor = PropertyUtils.getPropertyDescriptor(bo, attributeName);
        if (propertyDescriptor == null) {
            throw new ReferenceAttributeDoesntExistException("Requested attribute: '" + attributeName + "' does not exist " + "on class: '" + bo.getClass().getName() + "'.");
        }

        // get the class of the attribute name
        Class referenceClass = propertyDescriptor.getPropertyType();

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!PersistableBusinessObject.class.isAssignableFrom(referenceClass)) {
            throw new ObjectNotABusinessObjectException("Attribute requested (" + attributeName + ") is of class: " + "'" + referenceClass.getName() + "' and is not a " + "descendent of BusinessObject.  Only descendents of BusinessObject " + "can be used.");
        }

        return referenceClass;

    }

    /**
     * @see org.kuali.core.service.PersistenceService#getNestedForeignKeyMap(java.lang.Class)
     */
    @Cached
    public Map getNestedForeignKeyMap(Class persistableObjectClass) {
        Map fkMap = new HashMap();

        ClassDescriptor classDescriptor = getClassDescriptor(persistableObjectClass);
        Vector objectReferences = classDescriptor.getObjectReferenceDescriptors();
        for (Iterator iter = objectReferences.iterator(); iter.hasNext();) {
            ObjectReferenceDescriptor objectReferenceDescriptor = (ObjectReferenceDescriptor) iter.next();
            ClassDescriptor referenceDescriptor = this.getClassDescriptor(objectReferenceDescriptor.getItemClass());

            FieldDescriptor[] fkFields = objectReferenceDescriptor.getForeignKeyFieldDescriptors(classDescriptor);
            FieldDescriptor[] pkFields = referenceDescriptor.getPkFields();
            for (int i = 0; i < pkFields.length; i++) {
                FieldDescriptor pkField = pkFields[i];
                fkMap.put(objectReferenceDescriptor.getAttributeName() + "." + pkField.getAttributeName(), fkFields[i].getAttributeName());
            }
        }

        return fkMap;
    }

    /**
     * @see org.kuali.core.service.PersistenceMetadataService#hasPrimaryKeyFieldValues(java.lang.Object)
     */
    public boolean hasPrimaryKeyFieldValues(Object persistableObject) {
        Map keyFields = getPrimaryKeyFieldValues(persistableObject);

        boolean emptyField = false;
        for (Iterator i = keyFields.entrySet().iterator(); !emptyField && i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            Object fieldValue = e.getValue();
            if (fieldValue == null) {
                emptyField = true;
            }
            else if (fieldValue instanceof String) {
                if (StringUtils.isEmpty((String) fieldValue)) {
                    emptyField = true;
                }
                else {
                    emptyField = false;
                }
            }
        }

        return !emptyField;
    }

    /**
     * @see org.kuali.core.service.PersistenceService#getForeignKeyFieldsPopulationState(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(PersistableBusinessObject bo, String referenceName) {

        boolean allFieldsPopulated = true;
        boolean anyFieldsPopulated = false;
        List<String> unpopulatedFields = new ArrayList<String>();

        // yelp if nulls were passed in
        if (bo == null) {
            throw new IllegalArgumentException("The Class passed in for the BusinessObject argument was null.");
        }
        if (StringUtils.isBlank(referenceName)) {
            throw new IllegalArgumentException("The String passed in for the referenceName argument was null or empty.");
        }

        PropertyDescriptor propertyDescriptor = null;

        // make sure the attribute exists at all, throw exception if not
        try {
            propertyDescriptor = PropertyUtils.getPropertyDescriptor(bo, referenceName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (propertyDescriptor == null) {
            throw new ReferenceAttributeDoesntExistException("Requested attribute: '" + referenceName + "' does not exist " + "on class: '" + bo.getClass().getName() + "'.");
        }

        // get the class of the attribute name
        Class referenceClass = propertyDescriptor.getPropertyType();

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!PersistableBusinessObject.class.isAssignableFrom(referenceClass)) {
            throw new ObjectNotABusinessObjectRuntimeException("Attribute requested (" + referenceName + ") is of class: " + "'" + referenceClass.getName() + "' and is not a " + "descendent of BusinessObject.  Only descendents of BusinessObject " + "can be used.");
        }

        // make sure the attribute designated is listed as a reference-descriptor
        // on the clazz specified, otherwise throw an exception (OJB); UniversalUser objects
        // will be excluded from this
        ClassDescriptor classDescriptor = getClassDescriptor(bo.getClass());
        if (!UniversalUser.class.equals(referenceClass)) {
            ObjectReferenceDescriptor referenceDescriptor = classDescriptor.getObjectReferenceDescriptorByName(referenceName);
            if (referenceDescriptor == null) {
                throw new ReferenceAttributeNotAnOjbReferenceException("Attribute requested (" + referenceName + ") is not listed " + "in OJB as a reference-descriptor for class: '" + bo.getClass().getName() + "'");
            }

            // get the list of the foreign-keys for this reference-descriptor (OJB)
            Vector fkFields = referenceDescriptor.getForeignKeyFields();
            Iterator fkIterator = fkFields.iterator();

            // walk through the list of the foreign keys, get their types
            while (fkIterator.hasNext()) {

                // get the field name of the fk & pk field
                String fkFieldName = (String) fkIterator.next();

                // get the value for the fk field
                Object fkFieldValue = null;
                try {
                    fkFieldValue = PropertyUtils.getSimpleProperty(bo, fkFieldName);
                }

                // abort if the value is not retrievable
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // test the value
                if (fkFieldValue == null) {
                    allFieldsPopulated = false;
                    unpopulatedFields.add(fkFieldName);
                }
                else if (fkFieldValue instanceof String) {
                    if (StringUtils.isBlank((String) fkFieldValue)) {
                        allFieldsPopulated = false;
                        unpopulatedFields.add(fkFieldName);
                    }
                    else {
                        anyFieldsPopulated = true;
                    }
                }
                else {
                    anyFieldsPopulated = true;
                }
            }

        }

        // sanity check. if the flag for all fields populated is set, then
        // there should be nothing in the unpopulatedFields list
        if (allFieldsPopulated) {
            if (!unpopulatedFields.isEmpty()) {
                throw new RuntimeException("The flag is set that indicates all fields are populated, but there " + "are fields present in the unpopulatedFields list.  This should never happen, and indicates " + "that the logic in this method is broken.");
            }
        }

        return new ForeignKeyFieldsPopulationState(allFieldsPopulated, anyFieldsPopulated, unpopulatedFields);

    }

    /**
     * @see org.kuali.core.service.PersistenceStructureService#listReferenceObjectFieldNames(java.lang.Class)
     */
    @Cached
    public Map<String, Class> listReferenceObjectFields(Class boClass) {

        // validate parameter
        if (boClass == null) {
            throw new IllegalArgumentException("Class specified in the parameter was null.");
        }
        if (!PersistableBusinessObject.class.isAssignableFrom(boClass)) {
            throw new IllegalArgumentException("Class specified [" + boClass.getName() + "] must be a class that " + "inherits from BusinessObject.");
        }

        ClassDescriptor classDescriptor = getClassDescriptor(boClass);
        Collection<ObjectReferenceDescriptor> referenceDescriptors = classDescriptor.getObjectReferenceDescriptors(true);

        Map<String, Class> references = new HashMap();
        for (ObjectReferenceDescriptor referenceDescriptor : referenceDescriptors) {
            references.put(referenceDescriptor.getAttributeName(), referenceDescriptor.getItemClass());
        }
        return references;
    }

    @Cached
    public Map<String, Class> listCollectionObjectTypes(Class boClass) {

        if (boClass == null) {
            throw new IllegalArgumentException("Class specified in the parameter was null.");
        }

        Map<String, Class> references = new HashMap();
        ClassDescriptor classDescriptor = null;

        try {
            classDescriptor = getClassDescriptor(boClass);
        }
        catch (ClassNotPersistableException cnpe) {
            return references;
        }

        Collection<CollectionDescriptor> collectionDescriptors = classDescriptor.getCollectionDescriptors(true);

        for (CollectionDescriptor collectionDescriptor : collectionDescriptors) {
            references.put(collectionDescriptor.getAttributeName(), collectionDescriptor.getItemClass());
        }
        return references;
    }

    public Map<String, Class> listCollectionObjectTypes(PersistableBusinessObject bo) {

        // validate parameter
        if (bo == null) {
            throw new IllegalArgumentException("BO specified in the parameter was null.");
        }
        if (!(bo instanceof PersistableBusinessObject)) {
            throw new IllegalArgumentException("BO specified [" + bo.getClass().getName() + "] must be a class that " + "inherits from BusinessObject.");
        }

        return listCollectionObjectTypes(bo.getClass());
    }

    /**
     * @see org.kuali.core.service.PersistenceStructureService#listReferenceObjectFieldNames(org.kuali.core.bo.BusinessObject)
     */
    public Map<String, Class> listReferenceObjectFields(PersistableBusinessObject bo) {

        // validate parameter
        if (bo == null) {
            throw new IllegalArgumentException("BO specified in the parameter was null.");
        }
        if (!(bo instanceof PersistableBusinessObject)) {
            throw new IllegalArgumentException("BO specified [" + bo.getClass().getName() + "] must be a class that " + "inherits from BusinessObject.");
        }

        return listReferenceObjectFields(bo.getClass());
    }


    @Cached
    public boolean isReferenceUpdatable(Class boClass, String referenceName) {
        ClassDescriptor classDescriptor = getClassDescriptor(boClass);
        ObjectReferenceDescriptor refDesc = classDescriptor.getObjectReferenceDescriptorByName(referenceName);
        return refDesc.getCascadingStore() == ObjectReferenceDescriptor.CASCADE_OBJECT;
    }
    
    @Cached
    public boolean isCollectionUpdatable(Class boClass, String collectionName) {
        ClassDescriptor cd = getClassDescriptor(boClass);
        CollectionDescriptor collDesc = cd.getCollectionDescriptorByName(collectionName);
        return collDesc.getCascadingStore() == ObjectReferenceDescriptor.CASCADE_OBJECT;
    }


    @Cached
    public boolean hasCollection(Class boClass, String collectionName) {
        ClassDescriptor cd = getClassDescriptor(boClass);
        return cd.getCollectionDescriptorByName(collectionName) != null;
    }


    @Cached
    public boolean hasReference(Class boClass, String referenceName) {
        ClassDescriptor cd = getClassDescriptor(boClass);
        return cd.getObjectReferenceDescriptorByName(referenceName) != null;
    }
}
