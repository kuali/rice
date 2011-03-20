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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO - this class is not thread safe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class CriteriaBuilder<T> {
	
	private final List<Entry> entries; 
	
	private CriteriaBuilder() {
		entries = new ArrayList<Entry>();
	}
	
	public static <T> CriteriaBuilder<T> newCriteriaBuilder(Class<T> targetClass) {
		if (targetClass == null) {
			throw new IllegalArgumentException("Target class cannot be null.");
		}
		return new CriteriaBuilder<T>();
	}

	public void equal(String propertyPath, Object value) {
		EqualExpression expression = new EqualExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
	}
	
	public void notEqual(String propertyPath, Object value) {

	}
	
	public void like(String propertyPath, Object value) {
		LikeExpression expression = new LikeExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
	}
	
	public void in(String propertyPath, List<Object> values) {
		
	}
	
	public void notIn(String propertyPath, List<Object> values) {
		
	}
	
	public void greaterThan(String propertyPath, Object value) {
		GreaterThanExpression expression = new GreaterThanExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
	}
	
	public void greaterThanOrEqual(String propertyPath, Object value) {
		
	}
	
	public void lessThan(String propertyPath, Object value) {
		LessThanExpression expression = new LessThanExpression(propertyPath, CriteriaSupportUtils.determineCriteriaValue(value));
		entries.add(new SimpleEntry(expression));
	}
	
	public void lessThanOrEqual(String propertyPath, Object value) {
		
	}
			  
	public void isNull(String propertyPath, Object value) {
		
	}
	
	public void isNotNull(String propertyPath, Object value) {
		
	}
	
	public CriteriaBuilder<T> and() {
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>();
		entries.add(new AndEntry<T>(criteriaBuilder));
		return criteriaBuilder;
	}
	
	public CriteriaBuilder<T> or() {
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>();
		entries.add(new OrEntry<T>(criteriaBuilder));
		return criteriaBuilder;
	}
				  
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
		
		private SimpleExpression expression;
		
		SimpleEntry(SimpleExpression expression) {
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
