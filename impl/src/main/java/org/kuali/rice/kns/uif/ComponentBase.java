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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.decorator.ComponentDecorator;
import org.kuali.rice.kns.uif.decorator.DecoratorChain;
import org.kuali.rice.kns.uif.initializer.ComponentInitializer;

/**
 * Base implementation of <code>Component</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.kns.uif.Component
 */
public abstract class ComponentBase implements Component, ScriptEventSupport {
	private static final long serialVersionUID = -4449335748129894350L;

	private String id;
	private String name;
	private String template;
	private String title;

	private boolean render;

	private boolean hidden;
	private boolean readOnly;
	private Boolean required;
	private boolean disclosure;

	private String align;
	private String valign;
	private String width;
	private int colSpan;
	private int rowSpan;
	private String style;
	private String styleClass;

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

	private ComponentDecorator decorator;
	private DecoratorChain decoratorChain;

	private List<ComponentInitializer> componentInitializers;

	public ComponentBase() {
		componentInitializers = new ArrayList<ComponentInitializer>();

		colSpan = 1;
		rowSpan = 1;

		render = true;
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
	 * @see org.kuali.rice.kns.uif.Component#performUpdate(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	public void performUpdate(View view, Object model) {
		if (isReadOnly()) {
			for (Component component : getNestedComponents()) {
				if (component != null) {
					component.setReadOnly(true);
				}
			}
		}
	}

	/**
	 * The following setup is done here:
	 * 
	 * <ul>
	 * <li>Setup the decorator chain (if component has decorators) for rendering
	 * </li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.Component#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	public void performFinalize(View view, Object model) {
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

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getRequired() {
		return this.required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public boolean isDisclosure() {
		return this.disclosure;
	}

	public void setDisclosure(boolean disclosure) {
		this.disclosure = disclosure;
	}

	public String getAlign() {
		return this.align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getValign() {
		return this.valign;
	}

	public void setValign(String valign) {
		this.valign = valign;
	}

	public String getWidth() {
		return this.width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public int getColSpan() {
		return this.colSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	public int getRowSpan() {
		return this.rowSpan;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean isRender() {
		return this.render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public List<ComponentInitializer> getComponentInitializers() {
		return this.componentInitializers;
	}

	public void setComponentInitializers(List<ComponentInitializer> componentInitializers) {
		this.componentInitializers = componentInitializers;
	}

	public ComponentDecorator getDecorator() {
		return this.decorator;
	}

	public void setDecorator(ComponentDecorator decorator) {
		this.decorator = decorator;
	}

	public DecoratorChain getDecoratorChain() {
		return this.decoratorChain;
	}

	public void setDecoratorChain(DecoratorChain decoratorChain) {
		this.decoratorChain = decoratorChain;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ScriptEventSupport#getHasEventScript()
	 */
	public boolean getHasEventScript() {
		boolean hasEventScript = false;

		if (getSupportsOnLoad() && StringUtils.isNotBlank(getOnLoadScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnUnload() && StringUtils.isNotBlank(getOnUnloadScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnClose() && StringUtils.isNotBlank(getOnCloseScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnBlur() && StringUtils.isNotBlank(getOnBlurScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnChange() && StringUtils.isNotBlank(getOnChangeScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnClick() && StringUtils.isNotBlank(getOnClickScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnDblClick() && StringUtils.isNotBlank(getOnDblClickScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnFocus() && StringUtils.isNotBlank(getOnFocusScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnSubmit() && StringUtils.isNotBlank(getOnSubmitScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnKeyPress() && StringUtils.isNotBlank(getOnKeyPressScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnKeyUp() && StringUtils.isNotBlank(getOnKeyUpScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnKeyDown() && StringUtils.isNotBlank(getOnKeyDownScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnMouseOver() && StringUtils.isNotBlank(getOnMouseOverScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnMouseOut() && StringUtils.isNotBlank(getOnMouseOutScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnMouseUp() && StringUtils.isNotBlank(getOnMouseUpScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnMouseDown() && StringUtils.isNotBlank(getOnMouseDownScript())) {
			hasEventScript = true;
		}
		else if (getSupportsOnMouseMove() && StringUtils.isNotBlank(getOnMouseMoveScript())) {
			hasEventScript = true;
		}

		return hasEventScript;
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

}
