package org.kuali.rice.location.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.impl.campus.CampusServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class CampusServiceRemoteTest extends CampusServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        CampusService remoteProxy =
                harness.publishEndpointAndReturnProxy(CampusService.class, this.getCampusServiceImpl());
        super.setCampusService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
