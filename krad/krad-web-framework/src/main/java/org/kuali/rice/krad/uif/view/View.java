/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.authorization.Authorizer;
import org.kuali.rice.krad.uif.authorization.PresentationController;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.NavigationGroup;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.field.HeaderField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.widget.BreadCrumbs;
import org.kuali.rice.krad.uif.widget.Growls;
import org.kuali.rice.krad.util.ObjectUtils;

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

    private String viewName;

    private int idSequence;

    // application
    private HeaderField applicationHeader;
    private Group applicationFooter;

    // Breadcrumbs
    private BreadCrumbs breadcrumbs;
    private String viewLabelFieldPropertyName;
    private String appendOption;
    private boolean breadcrumbsInApplicationHeader;

    // Growls support
    private Growls growls;
    private boolean growlMessagingEnabled;

    private String entryPageId;

    @RequestParameter
    private String currentPageId;

    private NavigationGroup navigation;

    private Class<?> formClass;
    private String defaultBindingObjectPath;
    private Map<String, Class<?>> abstractTypeClasses;

    private List<String> additionalScriptFiles;
    private List<String> additionalCssFiles;

    private ViewType viewTypeName;
    private Class<? extends ViewHelperService> viewHelperServiceClassName;

    private String viewStatus;
    private ViewIndex viewIndex;
    private Map<String, String> viewRequestParameters;

    private Class<? extends PresentationController> presentationControllerClass;
    private Class<? extends Authorizer> authorizerClass;

    private BooleanMap actionFlags;
    private BooleanMap editModes;

    private Map<String, String> expressionVariables;

    private boolean singlePageView;
    private PageGroup page;

    private List<? extends Group> items;

    private LinkField viewMenuLink;
    private String viewMenuGrouping;

    private boolean validateDirty;
    private boolean translateCodes;
    private String preLoadScript;
    private Map<String, Object> clientSideState;

    private boolean supportsReadOnlyFieldsOverride;

    @RequestParameter
    private boolean dialogMode;

    @ReferenceCopy
    private ViewHelperService viewHelperService;

    public View() {
        dialogMode = false;
        singlePageView = false;
        translateCodes = false;
        viewTypeName = ViewType.DEFAULT;
        viewStatus = UifConstants.ViewStatus.CREATED;
        breadcrumbsInApplicationHeader = false;
        supportsReadOnlyFieldsOverride = true;

        idSequence = 0;
        this.viewIndex = new ViewIndex();

        additionalScriptFiles = new ArrayList<String>();
        additionalCssFiles = new ArrayList<String>();
        items = new ArrayList<Group>();
        abstractTypeClasses = new HashMap<String, Class<?>>();
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

        // assign IDs if necessary
        if (StringUtils.isBlank(component.getId())) {
            component.setId(getNextId());

        }
        if (StringUtils.isBlank(component.getFactoryId())) {
            component.setFactoryId(component.getId());
        }

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();
            if ((layoutManager != null) && StringUtils.isBlank(layoutManager.getId())) {
                layoutManager.setId(getNextId());
            }
        }

        // check if component has already been initialized to prevent cyclic references
        // TODO: move to VHS initialize
        //        if (initializedComponentIds.contains(component.getId())) {
        //            throw new RiceRuntimeException(
        //                    "Circular reference or duplicate id found for component with id: " + component.getId());
        //        }
        //        initializedComponentIds.add(component.getId());

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
        List<Component> components = super.getComponentsForLifecycle();

        components.add(applicationHeader);
        components.add(applicationFooter);
        components.add(navigation);
        components.add(breadcrumbs);
        components.add(growls);
        components.add(viewMenuLink);

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
    public HeaderField getApplicationHeader() {
        return applicationHeader;
    }

    /**
     * Setter for the application header
     *
     * @param applicationHeader
     */
    public void setApplicationHeader(HeaderField applicationHeader) {
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
     * Returns the next unique id available for components within the view instance
     *
     * @return String next id available
     */
    public String getNextId() {
        return Integer.toString(idSequence++);
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
     * @see org.kuali.rice.krad.uif.BindingInfo.getBindingObjectPath()
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

    public boolean isDialogMode() {
        return this.dialogMode;
    }

    public void setDialogMode(boolean dialogMode) {
        this.dialogMode = dialogMode;
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
    public Class<? extends ViewHelperService> getViewHelperServiceClassName() {
        return this.viewHelperServiceClassName;
    }

    /**
     * Setter for the <code>ViewHelperService</code> class name
     *
     * @param viewLifecycleService
     */
    public void setViewHelperServiceClassName(Class<? extends ViewHelperService> viewHelperServiceClassName) {
        this.viewHelperServiceClassName = viewHelperServiceClassName;
    }

    /**
     * Creates the <code>ViewHelperService</code> associated with the View
     *
     * @return ViewHelperService instance
     */
    public ViewHelperService getViewHelperService() {
        if (this.viewHelperService == null) {
            viewHelperService = ObjectUtils.newInstance(viewHelperServiceClassName);
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
     * PresentationController class that should be used for the
     * <code>View</code> instance
     * <p>
     * The presentation controller is consulted to determine component (group,
     * field) state such as required, read-only, and hidden. The presentation
     * controller does not take into account user permissions. The presentation
     * controller can also output action flags and edit modes that will be set
     * onto the view instance and can be referred to by conditional expressions
     * </p>
     *
     * @return Class<? extends PresentationController>
     * @see View.getActionFlags()
     * @see View.getEditModes()
     */
    public Class<? extends PresentationController> getPresentationControllerClass() {
        return this.presentationControllerClass;
    }

    /**
     * Setter for the view's presentation controller
     *
     * @param presentationControllerClass
     */
    public void setPresentationControllerClass(Class<? extends PresentationController> presentationControllerClass) {
        this.presentationControllerClass = presentationControllerClass;
    }

    /**
     * Authorizer class that should be used for the <code>View</code> instance
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
     * @return Class<? extends Authorizer>
     * @see View.getActionFlags()
     * @see View.getEditModes()
     */
    public Class<? extends Authorizer> getAuthorizerClass() {
        return this.authorizerClass;
    }

    /**
     * Setter for the view's authorizer
     *
     * @param authorizerClass
     */
    public void setAuthorizerClass(Class<? extends Authorizer> authorizerClass) {
        this.authorizerClass = authorizerClass;
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
     * onSubmit script configured on the <code>View</code> gets placed on the
     * form element
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getSupportsOnSubmit()
     */
    @Override
    public boolean getSupportsOnSubmit() {
        return true;
    }

    /**
     * onLoad script configured on the <code>View</code> gets placed in a load
     * call
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getSupportsOnLoad()
     */
    @Override
    public boolean getSupportsOnLoad() {
        return true;
    }

    /**
     * onDocumentReady script configured on the <code>View</code> gets placed in
     * a document ready jQuery block
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getSupportsOnLoad()
     */
    @Override
    public boolean getSupportsOnDocumentReady() {
        return true;
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
     * Indicates whether the breadcrumbs are rendered in the application header and should not
     * be rendered as part of the view template
     *
     * <p>
     * For layout purposes it is sometimes necessary to render the breadcrumbs in the application header. This flag
     * indicates that is being done and therefore should not be rendered in the view template.
     * </p>
     *
     * @return boolean true if breadcrumbs are rendered in the application header, false if not and they should be
     *         rendered with the view
     */
    public boolean isBreadcrumbsInApplicationHeader() {
        return breadcrumbsInApplicationHeader;
    }

    /**
     * Setter for the breadcrumbs in application header indicator
     *
     * @param breadcrumbsInApplicationHeader
     */
    public void setBreadcrumbsInApplicationHeader(boolean breadcrumbsInApplicationHeader) {
        this.breadcrumbsInApplicationHeader = breadcrumbsInApplicationHeader;
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
     * Growls use the messages contained in the message map. If enabled, info
     * messages in their entirety will be displayed in growls, for warning and
     * error messages a growl message will notify the user that these messages
     * exist on the page. If this setting is disabled, it is recommended that
     * infoMessage display be enabled for the page ErrorsField bean in order to
     * display relevant information to the user. Note: the growl scripts are
     * built out in the PageGroup class.
     *
     * @return the growlMessagingEnabled
     */
    public boolean isGrowlMessagingEnabled() {
        return this.growlMessagingEnabled;
    }

    /**
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
     * AttributeFields will be validated on refresh, navigate, cancel or close Action or on form
     * unload and if dirty, displays a message and user can decide whether to continue with
     * the action or stay on the form. For lookup and inquiry, it's not needed to validate.
     * </p>
     *
     * @return true if dirty validation is set
     */
    public boolean isValidateDirty() {
        return this.validateDirty;
    }

    /**
     * Setter for dirty validation.
     */
    public void setValidateDirty(boolean validateDirty) {
        this.validateDirty = validateDirty;
    }

    /**
     * Indicates whether the Name of the Code should be displayed when a property is of type <code>KualiCode</code>
     *
     * @param translateCodes - indicates whether <code>KualiCode</code>'s name should be included
     */
    public void setTranslateCodes(boolean translateCodes) {
        this.translateCodes = translateCodes;
    }

    /**
     * Returns whether the current view supports displaying <code>KualiCode</code>'s name as additional display value
     *
     * @return true if the current view supports
     */
    public boolean isTranslateCodes() {
        return translateCodes;
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
    public String getViewLabelFieldPropertyName() {
        return this.viewLabelFieldPropertyName;
    }

    /**
     * Setter for the view label property name
     *
     * @param viewLabelFieldPropertyName the viewLabelFieldPropertyName to set
     */
    public void setViewLabelFieldPropertyName(String viewLabelFieldPropertyName) {
        this.viewLabelFieldPropertyName = viewLabelFieldPropertyName;
    }

    /**
     * The option to use when appending the view label on the breadcrumb title.
     * Available options: 'dash', 'parenthesis', and 'replace'(don't append -
     * simply replace the title). MUST be set for the viewLabelField to be used
     * in the breadcrumb, if not set no appendage will be added.
     *
     * @return the appendOption
     */
    public String getAppendOption() {
        return this.appendOption;
    }

    /**
     * Setter for the append option
     *
     * @param appendOption the appendOption to set
     * @see View#setViewLabelFieldPropertyName(String)
     * @see View#setViewLabelFieldBindingInfo(BindingInfo)
     */
    public void setAppendOption(String appendOption) {
        this.appendOption = appendOption;
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
    public boolean isSupportsReadOnlyFieldsOverride() {
        return supportsReadOnlyFieldsOverride;
    }

    /**
     * Setter for the the read only field override indicator
     *
     * @param supportsReadOnlyFieldsOverride
     */
    public void setSupportsReadOnlyFieldsOverride(boolean supportsReadOnlyFieldsOverride) {
        this.supportsReadOnlyFieldsOverride = supportsReadOnlyFieldsOverride;
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
}
