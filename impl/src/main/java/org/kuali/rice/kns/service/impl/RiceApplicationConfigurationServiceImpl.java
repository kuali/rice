/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationService;
import org.kuali.rice.kns.service.ParameterConstants.COMPONENT;
import org.kuali.rice.kns.util.KNSUtils;

//@Transactional
public class RiceApplicationConfigurationServiceImpl implements RiceApplicationConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationServiceImpl.class);
    
    private List<ParameterDetailType> components = new ArrayList<ParameterDetailType>();
    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;    
    
    public String getConfigurationParameter( String parameterName ){
    	return getKualiConfigurationService().getPropertyString(parameterName);
    }
    
    /**
     * This method derived ParameterDetailedTypes from the DataDictionary for all BusinessObjects and Documents and from Spring for
     * all batch Steps.
     * 
     * @return List<ParameterDetailedType> containing the detailed types derived from the data dictionary and Spring
     */
    public List<ParameterDetailType> getNonDatabaseComponents() {
        if (components.isEmpty()) {
            Map<String, ParameterDetailType> uniqueParameterDetailTypeMap = new HashMap<String, ParameterDetailType>();
                        
            DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
            
            //dataDictionaryService.getDataDictionary().forceCompleteDataDictionaryLoad();
            for (BusinessObjectEntry businessObjectEntry : dataDictionaryService.getDataDictionary().getBusinessObjectEntries().values()) {
                try {
                    ParameterDetailType parameterDetailType = getParameterDetailType(businessObjectEntry.getBusinessObjectClass());
                    uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                }
                catch (Exception e) {
                    LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for business object class: " + businessObjectEntry.getBusinessObjectClass(), e);
                }
            }
            for (DocumentEntry documentEntry : dataDictionaryService.getDataDictionary().getDocumentEntries().values()) {
                if (documentEntry instanceof TransactionalDocumentEntry) {
                    try {
                        ParameterDetailType parameterDetailType = getParameterDetailType(documentEntry.getDocumentClass());
                        uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                    }
                    catch (Exception e) {
                        LOG.error("The getNonDatabaseDetailTypes method of ParameterServiceImpl encountered an exception while trying to create the detail type for transactional document class: " + documentEntry.getDocumentClass(), e);
                    }
                }
            }
            components.addAll(uniqueParameterDetailTypeMap.values());
        }
        return Collections.unmodifiableList(components);
    }
    
    protected ParameterDetailType getParameterDetailType(Class documentOrStepClass) {
        String detailTypeString = getParameterService().getDetailType(documentOrStepClass);
        String detailTypeName = getDetailTypeName(documentOrStepClass);
        ParameterDetailType detailType = new ParameterDetailType(getParameterService().getNamespace(documentOrStepClass), detailTypeString, (detailTypeName == null) ? detailTypeString : detailTypeName);
        detailType.refreshNonUpdateableReferences();
        return detailType;
    }

    protected String getDetailTypeName(Class documentOrStepClass) {
        if (documentOrStepClass == null) {
            throw new IllegalArgumentException("The getDetailTypeName method of ParameterServiceImpl requires non-null documentOrStepClass");
        }
                
        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        
        if (documentOrStepClass.isAnnotationPresent(COMPONENT.class)) {
            BusinessObjectEntry boe = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return ((COMPONENT) documentOrStepClass.getAnnotation(COMPONENT.class)).component();
            }
        }
        if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return dataDictionaryService.getDocumentLabelByClass(documentOrStepClass);
        }
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass) ) {
            BusinessObjectEntry boe = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return KNSUtils.getBusinessTitleForClass(documentOrStepClass);
            }
        }
        throw new IllegalArgumentException("The getDetailTypeName method of ParameterServiceImpl requires TransactionalDocument, BusinessObject, or Step class. Was: " + documentOrStepClass.getName() );
    }
    
    protected KualiConfigurationService getKualiConfigurationService() {
		if (kualiConfigurationService == null) {
			kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
		}
		return kualiConfigurationService;
	}
    
    protected ParameterService getParameterService() {
    	if (parameterService == null) {
    		parameterService = KNSServiceLocator.getParameterService();
    	}
    	return parameterService;
    }
    
    public void setParameterService(ParameterService parameterService) {
    	this.parameterService = parameterService;
    }
}