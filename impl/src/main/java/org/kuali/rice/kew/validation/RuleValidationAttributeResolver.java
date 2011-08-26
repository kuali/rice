package org.kuali.rice.kew.validation;

import org.kuali.rice.kew.rule.RuleValidationAttribute;

/**
 * Service which mediates RuleValidationAttribute lookup and invocation.
 * Determines appropriate (possibly remote) {@link org.kuali.rice.kew.framework.validation.RuleValidationAttributeExporterService} endpoint and
 * returns a wrapper which delegates to it.
 *
 * @see org.kuali.rice.kew.rule.RuleValidationAttribute
 * @see org.kuali.rice.kew.framework.validation.RuleValidationAttributeExporterService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RuleValidationAttributeResolver {
    /**
	 * Resolves the RuleValidationAttribute by name, possibly resulting in delegation over the KSB.
	 *
	 * @return a RuleValidationAttribute suitable (only) for invocation of #validate
	 */
	public RuleValidationAttribute resolveRuleValidationAttribute(String attributeName, String applicationId) throws Exception;
}
