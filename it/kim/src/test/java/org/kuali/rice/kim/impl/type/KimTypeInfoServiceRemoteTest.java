package org.kuali.rice.kim.impl.type;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class KimTypeInfoServiceRemoteTest extends KimTypeInfoServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        KimTypeInfoService remoteProxy =
                harness.publishEndpointAndReturnProxy(KimTypeInfoService.class, this.getKimTypeInfoServiceImpl());
        super.setKimTypeInfoService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
