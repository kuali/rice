package org.kuali.rice.core.impl.component;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.rice.core.api.parameter.ParameterRepositoryService;
import org.kuali.rice.core.impl.parameter.ParameterRepositoryServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class ComponentServiceRemoteTest extends ComponentServiceImplTest {
    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        ComponentService remoteProxy = harness.publishEndpointAndReturnProxy(
                ComponentService.class, this.getServiceImpl());
        super.setService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
