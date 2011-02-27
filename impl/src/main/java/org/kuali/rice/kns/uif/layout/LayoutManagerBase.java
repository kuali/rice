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
package org.kuali.rice.kns.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.View;

/**
 * Base class for all layout managers
 * 
 * <p>
 * Provides general properties of all layout managers, such as the unique id,
 * rendering template, and style settings
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LayoutManagerBase implements LayoutManager {
	private static final long serialVersionUID = -2657663560459456814L;

	private String id;
	private String template;
	private String style;
	private String styleClass;

	public LayoutManagerBase() {

	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.Container)
	 */
	public void performInitialization(View view, Container container) {
		// set id of layout manager from container
		if (StringUtils.isBlank(id)) {
			id = container.getId() + "_layout";
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	public void performApplyModel(View view, Object model, Container container) {

	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	public void performFinalize(View view, Object model, Container container) {

	}

	/**
	 * Default Impl
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return Container.class;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		return new ArrayList<Component>();
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getId()
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getTemplate()
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#setTemplate(java.lang.String)
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getStyle()
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#setStyle(java.lang.String)
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#getStyleClass()
	 */
	public String getStyleClass() {
		return this.styleClass;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManager#setStyleClass(java.lang.String)
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

}
