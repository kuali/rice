/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Utility methods to get/set property values and working with model objects
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModelUtils {

	public static Object getPropertyValue(Object model, String propertyPath) {
		return wrapModel(model).getPropertyValue(propertyPath);
	}

	public static Class<?> getPropertyType(Object model, String propertyPath) {
		return wrapModel(model).getPropertyType(propertyPath);
	}

	public static Class<?> getPropertyType(Class<?> model, String propertyPath) {
		return new BeanWrapperImpl(model).getPropertyType(propertyPath);
	}

	public static BeanWrapper wrapModel(Object model) {
		BeanWrapper beanWrapper =  new BeanWrapperImpl(model);
		beanWrapper.setAutoGrowNestedPaths(true);
		
		return beanWrapper;
	}

}
