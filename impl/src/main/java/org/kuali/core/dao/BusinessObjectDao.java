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
package org.kuali.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;

/**
 * This is the generic data access interface for business objects. This should be used for unit testing purposes only.
 * 
 * 
 */
public interface BusinessObjectDao {
    /**
     * Saves any object that implements the BusinessObject interface.
     * 
     * @param bo
     */
    public void save(PersistableBusinessObject bo);

    /**
     * Saves a List of BusinessObjects.
     * 
     * @param businessObjects
     */
    public void save(List businessObjects);

    /**
     * Retrieves an object instance identified bys it primary keys and values. This can be done by constructing a map where the key
     * to the map entry is the primary key attribute and the value of the entry being the primary key value. For composite keys,
     * pass in each primaryKey attribute and its value as a map entry.
     * 
     * @param clazz
     * @param primaryKeys
     * @return
     */
    public PersistableBusinessObject findByPrimaryKey(Class clazz, Map primaryKeys);

    /**
     * Retrieves an object instance identified by the class of the given object and the object's primary key values.
     * 
     * @param object
     * @return
     */
    public PersistableBusinessObject retrieve(PersistableBusinessObject object);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     * 
     * @param clazz
     * @return
     */
    public Collection findAll(Class clazz);
    
    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     * 
     * Adds criteria on active column to return only active records. Assumes there exist a mapping for PropertyConstants.Active
     * 
     * @param clazz
     * @return
     */
    public Collection findAllActive(Class clazz);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type. Orders the results by the given field.
     * 
     * @param clazz
     * @return
     */
    public Collection findAllOrderBy(Class clazz, String sortField, boolean sortAscending);
    
    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type. Orders the results by the given field.
     * 
     * Adds criteria on active column to return only active records. Assumes there exist a mapping for PropertyConstants.Active
     * @param clazz
     * @return
     */
    public Collection findAllActiveOrderBy(Class clazz, String sortField, boolean sortAscending);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name-expected value.
     * 
     * @param clazz
     * @param fieldValues
     * @return
     */
    public Collection findMatching(Class clazz, Map fieldValues);
    
    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name-expected value.
     * 
     * Adds criteria on active column to return only active records. Assumes there exist a mapping for PropertyConstants.Active
     * 
     * @param clazz
     * @param fieldValues
     * @return
     */
    public Collection findMatchingActive(Class clazz, Map fieldValues);

    /**
     * @param clazz
     * @param fieldValues
     * @return count of BusinessObjects of the given class whose fields match the values in the given Map.
     */
    public int countMatching(Class clazz, Map fieldValues);

    
    /**
     * 
     * This method returns the number of matching result given the positive criterias and
     * negative criterias. The negative criterias are the ones that will be set to 
     * "notEqualTo" or "notIn"
     * 
     * @param clazz
     * @param positiveFieldValues  Map of fields and values for positive criteria
     * @param negativeFieldValues  Map of fields and values for negative criteria
     * @return
     */
    public int countMatching(Class clazz, Map positiveFieldValues, Map negativeFieldValues);
    
    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name-expected value. Orders the results by the given field.
     * 
     * @param clazz
     * @param fieldValues
     * @return
     */
    public Collection findMatchingOrderBy(Class clazz, Map fieldValues, String sortField, boolean sortAscending);

    /**
     * Deletes a business object from the database.
     * 
     * @param bo
     */
    public void delete(PersistableBusinessObject bo);

    /**
     * Deletes each business object in the given List from the database.
     * 
     * @param boList
     */
    public void delete(List<PersistableBusinessObject> boList);

    /**
     * Deletes the business objects matching the given fieldValues
     * 
     * @param clazz
     * @param fieldValues
     */
    public void deleteMatching(Class clazz, Map fieldValues);
}