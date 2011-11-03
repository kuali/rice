/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.framework.engine.expression;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.krms.api.engine.IncompatibleTypeException;

public enum ComparisonOperator implements Coded {

	EQUALS("="),
	NOT_EQUALS("!="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_EQUAL("<=");
	
	private final String code;
	
	private ComparisonOperator(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static ComparisonOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ComparisonOperator comparisonOperator : values()) {
			if (comparisonOperator.code.equals(code)) {
				return comparisonOperator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ComparisionOperator with the given code: " + code);
	}
	
	public boolean compare(Object lhs, Object rhs) {
		
		// TODO this implementation seems largely incomplete, it seems we are need to have some kind of engine
		// or utility which can coerce types to possible forms for comparision purposes?  For now, let's verify
		// they are of the same type
		
		if (lhs != null && rhs != null && !lhs.getClass().equals(rhs.getClass())) {
			throw new IncompatibleTypeException("Could not compare values for operator " + this, lhs, rhs.getClass());
		}
		
		if (this == EQUALS) {
			return ObjectUtils.equals(lhs, rhs);
		} else if (this == NOT_EQUALS) {
			return ObjectUtils.notEqual(lhs, rhs);
		} else if (lhs == null || rhs == null) {
			// any other operation besides equals and not equals will evaluate to false in the case of null
			return false;
		}
		if (lhs instanceof Comparable && rhs instanceof Comparable) {
			
			// TODO not sure what to do about this cast and whether or not it will safe,
			// be sure to hit this in unit testing!
			
			int result = ((Comparable)lhs).compareTo(rhs);
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
		} else {
			throw new IncompatibleTypeException("Could not compare values, they are not comparable for operator " + this, lhs, rhs.getClass());
		}
		throw new IllegalStateException("Invalid operator detected: " + this);
	}
	
}
