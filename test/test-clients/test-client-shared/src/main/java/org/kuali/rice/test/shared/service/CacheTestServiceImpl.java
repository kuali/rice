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
package org.kuali.rice.test.shared.service;

import org.kuali.rice.ksb.cache.RiceCacheAdministrator;
import org.springframework.beans.factory.InitializingBean;

/**
 * Implementation of the CacheTestService interface.  Delegates calls to an underlying
 * RiceCacheAdministrator.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CacheTestServiceImpl implements CacheTestService, InitializingBean {

	private RiceCacheAdministrator cacheDelegate;

	public void setCacheDelegate(RiceCacheAdministrator cacheDelegate) {
		this.cacheDelegate = cacheDelegate;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (cacheDelegate == null) {
			throw new IllegalStateException("CacheDelegatorServiceImpl must have a cacheDelegate set!  Was null instead.");
		}
	}

	@Override
	public void flushEntry(String key) {
		cacheDelegate.flushEntry(key);
	}

	@Override
	public void flushAll() {
		cacheDelegate.flushAll();
	}

	@Override
	public void flushGroup(String group) {
		cacheDelegate.flushGroup(group);
	}

	@Override
	public String getFromCache(String key) {
		return (String)cacheDelegate.getFromCache(key);
	}

	@Override
	public void putInCache(String key, String value, String group) {
		cacheDelegate.putInCache(key, value, group);
	}
	
	@Override
	public void putInCache(String key, String value) {
		cacheDelegate.putInCache(key, value);
	}

}
