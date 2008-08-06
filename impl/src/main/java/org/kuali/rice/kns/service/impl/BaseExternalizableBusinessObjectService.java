/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.ExternalizableBusinessObjectService;

/**
 * 
 * This class is the base implementation of the interface ExternalizableBusinessObjectService
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public abstract class BaseExternalizableBusinessObjectService implements ExternalizableBusinessObjectService {


    private Class externalizableBusinessObjectClass;
    
    /***
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getExternalizableBusinessObjectClass()
     */
    public Class getExternalizableBusinessObjectClass(){
        return externalizableBusinessObjectClass;
    }

    /**
     * Sets the externalizableBusinessObjectClass attribute value.
     * @param externalizableBusinessObjectClass The externalizableBusinessObjectClass to set.
     */
    public void setExternalizableBusinessObjectClass(Class externalizableBusinessObjectClass) {
        this.externalizableBusinessObjectClass = externalizableBusinessObjectClass;
    }
    
    /***
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getBusinessObject(java.util.Map)
     */
    public BusinessObject getBusinessObject(Map<String, Object> fieldValues){
        return KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(getExternalizableBusinessObjectClass(), fieldValues);
    }

    /***
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject)
     */
    public String getInquiryUrl(BusinessObject businessObject){
        return null;
    }

    /***
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getLookupUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String, java.lang.String)
     */
    public String getLookupUrl(BusinessObject parentObject, String returnLocation, String formKey){
        return null;
    }

    /***
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getBusinessObjectEntry()
     */
    public BusinessObjectEntry getBusinessObjectEntry(){
        return KNSServiceLocator.getDataDictionaryService().getDataDictionary()
        							.getBusinessObjectEntry(getExternalizableBusinessObjectClass().getName());
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.ExternalizableBusinessObjectService#getListPrimaryKeyFieldNames(java.lang.Class)
	 */
	public List getListPrimaryKeyFieldNames(Class clazz) {
		return KNSServiceLocator.getPersistenceStructureService().getPrimaryKeys(getExternalizableBusinessObjectClass());
	}

}