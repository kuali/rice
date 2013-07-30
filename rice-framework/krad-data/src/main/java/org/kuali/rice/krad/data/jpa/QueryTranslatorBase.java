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
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
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
 * Base QueryTranslator impl
 */
abstract class QueryTranslatorBase<C, Q> implements QueryTranslator<C, Q> {
    protected abstract C createCriteria(Class entityClass);
    protected abstract C createInnerCriteria(C parent);

    /**
     * Generate the uppercase function form of the property
     */
    protected abstract String genUpperFunc(String pp);

    protected abstract void addNotNull(C criteria, String propertyPath);
    protected abstract void addIsNull(C criteria, String propertyPath);
    protected abstract void addEqualTo(C criteria, String propertyPath, Object value);
    protected abstract void addGreaterOrEqualTo(C criteria, String propertyPath, Object value);
    protected abstract void addGreaterThan(C criteria, String propertyPath, Object value);
    protected abstract void addLessOrEqualTo(C criteria, String propertyPath, Object value);
    protected abstract void addLessThan(C criteria, String propertyPath, Object value);
    protected abstract void addLike(C criteria, String propertyPath, Object value);
    protected abstract void addNotEqualTo(C criteria, String propertyPath, Object value);
    protected abstract void addNotLike(C criteria, String propertyPath, Object value);

    protected abstract void addIn(C criteria, String propertyPath, Collection values);
    protected abstract void addNotIn(C criteria, String propertyPath, Collection values);

    protected abstract void addAnd(C criteria, C inner);
    protected abstract void addOr(C criteria, C inner);

    protected void addEqualToIgnoreCase(C criteria, String propertyPath, String value) {
        addEqualTo(criteria, genUpperFunc(propertyPath), value.toUpperCase());
    }
    protected void addNotEqualToIgnoreCase(C criteria, String propertyPath, String value) {
        addNotEqualTo(criteria, genUpperFunc(propertyPath), value.toUpperCase());
    }

    /** this is a fatal error since this implementation should support all known predicates. */
    protected static class UnsupportedPredicateException extends RuntimeException {
        protected UnsupportedPredicateException(Predicate predicate) {
            super("Unsupported predicate [" + String.valueOf(predicate) + "]");
        }
    }

    @Override
    public C translateCriteria(Class queryClazz, Predicate predicate, LookupCustomizer customizer) {
        final C parent = createCriteria(queryClazz);

        if (predicate != null) {
            addPredicate(predicate, parent, customizer.getPredicateTransform());
        }

        return parent;
    }

    /** adds a predicate to a Criteria.*/
    protected void addPredicate(Predicate p, C parent, LookupCustomizer.Transform<Predicate, Predicate> transform) {
        p = transform.apply(p);

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
            addCompositePredicate((CompositePredicate) p, parent, transform);
        } else {
            throw new UnsupportedPredicateException(p);
        }
    }

    /** adds a single valued predicate to a Criteria. */
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

    /** adds a multi valued predicate to a Criteria. */
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

    /** adds a composite predicate to a Criteria. */
    protected void addCompositePredicate(final CompositePredicate p, final C parent,  LookupCustomizer.Transform<Predicate, Predicate> transform) {
        for (Predicate ip : p.getPredicates()) {
            final C inner = createInnerCriteria(parent);
            addPredicate(ip, inner, transform);
            if (p instanceof AndPredicate) {
                addAnd(parent, inner);
            } else if (p instanceof OrPredicate) {
                addOr(parent, inner);
            } else {
                throw new UnsupportedPredicateException(p);
            }
        }
    }

    protected static <U extends CriteriaValue<?>> Object getVal(U toConv) {
        Object o = toConv.getValue();
        if (o instanceof DateTime) {
            return new Timestamp(((DateTime) o).getMillis());
        }
        return o;
    }

    //this is unsafe b/c values could be converted resulting in a classcast exception
    @SuppressWarnings("unchecked")
    protected static <T, U extends CriteriaValue<T>> Set<T> getValsUnsafe(Set<? extends U> toConv) {
        return (Set<T>) getVals(toConv);
    }

    protected static Set<?> getVals(Set<? extends CriteriaValue<?>> toConv) {
        final Set<Object> values = new HashSet<Object>();
        for (CriteriaValue<?> value : toConv) {
            values.add(getVal(value));
        }
        return values;
    }

    //eliding performance for function composition....
    private static Set<String> toUpper(Set<String> strs) {
        final Set<String> values = new HashSet<String>();
        for (String value : strs) {
            values.add(value.toUpperCase());
        }
        return values;
    }
}