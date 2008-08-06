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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.InactivationBlockingDetectionService;
import org.kuali.rice.kns.service.InactivationBlockingDisplayService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class InactivationBlockingDisplayServiceImpl implements InactivationBlockingDisplayService {
	private PersistenceService persistenceService;
	private DataDictionaryService dataDictionaryService;
	private PersistenceStructureService persistenceStructureService;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.InactivationBlockingDisplayService#listAllBlockerRecords(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata)
	 */
	public List<String> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        String inactivationBlockingDetectionServiceBeanName = inactivationBlockingMetadata.getInactivationBlockingDetectionServiceBeanName();
        if (StringUtils.isBlank(inactivationBlockingDetectionServiceBeanName)) {
            inactivationBlockingDetectionServiceBeanName = KNSServiceLocator.DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE;
        }
        InactivationBlockingDetectionService inactivationBlockingDetectionService = KNSServiceLocator.getInactivationBlockingDetectionService(inactivationBlockingDetectionServiceBeanName);
        
        Collection<BusinessObject> collection = inactivationBlockingDetectionService.listAllBlockerRecords(blockedBo, inactivationBlockingMetadata);
        
        Map<String, Formatter> formatters = getFormattersForPrimaryKeyFields(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());
        Map<String, Boolean> authorizedToViewField = getFieldAuthorizations(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());

        List<String> displayValues = new ArrayList<String>();
        List<String> pkFieldNames = persistenceStructureService.listPrimaryKeyFieldNames(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());

        for (BusinessObject element : collection) {
        	StringBuilder buf = new StringBuilder();
        	
        	for (int i = 0; i < pkFieldNames.size(); i++) {
        		String pkFieldName = pkFieldNames.get(i);
        		Object value = ObjectUtils.getPropertyValue(element, pkFieldName);
    			
        		String displayValue = null;
        		if (authorizedToViewField.get(pkFieldName)) {
        			Formatter formatter = formatters.get(pkFieldName);
        			if (formatter != null) {
        				displayValue = (String) formatter.format(value);
        			}
        			else {
        				displayValue = String.valueOf(value);
        			}
        		}
        		else {
        			Mask m = dataDictionaryService.getAttributeDisplayMask(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), pkFieldName);
        			displayValue = m.getMaskFormatter().maskValue(value);
        		}
        		
        		buf.append(displayValue);
        		if (i < pkFieldNames.size() - 1) {
        			buf.append(" - ");
        		}
        	}

        	displayValues.add(buf.toString());
        }
		return displayValues;
	}
	
	protected Map<String, Formatter> getFormattersForPrimaryKeyFields(Class boClass) {
		List<String> keyNames = persistenceStructureService.listPrimaryKeyFieldNames(boClass);
		Map<String, Formatter> formattersForPrimaryKeyFields = new HashMap<String, Formatter>();
		
		for (String pkFieldName : keyNames) {
			Formatter formatter = null;
			
			Class<? extends Formatter> formatterClass = dataDictionaryService.getAttributeFormatter(boClass, pkFieldName);
			if (formatterClass != null) {
				try {
					formatter = formatterClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }
		return formattersForPrimaryKeyFields;
	}
	
	protected Map<String, Boolean> getFieldAuthorizations(Class boClass) {
		Map<String, Boolean> fieldAuthorizations = new HashMap<String, Boolean>();
		List<String> keyNames = persistenceStructureService.listPrimaryKeyFieldNames(boClass);

		for (String pkFieldName : keyNames) {
			String authorizedWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(boClass, pkFieldName);
			
			if (StringUtils.isBlank(authorizedWorkgroup)) {
				fieldAuthorizations.put(pkFieldName, Boolean.TRUE);
			}
			else {
				fieldAuthorizations.put(pkFieldName, Boolean.valueOf(GlobalVariables.getUserSession().getUniversalUser().isMember(authorizedWorkgroup)));
			}
		}
		
		return fieldAuthorizations;
	}
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void setPersistenceStructureService(
			PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}
}
