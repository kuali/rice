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

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * The primary API for interacting with the data layer.
 *
 * <p>Contains basic data access and query operations for supported data objects. Also provides access to the
 * {@link MetadataRepository} which contains information about the structure and relationships between data objects.</p>
 *
 * <p>This service supports the ability to create a {@link DataObjectWrapper} for a given data object. This
 * wrapper allows for accessing and manipulating properties within the data object as well as performing various
 * metadata-aware operations on the data object.</p>
 *
 * <p>This service is meant to act as a facade to specific persistence and query-capable solutions and data stores.
 * Implementations of this service may provide transactional capabilities where it makes sense to do so (and depending
 * on the transactional support of the backend persistence technology). The documentation for the specific
 * implementation of DataObjectService that is being used should be consulted for specifics on transaction
 * semantics.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectService {

    /**
     * Invoked to retrieve a data object instance by a single primary key field or id object. In the
     * case of a compound primary key consisting of multiple attributes on the data object, a
     * {@link CompoundKey} can be passed in order to encapsulate these into a single argument.
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
     *
     * @see CompoundKey
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
     * @throws IllegalArgumentException if {@code type} does not denote a data object type, also if the
     *  {@code queryByCriteria} is null or empty.
     * @throws DataAccessException if data access fails
     */
    <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Executes a query for the given data object returning all data objects for the given type.
     *
     * @param type tye type of data objects to query
     * @param <T> the data object class type
     *
     * @return the results of the query, will never return null but may return empty results.
     *
     * @throws IllegalArgumentException if {@code type} is null.
     * @throws DataAccessException if data access fails.
     */
    <T> QueryResults<T> findAll(Class<T> type);

    /**
     * Executes a query for the data object matching the given queryByCriteria and expecting a single unique result to
     * be returned. If no results match the given criteria, then null will be returned. If the given criteria matches
     * more than one result, then an {@link IncorrectResultSizeDataAccessException} will be
     * thrown.
     *
     * @param type the type of the data object to query
     * @param queryByCriteria query object defining the criteria for the query
     * @param <T> the data object class type
     *
     * @return the single result of the query, or null if no objects were matched
     *
     * @throws IllegalArgumentException if {@code type} does not denote a data object type
     * @throws IncorrectResultSizeDataAccessException if more than one object matched the given criteria
     */
    <T> T findUnique(Class<T> type, QueryByCriteria queryByCriteria);

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
     * Deletes records for the given type and matching the given criteria.  If the given type is null then an
     *  IllegalArgumentException will be thrown.  If the given criteria is null or empty an IllegalArgumentException
     *  is also thrown to help prevent table truncations.
     *
     * @param type the type of data objects to delete
     * @param queryByCriteria query object
     *
     * @throws IllegalArgumentException if {@code type} is null or if the {@code QueryByCriteria} is null or empty
     * @throws DataAccessException if data access fails
     */
    <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria);

    /**
     * Removes all records for the given data object type
     *
     * @param type the type of data objects
     * @param <T> the data object class type.
     *
     * @throws IllegalArgumentException if {@code type} is null
     * @throws DataAccessException if data access fails
     */
    <T> void deleteAll(Class<T> type);

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
     *
     * @return an accessor which wraps the given data object and it's associated metadata and provides utility and
     *         methods useful when accessing data and attributes on the data object
     *
     * @throws IllegalArgumentException if the given data object is null or an invalid data object type
     */
    <T> DataObjectWrapper<T> wrap(T dataObject);

    /**
     * Returns a copy of the given data object instance.
     *
     * <p>The method of copying is provider dependent, and will handle instances (including nested) using whatever
     * measures might be required to deal with the quirks of said provider (e.g. fetching lazy loaded relations).
     * </p>
     *
     * @param dataObject the data object to copy
     * @param <T> the type of the data object
     *
     * @return a copy of the given data object
     */
    <T> T copyInstance(T dataObject);

    /**
     * Returns whether the DataObjectService supports the given type, where
     * "supports" means that there is at least one PersistenceProvider that handles the given type.
     *
     * @param type the data object type
     *
     * @return whether the DataObjectService supports the given type
     */
    <T> boolean supports(Class<T> type);

}