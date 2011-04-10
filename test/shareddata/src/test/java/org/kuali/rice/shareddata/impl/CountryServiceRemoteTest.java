package org.kuali.rice.shareddata.impl;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.country.CountryService;
import org.kuali.rice.shareddata.impl.country.CountryServiceImplTest;

public class CountryServiceRemoteTest extends CountryServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        CountryService remoteProxy =
                harness.publishEndpointAndReturnProxy(CountryService.class, this.getCountryServiceImpl());
        super.setCountryService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
