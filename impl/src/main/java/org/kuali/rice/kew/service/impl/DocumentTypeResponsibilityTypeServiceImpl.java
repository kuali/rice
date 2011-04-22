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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;
import org.kuali.rice.kim.service.support.impl.KimResponsibilityTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypeResponsibilityTypeServiceImpl extends
		KimResponsibilityTypeServiceBase implements KimResponsibilityTypeService {
	DocumentTypeService documentTypeService;
	protected String exactMatchStringAttributeName;

	{
		requiredAttributes.add( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME );
		checkRequiredAttributes = true;
	}
	
	@Override
	protected List<KimResponsibilityInfo> performResponsibilityMatches(
			AttributeSet requestedDetails,
			List<KimResponsibilityInfo> responsibilitiesList) {
		Map<String, List<KimResponsibilityInfo>> potentialDocumentTypeMatches = new HashMap<String, List<KimResponsibilityInfo>>();
		for (KimResponsibilityInfo responsibility : responsibilitiesList) {
			if ((exactMatchStringAttributeName == null)
					|| responsibility
							.getDetails()
							.get(exactMatchStringAttributeName)
							.equals(
									requestedDetails
											.get(exactMatchStringAttributeName))) {
				if (!potentialDocumentTypeMatches.containsKey(responsibility
						.getDetails().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME))) {
					potentialDocumentTypeMatches.put(
							responsibility.getDetails().get(
									KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME),
							new ArrayList<KimResponsibilityInfo>());
				}
				potentialDocumentTypeMatches.get(
						responsibility.getDetails().get(
								KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)).add(
						responsibility);
			}
		}
		List<KimResponsibilityInfo> matchingResponsibilities = new ArrayList<KimResponsibilityInfo>();
		if (potentialDocumentTypeMatches.containsKey(requestedDetails
				.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME))) {
			matchingResponsibilities
					.addAll(potentialDocumentTypeMatches.get(requestedDetails
							.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)));
		} else {
			String closestParentDocumentTypeName = getClosestParentDocumentTypeName(
					getDocumentTypeService().findByName(
							requestedDetails
									.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)),
					potentialDocumentTypeMatches.keySet());
			if (closestParentDocumentTypeName != null) {
				matchingResponsibilities.addAll(potentialDocumentTypeMatches
						.get(closestParentDocumentTypeName));
			}
		}
		return matchingResponsibilities;
	}

	public DocumentTypeService getDocumentTypeService() {
		if (documentTypeService == null) {
			documentTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return this.documentTypeService;
	}
}
