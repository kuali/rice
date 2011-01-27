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
package org.kuali.rice.kns.uif.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.initializer.ComponentInitializer;
import org.kuali.rice.kns.uif.service.ViewHelperService;
import org.kuali.rice.kns.uif.util.ViewModelUtils;

/**
 * Default Implementation of <code>ViewHelperService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceImpl implements ViewHelperService {

	private DataDictionaryService dataDictionaryService;

	/**
	 * Default implementation consults the <code>View</code> instance for the
	 * configured allowed parameters. If matches of those parameter names are
	 * found in the given Map, the parameter key/value is placed into the
	 * returned context Map
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#createInitialViewContext(org.kuali.rice.kns.uif.container.View,
	 *      java.util.Map)
	 */
	@Override
	public Map<String, String> createInitialViewContext(View view, Map<String, String> parameters) {
		Map<String, String> context = new HashMap<String, String>();

		if (parameters == null || parameters.isEmpty()) {
			return context;
		}

		Set<String> allowedParameters = view.getAllowedParameters();
		for (String parameterName : allowedParameters) {
			if (parameters.containsKey(parameterName)) {
				context.put(parameterName, parameters.get(parameterName));
			}
		}

		return context;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      java.util.Map)
	 */
	@Override
	public void performInitialization(View view) {
		initializeComponent(view, view);
	}

	/**
	 * Performs initialization of a component by these steps:
	 * <ul>
	 * <li>For <code>AttributeField</code> instances, set defaults from the data
	 * dictionary.</li>
	 * <li>Invoke the initialize method on the component. Here the component can
	 * setup defaults and do other initialization that is specific to that
	 * component.</li>
	 * <li>Invoke any configured <code>ComponentInitializer</code> instances for
	 * the component.</li>
	 * <li>Call the component to get the List of components that are nested
	 * within and recursively call this method to initialize those components.</li>
	 * <li>Call custom initialize hook for service overrides</li>
	 * </ul>
	 * 
	 * <p>
	 * Note the order various initialize points are called, this can sometimes
	 * be an important factor to consider when initializing a component
	 * </p>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param component
	 *            - component instance to initialize
	 */
	protected void initializeComponent(View view, Component component) {
		if (component == null) {
			return;
		}

		// invoke component to initialize itself after properties have been set
		component.performInitialization(view);

		// for attribute fields, set defaults from dictionary entry
		if (component instanceof AttributeField) {
			initializeAttributeFieldFromDataDictionary(view, (AttributeField) component);

			// add attribute field to the view's index
			view.getViewIndex().addAttributeField((AttributeField) component);
		}

		// invoke component initializers
		for (ComponentInitializer initializer : component.getComponentInitializers()) {
			initializer.performInitialization(view, component);
		}

		// initialize nested components
		for (Component nestedComponent : component.getNestedComponents()) {
			initializeComponent(view, nestedComponent);
		}

		// invoke initialize service hook
		performCustomInitialization(view, component);
	}

	/**
	 * Sets properties of the <code>AttributeField</code> (if blank) to the
	 * corresponding attribute entry in the data dictionary
	 * 
	 * @param view
	 *            - view instance containing the field
	 * @param field
	 *            - field instance to initialize
	 */
	protected void initializeAttributeFieldFromDataDictionary(View view, AttributeField field) {
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
				&& field.getBindingInfo().isBindToModel()) {
			dictionaryAttributeName = field.getName();
			Class<?> dictionaryModelClass = ViewModelUtils.getPropertyType(view, field.getBindingInfo().getModelPath());
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
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performApplyModel(View view, Object model) {
		performComponentConditionalLogic(view, view, model);
	}

	protected void performComponentConditionalLogic(View view, Component component, Object model) {
		if (component == null) {
			return;
		}

		// invoke component to perform its conditional logic
		component.performApplyModel(view, model);

		// invoke service override hook
		performCustomApplyModel(view, component, model);

		// get components children and recursively call perform conditional
		// logic
		for (Component nestedComponent : component.getNestedComponents()) {
			performComponentConditionalLogic(view, nestedComponent, model);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#performUpdateState(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performUpdateState(View view) {

	}

	/**
	 * Hook for service overrides to perform custom initialization on the
	 * component
	 * 
	 * @param view
	 *            - view instance containing the component
	 * @param component
	 *            - component instance to initialize
	 */
	protected void performCustomInitialization(View view, Component component) {

	}

	/**
	 * Hook for service overrides to perform custom apply model logic on the
	 * component
	 * 
	 * @param view
	 *            - view instance containing the component
	 * @param component
	 *            - component instance to apply model to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	protected void performCustomApplyModel(View view, Component component, Object model) {

	}

	protected DataDictionaryService getDataDictionaryService() {
		return this.dataDictionaryService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
