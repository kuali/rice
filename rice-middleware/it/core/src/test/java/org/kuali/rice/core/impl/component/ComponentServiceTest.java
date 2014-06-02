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
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.List;

import static org.junit.Assert.*;

/**
 * An integration test which tests the reference implementation of the ComponentService.
 *
 * TODO - for now this test is part of KRAD even though it should be part of the core (pending further modularity work)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ComponentServiceTest extends CORETestCase {

    private ComponentService componentService;
    private DataObjectService dataObjectService;

    @Before
    public void establishServices() {
        componentService = CoreServiceApiServiceLocator.getComponentService();
        assertNotNull("Failed to locate ComponentService", componentService);

        dataObjectService = KRADServiceLocator.getDataObjectService();
        assertNotNull("Failed to locate DataObjectService", dataObjectService);
    }

    /**
     * Ensure that an existing {@link Component} can be fetched by the {link DataObjectService}.
     */
    @Test
    public void testGetComponentByDataObjectService() {
        NamespaceBo namespace = NamespaceBo.from(Namespace.Builder.create("KR-TST").build());
        dataObjectService.save(namespace);
        ComponentBo component = ComponentBo.from(Component.Builder.create("KR-TST", "TST-DOS", "Test Data Object Service").build());
        dataObjectService.save(component);

        // get a component we know exists
        QueryByCriteria.Builder query = QueryByCriteria.Builder.forAttribute("namespace.code", "KR-TST");
        QueryResults<ComponentBo> results = dataObjectService.findMatching(ComponentBo.class, query.build());
        assertNotNull("Results were null", results);
        assertTrue("Results were empty", CollectionUtils.isNotEmpty(results.getResults()));
    }

    /**
     * Ensure that {@link ComponentService#getComponentByCode(String, String)} returns a null {@link Component} when no
     * records exist by that namespace and code and a not-null {@link Component} when a record exists by that namespace
     * and code.
     */
    @Test
    public void testGetComponentByCode() {
        NamespaceBo namespace = NamespaceBo.from(Namespace.Builder.create("KR-TST").build());
        dataObjectService.save(namespace);
        ComponentBo component = ComponentBo.from(Component.Builder.create("KR-TST", "TST-CD", "Test Code").build());
        dataObjectService.save(component);

        // get a component we know does not exist
        Component nonExistingComponent = componentService.getComponentByCode("blah", "blah");
        assertNull("Component was not null", nonExistingComponent);

        // get a component we know exists
        Component existingComponent = componentService.getComponentByCode("KR-TST", "TST-CD");
        assertNotNull("Component was null", existingComponent);
        assertTrue("Component was not active", existingComponent.isActive());
    }

    /**
     * Ensure that {@link ComponentService#getAllComponentsByNamespaceCode(String)} returns a null {@link Component}
     * when no records exist by that namespace and a not-null {@link Component} when a record exists by that namespace.
     */
    @Test
    public void testGetAllComponentsByNamespaceCode() {
        NamespaceBo namespace = NamespaceBo.from(Namespace.Builder.create("KR-TST").build());
        dataObjectService.save(namespace);
        ComponentBo component1 = ComponentBo.from(Component.Builder.create("KR-TST", "TST-NMSPC1", "Test Namespace 1").build());
        dataObjectService.save(component1);
        ComponentBo component2 = ComponentBo.from(Component.Builder.create("KR-TST", "TST-NMSPC2", "Test Namespace 2").build());
        dataObjectService.save(component2);

        // get components we know do not exist
        List<Component> nonExistingComponents = componentService.getAllComponentsByNamespaceCode("blah");
        assertNotNull("Components were null", nonExistingComponents);
        assertEquals("Wrong number of components were found", 0, nonExistingComponents.size());

        // get components we know exist
        List<Component> existingComponents = componentService.getAllComponentsByNamespaceCode("KR-TST");
        assertEquals("Wrong number of components were found", 2, existingComponents.size());

        // all components should be active
        for (Component existingComponent : existingComponents) {
            assertTrue("Component should have been active: " + existingComponent, existingComponent.isActive());
        }

        // inactivate last component
        ComponentBo lastComponent = ComponentBo.from(existingComponents.get(existingComponents.size() - 1));
        lastComponent.setActive(false);
        dataObjectService.save(lastComponent);

        // get components we know exist
        List<Component> activeOrInactiveComponents = componentService.getAllComponentsByNamespaceCode("KR-TST");
        assertEquals("Wrong number of components were found", 2, activeOrInactiveComponents.size());

        // count active and inactive components
        int numActive = 0;
        int numInactive = 0;
        for (Component activeOrInactiveComponent : activeOrInactiveComponents) {
            if (activeOrInactiveComponent.isActive()) {
                numActive++;
            } else {
                numInactive++;
            }
        }

        // should be 1 active and 1 inactive component
        assertEquals("Wrong number of components were active", 1, numActive);
        assertEquals("Wrong number of components were inactive", 1, numInactive);
    }

    /**
     * Ensure that {@link ComponentService#getAllComponentsByNamespaceCode(String)} returns a null {@link Component}
     * when no active records exist by that namespace and a not-null {@link Component} when an active record exists by
     * that namespace.
     */
    @Test
    public void testGetActiveComponentsByNamespaceCode() {
        NamespaceBo namespace = NamespaceBo.from(Namespace.Builder.create("KR-TST").build());
        dataObjectService.save(namespace);
        ComponentBo component1 = ComponentBo.from(Component.Builder.create("KR-TST", "TST-ACTV1", "Test Active 1").build());
        dataObjectService.save(component1);
        ComponentBo component2 = ComponentBo.from(Component.Builder.create("KR-TST", "TST-ACTV2", "Test Active 2").build());
        dataObjectService.save(component2);

        // get components we know do not exist
        List<Component> nonExistingComponents = componentService.getActiveComponentsByNamespaceCode("blah");
        assertNotNull("Components were null", nonExistingComponents);
        assertEquals("Wrong number of components were found", 0, nonExistingComponents.size());

        // get components we know exist
        List<Component> existingComponents = componentService.getActiveComponentsByNamespaceCode("KR-TST");
        assertEquals("Wrong number of components were found", 2, existingComponents.size());

        // all components should be active
        for (Component existingComponent : existingComponents) {
            assertTrue("Component should have been active: " + existingComponent, existingComponent.isActive());
        }

        // inactivate last component
        ComponentBo lastComponent = ComponentBo.from(existingComponents.get(existingComponents.size() - 1));
        lastComponent.setActive(false);
        dataObjectService.save(lastComponent);

        // get components we know exist
        List<Component> activeComponents = componentService.getActiveComponentsByNamespaceCode("KR-TST");
        assertEquals("Wrong number of components were found", 1, activeComponents.size());

        // all components should be active
        for (Component activeComponent : activeComponents) {
            assertTrue("Component should have been active: " + activeComponent, activeComponent.isActive());
        }
    }
}
