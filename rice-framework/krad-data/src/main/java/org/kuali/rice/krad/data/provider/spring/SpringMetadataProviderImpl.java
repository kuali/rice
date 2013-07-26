package org.kuali.rice.krad.data.provider.spring;

import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.provider.impl.MetadataProviderBase;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Metadata provider which can be configured via the standard spring mechanisms. The bean locations are listed as part
 * of the metadata provider service definition. The beans in this provider are loaded into a separate context from all
 * other beans in Rice.
 * 
 * @author jonathan
 * 
 */
public class SpringMetadataProviderImpl extends MetadataProviderBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SpringMetadataProviderImpl.class);

	protected List<String> resourceLocations;
	protected DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
	protected DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	public SpringMetadataProviderImpl() {
		LOG.debug("Building SpringMetadataProviderImpl");
	}

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
					((DataObjectMetadataImpl) metadata).setProvider(this);
				}
				masterMetadataMap.put(metadata.getType(), metadata);
			} else {
				LOG.error("Configuration Error.  MetadataObject in the Spring context contained a null DataObjectType reference: "
						+ metadata);
			}
		}
	}

	public List<String> getResourceLocations() {
		return resourceLocations;
	}

	public void setResourceLocations(List<String> resourceLocations) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resource locations set to: " + resourceLocations);
		}
		this.resourceLocations = resourceLocations;
	}

	@Override
	public String toString() {
		return getClass().getName() + " : " + resourceLocations;
	}
}
