/*
 * Copyright 2009 Arizona Board of Regents
 *
 */
package edu.arizona.kim.dataaccess.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NameClassPair;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.dao.impl.LdapPrincipalDaoImpl;
import org.kuali.rice.kim.ldap.InvalidLdapEntityException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextMapperCallbackHandler;
import org.springframework.ldap.core.DistinguishedName;

import edu.arizona.kim.eds.UaEdsConstants;

/**
 * Integrated Data Access via LDAP to EDS. Provides implementation to interface
 * method for using Spring-LDAP to communicate with EDS.
 *
 * @author Leo Przybylski (przybyls@arizona.edu)
 */
public class UaEdsPrincipalDaoOjb extends LdapPrincipalDaoImpl {
	protected static final Logger LOG = Logger.getLogger(UaEdsPrincipalDaoOjb.class);

	protected UaEdsConstants edsConstants;
	protected String base;
	protected DistinguishedName baseDistinguishedName;

	/**
	 * In EDS, the principalId, principalName, and entityId will all be the
	 * same.
	 */
	@Override
	public Principal getPrincipal(String principalId) {
		if (principalId == null) {
			return null;
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUaIdContextKey(), principalId);
		List<Principal> results = search(Principal.class, criteria);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * In EDS, the principalId, principalName, and entityId will all be the
	 * same.
	 */
	@Override
	public Principal getPrincipalByName(String principalName) {
		if (principalName == null) {
			return null;
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUidContextKey(), principalName);
		List<Principal> results = search(Principal.class, criteria);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	/** FIND entity objects based on the given criteria. */
	@Override
	public Entity getEntity(String entityId) {
		if (entityId == null) {
			return null;
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUaIdContextKey(), entityId);
		if (LOG.isInfoEnabled()) {
			LOG.info("Searching for entity with id " + entityId);
		}
		List<Entity> results = search(Entity.class, criteria);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Got results from info lookup " + results + " with size " + results.size());
		}
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public EntityDefault getEntityDefault(String entityId) {
		if (entityId == null) {
			return null;
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUaIdContextKey(), entityId);
		List<EntityDefault> results = search(EntityDefault.class, criteria);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Got results from info lookup " + results + " with size " + results.size());
		}
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * entityid and principalId are treated as the same.
	 * 
	 * @see #getEntityDefaultInfo(String)
	 */
	public EntityDefault getEntityDefaultByPrincipalId(String principalId) {
		return getEntityDefault(principalId);
	}

	@Override
	public EntityDefault getEntityDefaultByPrincipalName(String principalName) {
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUidContextKey(), principalName);
		List<EntityDefault> results = search(EntityDefault.class, criteria);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * takes a comma-delimited String as input and returns a List of String
	 * objects
	 * 
	 * @param input
	 * @return List of String objects
	 * 
	 *         private List<String> buildListFromCommaDelimitedString(String
	 *         input) { List<String> retval = new ArrayList<String>();
	 * 
	 *         StringTokenizer st = new StringTokenizer(input, ",");
	 * 
	 *         while (st.hasMoreTokens()) { retval.add(st.nextToken().trim()); }
	 * 
	 *         return retval; }
	 */

	@Override
	public EntityPrivacyPreferences getEntityPrivacyPreferences(String entityId) {
		if (entityId == null) {
			return null;
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(getEdsConstants().getUaIdContextKey(), entityId);
		List<EntityPrivacyPreferences> results = search(EntityPrivacyPreferences.class, criteria);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public Map<String, EntityNamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		Map<String, Object> criteria = new HashMap<String, Object>();
		Map<String, EntityNamePrincipalName> retval = new HashMap<String, EntityNamePrincipalName>();
		criteria.put(getEdsConstants().getUaIdContextKey(), principalIds);
		List<EntityNamePrincipalName> results = search(EntityNamePrincipalName.class, criteria);
		for (EntityNamePrincipalName nameInfo : results) {
			retval.put(nameInfo.getPrincipalName(), nameInfo);
		}
		return retval;
	}

	@Override
	public Map<String, EntityNamePrincipalName> getDefaultNamesForEntityIds(List<String> entityIds) {
		return getDefaultNamesForPrincipalIds(entityIds);
	}

	public UaEdsConstants getEdsConstants() {
		return edsConstants;
	}

	public void setEdsConstants(UaEdsConstants edsConstants) {
		this.edsConstants = edsConstants;
	}

	public DistinguishedName getBaseName() {
		if (baseDistinguishedName == null) {
			baseDistinguishedName = new DistinguishedName(base);
		}
		return baseDistinguishedName;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	/**
	 * Overrides the existing {@link ContextMapperCallbackHandler} because we
	 * want to intercede when there is invalid results from EDS.
	 * 
	 * @author Leo Przybylski (przybyls@arizona.edu)
	 */
	private static final class CustomContextMapperCallbackHandler extends ContextMapperCallbackHandler {
		public CustomContextMapperCallbackHandler(ContextMapper mapper) {
			super(mapper);
		}

		@Override
		public void handleNameClassPair(NameClassPair nameClassPair) {
			try {
				super.handleNameClassPair(nameClassPair);
			} catch (InvalidEdsEntityException ieee) {
				LOG.warn("LDAP Search Results yielded an invalid result from " + nameClassPair);
			}
		}
	}

	/**
	 * Exception that is used when we retrieve results from EDS that are not
	 * compatible with KIM
	 * 
	 * @author Leo Przybylski
	 */
	private static final class InvalidEdsEntityException extends InvalidLdapEntityException {
		private static final long serialVersionUID = 1L;

		public InvalidEdsEntityException(String message) {
			super(message);
		}
	}

}
