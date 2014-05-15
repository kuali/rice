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
package org.kuali.rice.krad.data.jpa;

import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 * Simple interface that abstracts ORM query by criteria and delete logic.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
interface CriteriaQuery {
    /**
     * Looks up a type based on a query criteria.
     *
     * @param queryClass the class to lookup
     * @param criteria the criteria to lookup against. cannot be null.
     * @param <T> the type that is being looked up.
     * @return the results. will never be null.
     * @throws IllegalArgumentException if the criteria is null
     */
    <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria);

    /**
     * Deletes all data objects based on the given type.
     *
     * @param type the type of data objects to delete
     * @param <T> the data object class type
     *
     * @throws IllegalArgumentException if the class type is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    public <T> void deleteAll(Class<T> type);


    /**
     * Deletes data objects based on the given criteria
     *
     * <p>If the given criteria is empty or null than an {@link java.lang.IllegalArgumentException} will be thrown.
     *   If the given type is null then an {@link java.lang.IllegalArgumentException} will be thrown.</p>
     *
     * @param type the type of data object
     * @param criteria criteria to filter by
     *
     * @throws IllegalArgumentException if the criteria or criteria predicate is null
     * @throws org.springframework.dao.DataAccessException if data access fails
     */
    public <T> void deleteMatching(Class<T> type, QueryByCriteria criteria);

}
