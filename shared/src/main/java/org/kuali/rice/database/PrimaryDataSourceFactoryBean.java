package org.kuali.rice.database;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.jndi.JndiTemplate;

public class PrimaryDataSourceFactoryBean extends AbstractFactoryBean {

	private TransactionManager transactionManager;
	private JndiTemplate jndiTemplate;

	public PrimaryDataSourceFactoryBean() {
		setSingleton(true);
	}

	public Class getObjectType() {
		return DataSource.class;
	}

	@Override
	protected Object createInstance() throws Exception {
		Config config = Core.getCurrentContextConfig();
		DataSource dataSource = (DataSource)config.getObject(Config.DATASOURCE_OBJ);
		if (dataSource == null) {
			String dataSourceJndiName = Core.getCurrentContextConfig().getProperty(Config.DATASOURCE_JNDI);
			if (!StringUtils.isEmpty(dataSourceJndiName)) {
				if (this.jndiTemplate == null) {
				    this.jndiTemplate = new JndiTemplate();
				}
				try {
					dataSource = (DataSource)this.jndiTemplate.lookup(dataSourceJndiName, DataSource.class);
				} catch (NamingException e) {
					throw new ConfigurationException("Could not locate the DataSource at the given JNDI location: '" + dataSourceJndiName + "'", e);
				}
			}

		}
		if (dataSource != null) {
			return dataSource;
		}
		if (this.transactionManager == null) {
			throw new ConfigurationException("A transactionManager must be specified!");
		}
		return createDefaultDataSource(config, this.transactionManager);
	}

	protected DataSource createDefaultDataSource(Config config, TransactionManager transactionManager) throws Exception {
		XAPoolDataSource dataSource = new XAPoolDataSource();
		dataSource.setTransactionManager(transactionManager);

		dataSource.setDriverClassName(getStringProperty(config, Config.DATASOURCE_DRIVER_NAME));
		dataSource.setUrl(getStringProperty(config, Config.DATASOURCE_URL));
		dataSource.setMaxSize(getIntProperty(config, Config.DATASOURCE_POOL_MAXSIZE));
		dataSource.setMinSize(getIntProperty(config, Config.DATASOURCE_POOL_MINSIZE));
		dataSource.setMaxWait(getIntProperty(config, Config.DATASOURCE_POOL_MAXWAIT));
		dataSource.setValidationQuery(getStringProperty(config, Config.DATASOURCE_POOL_VALIDATION_QUERY));
		dataSource.setUsername(getStringProperty(config, Config.DATASOURCE_USERNAME));
		dataSource.setPassword(getStringProperty(config, Config.DATASOURCE_PASSWORD));

		dataSource.start();

		return dataSource;
	}

	protected void destroyInstance(Object instance) throws Exception {
		if (instance instanceof Lifecycle) {
			((Lifecycle)instance).stop();
		}
	}

	protected String getStringProperty(Config config, String propertyName) {
		String data = config.getProperty(propertyName);
		if (StringUtils.isEmpty(data)) {
			throw new ConfigurationException("Could not locate a value for the given property '" + propertyName + "'.");
		}
		return data;
	}

	protected int getIntProperty(Config config, String propertyName) {
		String data = getStringProperty(config, propertyName);
		try {
			int intData = Integer.parseInt(data);
			return intData;
		} catch (NumberFormatException e) {
			throw new ConfigurationException("The given property '" + propertyName + "' was not a valid integer.  Value was '" + data + "'");
		}
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public JndiTemplate getJndiTemplate() {
		return this.jndiTemplate;
	}

	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}

}
