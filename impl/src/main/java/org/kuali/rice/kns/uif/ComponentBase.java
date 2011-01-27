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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.decorator.ComponentDecorator;
import org.kuali.rice.kns.uif.initializer.ComponentInitializer;

/**
 * Base implementation of <code>Component</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.kns.uif.Component
 */
public abstract class ComponentBase implements Component {
	private String id;
	private String name;
	private String template;

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

	private List<ComponentInitializer> componentInitializers;
	private Map<String, ComponentDecorator> decorators;

	public ComponentBase() {
		componentInitializers = new ArrayList<ComponentInitializer>();
		decorators = new HashMap<String, ComponentDecorator>();

		colSpan = 1;
		rowSpan = 1;

		render = true;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {

	}

	/**
	 * Default implementation does nothing
	 * 
	 * @see org.kuali.rice.kns.uif.Component#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performApplyModel(View view, Object model) {

	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		return new ArrayList<Component>();
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

	public Map<String, ComponentDecorator> getDecorators() {
		return this.decorators;
	}

	public void setDecorators(Map<String, ComponentDecorator> decorators) {
		this.decorators = decorators;
	}

}
