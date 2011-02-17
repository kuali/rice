package org.kuali.rice.core.api.parameter;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.exception.DuplicateEntryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** extracted from MaintenanceDocumentEntry */
public class MaintenanceDocumentApcEntry {
        protected Class<? extends BusinessObject> businessObjectClass;

    protected List<ApcRuleDefinition> apcRules = new ArrayList<ApcRuleDefinition>();
    protected Map<String,ApcRuleDefinition> apcRuleMap = new LinkedHashMap<String, ApcRuleDefinition>();

    public void completeValidation() {

        for ( ApcRuleDefinition apcRule : apcRules ) {
            apcRule.completeValidation(businessObjectClass, null);
        }
    }

    /**
     *
     * @return List of all apcRule ApcRuleDefinitions associated with this MaintenanceDocument, in the order in which they were
     *         added
     *
     */
    public List<ApcRuleDefinition> getApcRules() {
        return apcRules;
    }

    /**
     *
     * @return List of all apcRule rule's fieldNames associated with this MaintenanceDocument, in the order in which they were added
     *
     */
    public List<String> getApcRuleFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.apcRuleMap.keySet());

        return fieldNames;
    }

        /*
                    The apcRule element is used to specifiy legal values
                    for an attribute.  This is done by specifiying the key
                    to the System Parameters table that indicates
                    the allowable values.

                    JSTL: apcRules are Maps with the following keys:
                    * attributeName (String)
                    * parameterNamespace (String)
                    * parameterDetailType (String)
                    * parameterName (String)
                    * errorMessage (String) a property key usually defined in ApplicationResources.properties

                    See DictionaryValidationService.validateApcRule
     */
    public void setApcRules(List<ApcRuleDefinition> apcRules) {
        apcRuleMap.clear();
        for ( ApcRuleDefinition apcRule : apcRules ) {
            if (apcRule == null) {
                throw new IllegalArgumentException("invalid (null) apcRule");
            }

            String keyName = apcRule.getAttributeName();
            if (apcRuleMap.containsKey(keyName)) {
                throw new DuplicateEntryException("duplicate apcRule entry for attribute '" + keyName + "'");
            }

            apcRuleMap.put(keyName, apcRule);
        }
        this.apcRules = apcRules;
    }
}
