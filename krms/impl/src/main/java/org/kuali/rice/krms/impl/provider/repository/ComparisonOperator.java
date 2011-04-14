package org.kuali.rice.krms.impl.provider.repository;

import org.apache.commons.lang.ObjectUtils;

public enum ComparisonOperator {

	EQUALS,
	NOT_EQUALS,
	GREATER_THAN,
	GREATER_THAN_EQUAL,
	LESS_THAN,
	LESS_THAN_EQUAL;
	
	public boolean compare(Object lhs, Object rhs) {
		// there are various cases where only "equals" is suppored
		if (lhs == null || rhs == null) {
			if (this == EQUALS) {
				return ObjectUtils.equals(lhs, rhs);
			} else if (this == NOT_EQUALS) {
				return ObjectUtils.notEqual(lhs, rhs);
			}
			// any other operation will evaluate to false in the case of null
			return false;
		}
		
		// TODO
		
//		if (lhs instanceof Comparable) {
//			TypeVariable<?>[] typeVariables = lhs.getClass().getTypeParameters();
//			int result = ((Comparable<? extends Object>)lhs).compareTo(rhs);
//		}
//		
//		if (this == EQUALS) {
//			return result == 0;
//		}
//		if (this == NOT_EQUALS) {
//			return result != 0;
//		}
//		if (this == GREATER_THAN) {
//			return result > 0;
//		}
//		if (this == GREATER_THAN_EQUAL) {
//			return result >= 0;
//		}
//		if (this == LESS_THAN) {
//			return result < 0;
//		}
//		if (this == LESS_THAN_EQUAL) {
//			return result <= 0;
//		}
		throw new IllegalStateException("Invalid operator detected: " + this);
	}
	
	private static boolean isOnlyEquivalencySupported(Object lhs, Object rhs) {
		return lhs == null || rhs == null;
	}
	
}
