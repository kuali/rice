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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UifConstants.Orientation;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ModelUtils;
import org.kuali.rice.kns.uif.util.ViewModelUtils;

/**
 * Layout manager that works with <code>CollectionGroup</code> containers and
 * renders the collection lines in a vertical row
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StackedLayoutManager extends BoxLayoutManager {
	private String summaryTitle;
	private List<String> summaryFields;

	private Group lineGroupPrototype;

	private List<Group> collectionGroups;

	public StackedLayoutManager() {
		super();

		setOrientation(Orientation.VERTICAL);

		summaryFields = new ArrayList<String>();
		collectionGroups = new ArrayList<Group>();
	}

	/**
	 * Builds up the table by creating a row for each collection line
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performConditionalLogic(org.kuali.rice.kns.uif.container.View,
	 *      java.util.Map, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performConditionalLogic(View view, Map<String, Object> models, Container container) {
		super.performConditionalLogic(view, models, container);

		if (!(container instanceof CollectionGroup)) {
			throw new IllegalArgumentException(
					"Only CollectionGroup containers supported by the TableLayoutManager, found container class: "
							+ container.getClass());
		}

		CollectionGroup collectionGroup = (CollectionGroup) container;

		collectionGroups = new ArrayList<Group>();

		// get the collection for this group from the model
		List<Object> modelCollection = ViewModelUtils.getCollectionFromModels(models, collectionGroup);

		// for each collection row create a new Group
		for (int index = 0; index < modelCollection.size(); index++) {
			Group lineGroup = ComponentUtils.copy(lineGroupPrototype);

			String lineHeaderText = buildLineHeaderText(modelCollection.get(index));
			lineGroup.getHeader().setHeaderText(lineHeaderText);

			// copy fields adding the collection line to their binding prefix
			String bindingPathPrefix = collectionGroup.getBindingPath() + "[" + index + "]";
			List<Field> lineFields = ComponentUtils.copyFieldListAndPrefix((List<Field>) collectionGroup.getItems(),
					bindingPathPrefix);

			lineGroup.setItems(lineFields);

			// create group footer containing the action fields
			List<ActionField> lineActions = collectionGroup.getLineActions(index);
			lineGroup.getFooter().setItems(lineActions);
			
			// refresh the group's layout manager
			lineGroup.getLayoutManager().refresh(view, lineGroup);

			collectionGroups.add(lineGroup);
		}
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

	public String getSummaryTitle() {
		return this.summaryTitle;
	}

	public void setSummaryTitle(String summaryTitle) {
		this.summaryTitle = summaryTitle;
	}

	public List<String> getSummaryFields() {
		return this.summaryFields;
	}

	public void setSummaryFields(List<String> summaryFields) {
		this.summaryFields = summaryFields;
	}

	public Group getLineGroupPrototype() {
		return this.lineGroupPrototype;
	}

	public void setLineGroupPrototype(Group lineGroupPrototype) {
		this.lineGroupPrototype = lineGroupPrototype;
	}

	public List<Group> getCollectionGroups() {
		return this.collectionGroups;
	}

	public void setCollectionGroups(List<Group> collectionGroups) {
		this.collectionGroups = collectionGroups;
	}

}
