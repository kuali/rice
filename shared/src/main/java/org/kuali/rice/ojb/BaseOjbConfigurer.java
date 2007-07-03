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
package org.kuali.rice.ojb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.util.ClassLoaderUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Base Ojb Configurer implementation which configures OJB for a particular rice module.
 *
 * @author ewestfal
 */
public abstract class BaseOjbConfigurer extends BaseLifecycle {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseOjbConfigurer.class);

	private static final String OJB_PROPERTIES_PROP = "OJB.properties";
	private static final String DEFAULT_OJB_PROPERTIES = "org/kuali/rice/ojb/RiceOJB.properties";

	@Override
	public void start() throws Exception {
		// if OJB has not already been loaded, let's trigger a load using our built-in OJB properties file
		String currentValue = System.getProperty(OJB_PROPERTIES_PROP);
		try {
			System.setProperty(OJB_PROPERTIES_PROP, DEFAULT_OJB_PROPERTIES);
			MetadataManager mm = MetadataManager.getInstance();
			establishConnectionMetaData(mm);
			establishRepositoryMetaData(mm);
		} finally {
			if (currentValue == null) {
				System.getProperties().remove(OJB_PROPERTIES_PROP);
			} else {
				System.setProperty(OJB_PROPERTIES_PROP, currentValue);
			}
		}
		super.start();
	}

	protected String getOjbPropertiesLocation() {
		return DEFAULT_OJB_PROPERTIES;
	}

	protected void establishConnectionMetaData(MetadataManager mm) throws Exception {
		String connMetadata = getMetadataLocation();
		if (StringUtils.isBlank(connMetadata)) {
			LOG.info("No OJB connection metadata loaded.");
			return;
		}
		 if (!isConnectionAlreadyConfigured(mm)) {
			 LOG.info("Loading OJB Connection Metadata from " + connMetadata);
			 DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
			 InputStream is = resourceLoader.getResource(connMetadata).getInputStream();
			 is = preprocessConnectionMetadata(is);
			 ConnectionRepository cr = mm.readConnectionRepository(is);
			 mm.mergeConnectionRepository(cr);
			 try {
                is.close();
			 } catch (Exception e) {
				 LOG.warn("Failed to close stream to file " + connMetadata, e);
			 }
	    } else {
        	LOG.info("OJB Connection already configured for jcd alias '" + getJcdAlias() + "', skipping Metadata merge.");
        }
	}

	protected InputStream preprocessConnectionMetadata(InputStream inputStream) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputStream));
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList connectionDescriptors = (NodeList)xpath.evaluate("/descriptor-repository/jdbc-connection-descriptor", document, XPathConstants.NODESET);
		for (int index = 0; index < connectionDescriptors.getLength(); index++) {
			Element descriptor = (Element)connectionDescriptors.item(index);
			String currentPlatform = descriptor.getAttribute("platform");
			if (StringUtils.isBlank(currentPlatform)) {
				String ojbPlatform = Core.getCurrentContextConfig().getProperty(Config.OJB_PLATFORM);
				if (StringUtils.isEmpty(ojbPlatform)) {
					throw new ConfigurationException("Could not configure OJB, the '" + Config.OJB_PLATFORM + "' configuration property was not set.");
				}
				LOG.info("Setting " + getJcdAlias() + " OJB connection descriptor database platform to '" + ojbPlatform + "'");
				descriptor.setAttribute("platform", ojbPlatform);
			}
		}
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(document), new StreamResult(new BufferedOutputStream(baos)));
		return new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
	}

	protected boolean isConnectionAlreadyConfigured(MetadataManager mm) {
		List descriptors = mm.connectionRepository().getAllDescriptor();
		for (Iterator iterator = descriptors.iterator(); iterator.hasNext();) {
			JdbcConnectionDescriptor descriptor = (JdbcConnectionDescriptor) iterator.next();
			if (descriptor.getJcdAlias().equals(getJcdAlias())) {
				return true;
			}
		}
		return false;
	}

	protected InputStream preprocessRepositoryMetadata(InputStream inputStream) throws Exception {
		return inputStream;
	}

	protected void establishRepositoryMetaData(MetadataManager mm) throws Exception {
		String repoMetadata = getMetadataLocation();
		if (StringUtils.isBlank(repoMetadata)) {
			LOG.info("No OJB repository metadata loaded.");
			return;
		}
		LOG.info("Loading OJB Metadata from " + repoMetadata);
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
		InputStream is = resourceLoader.getResource(repoMetadata).getInputStream();
        is = preprocessRepositoryMetadata(is);
		DescriptorRepository dr = mm.readDescriptorRepository(is);
        mm.mergeDescriptorRepository(dr);
        try {
        	is.close();
        } catch (Exception e) {
        	LOG.warn("Failed to close stream to file " + repoMetadata, e);
        }
	}

	/**
	 * Return the jcd alias of the connection loaded by the connection metadata.
	 * @return
	 */
	protected abstract String getJcdAlias();

	/**
	 * Should return a String representing the location of a file to load OJB connection and
	 * repository metadata from.  If null or empty than no metadata will be loaded.
	 */
	protected abstract String getMetadataLocation();

//	protected void establishConnectionMetaData(MetadataManager mm) throws Exception {
//	ConnectionRepository repository = getConnectionRepository();
//	if (repository == null) {
//		throw new ConfigurationException("Could not load OJB connection repository");
//	}
//	mm.mergeConnectionRepository(repository);
//}

//protected ConnectionRepository getConnectionRepository() {
//	ConnectionRepository repository = new ConnectionRepository();
//	JdbcConnectionDescriptor descriptor = new JdbcConnectionDescriptor();
//	descriptor.setJdbcLevel("3.0");
//	descriptor.setEagerRelease(false);
//	descriptor.setBatchMode(false);
//	descriptor.setUseAutoCommit(0);
//	descriptor.setIgnoreAutoCommitExceptions(false);
//	descriptor.setJcdAlias(getJcdAlias());
//	descriptor.setDefaultConnection(false);
//	String ojbPlatform = Core.getCurrentContextConfig().getProperty(Config.OJB_PLATFORM);
//	if (StringUtils.isEmpty(ojbPlatform)) {
//		throw new ConfigurationException("Could not configure OJB, the '" + Config.OJB_PLATFORM + "' configuration property was not set.");
//	}
//	if (StringUtils.isBlank(descriptor.getDbms())) {
//		log.info("Setting " + descriptor.getJcdAlias() + " OJB connection descriptor database platform to '" + ojbPlatform + "'");
//		descriptor.setDbms(ojbPlatform);
//	}
//
//	SequenceDescriptor sequenceDescriptor = new SequenceDescriptor(descriptor);
//	sequenceDescriptor.addAttribute("property.prefix", "datasource.ojb.sequenceManager");
//	sequenceDescriptor.setSequenceManagerClass(ConfigurableSequenceManager.class);
//	descriptor.setSequenceDescriptor(sequenceDescriptor);
//
//	ObjectCacheDescriptor cacheDescriptor = descriptor.getObjectCacheDescriptor();
//	cacheDescriptor.setObjectCache(ObjectCachePerBrokerImpl.class);
//
//	repository.addDescriptor(descriptor);
//	return repository;
//
//
//}


}
