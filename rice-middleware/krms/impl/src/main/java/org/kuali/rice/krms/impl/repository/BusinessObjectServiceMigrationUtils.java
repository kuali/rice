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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.DataObjectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to help with adapting BusinessObjectService calls to use the DataObjectService instead
 */
public class BusinessObjectServiceMigrationUtils {

    /**
     * Adapts BusinessObjectService.findByPrimaryKey calls to use the DataObjectService.
     *
     * @param dos the DataObjectService instance
     * @param entityClass
     * @param queryAttrs attribute:value pairs that will be ANDed together in the query
     * @param <T> the type of the entity class
     * @return the matching entity
     */
    public static <T> T findSingleMatching(DataObjectService dos, Class<T> entityClass, Map<String,?> queryAttrs) {
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(queryAttrs).build();
        QueryResults<T> queryResults = dos.findMatching(entityClass, criteria);

        if (queryResults != null && !CollectionUtils.isEmpty(queryResults.getResults())) {
            List<T> results = queryResults.getResults();

            if (results.size() != 1) {
                throw new IllegalArgumentException("multiple results returned from query");
            }

            return queryResults.getResults().get(0);
        }

        return null;
    }

    /**
     * Adapts BusinessObjectService.findMatching calls to use the DataObjectService.
     *
     * @param dos the DataObjectService instance
     * @param entityClass
     * @param queryAttrs attribute:value pairs that will be ANDed together in the query
     * @param <T> the type of the entity class
     * @return the matching entities
     */
    public static <T> List<T> findMatching(DataObjectService dos, Class<T> entityClass, Map<String,?> queryAttrs) {
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(queryAttrs).build();
        QueryResults<T> queryResults = dos.findMatching(entityClass, criteria);

        if (queryResults.getResults() != null) {
            return queryResults.getResults();
        }

        return new ArrayList<T>();
    }

    /**
     * Adapts BusinessObjectService.findMatchingOrderBy calls to use the DataObjectService.
     *
     * @param dos the DataObjectService instance
     * @param entityClass
     * @param queryAttrs attribute:value pairs that will be ANDed together in the query
     * @param orderByField
     * @param sortAscending
     * @param <T> the type of the entity class
     * @return the matching entities
     */
    public static <T> List<T> findMatchingOrderBy(DataObjectService dos, Class<T> entityClass, Map<String,?> queryAttrs, String orderByField, boolean sortAscending) {
        QueryByCriteria.Builder critBuilder = QueryByCriteria.Builder.andAttributes(queryAttrs);
        OrderDirection sortDirection = sortAscending ? OrderDirection.ASCENDING : OrderDirection.DESCENDING;
        critBuilder.setOrderByFields(OrderByField.Builder.create(orderByField, sortDirection).build());

        QueryResults<T> queryResults = dos.findMatching(entityClass, critBuilder.build());

        if (queryResults.getResults() != null) {
            return queryResults.getResults();
        }

        return new ArrayList<T>();
    }

    /**
     * Adapts BusinessObjectService.deleteMatching calls to use the DataObjectService.
     *
     * @param dos the DataObjectService instance
     * @param entityClass
     * @param queryAttrs attribute:value pairs that will be ANDed together in the query
     * @param <T> the type of the entity class
     */
    public static <T> void deleteMatching(DataObjectService dos, Class<T> entityClass, Map<String,?> queryAttrs) {
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(queryAttrs).build();
        dos.deleteMatching(entityClass, criteria);
    }
}
