/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Code obtained from http://opensource.atlassian.com/confluence/spring/display/DISC/Caching+the+result+of+methods+using+Spring+and+EHCache
 * and modified for use within Kuali
 */
 
// begin Kuali Foundation modification
package org.kuali.rice.krad.util.cache;

// Kuali Foundation modification: changed some imports
import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.util.cache.CopiedObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * begin Kuali Foundation modification
 * This class implements org.aopalliance.intercept.MethodInterceptor. This interceptor builds the cache key for the method and
 * checks if an earlier result was cached with that key. If so, the cached result is returned; otherwise, the intercepted method is
 * called and the result cached for future use.
 * end Kuali Foundation modification
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 2004.10.07
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean {
    private static final Log LOG = LogFactory.getLog(MethodCacheInterceptor.class);

    private Cache cache;
    // begin Kuali Foundation modification
    private int expirationTimeInSeconds = 1000;
    private long maxEntrySizeInBytes = 0;
    // end Kuali Foundation modification

    /**
     * begin Kuali Foundation modification
     * @param cache name of cache to be used
     * end Kuali Foundation modification
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    // begin Kuali Foundation modification
    /**
     * Entries older than this will have their contents replaced by the return value from a call to the appropriate method
     * 
     * @param expirationTimeInSeconds
     */
    public void setExpirationTimeInSeconds(int expirationTimeInSeconds) {
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }

    /**
     * Entries whose size is larger than the current value will not be cached. If the maxEntrySizeInBytes <= 0, no size limit will
     * be applied.
     * 
     * @param maxEntrySizeInBytes
     */
    public void setMaxEntrySizeInBytes(long maxEntrySizeInBytes) {
        this.maxEntrySizeInBytes = maxEntrySizeInBytes;
    }
    // end Kuali Foundation modification


    /**
     * Checks if required attributes are provided.
     * 
     * begin Kuali Foundation modification
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * end Kuali Foundation modification
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cache, "A cache is required. Use setCache(Cache) to provide one.");
    }

    /**
     * begin Kuali Foundation modification
     * Caches method results, if possible.
     * <p>
     * Results must be Serializable to be cached. Method with unSerializable results will never have their results cached, and will
     * log error messages complaining about that fact.
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     * end Kuali Foundation modification
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // begin Kuali Foundation modification
        boolean cancelUpdate = true;

        Object methodResult = null;
        String cacheKey = buildCacheKey(invocation);

        // lookup result in cache
        if (LOG.isTraceEnabled()) {
            LOG.trace("looking for method result for invocation '" + cacheKey + "'");
        }
        try {
            CopiedObject cachedEntry = (CopiedObject) cache.getFromCache(cacheKey, expirationTimeInSeconds);
            if (LOG.isDebugEnabled()) {
                LOG.debug("using cached result deepCopy for invocation '" + cacheKey + "'");
            }

            // really expensive hack to try to keep from returning direct references to modifiable cached values
            // because if you return a direct reference to a cached value to someone, and it is of a mutable type, any changes they
            // make to what seems like "their copy" will also be reflected in the cached value, which is a Really Bad Thing
            methodResult = cachedEntry.getContent(); // getContent returns a copy
            cancelUpdate = false;
        }
        catch (NeedsRefreshException e) {
            // call intercepted method
            try {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("calling intercepted method for invocation '" + cacheKey + "'");
                }
                methodResult = invocation.proceed();
            }
            catch (Exception invocationException) {
                LOG.warn("unable to cache methodResult: caught exception invoking intercepted method: '" + invocationException);
                throw invocationException;
            }

            // cache method result, if possible
            // there's no way to tell whether a result is cacheable until after the method gets called,
            // since methods may hand back a Serializable object even if the method is declared to return
            // a more general nonSerializable type (e.g. List is not Serializable, but if the method
            // actually returns ArrayList instances, they are)
            //
            // nulls are a special case, since isAssignableFrom will never return true, yet they are
            // Serializable (at least for this kind of use)
            //
            // caching a deepCopy of the methodResult to prevent someone changing the cached value directly,
            // through a shared reference
            if ((methodResult == null) || (Serializable.class.isAssignableFrom(methodResult.getClass()))) {
                try {
                    CopiedObject oldContent = (CopiedObject) e.getCacheContent();

                    CopiedObject newContent = oldContent;
                    if ( newContent == null ) {
                    newContent = new CopiedObject();
                    }
                    newContent.setContent((Serializable)methodResult);

                    // if no size limit, or under size limit, add to cache
                    if ((maxEntrySizeInBytes <= 0) || (newContent.getSize() <= maxEntrySizeInBytes)) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("caching results for invocation '" + cacheKey + "'");
                        }
                        cache.putInCache(cacheKey, newContent);

                        // adding, not updating
                        cancelUpdate = false;
                    }
                    else {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("rejecting oversized methodResult (" + newContent.getSize() + " bytes) for invocation '" + cacheKey + "'");
                        }

                        // size limit exceeded: remove existing (expired) cache entry, if any
                        if (oldContent != null) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("flushing previous value for oversized invocation '" + cacheKey + "'");
                            }

                            cache.cancelUpdate(cacheKey);
                            cache.flushEntry(cacheKey);

                            // already canceled the update, don't need to cancel the update again
                            cancelUpdate = false;
                        }
                    }
                }
                catch (Exception cacheException) {
                    LOG.error("unable to cache methodResult: caught exception invoking putInCache: '" + cacheException);
                    throw cacheException;
                }
            }
            else {
                LOG.error("unable to cache nonSerializable result type for invocation '" + cacheKey + "'");
            }
        }
        finally {
            // it is imperative that you call cancelUpdate if you aren't going to update the cache entry
            if (cancelUpdate) {
                cache.cancelUpdate(cacheKey);
            }
        }


        return methodResult;
        // end Kuali Foundation modification
    }

    // begin Kuali Foundation modification
    /**
     * @param invocation MethodInvocation being handled
     * @return cache key: className.methodName(paramClass=argValue[,paramClass=argValue...])
     */
    private String buildCacheKey(MethodInvocation invocation) {
        return buildCacheKey(invocation.getStaticPart().toString(), invocation.getArguments());
    }


    /**
     * @param className
     * @param methodName
     * @param paramTypes
     * @param argValues
     * @return cache key: className.methodName(paramClass=argValue[,paramClass=argValue...])
     */
    public String buildCacheKey(String methodSignature, Object[] argValues) {
        StringBuffer cacheKey = new StringBuffer(methodSignature);
        cacheKey.append(": ");
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                if (i > 0) {
                    cacheKey.append(",");
                }
                // handle weird cache bug:
                // if you call a one-arg method foo with a null arg i.e. foo(null),
                // and then call it with an argument whose toString evaluates to "null",
                // OSCache gets stuck in an infinite wait() call because it somehow thinks
                // another thread is already updating this cache entry
                //
                // workaround: change so that args which are actually null literal have
                // some weird, unlikely-to-be-encountered String representation
                if (argValues[i] == null) {
                    cacheKey.append("<literal null>");
                }
                else {
                    cacheKey.append(argValues[i]);
                }
            }
        }
        return cacheKey.toString();
    }


    /**
     * @param key
     * @return true if the cache contains an entry with the given key
     */
    public boolean containsCacheKey(String key) {
        boolean contains = false;

        try {
            cache.getFromCache(key);
            contains = true;
        }
        catch (NeedsRefreshException e) {
            // it is imperative that you call cancelUpdate if you aren't going to update the cache entry that caused the
            // NeedsRefreshException above
            cache.cancelUpdate(key);
            contains = false;
        }

        return contains;
    }
    /** 
     * Removes a method cache if one exists for the given key.
     * @param cacheKey - key for method signature and parameters - see buildCacheKey
     */
    public void removeCacheKey(String cacheKey) {
    	if (!containsCacheKey(cacheKey)) {
    		return;
      	}
      
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("removing method cache for key: " + cacheKey);
		}
		cache.cancelUpdate(cacheKey);
		cache.flushEntry(cacheKey);
    }
    
    // Kuali Foundation modification: removed getCacheKey(String, String, Object[])
    // end Kuali Foundation modification
}
