/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.server.JettyServer;
import org.kuali.rice.test.server.JettyServers;

/**
 * A test case that supports declaratively defining a JettServer to run via annotations.
 * Although this class supports running JettyServers on a per-suite and per-test basis, there is a
 * pragmatic issue of concurrently running webapp contexts in the same classloader, so the webapp
 * will have to support this (e.g. not rely on static singletons...)
 * Another issue is that there is no suite "shutdown" per se, so a Jetty started up in a suite
 * lifecycle will never get explicitly shut down.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class JettyServerTestCase extends BaselineTestCase {
    private static Set<String> classesHandled = new HashSet<String>();
    
    /**
     * @see {@link BaselineTestCase#BaselineTestCase(String, Mode)
     */
    public JettyServerTestCase(String moduleName, Mode mode) {
        super(moduleName, mode);
    }

    /**
     * @see {@link BaselineTestCase#BaselineTestCase(String)
     */
    public JettyServerTestCase(String moduleName) {
        super(moduleName);
    }

    private List<JettyServer> readPerTestDefinitions(Method method) {
        return getJettyServerDefinitions((JettyServers) method.getAnnotation(JettyServers.class), (JettyServer) method.getAnnotation(JettyServer.class));
    }

    private List<JettyServer> readSuiteServerDefinitions(Class clazz) {
        return getJettyServerDefinitions((JettyServers) clazz.getAnnotation(JettyServers.class), (JettyServer) clazz.getAnnotation(JettyServer.class));
    }

    private List<JettyServer> getJettyServerDefinitions(JettyServers servers, JettyServer server) {
        List<JettyServer> defs = new ArrayList<JettyServer>();
        if (servers != null) {
            if (servers.servers() != null) {
                defs.addAll(Arrays.asList(servers.servers()));
            }
        }
        if (server != null) {
            defs.add(server);
        }
        return defs;
    }

    protected JettyServerLifecycle constructJettyServerLifecycle(JettyServer def) {
        String portConfigParam = def.portConfigParam();
        if (StringUtils.isBlank(portConfigParam)) {
            portConfigParam = null;
        }
        if (def.port() != JettyServer.CONFIG_PARAM_PORT && portConfigParam != null) {
            throw new IllegalArgumentException("Either but not both of port or portConfigParam must be specified on JettyServer annotation");
        }
        if (def.port() == JettyServer.CONFIG_PARAM_PORT && portConfigParam == null) {
            portConfigParam = "unittest.jetty." + def.context() + ".port";
        }
        JettyServerLifecycle lc;
        if (def.port() != JettyServer.CONFIG_PARAM_PORT) {
            lc = new RuntimePortJettyServerLifecycle(def.port(), "/" + def.context(), def.webapp());
        } else {
            lc = new RuntimePortJettyServerLifecycle(def.portConfigParam(), "/" + def.context(), def.webapp());
        }
        lc.setConfigMode(def.configMode());
        lc.setAddWebappResourceLoaders(def.addWebappResourceLoader());
        return lc;
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
        List<Lifecycle> lifecycles = super.getSuiteLifecycles();
        List<Class> classesToHandle;
        try {
            classesToHandle = TestUtilities.getHierarchyClassesToHandle(this.getClass(), new Class[] { JettyServers.class, JettyServer.class }, classesHandled);
        } catch (Exception e) {
            throw new RuntimeException("Error determining classes to handle", e);
        }
        List<JettyServer> suiteDefs = new ArrayList<JettyServer>();
        for (Class c: classesToHandle) {
            suiteDefs.addAll(readSuiteServerDefinitions(c));
            classesHandled.add(c.getName());
        }
        // add all our jetties...
        for (JettyServer def: suiteDefs) {
            lifecycles.add(constructJettyServerLifecycle(def));
        }
        return lifecycles;
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getPerTestLifecycles();
        List<JettyServer> defs = readPerTestDefinitions(method);
        // add all our jetties...
        for (JettyServer def: defs) {
            lifecycles.add(constructJettyServerLifecycle(def));
        }
        return lifecycles;
    }
    
    private static final class RuntimePortJettyServerLifecycle extends JettyServerLifecycle {
        private String portConfigParam;
        public RuntimePortJettyServerLifecycle(int port, String contextName, String relativeWebappRoot) {
            super(port, contextName, relativeWebappRoot);
        }
        public RuntimePortJettyServerLifecycle(String portConfigParam, String contextName, String relativeWebappRoot) {
            super(JettyServer.CONFIG_PARAM_PORT, contextName, relativeWebappRoot);
            this.portConfigParam = portConfigParam;
        }
        public void start() throws Exception {
            if (jettyServer.getPort() == JettyServer.CONFIG_PARAM_PORT) {
                String val = ConfigContext.getCurrentContextConfig().getProperty(portConfigParam);
                if (val == null) {
                    throw new RuntimeException("Jetty port not found in config param: " + portConfigParam);
                }
                jettyServer.setPort(Integer.parseInt(val));
            }
            super.start();
        }
        @Override
        public void stop() throws Exception {
            System.err.println("Shutting down jetty");
            super.stop();
        }
    }
}
