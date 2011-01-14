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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.LabelField;
import org.kuali.rice.kns.uif.util.ViewModelUtils;

/**
 * Group that holds a collection of objects each containing a group of
 * <code>Field</code> instances
 * 
 * <p>
 * Based on the fields defined, the <code>CollectionGroup</code> will
 * dynamically create instances of the fields for each collection row. In
 * addition, the group can create standard fields like the action and sequencer
 * fields for each row. The footer <code>Group</code> inherited from the base is
 * used to presented for the the entire collection (possibly for actions that
 * apply to the collection as a whole).
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

	private String bindingPath;
	private String modelName;

	private boolean includeAddLine;

	private boolean renderActionFields;
	private List<ActionField> actionFields;

	private boolean renderSequenceField;
	private AttributeField sequenceField;

	private boolean repeatHeader;
	private List<LabelField> headerFields;

	private Group rowGroupTemplate;
	private List<Group> tableGroups;

	private List<CollectionGroup> subCollections;

	public CollectionGroup() {
		includeAddLine = true;
		renderActionFields = true;
		renderSequenceField = false;
		repeatHeader = false;

		headerFields = new ArrayList<LabelField>();
		tableGroups = new ArrayList<Group>();
		subCollections = new ArrayList<CollectionGroup>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#performConditionalLogic(org.kuali.rice.kns.uif.container.View,
	 *      java.util.Map)
	 */
	@Override
	public void performConditionalLogic(View view, Map<String, Object> models) {
		super.performConditionalLogic(view, models);

		// the list that hold the generated items and become the final list of
		// items for the group
		List<Component> collectionItems = new ArrayList<Component>();

		// get the collection for this group from the model
		Collection<Object> modelCollection = ViewModelUtils.getCollectionFromModels(models, this);

		// for each collection row create a new group from the template
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.addAll(subCollections);

		return components;
	}

	public Class<?> getCollectionObjectClass() {
		return this.collectionObjectClass;
	}

	public void setCollectionObjectClass(Class<?> collectionObjectClass) {
		this.collectionObjectClass = collectionObjectClass;
	}

	public String getBindingPath() {
		return this.bindingPath;
	}

	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public boolean isIncludeAddLine() {
		return this.includeAddLine;
	}

	public void setIncludeAddLine(boolean includeAddLine) {
		this.includeAddLine = includeAddLine;
	}

	public boolean isRenderActionFields() {
		return this.renderActionFields;
	}

	public void setRenderActionFields(boolean renderActionFields) {
		this.renderActionFields = renderActionFields;
	}

	public List<ActionField> getActionFields() {
		return this.actionFields;
	}

	public void setActionFields(List<ActionField> actionFields) {
		this.actionFields = actionFields;
	}

	public boolean isRenderSequenceField() {
		return this.renderSequenceField;
	}

	public void setRenderSequenceField(boolean renderSequenceField) {
		this.renderSequenceField = renderSequenceField;
	}

	public AttributeField getSequenceField() {
		return this.sequenceField;
	}

	public void setSequenceField(AttributeField sequenceField) {
		this.sequenceField = sequenceField;
	}

	public boolean isRepeatHeader() {
		return this.repeatHeader;
	}

	public void setRepeatHeader(boolean repeatHeader) {
		this.repeatHeader = repeatHeader;
	}

	public List<LabelField> getHeaderFields() {
		return this.headerFields;
	}

	public void setHeaderFields(List<LabelField> headerFields) {
		this.headerFields = headerFields;
	}

	public Group getRowGroupTemplate() {
		return this.rowGroupTemplate;
	}

	public void setRowGroupTemplate(Group rowGroupTemplate) {
		this.rowGroupTemplate = rowGroupTemplate;
	}

	public List<Group> getTableGroups() {
		return this.tableGroups;
	}

	public void setTableGroups(List<Group> tableGroups) {
		this.tableGroups = tableGroups;
	}

	public List<CollectionGroup> getSubCollections() {
		return this.subCollections;
	}

	public void setSubCollections(List<CollectionGroup> subCollections) {
		this.subCollections = subCollections;
	}

}
