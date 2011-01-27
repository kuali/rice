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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FormView extends View {
	private String stateHandler;

	// TODO: is this necessary or can we determine it based on request?
	private String controllerRequestMapping;

	private boolean renderForm;
	private boolean validateModelData;
	private String persistenceMode;

	public FormView() {
		renderForm = true;
		validateModelData = true;
	}

	public String getStateHandler() {
		return this.stateHandler;
	}

	public void setStateHandler(String stateHandler) {
		this.stateHandler = stateHandler;
	}

	/**
	 * Indicates whether a Form element should be rendered for the View. This is
	 * necessary for pages that need to submit data back to the server. Note
	 * that even if a page is read-only, a form element is generally needed for
	 * the navigation. Defaults to true
	 * 
	 * @return true if the form element should be rendered, false if it should
	 *         not be
	 */
	public boolean isRenderForm() {
		return this.renderForm;
	}

	/**
	 * Setter for the render form indicator
	 * 
	 * @param renderForm
	 */
	public void setRenderForm(boolean renderForm) {
		this.renderForm = renderForm;
	}

	/**
	 * Indicates whether to perform the validate model phase of the view
	 * lifecycle. This phase will validate the model against configured
	 * dictionary validations and report errors. Defaults to true
	 * 
	 * @return boolean true if model data should be validated, false if it
	 *         should not be
	 * @see
	 */
	public boolean isValidateModelData() {
		return this.validateModelData;
	}

	/**
	 * Setter for the validate model data indicator
	 * 
	 * @param validateModelData
	 */
	public void setValidateModelData(boolean validateModelData) {
		this.validateModelData = validateModelData;
	}

	public String getControllerRequestMapping() {
		return this.controllerRequestMapping;
	}

	public void setControllerRequestMapping(String controllerRequestMapping) {
		this.controllerRequestMapping = controllerRequestMapping;
	}

	/**
	 * Mode for storing the views state. By default the REQUEST and SESSION
	 * modes are supported. This affects how hidden data and containers are
	 * rendered and how the view is restored on post back
	 * 
	 * @return String persistence mode
	 * @see org.kuali.rice.kns.uif.UifConstants.PersistenceMode
	 */
	public String getPersistenceMode() {
		return this.persistenceMode;
	}

	/**
	 * Setter for the views persistence mode
	 * 
	 * @param persistenceMode
	 */
	public void setPersistenceMode(String persistenceMode) {
		this.persistenceMode = persistenceMode;
	}

}
