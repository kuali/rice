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
package org.kuali.rice.kns.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.UifPropertyPaths;
import org.kuali.rice.kns.uif.UifConstants.IdSuffixes;
import org.kuali.rice.kns.uif.UifConstants.Orientation;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ModelUtils;

/**
 * Layout manager that works with <code>CollectionGroup</code> containers and
 * renders the collection lines in a vertical row
 * 
 * <p>
 * For each line of the collection, a <code>Group</code> instance is created.
 * The group header contains a label for the line (summary information), the
 * group fields are the collection line fields, and the group footer contains
 * the line actions. All the groups are rendered using the
 * <code>BoxLayoutManager</code> with vertical orientation.
 * </p>
 * 
 * <p>
 * Modify the lineGroupPrototype to change header/footer styles or any other
 * customization for the line groups
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StackedLayoutManager extends BoxLayoutManager {
	private String summaryTitle;
	private List<String> summaryFields;

	private Group lineGroupPrototype;

	private List<Group> stackedGroups;

	public StackedLayoutManager() {
		super();

		setOrientation(Orientation.VERTICAL);

		summaryFields = new ArrayList<String>();
		stackedGroups = new ArrayList<Group>();
	}

	/**
	 * Builds a <code>Group</code> for each collection line (including the add
	 * line if enabled). The groups are added to the stackedGroups
	 * <code>List</code> and picked up by the template.
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performApplyModel(View view, Object model, Container container) {
		super.performApplyModel(view, model, container);

		if (!(container instanceof CollectionGroup)) {
			throw new IllegalArgumentException(
					"Only CollectionGroup containers supported by the TableLayoutManager, found container class: "
							+ container.getClass());
		}

		CollectionGroup collectionGroup = (CollectionGroup) container;

		stackedGroups = new ArrayList<Group>();

		// create add line
		if (collectionGroup.isRenderAddLine()) {
			buildAddLine(view, collectionGroup, model);
		}

		// get the collection for this group from the model
		List<Object> modelCollection = (List<Object>) ModelUtils.getPropertyValue(model,
				((DataBinding) collectionGroup).getBindingInfo().getBindingPath());

		// for each collection row create a new Group
		if (modelCollection != null) {
			for (int index = 0; index < modelCollection.size(); index++) {
				String lineHeaderText = buildLineHeaderText(modelCollection.get(index));
				String bindingPathPrefix = collectionGroup.getBindingInfo().getBindingName() + "[" + index + "]";
				String idSuffix = "_" + index;

				buildLine(view, collectionGroup, lineHeaderText, bindingPathPrefix, idSuffix,
						collectionGroup.getLineActions(index), false);
			}
		}
	}

	/**
	 * Builds the add line Group
	 * 
	 * <p>
	 * Group header is set to the add line label given on the collection group
	 * (org.kuali.rice.kns.uif.container.CollectionGroup.getAddLineLabel()).
	 * Also the binding is setup for the add line and if the managed by the
	 * framework (bound to generic form Map) then the Map entry is initialized.
	 * Finally the add line actions are set in the group footer
	 * </p>
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param model
	 *            - Object containing the view data, should extend UifFormBase
	 *            if using framework managed new lines
	 */
	protected void buildAddLine(View view, CollectionGroup collectionGroup, Object model) {
		// determine how the new line should be managed and the binding path
		String addLineBindingPath = "";
		boolean bindAddLineToForm = false;
		if (StringUtils.isNotBlank(collectionGroup.getAddLineName())) {
			// managed in model
			addLineBindingPath = collectionGroup.getAddLineBindingInfo().getBindingPath();
		}
		else {
			// managed with framework, generic map on form
			addLineBindingPath = collectionGroup.initNewCollectionLine(model, false);
			collectionGroup.getAddLineBindingInfo().setBindingPath(addLineBindingPath);
			bindAddLineToForm = true;
		}

		buildLine(view, collectionGroup, collectionGroup.getAddLineLabel(), addLineBindingPath, IdSuffixes.ADD_LINE,
				collectionGroup.getAddLineActions(), bindAddLineToForm);
	}

	/**
	 * Builds a <code>Group</code> instance for a collection line. A new
	 * instance of the lineGroupPrototype is created for the lines group. The
	 * original items specified on the <code>CollectionGroup</code> are also
	 * copied and adjusted as necessary for the line (binding path, id)
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param headerText
	 *            - text to be used for the group header
	 * @param bindingPath
	 *            - binding path for the groups items (if DataBinding)
	 * @param idSuffix
	 *            - suffix to use for the groups items (in order to maintain
	 *            unique ids)
	 * @param actions
	 *            - List of actions to set in the groups footer
	 * @param bindToForm
	 *            - whether the bindToForm property on the items bindingInfo
	 *            should be set to true (needed for add line)
	 */
	protected void buildLine(View view, CollectionGroup collectionGroup, String headerText, String bindingPath,
			String idSuffix, List<ActionField> actions, boolean bindToForm) {
		Group lineGroup = ComponentUtils.copy(lineGroupPrototype);

		lineGroup.getHeader().setHeaderText(headerText);

		List<Field> lineFields = ComponentUtils.copyFieldList((List<Field>) collectionGroup.getItems(), bindingPath);
		if (bindToForm) {
			ComponentUtils.setComponentsPropertyDeep(lineFields, UifPropertyPaths.BIND_TO_FORM, new Boolean(true));
		}
		view.getViewIndex().addFields(lineFields);

		lineGroup.setItems(lineFields);

		// set line actions on group footer
		lineGroup.getFooter().setItems(actions);

		// refresh the group's layout manager
		lineGroup.getLayoutManager().refresh(view, lineGroup);

		// suffix all the groups ids so they will be unique
		ComponentUtils.updateIds(lineGroup, idSuffix);

		stackedGroups.add(lineGroup);
	}

	/**
	 * Builds the header text for the collection line
	 * 
	 * <p>
	 * Header text is built up by first the collection label, either specified
	 * on the collection definition or retrieved from the dictionary. Then for
	 * each summary field defined, the value from the model is retrieved and
	 * added to the header.
	 * </p>
	 * 
	 * @param line
	 *            - Collection line containing data
	 * @return String header text for line
	 */
	protected String buildLineHeaderText(Object line) {
		String summaryFieldString = "";
		for (String summaryField : summaryFields) {
			Object summaryFieldValue = ModelUtils.getPropertyValue(line, summaryField);
			if (StringUtils.isNotBlank(summaryFieldString)) {
				summaryFieldString += " - ";
			}

			if (summaryFieldValue != null) {
				summaryFieldString += summaryFieldValue;
			}
			else {
				summaryFieldString += "Null";
			}
		}

		String headerText = summaryTitle;
		if (StringUtils.isNotBlank(summaryFieldString)) {
			headerText += " ( " + summaryFieldString + " )";
		}

		return headerText;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return CollectionGroup.class;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(lineGroupPrototype);

		return components;
	}

	/**
	 * Text to appears in the header for each collection lines Group. Used in
	 * conjunction with {@link #getSummaryFields()} to build up the final header
	 * text
	 * 
	 * @return String summary title text
	 */
	public String getSummaryTitle() {
		return this.summaryTitle;
	}

	/**
	 * Setter for the summary title text
	 * 
	 * @param summaryTitle
	 */
	public void setSummaryTitle(String summaryTitle) {
		this.summaryTitle = summaryTitle;
	}

	/**
	 * List of attribute names from the collection line class that should be
	 * used to build the line summary. To build the summary the value for each
	 * attribute is retrieved from the line instance. All the values are then
	 * placed together with a separator.
	 * 
	 * @return List<String> summary field names
	 * @see #buildLineHeaderText(java.lang.Object)
	 */
	public List<String> getSummaryFields() {
		return this.summaryFields;
	}

	/**
	 * Setter for the summary field name list
	 * 
	 * @param summaryFields
	 */
	public void setSummaryFields(List<String> summaryFields) {
		this.summaryFields = summaryFields;
	}

	/**
	 * Group instance that is used as a prototype for creating the collection
	 * line groups. For each line a copy of the prototype is made and then
	 * adjusted as necessary
	 * 
	 * @return Group instance to use as prototype
	 */
	public Group getLineGroupPrototype() {
		return this.lineGroupPrototype;
	}

	/**
	 * Setter for the line group prototype
	 * 
	 * @param lineGroupPrototype
	 */
	public void setLineGroupPrototype(Group lineGroupPrototype) {
		this.lineGroupPrototype = lineGroupPrototype;
	}

	/**
	 * Final <code>List</code> of Groups to render for the collection
	 * 
	 * @return List<Group> collection groups
	 */
	public List<Group> getStackedGroups() {
		return this.stackedGroups;
	}

	/**
	 * Setter for the collection groups
	 * 
	 * @param stackedGroups
	 */
	public void setStackedGroups(List<Group> stackedGroups) {
		this.stackedGroups = stackedGroups;
	}

}
