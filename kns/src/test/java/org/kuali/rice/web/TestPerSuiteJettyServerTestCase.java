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
import org.kuali.rice.core.Core;
import org.kuali.rice.test.JettyServerTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle.ConfigMode;
import org.kuali.rice.test.server.JettyServer;
import org.springframework.test.AssertThrows;

/**
 * Tests that the JettyServer is present for the duration of all tests in this class 
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
// ignored for now because the KNS webapp cannot run concurrently in the same classloading space because
// of use of static singletons, etc.
@Ignore
@JettyServer(
    context = "SampleRiceClient",
    webapp = "/src/test/webapp",
    portConfigParam = "unittest.jetty.server1.port",
    configMode = ConfigMode.NONE
)
public class TestPerSuiteJettyServerTestCase extends JettyServerTestCase {
    public TestPerSuiteJettyServerTestCase() {
        super("kns");
    }

    @Override
    protected String getModuleTestConfigLocation() {
        return "classpath:META-INF/sample-app-test-config.xml";
    }

    private void testJettyServerIsPresent() {
        String portStr = Core.getCurrentContextConfig().getProperty("unittest.jetty.server1.port");
        assertNotNull(portStr);
        int port = Integer.parseInt(portStr);
        
        log.info("Jetty should be listening on port " + port);

        final InetSocketAddress isa = new InetSocketAddress(port);
        final Socket s = new Socket();
        new AssertThrows(IOException.class) {
            public void test() throws IOException {
                s.bind(isa);
            }
        }.runTest();
    }

    /**
     * Tests that the JettyServer is present when this test is running
     */
    @Test
    public void testJettyServerIsPresent1() {
        testJettyServerIsPresent();
    }
    /**
     * Tests that the JettyServer is present when this test is running
     */
    @Test
    public void testJettyServerIsPresent2() {
        testJettyServerIsPresent();
    }
    /**
     * Tests that the JettyServer is present when this test is running
     */
    @Test
    public void testJettyServerIsPresent3() {
        testJettyServerIsPresent();
    }
}