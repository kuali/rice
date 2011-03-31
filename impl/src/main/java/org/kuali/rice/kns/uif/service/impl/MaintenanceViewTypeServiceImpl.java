/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifConstants.ViewTypeParameterNames;
import org.kuali.rice.kns.uif.container.MaintenanceView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewTypeService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * Type service implementation for maintenance views
 * 
 * <p>
 * Indexes views on object class and name. Can retrieve views by object class,
 * object class and name, or document id
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceViewTypeServiceImpl implements ViewTypeService {
	private DocumentService documentService;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getViewTypeName()
	 */
	public String getViewTypeName() {
		return ViewType.MAINTENANCE;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromView(org.kuali.rice.kns.uif.container.View)
	 */
	public Map<String, String> getParametersFromView(View view) {
		Map<String, String> parameters = new HashMap<String, String>();

		MaintenanceView maintenanceView = (MaintenanceView) view;

		parameters.put(ViewTypeParameterNames.NAME, maintenanceView.getViewName());
		parameters.put(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME, maintenanceView.getDataObjectClassName()
				.getName());

		return parameters;
	}

	/**
	 * Check for document id in request parameters, if given retrieve document
	 * instance to get the object class and set the name parameter
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromRequest(java.util.Map)
	 */
	@Override
	public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters) {
		Map<String, String> parameters = new HashMap<String, String>();

		if (requestParameters.containsKey(ViewTypeParameterNames.NAME)) {
			parameters.put(ViewTypeParameterNames.NAME, requestParameters.get(ViewTypeParameterNames.NAME));
		}
		else {
			parameters.put(ViewTypeParameterNames.NAME, UifConstants.DEFAULT_VIEW_NAME);
		}

		if (requestParameters.containsKey(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME)) {
			parameters.put(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME,
					requestParameters.get(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME));
		}
		else if (requestParameters.containsKey(KNSPropertyConstants.DOC_ID)) {
			String documentNumber = requestParameters.get(KNSPropertyConstants.DOC_ID);

			boolean objectClassFound = false;
			try {
				// determine object class based on the document type
				Document document = documentService.getByDocumentHeaderId(documentNumber);
				if (document != null) {
					String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
					Class<?> objectClassName = maintenanceDocumentDictionaryService.getBusinessObjectClass(docTypeName);
					if (objectClassName != null) {
						objectClassFound = true;
						parameters.put(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME, objectClassName.getName());
					}
				}

				if (!objectClassFound) {
					throw new RuntimeException("Could not determine object class for maintenance document with id: "
							+ documentNumber);
				}
			}
			catch (WorkflowException e) {
				throw new RuntimeException("Encountered workflow exception while retrieving document with id: "
						+ documentNumber, e);
			}
		}

		return parameters;
	}

	protected DocumentService getDocumentService() {
		return this.documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		return this.maintenanceDocumentDictionaryService;
	}

	public void setMaintenanceDocumentDictionaryService(
			MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
		this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
	}

}
