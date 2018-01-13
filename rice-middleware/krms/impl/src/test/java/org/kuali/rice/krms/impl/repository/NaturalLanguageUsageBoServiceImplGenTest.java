/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.KrmsTypeGenTest;
import org.kuali.rice.krms.api.repository.NaturalLanguageUsageGenTest;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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
public class NaturalLanguageUsageBoServiceImplGenTest {
    private NaturalLanguageUsageBoServiceImpl service;
    private NaturalLanguageUsage naturalLanguageUsage;
    private DataObjectService mockDataObjectService;

    public NaturalLanguageUsageBoServiceImplGenTest() {
        mockDataObjectService = mock(DataObjectService.class);
    }

    @Before
    public void setUp() {
        service = new NaturalLanguageUsageBoServiceImpl();
        service.setDataObjectService(mockDataObjectService);
    }

    @Test
    public void test_createNaturalLanguageUsage() {
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        naturalLanguageUsage = service.getNaturalLanguageUsageByName(def.getNamespace(), def.getName());
        if (naturalLanguageUsage == null) {
            naturalLanguageUsage = service.createNaturalLanguageUsage(def);
        }
    }

    @Test
    public void test_createNaturalLanguageUsageGeneratedId() {
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsageNoId();
        naturalLanguageUsage = service.createNaturalLanguageUsage(def);
    }

    @Test
    public void test_createNaturalLanguageUsage_GeneratedId_success() {
        NaturalLanguageUsageBo saveResult = service.from(NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage());
        when(mockDataObjectService.findMatching(any(Class.class), any(QueryByCriteria.class))).thenReturn(null);
        when(mockDataObjectService.save(any(NaturalLanguageUsageBo.class), any(PersistenceOption.class))).thenReturn(saveResult);
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsageNoId();
        naturalLanguageUsage = service.createNaturalLanguageUsage(def);
        assertNotNull(naturalLanguageUsage);
    }

    @Test
    public void test_getNaturalLanguageUsageByName_success() {
        final GenericQueryResults.Builder findMatchingResult = GenericQueryResults.Builder.create();
        final List<NaturalLanguageUsageBo> resultList = new ArrayList<NaturalLanguageUsageBo>();
        NaturalLanguageUsageBo e = service.from(NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage());
        resultList.add(e);
        findMatchingResult.setResults(resultList);
        when(mockDataObjectService.findMatching(any(Class.class), any(QueryByCriteria.class))).thenReturn(findMatchingResult);
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        naturalLanguageUsage = service.getNaturalLanguageUsageByName(def.getNamespace(), def.getName());
        assertNotNull(naturalLanguageUsage);
    }

    @Test
    public void test_updateNaturalLanguageUsage_success() {
        NaturalLanguageUsage data = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        NaturalLanguageUsage findResult = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        NaturalLanguageUsageBo saveResult = service.from(NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage());
        when(mockDataObjectService.find(any(Class.class), any(String.class))).thenReturn(service.from(findResult));
        when(mockDataObjectService.save(any(NaturalLanguageUsageBo.class), any(PersistenceOption.class))).thenReturn(saveResult);
        NaturalLanguageUsage updatedData = service.updateNaturalLanguageUsage(data);
        assertNotNull(updatedData);
    }

    @Test
    public void test_from_null_yields_null() {
        assertNull(service.from(null));
    }

    @Test
    public void test_from() {
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        NaturalLanguageUsageBo def2 = service.from(def);
        assertEquals(def2.getName(), def.getName());
        assertEquals(def2.getNamespace(),def.getNamespace());
        assertEquals(def2.getId(), def.getId());
    }

    @Test
    public void test_to() {
        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullNaturalLanguageUsage();
        NaturalLanguageUsageBo naturalLanguageUsageBo = service.from(def);
        NaturalLanguageUsage def2 = NaturalLanguageUsageBo.to(naturalLanguageUsageBo);
        assertEquals(def, def2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findNaturalLanguageUsagesByName_null_fail() {
        service.findNaturalLanguageUsagesByName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findNaturalLanguageUsagesByDescription_null_fail() {
        service.findNaturalLanguageUsagesByDescription(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findNaturalLanguageUsagesByNamespace_null_fail() {
        service.findNaturalLanguageUsagesByNamespace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createNaturalLanguageUsage_null_fail() {
        service.createNaturalLanguageUsage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateNaturalLanguageUsage_null_fail() {
        service.updateNaturalLanguageUsage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteNaturalLanguageUsage_null_fail() {
        service.deleteNaturalLanguageUsage(null);
    }

    public NaturalLanguageUsage getNaturalLanguageUsage() {
        return naturalLanguageUsage;
    }

    public void setNaturalLanguageUsageBoServiceImpl(NaturalLanguageUsageBoServiceImpl impl) {
        this.service = impl;
    }

    public static NaturalLanguageUsageBoServiceImplGenTest create(NaturalLanguageUsageBoServiceImpl impl) {
        NaturalLanguageUsageBoServiceImplGenTest test = new NaturalLanguageUsageBoServiceImplGenTest();
        test.setNaturalLanguageUsageBoServiceImpl(impl);
        return test;
    }

//    void createNaturalLanguageUsage() { // TODO gen what to do when no FKs?
//        // TODO change the Object type of the parameters
//        NaturalLanguageUsage def = NaturalLanguageUsageGenTest.buildFullFKNaturalLanguageUsage();
//        naturalLanguageUsage = service.createNaturalLanguageUsage(def);
//    }

}
