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

import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.CompositePredicate;
import org.kuali.rice.core.api.criteria.CriteriaValue;
import org.kuali.rice.core.api.criteria.EqualIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.EqualPredicate;
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
import org.kuali.rice.core.api.criteria.NotInIgnoreCasePredicate;
import org.kuali.rice.core.api.criteria.NotInPredicate;
import org.kuali.rice.core.api.criteria.NotLikePredicate;
import org.kuali.rice.core.api.criteria.NotNullPredicate;
import org.kuali.rice.core.api.criteria.NullPredicate;
import org.kuali.rice.core.api.criteria.OrPredicate;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PropertyPathPredicate;
import org.kuali.rice.core.api.criteria.SingleValuedPredicate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Base {@link QueryTranslator} implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
abstract class QueryTranslatorBase<C, Q> implements QueryTranslator<C, Q> {

    /**
     * Creates a criteria from the given type.
     *
     * @param entityClass the type to create the criteria from.
     * @return a criteria created from the given type.
     */
    protected abstract C createCriteria(Class entityClass);

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
     * Adds an IN clause to the property.
     *
     * @param criteria the criteria to add to.
     * @param propertyPath the property to add to.
     * @param values the values to compare.
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

        /**
         * Creates an exception for if the {@link Predicate} is not recognized.
         * @param predicate the {@link Predicate} in error.
         */
        protected UnsupportedPredicateException(Predicate predicate) {
            super("Unsupported predicate [" + String.valueOf(predicate) + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C translateCriteria(Class queryClazz, Predicate predicate) {
        final C parent = createCriteria(queryClazz);

        if (predicate != null) {
            addPredicate(predicate, parent);
        }

        return parent;
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
        final String pp = p.getPropertyPath();
        if (p instanceof InPredicate) {
            final Set<?> values = getVals(p.getValues());
            addIn(parent, pp, values);
        } else if (p instanceof InIgnoreCasePredicate) {
            final Set<String> values = toUpper(getValsUnsafe(((InIgnoreCasePredicate) p).getValues()));
            addIn(parent, genUpperFunc(pp), values);
        } else if (p instanceof NotInPredicate) {
            final Set<?> values = getVals(p.getValues());
            addNotIn(parent, pp, values);
        } else if (p instanceof NotInIgnoreCasePredicate) {
            final Set<String> values = toUpper(getValsUnsafe(((NotInIgnoreCasePredicate) p).getValues()));
            addNotIn(parent, genUpperFunc(pp), values);
        } else {
            throw new UnsupportedPredicateException(p);
        }
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