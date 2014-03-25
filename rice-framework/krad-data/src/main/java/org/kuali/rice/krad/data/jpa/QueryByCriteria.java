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

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Defines a criteria-based query.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
class QueryByCriteria {

    private EntityManager entityManager;
    private Criteria criteria;
    private QueryByCriteriaType type;

    /**
     * Defines the type of query to run (SELECT, UPDATE, DELETE, etc.).
     */
    public enum QueryByCriteriaType {SELECT, UPDATE, DELETE}

    /**
     * Creates a criteria-based query.
     *
     * @param entityManager the entity manager for interacting with the database.
     * @param criteria the criteria to convert into a query.
     */
    QueryByCriteria(EntityManager entityManager, Criteria criteria) {
        this(entityManager, criteria, QueryByCriteriaType.SELECT);
    }

    /**
     * Creates a criteria-based query.
     *
     * @param entityManager the entity manager for interacting with the database.
     * @param criteria the criteria to convert into a query.
     * @param type the type of query to run.
     */
    QueryByCriteria(EntityManager entityManager, Criteria criteria, QueryByCriteriaType type) {
        this.entityManager = entityManager;
        this.criteria = criteria;
        this.type = type;
    }

    /**
     * Converts the current {@link Criteria} to a {@link Query}.
     *
     * @return a {@link Query} associated with the current {@link Criteria}.
     */
    public Query toQuery() {
        Query query = entityManager.createQuery(criteria.toQuery(type));
        if (criteria.getSearchLimit() != null) {
            query.setMaxResults(criteria.getSearchLimit());        	
        }
        criteria.prepareParameters(query);
        return query;
    }

    /**
     * Converts the current {@link Criteria} to a count {@link Query}.
     *
     * @return a count {@link Query} associated with the current {@link Criteria}.
     */
    public Query toCountQuery() {
        Query query = entityManager.createQuery(criteria.toCountQuery());
        if (criteria.getSearchLimit() != null) {
            query.setMaxResults(criteria.getSearchLimit());        	
        }
        criteria.prepareParameters(query);
        return query;
    }

}
