package org.kuali.rice.core.impl.parameter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.parameter.ApcRuleDefinition;
import org.kuali.rice.core.api.parameter.DictionaryValidationApcService;
import org.kuali.rice.core.api.parameter.MaintenanceDocumentDictionaryApcService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

import java.util.Collection;
import java.util.Iterator;

/** extracted from DictionaryValidationServiceImpl. */
public class DictionaryValidationApcServiceImpl implements DictionaryValidationApcService {

    private ParameterEvaluatorService parameterEvaluatorService;
    private MaintenanceDocumentDictionaryApcService maintenanceDocumentDictionaryService;

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    public void setMaintenanceDocumentDictionaryApcService(MaintenanceDocumentDictionaryApcService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    @Override
	public boolean validateApcRule(BusinessObject bo, ApcRuleDefinition apcRule) {
        boolean success = true;
        Object attrValue;
        try {
            attrValue = PropertyUtils.getSimpleProperty(bo, apcRule.getAttributeName());
            // if the value we get back is null that means that the user didn't fill anything in
            // so the rule shouldn't fail because of this
            if (StringUtils.isEmpty((String) attrValue)) {
                return success;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        String attrValueStr = attrValue.toString();
        if (!parameterEvaluatorService.getParameterEvaluator(apcRule.getParameterNamespace(), apcRule.getParameterDetailType(), apcRule.getParameterName(), attrValueStr).evaluationSucceeds()) {
        //if (!configService.evaluateConstrainedValue(apcRule.getParameterNamespace(), apcRule.getParameterDetailType(), apcRule.getParameterName(),attrValueStr)) {
            success &= false;
            GlobalVariables.getMessageMap().putError(apcRule.getAttributeName(), apcRule.getErrorMessage());
        }

        return success;
    }

    @Override
	public boolean validateApcRules(BusinessObject bo) {
        boolean success = true;

        // get a collection of all the apcRuleDefinitions setup for this object
        Collection rules = maintenanceDocumentDictionaryService.getApplyApcRules(bo.getClass());

        // walk through the rules, doing the tests on each
        for (Iterator iter = rules.iterator(); iter.hasNext();) {
            ApcRuleDefinition rule = (ApcRuleDefinition) iter.next();

            // do the existence and validation testing
            success &= validateApcRule(bo, rule);
        }
        return success;
    }
}
