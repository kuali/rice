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

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.Predicate;
import org.springframework.util.ClassUtils;

/**
 * Translates queries to JPA specific classes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("rawtypes")
class JpaQueryTranslator extends QueryTranslatorBase<Criteria, Query> {

    /**
     * The entity manager used in interacting with the database.
     */
    protected EntityManager entityManager;

    /**
     * Creates a JPA query translator.
     *
     * @param entityManager the entity manager used in interacting with the database.
     */
    JpaQueryTranslator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public Query createQuery(Class queryClazz, Criteria criteria) {
        return new QueryByCriteria(entityManager, criteria).toQuery();
    }

    @Override
    protected Criteria createCriteriaForSubQuery(Class queryClazz, Criteria parentContext) {
		throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Criteria createCriteria(Class entityClass) {
        return new Criteria(entityClass.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Criteria createInnerCriteria(Criteria parent) {
        try {
            return createCriteria(ClassUtils.getDefaultClassLoader().loadClass(parent.getEntityName()));
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void convertQueryFlags(org.kuali.rice.core.api.criteria.QueryByCriteria qbc, Query query) {
        //ojb's is 1 based, our query api is zero based
        //final int startAtIndex = criteria.getStartAtIndex() != null ? criteria.getStartAtIndex() + 1 : 1;
        // XXX: FIXME: had to change this to 0...why was this setting start index to 1?
        // experimentation shows that initial offset of 1 is required for OJB criteria tests to pass
        final int startAtIndex = qbc.getStartAtIndex() != null ? qbc.getStartAtIndex() : 0;
        query.setFirstResult(startAtIndex);

        if (qbc.getMaxResults() != null) {
            //not subtracting one from MaxResults in order to retrieve
            //one extra row so that the MoreResultsAvailable field can be set
            query.setMaxResults(qbc.getMaxResults());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotNull(Criteria criteria, String propertyPath) {
        criteria.notNull(propertyPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addIsNull(Criteria criteria, String propertyPath) {
        criteria.isNull(propertyPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addEqualTo(Criteria criteria, String propertyPath, Object value) {
        criteria.eq(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addGreaterOrEqualTo(Criteria criteria, String propertyPath, Object value) {
        criteria.gte(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addGreaterThan(Criteria criteria, String propertyPath, Object value) {
        criteria.gt(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLessOrEqualTo(Criteria criteria, String propertyPath, Object value) {
        criteria.lte(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLessThan(Criteria criteria, String propertyPath, Object value) {
        criteria.lt(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLike(Criteria criteria, String propertyPath, Object value) {
        criteria.like(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotEqualTo(Criteria criteria, String propertyPath, Object value) {
        criteria.ne(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotLike(Criteria criteria, String propertyPath, Object value) {
        criteria.notLike(propertyPath, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addIn(Criteria criteria, String propertyPath, Collection values) {
        criteria.in(propertyPath, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotIn(Criteria criteria, String propertyPath, Collection values) {
        criteria.notIn(propertyPath, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAnd(Criteria criteria, Criteria inner) {
        criteria.and(inner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOr(Criteria criteria, Criteria inner) {
        criteria.or(inner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String genUpperFunc(String pp) {
        if (StringUtils.contains(pp, "__JPA_ALIAS[[")) {
            pp = "UPPER(" + pp + ")";
        } else {
            pp = "UPPER(__JPA_ALIAS[[0]]__." + pp + ")";
        }
        return pp;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addExistsSubquery(Criteria criteria, String subQueryType, Predicate subQueryPredicate) {
		throw new UnsupportedOperationException();
	}
}
