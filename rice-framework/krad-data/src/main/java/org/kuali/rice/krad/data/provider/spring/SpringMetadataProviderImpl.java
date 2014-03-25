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
package org.kuali.rice.krad.data.provider.spring;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.provider.impl.MetadataProviderBase;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * Metadata provider which can be configured via the standard spring mechanisms.
 *
 * <p>
 * The bean locations are listed as part of the metadata provider service definition. The beans in this provider are
 * loaded into a separate context from all other beans in Rice.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class SpringMetadataProviderImpl extends MetadataProviderBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SpringMetadataProviderImpl.class);

    /**
     * The locations of the bean resources.
     */
	protected List<String> resourceLocations;

    /**
     * The default resource loader to use.
     */
	protected DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());

    /**
     * The default bean factory to use.
     */
	protected DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    /**
     * Creates a metadata provider which can be configured via the standard spring mechanisms.
     */
	public SpringMetadataProviderImpl() {
		LOG.debug("Building SpringMetadataProviderImpl");
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public synchronized void initializeMetadata(Collection<Class<?>> types) {
		// First, extract the data from the spring configuration into a form usable by the Spring XML parser
		if (LOG.isDebugEnabled()) {
			LOG.debug("Loading Metadata Bean Definitions from Locations:");
			for (String loc : resourceLocations) {
				LOG.debug(loc);
			}
		}
		// Now, parse the beans and load them into the bean factory
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(beanFactory);
		String configFileLocationsArray[] = new String[resourceLocations.size()];
		configFileLocationsArray = resourceLocations.toArray(configFileLocationsArray);
		xmlReader.loadBeanDefinitions(configFileLocationsArray);

		// extract the objects from the bean factory, by pulling all the DataObjectMetadata objects
		Map<String, DataObjectMetadata> metadataObjects = beanFactory.getBeansOfType(DataObjectMetadata.class);
		if (LOG.isInfoEnabled()) {
			LOG.info(metadataObjects.size() + " DataObjectMetadata objects in Spring configuration files");
		}
		// populate the map
		masterMetadataMap.clear();
		for (DataObjectMetadata metadata : metadataObjects.values()) {
			if (metadata.getType() != null) {
				if (metadata instanceof DataObjectMetadataImpl) {
					((DataObjectMetadataImpl) metadata).setProviderName(this.getClass().getSimpleName());
				}
				masterMetadataMap.put(metadata.getType(), metadata);
			} else {
				LOG.error("Configuration Error.  MetadataObject in the Spring context contained a null DataObjectType reference: "
						+ metadata);
			}
		}
	}

    /**
     * Gets the locations of the bean resources.
     *
     * @return the locations of the bean resources.
     */
	public List<String> getResourceLocations() {
		return resourceLocations;
	}

    /**
     * Setter for the resource locations.
     *
     * @param resourceLocations the resource locations to set.
     */
	public void setResourceLocations(List<String> resourceLocations) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resource locations set to: " + resourceLocations);
		}
		this.resourceLocations = resourceLocations;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		return getClass().getName() + " : " + resourceLocations;
	}
}
