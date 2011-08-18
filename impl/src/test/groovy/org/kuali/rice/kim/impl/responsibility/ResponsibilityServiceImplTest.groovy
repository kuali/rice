package org.kuali.rice.kim.impl.responsibility

import org.junit.Test
import org.kuali.rice.kim.api.responsibility.ResponsibilityService
import org.kuali.rice.kim.api.responsibility.Responsibility
import org.junit.Before
import groovy.mock.interceptor.MockFor
import org.kuali.rice.krad.service.BusinessObjectService
import org.junit.BeforeClass
import org.kuali.rice.core.api.exception.RiceIllegalStateException
import org.junit.Assert
import org.kuali.rice.kim.api.common.template.Template
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService
import org.kuali.rice.core.api.criteria.CriteriaLookupService
import org.kuali.rice.core.api.criteria.QueryByCriteria
import org.kuali.rice.core.api.criteria.GenericQueryResults
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo
import org.kuali.rice.kim.api.role.RoleService
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction
import org.kuali.rice.kim.api.role.RoleMembership
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults
import org.kuali.rice.core.api.criteria.Predicate

import static org.kuali.rice.core.api.criteria.PredicateFactory.*
import org.kuali.rice.core.api.criteria.LookupCustomizer
import org.kuali.rice.kim.api.common.template.TemplateQueryResults
import org.kuali.rice.kim.impl.type.KimTypeAttributeBo
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo
import org.junit.Ignore;

/*
 * Copyright 2007-2009 The Kuali Foundation
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
class ResponsibilityServiceImplTest {
    private MockFor mockBoService;
    private MockFor mockKimTypeInfoService;
    private MockFor mockResponsibilityTypeService;
    private MockFor mockCriteriaLookupService;
    private MockFor mockRoleService;
    private BusinessObjectService boService;
    private KimTypeInfoService kimTypeInfoService;
    private ResponsibilityTypeService responsibilityTypeService;
    private CriteriaLookupService criteriaLookupService;
    private RoleService roleService;
    ResponsibilityService responsibilityService;
    ResponsibilityServiceImpl responsibilityServiceImpl;
    static Map<String, ResponsibilityBo> sampleResponsibilities = new HashMap<String, ResponsibilityBo>();
    static Map<String, ResponsibilityTemplateBo> sampleTemplates = new HashMap<String, ResponsibilityTemplateBo>();
    static Map<String, KimTypeBo> sampleKimTypes = new HashMap<String, KimTypeBo>();
    static Map<String, RoleResponsibilityBo> sampleRoleResponsibilities = new HashMap<String, RoleResponsibilityBo>();
    static Map<String, RoleResponsibilityActionBo> sampleRoleResponsibilityActions = new HashMap<String, RoleResponsibilityActionBo>();

    @BeforeClass
    static void createSampleBOs() {
        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        List<KimTypeAttributeBo> kimTypeAttributes = new ArrayList<KimTypeAttributeBo>();
        kimTypeAttributes.add(new KimTypeAttributeBo(id: "kimtypeattidone", versionNumber: 1));
        List<ResponsibilityAttributeBo> responsibilityAttributes = new ArrayList<ResponsibilityAttributeBo>();
        KimTypeBo firstKimType = new KimTypeBo(id: "kimtypeidone", versionNumber: 1, attributeDefinitions: kimTypeAttributes);
        KimAttributeBo firstKimAttributeBo = new KimAttributeBo(id: "kimattidone", versionNumber: 1, attributeName: "kimattnameone");
        responsibilityAttributes.add(new ResponsibilityAttributeBo(id: "resattributeidone", versionNumber: 1, kimType: firstKimType, kimAttributeId: "kimattidone", kimAttribute: firstKimAttributeBo, attributeValue: "kimattvalueone"));
        ResponsibilityBo firstResponsibilityBo = new ResponsibilityBo(id: "respidone", namespaceCode: "namespacecodeone", name: "respnameone", template: firstResponsibilityTemplate, versionNumber: 1, active: "Y", attributeDetails: responsibilityAttributes);
        KimTypeBo firstKimTypeBo = new KimTypeBo(id: "kimtypeidone");
        RoleResponsibilityBo firstRoleResponsibilityBo = new RoleResponsibilityBo(roleId: "rolerespidone");
        RoleResponsibilityActionBo firstRoleResponsibilityActionBo = new RoleResponsibilityActionBo(id: "rolerespactionidone", versionNumber: 1);

        ResponsibilityTemplateBo secondResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidtwo", name: "resptemplatetwo", namespaceCode: "respnamespacecodetwo", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo secondResponsibilityBo = new ResponsibilityBo(id: "respidtwo", namespaceCode: "namespacecodetwo", name: "respnametwo", template: secondResponsibilityTemplate, versionNumber: 1, active: "Y");
        KimTypeBo secondKimTypeBo = new KimTypeBo(id: "kimtypeidtwo");
        RoleResponsibilityBo secondRoleResponsibilityBo = new RoleResponsibilityBo(roleId: "rolerespidtwo");
        RoleResponsibilityActionBo secondRoleResponsibilityActionBo = new RoleResponsibilityActionBo(id: "rolerespactionidtwo", versionNumber: 1);

        for (bo in [firstResponsibilityBo, secondResponsibilityBo]) {
            sampleResponsibilities.put(bo.id, bo)
        }

        for (bo in [firstResponsibilityTemplate, secondResponsibilityTemplate]) {
            sampleTemplates.put(bo.id, bo)
        }

        for (bo in [firstKimTypeBo, secondKimTypeBo]) {
            sampleKimTypes.put(bo.id, bo)
        }

        for (bo in [firstRoleResponsibilityBo, secondRoleResponsibilityBo]) {
            sampleRoleResponsibilities.put(bo.roleId, bo)
        }

        for (bo in [firstRoleResponsibilityActionBo, secondRoleResponsibilityActionBo]) {
            sampleRoleResponsibilityActions.put(bo.id, bo)
        }
    }

    @Before
    void setupMockContext() {
        mockBoService = new MockFor(BusinessObjectService.class);
        mockKimTypeInfoService = new MockFor(KimTypeInfoService.class);
        mockResponsibilityTypeService = new MockFor(ResponsibilityTypeService.class);
        mockCriteriaLookupService = new MockFor(CriteriaLookupService.class);
        mockRoleService = new MockFor(RoleService.class);
    }

    @Before
    void setupServiceUnderTest() {
        responsibilityServiceImpl = new ResponsibilityServiceImpl()
        responsibilityService = responsibilityServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectBusinessObjectServiceIntoResponsibilityService() {
        boService = mockBoService.proxyDelegateInstance();
        responsibilityServiceImpl.setBusinessObjectService(boService);
    }

    void injectKimTypeInfoServiceIntoResponsibilityService() {
        kimTypeInfoService = mockKimTypeInfoService.proxyDelegateInstance();
        responsibilityServiceImpl.setKimTypeInfoService(kimTypeInfoService);
    }

    void injectResponsibilityTypeServiceIntoResponsibilityService() {
        responsibilityTypeService = mockResponsibilityTypeService.proxyDelegateInstance();
        responsibilityServiceImpl.setDefaultResponsibilityTypeService(responsibilityTypeService);
    }

    void injectCriteriaLookupServiceIntoResponsibilityService() {
        criteriaLookupService = mockCriteriaLookupService.proxyDelegateInstance();
        responsibilityServiceImpl.setCriteriaLookupService(criteriaLookupService);
    }

    void injectRoleServiceIntoResponsibilityService() {
        roleService = mockRoleService.proxyDelegateInstance();
        responsibilityServiceImpl.setRoleService(roleService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateResponsibilityWithNullFails() {
        Responsibility responsibility = responsibilityService.createResponsibility(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testCreateResponsibilityWithExistingResponsibilityFails() {
        mockBoService.demand.findBySinglePrimaryKey(1..1) {
            Class clazz, Object primaryKey -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.id.equals(primaryKey))
                {
                    return responsibilityBo;
                }
            }
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo responsibilityBo = new ResponsibilityBo(id: "respidone", namespaceCode: "namespacecodeone", name: "respnameone", template: firstResponsibilityTemplate, versionNumber: 1);
        responsibilityService.createResponsibility(ResponsibilityBo.to(responsibilityBo));
    }

    @Test
    public void testCreateResponsibilitySucceeds() {
        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo newResponsibilityBo = new ResponsibilityBo(id: "respidthree", namespaceCode: "namespacecodethree", name: "respnamethree", template: firstResponsibilityTemplate, versionNumber: 1);

        mockBoService.demand.findBySinglePrimaryKey(1..1) {
            Class clazz, Object primaryKey -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.id.equals(primaryKey))
                {
                    return responsibilityBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            ResponsibilityBo bo -> return newResponsibilityBo;
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        Responsibility responsibility = responsibilityService.createResponsibility(ResponsibilityBo.to(newResponsibilityBo));

        Assert.assertEquals(ResponsibilityBo.to(newResponsibilityBo), responsibility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateResponsibilityWithNullFails() {
        Responsibility responsibility = responsibilityService.updateResponsibility(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateResponsibilityWithNonExistingObjectFails() {
        mockBoService.demand.findBySinglePrimaryKey(1..1) {
            Class clazz, Object primaryKey -> return null;
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo newResponsibilityBo = new ResponsibilityBo(id: "respidthree", namespaceCode: "namespacecodethree", name: "respnamethree", template: firstResponsibilityTemplate, versionNumber: 1);
        Responsibility responsibility = responsibilityService.updateResponsibility(ResponsibilityBo.to(newResponsibilityBo));
    }

    @Test
    @Ignore
    public void testUpdateResponsibilitySucceeds() {
        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        List<KimTypeAttributeBo> kimTypeAttributes = new ArrayList<KimTypeAttributeBo>();
        kimTypeAttributes.add(new KimTypeAttributeBo(id: "kimtypeattidone", versionNumber: 1));
        List<ResponsibilityAttributeBo> responsibilityAttributes = new ArrayList<ResponsibilityAttributeBo>();
        KimTypeBo firstKimType = new KimTypeBo(id: "kimtypeidone", versionNumber: 1, attributeDefinitions: kimTypeAttributes);
        KimAttributeBo firstKimAttributeBo = new KimAttributeBo(id: "kimattidone", versionNumber: 1, attributeName: "kimattnameone");
        responsibilityAttributes.add(new ResponsibilityAttributeBo(id: "resattributeidone", versionNumber: 1, kimType: firstKimType, kimAttributeId: "kimattidone", kimAttribute: firstKimAttributeBo, attributeValue: "kimattvalueone"));
        ResponsibilityBo existingResponsibilityBo = new ResponsibilityBo(id: "respidone", namespaceCode: "namespacecodeone", name: "respnameone", template: firstResponsibilityTemplate, versionNumber: 1, active: "Y", attributeDetails: responsibilityAttributes);

        mockBoService.demand.findBySinglePrimaryKey(1..1) {
            Class clazz, Object primaryKey -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.id.equals(primaryKey))
                {
                    return responsibilityBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            ResponsibilityBo bo -> return existingResponsibilityBo;
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        Responsibility responsibility = responsibilityService.updateResponsibility(ResponsibilityBo.to(existingResponsibilityBo));

        Assert.assertEquals(ResponsibilityBo.to(existingResponsibilityBo), responsibility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityWithNullFails() {
        Responsibility responsibility = responsibilityService.getResponsibility(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityWithBlankFails() {
        Responsibility responsibility = responsibilityService.getResponsibility("");
    }

    @Test
    public void testGetResponsibilitySucceeds() {
        mockBoService.demand.findBySinglePrimaryKey(1..sampleResponsibilities.size()) {
            Class clazz, Object primaryKey -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.id.equals(primaryKey))
                {
                    return responsibilityBo;
                }
            }
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
            Assert.assertEquals(ResponsibilityBo.to(responsibilityBo), responsibilityService.getResponsibility(responsibilityBo.id))
        }

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespByNamespaceCodeAndNameWithNullNamespaceCodeFails() {
        Responsibility responsibility = responsibilityService.findRespByNamespaceCodeAndName(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespByNamespaceCodeAndNameWithNullNameFails() {
        Responsibility responsibility = responsibilityService.findRespByNamespaceCodeAndName("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespByNamespaceCodeAndNameWithBlankNamespaceCodeFails() {
        Responsibility responsibility = responsibilityService.findRespByNamespaceCodeAndName("", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespByNamespaceCodeAndNameWithBlankNameFails() {
        Responsibility responsibility = responsibilityService.findRespByNamespaceCodeAndName("test", "");
    }

    @Test
    public void testFindRespByNamespaceCodeAndNameSucceeds() {
        mockBoService.demand.findMatching(1..sampleResponsibilities.size()) {
            Class clazz, Map map -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityBo.name.equals(map.get("name")))
                {
                    Collection<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
                    responsibilities.add(responsibilityBo);
                    return responsibilities;
                }
            }
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
            Assert.assertEquals(ResponsibilityBo.to(responsibilityBo), responsibilityService.findRespByNamespaceCodeAndName(responsibilityBo.namespaceCode, responsibilityBo.name))
        }

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityTemplateWithNullFails() {
        Template template = responsibilityService.getResponsibilityTemplate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityTemplateWithBlankFails() {
        Template template = responsibilityService.getResponsibilityTemplate("");
    }

    @Test
    public void testGetResponsibilityTemplateSucceeds() {
        mockBoService.demand.findBySinglePrimaryKey(1..sampleTemplates.size()) {
            Class clazz, Object primaryKey -> for (ResponsibilityTemplateBo responsibilityTemplateBo in sampleTemplates.values()) {
                if (responsibilityTemplateBo.id.equals(primaryKey))
                {
                    return responsibilityTemplateBo;
                }
            }
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        for (ResponsibilityTemplateBo responsibilityTemplateBo in sampleTemplates.values()) {
            Assert.assertEquals(ResponsibilityTemplateBo.to(responsibilityTemplateBo), responsibilityService.getResponsibilityTemplate(responsibilityTemplateBo.id))
        }

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespTemplateByNamespaceCodeAndNameWithNullNamespaceCodeFails() {
        Template template = responsibilityService.findRespTemplateByNamespaceCodeAndName(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespTemplateByNamespaceCodeAndNameWithNullNameFails() {
        Template template = responsibilityService.findRespTemplateByNamespaceCodeAndName("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespTemplateByNamespaceCodeAndNameWithBlankNamespaceCodeFails() {
        Template template = responsibilityService.findRespTemplateByNamespaceCodeAndName("", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRespTemplateByNamespaceCodeAndNameWithBlankNameFails() {
        Template template = responsibilityService.findRespTemplateByNamespaceCodeAndName("test", "");
    }

    @Test
    public void testFindRespTemplateByNamespaceCodeAndNameSucceeds() {
        mockBoService.demand.findMatching(1..sampleTemplates.size()) {
            Class clazz, Map map -> for (ResponsibilityTemplateBo responsibilityTemplateBo in sampleTemplates.values()) {
                if (responsibilityTemplateBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityTemplateBo.name.equals(map.get("name")))
                {
                    Collection<ResponsibilityTemplateBo> templates = new ArrayList<ResponsibilityTemplateBo>();
                    templates.add(responsibilityTemplateBo);
                    return templates;
                }
            }
        }

        injectBusinessObjectServiceIntoResponsibilityService();

        for (ResponsibilityTemplateBo responsibilityTemplateBo in sampleTemplates.values()) {
            Assert.assertEquals(ResponsibilityTemplateBo.to(responsibilityTemplateBo), responsibilityService.findRespTemplateByNamespaceCodeAndName(responsibilityTemplateBo.namespaceCode, responsibilityTemplateBo.name))
        }

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithNullPrincipalIdFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility(null, "test", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithBlankPrincipalIdFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("", "test", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithNullNamespaceCodeFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", null, "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithBlankNamespaceCodeFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", "", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithNullRespNameFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", "test", null, new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithBlankRespNameFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", "test", "", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithNullQualificationFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", "test", "test", null, new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityWithNullResponsibilityDetailsFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibility("test", "test", "test", new HashMap<String, String>(), null);
    }

    @Test
    public void testHasResponsibilitySucceeds() {
        mockBoService.demand.findMatching(1..1) {
            Class clazz, Map map -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityBo.name.equals(map.get("name")))
                {
                    Collection<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
                    responsibilities.add(responsibilityBo);
                    return responsibilities;
                }
            }
        }

        mockKimTypeInfoService.demand.getKimType(1..1) {
            String id -> return KimTypeBo.to(sampleKimTypes.get("kimtypeidone"));
        }

        mockResponsibilityTypeService.demand.getMatchingResponsibilities(1..1) {
            Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList ->
                List<Responsibility> responsibilities = new ArrayList<Responsibility>();
                responsibilities.add(ResponsibilityBo.to(sampleResponsibilities.get("respidone")));
                return responsibilities;
        }

        GenericQueryResults.Builder<RoleResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        roleResponsibilities.add(sampleRoleResponsibilities.get("rolerespidone"));
        genericQueryResults.results = roleResponsibilities;
        GenericQueryResults<RoleResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        mockRoleService.demand.principalHasRole(1..1) {
            String principalId, List<String> roleIds, Map<String, String> qualification -> return true;
        }

        injectBusinessObjectServiceIntoResponsibilityService();
        injectKimTypeInfoServiceIntoResponsibilityService();
        injectResponsibilityTypeServiceIntoResponsibilityService();
        injectCriteriaLookupServiceIntoResponsibilityService();
        injectRoleServiceIntoResponsibilityService();

        Map<String, String> responsibilityDetails = new HashMap<String, String>();
        responsibilityDetails.put("test", "test");
        boolean hasResponsibility = responsibilityService.hasResponsibility("principalid", "namespacecodeone", "respnameone", new HashMap<String, String>(), responsibilityDetails);

        Assert.assertEquals(true, hasResponsibility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithNullPrincipalIdFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName(null, "test", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithBlankPrincipalIdFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("", "test", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithNullNamespaceCodeFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", null, "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithBlankNamespaceCodeFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", "", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithNullRespTemplateNameFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", "test", null, new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithBlankRespTemplateNameFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", "test", "", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithNullQualificationFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", "test", "test", null, new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasResponsibilityByTemplateNameWithNullResponsibilityDetailsFails() {
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("test", "test", "test", new HashMap<String, String>(), null);
    }

    @Test
    public void testHasResponsibilityByTemplateNameSucceeds() {
        mockBoService.demand.findMatching(1..1) {
            Class clazz, Map map -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityBo.template.name.equals(map.get("template.name")))
                {
                    Collection<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
                    responsibilities.add(responsibilityBo);
                    return responsibilities;
                }
            }
        }

        mockKimTypeInfoService.demand.getKimType(1..1) {
            String id -> return KimTypeBo.to(sampleKimTypes.get("kimtypeidone"));
        }

        mockResponsibilityTypeService.demand.getMatchingResponsibilities(1..1) {
            Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList ->
                List<Responsibility> responsibilities = new ArrayList<Responsibility>();
                responsibilities.add(ResponsibilityBo.to(sampleResponsibilities.get("respidone")));
                return responsibilities;
        }

        GenericQueryResults.Builder<RoleResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        roleResponsibilities.add(sampleRoleResponsibilities.get("rolerespidone"));
        genericQueryResults.results = roleResponsibilities;
        GenericQueryResults<RoleResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        mockRoleService.demand.principalHasRole(1..1) {
            String principalId, List<String> roleIds, Map<String, String> qualification -> return true;
        }

        injectBusinessObjectServiceIntoResponsibilityService();
        injectKimTypeInfoServiceIntoResponsibilityService();
        injectResponsibilityTypeServiceIntoResponsibilityService();
        injectCriteriaLookupServiceIntoResponsibilityService();
        injectRoleServiceIntoResponsibilityService();

        Map<String, String> responsibilityDetails = new HashMap<String, String>();
        responsibilityDetails.put("test", "test");
        boolean hasResponsibility = responsibilityService.hasResponsibilityByTemplateName("principalid", "namespacecodeone", "resptemplateone", new HashMap<String, String>(), responsibilityDetails);

        Assert.assertEquals(true, hasResponsibility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithNullNamespaceCodeFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions(null, "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithBlankNamespaceCodeFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithNullResponsibilityNameFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("test", null, new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithBlankResponsibilityNameFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("test", "", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithNullQualificationFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("test", "test", null, new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsWithNullResponsibilityDetailsFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("test", "test", new HashMap<String, String>(), null);
    }

    @Test
    public void testGetResponsibilityActionsSucceeds() {
        mockBoService.demand.findMatching(1..1) {
            Class clazz, Map map -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityBo.name.equals(map.get("name")))
                {
                    Collection<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
                    responsibilities.add(responsibilityBo);
                    return responsibilities;
                }
            }
        }

        GenericQueryResults.Builder<RoleResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        roleResponsibilities.add(sampleRoleResponsibilities.get("rolerespidone"));
        genericQueryResults.results = roleResponsibilities;
        GenericQueryResults<RoleResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        mockRoleService.demand.getRoleMembers(1..1) {
            List<String> roleIds, Map<String, String> qualification ->
                List<RoleMembership> roleMemberships = new ArrayList<RoleMembership>();
                RoleMembership.Builder builder = new RoleMembership.Builder();
                builder.memberId = "memberidone";
                builder.embeddedRoleId = "embeddedroleidone";
                builder.roleId = "roleidone";
                builder.qualifier = new HashMap<>();
                builder.memberTypeCode = "P";
                builder.roleMemberId = "rolememberidone";
                roleMemberships.add(builder.build());
                return roleMemberships;
        }

        GenericQueryResults.Builder<RoleResponsibilityActionBo> actionGenericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        actionGenericQueryResults.totalRowCount = 1;
        actionGenericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityActionBo> roleResponsibilityActions = new ArrayList<RoleResponsibilityActionBo>();
        roleResponsibilityActions.add(sampleRoleResponsibilityActions.get("rolerespactionidone"));
        actionGenericQueryResults.results = roleResponsibilityActions;
        GenericQueryResults<RoleResponsibilityActionBo> actionResults = actionGenericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityActionBo> queryClass, QueryByCriteria criteria -> return actionResults;
        }

        injectBusinessObjectServiceIntoResponsibilityService();
        injectCriteriaLookupServiceIntoResponsibilityService();
        injectRoleServiceIntoResponsibilityService();

        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActions("namespacecodeone", "respnameone", new HashMap<String, String>(), new HashMap<String, String>());

        Assert.assertEquals("memberidone", responsibilityActions[0].principalId);
        Assert.assertEquals("rolerespactionidone", responsibilityActions[0].roleResponsibilityActionId);
        Assert.assertEquals("embeddedroleidone", responsibilityActions[0].memberRoleId);
        Assert.assertEquals("respidone", responsibilityActions[0].responsibilityId);
        Assert.assertEquals("roleidone", responsibilityActions[0].roleId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithNullNamespaceCodeFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName(null, "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithBlankNamespaceCodeFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("", "test", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithNullTemplateNameFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("test", null, new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithBlankTemplateNameFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("test", "", new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithNullQualificationFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("test", "test", null, new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponsibilityActionsByTemplateNameWithNullResponsibilityDetailsFails() {
        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("test", "test", new HashMap<String, String>(), null);
    }

    @Test
    public void testGetResponsibilityActionsByTemplateNameSucceeds() {
        mockBoService.demand.findMatching(1..1) {
            Class clazz, Map map -> for (ResponsibilityBo responsibilityBo in sampleResponsibilities.values()) {
                if (responsibilityBo.namespaceCode.equals(map.get("namespaceCode"))
                    && responsibilityBo.template.name.equals(map.get("template.name")))
                {
                    Collection<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
                    responsibilities.add(responsibilityBo);
                    return responsibilities;
                }
            }
        }

        GenericQueryResults.Builder<RoleResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        roleResponsibilities.add(sampleRoleResponsibilities.get("rolerespidone"));
        genericQueryResults.results = roleResponsibilities;
        GenericQueryResults<RoleResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        mockRoleService.demand.getRoleMembers(1..1) {
            List<String> roleIds, Map<String, String> qualification ->
                List<RoleMembership> roleMemberships = new ArrayList<RoleMembership>();
                RoleMembership.Builder builder = new RoleMembership.Builder();
                builder.memberId = "memberidone";
                builder.embeddedRoleId = "embeddedroleidone";
                builder.roleId = "roleidone";
                builder.qualifier = new HashMap<>();
                builder.memberTypeCode = "P";
                builder.roleMemberId = "rolememberidone";
                roleMemberships.add(builder.build());
                return roleMemberships;
        }

        GenericQueryResults.Builder<RoleResponsibilityActionBo> actionGenericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        actionGenericQueryResults.totalRowCount = 1;
        actionGenericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityActionBo> roleResponsibilityActions = new ArrayList<RoleResponsibilityActionBo>();
        roleResponsibilityActions.add(sampleRoleResponsibilityActions.get("rolerespactionidone"));
        actionGenericQueryResults.results = roleResponsibilityActions;
        GenericQueryResults<RoleResponsibilityActionBo> actionResults = actionGenericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityActionBo> queryClass, QueryByCriteria criteria -> return actionResults;
        }

        injectBusinessObjectServiceIntoResponsibilityService();
        injectCriteriaLookupServiceIntoResponsibilityService();
        injectRoleServiceIntoResponsibilityService();

        List<ResponsibilityAction> responsibilityActions = responsibilityService.getResponsibilityActionsByTemplateName("namespacecodeone", "resptemplateone", new HashMap<String, String>(), new HashMap<String, String>());

        Assert.assertEquals("memberidone", responsibilityActions[0].principalId);
        Assert.assertEquals("rolerespactionidone", responsibilityActions[0].roleResponsibilityActionId);
        Assert.assertEquals("embeddedroleidone", responsibilityActions[0].memberRoleId);
        Assert.assertEquals("respidone", responsibilityActions[0].responsibilityId);
        Assert.assertEquals("roleidone", responsibilityActions[0].roleId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRoleIdsForResponsibilityWithNullIdFails() {
        List<String> roleIds = responsibilityService.getRoleIdsForResponsibility(null, new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRoleIdsForResponsibilityWithBlankIdFails() {
        List<String> roleIds = responsibilityService.getRoleIdsForResponsibility("", new HashMap<String, String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRoleIdsForResponsibilityWithNullQualificationFails() {
        List<String> roleIds = responsibilityService.getRoleIdsForResponsibility("test", null);
    }

    @Test
    public void testGetRoleIdsForResponsibilitySucceeds() {
        GenericQueryResults.Builder<RoleResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<RoleResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        roleResponsibilities.add(sampleRoleResponsibilities.get("rolerespidone"));
        genericQueryResults.results = roleResponsibilities;
        GenericQueryResults<RoleResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<RoleResponsibilityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        injectCriteriaLookupServiceIntoResponsibilityService();

        List<String> roleIds = responsibilityService.getRoleIdsForResponsibility("respidone", new HashMap<String, String>());

        Assert.assertEquals("rolerespidone", roleIds[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindResponsibilitiesWithNullFails() {
        ResponsibilityQueryResults responsibilityQueryResults = responsibilityService.findResponsibilities(null);
    }

    @Test
    public void testFindResponsibilitiesSucceeds() {
        Predicate p = equal("id", "respidone");

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(p);

        GenericQueryResults.Builder<ResponsibilityBo> genericQueryResults = new GenericQueryResults.Builder<ResponsibilityBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<ResponsibilityBo> responsibilities = new ArrayList<ResponsibilityBo>();
        responsibilities.add(sampleResponsibilities.get("respidone"));
        genericQueryResults.results = responsibilities;
        GenericQueryResults<ResponsibilityBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<ResponsibilityBo> queryClass, QueryByCriteria criteria, LookupCustomizer<ResponsibilityBo> customizer -> return results;
        }

        injectCriteriaLookupServiceIntoResponsibilityService();

        ResponsibilityQueryResults responsibilityQueryResults = responsibilityService.findResponsibilities(builder.build());
        List<Responsibility> actualResponsibilities = responsibilityQueryResults.getResults();

        Assert.assertEquals("respidone", actualResponsibilities[0].id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindResponsibilityTemplatesWithNullFails() {
        TemplateQueryResults templateQueryResults = responsibilityService.findResponsibilityTemplates(null);
    }

    @Test
    public void testFindResponsibilityTemplatesSucceeds() {
        Predicate p = equal("id", "resptemplateidone");

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(p);

        GenericQueryResults.Builder<ResponsibilityTemplateBo> genericQueryResults = new GenericQueryResults.Builder<ResponsibilityTemplateBo>();
        genericQueryResults.totalRowCount = 1;
        genericQueryResults.moreResultsAvailable = false;
        List<ResponsibilityTemplateBo> responsibilityTemplates = new ArrayList<ResponsibilityTemplateBo>();
        responsibilityTemplates.add(sampleTemplates.get("resptemplateidone"));
        genericQueryResults.results = responsibilityTemplates;
        GenericQueryResults<ResponsibilityTemplateBo> results = genericQueryResults.build();

        mockCriteriaLookupService.demand.lookup(1..1) {
            Class<ResponsibilityTemplateBo> queryClass, QueryByCriteria criteria -> return results;
        }

        injectCriteriaLookupServiceIntoResponsibilityService();

        TemplateQueryResults templateQueryResults = responsibilityService.findResponsibilityTemplates(builder.build());
        List<Template> actualTemplates = templateQueryResults.getResults();
        Assert.assertEquals("resptemplateidone", actualTemplates[0].id);
    }
}
