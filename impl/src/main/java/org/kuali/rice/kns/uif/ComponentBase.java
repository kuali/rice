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
package org.kuali.rice.kns.uif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.UifConstants.ViewStatus;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.decorator.ComponentDecorator;
import org.kuali.rice.kns.uif.decorator.DecoratorChain;
import org.kuali.rice.kns.uif.modifier.ComponentModifier;

/**
 * Base implementation of <code>Component</code> which other component
 * implementations should extend
 * 
 * <p>
 * Provides base component properties such as id and template. Also provides
 * default implementation for the <code>ScriptEventSupport</code> and
 * <code>Ordered</code> interfaces. By default no script events are supported.
 * </p>
 * 
 * <p>
 * <code>ComponentBase</code> also provides properties for a
 * <code>ComponentDecorator</code> and a List of
 * </code>ComponentInitializers</code>
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ComponentBase implements Component, ScriptEventSupport {
	private static final long serialVersionUID = -4449335748129894350L;

	private String id;
	private String template;
	private String title;

	private boolean render;
	private String conditionalRender;
	private boolean hidden;
	private boolean readOnly;
	private String conditionalReadOnly;
	private Boolean required;

	private String align;
	private String valign;
	private String width;
	private int colSpan;
	private int rowSpan;
	private String style;
	private List<String> styleClasses;

	private int order;

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

	private ComponentDecorator decorator;
	private transient DecoratorChain decoratorChain;

	private List<ComponentModifier> componentModifiers;

	private Map<String, String> componentOptions;

	private transient Map<String, Object> context;

	private List<PropertyReplacer> propertyReplacers;

	public ComponentBase() {
		order = 0;
		colSpan = 1;
		rowSpan = 1;

		render = true;

		styleClasses = new ArrayList<String>();
		componentModifiers = new ArrayList<ComponentModifier>();
		componentOptions = new HashMap<String, String>();
		context = new HashMap<String, Object>();
		propertyReplacers = new ArrayList<PropertyReplacer>();
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	public void performInitialization(View view) {

	}

	/**
	 * The following updates are done here:
	 * 
	 * <ul>
	 * <li>If component is readonly (unconditionally), update state of child
	 * components</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.Component#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	public void performApplyModel(View view, Object model) {
		if (isReadOnly()) {
			for (Component component : getNestedComponents()) {
				if (component != null) {
					component.setReadOnly(true);
				}
			}
		}
	}

	/**
	 * The following finalization is done here:
	 * 
	 * <ul>
	 * <li>If any of the style properties were given, sets the style string on
	 * the style property</li>
	 * <li>Setup the decorator chain (if component has decorators) for rendering
	 * </li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.Component#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	public void performFinalize(View view, Object model) {
		if (!ViewStatus.FINAL.equals(view.getViewStatus())) {
			// add the align, valign, and width settings to style
			if (StringUtils.isNotBlank(getAlign())) {
				setStyle(getStyle() + CssConstants.TEXT_ALIGN + getAlign() + ";");
			}

			if (StringUtils.isNotBlank(getValign())) {
				setStyle(getStyle() + CssConstants.VERTICAL_ALIGN + getValign() + ";");
			}

			if (StringUtils.isNotBlank(getWidth())) {
				setStyle(getStyle() + CssConstants.WIDTH + getWidth() + ";");
			}
		}

		decoratorChain = new DecoratorChain(this);
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = new ArrayList<Component>();

		components.add(decorator);

		return components;
	}

	/**
	 * Set of property names for the component base for which on the property
	 * value reference should be copied. Subclasses can override this but should
	 * include a call to super
	 * 
	 * @see org.kuali.rice.kns.uif.Component#getPropertiesForReferenceCopy()
	 */
	public Set<String> getPropertiesForReferenceCopy() {
		Set<String> refCopyProperties = new HashSet<String>();

		refCopyProperties.add(UifPropertyPaths.COMPONENT_MODIFIERS);
		refCopyProperties.add(UifPropertyPaths.CONTEXT);

		return refCopyProperties;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getId()
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getTemplate()
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setTemplate(java.lang.String)
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getTitle()
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#isHidden()
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setHidden(boolean)
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#isReadOnly()
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Expression language string for conditionally setting the readOnly
	 * property
	 * 
	 * @return String el that should evaluate to boolean
	 */
	public String getConditionalReadOnly() {
		return this.conditionalReadOnly;
	}

	/**
	 * Setter for the conditional readOnly string
	 * 
	 * @param conditionalReadOnly
	 */
	public void setConditionalReadOnly(String conditionalReadOnly) {
		this.conditionalReadOnly = conditionalReadOnly;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getRequired()
	 */
	public Boolean getRequired() {
		return this.required;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setRequired(java.lang.Boolean)
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * Expression language string for conditionally setting the render property
	 * 
	 * @return String el that should evaluate to boolean
	 */
	public String getConditionalRender() {
		return this.conditionalRender;
	}

	/**
	 * Setter for the conditional render string
	 * 
	 * @param conditionalRender
	 */
	public void setConditionalRender(String conditionalRender) {
		this.conditionalRender = conditionalRender;
	}

	/**
	 * Horizontal alignment of the component within its container
	 * 
	 * <p>
	 * All components belong to a <code>Container</code> and are placed using a
	 * <code>LayoutManager</code>. This property specifies how the component
	 * should be aligned horizontally within the container. During the finalize
	 * phase the CSS text-align style will be created for the align setting.
	 * </p>
	 * 
	 * @return String horizontal align
	 * @see org.kuali.rice.kns.uif.CssConstants.TextAligns
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
	 * @see org.kuali.rice.kns.uif.CssConstants.VerticalAligns
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
	 * 
	 * <p>
	 * All components belong to a <code>Container</code> and are placed using a
	 * <code>LayoutManager</code>. This property specifies a width the component
	 * should take up in the Container. This is not applicable for all layout
	 * managers. During the finalize phase the CSS width style will be created
	 * for the width setting.
	 * </p>
	 * 
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
	 * @see org.kuali.rice.kns.uif.Component#getColSpan()
	 */
	public int getColSpan() {
		return this.colSpan;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setColSpan(int)
	 */
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getRowSpan()
	 */
	public int getRowSpan() {
		return this.rowSpan;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setRowSpan(int)
	 */
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getStyle()
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setStyle(java.lang.String)
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getStyleClasses()
	 */
	public List<String> getStyleClasses() {
		return this.styleClasses;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setStyleClasses(java.util.List)
	 */
	public void setStyleClasses(List<String> styleClasses) {
		this.styleClasses = styleClasses;
	}

	/**
	 * Builds the HTML class attribute string by combining the styleClasses list
	 * with a space delimiter
	 * 
	 * @return String class attribute string
	 */
	public String getStyleClassesAsString() {
		if (styleClasses != null) {
			return StringUtils.join(styleClasses, " ");
		}

		return "";
	}

	/**
	 * Sets the styleClasses list from the given string that has the classes
	 * delimited by space. This is a convenience for configuration. If a child
	 * bean needs to inherit the classes from the parent, it should configure as
	 * a list and use merge="true"
	 * 
	 * @param styleClasses
	 */
	public void setStyleClasses(String styleClasses) {
		String[] classes = StringUtils.split(styleClasses);
		this.styleClasses = Arrays.asList(classes);
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#isRender()
	 */
	public boolean isRender() {
		return this.render;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setRender(boolean)
	 */
	public void setRender(boolean render) {
		this.render = render;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getComponentModifiers()
	 */
	public List<ComponentModifier> getComponentModifiers() {
		return this.componentModifiers;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setComponentModifiers(java.util.List)
	 */
	public void setComponentModifiers(List<ComponentModifier> componentModifiers) {
		this.componentModifiers = componentModifiers;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getContext()
	 */
	public Map<String, Object> getContext() {
		return this.context;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setContext(java.util.Map)
	 */
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#pushObjectToContext(java.lang.String,
	 *      java.lang.Object)
	 */
	public void pushObjectToContext(String objectName, Object object) {
		if (this.context == null) {
			this.context = new HashMap<String, Object>();
		}

		this.context.put(objectName, object);
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getPropertyReplacers()
	 */
	public List<PropertyReplacer> getPropertyReplacers() {
		return this.propertyReplacers;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setPropertyReplacers(java.util.List)
	 */
	public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
		this.propertyReplacers = propertyReplacers;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getDecorator()
	 */
	public ComponentDecorator getDecorator() {
		return this.decorator;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#setDecorator(org.kuali.rice.kns.uif.decorator.ComponentDecorator)
	 */
	public void setDecorator(ComponentDecorator decorator) {
		this.decorator = decorator;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getDecoratorChain()
	 */
	public DecoratorChain getDecoratorChain() {
		return this.decoratorChain;
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnLoad()
	 */
	public boolean getSupportsOnLoad() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnLoadScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnDocumentReady()
	 */
	public boolean getSupportsOnDocumentReady() {
		return false;
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnUnload()
	 */
	public boolean getSupportsOnUnload() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnUnloadScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnClose()
	 */
	public boolean getSupportsOnClose() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnCloseScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnBlur()
	 */
	public boolean getSupportsOnBlur() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnBlurScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnChange()
	 */
	public boolean getSupportsOnChange() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnChangeScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnClick()
	 */
	public boolean getSupportsOnClick() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnClickScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnDblClick()
	 */
	public boolean getSupportsOnDblClick() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnDblClickScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnFocus()
	 */
	public boolean getSupportsOnFocus() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnFocusScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnSubmit()
	 */
	public boolean getSupportsOnSubmit() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnSubmitScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnKeyPress()
	 */
	public boolean getSupportsOnKeyPress() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnKeyPressScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnKeyUp()
	 */
	public boolean getSupportsOnKeyUp() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnKeyUpScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnKeyDown()
	 */
	public boolean getSupportsOnKeyDown() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnKeyDownScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnMouseOver()
	 */
	public boolean getSupportsOnMouseOver() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnMouseOverScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnMouseOut()
	 */
	public boolean getSupportsOnMouseOut() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnMouseOutScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnMouseUp()
	 */
	public boolean getSupportsOnMouseUp() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnMouseUpScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnMouseDown()
	 */
	public boolean getSupportsOnMouseDown() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnMouseDownScript()
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
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getSupportsOnMouseMove()
	 */
	public boolean getSupportsOnMouseMove() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getOnMouseMoveScript()
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
	 * @see org.kuali.rice.kns.uif.widget.Widget#getWidgetOptions()
	 */
	public Map<String, String> getComponentOptions() {
		if (componentOptions == null) {
			componentOptions = new HashMap<String, String>();
		}
		return this.componentOptions;
	}

	/**
	 * @see org.kuali.rice.kns.uif.widget.Widget#setWidgetOptions(java.util.Map)
	 */
	public void setComponentOptions(Map<String, String> componentOptions) {
		this.componentOptions = componentOptions;
	}

	/**
	 * Builds a string from the underlying <code>Map</code> of component options
	 * that will export that options as a JavaScript Map for use in js and
	 * jQuery plugins
	 * 
	 * @return String of widget options formatted as JS Map
	 */
	public String getComponentOptionsJSString() {
		if (componentOptions == null) {
			componentOptions = new HashMap<String, String>();
		}
		StringBuffer sb = new StringBuffer();

		sb.append("{");

		for (String optionKey : componentOptions.keySet()) {
			String optionValue = componentOptions.get(optionKey);

			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(optionKey);
			sb.append(":");

			// If an option value starts with { or [, it would be a nested value
			// and it should not use quotes around it
			if (StringUtils.startsWith(optionValue, "{") || StringUtils.startsWith(optionValue, "[")) {
				sb.append(optionValue);
			}
			else {
				sb.append("\"" + optionValue + "\"");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	/**
	 * This method adds a single style to the list of css style classes on this component
	 * 
	 * @param style
	 */
	@Override
	public void addStyleClass(String styleClass){
		if(!styleClasses.contains(styleClass)){
			styleClasses.add(styleClass);
		}
	}

	/**
	 * Script to be run when the document is 'ready'
	 * @return the onDocumentReadyScript
	 */
	public String getOnDocumentReadyScript() {
		return onDocumentReadyScript;
	}
	
	/**
	 * @see org.kuali.rice.kns.uif.Component#appendToStyle(java.lang.String)
	 */
	@Override
	public void appendToStyle(String styleRules) {
		style = style + styleRules; 
	}
}
