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
package org.kuali.core.service;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.BusinessObjectEntry;

/**
 * 
 * This is an interface for externalizable business object services. 
 * Implementations of this interface will perform retrieval of the externalizable BO data. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface ExternalizableBusinessObjectService {

    /**
     * This method gets business object for the given primary keys.
     * @param fieldValues
     * @return BusinessObject
     */
    public BusinessObject getBusinessObject(Map<String, Object> fieldValues);

    /**
     * This method gets the inquiry url for the given externalizable business object
     * @param businessObject
     * @return String
     */
    public String getInquiryUrl(BusinessObject businessObject);

    /**
     * This method gets the lookup url for the given externalizable business object
     * @param businessObject
     * @return String
     */
    public String getLookupUrl(BusinessObject parentObject, String returnLocation, String formKey);

    /**
     * This method gets the business object entry for the given externalizable business object
     * @param businessObject
     * @return String
     */
    public BusinessObjectEntry getBusinessObjectEntry();

    /**
     * This method returns the actual type returned by getBusinessObject(Map)
     * @param businessObject
     * @return String
     */
    public Class getExternalizableBusinessObjectClass();

    /**
     * This method returns the actual type returned by getBusinessObject(Map)
     * @param Class
     * @return Map
     */
    public List getListPrimaryKeyFieldNames(Class clazz);
}