/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.cache;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.exceptions.RiceRuntimeException;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.plugins.clustersupport.AbstractBroadcastingListener;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;

import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.KEWJavaService;

/**
 * An OSCache listener which listens for events from the local cache and sends
 * them to a Topic on the bus so that all other entities on the bus can flush
 * their cache if neccessary.
 * 
 * @author rkirkend
 */
public class RiceDistributedCacheListener extends AbstractBroadcastingListener implements KEWJavaService {

	private static final Logger LOG = Logger.getLogger(RiceDistributedCacheListener.class);

	private static final String SERVICE_NAME = "OSCacheNotificationService";

	private String messageEntity;

	@Override
	public void initialize(Cache cache, Config config) throws InitializationException {

		LOG.info("Initializing cache listener");
		super.initialize(cache, config);
		// the following property was put on the OSCache properties used for
		// cache configuration
		String messageEntity = config.getProperty(org.kuali.rice.config.Config.MESSAGE_ENTITY);
		boolean forceRegistryRefresh = new Boolean((Boolean)config.getProperties().get(RiceCacheAdministrator.FORCE_REGISTRY_REFRESH_KEY));
		if (messageEntity == null) {
			throw new RiceRuntimeException("Cannot create KEWDistributedCacheListener with null messageEntity");
		}
		this.messageEntity = messageEntity;
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setPriority(3);
		serviceDef.setRetryAttempts(3);
		serviceDef.setService(this);
		serviceDef.setServiceName(getServiceName());
		serviceDef.setQueue(false);
		try {
			serviceDef.validate();
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
		KSBServiceLocator.getServiceDeployer().registerService(serviceDef, forceRegistryRefresh);
	}

	@Override
	protected void sendNotification(ClusterNotification notification) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending cache notification " + notification);
		}
		try {
			KEWJavaService oscacheNotificationService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(getServiceName());
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

	private QName getServiceName() {
		return new QName(this.messageEntity, SERVICE_NAME);
	}
}