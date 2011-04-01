package org.kuali.rice.shareddata.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.county.CountyService;
import org.kuali.rice.shareddata.impl.county.CountyServiceImplTest;

import javax.xml.ws.Endpoint;

public class CountyServiceRemoteTest extends CountyServiceImplTest {

    Endpoint endpoint;

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CountyService.class);
        factory.setAddress(ServiceEndpointLocation.ENDPOINT_URL);
        this.setCountyService((CountyService) factory.create());

        //Note: Endpoint.publish only starts up an internal (jetty) server the first time it is invoked.
        endpoint = Endpoint.publish(ServiceEndpointLocation.ENDPOINT_URL, this.getCountyServiceImpl());
    }

    @After
    public void unPublishEndpoint() {
        endpoint.stop();
    }
}
