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
package org.kuali.rice.krad.data.provider;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.PersistenceOption;

/**
 * Defines persistence SPI for data providers.
 *
 * <p>
 * PersistenceProviders are responsible for creating, updating, querying, copying and deleting data objects.
 * DataObjectTypes the PersistenceProvider supports must be queried through {@link #handles(Class)} before interaction
 * with the PersistenceProvider.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PersistenceProvider extends Provider {

    /**
     * Saves the given data object, determining whether or not this is a new data object which is being created, or an
     * existing one which should be updated.
     *
     * <p>
     * Optional persistence options can be passed to indicate whether or not linking should be performed prior to
     * persistence and whether or not validation should be performed. By default, linking is performed as well as
     * validation.
     * </p>
     *
     * @param dataObject the data object to save
     * @param options the options to use when saving the data object
     * @param <T> the data object class type
     *
     * @return the saved data object, calling code should always use the reference the object returned from this method
     * for future operations after calling the save since it could have been updated
     *
     * @throws IllegalArgumentException if {@code dataObject} is not a valid data object
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    <T> T save(T dataObject, PersistenceOption... options);

    /**
     * Invoked to retrieve a data object instance by a single primary key field or id object.
     *
     * <p>In the case of a compound primary key consisting of multiple attributes on the data object, a CompoundKey can
     * be passed in order to encapsulate these into a single argument.
     * </p>
     *
     * @param type the type of the data object to find
     * @param id the id representing the primary key of the data object to find
     * @param <T> the data object class type
     *
     * @return the entity with the given primary key or null if none found
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type or {@code id} is not a valid
     * type for the data object's primary key or is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    <T> T find(Class<T> type, Object id);

    /**
     * Executes a query for the given data object.
     *
     * <p>
     * If the given QueryByCriteria is empty or null, then all data objects for the given type will be returned.
     * Depending on the given criteria and the implementation for the query execution, not all matching results may be
     * returned. The QueryResults will contain information on whether or not there are additional results which can be
     * used for paging and similar functionality.
     * </p>
     *
     * @param type the type of the data objects to query
     * @param queryByCriteria query object, can contain sorting and page request configuration
     * @param <T> the data object class type
     *
     * @return the results of the query, will never return null but may return empty results
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type or if {@code queryByCriteria}
     *  is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Retrieves all data objects for the given type.
     *
     * @param type the type of data objects to find
     * @param <T> the data object class type
     *
     * @return the results of the query, will never return null but may return empty results
     *
     * @throws java.lang.IllegalArgumentException if type is null.
     */
    <T> QueryResults<T> findAll(Class<T> type);

    /**
     * Deletes a given data object.
     *
     * @param dataObject the data object to delete
     *
     * @throws IllegalArgumentException if {@code dataObject} is not a valid data object
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    void delete(Object dataObject);

    /**
     * Deletes data objects based on the given criteria
     *
     * <p>If the given criteria is empty or null than an {@link java.lang.IllegalArgumentException} will be thrown.
     *   If the given type is null then an {@link java.lang.IllegalArgumentException} will be thrown.</p>
     *
     * @param type the type of data object
     * @param queryByCriteria criteria to filter by
     *
     * @throws IllegalArgumentException if the criteria or criteria predicate is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Deletes all data objects based on the given type.
     *
     * @param type the type of data objects to delete
     * @param <T> the data object class type
     *
     * @throws IllegalArgumentException if the class type is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    <T> void deleteAll(Class<T> type);

    /**
     * Returns a copy of the given data object instance.
     *
     * <p>
     * The method of copying is provider dependent, and will handle instances (including nested) using whatever measures
     * might be required to deal with the quirks of said provider (e.g. fetching lazy loaded relations).
     * </p>
     *
     * @param dataObject the data object to copy
     * @param <T> the type of the data object
     *
     * @return a copy of the given data object
     */
    <T> T copyInstance(T dataObject);

    /**
     * Indicates whether or not this provider handles persistence for the given data object type.
     *
     * <p>
     * Responsibility on with the caller to call prior to invocation of any other PersistenceProvider methods to ensure
     * the data objects of the right type are passed.
     * </p>
     *
     * @param type the data object type to check
     *
     * @return true if this provider can handle the given type, false otherwise
     */
    boolean handles(Class<?> type);

    /**
	 * Flush any outstanding changes within the current context for the provider pertaining to the given data object
	 * Class type.
	 * 
	 * <p>
	 * If an implementation of this interface does not support or require the concept of "flushing", this method can be
	 * ignored. However, when used, this must purge *all* cache items for the given types from all caches managed by
	 * this provider.
	 * </p>
	 * 
	 * @param type
	 *            the type of the data object for which to perform the flush. This should be used to identify the
	 *            context in which to perform the flush.
	 */
    void flush(Class<?> type);

}