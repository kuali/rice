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

import org.kuali.rice.lifecycle.Lifecycle;

/**
 * Service for accessing and managing KEW primary cache
 *
 * @author rkirkend
 */
public interface RiceCacheAdministrator extends Lifecycle {
	
	public static final String FORCE_REGISTRY_REFRESH_KEY = "_FORCE_REGISTRY_REFRESH";

	public Object getFromCache(String key);

	public void putInCache(String key, Object content, String[] groups);

	public void putInCache(String key, Object content, String group);

	public void putInCache(String key, Object content);

	public void flushEntry(String key);

	public void flushGroup(String group);

	public void flushAll();

	public void setCacheCapacity(int capacity);
	
	public void setForceRegistryRefresh(boolean forceRegistryRefresh);

}