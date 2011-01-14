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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.uif.Component;

/**
 * Root of the component tree which encompasses a set of related
 * <code>Page</code> instances tied together with a common page layout and
 * navigation.
 * <p>
 * The <code>View</code> component ties together all the components and
 * configuration of the User Interface for a piece of functionality. In Rice
 * applications the view is typically associated with a <code>Document</code>
 * instance.
 * </p>
 * <p>
 * The view template lays out the common header, footer, and navigation for the
 * related pages. In addition the view renders the HTML head element bringing in
 * common script files and style sheets, along with optionally rendering a form
 * element for pages that need to post data back to the server.
 * </p>
 * <p>
 * Configuration of UIF features such as model validation is also done through
 * the <code>View</code>
 * </p>
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
	private String persistenceMode;

	private NavigationGroup navigation;

	private Class<?> formClass;
	private Map<String, Class<?>> modelClasses;

	private List<String> additionalScriptFiles;

	private String viewHelperServiceBeanId;

	private Set<String> allowedParameters;
	private Map<String, String> context;

	// scripting variables
	private boolean dialogMode;

	public View() {
		renderForm = true;
		validateModelData = true;
		dialogMode = false;

		modelClasses = new HashMap<String, Class<?>>();
		allowedParameters = new HashSet<String>();
		context = new HashMap<String, String>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>If current page id is not set, it is set to the configured entry page
	 * id</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		this.currentPageId = this.entryPageId;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(navigation);

		return components;
	}

	/**
	 * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
	 */
	@Override
	public Set<Class> getSupportedComponents() {
		Set<Class> supportedComponents = new HashSet<Class>();
		supportedComponents.add(Group.class);

		return supportedComponents;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getComponentTypeName()
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

	/**
	 * Specifies what page should be rendered by default. This is the page that
	 * will be rendered when the <code>View</code> is first rendered or when the
	 * current page is not set
	 * 
	 * @return String id of the page to render by default
	 */
	public String getEntryPageId() {
		return this.entryPageId;
	}

	/**
	 * Setter for default Page id
	 * 
	 * @param entryPageId
	 */
	public void setEntryPageId(String entryPageId) {
		this.entryPageId = entryPageId;
	}

	/**
	 * The id for the page within the view that should be displayed in the UI.
	 * Other pages of the view will not be rendered
	 * 
	 * @return String id of the page that should be displayed
	 */
	public String getCurrentPageId() {
		return this.currentPageId;
	}

	/**
	 * Setter for the page id to display
	 * 
	 * @param currentPageId
	 */
	public void setCurrentPageId(String currentPageId) {
		this.currentPageId = currentPageId;
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

	/**
	 * <code>NavigationGroup</code> instance for the <code>View</code>
	 * <p>
	 * Provides configuration necessary to render the navigation. This includes
	 * navigation items in addition to configuration for the navigation
	 * renderer.
	 * </p>
	 * 
	 * @return NavigationGroup
	 */
	public NavigationGroup getNavigation() {
		return this.navigation;
	}

	/**
	 * Setter for the View's <code>NavigationGroup</code>
	 * 
	 * @param navigation
	 */
	public void setNavigation(NavigationGroup navigation) {
		this.navigation = navigation;
	}

	public Class<?> getFormClass() {
		return this.formClass;
	}

	public void setFormClass(Class<?> formClass) {
		this.formClass = formClass;
	}

	/**
	 * Declares the model classes that will provide the view's data
	 * 
	 * <p>
	 * Each <code>Map</code> entry defines a model. The <code>Map</code> key
	 * gives a name that should be associated with the model. When the view also
	 * contains a form class, this name should be the property name on the form
	 * that holds the model. This will be used to create the final bindingPath
	 * for the attributes that belong to that model.
	 * </p>
	 * 
	 * <p>
	 * The <code>Map</code> value gives the <code>Class</code> for the model.
	 * This is used to find dictionary entries along with creating new instances
	 * of the model when needed.
	 * </p>
	 * 
	 * @return Map<String, Class> of model entries
	 */
	public Map<String, Class<?>> getModelClasses() {
		return this.modelClasses;
	}

	/**
	 * Setter for the model classes Map
	 * 
	 * @param modelClasses
	 */
	public void setModelClasses(Map<String, Class<?>> modelClasses) {
		this.modelClasses = modelClasses;
	}

	/**
	 * Declares additional script files that should be included with the
	 * <code>View</code>. These files are brought into the HTML page along with
	 * common script files configured for the Rice application. Each entry
	 * should contain the path to the script relative to the web root.
	 * <p>
	 * e.g. '/krad/scripts/myScript.js'
	 * </p>
	 * 
	 * @return
	 */
	public List<String> getAdditionalScriptFiles() {
		return this.additionalScriptFiles;
	}

	/**
	 * Setter for the List of additional script files to include
	 * 
	 * @param additionalScriptFiles
	 */
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

	/**
	 * Mode for storing the views state. By default the REQUEST and SESSION
	 * modes are supported. This affects how hidden data and containers are
	 * rendered and how the view is restored on post back
	 * 
	 * @return String persistence mode
	 * @see org.kuali.rice.kns.uif.UIFConstants.PersistenceMode
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

	/**
	 * Spring bean id of the <code>ViewHelperService</code> that handles the
	 * various phases of the Views lifecycle
	 * 
	 * @return String id for the spring bean
	 * @see org.kuali.rice.kns.uif.service.ViewHelperService
	 */
	public String getViewHelperServiceBeanId() {
		return this.viewHelperServiceBeanId;
	}

	/**
	 * Setter for the <code>ViewHelperService</code> bean id
	 * 
	 * @param viewLifecycleService
	 */
	public void setViewHelperServiceBeanId(String viewHelperServiceBeanId) {
		this.viewHelperServiceBeanId = viewHelperServiceBeanId;
	}

	/**
	 * Set of parameter names that are valid options for the view
	 * <p>
	 * Views can be configured by parameters. These might impact which parts of
	 * the view are rendered or how the view behaves. Generally these would get
	 * passed in when a new view is requested (by request parameters). When a
	 * new view is requested from the <code>ViewService</code> it will consult
	 * the view to see what parameters are allowed. If those parameters where
	 * then specified to the view service it will retrieve those values and put
	 * into the views context.
	 * </p>
	 * <p>
	 * Example parameter would be for MaintenaceView whether a New, Edit, or
	 * Copy was requested (maintenance mode)
	 * </p>
	 * <p>
	 * If allowed parameters is empty, nothing will be set in the context for
	 * the view request
	 * </p>
	 * 
	 * @return Set<String> allowed parameters for the view
	 * @see getContext()
	 */
	public Set<String> getAllowedParameters() {
		return this.allowedParameters;
	}

	/**
	 * Setter for the allowed parameters
	 * 
	 * @param allowedParameters
	 */
	public void setAllowedParameters(Set<String> allowedParameters) {
		this.allowedParameters = allowedParameters;
	}

	/**
	 * Key value pairs that provide context information for the view instance
	 * <p>
	 * Will contain any view parameters that were set and can be used by the
	 * view components or view helper service to place additional information
	 * about the context
	 * </p>
	 * 
	 * @return Map<String, String> context
	 * @see getAllowedParameters()
	 */
	public Map<String, String> getContext() {
		return this.context;
	}

	/**
	 * Setter for the view context
	 * 
	 * @param context
	 */
	public void setContext(Map<String, String> context) {
		this.context = context;
	}

}
