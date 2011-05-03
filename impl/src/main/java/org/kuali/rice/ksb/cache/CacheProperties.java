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
package org.kuali.rice.ksb.cache;

/**
 * Defines some property name constants that are used as part of the internal cache implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
final class CacheProperties {

	static final String FORCE_REGISTRY_REFRESH_KEY = "_FORCE_REGISTRY_REFRESH";
	static final String SERVICE_NAME_KEY = "_SERVICE_NAME";
	static final String REMOTED_SERVICE_REGISTRY = "remotedServiceRegistry";
	
	private CacheProperties() {
		throw new UnsupportedOperationException("do not call");
	}
	
}
