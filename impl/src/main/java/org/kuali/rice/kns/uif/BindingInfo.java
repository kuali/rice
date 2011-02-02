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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;

/**
 * Provides binding configuration for an DataBinding component (attribute or
 * collection)
 * 
 * <p>
 * From the binding configuration the binding path is determined (if not
 * manually set) and used to set the path in the UI or to get the value from the
 * model
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BindingInfo implements Serializable {
	private boolean bindToForm;

	private String bindingName;
	private String bindByNamePrefix;
	private String modelPath;

	private String bindingPath;

	public BindingInfo() {
		bindToForm = false;
	}

	public void setDefaults(View view, Component component) {
		if (StringUtils.isBlank(bindingName)) {
			bindingName = component.getName();
		}

		if (StringUtils.isBlank(modelPath)) {
			modelPath = view.getDefaultModelPath();
		}
	}

	/**
	 * Path to the property on the model the component binds to. Uses standard
	 * dot notation for nested properties. If the binding path was manually set
	 * it will be returned as it is, otherwise the path will be formed by using
	 * the model path and the bind prefix
	 * 
	 * <p>
	 * e.g. Property name 'foo' on a model would have binding path "foo", while
	 * property name 'name' of the nested model property 'account' would have
	 * binding path "account.name"
	 * </p>
	 * 
	 * @return String binding path
	 */
	public String getBindingPath() {
		if (StringUtils.isNotBlank(bindingPath)) {
			return bindingPath;
		}

		String formedBindingPath = "";

		if (!bindToForm && StringUtils.isNotBlank(modelPath)) {
			formedBindingPath = modelPath + ".";
		}

		if (StringUtils.isNotBlank(bindByNamePrefix)) {
			formedBindingPath += bindByNamePrefix + "." + bindingName;
		}
		else {
			formedBindingPath += bindingName;
		}

		return formedBindingPath;
	}

	/**
	 * Setter for the binding path. Can be left blank in which the path will be
	 * determined from the binding configuration
	 * 
	 * @param bindingPath
	 */
	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	public boolean isBindToForm() {
		return this.bindToForm;
	}

	public void setBindToForm(boolean bindToForm) {
		this.bindToForm = bindToForm;
	}

	public String getBindingName() {
		return this.bindingName;
	}

	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}

	/**
	 * Prefix that will be used to form the binding path from the component
	 * name. Typically used for nested collection properties
	 * 
	 * @return String binding prefix
	 */
	public String getBindByNamePrefix() {
		return this.bindByNamePrefix;
	}

	/**
	 * Setter for the prefix to use for forming the binding path by name
	 * 
	 * @param bindByNamePrefix
	 */
	public void setBindByNamePrefix(String bindByNamePrefix) {
		this.bindByNamePrefix = bindByNamePrefix;
	}

	public String getModelPath() {
		return this.modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

}
