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
package org.kuali.rice.krad.data.provider;

import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.PersistenceOption;

/**
 * Created with IntelliJ IDEA.
 * User: aaron
 * Date: 2/20/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPersistenceProvider implements PersistenceProvider {
    @Override
    public <T> T save(T dataObject, PersistenceOption... options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria, LookupCustomizer<T> lookupCustomizer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(Object dataObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean handles(Class<?> type) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isProxied(Object dataObject) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object resolveProxy(Object dataObject) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
