/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.core.config;

import java.util.List;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kcb.config.KCBConfigurer;
import org.kuali.rice.ken.config.KENConfigurer;
import org.kuali.rice.kew.config.KEWConfigurer;
import org.kuali.rice.kim.config.KIMConfigurer;
import org.kuali.rice.kns.config.KNSConfigurer;
import org.kuali.rice.kns.web.servlet.dwr.GlobalResourceDelegatingSpringCreator;
import org.kuali.rice.ksb.messaging.config.KSBConfigurer;

/**
 * Used to configure common Rice configuration properties.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceConfigurer extends RiceConfigurerBase {

	private static final Logger LOG = Logger.getLogger(RiceConfigurer.class);

	private DataSource dataSource;
	private DataSource nonTransactionalDataSource;
	private DataSource serverDataSource;
	private String platform;
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;
    private String dataSourceJndiLocation;
    private String nonTransactionalDataSourceJndiLocation;
    private String serverDataSourceJndiLocation;
	private String userTransactionJndiLocation;
	private String transactionManagerJndiLocation;
	private CredentialsSourceFactory credentialsSourceFactory;
	
	private KSBConfigurer ksbConfigurer;
	private KNSConfigurer knsConfigurer;
	private KIMConfigurer kimConfigurer;
	private KCBConfigurer kcbConfigurer;
	private KEWConfigurer kewConfigurer;
	private KENConfigurer kenConfigurer;
	
	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#start()
	 */
	public void start() throws Exception {
		//Add the configurers to modules list in the desired sequence.
		// and at the beginning if any other modules were specified
		int index = 0;
		if(getKsbConfigurer()!=null) getModules().add(index++,getKsbConfigurer());
		if(getKnsConfigurer()!=null) getModules().add(index++,getKnsConfigurer());
		if(getKimConfigurer()!=null) getModules().add(index++,getKimConfigurer());
		if(getKcbConfigurer()!=null) getModules().add(index++,getKcbConfigurer());
		if(getKewConfigurer()!=null) getModules().add(index++,getKewConfigurer());
		if(getKenConfigurer()!=null) getModules().add(index++,getKenConfigurer());
		// now execute the super class's start method which will initialize configuration and resource loaders
		super.start();
	}
	

	/**
	 * 
	 * This method decides the sequence of module resource loaders to be added to global resource loader (GRL).
	 * It asks the individual module configurers for the resource loader they want to register and adds them to GRL.
	 * 
	 * <p>TODO: the implementation of this method seems like a total HACK, it seems like the implementation on
	 * RiceConfigurerBase makes more sense since it is more general, also, very strange how the
	 * getResourceLoaderToRegister method on KEWConfigurer is side-affecting.  This whole thing looks like a mess.
	 * Somebody untangle this, please!
	 * 
	 * @throws Exception
	 */
	@Override
	protected void addModulesResourceLoaders() throws Exception {
		if(getKewConfigurer()!=null){
			// TODO: Check - In the method getResourceLoaderToRegister of KewConfigurer, 
			// does the call registry.start() depend on the preceding line GlobalResourceLoader.addResourceLoader(coreResourceLoader)?
			// Ideally we would like to register the resource loader into GRL over here
			getKewConfigurer().getResourceLoaderToRegister();
		}
		if(getKsbConfigurer()!=null){
			GlobalResourceLoader.addResourceLoader(getKsbConfigurer().getResourceLoaderToRegister());
		}
	}


	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 */
	protected List<Lifecycle> loadLifecycles() throws Exception {
		 GlobalResourceDelegatingSpringCreator.APPLICATION_BEAN_FACTORY = getBeanFactory();
		 return super.loadLifecycles();
	}
		    

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeBaseConfiguration(Config currentConfig) throws Exception {
		super.initializeBaseConfiguration(currentConfig);
		configureJta(currentConfig);
		configureDataSource(currentConfig);
		configurePlatform(currentConfig);
		configureCredentialsSourceFactory(currentConfig);
	}

	protected void configureCredentialsSourceFactory(final Config rootConfig) {
		if (credentialsSourceFactory != null) {
			rootConfig.putObject(Config.CREDENTIALS_SOURCE_FACTORY, this.credentialsSourceFactory);
		}
		
	}

	protected void configurePlatform(Config config) {
		if (!StringUtils.isBlank(this.platform)) {
			String platformClassName = "org.kuali.rice.core.database.platform."+this.platform+"DatabasePlatform";
			config.putProperty(Config.DATASOURCE_PLATFORM, platformClassName);
			config.putProperty(Config.OJB_PLATFORM, this.platform);
		}
	}
 
	protected void configureDataSource(Config config) {
		if (this.dataSource != null) {
			config.putObject(RiceConstants.DATASOURCE_OBJ, this.dataSource);
		} else if (!StringUtils.isBlank(this.dataSourceJndiLocation)) {
			config.putProperty(RiceConstants.DATASOURCE_JNDI, this.dataSourceJndiLocation);
		}
        if (this.nonTransactionalDataSource != null) {
            config.putObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ, this.nonTransactionalDataSource);
        } else if (!StringUtils.isBlank(this.nonTransactionalDataSourceJndiLocation)) {
            config.putProperty(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_JNDI, this.nonTransactionalDataSourceJndiLocation);
        }
        if (this.serverDataSource != null) {
        	config.putObject(RiceConstants.SERVER_DATASOURCE_OBJ, this.serverDataSource);
        }  else if (!StringUtils.isBlank(this.serverDataSourceJndiLocation)) {
        	config.putProperty(RiceConstants.SERVER_DATASOURCE_JNDI, this.serverDataSourceJndiLocation);
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
		if (!StringUtils.isEmpty(this.userTransactionJndiLocation)) {
			config.putProperty(RiceConstants.USER_TRANSACTION_JNDI, this.userTransactionJndiLocation);
		}
		if (!StringUtils.isEmpty(this.transactionManagerJndiLocation)) {
			config.putProperty(RiceConstants.TRANSACTION_MANAGER_JNDI, this.transactionManagerJndiLocation);
		}
		boolean userTransactionConfigured = this.userTransaction != null || !StringUtils.isEmpty(this.userTransactionJndiLocation);
		boolean transactionManagerConfigured = this.transactionManager != null || !StringUtils.isEmpty(this.transactionManagerJndiLocation);
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

	public void setDataSourceJndiLocation(String dataSourceJndiLocation) {
		this.dataSourceJndiLocation = dataSourceJndiLocation;
	}

    public void setNonTransactionalDataSourceJndiLocation(String nonTransactionalDataSourceJndiLocation) {
        this.nonTransactionalDataSourceJndiLocation = nonTransactionalDataSourceJndiLocation;
    }

	public void setServerDataSourceJndiLocation(String serverDataSourceJndiLocation) {
		this.serverDataSourceJndiLocation = serverDataSourceJndiLocation;
	}

	public String getTransactionManagerJndiLocation() {
		return this.transactionManagerJndiLocation;
	}

    public void setTransactionManagerJndiLocation(String transactionManagerJndiLocation) {
		this.transactionManagerJndiLocation = transactionManagerJndiLocation;
	}

	public String getUserTransactionJndiLocation() {
		return this.userTransactionJndiLocation;
	}

	public void setUserTransactionJndiLocation(String userTransactionJndiLocation) {
		this.userTransactionJndiLocation = userTransactionJndiLocation;
	}

	public CredentialsSourceFactory getCredentialsSourceFactory() {
		return credentialsSourceFactory;
	}

	public void setCredentialsSourceFactory(
			final CredentialsSourceFactory credentialsSourceFactory) {
		this.credentialsSourceFactory = credentialsSourceFactory;
	}

	/**
	 * @return the kcbConfigurer
	 */
	public KCBConfigurer getKcbConfigurer() {
		return this.kcbConfigurer;
	}

	/**
	 * @param kcbConfigurer the kcbConfigurer to set
	 */
	public void setKcbConfigurer(KCBConfigurer kcbConfigurer) {
		this.kcbConfigurer = kcbConfigurer;
	}

	/**
	 * @return the kenConfigurer
	 */
	public KENConfigurer getKenConfigurer() {
		return this.kenConfigurer;
	}

	/**
	 * @param kenConfigurer the kenConfigurer to set
	 */
	public void setKenConfigurer(KENConfigurer kenConfigurer) {
		this.kenConfigurer = kenConfigurer;
	}

	/**
	 * @return the kewConfigurer
	 */
	public KEWConfigurer getKewConfigurer() {
		return this.kewConfigurer;
	}

	/**
	 * @param kewConfigurer the kewConfigurer to set
	 */
	public void setKewConfigurer(KEWConfigurer kewConfigurer) {
		this.kewConfigurer = kewConfigurer;
	}

	/**
	 * @return the kimConfigurer
	 */
	public KIMConfigurer getKimConfigurer() {
		return this.kimConfigurer;
	}

	/**
	 * @param kimConfigurer the kimConfigurer to set
	 */
	public void setKimConfigurer(KIMConfigurer kimConfigurer) {
		this.kimConfigurer = kimConfigurer;
	}

	/**
	 * @return the knsConfigurer
	 */
	public KNSConfigurer getKnsConfigurer() {
		return this.knsConfigurer;
	}

	/**
	 * @param knsConfigurer the knsConfigurer to set
	 */
	public void setKnsConfigurer(KNSConfigurer knsConfigurer) {
		this.knsConfigurer = knsConfigurer;
	}

	/**
	 * @return the ksbConfigurer
	 */
	public KSBConfigurer getKsbConfigurer() {
		return this.ksbConfigurer;
	}

	/**
	 * @param ksbConfigurer the ksbConfigurer to set
	 */
	public void setKsbConfigurer(KSBConfigurer ksbConfigurer) {
		this.ksbConfigurer = ksbConfigurer;
	}
	
}
