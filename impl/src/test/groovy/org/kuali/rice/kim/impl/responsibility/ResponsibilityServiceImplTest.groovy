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
import org.kuali.rice.kim.api.common.attribute.KimAttributeData
import org.kuali.rice.kim.api.common.template.Template
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService
import org.kuali.rice.core.api.criteria.CriteriaLookupService
import org.kuali.rice.core.api.criteria.QueryByCriteria
import org.kuali.rice.core.api.criteria.GenericQueryResults
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo
import org.kuali.rice.kim.api.role.RoleService

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

    @BeforeClass
    static void createSampleBOs() {
        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo firstResponsibilityBo = new ResponsibilityBo(id: "respidone", namespaceCode: "namespacecodeone", name: "respnameone", template: firstResponsibilityTemplate, versionNumber: 1);
        KimTypeBo firstKimTypeBo = new KimTypeBo(id: "kimtypeidone");
        RoleResponsibilityBo firstRoleResponsibilityBo = new RoleResponsibilityBo(roleId: "rolerespidone");

        ResponsibilityTemplateBo secondResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidtwo", name: "resptemplatetwo", namespaceCode: "respnamespacecodetwo", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo secondResponsibilityBo = new ResponsibilityBo(id: "respidtwo", namespaceCode: "namespacecodetwo", name: "respnametwo", template: secondResponsibilityTemplate, versionNumber: 1);
        KimTypeBo secondKimTypeBo = new KimTypeBo(id: "kimtypeidtwo");
        RoleResponsibilityBo secondRoleResponsibilityBo = new RoleResponsibilityBo(roleId: "rolerespidtwo");

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
}
