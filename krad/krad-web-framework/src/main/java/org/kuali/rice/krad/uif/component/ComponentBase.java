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
package org.kuali.rice.krad.uif.component;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.control.ControlBase;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of <code>Component</code> which other component
 * implementations should extend
 *
 * <p>
 * Provides base component properties such as id and template. Also provides
 * default implementation for the <code>ScriptEventSupport</code> and
 * <code>Ordered</code> interfaces. By default no script events except the
 * onDocumentReady are supported.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ComponentBase extends ConfigurableBase implements Component {
    private static final long serialVersionUID = -4449335748129894350L;

    private String id;
    private String baseId;
    private String template;
    private String templateName;

    private String title;

    private boolean render;

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
    private boolean refreshedByAction;

    private int refreshTimer;

    private boolean resetDataOnRefresh;
    private String methodToCallOnRefresh;

    private boolean hidden;
    private boolean readOnly;
    private Boolean required;

    private String align;
    private String valign;
    private String width;

    private int colSpan;
    private int rowSpan;

    private String style;
    private List<String> cssClasses;

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

    private Map<String, String> templateOptions;
    private String templateOptionsJSString;

    @ReferenceCopy(newCollectionInstance = true)
    private transient Map<String, Object> context;

    private List<PropertyReplacer> propertyReplacers;
    
    private Map<String,String> dataAttributes;

    public ComponentBase() {
        super();

        order = 0;
        colSpan = 1;
        rowSpan = 1;

        render = true;
        selfRendered = false;
        progressiveRenderViaAJAX = false;
        progressiveRenderAndRefresh = false;
        refreshedByAction = false;
        resetDataOnRefresh = false;
        disableSessionPersistence = false;
        forceSessionPersistence = false;

        componentSecurity = ObjectUtils.newInstance(getComponentSecurityClass());

        refreshWhenChangedPropertyNames = new ArrayList<String>();
        finalizeMethodAdditionalArguments = new ArrayList<Object>();
        cssClasses = new ArrayList<String>();
        componentModifiers = new ArrayList<ComponentModifier>();
        templateOptions = new HashMap<String, String>();
        context = new HashMap<String, Object>();
        propertyReplacers = new ArrayList<PropertyReplacer>();
        dataAttributes = new HashMap<String, String>();
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li></li>
     * </ul>
     *
     * @see Component#performInitialization(org.kuali.rice.krad.uif.view.View, java.lang.Object)
     */
    public void performInitialization(View view, Object model) {

    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Evaluate the progressive render condition (if set) and combine with the current render status to set the
     * render status</li>
     * </ul>
     *
     * @see Component#performApplyModel(org.kuali.rice.krad.uif.view.View, java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    public void performApplyModel(View view, Object model, Component parent) {
        if (this.render && StringUtils.isNotEmpty(progressiveRender)) {
            // progressive anded with render, will not render at least one of the two are false
            ExpressionEvaluatorService expressionEvaluatorService =
                    KRADServiceLocatorWeb.getExpressionEvaluatorService();
            String adjustedProgressiveRender = expressionEvaluatorService.replaceBindingPrefixes(view, this,
                    progressiveRender);
            Boolean progRenderEval = (Boolean) expressionEvaluatorService.evaluateExpression(model, context,
                    adjustedProgressiveRender);

            this.setRender(progRenderEval);
        }
    }

    /**
     * The following finalization is done here:
     *
     * <ul>
     * <li>progressiveRender and conditionalRefresh variables are processed if set</li>
     * <li>If any of the style properties were given, sets the style string on
     * the style property</li>
     * <li>Set the skipInTabOrder flag for nested components</li>
     * </ul>
     *
     * @see Component#performFinalize(org.kuali.rice.krad.uif.view.View, java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    public void performFinalize(View view, Object model, Component parent) {
        if (StringUtils.isNotEmpty(progressiveRender)) {
            progressiveRender = KRADServiceLocatorWeb.getExpressionEvaluatorService().replaceBindingPrefixes(view, this,
                    progressiveRender);
            progressiveDisclosureControlNames = new ArrayList<String>();
            progressiveDisclosureConditionJs = ExpressionUtils.parseExpression(progressiveRender,
                    progressiveDisclosureControlNames);
        }

        if (StringUtils.isNotEmpty(conditionalRefresh)) {
            conditionalRefresh = KRADServiceLocatorWeb.getExpressionEvaluatorService().replaceBindingPrefixes(view,
                    this, conditionalRefresh);
            conditionalRefreshControlNames = new ArrayList<String>();
            conditionalRefreshConditionJs = ExpressionUtils.parseExpression(conditionalRefresh,
                    conditionalRefreshControlNames);
        }

        List<String> adjustedRefreshPropertyNames = new ArrayList<String>();
        for (String refreshPropertyName : refreshWhenChangedPropertyNames) {
            adjustedRefreshPropertyNames.add(
                    KRADServiceLocatorWeb.getExpressionEvaluatorService().replaceBindingPrefixes(view, this,
                            refreshPropertyName));
        }
        refreshWhenChangedPropertyNames = adjustedRefreshPropertyNames;

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
        for (Component component : getComponentsForLifecycle()) {
            if (component != null && component instanceof ComponentBase && skipInTabOrder) {
                ((ComponentBase) component).setSkipInTabOrder(skipInTabOrder);
                if (component instanceof ControlBase) {
                    ((ControlBase) component).setTabIndex(-1);
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
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentsForLifecycle()
     */
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        for (ComponentModifier modifier : componentModifiers) {
            components.addAll(modifier.getComponentPrototypes());
        }

        components.addAll(getPropertyReplacerComponents());

        return components;
    }

    /**
     * Returns list of components that are being held in property replacers configured for this component
     *
     * @return List<Component>
     */
    public List<Component> getPropertyReplacerComponents() {
        List<Component> components = new ArrayList<Component>();
        for (Object replacer : propertyReplacers) {
            components.addAll(((PropertyReplacer) replacer).getNestedComponents());
        }

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setId(java.lang.String)
     */
    public void setId(String id) {
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
        this.baseId = baseId;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTemplate()
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTemplate(java.lang.String)
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTitle()
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isHidden()
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setHidden(boolean)
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isReadOnly()
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRequired()
     */
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRequired(java.lang.Boolean)
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isRender()
     */
    public boolean isRender() {
        return this.render;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRender(boolean)
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getColSpan()
     */
    public int getColSpan() {
        return this.colSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setColSpan(int)
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRowSpan()
     */
    public int getRowSpan() {
        return this.rowSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setRowSpan(int)
     */
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    /**
     * Horizontal alignment of the component within its container
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned horizontally within the container. During the finalize
     * phase the CSS text-align style will be created for the align setting.
     * </p>
     *
     * @return String horizontal align
     * @see org.kuali.rice.krad.uif.CssConstants.TextAligns
     */
    public String getAlign() {
        return this.align;
    }

    /**
     * Sets the components horizontal alignment
     *
     * @param align
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * Vertical alignment of the component within its container
     *
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned vertically within the container. During the finalize
     * phase the CSS vertical-align style will be created for the valign
     * setting.
     * </p>
     *
     * @return String vertical align
     * @see org.kuali.rice.krad.uif.CssConstants.VerticalAligns
     */
    public String getValign() {
        return this.valign;
    }

    /**
     * Setter for the component's vertical align
     *
     * @param valign
     */
    public void setValign(String valign) {
        this.valign = valign;
    }

    /**
     * Width the component should take up in the container
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies a width the component
     * should take up in the Container. This is not applicable for all layout
     * managers. During the finalize phase the CSS width style will be created
     * for the width setting.
     * </p>
     * <p>
     * e.g. '30%', '55px'
     * </p>
     *
     * @return String width string
     */
    public String getWidth() {
        return this.width;
    }

    /**
     * Setter for the components width
     *
     * @param width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getStyle()
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setStyle(java.lang.String)
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCssClasses()
     */
    public List<String> getCssClasses() {
        return this.cssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setCssClasses(java.util.List)
     */
    public void setCssClasses(List<String> cssClasses) {
        this.cssClasses = cssClasses;
    }

    /**
     * Builds the HTML class attribute string by combining the styleClasses list
     * with a space delimiter
     *
     * @return String class attribute string
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
        if (!cssClasses.contains(styleClass)) {
            cssClasses.add(styleClass);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#appendToStyle(java.lang.String)
     */
    public void appendToStyle(String styleRules) {
        if (style == null) {
            style = "";
        }
        style = style + styleRules;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodToCall()
     */
    public String getFinalizeMethodToCall() {
        return this.finalizeMethodToCall;
    }

    /**
     * Setter for the finalize method
     *
     * @param finalizeMethodToCall
     */
    public void setFinalizeMethodToCall(String finalizeMethodToCall) {
        this.finalizeMethodToCall = finalizeMethodToCall;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodAdditionalArguments()
     */
    public List<Object> getFinalizeMethodAdditionalArguments() {
        return finalizeMethodAdditionalArguments;
    }

    /**
     * Setter for the finalize additional arguments list
     *
     * @param finalizeMethodAdditionalArguments
     */
    public void setFinalizeMethodAdditionalArguments(List<Object> finalizeMethodAdditionalArguments) {
        this.finalizeMethodAdditionalArguments = finalizeMethodAdditionalArguments;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getFinalizeMethodInvoker()
     */
    public MethodInvokerConfig getFinalizeMethodInvoker() {
        return this.finalizeMethodInvoker;
    }

    /**
     * Setter for the method invoker instance
     *
     * @param finalizeMethodInvoker
     */
    public void setFinalizeMethodInvoker(MethodInvokerConfig finalizeMethodInvoker) {
        this.finalizeMethodInvoker = finalizeMethodInvoker;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isSelfRendered()
     */
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
     * @see Component#isDisableSessionPersistence()
     */
    public boolean isDisableSessionPersistence() {
        return disableSessionPersistence;
    }

    /**
     * @see Component#setDisableSessionPersistence(boolean)
     */
    public void setDisableSessionPersistence(boolean disableSessionPersistence) {
        this.disableSessionPersistence = disableSessionPersistence;
    }

    /**
     * @see Component#isForceSessionPersistence()
     */
    public boolean isForceSessionPersistence() {
        return forceSessionPersistence;
    }

    /**
     * @see Component#setForceSessionPersistence(boolean)
     */
    public void setForceSessionPersistence(boolean forceSessionPersistence) {
        this.forceSessionPersistence = forceSessionPersistence;
    }

    /**
     * @see Component#getComponentSecurity()
     */
    public ComponentSecurity getComponentSecurity() {
        return componentSecurity;
    }

    /**
     * @see Component#setComponentSecurity(org.kuali.rice.krad.uif.component.ComponentSecurity)
     */
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        this.componentSecurity = componentSecurity;
    }

    /**
     * Returns the security class that is associated with the component (used for initialization and validation)
     *
     * @return Class<? extends ComponentSecurity>
     */
    protected Class<? extends ComponentSecurity> getComponentSecurityClass() {
        return ComponentSecurity.class;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentModifiers()
     */
    public List<ComponentModifier> getComponentModifiers() {
        return this.componentModifiers;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setComponentModifiers(java.util.List)
     */
    public void setComponentModifiers(List<ComponentModifier> componentModifiers) {
        this.componentModifiers = componentModifiers;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getContext()
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setContext(java.util.Map)
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#pushObjectToContext(java.lang.String,
     *      java.lang.Object)
     */
    public void pushObjectToContext(String objectName, Object object) {
        if (this.context == null) {
            this.context = new HashMap<String, Object>();
        }
        pushToPropertyReplacerContext(objectName, object);
        this.context.put(objectName, object);
    }

    /*
    * Adds the object to the context of the components in the
    * PropertyReplacer object. Only checks for a list, map or component.
    */
    protected void pushToPropertyReplacerContext(String objectName, Object object) {
        for (Component replacerComponent : getPropertyReplacerComponents()) {
            replacerComponent.pushObjectToContext(objectName, object);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#pushAllToContext
     */
    public void pushAllToContext(Map<String, Object> objects) {
        if (objects != null) {
            for (Map.Entry<String, Object> objectEntry : objects.entrySet()) {
                pushObjectToContext(objectEntry.getKey(), objectEntry.getValue());
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getPropertyReplacers()
     */
    public List<PropertyReplacer> getPropertyReplacers() {
        return this.propertyReplacers;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setPropertyReplacers(java.util.List)
     */
    public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
        this.propertyReplacers = propertyReplacers;
    }

    /**
     * @see org.springframework.core.Ordered#getOrder()
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Setter for the component's order
     *
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getToolTip()
     */
    public Tooltip getToolTip() {
        return toolTip;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setToolTip(Tooltip)
     */
    public void setToolTip(Tooltip toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnLoadScript()
     */
    public String getOnLoadScript() {
        return onLoadScript;
    }

    /**
     * Setter for the components onLoad script
     *
     * @param onLoadScript
     */
    public void setOnLoadScript(String onLoadScript) {
        this.onLoadScript = onLoadScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnDocumentReadyScript()
     */
    public String getOnDocumentReadyScript() {
        String onDocScript =  this.onDocumentReadyScript;
        // if the refreshTimer property has been set then pre-append the call to refreshComponetUsingTimer tp the onDocumentReadyScript.
        // if the refreshTimer property is set then the methodToCallOnRefresh should also be set.
        if(refreshTimer > 0) {
            onDocScript = (null == onDocScript) ? "" : onDocScript;
            onDocScript = "refreshComponentUsingTimer('"+ this.id +"','" + this.methodToCallOnRefresh + "'," + refreshTimer +");" + onDocScript;
        }
        return onDocScript;
    }

    /**
     * Setter for the components onDocumentReady script
     *
     * @param onDocumentReadyScript
     */
    public void setOnDocumentReadyScript(String onDocumentReadyScript) {
        this.onDocumentReadyScript = onDocumentReadyScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnUnloadScript()
     */
    public String getOnUnloadScript() {
        return onUnloadScript;
    }

    /**
     * Setter for the components onUnload script
     *
     * @param onUnloadScript
     */
    public void setOnUnloadScript(String onUnloadScript) {
        this.onUnloadScript = onUnloadScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnCloseScript()
     */
    public String getOnCloseScript() {
        return onCloseScript;
    }

    /**
     * Setter for the components onClose script
     *
     * @param onCloseScript
     */
    public void setOnCloseScript(String onCloseScript) {
        this.onCloseScript = onCloseScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnBlurScript()
     */
    public String getOnBlurScript() {
        return onBlurScript;
    }

    /**
     * Setter for the components onBlur script
     *
     * @param onBlurScript
     */
    public void setOnBlurScript(String onBlurScript) {
        this.onBlurScript = onBlurScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnChangeScript()
     */
    public String getOnChangeScript() {
        return onChangeScript;
    }

    /**
     * Setter for the components onChange script
     *
     * @param onChangeScript
     */
    public void setOnChangeScript(String onChangeScript) {
        this.onChangeScript = onChangeScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnClickScript()
     */
    public String getOnClickScript() {
        return onClickScript;
    }

    /**
     * Setter for the components onClick script
     *
     * @param onClickScript
     */
    public void setOnClickScript(String onClickScript) {
        this.onClickScript = onClickScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnDblClickScript()
     */
    public String getOnDblClickScript() {
        return onDblClickScript;
    }

    /**
     * Setter for the components onDblClick script
     *
     * @param onDblClickScript
     */
    public void setOnDblClickScript(String onDblClickScript) {
        this.onDblClickScript = onDblClickScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnFocusScript()
     */
    public String getOnFocusScript() {
        return onFocusScript;
    }

    /**
     * Setter for the components onFocus script
     *
     * @param onFocusScript
     */
    public void setOnFocusScript(String onFocusScript) {
        this.onFocusScript = onFocusScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnSubmitScript()
     */
    public String getOnSubmitScript() {
        return onSubmitScript;
    }

    /**
     * Setter for the components onSubmit script
     *
     * @param onSubmitScript
     */
    public void setOnSubmitScript(String onSubmitScript) {
        this.onSubmitScript = onSubmitScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyPressScript()
     */
    public String getOnKeyPressScript() {
        return onKeyPressScript;
    }

    /**
     * Setter for the components onKeyPress script
     *
     * @param onKeyPressScript
     */
    public void setOnKeyPressScript(String onKeyPressScript) {
        this.onKeyPressScript = onKeyPressScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyUpScript()
     */
    public String getOnKeyUpScript() {
        return onKeyUpScript;
    }

    /**
     * Setter for the components onKeyUp script
     *
     * @param onKeyUpScript
     */
    public void setOnKeyUpScript(String onKeyUpScript) {
        this.onKeyUpScript = onKeyUpScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnKeyDownScript()
     */
    public String getOnKeyDownScript() {
        return onKeyDownScript;
    }

    /**
     * Setter for the components onKeyDown script
     *
     * @param onKeyDownScript
     */
    public void setOnKeyDownScript(String onKeyDownScript) {
        this.onKeyDownScript = onKeyDownScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseOverScript()
     */
    public String getOnMouseOverScript() {
        return onMouseOverScript;
    }

    /**
     * Setter for the components onMouseOver script
     *
     * @param onMouseOverScript
     */
    public void setOnMouseOverScript(String onMouseOverScript) {
        this.onMouseOverScript = onMouseOverScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseOutScript()
     */
    public String getOnMouseOutScript() {
        return onMouseOutScript;
    }

    /**
     * Setter for the components onMouseOut script
     *
     * @param onMouseOutScript
     */
    public void setOnMouseOutScript(String onMouseOutScript) {
        this.onMouseOutScript = onMouseOutScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseUpScript()
     */
    public String getOnMouseUpScript() {
        return onMouseUpScript;
    }

    /**
     * Setter for the components onMouseUp script
     *
     * @param onMouseUpScript
     */
    public void setOnMouseUpScript(String onMouseUpScript) {
        this.onMouseUpScript = onMouseUpScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseDownScript()
     */
    public String getOnMouseDownScript() {
        return onMouseDownScript;
    }

    /**
     * Setter for the components onMouseDown script
     *
     * @param onMouseDownScript
     */
    public void setOnMouseDownScript(String onMouseDownScript) {
        this.onMouseDownScript = onMouseDownScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnMouseMoveScript()
     */
    public String getOnMouseMoveScript() {
        return onMouseMoveScript;
    }

    /**
     * Setter for the components onMouseMove script
     *
     * @param onMouseMoveScript
     */
    public void setOnMouseMoveScript(String onMouseMoveScript) {
        this.onMouseMoveScript = onMouseMoveScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getTemplateOptions()
     * @return
     */
    public Map<String, String> getTemplateOptions() {
        if (templateOptions == null) {
            templateOptions = new HashMap<String, String>();
        }
        return this.templateOptions;
    }

    /**
     * @see Component#setTemplateOptions(java.util.Map)
     * @param templateOptions
     */
    public void setTemplateOptions(Map<String, String> templateOptions) {
        this.templateOptions = templateOptions;
    }

    /**
     * Builds a string from the underlying <code>Map</code> of template options
     * that will export that options as a JavaScript Map for use in js and
     * jQuery plugins
     *
     * @return String of widget options formatted as JS Map
     */
    @Override
    public String getTemplateOptionsJSString() {
        if (templateOptionsJSString != null) {
            return templateOptionsJSString;
        }

        if (templateOptions == null) {
            templateOptions = new HashMap<String, String>();
        }
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (String optionKey : templateOptions.keySet()) {
            String optionValue = templateOptions.get(optionKey);

            if (sb.length() > 1) {
                sb.append(",");
            }

            sb.append(optionKey);
            sb.append(":");

            sb.append(ScriptUtils.convertToJsValue(optionValue));
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public void setTemplateOptionsJSString(String templateOptionsJSString) {
        this.templateOptionsJSString = templateOptionsJSString;
    }

    /**
     * When set if the condition is satisfied, the component will be displayed. The component MUST BE a
     * container or field type. progressiveRender is defined in a limited Spring EL syntax. Only valid
     * form property names, and, or, logical comparison operators (non-arithmetic), and the matches
     * clause are allowed. String and regex values must use single quotes ('), booleans must be either true or false,
     * numbers must be a valid double, either negative or positive.
     *
     * <p>
     * DO NOT use progressiveRender and a conditional refresh statement on the same component
     * unless it is known that the component will always be visible in all cases when a conditional refresh happens
     * (ie conditional refresh has progressiveRender's condition anded with its own condition).
     * </p>
     *
     * <p>
     * <b>If a component should be refreshed every time it is shown, use the progressiveRenderAndRefresh option
     * with this property instead.</b>
     * </p>
     *
     * @return String progressiveRender expression
     */
    public String getProgressiveRender() {
        return this.progressiveRender;
    }

    /**
     * @param progressiveRender the progressiveRender to set
     */
    public void setProgressiveRender(String progressiveRender) {
        this.progressiveRender = progressiveRender;
    }

    /**
     * When set if the condition is satisfied, the component will be refreshed.
     *
     * <p>The component MUST BE a container or field type. conditionalRefresh is
     * defined in a limited Spring EL syntax. Only valid form property names,
     * and, or, logical comparison operators (non-arithmetic), and the matches
     * clause are allowed. String and regex values must use single quotes ('),
     * booleans must be either true or false, numbers must be a valid double
     * either negative or positive.
     *
     * <p>DO NOT use progressiveRender and conditionalRefresh on the same component
     * unless it is known that the component will always be visible in all cases
     * when a conditionalRefresh happens (ie conditionalRefresh has
     * progressiveRender's condition anded with its own condition). <b>If a
     * component should be refreshed every time it is shown, use the
     * progressiveRenderAndRefresh option with this property instead.</b></p>
     *
     * @return the conditionalRefresh
     */
    public String getConditionalRefresh() {
        return this.conditionalRefresh;
    }

    /**
     * Set the conditional refresh condition
     *
     * @param conditionalRefresh the conditionalRefresh to set
     */
    public void setConditionalRefresh(String conditionalRefresh) {
        this.conditionalRefresh = conditionalRefresh;
    }

    /**
     * Control names used to control progressive disclosure, set internally
     * cannot be set.
     *
     * @return the progressiveDisclosureControlNames
     */
    public List<String> getProgressiveDisclosureControlNames() {
        return this.progressiveDisclosureControlNames;
    }

    /**
     * The condition to show this component progressively converted to a js
     * expression, set internally cannot be set.
     *
     * @return the progressiveDisclosureConditionJs
     */
    public String getProgressiveDisclosureConditionJs() {
        return this.progressiveDisclosureConditionJs;
    }

    /**
     * The condition to refresh this component converted to a js expression, set
     * internally cannot be set.
     *
     * @return the conditionalRefreshConditionJs
     */
    public String getConditionalRefreshConditionJs() {
        return this.conditionalRefreshConditionJs;
    }

    /**
     * Control names used to control conditional refresh, set internally cannot
     * be set.
     *
     * @return the conditionalRefreshControlNames
     */
    public List<String> getConditionalRefreshControlNames() {
        return this.conditionalRefreshControlNames;
    }

    /**
     * When progressiveRenderViaAJAX is true, this component will be retrieved
     * from the server when it first satisfies its progressive render condition.
     *
     * <p>After the first retrieval, it is hidden/shown in the html by the js when
     * its progressive condition result changes. <b>By default, this is false,
     * so components with progressive render capabilities will always be already
     * within the client html and toggled to be hidden or visible.</b></p>
     *
     * @return the progressiveRenderViaAJAX
     */
    public boolean isProgressiveRenderViaAJAX() {
        return this.progressiveRenderViaAJAX;
    }

    /**
     * @param progressiveRenderViaAJAX the progressiveRenderViaAJAX to set
     */
    public void setProgressiveRenderViaAJAX(boolean progressiveRenderViaAJAX) {
        this.progressiveRenderViaAJAX = progressiveRenderViaAJAX;
    }

    /**
     * If true, when the progressiveRender condition is satisfied, the component
     * will always be retrieved from the server and shown(as opposed to being
     * stored on the client, but hidden, after the first retrieval as is the
     * case with the progressiveRenderViaAJAX option).
     *
     * <p><b>By default, this is
     * false, so components with progressive render capabilities will always be
     * already within the client html and toggled to be hidden or visible.</b></p>
     *
     * @return the progressiveRenderAndRefresh
     */
    public boolean isProgressiveRenderAndRefresh() {
        return this.progressiveRenderAndRefresh;
    }

    /**
     * Set the progressive render and refresh option.
     *
     * @param progressiveRenderAndRefresh the progressiveRenderAndRefresh to set
     */
    public void setProgressiveRenderAndRefresh(boolean progressiveRenderAndRefresh) {
        this.progressiveRenderAndRefresh = progressiveRenderAndRefresh;
    }

    /**
     * @see Component#getRefreshWhenChangedPropertyNames()
     */
    public List<String> getRefreshWhenChangedPropertyNames() {
        return this.refreshWhenChangedPropertyNames;
    }

    /**
     * @see Component#setRefreshWhenChangedPropertyNames(java.util.List<java.lang.String>)
     */
    public void setRefreshWhenChangedPropertyNames(List<String> refreshWhenChangedPropertyNames) {
        this.refreshWhenChangedPropertyNames = refreshWhenChangedPropertyNames;
    }

    /**
     * @see Component#isRefreshedByAction()
     */
    public boolean isRefreshedByAction() {
        return refreshedByAction;
    }

    /**
     * @see Component#setRefreshedByAction(boolean)
     */
    public void setRefreshedByAction(boolean refreshedByAction) {
        this.refreshedByAction = refreshedByAction;
    }

    /**
     * Time in seconds that the component will be automatically refreshed
     * <p>
     *     This will invoke the refresh process just like the conditionalRefresh and refreshWhenChangedPropertyNames.
     *     When using this property methodToCallOnRefresh and id should also be specified
     * </p>
     * @return  refreshTimer
     */
    public int getRefreshTimer() {
        return refreshTimer;
    }

    /**
     * Setter for refreshTimer
     *
     * @param refreshTimer
     */
    public void setRefreshTimer(int refreshTimer) {
        this.refreshTimer = refreshTimer;
    }

    /**
     * @see Component#isResetDataOnRefresh()
     */
    public boolean isResetDataOnRefresh() {
        return resetDataOnRefresh;
    }

    /**
     * @see Component#setResetDataOnRefresh(boolean)
     */
    public void setResetDataOnRefresh(boolean resetDataOnRefresh) {
        this.resetDataOnRefresh = resetDataOnRefresh;
    }

    /**
     * Name of a method on the controller that should be invoked as part of the component refresh and disclosure process
     *
     * <p>
     * During the component refresh or disclosure process it might be necessary to perform other operations, such as
     * preparing data or executing a business process. This allows the configuration of a method on the underlying
     * controller that should be called for the component refresh action. In this method, the necessary logic can be
     * performed and then the base component update method invoked to carry out the component refresh.
     * </p>
     *
     * <p>
     * Controller method to invoke must accept the form, binding result, request, and response arguments
     * </p>
     *
     * @return String valid controller method name
     */
    public String getMethodToCallOnRefresh() {
        return methodToCallOnRefresh;
    }

    /**
     * Setter for the controller method to call for a refresh or disclosure action on this component
     *
     * @param methodToCallOnRefresh
     */
    public void setMethodToCallOnRefresh(String methodToCallOnRefresh) {
        this.methodToCallOnRefresh = methodToCallOnRefresh;
    }

    /**
     * @param skipInTabOrder flag
     */
    public void setSkipInTabOrder(boolean skipInTabOrder) {
        this.skipInTabOrder = skipInTabOrder;
    }

    /**
     * Flag indicating that this component and its nested components must be
     * skipped when keyboard tabbing.
     *
     * @return the skipInTabOrder flag
     */
    public boolean isSkipInTabOrder() {
        return skipInTabOrder;
    }



    /**
     * Get the dataAttributes setup for this component - to be written to the html/jQuery data
     *
     * <p>The attributes that are complex objects (contain {}) they will be written through script.
     * The attritubes that are simple (contain no objects) will be written directly to the html of the
     * component using standard data-.
     * Either way they can be access through .data() call in jQuery</p>
     *
     * @return map of dataAttributes
     */
    public Map<String, String> getDataAttributes() {
        return dataAttributes;
    }

    /**
     * DataAttributes that will be written to the html and/or through script to be consumed by jQuery.
     *
     * @param dataAttributes the data attributes to set for this component
     */
    public void setDataAttributes(Map<String, String> dataAttributes) {
        this.dataAttributes = dataAttributes;
    }

    /**
     * Add a data attribute to the dataAttributes map - to be written to the html/jQuery data.
     *
     * @param key key of the data attribute
     * @param value value of the data attribute
     */
    public void addDataAttribute(String key, String value){
        dataAttributes.put(key,value);    
    }
    
    /**
     * Add a data attribute to the dataAttributes map if the given value is non null
     * or the empty string
     *
     * @param key - key for the data attribute entry
     * @param value - value for the data attribute
     */
    public void addDataAttributeIfNonEmpty(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            addDataAttribute(key, value);
        }
    }

    /**
     * Returns js that will add data to this component by the element which matches its id.
     * This will return script for only the complex data elements (containing {});
     *
     * @return jQuery data script for adding complex data attributes
     */
    public String getComplexDataAttributesJs(){
        String js = "";
        if (getDataAttributes() == null) {
            return js;
        } else {
            for(Map.Entry<String,String> data: getDataAttributes().entrySet()){
                if(data.getValue().trim().startsWith("{") && data.getValue().trim().endsWith("}")){
                    js = js + "jQuery('#" + this.getId() + "').data('" + data.getKey()
                            +"', " + data.getValue() +");";
                }
            }
            return js;
        }
    }

    /**
     * Returns a string that can be put into a the tag of a component to add data attributes inline.
     * This does not include the complex attributes which contain {}
     *
     * @return html string for data attributes for the simple attributes
     */
    public String getSimpleDataAttributes(){
        String attributes = "";
        if (getDataAttributes() == null) {
            return attributes;
        } else {
            for(Map.Entry<String,String> data: getDataAttributes().entrySet()){
                if(!data.getValue().trim().startsWith("{")){
                    attributes = attributes + " " + "data-" + data.getKey() + "=\"" + data.getValue() + "\"";
                }
            }
            return attributes;
        }
    }


    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAllDataAttributesJs()
     */
    @Override
    public String getAllDataAttributesJs() {
        String js = "";
        if (getDataAttributes() == null) {
            return js;
        } else {
            for(Map.Entry<String,String> data: getDataAttributes().entrySet()){
                js = js + "jQuery('#" + this.getId() + "').data('" + data.getKey()
                        +"', " + ScriptUtils.convertToJsValue(data.getValue()) +");";
            }
            return js;
        }
    }

}
