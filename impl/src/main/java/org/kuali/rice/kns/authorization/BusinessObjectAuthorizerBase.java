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

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;

public class BusinessObjectAuthorizerBase implements BusinessObjectAuthorizer {
	private static IdentityManagementService identityManagementService;
	private static PersonService<Person> personService;
	private static KualiModuleService kualiModuleService;
	private static DataDictionaryService dataDictionaryService;
	private static final String ROLE_QUALIFICATION_CACHE_NAME = "BusinessObjectAuthorizerBase.roleQualification";
	private static final String PERMISSION_DETAILS_CACHE_NAME = "BusinessObjectAuthorizerBase.permissionDetails";

	/**
	 * Override this method to populate the role qualifier attributes from the
	 * primary business object or document. This will only be called once per
	 * request.
	 * 
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @param attributes
	 *            role qualifiers will be added to this map
	 */
	protected void addRoleQualification(
			BusinessObject primaryBusinessObjectOrDocument,
			Map<String, String> attributes) {
		addStandardAttributes(primaryBusinessObjectOrDocument, attributes);
	}

	/**
	 * Override this method to populate the permission details from the primary
	 * business object or document. This will only be called once per request.
	 * 
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @param attributes
	 *            permission details will be added to this map
	 */
	protected void addPermissionDetails(
			BusinessObject primaryBusinessObjectOrDocument,
			Map<String, String> attributes) {
		addStandardAttributes(primaryBusinessObjectOrDocument, attributes);
	}

	/**
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @param attributes
	 *            attributes (i.e. role qualifications or permission details)
	 *            will be added to this map
	 */
	private void addStandardAttributes(
			BusinessObject primaryBusinessObjectOrDocument,
			Map<String, String> attributes) {
		attributes
				.putAll(KimCommonUtils
						.getNamespaceAndComponentSimpleName(primaryBusinessObjectOrDocument
								.getClass()));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName) {
		return getIdentityManagementService()
				.isPermissionDefinedForTemplateName(
						namespaceCode,
						permissionTemplateName,
						new AttributeSet(
								getPermissionDetailValues(businessObject)));
	}

	protected final boolean permissionExistsByTemplate(String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		return getIdentityManagementService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName,
						new AttributeSet(permissionDetails));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		AttributeSet combinedPermissionDetails = new AttributeSet(
				getPermissionDetailValues(businessObject));
		combinedPermissionDetails.putAll(permissionDetails);
		return getIdentityManagementService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName, combinedPermissionDetails);
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId) {
		return getIdentityManagementService().isAuthorized(principalId,
				namespaceCode, permissionName,
				new AttributeSet(getPermissionDetailValues(businessObject)),
				new AttributeSet(getRoleQualification(businessObject)));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject businessObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				new AttributeSet(getPermissionDetailValues(businessObject)),
				new AttributeSet(getRoleQualification(businessObject)));
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
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject));
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
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
		return isAuthorizedByTemplate(businessObject, null, namespaceCode,
				permissionTemplateName, principalId,
				collectionOrFieldLevelPermissionDetails,
				collectionOrFieldLevelRoleQualification);
	}

	public final boolean isAuthorizedByTemplate(BusinessObject businessObject,
			BusinessObject collectionElementBusinessObject,
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
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject));
		}
		if (collectionElementBusinessObject != null) {

		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = new AttributeSet(
					getPermissionDetailValues(businessObject));
		}
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				permissionDetails, roleQualifiers);
	}

	@SuppressWarnings("unchecked")
	private Map<Object,Map<String,String>> getRoleQualificationCache() {
		Map<Object,Map<String,String>> roleQualificationCache = (Map<Object,Map<String,String>>)GlobalVariables.getRequestCache(ROLE_QUALIFICATION_CACHE_NAME);
		if ( roleQualificationCache == null ) {
			roleQualificationCache = new HashMap<Object,Map<String,String>>();
			GlobalVariables.setRequestCache(ROLE_QUALIFICATION_CACHE_NAME, roleQualificationCache);
		}		
		return roleQualificationCache;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Object,Map<String,String>> getPermissionDetailsCache() {
		Map<Object,Map<String,String>> permissionDetailsCache = (Map<Object,Map<String,String>>)GlobalVariables.getRequestCache(PERMISSION_DETAILS_CACHE_NAME);
		if ( permissionDetailsCache == null ) {
			permissionDetailsCache = new HashMap<Object,Map<String,String>>();
			GlobalVariables.setRequestCache(PERMISSION_DETAILS_CACHE_NAME, permissionDetailsCache);
		}		
		return permissionDetailsCache;
	}
	
	/**
	 * Returns a role qualification map based off data from the primary business
	 * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
	 * 
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @return a Map containing role qualifications
	 */
	protected final Map<String, String> getRoleQualification(
			BusinessObject primaryBusinessObjectOrDocument) {
		Map<Object,Map<String,String>> roleQualificationCache = getRoleQualificationCache();
		Map<String, String> roleQualification = roleQualificationCache.get( primaryBusinessObjectOrDocument );
		if (roleQualification == null ) {
			roleQualification = new HashMap<String, String>();
			addRoleQualification(primaryBusinessObjectOrDocument, roleQualification);
			roleQualification.put(KimAttributes.PRINCIPAL_ID,
					GlobalVariables.getUserSession().getPerson().getPrincipalId());
			roleQualificationCache.put( primaryBusinessObjectOrDocument, roleQualification );
		}
		return roleQualification;
	}

	/**
	 * @see org.kuali.rice.kns.authorization.BusinessObjectAuthorizer#getCollectionItemPermissionDetails(org.kuali.rice.kns.bo.BusinessObject)
	 */
	public Map<String, String> getCollectionItemPermissionDetails(
			BusinessObject collectionItemBusinessObject) {
		return new AttributeSet();
	}

	/**
	 * @see org.kuali.rice.kns.authorization.BusinessObjectAuthorizer#getCollectionItemRoleQualifications(org.kuali.rice.kns.bo.BusinessObject)
	 */
	public Map<String, String> getCollectionItemRoleQualifications(
			BusinessObject collectionItemBusinessObject) {
		return new AttributeSet();
	}

	/**
	 * Returns a permission details map based off data from the primary business
	 * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
	 * 
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @return a Map containing permission details
	 */
	protected final Map<String, String> getPermissionDetailValues(
			BusinessObject businessObject) {
		Map<Object,Map<String,String>> permissionDetailsCache = getPermissionDetailsCache();
		Map<String, String> permissionDetails = permissionDetailsCache.get( businessObject );
		if (permissionDetails == null) {
			permissionDetails = new HashMap<String, String>();
			addPermissionDetails(businessObject, permissionDetails);
			permissionDetailsCache.put( businessObject, permissionDetails );
		}
		return permissionDetails;
	}

	protected static final IdentityManagementService getIdentityManagementService() {
		if (identityManagementService == null) {
			identityManagementService = KIMServiceLocator
					.getIdentityManagementService();
		}
		return identityManagementService;
	}

	protected static final PersonService<Person> getPersonService() {
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