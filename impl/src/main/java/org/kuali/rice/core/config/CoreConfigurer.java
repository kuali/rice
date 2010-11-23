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
package org.kuali.rice.core.config;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.web.servlet.dwr.GlobalResourceDelegatingSpringCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * This is a place to put some of the common configuration logic that used to be done by the RiceConfigurer.
 */
public class CoreConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean, BeanFactoryAware {

	private BeanFactory beanFactory;
	private Config config;

	private DataSource dataSource;
	private DataSource nonTransactionalDataSource;
	private DataSource serverDataSource;
	private String platform;
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;
	private CredentialsSourceFactory credentialsSourceFactory;
	
	
	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	public Config getConfig() {
		return this.config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public void destroy() throws Exception {
		stop();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}
	
	@Override
	public void start() throws Exception {
		initializeFullConfiguration();
		super.start();
	}

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		GlobalResourceDelegatingSpringCreator.APPLICATION_BEAN_FACTORY = getBeanFactory(); 
		
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		 if (isConfigureLogging()) {
			 lifecycles.add(new Log4jLifeCycle());
		 }
		 
		 return lifecycles;
	}
	
	protected void initializeFullConfiguration() throws Exception {
		configureJta(config);
		configureDataSource(config);
		configureCredentialsSourceFactory(config);
	}
	
	protected boolean isConfigureLogging() {
		return ConfigContext.getCurrentContextConfig().getBooleanProperty(RiceConstants.RICE_LOGGING_CONFIGURE, false);
	}
	
	protected void configureCredentialsSourceFactory(final Config rootConfig) {
		if (credentialsSourceFactory != null) {
			rootConfig.putObject(Config.CREDENTIALS_SOURCE_FACTORY, this.credentialsSourceFactory);
		}
	}
 
	protected void configureDataSource(Config config) {
		if (this.dataSource != null) {
			config.putObject(RiceConstants.DATASOURCE_OBJ, this.dataSource);
		}
		
        if (this.nonTransactionalDataSource != null) {
            config.putObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ, this.nonTransactionalDataSource);
        }
        
        if (this.serverDataSource != null) {
        	config.putObject(RiceConstants.SERVER_DATASOURCE_OBJ, this.serverDataSource);
        }
	}

	/**
	 * If the user injected JTA classes into this configurer, verify that both the
	 * UserTransaction and TransactionManager are set and then attach them to
	 * the configuration.
	 */
	protected void configureJta(Config config) {
		if (this.userTransaction != null) {
			config.putObject(RiceConstants.USER_TRANSACTION_OBJ, this.userTransaction);
		}
		if (this.transactionManager != null) {
			config.putObject(RiceConstants.TRANSACTION_MANAGER_OBJ, this.transactionManager);
		}
		boolean userTransactionConfigured = this.userTransaction != null || !StringUtils.isEmpty(config.getProperty(RiceConstants.USER_TRANSACTION_JNDI));
		boolean transactionManagerConfigured = this.transactionManager != null || !StringUtils.isEmpty(config.getProperty(RiceConstants.TRANSACTION_MANAGER_JNDI));
		if (userTransactionConfigured && !transactionManagerConfigured) {
			throw new ConfigurationException("When configuring JTA, both a UserTransaction and a TransactionManager are required.  Only the UserTransaction was configured.");
		}
		if (transactionManagerConfigured && !userTransactionConfigured) {
			throw new ConfigurationException("When configuring JTA, both a UserTransaction and a TransactionManager are required.  Only the TransactionManager was configured.");
		}
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

    public DataSource getNonTransactionalDataSource() {
        return this.nonTransactionalDataSource;
    }

    public void setNonTransactionalDataSource(DataSource nonTransactionalDataSource) {
        this.nonTransactionalDataSource = nonTransactionalDataSource;
    }

    public DataSource getServerDataSource() {
		return this.serverDataSource;
	}

	public void setServerDataSource(DataSource serverDataSource) {
		this.serverDataSource = serverDataSource;
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public UserTransaction getUserTransaction() {
		return this.userTransaction;
	}

	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	public CredentialsSourceFactory getCredentialsSourceFactory() {
		return credentialsSourceFactory;
	}

	public void setCredentialsSourceFactory(
			final CredentialsSourceFactory credentialsSourceFactory) {
		this.credentialsSourceFactory = credentialsSourceFactory;
	}


}
