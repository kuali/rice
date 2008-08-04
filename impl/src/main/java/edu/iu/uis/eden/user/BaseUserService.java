/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.user;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.clientapp.vo.EmplIdVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UuIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.dao.BaseUserDAO;
import edu.iu.uis.eden.xml.UserXmlHandler;

/**
 * A UserService implementation which is backed by a database and fronted by an in-memory cache.
 * Because of the caching strategy being used, this service is not safe for use in a clustered environment.
 * The user objects maintained within this service are instances of SimpleWorkflowUser.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseUserService implements UserService {

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseUserService.class);
    public static final String ERR_KEY_INVALID_USER = "user.userservice.id.invalid";
    
    protected BaseUserDAO userDao;
    
    protected UserCache cache = new UserCache();
    protected UserCapabilities capabilities = UserCapabilities.getAll();
        
    public void setUserDAO(BaseUserDAO userDao) {
        this.userDao = userDao;
    }
    
    /**
     * Supports all user capabilities.
     */
    public UserCapabilities getCapabilities() {
		return capabilities;
	}

	public WorkflowUser getWorkflowUser(UserIdVO userId) throws EdenUserNotFoundException {
        UserId userIdInterface = null;
        if (userId instanceof EmplIdVO) {
            userIdInterface = new EmplId(((EmplIdVO)userId).getEmplId());
        } else if (userId instanceof NetworkIdVO) {
            userIdInterface = new AuthenticationUserId(((NetworkIdVO)userId).getNetworkId());
        } else if (userId instanceof UuIdVO) {
            userIdInterface = new UuId(((UuIdVO)userId).getUuId());
        } else if (userId instanceof WorkflowIdVO) {
            userIdInterface = new WorkflowUserId(((WorkflowIdVO)userId).getWorkflowId());
        } else {
            throw new EdenUserNotFoundException("Attempting to fetch user with unknown id type");
        }
        return getWorkflowUser(userIdInterface);
    }
    
    public WorkflowUser getWorkflowUser(UserId userId) throws EdenUserNotFoundException {
        WorkflowUser user = getFromCache(userId);
        if (user == null) {
        	user = (WorkflowUser) userDao.getWorkflowUser(userId);
        	if (user == null) {
            	throw new EdenUserNotFoundException("User is invalid. userId " + userId.toString());
        	} else {
        		addToCache(user);
        	}
        }
        return user;
    }
        
	public WorkflowUser getBlankUser() {
		return new BaseWorkflowUser();
	}
    
    public List<WorkflowUser> search(WorkflowUser user, boolean usesWildCard) {
    	return userDao.getSearchResults(user.getLastName(), user.getGivenName(), user.getAuthenticationUserId().getAuthenticationId(), user.getWorkflowUserId().getWorkflowId(), user.getEmplId().getEmplId(), user.getUuId().getUuId());
    }
    
    public void save(WorkflowUser user) {
    	BaseWorkflowUser simpleUser = (BaseWorkflowUser)user;
    	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    	if (user.getWorkflowId() == null || simpleUser.getCreateDate() == null) {
    		simpleUser.setCreateDate(currentTimestamp);
    	} else {
    		removeFromCache(user.getWorkflowUserId());
    	}
    	simpleUser.setLastUpdateDate(currentTimestamp);
		userDao.save(simpleUser);	
	}
    
    public WorkflowUser copy(WorkflowUser user, boolean preserveKeys) {
    	BaseWorkflowUser original = (BaseWorkflowUser)user;
    	BaseWorkflowUser userCopy = (BaseWorkflowUser) getBlankUser();
    	if (preserveKeys) {
    		userCopy.setWorkflowUserId(user.getWorkflowUserId());
    		userCopy.setLockVerNbr(original.getLockVerNbr());
    	}
    	userCopy.setAuthenticationUserId(original.getAuthenticationUserId());
    	userCopy.setDisplayName(original.getDisplayName());
    	userCopy.setEmailAddress(original.getEmailAddress());
    	userCopy.setEmplId(original.getEmplId());
    	userCopy.setGivenName(original.getGivenName());
    	userCopy.setLastName(original.getLastName());
    	userCopy.setUuId(original.getUuId());
    	userCopy.setCreateDate(original.getCreateDate());
    	userCopy.setLastUpdateDate(original.getLastUpdateDate());
    	return userCopy;
    }
    
	public void loadXml(InputStream stream, WorkflowUser user) {
        try {
            List parsedUsers = new UserXmlHandler().parseUserEntries(this, stream);
            for(Iterator iter = parsedUsers.iterator(); iter.hasNext();) {
            	save((BaseWorkflowUser) iter.next());
            }
        } catch (Exception e) {
        	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	}
            throw new RuntimeException("Caught Exception parsing user xml.", e);
        }
    }

	/**
	 * Add the given user to the cache.
	 */
	protected void addToCache(WorkflowUser user) {
		getCache().addToCache(user);
	}

	/**
	 * Remove the user with the given id from the cache.
	 */
	protected void removeFromCache(UserId userId) {
		getCache().removeFromCache(userId);
	}
	
	/**
	 * Retrieve the user with the given id from the cache. Returns null if there is no
	 * user in the cache with the given id.
	 */
	protected WorkflowUser getFromCache(UserId userId) {
		return getCache().getFromCache(userId);
	}

	/**
	 * Return the UserCache in which the users are being cached.
	 */
	protected UserCache getCache() {
		return cache;
	}
	
	protected BaseUserDAO getUserDAO() {
		return userDao;
	}
	
	/**
	 * A helper class which manages caching of users by various id types and retrieval of users by those id types.
	 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
	 */
	protected class UserCache {
		
		private Map authenticationIdMap = new HashMap();
	    private Map uuIdMap = new HashMap();
	    private Map emplIdMap = new HashMap();
	    private Map workflowIdMap = new HashMap();
		
	    public void addToCache(WorkflowUser user) {
	    	workflowIdMap.put(user.getWorkflowUserId(), user);
	    	authenticationIdMap.put(user.getAuthenticationUserId(), user);
	    	if (user.getUuId() != null && !StringUtils.isEmpty(user.getUuId().getUuId())) {
	    		uuIdMap.put(user.getUuId(), user);
	    	}
	    	if (user.getEmplId() != null && !StringUtils.isEmpty(user.getEmplId().getEmplId())) {
	    		emplIdMap.put(user.getEmplId(), user);
	    	}
	    }
	    
	    public void removeFromCache(UserId userId) {
	    	WorkflowUser user = getFromCache(userId);
	    	if (user != null) {
	    		workflowIdMap.remove(user.getWorkflowUserId());
	    		authenticationIdMap.remove(user.getAuthenticationUserId());
	    		if (user.getUuId() != null && !StringUtils.isEmpty(user.getUuId().getUuId())) {
	    			uuIdMap.remove(user.getUuId());
	    		}
	    		if (user.getEmplId() != null && !StringUtils.isEmpty(user.getEmplId().getEmplId())) {
	    			emplIdMap.remove(user.getEmplId());
	    		}
	    	}
	    }
	    
	    public WorkflowUser getFromCache(UserId userId) {
	    	WorkflowUser user = null;
	    	if (userId instanceof WorkflowUserId) {
	    		user = (WorkflowUser)workflowIdMap.get(userId);
	    	}
	    	if (user == null && userId instanceof AuthenticationUserId) {
	    		user = (WorkflowUser)authenticationIdMap.get(userId);
	    	}
	    	if (user == null && userId instanceof UuId) {
	    		user = (WorkflowUser)uuIdMap.get(userId);
	    	}
	    	if (user == null && userId instanceof EmplId) {
	    		user = (WorkflowUser)emplIdMap.get(userId);
	    	}
	    	return user;
	    }
	    
	}    
}