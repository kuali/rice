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
package org.kuali.rice.kns.ui.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class View extends ContainerBase {
	private String entryPageId;
	private String currentPageId;
	private String stateHandler;

	// TODO: is this necessary or can we determine it based on request?
	private String controllerRequestMapping;

	private boolean renderForm;
	private boolean validateModelData;

	private NavigationGroup navigation;

	private Map<String, Class> modelClasses;
	private List<String> additionalScriptFiles;

	// scripting variables
	private boolean dialogMode;

	public View() {
		renderForm = true;
		validateModelData = true;
		dialogMode = false;
	}

	@Override
	public void initialize() {
		super.initialize();

		this.currentPageId = this.entryPageId;
	}

	/**
	 * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
	 */
	@Override
	public List<Class> getSupportedComponents() {
		List<Class> supportedComponents = new ArrayList<Class>();
		supportedComponents.add(Group.class);

		return supportedComponents;
	}

	/**
	 * @see org.kuali.rice.kns.ui.Component#getComponentTypeName()
	 */
	@Override
	public String getComponentTypeName() {
		return "view";
	}

	/**
	 * Iterates through the contained page items and returns the Page that
	 * matches the set current page id
	 * 
	 * @return Page instance
	 */
	public Group getCurrentPage() {
		for (Iterator iterator = this.getItems().iterator(); iterator.hasNext();) {
			Group page = (Group) iterator.next();
			if (page.getId().equals(getCurrentPageId())) {
				return page;
			}
		}

		return null;
	}

	public String getEntryPageId() {
		return this.entryPageId;
	}

	public void setEntryPageId(String entryPageId) {
		this.entryPageId = entryPageId;
	}

	public String getCurrentPageId() {
		if (StringUtils.isBlank(currentPageId)) {
			return this.entryPageId;
		}

		return this.currentPageId;
	}

	public void setCurrentPageId(String currentPageId) {
		this.currentPageId = currentPageId;
	}

	public String getStateHandler() {
		return this.stateHandler;
	}

	public void setStateHandler(String stateHandler) {
		this.stateHandler = stateHandler;
	}

	public boolean isRenderForm() {
		return this.renderForm;
	}

	public void setRenderForm(boolean renderForm) {
		this.renderForm = renderForm;
	}

	public boolean isValidateModelData() {
		return this.validateModelData;
	}

	public void setValidateModelData(boolean validateModelData) {
		this.validateModelData = validateModelData;
	}

	public NavigationGroup getNavigation() {
		return this.navigation;
	}

	public void setNavigation(NavigationGroup navigation) {
		this.navigation = navigation;
	}

	public Map<String, Class> getModelClasses() {
		return this.modelClasses;
	}

	public void setModelClasses(Map<String, Class> modelClasses) {
		this.modelClasses = modelClasses;
	}

	public List<String> getAdditionalScriptFiles() {
		return this.additionalScriptFiles;
	}

	public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
		this.additionalScriptFiles = additionalScriptFiles;
	}

	public String getControllerRequestMapping() {
		return this.controllerRequestMapping;
	}

	public void setControllerRequestMapping(String controllerRequestMapping) {
		this.controllerRequestMapping = controllerRequestMapping;
	}

	public boolean isDialogMode() {
		return this.dialogMode;
	}

	public void setDialogMode(boolean dialogMode) {
		this.dialogMode = dialogMode;
	}
}
