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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifConstants.ViewStatus;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.field.LinkField;
import org.kuali.rice.kns.uif.service.ViewHelperService;

/**
 * Root of the component tree which encompasses a set of related
 * <code>GroupContainer</code> instances tied together with a common page layout
 * and navigation.
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
	private static final long serialVersionUID = -1220009725554576953L;

	private String viewName;

	private String entryPageId;
	private String currentPageId;

	private NavigationGroup navigation;

	private Class<?> formClass;
	private String defaultBindingObjectPath;
	private Map<String, Class<?>> abstractTypeClasses;

	private List<String> additionalScriptFiles;
	private List<String> additionalCssFiles;

	private String viewTypeName;
	private String viewHelperServiceBeanId;

	private Set<String> allowedParameters;
	private Map<String, String> context;

	private String viewStatus;
	private ViewIndex viewIndex;

	private boolean singlePageView;
	private Group page;

	private List<? extends Group> items;

	private LinkField viewMenuLink;
	private String viewMenuGrouping;

	// TODO: scripting variables, should be in context
	private boolean dialogMode;

	public View() {
		dialogMode = false;
		singlePageView = false;
		viewTypeName = ViewType.DEFAULT;
		viewStatus = UifConstants.ViewStatus.CREATED;

		viewIndex = new ViewIndex();

		additionalScriptFiles = new ArrayList<String>();
		additionalCssFiles = new ArrayList<String>();
		items = new ArrayList<Group>();
		abstractTypeClasses = new HashMap<String, Class<?>>();
		allowedParameters = new HashSet<String>();
		context = new HashMap<String, String>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>If a single paged view, set items in page group and put the page in
	 * the items list</li>
	 * <li>If current page id is not set, it is set to the configured entry page
	 * or first item in list id</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		// populate items on page for single paged view
		if (singlePageView) {
			if (page != null) {
				page.setItems(new ArrayList<Group>(items));

				// reset the items list to include the one page
				items = new ArrayList<Group>();
				((List<Group>) items).add(page);
			}
			else {
				throw new RuntimeException("For single paged views the page Group must be set.");
			}
		}

		// set the entry page
		if (StringUtils.isNotBlank(entryPageId)) {
			currentPageId = entryPageId;
		}
		else {
			Group firstPageGroup = getItems().get(0);
			currentPageId = firstPageGroup.getId();
		}
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
	public Set<Class<? extends Component>> getSupportedComponents() {
		Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
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
		for (Iterator<? extends Group> iterator = this.getItems().iterator(); iterator.hasNext();) {
			Group pageGroup = iterator.next();
			if (pageGroup.getId().equals(getCurrentPageId())) {
				return pageGroup;
			}
		}

		return null;
	}

	/**
	 * View name provides an identifier for a view within a type. That is if a
	 * set of <code>View</code> instances have the same values for the
	 * properties that are used to retrieve them by their type, the name can be
	 * given to further qualify the view that should be retrieved.
	 * 
	 * <p>
	 * A view type like the <code>LookupView</code> might have several views for
	 * the same object class, but one that is the 'default' lookup and another
	 * that is the 'advanced' lookup. Therefore the name on the first could be
	 * set to 'default', and likewise the name for the second 'advanced'.
	 * </p>
	 * 
	 * @return String name of view
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Setter for the view's name
	 * 
	 * @param viewName
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
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

	/**
	 * Class of the Form that should be used with the <code>View</code>
	 * instance. The form is the top level object for all the view's data and is
	 * used to present and accept data in the user interface. All form classes
	 * should extend UifFormBase
	 * 
	 * @return Class<?> class for the view's form
	 * @see org.kuali.rice.kns.web.spring.form.UifFormBase
	 */
	public Class<?> getFormClass() {
		return this.formClass;
	}

	/**
	 * Setter for the form class
	 * 
	 * @param formClass
	 */
	public void setFormClass(Class<?> formClass) {
		this.formClass = formClass;
	}

	/**
	 * For <code>View</code> types that work primarily with one nested object of
	 * the form (for instance document, or bo) the default binding object path
	 * can be set for each of the views <code>DataBinding</code> components. If
	 * the component does not set its own binding object path it will inherit
	 * the default
	 * 
	 * @return String binding path to the object from the form
	 * @see org.kuali.rice.kns.uif.BindingInfo.getBindingObjectPath()
	 */
	public String getDefaultBindingObjectPath() {
		return this.defaultBindingObjectPath;
	}

	/**
	 * Setter for the default binding object path to use for the view
	 * 
	 * @param defaultBindingObjectPath
	 */
	public void setDefaultBindingObjectPath(String defaultBindingObjectPath) {
		this.defaultBindingObjectPath = defaultBindingObjectPath;
	}

	/**
	 * Configures the concrete classes that will be used for properties in the
	 * form object graph that have an abstract or interface type
	 * 
	 * <p>
	 * For properties that have an abstract or interface type, it is not
	 * possible to perform operations like getting/settings property values and
	 * getting information from the dictionary. When these properties are
	 * encountered in the object graph, this <code>Map</code> will be consulted
	 * to determine the concrete type to use.
	 * </p>
	 * 
	 * <p>
	 * e.g. Suppose we have a property document.accountingLine.accountNumber and
	 * the accountingLine property on the document instance has an interface
	 * type 'AccountingLine'. We can then put an entry into this map with key
	 * 'document.accountingLine', and value
	 * 'org.kuali.rice.sampleapp.TravelAccountingLine'. When getting the
	 * property type or an entry from the dictionary for accountNumber, the
	 * TravelAccountingLine class will be used.
	 * </p>
	 * 
	 * @return Map<String, Class> of class implementations keyed by path
	 */
	public Map<String, Class<?>> getAbstractTypeClasses() {
		return this.abstractTypeClasses;
	}

	/**
	 * Setter for the Map of class implementations keyed by path
	 * 
	 * @param abstractTypeClasses
	 */
	public void setAbstractTypeClasses(Map<String, Class<?>> abstractTypeClasses) {
		this.abstractTypeClasses = abstractTypeClasses;
	}

	/**
	 * Declares additional script files that should be included with the
	 * <code>View</code>. These files are brought into the HTML page along with
	 * common script files configured for the Rice application. Each entry
	 * contain the path to the CSS file, either a relative path, path from web
	 * root, or full URI
	 * 
	 * <p>
	 * e.g. '/krad/scripts/myScript.js', '../scripts/myScript.js',
	 * 'http://my.edu/web/myScript.js'
	 * </p>
	 * 
	 * @return List<String> script file locations
	 */
	public List<String> getAdditionalScriptFiles() {
		return this.additionalScriptFiles;
	}

	/**
	 * Setter for the List of additional script files to included with the
	 * <code>View</code>
	 * 
	 * @param additionalScriptFiles
	 */
	public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
		this.additionalScriptFiles = additionalScriptFiles;
	}

	/**
	 * Declares additional CSS files that should be included with the
	 * <code>View</code>. These files are brought into the HTML page along with
	 * common CSS files configured for the Rice application. Each entry should
	 * contain the path to the CSS file, either a relative path, path from web
	 * root, or full URI
	 * 
	 * <p>
	 * e.g. '/krad/css/stacked-view.css', '../css/stacked-view.css',
	 * 'http://my.edu/web/stacked-view.css'
	 * </p>
	 * 
	 * @return List<String> CSS file locations
	 */
	public List<String> getAdditionalCssFiles() {
		return this.additionalCssFiles;
	}

	/**
	 * Setter for the List of additional CSS files to included with the
	 * <code>View</code>
	 * 
	 * @param additionalCssFiles
	 */
	public void setAdditionalCssFiles(List<String> additionalCssFiles) {
		this.additionalCssFiles = additionalCssFiles;
	}

	public boolean isDialogMode() {
		return this.dialogMode;
	}

	public void setDialogMode(boolean dialogMode) {
		this.dialogMode = dialogMode;
	}

	/**
	 * View type name the view is associated with
	 * 
	 * <p>
	 * Views that share common features and functionality can be grouped by the
	 * view type. Usually view types extend the <code>View</code> class to
	 * provide additional configuration and to set defaults. View types can also
	 * implement the <code>ViewTypeService</code> to add special indexing and
	 * retrieval of views.
	 * </p>
	 * 
	 * @return String view type name for the view
	 */
	public String getViewTypeName() {
		return this.viewTypeName;
	}

	/**
	 * Setter for the view's type name
	 * 
	 * @param viewTypeName
	 */
	public void setViewTypeName(String viewTypeName) {
		this.viewTypeName = viewTypeName;
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
	 * Retrieves the <code>ViewHelperService</code> configured for the view from
	 * the application context. If a service is not found a
	 * <code>RuntimeException</code> will be thrown.
	 * 
	 * @return ViewHelperService instance
	 */
	public ViewHelperService getViewHelperService() {
		ViewHelperService viewHelperService = KNSServiceLocator.getService(getViewHelperServiceBeanId());

		if (viewHelperService == null) {
			throw new RuntimeException("Unable to find ViewHelperService (using bean id: "
					+ getViewHelperServiceBeanId() + ") for view: " + getId());
		}

		return viewHelperService;
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

	/**
	 * Holds field indexes of the <code>View</code> instance for retrieval
	 * 
	 * @return ViewIndex instance
	 */
	public ViewIndex getViewIndex() {
		return this.viewIndex;
	}

	/**
	 * Setter for the <code>ViewIndex</code> instance
	 * 
	 * @param viewIndex
	 */
	public void setViewIndex(ViewIndex viewIndex) {
		this.viewIndex = viewIndex;
	}

	/**
	 * Indicates whether the <code>View</code> only has a single page
	 * <code>Group</code> or contains multiple page <code>Group</code>
	 * instances. In the case of a single page it is assumed the group's items
	 * list contains the section groups for the page, and the page itself is
	 * given by the page property ({@link #getPage()}. This is for convenience
	 * of configuration and also can drive other configuration like styling.
	 * 
	 * @return boolean true if the view only contains one page group, false if
	 *         it contains multple pages
	 */
	public boolean isSinglePageView() {
		return this.singlePageView;
	}

	/**
	 * Setter for the single page indicator
	 * 
	 * @param singlePageView
	 */
	public void setSinglePageView(boolean singlePageView) {
		this.singlePageView = singlePageView;
	}

	/**
	 * For single paged views ({@link #isSinglePageView()}, gives the page
	 * <code>Group</code> the view should render. The actual items for the page
	 * is taken from the group's items list ({@link #getItems()}, and set onto
	 * the give page group. This is for convenience of configuration.
	 * 
	 * @return Group page group for single page views
	 */
	public Group getPage() {
		return this.page;
	}

	/**
	 * Setter for the page group for single page views
	 * 
	 * @param page
	 */
	public void setPage(Group page) {
		this.page = page;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#getItems()
	 */
	@Override
	public List<? extends Group> getItems() {
		return this.items;
	}

	/**
	 * Setter for the view's <code>Group</code> instances
	 * 
	 * @param items
	 */
	@Override
	public void setItems(List<? extends Component> items) {
		// TODO: fix this generic issue
		this.items = (List<? extends Group>) items;
	}

	/**
	 * Provides configuration for displaying a link to the view from an
	 * application menu
	 * 
	 * @return LinkField view link field
	 */
	public LinkField getViewMenuLink() {
		return this.viewMenuLink;
	}

	/**
	 * Setter for the views link field
	 * 
	 * @param viewMenuLink
	 */
	public void setViewMenuLink(LinkField viewMenuLink) {
		this.viewMenuLink = viewMenuLink;
	}

	/**
	 * Provides a grouping string for the view to group its menu link (within a
	 * portal for instance)
	 * 
	 * @return String menu grouping
	 */
	public String getViewMenuGrouping() {
		return this.viewMenuGrouping;
	}

	/**
	 * Setter for the views menu grouping
	 * 
	 * @param viewMenuGrouping
	 */
	public void setViewMenuGrouping(String viewMenuGrouping) {
		this.viewMenuGrouping = viewMenuGrouping;
	}

	/**
	 * Indicates what lifecycle phase the View instance is in
	 * 
	 * <p>
	 * The view lifecycle begins with the CREATED status. In this status a new
	 * instance of the view has been retrieved from the dictionary, but no
	 * further processing has been done. After the initialize phase has been run
	 * the status changes to INITIALIZED. After the model has been applied and
	 * the view is ready for render the status changes to FINAL
	 * </p>
	 * 
	 * @return String view status
	 * @see org.kuali.rice.kns.uif.UifConstants.ViewStatus
	 */
	public String getViewStatus() {
		return this.viewStatus;
	}

	/**
	 * Setter for the view status
	 * 
	 * @param viewStatus
	 */
	public void setViewStatus(String viewStatus) {
		this.viewStatus = viewStatus;
	}

	/**
	 * Indicates whether the view has been initialized
	 * 
	 * @return boolean true if the view has been initialized, false if not
	 */
	public boolean isInitialized() {
		return StringUtils.equals(viewStatus, ViewStatus.INITIALIZED)
				|| StringUtils.equals(viewStatus, ViewStatus.FINAL);
	}

	/**
	 * Indicates whether the view has been updated from the model and final
	 * updates made
	 * 
	 * @return boolean true if the view has been updated, false if not
	 */
	public boolean isFinal() {
		return StringUtils.equals(viewStatus, ViewStatus.FINAL);
	}

	/**
	 * onSubmit script configured on the <code>View</code> gets placed on the
	 * form element
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#getSupportsOnSubmit()
	 */
	@Override
	public boolean getSupportsOnSubmit() {
		return true;
	}

	/**
	 * onLoad script configured on the <code>View</code> gets placed in a
	 * document ready jQuery block
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#getSupportsOnLoad()
	 */
	@Override
	public boolean getSupportsOnLoad() {
		return true;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performFinalize(org.kuali.rice.kns.uif.container.View, java.lang.Object)
	 */
	@Override
	public void performFinalize(View view, Object model) {
		super.performFinalize(view, model);
		String prefixScript = "";
		if(this.getOnLoadScript() != null){
			prefixScript = this.getOnLoadScript();
		}
		this.setOnLoadScript(prefixScript +
				"$(document).ready(function()" +
					"{$('#kualiForm').validate({ onfocusout: function(element) { $(element).valid(); }});" +
				"});");
	}
}
