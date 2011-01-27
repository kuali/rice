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
package org.kuali.rice.kns.uif.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.BindingInfo;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.UifConstants.ActionParameterNames;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.util.ComponentUtils;

/**
 * Group that holds a collection of objects and configuration for presenting the
 * collection in the UI. Supports functionality such as add line, line actions,
 * and nested collections.
 * 
 * <p>
 * Note the standard header/footer can be used to give a header to the
 * collection as a whole, or to provide actions that apply to the entire
 * collection
 * </p>
 * 
 * <p>
 * For binding purposes the binding path of each row field is indexed. The name
 * property inherited from <code>ComponentBase</code> is used as the collection
 * name. The collectionObjectClass property is used to lookup attributes from
 * the data dictionary.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroup extends Group implements DataBinding {
	private Class<?> collectionObjectClass;

	private BindingInfo bindingInfo;

	private boolean renderAddLine;
	private boolean renderLineActions;

	private List<ActionField> actionFields;
	private List<CollectionGroup> subCollections;

	public CollectionGroup() {
		renderAddLine = true;
		renderLineActions = true;

		actionFields = new ArrayList<ActionField>();
		subCollections = new ArrayList<CollectionGroup>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set defaults for binding</li>
	 * <li>Sets the dictionary entry (if blank) on each of the items to the
	 * collection class</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);
		
		if (bindingInfo != null) {
			bindingInfo.setDefaults(view, this);
		}

		for (Component item : getItems()) {
			if (item instanceof AttributeField) {
				AttributeField field = (AttributeField) item;
				
				if (StringUtils.isBlank(field.getDictionaryObjectEntry())) {
					field.setDictionaryObjectEntry(collectionObjectClass.getName());
				}
			}
		}
	}

	/**
	 * Creates new <code>ActionField</code> instances for the line
	 * 
	 * <p>
	 * Adds context to the action fields for the given line so that the line the
	 * action was performed on can be determined when that action is selected
	 * </p>
	 * 
	 * @param lineIndex
	 *            - index of the line the actions should apply to
	 */
	public List<ActionField> getLineActions(int lineIndex) {
		List<ActionField> lineActions = ComponentUtils.copyFieldList(actionFields);
		for (ActionField actionField : lineActions) {
			actionField.addActionParameter(ActionParameterNames.selectedCollectionName, getName());
			actionField.addActionParameter(ActionParameterNames.selectedLineIndex, Integer.toString(lineIndex));
		}

		return lineActions;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.addAll(subCollections);
		components.addAll(actionFields);

		return components;
	}

	public Class<?> getCollectionObjectClass() {
		return this.collectionObjectClass;
	}

	public void setCollectionObjectClass(Class<?> collectionObjectClass) {
		this.collectionObjectClass = collectionObjectClass;
	}

	public BindingInfo getBindingInfo() {
		return this.bindingInfo;
	}

	public void setBindingInfo(BindingInfo bindingInfo) {
		this.bindingInfo = bindingInfo;
	}

	public boolean isRenderAddLine() {
		return this.renderAddLine;
	}

	public void setRenderAddLine(boolean renderAddLine) {
		this.renderAddLine = renderAddLine;
	}

	public List<ActionField> getActionFields() {
		return this.actionFields;
	}

	public void setActionFields(List<ActionField> actionFields) {
		this.actionFields = actionFields;
	}

	public boolean isRenderLineActions() {
		return this.renderLineActions;
	}

	public void setRenderLineActions(boolean renderLineActions) {
		this.renderLineActions = renderLineActions;
	}

	public List<CollectionGroup> getSubCollections() {
		return this.subCollections;
	}

	public void setSubCollections(List<CollectionGroup> subCollections) {
		this.subCollections = subCollections;
	}

}
