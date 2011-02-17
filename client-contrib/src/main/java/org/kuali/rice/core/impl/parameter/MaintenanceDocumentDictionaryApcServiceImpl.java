package org.kuali.rice.core.impl.parameter;

import org.kuali.rice.core.api.parameter.MaintenanceDocumentDictionaryApcService;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

import java.util.Collection;

/** extracted from MaintenanceDocumentDictionaryServiceImpl */
public class MaintenanceDocumentDictionaryApcServiceImpl implements MaintenanceDocumentDictionaryApcService {

    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    public Collection getApplyApcRules(Class businessObjectClass) {
        return getApplyApcRules(maintenanceDocumentDictionaryService.getDocumentTypeName(businessObjectClass));
    }

    public Collection getApplyApcRules(String docTypeName) {

        Collection apcRules = null;

/*        MaintenanceDocumentEntry entry = maintenanceDocumentDictionaryService.getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            apcRules = entry.getApcRules();
        }
*/
        return apcRules;
    }
}
