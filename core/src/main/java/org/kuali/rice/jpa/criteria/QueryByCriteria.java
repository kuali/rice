/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.jpa.criteria;

import javax.persistence.EntityManager;
import javax.persistence.Query;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QueryByCriteria {

    private EntityManager entityManager;
    private Criteria criteria;
    private QueryByCriteriaType type;
    
    public enum QueryByCriteriaType {SELECT, DELETE}
    
    public QueryByCriteria(EntityManager entityManager, Criteria criteria) {
        this(entityManager, criteria, QueryByCriteriaType.SELECT);
    }

    public QueryByCriteria(EntityManager entityManager, Criteria criteria, QueryByCriteriaType type) {
        this.entityManager = entityManager;
        this.criteria = criteria;
        this.type = type;
    }

    public Query toQuery() {
        Query query = entityManager.createQuery(criteria.toQuery(type));
        criteria.prepareParameters(query);
        return query;
    }
    
    public Query toCountQuery() {
        Query query = entityManager.createQuery(criteria.toCountQuery());
        criteria.prepareParameters(query);
        return query;
    }

}
