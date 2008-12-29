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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Base class for all MaintenanceDocumentPresentationControllers.
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase implements MaintenanceDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(MaintenanceDocumentPresentationControllerBase.class);

    private static MaintenanceDocumentDictionaryService  maintenanceDocumentDictionaryService;
    

   /**
    * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCreate(org.kuali.rice.kns.document.Document)
    */
    public final boolean canCreate(Class boClass){
    	//TODO: return the value of allowsNewOrCopy from the data dictionary
    	return true;
    }

	public Set<String> getConditionallyReadOnlyPropertyNames(Document document) {
		return new HashSet<String>();
	}
	
	public Set<String> getConditionallyHiddenPropertyNames(Document document) {
		return new HashSet<String>();
	}
	
	public Set<String> getConditionallyHiddenSectionIds(Document document) {
		return new HashSet<String>();
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
    @Deprecated
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
	
	@Deprecated
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
	public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController#addMaintenanceDocumentRestrictions(org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations, org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	public void addMaintenanceDocumentRestrictions(
			MaintenanceDocumentAuthorizations auths,
			MaintenanceDocument document) {
    	Set<String> readOnlyPropertyNames = getReadOnlyPropertyNames(document);
		for (String readOnlyPropertyName : readOnlyPropertyNames) {
			auths.addReadonlyAuthField(readOnlyPropertyName);
		}
		
		Set<String> hiddenPropertyNames = getHiddenPropertyNames(document);
		for (String hiddenPropertyName : hiddenPropertyNames) {
			auths.addHiddenAuthField(hiddenPropertyName);
		}
		
		Set<String> hiddenSectionIds = getHiddenSectionIds(document);
		for (String hiddenSectionId : hiddenSectionIds) {
		}
	}
	
	protected Set<String> getReadonlyPropertyNames(List<? extends MaintainableItemDefinition> items, String fieldPrefix) {
		Set<String> readonlyFields = new HashSet<String>();
		for (MaintainableItemDefinition item : items) {
			if (item instanceof MaintainableFieldDefinition) {
				if (((MaintainableFieldDefinition) item).isUnconditionallyReadOnly()) {
					readonlyFields.add(fieldPrefix + item.getName());
				}
				else if (item instanceof MaintainableCollectionDefinition) {
					MaintainableCollectionDefinition maintainableCollectionDefinition = (MaintainableCollectionDefinition) item; 
					Set<String> subcollectionReadonlyFields = getReadonlyPropertyNames(maintainableCollectionDefinition.getMaintainableCollections(), fieldPrefix + maintainableCollectionDefinition.getName() + ".");
					readonlyFields.addAll(subcollectionReadonlyFields);
					
					Set<String> fieldReadonlyFields = getReadonlyPropertyNames(maintainableCollectionDefinition.getMaintainableFields(), fieldPrefix);
					readonlyFields.addAll(fieldReadonlyFields);
				}
			}
		}
		return readonlyFields;
	}
	
	protected Set<String> getHiddenPropertyNames(MaintenanceDocument document) {
		return new HashSet<String>();
	}
	
    protected Set<String> getReadOnlyPropertyNames(MaintenanceDocument document) {
		String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
    	MaintenanceDocumentEntry objectEntry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(documentType);
    	List<MaintainableSectionDefinition> maintainableSectionDefinitions = objectEntry.getMaintainableSections();

    	
    	Set<String> documentReadOnlyFields = new HashSet<String>();
    	for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
    		documentReadOnlyFields.addAll(getReadonlyPropertyNames(maintainableSectionDefinition.getMaintainableItems(), ""));
    	}
    	return documentReadOnlyFields;
    }
    
    protected Set<String> getHiddenSectionIds(MaintenanceDocument document) {
    	return new HashSet<String>();
    }
}
