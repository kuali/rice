package org.kuali.rice.shareddata.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.state.StateService;
import org.kuali.rice.shareddata.impl.state.StateServiceImplTest;

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
