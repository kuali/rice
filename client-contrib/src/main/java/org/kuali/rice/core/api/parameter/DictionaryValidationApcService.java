package org.kuali.rice.core.api.parameter;

import org.kuali.rice.kns.bo.BusinessObject;

/** extracted from DictionaryValidationService */
public interface DictionaryValidationApcService {

    /**
     *
     * This method applies a specific rule against the given BusinessObject as defined in the MaintenanceDocument.xml file.
     *
     * Appropriate errors will also be placed in the GlobalVariables.ErrorMap.
     *
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     *
     * @param bo
     * @param apcRule
     * @return true if rule passes
     */
    public boolean validateApcRule(BusinessObject bo, ApcRuleDefinition apcRule);

    /**
     * This method applies all rules against the given BusinessObject as defined in the MaintenanceDocument.xml file.
     *
     * Appropriate errors will also be placed in the GlobalVariables.ErrorMap.
     *
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     *
     * @param bo
     * @return true if rule passes
     */
    public boolean validateApcRules(BusinessObject bo);
}
