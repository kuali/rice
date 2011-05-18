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
package org.kuali.rice.kew.useroptions;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.ksb.api.bus.services.KsbApiServiceLocator;

/**
 * This class decorates a UserOptionsService and provides distributed caching for findByOptionId calls, and 
 * intelligently clears cache entries on update/delete type calls.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UserOptionsServiceCache implements UserOptionsService {
	
	private UserOptionsService inner;

	private final ClusterSafeMethodCache cache = new ClusterSafeMethodCache(UserOptionsService.class.getSimpleName()); 
	
	/**
	 * clears any entry in the cache for this option id
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#deleteUserOptions(org.kuali.rice.kew.useroptions.UserOptions)
	 */
	public void deleteUserOptions(UserOptions userOptions) {
		if (userOptions != null && userOptions.getWorkflowId() != null) {
			cache.clearCacheEntry(userOptions.getWorkflowId(), "findByOptionId", userOptions.getOptionId(), userOptions.getWorkflowId());
		}
		inner.deleteUserOptions(userOptions);
	}

	/**
	 * Checks the cache for results.  On cache miss, delegates the call and caches the result before returning.
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#findByOptionId(java.lang.String, java.lang.String)
	 */
	public UserOptions findByOptionId(String optionId, String principalId) {
		Object result = null;
		if (optionId != null && principalId != null) {
			String methodName = "findByOptionId";
			Object [] arguments = {optionId, principalId};

			result = cache.getFromCache(principalId, methodName, arguments);
			if (result == null) {
				result = inner.findByOptionId(optionId, principalId);
				cache.putInCache(principalId, methodName, (result != null) ? result : cache.NULL_OBJECT, arguments);
			}
		} else {
			// just delegate on bad inputs:
			result = inner.findByOptionId(optionId, principalId);
		}

    	return (result == cache.NULL_OBJECT) ? null : (UserOptions) result;		
	}

	/**
	 * This overridden method delegates the call.
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#findByOptionValue(java.lang.String, java.lang.String)
	 */
	public Collection<UserOptions> findByOptionValue(String optionId,
			String optionValue) {
		// no caching for this method since it doesn't discriminate by user
		return inner.findByOptionValue(optionId, optionValue);
	}

	/**
	 * This overridden method delegates the call.
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#findByUserQualified(java.lang.String, java.lang.String)
	 */
	public List<UserOptions> findByUserQualified(String principalId,
			String likeString) {
		// no caching for this method since it could be pulling in saveRefreshUserOption generated UserOptions
		return inner.findByUserQualified(principalId, likeString);
	}

	/**
	 * This overridden method delegates the call.
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#findByWorkflowUser(java.lang.String)
	 */
	public Collection<UserOptions> findByWorkflowUser(String principalId) {
		// no caching for this method since it could be pulling in saveRefreshUserOption generated UserOptions
		return inner.findByWorkflowUser(principalId);
	}

	/**
	 * This overridden method ...
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#refreshActionList(java.lang.String)
	 */
	public boolean refreshActionList(String principalId) {
        // ignore the cache for this user option, as these are generated with unique numbers and shouldn't be
	    // retrieved more then once, as they are deleted once retrieved
		return inner.refreshActionList(principalId);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#save(java.lang.String, java.util.Map)
	 */
	public void save(String principalId, Map<String, String> optionsMap) {
		inner.save(principalId, optionsMap);
		cache.clearCacheGroup(principalId);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#save(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void save(String principalId, String optionId, String optionValue) {
		inner.save(principalId, optionId, optionValue);
		cache.clearCacheGroup(principalId);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#save(org.kuali.rice.kew.useroptions.UserOptions)
	 */
	public void save(UserOptions userOptions) {
		inner.save(userOptions);
		cache.clearCacheGroup(userOptions.getWorkflowId());
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.useroptions.UserOptionsService#saveRefreshUserOption(java.lang.String)
	 */
	public void saveRefreshUserOption(String principalId) {
		// this shouldn't impact our simple cache since we're only caching single options queries, and this will 
		// generate a key that hasn't been used previously
		inner.saveRefreshUserOption(principalId);
	}
	
	/**
	 * @param inner the decorated service
	 */
	public void setInnerService(UserOptionsService inner) {
		this.inner = inner;
	}
	
	/**
	 * a helper class to encapsulate cluster safe cache functionality 
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
    private static class ClusterSafeMethodCache {
    	
    	public static final Object NULL_OBJECT = new Object(); 
    	public final String cachedServiceName;
    	
    	/**
		 * This constructs a ClusterSaveMethodCache
		 * 
		 * @param cachedServiceName a name for the service being cached
		 */
		public ClusterSafeMethodCache(String cachedServiceName) {
			this.cachedServiceName = cachedServiceName;
		}
    	
    	/**
    	 * build a cache key based on the user, method name and the parameters
    	 * 
    	 * @param methodName
    	 * @param args
    	 * @return
    	 */
    	private String getCacheKey(String principalId, String methodName, Object ... args) {
    		
    		StringBuilder sb = new StringBuilder(principalId);
    		sb.append("/");
    		sb.append(cachedServiceName);
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
    	
    	/**
    	 * caches a method result
    	 * 
    	 * @param principalId the principal for whom the call is being cached
    	 * @param methodName the name of the method whose result is being cached
    	 * @param value 
    	 * @param keySource the parameters to the method call that is being cached.  These are used to build the cache key, so order is important. 
    	 */
    	@SuppressWarnings("unchecked")
    	public void putInCache(String principalId, String methodName, Object value, Object ... keySource) {
    		if (value != null) {
    			KsbApiServiceLocator.getCacheAdministrator().putInCache(getCacheKey(principalId, methodName, keySource), new SoftReference(value), getCacheGroup(principalId));
    		} else {
    			KsbApiServiceLocator.getCacheAdministrator().putInCache(getCacheKey(principalId, methodName, keySource), NULL_OBJECT, getCacheGroup(principalId));
    		}
    	}
    	
    	/**
    	 * retrieves a method result from the cache.  If the static final NULL_OBJECT is returned then the cached result
    	 * was null.
    	 * 
    	 * @param principalId the principal for whom the call is being cached
    	 * @param methodName the name of the method whose result is being cached
    	 * @param keySource the parameters to the method call that is being cached.  These are used to build the cache key, so order is important. 
    	 * @return
    	 */
    	@SuppressWarnings("unchecked")
    	public Object getFromCache(String principalId, String methodName, Object ... keySource) {
    		Object result = null;
    		Object cacheReturned = KsbApiServiceLocator.getCacheAdministrator().getFromCache(getCacheKey(principalId, methodName, keySource));
    		if (cacheReturned == NULL_OBJECT) {
    			result = cacheReturned;
    		} else {
    			SoftReference reference = (SoftReference)cacheReturned;
    			if (reference != null) {
    				result = reference.get();
    			}
    		}
    		return result;
    	}
    	
    	/**
    	 * This method clears all cached calls for the given principal
    	 * 
    	 * @param principalId the principal for whom the cache is being cleared
    	 */
    	public void clearCacheGroup(String principalId) {
    		if (principalId != null) {
    			KsbApiServiceLocator.getCacheAdministrator().flushGroup(getCacheGroup(principalId));
    		}
    	}
    	
    	/**
    	 * This method clears a cache entry for a given method call / arguments combination
    	 * 
    	 * @param principalId the principal for whom the cache entry is being cleared
    	 * @param methodName the name of the method whose result is being cached
    	 * @param keySource the parameters to the method call that is being cached.  These are used to build the cache key, so order is important.
    	 */
    	public void clearCacheEntry(String principalId, String methodName, Object ... keySource) {
    		KsbApiServiceLocator.getCacheAdministrator().flushEntry(getCacheKey(principalId, methodName, keySource));
    	}
    	
    	/**
    	 * This method gets the cache group name (an entire cache group can be cleared at once)
    	 * 
    	 * @param principalId the principal whose cache group name is being generated
    	 * @return the cache group name
    	 */
    	private String getCacheGroup(String principalId) {
    		return principalId + "/" + cachedServiceName;
    	}
    }
	
}
