package org.kuali.rice.krad.data.provider.query;

import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 * Translates queries from generic API classes to platform-specific concrete classes
 */
public interface QueryTranslator<C, Q> {
    C translateCriteria(Class queryClazz, Predicate predicate, LookupCustomizer customizer);
    Q createQuery(Class queryClazz, C criteria);
    void convertQueryFlags(QueryByCriteria qbc, Q query);
}