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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 * JPA QueryTranslator that translates queries directly into native JPA 2 Criteria API.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class NativeJpaQueryTranslator extends QueryTranslatorBase<NativeJpaQueryTranslator.TranslationContext, TypedQuery> {

    /**
     * Wildcard characters that are allowed in queries.
     */
	protected static final String[] LOOKUP_WILDCARDS = { "*", "?" };

    /**
     * Wildcard characters that are allowed in queries (in their escape formats).
     */
	protected static final String[] ESCAPED_LOOKUP_WILDCARDS = { "\\*", "\\?" };

    /**
     * Wildcard character equivalents in JPQL.
     */
	protected static final char[] JPQL_WILDCARDS = { '%', '_' };

    /**
     * The entity manager for interacting with the database.
     */
    protected EntityManager entityManager;

    /**
     * Thin abstraction/container for criteria parsing context.
     */
    public static class TranslationContext {

        /**
         * The builder for the criteria.
         */
        CriteriaBuilder builder;

        /**
         * The query for the criteria.
         */
        CriteriaQuery query;

        /**
         * The FROM clause root type.
         */
        Root root;

        /**
         * The list of predicates.
         */
        List<Predicate> predicates = new ArrayList<Predicate>();

        /**
         * Creates a new criteria parsing context.
         *
         * @param entityManager the entity manager to use for interacting with the database.
         * @param queryClass the type of the query.
         */
        TranslationContext(EntityManager entityManager, Class queryClass) {
            builder = entityManager.getCriteriaBuilder();
            query = builder.createQuery(queryClass);
            // establish single consistent root instance
            // we need this or erroneous query against distinct table aliases is generated
            root = query.from(query.getResultType());
        }

        /**
         * Creates a new criteria parsing context that is a container for the inner predicates.
         *
         * @param parent the parent criteria parsing context.
         */
        TranslationContext(TranslationContext parent) {
            builder = parent.builder;
            query = parent.query;
            root = parent.root;
        }

        /**
         * Adds a predicate.
         *
         * @param predicate the predicate to add.
         */
        void addPredicate(Predicate predicate) {
            predicates.add(predicate);
        }

        /**
         * Adds an AND clause.
         *
         * @param predicate the predicate to AND.
         */
        void and(TranslationContext predicate) {
            addPredicate(predicate.getCriteriaPredicate());
        }

        /**
         * Adds an OR clause.
         *
         * @param predicate the predicate to OR.
         */
        void or(TranslationContext predicate) {
            List<Predicate> newpredicates = new ArrayList<Predicate>();
            //When traversed to a simple OR predicate you may not have a criteria predicate set so check and just
            //add to builder if necKradEclipseLinkEntityManagerFactoryessary
            Predicate criteriaPredicate = getCriteriaPredicate();
            Predicate orPredicate = null;
            if(criteriaPredicate != null){
                orPredicate = builder.or(new Predicate[] {  predicate.getCriteriaPredicate(), getCriteriaPredicate() });
            } else {
                orPredicate = builder.or(predicate.getCriteriaPredicate());
            }
            newpredicates.add(orPredicate);
            predicates = newpredicates;
        }

        /**
         * Gets the criteria predicate.
         *
         * @return the criteria predicate.
         */
        Predicate getCriteriaPredicate() {
            if (predicates.size() == 1) {
                return predicates.get(0);
            } else if(predicates.size() > 1){
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            } else {
                return null;
            }
        }

        /**
         * Gets the path for the given attribute.
         *
         * @param attr the attribute path.
         * @return the path for the given attribute.
         */
        Path attr(String attr) {
            if (StringUtils.isBlank(attr)) {
                throw new IllegalArgumentException("Encountered an empty attribute path");
            }

            Path path = root;
            // split the attribute based on a period for nested property paths, for example if you want to pass an attribute
            // like "property1.property2" then JPA will not interpret that properly, you have to split in manually
            String[] attrArray = attr.split("\\.");
            for (String attrElement : attrArray) {
                if (StringUtils.isBlank(attrElement)) {
                    throw new IllegalArgumentException("Encountered an empty path element in property path: " + attr);
                }
                path = path.get(attrElement);
            }
            return path;
        }
    }

    /**
     * Creates a native JPA translator for queries.
     *
     * @param entityManager the entity manager to use for interacting with the database.
     */
    public NativeJpaQueryTranslator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypedQuery createQuery(Class queryClazz, TranslationContext criteria) {
        CriteriaQuery jpaQuery = criteria.query;
        // it is important to not create an empty or predicate
        if (!criteria.predicates.isEmpty()) {
            jpaQuery = jpaQuery.where(criteria.getCriteriaPredicate());
        }
        return entityManager.createQuery(jpaQuery);
    }

    /**
     * {@inheritDoc}
     */
    public Query createDeletionQuery(Class queryClazz, TranslationContext criteria) {
        CriteriaDelete jpaQuery = entityManager.getCriteriaBuilder().createCriteriaDelete(queryClazz);

        if (!criteria.predicates.isEmpty()) {
            jpaQuery = jpaQuery.where(criteria.getCriteriaPredicate());
        }

        return entityManager.createQuery(jpaQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TranslationContext createCriteria(Class queryClazz) {
        return new TranslationContext(entityManager, queryClazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TranslationContext createInnerCriteria(TranslationContext parent) {
        // just a container for the inner predicates
        // copy everything else
        return new TranslationContext(parent);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAnd(TranslationContext criteria, TranslationContext inner) {
        criteria.and(inner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotNull(TranslationContext criteria, String propertyPath) {
        criteria.addPredicate(criteria.builder.isNotNull(criteria.attr(propertyPath)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addIsNull(TranslationContext criteria, String propertyPath) {
        criteria.addPredicate(criteria.builder.isNull(criteria.attr(propertyPath)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.equal(criteria.attr(propertyPath), value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addEqualToIgnoreCase(TranslationContext criteria, String propertyPath, String value) {
        criteria.addPredicate(criteria.builder.equal(criteria.builder.upper(criteria.attr(propertyPath)), value.toUpperCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addGreaterOrEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.greaterThanOrEqualTo(criteria.attr(propertyPath), (Comparable) value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addGreaterThan(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.greaterThan(criteria.attr(propertyPath), (Comparable) value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLessOrEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.lessThanOrEqualTo(criteria.attr(propertyPath), (Comparable) value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLessThan(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.lessThan(criteria.attr(propertyPath), (Comparable) value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLike(TranslationContext criteria, String propertyPath, Object value) {
        // value should be a String pattern
		criteria.addPredicate(criteria.builder.like(criteria.attr(propertyPath), fixSearchPattern(value.toString())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLikeIgnoreCase(TranslationContext criteria, String propertyPath, String value){
        criteria.addPredicate(criteria.builder.like(criteria.builder.upper(criteria.attr(propertyPath)),
                fixSearchPattern(value.toUpperCase())));
    }


	/**
	 * Fixes the search pattern by converting all non-escaped lookup wildcards ("*" and "?") into their respective JPQL
	 * wildcards ("%" and "_").
     *
     * <p>Any lookup wildcards escaped by a backslash are converted into their non-backslashed equivalents.</p>
     *
     * @param value the search pattern to fix.
     * @return a fixed search pattern.
	 */
	protected String fixSearchPattern(String value) {
		StringBuilder fixedPattern = new StringBuilder(value);
		// Convert all non-escaped wildcards.
		for (int i = 0; i < LOOKUP_WILDCARDS.length; i++) {
			String lookupWildcard = LOOKUP_WILDCARDS[i];
			String escapedLookupWildcard = ESCAPED_LOOKUP_WILDCARDS[i];
			char jpqlWildcard = JPQL_WILDCARDS[i];
			int wildcardIndex = fixedPattern.indexOf(lookupWildcard);
			int escapedWildcardIndex = fixedPattern.indexOf(escapedLookupWildcard);
			while (wildcardIndex != -1) {
				if (wildcardIndex == 0 || escapedWildcardIndex != wildcardIndex - 1) {
					fixedPattern.setCharAt(wildcardIndex, jpqlWildcard);
					wildcardIndex = fixedPattern.indexOf(lookupWildcard, wildcardIndex);
				} else {
					fixedPattern.replace(escapedWildcardIndex, wildcardIndex + 1, lookupWildcard);
					wildcardIndex = fixedPattern.indexOf(lookupWildcard, wildcardIndex);
					escapedWildcardIndex = fixedPattern.indexOf(escapedLookupWildcard, wildcardIndex);
				}
			}
		}
		return fixedPattern.toString();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotEqualTo(TranslationContext criteria, String propertyPath, Object value) {
        criteria.addPredicate(criteria.builder.notEqual(criteria.attr(propertyPath), value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotEqualToIgnoreCase(TranslationContext criteria, String propertyPath, String value) {
        criteria.addPredicate(criteria.builder.notEqual(criteria.builder.upper(criteria.attr(propertyPath)), value.toUpperCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotLike(TranslationContext criteria, String propertyPath, Object value) {
        // value should be a String pattern
		criteria.addPredicate(criteria.builder.notLike(criteria.attr(propertyPath), fixSearchPattern(value.toString())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addIn(TranslationContext criteria, String propertyPath, Collection values) {
        criteria.addPredicate(criteria.attr(propertyPath).in(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNotIn(TranslationContext criteria, String propertyPath, Collection values) {
        criteria.addPredicate(criteria.builder.not(criteria.attr(propertyPath).in(values)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOr(TranslationContext criteria, TranslationContext inner) {
        criteria.or(inner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String genUpperFunc(String pp) {
        throw new IllegalStateException("genUpperFunc should not have been invoked for NativeJpaQueryTranslator");
    }

}
