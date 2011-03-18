/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.InactivationBlockingDisplayService;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.DisplayInactivationBlockersForm;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DisplayInactivationBlockersAction extends KualiAction {
	
	public ActionForward displayAllInactivationBlockers(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DisplayInactivationBlockersForm displayInactivationBlockersForm = (DisplayInactivationBlockersForm) form;
		DataDictionaryService dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
		InactivationBlockingDisplayService inactivationBlockingDisplayService = KNSServiceLocatorInternal.getInactivationBlockingDisplayService();
		
		Class blockedBoClass = Class.forName(displayInactivationBlockersForm.getBusinessObjectClassName());
		BusinessObject blockedBo = (BusinessObject) blockedBoClass.newInstance();
		for (String key : displayInactivationBlockersForm.getPrimaryKeyFieldValues().keySet()) {
			ObjectUtils.setObjectProperty(blockedBo, key, displayInactivationBlockersForm.getPrimaryKeyFieldValues().get(key));
		}
		
		Map<String, List<String>> allBlockers = new TreeMap<String, List<String>>();
		
		Set<InactivationBlockingMetadata> inactivationBlockers = dataDictionaryService.getAllInactivationBlockingDefinitions(blockedBoClass);
		for (InactivationBlockingMetadata inactivationBlockingMetadata : inactivationBlockers) {
			String blockingBoLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass().getName()).getObjectLabel();
			String relationshipLabel = inactivationBlockingMetadata.getRelationshipLabel();
			String displayLabel;
			if (StringUtils.isEmpty(relationshipLabel)) {
				displayLabel = blockingBoLabel;
			}
			else {
				displayLabel = blockingBoLabel + " (" + relationshipLabel + ")";
			}
			List<String> blockerObjectList = inactivationBlockingDisplayService.listAllBlockerRecords(blockedBo, inactivationBlockingMetadata);
			
			if (!blockerObjectList.isEmpty()) {
				List<String> existingList = allBlockers.get(displayLabel);
				if (existingList != null) {
					existingList.addAll(blockerObjectList);
				}
				else {
					allBlockers.put(displayLabel, blockerObjectList);
				}
			}
		}
		
		displayInactivationBlockersForm.setBlockingValues(allBlockers);
		
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}
}
