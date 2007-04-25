/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.util.cache;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.cache.ObjectCacheInternal;
import org.apache.ojb.broker.cache.RuntimeCacheException;
import org.kuali.rice.KNSServiceLocator;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/*
 * Copyright 2002-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

/**
 * ObjectCacheOSCacheImpl
 * 
 * Original source http://db.apache.org/ojb/docu/guides/objectcache.html#oscache
 * 
 * We use the extended caching interface {@link ObjectCacheInternal}to allow usage of this implementation in
 * {@link ObjectCacheTwoLevelImpl}.
 */
public class ObjectCacheOSCacheImpl implements ObjectCacheInternal {
    private static final Logger LOG = Logger.getLogger(ObjectCacheOSCacheImpl.class);
    private static final int REFRESH_PERIOD = com.opensymphony.oscache.base.CacheEntry.INDEFINITE_EXPIRY;

    // using SpringServiceLocator to get the admin, since there's no obvious way to let Spring inject
    // create/manage the actual cache instance that OJB will use
    private final GeneralCacheAdministrator admin;

    /**
     * Required default constructor.
     */
    public ObjectCacheOSCacheImpl() {
        admin = KNSServiceLocator.getPersistenceCacheAdministrator();
    }

    /**
     * Required constructor.
     * 
     * @param broker
     * @param prop
     */
    public ObjectCacheOSCacheImpl(PersistenceBroker broker, Properties prop) {
        this();
    }


    /**
     * @see org.apache.ojb.broker.cache.ObjectCache#cache(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public void cache(Identity oid, Object obj) {
        Cache cache = admin.getCache();

        try {
            /*
             * Actually, OSCache sends notifications (Events) only on flush events. The putInCache method do not flush the cache, so
             * no event is sent. The ObjectCacheOSCacheInternalImpl should force OSCache to flush the entry in order to generate an
             * event. This guarantee that other nodes always in sync with the DB. Alternative a non-indefinite refresh-period could
             * be used in conjunction with optimistic-locking for persistent objects.
             */
            remove(oid);
            admin.putInCache(oid.toString(), obj);
        }
        catch (Exception e) {
            LOG.error("error caching object: " + oid, e);
        }


    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCacheInternal#doInternalCache(org.apache.ojb.broker.Identity, java.lang.Object, int)
     */
    public void doInternalCache(Identity oid, Object obj, int type) {
        cache(oid, obj);
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCacheInternal#cacheIfNew(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public boolean cacheIfNew(Identity oid, Object obj) {
        boolean result = false;

        Cache cache = admin.getCache();
        try {
            cache.getFromCache(oid.toString());
        }
        catch (NeedsRefreshException e) {
            try {
                cache.putInCache(oid.toString(), obj);
                result = true;
            }
            catch (Exception e1) {
                cache.cancelUpdate(oid.toString());
                LOG.error("error caching object: " + oid, e);
            }
        }

        return result;
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCache#lookup(org.apache.ojb.broker.Identity)
     */
    public Object lookup(Identity oid) {
        Object cachedObject = null;
        Cache cache = admin.getCache();
        try {
            cachedObject = cache.getFromCache(oid.toString(), REFRESH_PERIOD);
        }
        catch (NeedsRefreshException e) {
            cache.cancelUpdate(oid.toString());
        }
        catch (Exception e) {
            cache.cancelUpdate(oid.toString());
            LOG.error("error retrieving object from cache: " + oid, e);
        }

        return cachedObject;
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCache#remove(org.apache.ojb.broker.Identity)
     */
    public void remove(Identity oid) {
        try {
            admin.flushEntry(oid.toString());
        }
        catch (Exception e) {
            throw new RuntimeCacheException("error removing object from cache: " + oid, e);
        }
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCache#clear()
     */
    public void clear() {
        try {
            admin.flushAll();
        }
        catch (Exception e) {
            throw new RuntimeCacheException("error clearing cache", e);
        }
    }
}
