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

import java.util.List;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface CriteriaBuilder<T> {

	public void equal(PropertyPath<T> propertyPath, Object value);
	
	public void notEqual(PropertyPath<T> propertyPath, Object value);
	
	public void like(PropertyPath<T> propertyPath, String value);
	
	public void in(PropertyPath<T> propertyPath, List<Object> values);
	
	public void notIn(PropertyPath<T> propertyPath, List<Object> values);
	
	public void greaterThan(PropertyPath<T> propertyPath, Object value);
	
	public void greaterThanOrEqual(PropertyPath<T> propertyPath, Object value);
	
	public void lessThan(PropertyPath<T> propertyPath, Object value);
	
	public void lessThanOrEqual(PropertyPath<T> propertyPath, Object value);
			  
	public void isNull(PropertyPath<T> propertyPath, Object value);
	
	public void isNotNull(PropertyPath<T> propertyPath, Object value);
	
	public void and(CriteriaBuilder<T> criteria);
	
	public void or(CriteriaBuilder<T> criteria);
				  
	public CriteriaBuilder<T> newCriteriaBuilder();
	   
	public Criteria build();
	
	
}
