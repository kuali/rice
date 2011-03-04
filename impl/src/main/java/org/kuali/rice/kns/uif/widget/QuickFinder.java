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

import java.util.Map;

import org.kuali.rice.kns.uif.field.ActionField;

/**
 * Widget for navigating to a lookup from a field (called a quickfinder)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickFinder extends WidgetBase {
	private static final long serialVersionUID = 3302390972815386785L;

	private Class<?> objectClassName;
	private String viewId;
	private String viewName;

	private Map<String, String> fieldConversions;
	private Map<String, String> lookupParameters;

	boolean hideReturnColumn;
	boolean hideActionsColumn;
	boolean performAutoSearch;

	private ActionField quickfinderActionField;

	public QuickFinder() {
		super();
	}

	public Class<?> getObjectClassName() {
		return this.objectClassName;
	}

	public void setObjectClassName(Class<?> objectClassName) {
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

	public Map<String, String> getFieldConversions() {
		return this.fieldConversions;
	}

	public void setFieldConversions(Map<String, String> fieldConversions) {
		this.fieldConversions = fieldConversions;
	}

	public Map<String, String> getLookupParameters() {
		return this.lookupParameters;
	}

	public void setLookupParameters(Map<String, String> lookupParameters) {
		this.lookupParameters = lookupParameters;
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
