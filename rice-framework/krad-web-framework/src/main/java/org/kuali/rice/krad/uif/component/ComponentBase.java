/**
 * Copyright 2005-2013 The Kuali Foundation
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
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.control.ControlBase;
import org.kuali.rice.krad.uif.lifecycle.NoLifecycle;
import org.kuali.rice.krad.uif.lifecycle.RenderComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;
import org.kuali.rice.krad.uif.util.LifecycleAwareMap;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Base implementation of <code>Component</code> which other component implementations should extend
 * 
 * <p>
 * Provides base component properties such as id and template. Also provides default implementation
 * for the <code>ScriptEventSupport</code> and <code>Ordered</code> interfaces. By default no script
 * events except the onDocumentReady are supported.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "componentBase-bean", parent = "Uif-ComponentBase")
public abstract class ComponentBase extends UifDictionaryBeanBase implements Component {

    private static final long serialVersionUID = -4449335748129894350L;
    
    private String id;
    private String baseId;
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
    private boolean readOnly;
    private Boolean required;

    private String align;
    private String valign;
    private String width;

    // optional table-backed layout options
    private int colSpan;
    private int rowSpan;
    private List<String> cellCssClasses;
    private String cellStyle;
    private String cellWidth;

    private String style;
    
    private List<String> libraryCssClasses;
    private List<String> cssClasses;
    private List<String> additionalCssClasses;

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

    private String preRenderContent;
    private String postRenderContent;
    
    public ComponentBase() {
        super();

//        ProcessLogger.ntrace("new-comp:", ":" + getClass().getSimpleName(), 1000);

        order = 0;
        colSpan = 1;
        rowSpan = 1;

        viewStatus = ViewStatus.CREATED;
        
        render = true;
        selfRendered = false;
        progressiveRenderViaAJAX = false;
        progressiveRenderAndRefresh = false;
        refreshedByAction = false;
        resetDataOnRefresh = false;
        disableSessionPersistence = false;
        forceSessionPersistence = false;

        context = Collections.emptyMap();
        dataAttributes = Collections.emptyMap();
        templateOptions = Collections.emptyMap();
        
        cssClasses = Collections.emptyList();
        libraryCssClasses = Collections.emptyList();
        additionalCssClasses = Collections.emptyList();
    }
    
    /**
     * @see LifecycleElement#checkMutable(boolean)
     */
    public void checkMutable(boolean legalDuringInitialization) {
        if (UifConstants.ViewStatus.CACHED.equals(viewStatus)) {
            ViewLifecycle.reportIllegalState("Cached component " + getClass() + " " + getId()
                    + " is immutable, use copy() to get a mutable instance");
            return;
        }

        if (ViewLifecycle.isActive()) {
            return;
        }
        
        if (UifConstants.ViewStatus.CREATED.equals(viewStatus)) {
            if (!legalDuringInitialization) {
                ViewLifecycle.reportIllegalState(
                        "View has not been fully initialized, attempting to change component "
                                + getClass() + " " + getId());
                return;
            }
        } else {
            ViewLifecycle.reportIllegalState("Component " + getClass() + " " + getId()
                    + " has been initialized, but the lifecycle is not active.");
            return;
        }
    }

    /**
     * @see LifecycleElement#isMutable(boolean)
     */
    public boolean isMutable(boolean legalDuringInitialization) {
        return (UifConstants.ViewStatus.CREATED.equals(viewStatus) && legalDuringInitialization)
                || ViewLifecycle.isActive();
    }
    
    /**
     * Indicates what lifecycle phase the component instance is in
     *
     * <p>
     * The view lifecycle begins with the CREATED status. In this status a new
     * instance of the view has been retrieved from the dictionary, but no
     * further processing has been done. After the initialize phase has been run
     * the status changes to INITIALIZED. After the model has been applied and
     * the view is ready for render the status changes to FINAL
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
     * @param viewStatus
     */
    @Override
    public void setViewStatus(String status) {
        if (!UifConstants.ViewStatus.CREATED.equals(status)) {
            checkMutable(true);
        }
        
        this.viewStatus = status;
    }

    /**
     * Setter for the view status
     *
     * @param viewStatus
     */
    @Override
    public void setViewStatus(ViewLifecyclePhase phase) {
        if (!viewStatus.equals(phase.getStartViewStatus())) {
            ViewLifecycle.reportIllegalState("Component " + getClass().getName() + " is not in expected status "
                    + phase.getStartViewStatus() + " marking the completion of a lifecycle phase, found " + viewStatus
                    + "\nPhase: " + phase);
        }

        this.viewStatus = phase.getEndViewStatus();
    }

    /**
     * @see Component#notifyCompleted(org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase)
     */
    @Override
    public void notifyCompleted(ViewLifecyclePhase phase) {
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
     * Indicates whether the component has been updated from the model and final
     * updates made.
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
     * <li></li>
     * </ul>
     * 
     * @see org.kuali.rice.krad.uif.component.Component#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(Object model) {
    }

    /**
     * The following updates are done here:
     * 
     * <ul>
     * <li>Evaluate the progressive render condition (if set) and combine with the current render
     * status to set the render status</li>
     * </ul>
     * 
     * @see org.kuali.rice.krad.uif.component.Component#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
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
     * The following finalization is done here:
     * 
     * <ul>
     * <li>progressiveRender and conditionalRefresh variables are processed if set</li>
     * <li>If any of the style properties were given, sets the style string on the style property</li>
     * <li>Set the skipInTabOrder flag for nested components</li>
     * </ul>
     * 
     * @see org.kuali.rice.krad.uif.component.Component#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        // progressiveRender expression setup
        if (StringUtils.isNotEmpty(progressiveRender)) {
            View view = ViewLifecycle.getView();
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
            progressiveRender = expressionEvaluator.replaceBindingPrefixes(view, this, progressiveRender);
            progressiveDisclosureControlNames = new ArrayList<String>();
            progressiveDisclosureConditionJs = ExpressionUtils.parseExpression(progressiveRender,
                    progressiveDisclosureControlNames);
        }

        // conditional refresh expression setup
        if (StringUtils.isNotEmpty(conditionalRefresh)) {
            View view = ViewLifecycle.getView();
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
            conditionalRefresh = expressionEvaluator.replaceBindingPrefixes(view, this, conditionalRefresh);
            conditionalRefreshControlNames = new ArrayList<String>();
            conditionalRefreshConditionJs = ExpressionUtils.parseExpression(conditionalRefresh,
                    conditionalRefreshControlNames);
        }

        if (refreshWhenChangedPropertyNames != null) {
            View view = ViewLifecycle.getView();
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
            List<String> adjustedRefreshPropertyNames = new ArrayList<String>(refreshWhenChangedPropertyNames.size());
            for (String refreshPropertyName : refreshWhenChangedPropertyNames) {
                adjustedRefreshPropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this,
                        refreshPropertyName));
            }
            refreshWhenChangedPropertyNames = adjustedRefreshPropertyNames;
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
            for (Component component : getComponentsForLifecycle()) {
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

        // put together all css class names for this component, in order
        List<String> finalCssClasses = new ArrayList<String>();

        if (this.libraryCssClasses != null && (!ViewLifecycle.isActive() ||
                ViewLifecycle.getView().isUseLibraryCssClasses())) {
            finalCssClasses.addAll(libraryCssClasses);
        }

        if (this.cssClasses != null) {
            finalCssClasses.addAll(cssClasses);
        }

        if (this.additionalCssClasses != null) {
            finalCssClasses.addAll(additionalCssClasses);
        }

        cssClasses = finalCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.util.LifecycleElement#initializePendingTasks(org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase, java.util.Queue)
     */
    @Override
    public void initializePendingTasks(ViewLifecyclePhase phase, Queue<ViewLifecycleTask> pendingTasks) {
        // TODO: migrate tasks
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentsForLifecycle()
     */
    @NoLifecycle
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        components.add(toolTip);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        if (componentModifiers != null) {
            for (ComponentModifier modifier : componentModifiers) {
                components.addAll(modifier.getComponentPrototypes());
            }
        }

        List<Component> propertyReplacerComponents = getPropertyReplacerComponents();
        if (propertyReplacerComponents != null) {
            components.addAll(propertyReplacerComponents);
        }

        return components;
    }

    /**
     * Returns list of components that are being held in property replacers configured for this
     * component
     * 
     * @return List<Component>
     */
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
     * @see org.kuali.rice.krad.uif.component.Component#getId()
     */
    @BeanTagAttribute(name = "id")
    public String getId() {
        return this.id;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setId(java.lang.String)
     */
    public void setId(String id) {
        checkMutable(true);
        this.id = id;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getBaseId()
     */
    public String getBaseId() {
        return this.baseId;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setBaseId(java.lang.String)
     */
    public void setBaseId(String baseId) {
        checkMutable(true);
        this.baseId = baseId;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTemplate()
     */
    @BeanTagAttribute(name = "template")
    public String getTemplate() {
        return this.template;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTemplate(java.lang.String)
     */
    public void setTemplate(String template) {
        checkMutable(true);
        this.template = template;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTemplateName()
     */
    @BeanTagAttribute(name = "templateName")
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTemplateName(java.lang.String)
     */
    public void setTemplateName(String templateName) {
        checkMutable(true);
        this.templateName = templateName;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTitle()
     */
    @BeanTagAttribute(name = "title")
    public String getTitle() {
        return this.title;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        checkMutable(true);
        this.title = title;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isHidden()
     */
    @BeanTagAttribute(name = "hidden")
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setHidden(boolean)
     */
    public void setHidden(boolean hidden) {
        checkMutable(true);
        this.hidden = hidden;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isReadOnly()
     */
    @BeanTagAttribute(name = "readOnly")
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        checkMutable(true);
        this.readOnly = readOnly;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRequired()
     */
    @BeanTagAttribute(name = "required")
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRequired(java.lang.Boolean)
     */
    public void setRequired(Boolean required) {
        checkMutable(true);
        this.required = required;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isRender()
     */
    @BeanTagAttribute(name = "render")
    public boolean isRender() {
        return this.render;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRender(boolean)
     */
    public void setRender(boolean render) {
        checkMutable(true);
        this.render = render;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isRetrieveViaAjax()
     */
    @BeanTagAttribute(name = "retrieveViaAjax")
    public boolean isRetrieveViaAjax() {
        return retrieveViaAjax;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRetrieveViaAjax(boolean)
     */
    public void setRetrieveViaAjax(boolean retrieveViaAjax) {
        checkMutable(true);
        this.retrieveViaAjax = retrieveViaAjax;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getColSpan()
     */
    @BeanTagAttribute(name = "ColSpan")
    public int getColSpan() {
        return this.colSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setColSpan(int)
     */
    public void setColSpan(int colSpan) {
        checkMutable(true);
        this.colSpan = colSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRowSpan()
     */
    @BeanTagAttribute(name = "rowSpan")
    public int getRowSpan() {
        return this.rowSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRowSpan(int)
     */
    public void setRowSpan(int rowSpan) {
        checkMutable(true);
        this.rowSpan = rowSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCellCssClasses()
     */
    public List<String> getCellCssClasses() {
        return cellCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setCellCssClasses(java.util.List)
     */
    public void setCellCssClasses(List<String> cellCssClasses) {
        checkMutable(true);
        this.cellCssClasses = cellCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#addCellCssClass(String)
     */
    public void addCellCssClass(String cssClass) {
        checkMutable(false);
        if (this.cellCssClasses == null) {
            this.cellCssClasses = new ArrayList<String>();
        }

        if (cssClass != null) {
            this.cellCssClasses.add(cssClass);
        }
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list with a space
     * delimiter
     * 
     * @return class attribute string
     */
    public String getCellStyleClassesAsString() {
        if (cellCssClasses != null) {
            return StringUtils.join(cellCssClasses, " ");
        }

        return "";
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCellStyle()
     */
    public String getCellStyle() {
        return cellStyle;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setCellStyle(java.lang.String)
     */
    public void setCellStyle(String cellStyle) {
        checkMutable(true);
        this.cellStyle = cellStyle;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCellWidth()
     */
    public String getCellWidth() {
        return cellWidth;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setCellWidth(java.lang.String)
     */
    public void setCellWidth(String cellWidth) {
        checkMutable(true);
        this.cellWidth = cellWidth;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAlign()
     */
    @BeanTagAttribute(name = "align")
    public String getAlign() {
        return this.align;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setAlign(java.lang.String)
     */
    public void setAlign(String align) {
        checkMutable(true);
        this.align = align;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getValign()
     */
    @BeanTagAttribute(name = "valign")
    public String getValign() {
        return this.valign;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setValign(java.lang.String)
     */
    public void setValign(String valign) {
        checkMutable(true);
        this.valign = valign;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getWidth()
     */
    @BeanTagAttribute(name = "width")
    public String getWidth() {
        return this.width;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setWidth(java.lang.String)
     */
    public void setWidth(String width) {
        checkMutable(true);
        this.width = width;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getStyle()
     */
    @BeanTagAttribute(name = "style")
    public String getStyle() {
        return this.style;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setStyle(java.lang.String)
     */
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
            libraryCssClasses = new LifecycleAwareList<String>(this);
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
            this.libraryCssClasses = new LifecycleAwareList<String>(this, libraryCssClasses);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCssClasses()
     */
    @BeanTagAttribute(name = "cssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getCssClasses() {
        if (cssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            cssClasses = new LifecycleAwareList<String>(this);
        }
        
        return cssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setCssClasses(java.util.List)
     */
    public void setCssClasses(List<String> cssClasses) {
        checkMutable(true);
        if (cssClasses == null) {
            this.cssClasses = Collections.emptyList();
        } else {
            this.cssClasses = new LifecycleAwareList<String>(this, cssClasses);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAdditionalCssClasses()
     */
    @BeanTagAttribute(name = "additionalCssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalCssClasses() {
        if (additionalCssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            additionalCssClasses = new LifecycleAwareList<String>(this);
        }
        
        return additionalCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setAdditionalCssClasses(java.util.List)
     */
    public void setAdditionalCssClasses(List<String> additionalCssClasses) {
        checkMutable(true);
        if (additionalCssClasses == null) {
            this.additionalCssClasses = Collections.emptyList();
        } else {
            this.additionalCssClasses = new LifecycleAwareList<String>(this, additionalCssClasses);
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
            return StringUtils.join(cssClasses, " ");
        }

        return "";
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#addStyleClass(java.lang.String)
     */
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
     * @see org.kuali.rice.krad.uif.component.Component#appendToStyle(java.lang.String)
     */
    public void appendToStyle(String styleRules) {
        checkMutable(false);
        if (style == null) {
            style = "";
        }
        style = style + styleRules;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodToCall()
     */
    @BeanTagAttribute(name = "finalizeMethodToCall")
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
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodAdditionalArguments()
     */
    @BeanTagAttribute(name = "finalizeMethodAdditionalArguments", type = BeanTagAttribute.AttributeType.LISTBEAN)
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
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodInvoker()
     */
    @BeanTagAttribute(name = "finalizeMethodInvoker", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
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
     * @see org.kuali.rice.krad.uif.component.Component#isSelfRendered()
     */
    @BeanTagAttribute(name = "selfRendered")
    public boolean isSelfRendered() {
        return this.selfRendered;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setSelfRendered(boolean)
     */
    public void setSelfRendered(boolean selfRendered) {
        this.selfRendered = selfRendered;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRenderedHtmlOutput()
     */
    @BeanTagAttribute(name = "renderedHtmlOutput")
    public String getRenderedHtmlOutput() {
        return this.renderedHtmlOutput;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRenderedHtmlOutput(java.lang.String)
     */
    public void setRenderedHtmlOutput(String renderedHtmlOutput) {
        this.renderedHtmlOutput = renderedHtmlOutput;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isDisableSessionPersistence()
     */
    @BeanTagAttribute(name = "disableSessionPersistence")
    public boolean isDisableSessionPersistence() {
        return disableSessionPersistence;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setDisableSessionPersistence(boolean)
     */
    public void setDisableSessionPersistence(boolean disableSessionPersistence) {
        checkMutable(true);
        this.disableSessionPersistence = disableSessionPersistence;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isForceSessionPersistence()
     */
    @BeanTagAttribute(name = "forceSessionPersistence")
    public boolean isForceSessionPersistence() {
        return forceSessionPersistence;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setForceSessionPersistence(boolean)
     */
    public void setForceSessionPersistence(boolean forceSessionPersistence) {
        checkMutable(true);
        this.forceSessionPersistence = forceSessionPersistence;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentSecurity()
     */
    @BeanTagAttribute(name = "componentSecurity", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public ComponentSecurity getComponentSecurity() {
        return componentSecurity;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setComponentSecurity(org.kuali.rice.krad.uif.component.ComponentSecurity)
     */
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        checkMutable(true);
        this.componentSecurity = componentSecurity;
    }

    /**
     * Initializes (if necessary) the component security instance for the component type
     */
    protected void initializeComponentSecurity() {
        if (this.componentSecurity == null) {
            this.componentSecurity = DataObjectUtils.newInstance(ComponentSecurity.class);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#isEditAuthz()
     */
    public Boolean isEditAuthz() {
        initializeComponentSecurity();

        return this.componentSecurity.isEditAuthz();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#setEditAuthz(boolean)
     */
    public void setEditAuthz(Boolean editAuthz) {
        checkMutable(true);
        initializeComponentSecurity();

        this.componentSecurity.setEditAuthz(editAuthz);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#isViewAuthz()
     */
    public Boolean isViewAuthz() {
        initializeComponentSecurity();

        return this.componentSecurity.isViewAuthz();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentSecurity#setViewAuthz(boolean)
     */
    public void setViewAuthz(Boolean viewAuthz) {
        checkMutable(true);
        initializeComponentSecurity();

        this.componentSecurity.setViewAuthz(viewAuthz);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentModifiers()
     */
    @BeanTagAttribute(name = "componentModifiers", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<ComponentModifier> getComponentModifiers() {
        return this.componentModifiers;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setComponentModifiers(java.util.List)
     */
    public void setComponentModifiers(List<ComponentModifier> componentModifiers) {
        checkMutable(true);
        this.componentModifiers = componentModifiers == null
                ? Collections.<ComponentModifier> emptyList()
                : Collections.<ComponentModifier> unmodifiableList(componentModifiers);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getContext()
     */
    @BeanTagAttribute(name = "context", type = BeanTagAttribute.AttributeType.MAPBEAN)
    public Map<String, Object> getContext() {
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
        }
        
        return context;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setContext(java.util.Map)
     */
    public void setContext(Map<String, Object> context) {
        checkMutable(true);

        if (context == null) {
            this.context = Collections.emptyMap();
        } else {
            this.context = new LifecycleAwareMap<String, Object>(this, context);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#pushObjectToContext(java.lang.String,
     *      java.lang.Object)
     */
    public void pushObjectToContext(String objectName, Object object) {
        checkMutable(true);
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
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
     * @see org.kuali.rice.krad.uif.component.ComponentBase#pushAllToContext
     */
    public void pushAllToContext(Map<String, Object> objects) {
        checkMutable(true);
        if (objects == null || objects.isEmpty()) {
            return;
        }
        
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
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
     * @see org.kuali.rice.krad.uif.component.Component#getPropertyReplacers()
     */
    @BeanTagAttribute(name = "propertyReplacers", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<PropertyReplacer> getPropertyReplacers() {
        return this.propertyReplacers;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setPropertyReplacers(java.util.List)
     */
    public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
        checkMutable(true);
        this.propertyReplacers = propertyReplacers == null
                ? Collections.<PropertyReplacer> emptyList()
                : Collections.<PropertyReplacer> unmodifiableList(propertyReplacers);
    }

    /**
     * @see org.springframework.core.Ordered#getOrder()
     */
    @BeanTagAttribute(name = "order")
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
     * @see org.kuali.rice.krad.uif.component.Component#getToolTip()
     */
    @BeanTagAttribute(name = "toolTip", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Tooltip getToolTip() {
        return toolTip;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setToolTip(Tooltip)
     */
    public void setToolTip(Tooltip toolTip) {
        checkMutable(true);
        this.toolTip = toolTip;
    }

    /**
     * @see Component#getEventHandlerScript()
     */
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
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnLoadScript()
     */
    @BeanTagAttribute(name = "onLoadScript")
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
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnDocumentReadyScript()
     */
    @BeanTagAttribute(name = "onDocumentReadyScript")
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
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnUnloadScript()
     */
    @BeanTagAttribute(name = "onUnloadScript")
    public String getOnUnloadScript() {
        return onUnloadScript;
    }

    /**
     * @see ScriptEventSupport#setOnUnloadScript(java.lang.String)
     */
    public void setOnUnloadScript(String onUnloadScript) {
        checkMutable(true);
        this.onUnloadScript = onUnloadScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnCloseScript()
     */
    @BeanTagAttribute(name = "onCloseScript")
    public String getOnCloseScript() {
        return onCloseScript;
    }

    /**
     * @see ScriptEventSupport#setOnCloseScript(java.lang.String)
     */
    public void setOnCloseScript(String onCloseScript) {
        checkMutable(true);
        this.onCloseScript = onCloseScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnBlurScript()
     */
    @BeanTagAttribute(name = "onBlurScript")
    public String getOnBlurScript() {
        return onBlurScript;
    }

    /**
     * @see ScriptEventSupport#setOnBlurScript(java.lang.String)
     */
    public void setOnBlurScript(String onBlurScript) {
        checkMutable(true);
        this.onBlurScript = onBlurScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnChangeScript()
     */
    @BeanTagAttribute(name = "onChangeScript")
    public String getOnChangeScript() {
        return onChangeScript;
    }

    /**
     * @see ScriptEventSupport#setOnChangeScript(java.lang.String)
     */
    public void setOnChangeScript(String onChangeScript) {
        checkMutable(true);
        this.onChangeScript = onChangeScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnClickScript()
     */
    @BeanTagAttribute(name = "onClickScript")
    public String getOnClickScript() {
        return onClickScript;
    }

    /**
     * @see ScriptEventSupport#setOnClickScript(java.lang.String)
     */
    public void setOnClickScript(String onClickScript) {
        checkMutable(true);
        this.onClickScript = onClickScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnDblClickScript()
     */
    @BeanTagAttribute(name = "onDblClickScript")
    public String getOnDblClickScript() {
        return onDblClickScript;
    }

    /**
     * @see ScriptEventSupport#setOnDblClickScript(java.lang.String)
     */
    public void setOnDblClickScript(String onDblClickScript) {
        checkMutable(true);
        this.onDblClickScript = onDblClickScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnFocusScript()
     */
    @BeanTagAttribute(name = "onFocusScript")
    public String getOnFocusScript() {
        return onFocusScript;
    }

    /**
     * @see ScriptEventSupport#setOnFocusScript(java.lang.String)
     */
    public void setOnFocusScript(String onFocusScript) {
        checkMutable(true);
        this.onFocusScript = onFocusScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnSubmitScript()
     */
    @BeanTagAttribute(name = "onSubmitScript")
    public String getOnSubmitScript() {
        return onSubmitScript;
    }

    /**
     * @see ScriptEventSupport#setOnSubmitScript(java.lang.String)
     */
    public void setOnSubmitScript(String onSubmitScript) {
        checkMutable(true);
        this.onSubmitScript = onSubmitScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyPressScript()
     */
    @BeanTagAttribute(name = "onKeyPressScript")
    public String getOnKeyPressScript() {
        return onKeyPressScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyPressScript(java.lang.String)
     */
    public void setOnKeyPressScript(String onKeyPressScript) {
        checkMutable(true);
        this.onKeyPressScript = onKeyPressScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyUpScript()
     */
    @BeanTagAttribute(name = "onKeyUpScript")
    public String getOnKeyUpScript() {
        return onKeyUpScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyUpScript(java.lang.String)
     */
    public void setOnKeyUpScript(String onKeyUpScript) {
        checkMutable(true);
        this.onKeyUpScript = onKeyUpScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyDownScript()
     */
    @BeanTagAttribute(name = "onKeyDownScript")
    public String getOnKeyDownScript() {
        return onKeyDownScript;
    }

    /**
     * @see ScriptEventSupport#setOnKeyDownScript(java.lang.String)
     */
    public void setOnKeyDownScript(String onKeyDownScript) {
        checkMutable(true);
        this.onKeyDownScript = onKeyDownScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseOverScript()
     */
    @BeanTagAttribute(name = "onMouseOverScript")
    public String getOnMouseOverScript() {
        return onMouseOverScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseOverScript(java.lang.String)
     */
    public void setOnMouseOverScript(String onMouseOverScript) {
        checkMutable(true);
        this.onMouseOverScript = onMouseOverScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseOutScript()
     */
    @BeanTagAttribute(name = "onMouseOutScript")
    public String getOnMouseOutScript() {
        return onMouseOutScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseOutScript(java.lang.String)
     */
    public void setOnMouseOutScript(String onMouseOutScript) {
        checkMutable(true);
        this.onMouseOutScript = onMouseOutScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseUpScript()
     */
    @BeanTagAttribute(name = "onMouseUpScript")
    public String getOnMouseUpScript() {
        return onMouseUpScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseUpScript(java.lang.String)
     */
    public void setOnMouseUpScript(String onMouseUpScript) {
        checkMutable(true);
        this.onMouseUpScript = onMouseUpScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseDownScript()
     */
    @BeanTagAttribute(name = "onMouseDownScript")
    public String getOnMouseDownScript() {
        return onMouseDownScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseDownScript(java.lang.String)
     */
    public void setOnMouseDownScript(String onMouseDownScript) {
        checkMutable(true);
        this.onMouseDownScript = onMouseDownScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseMoveScript()
     */
    @BeanTagAttribute(name = "onMouseMoveScript")
    public String getOnMouseMoveScript() {
        return onMouseMoveScript;
    }

    /**
     * @see ScriptEventSupport#setOnMouseMoveScript(java.lang.String)
     */
    public void setOnMouseMoveScript(String onMouseMoveScript) {
        checkMutable(true);
        this.onMouseMoveScript = onMouseMoveScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTemplateOptions()
     */
    @BeanTagAttribute(name = "templateOptions", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getTemplateOptions() {
        if (templateOptions == Collections.EMPTY_MAP && isMutable(true)) {
            templateOptions = new LifecycleAwareMap<String, String>(this);
        }
        
        return templateOptions;
    }

    /**
     * @see Component#setTemplateOptions(java.util.Map)
     */
    public void setTemplateOptions(Map<String, String> templateOptions) {
        checkMutable(true);
        if (templateOptions == null) {
            this.templateOptions = Collections.emptyMap();
        } else {
            this.templateOptions = new LifecycleAwareMap<String, String>(this, templateOptions);
        }
    }

    /**
     * Builds a string from the underlying <code>Map</code> of template options that will export
     * that options as a JavaScript Map for use in js and jQuery plugins
     * 
     * @return String of widget options formatted as JS Map.
     */
    @Override
    @BeanTagAttribute(name = "templateOptionsJSString")
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
    @BeanTagAttribute(name = "progressiveRender")
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
    @BeanTagAttribute(name = "conditionalRefresh")
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
    @BeanTagAttribute(name = "progressiveRenderViaAJAX")
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
    @BeanTagAttribute(name = "progressiveRenderAndRefresh")
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
     * @see org.kuali.rice.krad.uif.component.Component#getRefreshWhenChangedPropertyNames()
     */
    @BeanTagAttribute(name = "refreshWhenChangedPropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getRefreshWhenChangedPropertyNames() {
        return this.refreshWhenChangedPropertyNames;
    }

    /**
     * @see 
     *      org.kuali.rice.krad.uif.component.Component#setRefreshWhenChangedPropertyNames(java.util.
     *      List<java.lang.String>)
     */
    public void setRefreshWhenChangedPropertyNames(List<String> refreshWhenChangedPropertyNames) {
        checkMutable(true);
        this.refreshWhenChangedPropertyNames = refreshWhenChangedPropertyNames == null ?
                Collections.<String> emptyList() :
                Collections.<String> unmodifiableList(refreshWhenChangedPropertyNames);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAdditionalComponentsToRefresh()
     */
    @BeanTagAttribute(name = "additionalComponentsToRefresh", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalComponentsToRefresh() {
        return additionalComponentsToRefresh;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setAdditionalComponentsToRefresh(java.util.List<java.lang.String>)
     */
    public void setAdditionalComponentsToRefresh(List<String> additionalComponentsToRefresh) {
        checkMutable(true);
        this.additionalComponentsToRefresh = additionalComponentsToRefresh == null
                ? Collections.<String> emptyList()
                : Collections.<String> unmodifiableList(additionalComponentsToRefresh);
        this.additionalComponentsToRefreshJs = null;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAdditionalComponentsToRefreshJs
     */
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
     * @see org.kuali.rice.krad.uif.component.Component#isRefreshedByAction()
     */
    public boolean isRefreshedByAction() {
        return refreshedByAction;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRefreshedByAction(boolean)
     */
    public void setRefreshedByAction(boolean refreshedByAction) {
        checkMutable(true);
        this.refreshedByAction = refreshedByAction;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isDisclosedByAction()
     */
    public boolean isDisclosedByAction() {
        return disclosedByAction;
    }

    /**
     * @see Component#setDisclosedByAction(boolean)
     */
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
    @BeanTagAttribute(name = "refreshTimer")
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
     * @see org.kuali.rice.krad.uif.component.Component#isResetDataOnRefresh()
     */
    @BeanTagAttribute(name = "resetDataOnRefresh")
    public boolean isResetDataOnRefresh() {
        return resetDataOnRefresh;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setResetDataOnRefresh(boolean)
     */
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
    @BeanTagAttribute(name = "methodToCallOnRefresh")
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
     * @param skipInTabOrder flag
     */
    public void setSkipInTabOrder(boolean skipInTabOrder) {
        checkMutable(true);
        this.skipInTabOrder = skipInTabOrder;
    }

    /**
     * Flag indicating that this component and its nested components must be skipped when keyboard
     * tabbing.
     * 
     * @return the skipInTabOrder flag
     */
    @BeanTagAttribute(name = "skipInTabOrder")
    public boolean isSkipInTabOrder() {
        return skipInTabOrder;
    }

    /**
     * Get the dataAttributes setup for this component - to be written to the html/jQuery data
     * 
     * <p>
     * The attributes that are complex objects (contain {}) they will be written through script. The
     * attritubes that are simple (contain no objects) will be written directly to the html of the
     * component using standard data-. Either way they can be access through .data() call in jQuery
     * </p>
     * 
     * @return map of dataAttributes
     */
    @BeanTagAttribute(name = "dataAttributes", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getDataAttributes() {
        if (dataAttributes == Collections.EMPTY_MAP) {
            dataAttributes = new LifecycleAwareMap<String, String>(this);
        }
        
        return dataAttributes;
    }

    /**
     * DataAttributes that will be written to the html and/or through script to be consumed by
     * jQuery.
     * 
     * @param dataAttributes the data attributes to set for this component
     */
    public void setDataAttributes(Map<String, String> dataAttributes) {
        checkMutable(true);
        if (dataAttributes == null) {
            this.dataAttributes = Collections.emptyMap();
        } else {
            this.dataAttributes = new LifecycleAwareMap<String, String>(this, dataAttributes);
        }
    }

    /**
     * Add a data attribute to the dataAttributes map - to be written to the html/jQuery data.
     * 
     * @param key key of the data attribute
     * @param value value of the data attribute
     */
    public void addDataAttribute(String key, String value) {
        checkMutable(true);
        
        if (dataAttributes == Collections.EMPTY_MAP) {
            dataAttributes = new LifecycleAwareMap<String, String>(this);
        }

        dataAttributes.put(key, value);
    }

    /**
     * Add a data attribute to the dataAttributes map if the given value is non null or the empty
     * string
     * 
     * @param key key for the data attribute entry
     * @param value value for the data attribute
     */
    public void addDataAttributeIfNonEmpty(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            addDataAttribute(key, value);
        }
    }

    /**
     * Returns a string that can be put into a the tag of a component to add all data attributes
     * inline.
     * 
     * @return html string for data attributes for the simple attributes
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

    /**
     * @see Component#getPreRenderContent()
     */
    @BeanTagAttribute(name = "preRenderContent")
    public String getPreRenderContent() {
        return preRenderContent;
    }

    /**
     * @see Component#setPreRenderContent(String)
     */
    public void setPreRenderContent(String preRenderContent) {
        checkMutable(true);
        this.preRenderContent = preRenderContent;
    }

    /**
     * @see Component#getPostRenderContent()
     */
    @BeanTagAttribute(name = "postRenderContent")
    public String getPostRenderContent() {
        return postRenderContent;
    }

    /**
     * @see Component#setPostRenderContent(String)
     */
    public void setPostRenderContent(String postRenderContent) {
        checkMutable(true);
        this.postRenderContent = postRenderContent;
    }

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
     * Set view status to {@link UifConstants.ViewStatus#CACHED} to prevent modification.
     * 
     * @see Copyable#preventModification()
     */
    @Override
    public void preventModification() {
        if (!UifConstants.ViewStatus.CREATED.equals(viewStatus)
                && !UifConstants.ViewStatus.CACHED.equals(viewStatus)) {
            ViewLifecycle.reportIllegalState("View status is " + viewStatus + " prior to caching "
                    + getClass().getName() + " " + getId() + ", expected C or X");
        }
     
        viewStatus = UifConstants.ViewStatus.CACHED;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        ComponentBase componentCopy = ((ComponentBase) component);

        componentCopy.setId(this.id);
        componentCopy.setBaseId(this.baseId);

        // Copy initialized status, but reset to created for others.
        // This allows prototypes to bypass repeating the initialized phase.
        if (UifConstants.ViewStatus.INITIALIZED.equals(viewStatus)) {
            componentCopy.viewStatus = UifConstants.ViewStatus.INITIALIZED;
        } else {
            componentCopy.viewStatus = UifConstants.ViewStatus.CREATED;
        }

        List<String> copyAdditionalComponentsToRefresh = this.getAdditionalComponentsToRefresh();
        if (copyAdditionalComponentsToRefresh != null) {
            componentCopy.setAdditionalComponentsToRefresh(new ArrayList<String>(copyAdditionalComponentsToRefresh));
        }

        if (this.additionalCssClasses != null) {
            componentCopy.setAdditionalCssClasses(new ArrayList<String>(this.additionalCssClasses));
        }

        componentCopy.setAlign(this.align);

        if (this.cellCssClasses != null) {
            componentCopy.setCellCssClasses(new ArrayList<String>(this.cellCssClasses));
        }

        componentCopy.setCellStyle(this.cellStyle);
        componentCopy.setCellWidth(this.cellWidth);
        componentCopy.setColSpan(this.colSpan);
        componentCopy.setConditionalRefresh(this.conditionalRefresh);

        if (this.libraryCssClasses != null) {
            componentCopy.setLibraryCssClasses(new ArrayList<String>(this.libraryCssClasses));
        }

        if (this.cssClasses != null) {
            componentCopy.setCssClasses(new ArrayList<String>(this.cssClasses));
        }

        if (this.dataAttributes != null) {
            componentCopy.setDataAttributes(new HashMap<String, String>(this.dataAttributes));
        }

        componentCopy.setDisableSessionPersistence(this.disableSessionPersistence);
        componentCopy.setDisclosedByAction(this.disclosedByAction);
        componentCopy.setFinalizeMethodToCall(this.finalizeMethodToCall);
        componentCopy.setFinalizeMethodAdditionalArguments(this.finalizeMethodAdditionalArguments);
        componentCopy.setFinalizeMethodInvoker(CloneUtils.deepClone(this.finalizeMethodInvoker));
        componentCopy.setForceSessionPersistence(this.forceSessionPersistence);
        componentCopy.setHidden(this.hidden);
        componentCopy.setMethodToCallOnRefresh(this.methodToCallOnRefresh);
        componentCopy.setOnBlurScript(this.onBlurScript);
        componentCopy.setOnChangeScript(this.onChangeScript);
        componentCopy.setOnClickScript(this.onClickScript);
        componentCopy.setOnCloseScript(this.onCloseScript);
        componentCopy.setOnDblClickScript(this.onDblClickScript);
        componentCopy.setOnDocumentReadyScript(this.onDocumentReadyScript);
        componentCopy.setOnFocusScript(this.onFocusScript);
        componentCopy.setOnKeyDownScript(this.onKeyDownScript);
        componentCopy.setOnKeyPressScript(this.onKeyPressScript);
        componentCopy.setOnKeyUpScript(this.onKeyUpScript);
        componentCopy.setOnLoadScript(this.onLoadScript);
        componentCopy.setOnMouseDownScript(this.onMouseDownScript);
        componentCopy.setOnMouseMoveScript(this.onMouseMoveScript);
        componentCopy.setOnMouseOutScript(this.onMouseOutScript);
        componentCopy.setOnMouseOverScript(this.onMouseOverScript);
        componentCopy.setOnMouseUpScript(this.onMouseUpScript);
        componentCopy.setOnSubmitScript(this.onSubmitScript);
        componentCopy.setOnUnloadScript(this.onUnloadScript);
        componentCopy.setOrder(this.order);
        componentCopy.setPostRenderContent(this.postRenderContent);
        componentCopy.setPreRenderContent(this.preRenderContent);
        componentCopy.setProgressiveRender(this.progressiveRender);
        componentCopy.setProgressiveRenderViaAJAX(this.progressiveRenderViaAJAX);
        componentCopy.setReadOnly(this.readOnly);
        componentCopy.setRefreshedByAction(this.refreshedByAction);
        componentCopy.setRefreshTimer(this.refreshTimer);

        if (this.refreshWhenChangedPropertyNames != null) {
            componentCopy.setRefreshWhenChangedPropertyNames(new ArrayList<String>(
                    this.refreshWhenChangedPropertyNames));
        }

        componentCopy.setRender(this.render);
        componentCopy.setRetrieveViaAjax(this.retrieveViaAjax);
        componentCopy.setRenderedHtmlOutput(this.renderedHtmlOutput);
        componentCopy.setRequired(this.required);
        componentCopy.setResetDataOnRefresh(this.resetDataOnRefresh);
        componentCopy.setRowSpan(this.rowSpan);
        componentCopy.setSelfRendered(this.selfRendered);
        componentCopy.setSkipInTabOrder(this.skipInTabOrder);
        componentCopy.setStyle(this.style);
        componentCopy.setTemplate(this.template);
        componentCopy.setTemplateName(this.templateName);

        if (this.templateOptions != null) {
            componentCopy.setTemplateOptions(new HashMap<String, String>(this.templateOptions));
        }

        componentCopy.setTemplateOptionsJSString(this.templateOptionsJSString);

        componentCopy.setTitle(this.title);
        componentCopy.setValign(this.valign);
        componentCopy.setWidth(this.width);

        if (componentModifiers != null) {
            List<ComponentModifier> componentModifiersCopy = new ArrayList<ComponentModifier>();
            for (ComponentModifier componentModifer : this.componentModifiers) {
                componentModifiersCopy.add((ComponentModifier) componentModifer.copy());
            }

            componentCopy.setComponentModifiers(componentModifiersCopy);
        }

        if (this.componentSecurity != null) {
            componentCopy.setComponentSecurity((ComponentSecurity) this.componentSecurity.copy());
        }

        if (this.toolTip != null) {
            componentCopy.setToolTip((Tooltip) this.toolTip.copy());
        }

        componentCopy.setContext(new HashMap<String, Object>(this.context));

        if (propertyReplacers != null) {
            List<PropertyReplacer> propertyReplacersCopy = new ArrayList<PropertyReplacer>();
            for (PropertyReplacer propertyReplacer : this.propertyReplacers) {
                propertyReplacersCopy.add((PropertyReplacer) propertyReplacer.copy());
            }

            componentCopy.setPropertyReplacers(propertyReplacersCopy);
        }

        componentCopy.setComponentSecurity(getComponentSecurity());
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
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
