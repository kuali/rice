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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;

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

    private Object coerceRhs(Object lhs, Object rhs) {
        if (lhs != null && rhs != null) {
            if  (!lhs.getClass().equals(rhs.getClass()) && rhs instanceof String) {
                rhs = coerceRhsHelper(lhs, rhs.toString(), Double.class, Float.class, Long.class, Integer.class);

                if (rhs instanceof String) { // was coercion successful?
                    if (lhs instanceof BigDecimal) {
                        try {
                            rhs = BigDecimal.valueOf(Double.valueOf(rhs.toString()));
                        } catch (NumberFormatException e) {
                            throw new IncompatibleTypeException("Could not coerce String to BigDecimal" + this, rhs, lhs.getClass());
                        }
                    } else if (lhs instanceof BigInteger) {
                        try {
                            rhs = BigInteger.valueOf(Long.valueOf(rhs.toString()));
                        } catch (NumberFormatException e) {
                            throw new IncompatibleTypeException("Could not coerce String to BigInteger" + this, rhs, lhs.getClass());
                        }
                    } else {
                        throw new IncompatibleTypeException("Could not compare values for operator " + this, lhs, rhs.getClass());
                    }
                }
            }
        }
        return rhs;
    }

    private Object coerceRhsHelper(Object lhs, String rhs, Class<?> ... clazzes) {
        for (Class clazz : clazzes) {
            if (clazz.isInstance(lhs)) {
                try {
                    return clazz.getMethod("valueOf", String.class).invoke(null, rhs);
                } catch (NumberFormatException e) {
                    throw new IncompatibleTypeException("Could not coerce String to " +
                            clazz.getSimpleName() + " " + this, rhs, lhs.getClass());
                } catch (NoSuchMethodException e) {
                    throw new IncompatibleTypeException("Could not coerce String to " +
                            clazz.getSimpleName() + " " + this, rhs, lhs.getClass());
                } catch (InvocationTargetException e) {
                    throw new IncompatibleTypeException("Could not coerce String to " +
                            clazz.getSimpleName() + " " + this, rhs, lhs.getClass());
                } catch (IllegalAccessException e) {
                    throw new IncompatibleTypeException("Could not coerce String to " +
                            clazz.getSimpleName() + " " + this, rhs, lhs.getClass());
                }
            }
        }
        return rhs;
    }
	
	public boolean compare(Object lhs, Object rhs) {
		
		// TODO this implementation seems largely incomplete, it seems we are need to have some kind of engine
		// or utility which can coerce types to possible forms for comparision purposes?  For now, let's verify
		// they are of the same type

        rhs = coerceRhs(lhs, rhs);

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
