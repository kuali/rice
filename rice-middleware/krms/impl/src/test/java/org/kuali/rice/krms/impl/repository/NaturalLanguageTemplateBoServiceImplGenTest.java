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
package org.kuali.rice.krms.impl.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class NaturalLanguageTemplateBoServiceImplGenTest {
    private final static String ID = "ID";
    private final static String LANGUAGE_CODE = "NL";
    private final static String NATURAL_LANGUAGE_USAGE_ID = "NATURAL_LANGUAGE_USAGE_ID";
    private final static String TEMPLATE = "TEMPLATE";
    private final static String TYPE_ID = "TYPE_ID";
    private NaturalLanguageTemplateBoServiceImpl naturalLanguageTemplateBoServiceImpl;
    private NaturalLanguageTemplate naturalLanguageTemplate;
    private KrmsAttributeDefinitionService krmsAttributeDefinitionService;
    @Mock private KrmsAttributeDefinitionService mockAttributeService;
    @Mock private KrmsTypeRepositoryService mockTypeRepositoryService;
    @Mock private DataObjectService mockDataObjectService;

    public NaturalLanguageTemplateBoServiceImplGenTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() {
        naturalLanguageTemplateBoServiceImpl = new NaturalLanguageTemplateBoServiceImpl();
        NaturalLanguageTemplateBo.setAttributeDefinitionService(mockAttributeService);
        NaturalLanguageTemplateBo.setTypeRepositoryService(mockTypeRepositoryService);
        naturalLanguageTemplateBoServiceImpl.setAttributeDefinitionService(mockAttributeService);
        naturalLanguageTemplateBoServiceImpl.setDataObjectService(mockDataObjectService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getNaturalLanguageTemplatesByAttributes_null_fail() {
        naturalLanguageTemplateBoServiceImpl.findNaturalLanguageTemplatesByAttributes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getNaturalLanguageTemplatesByLanguageCode_null_fail() {
        naturalLanguageTemplateBoServiceImpl.findNaturalLanguageTemplatesByLanguageCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getNaturalLanguageTemplatesByNaturalLanguageUsage_null_fail() {
        naturalLanguageTemplateBoServiceImpl.findNaturalLanguageTemplatesByNaturalLanguageUsage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getNaturalLanguageTemplatesByType_null_fail() {
        naturalLanguageTemplateBoServiceImpl.findNaturalLanguageTemplatesByType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getNaturalLanguageTemplatesByTemplate_null_fail() {
        naturalLanguageTemplateBoServiceImpl.findNaturalLanguageTemplatesByTemplate(null);
    }

    @Test
    public void test_from_null_yields_null() {
        assertNull(naturalLanguageTemplateBoServiceImpl.from(null));
    }

    @Test
    public void test_from() {
        NaturalLanguageTemplate def = buildFullNaturalLanguageTemplate();
        NaturalLanguageTemplateBo naturalLanguageTemplateBo = naturalLanguageTemplateBoServiceImpl.from(def);
        assert(naturalLanguageTemplateBo.getLanguageCode().equals(def.getLanguageCode()));
        assert(naturalLanguageTemplateBo.getNaturalLanguageUsageId().equals(def.getNaturalLanguageUsageId()));
        assert(naturalLanguageTemplateBo.getTypeId().equals(def.getTypeId()));
        assert(naturalLanguageTemplateBo.getTemplate().equals(def.getTemplate()));
        assert(naturalLanguageTemplateBo.getId().equals(def.getId()));
    }

    @Test
    public void test_to() {
        NaturalLanguageTemplate def = buildFullNaturalLanguageTemplate();
        NaturalLanguageTemplateBo naturalLanguageTemplateBo = naturalLanguageTemplateBoServiceImpl.from(def);
        NaturalLanguageTemplate def2 = NaturalLanguageTemplateBo.to(naturalLanguageTemplateBo);
        assert(def.equals(def2));
    }

    @Test
    public void test_createNaturalLanguageTemplate() {
        NaturalLanguageTemplate def = buildFullNaturalLanguageTemplate();
        naturalLanguageTemplate = naturalLanguageTemplateBoServiceImpl.createNaturalLanguageTemplate(def);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createNaturalLanguageTemplate_null_fail() {
        naturalLanguageTemplateBoServiceImpl.createNaturalLanguageTemplate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateNaturalLanguageTemplate_null_fail() {
        naturalLanguageTemplateBoServiceImpl.updateNaturalLanguageTemplate(null);
    }

    @Test
    public void test_updateNaturalLanguageTemplate_success() {
        NaturalLanguageTemplate data1 = buildFullNaturalLanguageTemplate(LANGUAGE_CODE, NATURAL_LANGUAGE_USAGE_ID, TEMPLATE, TYPE_ID);
        NaturalLanguageTemplate data2 = buildFullNaturalLanguageTemplate(LANGUAGE_CODE, NATURAL_LANGUAGE_USAGE_ID, TEMPLATE, TYPE_ID);
        NaturalLanguageTemplateBo data3 = naturalLanguageTemplateBoServiceImpl.from(data1);
        NaturalLanguageTemplateBo data4 = naturalLanguageTemplateBoServiceImpl.from(data2);

        when(mockDataObjectService.find(any(Class.class),any(String.class))).thenReturn(data3);
        when(mockDataObjectService.save(any(NaturalLanguageTemplateBo.class), eq(PersistenceOption.FLUSH))).thenReturn(data4);

        NaturalLanguageTemplate updatedData = naturalLanguageTemplateBoServiceImpl.updateNaturalLanguageTemplate(data2);

        assertNotNull(updatedData);
        assertNotNull(updatedData.getLanguageCode());
        assertNotNull(updatedData.getNaturalLanguageUsageId());
        assertNotNull(updatedData.getTypeId());
        assertNotNull(updatedData.getTemplate());
        assertNotNull(updatedData.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteNaturalLanguageTemplate_null_fail() {
        naturalLanguageTemplateBoServiceImpl.deleteNaturalLanguageTemplate(null);
    }

    void createNaturalLanguageTemplate(NaturalLanguageUsage naturalLanguageUsage, KrmsTypeDefinition type) {
        NaturalLanguageTemplate def = buildFullNaturalLanguageTemplate(naturalLanguageUsage, type);
        for (Map.Entry<String, String> attributeEntry : def.getAttributes().entrySet()) {
            // check for template attribute definition, create if not there
            KrmsAttributeDefinition attrDef = krmsAttributeDefinitionService.getAttributeDefinitionByNameAndNamespace(attributeEntry.getKey(), type.getNamespace());
            // rebuild attributes in all cases until Constraint Error found and corrected
            KrmsAttributeDefinition.Builder attrDefBuilder = KrmsAttributeDefinition.Builder.create(null, attributeEntry.getKey(), type.getNamespace());
            krmsAttributeDefinitionService.createAttributeDefinition(attrDefBuilder.build());
        }

        naturalLanguageTemplate = naturalLanguageTemplateBoServiceImpl.createNaturalLanguageTemplate(def);
    }

    public NaturalLanguageTemplate getNaturalLanguageTemplate() {
        return naturalLanguageTemplate;
    }

    public void setNaturalLanguageTemplateBoServiceImpl(NaturalLanguageTemplateBoServiceImpl impl) {
        this.naturalLanguageTemplateBoServiceImpl = impl;
    }

    public void setKrmsAttributeDefinitionService(KrmsAttributeDefinitionService impl) {
        krmsAttributeDefinitionService = impl;
    }

    public static NaturalLanguageTemplateBoServiceImplGenTest create(NaturalLanguageTemplateBoServiceImpl nlTemplateBoService, KrmsAttributeDefinitionService attributeDefService) {
        NaturalLanguageTemplateBoServiceImplGenTest test = new NaturalLanguageTemplateBoServiceImplGenTest();
        test.setKrmsAttributeDefinitionService(attributeDefService);
        test.setNaturalLanguageTemplateBoServiceImpl(nlTemplateBoService);
        return test;
    }

    public static NaturalLanguageTemplate buildFullNaturalLanguageTemplate() {
        return buildFullNaturalLanguageTemplate(LANGUAGE_CODE, NATURAL_LANGUAGE_USAGE_ID, TEMPLATE, TYPE_ID);
    }

    public static NaturalLanguageTemplate buildFullNaturalLanguageTemplate(String languageCode, String naturalLanguageUsageId, String template, String typeId) {
        NaturalLanguageTemplate.Builder builder = NaturalLanguageTemplate.Builder.create(languageCode, naturalLanguageUsageId, template, typeId);
        builder.setId(ID);
        NaturalLanguageTemplate naturalLanguageTemplate = builder.build();
        return naturalLanguageTemplate;
    }

    public static NaturalLanguageTemplate buildFullNaturalLanguageTemplate(NaturalLanguageUsage depDef, KrmsTypeDefinition typeDef) { // TODO gen
        NaturalLanguageTemplate.Builder builder = NaturalLanguageTemplate.Builder.create(LANGUAGE_CODE, depDef.getId(), TEMPLATE, typeDef.getId());
        builder.setId(ID);
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("TEMPLATE", "template");
        builder.setAttributes(attributes);
        NaturalLanguageTemplate naturalLanguageTemplate = builder.build();
        return naturalLanguageTemplate;
    }
}
