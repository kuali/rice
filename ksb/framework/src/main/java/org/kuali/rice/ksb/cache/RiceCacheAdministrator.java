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

import org.kuali.rice.core.api.lifecycle.Lifecycle;

/**
 * Service for accessing and managing KEW primary cache
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RiceCacheAdministrator extends Lifecycle {
	
	public static final String FORCE_REGISTRY_REFRESH_KEY = "_FORCE_REGISTRY_REFRESH";
	public static final String SERVICE_NAME_KEY = "_SERVICE_NAME";
	public static final String REMOTED_SERVICE_REGISTRY = "remotedServiceRegistry";

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
