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
package org.kuali.rice.kns.ui.service.impl;

import java.util.Map;

import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.container.Group;
import org.kuali.rice.kns.ui.container.View;
import org.kuali.rice.kns.ui.field.Field;
import org.kuali.rice.kns.ui.field.GroupField;
import org.kuali.rice.kns.ui.service.ViewLifecycleService;

/**
 * Default Implementation of <code>ViewLifecycleService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecycleServiceImpl implements ViewLifecycleService {

	/**
	 * @see org.kuali.rice.kns.ui.service.ViewLifecycleService#performInitialization(org.kuali.rice.kns.ui.container.View,
	 *      java.util.Map)
	 */
	@Override
	public void performInitialization(View view, Map<String, String> options) {
		for (Component component : view.getItems()) {
			initializeGroup((Group) component, options);
		}

		initializeNavigation(view.getNavigation(), options);
		initializeField(view.getHeader(), options);
		initializeGroup(view.getFooter(), options);

		view.initialize(options);
	}

	protected void initializeGroup(Group group, Map<String, String> options) {
		if (group == null) {
			return;
		}

		for (Component component : group.getItems()) {
			if (component instanceof Group) {
				initializeGroup((Group) component, options);
			}
			else {
				initializeField((Field) component, options);
			}
		}

		initializeField(group.getHeader(), options);
		initializeGroup(group.getFooter(), options);

		group.initialize(options);
	}

	protected void initializeField(Field field, Map<String, String> options) {
		if (field == null) {
			return;
		}

		field.initialize(options);

		if (field instanceof GroupField) {
			GroupField groupField = (GroupField) field;
			initializeGroup(groupField.getGroup(), options);
		}
	}

	protected void initializeNavigation(Group navigation, Map<String, String> options) {
		if (navigation == null) {
			return;
		}

		navigation.initialize(options);
	}

}
