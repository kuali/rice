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
package org.kuali.rice.krad.uif.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;

/**
 * Field that contains a nested <code>Group</code>. Can be used to group
 * together fields by providing a group without header and footer, or simply to
 * nest full groups. The items getter/setter provided is for convenience and
 * will set the items <code>List</code> in the nested <code>Group</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GroupField extends FieldBase {
	private static final long serialVersionUID = -505654043702442196L;

	private Group group;

	public GroupField() {
		super();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the align on group if empty and the align has been set on the
	 * field</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performInitialization(org.kuali.rice.krad.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (StringUtils.isNotBlank(getAlign()) && group != null) {
			group.setAlign(getAlign());
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(group);

		return components;
	}

	/**
	 * <code>Group</code> instance that is contained within in the field
	 * 
	 * @return Group instance
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * Setter for the field's nested group
	 * 
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * List of <code>Component</code> instances contained in the nested group
	 * 
	 * <p>
	 * Convenience method for configuration to get the items List from the
	 * field's nested group
	 * </p>
	 * 
	 * @return List<? extends Component> items
	 */
	public List<? extends Component> getItems() {
		if (group != null) {
			return group.getItems();
		}

		return null;
	}

	/**
	 * Setter for the field's nested group items
	 * 
	 * <p>
	 * Convenience method for configuration to set the items List for the
	 * field's nested group
	 * </p>
	 * 
	 * @param items
	 */
	public void setItems(List<? extends Component> items) {
		if (group != null) {
			group.setItems(items);
		}
	}

}
