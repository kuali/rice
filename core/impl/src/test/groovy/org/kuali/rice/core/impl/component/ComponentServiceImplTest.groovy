/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.impl.component

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.parameter.EvaluationOperator
import org.kuali.rice.core.api.parameter.Parameter
import org.kuali.rice.core.api.parameter.ParameterContract
import org.kuali.rice.core.api.parameter.ParameterKey
import org.kuali.rice.core.api.parameter.ParameterRepositoryService
import org.kuali.rice.core.api.parameter.ParameterType
import org.kuali.rice.core.api.parameter.ParameterTypeContract
import org.kuali.rice.core.impl.parameter.ParameterBo
import org.kuali.rice.core.impl.parameter.ParameterRepositoryServiceImpl
import org.kuali.rice.krad.service.BusinessObjectService
import static org.junit.Assert.assertNotNull
import org.kuali.rice.core.api.component.ComponentService
import org.junit.After
import org.kuali.rice.core.api.component.Component
import static org.junit.Assert.assertNull

class ComponentServiceImplTest {

    private MockFor mock

    //importing the should fail method since I don't want to extend
    //GroovyTestCase which is junit 3 style
    private final shouldFail = new GroovyTestCase().&shouldFail

    ComponentServiceImpl serviceImpl;
    ComponentService service;
    BusinessObjectService boService;

    static final Component component = createComponent();
    static final ComponentBo componentBo = ComponentBo.from(component);

    @Before
    void setupServiceUnderTest() {
        service = serviceImpl = new ComponentServiceImpl()
    }

    @Before
    void setupBoServiceMockContext() {
        mock = new MockFor(BusinessObjectService)
    }

    @After
    void verifyMocks() {
        mock.verify(boService);
    }

    void injectBusinessObjectService() {
        boService = mock.proxyDelegateInstance()
        serviceImpl.setBusinessObjectService(boService)
    }

    @Test
    void test_getComponentByCode_null_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode(null, "myComponentCode")
        }
    }

    @Test
    void test_getComponentByCode_empty_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode("", "myComponentCode")
        }
    }

    @Test
    void test_getComponentByCode_blank_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode("  ", "myComponentCode")
        }
    }

    @Test
    void test_getComponentByCode_null_componentCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode("myNamespaceCode", null)
        }
    }

    @Test
    void test_getComponentByCode_empty_componentCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode("myNamespaceCode", "")
        }
    }

    @Test
    void test_getComponentByCode_blank_componentCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getComponentByCode("myNamespaceCode", "  ")
        }
    }

    @Test
    void test_getComponentByCode_exists() {
        mock.demand.findByPrimaryKey(1..1) { clazz, map -> componentBo }
        injectBusinessObjectService()
        assert component == service.getComponentByCode(NAMESPACE_CODE, CODE)
    }

    @Test
    void test_getComponentsByCode_not_exists() {
        mock.demand.findByPrimaryKey(1..1) { clazz, map -> null }
        injectBusinessObjectService()
        assert null == service.getComponentByCode("blah", "blah")
    }

    @Test
    void test_getgetAllComponentsByNamespaceCode_null_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getAllComponentsByNamespaceCode(null)
        }
    }

    @Test
    void test_getgetAllComponentsByNamespaceCode_empty_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getAllComponentsByNamespaceCode("")
        }
    }

    @Test
    void test_getAllComponentsByNamespaceCode_blank_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getAllComponentsByNamespaceCode("  ")
        }
    }

    @Test
    void test_getAllComponentsByNamespaceCode_exists() {
        mock.demand.findMatching(1..1) { clazz, map -> [componentBo] }
        injectBusinessObjectService()
        List<Component> components = service.getAllComponentsByNamespaceCode(NAMESPACE_CODE)
        assertNotNull components
        assert 1 == components.size()
        assert component == components[0]

        // list should be unmodifiable
        shouldFail(UnsupportedOperationException) {
            components.add(component)
        }
    }

    @Test
    void test_getAllComponentsByNamespaceCode_not_exists() {
        mock.demand.findMatching(1..1) { clazz, map -> [] }
        injectBusinessObjectService()
        List<Component> components = service.getAllComponentsByNamespaceCode("blah")
        assertNotNull components
        assert 0 == components.size()

        // list should be unmodifiable
        shouldFail(UnsupportedOperationException) {
            components.add(component)
        }
    }


    private static final String NAMESPACE_CODE = "MyNamespaceCode"
    private static final String CODE = "MyComponentCode"
    private static final String NAME = "This is my component!"

    private static createComponent() {
        Component.Builder builder = Component.Builder.create(NAMESPACE_CODE, CODE, NAME)
        return builder.build()
    }

}
