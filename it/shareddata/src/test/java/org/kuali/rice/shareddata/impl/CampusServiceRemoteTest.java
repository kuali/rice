package org.kuali.rice.shareddata.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.impl.campus.CampusServiceImplTest;
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
