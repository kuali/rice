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
import org.kuali.rice.krms.api.repository.KrmsTypeGenTest;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public final class KrmsTypeBoServiceImplGenTest {
    private KrmsTypeBoServiceImpl service;
    @Mock private KrmsAttributeDefinitionService mockAttributeService;
    @Mock private DataObjectService mockDataObjectService;
    KrmsTypeDefinition krmsType;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new KrmsTypeBoServiceImpl();
        service.setDataObjectService(mockDataObjectService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getTypeById_null_fail() {
        service.getTypeById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getTypeByName_null_fail() {
        service.getTypeByName(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findAllTypesByNamespace_null_fail() {
        service.findAllTypesByNamespace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findAllAgendaTypesByContextId_null_fail() {
        service.findAllAgendaTypesByContextId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAgendaTypeByAgendaTypeIdAndContextId_null_fail() {
        service.getAgendaTypeByAgendaTypeIdAndContextId(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findAllRuleTypesByContextId_null_fail() {
        service.findAllRuleTypesByContextId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getRuleTypeByRuleTypeIdAndContextId_null_fail() {
        service.getRuleTypeByRuleTypeIdAndContextId(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findAllActionTypesByContextId_null_fail() {
        service.findAllActionTypesByContextId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getActionTypeByActionTypeIdAndContextId_null_fail() {
        service.getActionTypeByActionTypeIdAndContextId(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAttributeDefinitionById_null_fail() {
        service.getAttributeDefinitionById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAttributeDefinitionByName_null_fail() {
        service.getAttributeDefinitionByName(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createKrmsType_null_fail() {
        service.createKrmsType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateKrmsType_null_fail() {
        service.updateKrmsType(null);
    }

    @Test
    public void test_createKrmsType() {
        KrmsTypeDefinition def = KrmsTypeGenTest.buildFullKrmsTypeDefinition();

        krmsType = service.getTypeByName(def.getNamespace(), def.getName());

        if (krmsType == null) {
            krmsType = service.createKrmsType(def);
        }
    }

    public KrmsTypeDefinition getKrmsType() {
        return krmsType;
    }

    public KrmsTypeDefinition getKrmsTypeDefinition() {
        return getKrmsType();
    }

    public void setKrmsTypeBoServiceImpl(KrmsTypeBoServiceImpl impl) {
        this.service = impl;
    }

    public static KrmsTypeBoServiceImplGenTest create(KrmsTypeBoServiceImpl impl) {
        KrmsTypeBoServiceImplGenTest test = new KrmsTypeBoServiceImplGenTest();
        test.setKrmsTypeBoServiceImpl(impl);
        return test;
    }
}
