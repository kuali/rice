/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.krms.api.engine.IncompatibleTypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The default {@link ComparisonOperator}.  If no other {@link EngineComparatorExtension} have been configured to handle
 * a type, the DefaultComparisonOperator will be used.  At the moment the DefaultComparisonOperator is also the default
 * {@link StringCoercionExtension} for coercing types.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DefaultComparisonOperator implements EngineComparatorExtension, StringCoercionExtension {

    @Override
    public int compare(Object lhs, Object rhs) {

        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return -1;
        } else if (rhs == null) {
            return 1;
        }

        rhs = coerceRhs(lhs, rhs);
        if (ObjectUtils.equals(lhs, rhs)) {
            return 0;
        }

        if (lhs instanceof Comparable && rhs instanceof Comparable) {
            int result = ((Comparable)lhs).compareTo(rhs);
            return result;
        }
        else {
            throw new IncompatibleTypeException("DefaultComparisonOperator could not compare values", lhs, rhs.getClass());
        }
    }

    @Override
    public boolean canCompare(Object lhs, Object rhs) {
        try {
            compare(lhs, rhs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 
     * @param lhs
     * @param rhs
     * @return Object
     * @throws IncompatibleTypeException
     */
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

    /**
     *
     * @param lhs
     * @param rhs
     * @param clazzes
     * @return The object of one of the given types, whose value is rhs
     */
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

    @Override
    public Object coerce(String type, String value) {
        try {
            Class clazz = Class.forName(type);
            // Constructor that takes string  a bit more generic than the coerceRhs
            Constructor constructor = clazz.getConstructor(new Class[]{String.class});
            Object propObject = constructor.newInstance(type);
            return propObject;
        } catch (Exception e) {
            return null; // TODO EGHM dev log?
        }
    }

    @Override
    public boolean canCoerce(String type, String value) {
        return coerce(type, value) != null;
    }
}
