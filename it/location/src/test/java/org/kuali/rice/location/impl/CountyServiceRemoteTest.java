package org.kuali.rice.location.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.impl.county.CountyServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class CountyServiceRemoteTest extends CountyServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        CountyService remoteProxy =
                harness.publishEndpointAndReturnProxy(CountyService.class, this.getCountyServiceImpl());
        super.setCountyService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
