package org.kuali.rice.kns.rules;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.DictionaryValidationService;

/**
 * KNS version of DocumentRuleBase
 */
public class DocumentRuleBase extends org.kuali.rice.krad.rules.DocumentRuleBase {

    protected DictionaryValidationService getDictionaryValidationService() {
        return LazyServicesHolder.dictionaryValidationService;
    }

    // Lazy init holder class, see Effective Java #71
    private static class LazyServicesHolder {
        static final DictionaryValidationService dictionaryValidationService =
                KNSServiceLocator.getKNSDictionaryValidationService();
    }
}
