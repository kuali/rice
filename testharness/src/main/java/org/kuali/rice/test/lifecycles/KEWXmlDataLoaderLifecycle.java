package org.kuali.rice.test.lifecycles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.FileXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDoc;
import edu.iu.uis.eden.batch.XmlDocCollection;

/**
 * A lifecycle for loading KEW XML datasets.
 * 
 * @author natjohns
 */
public class KEWXmlDataLoaderLifecycle implements Lifecycle {

	private boolean started;

	private String filename;

	public KEWXmlDataLoaderLifecycle() {
		this("classpath:DefaultTestData.xml");
	}

	public KEWXmlDataLoaderLifecycle(String filename) {
		this.filename = filename;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		if (new Boolean(Core.getCurrentContextConfig().getProperty("use.kewXmlmlDataLoaderLifecycle"))) {
			loadDefaultTestData();
			started = true;
		}
	}

	public void stop() throws Exception {
		started = false;
	}

	/**
	 * By default this loads the "default" data set from the DefaultTestData.sql
	 * and DefaultTestData.xml files. Subclasses can override this to change
	 * this behaviour or pass in a filename to the constructor
	 */
	protected void loadDefaultTestData() throws Exception {
		this.loadXmlFile(filename);
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
				throw new RuntimeException("Failed to ingest xml doc: " + doc.getName());
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
