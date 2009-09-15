/*
 * Copyright 2005-2007 The Kuali Foundation
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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;


/**
 * Monitor entry-related events for a cache.
 */
@SuppressWarnings("unchecked")
abstract public class OSCacheMonitor implements CacheEntryEventListener {
    private static final Log LOG = LogFactory.getLog(OSCacheMonitor.class);

    private final String purpose;
    private final Set entries;

    /**
     * Constructs a OSCacheMonitor with the given purpose
     * 
     * @param purpose
     */
	public OSCacheMonitor(String purpose) {
        this.purpose = purpose;
        entries = new HashSet();

        if ( LOG.isInfoEnabled() ) {
        	LOG.info("created " + purpose + " CacheMonitor ");
        }
    }


    public void cacheEntryAdded(CacheEntryEvent event) {
        entries.add(event.getKey());
        if ( LOG.isDebugEnabled() ) {
        	logEntryEvent("added", event);
        }
    }

    public void cacheEntryUpdated(CacheEntryEvent event) {
    	if ( LOG.isDebugEnabled() ) {
    		logEntryEvent("updated", event);
    	}
    }

    public void cacheEntryFlushed(CacheEntryEvent event) {
        entries.remove(event.getKey());
        if ( LOG.isDebugEnabled() ) {
        	logEntryEvent("flushed", event);
        }
    }

    public void cacheEntryRemoved(CacheEntryEvent event) {
        entries.remove(event.getKey());
        if ( LOG.isDebugEnabled() ) {
        	logEntryEvent("removed", event);
        }
    }

    public void cacheFlushed(CachewideEvent event) {
        entries.clear();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug(purpose + " flushed cache (" + entries.size() + " entries)");
        }
    }


    public void cacheGroupFlushed(CacheGroupEvent event) {
        // if I read the code correctly, each entry gets its own cacheEntryFlused event
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info(purpose + " flushed cache group '" + event.getGroup() + "'");
    	}
    }

    public void cachePatternFlushed(CachePatternEvent event) {
        // if I read the code correctly, each entry gets its own cacheEntryFlused event
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info(purpose + " flushed cache pattern '" + event.getPattern() + "'");
    	}
    }

    private void logEntryEvent(String verb, CacheEntryEvent event) {
        LOG.debug(purpose + " " + verb + " entry '" + event.getKey() + "' (" + entries.size() + " entries)");
    }
}
