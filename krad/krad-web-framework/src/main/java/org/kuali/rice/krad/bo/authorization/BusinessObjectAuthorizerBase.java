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
package org.kuali.rice.krad.bo.authorization;


import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.HashMap;
import java.util.Map;

public class BusinessObjectAuthorizerBase implements BusinessObjectAuthorizer {
//	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
//			.getLogger(BusinessObjectAuthorizerBase.class);

	private static PermissionService permissionService;
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
				.putAll(KRADUtils
						.getNamespaceAndComponentSimpleName(primaryDataObjectOrDocument.getClass()));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName) {
		return getPermissionService()
				.isPermissionDefinedForTemplateName(
						namespaceCode,
						permissionTemplateName,
						new HashMap<String, String>(
								getPermissionDetailValues(businessObject)));
	}

	protected final boolean permissionExistsByTemplate(String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		return getPermissionService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName,
						new HashMap<String, String>(permissionDetails));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		Map<String, String> combinedPermissionDetails = new HashMap<String, String>(
				getPermissionDetailValues(businessObject));
		combinedPermissionDetails.putAll(permissionDetails);
		return getPermissionService()
				.isPermissionDefinedForTemplateName(namespaceCode,
						permissionTemplateName, combinedPermissionDetails);
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId) {
		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName,
				new HashMap<String, String>(getPermissionDetailValues(businessObject)),
				new HashMap<String, String>(getRoleQualification(businessObject, principalId)));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getPermissionService().isAuthorizedByTemplateName(
				principalId, namespaceCode, permissionTemplateName,
				new HashMap<String, String>(getPermissionDetailValues(dataObject)),
				new HashMap<String, String>(getRoleQualification(dataObject, principalId)));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		Map<String, String> roleQualifiers = null;
		Map<String, String> permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
		}
		
		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName, permissionDetails,
				roleQualifiers);
	}

	public final boolean isAuthorizedByTemplate(Object dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		Map<String, String> roleQualifiers = new HashMap<String, String>(
				getRoleQualification(dataObject, principalId));
		Map<String, String> permissionDetails = new HashMap<String, String>(
				getPermissionDetailValues(dataObject));
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		}
		if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		}
		
		return getPermissionService().isAuthorizedByTemplateName(
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
	 * @see org.kuali.rice.krad.authorization.BusinessObjectAuthorizer#getCollectionItemPermissionDetails(org.kuali.rice.krad.bo.BusinessObject)
	 */
	public Map<String, String> getCollectionItemPermissionDetails(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
	}

	/**
	 * @see org.kuali.rice.krad.authorization.BusinessObjectAuthorizer#getCollectionItemRoleQualifications(org.kuali.rice.krad.bo.BusinessObject)
	 */
	public Map<String, String> getCollectionItemRoleQualifications(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
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

	protected static final PermissionService getPermissionService() {
		if (permissionService == null) {
			permissionService = KimApiServiceLocator
					.getPermissionService();
		}
		return permissionService;
	}

	protected static final PersonService getPersonService() {
		if (personService == null) {
			personService = KimApiServiceLocator.getPersonService();
		}
		return personService;
	}

	protected static final KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
		}
		return kualiModuleService;
	}

	protected static final DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KRADServiceLocatorWeb
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}
