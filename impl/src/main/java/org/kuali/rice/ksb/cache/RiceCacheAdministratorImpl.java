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

package org.kuali.rice.ksb.cache;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;

import java.util.Properties;

/**
 * Default implementation of the {@link RiceCacheAdministrator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RiceCacheAdministratorImpl implements RiceCacheAdministrator {

    private static final String DEFAULT_SERVICE_NAME = "OSCacheNotificationService";
    
	private static final String CACHE_PREFIX = "cache.";
	private GeneralCacheAdministrator cacheAdministrator;
	private boolean started;
	private boolean forceRegistryRefresh;
	private String serviceName;
	protected RemotedServiceRegistry remotedServiceRegistry;
	private static final Logger LOG = Logger.getLogger(RiceCacheAdministratorImpl.class);
	
	/**
	 * @return the remotedServiceRegistry
	 */
	public RemotedServiceRegistry getRemotedServiceRegistry() {
		return this.remotedServiceRegistry;
	}

	/**
	 * @param remotedServiceRegistry the remotedServiceRegistry to set
	 */
	public void setRemotedServiceRegistry(
			RemotedServiceRegistry remotedServiceRegistry) {
		this.remotedServiceRegistry = remotedServiceRegistry;
	}

	public boolean isStarted() {
		return this.started;
	}

	public Object getFromCache(String key) {
		try {
			return getCacheAdministrator().getFromCache(key);
		} catch (NeedsRefreshException e) {
			getCacheAdministrator().cancelUpdate(key);
			return null;
		}
	}

	public void putInCache(String key, Object content, String[] groups) {
	    try {
	        getCacheAdministrator().putInCache(key, content, groups);
	    } catch (IllegalStateException e) {
	        LOG.warn("Failed to insert object into cache with key: " + key);
	    }
	}

	public void putInCache(String key, Object content) {
	    try {
	        getCacheAdministrator().putInCache(key, content);
	    } catch (IllegalStateException e) {
            LOG.warn("Failed to insert object into cache with key: " + key);
        }
	}


	public void flushEntry(String key) {
		getCacheAdministrator().flushEntry(key);
	}


	public void flushGroup(String group) {
		getCacheAdministrator().flushGroup(group);
	}

	public void flushAll() {
		getCacheAdministrator().flushAll();
	}

	public void setCacheCapacity(int capacity) {
		getCacheAdministrator().setCacheCapacity(capacity);
	}

	public int getCacheCapacity() {
		return getCacheAdministrator().getCache().getCapacity();
	}

	public int getSize() {
		return getCacheAdministrator().getCache().getSize();
	}

	public void start() throws Exception {
		Properties props = loadCacheSettings();
		this.cacheAdministrator = new GeneralCacheAdministrator(props);
	}

	protected Properties loadCacheSettings() {
		Properties properties = new Properties();
		Properties configProperties = ConfigContext.getCurrentContextConfig().getProperties();
		for (Object keyObject : configProperties.keySet()) {
			String key = (String)keyObject;
			if (key.startsWith(CACHE_PREFIX)) {
				properties.put(key, configProperties.getProperty(key));
			}
		}
		// setup defaults if certain properties aren't set
		if (!properties.containsKey(AbstractCacheAdministrator.CACHE_MEMORY_KEY)) {
			properties.put(AbstractCacheAdministrator.CACHE_MEMORY_KEY, "true");
		}
		if (!properties.containsKey(AbstractCacheAdministrator.CACHE_ENTRY_EVENT_LISTENERS_KEY)) {
			properties.put(AbstractCacheAdministrator.CACHE_ENTRY_EVENT_LISTENERS_KEY, RiceDistributedCacheListener.class.getName());
		}
		if (!properties.containsKey(AbstractCacheAdministrator.CACHE_BLOCKING_KEY)) {
			properties.put(AbstractCacheAdministrator.CACHE_BLOCKING_KEY, "false");
		}
		properties.put(RiceCacheAdministrator.FORCE_REGISTRY_REFRESH_KEY, new Boolean(this.forceRegistryRefresh));
		properties.put(RiceCacheAdministrator.REMOTED_SERVICE_REGISTRY, remotedServiceRegistry);
		if (StringUtils.isBlank(this.serviceName)) {
		    this.serviceName = DEFAULT_SERVICE_NAME;
		}
		properties.put(RiceCacheAdministrator.SERVICE_NAME_KEY, this.serviceName);
		return properties;
	}

	public void stop() throws Exception {
		getCacheAdministrator().destroy();
		this.started = false;
	}

	public void putInCache(String key, Object content, String group) {
		putInCache(key, content, new String[] {group});
	}

	protected GeneralCacheAdministrator getCacheAdministrator() {
		return this.cacheAdministrator;
	}

	public void setForceRegistryRefresh(boolean forceRegistryRefresh) {
		this.forceRegistryRefresh = forceRegistryRefresh;
	}

	public String getServiceName() {
	    return this.serviceName;
	}

	public void setServiceName(String serviceName) {
	    this.serviceName = serviceName;
	}
	
}
