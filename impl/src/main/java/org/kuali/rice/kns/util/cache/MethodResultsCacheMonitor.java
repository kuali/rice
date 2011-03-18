/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.util.cache.CopiedObject;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.events.CacheEntryEvent;

/**
 * MethodCacheMonitor
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class MethodResultsCacheMonitor extends OSCacheMonitor {
    private static final Log LOG = LogFactory.getLog(MethodResultsCacheMonitor.class);

    private int entryCount;
    private int byteSize;

    public MethodResultsCacheMonitor() {
        super("methodResultsCache");

        byteSize = 0;
        entryCount = 0;
    }

    /**
     * @see org.kuali.rice.kns.util.cache.OSCacheMonitor#cacheEntryAdded(com.opensymphony.oscache.base.events.CacheEntryEvent)
     */
	public void cacheEntryAdded(CacheEntryEvent event) {
        super.cacheEntryAdded(event);

        CacheEntry ce = event.getEntry();
        CopiedObject co = (CopiedObject) ce.getContent();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("cached entry for key '" + ce.getKey() + "', size " + co.getSize() + " bytes");
        }

        entryCount++;
        byteSize += co.getSize();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("cache size now " + entryCount + " entries, " + byteSize + " bytes");
        }
    }

    /**
     * @see org.kuali.rice.kns.util.cache.OSCacheMonitor#cacheEntryFlushed(com.opensymphony.oscache.base.events.CacheEntryEvent)
     */
    public void cacheEntryFlushed(CacheEntryEvent event) {
        super.cacheEntryFlushed(event);

        CacheEntry ce = event.getEntry();
        CopiedObject co = (CopiedObject) ce.getContent();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("flushed entry for key '" + ce.getKey() + "', size " + co.getSize() + " bytes");
        }

        entryCount--;
        byteSize -= co.getSize();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("cache size now " + entryCount + " entries, " + byteSize + " bytes");
        }
    }

    /**
     * @see org.kuali.rice.kns.util.cache.OSCacheMonitor#cacheEntryRemoved(com.opensymphony.oscache.base.events.CacheEntryEvent)
     */
    public void cacheEntryRemoved(CacheEntryEvent event) {
        super.cacheEntryRemoved(event);

        CacheEntry ce = event.getEntry();
        CopiedObject co = (CopiedObject) ce.getContent();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("removed entry for key '" + ce.getKey() + "', size " + co.getSize() + " bytes");
        }

        entryCount--;
        byteSize -= co.getSize();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("cache size now " + entryCount + " entries, " + byteSize + " bytes");
        }
    }

    /**
     * @see org.kuali.rice.kns.util.cache.OSCacheMonitor#cacheEntryUpdated(com.opensymphony.oscache.base.events.CacheEntryEvent)
     */
    public void cacheEntryUpdated(CacheEntryEvent event) {
        super.cacheEntryUpdated(event);

        CacheEntry ce = event.getEntry();
        CopiedObject co = (CopiedObject) ce.getContent();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("updated entry for key '" + ce.getKey() + "', size " + co.getSize() + " bytes");
        }

        byteSize -= co.getOldSize();
        byteSize += co.getSize();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("cache size now " + entryCount + " entries, " + byteSize + " bytes");
        }
    }
}
