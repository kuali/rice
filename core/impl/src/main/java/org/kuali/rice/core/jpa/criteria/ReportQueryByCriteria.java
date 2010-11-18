/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.jpa.criteria;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria.QueryByCriteriaType;


/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReportQueryByCriteria {

    private EntityManager entityManager;
    private Criteria criteria;
    private String[] queryAttributes;
    
    public ReportQueryByCriteria(EntityManager entityManager, Criteria criteria, String[] queryAttributes){
        this.entityManager = entityManager;
        this.criteria = criteria;
        this.queryAttributes = queryAttributes;
    }

    public Query toQuery() {
        Query query = entityManager.createQuery(criteria.toQuery(QueryByCriteriaType.SELECT, queryAttributes));
        if (criteria.getSearchLimit() != null) {
            query.setMaxResults(criteria.getSearchLimit());        	
        }
        criteria.prepareParameters(query);
        
        return query;
    }

}
