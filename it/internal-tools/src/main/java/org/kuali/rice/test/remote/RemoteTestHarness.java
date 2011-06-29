package org.kuali.rice.test.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.kuali.rice.core.cxf.interceptors.ImmutableCollectionsInInterceptor;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * Harness used to hold a reference to an endpoint that is published to support remote tests.  Tests using
 * this harness should pass in a @WebService annotated interface class and an object of an implementing class
 * of that interface to the publishEndpointAndReturnProxy method in @Before or setUp methods used in tests.
 * <p/>
 * After each test is run, stopEndPoint should be called in @After or tearDown methods in order to unpublish the
 * endpoint.
 */
public class RemoteTestHarness {

    private static final Log LOG = LogFactory.getLog(RemoteTestHarness.class);

    private Endpoint endpoint;

    @SuppressWarnings("unchecked")
    /**
     * Creates a published endpoint from the passed in serviceImplementation and also returns a proxy implementation
     * of the passed in interface for clients to use to hit the created endpoint.
     */
    public <T> T publishEndpointAndReturnProxy(Class<T> jaxWsAnnotatedInterface, T serviceImplementation) {
        if (jaxWsAnnotatedInterface.isInterface() &&
                jaxWsAnnotatedInterface.getAnnotation(WebService.class) != null &&
                jaxWsAnnotatedInterface.isInstance(serviceImplementation)) {

            endpoint = Endpoint.publish(ServiceEndpointLocation.ENDPOINT_URL, serviceImplementation);

            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(jaxWsAnnotatedInterface);
            factory.setAddress(ServiceEndpointLocation.ENDPOINT_URL);

            T serviceProxy = (T) factory.create();
            Client cxfClient = ClientProxy.getClient(serviceProxy);
            cxfClient.getInInterceptors().add(new ImmutableCollectionsInInterceptor());

            waitAndCheck(endpoint, false);

            return serviceProxy;
        } else {
            throw new IllegalArgumentException("Passed in interface class type must be annotated with @WebService " +
                    "and object reference must be an implementing class of that interface");

        }
    }

    /**
     * Stops and makes an internal endpoint unpublished if it was previously published.
     * Otherwise, this method is a no-op.
     */
    public void stopEndpoint() {
        if (endpoint != null) {
            endpoint.stop();
            waitAndCheck(endpoint, true);
        }
    }

    private static void waitAndCheck(Endpoint ep, boolean published) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (ep.isPublished() == published) {
            LOG.warn("endpoint: " + ep + " published: " + published);
        }
    }
}
