package org.kuali.rice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBroker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.PersistenceBrokerTemplate;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.FileXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDoc;
import edu.iu.uis.eden.batch.XmlDocCollection;
import edu.iu.uis.eden.config.Config;
import edu.iu.uis.eden.core.Core;
import edu.iu.uis.eden.core.Lifecycle;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Useful superclass for all Workflow test cases. Handles setup of test
 * utilities and a test environment. Configures the Spring test environment
 * providing a template method for custom context files in test mode. Also
 * provides a template method for running custom transactional setUp. Tear down
 * handles automatic tear down of objects created inside the test environment.
 */
public abstract class RiceTestCase extends Assert {
    
    private static final Logger LOG = Logger.getLogger(RiceTestCase.class);

	private boolean isSetUp = false;
	private boolean isTornDown = false;
	private List<Lifecycle> lifeCycles = new LinkedList<Lifecycle>();
	private List<String> reports = new ArrayList<String>();
    
    private static final String TEST_CONFIG_LOCATION = "classpath:rice-test-client-config.xml";
	
	public RiceTestCase() {
		super();
	}

	@Before public void setUp() throws Exception {
		beforeRun();
		long startTime = System.currentTimeMillis();
        // TODO Fix this hack when we get the real config in rice 1.0
        RiceConfigurer.setConfigurationFile("classpath:test_configuration.properties");
        Config riceTestConfig = new RiceTestConfig("classpath:rice-test-client-config.xml");
        riceTestConfig.parseConfig();
		Core.init(riceTestConfig);
		long initTime = System.currentTimeMillis();
		report("Time to initialize Core: " + (initTime - startTime));
		lifeCycles = getLifecycles();
		try {
			startLifecycles();
		} catch (Exception e) {
            e.printStackTrace();
			stopLifecycles();
            throw e;
		}
		report("Time to start all Lifecycles: " + (System.currentTimeMillis() - initTime));
		
		try {
            loadTestDataTransactionally();
		} catch (WrappedTransactionRuntimeException e) {
			throw (Exception) e.getCause();
		}
		//this is to make testing against the queue easier.  Otherwise unprocessed cache clearing 
		//can make queue counts off.
//		waitForCacheNotificationsToClearFromQueue();
		isSetUp = true;
	}
	
	/**
	 * Can be overridden to allow for specification of the client protocol to use in the test.  If none is specified, then the 
	 * default protocol is embedded.
	 */
	protected String getClientProtocol() {
		return null;
	}
	

	protected void setUpTransaction() throws Exception {
		// subclasses can override this method to do their setup within a
		// transaction
	}

	@After public void tearDown() throws Exception {
		stopLifecycles();
		Core.destroy();
		//super.tearDown();
		isTornDown = true;
		afterRun();
	}

	protected void beforeRun() {
		System.out.println("##############################################################");
		System.out.println("# Starting test " + getClass().getSimpleName()+"...");
		System.out.println("# " + dumpMemory());
		System.out.println("##############################################################");
	}
	
	protected void afterRun() {
		System.out.println("##############################################################");
		System.out.println("# ...finished test " + getClass().getSimpleName());
		System.out.println("# " + dumpMemory());
		for (String report : reports) {
			System.out.println("# " + report);
		}
		System.out.println("##############################################################\n\n\n");
	}
    
	/**
	 * By default this loads the "default" data set from the data/TestData.xml
	 * file. Subclasses can override this to change this behaviour
	 */
	protected void loadDefaultTestData() throws Exception {
		this.loadXmlFile(RiceTestCase.class, "DefaultTestData.xml");
        new SQLDataLoader("DefaultTestData.sql").runSql();
	}

    protected void loadTestDataTransactionally() throws Exception {
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(final TransactionStatus status) {
                getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
                    public Object doInPersistenceBroker(PersistenceBroker broker) {
                        try {
                            long t1 = System.currentTimeMillis();
                            //we need the messaging to be default of only sending message out with the transaction
                            //otherwise the importing of constants can get messed up with the digital signature 
                            //constant's notification of the other nodes.
                            Core.getCurrentContextConfig().overrideProperty(EdenConstants.MESSAGE_PERSISTENCE, "default");
                            loadDefaultTestData();
                            Core.getCurrentContextConfig().overrideProperty(EdenConstants.MESSAGE_PERSISTENCE, EdenConstants.MESSAGING_SYNCHRONOUS);
                            
                            long t2 = System.currentTimeMillis();
                            report("Time to load default test data: " + (t2-t1));
                            
                            loadTestData();
                            
                            long t3 = System.currentTimeMillis();
                            report("Time to load test-specific test data: " + (t3-t2));
                            
                            setUpTransaction();
                            
                            long t4 = System.currentTimeMillis();
                            report("Time to run test-specific setup: " + (t4-t3));
                        } catch (Exception e) {
                            throw new WrappedTransactionRuntimeException(e);
                        }
                        return null;
                    }
                });
            }
        });
    }
    
	/**
	 * @Override
	 */
	protected void loadTestData() throws Exception {
		// override this to load your own test data
	}

	protected void loadXmlFile(String fileName) {
		if (fileName.indexOf('/') < 0) {
			this.loadXmlFile(getClass(), fileName);
		} else {
			loadXmlStream(getClass().getClassLoader().getResourceAsStream(fileName));
		}
	}

	protected void loadXmlFile(Class clazz, String fileName) {
		InputStream xmlFile = loadResource(clazz, fileName);
		if (xmlFile == null) {
			throw new RuntimeException("Didn't find file " + fileName);
		}
		loadXmlStream(xmlFile);
	}

	protected void loadXmlStream(InputStream xmlStream) {
		try {
			List<XmlDocCollection> xmlFiles = new ArrayList<XmlDocCollection>();
			XmlDocCollection docCollection = getFileXmlDocCollection(xmlStream, "RiceTestTemp");
			xmlFiles.add(docCollection);
			KEWServiceLocator.getXmlIngesterService().ingest(xmlFiles);
			for (Iterator iterator = docCollection.getXmlDocs().iterator(); iterator.hasNext();) {
				XmlDoc doc = (XmlDoc) iterator.next();
				if (!doc.isProcessed()) {
					fail("Failed to ingest xml doc: " + doc.getName());
				}
			}
//			Thread.sleep(4000);//let the cache catch up
		} catch (Exception e) {
			throw new RuntimeException("Caught exception parsing xml file", e);
		}
		
	}

	protected FileXmlDocCollection getFileXmlDocCollection(InputStream xmlFile, String tempFileName) throws IOException {
		if (xmlFile == null) {
			throw new RuntimeException("Didn't find the xml file " + tempFileName);
		}
		File temp = File.createTempFile(tempFileName, ".xml");
		FileOutputStream fos = new FileOutputStream(temp);
		int data = -1;
		while ((data = xmlFile.read()) != -1) {
			fos.write(data);
		}
		fos.close();
		return new FileXmlDocCollection(temp);
	}

	private class WrappedTransactionRuntimeException extends RuntimeException {

		private static final long serialVersionUID = 3097909333572509600L;

		public WrappedTransactionRuntimeException(Exception e) {
			super(e);
		}
	}

	/**
	 * Subclasses can override this to use a custom Spring context
	 */
	protected String getAltAppContextFile() {
		return "TestSpring.xml";
	}
	
	public String getOjbPropertyFileLoc() {
		return "TestOJB.properties";
	}
	
	public void startLifecycles() throws Exception {
		for (Iterator iter = lifeCycles.iterator(); iter.hasNext();) {
			long s = System.currentTimeMillis();
			Lifecycle lifecycle = (Lifecycle) iter.next();
            try {
                lifecycle.start();    
            } catch (Exception e) {
                throw new Exception("Failed to start lifecycle " + lifecycle, e);
            }
			long e = System.currentTimeMillis();
            LOG.info("Started lifecycle " + lifecycle + " in " + (e-s) + " ms.");
			report("Time to start lifecycle " + lifecycle + ": " + (e-s));
		}
	}
	
	public void stopLifecycles() throws Exception {
		Lifecycle lifeCycle;
		ListIterator iter = lifeCycles.listIterator();
		while(iter.hasNext()) {
			iter.next();
		}
		while(iter.hasPrevious()) {
			lifeCycle = (Lifecycle) iter.previous();
			try {
				lifeCycle.stop();
			} catch (Exception e) {
                LOG.warn("Failed to shutdown one of the lifecycles!", e);
			}
		}
	}
	
	public List<Lifecycle> getLifecycles() {
		//set up an alternate workflow config file location so the workflow server will not load the default
		System.setProperty(EdenConstants.DEFAULT_CONFIG_LOCATION_PARAM, TEST_CONFIG_LOCATION);
		lifeCycles.add(new ClearDatabaseLifecycle());
		lifeCycles.add(new TestHarnessWebApp());
		return lifeCycles;
	}
    
	protected void report(String report) {
		reports.add(report);
	}
	
	private String dumpMemory() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long max = Runtime.getRuntime().maxMemory();
		return "[Memory] max: " + max + ", total: " + total + ", free: " + free;
	}


	public boolean isSetUp() {
		return isSetUp;
	}


	public boolean isTornDown() {
		return isTornDown;
	}
    
    public static PersistenceBrokerTemplate getPersistenceBrokerTemplate() {
        return new PersistenceBrokerTemplate();
    }
    
    /**
     * wait until the route queue is empty because we may have some cache items to be picked up still.
     * 
     * @throws Exception
     */
    public static void waitForCacheNotificationsToClearFromQueue() throws Exception {
        int iterations = 0;
        while (true) {
            int itemCount = KEWServiceLocator.getRouteQueueService().findAll().size();
            if (itemCount == 0) {
                break;
            }
            if (iterations > 20) {
                throw new WorkflowRuntimeException("Waited too long for route queue to clear out cache notifications");
            }
            iterations++;
            System.out.println("!!!Sleeping for 1 second to let cache notifications clear out");
            Thread.sleep(1000);
        }
    }
    
    public static InputStream loadResource(Class packageClass, String resourceName) {
        return packageClass.getResourceAsStream(resourceName);
    }

    protected TransactionTemplate getTransactionTemplate() {
        return (TransactionTemplate) KNSServiceLocator.getTransactionTemplate();
    }
    
}