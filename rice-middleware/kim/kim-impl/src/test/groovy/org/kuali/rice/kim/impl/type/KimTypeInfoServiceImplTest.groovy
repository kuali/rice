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
package org.kuali.rice.kim.impl.type

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.criteria.GenericQueryResults
import org.kuali.rice.core.api.criteria.QueryResults
import org.kuali.rice.kim.api.type.KimType
import org.kuali.rice.kim.api.type.KimTypeAttributeContract
import org.kuali.rice.kim.api.type.KimTypeContract
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krad.service.BusinessObjectService

class KimTypeInfoServiceImplTest {

    private def MockFor mock

    //importing the should fail method since I don't want to extend
    //GroovyTestCase which is junit 3 style
    private final shouldFail = new GroovyTestCase().&shouldFail

    private static final KimType kimType = create();
    private static final String key = "the_id";
    KimTypeBo bo = KimTypeBo.from(kimType)

    KimTypeInfoServiceImpl kimTypeInfoServiceImpl;
    KimTypeInfoService kimTypeInfoService;

    @Before
    void setupDoServiceMockContext() {
        mock = new MockFor(DataObjectService)

    }

    @Before
    void setupServiceUnderTest() {
        kimTypeInfoServiceImpl = new KimTypeInfoServiceImpl()
        kimTypeInfoService = kimTypeInfoServiceImpl
    }

    @Test
    void test_get_kim_type_null_id() {
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);

        shouldFail(IllegalArgumentException.class) {
            kimTypeInfoServiceImpl.getKimType(null)
        }
        mock.verify(doService)
    }

    @Test
    void test_get_kim_type_exists() {
        mock.demand.find (1) { clazz, obj -> bo }
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);
        Assert.assertEquals (kimType, kimTypeInfoServiceImpl.getKimType(key))
        mock.verify(doService)
    }

    @Test
    void test_find_kim_type_by_name_namespace_null_first() {
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);

        shouldFail(IllegalArgumentException.class) {
            kimTypeInfoServiceImpl.findKimTypeByNameAndNamespace(null, "the_name")
        }
        mock.verify(doService)
    }

    @Test
    void test_find_kim_type_by_name_namespace_null_second() {
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);

        shouldFail(IllegalArgumentException.class) {
            kimTypeInfoServiceImpl.findKimTypeByNameAndNamespace("ns", null)
        }
        mock.verify(doService)
    }

    @Test
    void test_find_kim_type_by_name_namespace_exists() {
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults([bo]);
        mock.demand.findMatching (1) { clazz, queryByCriteria -> builder.build() }
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);
        Assert.assertEquals (kimType, kimTypeInfoServiceImpl.findKimTypeByNameAndNamespace("ns", "the_name"))
        mock.verify(doService)
    }

    @Test
    void test_find_kim_type_by_name_namespace_multiple() {
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults([bo, bo]);
        mock.demand.findMatching (1) { clazz, queryByCriteria -> builder.build() }
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);
        shouldFail(IllegalStateException.class) {
            kimTypeInfoServiceImpl.findKimTypeByNameAndNamespace("ns", "the_name")
        }
        mock.verify(doService)
    }

    @Test
    void test_find_all_kim_types_none() {
        mock.demand.findMatching (1) { clazz, queryByCriteria -> GenericQueryResults.Builder.create().build() }
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);
        def values = kimTypeInfoServiceImpl.findAllKimTypes();
        Assert.assertTrue(values.isEmpty())

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add("")
        }
        mock.verify(doService)
    }

    @Test
    void test_find_all_kim_types_exists() {
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults([bo, bo]);
        mock.demand.findMatching (1) { clazz, queryByCriteria -> builder.build() }
        def doService = mock.proxyDelegateInstance()
        kimTypeInfoServiceImpl.setDataObjectService(doService);
        def values = kimTypeInfoServiceImpl.findAllKimTypes();
        Assert.assertTrue(values.size() == 2)
        Assert.assertEquals(KimTypeBo.to(bo), new ArrayList(values)[0]);
        Assert.assertEquals(KimTypeBo.to(bo), new ArrayList(values)[1]);

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add("")
        }
        mock.verify(doService)
    }

    private static create() {
        return KimType.Builder.create(new KimTypeContract() {
            String id = "the_id"
            String serviceName = "fooService"
            String namespaceCode = "ns"
            String name = "the_name"
            List<KimTypeAttributeContract> attributeDefinitions = Collections.emptyList()
            boolean active = true
            Long versionNumber = 1
            String objectId = "S:dsadas"
        }).build()
    }
}
