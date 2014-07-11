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

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

import javax.persistence.Query;

/**
 * Translates queries from generic API classes to platform-specific concrete classes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
interface QueryTranslator<C, Q> {

    /**
     * Translates the given {@link org.kuali.rice.core.api.criteria.QueryByCriteria} to a platform-specific criteria.
     *
     * @param queryClazz the type of the query.
     * @param criteria the {@link org.kuali.rice.core.api.criteria.QueryByCriteria} to translate.
     * @return a criteria for the given {@link org.kuali.rice.core.api.criteria.QueryByCriteria}.
     */
    C translateCriteria(Class queryClazz, QueryByCriteria criteria);

    /**
     * Creates a query from the given criteria.
     *
     * @param queryClazz the type of the query.
     * @param criteria the criteria to translate.
     * @return a query from the given criteria.
     */
    Q createQuery(Class queryClazz, C criteria);

    /**
     * Creates a query to delete records from the given criteria
     * @param queryClass the type of the query
     * @param criteria the criteria to translate
     * @return a query from the given criteria
     */
    Query createDeletionQuery(Class queryClass, C criteria);

    /**
     * Translates the {@link QueryByCriteria} flags to the query.
     * @param qbc the {@link QueryByCriteria} to translate from.
     * @param query the query to translate to.
     */
    void convertQueryFlags(QueryByCriteria qbc, Q query);
}