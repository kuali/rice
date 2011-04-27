package org.kuali.rice.core.api.criteria;

import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a factory class to construct {@link Predicate Predicates}.
 *
 * <p>
 *    For more readable predicate construction it is recommended that this class
 *    is statically imported.
 * <code>
 *    import static org.kuali.rice.core.api.criteria.PredicateFactory.*;
 * </code>
 * </p>
 *
 * to create a simple predicate where the property
 * foo.bar equals "baz" do the following:
 * <code>
 *     Predicate simple = equals("foo.bar", "baz");
 * </code>
 *
 * to create a compound predicate where the property
 * foo.bar equals "baz" and foo.id equals 1 do the following:
 * <code>
 *     Predicate compound = and(equals("foo.bar", "baz"), equals("foo.id", 1))
 * </code>
 *
 * to create a deeply nested predicate where lots of
 * properties are evaluated do the following:
 *
 * Predicate deep =
 *  and(
 *      like("display", "*Eric*"),
 *		greaterThan("birthDate", gtBirthDate),
 * 		lessThan("birthDate", ltBirthDate),
 *      or(
 *         equal("name.first", "Eric"),
 *         equal("name.last", "Westfall")))
 *
 * @see QueryByCriteria
 */
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
	 * @param values the values to compare with the property value located at the
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
     * <p>
     *     This factory method does automatic reductions such that
     *     this method may return a different predicate than expected.
     *     Do not assume the concrete return type of this method.
     * </p>
	 *
     * @param predicates to "and" together
     *
	 * @return an AndPredicate
	 *
	 * @see AndPredicate for more information
	 */
	public static Predicate and(Predicate... predicates) {
        //reduce single item compound
        if (predicates != null && predicates.length == 1 && predicates[0] != null) {
            return predicates[0];
        }
        final Set<Predicate> predicateSet = new HashSet<Predicate>();
        CollectionUtils.addAll(predicateSet, predicates);
        return new AndPredicate(predicateSet);
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
     * <p>
     *     This factory method does automatic reductions such that
     *     this method may return a different predicate than expected.
     *     Do not assume the concrete return type of this method.
     * </p>
     * @param predicates to "or" together
     *
	 * @return an OrPredicate
	 *
	 * @see OrPredicate for more information
	 */
	public static Predicate or(Predicate... predicates) {
        //reduce single item compound
        if (predicates != null && predicates.length == 1 && predicates[0] != null) {
            return predicates[0];
        }

        final Set<Predicate> predicateSet = new HashSet<Predicate>();
        CollectionUtils.addAll(predicateSet, predicates);
		return new OrPredicate(predicateSet);
	}
}
