package org.kuali.rice.core.impl.parameter;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.core.api.parameter.ParameterRepositoryService;
import org.kuali.rice.core.impl.parameter.ParameterRepositoryServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class ParameterRepositoryServiceRemoteTest extends ParameterRepositoryServiceImplTest {
    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        ParameterRepositoryService remoteProxy = harness.publishEndpointAndReturnProxy(
                ParameterRepositoryService.class, this.getPserviceImpl());
        super.setPservice(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
