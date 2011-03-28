/*
 * Copyright 2011 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DataObjectMetaDataService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.springframework.beans.BeanWrapper;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectMetaDataServiceImpl implements DataObjectMetaDataService {

    private DataDictionaryService dataDictionaryService;
    private KualiModuleService kualiModuleService;
    private PersistenceStructureService persistenceStructureService;
    
    
    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#listPrimaryKeyFieldNames(java.lang.Class)
     */
    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz) {
        if (persistenceStructureService.isPersistable(clazz)) {
            return persistenceStructureService.listPrimaryKeyFieldNames(clazz);
        }
        
        ModuleService responsibleModuleService = getKualiModuleService()
                .getResponsibleModuleService(clazz);
        if (responsibleModuleService != null
                && responsibleModuleService.isExternalizable(clazz))
            return responsibleModuleService.listPrimaryKeyFieldNames(clazz);

        // check the Data Dictionary for PK's of non PBO/EBO
        List<String> pks = dataDictionaryService.getDataDictionary()
            .getDataObjectEntry(clazz.getName()).getPrimaryKeys();
        if(pks != null && !pks.isEmpty())
            return pks;

        return new ArrayList<String>();
    }

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return getPrimaryKeyFieldValues(dataObject, false);
    }

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object, boolean)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames) {
        Map<String, Object> keyValueMap;
        
        if (sortFieldNames) {
            keyValueMap = new TreeMap<String, Object>();
        } else {
            keyValueMap = new HashMap<String, Object>();
        }

        BeanWrapper wrapper = ObjectPropertyUtils.wrapObject(dataObject);
        
        List<String> fields = listPrimaryKeyFieldNames(dataObject.getClass());
        for (String fieldName : fields) {
            keyValueMap.put(fieldName, wrapper.getPropertyValue(fieldName));
        }

        return keyValueMap;
    }
    
    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#equalsByPrimaryKeys(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        boolean equal = true;

        if (do1 == null && do2 == null) {
            equal = true;
        }
        else if (do1 == null || do2 == null) {
            equal = false;
        }
        else if (!do1.getClass().getName().equals(do2.getClass().getName())) {
            equal = false;
        }
        else {
            Map<String, ?> do1Keys = getPrimaryKeyFieldValues(do1);
            Map<String, ?> do2Keys = getPrimaryKeyFieldValues(do2);
            for (Iterator<String> iter = do1Keys.keySet().iterator(); iter.hasNext();) {
                String keyName = iter.next();
                if (do1Keys.get(keyName) != null && do2Keys.get(keyName) != null) {
                    if (!do1Keys.get(keyName).toString().equals(do2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                } else {
                    equal = false;
                }
            }
        }  


        return equal;
    }

    /**
     * Protected method to allow subclasses to access the dataDictionaryService.
     *
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Protected method to allow subclasses to access the kualiModuleService.
     *
     * @return Returns the persistenceStructureService.
     */
    protected KualiModuleService getKualiModuleService() {
        return this.kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Protected method to allow subclasses to access the persistenceStructureService.
     *
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return this.persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

}
