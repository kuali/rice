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
package org.kuali.rice.krms.impl.repository

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krms.api.repository.action.ActionDefinition
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract
import org.kuali.rice.krms.api.repository.context.ContextDefinition
import org.kuali.rice.krms.api.repository.context.ContextDefinitionContract

import static org.kuali.rice.krms.impl.repository.RepositoryTestUtils.buildQueryResults

class ContextBoServiceImplTest {
    private static final String ID1 = "ID01";
    private static final String NAMESPACE1 = "NAMESPACE01";
    private static final String TYPE_ID1 = "TYPE01";
    private static final String NAME1 = "NAME01";
    private static final String DESCRIPTION1 = "DESCRIPTION01";
    private static final String ID2 = "ID02";
    private static final String NAMESPACE2 = "NAMESPACE02";
    private static final String TYPE_ID2 = "TYPE02";
    private static final String NAME2 = "NAME02";
    private static final String DESCRIPTION2 = "DESCRIPTION02";
    private static final String ID3 = "ID03";
    private static final String NAMESPACE3 = "NAMESPACE03";
    private static final String TYPE_ID3 = "TYPE03";
    private static final String NAME3 = "NAME03";
    private static final String DESCRIPTION3 = "DESCRIPTION03";
    def mockDataObjectService

    @Before
    void setupBoServiceMockContext() {
        mockDataObjectService = new MockFor(DataObjectService.class)
    }

    @Test
    void test_updateAction_success() {
        ContextBo data1 = ContextBo.from(create(ID1, NAMESPACE1, NAME1, TYPE_ID1, DESCRIPTION1));
        ContextBo data2 = ContextBo.from(create(ID2, NAMESPACE2, NAME2, TYPE_ID2, DESCRIPTION2));
        ContextDefinition data3 = create(ID3, NAMESPACE3, NAME3, TYPE_ID3, DESCRIPTION3);

        mockDataObjectService.demand.find(1..1) { clazz, id -> data1}
        mockDataObjectService.demand.deleteMatching(1) { clazz, map -> }
        mockDataObjectService.demand.save { bo, po -> data2}

        DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
        ContextBoService service = new ContextBoServiceImpl()
        service.setDataObjectService(dataObjectService)

        KrmsAttributeDefinitionService kads = new KrmsAttributeDefinitionServiceImpl();
        kads.setDataObjectService(dataObjectService)
        KrmsRepositoryServiceLocator.setKrmsAttributeDefinitionService(kads)

        def updatedData = service.updateContext(data3)

        Assert.assertNotNull(updatedData)
        Assert.assertNotNull(updatedData.getId())
        Assert.assertNotNull(updatedData.getName())
        Assert.assertNotNull(updatedData.getNamespace())
        Assert.assertNotNull(updatedData.getTypeId())
        mockDataObjectService.verify(dataObjectService)
    }

    private ContextDefinition create(final String id,final String namespace,final String name,final String typeId,final String description){
        return ContextDefinition.Builder.create(new ContextDefinitionContract() {
            @Override
            public String getNamespace() {
                return namespace;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getTypeId() {
                return typeId;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public List<? extends AgendaDefinitionContract> getAgendas() {
                return new ArrayList<AgendaDefinitionContract>();
            }

            @Override
            public Map<String, String> getAttributes() {
                return new HashMap<String, String>();
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public Long getVersionNumber() {
                return null;
            }
        }).build();
    }
}
