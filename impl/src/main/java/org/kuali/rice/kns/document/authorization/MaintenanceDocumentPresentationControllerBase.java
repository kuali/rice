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
package org.kuali.rice.kns.document.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

/**
 * Base class for all MaintenanceDocumentPresentationControllers.
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase implements MaintenanceDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(MaintenanceDocumentPresentationControllerBase.class);

    private static MaintenanceDocumentDictionaryService  maintenanceDocumentDictionaryService;
    

   /**
    * 
    * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCreate(org.kuali.rice.kns.document.Document)
    */
    public boolean canCreate(Class boClass){
    	//TODO: 
    	return true;
    }
   

	/**
	 * 
	 * @param document
	 * @return
	 */
	public List getReadOnlyFields(Document document){
		List<MaintainableFieldDefinition> readOnlyFields = new ArrayList();
		
		String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

    	MaintenanceDocumentEntry objectEntry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(documentType);
    	
    	List<MaintainableSectionDefinition> maintainableSectionDefinitions = objectEntry.getMaintainableSections();
		for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
			List<MaintainableItemDefinition> maintainableItems = maintainableSectionDefinition.getMaintainableItems();
			
			readOnlyFields.addAll(getReadOnlyFieldList(readOnlyFields,maintainableItems));
			
		}
		return readOnlyFields;
	}
	
	private static List<MaintainableFieldDefinition> getReadOnlyFieldList(
			List<MaintainableFieldDefinition> returnList, List items) {
		
		for (Object item: items) {
			if (item instanceof MaintainableFieldDefinition) {
				MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;
				
				if(maintainableFieldDefinition.isUnconditionallyReadOnly()){
					returnList.add(maintainableFieldDefinition);
				}
			} else if (item instanceof MaintainableCollectionDefinition) {
				MaintainableCollectionDefinition maintainableCollectionDefinition = (MaintainableCollectionDefinition) item;
				getReadOnlyFieldList(
						returnList,
						maintainableCollectionDefinition.getMaintainableCollections());
				getReadOnlyFieldList(
						returnList,
						maintainableCollectionDefinition.getMaintainableFields());
			}
		}
		return returnList;
	}

	/**
	 * @return the maintenanceDocumentDictionaryService
	 */
	public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		return maintenanceDocumentDictionaryService;
	}
	
	
}
