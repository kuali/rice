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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ConfigurableBase;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.ReferenceCopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public abstract class LayoutManagerBase extends ConfigurableBase implements LayoutManager {
	private static final long serialVersionUID = -2657663560459456814L;

	private String id;
	private String template;
	private String style;

	private List<String> styleClasses;

	@ReferenceCopy(newCollectionInstance=true)
	private Map<String, Object> context;

	private List<PropertyReplacer> propertyReplacers;

	public LayoutManagerBase() {
        super();

		styleClasses = new ArrayList<String>();
		context = new HashMap<String, Object>();
		propertyReplacers = new ArrayList<PropertyReplacer>();
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#performInitialization(org.kuali.rice.krad.uif.view.View,
	 *      org.kuali.rice.krad.uif.container.Container)
	 */
	public void performInitialization(View view, Container container) {
		// set id of layout manager from container
		if (StringUtils.isBlank(id)) {
			id = container.getId() + "_layout";
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#performApplyModel(org.kuali.rice.krad.uif.view.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
	 */
	public void performApplyModel(View view, Object model, Container container) {

	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#performFinalize(org.kuali.rice.krad.uif.view.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
	 */
	public void performFinalize(View view, Object model, Container container) {

	}

	/**
	 * Set of property names for the layout manager base for which on the
	 * property value reference should be copied. Subclasses can override this
	 * but should include a call to super
	 * 
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager.
	 *      getPropertiesForReferenceCopy()
	 */
	public Set<String> getPropertiesForReferenceCopy() {
		Set<String> refCopyProperties = new HashSet<String>();

		refCopyProperties.add(UifPropertyPaths.CONTEXT);

		return refCopyProperties;
	}

	/**
	 * Default Impl
	 * 
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return Container.class;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		return new ArrayList<Component>();
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getId()
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getTemplate()
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setTemplate(java.lang.String)
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getStyle()
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setStyle(java.lang.String)
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getStyleClasses()
	 */
	public List<String> getStyleClasses() {
		return this.styleClasses;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setStyleClasses(java.util.List)
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
	 * This method adds a single style to the list of css style classes on this layoutManager
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
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getContext()
	 */
	public Map<String, Object> getContext() {
		return this.context;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setContext(java.util.Map)
	 */
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#pushObjectToContext(java.lang.String,
	 *      java.lang.Object)
	 */
	public void pushObjectToContext(String objectName, Object object) {
		if (this.context == null) {
			this.context = new HashMap<String, Object>();
		}

		this.context.put(objectName, object);
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#getPropertyReplacers()
	 */
	public List<PropertyReplacer> getPropertyReplacers() {
		return this.propertyReplacers;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManager#setPropertyReplacers(java.util.List)
	 */
	public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
		this.propertyReplacers = propertyReplacers;
	}

}
