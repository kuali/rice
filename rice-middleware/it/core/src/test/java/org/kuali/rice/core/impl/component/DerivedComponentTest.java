/*
 * Copyright 2006-2014 The Kuali Foundation
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

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.test.CORETestCase;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * An integration test which tests the reference implementation of the ComponentService
 *
 * TODO - For now this test is part of KRAD even though it should be part of the core (pending
 * further modularity work)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class DerivedComponentTest extends CORETestCase {

    private ComponentService componentService;

    @Before
    public void establishComponentService() {
        componentService = CoreServiceApiServiceLocator.getComponentService();
        assertNotNull("Failed to locate ComponentService", componentService);
    }

    @Test
    /**
     * tests {@link org.kuali.rice.coreservice.api.component.ComponentService#getDerivedComponentSet(String)} and {@link org.kuali.rice.coreservice.api.component.ComponentService#publishDerivedComponents(String, java.util.List)}
     */
    public void testPublishComponents_and_getPublishedComponentSet() {

        String testComponentSetId = "testComponentSet";
        String workflowNamespace = "KR-WKFLW";
        String testNamespace1 = "TestNamespace1";
        String testNamespace2 = "TestNamespace2";

        List<Component> testComponentSet = componentService.getDerivedComponentSet(testComponentSetId);
        assertTrue("Initial testComponentSet should be empty", testComponentSet.isEmpty());
        List<Component> workflowComponents = componentService.getAllComponentsByNamespaceCode(workflowNamespace);

        assertTrue(componentService.getAllComponentsByNamespaceCode(testNamespace1).isEmpty());
        assertTrue(componentService.getAllComponentsByNamespaceCode(testNamespace2).isEmpty());

        String customTestWorkflowComponent = "CustomTestWorkflowComponent";
        Component component1 = Component.Builder.create(workflowNamespace, customTestWorkflowComponent, customTestWorkflowComponent).build();
        String testNamespace1Component = "TestNamespace1Component";
        Component component2 = Component.Builder.create(testNamespace1, testNamespace1Component, testNamespace1Component).build();
        String testNamespace2Component1 = "TestNamespace2Component1";
        Component component3 = Component.Builder.create(testNamespace2, testNamespace2Component1, testNamespace2Component1).build();
        String testNamespace2Component2 = "TestNamespace2Component2";
        Component component4 = Component.Builder.create(testNamespace2, testNamespace2Component2, testNamespace2Component2).build();

        List<Component> setToPublish = new ArrayList<Component>();
        setToPublish.add(component1);
        setToPublish.add(component2);
        setToPublish.add(component3);
        setToPublish.add(component4);

        componentService.publishDerivedComponents(testComponentSetId, setToPublish);

        // now if we fetch the component set it should be non-empty and should contain our 4 items
        testComponentSet = componentService.getDerivedComponentSet(testComponentSetId);
        assertEquals(4, testComponentSet.size());
        for (Component component : testComponentSet) {
            // ensure they all have the appropriate component set id
            assertEquals(testComponentSetId, component.getComponentSetId());
        }

        List<Component> shuffledComponentSet = new ArrayList<Component>(testComponentSet);
        // now, do a slight shuffle of the list and republish...
        Collections.shuffle(shuffledComponentSet);
        componentService.publishDerivedComponents(testComponentSetId, shuffledComponentSet);

        // we should still have the same set
        testComponentSet = componentService.getDerivedComponentSet(testComponentSetId);
        assertEquals(4, testComponentSet.size());

        // refetch by workflow namespace, we should have an additional component now
        List<Component> workflowComponentsNew = componentService.getAllComponentsByNamespaceCode(workflowNamespace);
        assertEquals(workflowComponents.size() + 1, workflowComponentsNew.size());

        // now republish our component set without the workflow namespace component
        setToPublish = new ArrayList<Component>();
        setToPublish.add(component2);
        setToPublish.add(component3);
        setToPublish.add(component4);
        componentService.publishDerivedComponents(testComponentSetId, setToPublish);

        // we should have 3 components now
        testComponentSet = componentService.getDerivedComponentSet(testComponentSetId);
        assertEquals(3, testComponentSet.size());

        // and the workflow component should be gone
        workflowComponentsNew = componentService.getAllComponentsByNamespaceCode(workflowNamespace);
        assertEquals(workflowComponents.size(), workflowComponentsNew.size());
    }


}
