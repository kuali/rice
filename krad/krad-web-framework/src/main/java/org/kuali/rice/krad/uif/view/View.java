/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.NavigationGroup;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.widget.BreadCrumbs;
import org.kuali.rice.krad.uif.widget.Growls;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Root of the component tree which encompasses a set of related
 * <code>GroupContainer</code> instances tied together with a common page layout
 * and navigation.
 *
 * <p>
 * The <code>View</code> component ties together all the components and
 * configuration of the User Interface for a piece of functionality. In Rice
 * applications the view is typically associated with a <code>Document</code>
 * instance.
 * </p>
 *
 * <p>
 * The view template lays out the common header, footer, and navigation for the
 * related pages. In addition the view renders the HTML head element bringing in
 * common script files and style sheets, along with optionally rendering a form
 * element for pages that need to post data back to the server.
 * </p>
 *
 * <p>
 * Configuration of UIF features such as model validation is also done through
 * the <code>View</code>
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class View extends ContainerBase {
    private static final long serialVersionUID = -1220009725554576953L;

    private String namespaceCode;
    private String viewName;
    private ViewTheme theme;

    private int idSequence;

    private String stateObjectBindingPath;
    private StateMapping stateMapping;

    // application
    private Header applicationHeader;
    private Group applicationFooter;

    // Breadcrumbs
    private BreadCrumbs breadcrumbs;
    private String breadcrumbTitlePropertyName;
    private String breadcrumbTitleDisplayOption;

    private boolean renderBreadcrumbsInView;

    // Growls support
    private Growls growls;
    private boolean growlMessagingEnabled;

    private String entryPageId;

    @RequestParameter
    private String currentPageId;

    private NavigationGroup navigation;

    private Class<?> formClass;
    private String defaultBindingObjectPath;
    private Map<String, Class<?>> objectPathToConcreteClassMapping;

    private List<String> additionalScriptFiles;
    private List<String> additionalCssFiles;

    private ViewType viewTypeName;

    private String viewStatus;
    private ViewIndex viewIndex;
    private Map<String, String> viewRequestParameters;

    private boolean persistFormToSession;

    private ViewPresentationController presentationController;
    private ViewAuthorizer authorizer;

    private BooleanMap actionFlags;
    private BooleanMap editModes;

    private Map<String, String> expressionVariables;

    private boolean singlePageView;
    private PageGroup page;

    private List<? extends Group> items;

    private Link viewMenuLink;
    private String viewMenuGroupName;

    private boolean applyDirtyCheck;
    private boolean translateCodesOnReadOnlyDisplay;
    private boolean supportsRequestOverrideOfReadOnlyFields;

    private String preLoadScript;
    private Map<String, Object> clientSideState;

    @RequestParameter
    private boolean renderedInLightBox;
    
    private int preloadPoolSize;

    private Class<? extends ViewHelperService> viewHelperServiceClass;

    @ReferenceCopy
    private ViewHelperService viewHelperService;

    public View() {
        renderedInLightBox = false;
        singlePageView = false;
        translateCodesOnReadOnlyDisplay = false;
        viewTypeName = ViewType.DEFAULT;
        viewStatus = UifConstants.ViewStatus.CREATED;
        formClass = UifFormBase.class;
        renderBreadcrumbsInView = true;
        supportsRequestOverrideOfReadOnlyFields = true;
        persistFormToSession = true;

        idSequence = 0;
        this.viewIndex = new ViewIndex();
        preloadPoolSize = 0;

        additionalScriptFiles = new ArrayList<String>();
        additionalCssFiles = new ArrayList<String>();
        items = new ArrayList<Group>();
        objectPathToConcreteClassMapping = new HashMap<String, Class<?>>();
        viewRequestParameters = new HashMap<String, String>();
        expressionVariables = new HashMap<String, String>();
        clientSideState = new HashMap<String, Object>();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>If a single paged view, set items in page group and put the page in
     * the items list</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(View, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        // populate items on page for single paged view
        if (singlePageView) {
            if (page != null) {
                page.setItems(new ArrayList<Group>(items));

                // reset the items list to include the one page
                items = new ArrayList<Group>();
                ((List<Group>) items).add(page);
            } else {
                throw new RuntimeException("For single paged views the page Group must be set.");
            }
        }

        // make sure all the pages have ids before selecting the current page
        for (Group group : this.getItems()) {
            if (StringUtils.isBlank(group.getId())) {
                group.setId(view.getNextId());
            }
        }
    }

    /**
     * The following is performed:
     *
     * <ul>
     * <li>Adds to its document ready script the setupValidator js function for setting
     * up the validator for this view</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performFinalize(View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getPreLoadScript();
        }

        String growlScript = "";
        Growls gw = view.getGrowls();
        if (!gw.getTemplateOptions().isEmpty()) {
            growlScript = "setGrowlDefaults(" + gw.getTemplateOptionsJSString() + ");";
        }

        this.setPreLoadScript(prefixScript + growlScript);

        prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }

        this.setOnDocumentReadyScript(prefixScript + "jQuery.extend(jQuery.validator.messages, " +
                ClientValidationUtils.generateValidatorMessagesOption() + ");");
    }

    /**
     * Assigns an id to the component if one was not configured
     *
     * @param component - component instance to assign id to
     */
    public void assignComponentIds(Component component) {
        if (component == null) {
            return;
        }
        
        Integer currentSequenceVal = idSequence;

        // assign ID if necessary
        if (StringUtils.isBlank(component.getId())) {
            component.setId(UifConstants.COMPONENT_ID_PREFIX + getNextId());
        }

        // capture current sequence value for component refreshes
        getViewIndex().addSequenceValueToSnapshot(component.getId(), currentSequenceVal);

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();
            if ((layoutManager != null) && StringUtils.isBlank(layoutManager.getId())) {
                layoutManager.setId(UifConstants.COMPONENT_ID_PREFIX + getNextId());
            }
        }

        // assign id to nested components
        List<Component> allNested = new ArrayList<Component>(component.getComponentsForLifecycle());
        allNested.addAll(component.getComponentPrototypes());
        for (Component nestedComponent : allNested) {
            assignComponentIds(nestedComponent);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        components.add(applicationHeader);
        components.add(applicationFooter);
        components.add(navigation);
        components.add(breadcrumbs);
        components.add(growls);
        components.add(viewMenuLink);

        // Note super items should be added after navigation and other view components so
        // conflicting ids between nav and page do not occur on page navigation via ajax
        components.addAll(super.getComponentsForLifecycle());

        // remove all pages that are not the current page
        if (!singlePageView) {
            for (Group group : this.getItems()) {
                if ((group instanceof PageGroup) && !StringUtils.equals(group.getId(), getCurrentPageId()) && components
                        .contains(group)) {
                    components.remove(group);
                }
            }
        }

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        components.add(page);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.Container#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentTypeName()
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
    public PageGroup getCurrentPage() {
        for (Group pageGroup : this.getItems()) {
            if (StringUtils.equals(pageGroup.getId(), getCurrentPageId()) && pageGroup instanceof PageGroup) {
                return (PageGroup) pageGroup;
            }
        }

        return null;
    }

    /**
     * Namespace code the view should be associated with
     *
     * <p>
     * The namespace code is used within the framework in such places as permission checks and parameter
     * retrieval
     * </p>
     *
     * @return String namespace code
     */
    public String getNamespaceCode() {
        return namespaceCode;
    }

    /**
     * Setter for the view's namespace code
     *
     * @param namespaceCode
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    /**
     * View name provides an identifier for a view within a type. That is if a
     * set of <code>View</code> instances have the same values for the
     * properties that are used to retrieve them by their type, the name can be
     * given to further qualify the view that should be retrieved.
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
     * Header for the application containing the view
     *
     * <p>
     * When deploying outside a portal, the application header and footer property can be configured to
     * display a consistent header/footer across all views. Here application logos, menus, login controls
     * and so on can be rendered.
     * </p>
     *
     * @return HeaderField application header
     */
    public Header getApplicationHeader() {
        return applicationHeader;
    }

    /**
     * Setter for the application header
     *
     * @param applicationHeader
     */
    public void setApplicationHeader(Header applicationHeader) {
        this.applicationHeader = applicationHeader;
    }

    /**
     * Footer for the application containing the view
     *
     * <p>
     * When deploying outside a portal, the application header and footer property can be configured to
     * display a consistent header/footer across all views. Here such things as application links, copyrights
     * and so on can be rendered.
     * </p>
     *
     * @return Group application footer
     */
    public Group getApplicationFooter() {
        return applicationFooter;
    }

    /**
     * Setter for the application footer
     *
     * @param applicationFooter
     */
    public void setApplicationFooter(Group applicationFooter) {
        this.applicationFooter = applicationFooter;
    }

    /**
     * Current sequence value for id assignment
     *
     * @return int id sequence
     */
    public int getIdSequence() {
        return idSequence;
    }

    /**
     * Setter for the current id sequence value
     *
     * @param idSequence
     */
    public void setIdSequence(int idSequence) {
        this.idSequence = idSequence;
    }

    /**
     * Returns the next unique id available for components within the view instance
     *
     * @return String next id available
     */
    public String getNextId() {
        idSequence += 1;
        return Integer.toString(idSequence);
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
     * <p>
     * If current page id is not set, it is set to the configured entry page or first item in list id
     * </p>
     *
     * @return String id of the page that should be displayed
     */
    public String getCurrentPageId() {
        // default current page if not set
        if (StringUtils.isBlank(currentPageId)) {
            if (StringUtils.isNotBlank(entryPageId)) {
                currentPageId = entryPageId;
            } else if ((getItems() != null) && !getItems().isEmpty()) {
                Group firstPageGroup = getItems().get(0);
                currentPageId = firstPageGroup.getId();
            }
        }

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
     * @see org.kuali.rice.krad.web.form.UifFormBase
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
    public Map<String, Class<?>> getObjectPathToConcreteClassMapping() {
        return this.objectPathToConcreteClassMapping;
    }

    /**
     * Setter for the Map of class implementations keyed by path
     *
     * @param objectPathToConcreteClassMapping
     */
    public void setObjectPathToConcreteClassMapping(Map<String, Class<?>> objectPathToConcreteClassMapping) {
        this.objectPathToConcreteClassMapping = objectPathToConcreteClassMapping;
    }

    /**
     * Declares additional script files that should be included with the
     * <code>View</code>. These files are brought into the HTML page along with
     * common script files configured for the Rice application. Each entry
     * contain the path to the CSS file, either a relative path, path from web
     * root, or full URI
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

    /**
     * Indicates whether the view is rendered within a lightbox
     *
     * <p>
     * Some discussion (for example how a close button behaves) need to change based on whether the
     * view is rendered within a lightbox or the standard browser window. This boolean is true when it is
     * within a lightbox
     * </p>
     *
     * @return boolean true if view is rendered within a lightbox, false if not
     */
    public boolean isRenderedInLightBox() {
        return this.renderedInLightBox;
    }

    /**
     * Setter for the rendered within lightbox indicator
     *
     * @param renderedInLightBox
     */
    public void setRenderedInLightBox(boolean renderedInLightBox) {
        this.renderedInLightBox = renderedInLightBox;
    }

    /**
     * Specifies the size of the pool that will contain pre-loaded views
     *
     * <p>
     * The spring loading of some views can take a few seconds which hurts performance. The framework supports
     * pre-loading of view instances so they are available right away when a request is made. This property configures
     * how many view instances will be pre-loaded. A value of 0 (the default) means no view instances will be
     * pre-loaded
     * </p>
     *
     * @return int number of view instances to pre-load
     */
    public int getPreloadPoolSize() {
        return preloadPoolSize;
    }

    /**
     * Setter for the preloaded view pool size
     *
     * @param preloadPoolSize
     */
    public void setPreloadPoolSize(int preloadPoolSize) {
        this.preloadPoolSize = preloadPoolSize;
    }

    /**
     * View type name the view is associated with the view instance
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
    public ViewType getViewTypeName() {
        return this.viewTypeName;
    }

    /**
     * Setter for the view's type name
     *
     * @param viewTypeName
     */
    public void setViewTypeName(ViewType viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    /**
     * Class name of the <code>ViewHelperService</code> that handles the various
     * phases of the Views lifecycle
     *
     * @return Class for the spring bean
     * @see org.kuali.rice.krad.uif.service.ViewHelperService
     */
    public Class<? extends ViewHelperService> getViewHelperServiceClass() {
        return this.viewHelperServiceClass;
    }

    /**
     * Setter for the <code>ViewHelperService</code> class name
     *
     * @param viewHelperServiceClass
     */
    public void setViewHelperServiceClass(Class<? extends ViewHelperService> viewHelperServiceClass) {
        this.viewHelperServiceClass = viewHelperServiceClass;
    }

    /**
     * Creates the <code>ViewHelperService</code> associated with the View
     *
     * @return ViewHelperService instance
     */
    public ViewHelperService getViewHelperService() {
        if ((this.viewHelperService == null) && (this.viewHelperServiceClass != null)) {
            viewHelperService = ObjectUtils.newInstance(viewHelperServiceClass);
        }

        return viewHelperService;
    }

    /**
     * Invoked to produce a ViewIndex of the current view's components
     */
    public void index() {
        if (this.viewIndex == null) {
            this.viewIndex = new ViewIndex();
        }
        this.viewIndex.index(this);
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
     * Map of parameters from the request that set view options, used to rebuild
     * the view on each post
     * <p>
     * Views can be configured by parameters. These might impact which parts of
     * the view are rendered or how the view behaves. Generally these would get
     * passed in when a new view is requested (by request parameters). These
     * will be used to initially populate the view properties. In addition, on a
     * post the view will be rebuilt and properties reset again by the allow
     * request parameters.
     * </p>
     * <p>
     * Example parameter would be for MaintenaceView whether a New, Edit, or
     * Copy was requested (maintenance mode)
     * </p>
     *
     * @return
     */
    public Map<String, String> getViewRequestParameters() {
        return this.viewRequestParameters;
    }

    /**
     * Setter for the view's request parameters map
     *
     * @param viewRequestParameters
     */
    public void setViewRequestParameters(Map<String, String> viewRequestParameters) {
        this.viewRequestParameters = viewRequestParameters;
    }

    /**
     * Indicates whether the form (model) associated with the view should be stored in the user session
     *
     * <p>
     * The form class (or model) is used to hold the data that backs the view along with the built view object. Storing
     * the form instance in session allows many things:
     *
     * <ul>
     *   <li>Data does not need to be rebuilt for each server request (for example a collection)</li>
     *   <li>Data that does not need to go to the user can remain on the form, reducing the size of the response and
     *   improving security</li>
     *   <li>Data can be keep around in a 'pre-save' state. When requested by the user changes can then be persisted to
     *   the database</li>
     *   <li>Certain information about the view that was rendered, such as input fields, collection paths, and refresh
     *   components can be kept on the form to support UI interaction</li>
     * </ul>
     *
     * Setting this flag to false will prevent the form from being kept in session and as a result will limit what can
     * be done by the framework. In almost all cases this is not recommended.
     * </p>
     *
     * <p>
     * Note all forms will be cleared when the user session expires (based on the rice configuration). In addition, the
     * framework enables clear points on certain actions to remove the form when it is no longer needed
     * </p>
     *
     * @return boolean true if the form should be stored in the user session, false if only request based
     */
    public boolean isPersistFormToSession() {
        return persistFormToSession;
    }

    /**
     * Setter for the persist form to session indicator
     *
     * @param persistFormToSession
     */
    public void setPersistFormToSession(boolean persistFormToSession) {
        this.persistFormToSession = persistFormToSession;
    }

    /**
     * PresentationController that should be used for the <code>View</code> instance
     *
     * <p>
     * The presentation controller is consulted to determine component (group,
     * field) state such as required, read-only, and hidden. The presentation
     * controller does not take into account user permissions. The presentation
     * controller can also output action flags and edit modes that will be set
     * onto the view instance and can be referred to by conditional expressions
     * </p>
     *
     * @return PresentationController
     */
    public ViewPresentationController getPresentationController() {
        return this.presentationController;
    }

    /**
     * Setter for the view's presentation controller
     *
     * @param presentationController
     */
    public void setPresentationController(ViewPresentationController presentationController) {
        this.presentationController = presentationController;
    }

    /**
     * Setter for the view's presentation controller by class
     *
     * @param presentationControllerClass
     */
    public void setPresentationControllerClass(
            Class<? extends ViewPresentationController> presentationControllerClass) {
        this.presentationController = ObjectUtils.newInstance(presentationControllerClass);
    }

    /**
     * Authorizer that should be used for the <code>View</code> instance
     *
     * <p>
     * The authorizer class is consulted to determine component (group, field)
     * state such as required, read-only, and hidden based on the users
     * permissions. It typically communicates with the Kuali Identity Management
     * system to determine roles and permissions. It is used with the
     * presentation controller and dictionary conditional logic to determine the
     * final component state. The authorizer can also output action flags and
     * edit modes that will be set onto the view instance and can be referred to
     * by conditional expressions
     * </p>
     *
     * @return Authorizer
     */
    public ViewAuthorizer getAuthorizer() {
        return this.authorizer;
    }

    /**
     * Setter for the view's authorizer
     *
     * @param authorizer
     */
    public void setAuthorizer(ViewAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    /**
     * Setter for the view's authorizer by class
     *
     * @param authorizerClass
     */
    public void setAuthorizerClass(Class<? extends ViewAuthorizer> authorizerClass) {
        this.authorizer = ObjectUtils.newInstance(authorizerClass);
    }

    /**
     * Map of strings that flag what actions can be taken in the UI
     * <p>
     * These can be used in conditional expressions in the dictionary or by
     * other UI logic
     * </p>
     *
     * @return BooleanMap action flags
     */
    public BooleanMap getActionFlags() {
        return this.actionFlags;
    }

    /**
     * Setter for the action flags Map
     *
     * @param actionFlags
     */
    public void setActionFlags(BooleanMap actionFlags) {
        this.actionFlags = actionFlags;
    }

    /**
     * Map of edit modes that enabled for the view
     * <p>
     * These can be used in conditional expressions in the dictionary or by
     * other UI logic
     * </p>
     *
     * @return BooleanMap edit modes
     */
    public BooleanMap getEditModes() {
        return this.editModes;
    }

    /**
     * Setter for the edit modes Map
     *
     * @param editModes
     */
    public void setEditModes(BooleanMap editModes) {
        this.editModes = editModes;
    }

    /**
     * Map that contains expressions to evaluate and make available as variables
     * for conditional expressions within the view
     * <p>
     * Each Map entry contains one expression variables, where the map key gives
     * the name for the variable, and the map value gives the variable
     * expression. The variables expressions will be evaluated before
     * conditional logic is run and made available as variables for other
     * conditional expressions. Variable expressions can be based on the model
     * and any object contained in the view's context
     * </p>
     *
     * @return Map<String, String> variable expressions
     */
    public Map<String, String> getExpressionVariables() {
        return this.expressionVariables;
    }

    /**
     * Setter for the view's map of variable expressions
     *
     * @param expressionVariables
     */
    public void setExpressionVariables(Map<String, String> expressionVariables) {
        this.expressionVariables = expressionVariables;
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
    public PageGroup getPage() {
        return this.page;
    }

    /**
     * Setter for the page group for single page views
     *
     * @param page
     */
    public void setPage(PageGroup page) {
        this.page = page;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.ContainerBase#getItems()
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
     * @return Link view link field
     */
    public Link getViewMenuLink() {
        return this.viewMenuLink;
    }

    /**
     * Setter for the views link field
     *
     * @param viewMenuLink
     */
    public void setViewMenuLink(Link viewMenuLink) {
        this.viewMenuLink = viewMenuLink;
    }

    /**
     * Provides a grouping string for the view to group its menu link (within a
     * portal for instance)
     *
     * @return String menu grouping
     */
    public String getViewMenuGroupName() {
        return this.viewMenuGroupName;
    }

    /**
     * Setter for the views menu grouping
     *
     * @param viewMenuGroupName
     */
    public void setViewMenuGroupName(String viewMenuGroupName) {
        this.viewMenuGroupName = viewMenuGroupName;
    }

    /**
     * Indicates what lifecycle phase the View instance is in
     * <p>
     * The view lifecycle begins with the CREATED status. In this status a new
     * instance of the view has been retrieved from the dictionary, but no
     * further processing has been done. After the initialize phase has been run
     * the status changes to INITIALIZED. After the model has been applied and
     * the view is ready for render the status changes to FINAL
     * </p>
     *
     * @return String view status
     * @see org.kuali.rice.krad.uif.UifConstants.ViewStatus
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
        return StringUtils.equals(viewStatus, ViewStatus.INITIALIZED) ||
                StringUtils.equals(viewStatus, ViewStatus.FINAL);
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
     * Breadcrumb widget used for displaying homeward path and history
     *
     * @return the breadcrumbs
     */
    public BreadCrumbs getBreadcrumbs() {
        return this.breadcrumbs;
    }

    /**
     * @param breadcrumbs the breadcrumbs to set
     */
    public void setBreadcrumbs(BreadCrumbs breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    /**
     * Indicates whether the breadcrumbs should be rendered in the view or if they have been rendered in
     * the application header
     *
     * <p>
     * For layout purposes it is sometimes necessary to render the breadcrumbs in the application header. This flag
     * indicates that is being done (by setting to false) and therefore should not be rendered in the view template.
     * </p>
     *
     * @return boolean true if breadcrumbs should be rendered in the view, false if not (are rendered in the
     * application header)
     */
    public boolean isRenderBreadcrumbsInView() {
        return renderBreadcrumbsInView;
    }

    /**
     * Setter for the render breadcrumbs in view indicator
     *
     * @param renderBreadcrumbsInView
     */
    public void setRenderBreadcrumbsInView(boolean renderBreadcrumbsInView) {
        this.renderBreadcrumbsInView = renderBreadcrumbsInView;
    }

    /**
     * Growls widget which sets up global settings for the growls used in this
     * view and its pages
     *
     * @return the growls
     */
    public Growls getGrowls() {
        return this.growls;
    }

    /**
     * @param growls the growls to set
     */
    public void setGrowls(Growls growls) {
        this.growls = growls;
    }

    /**
     * whether to use growls to show messages - info, warning and error
     *
     * <p>Growls use the messages contained in the message map. If enabled, info
     * messages in their entirety will be displayed in growls, for warning and
     * error messages a growl message will notify the user that these messages
     * exist on the page.</p>
     *
     * <p> If this setting is disabled, it is recommended that
     * infoMessage display be enabled for the page ValidationMessages bean in order to
     * display relevant information to the user. Note: the growl scripts are
     * built out in the PageGroup class.</p>
     *
     * @return the growlMessagingEnabled
     */
    public boolean isGrowlMessagingEnabled() {
        return this.growlMessagingEnabled;
    }

    /**
     * enable or disable showing of messages using growls
     *
     * @param growlMessagingEnabled the growlMessagingEnabled to set
     */
    public void setGrowlMessagingEnabled(boolean growlMessagingEnabled) {
        this.growlMessagingEnabled = growlMessagingEnabled;
    }

    /**
     * Indicates whether the form should be validated for dirtyness
     *
     * <p>
     * For FormView, it's necessary to validate when the user tries to navigate out of the form. If set, all the
     * InputFields will be validated on refresh, navigate, cancel or close Action or on form
     * unload and if dirty, displays a message and user can decide whether to continue with
     * the action or stay on the form. For lookup and inquiry, it's not needed to validate.
     * </p>
     *
     * @return true if dirty validation is set
     */
    public boolean isApplyDirtyCheck() {
        return this.applyDirtyCheck;
    }

    /**
     * Setter for dirty validation.
     */
    public void setApplyDirtyCheck(boolean applyDirtyCheck) {
        this.applyDirtyCheck = applyDirtyCheck;
    }

    /**
     * Indicates whether the Name of the Code should be displayed when a property is of type <code>KualiCode</code>
     *
     * @param translateCodesOnReadOnlyDisplay - indicates whether <code>KualiCode</code>'s name should be included
     */
    public void setTranslateCodesOnReadOnlyDisplay(boolean translateCodesOnReadOnlyDisplay) {
        this.translateCodesOnReadOnlyDisplay = translateCodesOnReadOnlyDisplay;
    }

    /**
     * Returns whether the current view supports displaying <code>KualiCode</code>'s name as additional display value
     *
     * @return true if the current view supports
     */
    public boolean isTranslateCodesOnReadOnlyDisplay() {
        return translateCodesOnReadOnlyDisplay;
    }

    /**
     * The property name to be used to determine what will be used in the
     * breadcrumb title of this view
     *
     * <p>
     * The title can be determined from a combination of this and viewLabelFieldbindingInfo: If only
     * viewLabelFieldPropertyName is set, the title we be determined against the
     * defaultBindingObjectPath. If only viewLabelFieldbindingInfo is set it
     * must provide information about what object(bindToForm or explicit) and
     * path to use. If both viewLabelFieldbindingInfo and viewLabelFieldPropertyName are set,
     * the bindingInfo will be used with a
     * the viewLabelFieldPropertyName as its bindingPath. If neither are set,
     * the default title attribute from the dataObject's metadata (determined by the
     * defaultBindingObjectPath's object) will be used.
     * </p>
     *
     * @return String property name whose value should be displayed in view label
     */
    public String getBreadcrumbTitlePropertyName() {
        return this.breadcrumbTitlePropertyName;
    }

    /**
     * Setter for the view label property name
     *
     * @param breadcrumbTitlePropertyName the viewLabelFieldPropertyName to set
     */
    public void setBreadcrumbTitlePropertyName(String breadcrumbTitlePropertyName) {
        this.breadcrumbTitlePropertyName = breadcrumbTitlePropertyName;
    }

    /**
     * The option to use when appending the view label on the breadcrumb title.
     * Available options: 'dash', 'parenthesis', and 'replace'(don't append -
     * simply replace the title). MUST be set for the viewLabelField to be used
     * in the breadcrumb, if not set no appendage will be added.
     *
     * @return the appendOption
     */
    public String getBreadcrumbTitleDisplayOption() {
        return this.breadcrumbTitleDisplayOption;
    }

    /**
     * Setter for the append option
     *
     * @param breadcrumbTitleDisplayOption the appendOption to set
     */
    public void setBreadcrumbTitleDisplayOption(String breadcrumbTitleDisplayOption) {
        this.breadcrumbTitleDisplayOption = breadcrumbTitleDisplayOption;
    }

    /**
     * Map of key name/value pairs that will be exposed on the client with JavaScript
     *
     * <p>
     * Any state contained in the Map will be in addition to general state added by the
     * <code>ViewHelperService</code> and also state generated from the component properties
     * annotated with <code>ClientSideState</code>. If this map does contain a key that is
     * the same as the generated state, it will override the generated, with the exception
     * of keys that refer to component ids and have a nested map as value, which will be merged
     * </p>
     *
     * @return Map<String, Object> contains key name/value pairs to expose on client
     */
    public Map<String, Object> getClientSideState() {
        return clientSideState;
    }

    /**
     * Setter for the client side state map
     *
     * @param clientSideState
     */
    public void setClientSideState(Map<String, Object> clientSideState) {
        this.clientSideState = clientSideState;
    }

    /**
     * Adds a variable name/value pair to the client side state map associated with the given
     * component id
     *
     * @param componentId - id of the component the state is associated with
     * @param variableName - name to expose the state as
     * @param value - initial value for the variable on the client
     */
    public void addToClientSideState(String componentId, String variableName, Object value) {
        Map<String, Object> componentClientState = new HashMap<String, Object>();

        // find any existing client state for component
        if (clientSideState.containsKey(componentId)) {
            Object clientState = clientSideState.get(componentId);
            if ((clientState != null) && (clientState instanceof Map)) {
                componentClientState = (Map<String, Object>) clientState;
            } else {
                throw new IllegalArgumentException("Client side state for component: " + componentId + " is not a Map");
            }
        }

        // add variables to component state and reinsert into view's client state
        componentClientState.put(variableName, value);
        clientSideState.put(componentId, componentClientState);
    }

    /**
     * Indicates whether the view allows read only fields to be specified on the request URL which will
     * override the view setting
     *
     * <p>
     * If enabled, the readOnlyFields request parameter can be sent to indicate fields that should be set read only
     * </p>
     *
     * @return boolean true if read only request overrides are allowed, false if not
     */
    public boolean isSupportsRequestOverrideOfReadOnlyFields() {
        return supportsRequestOverrideOfReadOnlyFields;
    }

    /**
     * Setter for the the read only field override indicator
     *
     * @param supportsRequestOverrideOfReadOnlyFields
     */
    public void setSupportsRequestOverrideOfReadOnlyFields(boolean supportsRequestOverrideOfReadOnlyFields) {
        this.supportsRequestOverrideOfReadOnlyFields = supportsRequestOverrideOfReadOnlyFields;
    }

    /**
     * Script that is executed at the beginning of page load (before any other script)
     *
     * <p>
     * Many used to set server variables client side
     * </p>
     *
     * @return String pre load script
     */
    public String getPreLoadScript() {
        return preLoadScript;
    }

    /**
     * Setter for the pre load script
     *
     * @param preLoadScript
     */
    public void setPreLoadScript(String preLoadScript) {
        this.preLoadScript = preLoadScript;
    }

    /**
     * The theme which contains stylesheets for this view
     * @return
     */
    public ViewTheme getTheme() {
        return theme;
    }

    /**
     * Setter for The theme which contains stylesheets for this view
     * @return
     */
    public void setTheme(ViewTheme theme) {
        this.theme = theme;
    }

    /**
     * The stateObject's binding path, this will be used along with the StateMapping's statePropertyName to
     * determine what field in the model state information is stored in for this view.  Used during View validation.
     *
     * @return stateObjectBindingPath path to the object storing state information
     */
    public String getStateObjectBindingPath() {
        return stateObjectBindingPath;
    }

    /**
     *  The stateObject's binding path, this will be used along with the StateMapping's statePropertyName to
     * determine what field in the model state information is stored in for this view.  Used during View validation.
     *
     * @param stateObjectBindingPath
     */
    public void setStateObjectBindingPath(String stateObjectBindingPath) {
        this.stateObjectBindingPath = stateObjectBindingPath;
    }

    /**
     * Gets the stateMapping.
     *
     * <p>The state mapping object is used to determine the state information for a view,
     * it must include an ordered list of states, and where to find the state information for the view.
     * A stateMapping must be set for state based validation to occur.  When stateMapping information is
     * not included, the view's model is considered stateless and all constraints will apply regardless of their
     * state information or replacements (ie, they will function as they did in version 2.1).</p>
     *
     * @since 2.2
     * @return StateMapping information needed for state based validation, if null no state based validation functionality
     * will exist and configured constraints will apply regardless of state
     */
    public StateMapping getStateMapping() {
        return stateMapping;
    }

    /**
     * Set the stateMapping
     *
     * @param stateMapping
     */
    public void setStateMapping(StateMapping stateMapping) {
        this.stateMapping = stateMapping;
    }
}
