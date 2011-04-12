package org.kuali.rice.core.impl.parameter;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.core.api.style.StyleRepositoryService;
import org.kuali.rice.core.impl.style.StyleRepositoryServiceImplTest;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class StyleRepositoryServiceRemoteTest extends StyleRepositoryServiceImplTest {
    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        StyleRepositoryService remoteProxy = harness.publishEndpointAndReturnProxy(
                StyleRepositoryService.class, this.getStyleRepositoryServiceImpl());
        super.setStyleRepositoryService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
