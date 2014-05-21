/*
 * Copyright 2006-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.ksb.messaging;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.ksb.api.KsbApiConstants;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.support.JavaServiceConfiguration;
import org.kuali.rice.ksb.messaging.remotedservices.EchoService;
import org.kuali.rice.ksb.messaging.serviceconnectors.HttpInvokerConnector;
import org.kuali.rice.ksb.server.TestClient1;
import org.kuali.rice.ksb.test.KSBTestCase;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * simple test verifying HttpInvoker based service clients are working.
 *
 * <p>HttpInvoker-based services (services defined by JavaServiceDefinitions) are tested via http and https, with
 * bus security and auth enabled</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class HttpInvokerConnectorTest extends KSBTestCase {

    public boolean startClient1() {
        return true;
    }

    private static final QName serviceName = new QName("urn:TestCl1", "httpInvoker-echoServiceSecure");

    /**
     * Make sure our registry is up to date with the services from TestClient1
     */
    @Before
    public void setup() {
        KsbApiServiceLocator.getServiceBus().synchronize();
    }

    /**
     * Tests a secured and auth-enabled HttpInvoker-based service with a simple call over http
     */
    @Test
    public void testHttpInvokerServiceCall() {
        Endpoint endpoint = KsbApiServiceLocator.getServiceBus().getEndpoint(serviceName);

        Assert.assertTrue(KsbApiConstants.ServiceTypes.HTTP_INVOKER.equals(
                endpoint.getServiceConfiguration().getType()));

        EchoService echoService = (EchoService)KsbApiServiceLocator.getServiceBus().getService(serviceName);

        Assert.assertTrue("foo".equals(echoService.echo("foo")));
    }

    /**
     * Tests a secured and auth-enabled HttpInvoker-based service with an https call
     */
    @Test
    public void testSecureHttpInvokerServiceCall() throws MalformedURLException {
        Endpoint endpoint = KsbApiServiceLocator.getServiceBus().getEndpoint(serviceName);

        Assert.assertTrue(KsbApiConstants.ServiceTypes.HTTP_INVOKER.equals(endpoint.getServiceConfiguration().getType()));

        // we need to build a custom URL to our https endpoint

        ServiceConfiguration serviceConfiguration = endpoint.getServiceConfiguration();
        URL endpointUrl = serviceConfiguration.getEndpointUrl();
        TestClient1.ConfigConstants configConstants = new TestClient1.ConfigConstants();

        URL httpsUrl = new URL("https", endpointUrl.getHost(), configConstants.SERVER_HTTPS_PORT, endpointUrl.getFile());

        // manually build our service proxy using the HttpInvokerConnector
        HttpInvokerConnector connector = new HttpInvokerConnector((JavaServiceConfiguration)serviceConfiguration, httpsUrl);
        EchoService httpsEchoService = (EchoService) connector.getService();

        // test the secure service call
        Assert.assertTrue("foo".equals(httpsEchoService.echo("foo")));
    }

}
