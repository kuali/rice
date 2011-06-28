package org.kuali.rice.kim.impl.group;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.test.remote.RemoteTestHarness;

public class GroupServiceRemoteTest extends GroupServiceImplTest {

      RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        GroupService remoteProxy =
                harness.publishEndpointAndReturnProxy(GroupService.class, this.getGroupServiceImpl());
        super.setGroupService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
    }
}
