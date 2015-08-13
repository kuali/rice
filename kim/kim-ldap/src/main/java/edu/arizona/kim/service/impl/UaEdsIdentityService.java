/*
 * Copyright 2009 Arizona Board of Regents
 *
 */
package edu.arizona.kim.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.dao.LdapPrincipalDao;
import org.kuali.rice.kim.service.LdapIdentityService;
import org.kuali.rice.kim.service.impl.LdapIdentityDelegateServiceImpl;

/**
 * Implementation of {@link IdentityService} that communicates with and serves
 * information from the UA Enterprise Directory Service.
 * 
 * 
 * @author Leo Przybylski (przybyls@arizona.edu)
 */
public class UaEdsIdentityService extends LdapIdentityDelegateServiceImpl implements LdapIdentityService {
	private LdapPrincipalDao principalDao;

	/** Get a Principal object based on it's unique principal ID */
	@Override
	public Principal getPrincipal(String principalId) {
		Principal edsInfo = getPrincipalDao().getPrincipal(principalId);
		if (edsInfo != null) {
			return edsInfo;
		} else {
			return super.getPrincipal(principalId);
		}
	}

	/** Get a Principal object based on the principalName. */
	@Override
	public Principal getPrincipalByPrincipalName(String principalName) {
		Principal edsInfo = getPrincipalDao().getPrincipalByName(principalName);
		if (edsInfo != null) {
			return edsInfo;
		} else {
			return super.getPrincipalByPrincipalName(principalName);
		}
	}

	/**
	 * Password lookups not supported by EDS. Use Natural Authentication
	 * strategies instead of this if that's what you need.
	 *
	 */
	@Deprecated
	@Override
	public Principal getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
		return getPrincipalByPrincipalName(principalName);
	}

	/** Find entity objects based on the given criteria. */
	@Override
	public EntityDefault getEntityDefault(String entityId) {
		EntityDefault edsInfo = getPrincipalDao().getEntityDefault(entityId);
		if (edsInfo != null) {
			return edsInfo;
		} else {
			return super.getEntityDefault(entityId);
		}
	}

	@Override
	public EntityDefault getEntityDefaultByPrincipalId(String principalId) {
		return getEntityDefault(principalId);
	}

	@Override
	public EntityDefault getEntityDefaultByPrincipalName(String principalName) {
		EntityDefault edsInfo = getPrincipalDao().getEntityDefaultByPrincipalName(principalName);
		if (edsInfo != null) {
			return edsInfo;
		} else {
			return super.getEntityDefaultByPrincipalName(principalName);
		}
	}

	/**
	 * Overridden to populate this information from the EdsPrincipalDao
	 *
	 * @see org.kuali.rice.kim.impl.identity.IdentityServiceImpl#getEntityInfoByPrincipalId(java.lang.String)
	 */
	@Override
	public Entity getEntityByPrincipalId(String principalId) {
		return getPrincipalDao().getEntityByPrincipalId(principalId);
	}

	public List<EntityDefault> lookupEntityDefault(Map<String, String> searchCriteria, boolean unbounded) {
		return getPrincipalDao().lookupEntityDefault(searchCriteria, unbounded);
	}

	public List<EntityDefault> lookupEntity(Map<String, String> searchCriteria, boolean unbounded) {
		return getPrincipalDao().lookupEntityDefault(searchCriteria, unbounded);
	}

	public int getMatchingEntityCount(Map<String, String> searchCriteria) {
		return lookupEntityDefault(searchCriteria, true).size();
	}

	@Override
	public EntityPrivacyPreferences getEntityPrivacyPreferences(String entityId) {
		return getPrincipalDao().getEntityPrivacyPreferences(entityId);
	}

	public Map<String, EntityNamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		return getPrincipalDao().getDefaultNamesForPrincipalIds(principalIds);
	}

	public Map<String, EntityNamePrincipalName> getDefaultNamesForEntityIds(List<String> entityIds) {
		return getPrincipalDao().getDefaultNamesForEntityIds(entityIds);
	}

	/**
	 * Get the entity info for the entity with the given id.
	 */
	@Override
	public Entity getEntity(String entityId) {
		Entity edsInfo = getPrincipalDao().getEntity(entityId);
		if (edsInfo != null) {
			return edsInfo;
		} else {
			return super.getEntity(entityId);
		}
	}

	public void setPrincipalDao(LdapPrincipalDao principalDao) {
		this.principalDao = principalDao;
	}

	public LdapPrincipalDao getPrincipalDao() {
		return principalDao;
	}
}