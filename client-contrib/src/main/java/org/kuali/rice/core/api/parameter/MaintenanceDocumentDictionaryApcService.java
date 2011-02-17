package org.kuali.rice.core.api.parameter;

import java.util.Collection;

/** extracted from MaintenanceDocumentDictionaryService */
public interface MaintenanceDocumentDictionaryApcService {

        /**
     * The collection of apcRuleDefinition objects defined as applyApcRules for the MaintenanceDocument
     *
     * @param businessObjectClass
     * @return A collection of ApcRuleDefinitions
     */
    public Collection getApplyApcRules(Class businessObjectClass);

    /**
     * The collection of apcRuleDefinition objects defined as applyApcRules for the MaintenanceDocument
     *
     * @param docTypeName
     * @return A collection of ApcRuleDefinitions
     */
    public Collection getApplyApcRules(String docTypeName);
}
