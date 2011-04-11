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
package org.kuali.rice.kns.uif.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.UifPropertyPaths;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.core.DataBinding;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.field.GroupField;
import org.kuali.rice.kns.uif.layout.CollectionLayoutManager;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.UifFormBase;

/**
 * Builds out the <code>Field</code> instances for a collection group with a
 * series of steps that interact with the configured
 * <code>CollectionLayoutManager</code> to assemble the fields as necessary for
 * the layout
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupBuilder implements Serializable {
	private static final long serialVersionUID = -4762031957079895244L;

	/**
	 * Creates the <code>Field</code> instances that make up the table
	 * 
	 * <p>
	 * The corresponding collection is retrieved from the model and iterated
	 * over to create the necessary fields. The binding path for fields that
	 * implement <code>DataBinding</code> is adjusted to point to the collection
	 * line it is apart of. For example, field 'number' of collection 'accounts'
	 * for line 1 will be set to 'accounts[0].number', and for line 2
	 * 'accounts[1].number'. Finally parameters are set on the line's action
	 * fields to indicate what collection and line they apply to.
	 * </p>
	 * 
	 * @param view
	 *            - View instance the collection belongs to
	 * @param model
	 *            - Top level object containing the data
	 * @param collectionGroup
	 *            - CollectionGroup component for the collection
	 */
	public void build(View view, Object model, CollectionGroup collectionGroup) {
		// create add line
		if (collectionGroup.isRenderAddLine() && !collectionGroup.isReadOnly()) {
			buildAddLine(view, model, collectionGroup);
		}

		// get the collection for this group from the model
		List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model, ((DataBinding) collectionGroup)
				.getBindingInfo().getBindingPath());

		// for each collection row create a new Group
		if (modelCollection != null) {
			for (int index = 0; index < modelCollection.size(); index++) {
				String bindingPathPrefix = collectionGroup.getBindingInfo().getBindingName() + "[" + index + "]";
				if (StringUtils.isNotBlank(collectionGroup.getBindingInfo().getBindByNamePrefix())) {
					bindingPathPrefix = collectionGroup.getBindingInfo().getBindByNamePrefix() + "."
							+ bindingPathPrefix;
				}

				Object currentLine = modelCollection.get(index);

				List<ActionField> actions = getLineActions(view, model, collectionGroup, currentLine, index);
				buildLine(view, model, collectionGroup, bindingPathPrefix, actions, false, currentLine, index);
			}
		}
	}

	/**
	 * Builds the fields for holding the collection add line and if necessary
	 * makes call to setup the new line instance
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param model
	 *            - Object containing the view data, should extend UifFormBase
	 *            if using framework managed new lines
	 */
	protected void buildAddLine(View view, Object model, CollectionGroup collectionGroup) {
		boolean bindAddLineToForm = false;

		// determine whether the add line binds to the generic form map or a
		// specified property
		if (StringUtils.isBlank(collectionGroup.getAddLinePropertyName())) {
			initializeNewCollectionLine(view, model, collectionGroup, false);
			bindAddLineToForm = true;

			// set binding path for add line
			String collectionAddLineKey = WebUtils.translateToMapSafeKey(collectionGroup.getBindingInfo()
					.getBindingPath());
			String addLineBindingPath = UifPropertyPaths.NEW_COLLECTION_LINES + "['" + collectionAddLineKey + "']";
			collectionGroup.getAddLineBindingInfo().setBindingPath(addLineBindingPath);
		}

		String addLineBindingPath = collectionGroup.getAddLineBindingInfo().getBindingPath();
		List<ActionField> actions = getAddLineActions(view, model, collectionGroup);

		buildLine(view, model, collectionGroup, addLineBindingPath, actions, bindAddLineToForm, null, -1);
	}

	/**
	 * Builds the field instances for the collection line. A copy of the
	 * configured items on the <code>CollectionGroup</code> is made and adjusted
	 * for the line (id and binding). Then a call is made to the
	 * <code>CollectionLayoutManager</code> to assemble the line as necessary
	 * for the layout
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param model
	 *            - top level object containing the data
	 * @param collectionGroup
	 *            - collection group component for the collection
	 * @param bindingPath
	 *            - binding path for the line fields (if DataBinding)
	 * @param actions
	 *            - List of actions to set in the lines action column
	 * @param bindLineToForm
	 *            - whether the bindToForm property on the items bindingInfo
	 *            should be set to true (needed for add line)
	 * @param currentLine
	 *            - object instance for the current line, or null if add line
	 * @param lineIndex
	 *            - index of the line in the collection, or -1 if we are
	 *            building the add line
	 */
	@SuppressWarnings("unchecked")
	protected void buildLine(View view, Object model, CollectionGroup collectionGroup, String bindingPath,
			List<ActionField> actions, boolean bindToForm, Object currentLine, int lineIndex) {
		CollectionLayoutManager layoutManager = (CollectionLayoutManager) collectionGroup.getLayoutManager();

		// copy group items for new line
		List<Field> lineFields = (List<Field>) ComponentUtils.copyFieldList(collectionGroup.getItems(), bindingPath);
		ComponentUtils.updateContextsForLine(lineFields, currentLine, lineIndex);

		if (bindToForm) {
			ComponentUtils.setComponentsPropertyDeep(lineFields, UifPropertyPaths.BIND_TO_FORM, new Boolean(true));
		}

		// add generated fields to the view's index
		view.getViewIndex().addFields(lineFields);

		// if not add line build sub-collection field groups
		List<GroupField> subCollectionFields = new ArrayList<GroupField>();
		if ((lineIndex != -1) && (collectionGroup.getSubCollections() != null)) {
			for (int subLineIndex = 0; subLineIndex < collectionGroup.getSubCollections().size(); subLineIndex++) {
				CollectionGroup subCollectionPrototype = collectionGroup.getSubCollections().get(subLineIndex);
				CollectionGroup subCollectionGroup = ComponentUtils.copy(subCollectionPrototype);

				subCollectionGroup.getBindingInfo().setBindByNamePrefix(bindingPath);
				subCollectionGroup.getAddLineBindingInfo().setBindByNamePrefix(bindingPath);

				GroupField groupFieldPrototype = layoutManager.getSubCollectionGroupFieldPrototype();
				GroupField subCollectionGroupField = ComponentUtils.copy(groupFieldPrototype);
				subCollectionGroupField.setGroup(subCollectionGroup);
				
				view.getViewIndex().addCollection(subCollectionGroup);

				subCollectionFields.add(subCollectionGroupField);
			}
		}

		// invoke layout manager to build the complete line
		layoutManager.buildLine(view, model, collectionGroup, lineFields, subCollectionFields, bindingPath, actions,
				"_" + lineIndex, currentLine, lineIndex);
	}

	/**
	 * Creates new <code>ActionField</code> instances for the line
	 * 
	 * <p>
	 * Adds context to the action fields for the given line so that the line the
	 * action was performed on can be determined when that action is selected
	 * </p>
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param model
	 *            - top level object containing the data
	 * @param collectionGroup
	 *            - collection group component for the collection
	 * @param collectionLine
	 *            - object instance for the current line
	 * @param lineIndex
	 *            - index of the line the actions should apply to
	 */
	protected List<ActionField> getLineActions(View view, Object model, CollectionGroup collectionGroup,
			Object collectionLine, int lineIndex) {
		List<ActionField> lineActions = ComponentUtils.copyFieldList(collectionGroup.getActionFields());
		for (ActionField actionField : lineActions) {
			actionField.addActionParameter(UifParameters.SELLECTED_COLLECTION_PATH, collectionGroup.getBindingInfo()
					.getBindingPath());
			actionField.addActionParameter(UifParameters.SELECTED_LINE_INDEX, Integer.toString(lineIndex));
		}

		ComponentUtils.updateContextsForLine(lineActions, collectionLine, lineIndex);

		return lineActions;
	}

	/**
	 * Creates new <code>ActionField</code> instances for the add line
	 * 
	 * <p>
	 * Adds context to the action fields for the add line so that the collection
	 * the action was performed on can be determined
	 * </p>
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param model
	 *            - top level object containing the data
	 * @param collectionGroup
	 *            - collection group component for the collection
	 */
	protected List<ActionField> getAddLineActions(View view, Object model, CollectionGroup collectionGroup) {
		List<ActionField> lineActions = ComponentUtils.copyFieldList(collectionGroup.getAddLineActionFields());
		for (ActionField actionField : lineActions) {
			actionField.addActionParameter(UifParameters.SELLECTED_COLLECTION_PATH, collectionGroup.getBindingInfo()
					.getBindingPath());
		}

		// get add line for context
		String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
		Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);

		ComponentUtils.updateContextsForLine(lineActions, addLine, -1);

		return lineActions;
	}

	/**
	 * Initializes a new instance of the collection class (configured on the
	 * collection group) in the UifFormBase generic newCollectionLines Map
	 * 
	 * @see org.kuali.rice.kns.uif.container.CollectionGroup.
	 *      initializeNewCollectionLine(View, Object, CollectionGroup, boolean)
	 */
	public void initializeNewCollectionLine(View view, Object model, CollectionGroup collectionGroup,
			boolean clearExistingLine) {
		if (!(model instanceof UifFormBase)) {
			throw new RuntimeException("Cannot create new collection line for group: "
					+ collectionGroup.getPropertyName() + ". Model does not extend " + UifFormBase.class.getName());
		}

		// get new collection line map from form
		Map<String, Object> newCollectionLines = ObjectPropertyUtils.getPropertyValue(model,
				UifPropertyPaths.NEW_COLLECTION_LINES);
		if (newCollectionLines == null) {
			newCollectionLines = new HashMap<String, Object>();
			ObjectPropertyUtils.setPropertyValue(model, UifPropertyPaths.NEW_COLLECTION_LINES, newCollectionLines);
		}

		// if there is not an instance available or we need to clear create a
		// new instance
		BindingInfo bindingInfo = collectionGroup.getBindingInfo();
		String newCollectionLineKey = WebUtils.translateToMapSafeKey(bindingInfo.getBindingPath());
		if (!newCollectionLines.containsKey(newCollectionLineKey)
				|| (newCollectionLines.get(newCollectionLineKey) == null) || clearExistingLine) {
			// create new instance of the collection type for the add line
			try {
				Object newLineInstance = collectionGroup.getCollectionObjectClass().newInstance();
				newCollectionLines.put(newCollectionLineKey, newLineInstance);
			}
			catch (Exception e) {
				throw new RuntimeException("Cannot create new add line instance for group: "
						+ collectionGroup.getPropertyName() + " with collection class: "
						+ collectionGroup.getCollectionObjectClass().getName());
			}
		}
	}

}
