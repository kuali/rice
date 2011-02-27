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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.Ordered;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.field.GroupField;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.OrderComparator;

/**
 * Utility class providing methods to help create and modify
 * <code>Component</code> instances
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentUtils {

	@SuppressWarnings("unchecked")
	public static <T extends Component> T copy(T component, String idSuffix) {
		T copy = (T) ObjectUtils.deepCopy(component);
		updateIdsWithSuffix(copy, idSuffix);

		return copy;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T copy(T component) {
		return (T) ObjectUtils.deepCopy(component);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T copyField(T component, String addBindingPrefix, String idSuffix) {
		T copy = (T) ObjectUtils.deepCopy(component);
		updateIdsWithSuffix(copy, idSuffix);

		if (copy instanceof DataBinding) {
			prefixBindingPath((DataBinding) copy, addBindingPrefix);
		}

		return copy;
	}

	public static <T extends Field> List<T> copyFieldList(List<T> fields, String addBindingPrefix) {
		List<T> copiedFieldList = copyFieldList(fields);

		prefixBindingPath(copiedFieldList, addBindingPrefix);

		return copiedFieldList;
	}

	public static <T extends Field> List<T> copyFieldList(List<T> fields) {
		List<T> copiedFieldList = new ArrayList<T>();

		for (T field : fields) {
			T copiedField = copy(field);
			copiedFieldList.add(copiedField);
		}

		return copiedFieldList;
	}

	public static <T extends Component> List<T> copyComponentList(List<T> components) {
		List<T> copiedComponentList = new ArrayList<T>();

		for (T field : components) {
			T copiedComponent = copy(field);
			copiedComponentList.add(copiedComponent);
		}

		return copiedComponentList;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> List<T> getComponentsOfType(List<? extends Component> items,
			Class<T> componentType) {
		List<T> typeComponents = new ArrayList<T>();

		for (Component component : items) {
			if (componentType.isAssignableFrom(component.getClass())) {
				typeComponents.add((T) component);
			}
		}

		return typeComponents;
	}

	public static void prefixBindingPath(List<? extends Field> fields, String addBindingPrefix) {
		for (Field field : fields) {
			if (field instanceof DataBinding) {
				prefixBindingPath((DataBinding) field, addBindingPrefix);
			}
			else if (field instanceof GroupField) {
				List<Field> groupFields = getComponentsOfType(((GroupField) field).getItems(), Field.class);
				prefixBindingPath(groupFields, addBindingPrefix);
			}
		}
	}

	public static void prefixBindingPath(DataBinding field, String addBindingPrefix) {
		String bindingPrefix = addBindingPrefix;
		if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
			bindingPrefix += "." + field.getBindingInfo().getBindByNamePrefix();
		}
		field.getBindingInfo().setBindByNamePrefix(bindingPrefix);
	}

	public static void updateIdsWithSuffix(List<? extends Component> components, String idSuffix) {
		for (Component component : components) {
			updateIdsWithSuffix(component, idSuffix);
		}
	}

	public static void updateIdsWithSuffix(Component component, String idSuffix) {
		component.setId(component.getId() + idSuffix);

		for (Component nested : component.getNestedComponents()) {
			if (nested != null) {
				updateIdsWithSuffix(nested, idSuffix);
			}
		}
	}

	public static void setComponentsPropertyDeep(List<? extends Component> components, String propertyPath,
			Object propertyValue) {
		for (Component component : components) {
			setComponentPropertyDeep(component, propertyPath, propertyValue);
		}
	}

	public static void setComponentPropertyDeep(Component component, String propertyPath, Object propertyValue) {
		ModelUtils.setPropertyValue(component, propertyPath, propertyValue, true);

		for (Component nested : component.getNestedComponents()) {
			if (nested != null) {
				setComponentPropertyDeep(nested, propertyPath, propertyValue);
			}
		}
	}

	public static List<String> getComponentPropertyNames(Class<? extends Component> componentClass) {
		List<String> componentProperties = new ArrayList<String>();

		PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(componentClass);
		for (int i = 0; i < properties.length; i++) {
			PropertyDescriptor descriptor = properties[i];
			if (descriptor.getReadMethod() != null) {
				componentProperties.add(descriptor.getName());
			}
		}

		return componentProperties;
	}

	/**
	 * Performs sorting logic of the given list of <code>Ordered</code>
	 * instances by its order property
	 * 
	 * <p>
	 * Items list is sorted based on its order property. Lower order values are
	 * placed higher in the list. If a item does not have a value assigned for
	 * the order (or is equal to the default order of 0), it will be assigned
	 * the a value based on the given order sequence integer. If two or more
	 * items share the same order value, all but the last item found in the list
	 * will be removed.
	 * </p>
	 * 
	 * @param items
	 * @param defaultOrderSequence
	 * @return List<Ordered> sorted items
	 * @see org.kuali.rice.kns.uif.Component.getOrder()
	 * @see @see org.springframework.core.Ordered
	 */
	public static List<? extends Ordered> sort(List<? extends Ordered> items, int defaultOrderSequence) {
		List<Ordered> orderedItems = new ArrayList<Ordered>();

		// do replacement for items with the same order property value
		Set<Integer> foundOrders = new HashSet<Integer>();

		// reverse the list, so items later in the list win
		Collections.reverse(items);
		for (Ordered component : items) {
			int order = component.getOrder();

			// if order not set just add to list
			if (order == Ordered.INITIAL_ORDER_VALUE) {
				orderedItems.add(component);
			}
			// check if the order value has been used already
			else if (!foundOrders.contains(new Integer(order))) {
				orderedItems.add(component);
				foundOrders.add(new Integer(order));
			}
		}

		// now reverse the list back so we can assign defaults for items without
		// an order value
		Collections.reverse(items);
		for (Ordered component : items) {
			int order = component.getOrder();

			// if order property not set assign default
			if (order == Ordered.INITIAL_ORDER_VALUE) {
				defaultOrderSequence++;
				while (foundOrders.contains(new Integer(defaultOrderSequence))) {
					defaultOrderSequence++;
				}
				component.setOrder(defaultOrderSequence);
			}
		}

		// now sort the list by its order property
		Collections.sort(orderedItems, new OrderComparator());

		return orderedItems;
	}

}
