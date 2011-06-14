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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
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
public class MethodCacheNoCopyInterceptor implements MethodInterceptor, InitializingBean {
    private static final Logger LOG = Logger.getLogger(MethodCacheNoCopyInterceptor.class);

    private Cache cache;
    // begin Kuali Foundation modification
    private int expirationTimeInSeconds = 1000;
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
    @SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation invocation) throws Throwable {
        // begin Kuali Foundation modification
        boolean cancelUpdate = true;

        Object methodResult = null;
        String cacheKey = buildCacheKey(invocation);

        // lookup result in cache
        if (LOG.isDebugEnabled()) {
            LOG.debug("looking for method result for invocation '" + cacheKey + "'");
        }
        try {
            methodResult = cache.getFromCache(cacheKey, expirationTimeInSeconds);
            if (LOG.isDebugEnabled()) {
                LOG.debug("using cached result invocation '" + cacheKey + "'");
            }

            cancelUpdate = false;
        }
        catch (NeedsRefreshException e) {
            // call intercepted method
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("calling intercepted method for invocation '" + cacheKey + "'");
                }
                methodResult = invocation.proceed();
            }
            catch (Exception invocationException) {
                LOG.warn("unable to cache methodResult: caught exception invoking intercepted method: '" + invocationException);
                throw invocationException;
            }

            // cache method result, if possible
            if ( methodResult == null || Serializable.class.isAssignableFrom(methodResult.getClass() ) ) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("caching results for invocation '" + cacheKey + "'");
                    }
                    // ensure that items like lists/maps/collections are not modified once returned
                    if ( methodResult != null ) {
                    	if ( methodResult instanceof SortedMap ) {
                    		methodResult = Collections.unmodifiableSortedMap((SortedMap)methodResult);
                    	} else if ( methodResult instanceof Map ) {
                    		methodResult = Collections.unmodifiableMap((Map)methodResult);
                    	} else if ( methodResult instanceof List ) {
                    		methodResult = Collections.unmodifiableList((List)methodResult);
                    	} else if ( methodResult instanceof SortedSet ) {
                    		methodResult = Collections.unmodifiableSortedSet((SortedSet)methodResult);
                    	} else if ( methodResult instanceof Set ) {
                    		methodResult = Collections.unmodifiableSet((Set)methodResult);
                    	} else if ( methodResult instanceof Collection ) {
                    		methodResult = Collections.unmodifiableCollection((Collection)methodResult);
                    	}
                    }
                    cache.putInCache(cacheKey, methodResult);
    
                    // adding, not updating
                    cancelUpdate = false;
                } catch (Exception cacheException) {
                    LOG.error("unable to cache methodResult: caught exception invoking putInCache: '" + cacheException);
                    throw cacheException;
                }
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
