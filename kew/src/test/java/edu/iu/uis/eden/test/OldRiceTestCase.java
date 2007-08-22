/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.LoggableTestCase;
import org.kuali.rice.test.TestHarnessWebApp;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Useful superclass for all Workflow test cases. Handles setup of test
 * utilities and a test environment. Configures the Spring test environment
 * providing a template method for custom context files in test mode. Also
 * provides a template method for running custom transactional setUp. Tear down
 * handles automatic tear down of objects created inside the test environment.
 *
 * @author
 * @version $Revision: 1.1.2.1 $ $Date: 2007-08-22 16:19:51 $
 * @since 0.9
 */
public abstract class OldRiceTestCase extends LoggableTestCase {

    private static final Logger LOG = Logger.getLogger(OldRiceTestCase.class);

    private static final String TEST_CONFIG_LOCATION = "classpath:org/kuali/rice/test/rice-test-client-config.xml";

    private static final String BUILD_PROPERTIES = "build.properties";

    public static final String TEST_CONFIG_LOCATION_PROPERTY = "test.config.location";

    private boolean setUp = false;

    private boolean tornDown = false;

    private List<Lifecycle> lifeCycles = new LinkedList<Lifecycle>();

    private List<String> reports = new ArrayList<String>();

    public OldRiceTestCase() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        beforeRun();
        final long startTime = System.currentTimeMillis();
        final Config riceTestConfig = new SimpleConfig();
        riceTestConfig.getProperties().putAll(System.getProperties());
        loadBootstrapConfig(riceTestConfig);
        Config testConfig = loadConfig(riceTestConfig);
        testConfig.parseConfig();
        riceTestConfig.getProperties().putAll(testConfig.getProperties());
        Core.init(riceTestConfig);
        verifyTestConfiguration();
        final long initTime = System.currentTimeMillis();
        report("Time to initialize Core: " + (initTime - startTime));
        this.lifeCycles = getLifecycles();
        try {
            startLifecycles();
        } catch (Exception e) {
            e.printStackTrace();
            stopLifecycles();
            throw e;
        }
        report("Time to start all Lifecycles: "
            + (System.currentTimeMillis() - initTime));

        try {
            loadTestDataInternal();
        } catch (WrappedTransactionRuntimeException e) {
            throw (Exception) e.getCause();
        }
        this.setUp = true;
    }

    protected void loadBootstrapConfig(final Config config) throws Exception {
        final Properties buildProperties = loadBuildProperties();
        config.getProperties().putAll(buildProperties);
    }

    protected Properties loadBuildProperties() throws IOException {
        Properties localProperties = loadLocalProperties();
        if (localProperties == null) {
            localProperties = new Properties();
        }
        final Properties userProperties = loadUserProperties();
        if (userProperties != null) {
            localProperties.putAll(userProperties);
        }
        return localProperties;
    }

    protected void verifyTestConfiguration() {
        if (!Core.getCurrentContextConfig().getProperties().containsKey(
            TEST_CONFIG_LOCATION_PROPERTY)) {
            throw new ConfigurationException(
                "Could not locate the test.config.location property in your test configuration.");
        }
    }

    /**
     * Load the user's build.properties in user's home
     */
    private Properties loadUserProperties() throws IOException {
        return loadProperties(new File(SystemUtils.USER_HOME + "/"
            + BUILD_PROPERTIES));
    }

    /**
     * Load the "local" build.properties in the current directory.
     */
    private Properties loadLocalProperties() throws IOException {
        return loadProperties(new File(BUILD_PROPERTIES));
    }

    /**
     * Loads a file into a Properties object
     *
     * @param file the file
     * @return a Properties object
     */
    private static Properties loadProperties(File file) throws IOException {
        if (!file.isFile()) {
            return null;
        }
        final Properties properties = new Properties();
        final FileInputStream fis = new FileInputStream(file);
        try {
            properties.load(fis);
        } finally {
            fis.close();
        }
        return properties;
    }

    protected Config loadConfig(final Config rootConfig) {
        return new SimpleConfig(TEST_CONFIG_LOCATION, rootConfig
            .getProperties());
    }

    /**
     * Can be overridden to allow for specification of the client protocol to
     * use in the test. If none is specified, then the default protocol is
     * embedded.
     */
    protected String getClientProtocol() {
        return null;
    }

    protected void setUpTransaction() throws Exception {
        // subclasses can override this method to do their setup within a
        // transaction
    }

    @After
    public void tearDown() throws Exception {
        stopLifecycles();
        Core.destroy();
        // super.tearDown();
        this.tornDown = true;
        afterRun();
    }

    protected void beforeRun() {
        System.out
            .println("##############################################################");
        System.out.println("# Starting test " + getClass().getSimpleName()
            + "...");
        System.out.println("# " + dumpMemory());
        System.out
            .println("##############################################################");
    }

    protected void afterRun() {
        System.out
            .println("##############################################################");
        System.out.println("# ...finished test " + getClass().getSimpleName());
        System.out.println("# " + dumpMemory());
        for (final String report : this.reports) {
            System.out.println("# " + report);
        }
        System.out
            .println("##############################################################\n\n\n");
    }

    /**
     * By default this loads the "default" data set from the data/TestData.xml
     * file. Subclasses can override this to change this behaviour
     */
    protected void loadDefaultTestData() throws Exception {
        // template method
    }

    protected void loadTestDataInternal() throws Exception {
        final PlatformTransactionManager platformTransactionManager = getPlatformTransactionManager();
        if (platformTransactionManager != null) {
            new TransactionTemplate(platformTransactionManager)
                .execute(new TransactionCallbackWithoutResult(){

                    public void doInTransactionWithoutResult(
                        final TransactionStatus status) {
                        try {
                            setUpTransactionInternal();
                        } catch (Exception e) {
                            throw new WrappedTransactionRuntimeException(e);
                        }
                    }
                });
        } else {
            setUpTransactionInternal();
        }
    }

    protected void setUpTransactionInternal() throws Exception {
        final long t1 = System.currentTimeMillis();
        // we need the messaging to be default of only sending message out with
        // the transaction
        // otherwise the importing of constants can get messed up with the
        // digital signature
        // constant's notification of the other nodes.
        loadDefaultTestData();

        final long t2 = System.currentTimeMillis();
        report("Time to load default test data: " + (t2 - t1));

        loadTestData();

        final long t3 = System.currentTimeMillis();
        report("Time to load test-specific test data: " + (t3 - t2));

        setUpTransaction();

        final long t4 = System.currentTimeMillis();
        report("Time to run test-specific setup: " + (t4 - t3));
    }

    /**
     * @Override
     */
    protected void loadTestData() throws Exception {
        // override this to load your own test data
    }

    protected PlatformTransactionManager getPlatformTransactionManager() {
        return null;
    }

    private class WrappedTransactionRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 3097909333572509600L;

        public WrappedTransactionRuntimeException(Exception e) {
            super(e);
        }
    }

    public void startLifecycles() throws Exception {
        for (final Lifecycle lifecycle : this.lifeCycles) {
            final long s = System.currentTimeMillis();
            try {
                lifecycle.start();
            } catch (final Exception e) {
                throw new Exception("Failed to start lifecycle " + lifecycle, e);
            }
            final long e = System.currentTimeMillis();
            LOG.info("Started lifecycle " + lifecycle + " in " + (e - s)
                + " ms.");
            report("Time to start lifecycle " + lifecycle + ": " + (e - s));
        }
    }

    public void stopLifecycles() throws Exception {
        final ListIterator<Lifecycle> iter = this.lifeCycles.listIterator();
        while (iter.hasNext()) {
            iter.next();
        }
        while (iter.hasPrevious()) {
            final Lifecycle lifeCycle = iter.previous();
            try {
                lifeCycle.stop();
            } catch (Exception e) {
                LOG.warn("Failed to shutdown one of the lifecycles!", e);
            }
        }
    }

    public List<Lifecycle> getLifecycles() {
        this.lifeCycles.add(new OldClearDatabaseLifecycle());
        this.lifeCycles.add(new TestHarnessWebApp());
        return this.lifeCycles;
    }

    protected void report(final String report) {
        this.reports.add(report);
    }

    private String dumpMemory() {
        final long total = Runtime.getRuntime().totalMemory();
        final long free = Runtime.getRuntime().freeMemory();
        final long max = Runtime.getRuntime().maxMemory();
        return "[Memory] max: " + max + ", total: " + total + ", free: " + free;
    }

    public boolean isSetUp() {
        return this.setUp;
    }

    public boolean isTornDown() {
        return this.tornDown;
    }

    public static InputStream loadResource(final Class< ? > packageClass,
        final String resourceName) {
        return packageClass.getResourceAsStream(resourceName);
    }

}