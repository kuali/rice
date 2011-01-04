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
package org.kuali.rice.kns.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.ui.initializer.ComponentInitializer;

/**
 * Base implementation of <code>Component</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.kns.ui.Component
 */
public abstract class ComponentBase implements Component {
	private String id;
	private String name;
	private String template;

	private boolean render;

	private boolean hidden;
	private boolean readOnly;
	private boolean required;
	private boolean disclosure;

	private String align;
	private String valign;
	private String width;
	private int colSpan;
	private int rowSpan;
	private String style;
	private String styleClass;

	private List<ComponentInitializer> componentInitializers;

	public ComponentBase() {
		componentInitializers = new ArrayList<ComponentInitializer>();

		colSpan = 1;
		rowSpan = 1;

		render = true;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>If id is not given and name is, set it to the name prefixed with
	 * 'id_' , else set id to 'id_' and the next sequence number.</li>
	 * <li>Executes any configured <code>ComponentInitializer</code> instances</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize()
	 */
	@Override
	public void initialize(Map<String, String> options) {
		// set id
		if (StringUtils.isBlank(id)) {
			if (StringUtils.isNotBlank(name)) {
				id = "id_" + name;
			}
			else {
				// TODO: revisit this when working on binding paths, need to keep in mind containers as well
				id = "id_" + UUID.randomUUID().toString();
			}
		}

		// invoke initializers
		for (ComponentInitializer initializer : componentInitializers) {
			initializer.initialize(this, options);
		}
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

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
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

	/**
	 * <code>ComponentInitializer</code> instances that should be invoked to
	 * initialize the component
	 * <p>
	 * These provide dynamic initialization behavior for the component and are
	 * configured through the components definition. Each initializer will get
	 * invoked by the initialize method.
	 * </p>
	 * 
	 * @return List of component initializers
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize
	 */
	public List<ComponentInitializer> getComponentInitializers() {
		return this.componentInitializers;
	}

	public void setComponentInitializers(List<ComponentInitializer> componentInitializers) {
		this.componentInitializers = componentInitializers;
	}

}
