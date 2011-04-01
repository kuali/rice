package org.kuali.rice.shareddata.impl;

/**
 * Class used for tests within shareddata module just to find an appropriate http endpoint to publish each service on.
 * This system property is either set in the systemPropertyVariables section of the failsafe plugin's configuration
 * section or can be overridden on the java/maven invocation by passing in
 * -Djaxws.endpoint.publish.test.address=${your.url}
 */
class ServiceEndpointLocation {
    static String ENDPOINT_URL = "http://localhost:23000/service"; //Default URL
    static {
        String endpoint = System.getProperty("jaxws.endpoint.publish.test.address");
        if (endpoint != null) {
            ENDPOINT_URL = endpoint;
        }
    }
}
