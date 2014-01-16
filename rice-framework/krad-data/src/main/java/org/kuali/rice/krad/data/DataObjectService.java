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
package org.kuali.rice.krad.data;

import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.springframework.dao.DataAccessException;

/**
 * KRAD Data Layer API containing basic CRUD operations and access to a metadata repository.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectService {

    /**
     * Invoked to retrieve a data object instance by a single primary key field or id object. In the
     * case of a compound primary key consisting of multiple attributes on the data object, a
     * CompoundKey can be passed in order to encapsulate these into a single argument.
     *
     * @param type the type of the data object to find
     * @param id the id representing the primary key of the data object to find
     * @param <T> the data object class type
     *
     * @return the entity with the given primary key or null if none found
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type or {@code id} is not a valid
     * type for the data object's primary key or is null
     * @throws DataAccessException if data access fails
     */
    <T> T find(Class<T> type, Object id);

    /**
     * Executes a query for the given data object. If the given QueryByCriteria is empty or null, then
     * all data objects for the given type will be returned. Depending on the given criteria and the
     * implementation for the query execution, not all matching results may be returned. The QueryResults
     * will contain information on whether or not there are additional results which can be used for paging
     * and similar functionality.
     *
     * @param type the type of the data objects to query
     * @param queryByCriteria query object, can contain sorting and page request configuration
     * @param <T> the data object class type
     *
     * @return the results of the query, will never return null but may return empty results
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type
     * @throws DataAccessException if data access fails
     */
    <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Executes a query for the given data object. If the given QueryByCriteria is empty or null, then
     * all data objects for the given type will be returned. Depending on the given criteria and the
     * implementation for the query execution, not all matching results may be returned. The QueryResults
     * will contain information on whether or not there are additional results which can be used for paging
     * and similar functionality.
     *
     * @param type the type of the data objects to query
     * @param queryByCriteria query object, can contain sorting and page request configuration
     * @param lookupCustomizer predicate transformation object
     * @param <T> the data object class type
     *
     * @return the results of the query, will never return null but may return empty results
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type
     * @throws DataAccessException if data access fails
     */
    <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria, LookupCustomizer<T> lookupCustomizer);

    /**
     * Deletes a given data object.
     *
     * @param dataObject the data object to delete
     *
     * @throws IllegalArgumentException if {@code dataObject} is not a valid data object
     * @throws DataAccessException if data access fails
     */
    void delete(Object dataObject);

    /**
     * Deletes data objects
     *
     * @param type the type of the data objects to query
     * @param queryByCriteria query object
     * @throws DataAccessException if data access fails
     */
    <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Saves the given data object, determining whether or not this is a new data object which is being created, or an
     * existing one which should be updated.
     *
     * <p>Optional persistence options can be passed to indicate whether or not linking should be performed prior to
     * persistence. By default, linking is performed.</p>
     *
     * @param dataObject the data object to save
     * @param options the options to use when saving the data object
     * @param <T> the data object class type
     *
     * @return the saved data object, calling code should always use the reference the object returned from this method
     * for future operations after calling the save since it could have been updated
     *
     * @throws IllegalArgumentException if {@code dataObject} is not a valid data object
     * @throws DataAccessException if data access fails
     */
	<T> T save(T dataObject, PersistenceOption... options);

    /**
     * Flushes any outstanding work to the backend data store.
     *
     * <p>Depending on the backend persistence implementation for the given type, this method may or may not do
     * anything.</p>
     *
     * @param type the type of the data object for which to perform the flush. This is primarily used to identify the
     * context in which to perform the flush.
     */
    void flush(Class<?> type);


    /**
     * Returns the MetadataRepository which provides access to all data object metadata known to the system.
     *
     * @return the MetadataRepository
     */
    MetadataRepository getMetadataRepository();

    /**
     * Wraps the given data object in an accessor which provides numerous utility and helper methods related to
     * accessing data and attributes on the data object.
     *
     * @param dataObject the data object to wrap, must be non-null
     * @param <T> the type of the data object
     * @return an accessor which wraps the given data object and it's associated metadata and provides utility and
     *         methods useful when accessing data and attributes on the data object
     * @throws IllegalArgumentException if the given data object is null or an invalid data object type
     */
    <T> DataObjectWrapper<T> wrap(T dataObject);

    /**
     * Returns whether the DataObjectService supports the given type, where
     * "supports" means that there is at least one PersistenceProvider that handles the given type.
     *
     * @param type the data object type
     * @return whether the DataObjectService supports the given type
     */
    <T> boolean supports(Class<T> type);

}