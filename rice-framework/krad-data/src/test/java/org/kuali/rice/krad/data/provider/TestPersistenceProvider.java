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
package org.kuali.rice.krad.data.provider;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.PersistenceOption;

public class TestPersistenceProvider implements PersistenceProvider {
    @Override
    public <T> T save(T dataObject, PersistenceOption... options) {
        return null;
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        return null;
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        return null;
    }

    @Override
    public <T> QueryResults<T> findAll(Class<T> type) {
        return null;
    }

    @Override
    public void delete(Object dataObject) {

    }

    @Override
    public <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria) {

    }

    @Override
    public <T> void deleteAll(Class<T> type) {

    }

    @Override
    public <T> T copyInstance(T dataObject) {
        return null;
    }

    @Override
    public boolean handles(Class<?> type) {
        return true;
    }

    @Override
    public void flush(Class<?> type){
    }
}
