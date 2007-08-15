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
package org.kuali.rice.resourceloader.management;

import org.kuali.rice.lifecycle.Lifecycle;

/**
 * Defines the simple MBean interface for ResourceLoader management.
 *
 * @author Eric Westfall
 */
public interface ResourceLoaderWrapperMBean extends Lifecycle {

	/**
	 * Retrieve the name of the ResourceLoader.
	 */
	public String getName();
	
	/**
	 * Set the name of the ResourceLoader
	 */
	public void setName(String name);
	
	/**
	 * Retrieves the names of all the children of the resource loader.
	 */
	public String[] getChildren();
	
	/**
	 * Returns the type of the resource loader (it's fully qualified java classname)
	 */
	public String getType();
	
	/**
	 * Returns a string representation of the hierarchy of resource loaders beneath this resource loader.
	 */
	public String examineContents();
	
	/**
	 * Dumps the contents of the resource loader to standard output.
	 */
	public void dumpContents();
	
	/**
	 * Returns true if the service with the given namespace URI and local part exists in this resource loader
	 */
	public boolean hasService(String namespaceUri, String localPart);
	
}
