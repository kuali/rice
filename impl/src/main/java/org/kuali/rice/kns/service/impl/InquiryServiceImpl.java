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

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryAuthorizerBase;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.kns.inquiry.InquiryPresentationControllerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.InquiryService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class InquiryServiceImpl implements InquiryService {
	protected DataDictionaryService dataDictionaryService;

	public <T extends BusinessObject> InquiryAuthorizer getAuthorizer(
			Class<T> businessObjectClass) {
		Class inquiryAuthorizerClass = getDataDictionary()
				.getBusinessObjectEntry(businessObjectClass.getName())
				.getInquiryDefinition().getAuthorizerClass();
		if (inquiryAuthorizerClass == null) {
			inquiryAuthorizerClass = InquiryAuthorizerBase.class;
		}
		try {
			return (InquiryAuthorizer) inquiryAuthorizerClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to instantiate InquiryAuthorizer class: "
							+ inquiryAuthorizerClass, e);
		}
	}

	public <T extends BusinessObject> InquiryPresentationController getPresentationController(
			Class<T> businessObjectClass) {
		Class inquiryPresentationControllerClass = getDataDictionary()
				.getBusinessObjectEntry(businessObjectClass.getName())
				.getInquiryDefinition().getPresentationControllerClass();
		if (inquiryPresentationControllerClass == null) {
			inquiryPresentationControllerClass = InquiryPresentationControllerBase.class;
		}
		try {
			return (InquiryPresentationController) inquiryPresentationControllerClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to instantiate InquiryAuthorizer class: "
							+ inquiryPresentationControllerClass, e);
		}
	}

	protected DataDictionary getDataDictionary() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KNSServiceLocator
					.getDataDictionaryService();
		}
		return dataDictionaryService.getDataDictionary();
	}
}
