package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.Asset;

public class StringTermBasedProposition extends ComparableTermBasedProposition<String> {

	public StringTermBasedProposition(ComparisonOperator operator, Asset term, String expectedValue) {
		super(operator, term, expectedValue);
	}	

}
