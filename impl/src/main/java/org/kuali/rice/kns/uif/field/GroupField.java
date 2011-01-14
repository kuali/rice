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
package org.kuali.rice.kns.uif.field;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;

/**
 * Field that contains a nested <code>Group</code>. Can be used to group
 * together fields by providing a group without header and footer, or simply to
 * nest full groups. The items <code>List</code> provided is for convenience and
 * will set the items <code>List</code> in the nested <code>Group</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GroupField extends FieldLabelBase {
	private Group group;

	private List<Component> items;

	public GroupField() {
		items = new ArrayList<Component>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Sets the items on the group (if empty) from the field's items list
	 * (if not empty)</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (group != null) {
			if (!items.isEmpty() && group.getItems().isEmpty()) {
				group.setItems(items);
			}
		}
	}
	
	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(group);

		return components;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Component> getItems() {
		return this.items;
	}

	public void setItems(List<Component> items) {
		this.items = items;
	}

}
