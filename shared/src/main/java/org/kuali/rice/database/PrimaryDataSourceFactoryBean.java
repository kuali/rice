/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.database;

import java.util.ArrayList;
import java.util.List;

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

/**
 * A class that can be used to load the primary datasource for a Rice module
 * from an object in the Config system or from a JNDI url specified by the
 * Configuration system.  By default, it loads these values from the
 * follwing properties:
 *
 * <ul>
 *   <li>{@link Config#DATASOURCE_OBJ}</li>
 *   <li>{@link Config#DATASOURCE_JNDI}</li>
 * </ul>
 *
 * <p>The config properties checked can be overridden by setting the
 *
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrimaryDataSourceFactoryBean extends AbstractFactoryBean {

	private static final String DEFAULT_DATASOURCE_PARAM = Config.DATASOURCE_OBJ;
	private static final String DEFAULT_DATASOURCE_JNDI_PARAM = Config.DATASOURCE_JNDI;

	private TransactionManager transactionManager;
	private JndiTemplate jndiTemplate;
	private String defaultDataSourceParam = DEFAULT_DATASOURCE_PARAM;
	private String defaultDataSourceJndiParam = DEFAULT_DATASOURCE_JNDI_PARAM;
	private List<String> preferredDataSourceParams = new ArrayList<String>();
	private List<String> preferredDataSourceJndiParams = new ArrayList<String>();

	public PrimaryDataSourceFactoryBean() {
		setSingleton(true);
	}

	public Class getObjectType() {
		return DataSource.class;
	}

	@Override
	protected Object createInstance() throws Exception {
		Config config = Core.getCurrentContextConfig();
		DataSource dataSource = createDataSource(config);
		if (dataSource != null) {
			return dataSource;
		}
		if (this.transactionManager == null) {
			throw new ConfigurationException("A transactionManager must be specified!");
		}
		return createDefaultDataSource(config, this.transactionManager);
	}

	protected DataSource createDataSource(Config config) throws Exception {
		DataSource dataSource = loadPreferredDataSourceFromConfig(config);
		if (dataSource == null) {
			Object dataSourceObject = config.getObject(getDefaultDataSourceParam());
			if (dataSourceObject != null) {
				validateDataSource(getDefaultDataSourceParam(), dataSourceObject);
				dataSource = (DataSource)dataSourceObject;
			} else {
				dataSource = getDataSourceFromJndi(config, getDefaultDataSourceJndiParam());
			}
		}
		return dataSource;
	}

	protected DataSource loadPreferredDataSourceFromConfig(Config config) {
		for (String dataSourceParam : getPreferredDataSourceParams()) {
			Object dataSource = config.getObject(dataSourceParam);
			if (dataSource != null) {
				validateDataSource(dataSourceParam, dataSource);
				return (DataSource)dataSource;
			}
		}
		if (this.jndiTemplate == null) {
		    this.jndiTemplate = new JndiTemplate();
		}
		for (String dataSourceJndiParam : getPreferredDataSourceJndiParams()) {
			DataSource dataSource = getDataSourceFromJndi(config, dataSourceJndiParam);
			if (dataSource != null) {
				return dataSource;
			}
		}
		return null;
	}

	protected void validateDataSource(String paramName, Object dataSourceObject) {
		if (!(dataSourceObject instanceof DataSource)) {
			throw new ConfigurationException("DataSource configured for parameter '" + paramName + "' was not an instance of DataSource.  Was instead " + dataSourceObject.getClass().getName());
		}
	}

	protected DataSource getDataSourceFromJndi(Config config, String dataSourceJndiParam) {
		String jndiName = config.getProperty(dataSourceJndiParam);
		if (!StringUtils.isBlank(jndiName)) {
			try {
				Object dataSource = getJndiTemplate().lookup(jndiName, DataSource.class);
				if (dataSource != null) {
					validateDataSource(dataSourceJndiParam, dataSource);
					return (DataSource)dataSource;
				}
			} catch (NamingException e) {
				throw new ConfigurationException("Could not locate the DataSource at the given JNDI location: '" + jndiName + "'", e);
			}
		}
		return null;
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

		dataSource.afterPropertiesSet();

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

	public String getDefaultDataSourceJndiParam() {
		return defaultDataSourceJndiParam;
	}

	public void setDefaultDataSourceJndiParam(String defaultDataSourceJndiParam) {
		this.defaultDataSourceJndiParam = defaultDataSourceJndiParam;
	}

	public String getDefaultDataSourceParam() {
		return defaultDataSourceParam;
	}

	public void setDefaultDataSourceParam(String defaultDataSourceParam) {
		this.defaultDataSourceParam = defaultDataSourceParam;
	}

	public List<String> getPreferredDataSourceJndiParams() {
		return preferredDataSourceJndiParams;
	}

	public void setPreferredDataSourceJndiParams(List<String> preferredDataSourceJndiParams) {
		this.preferredDataSourceJndiParams = preferredDataSourceJndiParams;
	}

	public List<String> getPreferredDataSourceParams() {
		return preferredDataSourceParams;
	}

	public void setPreferredDataSourceParams(List<String> preferredDataSourceParams) {
		this.preferredDataSourceParams = preferredDataSourceParams;
	}




}
