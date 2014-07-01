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
package org.kuali.rice.krad.uif.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.control.ControlBase;
import org.kuali.rice.krad.uif.layout.CssGridSizes;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.util.LifecycleAwareMap;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewIndex;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Base implementation of Component which other component implementations should extend.
 *
 * <p>
 * Provides base component properties such as id and template. Also provides default implementation
 * for the <code>ScriptEventSupport</code> and <code>Ordered</code> interfaces. By default no script
 * events except the onDocumentReady are supported.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "componentBase", parent = "Uif-ComponentBase")
public abstract class ComponentBase extends UifDictionaryBeanBase implements Component {
    private static final long serialVersionUID = -4449335748129894350L;

    private String id;
    private String containerIdSuffix;
    private String viewPath;
    private Map<String, String> phasePathMapping;

    private String template;
    private String templateName;

    private String viewStatus;

    private String title;

    private boolean render;
    private boolean retrieveViaAjax;

    @KeepExpression
    private String progressiveRender;
    private boolean progressiveRenderViaAJAX;
    private boolean progressiveRenderAndRefresh;
    private List<String> progressiveDisclosureControlNames;
    private String progressiveDisclosureConditionJs;

    @KeepExpression
    private String conditionalRefresh;
    private String conditionalRefreshConditionJs;
    private List<String> conditionalRefreshControlNames;

    private List<String> refreshWhenChangedPropertyNames;
    private List<String> additionalComponentsToRefresh;
    private String additionalComponentsToRefreshJs;
    private boolean refreshedByAction;
    private boolean disclosedByAction;

    private int refreshTimer;

    private boolean resetDataOnRefresh;
    private String methodToCallOnRefresh;

    private boolean hidden;
    private Boolean readOnly;
    private Boolean required;

    private String align;
    private String valign;
    private String width;

    // optional table-backed layout options
    private int colSpan;
    private int rowSpan;
    private List<String> wrapperCssClasses;
    private String wrapperStyle;
    private String cellWidth;
    private CssGridSizes cssGridSizes;

    private List<String> layoutCssClasses;

    private String style;

    private List<String> libraryCssClasses;
    private List<String> cssClasses;
    private List<String> additionalCssClasses;

    @DelayedCopy
    private Tooltip toolTip;

    private int order;

    private boolean skipInTabOrder;

    private String finalizeMethodToCall;
    private List<Object> finalizeMethodAdditionalArguments;
    private MethodInvokerConfig finalizeMethodInvoker;

    private boolean selfRendered;
    private String renderedHtmlOutput;

    private boolean disableSessionPersistence;
    private boolean forceSessionPersistence;

    private ComponentSecurity componentSecurity;

    private String onLoadScript;
    private String onUnloadScript;
    private String onCloseScript;
    private String onBlurScript;
    private String onChangeScript;
    private String onClickScript;
    private String onDblClickScript;
    private String onFocusScript;
    private String onSubmitScript;
    private String onInputScript;
    private String onKeyPressScript;
    private String onKeyUpScript;
    private String onKeyDownScript;
    private String onMouseOverScript;
    private String onMouseOutScript;
    private String onMouseUpScript;
    private String onMouseDownScript;
    private String onMouseMoveScript;
    private String onDocumentReadyScript;

    private List<ComponentModifier> componentModifiers;

    protected Map<String, String> templateOptions;

    private String templateOptionsJSString;

    @ReferenceCopy(newCollectionInstance = true)
    private transient Map<String, Object> context;

    private List<PropertyReplacer> propertyReplacers;

    private Map<String, String> dataAttributes;
    private Map<String, String> scriptDataAttributes;
    
    private String role;
    private Map<String, String> ariaAttributes;

    private String preRenderContent;
    private String postRenderContent;
    
    private String excludeIf;
    private String excludeUnless;

    public ComponentBase() {
        super();

        order = 0;
        colSpan = 1;
        rowSpan = 1;
        cssGridSizes = new CssGridSizes();

        viewStatus = ViewStatus.CREATED;

        render = true;
        selfRendered = false;
        progressiveRenderViaAJAX = false;
        progressiveRenderAndRefresh = false;
        refreshedByAction = false;
        resetDataOnRefresh = false;
        disableSessionPersistence = false;
        forceSessionPersistence = false;

        phasePathMapping = new HashMap<String, String>();
        context = Collections.emptyMap();
        dataAttributes = Collections.emptyMap();
        scriptDataAttributes = Collections.emptyMap();
        ariaAttributes = Collections.emptyMap();
        templateOptions = Collections.emptyMap();

        cssClasses = Collections.emptyList();
        libraryCssClasses = Collections.emptyList();
        additionalCssClasses = Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean skipLifecycle() {
            return this.isRetrieveViaAjax();
    }

    /**
     * @see LifecycleElement#checkMutable(boolean)
     */
    public void checkMutable(boolean legalDuringInitialization) {
        if (UifConstants.ViewStatus.CACHED.equals(viewStatus)) {
            ViewLifecycle.reportIllegalState("Cached component "
                    + getClass()
                    + " "
                    + getId()
                    + " is immutable, use copy() to get a mutable instance");
            return;
        }

        if (ViewLifecycle.isActive()) {
            return;
        }

        if (UifConstants.ViewStatus.CREATED.equals(viewStatus) && !legalDuringInitialization) {
            ViewLifecycle.reportIllegalState("View has not been fully initialized, attempting to change component "
                    + getClass()
                    + " "
                    + getId());
            return;
        }
    }

    /**
     * @see LifecycleElement#isMutable(boolean)
     */
    public boolean isMutable(boolean legalDuringInitialization) {
        return (UifConstants.ViewStatus.CREATED.equals(viewStatus) && legalDuringInitialization) || ViewLifecycle
                .isActive();
    }

    /**
     * Indicates what lifecycle phase the component instance is in
     * 
     * <p>
     * The view lifecycle begins with the CREATED status. In this status a new instance of the view
     * has been retrieved from the dictionary, but no further processing has been done. After the
     * initialize phase has been run the status changes to INITIALIZED. After the model has been
     * applied and the view is ready for render the status changes to FINAL
     * </p>
     * 
     * @return view status
     * @see org.kuali.rice.krad.uif.UifConstants.ViewStatus
     */
    public String getViewStatus() {
        return this.viewStatus;
    }

    /**
     * Setter for the view status
     * 
     * @param status view status
     */
    @Override
    public void setViewStatus(String status) {
        if (!UifConstants.ViewStatus.CREATED.equals(status) && !UifConstants.ViewStatus.CACHED.equals(status)) {
            checkMutable(true);
        }

        this.viewStatus = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyCompleted(ViewLifecyclePhase phase) {
        if (!UifConstants.ViewPhases.PRE_PROCESS.equals(phase.getViewPhase())) {
            ViewIndex viewIndex = ViewLifecycle.getView().getViewIndex();
            if (viewIndex != null) {
                viewIndex.indexComponent(this);
            }
        }
    }

    /**
     * Indicates whether the component has been initialized.
     * 
     * @return True if the component has been initialized, false if not.
     */
    public boolean isInitialized() {
        return StringUtils.equals(viewStatus, ViewStatus.INITIALIZED) || isModelApplied();
    }

    /**
     * Indicates whether the component has been updated from the model.
     * 
     * @return True if the component has been updated, false if not.
     */
    public boolean isModelApplied() {
        return StringUtils.equals(viewStatus, ViewStatus.MODEL_APPLIED) || isFinal();
    }

    /**
     * Indicates whether the component has been updated from the model and final updates made.
     * 
     * @return True if the component has been updated, false if not.
     */
    public boolean isFinal() {
        return StringUtils.equals(viewStatus, ViewStatus.FINAL) || isRendered();
    }

    /**
     * Indicates whether the component has been fully rendered.
     * 
     * @return True if the component has fully rendered, false if not.
     */
    public boolean isRendered() {
        return StringUtils.equals(viewStatus, ViewStatus.RENDERED);
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>tooltip is removed if content is null</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {}

    /**
     * The following updates are done here:
     * 
     * <ul>
     * <li>Evaluate the progressive render condition (if set) and combine with the current render
     * status to set the render status</li>
     * </ul>
     * 
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        View view = ViewLifecycle.getView();

        if (this.render && StringUtils.isNotEmpty(progressiveRender)) {
            // progressive anded with render, will not render at least one of the two are false
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

            String adjustedProgressiveRender = expressionEvaluator.replaceBindingPrefixes(view, this,
                    progressiveRender);

            Boolean progRenderEval = (Boolean) expressionEvaluator.evaluateExpression(context,
                    adjustedProgressiveRender);

            this.setRender(progRenderEval);
        }
    }

    /**
     * No-op impelemtnation.  Override for custom behavior in subclasses.
     * 
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
    }

    /**
     * The following finalization is done here:
     * 
     * <ul>
     * <li>progressiveRender and conditionalRefresh variables are processed if set</li>
     * <li>If any of the style properties were given, sets the style string on the style property</li>
     * <li>Set the skipInTabOrder flag for nested components</li>
     * </ul>
     * 
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        View view = ViewLifecycle.getView();
        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
        // progressiveRender expression setup
        if (StringUtils.isNotEmpty(progressiveRender)) {
            progressiveRender = expressionEvaluator.replaceBindingPrefixes(view, this, progressiveRender);
            progressiveDisclosureControlNames = new ArrayList<String>();
            progressiveDisclosureConditionJs = expressionEvaluator.parseExpression(progressiveRender,
                    progressiveDisclosureControlNames, this.getContext());
        }

        // conditional refresh expression setup
        if (StringUtils.isNotEmpty(conditionalRefresh)) {
            conditionalRefresh = expressionEvaluator.replaceBindingPrefixes(view, this, conditionalRefresh);
            conditionalRefreshControlNames = new ArrayList<String>();
            conditionalRefreshConditionJs = expressionEvaluator.parseExpression(conditionalRefresh,
                    conditionalRefreshControlNames, this.getContext());
        }

        if (refreshWhenChangedPropertyNames != null) {
            List<String> adjustedRefreshPropertyNames = new ArrayList<String>(refreshWhenChangedPropertyNames.size());
            for (String refreshPropertyName : refreshWhenChangedPropertyNames) {
                adjustedRefreshPropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this,
                        refreshPropertyName));
            }
            refreshWhenChangedPropertyNames = adjustedRefreshPropertyNames;
            if (!retrieveViaAjax) {
                //need to add the "refresh" method so that regular calls will work
                ViewLifecycle.getViewPostMetadata().addAccessibleMethodToCall(KRADConstants.RETURN_METHOD_TO_CALL);
            }
        }

        // retrieveViaAjax forces session persistence because it assumes that this component will be retrieved by
        // some ajax retrieval call
        if (retrieveViaAjax) {
            forceSessionPersistence = true;
        }

        // add the align, valign, and width settings to style
        if (StringUtils.isNotBlank(getAlign()) && !StringUtils.contains(getStyle(), CssConstants.TEXT_ALIGN)) {
            appendToStyle(CssConstants.TEXT_ALIGN + getAlign() + ";");
        }

        if (StringUtils.isNotBlank(getValign()) && !StringUtils.contains(getStyle(), CssConstants.VERTICAL_ALIGN)) {
            appendToStyle(CssConstants.VERTICAL_ALIGN + getValign() + ";");
        }

        if (StringUtils.isNotBlank(getWidth()) && !StringUtils.contains(getStyle(), CssConstants.WIDTH)) {
            appendToStyle(CssConstants.WIDTH + getWidth() + ";");
        }

        // Set the skipInTabOrder flag on all nested components
        // Set the tabIndex on controls to -1 in order to be skipped on tabbing
        if (skipInTabOrder) {
            for (LifecycleElement component : ViewLifecycleUtils.getElementsForLifecycle(this).values()) {
                if (component != null && component instanceof ComponentBase) {
                    ((ComponentBase) component).setSkipInTabOrder(skipInTabOrder);
                    if (component instanceof ControlBase) {
                        ((ControlBase) component).setTabIndex(-1);
                    }
                }
            }
        }

        // if this is not rendering and it is not rendering via an ajax call, but still has a progressive render
        // condition we still want to render the component, but hide it (in ajax cases, template creates a placeholder)
        boolean hide = false;
        if (!this.render && !this.progressiveRenderViaAJAX && !this.progressiveRenderAndRefresh && StringUtils
                .isNotBlank(progressiveRender)) {
            hide = true;
        } else if (this.isHidden()) {
            hide = true;
        }

        if (hide) {
            if (StringUtils.isNotBlank(this.getStyle())) {
                if (this.getStyle().endsWith(";")) {
                    this.setStyle(this.getStyle() + " display: none;");
                } else {
                    this.setStyle(this.getStyle() + "; display: none;");
                }
            } else {
                this.setStyle("display: none;");
            }
        }

        // setup refresh timer
        // if the refreshTimer property has been set then pre-append the call to refreshComponetUsingTimer
        // to the onDocumentReadyScript
        if (refreshTimer > 0) {
            String timerScript = getOnDocumentReadyScript();

            if (StringUtils.isBlank(this.methodToCallOnRefresh)) {
                this.methodToCallOnRefresh = "refresh";
            }

            timerScript = (null == timerScript) ? "" : timerScript;
            timerScript = "refreshComponentUsingTimer('"
                    + this.id
                    + "','"
                    + this.methodToCallOnRefresh
                    + "',"
                    + refreshTimer
                    + ");"
                    + timerScript;

            setOnDocumentReadyScript(timerScript);
        }

        // Add tooltip class
        if (this.getToolTip() != null && StringUtils.isNotBlank(this.getToolTip().getTooltipContent())) {
            this.addStyleClass(CssConstants.Classes.TOOLTIP);
        }

        // put together all css class names for this component, in order
        List<String> finalCssClasses = new ArrayList<String>();

        if (this.libraryCssClasses != null && (!ViewLifecycle.isActive() || ViewLifecycle.getView()
                .isUseLibraryCssClasses())) {
            finalCssClasses.addAll(libraryCssClasses);
        }

        if (this.cssClasses != null) {
            finalCssClasses.addAll(cssClasses);
        }

        if (this.additionalCssClasses != null) {
            finalCssClasses.addAll(additionalCssClasses);
        }

        cssClasses = finalCssClasses;

        // add the method to call as an available method
        if (StringUtils.isNotBlank(methodToCallOnRefresh)) {
            ViewLifecycle.getViewPostMetadata().addAvailableMethodToCall(methodToCallOnRefresh);
        }

        if ((isRender() || StringUtils.isNotBlank(getProgressiveRender())) && StringUtils.isNotBlank(
                methodToCallOnRefresh)) {
            ViewLifecycle.getViewPostMetadata().addAccessibleMethodToCall(methodToCallOnRefresh);
        }
    }

    /**
     * Returns list of components that are being held in property replacers configured for this
     * component
     * 
     * @return List<Component>
     */
    @ViewLifecycleRestriction
    public List<Component> getPropertyReplacerComponents() {
        if (propertyReplacers == null) {
            return Collections.emptyList();
        }

        List<Component> components = new ArrayList<Component>();
        for (Object replacer : propertyReplacers) {
            components.addAll(((PropertyReplacer) replacer).getNestedComponents());
        }

        return components;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(String id) {
        checkMutable(true);
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContainerIdSuffix() {
        return containerIdSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContainerIdSuffix(String containerIdSuffix) {
        this.containerIdSuffix = containerIdSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPath() {
        return this.viewPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewPath(String viewPath) {
        checkMutable(true);
        this.viewPath = viewPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getPhasePathMapping() {
        return phasePathMapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhasePathMapping(Map<String, String> phasePathMapping) {
        this.phasePathMapping = phasePathMapping;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getTemplate() {
        return this.template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplate(String template) {
        checkMutable(true);
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAdditionalTemplates() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getTemplateName() {
        return templateName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateName(String templateName) {
        checkMutable(true);
        this.templateName = templateName;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title) {
        checkMutable(true);
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(boolean hidden) {
        checkMutable(true);
        this.hidden = hidden;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public Boolean getReadOnly() {
        return this.readOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(Boolean readOnly) {
        checkMutable(true);
        this.readOnly = readOnly;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequired(Boolean required) {
        checkMutable(true);
        this.required = required;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public boolean isRender() {
        return this.render;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRender(boolean render) {
        checkMutable(true);
        this.render = render;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public boolean isRetrieveViaAjax() {
        return retrieveViaAjax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRetrieveViaAjax(boolean retrieveViaAjax) {
        checkMutable(true);
        this.retrieveViaAjax = retrieveViaAjax;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public int getColSpan() {
        return this.colSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColSpan(int colSpan) {
        checkMutable(true);
        this.colSpan = colSpan;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public int getRowSpan() {
        return this.rowSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowSpan(int rowSpan) {
        checkMutable(true);
        this.rowSpan = rowSpan;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public List<String> getWrapperCssClasses() {
        return wrapperCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWrapperCssClasses(List<String> wrapperCssClasses) {
        checkMutable(true);
        this.wrapperCssClasses = wrapperCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWrapperCssClass(String cssClass) {
        checkMutable(false);
        if (this.wrapperCssClasses == null) {
            this.wrapperCssClasses = new ArrayList<String>();
        }

        if (cssClass != null) {
            this.wrapperCssClasses.add(cssClass);
        }
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list with a space
     * delimiter.
     * 
     * @return class attribute string
     */
    public String getWrapperCssClassesAsString() {
        if (wrapperCssClasses != null) {
            return StringUtils.join(wrapperCssClasses, " ").trim();
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getWrapperStyle() {
        return wrapperStyle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWrapperStyle(String wrapperStyle) {
        checkMutable(true);
        this.wrapperStyle = wrapperStyle;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getCellWidth() {
        return cellWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellWidth(String cellWidth) {
        checkMutable(true);
        this.cellWidth = cellWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public CssGridSizes getCssGridSizes() {
        return cssGridSizes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssGridSizes(CssGridSizes cssGridSizes) {
        this.cssGridSizes = cssGridSizes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAlign() {
        return this.align;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlign(String align) {
        checkMutable(true);
        this.align = align;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getValign() {
        return this.valign;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValign(String valign) {
        checkMutable(true);
        this.valign = valign;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(String width) {
        checkMutable(true);
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getStyle() {
        return this.style;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStyle(String style) {
        checkMutable(true);
        this.style = style;
    }

    /**
     * Additional css classes that come before css classes listed in the cssClasses property
     * 
     * <p>
     * These are used by the framework for styling with a library (for example, bootstrap), and
     * should normally not be overridden.
     * </p>
     * 
     * @return the library cssClasses
     */
    public List<String> getLibraryCssClasses() {
        if (libraryCssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            libraryCssClasses = new ArrayList<String>();
        }

        return libraryCssClasses;
    }

    /**
     * Set the libraryCssClasses
     * 
     * @param libraryCssClasses
     */
    public void setLibraryCssClasses(List<String> libraryCssClasses) {
        checkMutable(true);

        if (libraryCssClasses == null) {
            this.libraryCssClasses = Collections.emptyList();
        } else {
            this.libraryCssClasses = libraryCssClasses;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getCssClasses() {
        if (cssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            cssClasses = new ArrayList<String>();
        }

        return cssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssClasses(List<String> cssClasses) {
        checkMutable(true);
        if (cssClasses == null) {
            this.cssClasses = Collections.emptyList();
        } else {
            this.cssClasses = cssClasses;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getAdditionalCssClasses() {
        if (additionalCssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            additionalCssClasses = new ArrayList<String>();
        }

        return additionalCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdditionalCssClasses(List<String> additionalCssClasses) {
        checkMutable(true);
        if (additionalCssClasses == null) {
            this.additionalCssClasses = Collections.emptyList();
        } else {
            this.additionalCssClasses = additionalCssClasses;
        }
    }

    /**
     * Builds the HTML class attribute string by combining the styleClasses list with a space
     * delimiter
     * 
     * @return class attribute string
     */
    public String getStyleClassesAsString() {
        if (cssClasses != null) {
            return StringUtils.join(cssClasses, " ").trim();
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStyleClass(String styleClass) {
        checkMutable(false);
        if (StringUtils.isEmpty(styleClass)) {
            return;
        }

        if (cssClasses.isEmpty()) {
            setCssClasses(new ArrayList<String>());
        }

        if (!cssClasses.contains(styleClass)) {
            cssClasses.add(styleClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendToStyle(String styleRules) {
        checkMutable(false);
        if (style == null) {
            style = "";
        }
        style = style + styleRules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getFinalizeMethodToCall() {
        return this.finalizeMethodToCall;
    }

    /**
     * Setter for the finalize method
     * 
     * @param finalizeMethodToCall
     */
    public void setFinalizeMethodToCall(String finalizeMethodToCall) {
        checkMutable(true);
        this.finalizeMethodToCall = finalizeMethodToCall;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<Object> getFinalizeMethodAdditionalArguments() {
        return finalizeMethodAdditionalArguments;
    }

    /**
     * Setter for the finalize additional arguments list
     * 
     * @param finalizeMethodAdditionalArguments
     */
    public void setFinalizeMethodAdditionalArguments(List<Object> finalizeMethodAdditionalArguments) {
        checkMutable(true);
        this.finalizeMethodAdditionalArguments = finalizeMethodAdditionalArguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public MethodInvokerConfig getFinalizeMethodInvoker() {
        return this.finalizeMethodInvoker;
    }

    /**
     * Setter for the method invoker instance
     * 
     * @param finalizeMethodInvoker
     */
    public void setFinalizeMethodInvoker(MethodInvokerConfig finalizeMethodInvoker) {
        checkMutable(true);
        this.finalizeMethodInvoker = finalizeMethodInvoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isSelfRendered() {
        return this.selfRendered;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelfRendered(boolean selfRendered) {
        this.selfRendered = selfRendered;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getRenderedHtmlOutput() {
        return this.renderedHtmlOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderedHtmlOutput(String renderedHtmlOutput) {
        this.renderedHtmlOutput = renderedHtmlOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isDisableSessionPersistence() {
        return disableSessionPersistence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableSessionPersistence(boolean disableSessionPersistence) {
        checkMutable(true);
        this.disableSessionPersistence = disableSessionPersistence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isForceSessionPersistence() {
        return forceSessionPersistence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForceSessionPersistence(boolean forceSessionPersistence) {
        checkMutable(true);
        this.forceSessionPersistence = forceSessionPersistence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public ComponentSecurity getComponentSecurity() {
        return componentSecurity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        checkMutable(true);
        this.componentSecurity = componentSecurity;
    }

    /**
     * Initializes (if necessary) the component security instance for the component type
     */
    protected void initializeComponentSecurity() {
        if (this.componentSecurity == null) {
            this.componentSecurity = KRADUtils.createNewObjectFromClass(ComponentSecurity.class);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#isEditAuthz()
     */
    @BeanTagAttribute
    public Boolean isEditAuthz() {
        initializeComponentSecurity();

        return this.componentSecurity.isEditAuthz();
    }

    /**
     * Setter for {@link #isEditAuthz()}
     * 
     * @param editAuthz property value
     */
    public void setEditAuthz(Boolean editAuthz) {
        checkMutable(true);
        initializeComponentSecurity();

        this.componentSecurity.setEditAuthz(editAuthz);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#isViewAuthz()
     */
    @BeanTagAttribute
    public Boolean isViewAuthz() {
        initializeComponentSecurity();

        return this.componentSecurity.isViewAuthz();
    }

    /**
     * Setter for {@link #isViewAuthz()}
     * 
     * @param viewAuthz property value
     */
    public void setViewAuthz(Boolean viewAuthz) {
        checkMutable(true);
        initializeComponentSecurity();

        this.componentSecurity.setViewAuthz(viewAuthz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<ComponentModifier> getComponentModifiers() {
        return this.componentModifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentModifiers(List<ComponentModifier> componentModifiers) {
        checkMutable(true);
        this.componentModifiers = componentModifiers == null ? Collections.<ComponentModifier> emptyList() :
                componentModifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Map<String, Object> getContext() {
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new HashMap<String, Object>();
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContext(Map<String, Object> context) {
        checkMutable(true);

        if (context == null) {
            this.context = Collections.emptyMap();
        } else {
            this.context = context;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushObjectToContext(String objectName, Object object) {
        checkMutable(true);
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new HashMap<String, Object>();
        }

        pushToPropertyReplacerContext(objectName, object);
        context.put(objectName, object);
    }

    /*
    * Adds the object to the context of the components in the
    * PropertyReplacer object. Only checks for a list, map or component.
    */
    protected void pushToPropertyReplacerContext(String objectName, Object object) {
        checkMutable(true);
        List<Component> propertyReplacerComponents = getPropertyReplacerComponents();
        if (propertyReplacerComponents != null) {
            for (Component replacerComponent : propertyReplacerComponents) {
                replacerComponent.pushObjectToContext(objectName, object);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushAllToContext(Map<String, Object> objects) {
        checkMutable(true);
        if (objects == null || objects.isEmpty()) {
            return;
        }

        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new HashMap<String, Object>();
        }

        context.putAll(objects);

        List<Component> propertyReplacerComponents = getPropertyReplacerComponents();
        if (propertyReplacerComponents != null) {
            for (Component replacerComponent : propertyReplacerComponents) {
                replacerComponent.pushAllToContext(objects);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<PropertyReplacer> getPropertyReplacers() {
        return this.propertyReplacers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
        checkMutable(true);
        this.propertyReplacers = propertyReplacers == null ? Collections.<PropertyReplacer> emptyList() :
                propertyReplacers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public int getOrder() {
        return this.order;
    }

    /**
     * Setter for the component's order
     * 
     * @param order
     */
    public void setOrder(int order) {
        checkMutable(true);
        this.order = order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Tooltip getToolTip() {
        return toolTip;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTip(Tooltip toolTip) {
        checkMutable(true);
        this.toolTip = toolTip;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventHandlerScript() {
        StringBuilder sb = new StringBuilder();

        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "load", getOnLoadScript()));

        // special handling for ready since it needs to bind to the document
        if (StringUtils.isNotBlank(getOnDocumentReadyScript())) {
            sb.append("jQuery(document).ready(function(e) {");
            sb.append(getOnDocumentReadyScript());
            sb.append("});");
        }

        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "unload", getOnUnloadScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "blur", getOnBlurScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "change", getOnChangeScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "click", getOnClickScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "dblclick", getOnDblClickScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "focus", getOnFocusScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "input", getOnInputScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "keypress", getOnKeyPressScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "keyup", getOnKeyUpScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "keydown", getOnKeyDownScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "mouseover", getOnMouseOverScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "mouseout", getOnMouseOutScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "mouseup", getOnMouseUpScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "mousedown", getOnMouseDownScript()));
        sb.append(ScriptUtils.buildEventHandlerScript(getId(), "mousemove", getOnMouseMoveScript()));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnLoadScript() {
        return onLoadScript;
    }

    /**
     * @see ScriptEventSupport#setOnLoadScript(java.lang.String)
     */
    public void setOnLoadScript(String onLoadScript) {
        checkMutable(true);
        this.onLoadScript = onLoadScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnDocumentReadyScript() {
        return this.onDocumentReadyScript;
    }

    /**
     * @see ScriptEventSupport#setOnDocumentReadyScript(java.lang.String)
     */
    public void setOnDocumentReadyScript(String onDocumentReadyScript) {
        checkMutable(true);
        this.onDocumentReadyScript = onDocumentReadyScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnUnloadScript() {
        return onUnloadScript;
    }

    /**
     * @see ScriptEventSupport#setOnUnloadScript(java.lang.String)
     */
    @Override
    public void setOnUnloadScript(String onUnloadScript) {
        checkMutable(true);
        this.onUnloadScript = onUnloadScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnCloseScript() {
        return onCloseScript;
    }

    /**
     * @see ScriptEventSupport#setOnCloseScript(java.lang.String)
     */
    @Override
    public void setOnCloseScript(String onCloseScript) {
        checkMutable(true);
        this.onCloseScript = onCloseScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnBlurScript() {
        return onBlurScript;
    }

    /**
     * @see ScriptEventSupport#setOnBlurScript(java.lang.String)
     */
    @Override
    public void setOnBlurScript(String onBlurScript) {
        checkMutable(true);
        this.onBlurScript = onBlurScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnChangeScript() {
        return onChangeScript;
    }

    /**
     * @see ScriptEventSupport#setOnChangeScript(java.lang.String)
     */
    @Override
    public void setOnChangeScript(String onChangeScript) {
        checkMutable(true);
        this.onChangeScript = onChangeScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnClickScript() {
        return onClickScript;
    }

    /**
     * @see ScriptEventSupport#setOnClickScript(java.lang.String)
     */
    @Override
    public void setOnClickScript(String onClickScript) {
        checkMutable(true);
        this.onClickScript = onClickScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnDblClickScript() {
        return onDblClickScript;
    }

    /**
     * @see ScriptEventSupport#setOnDblClickScript(java.lang.String)
     */
    @Override
    public void setOnDblClickScript(String onDblClickScript) {
        checkMutable(true);
        this.onDblClickScript = onDblClickScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnFocusScript() {
        return onFocusScript;
    }

    /**
     * @see ScriptEventSupport#setOnFocusScript(java.lang.String)
     */
    @Override
    public void setOnFocusScript(String onFocusScript) {
        checkMutable(true);
        this.onFocusScript = onFocusScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnSubmitScript() {
        return onSubmitScript;
    }

    /**
     * @see ScriptEventSupport#setOnSubmitScript(java.lang.String)
     */
    @Override
    public void setOnSubmitScript(String onSubmitScript) {
        checkMutable(true);
        this.onSubmitScript = onSubmitScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnInputScript() {
        return onInputScript;
    }

    /**
     * @see ScriptEventSupport#setOnInputScript(java.lang.String)
     */
    @Override
    public void setOnInputScript(String onInputScript) {
        checkMutable(true);
        this.onInputScript = onInputScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnKeyPressScript() {
        return onKeyPressScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyPressScript(java.lang.String)
     */
    @Override
    public void setOnKeyPressScript(String onKeyPressScript) {
        checkMutable(true);
        this.onKeyPressScript = onKeyPressScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnKeyUpScript() {
        return onKeyUpScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyUpScript(java.lang.String)
     */
    @Override
    public void setOnKeyUpScript(String onKeyUpScript) {
        checkMutable(true);
        this.onKeyUpScript = onKeyUpScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnKeyDownScript() {
        return onKeyDownScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyDownScript(java.lang.String)
     */
    @Override
    public void setOnKeyDownScript(String onKeyDownScript) {
        checkMutable(true);
        this.onKeyDownScript = onKeyDownScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnMouseOverScript() {
        return onMouseOverScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseOverScript(java.lang.String)
     */
    @Override
    public void setOnMouseOverScript(String onMouseOverScript) {
        checkMutable(true);
        this.onMouseOverScript = onMouseOverScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnMouseOutScript() {
        return onMouseOutScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseOutScript(java.lang.String)
     */
    @Override
    public void setOnMouseOutScript(String onMouseOutScript) {
        checkMutable(true);
        this.onMouseOutScript = onMouseOutScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnMouseUpScript() {
        return onMouseUpScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseUpScript(java.lang.String)
     */
    @Override
    public void setOnMouseUpScript(String onMouseUpScript) {
        checkMutable(true);
        this.onMouseUpScript = onMouseUpScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnMouseDownScript() {
        return onMouseDownScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseDownScript(java.lang.String)
     */
    @Override
    public void setOnMouseDownScript(String onMouseDownScript) {
        checkMutable(true);
        this.onMouseDownScript = onMouseDownScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getOnMouseMoveScript() {
        return onMouseMoveScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseMoveScript(java.lang.String)
     */
    @Override
    public void setOnMouseMoveScript(String onMouseMoveScript) {
        checkMutable(true);
        this.onMouseMoveScript = onMouseMoveScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Map<String, String> getTemplateOptions() {
        if (templateOptions == Collections.EMPTY_MAP && isMutable(true)) {
            templateOptions = new HashMap<String, String>();
        }

        return templateOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateOptions(Map<String, String> templateOptions) {
        checkMutable(true);
        if (templateOptions == null) {
            this.templateOptions = Collections.emptyMap();
        } else {
            this.templateOptions = templateOptions;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getTemplateOptionsJSString() {
        if (templateOptionsJSString != null) {
            return templateOptionsJSString;
        }

        if (templateOptions == null) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (Entry<String, String> option : templateOptions.entrySet()) {

            if (sb.length() > 1) {
                sb.append(",");
            }

            sb.append(option.getKey());
            sb.append(":");

            sb.append(ScriptUtils.convertToJsValue(option.getValue()));
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateOptionsJSString(String templateOptionsJSString) {
        checkMutable(true);
        this.templateOptionsJSString = templateOptionsJSString;
    }

    /**
     * When set if the condition is satisfied, the component will be displayed. The component MUST
     * BE a container or field type. progressiveRender is defined in a limited Spring EL syntax.
     * Only valid form property names, and, or, logical comparison operators (non-arithmetic),
     * #listContains, #emptyList, matches clause are allowed. String and regex values must use
     * single quotes ('), booleans must be either true or false, numbers must be a valid double,
     * either negative or positive.
     * 
     * <p>
     * DO NOT use progressiveRender and a conditional refresh statement on the same component unless
     * it is known that the component will always be visible in all cases when a conditional refresh
     * happens (ie conditional refresh has progressiveRender's condition anded with its own
     * condition).
     * </p>
     * 
     * <p>
     * <b>If a component should be refreshed every time it is shown, use the
     * progressiveRenderAndRefresh option with this property instead.</b>
     * </p>
     * 
     * @return progressiveRender expression
     */
    @BeanTagAttribute
    public String getProgressiveRender() {
        return this.progressiveRender;
    }

    /**
     * @param progressiveRender the progressiveRender to set.
     */
    public void setProgressiveRender(String progressiveRender) {
        checkMutable(true);
        this.progressiveRender = progressiveRender;
    }

    /**
     * When set if the condition is satisfied, the component will be refreshed.
     * 
     * <p>
     * The component MUST BE a container or field type. conditionalRefresh is defined in a limited
     * Spring EL syntax. Only valid form property names, and, or, logical comparison operators
     * (non-arithmetic), #listContains, #emptyList, and the matches clause are allowed. String and
     * regex values must use single quotes ('), booleans must be either true or false, numbers must
     * be a valid double either negative or positive.
     * 
     * <p>
     * DO NOT use progressiveRender and conditionalRefresh on the same component unless it is known
     * that the component will always be visible in all cases when a conditionalRefresh happens (ie
     * conditionalRefresh has progressiveRender's condition anded with its own condition). <b>If a
     * component should be refreshed every time it is shown, use the progressiveRenderAndRefresh
     * option with this property instead.</b>
     * </p>
     * 
     * @return the conditionalRefresh
     */
    @BeanTagAttribute
    public String getConditionalRefresh() {
        return this.conditionalRefresh;
    }

    /**
     * Set the conditional refresh condition
     * 
     * @param conditionalRefresh the conditionalRefresh to set
     */
    public void setConditionalRefresh(String conditionalRefresh) {
        checkMutable(true);
        this.conditionalRefresh = conditionalRefresh;
    }

    /**
     * Control names used to control progressive disclosure, set internally cannot be set.
     * 
     * @return the progressiveDisclosureControlNames
     */
    public List<String> getProgressiveDisclosureControlNames() {
        return this.progressiveDisclosureControlNames;
    }

    /**
     * The condition to show this component progressively converted to a js expression, set
     * internally cannot be set.
     * 
     * @return the progressiveDisclosureConditionJs
     */
    public String getProgressiveDisclosureConditionJs() {
        return this.progressiveDisclosureConditionJs;
    }

    /**
     * The condition to refresh this component converted to a js expression, set internally cannot
     * be set.
     * 
     * @return the conditionalRefreshConditionJs
     */
    public String getConditionalRefreshConditionJs() {
        return this.conditionalRefreshConditionJs;
    }

    /**
     * Control names used to control conditional refresh, set internally cannot be set.
     * 
     * @return the conditionalRefreshControlNames
     */
    public List<String> getConditionalRefreshControlNames() {
        return this.conditionalRefreshControlNames;
    }

    /**
     * When progressiveRenderViaAJAX is true, this component will be retrieved from the server when
     * it first satisfies its progressive render condition.
     * 
     * <p>
     * After the first retrieval, it is hidden/shown in the html by the js when its progressive
     * condition result changes. <b>By default, this is false, so components with progressive render
     * capabilities will always be already within the client html and toggled to be hidden or
     * visible.</b>
     * </p>
     * 
     * @return the progressiveRenderViaAJAX
     */
    @BeanTagAttribute
    public boolean isProgressiveRenderViaAJAX() {
        return this.progressiveRenderViaAJAX;
    }

    /**
     * @param progressiveRenderViaAJAX the progressiveRenderViaAJAX to set.
     */
    public void setProgressiveRenderViaAJAX(boolean progressiveRenderViaAJAX) {
        checkMutable(true);
        this.progressiveRenderViaAJAX = progressiveRenderViaAJAX;
    }

    /**
     * If true, when the progressiveRender condition is satisfied, the component will always be
     * retrieved from the server and shown(as opposed to being stored on the client, but hidden,
     * after the first retrieval as is the case with the progressiveRenderViaAJAX option).
     * 
     * <p>
     * <b>By default, this is false, so components with progressive render capabilities will always
     * be already within the client html and toggled to be hidden or visible.</b>
     * </p>
     * 
     * @return the progressiveRenderAndRefresh
     */
    @BeanTagAttribute
    public boolean isProgressiveRenderAndRefresh() {
        return this.progressiveRenderAndRefresh;
    }

    /**
     * Set the progressive render and refresh option.
     * 
     * @param progressiveRenderAndRefresh the progressiveRenderAndRefresh to set.
     */
    public void setProgressiveRenderAndRefresh(boolean progressiveRenderAndRefresh) {
        checkMutable(true);
        this.progressiveRenderAndRefresh = progressiveRenderAndRefresh;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getRefreshWhenChangedPropertyNames() {
        return this.refreshWhenChangedPropertyNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRefreshWhenChangedPropertyNames(List<String> refreshWhenChangedPropertyNames) {
        checkMutable(true);
        this.refreshWhenChangedPropertyNames =
                refreshWhenChangedPropertyNames == null ? Collections.<String> emptyList() :
                        Collections.<String> unmodifiableList(refreshWhenChangedPropertyNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "additionalComponentsToRefresh", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalComponentsToRefresh() {
        return additionalComponentsToRefresh;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdditionalComponentsToRefresh(List<String> additionalComponentsToRefresh) {
        checkMutable(true);
        this.additionalComponentsToRefresh = additionalComponentsToRefresh == null ? Collections.<String> emptyList() :
                Collections.<String> unmodifiableList(additionalComponentsToRefresh);
        this.additionalComponentsToRefreshJs = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdditionalComponentsToRefreshJs() {
        if (additionalComponentsToRefreshJs == null
                && additionalComponentsToRefresh != null
                && !additionalComponentsToRefresh.isEmpty()) {
            additionalComponentsToRefreshJs = ScriptUtils.convertStringListToJsArray(
                    this.getAdditionalComponentsToRefresh());
        }

        return additionalComponentsToRefreshJs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRefreshedByAction() {
        return refreshedByAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRefreshedByAction(boolean refreshedByAction) {
        checkMutable(true);
        this.refreshedByAction = refreshedByAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisclosedByAction() {
        return disclosedByAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisclosedByAction(boolean disclosedByAction) {
        checkMutable(true);
        this.disclosedByAction = disclosedByAction;
    }

    /**
     * Time in seconds that the component will be automatically refreshed
     * 
     * <p>
     * This will invoke the refresh process just like the conditionalRefresh and
     * refreshWhenChangedPropertyNames. When using this property methodToCallOnRefresh and id should
     * also be specified
     * </p>
     * 
     * @return refreshTimer
     */
    @BeanTagAttribute
    public int getRefreshTimer() {
        return refreshTimer;
    }

    /**
     * Setter for refreshTimer
     * 
     * @param refreshTimer
     */
    public void setRefreshTimer(int refreshTimer) {
        checkMutable(true);
        this.refreshTimer = refreshTimer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isResetDataOnRefresh() {
        return resetDataOnRefresh;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResetDataOnRefresh(boolean resetDataOnRefresh) {
        checkMutable(true);
        this.resetDataOnRefresh = resetDataOnRefresh;
    }

    /**
     * Name of a method on the controller that should be invoked as part of the component refresh
     * and disclosure process
     * 
     * <p>
     * During the component refresh or disclosure process it might be necessary to perform other
     * operations, such as preparing data or executing a business process. This allows the
     * configuration of a method on the underlying controller that should be called for the
     * component refresh action. In this method, the necessary logic can be performed and then the
     * base component update method invoked to carry out the component refresh.
     * </p>
     * 
     * <p>
     * Controller method to invoke must accept the form, binding result, request, and response
     * arguments
     * </p>
     * 
     * @return valid controller method name
     */
    @BeanTagAttribute
    public String getMethodToCallOnRefresh() {
        return methodToCallOnRefresh;
    }

    /**
     * Setter for the controller method to call for a refresh or disclosure action on this component
     * 
     * @param methodToCallOnRefresh
     */
    public void setMethodToCallOnRefresh(String methodToCallOnRefresh) {
        checkMutable(true);
        this.methodToCallOnRefresh = methodToCallOnRefresh;
    }

    /**
     * Flag indicating that this component and its nested components must be skipped when keyboard
     * tabbing.
     * 
     * @return the skipInTabOrder flag
     */
    @BeanTagAttribute
    public boolean isSkipInTabOrder() {
        return skipInTabOrder;
    }

    /**
     * @see ComponentBase#isSkipInTabOrder()
     */
    public void setSkipInTabOrder(boolean skipInTabOrder) {
        checkMutable(true);
        this.skipInTabOrder = skipInTabOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Map<String, String> getDataAttributes() {
        if (dataAttributes == Collections.EMPTY_MAP) {
            dataAttributes = new HashMap<String, String>();
        }

        return dataAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataAttributes(Map<String, String> dataAttributes) {
        checkMutable(true);
        if (dataAttributes == null) {
            this.dataAttributes = Collections.emptyMap();
        } else {
            this.dataAttributes = dataAttributes;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Map<String, String> getScriptDataAttributes() {
        if (scriptDataAttributes == Collections.EMPTY_MAP) {
            scriptDataAttributes = new HashMap<String, String>();
        }

        return scriptDataAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScriptDataAttributes(Map<String, String> scriptDataAttributes) {
        this.scriptDataAttributes = scriptDataAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDataAttribute(String key, String value) {
        checkMutable(true);

        if (dataAttributes == Collections.EMPTY_MAP) {
            dataAttributes = new HashMap<String, String>();
        }

        dataAttributes.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addScriptDataAttribute(String key, String value) {
        checkMutable(true);

        if (scriptDataAttributes == Collections.EMPTY_MAP) {
            scriptDataAttributes = new HashMap<String, String>();
        }

        scriptDataAttributes.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleDataAttributes() {
        String attributes = "";

        if (getDataAttributes() == null) {
            return attributes;
        }

        for (Map.Entry<String, String> data : getDataAttributes().entrySet()) {
            if (data != null && data.getValue() != null) {
                attributes = attributes + " " + "data-" + data.getKey() + "=\"" +
                        KRADUtils.convertToHTMLAttributeSafeString(data.getValue()) + "\"";
            }
        }

        return attributes;
    }

    @Override
    public String getScriptDataAttributesJs() {
        String script = "";

        if (getScriptDataAttributes() == null || getScriptDataAttributes().isEmpty()) {
            return script;
        }

        String id = this.getId().replace(".", "\\\\.");
        String selector = "var dataComponent = jQuery('#" + id + "');";
        script = ScriptUtils.appendScript(script, selector);

        for (Map.Entry<String, String> data : getScriptDataAttributes().entrySet()) {
            if (data != null && data.getValue() != null) {
                script = ScriptUtils.appendScript(script,
                        "dataComponent.data('" + data.getKey() + "'," + ScriptUtils.convertToJsValue(data.getValue())
                                + ");");
            }
        }

        return script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getRole() {
        return role;
    }

    /**
     * {@inheritDoc}
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Map<String, String> getAriaAttributes() {
        if (ariaAttributes == Collections.EMPTY_MAP) {
            ariaAttributes = new LifecycleAwareMap<String, String>(this);
        }

        return ariaAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAriaAttributes(Map<String, String> ariaAttributes) {
        checkMutable(true);
        if (ariaAttributes == null) {
            this.ariaAttributes = Collections.emptyMap();
        } else {
            this.ariaAttributes = new LifecycleAwareMap<String, String>(this, ariaAttributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAriaAttribute(String key, String value) {
        checkMutable(true);

        if (ariaAttributes == Collections.EMPTY_MAP) {
            ariaAttributes = new LifecycleAwareMap<String, String>(this);
        }

        ariaAttributes.put(key, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAriaAttributesAsString() {
        String attributes = "";

        if (getAriaAttributes() == null) {
            return attributes;
        }

        for (Map.Entry<String, String> aria : getAriaAttributes().entrySet()) {
            if (aria != null && aria.getValue() != null) {
                attributes = attributes + " " + "aria-" + aria.getKey() + "=\"" +
                        KRADUtils.convertToHTMLAttributeSafeString(aria.getValue()) + "\"";
            }
        }

        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute(name = "preContent", type = BeanTagAttribute.AttributeType.ANY)
    public String getPreRenderContent() {
        return preRenderContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreRenderContent(String preRenderContent) {
        checkMutable(true);
        this.preRenderContent = preRenderContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "postContent", type = BeanTagAttribute.AttributeType.ANY)
    public String getPostRenderContent() {
        return postRenderContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPostRenderContent(String postRenderContent) {
        checkMutable(true);
        this.postRenderContent = postRenderContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeIf() {
        return this.excludeIf;
    }

    /**
     * @see #getExcludeIf()
     */
    public void setExcludeIf(String excludeIf) {
        this.excludeIf = excludeIf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeUnless() {
        return this.excludeUnless;
    }

    /**
     * @see #getExcludeIf()
     */
    public void setExcludeUnless(String excludeUnless) {
        this.excludeUnless = excludeUnless;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentBase clone() throws CloneNotSupportedException {
        ComponentBase copy = (ComponentBase) super.clone();

        // Copy initialized status, but reset to created for others.
        // This allows prototypes to bypass repeating the initialized phase.
        if (UifConstants.ViewStatus.INITIALIZED.equals(viewStatus)) {
            copy.viewStatus = UifConstants.ViewStatus.INITIALIZED;
        } else {
            copy.viewStatus = UifConstants.ViewStatus.CREATED;
        }

        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Check for invalid characters in the components id
        if (getId() != null) {
            if (getId().contains("'")
                    || getId().contains("\"")
                    || getId().contains("[]")
                    || getId().contains(".")
                    || getId().contains("#")) {
                String currentValues[] = {"id = " + getId()};
                tracer.createError("Id contains invalid characters", currentValues);
            }
        }

        if (tracer.getValidationStage() == ValidationTrace.BUILD) {
            // Check for a render presence if the component is set to render
            if ((isProgressiveRenderViaAJAX() || isProgressiveRenderAndRefresh()) && (getProgressiveRender() == null)) {
                String currentValues[] = {"progressiveRenderViaAJAX = " + isProgressiveRenderViaAJAX(),
                        "progressiveRenderAndRefresh = " + isProgressiveRenderAndRefresh(),
                        "progressiveRender = " + getProgressiveRender()};
                tracer.createError(
                        "ProgressiveRender must be set if progressiveRenderViaAJAX or progressiveRenderAndRefresh are true",
                        currentValues);
            }
        }

        // Check for rendered html if the component is set to self render
        if (isSelfRendered() && getRenderedHtmlOutput() == null) {
            String currentValues[] =
            {"selfRendered = " + isSelfRendered(), "renderedHtmlOutput = " + getRenderedHtmlOutput()};
            tracer.createError("RenderedHtmlOutput must be set if selfRendered is true", currentValues);
        }

        // Check to prevent over writing of session persistence status
        if (isDisableSessionPersistence() && isForceSessionPersistence()) {
            String currentValues[] = {"disableSessionPersistence = " + isDisableSessionPersistence(),
                    "forceSessionPersistence = " + isForceSessionPersistence()};
            tracer.createWarning("DisableSessionPersistence and forceSessionPersistence cannot be both true",
                    currentValues);
        }

        // Check for un-executable data resets when no refresh option is set
        if (getMethodToCallOnRefresh() != null || isResetDataOnRefresh()) {
            if (!isProgressiveRenderAndRefresh()
                    && !isRefreshedByAction()
                    && !isProgressiveRenderViaAJAX()
                    && !StringUtils.isNotEmpty(conditionalRefresh)
                    && !(refreshTimer > 0)) {
                String currentValues[] = {"methodToCallONRefresh = " + getMethodToCallOnRefresh(),
                        "resetDataONRefresh = " + isResetDataOnRefresh(),
                        "progressiveRenderAndRefresh = " + isProgressiveRenderAndRefresh(),
                        "refreshedByAction = " + isRefreshedByAction(),
                        "progressiveRenderViaAJAX = " + isProgressiveRenderViaAJAX(),
                        "conditionalRefresh = " + getConditionalRefresh(), "refreshTimer = " + getRefreshTimer()};
                tracer.createWarning(
                        "MethodToCallONRefresh and resetDataONRefresh should only be set when a trigger event is set",
                        currentValues);
            }
        }

        // Check to prevent complications with rendering and refreshing a component that is not always shown
        if (StringUtils.isNotEmpty(getProgressiveRender()) && StringUtils.isNotEmpty(conditionalRefresh)) {
            String currentValues[] = {"progressiveRender = " + getProgressiveRender(),
                    "conditionalRefresh = " + getConditionalRefresh()};
            tracer.createWarning(
                    "DO NOT use progressiveRender and conditionalRefresh on the same component unless "
                            + "it is known that the component will always be visible in all cases when a conditionalRefresh "
                            + "happens (ie conditionalRefresh has progressiveRender's condition anded with its own condition). "
                            + "If a component should be refreshed every time it is shown, use the progressiveRenderAndRefresh "
                            + "option with this property instead.", currentValues);
        }

        // Check for valid Spring EL format for progressiveRender
        if (!Validator.validateSpringEL(getProgressiveRender())) {
            String currentValues[] = {"progressiveRender =" + getProgressiveRender()};
            tracer.createError("ProgressiveRender must follow the Spring EL @{} format", currentValues);
        }

        // Check for valid Spring EL format for conditionalRefresh
        if (!Validator.validateSpringEL(getConditionalRefresh())) {
            String currentValues[] = {"conditionalRefresh =" + getConditionalRefresh()};
            tracer.createError("conditionalRefresh must follow the Spring EL @{} format", currentValues);
        }
    }
}
