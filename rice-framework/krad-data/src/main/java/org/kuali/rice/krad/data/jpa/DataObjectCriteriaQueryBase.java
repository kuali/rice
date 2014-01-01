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

import org.kuali.rice.core.api.criteria.CountFlag;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for QueryByCriteria lookups for both Jpa and Ojb PersistenceProvider
 * implementations.  This class implements the core api CriteriaLookupService, as that
 * is the exact interface required, however this class is not intended to be defined
 * as a "service", it is merely a helper.
 */
abstract class DataObjectCriteriaQueryBase<C, Q> implements CriteriaQuery {
    protected abstract QueryTranslator<C, Q> getQueryTranslator();
    protected abstract int getRowCount(Q query);
    protected abstract int getIncludedRowCount(Q query, List rows);
    protected abstract <T> List<T> getResults(Q query);

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria) {
        return lookup(queryClass, criteria, LookupCustomizer.Builder.<T>create().build());
    }

    @Override
    public <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria, LookupCustomizer<T> customizer) {
        if (queryClass == null) {
            throw new IllegalArgumentException("queryClass is null");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("criteria is null");
        }

        if (customizer == null) {
            throw new IllegalArgumentException("customizer is null");
        }

        final C parent = getQueryTranslator().translateCriteria(queryClass, criteria.getPredicate(), customizer);

        switch (criteria.getCountFlag()) {
            case ONLY:
                return forCountOnly(queryClass, criteria, parent);
            case NONE:
                return forRowResults(queryClass, criteria, parent, criteria.getCountFlag(), customizer.getResultTransform());
            case INCLUDE:
                return forRowResults(queryClass, criteria, parent, criteria.getCountFlag(), customizer.getResultTransform());
            default: throw new UnsupportedCountFlagException(criteria.getCountFlag());
        }
    }

    /** gets results where the actual rows are requested. */
    protected <T> GenericQueryResults<T> forRowResults(final Class<T> queryClass, final QueryByCriteria criteria, final C ojbCriteria, CountFlag flag, LookupCustomizer.Transform<T, T> transform) {
        final Q query = getQueryTranslator().createQuery(queryClass, ojbCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();

        getQueryTranslator().convertQueryFlags(criteria, query);

        final List<T> rows = getResults(query);
        if (flag == CountFlag.INCLUDE) {
            results.setTotalRowCount(getIncludedRowCount(query, rows));
        }

        if (criteria.getMaxResults() != null && rows.size() > criteria.getMaxResults()) {
            results.setMoreResultsAvailable(true);
            //remove the extra row that was returned
            rows.remove(criteria.getMaxResults().intValue());
        }

        results.setResults(transformResults(rows, transform));
        return results.build();
    }

    /** gets results where only the count is requested. */
    protected <T> GenericQueryResults<T> forCountOnly(final Class<T> queryClass, final QueryByCriteria criteria, final C platformCriteria) {
        final Q query = getQueryTranslator().createQuery(queryClass, platformCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();
        results.setTotalRowCount(getRowCount(query));
        return results.build();
    }

    protected static <T> List<T> transformResults(List<T> results, LookupCustomizer.Transform<T, T> transform) {
        final List<T> list = new ArrayList<T>();
        for (T r : results) {
            list.add(transform.apply(r));
        }
        return list;
    }

    /** this is a fatal error since this implementation should support all known count flags. */
    protected static class UnsupportedCountFlagException extends RuntimeException {
        protected UnsupportedCountFlagException(CountFlag flag) {
            super("Unsupported predicate [" + String.valueOf(flag) + "]");
        }
    }
}
