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
package org.kuali.rice.kns.util.documentserializer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;


public class MaintenanceDocumentPropertySerializibilityEvaluator 
				extends PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {

    /**
     * Reads the data dictionary to determine which properties of the document should be serialized.
     * 
     * @see org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluator#initializeEvaluator(org.kuali.rice.kns.document.Document)
     */
	@Override
    public void initializeEvaluatorForDataObject(Object businessObject){
        DataDictionary dictionary = KNSServiceLocatorWeb.getDataDictionaryService().getDataDictionary();
        MaintenanceDocumentEntry maintenanceDocumentEntry = 
        	dictionary.getMaintenanceDocumentEntryForBusinessObjectClass(businessObject.getClass());
        serializableProperties = new PropertySerializerTrie();
        populateSerializableProperties(maintenanceDocumentEntry.getMaintainableSections());
        serializableProperties.addSerializablePropertyName("boNotes", true);
        serializableProperties.addSerializablePropertyName("boNotes.attachment", true);
    }
    
    private void populateSerializableProperties(List<MaintainableSectionDefinition> maintainableSectionDefinitions){
        for(MaintainableSectionDefinition maintainableSectionDefinition: maintainableSectionDefinitions){
        	populateSerializablePropertiesWithItems("", maintainableSectionDefinition.getMaintainableItems());
        }
    }

    private void populateSerializablePropertiesWithItems(String basePath, List<MaintainableItemDefinition> maintainableItems){
    	for(MaintainableItemDefinition maintainableItemDefinition: maintainableItems){
            if(maintainableItemDefinition instanceof MaintainableFieldDefinition){
                serializableProperties.addSerializablePropertyName(getFullItemName(basePath, maintainableItemDefinition.getName()), true);
            } else if(maintainableItemDefinition instanceof MaintainableCollectionDefinition){
            	serializableProperties.addSerializablePropertyName(getFullItemName(basePath, maintainableItemDefinition.getName()), true);
            	populateSerializablePropertiesWithItems(getFullItemName(basePath, maintainableItemDefinition.getName()), 
            			getAllMaintainableFieldDefinitionsForSerialization(
            			(MaintainableCollectionDefinition)maintainableItemDefinition));            	
            } else if(maintainableItemDefinition instanceof MaintainableSubSectionHeaderDefinition){
            	//Ignore
            }
    	}
    }

    private String getFullItemName(String basePath, String itemName){
    	return StringUtils.isEmpty(basePath) ? itemName : basePath+"."+itemName;
    }
    
    public List<MaintainableItemDefinition> getAllMaintainableFieldDefinitionsForSerialization(
    		MaintainableCollectionDefinition maintainableCollectionDefinition){
		List<MaintainableItemDefinition> allMaintainableItemDefinitions = new ArrayList<MaintainableItemDefinition>();

		if(maintainableCollectionDefinition.getMaintainableFields()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getMaintainableFields());
		}

		if(maintainableCollectionDefinition.getSummaryFields()!=null){
			allMaintainableItemDefinitions.addAll(
					(List<MaintainableFieldDefinition>)maintainableCollectionDefinition.getSummaryFields());
		}

		if(maintainableCollectionDefinition.getDuplicateIdentificationFields()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getDuplicateIdentificationFields());
		}

		/*if(maintainableCollectionMap!=null){
			updateMaintainableCollectionDefinitionForSerialization(maintainableCollectionMap.values());
			allMaintainableItemDefinitions.addAll(maintainableCollectionMap.values());
		}*/
		if(maintainableCollectionDefinition.getMaintainableCollections()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getMaintainableCollections());
		}

		return allMaintainableItemDefinitions;
	}
    
}
