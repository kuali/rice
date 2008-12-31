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

    public final boolean canCreate(Class boClass){
    	return getMaintenanceDocumentDictionaryService().getAllowsNewOrCopy(getMaintenanceDocumentDictionaryService().getDocumentTypeName(boClass));
    }

	protected Set<String> getConditionallyReadOnlyPropertyNames(Document document) {
		return new HashSet<String>();
	}
	
	protected Set<String> getConditionallyReadOnlySectionIds(Document document) {
		return new HashSet<String>();
	}

	protected Set<String> getConditionallyHiddenPropertyNames(Document document) {
		return new HashSet<String>();
	}
	
	protected Set<String> getConditionallyHiddenSectionIds(Document document) {
		return new HashSet<String>();
	}

	public final void addMaintenanceDocumentRestrictions(
			MaintenanceDocumentAuthorizations auths,
			MaintenanceDocument document) {
    	Set<String> readOnlyPropertyNames = getConditionallyReadOnlyPropertyNames(document);
		for (String readOnlyPropertyName : readOnlyPropertyNames) {
			auths.addReadonlyAuthField(readOnlyPropertyName);
		}
		Set<String> hiddenPropertyNames = getConditionallyHiddenPropertyNames(document);
		for (String hiddenPropertyName : hiddenPropertyNames) {
			auths.addHiddenAuthField(hiddenPropertyName);
		}
		Set<String> readOnlySectionIds = getConditionallyReadOnlySectionIds(document);
		for (String readOnlySectionId : readOnlySectionIds) {
			auths.addReadOnlySectionId(readOnlySectionId);
		}
		Set<String> hiddenSectionIds = getConditionallyHiddenSectionIds(document);
		for (String hiddenSectionId : hiddenSectionIds) {
			auths.addHiddenSectionId(hiddenSectionId);
		}
	}
	
	public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}
}
