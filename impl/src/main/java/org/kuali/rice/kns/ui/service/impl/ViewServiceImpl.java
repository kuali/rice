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

import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.container.Group;
import org.kuali.rice.kns.ui.container.View;
import org.kuali.rice.kns.ui.field.Field;
import org.kuali.rice.kns.ui.field.GroupField;
import org.kuali.rice.kns.ui.service.ViewService;

/**
 * @see org.kuali.rice.kns.ui.service.ViewService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewServiceImpl implements ViewService {
	protected DataDictionaryService dataDictionaryService;

	/**
	 * @see org.kuali.rice.kns.ui.service.ViewService#getViewById(java.lang.String)
	 */
	public View getViewById(String viewId) {
		View view = dataDictionaryService.getViewById(viewId);

		initializeView(view);

		return view;
	}

	protected void initializeView(View view) {
		for (Component component : view.getItems()) {
			initializeGroup((Group) component);
		}

		initializeNavigation(view.getNavigation());
		initializeField(view.getHeader());
		initializeGroup(view.getFooter());

		view.initialize();
	}

	protected void initializeGroup(Group group) {
		if (group == null) {
			return;
		}

		for (Component component : group.getItems()) {
			if (component instanceof Group) {
				initializeGroup((Group) component);
			}
			else {
				initializeField((Field) component);
			}
		}

		initializeField(group.getHeader());
		initializeGroup(group.getFooter());

		group.initialize();
	}

	protected void initializeField(Field field) {
		if (field == null) {
			return;
		}

		field.initialize();

		if (field instanceof GroupField) {
			GroupField groupField = (GroupField) field;
			initializeGroup(groupField.getGroup());
		}
	}

	protected void initializeNavigation(Group navigation) {
		if (navigation == null) {
			return;
		}

		navigation.initialize();
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
