package org.kuali.rice.shareddata.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.impl.campus.CampusServiceImplTest;

import javax.xml.ws.soap.SOAPFaultException;

public class CampusServiceRemoteTest extends CampusServiceImplTest {

    RemoteTestHarness harness = new RemoteTestHarness();

    @Before
    @Override
    public void setupServiceUnderTest() {
        super.setupServiceUnderTest();
        CampusService remoteProxy =
                harness.publishEndpointAndReturnProxy(CampusService.class, this.getCampusServiceImpl());
        super.setCampusService(remoteProxy);
    }

    @After
    public void unPublishEndpoint() {
        harness.stopEndpoint();
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
