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

import java.util.Properties;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Default implementation of the {@link RiceCacheAdministrator}.
 *
 * @author rkirkend
 */
public class RiceCacheAdministratorImpl implements RiceCacheAdministrator {

	private static final String CACHE_PREFIX = "cache.";
	private GeneralCacheAdministrator cacheAdministrator;
	private boolean started;
	private String messageEntity;
	private boolean forceRegistryRefresh;

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
		getCacheAdministrator().putInCache(key, content, groups);
	}

	public void putInCache(String key, Object content) {
		getCacheAdministrator().putInCache(key, content);
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
		Properties configProperties = Core.getCurrentContextConfig().getProperties();
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
		// we put the message entity in as a property because it's required in order for the KEWDistributedCacheListener to be initialized
		properties.put(Config.MESSAGE_ENTITY, getMessageEntity());
		properties.put(RiceCacheAdministrator.FORCE_REGISTRY_REFRESH_KEY, new Boolean(this.forceRegistryRefresh));
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

	public String getMessageEntity() {
		if (this.messageEntity == null) {
			return Core.getCurrentContextConfig().getMessageEntity();
		}
		return this.messageEntity;
	}

	public void setMessageEntity(String notificationTopicName) {
		this.messageEntity = notificationTopicName;
	}

	public void setForceRegistryRefresh(boolean forceRegistryRefresh) {
		this.forceRegistryRefresh = forceRegistryRefresh;
	}
}