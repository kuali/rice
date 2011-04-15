package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface RuleAttributeContract extends BaseAttributeContract {

	/**
	 * This is the id of the Rule to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a Rule related to the attribute.
	 * </p>
	 * @return id for Rule related to the attribute.
	 */
	public String getRuleId();

}
