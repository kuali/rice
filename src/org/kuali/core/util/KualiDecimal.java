/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * This class is a wrapper around java.math.BigDecimal. It exposes the only the needed functionality of BigDecimal, recasting return
 * results as KualiDecimal to simplify its use, and provides wrappers for BigDecimal's divide and setScale methods which default
 * (where applicable) to using 2 decimal places, and Banker's Rounding (or ROUND_HALF_EVEN).
 * 
 * Members of this class are, like BigDecimal, immutable; even methods which might be expected to change the value (like setScale,
 * for example) actually just return a new instance with the new value.
 * 
 * 
 */

public class KualiDecimal extends Number implements Comparable {
    public static final KualiDecimal ZERO = new KualiDecimal(0);


    // Default rounding behavior is Banker's Rounding. This means that
    // it rounds towards the "nearest neighbor" unless both neighbors
    // are equidistant, in which case, round towards the even neighbor.
    public static final int ROUND_BEHAVIOR = BigDecimal.ROUND_HALF_EVEN;
    public static final int SCALE = 2;


    private final BigDecimal value;

    /**
     * Constructor - only accepts a string representation of the value.
     * 
     * This is done to prevent unexpected inaccuracy by conversion to and from floating-point values.
     * 
     * @param value String containing numeric value
     * @throws IllegalArgumentException if the given String is null
     */
    public KualiDecimal(String value) {
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) String in KualiDecimal constructor");
        }

        this.value = new BigDecimal(value);
    }

    /**
     * Initializes this instance to the given integer value, then sets its scale to 2 to prevent unexpected side-effects associated
     * with integer arithmetic.
     */
    public KualiDecimal(int value) {
        this(new BigDecimal(value));
    }

    /**
     * 
     * @param value
     */
    public KualiDecimal(double value) {
        this(new BigDecimal(value));
    }

    /**
     * Simple constructor, copies in the given BigDecimal as the value for the instance.
     * 
     * Note: problems may arise if you pass in an integral BigDecimal (scale 0) and then try to do math with it, but forcibly
     * setting scale here causes other problems (since that forcibly converts all integers retrieved from the database to become
     * floats).
     * 
     * @param value BigDecimal to be used as basis for value
     * @throws IllegalArgumentException if the given BigDecimal is null
     */
    public KualiDecimal(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) BigDecimal in KualiDecimal constructor");
        }

        this.value = value.setScale(SCALE, ROUND_BEHAVIOR);
    }


    /**
     * @param operand
     * @return true if this KualiDecimal is less than the given KualiDecimal
     */
    public boolean isLessThan(KualiDecimal operand) {
        if (operand == null) {
            throw new IllegalArgumentException("invalid (null) operand");
        }

        return (this.compareTo(operand) == -1);
    }

    /**
     * @param operand
     * @return true if this KualiDecimal is greater than the given KualiDecimal
     */
    public boolean isGreaterThan(KualiDecimal operand) {
        if (operand == null) {
            throw new IllegalArgumentException("invalid (null) operand");
        }

        return (this.compareTo(operand) == 1);
    }

    /**
     * @param operand
     * @return true if this KualiDecimal is less than or equal to the given KualiDecimal
     */
    public boolean isLessEqual(KualiDecimal operand) {
        if (operand == null) {
            throw new IllegalArgumentException("invalid (null) operand");
        }

        return !isGreaterThan(operand);
    }

    /**
     * @param operand
     * @return true if this KualiDecimal is greater than or equal to the given KualiDecimal
     */
    public boolean isGreaterEqual(KualiDecimal operand) {
        if (operand == null) {
            throw new IllegalArgumentException("invalid (null) operand");
        }

        return !isLessThan(operand);
    }

    /**
     * @return true if this KualiDecimal is less than zero
     */
    public boolean isNegative() {
        return (this.compareTo(ZERO) == -1);
    }

    /**
     * @return true if this KualiDecimal is greater than zero
     */
    public boolean isPositive() {
        return (this.compareTo(ZERO) == 1);
    }


    /**
     * @return true if this KualiDecimal is equal to zero
     */
    public boolean isZero() {
        return (this.compareTo(ZERO) == 0);
    }


    /**
     * @return true if this KualiDecimal is not equal to zero
     */
    public boolean isNonZero() {
        return !this.isZero();
    }


    /**
     * Wraps BigDecimal's add method to accept and return KualiDecimal instances instead of BigDecimals, so that users of the class
     * don't have to typecast the return value.
     * 
     * @param addend
     * @return result of adding the given addend to this value
     * @throws IllegalArgumentException if the given addend is null
     */
    public KualiDecimal add(KualiDecimal addend) {
        if (addend == null) {
            throw new IllegalArgumentException("invalid (null) addend");
        }

        BigDecimal sum = this.value.add(addend.value);
        return new KualiDecimal(sum);
    }

    /**
     * Wraps BigDecimal's subtract method to accept and return KualiDecimal instances instead of BigDecimals, so that users of the
     * class don't have to typecast the return value.
     * 
     * @param subtrahend
     * @return result of the subtracting the given subtrahend from this value
     * @throws IllegalArgumentException if the given subtrahend is null
     */
    public KualiDecimal subtract(KualiDecimal subtrahend) {
        if (subtrahend == null) {
            throw new IllegalArgumentException("invalid (null) subtrahend");
        }

        BigDecimal difference = this.value.subtract(subtrahend.value);
        return new KualiDecimal(difference);
    }

    /**
     * Wraps BigDecimal's multiply method to accept and return KualiDecimal instances instead of BigDecimals, so that users of the
     * class don't have to typecast the return value.
     * 
     * @param multiplicand
     * @return result of multiplying this value by the given multiplier
     * @throws IllegalArgumentException if the given multiplier is null
     */
    public KualiDecimal multiply(KualiDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("invalid (null) multiplier");
        }

        BigDecimal product = this.value.multiply(multiplier.value);
        return new KualiDecimal(product);
    }

    /**
     * This method calculates the mod between to KualiDecimal values by first casting to doubles and then by performing the %
     * operation on the two primitives.
     * 
     * @param modulus The other value to apply the mod to.
     * @return result of performing the mod calculation
     * @throws IllegalArgumentException if the given modulus is null
     */
    public KualiDecimal mod(KualiDecimal modulus) {
        if (modulus == null) {
            throw new IllegalArgumentException("invalid (null) modulus");
        }

        double difference = this.value.doubleValue() % modulus.doubleValue();

        return new KualiDecimal(new BigDecimal(difference).setScale(SCALE, BigDecimal.ROUND_UNNECESSARY));
    }

    /**
     * Wraps BigDecimal's divide method to enforce the default Kuali rounding behavior (Banker's Rounding, or
     * java.math.BigDecimal.ROUND_HALF_EVEN).
     * 
     * @param divisor
     * @return result of dividing this value by the given divisor
     * @throws an IllegalArgumentException if the given divisor is null
     */
    public KualiDecimal divide(KualiDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("invalid (null) divisor");
        }

        BigDecimal quotient = this.value.divide(divisor.value, ROUND_BEHAVIOR);
        return new KualiDecimal(quotient);
    }


    /**
     * Wraps BigDecimal's setScale method to enforce the default Kuali rounding behavior.
     * 
     * @return KualiDecimal instance set to the given scale, rounded with the default rounding behavior (if necessary)
     */
    public KualiDecimal setScale(int scale) {
        BigDecimal scaled = this.value.setScale(scale, ROUND_BEHAVIOR);
        return new KualiDecimal(scaled);
    }

    /**
     * Simplified wrapper over the setScale() method, this one has no arguments. When used with no arguments, it defaults to the
     * Kuali default Scale and Rounding.
     * 
     * @return a rounded, scaled, KualiDecimal
     */
    public KualiDecimal setScale() {
        return setScale(SCALE);
    }

    /**
     * @return a KualiDecimal with the same scale and a negated value (iff the value is non-zero)
     */
    public KualiDecimal negated() {
        return multiply(new KualiDecimal("-1"));
    }

    /**
     * @return a KualiDecimal with the same scale and the absolute value
     */
    public KualiDecimal abs() {
        KualiDecimal absolute = null;

        if (isNegative()) {
            absolute = negated();
        }
        else {
            absolute = this;
        }

        return absolute;
    }


    /**
     * @return true if the given String can be used to construct a valid KualiDecimal
     */
    public static boolean isNumeric(String s) {
        boolean isValid = false;

        if (!StringUtils.isBlank(s)) {
            try {
                new KualiDecimal(s);
                isValid = true;
            }
            catch (NumberFormatException e) {
            }
        }

        return isValid;
    }


    // Number methods
    /**
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return this.value.doubleValue();
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return this.value.floatValue();
    }

    /**
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return this.value.intValue();
    }

    /**
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return this.value.longValue();
    }

    /**
     * @return the value of this instance as a BigDecimal.
     */
    public BigDecimal bigDecimalValue() {
        return this.value;
    }


    // Comparable methods
    /**
     * Compares this KualiDecimal with the specified Object. If the Object is a KualiDecimal, this method behaves like
     * java.lang.Comparable#compareTo(java.lang.Object).
     * 
     * Otherwise, it throws a <tt>ClassCastException</tt> (as KualiDecimals are comparable only to other KualiDecimals).
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return compareTo((KualiDecimal) o);
    }

    /**
     * Returns the result of comparing the values of this KualiDecimal and the given KualiDecimal.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(KualiDecimal k) {
        return this.value.compareTo(k.value);
    }


    // Object methods
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof KualiDecimal) {
            KualiDecimal k = (KualiDecimal) obj;

            // using KualiDecimal.compareTo instead of BigDecimal.equals since BigDecimal.equals only returns true if the
            // scale and precision are equal, rather than comparing the actual (scaled) values
            equals = (this.compareTo(k) == 0);
        }

        return equals;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.value.toString();
    }
}
