/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ksb.messaging.config;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.core.impl.config.module.ModuleConfigurer;
import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.core.impl.lifecycle.ServiceDelegatingLifecycle;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.ksb.messaging.AlternateEndpoint;
import org.kuali.rice.ksb.messaging.AlternateEndpointLocation;
import org.kuali.rice.ksb.messaging.MessageFetcher;
import org.kuali.rice.ksb.messaging.ServiceDefinition;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.messaging.serviceconnectors.HttpInvokerConnector;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;
import org.quartz.Scheduler;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Used to configure the embedded workflow. This could be used to configure
 * embedded workflow programmatically but mostly this is a base class by which
 * to hang specific configuration behavior off of through subclassing
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KSBConfigurer extends ModuleConfigurer {

	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
	
    private List<AlternateEndpointLocation> alternateEndpointLocations = new ArrayList<AlternateEndpointLocation>();

	private List<AlternateEndpoint> alternateEndpoints = new ArrayList<AlternateEndpoint>();

	private DataSource registryDataSource;

	private DataSource messageDataSource;
	
	private DataSource nonTransactionalMessageDataSource;

	private Scheduler exceptionMessagingScheduler;

	private PlatformTransactionManager platformTransactionManager;
	
	@Override
	public void addAdditonalToConfig() {
		configureDataSource();
		configureBus();
		configureScheduler();
		configurePlatformTransactionManager();
		configureAlternateEndpoints();
	}

	@Override
	public List<String> getPrimarySpringFiles(){
		final List<String> springFileLocations = new ArrayList<String>();
		
		//hack 'cause KSB used KNS
		springFileLocations.add("classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml");
		
		springFileLocations.add("classpath:org/kuali/rice/ksb/config/KSBSpringBeans.xml");
        
        if (OrmUtils.isJpaEnabled("rice.ksb")) {
        	springFileLocations.add("classpath:org/kuali/rice/ksb/config/KSBJPASpringBeans.xml");
        }
        else {
        	springFileLocations.add("classpath:org/kuali/rice/ksb/config/KSBOJBSpringBeans.xml");
        }
        
        if (Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.LOAD_KNS_MODULE_CONFIGURATION)).booleanValue()) {
        	springFileLocations.add("classpath:org/kuali/rice/ksb/config/KSBModuleConfigurationSpringBeans.xml");
        }
        
        return springFileLocations;
	}
	
	/**
	 * Returns true - KSB UI should always be included.
	 * 
	 * @see org.kuali.rice.core.ConfigContext.getCurrentContextConfig().ModuleConfigurer#shouldRenderWebInterface()
	 */
	@Override
	public boolean shouldRenderWebInterface() {
		return true;
	}
	
	@Override
	public Collection<ResourceLoader> getResourceLoadersToRegister() throws Exception{
		ResourceLoader ksbRemoteResourceLoader = KSBResourceLoaderFactory.createRootKSBRemoteResourceLoader();
		ksbRemoteResourceLoader.start();
		return Collections.singletonList(ksbRemoteResourceLoader);
	}
	
	@Override
	public List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();

		// this validation of our service list needs to happen after we've
		// loaded our configs so it's a lifecycle
		lifecycles.add(new BaseLifecycle() {

			@Override
			public void start() throws Exception {
				// first check if we want to allow self-signed certificates for SSL communication
				if (Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.KSB_ALLOW_SELF_SIGNED_SSL)).booleanValue()) {
				    Protocol.registerProtocol("https", new Protocol("https",
					    (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443));
				}
				super.start();
			}
		});
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.THREAD_POOL_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.SCHEDULED_THREAD_POOL_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.REPEAT_TOPIC_INVOKING_QUEUE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.OBJECT_REMOTER));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.BUS_ADMIN_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBConstants.ServiceNames.REMOTED_SERVICE_REGISTRY));
		return lifecycles;
	}

    @Override
    public void doAdditonalConfigurerValidations() {
        for (final ServiceDefinition serviceDef : KSBConfigurer.this.services) {
			serviceDef.validate();
		}
    }

	@Override
	public void doAdditionalContextStartedLogic() {
		requeueMessages();
	}

	/**
     * Used to refresh the service registry after the Application Context is initialized.  This way any services that were exported on startup
     * will be available in the service registry once startup is complete.
     */
    private void requeueMessages() {
        LOG.info("Refreshing Service Registry to export services to the bus.");
        KSBServiceLocator.getServiceDeployer().refresh();
        
		//automatically requeue documents sitting with status of 'R'
		MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
		KSBServiceLocator.getThreadPool().execute(messageFetcher);
    }

	protected void configureBus() {
		LOG.debug("Configuring services for Service Namespace " + ConfigContext.getCurrentContextConfig().getServiceNamespace() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		configureServiceList(Config.BUS_DEPLOYED_SERVICES, getServices());
	}

	protected void configureServiceList(String key, List<ServiceDefinition> theServices) {
		LOG.debug("Configuring services for Service Namespace " + ConfigContext.getCurrentContextConfig().getServiceNamespace() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		@SuppressWarnings("unchecked")
		List<ServiceDefinition> serviceDefinitions = (List<ServiceDefinition>) ConfigContext.getCurrentContextConfig().getObject(key);
		if (serviceDefinitions == null) {
			ConfigContext.getCurrentContextConfig().putObject(key, theServices);
		} else if (theServices != null) {
			LOG.debug("Services already exist.  Adding additional services");
			serviceDefinitions.addAll(theServices);
		}
	}

	protected void configureScheduler() {
		if (this.getExceptionMessagingScheduler() != null) {
			LOG.info("Configuring injected exception messaging Scheduler");
			ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY, this.getExceptionMessagingScheduler());
		}
	}

	protected void configureDataSource() {
        if (getMessageDataSource() != null && getRegistryDataSource() == null) {
            throw new ConfigurationException("A message data source was defined but a registry data source was not defined.  Both must be specified.");
        }
        if (getMessageDataSource() == null && getRegistryDataSource() != null) {
            throw new ConfigurationException("A registry data source was defined but a message data source was not defined.  Both must be specified.");
        }

        if (getMessageDataSource() != null) {
            ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.KSB_MESSAGE_DATASOURCE, getMessageDataSource());
        }
        if (getNonTransactionalMessageDataSource() != null) {
            ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE, getNonTransactionalMessageDataSource());
        }
        if (getRegistryDataSource() != null) {
            ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.KSB_REGISTRY_DATASOURCE, getRegistryDataSource());
        }
    }

	protected void configurePlatformTransactionManager() {
		if (getPlatformTransactionManager() == null) {
			return;
		}
		ConfigContext.getCurrentContextConfig().putObject(RiceConstants.SPRING_TRANSACTION_MANAGER, getPlatformTransactionManager());
	}
	
	protected void configureAlternateEndpoints() {
		ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.KSB_ALTERNATE_ENDPOINT_LOCATIONS, getAlternateEndpointLocations());
		ConfigContext.getCurrentContextConfig().putObject(KSBConstants.Config.KSB_ALTERNATE_ENDPOINTS, getAlternateEndpoints());
	}
	
	@Override
	public void doAdditionalContextStoppedLogic() {
		try {
			HttpInvokerConnector.shutdownIdleConnectionTimeout();
		} catch (Exception e) {
			LOG.error("Failed to shutdown idle connection timeout evictor thread.", e);
		}
	    cleanUpConfiguration();
	}
	
	/**
     * Because our configuration is global, shutting down Rice does not get rid of objects stored there.  For that reason
     * we need to manually clean these up.  This is most important in the case of the service bus because the configuration
     * is used to store services to be exported.  If we don't clean this up then a shutdown/startup within the same
     * class loading context causes the service list to be doubled and results in "multiple endpoint" error messages.
     *
     */
    protected void cleanUpConfiguration() {
        ConfigContext.getCurrentContextConfig().removeObject(Config.BUS_DEPLOYED_SERVICES);
        ConfigContext.getCurrentContextConfig().removeObject(KSBConstants.Config.KSB_ALTERNATE_ENDPOINTS);
    }

	public List<ServiceDefinition> getServices() {
		return this.services;
	}

	public void setServices(List<ServiceDefinition> javaServices) {
		this.services = javaServices;
	}

	public DataSource getMessageDataSource() {
		return this.messageDataSource;
	}

	public void setMessageDataSource(DataSource messageDataSource) {
		this.messageDataSource = messageDataSource;
	}

    public DataSource getNonTransactionalMessageDataSource() {
        return this.nonTransactionalMessageDataSource;
    }

    public void setNonTransactionalMessageDataSource(DataSource nonTransactionalMessageDataSource) {
        this.nonTransactionalMessageDataSource = nonTransactionalMessageDataSource;
    }

    public DataSource getRegistryDataSource() {
		return this.registryDataSource;
	}

	public void setRegistryDataSource(DataSource registryDataSource) {
		this.registryDataSource = registryDataSource;
	}

	public Scheduler getExceptionMessagingScheduler() {
		return this.exceptionMessagingScheduler;
	}

	public void setExceptionMessagingScheduler(Scheduler exceptionMessagingScheduler) {
		this.exceptionMessagingScheduler = exceptionMessagingScheduler;
	}

	public PlatformTransactionManager getPlatformTransactionManager() {
		return platformTransactionManager;
	}

	public void setPlatformTransactionManager(PlatformTransactionManager springTransactionManager) {
		this.platformTransactionManager = springTransactionManager;
	}

    public List<AlternateEndpointLocation> getAlternateEndpointLocations() {
	    return this.alternateEndpointLocations;
    }

    public void setAlternateEndpointLocations(List<AlternateEndpointLocation> alternateEndpointLocations) {
	    this.alternateEndpointLocations = alternateEndpointLocations;
	}

    public List<AlternateEndpoint> getAlternateEndpoints() {
        return this.alternateEndpoints;
    }

    public void setAlternateEndpoints(List<AlternateEndpoint> alternateEndpoints) {
        this.alternateEndpoints = alternateEndpoints;
    }
    
}
