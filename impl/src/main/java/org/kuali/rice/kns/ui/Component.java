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

import java.io.Serializable;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Component extends Serializable {

	/**
	 * Returns the unique id (within a given tree) for the component
	 * 
	 * @return String id
	 */
	public String getId();
	
	/**
	 * Returns the name for the component type
	 * 
	 * @return String type name
	 */
	public String getComponentTypeName();

	/**
	 * Method should be called to initialize the component. This is where
	 * components can set defaults and setup other necessary state.
	 */
	public void initialize();

}
