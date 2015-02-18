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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.CompositePredicate;
import org.kuali.rice.core.api.criteria.CriteriaValue;
import org.kuali.rice.core.api.criteria.EqualIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.EqualPredicate;
import org.kuali.rice.core.api.criteria.ExistsSubQueryPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanPredicate;
import org.kuali.rice.core.api.criteria.InIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.InPredicate;
import org.kuali.rice.core.api.criteria.LessThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.LessThanPredicate;
import org.kuali.rice.core.api.criteria.LikeIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.MultiValuedPredicate;
import org.kuali.rice.core.api.criteria.NotEqualIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.NotEqualPredicate;
import org.kuali.rice.core.api.criteria.NotExistsSubQueryPredicate;
import org.kuali.rice.core.api.criteria.NotInIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.NotInPredicate;
import org.kuali.rice.core.api.criteria.NotLikeIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.NotLikePredicate;
import org.kuali.rice.core.api.criteria.NotNullPredicate;
import org.kuali.rice.core.api.criteria.NullPredicate;
import org.kuali.rice.core.api.criteria.OrPredicate;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.PropertyPathPredicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.SingleValuedPredicate;
import org.kuali.rice.core.api.criteria.SubQueryPredicate;
import org.kuali.rice.krad.data.jpa.NativeJpaQueryTranslator.TranslationContext;

/**
 * Base {@link QueryTranslator} implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("rawtypes")
abstract class QueryTranslatorBase<C, Q> implements QueryTranslator<C, Q> {

    public static final int MULTI_VALUE_CHUNK_SIZE = 1000;

    /**
     * Creates a criteria from the given type.
     *
     * @param entityClass the type to create the criteria from.
     * @return a criteria created from the given type.
     */
	protected abstract C createCriteria(Class entityClass);

	/**
	 * Creates a new criteria parsing context from the given type for an inner subquery. The parent context is stored to
	 * allow references between the inner and outer queries.
	 *
	 * @param queryClazz
	 *            the type of the query.
	 * @param parentContext
	 *            The {@link TranslationContext} of the outer query into which the subquery will be added as a
	 *            {@link Predicate}.
	 */
	protected abstract C createCriteriaForSubQuery(Class queryClazz, C parentContext);

    /**
     * Creates a critera from the given parent critiera.
     *
     * @param parent the parent critera to create a criteria from.
     * @return a critera created from a given parent criteria.
     */
    protected abstract C createInnerCriteria(C parent);

    /**
     * Generates the uppercase function form of the property.
     *
     * @param pp the property to modify.
     * @return the uppercase function form of the property.
     */
    protected abstract String genUpperFunc(String pp);

    /**
     * Adds a NOT NULL clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     */
    protected abstract void addNotNull(C criteria, String propertyPath);

    /**
     * Adds an IS NULL clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     */
    protected abstract void addIsNull(C criteria, String propertyPath);

    /**
     * Adds a "=" clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addEqualTo(C criteria, String propertyPath, Object value);

    /**
     * Adds a ">=" clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addGreaterOrEqualTo(C criteria, String propertyPath, Object value);

    /**
     * Adds a ">" clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addGreaterThan(C criteria, String propertyPath, Object value);

    /**
     * Adds a "<=" clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addLessOrEqualTo(C criteria, String propertyPath, Object value);

    /**
     * Adds a "<" clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addLessThan(C criteria, String propertyPath, Object value);

    /**
     * Adds a LIKE clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addLike(C criteria, String propertyPath, Object value);

    /**
     * Adds a != clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addNotEqualTo(C criteria, String propertyPath, Object value);

    /**
     * Adds a NOT LIKE clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected abstract void addNotLike(C criteria, String propertyPath, Object value);

    /**
	 * Adds a NOT LIKE clause to the property, ignoring case.
	 * 
	 * @param criteria
	 *            the criteria to add to.
	 * @param propertyPath
	 *            the property to add to.
	 * @param value
	 *            the value to compare.
	 */
	protected abstract void addNotLikeIgnoreCase(C criteria, String propertyPath, String value);

	/**
	 * Adds an IN clause to the property.
	 * 
	 * @param criteria
	 *            the criteria to add to.
	 * @param propertyPath
	 *            the property to add to.
	 * @param values
	 *            the values to compare.
	 */
    protected abstract void addIn(C criteria, String propertyPath, Collection values);

    /**
     * Adds a NOT IN clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param values the values to compare.
     */
    protected abstract void addNotIn(C criteria, String propertyPath, Collection values);

    /**
     * Adds an AND clause between criteria.
     *
     * @param criteria the criteria to add to.
     * @param inner the criteria to AND.
     */
    protected abstract void addAnd(C criteria, C inner);

    /**
     * Adds an OR clause between criteria.
     *
     * @param criteria the criteria to add to.
     * @param inner the criteria to OR.
     */
    protected abstract void addOr(C criteria, C inner);

	/**
	 * Adds an EXISTS clause to the criteria.
	 *
	 * @param criteria
	 *            the criteria to add to.
	 * @param subQueryType
	 *            The data object type of the inner subquery
	 * @param subQueryPredicate
	 *            Additional predicates to apply to the inner query - may be null.
	 */
	protected abstract void addExistsSubquery(C criteria, String subQueryType, Predicate subQueryPredicate);

    /**
     * Adds an NOT EXISTS clause to the criteria.
     *
     * @param criteria
     *            the criteria to add to.
     * @param subQueryType
     *            The data object type of the inner subquery
     * @param subQueryPredicate
     *            Additional predicates to apply to the inner query - may be null.
     */
    protected abstract void addNotExistsSubquery(C criteria, String subQueryType, Predicate subQueryPredicate);

    /**
     * Adds an order by clause to the given criteria
     *
     * @param criteria the criteria to add the order by clause to
     * @param propertyPath The attribute name to order by
     * @param sortAscending Boolean that determines whether to sort by ascending order
     */
    protected abstract void addOrderBy(C criteria, String propertyPath, boolean sortAscending);


    /**
     * Adds a "=" clause to the property, ignoring case.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected void addEqualToIgnoreCase(C criteria, String propertyPath, String value) {
        addEqualTo(criteria, genUpperFunc(propertyPath), value.toUpperCase());
    }

    /**
     * Adds a != clause to the property, ignoring case.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected void addNotEqualToIgnoreCase(C criteria, String propertyPath, String value) {
        addNotEqualTo(criteria, genUpperFunc(propertyPath), value.toUpperCase());
    }

    /**
     * Adds a LIKE clause to the property, ignoring case.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param value the value to compare.
     */
    protected void addLikeIgnoreCase(C criteria, String propertyPath, String value){
        addLike(criteria, genUpperFunc(propertyPath),value.toUpperCase());
    }

    /**
     * An error to throw when the {@link Predicate} is not recognized.
     *
     * <p>This is a fatal error since this implementation should support all known predicates.</p>
     */
    protected static class UnsupportedPredicateException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
		 * Creates an exception for if the {@link Predicate} is not recognized.
		 *
		 * @param predicate
		 *            the {@link Predicate} in error.
		 */
        protected UnsupportedPredicateException(Predicate predicate) {
            super("Unsupported predicate [" + String.valueOf(predicate) + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C translateCriteria(Class queryClazz, QueryByCriteria qbc) {
        final C parent = createCriteria(queryClazz);

        if (qbc.getPredicate() != null) {
            addPredicate(qbc.getPredicate(), parent);
        }

        if (!qbc.getOrderByFields().isEmpty()) {
            addOrderBy(qbc.getOrderByFields(), parent);
        }

        return parent;
    }

    /**
     * Adds one or more order by clauses to the criteria
     *
     * @param orderByFields
     * @param parent the pareent criteria to add to
     */
    protected void addOrderBy(List<OrderByField> orderByFields, C parent) {
        for (OrderByField field : orderByFields) {
            addOrderBy(parent, field.getFieldName(), OrderDirection.ASCENDING.equals(field.getOrderDirection()));
        }
    }

    /**
     * Adds a predicate to a criteria.
     *
     * @param p the {@link Predicate} to add.
     * @param parent the parent criteria to add to.
     */
    protected void addPredicate(Predicate p, C parent) {
        if (p instanceof PropertyPathPredicate) {
            final String pp = ((PropertyPathPredicate) p).getPropertyPath();
            if (p instanceof NotNullPredicate) {
                addNotNull(parent, pp);
            } else if (p instanceof NullPredicate) {
                addIsNull(parent, pp);
            } else if (p instanceof SingleValuedPredicate) {
                addSingleValuePredicate((SingleValuedPredicate) p, parent);
            } else if (p instanceof MultiValuedPredicate) {
                addMultiValuePredicate((MultiValuedPredicate) p, parent);
            } else {
                throw new UnsupportedPredicateException(p);
            }
        } else if (p instanceof CompositePredicate) {
            addCompositePredicate((CompositePredicate) p, parent);
		} else if (p instanceof SubQueryPredicate) {
			addSubQueryPredicate((SubQueryPredicate) p, parent);
        } else {
            throw new UnsupportedPredicateException(p);
        }
    }

    /**
     * Adds a single valued predicate to a criteria.
     *
     * @param p the single valued predicate to add.
     * @param parent the parent criteria to add to.
     */
    protected void addSingleValuePredicate(SingleValuedPredicate p, C parent) {
		final Object value = getVal(p.getValue());
        final String pp = p.getPropertyPath();
        if (p instanceof EqualPredicate) {
            addEqualTo(parent, pp, value);
        } else if (p instanceof EqualIgnoreCasePredicate) {
            addEqualToIgnoreCase(parent, pp, (String) value);
        } else if (p instanceof GreaterThanOrEqualPredicate) {
            addGreaterOrEqualTo(parent, pp, value);
        } else if (p instanceof GreaterThanPredicate) {
            addGreaterThan(parent, pp, value);
        } else if (p instanceof LessThanOrEqualPredicate) {
            addLessOrEqualTo(parent, pp, value);
        } else if (p instanceof LessThanPredicate) {
            addLessThan(parent, pp, value);
        } else if (p instanceof LikePredicate) {
            //no need to convert * or ? since ojb handles the conversion/escaping
            addLike(parent, pp, value);
        } else if(p instanceof LikeIgnoreCasePredicate){
            addLikeIgnoreCase(parent,pp,(String)value);
        } else if (p instanceof NotEqualPredicate) {
            addNotEqualTo(parent, pp, value);
        } else if (p instanceof NotEqualIgnoreCasePredicate) {
            addNotEqualToIgnoreCase(parent, pp, (String) value);
		} else if (p instanceof NotLikeIgnoreCasePredicate) {
			addNotLikeIgnoreCase(parent, pp, (String) value);
        } else if (p instanceof NotLikePredicate) {
            addNotLike(parent, pp, value);
        } else {
            throw new UnsupportedPredicateException(p);
        }
    }

    /**
     * Adds a multi-valued predicate to a criteria.
     *
     * @param p the multi-valued predicate to add.
     * @param parent the parent criteria to add to.
     */
    protected void addMultiValuePredicate(MultiValuedPredicate p, C parent) {
        if(p.getValues().size() > MULTI_VALUE_CHUNK_SIZE) {
            // This predicate is too large and needs to be split into multiple smaller predicates
            splitMultiValuePredicate(p, parent);
        } else {
            final String pp = p.getPropertyPath();
            if (p instanceof InPredicate) {
                final Set<?> predicateValues = getVals(p.getValues());
                addIn(parent, pp, predicateValues);
            } else if (p instanceof InIgnoreCasePredicate) {
                final Set<String> predicateValues = toUpper(getValsUnsafe(((InIgnoreCasePredicate) p).getValues()));
                addIn(parent, genUpperFunc(pp), predicateValues);
            } else if (p instanceof NotInPredicate) {
                final Set<?> predicateValues = getVals(p.getValues());
                addNotIn(parent, pp, predicateValues);
            } else if (p instanceof NotInIgnoreCasePredicate) {
                final Set<String> predicateValues = toUpper(getValsUnsafe(((NotInIgnoreCasePredicate) p).getValues()));
                addNotIn(parent, genUpperFunc(pp), predicateValues);
            } else {
                throw new UnsupportedPredicateException(p);
            }
        }
    }

    /**
     * This method takes in a multi-value predicate which has more values than
     * can fit into a single SQL clause and splits them up into multiple
     * clauses which are concatenated by an OR statement.
     * @param p The predicate which needs to be split into smaller predicates
     * @param parent The criteria to add the predicate to
     */
    private void splitMultiValuePredicate(MultiValuedPredicate p, C parent) {
        final String pp = p.getPropertyPath();
        int chunkCount = (int)Math.ceil(p.getValues().size() / (double)MULTI_VALUE_CHUNK_SIZE);
        Predicate[] multiValuePredicateChunks = new Predicate[chunkCount];
        Object[] values = p.getValues().toArray();
        int start = 0;
        for(int i = 0; i < chunkCount; i++) {
            Object[] valueChunk = Arrays.copyOfRange(values, start, start + MULTI_VALUE_CHUNK_SIZE);
            if (p instanceof InPredicate) {
                multiValuePredicateChunks[i] = PredicateFactory.in(pp, valueChunk);
            } else if (p instanceof InIgnoreCasePredicate) {
                multiValuePredicateChunks[i] = PredicateFactory.inIgnoreCase(pp, (CharSequence[])valueChunk);
            } else if (p instanceof NotInPredicate) {
                multiValuePredicateChunks[i] = PredicateFactory.notIn(pp, valueChunk);
            } else if (p instanceof NotInIgnoreCasePredicate) {
                multiValuePredicateChunks[i] = PredicateFactory.notInIgnoreCase(pp, (CharSequence[])valueChunk);
            } else {
                throw new UnsupportedPredicateException(p);
            }

            start += MULTI_VALUE_CHUNK_SIZE;
        }
        addPredicate(PredicateFactory.or(multiValuePredicateChunks), parent);
    }

    /**
     * Adds a composite predicate to a criteria.
     *
     * @param p the composite predicate to add.
     * @param parent the parent criteria to add to.
     */
    protected void addCompositePredicate(final CompositePredicate p, final C parent) {
        for (Predicate ip : p.getPredicates()) {
            final C inner = createInnerCriteria(parent);
            addPredicate(ip, inner);
            if (p instanceof AndPredicate) {
                addAnd(parent, inner);
            } else if (p instanceof OrPredicate) {
                addOr(parent, inner);
            } else {
                throw new UnsupportedPredicateException(p);
            }
        }
    }

    /**
	 * Adds a predicate representing a sub-query to the criteria.
	 *
	 * @param p the subquery predicate to add.
	 * @param parent the parent criteria to add to.
	 */
	protected void addSubQueryPredicate(SubQueryPredicate p, C parent) {
		if (p instanceof ExistsSubQueryPredicate) {
			addExistsSubquery(parent, p.getSubQueryType(), p.getSubQueryPredicate());
		} else if (p instanceof NotExistsSubQueryPredicate) {
            addNotExistsSubquery(parent, p.getSubQueryType(), p.getSubQueryPredicate());
        }else {
			throw new UnsupportedPredicateException(p);
		}
	}

	/**
	 * Converts any {@link DateTime} values to {@link Timestamp}s.
	 *
	 * @param toConv the {@link CriteriaValue} to convert.
	 * @param <U> the type of the {@link CriteriaValue}.
	 * @return the {@link CriteriaValue} converted.
	 */
    protected static <U extends CriteriaValue<?>> Object getVal(U toConv) {
        Object o = toConv.getValue();
        if (o instanceof DateTime) {
            return new Timestamp(((DateTime) o).getMillis());
        }
        return o;
    }

    /**
     * Converts a set of {@link CriteriaValue}s.
     *
     * <p>This is unsafe because values could be converted resulting in a class cast exception.</p>
     *
     * @param toConv the {@link CriteriaValue} to convert.
     * @param <U> the initial type of the {@link CriteriaValue} set.
     * @param <T> the final type of the {@link CriteriaValue} set.
     * @return the {@link CriteriaValue} set converted.
     */
    @SuppressWarnings("unchecked")
    protected static <T, U extends CriteriaValue<T>> Set<T> getValsUnsafe(Set<? extends U> toConv) {
        return (Set<T>) getVals(toConv);
    }

    /**
     * Converts a set of {@link CriteriaValue}s to an undefined type.
     *
     * @param toConv the {@link CriteriaValue} to convert.
     * @return a set of {@link CriteriaValue}s as an undefined type.
     */
    protected static Set<?> getVals(Set<? extends CriteriaValue<?>> toConv) {
        final Set<Object> values = new HashSet<Object>();
        for (CriteriaValue<?> value : toConv) {
            values.add(getVal(value));
        }
        return values;
    }

    /**
     * Converts a set of strings to upper case.
     *
     * <p>Here we are eliding performance for function composition.</p>
     *
     * @param strs the set of strings to convert.
     * @return a set of uppercase strings.
     */
    private static Set<String> toUpper(Set<String> strs) {
        final Set<String> values = new HashSet<String>();
        for (String value : strs) {
            values.add(value.toUpperCase());
        }
        return values;
    }
}