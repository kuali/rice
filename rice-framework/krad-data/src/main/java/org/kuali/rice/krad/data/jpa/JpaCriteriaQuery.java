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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA specialization of DataObjectCriteriaQuery.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class JpaCriteriaQuery extends DataObjectCriteriaQueryBase<NativeJpaQueryTranslator.TranslationContext, TypedQuery> {

    /**
     * The query translator to use for this implementation.
     */
    protected QueryTranslator<NativeJpaQueryTranslator.TranslationContext, TypedQuery> queryTranslator;

    /**
     * Creates a new JPA-specific criteria query.
     *
     * @param em the entity manager used in interacting with the database.
     */
    public JpaCriteriaQuery(EntityManager em) {
        this.queryTranslator = new NativeJpaQueryTranslator(em);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected QueryTranslator<NativeJpaQueryTranslator.TranslationContext, TypedQuery> getQueryTranslator() {
        return queryTranslator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getResults(TypedQuery query) {
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(Query query) {
        return query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getRowCount(TypedQuery query) {
        // NOTE: is there a better way to do this (e.g. without retrieving full results)?
        return query.getResultList().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIncludedRowCount(TypedQuery query, List rows) {
        return rows.size();
    }
}
