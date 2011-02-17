package org.kuali.rice.core.api.parameter;

/** extracted from MaintenanceDocumentRuleBase. */
public class MaintenanceApcRules {

    private ParameterEvaluatorService parameterEvaluatorService;

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    /**
     *
     * This method is a shallow inverse wrapper around applyApcRule, it simply reverses the return value, for better readability in
     * an if test.
     *
     * This method applies an APC rule based on the values provided.
     *
     * It will throw an ApplicationParameterException if the APC Group and Parm do not exist in the system.
     *
     * @param apcGroupName - The script or group name in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param parameterName - The name of the parm/rule in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param valueToTest - The String value to test against the APC rule. The value may be null or blank without throwing an error,
     *        but the rule will likely fail if null or blank.
     * @return True if the rule fails, False if the rule passes.
     *
     */
    protected boolean apcRuleFails(String parameterNamespace, String parameterDetailTypeCode, String parameterName, String valueToTest) {
        if (applyApcRule(parameterNamespace, parameterDetailTypeCode, parameterName, valueToTest) == false) {
            return true;
        }
        return false;
    }

    /**
     *
     * This method applies an APC rule based on the values provided.
     *
     * It will throw an ApplicationParameterException if the APC Group and Parm do not exist in the system.
     *
     * @param apcGroupName - The script or group name in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param parameterName - The name of the parm/rule in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param valueToTest - The String value to test against the APC rule. The value may be null or blank without throwing an error,
     *        but the rule will likely fail if null or blank.
     * @return True if the rule passes, False if the rule fails.
     *
     */
    protected boolean applyApcRule(String parameterNamespace, String parameterDetailTypeCode, String parameterName, String valueToTest) {

        // default to success
        boolean success = true;

        // apply the rule, see if it fails
        if (!parameterEvaluatorService.getParameterEvaluator(parameterNamespace, parameterDetailTypeCode, parameterName, valueToTest).evaluationSucceeds()) {
            success = false;
        }

        return success;
    }
}
