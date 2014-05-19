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
package org.kuali.rice.core.impl.component;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.test.CORETestCase;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.List;

import static org.junit.Assert.*;

/**
 * An integration test which tests the reference implementation of the ComponentService
 *
 * TODO - for now this test is part of KRAD even though it should be part of the core (pending
 * further modularity work)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ComponentServiceTest extends CORETestCase {

    private ComponentService componentService;

    @Before
    public void establishComponentService() {
        componentService = CoreServiceApiServiceLocator.getComponentService();
        assertNotNull("Failed to locate ComponentService", componentService);
    }


    @Test
    public void testGetComponentByDataObjectService() {
        QueryByCriteria.Builder qbc = QueryByCriteria.Builder.forAttribute("namespace.code", "KR-NS");
        QueryResults<ComponentBo> results = KRADServiceLocator.getDataObjectService().findMatching(ComponentBo.class, qbc.build());

        assertNotNull(results);
        assertTrue(CollectionUtils.isNotEmpty(results.getResults()));
    }


    @Test
    /**
     * tests {@link ComponentService#getComponentByCode(String, String)} for a component that does not exist
     * and for a component that exists
     */
    public void testGetComponentByCode() {
        // get a component we know does not exist
        assertNull(componentService.getComponentByCode("blah", "blah"));

        // get a component which we know exists
        Component component = componentService.getComponentByCode("KR-WKFLW", "DocumentSearch");
        assertNotNull(component);
        assertTrue(component.isActive());
    }

    @Test
    /**
     * tests {@link ComponentService#getAllComponentsByNamespaceCode(String)} by a component namespace that does not exist and
     * by a component namespace that does exist
     */
    public void testGetAllComponentsByNamespaceCode() {
        // get by a component namespace we know does not exist
        List<Component> components = componentService.getAllComponentsByNamespaceCode("blah");
        assertNotNull(components);
        assertEquals(0, components.size());

        // now fetch all components for a namespace which we know has more than 1,
        // we should have 7 components under the "KR-NS" namespace code in our default test data set as follows:
        // +----------+-----------------------------+
        // | NMSPC_CD | CMPNT_CD                    |
        // +----------+-----------------------------+
        // | KR-NS    | All                         |
        // | KR-NS    | Batch                       |
        // | KR-NS    | Document                    |
        // | KR-NS    | Lookup                      |
        // | KR-NS    | PurgePendingAttachmentsStep |
        // | KR-NS    | PurgeSessionDocumentsStep   |
        // | KR-NS    | ScheduleStep                |
        // +----------+-----------------------------+


        components = componentService.getAllComponentsByNamespaceCode("KR-NS");
        assertEquals(7, components.size());

        ComponentBo scheduleStepComponent = null;
        // all should be active
        for (Component component : components) {
            assertTrue("Component should have been active: " + component, component.isActive());
            if (component.getCode().equals("ScheduleStep")) {
                scheduleStepComponent = ComponentBo.from(component);
            }
        }
        assertNotNull("Failed to locate schedule step component", scheduleStepComponent);

        // inactivate schedule step component
        scheduleStepComponent.setActive(false);
        KRADServiceLocator.getDataObjectService().save(scheduleStepComponent);

        components = componentService.getAllComponentsByNamespaceCode("KR-NS");
        assertEquals(7, components.size());
        int numActive = 0;
        int numInactive = 0;
        for (Component component : components) {
            if (component.isActive()) {
                numActive++;
            } else {
                numInactive++;
            }
        }

        // should be 6 active, 1 inactive
        assertEquals(6, numActive);
        assertEquals(1, numInactive);
    }

    @Test
    /**
     * tests that {@link ComponentService#getActiveComponentsByNamespaceCode(String)} returns all active components
     * for the given name space code
     */
    public void testGetActiveComponentsByNamespaceCode() {
        // get by a component namespace we know does not exist
        List<Component> components = componentService.getActiveComponentsByNamespaceCode("blah");
        assertNotNull(components);
        assertEquals(0, components.size());

        // now fetch all components for a namespace which we know has more than 1,
        // we should have 7 components under the "KR-NS" namespace code in our default test data set as follows:
        // +----------+-----------------------------+
        // | NMSPC_CD | CMPNT_CD                    |
        // +----------+-----------------------------+
        // | KR-NS    | All                         |
        // | KR-NS    | Batch                       |
        // | KR-NS    | Document                    |
        // | KR-NS    | Lookup                      |
        // | KR-NS    | PurgePendingAttachmentsStep |
        // | KR-NS    | PurgeSessionDocumentsStep   |
        // | KR-NS    | ScheduleStep                |
        // +----------+-----------------------------+

        components = componentService.getActiveComponentsByNamespaceCode("KR-NS");
        assertEquals(7, components.size());

        ComponentBo scheduleStepComponent = null;
        // all should be active
        for (Component component : components) {
            assertTrue("Component should have been active: " + component, component.isActive());
            if (component.getCode().equals("ScheduleStep")) {
                scheduleStepComponent = ComponentBo.from(component);
            }
        }
        assertNotNull("Failed to locate schedule step component", scheduleStepComponent);

        // inactivate schedule step component
        scheduleStepComponent.setActive(false);
        KRADServiceLocator.getDataObjectService().save(scheduleStepComponent);

        components = componentService.getActiveComponentsByNamespaceCode("KR-NS");
        assertEquals(6, components.size());
        for (Component component : components) {
            assertTrue("Component should have been active: " + component, component.isActive());
        }
    }
}
