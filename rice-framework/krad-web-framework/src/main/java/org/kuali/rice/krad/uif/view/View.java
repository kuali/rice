/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.element.HeadLink;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.element.MetaTag;
import org.kuali.rice.krad.uif.element.ViewHeader;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.element.BreadcrumbItem;
import org.kuali.rice.krad.uif.element.BreadcrumbOptions;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;
import org.kuali.rice.krad.uif.util.LifecycleAwareMap;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ParentLocation;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.widget.BlockUI;
import org.kuali.rice.krad.uif.widget.Breadcrumbs;
import org.kuali.rice.krad.uif.widget.Growls;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root of the component tree which encompasses a set of related
 * GroupContainer instances tied together with a common page layout
 * and navigation.
 *
 * <p>
 * The View component ties together all the components and
 * configuration of the User Interface for a piece of functionality. In Rice
 * applications the view is typically associated with a Document
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
 * the View
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class View extends ContainerBase {
    private static final long serialVersionUID = -1220009725554576953L;
    private static final Logger LOG = LoggerFactory.getLogger(ContainerBase.class);

    private String namespaceCode;
    private String viewName;
    private ViewTheme theme;

    private String stateObjectBindingPath;
    private StateMapping stateMapping;

    // view header setting
    private boolean unifiedHeader;

    // additional view group(s)
    private Group topGroup;

    // application
    private Header applicationHeader;
    private Group applicationFooter;
    private String applicationTitleText;

    // sticky flags
    private boolean stickyTopGroup;
    private boolean stickyBreadcrumbs;
    private boolean stickyHeader;
    private boolean stickyApplicationHeader;
    private boolean stickyFooter;
    private boolean stickyApplicationFooter;

    private List<String> contentContainerCssClasses;

    // Breadcrumbs
    private Breadcrumbs breadcrumbs;
    private BreadcrumbOptions breadcrumbOptions;
    private BreadcrumbItem breadcrumbItem;
    private ParentLocation parentLocation;
    private List<BreadcrumbItem> pathBasedBreadcrumbs;

    // Growls support
    private Growls growls;
    private boolean growlMessagingEnabled;

    private BlockUI refreshBlockUI;
    private BlockUI navigationBlockUI;

    private String entryPageId;

    @RequestParameter
    private String currentPageId;

    private Group navigation;

    private Class<?> formClass;
    private String defaultBindingObjectPath;
    private Map<String, Class<?>> objectPathToConcreteClassMapping;

    private List<String> additionalScriptFiles;
    private List<String> additionalCssFiles;
    private List<HeadLink> additionalHeadLinks;
    private List<MetaTag> additionalMetaTags;
    private boolean useLibraryCssClasses;

    private ViewType viewTypeName;

    protected ViewIndex viewIndex;
    private Map<String, String> viewRequestParameters;

    private boolean persistFormToSession;
    private ViewSessionPolicy sessionPolicy;

    private ViewPresentationController presentationController;
    private ViewAuthorizer authorizer;

    private Map<String, Boolean> actionFlags;
    private Map<String, Boolean> editModes;

    private Map<String, String> expressionVariables;

    private boolean singlePageView;
    private boolean mergeWithPageItems;
    private PageGroup page;
    
    @ReferenceCopy(referenceTransient=true)
    private PageGroup currentPage;

    private List<Group> dialogs;

    protected boolean applyDirtyCheck;
    private boolean translateCodesOnReadOnlyDisplay;
    private boolean supportsRequestOverrideOfReadOnlyFields;
    private boolean disableNativeAutocomplete;
    private boolean disableBrowserCache;

    private String preLoadScript;

    @DelayedCopy
    private List<? extends Component> items;

    private List<String> viewTemplates;

    private Class<? extends ViewHelperService> viewHelperServiceClass;

    @ReferenceCopy
    private ViewHelperService viewHelperService;

    private Map<String, Object> preModelContext;

    public View() {
        singlePageView = false;
        mergeWithPageItems = true;
        translateCodesOnReadOnlyDisplay = false;
        viewTypeName = ViewType.DEFAULT;
        formClass = UifFormBase.class;
        supportsRequestOverrideOfReadOnlyFields = true;
        disableBrowserCache = true;
        persistFormToSession = true;
        sessionPolicy = new ViewSessionPolicy();

        this.viewIndex = new ViewIndex();

        additionalScriptFiles = Collections.emptyList();
        additionalCssFiles = Collections.emptyList();
        additionalHeadLinks = Collections.emptyList();
        additionalMetaTags = Collections.emptyList();
        objectPathToConcreteClassMapping = Collections.emptyMap();
        viewRequestParameters = Collections.emptyMap();
        expressionVariables = Collections.emptyMap();

        dialogs = Collections.emptyList();

        items = Collections.emptyList();
        viewTemplates = new LifecycleAwareList<String>(this);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>If a single paged view, set items in page group and put the page in
     * the items list</li>
     * <li>If {@link ViewSessionPolicy#enableTimeoutWarning} is enabled add the session timeout dialogs to the
     * views list of dialog groups</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performInitialization(Object model) {
        if (model instanceof UifFormBase) {
            UifFormBase form = (UifFormBase) model;

            // set view page to page requested on form
            if (StringUtils.isNotBlank(form.getPageId())) {
                setCurrentPageId(form.getPageId());
            }
        }

        super.performInitialization(model);

        assert this == ViewLifecycle.getView();

        // populate items on page for single paged view
        if (singlePageView) {
            if (page != null) {
                // remove default sections of page when requested
                if (!mergeWithPageItems) {
                    page.setItems(new ArrayList<Group>());
                }

                // add the items configured on the view to the page items, and set as the
                // new page items
                List<Component> newItems = (List<Component>) page.getItems();
                newItems.addAll(items);
                page.setItems(newItems);

                page.sortItems();

                // reset the items list to include the one page
                items = new ArrayList<Group>();
                ((List<Group>) items).add(page);
            }
        }
        // if items is only size one and instance of page, set singlePageView to true
        else if ((this.items != null) && (this.items.size() == 1)) {
            Component itemComponent = this.items.get(0);

            if (itemComponent instanceof PageGroup) {
                this.singlePageView = true;
            }
        }

        if (sessionPolicy.isEnableTimeoutWarning()) {
            Group warningDialog = ComponentFactory.getSessionTimeoutWarningDialog();
            warningDialog.setId(ComponentFactory.SESSION_TIMEOUT_WARNING_DIALOG);
            getDialogs().add(warningDialog);

            Group timeoutDialog = ComponentFactory.getSessionTimeoutDialog();
            timeoutDialog.setId(ComponentFactory.SESSION_TIMEOUT_DIALOG);
            getDialogs().add(timeoutDialog);
        }

        breadcrumbOptions.setupBreadcrumbs(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        super.afterEvaluateExpression();
        
        if (getReadOnly() == null) {
            setReadOnly(false);
        }
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Invoke expression evaluation on view theme</li>
     * <li>Invoke theme to configure defaults</li>
     * </ul>
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        View view = ViewLifecycle.getView();
        if (theme != null) {
            ViewLifecycle.getExpressionEvaluator().evaluateExpressionsOnConfigurable(view, theme, getContext());

            theme.configureThemeDefaults();
        }

        //handle parentLocation breadcrumb chain
        parentLocation.constructParentLocationBreadcrumbItems(view, model, view.getContext());
    }

    /**
     * The following is performed:
     *
     * <ul>
     * <li>Adds to its document ready script the setupValidator js function for setting
     * up the validator for this view</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        assert this == ViewLifecycle.getView();

        String preLoadScript = "";
        if (this.getPreLoadScript() != null) {
            preLoadScript = this.getPreLoadScript();
        }

        // Retrieve Growl and BlockUI settings
        Growls gw = getGrowls();
        if (!gw.getTemplateOptions().isEmpty()) {
            preLoadScript += "setGrowlDefaults(" + gw.getTemplateOptionsJSString() + ");";
        }

        BlockUI navBlockUI = getNavigationBlockUI();
        if (!navBlockUI.getTemplateOptions().isEmpty()) {
            preLoadScript += "setBlockUIDefaults("
                    + navBlockUI.getTemplateOptionsJSString()
                    + ", '"
                    + UifConstants.BLOCKUI_NAVOPTS
                    + "');";
        }

        BlockUI refBlockUI = getRefreshBlockUI();
        if (!refBlockUI.getTemplateOptions().isEmpty()) {
            preLoadScript += "setBlockUIDefaults("
                    + refBlockUI.getTemplateOptionsJSString()
                    + ", '"
                    + UifConstants.BLOCKUI_REFRESHOPTS
                    + "');";
        }

        this.setPreLoadScript(preLoadScript);

        String onReadyScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            onReadyScript = this.getOnDocumentReadyScript();
        }

        // initialize session timers for giving timeout warnings
        if (sessionPolicy.isEnableTimeoutWarning()) {
            // warning minutes gives us the time before the timeout occurs to give the warning,
            // so we need to determine how long that should be from the session start
            int sessionTimeoutInterval = ((UifFormBase) model).getSessionTimeoutInterval();
            int sessionWarningMilliseconds = (sessionPolicy.getTimeoutWarningSeconds() * 1000);

            if (sessionWarningMilliseconds >= sessionTimeoutInterval) {
                throw new RuntimeException(
                        "Time until giving the session warning should be less than the session timeout. Session Warning is "
                                + sessionWarningMilliseconds
                                + "ms, session timeout is "
                                + sessionTimeoutInterval
                                + "ms.");
            }

            int sessionWarningInterval = sessionTimeoutInterval - sessionWarningMilliseconds;

            onReadyScript = ScriptUtils.appendScript(onReadyScript, ScriptUtils.buildFunctionCall(
                    UifConstants.JsFunctions.INITIALIZE_SESSION_TIMERS, sessionWarningInterval,
                    sessionTimeoutInterval));
        }

        onReadyScript = ScriptUtils.appendScript(onReadyScript, "jQuery.extend(jQuery.validator.messages, "
                + ClientValidationUtils.generateValidatorMessagesOption()
                + ");");

        this.setOnDocumentReadyScript(onReadyScript);

        // Breadcrumb handling
        breadcrumbOptions.finalizeBreadcrumbs(model, this, breadcrumbItem);

        // Add validation default js options for validation framework to View's data attributes
        Object groupValidationDataDefaults = KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.GROUP_VALIDATION_DEFAULTS_MAP_ID);
        Object fieldValidationDataDefaults = KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.FIELD_VALIDATION_DEFAULTS_MAP_ID);
        Object actionDataDefaults = KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.ACTION_DEFAULTS_MAP_ID);
        Object requiredIndicator = KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.REQUIRED_INDICATOR_ID);

        // Add data defaults for common components to the view for use in js (to reduce size of individual components)
        this.addScriptDataAttribute(UifConstants.DataAttributes.GROUP_VALIDATION_DEFAULTS, ScriptUtils.convertToJsValue(
                (Map<String, String>) groupValidationDataDefaults));
        this.addScriptDataAttribute(UifConstants.DataAttributes.FIELD_VALIDATION_DEFAULTS, ScriptUtils.convertToJsValue(
                (Map<String, String>) fieldValidationDataDefaults));
        this.addScriptDataAttribute(UifConstants.DataAttributes.ACTION_DEFAULTS, ScriptUtils.convertToJsValue(
                (Map<String, String>) actionDataDefaults));
        this.addScriptDataAttribute(UifConstants.DataAttributes.REQ_INDICATOR, (String) requiredIndicator);

        // give view role attribute for js selections
        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.VIEW);

        // Add state mapping to post metadata
        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, "stateObjectBindingPath",
                stateObjectBindingPath);
        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, "stateMapping", stateMapping);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyCompleted(ViewLifecyclePhase phase) {
        super.notifyCompleted(phase);

        if (phase.getViewPhase().equals(UifConstants.ViewPhases.INITIALIZE)) {

            // get the list of dialogs from the view and then set the refreshedByAction on the
            // dialog to true.
            // This will leave the component in the viewIndex to be updated using an AJAX call
            // TODO: Figure out a better way to store dialogs only if it is rendered using an
            // ajax request
            for (Component dialog : getDialogs()) {
                dialog.setRefreshedByAction(true);
            }
        }

        if (phase.getViewPhase().equals(UifConstants.ViewPhases.FINALIZE)) {
            ViewLifecycle.getHelper().performCustomViewFinalize(ViewLifecycle.getModel());
        }
    }

    /**
     * Gets all breadcrumb items related to this view's parent location.
     *
     * @return breadcrumb items
     */
    public List<BreadcrumbItem> getBreadcrumbItems() {
        if (parentLocation == null) {
            return Collections.emptyList();
        }

        List<BreadcrumbItem> breadcrumbItems = new ArrayList<BreadcrumbItem>();
        breadcrumbItems.add(parentLocation.getPageBreadcrumbItem());
        breadcrumbItems.add(parentLocation.getViewBreadcrumbItem());
        for (BreadcrumbItem item : parentLocation.getResolvedBreadcrumbItems()) {
            if (!breadcrumbItems.contains(item)) {
                breadcrumbItems.add(item);
            }
        }

        return breadcrumbItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComponentTypeName() {
        return "view";
    }

    /**
     * Iterates through the contained page items and returns the Page that matches the set current page id or
     * the first page in the case of a single page view.
     *
     * @return page group instance
     */
    @ViewLifecycleRestriction(exclude = UifConstants.ViewPhases.PRE_PROCESS)
    public PageGroup getCurrentPage() {
        if (currentPage != null) {
            return currentPage;
        }
        
        for (Component item : this.getItems()) {
            if (!(item instanceof PageGroup)) {
                continue;
            }

            if (singlePageView || StringUtils.equals(item.getId(), getCurrentPageId())) {
                currentPage = (PageGroup) CopyUtils.unwrap(item);
            }
        }

        return currentPage;
    }

    /**
     * Getter for returning the view's items and page for inclusion in the pre-process phase.
     *
     * <p>Note this is necessary so we get IDs assigned for all the pages during the pre-process phase. For other
     * phases, only the current page is picked up.</p>
     *
     * @return list of components to include for the pre-process phase
     */
    @ViewLifecycleRestriction(UifConstants.ViewPhases.PRE_PROCESS)
    public List<Component> getPagesForPreprocessing() {
        List<Component> processProcessItems = new ArrayList<Component>();

        if (getItems() != null) {
            processProcessItems.addAll(getItems());
        }

        if (getPage() != null) {
            processProcessItems.add(getPage());
        }

        return processProcessItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortItems() {
        if (!singlePageView) {
            super.sortItems();
        }
    }

    /**
     * Namespace code the view should be associated with.
     *
     * <p>The namespace code is used within the framework in such places as permission checks and parameter
     * retrieval</p>
     *
     * @return namespace code
     */
    @BeanTagAttribute
    public String getNamespaceCode() {
        return namespaceCode;
    }

    /**
     * @see View#getNamespaceCode()
     */
    public void setNamespaceCode(String namespaceCode) {
        checkMutable(true);
        this.namespaceCode = namespaceCode;
    }

    /**
     * View name provides an identifier for a view within a type. That is if a
     * set of View instances have the same values for the
     * properties that are used to retrieve them by their type, the name can be
     * given to further qualify the view that should be retrieved.
     *
     * <p>A view type like the LookupView might have several views for
     * the same object class, but one that is the 'default' lookup and another
     * that is the 'advanced' lookup. Therefore the name on the first could be
     * set to 'default', and likewise the name for the second 'advanced'</p>
     *
     * @return name of view
     */
    @BeanTagAttribute
    public String getViewName() {
        return this.viewName;
    }

    /**
     * @see View#getViewName()
     */
    public void setViewName(String viewName) {
        checkMutable(true);
        this.viewName = viewName;
    }

    /**
     * When true, this view will use a unified header - the page header will be omitted and its title will be used
     * in the ViewHeader supportTitle property (dynamically updated on page change).
     *
     * @return true if using a unified header
     */
    @BeanTagAttribute
    public boolean isUnifiedHeader() {
        return unifiedHeader;
    }

    /**
     * @see View#isUnifiedHeader()
     */
    public void setUnifiedHeader(boolean unifiedHeader) {
        checkMutable(true);
        this.unifiedHeader = unifiedHeader;
    }

    /**
     * TopGroup is an optional group of content that appears above the breadcrumbs and view header.
     *
     * @return the topGroup component
     */
    @BeanTagAttribute
    public Group getTopGroup() {
        return topGroup;
    }

    /**
     * @see View#getTopGroup()
     */
    public void setTopGroup(Group topGroup) {
        checkMutable(true);
        this.topGroup = topGroup;
    }

    /**
     * Header for the application containing the view.
     *
     * <p>When deploying outside a portal, the application header and footer property can be configured to
     * display a consistent header/footer across all views. Here application logos, menus, login controls
     * and so on can be rendered</p>
     *
     * @return application header
     */
    @BeanTagAttribute
    public Header getApplicationHeader() {
        return applicationHeader;
    }

    /**
     * @see View#getApplicationHeader()
     */
    public void setApplicationHeader(Header applicationHeader) {
        checkMutable(true);
        this.applicationHeader = applicationHeader;
    }

    /**
     * Footer for the application containing the view.
     *
     * <p>When deploying outside a portal, the application header and footer property can be configured to
     * display a consistent header/footer across all views. Here such things as application links, copyrights
     * and so on can be rendered</p>
     *
     * @return application footer
     */
    @BeanTagAttribute
    public Group getApplicationFooter() {
        return applicationFooter;
    }

    /**
     * @see View#getApplicationFooter()
     */
    public void setApplicationFooter(Group applicationFooter) {
        checkMutable(true);
        this.applicationFooter = applicationFooter;
    }

    /**
     * Title text to be displayed on browser tab.
     *
     * @return
     */
    @BeanTagAttribute(name = "applicationTitleText")
    public String getApplicationTitleText() {
        return applicationTitleText;
    }

    /**
     * @see View#getApplicationTitleText()
     */
    public void setApplicationTitleText(String applicationTitleText) {
        this.applicationTitleText = applicationTitleText;
    }

    /**
     * If true, the top group will be sticky (fixed to top of window).
     *
     * @return true if the top group is sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyTopGroup() {
        return stickyTopGroup;
    }

    /**
     * @see View#isStickyTopGroup()
     */
    public void setStickyTopGroup(boolean stickyTopGroup) {
        checkMutable(true);
        this.stickyTopGroup = stickyTopGroup;
    }

    /**
     * If true, the breadcrumb widget will be sticky (fixed to top of window).
     *
     * @return true if breadcrumbs are sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyBreadcrumbs() {
        return stickyBreadcrumbs;
    }

    /**
     * @see View#isStickyBreadcrumbs()
     */
    public void setStickyBreadcrumbs(boolean stickyBreadcrumbs) {
        checkMutable(true);
        this.stickyBreadcrumbs = stickyBreadcrumbs;
    }

    /**
     * If true, the ViewHeader for this view will be sticky (fixed to top of window).
     *
     * @return true if the header is sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyHeader() {
        if (this.getHeader() != null && this.getHeader() instanceof ViewHeader) {
            return ((ViewHeader) this.getHeader()).isSticky();
        } else {
            return false;
        }
    }

    /**
     * @see View#isStickyHeader()
     */
    public void setStickyHeader(boolean stickyHeader) {
        checkMutable(true);
        this.stickyHeader = stickyHeader;
        if (this.getHeader() != null && this.getHeader() instanceof ViewHeader) {
            ((ViewHeader) this.getHeader()).setSticky(stickyHeader);
        }
    }

    /**
     * Set to true to make the applicationHeader sticky (fixed to top of window)
     *
     * @return true if applicationHeader is sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyApplicationHeader() {
        return stickyApplicationHeader;
    }

    /**
     * @see View#isStickyApplicationHeader()
     */
    public void setStickyApplicationHeader(boolean stickyApplicationHeader) {
        checkMutable(true);
        this.stickyApplicationHeader = stickyApplicationHeader;
    }

    /**
     * If true, the view footer will become sticky (fixed to bottom of window).
     *
     * @return ture if the view footer is sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyFooter() {
        return stickyFooter;
    }

    /**
     * @see View#isStickyFooter()
     */
    public void setStickyFooter(boolean stickyFooter) {
        checkMutable(true);
        this.stickyFooter = stickyFooter;
        if (this.getFooter() != null) {
            this.getFooter().addDataAttribute(UifConstants.DataAttributes.STICKY_FOOTER, Boolean.toString(
                    stickyFooter));
        }
    }

    /**
     * If true, the applicationFooter will become sticky (fixed to bottom of window).
     *
     * @return true if the application footer is sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isStickyApplicationFooter() {
        return stickyApplicationFooter;
    }

    /**
     * @see View#isStickyApplicationFooter()
     */
    public void setStickyApplicationFooter(boolean stickyApplicationFooter) {
        checkMutable(true);
        this.stickyApplicationFooter = stickyApplicationFooter;
    }

    /**
     * List of CSS style classes that will be applied to a div that wraps the content.
     *
     * <p>Wrapping the content gives the ability to move between a fluid width container or a fixed width
     * container. The div is also wraps content inside sticky elements (header and footer), so visual treatment
     * can be given to the full width of the screen while restricting the area of the content.</p>
     *
     * <p>In Bootstrap, use 'container-fluid' for a fluid width container, and 'container' for a fixed width
     * container.</p>
     *
     * @return List of css classes to apply to content wrapper div
     */
    @BeanTagAttribute
    public List<String> getContentContainerCssClasses() {
        return contentContainerCssClasses;
    }

    /**
     * @see View#getContentContainerCssClasses()
     */
    public void setContentContainerCssClasses(List<String> contentContainerCssClasses) {
        this.contentContainerCssClasses = contentContainerCssClasses;
    }

    /**
     * Returns the list of {@link View#getContentContainerCssClasses()} as a concatenated string (each class
     * is separated by a space).
     *
     * @return String of content css classes
     */
    public String getContentContainerClassesAsString() {
        if (contentContainerCssClasses != null) {
            return StringUtils.join(contentContainerCssClasses, " ");
        }

        return "";
    }

    /**
     * Specifies what page should be rendered by default. This is the page that
     * will be rendered when the View is first rendered or when the
     * current page is not set
     *
     * @return id of the page to render by default
     */
    @BeanTagAttribute
    public String getEntryPageId() {
        return this.entryPageId;
    }

    /**
     * @see View#getEntryPageId()
     */
    public void setEntryPageId(String entryPageId) {
        checkMutable(true);
        this.entryPageId = entryPageId;
    }

    /**
     * The id for the page within the view that should be displayed in the UI. Other pages of the view will not be
     * rendered.
     *
     * <p>If current page id is not set, it is set to the configured entry page or first item in list id</p>
     *
     * @return id of the page that should be displayed
     */
    public String getCurrentPageId() {
        // default current page if not set
        if (StringUtils.isBlank(currentPageId)) {
            if (StringUtils.isNotBlank(entryPageId)) {
                currentPageId = entryPageId;
            } else if ((getItems() != null) && !getItems().isEmpty()) {
                Component firstPageGroup = getItems().get(0);
                if (firstPageGroup instanceof PageGroup) {
                    currentPageId = firstPageGroup.getId();
                }
            }
        }

        return this.currentPageId;
    }

    /**
     * @see View#getCurrentPageId()
     */
    public void setCurrentPageId(String currentPageId) {
        checkMutable(true);
        this.currentPageId = currentPageId;
        this.currentPage = null;
    }

    /**
     * NavigationGroup instance for the View<
     *
     * <p>Provides configuration necessary to render the navigation. This includes
     * navigation items in addition to configuration for the navigation
     * renderer</p>
     *
     * @return NavigationGroup
     */
    @BeanTagAttribute
    public Group getNavigation() {
        return this.navigation;
    }

    /**
     * @see View#getNavigation()
     */
    public void setNavigation(Group navigation) {
        checkMutable(true);
        this.navigation = navigation;
    }

    /**
     * Class of the Form that should be used with the View instance.
     *
     * <p>The form is the top level object for all the view's data and is
     * used to present and accept data in the user interface. All form classes
     * should extend UifFormBase</p>
     *
     * @return class for the view's form
     * @see org.kuali.rice.krad.web.form.UifFormBase
     */
    @BeanTagAttribute
    public Class<?> getFormClass() {
        return this.formClass;
    }

    /**
     * @see View#getFormClass()
     */
    public void setFormClass(Class<?> formClass) {
        checkMutable(true);
        this.formClass = formClass;
    }

    /**
     * For View types that work primarily with one nested object of
     * the form (for instance document, or bo) the default binding object path
     * can be set for each of the views DataBinding components. If
     * the component does not set its own binding object path it will inherit
     * the default.
     *
     * @return binding path to the object from the form
     */
    @BeanTagAttribute
    public String getDefaultBindingObjectPath() {
        return this.defaultBindingObjectPath;
    }

    /**
     * @see View#getDefaultBindingObjectPath()
     */
    public void setDefaultBindingObjectPath(String defaultBindingObjectPath) {
        checkMutable(true);
        this.defaultBindingObjectPath = defaultBindingObjectPath;
    }

    /**
     * Configures the concrete classes that will be used for properties in the
     * form object graph that have an abstract or interface type.
     *
     * <p>For properties that have an abstract or interface type, it is not
     * possible to perform operations like getting/settings property values and
     * getting information from the dictionary. When these properties are
     * encountered in the object graph, this Map will be consulted
     * to determine the concrete type to use</p>
     *
     * <p>e.g. Suppose we have a property document.accountingLine.accountNumber and
     * the accountingLine property on the document instance has an interface
     * type 'AccountingLine'. We can then put an entry into this map with key
     * 'document.accountingLine', and value
     * 'org.kuali.rice.sampleapp.TravelAccountingLine'. When getting the
     * property type or an entry from the dictionary for accountNumber, the
     * TravelAccountingLine class will be used</p>
     *
     * @return Map<String, Class> of class implementations keyed by path
     */
    @BeanTagAttribute
    public Map<String, Class<?>> getObjectPathToConcreteClassMapping() {
        if (objectPathToConcreteClassMapping == Collections.EMPTY_MAP && isMutable(true)) {
            objectPathToConcreteClassMapping = new HashMap<String, Class<?>>();
        }

        return this.objectPathToConcreteClassMapping;
    }

    /**
     * @see View#getObjectPathToConcreteClassMapping()
     */
    public void setObjectPathToConcreteClassMapping(Map<String, Class<?>> objectPathToConcreteClassMapping) {
        checkMutable(true);
        this.objectPathToConcreteClassMapping = objectPathToConcreteClassMapping;
    }

    /**
     * Declares additional script files that should be included with the
     * View.
     *
     * <p>These files are brought into the HTML page along with
     * common script files configured for the Rice application. Each entry
     * contain the path to the CSS file, either a relative path, path from web
     * root, or full URI</p>
     *
     * <p>e.g. '/krad/scripts/myScript.js', '../scripts/myScript.js',
     * 'http://my.edu/web/myScript.js'</p>
     *
     * @return script file locations
     */
    @BeanTagAttribute
    public List<String> getAdditionalScriptFiles() {
        if (additionalScriptFiles == Collections.EMPTY_LIST && isMutable(true)) {
            additionalScriptFiles = new LifecycleAwareList<String>(this);
        }

        return additionalScriptFiles;
    }

    /**
     * @see View#getAdditionalScriptFiles()
     */
    public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
        checkMutable(true);
        if (additionalScriptFiles == null) {
            this.additionalScriptFiles = Collections.emptyList();
        } else {
            this.additionalScriptFiles = new LifecycleAwareList<String>(this, additionalScriptFiles);
        }
    }

    /**
     * Declares additional CSS files that should be included with the View.
     *
     * <p>These files are brought into the HTML page along with
     * common CSS files configured for the Rice application. Each entry should
     * contain the path to the CSS file, either a relative path, path from web
     * root, or full URI</p>
     *
     * <p>e.g. '/krad/css/stacked-view.css', '../css/stacked-view.css',
     * 'http://my.edu/web/stacked-view.css'</p>
     *
     * @return CSS file locations
     */
    @BeanTagAttribute
    public List<String> getAdditionalCssFiles() {
        if (additionalCssFiles == Collections.EMPTY_LIST && isMutable(true)) {
            additionalCssFiles = new LifecycleAwareList<String>(this);
        }

        return additionalCssFiles;
    }

    /**
     * @see View#getAdditionalCssFiles()
     */
    public void setAdditionalCssFiles(List<String> additionalCssFiles) {
        checkMutable(true);
        if (additionalCssFiles == null) {
            this.additionalCssFiles = Collections.emptyList();
        } else {
            this.additionalCssFiles = new LifecycleAwareList<String>(this, additionalCssFiles);
        }
    }

    /**
     * List of additional link tags that should be included with the View in the html head.
     *
     * @return headlink objects
     */
    @BeanTagAttribute
    public List<HeadLink> getAdditionalHeadLinks() {
        return additionalHeadLinks;
    }

    /**
     * @see View#getAdditionalHeadLinks()
     */
    public void setAdditionalHeadLinks(List<HeadLink> additionalHeadLinks) {
        this.additionalHeadLinks = additionalHeadLinks;
    }

    /**
     * List of additional meta tags that should be included with the View in the html head tag.
     *
     * @return   additionalMetaTags
     */
    @BeanTagAttribute
    public List<MetaTag> getAdditionalMetaTags() {
        return additionalMetaTags;
    }

    /**
     * @see View#getAdditionalMetaTags()
     */
    public void setAdditionalMetaTags(List<MetaTag> additionalMetaTags) {
        this.additionalMetaTags = additionalMetaTags;
    }

    /**
     * True if the libraryCssClasses set on components will be output to their class attribute, false otherwise.
     *
     * @return true if using libraryCssClasses on components
     */
    @BeanTagAttribute
    public boolean isUseLibraryCssClasses() {
        return useLibraryCssClasses;
    }

    /**
     * @see View#isUseLibraryCssClasses()
     */
    public void setUseLibraryCssClasses(boolean useLibraryCssClasses) {
        checkMutable(true);
        this.useLibraryCssClasses = useLibraryCssClasses;
    }

    /**
     * List of templates that are used to render the view.
     *
     * <p>This list will be populated by unique template names as the components of the view are being processed.
     * Additional templates can be added in the view configuration if desired. At the beginning of the the view
     * rendering, each template in the list will then be included or processed by the template language</p>
     *
     * <p>Note the user of this depends on the template language being used for rendering. Some languages might require
     * including the template for each component instance (for example JSP templates). While others might simply
     * include markup that is then available for rendering each component instance (for example FreeMarker which has
     * a macro for each component that the template defines)</p>
     *
     * @return list of template names that should be included for rendering the view
     */
    public List<String> getViewTemplates() {
        return viewTemplates;
    }

    /**
     * Adds a template to the views include list.
     * 
     * @param template path to template to add
     */
    public void addViewTemplate(String template) {
        if (StringUtils.isEmpty(template)) {
            return;
        }

        if (!viewTemplates.contains(template)) {
            synchronized (viewTemplates) {
                viewTemplates.add(template);
            }
        }
    }

    /**
     * Setter for the the list of template names that should be included to render the view
     *
     * @param viewTemplates
     */
    public void setViewTemplates(List<String> viewTemplates) {
        checkMutable(true);

        if (viewTemplates == null) {
            this.viewTemplates = new LifecycleAwareList<String>(this);
        } else {
            this.viewTemplates = new LifecycleAwareList<String>(this, viewTemplates);
        }
    }

    /**
     * View type name the view is associated with the view instance
     *
     * <p>Views that share common features and functionality can be grouped by the
     * view type. Usually view types extend the <code>View</code> class to
     * provide additional configuration and to set defaults. View types can also
     * implement the <code>ViewTypeService</code> to add special indexing and
     * retrieval of views</p>
     *
     * @return view type name for the view
     */
    @BeanTagAttribute
    public ViewType getViewTypeName() {
        return this.viewTypeName;
    }

    /**
     * @see View#getViewTypeName()
     */
    public void setViewTypeName(ViewType viewTypeName) {
        checkMutable(true);
        this.viewTypeName = viewTypeName;
    }

    /**
     * Class name of the ViewHelperService that handles the various phases of the Views lifecycle.
     *
     * @return Class for the spring bean
     * @see org.kuali.rice.krad.uif.service.ViewHelperService
     */
    @BeanTagAttribute
    public Class<? extends ViewHelperService> getViewHelperServiceClass() {
        return this.viewHelperServiceClass;
    }

    /**
     * Setter for the <code>ViewHelperService</code> class name
     * Also initializes the viewHelperService
     *
     * @param viewHelperServiceClass
     */
    public void setViewHelperServiceClass(Class<? extends ViewHelperService> viewHelperServiceClass) {
        checkMutable(true);
        this.viewHelperServiceClass = viewHelperServiceClass;
        if ((this.viewHelperService == null) && (this.viewHelperServiceClass != null)) {
            viewHelperService = KRADUtils.createNewObjectFromClass(viewHelperServiceClass);
        }
    }

    /**
     * Creates the <code>ViewHelperService</code> associated with the View
     *
     * @return ViewHelperService instance
     */
    @BeanTagAttribute
    public ViewHelperService getViewHelperService() {
        return viewHelperService;
    }

    /**
     * @see View#getViewHelperServiceClass()
     */
    public void setViewHelperService(ViewHelperService viewHelperService) {
        checkMutable(true);
        this.viewHelperService = viewHelperService;
    }

    /**
     * Invoked to produce a ViewIndex of the current view's components
     */
    public void clearIndex() {
        if (this.viewIndex == null) {
            this.viewIndex = new ViewIndex();
        }
        this.viewIndex.clearIndex(this);
    }

    /**
     * Holds field indexes of the View instance for retrieval.
     *
     * @return ViewIndex instance
     */
    public ViewIndex getViewIndex() {
        return this.viewIndex;
    }

    /**
     * Map of parameters from the request that set view options, used to rebuild
     * the view on each post
     *
     * <p>
     * Views can be configured by parameters. These might impact which parts of
     * the view are rendered or how the view behaves. Generally these would get
     * passed in when a new view is requested (by request parameters). These
     * will be used to initially populate the view properties. In addition, on a
     * post the view will be rebuilt and properties reset again by the allow
     * request parameters.
     * </p>
     *
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
     * @see View#getViewRequestParameters()
     */
    public void setViewRequestParameters(Map<String, String> viewRequestParameters) {
        checkMutable(true);
        this.viewRequestParameters = Collections.unmodifiableMap(viewRequestParameters);
    }

    /**
     * Indicates whether the form (model) associated with the view should be stored in the user session.
     *
     * <p>The form class (or model) is used to hold the data that backs the view along with the built view object. Storing
     * the form instance in session allows many things:
     *
     * <ul>
     * <li>Data does not need to be rebuilt for each server request (for example a collection)</li>
     * <li>Data that does not need to go to the user can remain on the form, reducing the size of the response and
     * improving security</li>
     * <li>Data can be keep around in a 'pre-save' state. When requested by the user changes can then be persisted to
     * the database</li>
     * <li>Certain information about the view that was rendered, such as input fields, collection paths, and refresh
     * components can be kept on the form to support UI interaction</li>
     * </ul>
     *
     * Setting this flag to false will prevent the form from being kept in session and as a result will limit what can
     * be done by the framework. In almost all cases this is not recommended</p>
     *
     * <p>Note all forms will be cleared when the user session expires (based on the rice configuration). In addition, the
     * framework enables clear points on certain actions to remove the form when it is no longer needed</p>
     *
     * @return true if the form should be stored in the user session, false if only request based
     */
    @BeanTagAttribute
    public boolean isPersistFormToSession() {
        return persistFormToSession;
    }

    /**
     * @see View#isPersistFormToSession()
     */
    public void setPersistFormToSession(boolean persistFormToSession) {
        checkMutable(true);
        this.persistFormToSession = persistFormToSession;
    }

    /**
     * Configures behavior that should occur when a session timeout occurs on the view.
     *
     * @return view session policy instance
     */
    @BeanTagAttribute
    public ViewSessionPolicy getSessionPolicy() {
        return sessionPolicy;
    }

    /**
     * @see View#getSessionPolicy()
     */
    public void setSessionPolicy(ViewSessionPolicy sessionPolicy) {
        checkMutable(true);
        this.sessionPolicy = sessionPolicy;
    }

    /**
     * PresentationController that should be used for the View instance.
     *
     * <p>The presentation controller is consulted to determine component (group,
     * field) state such as required, read-only, and hidden. The presentation
     * controller does not take into account user permissions. The presentation
     * controller can also output action flags and edit modes that will be set
     * onto the view instance and can be referred to by conditional expressions</p>
     *
     * @return PresentationController
     */
    @BeanTagAttribute
    public ViewPresentationController getPresentationController() {
        return this.presentationController;
    }

    /**
     * @see View#getPresentationController()
     */
    public void setPresentationController(ViewPresentationController presentationController) {
        checkMutable(true);
        this.presentationController = presentationController;
    }

    /**
     * Setter for the view's presentation controller by class
     *
     * @param presentationControllerClass
     */
    public void setPresentationControllerClass(
            Class<? extends ViewPresentationController> presentationControllerClass) {
        checkMutable(true);
        this.presentationController = KRADUtils.createNewObjectFromClass(presentationControllerClass);
    }

    /**
     * Authorizer that should be used for the View instance
     *
     * <p>The authorizer class is consulted to determine component (group, field)
     * state such as required, read-only, and hidden based on the users
     * permissions. It typically communicates with the Kuali Identity Management
     * system to determine roles and permissions. It is used with the
     * presentation controller and dictionary conditional logic to determine the
     * final component state. The authorizer can also output action flags and
     * edit modes that will be set onto the view instance and can be referred to
     * by conditional expressions</p>
     *
     * @return Authorizer
     */
    @BeanTagAttribute
    public ViewAuthorizer getAuthorizer() {
        return this.authorizer;
    }

    /**
     * @see View#getAuthorizer()
     */
    public void setAuthorizer(ViewAuthorizer authorizer) {
        checkMutable(true);
        this.authorizer = authorizer;
    }

    /**
     * Setter for the view's authorizer by class
     *
     * @param authorizerClass
     */
    public void setAuthorizerClass(Class<? extends ViewAuthorizer> authorizerClass) {
        checkMutable(true);
        this.authorizer = KRADUtils.createNewObjectFromClass(authorizerClass);
    }

    /**
     * Map of strings that flag what actions can be taken in the UI.
     *
     * <p>These can be used in conditional expressions in the dictionary or by
     * other UI logic</p>
     *
     * @return action flags
     */
    @BeanTagAttribute
    public Map<String, Boolean> getActionFlags() {
        if (actionFlags == Collections.EMPTY_MAP && isMutable(true)) {
            actionFlags = new LifecycleAwareMap<String, Boolean>(this);
        }

        return actionFlags;
    }

    /**
     * @see View#getActionFlags()
     */
    public void setActionFlags(Map<String, Boolean> actionFlags) {
        checkMutable(true);
        if (actionFlags == null) {
            this.actionFlags = Collections.emptyMap();
        } else {
            this.actionFlags = new LifecycleAwareMap<String, Boolean>(this, actionFlags);
        }
    }

    /**
     * Map of edit modes that enabled for the view.
     *
     * <p>These can be used in conditional expressions in the dictionary or by
     * other UI logic</p>
     *
     * @return edit modes
     */
    @BeanTagAttribute
    public Map<String, Boolean> getEditModes() {
        if (editModes == Collections.EMPTY_MAP && isMutable(true)) {
            editModes = new LifecycleAwareMap<String, Boolean>(this);
        }

        return editModes;
    }

    /**
     * @see View#getEditModes()
     */
    public void setEditModes(Map<String, Boolean> editModes) {
        checkMutable(true);
        if (editModes == null) {
            this.editModes = Collections.emptyMap();
        } else {
            this.editModes = new LifecycleAwareMap<String, Boolean>(this, editModes);
        }
    }

    /**
     * Map that contains expressions to evaluate and make available as variables
     * for conditional expressions within the view.
     *
     * <p>Each Map entry contains one expression variables, where the map key gives
     * the name for the variable, and the map value gives the variable
     * expression. The variables expressions will be evaluated before
     * conditional logic is run and made available as variables for other
     * conditional expressions. Variable expressions can be based on the model
     * and any object contained in the view's context</p>
     *
     * @return variable expressions
     */
    @BeanTagAttribute
    public Map<String, String> getExpressionVariables() {
        return this.expressionVariables;
    }

    /**
     * @see View#getExpressionVariables()
     */
    public void setExpressionVariables(Map<String, String> expressionVariables) {
        checkMutable(true);
        this.expressionVariables = Collections.unmodifiableMap(expressionVariables);
    }

    /**
     * Indicates whether the View only has a single page
     * Group or contains multiple page Group instances.
     *
     * <p>In the case of a single page it is assumed the group's items
     * list contains the section groups for the page, and the page itself is
     * given by the page property ({@link #getPage()}. This is for convenience
     * of configuration and also can drive other configuration like styling</p>
     *
     * @return true if the view only contains one page group, false if
     * it contains multple pages
     */
    @BeanTagAttribute
    public boolean isSinglePageView() {
        return this.singlePageView;
    }

    /**
     * @see View#isSinglePageView()
     */
    public void setSinglePageView(boolean singlePageView) {
        checkMutable(true);
        this.singlePageView = singlePageView;
    }

    /**
     * Indicates whether the default sections specified in the page items list
     * should be included for this view.  This only applies to single paged views.
     *
     * @return true if the view should contain the default sections
     * specified in the page
     */
    @BeanTagAttribute
    public boolean isMergeWithPageItems() {
        return mergeWithPageItems;
    }

    /**
     * @see View#isMergeWithPageItems()
     */
    public void setMergeWithPageItems(boolean mergeWithPageItems) {
        checkMutable(true);
        this.mergeWithPageItems = mergeWithPageItems;
    }

    /**
     * For single paged views ({@link #isSinglePageView()}, gives the page
     * Group the view should render. The actual items for the page
     * is taken from the group's items list ({@link #getItems()}, and set onto
     * the give page group. This is for convenience of configuration.
     *
     * @return page group for single page views
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECT)
    public PageGroup getPage() {
        return this.page;
    }

    /**
     * @see View#getPage()
     */
    public void setPage(PageGroup page) {
        checkMutable(true);
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public List<? extends Component> getItems() {
        if (items == Collections.EMPTY_LIST && isMutable(true)) {
            items = new LifecycleAwareList<Component>(this);
        }

        return items;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setItems(List<? extends Component> items) {
        checkMutable(true);

        if (items == null) {
            this.items = Collections.emptyList();
        } else {
            // TODO: Fix this unchecked condition.
            this.items = new LifecycleAwareList<Component>(this, (List<Component>) items);
        }
    }

    /**
     * Provide a list of dialog groups associated with this view.
     *
     * @return List of dialog Groups
     */
    @BeanTagAttribute
    public List<Group> getDialogs() {
        if (dialogs == Collections.EMPTY_LIST && isMutable(true)) {
            dialogs = new LifecycleAwareList<Group>(this);
        }

        return dialogs;
    }

    /**
     * @see View#getDialogs()
     */
    public void setDialogs(List<Group> dialogs) {
        checkMutable(true);

        if (dialogs == null) {
            this.dialogs = Collections.emptyList();
        } else {
            this.dialogs = new LifecycleAwareList<Group>(this, dialogs);
        }
    }

    /**
     * Breadcrumb widget used for displaying homeward path and history
     *
     * @return the breadcrumbs
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Breadcrumbs getBreadcrumbs() {
        return this.breadcrumbs;
    }

    /**
     * @param breadcrumbs the breadcrumbs to set
     */
    public void setBreadcrumbs(Breadcrumbs breadcrumbs) {
        checkMutable(true);
        this.breadcrumbs = breadcrumbs;
    }

    /**
     * The breadcrumbOptions for this view.
     *
     * <p>Render options set at the view level are always ignored (only apply to
     * page level BreadcrumbOptions).  BreadcrumbOptions for homewardPathBreadcrumbs,
     * preViewBreadcrumbs, prePageBreadcrumbs,
     * and breadcrumbOverrides are inherited by
     * child pages unless they override them themselves.</p>
     *
     * @return the BreadcrumbOptions for this view
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public BreadcrumbOptions getBreadcrumbOptions() {
        return breadcrumbOptions;
    }

    /**
     * @see View#getBreadcrumbOptions()
     */
    public void setBreadcrumbOptions(BreadcrumbOptions breadcrumbOptions) {
        checkMutable(true);
        this.breadcrumbOptions = breadcrumbOptions;
    }

    /**
     * The View's breadcrumbItem defines settings for the breadcrumb which appears in the breadcrumb list for this
     * view.
     *
     * @return the breadcrumbItem
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public BreadcrumbItem getBreadcrumbItem() {
        return breadcrumbItem;
    }

    /**
     * @see View#getBreadcrumbItem()
     */
    public void setBreadcrumbItem(BreadcrumbItem breadcrumbItem) {
        checkMutable(true);
        this.breadcrumbItem = breadcrumbItem;
    }

    /**
     * The parentLocation defines urls that represent the parent of a View in a conceptial site hierarchy.
     *
     * <p>By defining a parent with these urls defined, a breadcrumb chain can be generated and displayed automatically
     * before this View's breadcrumbItem(s).  To chain multiple views, the urls must be defining viewId and
     * controllerMapping settings instead of setting an href directly (this will end the chain).  If labels are
     * not set on parentLocations, the labels will attempt to be derived from parent views/pages breadcrumbItem
     * and headerText - if these contain expressions which cannot be evaluated in the current context an exception
     * will be thrown</p>
     *
     * @return the parentLocation
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public ParentLocation getParentLocation() {
        return parentLocation;
    }

    /**
     * @see View#getParentLocation()
     */
    public void setParentLocation(ParentLocation parentLocation) {
        checkMutable(true);
        this.parentLocation = parentLocation;
    }

    /**
     * The pathBasedBreadcrumbs for this View.  These can only be set by the framework.
     *
     * @return the path based breadcrumbs
     */
    public List<BreadcrumbItem> getPathBasedBreadcrumbs() {
        return pathBasedBreadcrumbs;
    }

    /**
     * The pathBasedBreadcrumbs for this View.
     *
     * @param pathBasedBreadcrumbs
     */
    public void setPathBasedBreadcrumbs(List<BreadcrumbItem> pathBasedBreadcrumbs) {
        checkMutable(true);
        this.pathBasedBreadcrumbs = pathBasedBreadcrumbs == null ? null : new LifecycleAwareList<BreadcrumbItem>(this,
                pathBasedBreadcrumbs);
    }

    /**
     * Growls widget which sets up global settings for the growls used in this
     * view and its pages.
     *
     * @return the growls
     */
    @BeanTagAttribute
    public Growls getGrowls() {
        return this.growls;
    }

    /**
     * @param growls the growls to set
     */
    public void setGrowls(Growls growls) {
        checkMutable(true);
        this.growls = growls;
    }

    /**
     * @return returns the refresh block object
     */
    @BeanTagAttribute
    public BlockUI getRefreshBlockUI() {
        return refreshBlockUI;
    }

    /**
     * Set the refresh BlockUI used with single element blocking
     * (such as ajax based element loading/updates)
     *
     * @param refreshBlockUI
     */
    public void setRefreshBlockUI(BlockUI refreshBlockUI) {
        checkMutable(true);
        this.refreshBlockUI = refreshBlockUI;
    }

    /**
     * @return returns the navigation block object
     */
    @BeanTagAttribute
    public BlockUI getNavigationBlockUI() {
        return navigationBlockUI;
    }

    /**
     * Set the navigation BlockUI used with single page blocking
     * (such as full page loading/saving)
     *
     * @param navigationBlockUI
     */
    public void setNavigationBlockUI(BlockUI navigationBlockUI) {
        checkMutable(true);
        this.navigationBlockUI = navigationBlockUI;
    }

    /**
     * Whether to use growls to show messages - info, warning and error
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
    @BeanTagAttribute
    public boolean isGrowlMessagingEnabled() {
        return this.growlMessagingEnabled;
    }

    /**
     * enable or disable showing of messages using growls
     *
     * @param growlMessagingEnabled the growlMessagingEnabled to set
     */
    public void setGrowlMessagingEnabled(boolean growlMessagingEnabled) {
        checkMutable(true);
        this.growlMessagingEnabled = growlMessagingEnabled;
    }

    /**
     * Indicates whether the form should be validated for dirtyness.
     *
     * <p>For FormView, it's necessary to validate when the user tries to navigate out of the form. If set, all the
     * InputFields will be validated on refresh, navigate, cancel or close Action or on form
     * unload and if dirty, displays a message and user can decide whether to continue with
     * the action or stay on the form. For lookup and inquiry, it's not needed to validate</p>
     *
     * @return true if dirty validation is set
     */
    @BeanTagAttribute
    public boolean isApplyDirtyCheck() {
        return this.applyDirtyCheck;
    }

    /**
     * @see View#isApplyDirtyCheck()
     */
    public void setApplyDirtyCheck(boolean applyDirtyCheck) {
        checkMutable(true);
        this.applyDirtyCheck = applyDirtyCheck;
    }

    /**
     * Returns whether the current view supports displaying <code>KualiCode</code>'s name as additional display value
     *
     * @return true if the current view supports
     */
    @BeanTagAttribute
    public boolean isTranslateCodesOnReadOnlyDisplay() {
        return translateCodesOnReadOnlyDisplay;
    }

    /**
     * Indicates whether the Name of the Code should be displayed when a property is of type <code>KualiCode</code>
     *
     * @param translateCodesOnReadOnlyDisplay indicates whether KualiCode's name should be included.
     */
    public void setTranslateCodesOnReadOnlyDisplay(boolean translateCodesOnReadOnlyDisplay) {
        checkMutable(true);
        this.translateCodesOnReadOnlyDisplay = translateCodesOnReadOnlyDisplay;
    }

    /**
     * Indicates whether the view allows read only fields to be specified on the request URL which will
     * override the view setting.
     *
     * <p>If enabled, the readOnlyFields request parameter can be sent to indicate fields that should be set read only</p>
     *
     * @return true if read only request overrides are allowed, false if not
     */
    @BeanTagAttribute
    public boolean isSupportsRequestOverrideOfReadOnlyFields() {
        return supportsRequestOverrideOfReadOnlyFields;
    }

    /**
     * @see View#isSupportsRequestOverrideOfReadOnlyFields()
     */
    public void setSupportsRequestOverrideOfReadOnlyFields(boolean supportsRequestOverrideOfReadOnlyFields) {
        checkMutable(true);
        this.supportsRequestOverrideOfReadOnlyFields = supportsRequestOverrideOfReadOnlyFields;
    }

    /**
     * Indicates whether the browser autocomplete functionality should be disabled for the
     * entire form (adds autocomplete="off").
     *
     * <p>The browser's native autocomplete functionality can cause issues with security fields and also fields
     * with the UIF suggest widget enabled</p>
     *
     * @return true if the native autocomplete should be turned off for the form, false if not
     */
    @BeanTagAttribute
    public boolean isDisableNativeAutocomplete() {
        return disableNativeAutocomplete;
    }

    /**
     * @see View#isDisableNativeAutocomplete()
     */
    public void setDisableNativeAutocomplete(boolean disableNativeAutocomplete) {
        checkMutable(true);
        this.disableNativeAutocomplete = disableNativeAutocomplete;
    }

    /**
     * Enables functionality to bust the browsers cache by appending an unique cache key.
     *
     * <p>Since response headers are unreliable for preventing caching in all browsers, the
     * framework uses a technique for updating the URL to include an unique cache key. If the
     * HTML 5 History API is supported a parameter can be added to the URL which causes the browser
     * to not find the cached page when the user goes back. If not the framework falls back to using
     * a hash key and resubmitting using script to pull the latest</p>
     *
     * @return true if cache for the view should be disabled, false if not
     */
    @BeanTagAttribute
    public boolean isDisableBrowserCache() {
        return disableBrowserCache;
    }

    /**
     * @see View#isDisableBrowserCache()
     */
    public void setDisableBrowserCache(boolean disableBrowserCache) {
        checkMutable(true);
        this.disableBrowserCache = disableBrowserCache;
    }

    /**
     * Script that is executed at the beginning of page load (before any other script).
     *
     * <p>Many used to set server variables client side</p>
     *
     * @return pre load script
     */
    @BeanTagAttribute
    public String getPreLoadScript() {
        return preLoadScript;
    }

    /**
     * @see View#getPreLoadScript()
     */
    public void setPreLoadScript(String preLoadScript) {
        checkMutable(true);
        this.preLoadScript = preLoadScript;
    }

    /**
     * The theme which contains stylesheets for this view.
     *
     * @return ViewTheme
     */
    @BeanTagAttribute
    public ViewTheme getTheme() {
        return theme;
    }

    /**
     * @see View#getTheme()
     */
    public void setTheme(ViewTheme theme) {
        checkMutable(true);
        this.theme = theme;
    }

    /**
     * The stateObject's binding path, this will be used along with the StateMapping's statePropertyName to
     * determine what field in the model state information is stored in for this view.  Used during View validation.
     *
     * @return stateObjectBindingPath path to the object storing state information
     */
    @BeanTagAttribute
    public String getStateObjectBindingPath() {
        return stateObjectBindingPath;
    }

    /**
     * The stateObject's binding path, this will be used along with the StateMapping's statePropertyName to
     * determine what field in the model state information is stored in for this view.  Used during View validation.
     *
     * @param stateObjectBindingPath
     */
    public void setStateObjectBindingPath(String stateObjectBindingPath) {
        checkMutable(true);
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
     * @return information needed for state based validation, if null no state based validation
     * functionality will exist and configured constraints will apply regardless of state
     * @since 2.2
     */
    @BeanTagAttribute
    public StateMapping getStateMapping() {
        return stateMapping;
    }

    /**
     * @see View#getStateMapping()
     */
    public void setStateMapping(StateMapping stateMapping) {
        checkMutable(true);
        this.stateMapping = stateMapping;
    }

    /**
     * Returns the general context that is available before the apply model phase (during the
     * initialize phase)
     *
     * @return context map
     */
    public Map<String, Object> getPreModelContext() {
        if (preModelContext == null) {
            Map<String, Object> context = new HashMap<String, Object>();

            context.put(UifConstants.ContextVariableNames.VIEW, this);
            context.put(UifConstants.ContextVariableNames.VIEW_HELPER, viewHelperService);

            ViewTheme theme = getTheme();
            if (theme != null) {
                context.put(UifConstants.ContextVariableNames.THEME_IMAGES, theme.getImageDirectory());
            }

            Map<String, String> properties = CoreApiServiceLocator.getKualiConfigurationService().getAllProperties();
            context.put(UifConstants.ContextVariableNames.CONFIG_PROPERTIES, properties);
            context.put(UifConstants.ContextVariableNames.CONSTANTS, KRADConstants.class);
            context.put(UifConstants.ContextVariableNames.UIF_CONSTANTS, UifConstants.class);
            context.put(UifConstants.ContextVariableNames.USER_SESSION, GlobalVariables.getUserSession());

            preModelContext = Collections.unmodifiableMap(context);
        }

        return preModelContext;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#clone()
     */
    @Override
    public View clone() throws CloneNotSupportedException {
        View viewCopy = (View) super.clone();
        viewCopy.viewIndex = new ViewIndex();
        return viewCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Check for the presence of a valid item with an not-null EntryPageId
        boolean validPageId = false;
        if (getEntryPageId() != null) {
            for (int i = 0; i < getItems().size(); i++) {
                if (getEntryPageId().compareTo(getItems().get(i).getId()) == 0) {
                    validPageId = true;
                }
            }
        } else {
            validPageId = true;
        }
        if (!validPageId) {
            String currentValues[] = {"entryPageId = " + getEntryPageId()};
            tracer.createError("Items must contain an item with a matching id to entryPageId", currentValues);
        }

        // Check to insure the view as not already been set
        if (tracer.getValidationStage() == ValidationTrace.START_UP) {
            if (getViewStatus().compareTo(ViewStatus.CREATED) != 0) {
                String currentValues[] = {"viewStatus = " + getViewStatus()};
                tracer.createError("ViewStatus should not be set", currentValues);
            }
        }

        // Check to insure the binding object path is a valid property
        boolean validDefaultBindingObjectPath = false;
        if (getDefaultBindingObjectPath() == null) {
            validDefaultBindingObjectPath = true;
        } else if (DataDictionary.isPropertyOf(getFormClass(), getDefaultBindingObjectPath())) {
            validDefaultBindingObjectPath = true;
        }
        if (!validDefaultBindingObjectPath) {
            String currentValues[] =
                    {"formClass = " + getFormClass(), "defaultBindingPath = " + getDefaultBindingObjectPath()};
            tracer.createError("DefaultBingdingObjectPath must be a valid property of the formClass", currentValues);
        }

        // Check to insure the page is set if the view is a single page
        if (isSinglePageView()) {
            if (getPage() == null) {
                String currentValues[] = {"singlePageView = " + isSinglePageView(), "page = " + getPage()};
                tracer.createError("Page must be set if singlePageView is true", currentValues);
            }
            for (int i = 0; i < getItems().size(); i++) {
                if (getItems().get(i).getClass() == PageGroup.class) {
                    String currentValues[] =
                            {"singlePageView = " + isSinglePageView(), "items(" + i + ") = " + getItems().get(i)
                                    .getClass()};
                    tracer.createError("Items cannot be pageGroups if singlePageView is true", currentValues);
                }
            }
        }

        // Checks to insure the Growls are set if growl messaging is enabled
        if (isGrowlMessagingEnabled() == true && getGrowls() == null) {
            if (Validator.checkExpressions(this, "growls")) {
                String currentValues[] =
                        {"growlMessagingEnabled = " + isGrowlMessagingEnabled(), "growls = " + getGrowls()};
                tracer.createError("Growls cannot be null if Growl Messaging is enabled", currentValues);
            }
        }

        // Checks that there are items present if the view is not a single page
        if (!isSinglePageView()) {
            if (getItems().size() == 0) {
                String currentValues[] =
                        {"singlePageView = " + isSinglePageView(), "items.size = " + getItems().size()};
                tracer.createWarning("Items cannot be empty if singlePageView is false", currentValues);
            } else {
                for (int i = 0; i < getItems().size(); i++) {
                    if (getItems().get(i).getClass() != PageGroup.class) {
                        String currentValues[] =
                                {"singlePageView = " + isSinglePageView(), "items(" + i + ") = " + getItems().get(i)
                                        .getClass()};
                        tracer.createError("Items must be pageGroups if singlePageView is false", currentValues);
                    }
                }
            }
        }
        super.completeValidation(tracer.getCopy());
    }

}
