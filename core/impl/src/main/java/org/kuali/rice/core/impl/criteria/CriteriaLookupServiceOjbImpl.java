package org.kuali.rice.core.impl.criteria;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.CompositePredicate;
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
import java.util.Collection;
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

        final Criteria ojbCriteria = fromPredicates(criteria.getPredicate());
        switch (criteria.getCountFlag()) {
            case ONLY:
                return forCountOnly(queryClass, criteria, ojbCriteria);
            case NONE:
                return forCountNone(queryClass, criteria, ojbCriteria);
            case INCLUDE:
                return forCountInclude(queryClass, criteria, ojbCriteria);
        }

        throw new UnsupportedOperationException("unsupported count flag.");
    }

    /**
     * gets results where the actual rows are requested & the count is not.
     */
    private <T> GenericQueryResults<T> forCountNone(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria) {
        return requiresRows(queryClass, criteria, ojbCriteria).build();
    }

    /**
     * gets results where the actual rows and count are requested.
     */
    private <T> GenericQueryResults<T> forCountInclude(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria) {
        GenericQueryResults.Builder<T> results = requiresRows(queryClass, criteria, ojbCriteria);
        results.setTotalRowCount(results.getResults().size());
        return results.build();
    }

    /**
     * gets results where the actual rows are requested.  Will return a mutable builder.
     */
    private <T> GenericQueryResults.Builder<T> requiresRows(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria) {
        final Query ojbQuery = newQuery(queryClass, ojbCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();

        if (criteria.getMaxResults() != null) {
            //add one so we can set the isMoreResultsAvailable
            ojbQuery.setFetchSize(criteria.getMaxResults() + 1);
        }

        if (criteria.getStartAtIndex() != null) {
            ojbQuery.setStartAtIndex(criteria.getStartAtIndex());
        }

        @SuppressWarnings("unchecked")
        final Collection<T> rows = getPersistenceBrokerTemplate().getCollectionByQuery(ojbQuery);

        results.setMoreResultsAvailable(criteria.getMaxResults() != null && rows.size() == criteria.getMaxResults() + 1);
        final List<T> rowsMinusOne = new ArrayList<T>(rows);
        if (criteria.getMaxResults() != null && rows.size() >= 1) {
            rowsMinusOne.remove(rows.size() - 1);
        }

        results.setResults(rowsMinusOne);
        return results;
    }

    /**
     * gets results where only the count is requested.
     */
    private <T> GenericQueryResults<T> forCountOnly(final Class<T> queryClass, final QueryByCriteria criteria, final Criteria ojbCriteria) {
        final Query ojbQuery = newQuery(queryClass, ojbCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();
        if (criteria.getMaxResults() != null) {
            ojbQuery.setFetchSize(criteria.getMaxResults());
        }

        if (criteria.getStartAtIndex() != null) {
            ojbQuery.setStartAtIndex(criteria.getStartAtIndex());
        }

        results.setTotalRowCount(getPersistenceBrokerTemplate().getCount(ojbQuery));

        return results.build();
    }

    private Criteria fromPredicates(Predicate... predicates) {
        Criteria ojbCriteria = newCriteria();
        if (predicates == null) {
            return ojbCriteria;
        }

        for (Predicate p : predicates) {
            if (p instanceof PropertyPathPredicate) {
                final String pp = ((PropertyPathPredicate) p).getPropertyPath();
                if (p instanceof NotNullPredicate) {
                    ojbCriteria.addNotNull(pp);
                } else if (p instanceof NullPredicate) {
                    ojbCriteria.addIsNull(pp);
                } else if (p instanceof SingleValuedPredicate) {
                    addSingleValuePredicate((SingleValuedPredicate) p, ojbCriteria);
                } else if (p instanceof MultiValuedPredicate) {
                    addMultiValuePredicate((MultiValuedPredicate) p, ojbCriteria);
                } else if (p instanceof CompositePredicate) {
                    addCompositePredicate((CompositePredicate) p, ojbCriteria);
                }
            }
        }

        return ojbCriteria;
    }

    private void addSingleValuePredicate(SingleValuedPredicate p, Criteria ojbCriteria) {
        final Object value = p.getValue().getValue();
        final String pp = p.getPropertyPath();
        if (p instanceof EqualPredicate) {
            ojbCriteria.addEqualTo(pp, value);
        } else if (p instanceof GreaterThanOrEqualPredicate) {
            ojbCriteria.addGreaterOrEqualThan(pp, value);
        } else if (p instanceof GreaterThanPredicate) {
            ojbCriteria.addGreaterThan(pp, value);
        } else if (p instanceof LessThanOrEqualPredicate) {
            ojbCriteria.addLessOrEqualThan(pp, value);
        } else if (p instanceof LessThanPredicate) {
            ojbCriteria.addLessThan(pp, value);
        } else if (p instanceof LikePredicate) {
            //no need to convert * or ? since ojb handles the conversion/escaping
            ojbCriteria.addLike(pp, value);
        } else if (p instanceof NotEqualPredicate) {
            ojbCriteria.addNotEqualTo(pp, value);
        } else if (p instanceof NotLikePredicate) {
            ojbCriteria.addNotLike(pp, value);
        }
    }

    private void addMultiValuePredicate(MultiValuedPredicate p, Criteria ojbCriteria) {
        final Set<Object> values = new HashSet<Object>();
        for (CriteriaValue<?> value : p.getValues()) {
            values.add(value.getValue());
        }
        final String pp = p.getPropertyPath();
        if (p instanceof InPredicate) {
            ojbCriteria.addIn(pp, values);
        } else if (p instanceof NotInPredicate) {
            ojbCriteria.addNotIn(pp, values);
        }
    }

    private void addCompositePredicate(CompositePredicate p, Criteria ojbCriteria) {
        final Predicate[] innerPreds = p.getPredicates().toArray(new Predicate[]{});
        if (p instanceof AndPredicate) {
            ojbCriteria.addAndCriteria(fromPredicates(innerPreds));
        } else if (p instanceof OrPredicate) {
            ojbCriteria.addOrCriteria(fromPredicates(innerPreds));
        }
    }

    protected Criteria newCriteria() {
        return new Criteria();
    }

    protected <T> Query newQuery(Class<T> queryClass, Criteria ojbCriteria) {
        return QueryFactory.newQuery(queryClass, ojbCriteria);
    }
}
