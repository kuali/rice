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
package org.kuali.rice.kew.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.impl.type.KewTypeAttributeBo;
import org.kuali.rice.kew.impl.type.KewTypeBo;
import org.kuali.rice.krad.data.DataObjectService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

@RunWith(MockitoJUnitRunner.class)
public class KewTypeBoServiceImplTest {

    @InjectMocks
    private KewTypeBoServiceImpl kewTypeBoService = new KewTypeBoServiceImpl();

    @Mock
    private DataObjectService dataObjectService;

    static Map<String, KewTypeBo> sampleTypes = new HashMap<String, KewTypeBo>();
    static Map<String, KewTypeBo> sampleTypesKeyedByName = new HashMap<String, KewTypeBo>();

    // create chart attribute Builder
    private static final String NAMESPACE = "KEW_TEST";
    private static final String TYPE_ID="KC_MAP123";
    private static final String NAME="KC_UNIT";
    private static final String SERVICE_NAME="kcUnitService";

    private static final String ATTR_ID_1="UNIT_NUM";
    private static final String UNIT_NUM_ATTR_DEF_ID = "1000";
    private static final Integer SEQUENCE_NUMBER_1 = new Integer(1);

    private static final String ATTR_ID_2="CAMPUS";
    private static final String CAMPUS_ATTR_DEF_ID = "1002";
    private static final Integer SEQUENCE_NUMBER_2 = new Integer(2);

    private static final String ATTR_ID_3="NewAttr";
    private static final String NEW_ATTR_DEF_ID = "1004";
    private static final Integer SEQUENCE_NUMBER_3 = new Integer(3);

    private static final String ORG_NAME = "ORG";
    private static final String ORG_LABEL = "Organization";
    private static final String COMPONENT = "someOrgComponent";

    private static KewAttributeDefinition.Builder unitNumAttrDefn = KewAttributeDefinition.Builder.create(UNIT_NUM_ATTR_DEF_ID, "testAttrDef1", NAMESPACE);
    private static KewAttributeDefinition.Builder campusAttrDefn = KewAttributeDefinition.Builder.create(CAMPUS_ATTR_DEF_ID, "testAttrDef2", NAMESPACE);
    private static KewAttributeDefinition.Builder newAttrDefn = KewAttributeDefinition.Builder.create(NEW_ATTR_DEF_ID, "testAttrDef3", NAMESPACE);
    // create sample KewType builder and build
    private static KewTypeAttribute.Builder unitNumAttrBuilder = KewTypeAttribute.Builder.create(ATTR_ID_1, TYPE_ID,
            UNIT_NUM_ATTR_DEF_ID, SEQUENCE_NUMBER_1).attributeDefinition(unitNumAttrDefn);
    private static KewTypeAttribute.Builder campusAttrBuilder = KewTypeAttribute.Builder.create(ATTR_ID_2, TYPE_ID,
            CAMPUS_ATTR_DEF_ID, SEQUENCE_NUMBER_2).attributeDefinition(campusAttrDefn);
    private static KewTypeAttribute newAttr = KewTypeAttribute.Builder.create(ATTR_ID_3, TYPE_ID, NEW_ATTR_DEF_ID,
            SEQUENCE_NUMBER_3).attributeDefinition(newAttrDefn).build();

    private static List<KewTypeAttribute.Builder> attrs = Arrays.asList(unitNumAttrBuilder, campusAttrBuilder);
    private static KewTypeDefinition TEST_KEW_TYPE_DEF = KewTypeDefinition.Builder.create(TYPE_ID, NAME, NAMESPACE)
            .serviceName(SERVICE_NAME)
            .attributes(attrs)
            .build();
    private static KewTypeBo TEST_KEW_TYPE_BO = KewTypeBo.from(TEST_KEW_TYPE_DEF);
    private static KewTypeAttributeBo TEST_KEW_TYPE_ATTRIBUTE_BO = KewTypeAttributeBo.from(newAttr, TEST_KEW_TYPE_BO);

    private static KewTypeBo createKewTypeBo(String id, boolean isActive, String name, String namespace,
            String serviceName) {

        KewTypeBo kewTypeBo = new KewTypeBo();

        kewTypeBo.setId(id);
        kewTypeBo.setActive(isActive);
        kewTypeBo.setName(name);
        kewTypeBo.setNamespace(namespace);
        kewTypeBo.setServiceName(serviceName);

        return kewTypeBo;
    }

    @BeforeClass
    public static void createSampleTypeBOs() {

        KewTypeBo defaultBo = createKewTypeBo("1", Boolean.TRUE, "DEFAULT", "KEW_TEST", "KewTypeBoServiceImpl");
        KewTypeBo studentBo = createKewTypeBo("2", Boolean.TRUE, "Student", "KEW_TEST", "KewTypeBoServiceImpl");
        KewTypeBo ifopalBo = createKewTypeBo("3", Boolean.TRUE, "IFOPAL", "KC_TEST", null);

        for (KewTypeBo bo : Arrays.asList(defaultBo, studentBo, ifopalBo)) {
            sampleTypes.put(bo.getId(), bo);
            sampleTypesKeyedByName.put(bo.getName(), bo);
        }
    }

    @Test
    public void testGetType() {
        when(dataObjectService.find(KewTypeBo.class, "1")).thenReturn(sampleTypes.get("1"));
        KewTypeDefinition testDefn = getKewTypeBoService().getTypeById("1");
        verify(dataObjectService).find(KewTypeBo.class, "1");
        assertEquals(KewTypeBo.to(sampleTypes.get("1")), testDefn);
    }

    @Test
    public void testGetByIdWhenNoneFound() {
        when(dataObjectService.find(KewTypeBo.class, "I DONT EXIST")).thenReturn(null);

        KewTypeDefinition testDefn = getKewTypeBoService().getTypeById("I DONT EXIST");
        verify(dataObjectService).find(KewTypeBo.class, "I DONT EXIST");
        assertNull(testDefn);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByPrimaryIdEmptyTypeId() {
        getKewTypeBoService().getTypeById("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByPrimaryIdNullTypeId() {
        getKewTypeBoService().getTypeById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByNameAndNamespace_null_type_id() {
        getKewTypeBoService().getTypeByNameAndNamespace(null, "KEW_TEST");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByNameAndNamespace_null_namespace() {
        getKewTypeBoService().getTypeByNameAndNamespace("Student", null);
    }

    @Test
    public void testGetByNameAndNamespace() {
        final String name = "Student";
        final String namespace = "KEW_TEST";
        ArgumentCaptor<QueryByCriteria> argument = setupDOSFetchKewTypeBoByFindMatching(Arrays.asList(
                sampleTypesKeyedByName.get("Student")));
        KewTypeDefinition kewTypeDefn = getKewTypeBoService().getTypeByNameAndNamespace(name, namespace);
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("Name passed as criteria for findMatching", StringUtils.contains(queryByCriteria.toString(),
                "name, Student"));
        assertTrue("Namespace passed as criteria for findMatching", StringUtils.contains(queryByCriteria.toString(),
                "namespace, KEW_TEST"));
        assertTrue("KewTypeDefinition was returned.", null != kewTypeDefn && kewTypeDefn.getName().equals(name) &&
                kewTypeDefn.getNamespace().equals(namespace));

        verify(dataObjectService, times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findAllTypesByNamespace_null_namespace() {
        final String namespace = null;
        getKewTypeBoService().findAllTypesByNamespace(namespace);
    }

    @Test
    public void test_findAllTypesByNamespace() {
        final String namespace = "KEW_TEST";
        ArgumentCaptor<QueryByCriteria> argument =
                setupDOSFetchKewTypeBoByFindMatching(Arrays.asList(sampleTypes.get("1"), sampleTypes.get("2")));

        List<KewTypeDefinition> resultList = getKewTypeBoService().findAllTypesByNamespace(namespace);
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("Namespace passed as criteria for findMatching", StringUtils.contains(queryByCriteria.toString(),
                "namespace, KEW_TEST"));
        assertTrue("findAllTypesByNamespace retrieved correctly", null != resultList && resultList.size() == 2);
        assertEquals(KewTypeBo.to(sampleTypes.get("1")), resultList.get(0));
        assertEquals(KewTypeBo.to(sampleTypes.get("2")), resultList.get(1));

        verify(dataObjectService, times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_findAllTypes() {
        setupDOSFetchKewTypeBoByFindMatching(Arrays.asList(sampleTypes.get("1"), sampleTypes.get("2"), sampleTypes.get(
                "3")));

        List<KewTypeDefinition> resultList = getKewTypeBoService().findAllTypes();
        assertTrue("findAllTypes retrived correctly", null != resultList && resultList.size() == 3);
        assertEquals(KewTypeBo.to(sampleTypes.get("1")), resultList.get(0));
        assertEquals(KewTypeBo.to(sampleTypes.get("2")), resultList.get(1));
        assertEquals(KewTypeBo.to(sampleTypes.get("3")), resultList.get(2));

        verify(dataObjectService, times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createKewType_null_input() {
        getKewTypeBoService().createKewType(null);
    }


    @Test(expected = IllegalStateException.class)
    public void test_createKewType_exists() {
        setupDOSFetchKewTypeBoByFindMatching(Arrays.asList(TEST_KEW_TYPE_BO));
        KewTypeDefinition kewTypeDefn = getKewTypeBoService().createKewType(TEST_KEW_TYPE_DEF);

        verify(dataObjectService, times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_createKewType_success() {
        setupDOSFetchKewTypeBoByFindMatching(new ArrayList<KewTypeBo>());
        KewTypeDefinition kewTypeDefn = getKewTypeBoService().createKewType(TEST_KEW_TYPE_DEF);

        verify(dataObjectService, times(1)).findMatching(Matchers.argThat(new ClassOrSubclassMatcher<KewTypeBo>(
                KewTypeBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateKewType_null_input() {
        getKewTypeBoService().updateKewType(null);
    }

    @Test(expected = IllegalStateException.class)
    public void test_updateKewType_does_not_exist() {
        setupDOSFetchKewTypeBoByFind(null);

        getKewTypeBoService().updateKewType(TEST_KEW_TYPE_DEF);

        verify(dataObjectService, times(1)).find(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)), anyObject());
    }

    @Test
    public void test_updateKewType_success() {
        setupDOSFetchKewTypeBoByFind(TEST_KEW_TYPE_BO);

        getKewTypeBoService().updateKewType(TEST_KEW_TYPE_DEF);

        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<KewTypeBo>(
                KewTypeBo.class)), any(String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createKewTypeAttribute_null_input() {
        getKewTypeBoService().createKewTypeAttribute(null);
    }

    @Test(expected = IllegalStateException.class)
    public void test_createKewTypeAttribute_exists() {
        setupDOSFetchKewTypeAttrByFind(TEST_KEW_TYPE_ATTRIBUTE_BO);

        getKewTypeBoService().createKewTypeAttribute(newAttr);

        verify(dataObjectService, times(1)).find(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeAttribute>(KewTypeAttribute.class)), anyObject());
    }

    @Test
    public void test_createKewTypeAttribute_success() {
        setupDOSFetchKewTypeAttrByFind(null);

        getKewTypeBoService().createKewTypeAttribute(newAttr);

        verify(dataObjectService, times(1)).find(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeAttributeBo>(KewTypeAttributeBo.class)), any(KewTypeAttribute.class));
        verify(dataObjectService, times(1)).save(any(KewTypeAttributeBo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateKewTypeAttribute_null_input() {
        getKewTypeBoService().updateKewTypeAttribute(null);
    }

    @Test(expected = IllegalStateException.class)
    public void test_updateKewTypeAttribute_does_not_exist() {
        setupDOSFetchKewTypeAttrByFind(null);

        getKewTypeBoService().updateKewTypeAttribute(newAttr);

        verify(dataObjectService, times(1)).find(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeAttributeBo>(KewTypeAttributeBo.class)), any(KewTypeAttribute.class));
    }

    @Test
    public void test_updateKewTypeAttribute_success() {
        setupDOSFetchKewTypeAttrByFind(TEST_KEW_TYPE_ATTRIBUTE_BO);

        getKewTypeBoService().updateKewTypeAttribute(newAttr);

        verify(dataObjectService, times(1)).find(Matchers.argThat(
                new ClassOrSubclassMatcher<KewTypeAttributeBo>(KewTypeAttributeBo.class)), any(KewTypeAttribute.class));
        verify(dataObjectService, times(1)).save(any(KewTypeAttributeBo.class));
    }


    /* -------------- */

    private ArgumentCaptor<QueryByCriteria> setupDOSFetchKewTypeBoByFindMatching(List<KewTypeBo> resultList) {
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();

        builder.setResults(resultList);
        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        when(dataObjectService.findMatching(Matchers.argThat(new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)),
                argument.capture())).thenReturn(builder.build());

        return argument;
    }

    private ArgumentCaptor<QueryByCriteria> setupDOSFetchKewTypeBoByFind(KewTypeBo resultBo) {

        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<KewTypeBo>(KewTypeBo.class)),
                argument.capture())).thenReturn(resultBo);

        return argument;
    }

    private ArgumentCaptor<QueryByCriteria> setupDOSFetchKewTypeAttrByFind(KewTypeAttributeBo resultAttr) {

        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<KewTypeAttributeBo>(
                KewTypeAttributeBo.class)), argument.capture())).thenReturn(resultAttr);

        return argument;
    }

    private KewTypeBoServiceImpl getKewTypeBoService() {
        return kewTypeBoService;
    }

    private static KewTypeAttribute createKewTypeAttribute(String attributeId, String typeId,
            String attrDefnId, Integer sequenceNumber) {

        KewTypeAttribute.Builder attrDefnBuilder = KewTypeAttribute.Builder.create(attributeId, typeId,
                attrDefnId, sequenceNumber);

        return attrDefnBuilder.build();

    }

    class ClassOrSubclassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassOrSubclassMatcher(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @SuppressWarnings("unchecked")
        public boolean matches(Object obj) {
            if (obj != null) {
                if (obj instanceof Class) {
                    return targetClass.isAssignableFrom((Class<T>) obj);
                }
            }
            return false;
        }

        public void describeTo(Description desc) {
            desc.appendText("Matches a class or subclass");
        }
    }
}