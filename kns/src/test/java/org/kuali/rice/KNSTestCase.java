package org.kuali.rice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.FileXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDoc;
import edu.iu.uis.eden.batch.XmlDocCollection;

public class KNSTestCase extends RiceTestCase {
	
	private JettyServer jettyServer = new JettyServer(9912);
	private static final String TEST_CONFIG_FILE = "classpath:META-INF/kns-test-config.xml";


	@Override
	public List<Lifecycle> getPerTestLifecycles() {
		return new ArrayList<Lifecycle>();
	}

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getPerTestLifecycles();
		lifeCycles.add(new Lifecycle() {
			boolean started = false;
			public boolean isStarted() {
				return this.started;
			}
			public void start() throws Exception {
				RiceConfigurer.setConfigurationFile(TEST_CONFIG_FILE);
				ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = TEST_CONFIG_FILE;

				jettyServer.start();
				//we should put this somewhere in the test harness...
				Map<ClassLoader, Config> configs = Core.getCONFIGS();
				for (Map.Entry<ClassLoader, Config> configEntry : configs.entrySet()) {
					if (configEntry.getKey() instanceof WebAppClassLoader) {
						ResourceLoader rl = GlobalResourceLoader.getResourceLoader(configEntry.getKey());
						if (rl == null) {
							fail("didn't find resource loader for workflow test harness web app");
						}
						GlobalResourceLoader.addResourceLoader(rl);
						configs.put(Thread.currentThread().getContextClassLoader(), configEntry.getValue());
					}
				}
				new SQLDataLoader("classpath:DefaultTestData.sql", ";").runSql();
			    loadDefaultTestData();
				this.started = true;
			}
			public void stop() throws Exception {
			    this.started = false;
			}
		});
		
		return lifeCycles;
	}

	@Override
	protected List<String> getConfigLocations() {
		return Arrays.asList(new String[]{TEST_CONFIG_FILE});
	}

	@Override
	protected String getDerbySQLFileLocation() {
		return "classpath:db/derby/kns.sql";
	}

	@Override
	protected String getModuleName() {
		return "kns";
	}
	
	/**
	 * By default this loads the "default" data set from the DefaultTestData.sql
	 * and DefaultTestData.xml files. Subclasses can override this to change
	 * this behaviour
	 */
	protected void loadDefaultTestData() throws Exception {
		this.loadXmlFile("classpath:DefaultTestData.xml");
	}

	protected void loadXmlFile(String fileName) throws Exception {
		Resource resource = new DefaultResourceLoader().getResource(fileName);
		InputStream xmlFile = resource.getInputStream();
		if (xmlFile == null) {
			throw new ConfigurationException("Didn't find file " + fileName);
		}
		List<XmlDocCollection> xmlFiles = new ArrayList<XmlDocCollection>();
		XmlDocCollection docCollection = getFileXmlDocCollection(xmlFile, "UnitTestTemp");
		xmlFiles.add(docCollection);
		KEWServiceLocator.getXmlIngesterService().ingest(xmlFiles);
		for (Iterator iterator = docCollection.getXmlDocs().iterator(); iterator.hasNext();) {
			XmlDoc doc = (XmlDoc) iterator.next();
			if (!doc.isProcessed()) {
				fail("Failed to ingest xml doc: " + doc.getName());
			}
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
	
}