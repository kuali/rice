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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.FieldAttributeSecurity;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.datadictionary.InquiryCollectionDefinition;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class InquiryAttributeSecurityUtils {

	public static Map<String, FieldAttributeSecurity> getRestrictedInquiryFields(
			BusinessObjectEntry objectEntry) {
		List<InquirySectionDefinition> inquirySections = objectEntry
				.getInquiryDefinition().getInquirySections();
		Map<String, FieldAttributeSecurity> returnInquiryFieldList = new HashMap<String, FieldAttributeSecurity>();
		for (InquirySectionDefinition inquirySectionDefinition : inquirySections) {
			returnInquiryFieldList.putAll(getRestrictedInquiryFieldList(inquirySectionDefinition
							.getInquiryFields(), objectEntry
							.getBusinessObjectClass(),
					KNSConstants.EMPTY_STRING));
			Map inquiryCollectionDefinition = inquirySectionDefinition
			.getInquiryCollections();
			if(inquiryCollectionDefinition != null && !inquiryCollectionDefinition.isEmpty()){
				returnInquiryFieldList.putAll(getRestrictedInquiryFieldList(inquirySectionDefinition
						.getInquiryCollections().values(), objectEntry
						.getBusinessObjectClass(),
				KNSConstants.EMPTY_STRING));
			}
		}
		return returnInquiryFieldList;
	}

	public static Map<String, FieldAttributeSecurity> getRestrictedInquiryFieldList(
			Collection fieldDefinitions, Class boClass, String key) {
		key = StringUtils.isEmpty(key) ? "" : key + ".";
		Map<String, FieldAttributeSecurity> returnInquiryFieldList = new HashMap<String, FieldAttributeSecurity>();
		for (Object fieldDefinition : fieldDefinitions) {
			if (fieldDefinition instanceof InquiryCollectionDefinition) {
				InquiryCollectionDefinition collection = (InquiryCollectionDefinition) fieldDefinition;
				returnInquiryFieldList.putAll(getRestrictedInquiryFieldList(
						collection.getInquiryCollections(), collection
								.getBusinessObjectClass(), key
								+ collection.getAttributeName()));
				returnInquiryFieldList.putAll(getRestrictedInquiryFieldList(
						collection.getInquiryFields(), collection
								.getBusinessObjectClass(), key));
			} else if (fieldDefinition instanceof FieldDefinition) {
				FieldDefinition fields = (FieldDefinition) fieldDefinition;
				AttributeDefinition attributeDefinition = (KNSServiceLocator
						.getDataDictionaryService().getDataDictionary()
						.getBusinessObjectEntry(boClass.getName())
						.getAttributeDefinition(fields.getAttributeName()));
				if (attributeDefinition != null
						&& attributeDefinition.getAttributeSecurity() != null) {
					FieldAttributeSecurity fieldAttributeSecurity = new FieldAttributeSecurity();
					fieldAttributeSecurity.setAttributeName(fields
							.getAttributeName());
					fieldAttributeSecurity.setBusinessObjectClass(boClass);
					fieldAttributeSecurity
							.setBusinessObjectAttributeSecurity(attributeDefinition
									.getAttributeSecurity());

					returnInquiryFieldList.put(key + fields.getAttributeName(),
							fieldAttributeSecurity);
				}
			}
		}
		return returnInquiryFieldList;
	}
}
