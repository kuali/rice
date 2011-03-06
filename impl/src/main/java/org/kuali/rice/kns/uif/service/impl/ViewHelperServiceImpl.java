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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.modifier.ComponentModifier;
import org.kuali.rice.kns.uif.service.ViewHelperService;
import org.kuali.rice.kns.uif.util.ModelUtils;
import org.kuali.rice.kns.uif.util.ViewModelUtils;
import org.kuali.rice.kns.uif.widget.Widget;

/**
 * Default Implementation of <code>ViewHelperService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceImpl implements ViewHelperService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ViewHelperServiceImpl.class);

	private transient DataDictionaryService dataDictionaryService;

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
	 * <li>Invoke any configured <code>ComponentModifier</code> instances for
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

		LOG.debug("Initializing component: " + component.getId() + " with type: " + component.getClass());

		// invoke component to initialize itself after properties have been set
		component.performInitialization(view);

		// for attribute fields, set defaults from dictionary entry
		if (component instanceof AttributeField) {
			initializeAttributeFieldFromDataDictionary(view, (AttributeField) component);

			// add attribute field to the view's index
			view.getViewIndex().addAttributeField((AttributeField) component);
		}

		// for collection groups set defaults from dictionary entry
		if (component instanceof CollectionGroup) {
			// TODO: initialize from dictionary

			// add collection group to the view's index
			view.getViewIndex().addCollection((CollectionGroup) component);
		}

		// invoke component initializers setup to run in the initialize phase
		for (ComponentModifier initializer : component.getComponentInitializers()) {
			if (StringUtils.equals(initializer.getRunPhase(), UifConstants.ViewPhases.INITIALIZE)) {
				initializer.performModification(view, component);
			}
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
		if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
			dictionaryAttributeName = field.getPropertyName();
		}

		// if both dictionary names not given and the field is from a model,
		// determine class based on the View and use field name as attribute
		// name
		if (StringUtils.isBlank(dictionaryAttributeName) && StringUtils.isBlank(dictionaryObjectEntry)
				&& !field.getBindingInfo().isBindToForm()) {
			dictionaryAttributeName = field.getPropertyName();
			Class<?> dictionaryModelClass = getDictionaryModelClass(view, field);
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

	protected Class<?> getDictionaryModelClass(View view, AttributeField field) {
		return ViewModelUtils.getPropertyType(view, field.getBindingInfo().getBindingObjectPath());
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performApplyModel(View view, Object model) {
		performComponentApplyModel(view, view, model);
	}

	/**
	 * Applies the model data to a component of the View instance
	 * 
	 * <p>
	 * The component is invoked to to apply the model data. Here the component
	 * can generate any additional fields needed or alter the configured fields.
	 * After the component is invoked a hook for custom helper service
	 * processing is invoked. Finally the method is recursively called for all
	 * the component children
	 * </p>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param component
	 *            - the component instance the model should be applied to
	 * @param model
	 *            - top level object containing the data
	 */
	protected void performComponentApplyModel(View view, Component component, Object model) {
		if (component == null) {
			return;
		}

		// invoke component to perform its conditional logic
		component.performApplyModel(view, model);

		// invoke service override hook
		performCustomApplyModel(view, component, model);

		// invoke component initializers setup to run in the apply model phase
		for (ComponentModifier initializer : component.getComponentInitializers()) {
			if (StringUtils.equals(initializer.getRunPhase(), UifConstants.ViewPhases.APPLY_MODEL)) {
				initializer.performModification(view, component);
			}
		}

		// get components children and recursively call perform conditional
		// logic
		for (Component nestedComponent : component.getNestedComponents()) {
			performComponentApplyModel(view, nestedComponent, model);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performFinalize(View view, Object model) {
		performComponentFinalize(view, view, model);
	}

	/**
	 * Update state of the given component and does final preparation for
	 * rendering
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param component
	 *            - the component instance that should be updated
	 * @param model
	 *            - top level object containing the data
	 */
	protected void performComponentFinalize(View view, Component component, Object model) {
		if (component == null) {
			return;
		}

		// invoke component to update its state
		component.performFinalize(view, model);

		// invoke service override hook
		performCustomUpdateState(view, component, model);

		// invoke component initializers setup to run in the finalize phase
		for (ComponentModifier initializer : component.getComponentInitializers()) {
			if (StringUtils.equals(initializer.getRunPhase(), UifConstants.ViewPhases.FINALIZE)) {
				initializer.performModification(view, component);
			}
		}

		// get components children and recursively update state
		for (Component nestedComponent : component.getNestedComponents()) {
			// invoke special finalize method for widgets
			if (nestedComponent instanceof Widget) {
				((Widget) nestedComponent).performFinalize(view, model, component);
			}
			
			performComponentFinalize(view, nestedComponent, model);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#processCollectionAddLine(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, java.lang.String)
	 */
	@Override
	public void processCollectionAddLine(View view, Object model, String collectionPath) {
		// get the collection group from the view
		CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
		if (collectionGroup == null) {
			logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
		}

		// get the collection instance for adding the new line
		Collection<Object> collection = ModelUtils.getPropertyValue(model, collectionPath);
		if (collection == null) {
			logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
		}

		// now get the new line we need to add
		String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
		Object addLine = ModelUtils.getPropertyValue(model, addLinePath);
		if (addLine == null) {
			logAndThrowRuntime("Add line instance not found for path: " + addLinePath);
		}

		processBeforeAddLine(view, collectionGroup, model, addLine);

		// validate the line to make sure it is ok to add
		boolean isValidLine = performAddLineValidation(view, collectionGroup, model, addLine);
		if (isValidLine) {
			// TODO: should check to see if there is an add line method on the
			// collection parent and if so call that instead of just adding to
			// the collection (so that sequence can be set)
			collection.add(addLine);

			// make a new instance for the add line
			collectionGroup.initNewCollectionLine(model, true);
		}

		processAfterAddLine(view, collectionGroup, model, addLine);
	}

	/**
	 * Performs validation on the new collection line before it is added to the
	 * corresponding collection
	 * 
	 * @param view
	 *            - view instance that the action was taken on
	 * @param collectionGroup
	 *            - collection group component for the collection
	 * @param addLine
	 *            - new line instance to validate
	 * @param model
	 *            - object instance that contain's the views data
	 * @return boolean true if the line is valid and it should be added to the
	 *         collection, false if it was not valid and should not be added to
	 *         the collection
	 */
	protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
		boolean isValid = true;

		// TODO: this should invoke rules, sublclasses like the document view
		// should create the document add line event

		return isValid;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService#processCollectionDeleteLine(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, java.lang.String, int)
	 */
	public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex) {
		// get the collection group from the view
		CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
		if (collectionGroup == null) {
			logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
		}

		// get the collection instance for adding the new line
		Collection<Object> collection = ModelUtils.getPropertyValue(model, collectionPath);
		if (collection == null) {
			logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
		}

		// TODO: look into other ways of identifying a line so we can deal with
		// unordered collections
		if (collection instanceof List) {
			Object deleteLine = ((List<Object>) collection).get(lineIndex);

			// validate the delete action is allowed for this line
			boolean isValid = performDeleteLineValidation(view, collectionGroup, deleteLine);
			if (isValid) {
				((List<Object>) collection).remove(lineIndex);
			}
		}
		else {
			logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
		}
	}

	/**
	 * Performs validation on the collection line before it is removed from the
	 * corresponding collection
	 * 
	 * @param view
	 *            - view instance that the action was taken on
	 * @param collectionGroup
	 *            - collection group component for the collection
	 * @param deleteLine
	 *            - line that will be removed
	 * @return boolean true if the action is allowed and the line should be
	 *         removed, false if the line should not be removed
	 */
	protected boolean performDeleteLineValidation(View view, CollectionGroup collectionGroup, Object deleteLine) {
		boolean isValid = true;

		// TODO: this should invoke rules, sublclasses like the document view
		// should create the document delete line event

		return isValid;
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

	/**
	 * Hook for service overrides to perform custom updating of state
	 * 
	 * @param view
	 *            - view instance containing the component
	 * @param component
	 *            - component instance to update
	 * @param model
	 *            - Top level object containing the data
	 */
	protected void performCustomUpdateState(View view, Component component, Object model) {

	}

	/**
	 * Hook for service overrides to process the new collection line before it
	 * is added to the collection
	 * 
	 * @param view
	 *            - view instance that is being presented (the action was taken
	 *            on)
	 * @param collectionGroup
	 *            - collection group component for the collection the line will
	 *            be added to
	 * @param model
	 *            - object instance that contain's the views data
	 * @param addLine
	 *            - the new line instance to be processed
	 */
	protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

	}

	/**
	 * Hook for service overrides to process the new collection line after it
	 * has been added to the collection
	 * 
	 * @param view
	 *            - view instance that is being presented (the action was taken
	 *            on)
	 * @param collectionGroup
	 *            - collection group component for the collection the line that
	 *            was added
	 * @param model
	 *            - object instance that contain's the views data
	 * @param addLine
	 *            - the new line that was added
	 */
	protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

	}

	protected void logAndThrowRuntime(String message) {
		LOG.error(message);
		throw new RuntimeException(message);
	}

	protected DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
