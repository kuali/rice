/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.util;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * <p>This is a generic caching proxy for KEW, using the cache administrator service to store results.  It's inteded
 * for caching query results for a DAO, but it likely has broader use.
 * <p>It will cache the results of calls matching method names which are set in {@link #setCacheForMethods}.  
 * It will clear this "cache group" for calls with matching method names set in {@link #setClearCacheOnMethods}.
 * <p>The cache group name (see {@link #setCacheGroupName(String)}) in most instances should unique for each class that 
 * is proxied.
 * <p>Configuration should be done through Spring, probably using BeanNameAutoProxyCreator.
 * <p>NOTE: One important assumption here is that the arguments for the methods being cached will all have 
 * {@link Object#toString()} implementations such that o1.equals(o2) iff o1.toString().equals(o2.toString()).
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CachingInterceptor implements MethodInterceptor {
	
	private static Log LOG = LogFactory.getLog(CachingInterceptor.class);
	
	private Set<String> clearCacheOnMethods = new HashSet<String>(50);
	private Set<String> cacheForMethods = new HashSet<String>(50);
	private String cacheGroupName = "CachingInterceptor";
	
	private static final Object NULL_OBJECT = new Object();
	
	public void setClearCacheOnMethods(String [] methods) {
		for (String method : methods) {
			clearCacheOnMethods.add(method);
		}
	}
	
	public void setCacheForMethods(String [] methods) {
		for (String method : methods) {
			cacheForMethods.add(method);
		}
	}
	
	public void setCacheGroupName(String name) {
		cacheGroupName = name;
	}
	
	/**
	 * This overridden method intercepts calls, and
	 * <ul>
	 * <li>if the intercepted method is in clearCacheOnMethods
	 * <ul><li>the cache is purged, then the method is called and any results returned.</ul>
	 * <li>if the intercepted method is in cacheForMethods
	 * <ul><li>the cache is checked.  On a hit, cached results are returned.  On a miss, the invocation
	 * occurrs and the results are cached.</ul>
	 * <li>otherwise
	 * <ul><li>the method is called and any results returned</ul>
	 * </ul> 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Object results = null;
		
		String methodName = invocation.getMethod().getName();

		if (clearCacheOnMethods.contains(methodName)) {
			if (LOG.isTraceEnabled()) { LOG.trace("clearing cache group " +cacheGroupName+" on " + methodName); }
			// clear the cache
			KEWServiceLocator.getCacheAdministrator().flushGroup(cacheGroupName);
			results = invocation.proceed();

		} else if (cacheForMethods.contains(methodName)) {
			boolean gotCachedResults = false;
			// attempt to use the cache;
			String cacheKey = getCacheKey(methodName, invocation.getArguments());
			if (cacheKey != null) {
				SoftReference<Object> resultRef = 
					(SoftReference<Object>) KEWServiceLocator.getCacheAdministrator().getFromCache(cacheKey);
				
				if (resultRef != null) { results = resultRef.get(); }
				if (null != results) {
					if (LOG.isTraceEnabled()) { LOG.trace("cache hit in group " +cacheGroupName+" on " + methodName); }
					gotCachedResults = true;
				}
			}
			
			if (!gotCachedResults) {
				if (LOG.isTraceEnabled()) { LOG.trace("cache miss in group " +cacheGroupName+" on " + methodName); }

				results = invocation.proceed();
				if (results == null) { results = NULL_OBJECT; /* we'll cache a null result too */ }
	
				// add to the cache
				if (cacheKey != null) {
					KEWServiceLocator.getCacheAdministrator().
					    putInCache(cacheKey, new SoftReference<Object>(results), cacheGroupName);
				}
			}
		} else {
			if (LOG.isTraceEnabled()) { LOG.trace("no caching enabled in group " +cacheGroupName+" on " + methodName); }
			results = invocation.proceed();
		}
		
		return (NULL_OBJECT == results) ? null : results;
	}

	/**
	 * build a cache key based on the method name and the parameters
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 */
	private String getCacheKey(String methodName, Object [] args) {
		StringBuilder sb = new StringBuilder(cacheGroupName);
		sb.append(".");
		sb.append(methodName);
		sb.append(":");
		boolean first = true;
		for (Object arg : args) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append((arg == null) ? "null" : arg.toString());
		}
		
		return sb.toString();
	}
}
