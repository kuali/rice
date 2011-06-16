/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.services.IdentityArchiveService;
import org.kuali.rice.kim.api.identity.services.IdentityService;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

import javax.jws.WebService;
import java.util.List;
import java.util.Map;

/**
 * This IdentityService implementation is largely just a knee-jerk delegator, except for
 * getters returning {@link KimEntityDefaultInfo} in which case the IdentityArchiveService 
 * will be invoked if the inner IndentityService impl returns null.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@WebService(endpointInterface = KIMWebServiceConstants.IdentityService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class IdentityCurrentAndArchivedServiceImpl implements IdentityService, IdentityUpdateService {

	private final IdentityArchiveService identityArchiveService;
	private final IdentityService innerIdentityService;
	
	/**
	 * This constructs a IdentityCurrentAndArchivedServiceImpl, injecting the
	 * needed services.
	 */
	public IdentityCurrentAndArchivedServiceImpl(IdentityService innerIdentityService, 
			IdentityArchiveService identityArchiveService) {
		this.innerIdentityService = innerIdentityService;
		this.identityArchiveService = identityArchiveService;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getAddressType(java.lang.String)
	 */
	public Type getAddressType(String code) {
		return getInnerIdentityService().getAddressType(code);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getAffiliationType(java.lang.String)
	 */
	public AffiliationTypeInfo getAffiliationType(String code) {
		return getInnerIdentityService().getAffiliationType(code);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getCitizenshipStatus(java.lang.String)
	 */
	public Type getCitizenshipStatus(String code) {
		return Type.Builder.create(getInnerIdentityService().getCitizenshipStatus(code)).build();
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, EntityName> getDefaultNamesForEntityIds(
			List<String> entityIds) {
		return getInnerIdentityService().getDefaultNamesForEntityIds(entityIds);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, KimEntityNamePrincipalNameInfo> getDefaultNamesForPrincipalIds(
			List<String> principalIds) {
		return getInnerIdentityService().getDefaultNamesForPrincipalIds(principalIds);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEmailType(java.lang.String)
	 */
	public Type getEmailType(String code) {
		return Type.Builder.create(getInnerIdentityService().getEmailType(code)).build();
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEmploymentStatus(java.lang.String)
	 */
	public EmploymentStatusInfo getEmploymentStatus(String code) {
		return getInnerIdentityService().getEmploymentStatus(code);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEmploymentType(java.lang.String)
	 */
	public EmploymentTypeInfo getEmploymentType(String code) {
		return getInnerIdentityService().getEmploymentType(code);
	}

	/**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfo(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
		KimEntityDefaultInfo entity = getInnerIdentityService().getEntityDefaultInfo(entityId);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchive( entityId );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
		return entity;
	}

	/**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(
			String principalId) {
		KimEntityDefaultInfo entity = getInnerIdentityService().getEntityDefaultInfoByPrincipalId(principalId);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchiveByPrincipalId( principalId );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
    	return entity;
	}

	/**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(
			String principalName) {
		KimEntityDefaultInfo entity = getInnerIdentityService().getEntityDefaultInfoByPrincipalName(principalName);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchiveByPrincipalName( principalName );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
    	return entity;
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfo(java.lang.String)
	 */
	public KimEntityInfo getEntityInfo(String entityId) {
		return getInnerIdentityService().getEntityInfo(entityId);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalId(String principalId) {
		return getInnerIdentityService().getEntityInfoByPrincipalId(principalId);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalName(String principalName) {
		return getInnerIdentityService().getEntityInfoByPrincipalName(principalName);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityNameType(java.lang.String)
	 */
	public Type getEntityNameType(String code) {
		return Type.Builder.create(getInnerIdentityService().getEntityNameType(code)).build();
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public EntityPrivacyPreferences getEntityPrivacyPreferences(
			String entityId) {
		return getInnerIdentityService().getEntityPrivacyPreferences(entityId);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityType(java.lang.String)
	 */
	public Type getEntityType(String code) {
		return Type.Builder.create(getInnerIdentityService().getEntityType(code)).build();
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getExternalIdentifierType(java.lang.String)
	 */
	public ExternalIdentifierTypeInfo getExternalIdentifierType(String code) {
		return getInnerIdentityService().getExternalIdentifierType(code);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getMatchingEntityCount(java.util.Map)
	 */
	public int getMatchingEntityCount(Map<String, String> searchCriteria) {
		return getInnerIdentityService().getMatchingEntityCount(searchCriteria);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPhoneType(java.lang.String)
	 */
	public Type getPhoneType(String code) {
		return Type.Builder.create(getInnerIdentityService().getPhoneType(code)).build();
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipal(java.lang.String)
	 */
	public Principal getPrincipal(String principalId) {
		return getInnerIdentityService().getPrincipal(principalId);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	public Principal getPrincipalByPrincipalName(String principalName) {
		return getInnerIdentityService().getPrincipalByPrincipalName(principalName);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
	 */
	public Principal getPrincipalByPrincipalNameAndPassword(
			String principalName, String password) {
		return getInnerIdentityService().getPrincipalByPrincipalNameAndPassword(
				principalName, password);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityDefaultInfo(java.util.Map, boolean)
	 */
	public List<KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String, String> searchCriteria, boolean unbounded) {
		return getInnerIdentityService().lookupEntityDefaultInfo(searchCriteria,
				unbounded);
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityInfo(java.util.Map, boolean)
	 */
	public List<KimEntityInfo> lookupEntityInfo(
			Map<String, String> searchCriteria, boolean unbounded) {
		return getInnerIdentityService().lookupEntityInfo(searchCriteria, unbounded);
	}

	private IdentityService getInnerIdentityService() {
		return innerIdentityService;
	}
	
	private IdentityArchiveService getIdentityArchiveService() {
		return identityArchiveService;
	}

}
