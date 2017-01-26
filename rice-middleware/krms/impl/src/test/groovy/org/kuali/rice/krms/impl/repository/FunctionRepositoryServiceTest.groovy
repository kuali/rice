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
package org.kuali.rice.krms.impl.repository

import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krms.api.repository.function.FunctionRepositoryService

import static groovy.util.GroovyTestCase.assertEquals

class FunctionRepositoryServiceTest {

    private def MockFor mock
    private final shouldFail = new GroovyTestCase().&shouldFail
    FunctionBoServiceImpl functionRepositoryServiceImpl;
    FunctionRepositoryService functionRepositoryService;

    @Before
    void setupServiceUnderTest() {
        functionRepositoryServiceImpl = new FunctionBoServiceImpl()
        functionRepositoryService = functionRepositoryServiceImpl
    }

    @Before
    void setupBoServiceMockContext() {
        mock = new MockFor(DataObjectService.class)
    }

//
// FunctionRepositoryService Tests
//

	// Test FunctionRepositoryService.getFunction()
	@Test
	public void test_get_function() {

        FunctionBo resultFunctionBo = new FunctionBo(id: "1", name: "FooFunc", namespace: "RICE", returnType: "java.lang.String", typeId: "1", parameters: [], categories: []);

        mock.demand.find(1..1) { a, b -> resultFunctionBo };

        def dataObjectService = mock.proxyDelegateInstance()
		functionRepositoryServiceImpl.setDataObjectService(dataObjectService)

        assertEquals("FooFunc", functionRepositoryService.getFunction("1").getName());

        mock.verify(dataObjectService)
	}

    @Test
    public void test_get_function_blank() {
        shouldFail(RiceIllegalArgumentException.class) {
            functionRepositoryService.getFunction(" ")
        }
    }

    @Test
    public void test_get_function_null() {
        shouldFail(RiceIllegalArgumentException.class) {
            functionRepositoryService.getFunction(null)
        }
    }

    // Test FunctionRepositoryService.getFunctions()
    @Test
    public void test_get_functions() {

        FunctionBo resultFunctionBo = new FunctionBo(id: "1", name: "FooFunc", namespace: "RICE", returnType: "java.lang.String", typeId: "1", parameters: [], categories: []);

        mock.demand.find(1..1) { a, b -> resultFunctionBo };

        def dataObjectService = mock.proxyDelegateInstance()
        functionRepositoryServiceImpl.setDataObjectService(dataObjectService)

        assertEquals("FooFunc", functionRepositoryService.getFunctions(["1"]).get(0).getName());

        mock.verify(dataObjectService)
    }

    @Test
    public void test_get_functions_null() {
        shouldFail(RiceIllegalArgumentException.class) {
            functionRepositoryService.getFunction(null)
        }
    }



}
