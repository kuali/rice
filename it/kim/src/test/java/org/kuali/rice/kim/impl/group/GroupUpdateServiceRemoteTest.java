package org.kuali.rice.kim.impl.group;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.kim.api.group.GroupUpdateService;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class GroupUpdateServiceRemoteTest extends org.kuali.rice.kim.impl.group.GroupUpdateServiceImplTest {

          RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        GroupUpdateService remoteProxy =
                harness.publishEndpointAndReturnProxy(GroupUpdateService.class, this.getGroupUpdateServiceImpl());
        super.setGroupUpdateService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
