/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.impl.SessionDocumentServiceImpl;

/**
 * Override LRUMap removeEntity method
 * 
 * 
 */
public class KualiLRUMap extends LRUMap {

	/** Serialization version */
	private static final long serialVersionUID = 1L;

	public KualiLRUMap() {
		super();
	}

	public KualiLRUMap(int maxSize) {
		super(maxSize);
	}

	protected void removeEntry(HashEntry entry, int hashIndex, HashEntry previous) {

		// It is for session document cache enhancement.
		// To control the size of cache. When the LRUMap reach the maxsize.
		// It will remove session document entries from the in-memory user
		// session objects.
		try {
			SessionDocumentServiceImpl.CachedObject cachedObject 
					= (SessionDocumentServiceImpl.CachedObject)this.entryValue(entry);
			cachedObject.getUserSession().removeObject(cachedObject.getFormKey());
		} catch (Exception ex) {
			Logger.getLogger(getClass()).warn( "Problem purging old entry from the user session when removing from the map: ", ex);
		}

		super.removeEntry(entry, hashIndex, previous);
	}

}
