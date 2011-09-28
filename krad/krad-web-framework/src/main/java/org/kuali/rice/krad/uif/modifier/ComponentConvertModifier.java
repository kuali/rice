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
package org.kuali.rice.krad.uif.modifier;

import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For a given <code>Component</code> instance converts all component properties
 * of a certain type to instances of another configured <code>Component</code>.
 * The conversion is performed recursively down all the component children
 * 
 * <p>
 * Some example uses of this are converting all checkbox controls to radio group
 * controls within a group and replacement of a widget with another
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentConvertModifier extends ComponentModifierBase {
	private static final long serialVersionUID = -7566547737669924605L;

	private Class<? extends Component> componentTypeToReplace;

	private Component componentReplacementPrototype;

	public ComponentConvertModifier() {
		super();
	}

	/**
	 * @see org.kuali.rice.krad.uif.modifier.ComponentModifier#performModification(org.kuali.rice.krad.uif.view.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
	 */
	@Override
	public void performModification(View view, Object model, Component component) {
		if (component == null) {
			return;
		}

		int idSuffix = 0;
		convertToReplacement(component, idSuffix);
	}

	/**
	 * Reads the component properties and looks for types that match the
	 * configured type to replace. If a match is found, a new instance of the
	 * replacement component prototype is created and set as the property value.
	 * The method is then called for each of the component's children
	 * 
	 * @param component
	 *            - component instance to inspect properties for
	 * @param idSuffix
	 *            - suffix string to use for any generated component
	 *            replacements
	 */
	protected void convertToReplacement(Component component, int idSuffix) {
		if (component == null) {
			return;
		}

		// check all component properties for the type to replace
		List<String> componentProperties = ComponentUtils.getComponentPropertyNames(component.getClass());
		for (String propertyPath : componentProperties) {
			Object propValue = ObjectPropertyUtils.getPropertyValue(component, propertyPath);

			if (propValue != null) {
				if (getComponentTypeToReplace().isAssignableFrom(propValue.getClass())) {
					// types match, convert the component
					performConversion(component, propertyPath, idSuffix++);
				}
			}
		}

		// recursively update components
		for (Component nestedComponent : component.getNestedComponents()) {
			convertToReplacement(nestedComponent, idSuffix);
		}
	}

	/**
	 * Creates a new instance of the replacement component prototype and sets a
	 * the property value for the given property name and component instance
	 * 
	 * @param component
	 *            - component instance to set property on
	 * @param componentProperty
	 *            - property name to set
	 * @param idSuffix
	 *            - suffix string to use for the generated component
	 */
	protected void performConversion(Component component, String componentProperty, int idSuffix) {
		// create new instance of replacement component
		Component componentReplacement = ComponentUtils.copy(getComponentReplacementPrototype(), Integer.toString(idSuffix));

		ObjectPropertyUtils.setPropertyValue(component, componentProperty, componentReplacement);
	}

	/**
	 * @see org.kuali.rice.krad.uif.modifier.ComponentModifier#getSupportedComponents()
	 */
	@Override
	public Set<Class<? extends Component>> getSupportedComponents() {
		Set<Class<? extends Component>> components = new HashSet<Class<? extends Component>>();
		components.add(Component.class);

		return components;
	}

	/**
	 * Type of component that should be replaced with an instance of the
	 * component prototype
	 * 
	 * @return Class<? extends Component> component type to replace
	 */
	public Class<? extends Component> getComponentTypeToReplace() {
		return this.componentTypeToReplace;
	}

	/**
	 * Setter for the component type to replace
	 * 
	 * @param componentTypeToReplace
	 */
	public void setComponentTypeToReplace(Class<? extends Component> componentTypeToReplace) {
		this.componentTypeToReplace = componentTypeToReplace;
	}

	/**
	 * Prototype for the component replacement
	 * 
	 * <p>
	 * Each time the type to replace if found a new instance of the component
	 * prototype will be created and set as the new property value
	 * </p>
	 * 
	 * @return
	 */
	public Component getComponentReplacementPrototype() {
		return this.componentReplacementPrototype;
	}

	/**
	 * Setter for the replacement component prototype
	 * 
	 * @param componentReplacementPrototype
	 */
	public void setComponentReplacementPrototype(Component componentReplacementPrototype) {
		this.componentReplacementPrototype = componentReplacementPrototype;
	}

}
