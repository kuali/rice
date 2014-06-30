/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.search.SearchOperator;

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
 * Predicate apiAuthors =
 *  and(
 *      like("display", "*Eric*"),
 *		greaterThan("birthDate", gtBirthDate),
 * 		lessThan("birthDate", ltBirthDate),
 *      or(
 *         equal("name.first", "Travis"),
 *         equal("name.last", "Schneeberger")))
 *
 * <p>
 *     <strong>WARNING:</strong> this class does automatic reductions
 *     such that you cannot assume the concrete {@link Predicate} type
 *     returned from this factory.
 * </p>
 *
 * @see QueryByCriteria
 */
public final class PredicateFactory {
    private PredicateFactory() {
        throw new IllegalArgumentException("do not call");
    }

    /**
	 * Creates an predicate representing equals comparison.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate equal(String propertyPath, Object value) {
		return new EqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

    /**
     * Creates an predicate representing an equals comparison between the property on the
     * object from the current query and another property.
     *
     * If the targetDataType is null, then the property will be resolved against the same object
     * as the propertyPath argument.
     *
     * For the purposes of inner queries, the targetPropertyPath may start with the string "parent." to refer to
     * the containing query's main object.  If "parent." is used in a non-inner query, query translation is likely
     * to fail.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param targetDataType If non-null, the data type against which the targetPropertyPath should be
     * resolved.  See above for special case values.
     * @param targetPropertyPath the value to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the targetPropertyPath is null
     * @since Rice 2.4.2
     */
    public static Predicate equalsProperty(String propertyPath, String targetDataType, String targetPropertyPath) {
        return new EqualPredicate(propertyPath, new CriteriaPropertyPathValue( new PropertyPath(targetDataType,targetPropertyPath) ));
    }

	/**
	 * Creates a predicate representing not equals comparison.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate notEqual(String propertyPath, Object value) {
		return new NotEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

    /**
     * Creates an predicate representing a not equals comparison between the property on the
     * object from the current query and another property.
     *
     * If the targetDataType is null, then the property will be resolved against the same object
     * as the propertyPath argument.
     *
     * For the purposes of inner queries, the targetDataType may be the string "parent" to refer to
     * the containing query.  If "parent" is used in a non-inner query, query translation is likely
     * to fail.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param targetDataType If non-null, the data type against which the targetPropertyPath should be
     * resolved.  See above for special case values.
     * @param targetPropertyPath the value to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the targetPropertyPath is null
     * @since Rice 2.4.2
     */
    public static Predicate notEqualsProperty(String propertyPath, String targetDataType, String targetPropertyPath) {
        return new NotEqualPredicate(propertyPath, new CriteriaPropertyPathValue( new PropertyPath(targetDataType,targetPropertyPath) ));
    }

    /**
	 * Creates an equals ignore case predicate.  Defines that the property
     * represented by the given path should be equal to the specified value ignoring
     * the case of the value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>character data</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate equalIgnoreCase(String propertyPath, CharSequence value) {
		return new EqualIgnoreCasePredicate(propertyPath, new CriteriaStringValue(value));
	}

    /**
     * Creates a like ignore case predicate.  Defines that the property
     * represented by the given path should be like to the specified value ignoring
     * the case of the value.
     *
     * <p>Supports the following types of values:
     *
     * <ul>
     *   <li>character data</li>
     * </ul>
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param value the value to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the value is null or of an invalid type
     */
    public static Predicate likeIgnoreCase(String propertyPath, CharSequence value) {
        return new LikeIgnoreCasePredicate(propertyPath, new CriteriaStringValue(value));
    }

	/**
	 * Creates a not equals ignore case predicate.  Defines that the property
     * represented by the given path should <strong>not</strong> be
	 * equal to the specified value ignoring the case of the value.
	 *
	 * <p>Supports the following types of values:
	 *
	 * <ul>
	 *   <li>character data</li>
	 * </ul>
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate notEqualIgnoreCase(String propertyPath, CharSequence value) {
		return new NotEqualIgnoreCasePredicate(propertyPath, new CriteriaStringValue(value));
	}

	/**
	 * Creates a like predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null
	 */
	public static Predicate like(String propertyPath, CharSequence value) {
		return new LikePredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

    /**
	 * Creates a not like predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null
	 */
	public static Predicate notLike(String propertyPath, CharSequence value) {
		return new NotLikePredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Create an in predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 */
	public static <T> Predicate in(String propertyPath, Collection<T> values ) {
		return new InPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
	}

    /**
     * Create an in predicate.  Defines that the property
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
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the values list is null, empty,
     * contains object of different types, or includes objects of an invalid type
     */
    public static <T> Predicate in(String propertyPath, T... values) {
        return new InPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
    }

	/**
	 * Create a not in predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 */
	public static <T> Predicate notIn(String propertyPath, T... values) {
		return new NotInPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
	}

    /**
     * Create a not in predicate.  Defines that the property
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
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the values list is null, empty,
     * contains object of different types, or includes objects of an invalid type
     */
    public static <T> Predicate notIn(String propertyPath, Collection<T> values) {
        return new NotInPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
    }

    /**
	 * Create an in ignore case predicate.  Defines that the property
     * represented by the given path should be contained within the
	 * specified list of values ignoring the case of the values.
	 *
	 * <p>Supports any of CharSequence value in the value list, with the
	 * restriction that all items in the list of values must be of the same type.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 * @param values the values to compare with the property value located at the
	 * propertyPath
	 *
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 */
	public static <T extends CharSequence> Predicate inIgnoreCase(String propertyPath, T... values) {
		return new InIgnoreCasePredicate(propertyPath, CriteriaSupportUtils.createCriteriaStringValueList(values));
	}

    /**
     * Create an in ignore case predicate.  Defines that the property
     * represented by the given path should be contained within the
     * specified list of values ignoring the case of the values.
     *
     * <p>Supports any of CharSequence value in the value list, with the
     * restriction that all items in the list of values must be of the same type.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param values the values to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the values list is null, empty,
     * contains object of different types, or includes objects of an invalid type
     */
    public static <T extends CharSequence> Predicate inIgnoreCase(String propertyPath, Collection<T> values) {
        return new InIgnoreCasePredicate(propertyPath, CriteriaSupportUtils.createCriteriaStringValueList(values));
    }

    /**
     * Create a not in ignore case.  Defines that the property
     * represented by the given path should <strong>not</strong> be
     * contained within the specified list of values ignoring the case of the values.
     *
     * <p>Supports any CharSequence value in the value list, with the
     * restriction that all items in the list of values must be of the same type.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param values the values to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the values list is null, empty,
     * contains object of different types, or includes objects of an invalid type
     */
    public static <T extends CharSequence> Predicate notInIgnoreCase(String propertyPath, T... values) {
        return new NotInIgnoreCasePredicate(propertyPath, CriteriaSupportUtils.createCriteriaStringValueList(values));
    }

    /**
     * Create a not in ignore case.  Defines that the property
     * represented by the given path should <strong>not</strong> be
     * contained within the specified list of values ignoring the case of the values.
     *
     * <p>Supports any CharSequence value in the value list, with the
     * restriction that all items in the list of values must be of the same type.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param values the values to compare with the property value located at the
     * propertyPath
     *
     * @return a predicate
     *
     * @throws IllegalArgumentException if the propertyPath is null
     * @throws IllegalArgumentException if the values list is null, empty,
     * contains object of different types, or includes objects of an invalid type
     */
    public static <T extends CharSequence> Predicate notInIgnoreCase(String propertyPath, Collection<T> values) {
        return new NotInIgnoreCasePredicate(propertyPath, CriteriaSupportUtils.createCriteriaStringValueList(values));
    }

	/**
	 * Creates a greater than predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate greaterThan(String propertyPath, Object value) {
		return new GreaterThanPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a greater than or equal predicate.  Defines that the
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate greaterThanOrEqual(String propertyPath, Object value) {
		return new GreaterThanOrEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a less than predicate.  Defines that the property
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate lessThan(String propertyPath, Object value) {
		return new LessThanPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates a less than or equal predicate.  Defines that the
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
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 */
	public static Predicate lessThanOrEqual(String propertyPath, Object value) {
		return new LessThanOrEqualPredicate(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
	}

	/**
	 * Creates an is null predicate.  Defines that the
	 * property represented by the given path should be null.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 *
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 */
	public static Predicate isNull(String propertyPath) {
		return new NullPredicate(propertyPath);
	}

	/**
	 * Creates an is not null predicate.  Defines that the property
     * represented by the given path should <strong>not</strong> be null.
	 *
	 * @param propertyPath the path to the property which should be evaluated
	 *
	 * @return a predicate
	 *
	 * @throws IllegalArgumentException if the propertyPath is null
	 */
	public static Predicate isNotNull(String propertyPath) {
		return new NotNullPredicate(propertyPath);
	}

    /**
     * Creates a (min/max -inclusive) between predicate.
     * @param propertyPath the path to the property which should be evaluated
     * @param value1 the first (lower bound) value
     * @param value2 the second (upper bound) value
     * @return a predicate representing the between expression
     */
    public static Predicate between(String propertyPath, Object value1, Object value2) {
        return between(propertyPath, value1, value2, SearchOperator.BETWEEN);
    }

    /**
     * Creates a (min/max -inclusive) between predicate, excluding those within.
     *
     * @param propertyPath the path to the property which should be evaluated
     * @param value1 the first (lower bound) value
     * @param value2 the second (upper bound) value
     * @return a predicate representing the between expression
     */
    public static Predicate notBetween(String propertyPath, Object value1, Object value2) {
        return notBetween(propertyPath, value1, value2, SearchOperator.BETWEEN);
    }

    /**
     * Creates a between predicate of the specified type
     * @param propertyPath the path to the property which should be evaluated
     * @param value1 the first (lower bound) value
     * @param value2 the second (upper bound) value
     * @param betweenType the type of between inclusivity/exclusivity
     * @return a predicate representing the between expression
     * @throws IllegalArgumentException of betweenType is not a valid BETWEEN search operator
     */
    public static Predicate between(String propertyPath, Object value1, Object value2, SearchOperator betweenType) {
        Predicate lower;
        Predicate upper;
        switch (betweenType) {
            case BETWEEN:
                lower = greaterThanOrEqual(propertyPath, value1);
                upper = lessThanOrEqual(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE:
                lower = greaterThan(propertyPath, value1);
                upper = lessThan(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE_LOWER:
                lower = greaterThan(propertyPath, value1);
                upper = lessThanOrEqual(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE_UPPER:
            case BETWEEN_EXCLUSIVE_UPPER2:
                lower = greaterThanOrEqual(propertyPath, value1);
                upper = lessThan(propertyPath, value2);
                break;
            default:
                throw new IllegalArgumentException("Invalid between operator: " + betweenType);
        }
        return and(lower, upper);
    }

    /**
     * Creates a not between predicate of the specified type
     * @param propertyPath the path to the property which should be evaluated
     * @param value1 the first (lower bound) value
     * @param value2 the second (upper bound) value
     * @param betweenType the type of between inclusivity/exclusivity
     * @return a predicate representing the between expression
     * @throws IllegalArgumentException of betweenType is not a valid BETWEEN search operator
     */
    public static Predicate notBetween(String propertyPath, Object value1, Object value2, SearchOperator betweenType) {
        Predicate lower;
        Predicate upper;
        switch (betweenType) {
            case BETWEEN:
                lower = lessThan(propertyPath, value1);
                upper = greaterThan(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE:
                lower = lessThanOrEqual(propertyPath, value1);
                upper = greaterThanOrEqual(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE_LOWER:
                lower = lessThanOrEqual(propertyPath, value1);
                upper = greaterThan(propertyPath, value2);
                break;
            case BETWEEN_EXCLUSIVE_UPPER:
            case BETWEEN_EXCLUSIVE_UPPER2:
                lower = lessThan(propertyPath, value1);
                upper = greaterThanOrEqual(propertyPath, value2);
                break;
            default:
                throw new IllegalArgumentException("Invalid between operator: " + betweenType);
        }
        return and(lower, upper);
    }

	/**
	 * Creates an and predicate that is used to "and" predicates together.
	 *
	 * <p>An "and" predicate will evaluate the truth value of of it's
	 * internal predicates and, if all of them evaluate to true, then
	 * the and predicate itself should evaluate to true.  The implementation
     * of an and predicate may short-circuit.
     *
     * <p>
     *     This factory method does automatic reductions.
     * </p>
	 *
     * @param predicates to "and" together
     *
	 * @return a predicate
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
	 * Creates an  or predicate that is used to "or" predicate together.
	 *
	 * <p>An "or" predicate will evaluate the truth value of of it's
	 * internal predicates and, if any one of them evaluate to true, then
	 * the or predicate itself should evaluate to true.  If all predicates
	 * contained within the "or" evaluate to false, then the or iself will
	 * evaluate to false.   The implementation of an or predicate may
     * short-circuit.
	 *
     * <p>
     *     This factory method does automatic reductions.
     * </p>
     * @param predicates to "or" together
     *
	 * @return a predicate
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


	/**
	 * This method will generate an "<tt>EXISTS ( SELECT 1 FROM subQueryType WHERE <predicate> )</tt>" style query.
	 *
	 * @param subQueryType The data type of the inner subquery
	 * @param subQueryPredicate Additional predicates to apply to the inner query - may be null.
	 * @return the Predicate
     * @throws IllegalArgumentException if subQueryBaseClass, subQueryProperty, or outerQueryProperty are blank or null.
     * @since Rice 2.4.2
	 */
	public static Predicate existsSubquery( String subQueryType, Predicate subQueryPredicate ) {
	    if ( StringUtils.isBlank( subQueryType ) ) {
            throw new IllegalArgumentException("subQueryBaseClass is blank or null");
	    }

	    return new ExistsSubQueryPredicate(subQueryType, subQueryPredicate);
	}


    /**
     * This method will construct a predicate based on the predicate name.
     *
     * ex: "or", Or, "OrPredicate" passing the arguments to the construction method.
     *
     * @param name the name of the predicate to create.
     * @param args the args required to construct the predicate
     * @return the Predicate
     */
    public static Predicate dynConstruct(String name, Object... args) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }

        final String correctedName = CriteriaSupportUtils.findDynName(name);
        outer:for (Method m : PredicateFactory.class.getMethods()) {
            // this class does overload some methods, so we need to make sure we call a version which accepts
            // the passed in arguments
            if (m.getName().equals(correctedName)) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
                    Class<?> parameterType = parameterTypes[parameterIndex];
                    Object arg = args[parameterIndex];
                    if (arg != null && !parameterType.isInstance(arg)) {
                        // this means the types don't match
                        continue outer;
                    }
                }
                try {
                    return (Predicate) m.invoke(null, args);
                } catch (InvocationTargetException e) {
                    throw new DynPredicateException(e);
                } catch (IllegalAccessException e) {
                    throw new DynPredicateException(e);
                }
            }
        }

        throw new DynPredicateException("predicate: " + name + " doesn't exist");
    }

    //this is really a fatal error (programming error)
    //and therefore is just a private exception not really meant to be caught
    private static class DynPredicateException extends RuntimeException {
        DynPredicateException(Throwable t) {
            super(t);
        }

        DynPredicateException(String s) {
            super(s);
        }
    }
}
