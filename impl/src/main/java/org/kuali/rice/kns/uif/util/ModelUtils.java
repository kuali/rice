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

import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Utility methods to get/set property values and working with model objects
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.springframework.beans.BeanWrapper
 */
public class ModelUtils {

	@SuppressWarnings("unchecked")
	public static <T extends Object> T getPropertyValue(Object model, String propertyPath) {
		return (T) wrapModel(model).getPropertyValue(propertyPath);
	}

	public static void setPropertyValue(Object model, String propertyPath, Object propertyValue) {
		wrapModel(model).setPropertyValue(propertyPath, propertyValue);
	}

	public static void setPropertyValue(Object model, String propertyPath, Object propertyValue, boolean ignoreUnknown) {
		try {
			wrapModel(model).setPropertyValue(propertyPath, propertyValue);
		}
		catch (Exception e) {
			if (!ignoreUnknown) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Class<?> getPropertyType(Object model, String propertyPath) {
		return wrapModel(model).getPropertyType(propertyPath);
	}

	public static Class<?> getPropertyType(Class<?> model, String propertyPath) {
		return new BeanWrapperImpl(model).getPropertyType(propertyPath);
	}

	public static void initializeProperty(Object model, String propertyPath) {
		Class<?> propertyType = getPropertyType(model, propertyPath);
		try {
			setPropertyValue(model, propertyPath, propertyType.newInstance());
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to set new instance for property: " + propertyPath, e);
		}
	}

	public static void copyPropertiesToModel(Map<String, String> properties, Object model) {
		for (Map.Entry<String, String> property : properties.entrySet()) {
			setPropertyValue(model, property.getKey(), property.getValue());
		}
	}

	public static BeanWrapper wrapModel(Object model) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(model);
		beanWrapper.setAutoGrowNestedPaths(true);

		return beanWrapper;
	}

}
