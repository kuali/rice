/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.test.JettyServerTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle.ConfigMode;
import org.kuali.rice.test.server.JettyServer;
import org.springframework.test.AssertThrows;

/**
 * Tests that a JettyServer can be brought up for a single test, and shutdown afterwards 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
// ignored for now because the KNS webapp cannot run concurrently in the same classloading space because
// of use of static singletons, etc.
@Ignore
public class TestPerTestJettyServerTestCase extends JettyServerTestCase {
    public TestPerTestJettyServerTestCase() {
        super("server");
    }

    @Override
    protected String getModuleTestConfigLocation() {
        return "classpath:META-INF/sample-app-test-config.xml";
    }

    private int perTestPort;

    @Test
    @JettyServer(
            context = "SampleRiceClient",
            webapp = "/src/main/webapp",
            portConfigParam = "unittest.jetty.server2.port",
            configMode = ConfigMode.NONE,
            addWebappResourceLoader = false
        )
    public void testJettyStartsOnRandomPortPerTest() {
        String portStr = ConfigContext.getCurrentContextConfig().getProperty("unittest.jetty.server2.port");
        assertNotNull(portStr);
        perTestPort = Integer.parseInt(portStr);
        
        log.info("Jetty should be listening on port " + perTestPort);

        final InetSocketAddress isa = new InetSocketAddress(perTestPort);
        final Socket s = new Socket();
        new AssertThrows(IOException.class) {
            public void test() throws IOException {
                s.bind(isa);
            }
        }.runTest();
        
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        
        // now test that jetty has shut down
        final InetSocketAddress isa = new InetSocketAddress(perTestPort);
        final Socket s = new Socket();
        s.bind(isa);
    }
    
}
