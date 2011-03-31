package org.kuali.rice.shareddata.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.impl.campus.CampusServiceImplTest;

import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPFaultException;

public class CampusServiceRemoteTest extends CampusServiceImplTest {

    Endpoint endpoint;

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CampusService.class);
        factory.setAddress(ServiceConstant.ENDPOINT_URL);
        this.setCampusService((CampusService) factory.create());

        //Note: Endpoint.publish only starts up an internal (jetty) server the first time it is invoked.
        endpoint = Endpoint.publish(ServiceConstant.ENDPOINT_URL, this.getCampusServiceImpl());
    }

    @After
    public void unPublishEndpoint() {
        endpoint.stop();
    }

    /*
     * All tests methods overridden are done so to expect SoapFaultException instead of a specific exception
     */

    @Override
    @Test(expected = SOAPFaultException.class)
    public void testGetCampusEmptyCode() {
        super.testGetCampusEmptyCode();
    }

    @Override
    @Test(expected = SOAPFaultException.class)
    public void testGetCampusNullCode() {
        super.testGetCampusNullCode();
    }

    @Override
    @Test(expected = SOAPFaultException.class)
    public void testGetCampusTypeEmptyCode() {
        super.testGetCampusTypeEmptyCode();
    }

    @Override
    @Test(expected = SOAPFaultException.class)
    public void testGetCampusTypeNullCode() {
        super.testGetCampusTypeNullCode();
    }
}
