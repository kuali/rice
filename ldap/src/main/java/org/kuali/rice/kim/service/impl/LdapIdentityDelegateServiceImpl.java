/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.service.impl.IdentityServiceImpl;

import org.kuali.rice.kim.dao.LdapPrincipalDao;

import static org.kuali.rice.core.util.BufferedLogger.*;

/**
 * Implementation of {@link IdentityService} that communicates with and serves information
 * from the UA Enterprise Directory Service.
 * 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LdapIdentityDelegateServiceImpl extends IdentityServiceImpl implements IdentityService {
    private LdapPrincipalDao principalDao;
    
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#lookupEntityIds(java.util.Map)
	 */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria) {
        final List<String> edsInfo = getPrincipalDao().lookupEntityIds(searchCriteria);
        if (edsInfo.size() > 0) {
            return edsInfo;
        } 
        else {
            return super.lookupEntityIds(searchCriteria);
        }
    }

	public int getMatchingEntityCount(Map<String,String> searchCriteria) {
        return lookupEntityIds(searchCriteria).size();
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityInfo(java.lang.String)
	 */
	public Entity getEntity(String entityId) {
        Entity edsInfo = getPrincipalDao().getEntity(entityId);
        if (edsInfo != null) {
            return edsInfo;
        } else {
            return super.getEntity(entityId);
        }
	}
	
	/**
	 * Overridden to populate this information from the LdapPrincipalDao
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	public Entity getEntityByPrincipalId(String principalId) {
        Entity edsInfo = getPrincipalDao().getEntityByPrincipalId(principalId);
        if (edsInfo != null) {
            return edsInfo;
        } else {
            return super.getEntityByPrincipalId(principalId);
        }
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	public Entity getEntityByPrincipalName(String principalName) {
        final Entity edsInfo = getPrincipalDao().getEntityByPrincipalName(principalName);
        if (edsInfo != null) {
            return edsInfo;
        } else {
            return super.getEntityByPrincipalName(principalName);
        }
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityDefault(java.lang.String)
	 */
	public EntityDefault getEntityDefault(String entityId) {
        EntityDefault edsInfo = getPrincipalDao().getEntityDefault(entityId);
        if (edsInfo != null) {
            return edsInfo;
        } 
        else {
            return super.getEntityDefault(entityId);
        }
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityDefaultByPrincipalId(java.lang.String)
	 */
	public EntityDefault getEntityDefaultByPrincipalId(String principalId) {
        final EntityDefault retval = getPrincipalDao().getEntityDefaultByPrincipalId(principalId);
        if (retval != null) {
            return retval;
        }
        else {
            return super.getEntityDefaultByPrincipalId(principalId);
        }
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityDefaultByPrincipalName(java.lang.String)
	 */
	public EntityDefault getEntityDefaultByPrincipalName(String principalName) {
        final EntityDefault retval = getPrincipalDao().getEntityDefaultByPrincipalName(principalName);
        if (retval != null) {
            return retval;
        }
        else {
            return super.getEntityDefaultByPrincipalName(principalName);
        }
	}
	
    /**
     * Password lookups not supported by EDS. Use Natural Authentication strategies instead
     * of this if that's what you need.
     *
     */
    @Deprecated
	public Principal getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
        return getPrincipalByPrincipalName(principalName);
    }
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public EntityPrivacyPreferences getEntityPrivacyPreferences(String entityId) {
        return getPrincipalDao().getEntityPrivacyPreferences(entityId);
	}

    /**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getPrincipal(java.lang.String)
	 */
	public Principal getPrincipal(String principalId) {	
        final Principal edsInfo = getPrincipalDao().getPrincipal(principalId);
            if (edsInfo != null) {
	        return edsInfo;
	    } else {
	        return super.getPrincipal(principalId);
	    }
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Principal getPrincipalByPrincipalName(String principalName) {
        final Principal edsInfo = getPrincipalDao().getPrincipalByName(principalName);
        if (edsInfo != null) {
            return edsInfo;
        } else {
            return super.getPrincipalByPrincipalName(principalName);
        }
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityIdByPrincipalId(java.lang.String)
	 */
	public String getEntityIdByPrincipalId(String principalId) {
        return getEntityByPrincipalId(principalId).getId();
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getEntityIdByPrincipalName(java.lang.String)
	 */
	public String getEntityIdByPrincipalName(String principalName) {
        return getEntityByPrincipalName(principalName).getId();
    }
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getPrincipalIdByPrincipalName(java.lang.String)
	 */
	public String getPrincipalIdByPrincipalName(String principalName) {
        return getPrincipalByPrincipalName(principalName).getPrincipalId();
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, EntityNamePrincipalName> getDefaultNamesForEntityIds(List<String> entityIds) {
        return getPrincipalDao().getDefaultNamesForEntityIds(entityIds);
	}



    /**
	 * @see org.kuali.rice.kim.api.identity.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, EntityNamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds) {
        return getPrincipalDao().getDefaultNamesForPrincipalIds(principalIds);
	}

    public void setPrincipalDao(LdapPrincipalDao principalDao) {
        this.principalDao = principalDao;
    }

    public LdapPrincipalDao getPrincipalDao() {
        return principalDao;
    } 
}