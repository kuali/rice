package org.kuali.rice.core.impl.component;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.kuali.test.KRADTestCase;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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

    @Test
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
        KRADServiceLocator.getBusinessObjectService().save(scheduleStepComponent);

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

}
