/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.serviceconnectors;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.api.bus.support.JavaServiceConfiguration;
import org.kuali.rice.ksb.messaging.KSBHttpInvokerProxyFactoryBean;
import org.kuali.rice.ksb.messaging.KSBHttpInvokerRequestExecutor;
import org.kuali.rice.ksb.security.httpinvoker.AuthenticationCommonsHttpInvokerRequestExecutor;

import java.net.URL;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 0.9
 */
public class HttpInvokerConnector extends AbstractServiceConnector {

	private static final Logger LOG = Logger.getLogger(HttpInvokerConnector.class);

    private static final String HTTP_CLIENT_CONFIG_BEAN = "rice.ksb.httpClientConfigurer";

    /**
     * Constructs an HttpInvokerConnector.
     *
     * @param serviceConfiguration the JavaServiceConfiguration
     * @param alternateEndpointUrl an alternate URL to use for the service endpoint
     */
    public HttpInvokerConnector(final JavaServiceConfiguration serviceConfiguration, final URL alternateEndpointUrl) {
		super(serviceConfiguration, alternateEndpointUrl);
    }

    @Override
	public JavaServiceConfiguration getServiceConfiguration() {
		return (JavaServiceConfiguration) super.getServiceConfiguration();
	}
	
	public Object getService() {
	    LOG.debug("Getting connector for endpoint " + getActualEndpointUrl());
		KSBHttpInvokerProxyFactoryBean client = new KSBHttpInvokerProxyFactoryBean();
		client.setServiceUrl(getActualEndpointUrl().toExternalForm());
		client.setServiceConfiguration(getServiceConfiguration());
		
		KSBHttpInvokerRequestExecutor executor;
		
		if (getCredentialsSource() != null) {
		    executor = new AuthenticationCommonsHttpInvokerRequestExecutor(getHttpClient(), getCredentialsSource(), getServiceConfiguration());
		} else {
		    executor = new KSBHttpInvokerRequestExecutor(getHttpClient());
		}
		executor.setSecure(getServiceConfiguration().getBusSecurity());
		client.setHttpInvokerRequestExecutor(executor);	
		client.afterPropertiesSet();
		return getServiceProxyWithFailureMode(client.getObject(), getServiceConfiguration());
	}

	/**
	 * Creates a httpcomponents HttpClient for service invocation.
     *
     * <p>The client is configured by the HttpClientConfigurer</p>
	 */
	public HttpClient getHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        getHttpClientConfigurer().customizeHttpClient(httpClientBuilder);

        return httpClientBuilder.build();
	}

    /**
     * Lazy initialization holder class idiom for static fields, see Effective Java item 71
     */
    private static class HttpClientConfigurerHolder {
        static final HttpClientConfigurer httpClientConfigurer =
                GlobalResourceLoader.getService(HTTP_CLIENT_CONFIG_BEAN);
    }

    /**
     * Gets the HttpClientConfigurer that will be used to customize http clients.
     *
     * <p>On first call, the bean specified by {@link #HTTP_CLIENT_CONFIG_BEAN} will be lazily assigned and returned.</p>
     *
     * @return the HttpClientConfigurer
     */
    private static HttpClientConfigurer getHttpClientConfigurer() {
        return HttpClientConfigurerHolder.httpClientConfigurer;
    }
}
