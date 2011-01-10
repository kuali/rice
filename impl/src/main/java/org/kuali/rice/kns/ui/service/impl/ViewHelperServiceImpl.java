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
package org.kuali.rice.kns.ui.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.container.Group;
import org.kuali.rice.kns.ui.container.NavigationGroup;
import org.kuali.rice.kns.ui.container.View;
import org.kuali.rice.kns.ui.field.AttributeField;
import org.kuali.rice.kns.ui.field.Field;
import org.kuali.rice.kns.ui.initializer.ComponentInitializer;
import org.kuali.rice.kns.ui.service.ViewHelperService;
import org.kuali.rice.kns.ui.widget.Widget;

/**
 * Default Implementation of <code>ViewHelperService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceImpl implements ViewHelperService {

	private DataDictionaryService dataDictionaryService;

	/**
	 * @see org.kuali.rice.kns.ui.service.ViewHelperService#performInitialization(org.kuali.rice.kns.ui.container.View,
	 *      java.util.Map)
	 */
	@Override
	public void performInitialization(View view, Map<String, String> options) {
		// create new instance of id generator so unique ids are assigned within
		// the view
		IdGenerator idGenerator = new IdGenerator();

		initializeComponent(view, view, idGenerator, options);
	}

	/**
	 * Performs initialization of a component with four steps:
	 * <ul>
	 * <li>Invoke the initialize method on the component. Here the component can
	 * setup defaults and do other initialization that is specific to that
	 * component.</li>
	 * <li>Invoke and configured <code>ComponentInitializer</code> instances for
	 * the component.</li>
	 * <li>Based on the component type, call help initialize method on the
	 * service. Here common initialization for the type can be done, along with
	 * providing fine grained hooks for service overrides.</li>
	 * <li>Call the component to get the List of components that are nested
	 * within and recursively call this method to initialize those components.</li>
	 * </ul>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param component
	 *            - component instance to initialize
	 * @param idGenerator
	 *            - instance of <code>IdGenerator</code> for assigning component
	 *            ids
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeComponent(View view, Component component, IdGenerator idGenerator,
			Map<String, String> options) {
		if (component == null) {
			return;
		}

		// assign component id if not set already
		if (StringUtils.isBlank(component.getId())) {
			component.setId("id_" + idGenerator.getNextId());
		}

		// for attribute fields, set defaults from dictionary entry
		if (component instanceof AttributeField) {
			initializeAttributeFieldFromDataDictionary(view, (AttributeField) component, options);
			initializeAttributeField(view, (AttributeField) component, options);
		}

		// invoke component to initialize itself after properties have been set
		component.initialize(options);

		// invoke component initializers
		for (ComponentInitializer initializer : component.getComponentInitializers()) {
			initializer.initialize(component, options);
		}

		// invoke initialize service hooks
		if (component instanceof View) {
			initializeView((View) component, options);
		}
		else if (component instanceof NavigationGroup) {
			initializeNavigation(view, (NavigationGroup) component, options);
		}
		if (component instanceof Group) {
			initializeGroup(view, (Group) component, options);
		}
		if (component instanceof AttributeField) {
			initializeAttributeField(view, (AttributeField) component, options);
		}
		if (component instanceof Field) {
			initializeField(view, (Field) component, options);
		}
		if (component instanceof Widget) {
			initializeWidget(view, (Widget) component, options);
		}

		// initialize nested components
		for (Component nestedComponent : component.getNestedComponents()) {
			initializeComponent(view, nestedComponent, idGenerator, options);
		}
	}

	/**
	 * Sets properties of the <code>AttributeField</code> (if blank) to the
	 * corresponding attribute entry in the data dictionary
	 * 
	 * @param view
	 *            - view instance containing the field
	 * @param field
	 *            - field instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeAttributeFieldFromDataDictionary(View view, AttributeField field,
			Map<String, String> options) {
		// determine attribute name and entry within the dictionary to lookup
		String dictionaryAttributeName = field.getDictionaryAttributeName();
		String dictionaryObjectEntry = field.getDictionaryObjectEntry();

		// if entry given but not attribute name, use field name as attribute
		// name
		if (StringUtils.isNotBlank(dictionaryObjectEntry)) {
			dictionaryAttributeName = field.getName();
		}

		// if both dictionary names not given and the field is from a model,
		// determine class based on the View and use field name as attribute
		// name
		if (StringUtils.isBlank(dictionaryAttributeName) && StringUtils.isBlank(dictionaryObjectEntry)
				&& field.isBindToModel()) {
			dictionaryAttributeName = field.getName();
			Class<?> dictionaryModelClass = getModelClassForAttribute(view, field);
			if (dictionaryModelClass != null) {
				dictionaryObjectEntry = dictionaryModelClass.getName();
			}
		}

		// if we were able to find a dictionary attribute and object, call
		// data dictionary service to get AttributeDefinition
		if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
			AttributeDefinition attributeDefinition = dataDictionaryService.getAttributeDefinition(
					dictionaryObjectEntry, dictionaryAttributeName);
			if (attributeDefinition != null) {
				field.copyFromAttributeDefinition(attributeDefinition);
			}
		}
	}

	/**
	 * Determines the model <code>Class</code> that the given attribute belongs
	 * to
	 * <p>
	 * Model is determined by inspecting the modelClasses Map of the
	 * <code>View</code>. If the Map only contains one entry, that entry value
	 * (giving the Class) is returned. Otherwise the the Map is searched for key
	 * that equals the bindByNamePrefix property for the
	 * <code>AttributeField</code>.
	 * </p>
	 * 
	 * @param view
	 *            - the view instance containing the field and model
	 *            configuration
	 * @param field
	 *            - the field containing the attribute whose model should be
	 *            determined
	 * @return Class<?> for the model which the attribute belongs, or Null if a
	 *         model was not found
	 */
	protected Class<?> getModelClassForAttribute(View view, AttributeField field) {
		Class<?> modelClass = null;

		Map<String, Class<?>> modelClasses = view.getModelClasses();
		if (modelClasses.size() == 1) {
			modelClass = modelClasses.values().iterator().next();
		}
		else {
			if (modelClasses.containsKey(field.getBindByNamePrefix())) {
				modelClass = modelClasses.get(field.getBindByNamePrefix());
			}
		}

		return modelClass;
	}

	/**
	 * Called to perform custom initialization on the <code>View</code>
	 * 
	 * @param view
	 *            - view instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeView(View view, Map<String, String> options) {

	}

	/**
	 * Called to perform custom initialization on the
	 * <code>NavigationGroup</code>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param navigationGroup
	 *            - navigation group instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeNavigation(View view, NavigationGroup navigationGroup, Map<String, String> options) {

	}

	/**
	 * Called to perform custom initialization on the <code>Group</code>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param group
	 *            - group instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeGroup(View view, Group group, Map<String, String> options) {

	}

	/**
	 * Called to perform custom initialization on the
	 * <code>AttributeField</code>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param field
	 *            - field instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeAttributeField(View view, AttributeField field, Map<String, String> options) {

	}

	/**
	 * Called to perform custom initialization on the <code>Field</code>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param field
	 *            - field instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeField(View view, Field field, Map<String, String> options) {

	}

	/**
	 * Called to perform custom initialization on the <code>Widget</code>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param widget
	 *            - widget instance to initialize
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	protected void initializeWidget(View view, Widget widget, Map<String, String> options) {

	}

	/**
	 * Generates a unique identifier by incrementing a counter (starting with 1)
	 */
	protected class IdGenerator {
		private int id;

		public IdGenerator() {
			id = 0;
		}

		/**
		 * Next available id
		 * 
		 * @return int
		 */
		public int getNextId() {
			id++;

			return id;
		}
	}

	protected DataDictionaryService getDataDictionaryService() {
		return this.dataDictionaryService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
