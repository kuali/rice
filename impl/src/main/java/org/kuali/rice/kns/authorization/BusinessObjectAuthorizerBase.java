/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;

public class BusinessObjectAuthorizerBase implements BusinessObjectAuthorizer {
	private static IdentityManagementService identityManagementService;
	private static PersonService personService;
	private static KualiModuleService kualiModuleService;
	private static DataDictionaryService dataDictionaryService;
	private ThreadLocal<AttributeSet> roleQualification = new ThreadLocal<AttributeSet>();
	private ThreadLocal<AttributeSet> permissionDetails = new ThreadLocal<AttributeSet>();

	/**
	 * Override this method to populate the role qualifier attributes from the
	 * business object. This will only be called once per request.
	 */
	protected void addRoleQualification(BusinessObject businessObject,
			Map<String, String> attributes) {
		addStandardAttributes(businessObject, attributes);
	}

	/**
	 * Override this method to populate the role qualifier attributes from the
	 * document for the given document. This will only be called once per
	 * request.
	 */
	protected void addPermissionDetails(BusinessObject businessObject,
			Map<String, String> attributes) {
		addStandardAttributes(businessObject, attributes);
	}

	private void addStandardAttributes(BusinessObject businessObject,
			Map<String, String> attributes) {
		attributes.putAll(KimCommonUtils
				.getNamespaceAndComponentSimpleName(businessObject.getClass()));
	}

	protected final boolean permissionExistsByTemplate(String namespaceCode,
			String permissionTemplateName, BusinessObject businessObject) {
		return getIdentityManagementService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName,
						getPermissionDetailValues(businessObject));
	}

	protected final boolean permissionExistsByTemplate(String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		return getIdentityManagementService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName,
						new AttributeSet(permissionDetails));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId) {
		return getIdentityManagementService().isAuthorized(principalId,
				namespaceCode, permissionName,
				getPermissionDetailValues(businessObject),
				getRoleQualification(businessObject));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject businessObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				getPermissionDetailValues(businessObject),
				getRoleQualification(businessObject));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		AttributeSet roleQualifiers = null;
		AttributeSet permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = getRoleQualification(businessObject);
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = getPermissionDetailValues(businessObject);
		}
		return getIdentityManagementService().isAuthorized(principalId,
				namespaceCode, permissionName, permissionDetails,
				roleQualifiers);
	}

	public final boolean isAuthorizedByTemplate(BusinessObject businessObject,
			String namespaceCode, String permissionTemplateName,
			String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		AttributeSet roleQualifiers = null;
		AttributeSet permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = getRoleQualification(businessObject);
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = getPermissionDetailValues(businessObject);
		}
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				permissionDetails, roleQualifiers);
	}

	private AttributeSet getRoleQualification(BusinessObject businessObject) {
		if (roleQualification.get() == null) {
			Map<String, String> attributes = new HashMap<String, String>();
			addRoleQualification(businessObject, attributes);
			attributes.put(KimConstants.PropertyNames.PRINCIPAL_ID,
					GlobalVariables.getUserSession().getPerson()
							.getPrincipalId());
			roleQualification.set(new AttributeSet(attributes));
		}
		return roleQualification.get();
	}

	protected final AttributeSet getPermissionDetailValues(
			BusinessObject businessObject) {
		if (permissionDetails.get() == null) {
			Map<String, String> attributes = new HashMap<String, String>();
			addPermissionDetails(businessObject, attributes);
			permissionDetails.set(new AttributeSet(attributes));
		}
		return permissionDetails.get();
	}

	protected static final IdentityManagementService getIdentityManagementService() {
		if (identityManagementService == null) {
			identityManagementService = KIMServiceLocator
					.getIdentityManagementService();
		}
		return identityManagementService;
	}

	protected static final PersonService getPersonService() {
		if (personService == null) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

	protected static final KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KNSServiceLocator.getKualiModuleService();
		}
		return kualiModuleService;
	}

	protected static final DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KNSServiceLocator
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}