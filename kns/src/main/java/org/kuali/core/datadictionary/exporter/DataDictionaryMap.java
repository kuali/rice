/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.datadictionary.exporter;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DataDictionaryEntry;
import org.kuali.core.datadictionary.MaintenanceDocumentEntry;
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.core.service.DataDictionaryService;

public class DataDictionaryMap extends DataDictionaryMapBase {

    private DataDictionaryService dataDictionaryService;

    BusinessObjectEntryMapper boMapper = new BusinessObjectEntryMapper();
    MaintenanceDocumentEntryMapper maintDocMapper = new MaintenanceDocumentEntryMapper();
    TransactionalDocumentEntryMapper transDocMapper = new TransactionalDocumentEntryMapper();
    
    Map<String,Map> ddMap = new HashMap<String,Map>();
    
    public DataDictionaryMap(DataDictionaryService dataDictionaryService) {
        super();
        this.dataDictionaryService = dataDictionaryService;
    }

    public Object get(Object key) {
        Map subMap = ddMap.get( key );
        if ( subMap == null ) { // need to load from DD
            synchronized( this ) { // ensure only one update access happening at a time
                subMap = ddMap.get( key );
                if ( subMap == null ) { // recheck in case it was loaded by another thread while this one was blocked
                    DataDictionaryEntry entry = dataDictionaryService.getDataDictionary().getDictionaryObjectEntry( key.toString() );
                    if ( entry != null ) {
                        if ( entry instanceof BusinessObjectEntry ) {
                            subMap = boMapper.mapEntry( (BusinessObjectEntry)entry ).getExportData();                    
                        } else if ( entry instanceof MaintenanceDocumentEntry ) {
                            subMap = maintDocMapper.mapEntry( (MaintenanceDocumentEntry)entry ).getExportData();                    
                        } else if ( entry instanceof TransactionalDocumentEntry ) {
                            subMap = transDocMapper.mapEntry( (TransactionalDocumentEntry)entry ).getExportData();                    
                        }
                    }
                    if ( subMap != null ) {
                        ddMap.put( key.toString(), subMap );
                    }
                }
            }
        }
        return subMap;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
