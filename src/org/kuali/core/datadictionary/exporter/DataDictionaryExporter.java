/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.Iterator;
import java.util.Map;

import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.MaintenanceDocumentEntry;
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.KNSServiceLocator;

/**
 * Creates a Map containing the contents of the DataDictionary. Each entry is converted into a String-containing, read-only Map
 * whose contents and hierarchy are described in the dataDictionary DTD.
 * <p>
 * The net result should be: accessing any part of an entry's value will require traversing a hierarchy of names and/or indices
 * corresponding to the names, keys, and/or positions of the XML tags describing the related structures.
 */
public class DataDictionaryExporter {
    final ExportMap dictionaryMap;

    /**
     * Default constructor.
     */
    public DataDictionaryExporter() {
        dictionaryMap = new ExportMap("DataDictionary");

        DataDictionary dictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();

        mapBusinessObjectEntries(dictionaryMap, dictionary.getBusinessObjectEntries());
        mapMaintenanceDocumentEntries(dictionaryMap, dictionary.getMaintenanceDocumentEntries());
        mapTransactionalDocumentEntries(dictionaryMap, dictionary.getTransactionalDocumentEntries());
    }

    /**
     * @return unmodifiable copy of the Map containing the Maps generated for each dataDictionary entry
     */
    public Map getDictionaryMap() {
        return this.dictionaryMap.getExportData();
    }

    private void mapBusinessObjectEntries(ExportMap exportMap, Map businessObjectEntries) {
        BusinessObjectEntryMapper mapper = new BusinessObjectEntryMapper();
        for (Iterator i = businessObjectEntries.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            BusinessObjectEntry entry = (BusinessObjectEntry) e.getValue();
            ExportMap entryMap = mapper.mapEntry(entry);

            exportMap.set(entryMap);
        }
    }

    private void mapMaintenanceDocumentEntries(ExportMap exportMap, Map maintenanceDocumentEntries) {
        MaintenanceDocumentEntryMapper mapper = new MaintenanceDocumentEntryMapper();
        for (Iterator i = maintenanceDocumentEntries.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry) e.getValue();
            ExportMap entryMap = mapper.mapEntry(entry);

            exportMap.set(entryMap);
        }
    }

    private void mapTransactionalDocumentEntries(ExportMap exportMap, Map transactionalDocumentEntries) {
        TransactionalDocumentEntryMapper mapper = new TransactionalDocumentEntryMapper();
        for (Iterator i = transactionalDocumentEntries.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            TransactionalDocumentEntry entry = (TransactionalDocumentEntry) e.getValue();
            ExportMap entryMap = mapper.mapEntry(entry);

            exportMap.set(entryMap);
        }
    }
}