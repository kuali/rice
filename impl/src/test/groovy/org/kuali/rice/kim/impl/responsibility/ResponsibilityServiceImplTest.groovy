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
    private BusinessObjectService boService;
    ResponsibilityService responsibilityService;
    ResponsibilityServiceImpl responsibilityServiceImpl;
    static Map<String, ResponsibilityBo> sampleResponsibilities = new HashMap<String, ResponsibilityBo>();

    @BeforeClass
    static void createSampleBOs() {
        ResponsibilityTemplateBo firstResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidone", name: "resptemplateone", namespaceCode: "respnamespacecodeone", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo firstResponsibilityBo = new ResponsibilityBo(id: "respidone", namespaceCode: "namespacecodeone", name: "respnameone", template: firstResponsibilityTemplate, versionNumber: 1);

        ResponsibilityTemplateBo secondResponsibilityTemplate = new ResponsibilityTemplateBo(id: "resptemplateidtwo", name: "resptemplatetwo", namespaceCode: "respnamespacecodetwo", versionNumber: 1, kimTypeId: "a");
        ResponsibilityBo secondResponsibilityBo = new ResponsibilityBo(id: "respidtwo", namespaceCode: "namespacecodetwo", name: "respnametwo", template: secondResponsibilityTemplate, versionNumber: 1);

        for (bo in [firstResponsibilityBo, secondResponsibilityBo]) {
            sampleResponsibilities.put(bo.id, bo)
        }
    }

    @Before
    void setupMockContext() {
        mockBoService = new MockFor(BusinessObjectService.class);
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
}
