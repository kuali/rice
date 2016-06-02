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
package org.kuali.rice.krms.impl.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.TypeTypeRelationGenTest;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeTypeRelationBoServiceImplGenTest {
    private TypeTypeRelationBoServiceImpl service;
    private TypeTypeRelation typeTypeRelation;
    private DataObjectService mockDataObjectService;

    public TypeTypeRelationBoServiceImplGenTest() {
        mockDataObjectService = mock(DataObjectService.class);
    }

    @Before
    public void setUp() {
        service = new TypeTypeRelationBoServiceImpl();
        service.setDataObjectService(mockDataObjectService);
    }

    @Test
    public void test_createTypeTypeRelation_success() {
        TypeTypeRelationBo findResult = null;
        TypeTypeRelationBo saveResult = service.from(TypeTypeRelationGenTest.buildFullTypeTypeRelation());

        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(findResult);
        when(mockDataObjectService.save(any(TypeTypeRelationBo.class),eq(PersistenceOption.FLUSH))).thenReturn(saveResult);

        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullTypeTypeRelation();
        TypeTypeRelation typeTypeRelation = service.createTypeTypeRelation(def);
        assertNotNull(typeTypeRelation);
    }

    @Test(expected = IllegalStateException.class)
    public void test_createTypeTypeRelation_find_failure() {
        TypeTypeRelationBo findResult = service.from(TypeTypeRelationGenTest.buildFullTypeTypeRelation());
        TypeTypeRelationBo saveResult = service.from(TypeTypeRelationGenTest.buildFullTypeTypeRelation());

        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(findResult);
        when(mockDataObjectService.save(any(TypeTypeRelationBo.class),eq(PersistenceOption.FLUSH))).thenReturn(saveResult);

        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullTypeTypeRelation();
        TypeTypeRelation typeTypeRelation = service.createTypeTypeRelation(def);
        assertNotNull(typeTypeRelation);
    }

    @Test
    public void test_updateTypeTypeRelation_success() {
        TypeTypeRelationBo data1 = service.from(TypeTypeRelationGenTest.buildFullTypeTypeRelation());
        TypeTypeRelationBo data2 = service.from(TypeTypeRelationGenTest.buildFullTypeTypeRelation());
        TypeTypeRelation data3 = TypeTypeRelationGenTest.buildFullTypeTypeRelation();

        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(data1);
        when(mockDataObjectService.save(any(TypeTypeRelationBo.class),eq(PersistenceOption.FLUSH))).thenReturn(data2);

        TypeTypeRelation updatedData = service.updateTypeTypeRelation(data3);
        assertNotNull(updatedData);
    }

    @Test
    public void test_from_null_yields_null() {
        assertNull(service.from(null));
    }

    @Test
    public void test_from() {
        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullTypeTypeRelation();
        TypeTypeRelationBo typeTypeRelationBo = service.from(def);
        assertEquals(typeTypeRelationBo.getFromTypeId(), def.getFromTypeId());
        assertEquals(typeTypeRelationBo.getToTypeId(),def.getToTypeId());
        assertEquals(typeTypeRelationBo.getId(), def.getId());
    }

    @Test
    public void test_to() {
        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullTypeTypeRelation();
        TypeTypeRelationBo typeTypeRelationBo = service.from(def);
        TypeTypeRelation def2 = TypeTypeRelationBo.to(typeTypeRelationBo);
        assertEquals(def, def2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByFromType_null_fail() {
        service.findTypeTypeRelationsByFromType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByToType_null_fail() {
        service.findTypeTypeRelationsByToType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByRelationshipType_null_fail() {
        service.findTypeTypeRelationsByRelationshipType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsBySequenceNumber_null_fail() {
        service.findTypeTypeRelationsBySequenceNumber(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createTypeTypeRelation_null_fail() {
        service.createTypeTypeRelation(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateTypeTypeRelation_null_fail() {
        service.updateTypeTypeRelation(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteTypeTypeRelation_null_fail() {
        service.deleteTypeTypeRelation(null);
    }

    public TypeTypeRelation createTypeTypeRelation(KrmsTypeDefinition fromType, KrmsTypeDefinition toType) {
        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullFKTypeTypeRelation(fromType, toType);
        typeTypeRelation = service.createTypeTypeRelation(def);
        return typeTypeRelation;
    }

    public TypeTypeRelation createTypeTypeRelationGeneratedId(KrmsTypeDefinition fromType, KrmsTypeDefinition toType) {
        TypeTypeRelation def = TypeTypeRelationGenTest.buildFullFKTypeTypeRelationNoId(fromType, toType);
        typeTypeRelation = service.createTypeTypeRelation(def);
        return typeTypeRelation;
    }

    public TypeTypeRelation getTypeTypeRelation() {
        return typeTypeRelation;
    }

    public void setTypeTypeRelationBoServiceImpl(TypeTypeRelationBoServiceImpl impl) {
        this.service = impl;
    }

    public static TypeTypeRelationBoServiceImplGenTest create(TypeTypeRelationBoServiceImpl impl) {
        TypeTypeRelationBoServiceImplGenTest test = new TypeTypeRelationBoServiceImplGenTest();
        test.setTypeTypeRelationBoServiceImpl(impl);
        return test;
    }
}
