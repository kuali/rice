package org.kuali.rice.core.api.criteria;

import java.util.Arrays;

public final class PredicateFactory {
    private PredicateFactory() {
        throw new IllegalArgumentException("do not call");
    }

    /**
	 * Creates an {@link EqualPredicate}.  Defines that the property
     * represented by the given path should be equal to the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>character data</li>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return an EqualPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see EqualPredicate for more information
	 */
	public static Predicate equal(String propertyPath, Object value) {
		return new EqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link NotEqualPredicate}.  Defines that the property
     * represented by the given path should <strong>not</strong> be
	 * equal to the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>character data</li>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a NotEqualPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see NotEqualPredicate for more information
	 */
	public static Predicate notEqual(String propertyPath, Object value) {
		return new NotEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link LikePredicate}.  Defines that the property
     * represented by the given path should match the specified value,
	 * but supports the use of wildcards in the given value.
	 *
	 * <p>The supported wildcards include:
	 *
	 * <ul>
	 *   <li><strong>?</strong> - matches on any single character</li>
	 *   <li><strong>*</strong> - matches any string of any length (including zero length)</li>
	 * </ul>
	 *
	 * <p>Because of this, the like predicate only supports character data
	 * for the passed-in value.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a LikePredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null
	 *
	 * @see LikePredicate for more information
	 */
	public static Predicate like(String propertyPath, CharSequence value) {
		return new LikePredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

    /**
	 * Creates a {@link NotLikePredicate}.  Defines that the property
     * represented by the given path should <strong>not</strong> match the specified value,
	 * but supports the use of wildcards in the given value.
	 *
	 * <p>The supported wildcards include:
	 *
	 * <ul>
	 *   <li><strong>?</strong> - matches on any single character</li>
	 *   <li><strong>*</strong> - matches any string of any length (including zero length)</li>
	 * </ul>
	 *
	 * <p>Because of this, the like predicate only supports character data
	 * for the passed-in value.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a NotLikePredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null
	 *
	 * @see LikePredicate for more information
	 */
	public static Predicate notLike(String propertyPath, CharSequence value) {
		return new NotLikePredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Create an {@link InPredicate}.  Defines that the property
     * represented by the given path should be contained within the
	 * specified list of values.
	 *
	 * <p>Supports any of the valid types of value in the value list, with the
	 * restriction that all items in the list of values must be of the same type.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param values the values to compare with the property value located at the
	 * propertyPath
	 *
	 * @return an InPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 *
	 * @see InPredicate for more information
	 */
	public static <T> Predicate in(String propertyPath, T... values) {
		return new InPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
	}

	/**
	 * Create a {@link NotInPredicate}.  Defines that the property
     * represented by the given path should <strong>not</strong> be
	 * contained within the specified list of values.
	 *
	 * <p>Supports any of the valid types of value in the value list, with the
	 * restriction that all items in the list of values must be of the same type.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a NotInPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 *
	 * @see NotInPredicate for more information
	 */
	public static <T> Predicate notIn(String propertyPath, T... values) {
		return new NotInPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
	}

	/**
	 * Creates a {@link GreaterThanPredicate}.  Defines that the property
     * represented by the given path should be greater than the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a GreaterThanPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see GreaterThanPredicate for more information
	 */
	public static Predicate greaterThan(String propertyPath, Object value) {
		return new GreaterThanPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link GreaterThanOrEqualPredicate}.  Defines that the
     * property represented by the given path should be greater than
	 * or equal to the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a GreaterThanOrEqualPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see GreaterThanOrEqualPredicate for more information
	 */
	public static Predicate greaterThanOrEqual(String propertyPath, Object value) {
		return new GreaterThanOrEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link LessThanPredicate}.  Defines that the property
     * represented by the given path should be less than the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a LessThanPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see LessThanPredicate for more information
	 */
	public static Predicate lessThan(String propertyPath, Object value) {
		return new LessThanPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link LessThanOrEqualPredicate}.  Defines that the
     * property represented by the given path should be less than
	 * or equal to the specified value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>decimals</li>
	 *   <li>integers</li>
	 *   <li>date-time</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a LessThanOrEqualPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 *
	 * @see LessThanOrEqualPredicate for more information
	 */
	public static Predicate lessThanOrEqual(String propertyPath, Object value) {
		return new LessThanOrEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a {@link NullPredicate}.  Defines that the
	 * property represented by the given path should be null.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 *
	 * @return a NullPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 *
	 * @see NullPredicate for more information
	 */
	public static Predicate isNull(String propertyPath) {
		return new NullPredicate(propertyPath);
	}

	/**
	 * Creates a {@link NotNullPredicate}.  Defines that the property
     * represented by the given path should <strong>not</strong> be null.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 *
	 * @return a NotNullPredicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 *
	 * @see NotNullPredicate for more information
	 */
	public static Predicate isNotNull(String propertyPath) {
		return new NotNullPredicate(propertyPath);
	}

	/**
	 * Creates an {@link AndPredicate} that is used to define the
	 * predicates contained within the "and" predicate.
	 *
	 * <p>An "and" predicate will evaluate the truth value of of it's
	 * internal predicates and, if all of them evaluate to true, then
	 * the and predicate itself should evaluate to true.  The implementation
     * of an and predicate may short-circuit.
	 *
     * @param predicates to "and" together
     *
	 * @return an AndPredicate
	 *
	 * @see AndPredicate for more information
	 */
	public static Predicate and(Predicate... predicates) {
		return new AndPredicate(Arrays.asList(predicates));
	}

	/**
	 * Creates an  {@link OrPredicate} that is used to define the
	 * predicates contained within the "or" predicate.
	 *
	 * <p>An "or" predicate will evaluate the truth value of of it's
	 * internal predicates and, if any one of them evaluate to true, then
	 * the or predicate itself should evaluate to true.  If all predicates
	 * contained within the "or" evaluate to false, then the or iself will
	 * evaluate to false.   The implementation of an or predicate may
     * short-circuit.
	 *
     * @param predicates to "or" together
     *
	 * @return an OrPredicate
	 *
	 * @see OrPredicate for more information
	 */
	public static Predicate or(Predicate... predicates) {
		return new OrPredicate(Arrays.asList(predicates));
	}
}
