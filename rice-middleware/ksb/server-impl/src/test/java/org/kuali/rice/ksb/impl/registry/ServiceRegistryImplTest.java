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
package org.kuali.rice.ksb.impl.registry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * A unit test for {@link ServiceRegistryImpl}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceRegistryImplTest {

    @Mock private DataObjectService dataObjectService;

    @InjectMocks private ServiceRegistryImpl serviceRegistryImpl = new ServiceRegistryImpl();
    private ServiceRegistry serviceRegistry = serviceRegistryImpl;

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Test(expected = RiceIllegalArgumentException.class)
    public void testGetAllServicesForInstance_Null() {
        serviceRegistry.getAllServicesForInstance(null);
    }

    @Test(expected = RiceIllegalArgumentException.class)
    public void testGetAllServicesForInstance_Empty() {
        serviceRegistry.getAllServicesForInstance("");
    }

    @Test(expected = RiceIllegalArgumentException.class)
    public void testGetAllServicesForInstance_Blank() {
        serviceRegistry.getAllServicesForInstance(" ");
    }

    @Test
    public void testGetAllServicesForInstance() {
        GenericQueryResults.Builder<ServiceInfoBo> resultBuilder = GenericQueryResults.Builder.<ServiceInfoBo>create();
        for (int i = 0; i < 5; i++) {
            resultBuilder.getResults().add(createServiceInfo("service" + i));
        }
        when(dataObjectService.findMatching(eq(ServiceInfoBo.class), any(QueryByCriteria.class))).thenReturn(resultBuilder.build());

        List<ServiceInfo> serviceInfos = serviceRegistry.getAllServicesForInstance("MyInstance");
        assertEquals(5, serviceInfos.size());
    }

    private static int ID_VALUE = 0;

    private String nextId() {
        ID_VALUE++;
        return ID_VALUE + "";
    }

    private ServiceInfoBo createServiceInfo(String serviceName) {
        ServiceInfoBo serviceInfo = new ServiceInfoBo();
        serviceInfo.setServiceId(nextId());
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setEndpointUrl("localhost");
        serviceInfo.setInstanceId("MyInstance");
        serviceInfo.setApplicationId(nextId());
        serviceInfo.setServerIpAddress("127.0.0.1");
        serviceInfo.setType("type");
        serviceInfo.setServiceVersion("1.0");
        serviceInfo.setStatusCode("A");
        serviceInfo.setServiceDescriptorId(nextId());
        serviceInfo.setChecksum("abcdefg");
        return serviceInfo;
    }



}
