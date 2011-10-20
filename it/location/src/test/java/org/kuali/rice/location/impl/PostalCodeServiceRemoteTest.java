package org.kuali.rice.location.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.impl.postalcode.PostalCodeServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class PostalCodeServiceRemoteTest extends PostalCodeServiceImplTest {
    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        PostalCodeService remoteProxy =
                harness.publishEndpointAndReturnProxy(PostalCodeService.class, this.getPostalCodeServiceImpl());
        super.setPostalCodeService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}

