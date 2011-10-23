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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.component.Component
import org.kuali.rice.core.api.component.ComponentService
import org.kuali.rice.krad.service.BusinessObjectService
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail
import java.sql.Timestamp

class ComponentServiceImplTest {

    private MockFor boServiceMock
    private MockFor componentSetDaoMock

    //importing the should fail method since I don't want to extend
    //GroovyTestCase which is junit 3 style
    private final shouldFail = new GroovyTestCase().&shouldFail

    ComponentServiceImpl serviceImpl;
    ComponentService service;
    BusinessObjectService boService;
    ComponentSetDao componentSetDao;

    static final Component component = createComponent();
    static final ComponentBo componentBo = ComponentBo.from(component);

    @Before
    void setupServiceUnderTest() {
        service = serviceImpl = new ComponentServiceImpl()
    }

    @Before
    void setupBoServiceMockContext() {
        boServiceMock = new MockFor(BusinessObjectService)
        componentSetDaoMock = new MockFor(ComponentSetDao)
    }

    @After
    void verifyMocks() {
        if (boService != null) {
            boServiceMock.verify(boService)
        }
        if (componentSetDao != null) {
            componentSetDaoMock.verify(componentSetDao)
        }
    }

    void injectBusinessObjectService() {
        boService = boServiceMock.proxyDelegateInstance()
        serviceImpl.setBusinessObjectService(boService)
    }

    void injectComponentSetDao() {
        componentSetDao = componentSetDaoMock.proxyDelegateInstance()
        serviceImpl.setComponentSetDao(componentSetDao)
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
        boServiceMock.demand.findByPrimaryKey { clazz, map -> componentBo }
        injectBusinessObjectService()
        assert component == service.getComponentByCode(NAMESPACE_CODE, CODE)
    }

    @Test
    void test_getComponentsByCode_not_exists() {
        boServiceMock.demand.findByPrimaryKey { clazz, map -> null }
        injectBusinessObjectService()
        assert null == service.getComponentByCode("blah", "blah")
    }

    @Test
    void test_getAllComponentsByNamespaceCode_null_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getAllComponentsByNamespaceCode(null)
        }
    }

    @Test
    void test_getAllComponentsByNamespaceCode_empty_namespaceCode() {
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
        boServiceMock.demand.findMatching { clazz, map -> [componentBo] }
        injectBusinessObjectService()
        List<Component> components = service.getAllComponentsByNamespaceCode(NAMESPACE_CODE)
        assertNotNull components
        assert 1 == components.size()
        assert component == components[0]
        assertImmutableList(components, component)
    }

    @Test
    void test_getAllComponentsByNamespaceCode_not_exists() {
        boServiceMock.demand.findMatching { clazz, map -> [] }
        injectBusinessObjectService()
        List<Component> components = service.getAllComponentsByNamespaceCode("blah")
        assertNotNull components
        assert 0 == components.size()
        assertImmutableList(components, component)
    }

    @Test
    void test_getActiveComponentsByNamespaceCode_null_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getActiveComponentsByNamespaceCode(null)
        }
    }

    @Test
    void test_getActiveComponentsByNamespaceCode_empty_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getActiveComponentsByNamespaceCode("")
        }
    }

    @Test
    void test_getActiveComponentsByNamespaceCode_blank_namespaceCode() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getActiveComponentsByNamespaceCode("  ")
        }
    }

    @Test
    void test_getActiveComponentsByNamespaceCode_exists() {
        boServiceMock.demand.findMatching { clazz, map ->
            if (!map.containsKey("active")) fail("Did not pass active criteria")
            [componentBo]
        }
        injectBusinessObjectService()
        List<Component> components = service.getActiveComponentsByNamespaceCode(NAMESPACE_CODE)
        assertNotNull components
        assert 1 == components.size()
        assert component == components[0]
        assertImmutableList(components, component)
    }

    @Test
    void test_getActiveComponentsByNamespaceCode_not_exists() {
        boServiceMock.demand.findMatching { clazz, map -> [] }
        injectBusinessObjectService()
        List<Component> components = service.getActiveComponentsByNamespaceCode("blah")
        assertNotNull components
        assert 0 == components.size()
        assertImmutableList(components, component)
    }

    @Test
    void test_getPublishedComponentSet_null_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getPublishedComponentSet(null)
        }
    }

    @Test
    void test_getPublishedComponentSet_empty_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getPublishedComponentSet("")
        }
    }

    @Test
    void test_getPublishedComponentSet_blank_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.getPublishedComponentSet("  ")
        }
    }

    @Test
    void test_getPublishedComponentSet_not_exists() {
        boServiceMock.demand.findMatching { clazz, map -> [] }
        injectBusinessObjectService()
        List<Component> components = service.getPublishedComponentSet("blah")
        assert components != null
        assert components.isEmpty()
        assertImmutableList(components, component)
    }

    @Test
    void test_publishComponents_null_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.publishComponents(null, [ component ])
        }
    }

    @Test
    void test_publishComponents_empty_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.publishComponents("", [ component ])
        }
    }

    @Test
    void test_publishComponents_blank_componentSetId() {
        injectBusinessObjectService()
        shouldFail(IllegalArgumentException.class) {
            service.publishComponents("  ", [ component ])
        }
    }

    @Test
    void test_publishComponents_invalidComponentSetId_onComponents() {
        injectBusinessObjectService()
        Component.Builder builder = Component.Builder.create(component)
        builder.setComponentSetId("myComponentSet")
        // should fail, componentSetIds don't match!
        shouldFail(IllegalArgumentException.class) {
            service.publishComponents("blah", [ builder.build() ])
        }
    }

    @Test
    void test_publishComponents_null_components() {

        ComponentSetBo savedComponentSet = null;
        componentSetDaoMock.demand.getComponentSet { id -> null }
        componentSetDaoMock.demand.saveIgnoreLockingFailure { cs -> savedComponentSet = cs; return true }
        boServiceMock.demand.deleteMatching { clazz, crit -> assert crit.containsKey("componentSetId") }
        boServiceMock.demand.findMatching { clazz, crit -> []}

        injectBusinessObjectService()
        injectComponentSetDao()

        service.publishComponents("myComponentSet", null)
        assert service.getPublishedComponentSet("myComponentSet").isEmpty()

        assert savedComponentSet != null
        assert savedComponentSet.checksum != null
        assert savedComponentSet.componentSetId == "myComponentSet"
        assert savedComponentSet.lastUpdateTimestamp != null
    }

    /**
     * Tests attempting to publish an empty list of components in a situation where there are already components for
     * the component set.
     */
    @Test
    void test_publishComponents_empty_components_withExisting_componentSet() {

        ComponentSetBo existingComponentSet = new ComponentSetBo(componentSetId:"myComponentSet", checksum:"blah",
                lastUpdateTimestamp:new Timestamp(System.currentTimeMillis()), versionNumber:500)
        ComponentSetBo savedComponentSet = null;

        componentSetDaoMock.demand.getComponentSet { id -> existingComponentSet }
        componentSetDaoMock.demand.saveIgnoreLockingFailure { cs -> cs.versionNumber++; savedComponentSet = cs; return true }
        boServiceMock.demand.deleteMatching { clazz, crit -> assert crit.containsKey("componentSetId") }
        boServiceMock.demand.findMatching { clazz, crit -> []}

        injectBusinessObjectService()
        injectComponentSetDao()

        service.publishComponents("myComponentSet", [])
        assert service.getPublishedComponentSet("myComponentSet").isEmpty()

        assert savedComponentSet != null
        assert savedComponentSet.versionNumber == 501
        assert savedComponentSet.checksum != "blah"
    }

    @Test
    void test_publishComponents() {

        List<ComponentBo> publishedComponentBos = []
        ComponentSetBo componentSet = null;
        
        boServiceMock.demand.findMatching { clazz, crit -> publishedComponentBos }
        boServiceMock.demand.deleteMatching { clazz, crit -> assert crit.containsKey("componentSetId") }
        boServiceMock.demand.save { bos -> publishedComponentBos = bos }
        componentSetDaoMock.demand.getComponentSet { id -> componentSet }
        componentSetDaoMock.demand.saveIgnoreLockingFailure { cs -> cs.versionNumber = 1; componentSet = cs; return true }
        boServiceMock.demand.findMatching { clazz, crit -> publishedComponentBos }

        injectBusinessObjectService()
        injectComponentSetDao()
        
        List<Component> components = service.getPublishedComponentSet("myComponentSet")
        assert components.isEmpty()
        service.publishComponents("myComponentSet", [ component ])
        components = service.getPublishedComponentSet("myComponentSet")
        assert components.size() == 1

        assert component.namespaceCode == components[0].namespaceCode
        assert component.code == components[0].code
    }

    /**
     * Tests that the calculateChecksum method returns the same checksum regardless of the order of the elements given
     * to it.
     */
    @Test
    void test_calculateChecksum_orderIndependent() {
        Component component2 = Component.Builder.create("a", "b", "name2").build()
        Component component3 = Component.Builder.create("b", "a", "name3").build()
        Component component4 = Component.Builder.create("c", "c", "name4").build()

        List<Component> components1 = new ArrayList<Component>()
        components1.add(component)
        components1.add(component2)
        components1.add(component3)
        components1.add(component4)

        String checksum1 = serviceImpl.calculateChecksum(components1)
        assert checksum1 != null


        List<Component> components2 = new ArrayList<Component>()
        components2.add(component3)
        components2.add(component2)
        components2.add(component)
        components2.add(component4)

        String checksum2 = serviceImpl.calculateChecksum(components2)
        assert checksum2 != null

        assert checksum1 == checksum2
    }

    @Test
    void test_calculateChecksum_emptyList() {
        String checksum1 = serviceImpl.calculateChecksum(new ArrayList<Component>())
        String checksum2 = serviceImpl.calculateChecksum(new ArrayList<Component>())
        assert checksum1 != null
        assert checksum2 != null
        assert checksum1 == checksum2
    }

    @Test
    void test_translateCollection_nullList() {
        List<Component> components = serviceImpl.translateCollection(null)
        assert components != null
        assert components.isEmpty()
        assertImmutableList(components, component)
    }

    @Test
    void test_translateCollection_emptyList() {
        List<Component> components = serviceImpl.translateCollection(new ArrayList<Component>())
        assert components != null
        assert components.isEmpty()
        assertImmutableList(components, component)
    }

    @Test
    void test_translateCollection() {
        List<Component> components = serviceImpl.translateCollection([componentBo])
        assert components != null
        assert components.size() == 1
        assert components[0] == component
        assertImmutableList(components, component)
    }

    private <T> void assertImmutableList(List<T> list, T object) {
        shouldFail(UnsupportedOperationException) {
            list.add(object)
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
