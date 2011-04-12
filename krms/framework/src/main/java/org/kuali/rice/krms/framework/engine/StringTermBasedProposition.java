package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.engine.Term;

// TODO: remove?  ComparableTermBasedProposition does this generically
public class StringTermBasedProposition extends ComparableTermBasedProposition<String> {

	public StringTermBasedProposition(ComparisonOperator operator, Term term, String expectedValue) {
		super(operator, term, expectedValue);
	}	

}
