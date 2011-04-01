package org.kuali.rice.shareddata.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.state.StateService;
import org.kuali.rice.shareddata.impl.state.StateServiceImplTest;

import javax.xml.ws.Endpoint;

public class StateServiceRemoteTest extends StateServiceImplTest {

    Endpoint endpoint;

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(StateService.class);
        factory.setAddress(ServiceEndpointLocation.ENDPOINT_URL);
        this.setStateService((StateService) factory.create());

        //Note: Endpoint.publish only starts up an internal (jetty) server the first time it is invoked.
        endpoint = Endpoint.publish(ServiceEndpointLocation.ENDPOINT_URL, this.getStateServiceImpl());
    }

    @After
    public void unPublishEndpoint() {
        endpoint.stop();
    }
}
