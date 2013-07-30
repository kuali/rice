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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA specialization of DataObjectCriteriaQuery
 */
class JpaCriteriaQuery extends DataObjectCriteriaQueryBase<NativeJpaQueryTranslator.TranslationContext, TypedQuery> {
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
