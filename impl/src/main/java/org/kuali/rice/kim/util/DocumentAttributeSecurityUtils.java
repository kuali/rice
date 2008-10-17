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
package org.kuali.rice.kim.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.FieldAttributeSecurity;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class DocumentAttributeSecurityUtils {

	public static Map<String, FieldAttributeSecurity> getRestrictionMaintainableFields(
			MaintenanceDocumentEntry objectEntry) {
		List<MaintainableSectionDefinition> maintainableSectionDefinitions = objectEntry
				.getMaintainableSections();
		Map<String, FieldAttributeSecurity> fieldAttributeSecurities = new HashMap<String, FieldAttributeSecurity>();
		for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
			List<MaintainableItemDefinition> maintainableItems = maintainableSectionDefinition
					.getMaintainableItems();
			getRestrictionMaintainableFieldList(fieldAttributeSecurities,
					maintainableItems, null, objectEntry.getDocumentTypeName(), KNSConstants.EMPTY_STRING);
		}
		return fieldAttributeSecurities;
	}

	private static Map<String, FieldAttributeSecurity> getRestrictionMaintainableFieldList(
			Map<String, FieldAttributeSecurity> returnList, List items, Class boClass,
			String documentTypeName, String key) {
		for (Object item: items) {
			if (item instanceof MaintainableFieldDefinition) {
				MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;
				//retrieve attribDef from the DD for the BO (DDS.getDD().getBOE(boClass).getAttributeDefinition(item.getName());
				AttributeDefinition attributeDefinition = 
					(KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(
							boClass.getName()).getAttributeDefinition(maintainableFieldDefinition.getName()));

				if(maintainableFieldDefinition.getAttributeSecurity()!=null || 
						(attributeDefinition!=null && attributeDefinition.getAttributeSecurity()!=null)){
					FieldAttributeSecurity fieldAttributeSecurity = new FieldAttributeSecurity();
					fieldAttributeSecurity.setMaintainableFieldAttributeSecurity(
							((MaintainableFieldDefinition) item).getAttributeSecurity());
					fieldAttributeSecurity.setBusinessObjectAttributeSecurity(attributeDefinition.getAttributeSecurity());
					fieldAttributeSecurity.setAttributeName(maintainableFieldDefinition.getName());
					fieldAttributeSecurity.setBusinessObjectClass(boClass);
					fieldAttributeSecurity.setDocumentTypeName(documentTypeName);
					returnList.put(
							(StringUtils.isEmpty(key)?"":key+".")+maintainableFieldDefinition.getName(), fieldAttributeSecurity);
				}
			} else if (item instanceof MaintainableCollectionDefinition) {
				MaintainableCollectionDefinition maintainableCollectionDefinition = (MaintainableCollectionDefinition) item;
				getRestrictionMaintainableFieldList(
						returnList,
						maintainableCollectionDefinition.getMaintainableCollections(),
						maintainableCollectionDefinition.getBusinessObjectClass(), 
						documentTypeName, 
						(StringUtils.isEmpty(key)?"":key+".") + maintainableCollectionDefinition.getName());
				getRestrictionMaintainableFieldList(
						returnList,
						maintainableCollectionDefinition.getMaintainableFields(),
						maintainableCollectionDefinition.getBusinessObjectClass(), 
						documentTypeName, 
						(StringUtils.isEmpty(key)?"":key+".") + maintainableCollectionDefinition.getName());
			}
		}
		return returnList;
	}
}
