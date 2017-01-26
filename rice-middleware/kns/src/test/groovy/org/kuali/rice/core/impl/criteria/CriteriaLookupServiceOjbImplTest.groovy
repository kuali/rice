/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.impl.criteria

import org.junit.Test
import org.kuali.rice.core.api.criteria.QueryByCriteria
import org.kuali.rice.coreservice.api.parameter.Parameter

import org.kuali.rice.krad.criteria.CriteriaLookupDaoOjb

@Deprecated
class CriteriaLookupServiceOjbImplTest {

    def lookup = new CriteriaLookupDaoOjb();

    @Test(expected=IllegalArgumentException.class)
    void test_lookup_with_null_1() {
        lookup.lookup(null, QueryByCriteria.Builder.create().build())
    }

    @Test(expected=IllegalArgumentException.class)
    void test_lookup_with_null_2() {
        lookup.lookup(Parameter.class, null)
    }
}
