/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentAuthorizationService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MaintenanceDocumentAuthorizationServiceImpl implements
		MaintenanceDocumentAuthorizationService {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.MaintenanceDocumentAuthorizationService#generateMaintenanceDocumentAuthorizations(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kim.bo.Person)
	 */
	public MaintenanceDocumentAuthorizations generateMaintenanceDocumentAuthorizations(
			MaintenanceDocument document, Person person) {
		MaintenanceDocumentAuthorizations maintenanceDocumentAuthorizations = new MaintenanceDocumentAuthorizations();
		
		String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

		MaintenanceDocumentPresentationController maintenanceDocumentPresentationController = (MaintenanceDocumentPresentationController) KNSServiceLocator.getDocumentTypeService().getDocumentPresentationController(docTypeName);
        maintenanceDocumentPresentationController.addMaintenanceDocumentRestrictions(maintenanceDocumentAuthorizations, document);
        
        MaintenanceDocumentAuthorizer maintenanceDocumentAuthorizer = (MaintenanceDocumentAuthorizer) KNSServiceLocator.getDocumentTypeService().getDocumentAuthorizer(docTypeName);
        maintenanceDocumentAuthorizer.addMaintenanceDocumentRestrictions(maintenanceDocumentAuthorizations, document, person);
        
		return maintenanceDocumentAuthorizations;
	}

}
