package org.kuali.rice.kew.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.validation.RuleValidationContext;
import org.kuali.rice.kew.api.validation.ValidationResults;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.validation.RuleValidationAttributeExporterService;
import org.kuali.rice.kew.rule.RuleValidationAttribute;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RuleValidationAttributeResolver reference impl.  Returns a proxy which delegates to the appropriate
 * RuleValidationExporterService.
 *
 * @see org.kuali.rice.kew.rule.RuleValidationAttribute
 * @see org.kuali.rice.kew.framework.validation.RuleValidationAttributeExporterService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleValidationAttributeResolverImpl implements RuleValidationAttributeResolver {
    @Override
    public RuleValidationAttribute resolveRuleValidationAttribute(final String attributeName, String applicationId) throws Exception {
        final RuleValidationAttributeExporterService service = findRuleValidationAttributeExporterService(applicationId);
        return (RuleValidationAttribute) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { RuleValidationAttribute.class }, new RuleValidationAttributeInvocationHandler() {
            @Override
            protected ValidationResults invokeValidate(RuleValidationContext context) throws Exception {
                return service.validate(attributeName, context);
            }
        });
    }

    protected RuleValidationAttributeExporterService findRuleValidationAttributeExporterService(String applicationId) {
        RuleValidationAttributeExporterService service = KewFrameworkServiceLocator.getRuleValidationAttributeExporterService(applicationId);
        if (service == null) {
            throw new WorkflowRuntimeException("Failed to locate RuleValidationAttributeExporterService for applicationId: " + applicationId);
        }
        return service;
    }

    protected static abstract class RuleValidationAttributeInvocationHandler implements InvocationHandler {
        @Override
        public ValidationResults invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (!StringUtils.equals(method.getName(), "validate")) {
                throw new UnsupportedOperationException("RuleValidationAttribute only supports 'validate'");
            }
            return invokeValidate((RuleValidationContext) objects[0]);
        }
        protected abstract ValidationResults invokeValidate(RuleValidationContext context) throws Exception;
    }
}