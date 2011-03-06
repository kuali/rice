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
package org.kuali.rice.kns.uif.widget;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.ActionField;

/**
 * Widget for navigating to a lookup from a field (called a quickfinder)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickFinder extends WidgetBase {
	private static final long serialVersionUID = 3302390972815386785L;

	private String baseLookupUrl;

	private String objectClassName;
	private String viewId;
	private String viewName;

	private Map<String, String> returnFieldMapping;
	private Map<String, String> parameterFieldMapping;

	boolean hideReturnColumn;
	boolean hideActionsColumn;
	boolean performAutoSearch;

	private ActionField quickfinderActionField;

	public QuickFinder() {
		super();
	}

	/**
	 * @see org.kuali.rice.kns.uif.widget.WidgetBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		quickfinderActionField.addActionParameter(UifParameters.BASE_LOOKUP_URL, baseLookupUrl);

		if (StringUtils.isNotBlank(objectClassName)) {
			quickfinderActionField.addActionParameter(UifParameters.OBJECT_CLASS_NAME, objectClassName);

			setRender(true);
		}
		else {
			setRender(false);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(quickfinderActionField);

		return components;
	}

	public String getBaseLookupUrl() {
		return this.baseLookupUrl;
	}

	public void setBaseLookupUrl(String baseLookupUrl) {
		this.baseLookupUrl = baseLookupUrl;
	}

	public String getObjectClassName() {
		return this.objectClassName;
	}

	public void setObjectClassName(String objectClassName) {
		this.objectClassName = objectClassName;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewName() {
		return this.viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<String, String> getReturnFieldMapping() {
		return this.returnFieldMapping;
	}

	public void setReturnFieldMapping(Map<String, String> returnFieldMapping) {
		this.returnFieldMapping = returnFieldMapping;
	}

	public Map<String, String> getParameterFieldMapping() {
		return this.parameterFieldMapping;
	}

	public void setParameterFieldMapping(Map<String, String> parameterFieldMapping) {
		this.parameterFieldMapping = parameterFieldMapping;
	}

	public boolean isHideReturnColumn() {
		return this.hideReturnColumn;
	}

	public void setHideReturnColumn(boolean hideReturnColumn) {
		this.hideReturnColumn = hideReturnColumn;
	}

	public boolean isHideActionsColumn() {
		return this.hideActionsColumn;
	}

	public void setHideActionsColumn(boolean hideActionsColumn) {
		this.hideActionsColumn = hideActionsColumn;
	}

	public boolean isPerformAutoSearch() {
		return this.performAutoSearch;
	}

	public void setPerformAutoSearch(boolean performAutoSearch) {
		this.performAutoSearch = performAutoSearch;
	}

	public ActionField getQuickfinderActionField() {
		return this.quickfinderActionField;
	}

	public void setQuickfinderActionField(ActionField quickfinderActionField) {
		this.quickfinderActionField = quickfinderActionField;
	}

}
