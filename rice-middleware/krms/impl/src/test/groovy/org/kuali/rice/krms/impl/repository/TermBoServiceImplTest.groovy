/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository

import groovy.mock.interceptor.MockFor
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition

class TermBoServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail
    def mockDataObjectService
    TermBoService service

    @Before
    void setup() {
        mockDataObjectService = new MockFor(DataObjectService.class)
        service = new TermBoServiceImpl()
    }

    @Test
    void test_updateTermSpecification_success() {
        TermSpecificationBo findResult = new TermSpecificationBo(id:"1", name:"1", namespace:"a", type:"a", description:"desc")
        TermSpecificationDefinition toUpdate = TermSpecificationBo.to(new TermSpecificationBo(id:"1", name:"1", namespace:"a", type:"a", description:"desc"))

        mockDataObjectService.demand.find (1..1) {Class clazz, Object id -> findResult }
        mockDataObjectService.demand.save {bo, opts -> bo}

        DataObjectService bos = mockDataObjectService.proxyDelegateInstance()
        service.setDataObjectService(bos)

        service.updateTermSpecification(toUpdate)

        mockDataObjectService.verify(bos)
    }
}
