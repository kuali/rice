package org.kuali.rice.shareddata.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.shareddata.api.country.CountryService;
import org.kuali.rice.shareddata.impl.country.CountryServiceImplTest;

import javax.xml.ws.Endpoint;

public class CountryServiceRemoteTest extends CountryServiceImplTest {

    Endpoint endpoint;

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CountryService.class);
        factory.setAddress(ServiceConstant.ENDPOINT_URL);
        this.setCountryService((CountryService) factory.create());

        //Note: Endpoint.publish only starts up an internal (jetty) server the first time it is invoked.
        endpoint = Endpoint.publish(ServiceConstant.ENDPOINT_URL, this.getCountryServiceImpl());
    }

    @After
    public void unPublishEndpoint() {
        endpoint.stop();
    }
}
