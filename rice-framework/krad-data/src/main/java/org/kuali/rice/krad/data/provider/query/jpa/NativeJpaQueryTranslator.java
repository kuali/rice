/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.provider.query.jpa;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.data.provider.query.QueryTranslatorBase;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JPA QueryTranslator that translates queries directly into native JPA 2 Criteria API.
 */
public class NativeJpaQueryTranslator extends QueryTranslatorBase<NativeJpaQueryTranslator.TranslationContext, TypedQuery> {
    protected EntityManager entityManager;

    /**
     * Thin abstraction/container for criteria parsing context
     */
    public static class TranslationContext {
        CriteriaBuilder builder;
        CriteriaQuery query;
        Root root;
        List<Predicate> predicates = new ArrayList<Predicate>();

        TranslationContext(EntityManager entityManager, Class queryClass) {
            builder = entityManager.getCriteriaBuilder();
            query = builder.createQuery(queryClass);
            // establish single consistent root instance
            // we need this or erroneous query against distinct table aliases is generated
            root = query.from(query.getResultType());
        }

        // just a container for the inner predicates
        // copy everything else
        TranslationContext(TranslationContext parent) {
            builder = parent.builder;
            query = parent.query;
            root = parent.root;
        }

        void addPredicate(Predicate predicate) {
            predicates.add(predicate);
        }

        void and(TranslationContext predicate) {
            addPredicate(predicate.getCriteriaPredicate());
        }

        void or(TranslationContext predicate) {
            List<Predicate> newpredicates = new ArrayList<Predicate>();
            // NOTE: On EclipseLink 2.4.1 I encounter NPE from CriteriaBiulderImpl.or if invoked with predicate order reversed
            // apparently the current getCriteriaPredicate() for some reason does not have a "current node", but the incoming (nested)
            // one does?
            // NPE: Predicate orPredicate = builder.or(new Predicate[] { getCriteriaPredicate(), predicate.getCriteriaPredicate() });
            Predicate orPredicate = builder.or(new Predicate[] { predicate.getCriteriaPredicate(), getCriteriaPredicate() });
            newpredicates.add(orPredicate);
            predicates = newpredicates;
        }

        Predicate getCriteriaPredicate() {
            if (predicates.size() == 1) return predicates.get(0);
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        }

        Path attr(String attr) {
            return root.get(attr);
        }
    }

    public NativeJpaQueryTranslator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TypedQuery createQuery(Class queryClazz, TranslationContext criteria) {
        CriteriaQuery jpaQuery = criteria.query;
        // it is important to not create an empty or predicate
        if (!criteria.predicates.isEmpty()) {
            jpaQuery = jpaQuery.where(criteria.getCriteriaPredicate());
        }
        return entityManager.createQuery(jpaQuery);
    }

    @Override
    protected TranslationContext createCriteria(Class queryClazz) {
        return new TranslationContext(entityManager, queryClazz);
    }

    @Override
    protected TranslationContext createInnerCriteria(TranslationContext parent) {
        // just a container for the inner predicates
        // copy everything else
        return new TranslationContext(parent);
    }

    @Override
    public void convertQueryFlags(QueryByCriteria qbc, TypedQuery query) {
        final int startAtIndex = qbc.getStartAtIndex() != null ? qbc.getStartAtIndex() : 0;

        query.setFirstResult(startAtIndex);

        if (qbc.getMaxResults() != null) {
            //not subtracting one from MaxResults in order to retrieve
            //one extra row so that the MoreResultsAvailable field can be set
            query.setMaxResults(qbc.getMaxResults());
        }
    }

    @Override
    protected void addAnd(TranslationContext criteria, TranslationContext inner) {
        criteria.and(inner);
    }

    @Override
    protected void addNotNull(TranslationContext criteria, String propertyPath) {
        criteria.addPredicate(criteria.builder.isNotNull(criteria.attr(propertyPath)));
    }

    @Override
    protected void addIsNull(TranslationContext criteria, String propertyPath) {
        criteria.addPredicate(criteria.builder.isNull(criteria.attr(propertyPath)));
    }

    @Override
    protected void addEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.equal(criteria.attr(propertyPath), value));
    }

    @Override
    protected void addEqualToIgnoreCase(TranslationContext criteria, String propertyPath, String value) {
        criteria.addPredicate(criteria.builder.equal(criteria.builder.upper(criteria.attr(propertyPath)), value.toUpperCase()));
    }

    @Override
    protected void addGreaterOrEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.greaterThanOrEqualTo(criteria.attr(propertyPath), (Comparable) value));
    }

    @Override
    protected void addGreaterThan(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.greaterThan(criteria.attr(propertyPath), (Comparable) value));
    }

    @Override
    protected void addLessOrEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.lessThanOrEqualTo(criteria.attr(propertyPath), (Comparable) value));
    }

    @Override
    protected void addLessThan(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.lessThan(criteria.attr(propertyPath), (Comparable) value));
    }

    @Override
    protected void addLike(TranslationContext criteria, String propertyPath, Object value) {
        // value should be a String pattern
        criteria.addPredicate(criteria.builder.like(criteria.attr(propertyPath), value.toString()));
    }

    @Override
    protected void addNotEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.notEqual(criteria.attr(propertyPath), value));
    }

    @Override
    protected void addNotEqualToIgnoreCase(TranslationContext criteria, String propertyPath, String value) {
        criteria.addPredicate(criteria.builder.notEqual(criteria.builder.upper(criteria.attr(propertyPath)), value.toUpperCase()));
    }

    @Override
    protected void addNotLike(TranslationContext criteria, String propertyPath, Object value) {
        // value should be a String pattern
        criteria.addPredicate(criteria.builder.notLike(criteria.attr(propertyPath), value.toString()));
    }

    @Override
    protected void addIn(TranslationContext criteria, String propertyPath, Collection values) {
        criteria.addPredicate(criteria.attr(propertyPath).in(values));
    }

    @Override
    protected void addNotIn(TranslationContext criteria, String propertyPath, Collection values) {
        criteria.addPredicate(criteria.builder.not(criteria.attr(propertyPath).in(values)));
    }

    @Override
    protected void addOr(TranslationContext criteria, TranslationContext inner) {
        criteria.or(inner);
    }

    @Override
    protected String genUpperFunc(String pp) {
        throw new IllegalStateException("genUpperFunc should not have been invoked for NativeJpaQueryTranslator");
//        if (StringUtils.contains(pp, "__JPA_ALIAS[[")) {
//            pp = "UPPER(" + pp + ")";
//        } else {
//            pp = "UPPER(__JPA_ALIAS[[0]]__." + pp + ")";
//        }
//        return pp;
    }
}
