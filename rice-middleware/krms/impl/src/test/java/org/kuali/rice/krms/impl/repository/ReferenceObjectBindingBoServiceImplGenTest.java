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
package org.kuali.rice.krms.impl.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.KrmsTypeGenTest;
import org.kuali.rice.krms.api.repository.ReferenceObjectBindingGenTest;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ReferenceObjectBindingBoServiceImplGenTest {
    private ReferenceObjectBindingBoServiceImpl service;
    private ReferenceObjectBinding referenceObjectBinding;
    private DataObjectService mockDataObjectService;

    public ReferenceObjectBindingBoServiceImplGenTest() {
        mockDataObjectService = mock(DataObjectService.class);
    }

    @Before
    public void setUp() {
        service = new ReferenceObjectBindingBoServiceImpl();
        service.setDataObjectService(mockDataObjectService);
    }

    @Test
    public void test_updateReferenceObjectBinding_success() {
        final ReferenceObjectBinding data = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        final ReferenceObjectBinding findResult = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        final ReferenceObjectBindingBo saveResult = service.from(ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding());
        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(service.from(findResult));
        when(mockDataObjectService.save(any(ReferenceObjectBindingBo.class), any(PersistenceOption.class))).thenReturn(saveResult);
        ReferenceObjectBinding updatedData = service.updateReferenceObjectBinding(data);
        assertNotNull(updatedData);
    }

    @Test
    public void test_from_null_yields_null() {
        assertNull(service.from(null));
    }

    @Test
    public void test_from() {
        ReferenceObjectBinding def = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        ReferenceObjectBindingBo def2 = service.from(def);
        assertEquals(def2.getKrmsDiscriminatorType(),def.getKrmsDiscriminatorType());
        assertEquals(def2.getKrmsObjectId(),def.getKrmsObjectId());
        assertEquals(def2.getNamespace(),def.getNamespace());
        assertEquals(def2.getReferenceDiscriminatorType(),def.getReferenceDiscriminatorType());
        assertEquals(def2.getReferenceObjectId(),def.getReferenceObjectId());
        assertEquals(def2.getId(),def.getId());
    }

    @Test
    public void test_to() {
        ReferenceObjectBinding def = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        ReferenceObjectBindingBo referenceObjectBindingBo = service.from(def);
        ReferenceObjectBinding def2 = ReferenceObjectBindingBo.to(referenceObjectBindingBo);
        assertEquals(def, def2);
    }

    @Test
    public void test_createReferenceObjectBinding_success() {
        final ReferenceObjectBinding findResult = null;
        final ReferenceObjectBindingBo saveResult = service.from(ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding());
        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(service.from(findResult));
        when(mockDataObjectService.save(any(ReferenceObjectBindingBo.class), any(PersistenceOption.class))).thenReturn(saveResult);
        ReferenceObjectBinding def = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        ReferenceObjectBinding referenceObjectBinding = service.createReferenceObjectBinding(def);
        assertNotNull(referenceObjectBinding);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByCollectionName_null_fail() {
        service.findReferenceObjectBindingsByCollectionName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByKrmsDiscriminatorType_null_fail() {
        service.findReferenceObjectBindingsByKrmsDiscriminatorType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByKrmsObject_null_fail() {
        service.findReferenceObjectBindingsByKrmsObject(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByNamespace_null_fail() {
        service.findReferenceObjectBindingsByNamespace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByReferenceDiscriminatorType_null_fail() {
        service.findReferenceObjectBindingsByReferenceDiscriminatorType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findReferenceObjectBindingsByReferenceObject_null_fail() {
        service.findReferenceObjectBindingsByReferenceObject(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createReferenceObjectBinding_null_fail() {
        service.createReferenceObjectBinding(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateReferenceObjectBinding_null_fail() {
        service.updateReferenceObjectBinding(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteReferenceObjectBinding_null_fail() {
        service.deleteReferenceObjectBinding(null);
    }

    @Test
    public void test_createReferenceObjectBinding() {
        ReferenceObjectBinding def = ReferenceObjectBindingGenTest.buildFullReferenceObjectBinding();
        referenceObjectBinding = service.createReferenceObjectBinding(def);
    }

    public ReferenceObjectBinding getReferenceObjectBinding() {
        return referenceObjectBinding;
    }

    public void setReferenceObjectBindingBoServiceImpl(ReferenceObjectBindingBoServiceImpl impl) {
        this.service = impl;
    }

    public static ReferenceObjectBindingBoServiceImplGenTest create(ReferenceObjectBindingBoServiceImpl impl) {
        ReferenceObjectBindingBoServiceImplGenTest test = new ReferenceObjectBindingBoServiceImplGenTest();
        test.setReferenceObjectBindingBoServiceImpl(impl);
        return test;
    }
}
