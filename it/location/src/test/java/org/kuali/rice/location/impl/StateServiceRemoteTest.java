package org.kuali.rice.location.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.impl.state.StateServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class StateServiceRemoteTest extends StateServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        StateService remoteProxy =
                harness.publishEndpointAndReturnProxy(StateService.class, this.getStateServiceImpl());
        super.setStateService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
