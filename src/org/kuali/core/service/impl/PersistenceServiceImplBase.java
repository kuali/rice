/*
 * Copyright 2006 The Kuali Foundation.
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.core.exceptions.IntrospectionException;
import org.kuali.core.util.ObjectUtils;

public class PersistenceServiceImplBase extends PersistenceServiceStructureImplBase {

    public Object getFieldValue(Object persistableObject, String fieldName) {
        ClassDescriptor classDescriptor = getClassDescriptor(persistableObject.getClass());
        FieldDescriptor fieldDescriptor = classDescriptor.getFieldDescriptorByName(fieldName);

        // if field is not anonymous, get value from main object
        Object fieldValue = null;
        if (!fieldDescriptor.isAnonymous()) {
            if (PropertyUtils.isReadable(persistableObject, fieldName)) {
                fieldValue = ObjectUtils.getPropertyValue(persistableObject, fieldName);
            }
        }
        else {
            // find the value from one of the other reference objects
            Vector objectReferences = classDescriptor.getObjectReferenceDescriptors();
            for (Iterator iter2 = objectReferences.iterator(); iter2.hasNext();) {
                ObjectReferenceDescriptor checkDescriptor = (ObjectReferenceDescriptor) iter2.next();

                fieldValue = getReferenceFKValue(persistableObject, checkDescriptor, fieldName);
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    break;
                }
            }
        }

        return fieldValue;
    }


    /**
     * @param persistableObject
     * @param referenceDescriptor
     * @param fkName
     * @return
     */
    protected Object getReferenceFKValue(Object persistableObject, ObjectReferenceDescriptor chkRefCld, String fkName) {
        ClassDescriptor classDescriptor = getClassDescriptor(persistableObject.getClass());
        Object referenceObject = ObjectUtils.getPropertyValue(persistableObject, chkRefCld.getAttributeName());

        if (referenceObject == null) {
            return null;
        }

        FieldDescriptor[] refFkNames = chkRefCld.getForeignKeyFieldDescriptors(classDescriptor);
        ClassDescriptor refCld = getClassDescriptor(chkRefCld.getItemClass());
        FieldDescriptor[] refPkNames = refCld.getPkFields();


        Object fkValue = null;
        for (int i = 0; i < refFkNames.length; i++) {
            FieldDescriptor fkField = refFkNames[i];

            if (fkField.getAttributeName().equals(fkName)) {
                fkValue = ObjectUtils.getPropertyValue(referenceObject, refPkNames[i].getAttributeName());
                break;
            }
        }

        return fkValue;
    }


    /**
     * @see org.kuali.core.service.PersistenceMetadataService#getPrimaryKeyFields(java.lang.Object)
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject) {
        return getPrimaryKeyFieldValues(persistableObject, false);
    }

    /**
     * @see org.kuali.core.service.PersistenceMetadataService#getPrimaryKeyFields(java.lang.Object, boolean)
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject, boolean sortFieldNames) {
        if (persistableObject == null) {
            throw new IllegalArgumentException("invalid (null) persistableObject");
        }

        Map keyValueMap = null;
        if (sortFieldNames) {
            keyValueMap = new TreeMap();
        }
        else {
            keyValueMap = new HashMap();
        }

        String className = null;
        String fieldName = null;
        try {
            List fields = listPrimaryKeyFieldNames(persistableObject.getClass());
            for (Iterator i = fields.iterator(); i.hasNext();) {
                fieldName = (String) i.next();
                className = persistableObject.getClass().getName();
                Object fieldValue = PropertyUtils.getSimpleProperty(persistableObject, fieldName);

                keyValueMap.put(fieldName, fieldValue);
            }
        }
        catch (IllegalAccessException e) {
            throw new IntrospectionException("problem accessing property '" + className + "." + fieldName + "'", e);
        }
        catch (NoSuchMethodException e) {
            throw new IntrospectionException("unable to invoke getter for property '" + className + "." + fieldName + "'", e);
        }
        catch (InvocationTargetException e) {
            throw new IntrospectionException("problem invoking getter for property '" + className + "." + fieldName + "'", e);
        }

        return keyValueMap;
    }


}
