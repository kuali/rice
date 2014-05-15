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
import org.kuali.rice.core.api.criteria.QueryByCriteria;

import javax.persistence.Query;
import java.util.List;

/**
 * Base class for QueryByCriteria lookups and deletes for JPA PersistenceProvider implementations.
 *
 * <p>
 * Implements the core api CriteriaLookupService, as that is the exact interface required, however this class is not
 * intended to be defined as a "service", it is merely a helper.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
abstract class DataObjectCriteriaQueryBase<C, Q> implements CriteriaQuery {

    /**
     * Gets the QueryTranslator to translate from the API to implementation-specific classes.
     *
     * @return the QueryTranslator to translate from the API to implementation-specific classes.
     */
    protected abstract QueryTranslator<C, Q> getQueryTranslator();

    /**
     * Gets the row count for the given query.
     *
     * @param query the query to count the rows on.
     * @return the row count for the given query.
     */
    protected abstract int getRowCount(Q query);

    /**
     * Gets the row count to include along with the results of the query.
     *
     * @param query the query to count the rows on.
     * @param rows the result rows.
     *
     * @return The row count to include along with the results of the query.
     */
    protected abstract int getIncludedRowCount(Q query, List rows);

    /**
     * Gets the results from the given query.
     *
     * @param query the query to use to get the results.
     * @param <T> the type of results to return.
     * @return a list of results from the given query.
     */
    protected abstract <T> List<T> getResults(Q query);

    /**
     * Executes the given query.
     *
     * @param query the query to execute.
     * @return the number of records successfully committed.
     */
    protected abstract int executeUpdate(Query query);

    /**
     * {@inheritDoc}
     */
    public <T> void deleteMatching(Class<T> type, QueryByCriteria criteria) {

        if (type == null) {
            throw new IllegalArgumentException("class type is null");
        }

        // do not allow delete * on an entire table, by default
        if (criteria == null || criteria.getPredicate() == null) {
            throw new IllegalArgumentException("criteria is null");
        }

        final C parent = getQueryTranslator().translateCriteria(type, criteria.getPredicate());
        final Query query = getQueryTranslator().createDeletionQuery(type, parent);
        executeUpdate(query);
    }

    /**
     * {@inheritDoc}
     */
    public <T> void deleteAll(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("class type is null");
        }

        final C parent = getQueryTranslator().translateCriteria(type,
                QueryByCriteria.Builder.create().build().getPredicate());
        final Query query = getQueryTranslator().createDeletionQuery(type, parent);
        executeUpdate(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria) {
        if (queryClass == null) {
            throw new IllegalArgumentException("queryClass is null");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("criteria is null");
        }

        final C parent = getQueryTranslator().translateCriteria(queryClass, criteria.getPredicate());

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

    /**
     * Gets results where the actual rows are requested.
     *
     * @param queryClass the type of the results to return.
     * @param criteria the criteria to use to get the results.
     * @param ojbCriteria the implementation-specific criteria.
     * @param flag the indicator to whether the row count is requested in the results.
     *
     * @return results where the actual rows are requested.
     */
    protected <T> GenericQueryResults<T> forRowResults(final Class<T> queryClass, final QueryByCriteria criteria, final C ojbCriteria, CountFlag flag) {
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

        results.setResults(rows);
        return results.build();
    }

    /**
     * Gets results where only the count is requested.
     *
     * @param queryClass the type of the results to return.
     * @param criteria the criteria to use to get the results.
     * @param platformCriteria the implementation-specific criteria.
     *
     * @return results where only the count is requested.
     */
    protected <T> GenericQueryResults<T> forCountOnly(final Class<T> queryClass, final QueryByCriteria criteria, final C platformCriteria) {
        final Q query = getQueryTranslator().createQuery(queryClass, platformCriteria);
        final GenericQueryResults.Builder<T> results = GenericQueryResults.Builder.<T>create();
        results.setTotalRowCount(getRowCount(query));
        return results.build();
    }

    /**
     * An error to throw when the CountFlag is not recognized.
     *
     * <p>This is a fatal error since this implementation should support all known count flags.</p>
     */
    protected static class UnsupportedCountFlagException extends RuntimeException {

        /**
         * Creates an exception for if the CountFlag is not recognized.
         * @param flag the flag in error.
         */
        protected UnsupportedCountFlagException(CountFlag flag) {
            super("Unsupported predicate [" + String.valueOf(flag) + "]");
        }
    }
}
