package org.kuali.rice.krms.framework.engine;

public enum ComparisonOperator {

	EQUALS,
	NOT_EQUALS,
	GREATER_THAN,
	GREATER_THAN_EQUAL,
	LESS_THAN,
	LESS_THAN_EQUAL;
	
	public <T> boolean compare(Comparable<T> lhs, T rhs) {
		int result = lhs.compareTo(rhs);
		if (this == EQUALS) {
			return result == 0;
		}
		if (this == NOT_EQUALS) {
			return result != 0;
		}
		if (this == GREATER_THAN) {
			return result > 0;
		}
		if (this == GREATER_THAN_EQUAL) {
			return result >= 0;
		}
		if (this == LESS_THAN) {
			return result < 0;
		}
		if (this == LESS_THAN_EQUAL) {
			return result <= 0;
		}
		throw new IllegalStateException("Invalid operator detected: " + this);
	}
	
	
}
