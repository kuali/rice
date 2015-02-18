/**
 * Copyright 2005-2015 The Kuali Foundation
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.PropertyPath;
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
		 * If the current context is a sub-query of another context, the parent context will be stored here.
		 */
		TranslationContext parentTranslationContext;

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
         * 
         * Creates a new criteria parsing context for an inner subquery.  The parent context is stored
         * to allow references between the inner and outer queries.
         * 
         * @param entityManager the entity manager to use for interacting with the database.
         * @param queryClass the type of the query.
         * @param parentContext The {@link TranslationContext} of the outer query into which the subquery will be added as a {@link Predicate}.
         */
        TranslationContext( EntityManager entityManager, Class queryClass, TranslationContext parentContext ) {
        	this(entityManager, queryClass);
        	this.parentTranslationContext = parentContext;
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
			parentTranslationContext = parent.parentTranslationContext;
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
		 * Adds a JPA Subquery to the predicates.
		 * 
		 * @param subquery the subquery to add.
		 */
		void addExistsSubquery(Subquery<?> subquery) {
			predicates.add(builder.exists(subquery));
		}

        /**
         * Adds a JPA Subquery to the predicates.
         *
         * @param subquery the subquery to add.
         */
        void addNotExistsSubquery(Subquery<?> subquery) {
            predicates.add(builder.not( builder.exists(subquery)) );
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
		@SuppressWarnings("rawtypes")
		Path attr(String attr) {
            if (StringUtils.isBlank(attr)) {
                throw new IllegalArgumentException("Encountered an empty attribute path");
            }

			// Tokenize the property string
            String[] attrArray = attr.split("\\.");
			// first, check if this is a reference to a field on the parent (outer) query.
			// If so, and we have a parent (outer) query, then strip off the parent keyword
			// and resolve the property in that context.
			if (attrArray.length > 0 && StringUtils.equals(attrArray[0], "parent") && parentTranslationContext != null) {
				return parentTranslationContext.attr(StringUtils.substringAfter(attr, "."));
			} else {
				Path path = root;
				// split the attribute based on a period for nested property paths, for example if you want to pass an
				// attribute
				// like "property1.property2" then JPA will not interpret that properly, you have to split in manually
				for (String attrElement : attrArray) {
					if (StringUtils.isBlank(attrElement)) {
						throw new IllegalArgumentException("Encountered an empty path element in property path: "
								+ attr);
					}
					path = path.get(attrElement);
				}
				return path;
            }
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
    @Override
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
	protected TranslationContext createCriteriaForSubQuery(Class queryClazz, TranslationContext parentContext) {
		return new TranslationContext(entityManager, queryClazz, parentContext);
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
	 * Translates the Rice Criteria API {@link PropertyPath} object into a native JPA path which can be used in JPA
	 * predicates.
	 * 
	 * @param criteria
	 *            The base criteria context for translation of the property if no specific data type is given.
	 * @param value
	 *            The {@link PropertyPath} object passed in from the Rice Criteria API.
	 * @return A JPA {@link Path} object which can be used in JPA {@link Predicate} statements.
	 */
	@SuppressWarnings("rawtypes")
	protected Path translatePropertyPathIntoJpaPath(TranslationContext criteria, PropertyPath value) {
		TranslationContext tempCriteria = criteria;
		if (value.getDataType() != null) {
			try {
				tempCriteria = createCriteria(Class.forName(value.getDataType()));
			} catch (ClassNotFoundException e) {
				// unable to find the type - ignore and attempt to resolve path without special context
				Logger.getLogger(this.getClass()).error(
						"Unable to find data type " + value.getDataType()
								+ ".  Falling back to the base root for the query: " + criteria.root.getJavaType());
			}
		}
		return tempCriteria.attr(value.getPropertyPath());
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addEqualTo(TranslationContext criteria, String propertyPath, Object value) {
		// If this is a property path criteria, we need to translate it first
		if (value instanceof PropertyPath) {
			// We *must* make the call separate here. If we don't, it binds to the (Expression,Object) version of the
			// JPA method
			// which converts our property path into a string literal.
			Path path = translatePropertyPathIntoJpaPath(criteria, (PropertyPath) value);
			criteria.addPredicate(criteria.builder.equal(criteria.attr(propertyPath), path));
		} else {
			criteria.addPredicate(criteria.builder.equal(criteria.attr(propertyPath), value));
		}
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
	 * {@inheritDoc}
	 */
	@Override
	protected void addNotLikeIgnoreCase(TranslationContext criteria, String propertyPath, String value) {
		criteria.addPredicate(criteria.builder.notLike(criteria.builder.upper(criteria.attr(propertyPath)),
				fixSearchPattern(value.toUpperCase())));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addExistsSubquery(TranslationContext criteria, String subQueryType,
			org.kuali.rice.core.api.criteria.Predicate subQueryPredicate) {
		try {
			Class<?> subQueryBaseClass = Class.forName(subQueryType);
			Subquery<?> subquery = criteria.query.subquery(subQueryBaseClass);
			TranslationContext subQueryJpaPredicate = createCriteriaForSubQuery(subQueryBaseClass, criteria);

			// If a subQueryPredicate is passed, this is a Rice Predicate object and must be translated
			// into JPA - so we add it to the list this way.
			if (subQueryPredicate != null) {
				addPredicate(subQueryPredicate, subQueryJpaPredicate);
			}

			subquery.where(subQueryJpaPredicate.predicates.toArray(new Predicate[0]));
			criteria.addExistsSubquery(subquery);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(subQueryType + " can not be resolved to a class for JPA");
		}
	}

    @Override
    protected void addNotExistsSubquery(TranslationContext criteria, String subQueryType,
            org.kuali.rice.core.api.criteria.Predicate subQueryPredicate) {

        try {
            Class<?> subQueryBaseClass = Class.forName(subQueryType);
            Subquery<?> subquery = criteria.query.subquery(subQueryBaseClass);
            TranslationContext subQueryJpaPredicate = createCriteriaForSubQuery(subQueryBaseClass, criteria);

            // If a subQueryPredicate is passed, this is a Rice Predicate object and must be translated
            // into JPA - so we add it to the list this way.
            if (subQueryPredicate != null) {
                addPredicate(subQueryPredicate, subQueryJpaPredicate);
            }

            subquery.where(subQueryJpaPredicate.predicates.toArray(new Predicate[0]));
            criteria.addNotExistsSubquery(subquery);

        } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(subQueryType + " can not be resolved to a class for JPA");
        }
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
		// If this is a property path criteria, we need to translate it first
		if (value instanceof PropertyPath) {
			// We *must* make the call separate here. If we don't, it binds to the (Expression,Object) version of the
			// JPA method
			// which converts our property path into a string literal.
			Path path = translatePropertyPathIntoJpaPath(criteria, (PropertyPath) value);
			criteria.addPredicate(criteria.builder.notEqual(criteria.attr(propertyPath), path));
		} else {
			criteria.addPredicate(criteria.builder.notEqual(criteria.attr(propertyPath), value));
		}
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
    protected void addOrderBy(TranslationContext criteria, String propertyPath, boolean sortAscending) {
        List<Order> orderList = criteria.query.getOrderList();
        if (orderList == null) {
            orderList = new ArrayList<Order>();
        }

        if (propertyPath.contains(".")) {
            String propertyPathStart = StringUtils.substringBefore( propertyPath, "." );
            String propertyPathEnd = StringUtils.substringAfter( propertyPath, "." );

            if (sortAscending) {
                orderList.add(criteria.builder.asc(criteria.root.get(propertyPathStart).get(propertyPathEnd)));
            } else {
                orderList.add(criteria.builder.desc(criteria.root.get(propertyPathStart).get(propertyPathEnd)));
            }
        } else {
            if (sortAscending) {
                orderList.add(criteria.builder.asc(criteria.root.get(propertyPath)));
            } else {
                orderList.add(criteria.builder.desc(criteria.root.get(propertyPath)));
            }
        }

        criteria.query.orderBy(orderList);
    }

	// protected void addSubquery( TranslationContext criteria )

    /**
     * {@inheritDoc}
     */
    @Override
    protected String genUpperFunc(String pp) {
        throw new IllegalStateException("genUpperFunc should not have been invoked for NativeJpaQueryTranslator");
    }

}
