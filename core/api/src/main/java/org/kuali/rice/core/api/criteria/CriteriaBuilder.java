/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Aids in the construction of {@link Criteria} object for use in
 * criteria-based queries.
 * 
 * <p>In order to construct an instance of a {@link CriteriaBuilder}, use the
 * {@link #newCriteriaBuilder(Class)} method.  The given class type should
 * indicate the type of object against which the criteria will be used to
 * query.  This is used to aid in the validation and construction of the
 * property paths which identify the properties of the object which will be 
 * evaluated during execution of the query.
 * 
 * <p>A propertyPath represents a relative path from the parameterized type "T"
 * and supports dot-notation to access nested properties of the "T" class.
 * 
 * <p>While many of the methods used to construct the criteria on this class
 * take a generic {@link Object} value, depending on the particular expression
 * only certain values might be valid.  The criteria framework supports four
 * different "classes" of values, as follows:
 * 
 * <ol>
 *   <li>character data</li>
 *   <li>decimals</li>
 *   <li>integers</li>
 *   <li>date-time</li>
 * </ol>
 * 
 * <p>For each of the above, the following object types are supported:
 * 
 * <ol>
 *   <li><strong>character data</strong> - {@link CharSequence}</li>
 *   <li><strong>decimals</strong> - {@link BigDecimal}, {@link Float}, {@link Double}</li>
 *   <li><strong>integers</strong> - {@link BigInteger}, {@link Short}, {@link Integer}, {@link Long}, {@link AtomicInteger}, {@link AtomicLong}</li>
 *   <li><strong>date-time</strong> - {@link Date}, {@link Calendar}</li>
 * </ol> 
 * 
 * <p>Once a new instance is obtained, the criteria is built by appending
 * the various expressions together using the methods provided.  The criteria
 * produced by the builder will contain an implicit "and" of any of the
 * expressions which are added at the top-level of the builder.  Each
 * invocation of one of the methods on the builder will add the related
 * expression to the end of the entries which are tracked internally on this
 * builder.  Once the {@link #build()} method is invoked, a criteria
 * object will be constructed and returned which contains all of the defined
 * expressions.
 * 
 * <p>It is possible to construct an empty set of criteria by creating a new
 * builder and then simply invoking {@link #build()} on it without adding any
 * expressions.
 * 
 * <p>This class is <strong>not</strong> thread safe.  Once a new instance is obtained it
 * should only be used by a single thread.  The resulting {@link Criteria}
 * which is created when the {@link #build()} method is invoked is both
 * immutable and thread-safe.
 * 
 * @param <T> the type of the class to which property paths are relative 
 * 
 * @see Criteria
 * @see QueryByCriteria
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class CriteriaBuilder<T> {
	
	private final List<Entry> entries; 
	
	private CriteriaBuilder() {
		entries = new ArrayList<Entry>();
	}
	
	/**
	 * Constructs a new {@link CriteriaBuilder} targeting criteria that will be
	 * used to query for data of the targetClass's type.  The builder
	 * that is created will produce a {@link Criteria} object which will contain
	 * an implicit "and" of all expressions contained within.
	 * 
	 * @param targetClass the target class which represents the root from which
	 * property paths are defined
	 * 
	 * @return the newly created {@link CriteriaBuilder}
	 * 
	 * @throws IllegalArgumentException if the targetClass is null
	 */
	public static <T> CriteriaBuilder<T> newCriteriaBuilder(Class<T> targetClass) {
		if (targetClass == null) {
			throw new IllegalArgumentException("Target class cannot be null.");
		}
		return new CriteriaBuilder<T>();
	}

	/**
	 * Appends an {@link EqualExpression} to the builder.  Defines that the
	 * property represented by the given path should be equal to the specified
	 * value. 
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see EqualExpression
	 */
	public CriteriaBuilder<T> equal(String propertyPath, Object value) {
		EqualExpression expression = new EqualExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link NotEqualExpression} to the builder.  Defines that the
	 * property represented by the given path should <strong>not</strong> be
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see NotEqualExpression
	 */
	public CriteriaBuilder<T> notEqual(String propertyPath, Object value) {
		NotEqualExpression expression = new NotEqualExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link LikeExpression} to the builder.  Defines that the
	 * property represented by the given path should match the specified value,
	 * but supports the use of wildcards in the given value.
	 * 
	 * <p>The supported wildcards include:
	 * 
	 * <ul>
	 *   <li><strong>?</strong> - matches on any single character</li>
	 *   <li><strong>*</strong> - matches any string of any length (including zero length)</li>
	 * </ul>
	 * 
	 * <p>Because of this, the like expression only supports character data
	 * for the passed-in value.
	 * 
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 * 
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null
	 * 
	 * @see LikeExpression for more information
	 */
	public CriteriaBuilder<T> like(String propertyPath, CharSequence value) {
		LikeExpression expression = new LikeExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends an {@link InExpression} to the builder.  Defines that the
	 * property represented by the given path should be contained within the
	 * specified list of values.
	 * 
	 * <p>Supports any of the valid types of value in the value list, with the
	 * restriction that all items in the list of values must be of the same type.
	 * 
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 * 
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 * 
	 * @see InExpression for more information
	 */
	public CriteriaBuilder<T> in(String propertyPath, List<?> values) {
		InExpression expression = new InExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link NotInExpression} to the builder.  Defines that the
	 * property represented by the given path should <strong>not</strong> be
	 * contained within the specified list of values.
	 * 
	 * <p>Supports any of the valid types of value in the value list, with the
	 * restriction that all items in the list of values must be of the same type.
	 * 
	 * @param propertyPath the path to the property which should be evaluated
	 * @param value the value to compare with the property value located at the
	 * propertyPath
	 * 
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the values list is null, empty,
	 * contains object of different types, or includes objects of an invalid type
	 * 
	 * @see InExpression
	 */
	public CriteriaBuilder<T> notIn(String propertyPath, List<?> values) {
		NotInExpression expression = new NotInExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValueList(values));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link GreaterThanExpression} to the builder.  Defines that
	 * the property represented by the given path should be greater than the
	 * specified value. 
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see GreaterThanExpression
	 */
	public CriteriaBuilder<T> greaterThan(String propertyPath, Object value) {
		GreaterThanExpression expression = new GreaterThanExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link GreaterThanOrEqualExpression} to the builder.  Defines
	 * that the property represented by the given path should be greater than
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see GreaterThanOrEqualExpression
	 */
	public CriteriaBuilder<T> greaterThanOrEqual(String propertyPath, Object value) {
		GreaterThanOrEqualExpression expression = new GreaterThanOrEqualExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link LessThanExpression} to the builder.  Defines that
	 * the property represented by the given path should be less than the
	 * specified value. 
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see LessThanExpression
	 */
	public CriteriaBuilder<T> lessThan(String propertyPath, Object value) {
		LessThanExpression expression = new LessThanExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link LessThanOrEqualExpression} to the builder.  Defines
	 * that the property represented by the given path should be less than
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
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * @throws IllegalArgumentException if the value is null or of an invalid type
	 * 
	 * @see LessThanOrEqualExpression
	 */
	public CriteriaBuilder<T> lessThanOrEqual(String propertyPath, Object value) {
		LessThanOrEqualExpression expression = new LessThanOrEqualExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
		return this;
	}
		
	/**
	 * Appends a {@link NullExpression} to the builder.  Defines that the
	 * property represented by the given path should be null.
	 * 
	 * @param propertyPath the path to the property which should be evaluated
	 * 
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * 
	 * @see NullExpression
	 */
	public CriteriaBuilder<T> isNull(String propertyPath) {
		NullExpression expression = new NullExpression(propertyPath);
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends a {@link NotNullExpression} to the builder.  Defines that the
	 * property represented by the given path should <strong>not</strong> be null.
	 * 
	 * @param propertyPath the path to the property which should be evaluated
	 * 
	 * @return a reference to "this" CriteriaBuilder, allows for fluent
	 * chaining of method calls
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null
	 * 
	 * @see NotNullExpression
	 */
	public CriteriaBuilder<T> isNotNull(String propertyPath) {
		NotNullExpression expression = new NotNullExpression(propertyPath);
		entries.add(new SimpleEntry(expression));
		return this;
	}
	
	/**
	 * Appends an {@link AndExpression} to the builder and returns a reference
	 * to a {@link CriteriaBuilder} that can be used to define the
	 * expressions contained within the "and" expression.
	 * 
	 * <p>An "and" expression will evaluate the truth value of all of it's
	 * internal expressions and, if all of them evaluate to true, then
	 * the and expression itself should evaluate to true.
	 * 
	 * <p>It's important to note that the {@link CriteriaBuilder} returned
	 * by the {@link #and()} method is not the same as the builder on which
	 * it was invoked.  Instead this is a new builder that can be used to
	 * construct the contents of the "and".  You should never have to manually
	 * invoke {@link #build()} on the builder returned from this method
	 * because it will be automatically invoked when the top-level builder's
	 * {@link #build()} method is invoked.
	 * 
	 * @return a reference to a {@link CriteriaBuilder} that can be used to
	 * construct the contents of the and expression
	 * 
	 * @see AndExpression
	 */
	public CriteriaBuilder<T> and() {
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>();
		entries.add(new AndEntry<T>(criteriaBuilder));
		return criteriaBuilder;
	}
	
	/**
	 * Appends an {@link OrExpression} to the builder and returns a reference
	 * to a {@link CriteriaBuilder} that can be used to define the
	 * expressions contained within the "or" expression.
	 * 
	 * <p>An "or" expression will evaluate the truth value of all of it's
	 * internal expressions and, if any one of them evaluate to true, then
	 * the and expression itself should evaluate to true.  If all expressions
	 * contained within the "or" evaluate to false, then the or iself will
	 * evaluate to false.
	 * 
	 * <p>It's important to note that the {@link CriteriaBuilder} returned
	 * by the {@link #or()} method is not the same as the builder on which
	 * it was invoked.  Instead this is a new builder that can be used to
	 * construct the contents of the "or".  You should never have to manually
	 * invoke {@link #build()} on the builder returned from this method
	 * because it will be automatically invoked when the top-level builder's
	 * {@link #build()} method is invoked.
	 * 
	 * @return a reference to a {@link CriteriaBuilder} that can be used to
	 * construct the contents of the or expression
	 * 
	 * @see OrExpression
	 */
	public CriteriaBuilder<T> or() {
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>();
		entries.add(new OrEntry<T>(criteriaBuilder));
		return criteriaBuilder;
	}
	
	/**
	 * Builds an immutable {@link Criteria} object which contains instantiated
	 * forms of all the expressions that were defined during interaction with
	 * this builder prior to the invocation of this method.
	 * 
	 * <p>The resulting criteria will represent an implicit "and" of all
	 * expressions included inside of it at the top level.  When invoked, this
	 * method will recursively invoke any builders created by the {@link #and()}
	 * and {@link #or()} and use the expressions on the resulting criteria to
	 * form the top-level criteria.
	 * 
	 * <p>In general practice, this method should only be invoked once, but if
	 * invoked more than once against the same builder state then it will
	 * return an equivalent criteria object.  Additionally, this builder is
	 * still active after this method has been invoked, so additional
	 * expressions could be added and a new criteria built at a later time.
	 * 
	 * @return a Criteria object built from the expressions defined by this builder
	 */
	public Criteria build() {
		List<Expression> builtExpressions = new ArrayList<Expression>();
		for (Entry entry : entries) {
			builtExpressions.add(entry.buildExpression());
		}
		return new Criteria(builtExpressions);
	}
	
	private static interface Entry {
		
		public Expression buildExpression();
		
	}
	
	private static final class SimpleEntry implements Entry {
		
		private Expression expression;
		
		SimpleEntry(Expression expression) {
			this.expression = expression;
		}
		
		public Expression buildExpression() {
			return expression;
		}
		
	}
	
	private static final class AndEntry<T> implements Entry {
		
		private CriteriaBuilder<T> criteriaBuilder;
		
		AndEntry(CriteriaBuilder<T> criteriaBuilder) {
			this.criteriaBuilder = criteriaBuilder;
		}
		
		public Expression buildExpression() {
			Criteria criteria = criteriaBuilder.build();
			return new AndExpression(criteria.getExpressions());
		}
		
	}
	
	private static final class OrEntry<T> implements Entry {
		
		private CriteriaBuilder<T> criteriaBuilder;
		
		OrEntry(CriteriaBuilder<T> criteriaBuilder) {
			this.criteriaBuilder = criteriaBuilder;
		}
		
		public Expression buildExpression() {
			Criteria criteria = criteriaBuilder.build();
			return new OrExpression(criteria.getExpressions());
		}
		
	}

}
