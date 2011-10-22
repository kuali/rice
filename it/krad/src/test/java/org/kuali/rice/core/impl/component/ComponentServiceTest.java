package org.kuali.rice.core.impl.component;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.test.KRADTestCase;

import static org.junit.Assert.*;

/**
 * An integration test which tests the reference implementation of the ComponentService.
 *
 * TODO - for now this test is part of KRAD even though it should be part of the core (pending
 * further modularity work)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentServiceTest extends KRADTestCase {

    private ComponentService componentService;

    @Before
    public void establishComponentService() {
        componentService = CoreApiServiceLocator.getComponentService();
        assertNotNull("Failed to locate ComponentService", componentService);
    }

    @Test
    public void testGetComponentByCode() {
        // get a component we know does not exist
        assertNull(componentService.getComponentByCode("blah", "blah"));

        // get a component which we know exists
        Component component = componentService.getComponentByCode("KR-WKFLW", "DocumentSearch");
        assertNotNull(component);
        assertTrue(component.isActive());
    }

}
