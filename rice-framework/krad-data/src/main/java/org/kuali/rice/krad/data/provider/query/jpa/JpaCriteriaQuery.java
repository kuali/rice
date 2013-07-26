package org.kuali.rice.krad.data.provider.query.jpa;

import org.kuali.rice.krad.data.provider.query.DataObjectCriteriaQueryBase;
import org.kuali.rice.krad.data.provider.query.QueryTranslator;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA specialization of DataObjectCriteriaQuery
 */
public class JpaCriteriaQuery extends DataObjectCriteriaQueryBase<NativeJpaQueryTranslator.TranslationContext, TypedQuery> {
    protected QueryTranslator<NativeJpaQueryTranslator.TranslationContext, TypedQuery> queryTranslator;

    public JpaCriteriaQuery(EntityManager em) {
        this.queryTranslator = new NativeJpaQueryTranslator(em);
    }

    @Override
    protected QueryTranslator<NativeJpaQueryTranslator.TranslationContext, TypedQuery> getQueryTranslator() {
        return queryTranslator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getResults(TypedQuery query) {
        return query.getResultList();
    }


    /** gets results where only the count is requested. */
    @Override
    protected int getRowCount(TypedQuery query) {
        // NOTE: is there a better way to do this (e.g. without retrieving full results)?
        return query.getResultList().size();
    }

    @Override
    public int getIncludedRowCount(TypedQuery query, List rows) {
        return rows.size();
    }
}
