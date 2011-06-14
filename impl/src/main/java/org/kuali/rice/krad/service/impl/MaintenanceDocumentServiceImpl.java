/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.MaintenanceDocumentDao;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.document.MaintenanceLock;
import org.kuali.rice.krad.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.service.BusinessObjectAuthorizationService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Service implementation for the MaintenanceDocument structure. This is the
 * default implementation, that is delivered with Kuali.
 */
@TransactionalNoValidationExceptionRollback
public class MaintenanceDocumentServiceImpl implements MaintenanceDocumentService {
	protected static final Logger LOG = Logger.getLogger(MaintenanceDocumentServiceImpl.class);

	private MaintenanceDocumentDao maintenanceDocumentDao;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
	private BusinessObjectAuthorizationService businessObjectAuthorizationService;
	private DocumentService documentService;

	/**
	 * @see org.kuali.rice.krad.service.MaintenanceDocumentService#setupNewMaintenanceDocument(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public MaintenanceDocument setupNewMaintenanceDocument(String objectClassName, String documentTypeName,
			String maintenanceAction) {
		if (StringUtils.isEmpty(objectClassName) && StringUtils.isEmpty(documentTypeName)) {
			throw new IllegalArgumentException("Document type name or bo class not given!");
		}

		// get document type if not passed
		if (StringUtils.isEmpty(documentTypeName)) {
			try {
				documentTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(Class
						.forName(objectClassName));
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

			if (StringUtils.isEmpty(documentTypeName)) {
				throw new RuntimeException(
						"documentTypeName is empty; does this Business Object have a maintenance document definition? "
								+ objectClassName);
			}
		}

		// check doc type allows new or copy if that action was requested
		if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)
				|| KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
			Class<? extends BusinessObject> boClass = maintenanceDocumentDictionaryService
					.getBusinessObjectClass(documentTypeName);
			boolean allowsNewOrCopy = getBusinessObjectAuthorizationService().canCreate(boClass,
					GlobalVariables.getUserSession().getPerson(), documentTypeName);
			if (!allowsNewOrCopy) {
				LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
				throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson()
						.getPrincipalId(), "newOrCopy", documentTypeName);
			}
		}

		// get new document from service
		try {
			return (MaintenanceDocument) getDocumentService().getNewDocument(documentTypeName);
		}
		catch (WorkflowException e) {
			LOG.error("Cannot get new maintenance document instance for doc type: " + documentTypeName, e);
			throw new RuntimeException(
					"Cannot get new maintenance document instance for doc type: " + documentTypeName, e);
		}
	}

	/**
	 * @see org.kuali.rice.krad.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.krad.document.MaintenanceDocument)
	 */
	public String getLockingDocumentId(MaintenanceDocument document) {
		return getLockingDocumentId(document.getNewMaintainableObject(), document.getDocumentNumber());
	}

	/**
	 * @see org.kuali.rice.krad.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.krad.maintenance.Maintainable,
	 *      java.lang.String)
	 */
	public String getLockingDocumentId(Maintainable maintainable, String documentNumber) {
		String lockingDocId = null;
		List<MaintenanceLock> maintenanceLocks = maintainable.generateMaintenanceLocks();
		for (MaintenanceLock maintenanceLock : maintenanceLocks) {
			lockingDocId = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),
					documentNumber);
			if (StringUtils.isNotBlank(lockingDocId)) {
				break;
			}
		}
		return lockingDocId;
	}

	/**
	 * @see org.kuali.rice.krad.service.MaintenanceDocumentService#deleteLocks(String)
	 */
	public void deleteLocks(String documentNumber) {
		maintenanceDocumentDao.deleteLocks(documentNumber);
	}

	/**
	 * @see org.kuali.rice.krad.service.MaintenanceDocumentService#saveLocks(List)
	 */
	public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
		maintenanceDocumentDao.storeLocks(maintenanceLocks);
	}

	/**
	 * @return Returns the maintenanceDocumentDao.
	 */
	public MaintenanceDocumentDao getMaintenanceDocumentDao() {
		return maintenanceDocumentDao;
	}

	/**
	 * @param maintenanceDocumentDao
	 *            The maintenanceDocumentDao to set.
	 */
	public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
		this.maintenanceDocumentDao = maintenanceDocumentDao;
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		return this.maintenanceDocumentDictionaryService;
	}

	public void setMaintenanceDocumentDictionaryService(
			MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
		this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
	}

	protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		return this.businessObjectAuthorizationService;
	}

	public void setBusinessObjectAuthorizationService(
			BusinessObjectAuthorizationService businessObjectAuthorizationService) {
		this.businessObjectAuthorizationService = businessObjectAuthorizationService;
	}

	protected DocumentService getDocumentService() {
		return this.documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

}
