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
package org.kuali.rice.kns.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSUtils;

public class BusinessObjectAuthorizerBase implements BusinessObjectAuthorizer {
//	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
//			.getLogger(BusinessObjectAuthorizerBase.class);

	private static IdentityManagementService identityManagementService;
	private static PersonService personService;
	private static KualiModuleService kualiModuleService;
	private static DataDictionaryService dataDictionaryService;
	private static PersistenceStructureService persistenceStructureService;

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
			Object primaryDataObjectOrDocument,
			Map<String, String> attributes) {
		addStandardAttributes(primaryDataObjectOrDocument, attributes);
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
			Object primaryDataObjectOrDocument,
			Map<String, String> attributes) {
		addStandardAttributes(primaryDataObjectOrDocument, attributes);
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
			Object primaryDataObjectOrDocument,
			Map<String, String> attributes) {
		attributes
				.putAll(KNSUtils
						.getNamespaceAndComponentSimpleName(primaryDataObjectOrDocument
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
				new AttributeSet(getRoleQualification(businessObject, principalId)));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				new AttributeSet(getPermissionDetailValues(dataObject)),
				new AttributeSet(getRoleQualification(dataObject, principalId)));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		AttributeSet roleQualifiers = null;
		AttributeSet permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject, principalId));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = new AttributeSet(
					getRoleQualification(businessObject, principalId));
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

	public final boolean isAuthorizedByTemplate(Object dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		AttributeSet roleQualifiers = new AttributeSet(
				getRoleQualification(dataObject, principalId));
		AttributeSet permissionDetails = new AttributeSet(
				getPermissionDetailValues(dataObject));
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		}
		
		return getIdentityManagementService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				permissionDetails, roleQualifiers);
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
		return getRoleQualification(primaryBusinessObjectOrDocument, GlobalVariables
					.getUserSession().getPerson().getPrincipalId());
	}
	
	protected final Map<String, String> getRoleQualification(
			Object primaryDataObjectOrDocument, String principalId) {
			Map<String, String> roleQualification = new HashMap<String, String>();
			addRoleQualification(primaryDataObjectOrDocument,
					roleQualification);
			roleQualification.put(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId);
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
			Object dataObject) {
		Map<String, String> permissionDetails = new HashMap<String, String>();
		addPermissionDetails(dataObject, permissionDetails);
		return permissionDetails;
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
			kualiModuleService = KNSServiceLocatorWeb.getKualiModuleService();
		}
		return kualiModuleService;
	}

	protected static final DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KNSServiceLocatorWeb
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}
