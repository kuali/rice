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
package org.kuali.rice.ksb.server;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.security.credentials.CredentialsSource;
import org.kuali.rice.core.api.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.ksb.BaseTestServer;
import org.kuali.rice.ksb.security.credentials.UsernamePasswordCredentialsSource;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class TestClient1 extends BaseTestServer {

    private static final Logger LOG = Logger.getLogger(TestClient1.class);

    /**
     * Class that does late binding of config params used to configure our server
     */
    public static final class ConfigConstants {
        public final String WEB_ROOT = "org/kuali/rice/ksb/testclient1";
        public final String CONTEXT = "/TestClient1";
        public final Integer SERVER_HTTP_PORT = Integer.valueOf(ConfigContext.getCurrentContextConfig().getProperty("ksb.client1.port"));
        public final Integer SERVER_HTTPS_PORT = Integer.valueOf(ConfigContext.getCurrentContextConfig().getProperty("ksb.client1.ssl.port"));
        public final String KEYSTORE_PASS = ConfigContext.getCurrentContextConfig().getKeystorePassword();
        public final String KEYSTORE_PATH; // assigned in constructor

        public ConfigConstants() {
            String keystoreFileTmp = null;

            try {
                keystoreFileTmp = ConfigContext.getCurrentContextConfig().getKeystoreFile();
            } catch (Exception e) {
                throw new RiceRuntimeException("Couldn't get keystore file location", e);
            }

            KEYSTORE_PATH = keystoreFileTmp;
        }
    }

    /**
     * Creates a Server that exposes the TestClient1 services via http and https
     *
     * @return the Server instance
     */
    @Override
    protected Server createServer() {

        // Need this CredentialsSourceFactory in our config to enable our test of basic auth
        // with our httpInvoker-echoServiceSecure

        registerTestCredentialsSourceFactory();

        ConfigConstants configConstants = new ConfigConstants();

        Server server = new Server();

        SelectChannelConnector connector0 = new SelectChannelConnector();
        connector0.setPort(configConstants.SERVER_HTTP_PORT);
        connector0.setMaxIdleTime(30000);
        connector0.setRequestHeaderSize(8192);

        SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();

        ssl_connector.setPort(configConstants.SERVER_HTTPS_PORT);
        SslContextFactory cf = ssl_connector.getSslContextFactory();
        cf.setKeyStore(configConstants.KEYSTORE_PATH);
        cf.setKeyStorePassword(configConstants.KEYSTORE_PASS);
        cf.setKeyManagerPassword(configConstants.KEYSTORE_PASS);

        server.setConnectors(new Connector[]{connector0, ssl_connector});

        URL webRoot = getClass().getClassLoader().getResource(configConstants.WEB_ROOT);
        String location = webRoot.getPath();

        LOG.debug("#####################################");
		LOG.debug("#");
		LOG.debug("#  Starting Client1 using following web root " + location);
		LOG.debug("#");
		LOG.debug("#####################################");

        WebAppContext context = new WebAppContext(location, configConstants.CONTEXT);
        context.setThrowUnavailableOnStartupException(true);
        context.setClassLoader(new KsbTestClientClassLoader());
        server.setHandler(context);

        return server;
    }

    /**
     * Registers a CredentialsSourceFactory with our ConfigContext.  Needed to verify that services requiring basic auth
     * work.
     */
    private void registerTestCredentialsSourceFactory() {
        List<CredentialsSource> credentialsSources =
                Collections.<CredentialsSource>singletonList(
                        new UsernamePasswordCredentialsSource("gilesp", "thuperthecret"));

        CredentialsSourceFactory credentialsSourceFactory = new CredentialsSourceFactory();
        credentialsSourceFactory.setCredentialsSources(credentialsSources);

        try {
            credentialsSourceFactory.afterPropertiesSet();
        } catch (Exception e) {
            throw new RiceRuntimeException(e);
        }

        ConfigContext.getCurrentContextConfig().putObject(Config.CREDENTIALS_SOURCE_FACTORY, credentialsSourceFactory);
    }
}
