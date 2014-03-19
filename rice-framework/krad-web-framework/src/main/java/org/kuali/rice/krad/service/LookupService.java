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
package org.kuali.rice.krad.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides search capabilities for the lookup framework. This service is primarily intended for internal use by the
 * lookup framework. Client code should preferably invoke {@link org.kuali.rice.krad.data.DataObjectService#findMatching(Class, org.kuali.rice.core.api.criteria.QueryByCriteria)}
 * passing the appropriate criteria.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupService {

    /**
     * Returns a collection of objects based on the given search parameters.
     * Will not limit results, so the returned Collection could be huge.
     *
     * @param type the type of the object for which to search
     * @param formProps a map of attributes against which to query
     *
     * @return an unbounded collection of results from the search
     * @deprecated please use {@link #findCollectionBySearchHelper(Class, java.util.Map, java.util.List, boolean, Integer)} instead
     */
    @Deprecated
    <T> Collection<T> findCollectionBySearchUnbounded(Class<T> type, Map<String, String> formProps);

    /**
     * Returns a collection of objects based on the given search parameters.
     *
     * @return Collection returned from the search
     * @deprecated please use {@link #findCollectionBySearchHelper(Class, java.util.Map, java.util.List, boolean, Integer)} instead
     */
    @Deprecated
    <T> Collection<T> findCollectionBySearch(Class<T> type, Map<String, String> formProps);

    /**
     * This version of findCollectionBySearchHelper is needed for version compatibility.   It allows executeSearch
     * to behave the same way as it did prior to 2.3. In the LookupDao, the value for searchResultsLimit will be
     * retrieved from the KNS version of LookupUtils in the LookupDao.
     *
     * @since 2.3
     * @deprecated please use {@link #findCollectionBySearchHelper(Class, java.util.Map, java.util.List, boolean, Integer)} instead
     */
    @Deprecated
    <T> Collection<T> findCollectionBySearchHelper(Class<T> type, Map<String, String> formProperties,
            boolean unbounded);

    /**
     *
     * @param type class name of the data object on which the lookup is performed
     * @param formProperties Map of search criteria properties obtained from the lookup form
     * @param unbounded determines if search limit used
     * @param searchResultsLimit search limit value
     * @return Collection of items found
     * @deprecated please use {@link #findCollectionBySearchHelper(Class, java.util.Map, java.util.List, boolean, Integer)} instead
     */
    @Deprecated
    <T> Collection<T> findCollectionBySearchHelper(Class<T> type, Map<String, String> formProperties,
            boolean unbounded, Integer searchResultsLimit);

    /**
     * Returns a collection of objects based on the given search parameters.
     *
     * <p>
     * This version of findCollectionBySearchHelper further isolates the UIFramework from the LookupService and
     * should be used instead of the deprecated version.
     * </p>
     *
     * @param type class name of the data object on which the lookup is performed
     * @param formProperties Map of search criteria properties obtained from the lookup form
     * @param wildcardAsLiteralPropertyNames List of property names with wildcards disabled
     * @param unbounded determines if search limit used
     * @param searchResultsLimit search limit value
     * @return Collection of items found
     */
    <T> Collection<T> findCollectionBySearchHelper(Class<T> type, Map<String, String> formProperties,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded, Integer searchResultsLimit);

    /**
     * Retrieves an Object based on the search criteria, which should uniquely
     * identify a record.
     *
     * @return Object returned from the search
     */
    <T> T findObjectBySearch(Class<T> type, Map<String, String> formProps);

     boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps);
}
