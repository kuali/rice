package org.kuali.rice.ojb;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.JdbcAccess;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.SequenceDescriptor;
import org.apache.ojb.broker.util.sequence.AbstractSequenceManager;
import org.apache.ojb.broker.util.sequence.SequenceManager;
import org.apache.ojb.broker.util.sequence.SequenceManagerException;
import org.apache.ojb.broker.util.sequence.SequenceManagerNextValImpl;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.ClassLoaderUtils;


/**
 * A sequence manager implementation which can be configured at runtime via the KEW
 * Configuration API.
 *
 * @author ewestfal
 */
public class ConfigurableSequenceManager implements SequenceManager {

	private static final String PROPERTY_PREFIX_ATTRIBUTE = "property.prefix";
	private static final String DEFAULT_PROPERTY_PREFIX = "datasource.ojb.sequenceManager";
	private static final String DEFAULT_SEQUENCE_MANAGER_CLASSNAME = SequenceManagerNextValImpl.class.getName();

	private PersistenceBroker broker;
	private SequenceManager sequenceManager;

	public ConfigurableSequenceManager(PersistenceBroker broker) {
		this.broker = broker;
		this.sequenceManager = createSequenceManager(broker);
	}

	protected SequenceManager createSequenceManager(PersistenceBroker broker) {
		String propertyPrefix = getPropertyPrefix();
		String sequenceManagerClassName = Core.getCurrentContextConfig().getProperty(getSequenceManagerClassNameProperty(propertyPrefix));
		if (StringUtils.isBlank(sequenceManagerClassName)) {
			sequenceManagerClassName = DEFAULT_SEQUENCE_MANAGER_CLASSNAME;
		}
		try {
			Class sequenceManagerClass = ClassLoaderUtils.getDefaultClassLoader().loadClass(sequenceManagerClassName);
			Object sequenceManagerObject = ConstructorUtils.invokeConstructor(sequenceManagerClass, broker);
			if (!(sequenceManagerObject instanceof SequenceManager)) {
				throw new ConfigurationException("The configured sequence manager ('" + sequenceManagerClassName + "') is not an instance of '" + SequenceManager.class.getName() + "'");
			}
			SequenceManager sequenceManager = (SequenceManager)sequenceManagerObject;
			if (sequenceManager instanceof AbstractSequenceManager) {
				((AbstractSequenceManager)sequenceManager).setConfigurationProperties(getSequenceManagerConfigProperties(propertyPrefix));
			}
			return sequenceManager;
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Could not locate sequence manager with the given class '" + sequenceManagerClassName + "'");
		} catch (Exception e) {
			throw new ConfigurationException("Property loading sequence manager class '" + sequenceManagerClassName + "'", e);
		}
	}

	protected String getSequenceManagerClassNameProperty(String propertyPrefix) {
		return propertyPrefix + ".className";
	}

	protected SequenceManager getConfiguredSequenceManager() {
		return this.sequenceManager;
	}

	protected Properties getSequenceManagerConfigProperties(String propertyPrefix) {
		Properties sequenceManagerProperties = new Properties();
		Properties properties = Core.getCurrentContextConfig().getProperties();
		String attributePrefix = propertyPrefix + ".attribute.";
		for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (key.startsWith(attributePrefix)) {
				String value = properties.getProperty(key);
				String attributeName = key.substring(attributePrefix.length());
				sequenceManagerProperties.setProperty(attributeName, value);
			}
		}
		return sequenceManagerProperties;
	}



	public void afterStore(JdbcAccess jdbcAccess, ClassDescriptor classDescriptor, Object object) throws SequenceManagerException {
		getConfiguredSequenceManager().afterStore(jdbcAccess, classDescriptor, object);
	}

	public Object getUniqueValue(FieldDescriptor fieldDescriptor) throws SequenceManagerException {
		return getConfiguredSequenceManager().getUniqueValue(fieldDescriptor);
	}

	public PersistenceBroker getBroker() {
		return this.broker;
	}

	public String getPropertyPrefix() {
		SequenceDescriptor sd = getBroker().serviceConnectionManager().getConnectionDescriptor().getSequenceDescriptor();
		String propertyPrefix = null;
		if (sd != null) {
			propertyPrefix = sd.getConfigurationProperties().getProperty(PROPERTY_PREFIX_ATTRIBUTE);
		}
		if (StringUtils.isBlank(propertyPrefix)) {
			propertyPrefix = DEFAULT_PROPERTY_PREFIX;
		}
		return propertyPrefix;
	}
}
