package org.kuali.rice.core.api.criteria;

public interface CriteriaLookupService {

    /**
     * Looks up a type based on a query criteria.
     *
     * @param queryClass the class to lookup
     * @param criteria the criteria to lookup against. cannot be null.
     * @param <T> the type that is being looked up.
     * @return the results. will never be null.
     * @throws IllegalArgumentException if the criteria is null
     */
    <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria);
}
