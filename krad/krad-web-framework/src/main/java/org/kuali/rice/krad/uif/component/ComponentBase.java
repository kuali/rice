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
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
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
@BeanTag(name = "componentBase", parent = "Uif-ComponentBase")
public abstract class ComponentBase extends UifDictionaryBeanBase implements Component {
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

    private Map<String, String> dataAttributes;

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
        additionalComponentsToRefresh = new ArrayList<String>();
        finalizeMethodAdditionalArguments = new ArrayList<Object>();
        cellCssClasses = new ArrayList<String>();
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
     * @see Component#performApplyModel(org.kuali.rice.krad.uif.view.View, java.lang.Object,
     *      org.kuali.rice.krad.uif.component.Component)
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
     * @see Component#performFinalize(org.kuali.rice.krad.uif.view.View, java.lang.Object,
     *      org.kuali.rice.krad.uif.component.Component)
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

        components.add(toolTip);

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
    @BeanTagAttribute(name = "id")
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
    @BeanTagAttribute(name = "template")
    public String getTemplate() {
        return this.template;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#setTemplate(java.lang.String)
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    @BeanTagAttribute(name = "templateName")
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
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
        this.render = render;
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
        this.rowSpan = rowSpan;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCellCssClasses()
     */
    public List<String> getCellCssClasses() {
        return cellCssClasses;
    }

    /**
     * @see Component#setCellCssClasses(java.util.List)
     */
    public void setCellCssClasses(List<String> cellCssClasses) {
        this.cellCssClasses = cellCssClasses;
    }

    /**
     * @see Component#addCellCssClass(String)
     */
    public void addCellCssClass(String cssClass) {
        if (this.cellCssClasses == null){
            this.cellCssClasses = new ArrayList<String>();
        }

        if(cssClass != null){
            this.cellCssClasses.add(cssClass);
        }
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list
     * with a space delimiter
     *
     * @return String class attribute string
     */
    public String getCellStyleClassesAsString() {
        if (cellCssClasses != null) {
            return StringUtils.join(cellCssClasses, " ");
        }

        return "";
    }

    /**
     * @see Component#getCellStyle()
     */
    public String getCellStyle() {
        return cellStyle;
    }

    /**
     * @see Component#setCellStyle(java.lang.String)
     */
    public void setCellStyle(String cellStyle) {
        this.cellStyle = cellStyle;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCellWidth()
     */
    public String getCellWidth() {
        return cellWidth;
    }

    /**
     * @see Component#setCellWidth(java.lang.String)
     */
    public void setCellWidth(String cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * @see Component#getAlign()
     */
    @BeanTagAttribute(name = "align")
    public String getAlign() {
        return this.align;
    }

    /**
     * @see Component#setAlign(java.lang.String)
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * @see Component#getValign()
     */
    @BeanTagAttribute(name = "valign")
    public String getValign() {
        return this.valign;
    }

    /**
     * @see Component#setValign(java.lang.String)
     */
    public void setValign(String valign) {
        this.valign = valign;
    }

    /**
     * @see Component#getWidth()
     */
    @BeanTagAttribute(name = "width")
    public String getWidth() {
        return this.width;
    }

    /**
     * @see Component#setWidth(java.lang.String)
     */
    public void setWidth(String width) {
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
        this.style = style;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCssClasses()
     */
    @BeanTagAttribute(name = "cssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
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
     * @see Component#isDisableSessionPersistence()
     */
    @BeanTagAttribute(name = "disableSessionPersistence")
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
    @BeanTagAttribute(name = "forceSessionPersistence")
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
    @BeanTagAttribute(name = "componentSecurity", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
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
    @BeanTagAttribute(name = "componentModifiers", type = BeanTagAttribute.AttributeType.LISTBEAN)
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
    @BeanTagAttribute(name = "context", type = BeanTagAttribute.AttributeType.MAPBEAN)
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
    @BeanTagAttribute(name = "propertyReplacers", type = BeanTagAttribute.AttributeType.LISTBEAN)
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
        this.toolTip = toolTip;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getOnLoadScript()
     */
    @BeanTagAttribute(name = "onLoadScript")
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
    @BeanTagAttribute(name = "onDocumentReadyScript")
    public String getOnDocumentReadyScript() {
        String onDocScript = this.onDocumentReadyScript;
        // if the refreshTimer property has been set then pre-append the call to refreshComponetUsingTimer to the onDocumentReadyScript.
        // if the refreshTimer property is set then the methodToCallOnRefresh should also be set.
        if (refreshTimer > 0) {
            onDocScript = (null == onDocScript) ? "" : onDocScript;
            onDocScript = "refreshComponentUsingTimer('"
                    + this.id
                    + "','"
                    + this.methodToCallOnRefresh
                    + "',"
                    + refreshTimer
                    + ");"
                    + onDocScript;
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
    @BeanTagAttribute(name = "onUnloadScript")
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
    @BeanTagAttribute(name = "onCloseScript")
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
    @BeanTagAttribute(name = "onBlurScript")
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
    @BeanTagAttribute(name = "onChangeScript")
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
    @BeanTagAttribute(name = "onClickScript")
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
    @BeanTagAttribute(name = "onDblClickScript")
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
    @BeanTagAttribute(name = "onFocusScript")
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
    @BeanTagAttribute(name = "onSubmitScript")
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
    @BeanTagAttribute(name = "onKeyPressScript")
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
    @BeanTagAttribute(name = "onKeyUpScript")
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
    @BeanTagAttribute(name = "onKeyDownScript")
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
    @BeanTagAttribute(name = "onMouseOverScript")
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
    @BeanTagAttribute(name = "onMouseOutScript")
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
    @BeanTagAttribute(name = "onMouseUpScript")
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
    @BeanTagAttribute(name = "onMouseDownScript")
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
    @BeanTagAttribute(name = "onMouseMoveScript")
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
     */
    @BeanTagAttribute(name = "templateOptions", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getTemplateOptions() {
        if (templateOptions == null) {
            templateOptions = new HashMap<String, String>();
        }
        return this.templateOptions;
    }

    /**
     * @see Component#setTemplateOptions(java.util.Map)
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
    @BeanTagAttribute(name = "templateOptionsJSString")
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
    @BeanTagAttribute(name = "progressiveRender")
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
    @BeanTagAttribute(name = "progressiveRenderViaAJAX")
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
    @BeanTagAttribute(name = "progressiveRenderAndRefresh")
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
    @BeanTagAttribute(name = "refreshWhenChangedPropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
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
     * @see Component#getAdditionalComponentsToRefresh()
     */
    @BeanTagAttribute(name = "additionalComponentsToRefresh", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalComponentsToRefresh() {
        return additionalComponentsToRefresh;
    }

    /**
     * @see Component#setAdditionalComponentsToRefresh(java.util.List<java.lang.String>)
     */
    public void setAdditionalComponentsToRefresh(List<String> additionalComponentsToRefresh) {
        this.additionalComponentsToRefresh = additionalComponentsToRefresh;
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
     * @see org.kuali.rice.krad.uif.component.Component#isDisclosedByAction()
     */
    public boolean isDisclosedByAction() {
        return disclosedByAction;
    }

    /**
     * @see Component#setDisclosedByAction(boolean)
     */
    public void setDisclosedByAction(boolean disclosedByAction) {
        this.disclosedByAction = disclosedByAction;
    }

    /**
     * Time in seconds that the component will be automatically refreshed
     *
     * <p>
     * This will invoke the refresh process just like the conditionalRefresh and refreshWhenChangedPropertyNames.
     * When using this property methodToCallOnRefresh and id should also be specified
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
        this.refreshTimer = refreshTimer;
    }

    /**
     * @see Component#isResetDataOnRefresh()
     */
    @BeanTagAttribute(name = "resetDataOnRefresh")
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
     * Name of a method on the controller that should be invoked as part of the component refresh and disclosure
     * process
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
    @BeanTagAttribute(name = "skipInTabOrder")
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
    @BeanTagAttribute(name = "dataAttributes", type = BeanTagAttribute.AttributeType.MAPVALUE)
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
    public void addDataAttribute(String key, String value) {
        dataAttributes.put(key, value);
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
    public String getComplexDataAttributesJs() {
        String js = "";
        if (getDataAttributes() == null) {
            return js;
        } else {
            for (Map.Entry<String, String> data : getDataAttributes().entrySet()) {
                if (data != null && data.getValue() != null &&
                        data.getValue().trim().startsWith("{") && data.getValue().trim().endsWith("}")) {
                    js = js + "jQuery('#" + this.getId() + "').data('" + data.getKey() + "', " + data.getValue() + ");";
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
    public String getSimpleDataAttributes() {
        String attributes = "";
        if (getDataAttributes() == null) {
            return attributes;
        } else {
            for (Map.Entry<String, String> data : getDataAttributes().entrySet()) {
                if (data != null && data.getValue() != null && !data.getValue().trim().startsWith("{")) {
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
            for (Map.Entry<String, String> data : getDataAttributes().entrySet()) {
                js = js + "jQuery('#" + this.getId() + "').data('" + data.getKey() + "', " + ScriptUtils
                        .convertToJsValue(data.getValue()) + ");";
            }
            return js;
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAdditionalComponentsToRefreshJs
     */
    public String getAdditionalComponentsToRefreshJs() {
        if (!(this.getAdditionalComponentsToRefresh().isEmpty())) {
            additionalComponentsToRefreshJs = ScriptUtils.convertStringListToJsArray(
                    this.getAdditionalComponentsToRefresh());
        }

        return additionalComponentsToRefreshJs;
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
            tracer.createWarning("DO NOT use progressiveRender and conditionalRefresh on the same component unless "
                    + "it is known that the component will always be visible in all cases when a conditionalRefresh "
                    + "happens (ie conditionalRefresh has progressiveRender's condition and with its own condition). "
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
            ;
        }
    }

}
