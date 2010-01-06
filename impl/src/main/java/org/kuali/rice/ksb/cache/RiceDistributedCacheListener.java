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
package org.kuali.rice.ksb.cache;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.ksb.messaging.JavaServiceDefinition;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;
import org.kuali.rice.ksb.messaging.service.KSBJavaService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.plugins.clustersupport.AbstractBroadcastingListener;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;


/**
 * An OSCache listener which listens for events from the local cache and sends
 * them to a Topic on the bus so that all other entities on the bus can flush
 * their cache if neccessary.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RiceDistributedCacheListener extends AbstractBroadcastingListener implements KSBJavaService {

	private static final Logger LOG = Logger.getLogger(RiceDistributedCacheListener.class);

	private String serviceName;
	
	protected RemotedServiceRegistry remotedServiceRegistry;
	
	@Override
	public void initialize(Cache cache, Config config) throws InitializationException {

		LOG.info("Initializing cache listener");
		super.initialize(cache, config);
		// the following property was put on the OSCache properties used for
		// cache configuration
		this.serviceName = config.getProperty(RiceCacheAdministrator.SERVICE_NAME_KEY);
		boolean forceRegistryRefresh = new Boolean((Boolean)config.getProperties().get(RiceCacheAdministrator.FORCE_REGISTRY_REFRESH_KEY));
		remotedServiceRegistry = (RemotedServiceRegistry)config.getProperties().get(RiceCacheAdministrator.REMOTED_SERVICE_REGISTRY);
		if (StringUtils.isBlank(this.serviceName)) {
			throw new RiceRuntimeException("Cannot create DistributedCacheListener with empty serviceName");
		}
		LOG.info("Publishing Cache Service on bus under service name " + this.serviceName);
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setPriority(3);
		serviceDef.setRetryAttempts(1);
		serviceDef.setService(this);
		serviceDef.setLocalServiceName(this.serviceName);
		serviceDef.setServiceNameSpaceURI("");
		serviceDef.setQueue(false);
		try {
			serviceDef.validate();
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
		if(remotedServiceRegistry!=null)
			remotedServiceRegistry.registerService(serviceDef, forceRegistryRefresh);
		else
			KSBServiceLocator.getServiceDeployer().registerService(serviceDef, forceRegistryRefresh);
	}

	@Override
	protected void sendNotification(ClusterNotification notification) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending cache notification " + notification);
		}
		try {
			KSBJavaService oscacheNotificationService = (KSBJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(this.serviceName));
			oscacheNotificationService.invoke(notification);
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
	}

	public void finialize() throws FinalizationException {
	    //no processing needed
	}

	public void invoke(Serializable payLoad) {
		super.handleClusterNotification((ClusterNotification) payLoad);
	}

}
