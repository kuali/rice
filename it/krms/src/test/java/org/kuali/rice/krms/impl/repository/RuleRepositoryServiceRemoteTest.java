package org.kuali.rice.krms.impl.repository;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class RuleRepositoryServiceRemoteTest extends RuleRepositoryServiceImplTest {

        RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        RuleRepositoryService remoteProxy =
                harness.publishEndpointAndReturnProxy(RuleRepositoryServiceImpl.class, this.getRuleRepositoryServiceImpl());
        super.setRuleRepositoryService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
