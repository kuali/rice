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

/**
 * Components that bind to a model (hold model data) should implement this
 * interface
 * 
 * <p>
 * Provides the methods necessary to do binding of values between the model and
 * component
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataBinding {

	/**
	 * Path to the property on the model the component binds to. Uses standard
	 * dot notation for nested properties
	 * 
	 * <p>
	 * e.g. Property name 'foo' on a model would have binding path "foo", while
	 * property name 'name' of the nested model property 'account' whould have
	 * binding path "account.name"
	 * </p>
	 * 
	 * @return String binding path
	 */
	public String getBindingPath();

	/**
	 * Name of the model as declared in the <code>View</code> modelClasses Map
	 * the component binds to. Can be empty if the view only contains one model
	 * 
	 * @return String model name
	 * @see org.kuali.rice.kns.uif.container.View#getModelClasses()
	 */
	public String getModelName();

	/**
	 * Prefix that will be used to form the binding path from the component
	 * name. Typically used for nested collection properties
	 * 
	 * @return String binding prefix
	 */
	public String getBindByNamePrefix();

	/**
	 * Setter for the prefix to use for forming the binding path by name
	 * 
	 * @param bindByNamePrefix
	 */
	public void setBindByNamePrefix(String bindByNamePrefix);

}
