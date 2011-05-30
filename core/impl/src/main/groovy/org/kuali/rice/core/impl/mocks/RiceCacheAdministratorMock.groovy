/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.impl.mocks

import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator

/**
 * A simple mock for the {@link org.kuali.rice.ksb.api.cache.RiceCacheAdministrator} which uses a Map-based backend.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class RiceCacheAdministratorMock implements RiceCacheAdministrator {

	Map<String, Object> cache = new HashMap<String, Object>();
	Map<String, Set<String>> keyToGroupMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> groupToKeyMap = new HashMap<String, Set<String>>();
	
	@Override
	public void start() throws Exception {}

	@Override
	public void stop() throws Exception {}

	@Override
	public boolean isStarted() {
		return true;
	}

	@Override
	public synchronized Object getFromCache(String key) {
		return cache.get(key);
	}
	
	//The following method does not factor in the refreshPeriod
	@Override
	public synchronized Object getFromCache(String key, int refreshPeriod) {
		return cache.get(key);
	}
	
	//The following method does not factor in the refreshPeriod or the cronExpression
	@Override
	public synchronized Object getFromCache(String key, int refreshPeriod, String cronExpression) {
		return cache.get(key);
	}

	@Override
	public synchronized void putInCache(String key, Object content, String[] groups) {
		cache.put(key, content);
		if (groups != null && groups.length > 0) {
			synchronized (keyToGroupMap) {
				Set<String> currentGroups = keyToGroupMap.get(key);
				if (currentGroups == null) {
					currentGroups = new HashSet<String>();
					keyToGroupMap.put(key, currentGroups);
				}
				for (String group : groups) {
					currentGroups.add(group);
					Set<String> currentGroupKeys = groupToKeyMap.get(group);
					if (currentGroupKeys == null) {
						currentGroupKeys = new HashSet<String>();
						groupToKeyMap.put(group, currentGroupKeys);
					}
					currentGroupKeys.add(key);
				}
			}
		}
	}

	@Override
	public synchronized void putInCache(String key, Object content, String group) {
		if (group != null) {
			putInCache(key, content, [ group ] as String[]);
		}
	}

	@Override
	public synchronized void putInCache(String key, Object content) {
		cache.put(key, content);
	}

	@Override
	public synchronized void flushEntry(String key) {
		cache.remove(key);
		keyToGroupMap.remove(key);
	}

	@Override
	public synchronized void flushGroup(String group) {
		Set<String> groupKeys = groupToKeyMap.get(group);
		if (groupKeys != null) {
			for (String groupKey : groupKeys) {
				flushEntry(groupKey);
			}
		}
		groupToKeyMap.remove(group);
	}

	@Override
	public synchronized void flushAll() {
		cache.clear();
		keyToGroupMap.clear();
		groupToKeyMap.clear();
	}

	@Override
	public void setCacheCapacity(int capacity) {}

	@Override
	public void setForceRegistryRefresh(boolean forceRegistryRefresh) {}
	
	public boolean isEmpty() {
		return cache.isEmpty();
	}
	
	public int getSize() {
		return cache.size();
	}

}
