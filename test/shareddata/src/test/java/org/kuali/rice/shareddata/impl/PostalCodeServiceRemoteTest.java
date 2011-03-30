package org.kuali.rice.shareddata.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.postalcode.PostalCodeService;
import org.kuali.rice.shareddata.impl.postalcode.PostalCodeServiceImplTest;

import javax.xml.ws.Endpoint;

public class PostalCodeServiceRemoteTest extends PostalCodeServiceImplTest {

    Endpoint endpoint;

    @Before
    public void publishEndpoint() {
        //Note: Endpoint.publish only starts up an internal (jetty) server the first time it is invoked.
        endpoint = Endpoint.publish(ServiceConstant.ENDPOINT_URL, this.getPostalCodeServiceImpl());

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(PostalCodeService.class);
        factory.setAddress(ServiceConstant.ENDPOINT_URL);
        this.setPostalCodeService((PostalCodeService) factory.create());
    }

    @After
    public void unPublishEndpoint() {
        endpoint.stop();
    }
}

