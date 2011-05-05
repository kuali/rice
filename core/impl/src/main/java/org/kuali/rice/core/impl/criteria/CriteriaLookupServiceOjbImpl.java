package org.kuali.rice.core.impl.criteria;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.CompositePredicate;
import org.kuali.rice.core.api.criteria.CountFlag;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.CriteriaValue;
import org.kuali.rice.core.api.criteria.EqualPredicate;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.GreaterThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanPredicate;
import org.kuali.rice.core.api.criteria.InPredicate;
import org.kuali.rice.core.api.criteria.LessThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.LessThanPredicate;
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.MultiValuedPredicate;
import org.kuali.rice.core.api.criteria.NotEqualPredicate;
import org.kuali.rice.core.api.criteria.NotInPredicate;
import org.kuali.rice.core.api.criteria.NotLikePredicate;
import org.kuali.rice.core.api.criteria.NotNullPredicate;
import org.kuali.rice.core.api.criteria.NullPredicate;
import org.kuali.rice.core.api.criteria.OrPredicate;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PropertyPathPredicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.SingleValuedPredicate;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CriteriaLookupServiceOjbImpl extends PlatformAwareDaoBaseOjb implements CriteriaLookupService {

    @Override
    public <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria) {
        if (queryClass == null) {
            throw new IllegalArgumentException("queryClass is null");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("criteria is null");
        }

        final Criteria parent = new Criteria();

        if (criteria.getPredicate() != null) {
            addPredicate(criteria.getPredicate(), parent);
        }

        switch (criteria.getCountFlag()) {
            case ONLY:
                return forCountOnly(queryClass, criteria, parent);
            case NONE:
                return forRowResults(queryClass, criteria, parent, criteria.getCountFlag());
            case INCLUDE:
                return forRowResults(queryClass, criteria, parent, criteria.getCountFlag());
            default: throw new UnsupportedCountFlagException(criteria.getCountFlag());
        }
    }

    /** gets results where the actual rows are requested.  Will return a mutable builder. */
    private <T> GenericQueryResults<T> forRowResults(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria, CountFlag flag) {
        final Query ojbQuery = QueryFactory.newQuery(queryClass, ojbCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();

        /*
         * have to ALWAYS query for the count in order to set the setMoreResultsAvailable().
         * This field could be set by querying of one extra row than the max requested but
         * that would depend on the MaxResults actually being set.
         */
        final int count = getPersistenceBrokerTemplate().getCount(ojbQuery);

        if (flag == CountFlag.INCLUDE) {
            results.setTotalRowCount(count);
        }

        //ojb's is 1 based, our query api is zero based
        final int startAtIndex = criteria.getStartAtIndex() != null ? criteria.getStartAtIndex() + 1 : 1;
        ojbQuery.setStartAtIndex(startAtIndex);

        if (criteria.getMaxResults() != null) {
            ojbQuery.setEndAtIndex(criteria.getMaxResults() + startAtIndex - 1);
        }

        @SuppressWarnings("unchecked")
        List<T> rows = new ArrayList<T>(getPersistenceBrokerTemplate().getCollectionByQuery(ojbQuery));

        results.setMoreResultsAvailable(count > rows.size());

        results.setResults(rows);
        return results.build();
    }

    /** gets results where only the count is requested. */
    private <T> GenericQueryResults<T> forCountOnly(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria) {
        final Query ojbQuery = QueryFactory.newQuery(queryClass, ojbCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();
        results.setTotalRowCount(getPersistenceBrokerTemplate().getCount(ojbQuery));

        return results.build();
    }

    /** adds a predicate to a Criteria.*/
    private void addPredicate(Predicate p, Criteria parent) {

        if (p instanceof PropertyPathPredicate) {
            final String pp = ((PropertyPathPredicate) p).getPropertyPath();
            if (p instanceof NotNullPredicate) {
                parent.addNotNull(pp);
            } else if (p instanceof NullPredicate) {
                parent.addIsNull(pp);
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

    /** adds a single valued predicate to a Criteria. */
    private void addSingleValuePredicate(SingleValuedPredicate p, Criteria parent) {
        final Object value = p.getValue().getValue();
        final String pp = p.getPropertyPath();
        if (p instanceof EqualPredicate) {
            parent.addEqualTo(pp, value);
        } else if (p instanceof GreaterThanOrEqualPredicate) {
            parent.addGreaterOrEqualThan(pp, value);
        } else if (p instanceof GreaterThanPredicate) {
            parent.addGreaterThan(pp, value);
        } else if (p instanceof LessThanOrEqualPredicate) {
            parent.addLessOrEqualThan(pp, value);
        } else if (p instanceof LessThanPredicate) {
            parent.addLessThan(pp, value);
        } else if (p instanceof LikePredicate) {
            //no need to convert * or ? since ojb handles the conversion/escaping
            parent.addLike(pp, value);
        } else if (p instanceof NotEqualPredicate) {
            parent.addNotEqualTo(pp, value);
        } else if (p instanceof NotLikePredicate) {
            parent.addNotLike(pp, value);
        } else {
            throw new UnsupportedPredicateException(p);
        }
    }

    /** adds a multi valued predicate to a Criteria. */
    private void addMultiValuePredicate(MultiValuedPredicate p, Criteria parent) {
        final Set<Object> values = new HashSet<Object>();
        for (CriteriaValue<?> value : p.getValues()) {
            values.add(value.getValue());
        }
        final String pp = p.getPropertyPath();
        if (p instanceof InPredicate) {
            parent.addIn(pp, values);
        } else if (p instanceof NotInPredicate) {
            parent.addNotIn(pp, values);
        } else {
            throw new UnsupportedPredicateException(p);
        }
    }

    /** adds a composite predicate to a Criteria. */
    private void addCompositePredicate(final CompositePredicate p, final Criteria parent) {
        for (Predicate ip : p.getPredicates()) {
            final Criteria inner = new Criteria();
            addPredicate(ip, inner);
            if (p instanceof AndPredicate) {
                parent.addAndCriteria(inner);
            } else if (p instanceof OrPredicate) {
                parent.addOrCriteria(inner);
            } else {
                throw new UnsupportedPredicateException(p);
            }
        }
    }

    /** this is a fatal error since this implementation should support all known predicates. */
    private static class UnsupportedPredicateException extends RuntimeException {
        private UnsupportedPredicateException(Predicate predicate) {
            super("Unsupported predicate [" + String.valueOf(predicate) + "]");
        }
    }

    /** this is a fatal error since this implementation should support all known count flags. */
    private static class UnsupportedCountFlagException extends RuntimeException {
        private UnsupportedCountFlagException(CountFlag flag) {
            super("Unsupported predicate [" + String.valueOf(flag) + "]");
        }
    }
}
