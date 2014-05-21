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

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.util.KSBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.ksb.messaging.serviceconnectors.HttpClientParams.*;

/**
 * Configures HttpClientBuilder instances for use by the HttpInvokerConnector.
 *
 * <p>This class adapts the configuration mechanism which was used with Commons HttpClient, which used a number of
 * specific Rice config params (see {@link HttpClientParams}) to work with  the HttpComponents HttpClient.  The
 * configuration doesn't all map across nicely, so coverage is not perfect.</p>
 *
 * <p>If the configuration parameters here are not sufficient, this implementation is designed to be extended.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DefaultHttpClientConfigurer implements HttpClientConfigurer, InitializingBean {

    static final Logger LOG = LoggerFactory.getLogger(DefaultHttpClientConfigurer.class);

    private static final String RETRY_SOCKET_EXCEPTION_PROPERTY = "ksb.thinClient.retrySocketException";
    private static final int DEFAULT_SOCKET_TIMEOUT = 2 * 60 * 1000; // two minutes in milliseconds

    /**
     * Default maximum total connections per client
     */
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    // list of config params starting with "http." to ignore when looking for unsupported params
    private static final Set<String> unsupportedParamsWhitelist =
            new HashSet<String>(Arrays.asList("http.port", "http.service.url"));

    /**
     * Customizes the configuration of the httpClientBuilder.
     *
     * <p>Internally, this uses several helper methods to assist with configuring:
     * <ul>
     *     <li>Calls {@link #buildConnectionManager()} and sets the resulting {@link HttpClientConnectionManager} (if
     *     non-null) into the httpClientBuilder.</li>
     *     <li>Calls {@link #buildRequestConfig()} and sets the resulting {@link RequestConfig} (if non-null) into the
     *     httpClientBuilder.</li>
     *     <li>Calls {@link #buildRetryHandler()} and sets the resulting {@link HttpRequestRetryHandler} (if non-null)
     *     into the httpClientBuilder.</li>
     * </ul>
     * </p>
     *
     * @param httpClientBuilder the httpClientBuilder being configured
     */
    @Override
    public void customizeHttpClient(HttpClientBuilder httpClientBuilder) {

        HttpClientConnectionManager connectionManager = buildConnectionManager();
        if (connectionManager != null) {
            httpClientBuilder.setConnectionManager(connectionManager);
        }

        RequestConfig requestConfig = buildRequestConfig();
        if (requestConfig != null) {
            httpClientBuilder.setDefaultRequestConfig(requestConfig);
        }

        HttpRequestRetryHandler retryHandler = buildRetryHandler();
        if (retryHandler != null) {
            httpClientBuilder.setRetryHandler(retryHandler);
        }
    }

    /**
     * Builds the HttpClientConnectionManager.
     *
     * <p>Note that this calls {@link #buildSslConnectionSocketFactory()} and registers the resulting {@link SSLConnectionSocketFactory}
     * (if non-null) with its socket factory registry.</p>
     *
     * @return the HttpClientConnectionManager
     */
    protected HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = null;

        SSLConnectionSocketFactory sslConnectionSocketFactory = buildSslConnectionSocketFactory();
        if (sslConnectionSocketFactory != null) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create().register("https", sslConnectionSocketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
            poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            poolingConnectionManager = new PoolingHttpClientConnectionManager();
        }

        // Configure the connection manager
        poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS.getValueOrDefault(DEFAULT_MAX_TOTAL_CONNECTIONS));

        // By default we'll set the max connections per route (essentially that means per host for us) to the max total
        poolingConnectionManager.setDefaultMaxPerRoute(MAX_TOTAL_CONNECTIONS.getValueOrDefault(DEFAULT_MAX_TOTAL_CONNECTIONS));


        SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
        socketConfigBuilder.setSoTimeout(SO_TIMEOUT.getValueOrDefault(DEFAULT_SOCKET_TIMEOUT));

        Integer soLinger = SO_LINGER.getValue();
        if (soLinger != null) {
            socketConfigBuilder.setSoLinger(soLinger);
        }

        Boolean isTcpNoDelay = TCP_NODELAY.getValue();
        if (isTcpNoDelay != null) {
            socketConfigBuilder.setTcpNoDelay(isTcpNoDelay);
        }

        poolingConnectionManager.setDefaultSocketConfig(socketConfigBuilder.build());

        ConnectionConfig.Builder connectionConfigBuilder = ConnectionConfig.custom();

        Integer sendBuffer = SO_SNDBUF.getValue();
        Integer receiveBuffer = SO_RCVBUF.getValue();

        // if either send or recieve buffer size is set, we'll set the buffer size to whichever is greater
        if (sendBuffer != null || receiveBuffer != null) {
            Integer bufferSize = -1;
            if (sendBuffer != null) {
                bufferSize = sendBuffer;
            }

            if (receiveBuffer != null && receiveBuffer > bufferSize) {
                bufferSize = receiveBuffer;
            }

            connectionConfigBuilder.setBufferSize(bufferSize);
        }

        String contentCharset = HTTP_CONTENT_CHARSET.getValue();
        if (contentCharset != null) {
            connectionConfigBuilder.setCharset(Charset.forName(contentCharset));
        }

        poolingConnectionManager.setDefaultConnectionConfig(connectionConfigBuilder.build());

        return poolingConnectionManager;
    }

    /**
     * Builds the retry handler if {@link #RETRY_SOCKET_EXCEPTION_PROPERTY} is true in the project's configuration.
     *
     * @return the HttpRequestRetryHandler or null depending on configuration
     */
    protected HttpRequestRetryHandler buildRetryHandler() {
        // If configured to do so, allow the client to retry once
        if (ConfigContext.getCurrentContextConfig().getBooleanProperty(RETRY_SOCKET_EXCEPTION_PROPERTY, false)) {
            return new DefaultHttpRequestRetryHandler(1, true);
        }

        return null;
    }

    /**
     * Configures and builds the RequestConfig for the HttpClient.
     *
     * @return the RequestConfig
     */
    protected RequestConfig buildRequestConfig() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        // was using "rfc2109" here, but apparently RFC-2956 is standard now.
        requestConfigBuilder.setCookieSpec(COOKIE_POLICY.getValueOrDefault(CookieSpecs.STANDARD));

        Integer connectionRequestTimeout = CONNECTION_MANAGER_TIMEOUT.getValue();
        if (connectionRequestTimeout != null) {
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
        }

        Integer connectionTimeout = CONNECTION_TIMEOUT.getValue();
        if (connectionTimeout != null) {
            requestConfigBuilder.setConnectTimeout(connectionTimeout);
        }

        Boolean isStaleConnectionCheckEnabled = STALE_CONNECTION_CHECK.getValue();
        if (isStaleConnectionCheckEnabled != null) {
            requestConfigBuilder.setStaleConnectionCheckEnabled(isStaleConnectionCheckEnabled);
        }

        requestConfigBuilder.setSocketTimeout(SO_TIMEOUT.getValueOrDefault(DEFAULT_SOCKET_TIMEOUT));

        Boolean isUseExpectContinue = USE_EXPECT_CONTINUE.getValue();
        if (isUseExpectContinue != null) {
            requestConfigBuilder.setExpectContinueEnabled(isUseExpectContinue);
        }

        Integer maxRedirects = MAX_REDIRECTS.getValue();
        if (maxRedirects != null) {
            requestConfigBuilder.setMaxRedirects(maxRedirects);
        }

        Boolean isCircularRedirectsAllowed = ALLOW_CIRCULAR_REDIRECTS.getValue();
        if (isCircularRedirectsAllowed != null) {
            requestConfigBuilder.setCircularRedirectsAllowed(isCircularRedirectsAllowed);
        }

        Boolean isRejectRelativeRedirects = REJECT_RELATIVE_REDIRECT.getValue();
        if (isRejectRelativeRedirects != null) {
            // negating the parameter value here to align with httpcomponents:
            requestConfigBuilder.setRelativeRedirectsAllowed(!isRejectRelativeRedirects);
        }

        return requestConfigBuilder.build();
    }

    /**
     * Builds the {@link SSLConnectionSocketFactory} used in the connection manager's socket factory registry.
     *
     * <p>Note that if {@link org.kuali.rice.ksb.util.KSBConstants.Config#KSB_ALLOW_SELF_SIGNED_SSL} is set to true
     * in the project configuration, this connection factory will be configured to accept self signed certs even if
     * the hostname doesn't match.</p>
     *
     * @return the SSLConnectionSocketFactory
     */
    protected SSLConnectionSocketFactory buildSslConnectionSocketFactory() {
        SSLContextBuilder builder = new SSLContextBuilder();

        if (ConfigContext.getCurrentContextConfig().getBooleanProperty(KSBConstants.Config.KSB_ALLOW_SELF_SIGNED_SSL)) {
            try {
                // allow self signed certs
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            } catch (NoSuchAlgorithmException e) {
                throw new RiceRuntimeException(e);
            } catch (KeyStoreException e) {
                throw new RiceRuntimeException(e);
            }
        }

        SSLConnectionSocketFactory sslsf = null;

        try {
            if (ConfigContext.getCurrentContextConfig().getBooleanProperty(KSBConstants.Config.KSB_ALLOW_SELF_SIGNED_SSL)) {
                // allow certs that don't match the hostname
                sslsf = new SSLConnectionSocketFactory(builder.build(), new AllowAllHostnameVerifier());
            } else {
                sslsf = new SSLConnectionSocketFactory(builder.build());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RiceRuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RiceRuntimeException(e);
        }

        return sslsf;
    }

    /**
     * Exercises the configuration to make it fail fast if there is a problem.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        customizeHttpClient(HttpClientBuilder.create());

        // Warn about any params that look like they are for the old Commons HttpClient config
        Map<String, String> httpParams = ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix("http.", false);

        for (Map.Entry<String, String> paramEntry : httpParams.entrySet()) {
            if (!isParamNameSupported(paramEntry.getKey()) && !unsupportedParamsWhitelist.contains(paramEntry)) {
                LOG.warn("Ignoring unsupported config param \"" + paramEntry.getKey() + "\" with value \"" + paramEntry.getValue() + "\"");
            }
        }
    }

    /**
     * Checks all the enum elements in HttpClientParams to see if the given param name is supported.
     *
     * @param paramName
     * @return true if HttpClientParams contains an element with that param name
     */
    private boolean isParamNameSupported(String paramName) {
        for (HttpClientParams param : HttpClientParams.values()) {
            if (param.getParamName().equals(paramName)) {
                return true;
            }
        }

        return false;
    }
}
