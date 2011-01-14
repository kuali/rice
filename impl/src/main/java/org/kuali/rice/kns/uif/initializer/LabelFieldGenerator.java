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
package org.kuali.rice.kns.uif.initializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.FieldLabelBase;

/**
 * <code>ComponentInitializer</code> that will pull a contained
 * <code>LabelField</code> from another Field and add to the container. This
 * allows the Fields to be rendered separately within a
 * <code>LayoutManager</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabelFieldGenerator implements ComponentInitializer {

	/**
	 * Modifies the List of fields within the <code>Group</code>. If the
	 * contained field has a LabelField, it will be pulled out and added to the
	 * Groups components separately. The <code>LabelField</code> is placed
	 * immediately before the Field it was pulled from in the Groups component
	 * List.
	 * 
	 * @see org.kuali.rice.kns.uif.initializer.ComponentInitializer#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.Component)
	 */
	@Override
	public void performInitialization(View view, Component component) {
		Group group = null;
		if (component instanceof Group) {
			group = (Group) component;
		}
		else {
			throw new IllegalArgumentException("Component must be a Group instance in order to use LabelFieldGenerator");
		}

		List<Component> allItems = new ArrayList<Component>();
		for (Component field : group.getItems()) {
			if (field instanceof FieldLabelBase) {
				FieldLabelBase fieldLabel = (FieldLabelBase) field;
				if (fieldLabel.getLabelField() != null && fieldLabel.isShowLabel()) {
					allItems.add(fieldLabel.getLabelField());

					// set boolean to indicate label field should not be
					// rendered with the attribute
					fieldLabel.setLabelFieldRendered(true);
				}
			}

			allItems.add(field);
		}

		// update Group
		group.setItems(allItems);
	}

	/**
	 * @see org.kuali.rice.kns.uif.initializer.ComponentInitializer#getSupportedComponents()
	 */
	@Override
	public Set<Class> getSupportedComponents() {
		Set<Class> supportedComponents = new HashSet<Class>();

		supportedComponents.add(Group.class);

		return supportedComponents;
	}

}
